package sk.tuke.service.impl;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.tuke.domain.CityAuthorities;
import sk.tuke.domain.Petition;
import sk.tuke.domain.Signee;
import sk.tuke.domain.User;
import sk.tuke.domain.enumeration.Status;
import sk.tuke.repository.CityAuthoritiesRepository;
import sk.tuke.repository.PetitionRepository;
import sk.tuke.repository.SigneeRepository;
import sk.tuke.security.TokenProvider;
import sk.tuke.service.EmailService;
import sk.tuke.service.PetitionService;
import sk.tuke.service.UserService;
import sk.tuke.service.dao.PetitionSigneesDAO;
import sk.tuke.service.dao.SignedPetitionWrapperDAO;
import sk.tuke.service.dto.EmailDetails;
import sk.tuke.service.dto.PetitionDTO;
import sk.tuke.service.dto.UserInfoDTO;
import sk.tuke.service.dto.specific.PetitionSigneesDTO;
import sk.tuke.service.dto.specific.PetitionSpecificDTO;
import sk.tuke.service.dto.specific.SignedPetitionDTO;
import sk.tuke.service.errors.PetitionAlreadySignedByUserException;
import sk.tuke.service.mapper.PetitionMapper;
import sk.tuke.service.mapper.UserInfoMapper;
import sk.tuke.service.mapper.specific.PetitionSpecificMapper;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class PetitionServiceImpl implements PetitionService {

    private final Integer SIGNEES_NEEDED = 3;
    private final CityAuthoritiesRepository cityAuthoritiesRepository;

    private final PetitionRepository petitionRepository;
    private final PetitionMapper petitionMapper;

    private final PetitionSpecificMapper petitionSpecificMapper;

    private final UserService userService;

    private final UserInfoMapper userInfoMapper;

    private final SigneeRepository signeeRepository;

    private final TokenProvider tokenProvider;

    private final EmailService emailService;

    public PetitionServiceImpl(PetitionRepository petitionRepository, PetitionMapper petitionMapper,
                               UserService userService, PetitionSpecificMapper petitionSpecificMapper, UserInfoMapper userInfoMapper,
                               SigneeRepository signeeRepository, TokenProvider tokenProvider,
                               EmailService emailService,
                               CityAuthoritiesRepository cityAuthoritiesRepository) {
        this.petitionRepository = petitionRepository;
        this.petitionMapper = petitionMapper;
        this.userService = userService;
        this.petitionSpecificMapper = petitionSpecificMapper;
        this.userInfoMapper = userInfoMapper;
        this.signeeRepository = signeeRepository;
        this.tokenProvider = tokenProvider;
        this.emailService = emailService;
        this.cityAuthoritiesRepository = cityAuthoritiesRepository;
    }

    /**
     * Deletes a petition by its ID.
     *
     * @param id ID of the petition to delete.
     */
    @Override
    public void delete(Integer id) {
        log.debug("Request to deleting petition with ID: {}", id);
        petitionRepository.deleteById(id);
    }

    /**
     * Deletes a petition by its ID.
     *
     * @param id ID of the petition to delete.
     */
    @Override
    public void setDelete(PetitionDTO petition) {
        log.debug("Request to set status delete on petition with ID: {}", petition.getId());
        petition.setStatus(Status.DELETED);
        petitionRepository.save(petitionMapper.toEntity(petition));
        petitionRepository.flush();
    }

    /**
     * Saves a new petition or updates an existing one.
     *
     * @param petitionDTO DTO of the petition to save.
     * @return the saved PetitionDTO.
     */
    @Override
    public PetitionDTO save(PetitionDTO petitionDTO) {
        log.debug("Request to saving petition with details: {}", petitionDTO);
        Petition petition = petitionMapper.toEntity(petitionDTO);
        petition = petitionRepository.save(petition);

        return petitionMapper.toDto(petition);
    }

    /**
     * Finds all petitions with pagination.
     *
     * @param pageable pagination debugrmation.
     * @return a paginated list of PetitionDTOs.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<PetitionSpecificDTO> findAll(Pageable pageable) {
        log.debug("Request to find all petitions");
        return petitionRepository.findAll(pageable)
            .map(petitionSpecificMapper::toDto);
    }

    /**
     * Finds all petitions with pagination.
     *
     * @param pageable pagination debugrmation.
     * @return a paginated list of PetitionDTOs.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<PetitionSpecificDTO> findAllActive(Pageable pageable) {
        log.debug("Request to find all active petitions");
        return petitionRepository.findAllByStatus(pageable, Status.ACTIVE)
            .map(petitionSpecificMapper::toDto);
    }

    @Override
    public Page<PetitionSpecificDTO> findAllByAuthorId(Long id, Pageable pageable) {
        return petitionRepository.findAllByUserId(pageable, id).map(petitionSpecificMapper::toDto);
    }

    /**
     * Finds a petition by its ID.
     *
     * @param id ID of the petition to find.
     * @return an Optional containing the PetitionDTO if found, otherwise empty.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<PetitionDTO> findOne(Integer id) {
        log.debug("Request to find petition with ID: {}", id);
        return petitionRepository.findById(id)
            .map(petitionMapper::toDto);
    }

    @Override
    public boolean sign(PetitionDTO petitionDTO, Long userId) {
        log.debug("Request to sign petition with ID {} by user {}", petitionDTO.getId(), userId);

        Optional<User> userOpt = userService.findUserByIdSpecific(userId);
        if (userOpt.isEmpty())
            return false;

        User user = userOpt.get();
        Petition petition = petitionMapper.toEntity(petitionDTO);

        Optional<Signee> signeeOpt = signeeRepository.findByUserIdAndPetitionId(user.getId(), petition.getId());

        if (signeeOpt.isPresent()) {
            // Ak je peticia pravoplatne podpisana
            if (signeeOpt.get().getVerified())
                throw new PetitionAlreadySignedByUserException();
            // Ak nieje, vymaz nepravoplatny podpis
            signeeRepository.delete(signeeOpt.get());
        }

        Signee signee = Signee.builder()
            .dateOfSigning(Instant.now())
            .verified(false)
            .user(user)
            .petition(petition).build();

        signeeRepository.save(signee);

        sendPetitionSignEmail(user.getEmail(), user.getId(), petition.getId(), petition.getName(), signee.getId());
        return true;
    }

    @Override
    public boolean finishSigning(Signee signee) {
        log.debug("Request to finish signing petition {} by user {}", signee.getPetition().getId(), signee.getUser().getId());

        // if petition is already signed and verified
        if (Boolean.TRUE.equals(signee.getVerified()))
            return false;

        signee.setVerified(true);
        signeeRepository.save(signee);
        Petition petition = signee.getPetition();
        petition.upSignCount();
        checkSignCount(petition);
        petitionRepository.save(petition);
        return true;
    }

    private void checkSignCount(Petition petition) {

        if (petition.getNoSignees() >= SIGNEES_NEEDED && !petition.getAuthorityNotified()) {
            log.debug("Petition reached needed amount to notify authorities");
            notifyAuthority(petition);
        }
    }

    @Async
    protected void notifyAuthority(Petition petition) {
        log.debug("Request to notify all authorities for petition with id: {}", petition.getId());

        List<CityAuthorities> cityAuthorities = cityAuthoritiesRepository.findAllByCityId(petition.getCity().getId());

        cityAuthorities.forEach(ca -> sendPetitionSuccessfulEmail(
            ca.getUser().getEmail(),
            petition.getName(),
            petition.getNoSignees(),
            petition.getUser().getFirstName() + petition.getUser().getLastName()));

        petition.setAuthorityNotified(true);
        petitionRepository.save(petition);
    }

    protected void sendPetitionSuccessfulEmail(String email,
                                               String petitionName,
                                               Integer noSignees,
                                               String authorName) {
        log.debug("Sending email about successful petition {}", petitionName);

        String message = "Petícia s názvom " + petitionName + " získala " + noSignees + " podpisov.\n\n" +
            "Autorom úspešnej petície je " + authorName + ".\n\n" +
            "Tím Citizen Voice";

        EmailDetails emailDetails = EmailDetails.builder()
            .subject("Úspešná petícia vo Vašom meste " + petitionName)
            .recipient(email)
            .msgBody(message).build();

        if (emailService.sendSimpleMail(emailDetails)) {
            log.debug("Email activation link send successfully.");
        } else {
            log.debug("Sending activation email failed :{{");
        }
    }

    @Async
    protected void sendPetitionSignEmail (@NotNull String email,
                                          @NotNull Long userId,
                                          @NotNull Integer petitionId,
                                          @NotNull String petitionName,
                                          @NotNull Integer signeeId) {
        log.debug("Sending activation email to recipient: {}", email);
        String token = tokenProvider.createPetitionSignToken(userId, email, petitionId, signeeId);

        // switch between local and 'prod' uri
        String link = "http://localhost:4200/confirm_petition_signature/" + token;
//        String link = "http://localhost:8080/api/petitions/sign/finish/" + token;

        String message = "Ďakujeme za podpis petície " + petitionName + ".\n\n" +
            "Pre overenie Vášho podpisu kliknite na link: " + link + "\n\n" +
            "Tím Citizen Voice";

        EmailDetails emailDetails = EmailDetails.builder()
            .subject("Overenie podpisu petície " + petitionName)
            .recipient(email)
            .msgBody(message).build();

        log.debug("MESSAGE TO SEND: " + emailDetails);
        if (emailService.sendSimpleMail(emailDetails)) {
            log.debug("Email activation link send successfully.");
        } else {
            log.debug("Sending activation email failed :{{");
        }
    }

    @Override
    public List<SignedPetitionDTO>  getAllSignedByUserId(Long userId) {
        List<SignedPetitionWrapperDAO> signedPetitions = signeeRepository.findSignedPetitionsByUserId(userId);
        List<SignedPetitionDTO> signedPetitionDTOS = new ArrayList<>();
        signedPetitions.forEach(sp -> {
            SignedPetitionDTO dto = SignedPetitionDTO.builder()
                .petition(petitionSpecificMapper.toDto(sp.getPetition()))
                .verified(sp.getVerified())
                .build();
            signedPetitionDTOS.add(dto);
        });
        return signedPetitionDTOS;
    }

    @Override
    public List<PetitionSigneesDTO> getAllPetitionSignees(Integer petitionId) {
        List<PetitionSigneesDAO> signees = signeeRepository.findAllSigneesByPetitionId(petitionId);
        List<PetitionSigneesDTO> signeesDTOS = new ArrayList<>();
        signees.forEach(s -> {
            PetitionSigneesDTO dto = PetitionSigneesDTO.builder()
                .user(userInfoMapper.toDto(s.getUser()))
                .verified(s.isVerified())
                .build();
            signeesDTOS.add(dto);
        });

        return signeesDTOS;
    }
}

