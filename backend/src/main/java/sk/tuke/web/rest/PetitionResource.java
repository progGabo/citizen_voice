package sk.tuke.web.rest;

import jakarta.validation.Valid;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import sk.tuke.domain.Petition;
import sk.tuke.domain.Signee;
import sk.tuke.domain.User;
import sk.tuke.domain.enumeration.Status;
import sk.tuke.repository.SigneeRepository;
import sk.tuke.security.AuthoritiesConstants;
import sk.tuke.security.TokenProvider;
import sk.tuke.service.PetitionService;
import sk.tuke.service.dto.PetitionDTO;
import sk.tuke.service.dto.UserInfoDTO;
import sk.tuke.service.dto.specific.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import sk.tuke.service.errors.PetitionAlreadySignedByUserException;

@RestController
@RequestMapping("/api/petitions")
public class PetitionResource {

    private final Logger log = LoggerFactory.getLogger(PetitionResource.class);
    private final PetitionService petitionService;

    private final TokenProvider tokenProvider;
    private final SigneeRepository signeeRepository;

    public PetitionResource(PetitionService petitionService,
                            TokenProvider tokenProvider,
                            SigneeRepository signeeRepository) {
        this.petitionService = petitionService;
        this.tokenProvider = tokenProvider;
        this.signeeRepository = signeeRepository;
    }

