package sk.tuke.web.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import sk.tuke.domain.User;
import sk.tuke.domain.enumeration.ActiveStatus;
import sk.tuke.domain.enumeration.AuthRole;
import sk.tuke.repository.UserRepository;
import sk.tuke.security.AuthoritiesConstants;
import sk.tuke.security.SecurityUtils;
import sk.tuke.security.TokenProvider;
import sk.tuke.service.UserService;
import sk.tuke.service.dto.AdminUserDTO;
import sk.tuke.service.dto.PasswordChangeDTO;
import sk.tuke.service.dto.specific.UserRegisterDTO;
import sk.tuke.service.dto.specific.UserTokenDetails;
import sk.tuke.service.utils.Constants;
import sk.tuke.web.rest.errors.EmailAlreadyUsedException;
import sk.tuke.web.rest.errors.InvalidPasswordException;
import sk.tuke.web.rest.errors.LoginAlreadyUsedException;
import sk.tuke.web.rest.vm.KeyAndPasswordVM;

import java.net.URI;
import java.util.Optional;
import java.util.Set;

import static sk.tuke.config.Constants.LOGIN_REGEX;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class AccountResource {

    private static final Logger log = LoggerFactory.getLogger(AccountResource.class);
    private static class AccountResourceException extends RuntimeException {

        private AccountResourceException(String message) {
            super(message);
        }
    }

    private static final Set<AuthRole> ROLE_WHITELIST = Set.of(
        AuthRole.ROLE_USER,
        AuthRole.ROLE_ORGANIZATION);

    private final UserRepository userRepository;

    private final UserService userService;

    private final TokenProvider tokenProvider;


    public AccountResource(UserRepository userRepository, UserService userService,
                           TokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.tokenProvider = tokenProvider;
    }

    /**
     * {@code POST  /register} : register the user.
     *
     * @param user the managed user View Model.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws LoginAlreadyUsedException {@code 400 (Bad Request)} if the login is already used.
     */
    @PostMapping("/auth/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserRegisterDTO> registerAccount(@Valid @RequestBody UserRegisterDTO user) {
        log.info("Request to register new account: {}", user.getEmail());

        if (isPasswordLengthInvalid(user.getPasswd())) {
            return ResponseEntity.badRequest()
                .header("X-Application-Error", "Invalid password length")
                .body(null);
        }

        if (!user.getEmail().matches(LOGIN_REGEX))
            return ResponseEntity.badRequest()
                .header("X-Application-Error", "Invalid email")
                .body(null);

        if (!ROLE_WHITELIST.contains(user.getRole())) {
            return ResponseEntity.badRequest()
                .header("X-Application-Error", "Invalid role")
                .body(null);
        }

        try {
            UserRegisterDTO registeredUser = userService.registerUser(user);
            registeredUser.setPasswd("secret");

            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(registeredUser.getId())
                .toUri();

            return ResponseEntity.created(location).body(registeredUser);
        } catch (EmailAlreadyUsedException e) {
            return ResponseEntity.badRequest()
                .header("X-Application-Error", "Email is already in use")
                .body(null);
        }
    }

    @GetMapping("/auth/register-finish/{token}")
    public ResponseEntity<RegisterFinishingInfo> finishUserRegister(@PathVariable String token) {
        log.debug("REST request to finish registration.");
        if (!tokenProvider.isRegisterTokenValid(token))
            return ResponseEntity.badRequest().build();

        UserTokenDetails details = tokenProvider.getTokenClaims(token);

        Optional<User> userOpt = userRepository.findByIdAndActiveStatus(details.getUserId(), ActiveStatus.NONE);

        if (userOpt.isEmpty())
            return ResponseEntity.notFound().build();

        User user = userOpt.get();
        setUserActiveStatus(user);
        userRepository.save(user);
        return ResponseEntity.ok().body(new RegisterFinishingInfo(user.getRole()));
    }

    /**
     * Sets active status for user.
     * If user has ROLE_USER then user is fully activated.
     * If user has ROLE_ORGANIZATION the user auth is PENDING, waiting to be approved by admin.
     * @param user
     */
    private void setUserActiveStatus (User user) {
        if (user.getActiveStatus() != ActiveStatus.NONE)
            return;

        switch (user.getRole()) {
            case AuthRole.ROLE_USER -> user.setActiveStatus(ActiveStatus.ACTIVATED);
            case AuthRole.ROLE_ORGANIZATION -> user.setActiveStatus(ActiveStatus.PENDING);
            default -> log.error("UNKNOWN AUTH ROLE ON REGISTRATION FINISH.");
        }
    }

    /**
     * {@code GET  /activate} : activate the registered user.
     *
     * @param key the activation key.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be activated.
     */
    @GetMapping("/activate")
    // TODO FINISH
    public void activateAccount(@RequestParam(value = "key") String key) {
//        Optional<User> user = userService.activateRegistration(key);
//        if (!user.isPresent()) {
//            throw new AccountResourceException("No user was found for this activation key");
//        }
    }

    // TODO fix
//    /**
//     * {@code GET  /account} : get the current user.
//     *
//     * @return the current user.
//     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be returned.
//     */
//    @GetMapping("/account")
//    public AdminUserDTO getAccount() {
//        return userService
//            .getUserWithAuthorities()
//            .map(AdminUserDTO::new)
//            .orElseThrow(() -> new AccountResourceException("User could not be found"));
//    }

    /**
     * {@code POST  /account} : update the current user information.
     *
     * @param userDTO the current user information.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user login wasn't found.
     */
    @PostMapping("/auth/account")
    public void saveAccount(@Valid @RequestBody AdminUserDTO userDTO) {
//        String userLogin = SecurityUtils.getCurrentUserLogin()
//            .orElseThrow(() -> new AccountResourceException("Current user login not found"));
//        Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
//        if (existingUser.isPresent() && (!existingUser.orElseThrow().getLogin().equalsIgnoreCase(userLogin))) {
//            throw new EmailAlreadyUsedException();
//        }
//        Optional<User> user = userRepository.findOneByLogin(userLogin);
//        if (!user.isPresent()) {
//            throw new AccountResourceException("User could not be found");
//        }
//        userService.updateUser(
//            userDTO.getFirstName(),
//            userDTO.getLastName(),
//            userDTO.getEmail(),
//            userDTO.getLangKey(),
//            userDTO.getImageUrl()
//        );
    }

    /**
     * {@code POST  /account/change-password} : changes the current user's password.
     *
     * @param passwordChangeDto current and new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the new password is incorrect.
     */
    @Secured({AuthoritiesConstants.ORGANIZATION, AuthoritiesConstants.USER})
    @PostMapping(path = "/account/change-password")
    public void changePassword(@RequestBody PasswordChangeDTO passwordChangeDto) {
        log.debug("REST request to change password for user: {}", SecurityUtils.getCurrentUserLogin());
        if (isPasswordLengthInvalid(passwordChangeDto.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        userService.changePassword(passwordChangeDto.getCurrentPassword(), passwordChangeDto.getNewPassword());
    }

    /**
     * {@code POST   /account/reset-password/init} : Send an email to reset the password of the user.
     *
     * @param mail the mail of the user.
     */
    @PostMapping(path = "/account/reset-password/init")
    public void requestPasswordReset(@RequestBody String mail) {
//        Optional<User> user = userService.requestPasswordReset(mail);
//        if (user.isPresent()) {
//            mailService.sendPasswordResetMail(user.orElseThrow());
//        } else {
//            // Pretend the request has been successful to prevent checking which emails really exist
//            // but log that an invalid attempt has been made
//            LOG.warn("Password reset requested for non existing mail");
//        }
    }

    /**
     * {@code POST   /account/reset-password/finish} : Finish to reset the password of the user.
     *
     * @param keyAndPassword the generated key and the new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the password could not be reset.
     */
    @PostMapping(path = "/account/reset-password/finish")
    public void finishPasswordReset(@RequestBody KeyAndPasswordVM keyAndPassword) {
//        if (isPasswordLengthInvalid(keyAndPassword.getNewPassword())) {
//            throw new InvalidPasswordException();
//        }
//        Optional<User> user = userService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey());
//
//        if (!user.isPresent()) {
//            throw new AccountResourceException("No user was found for this reset key");
//        }
    }

    private static boolean isPasswordLengthInvalid(String password) {
        return (
            StringUtils.isEmpty(password) ||
            password.length() < Constants.PASSWORD_MIN_LENGTH ||
            password.length() > Constants.PASSWORD_MAX_LENGTH
        );
    }

    static class RegisterFinishingInfo {

        public RegisterFinishingInfo(AuthRole role) {
            setIsOrganization(role);
        }
        boolean isOrganization = false;

        private void setIsOrganization (AuthRole authRole) {
            switch (authRole) {
                case ROLE_ORGANIZATION -> this.isOrganization = true;
                default -> this.isOrganization = false;
            }
        }

        @JsonProperty("isOrganization")
        boolean getIdToken() {
            return isOrganization;
        }
    }
}
