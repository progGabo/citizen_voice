package sk.tuke.service.impl;

import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.tuke.domain.User;
import sk.tuke.domain.enumeration.ActiveStatus;
import sk.tuke.repository.UserRepository;
import sk.tuke.security.SecurityUtils;
import sk.tuke.security.TokenProvider;
import sk.tuke.security.UserNotActivatedException;
import sk.tuke.service.EmailService;
import sk.tuke.service.UserService;
import sk.tuke.service.dto.EmailDetails;
import sk.tuke.service.dto.UserInfoDTO;
import sk.tuke.service.dto.specific.UserRegisterDTO;
import sk.tuke.service.errors.EmailAlreadyUsedException;
import sk.tuke.service.errors.InvalidPasswordException;
import sk.tuke.service.mapper.UserInfoMapper;
import sk.tuke.service.mapper.UserRegisterMapper;

import java.util.Optional;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;
    private final UserRegisterMapper userRegisterMapper;

    private final TokenProvider tokenProvider;

    private final UserInfoMapper userInfoMapper;


    public UserServiceImpl(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        UserRegisterMapper userRegisterMapper,
        EmailService emailService,
        TokenProvider tokenProvider,
        UserInfoMapper userInfoMapper
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRegisterMapper = userRegisterMapper;
        this.emailService = emailService;
        this.tokenProvider = tokenProvider;
        this.userInfoMapper = userInfoMapper;
    }


    // TODO implement reset password
//    public Optional<User> completePasswordReset(String newPassword, String key) {
//        LOG.debug("Reset user password for reset key {}", key);
//        return userRepository
//            .findOneByResetKey(key)
//            .filter(user -> user.getResetDate().isAfter(Instant.now().minus(1, ChronoUnit.DAYS)))
//            .map(user -> {
//                user.setPassword(passwordEncoder.encode(newPassword));
//                user.setResetKey(null);
//                user.setResetDate(null);
//                this.clearUserCaches(user);
//                return user;
//            });
//    }

//    public Optional<User> requestPasswordReset(String mail) {
//        return userRepository
//            .findOneByEmailIgnoreCase(mail)
//            .filter(User::isActivated)
//            .map(user -> {
//                user.setResetKey(RandomUtil.generateResetKey());
//                user.setResetDate(Instant.now());
//                this.clearUserCaches(user);
//                return user;
//            });
//    }

    public UserRegisterDTO registerUser(UserRegisterDTO userDTO) {
        log.info("Registering entity: {}", userDTO.getEmail());

        userDTO.setEmail(userDTO.getEmail().trim().toLowerCase());
        userRepository
            .findOneByEmailIgnoreCase(userDTO.getEmail())
            .ifPresent(existingUser -> {
                boolean removed = removeNonActivatedUser(existingUser);
                if (!removed) {
                    throw new EmailAlreadyUsedException();
                }
            });

        String encryptedPassword = passwordEncoder.encode(userDTO.getPasswd());
        userDTO.setPasswd(encryptedPassword);

        User user = userRegisterMapper.toEntity(userDTO);

        user.setActiveStatus(ActiveStatus.NONE);

        userRepository.save(user);
        userRepository.flush();
        userDTO.setId(user.getId());
        sendActivationEmail(user.getEmail(), user.getId());
        return userDTO;
    }

    @Async
    protected void sendActivationEmail (@NotNull String email,
                                        @NotNull Long userId) {
        log.debug("Sending activation email to recipient: {}", email);
        String token = tokenProvider.createTokenForUserRegistration(userId, email);

        // change local dev and 'prod' before and after development
        // String link = "http://localhost:4200/activate/" + token;
        String link = "http://localhost:8080/api/auth/register-finish/" + token;

        String message = "Vitajte v aplikácii Citizen Voice.\n\n " +
            "Pre aktiváciu účtu kliknite na tento link: " + link;

        EmailDetails emailDetails = EmailDetails.builder()
            .subject("Aktivácia účtu Citizen Voice")
            .recipient(email)
            .msgBody(message).build();

        log.debug("MESSAGE TO SEND: " + emailDetails);
        if (emailService.sendSimpleMail(emailDetails)) {
            log.debug("Email activation link send successfully.");
        } else {
            log.debug("Sending activation email failed :{{");
        }
    }

    private boolean removeNonActivatedUser(User existingUser) {
        if (existingUser.getActiveStatus() != ActiveStatus.NONE) {
            return false;
        }
        userRepository.delete(existingUser);
        userRepository.flush();
        return true;
    }

    // FIND USER/s METHODS

    @Transactional(readOnly = true)
    public Optional<User> findUserByEmail(String email) {
        log.debug("Request to find user by email: {}", email);
        return userRepository.findOneByEmailIgnoreCase(email);
    }

    @Transactional(readOnly = true)
    public Optional<UserInfoDTO> findUserByLogin(String email) {
        log.debug("Request to find user by email: {}", email);
        return userRepository.findOneByEmailIgnoreCase(email).map(userInfoMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<UserInfoDTO> findUserById(Long id) {
        log.debug("Request to find user by id: {}", id);
        return userRepository.findById(id).map(userInfoMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<User> findUserByIdSpecific(Long id) {
        log.debug("Request to find user by id: {}", id);
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Page<UserInfoDTO> getAllManagedUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userInfoMapper::toDto);
    }

    public void deleteUser(Long id) {
        userRepository
            .findById(id)
            .ifPresent(user -> {
                userRepository.delete(user);
                log.debug("Deleted User: {}", user);
            });
    }

    @Transactional
    @Override
    public void changePassword(String currentClearTextPassword, String newPassword) {
        SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByEmailIgnoreCase)
            .ifPresent(user -> {
                if (user.getActiveStatus() != ActiveStatus.ACTIVATED)
                    throw new UserNotActivatedException("Cannot change password for non activated user.");
                String currentEncryptedPassword = user.getPasswd();
                if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
                    throw new InvalidPasswordException();
                }
                String encryptedPassword = passwordEncoder.encode(newPassword);
                user.setPasswd(encryptedPassword);
                log.debug("Changed password for User: {}", user);
            });
    }

    @Override
    public Page<UserInfoDTO> getPendingOrganizations(Pageable pageable) {
        log.debug("Request to get all organizations with PENDING status");
        return userRepository.findAllByActiveStatus(pageable, ActiveStatus.PENDING).map(userInfoMapper::toDto);
    }

    @Override
    public UserInfoDTO activateOrganization(User org) {
        log.debug("Request to activate organization with id: {}", org.getId());
        org.setActiveStatus(ActiveStatus.ACTIVATED);
        return userInfoMapper.toDto(userRepository.save(org));
    }

    @Override
    @Transactional
    public UserInfoDTO updateSettings(User user) {
        user = userRepository.saveAndFlush(user);

        return userInfoMapper.toDto(user);
    }

    //    // TODO nechame tuto funkcionalitu
////    /**
////     * Not activated users should be automatically deleted after 3 days.
////     * <p>
////     * This is scheduled to get fired everyday, at 01:00 (am).
////     */
////    @Scheduled(cron = "0 0 1 * * ?")
////    public void removeNotActivatedUsers() {
////        userRepository
////            .findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant.now().minus(3, ChronoUnit.DAYS))
////            .forEach(user -> {
////                LOG.debug("Deleting not activated user {}", user.getLogin());
////                userRepository.delete(user);
////                this.clearUserCaches(user);
////            });
////    }

}