    /**
     * Creates a new Petition.
     *
     * @param petitionDTO DTO of the petition to create.
     * @return ResponseEntity with the created PetitionDTO and status 201 (Created).
     * @throws IllegalArgumentException if the petitionDTO has a non-null ID.
     */
    @PostMapping
    @Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER, AuthoritiesConstants.ORGANIZATION})
    public ResponseEntity<PetitionDTO> createPetition(@Valid @RequestBody PetitionCreateDTO request) {
        log.info("Request to create Petition with name: {}", request.getName());
        Long userId = getUserId();

        PetitionDTO petitionDTO = PetitionDTO.builder()
            .name(request.getName())
            .content(request.getContent())
            .requiredSignees(request.getRequiredSignees())
            .noSignees(0)
            .status(Status.ACTIVE)
            .authorityNotified(false)
            .userId(userId)
            .cityId(request.getCityId())
            .build();

        PetitionDTO result = petitionService.save(petitionDTO);
        petitionService.sign(result, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * Updates an existing Petition.
     *
     * @return ResponseEntity with the updated PetitionDTO and status 200 (OK).
     * @throws IllegalArgumentException if the ID in the path and DTO do not match.
     */
    @Secured({AuthoritiesConstants.ADMIN})
    @PutMapping("/{id}")
    public ResponseEntity<PetitionDTO> updatePetition(@Valid @RequestBody PetitionDTO updated, @PathVariable Long id) {
        log.info("Request to update Petition with ID: {}", updated.getId());

        Optional<PetitionDTO> petitionOpt = petitionService.findOne(updated.getId());

        if (petitionOpt.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        updated.setNoSignees(petitionOpt.get().getNoSignees());

        PetitionDTO result = petitionService.save(updated);
        return ResponseEntity.ok(result);
    }

    /**
     * Retrieves a Petition by its ID.
     *
     * @param id ID of the petition to retrieve.
     * @return ResponseEntity with the found PetitionDTO and status 200 (OK), or 404 (Not Found) if not found.
     */

    @GetMapping("/{id}")
    public ResponseEntity<PetitionDTO> getPetition(@PathVariable Integer id) {
        log.info("Request to retrieve Petition with ID: {}", id);

        return petitionService.findOne(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Retrieves all Petitions with pagination.
     *
     * @param pageable pagination information.
     * @return ResponseEntity with a paginated list of PetitionDTOs and status 200 (OK).
     */
    @GetMapping
    public ResponseEntity<Page<PetitionSpecificDTO>> getAllPetitions(Pageable pageable) {
        log.info("Request to retrieve all Petitions with pagination");

        return ResponseEntity.ok(petitionService.findAllActive(pageable));
    }

    @Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER, AuthoritiesConstants.ORGANIZATION})
    @GetMapping("/signed")
    public ResponseEntity<List<SignedPetitionDTO>> getSignedPetitions() {
        log.debug("REST request to get signed petitions by user");
        List<SignedPetitionDTO> signedPetitions = petitionService.getAllSignedByUserId(getUserId());
        return ResponseEntity.ok().body(signedPetitions);
    }

    @Secured({AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER, AuthoritiesConstants.ORGANIZATION})
    @GetMapping("/sign/{id}")
    public ResponseEntity<Void> signPetition(@PathVariable Integer id) {
        log.debug("REST request to sign petition with id: {}", id);

        Optional<PetitionDTO> petitionOpt = petitionService.findOne(id);

        if (petitionOpt.isEmpty())
            return ResponseEntity.notFound().build();
        PetitionDTO petitionDTO = petitionOpt.get();

        // status 423 LOCKED if petition is not active
        if (petitionDTO.getStatus() != Status.ACTIVE)
            return ResponseEntity.status(HttpStatus.LOCKED).build();

        try {
            petitionService.sign(petitionDTO, getUserId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            if (e instanceof PetitionAlreadySignedByUserException)
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            log.error("Unexpected error occurred on signing petition: {}",  e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/sign/finish/{token}")
    public ResponseEntity<Void> finishSignPetititon(@PathVariable String token) {
        log.debug("REST request to finish petition signing.");
        if (!tokenProvider.isPetitionSignTokenValid(token))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        UserTokenDetails details = tokenProvider.getTokenClaims(token);

        Optional<Signee> signeeOpt = signeeRepository.findByIdAndUserIdAndPetitionId(details.getSigneeId(), details.getUserId(), details.getPetitionId());
        if (signeeOpt.isEmpty())
            return ResponseEntity.notFound().build();
        boolean valid = petitionService.finishSigning(signeeOpt.get());

        if (!valid)
            // petition already successfully signed
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        return ResponseEntity.ok().build();
    }


    /**
     * Deletes a Petition by its ID.
     *
     * @param id ID of the petition to delete.
     * @return ResponseEntity with status 204 (No Content).
     */
    @Secured({AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN, AuthoritiesConstants.ORGANIZATION})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePetition(@PathVariable Integer id) {
        log.info("Request to delete Petition with ID: {}", id);

        Optional<PetitionDTO> petitionOpt = petitionService.findOne(id);

        if (petitionOpt.isEmpty())
            return ResponseEntity.badRequest().build();

        Long userId = getUserId();
        PetitionDTO petition = petitionOpt.get();
        if (!Objects.equals(userId, petition.getUserId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        petitionService.setDelete(petition);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status/change")
    @Secured({AuthoritiesConstants.USER, AuthoritiesConstants.ORGANIZATION})
    public ResponseEntity<PetitionDTO> changeStatus(@PathVariable Integer id) {
        Optional<PetitionDTO> petition = petitionService.findOne(id);

        if (petition.isEmpty()){
            return ResponseEntity.badRequest().build();
        }

        PetitionDTO petitionDTO = petition.get();
        if (petitionDTO.getStatus() == Status.ACTIVE){
            petitionDTO.setStatus(Status.INACTIVE);
        }
        else{
            petitionDTO.setStatus(Status.ACTIVE);
        }

        PetitionDTO result = petitionService.save(petitionDTO);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/user/petitions")
    @Secured({AuthoritiesConstants.USER, AuthoritiesConstants.ORGANIZATION})
    public ResponseEntity<Page<PetitionSpecificDTO>> getAllPetitionsByAuthor(Pageable pageable) {

        return ResponseEntity.ok(petitionService.findAllByAuthorId(getUserId() ,pageable));
    }

    private Long getUserId () {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();
        return user.getId();
    }

    @GetMapping("/{id}/signed")
    @Secured({AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN, AuthoritiesConstants.ORGANIZATION})
    public ResponseEntity<Signed> isPetitionSignedByUser(@PathVariable Integer id) {
        log.debug("Request to get information if petition with id {id} is signed.", id);
        Optional<Signee> signeeOpt = signeeRepository.findByUserIdAndPetitionId(getUserId(), id);

        if (signeeOpt.isEmpty())
            return ResponseEntity.ok().body(new Signed());

        Signed signed = new Signed(true, signeeOpt.get().getVerified());
        return ResponseEntity.ok(signed);
    }

    @GetMapping("/{id}/signees")
    @Secured({AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN, AuthoritiesConstants.ORGANIZATION})
    public ResponseEntity<List<PetitionSigneesDTO>> getAllPetitionSignees(@PathVariable Integer id) {
        log.debug("REST request to get all signees for petition with id: ");
        List<PetitionSigneesDTO> signees = petitionService.getAllPetitionSignees(id);
        return ResponseEntity.ok(signees);
    }

    @Getter
    static class Signed {

        public Signed(boolean isSigned, boolean verified) {
            this.isSigned = isSigned;
            this.verified = verified;
        }

        public Signed(){}

        private boolean isSigned = false;

        private boolean verified = false;
    }
}
