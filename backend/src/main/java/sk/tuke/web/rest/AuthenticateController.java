package sk.tuke.web.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.web.bind.annotation.*;
import sk.tuke.domain.User;
import sk.tuke.domain.enumeration.ActiveStatus;
import sk.tuke.security.AuthoritiesConstants;
import sk.tuke.security.SecurityUtils;
import sk.tuke.security.TokenProvider;
import sk.tuke.service.impl.UserServiceImpl;
import sk.tuke.web.rest.vm.LoginVM;

import java.security.Principal;
import java.util.Optional;

/**
 * Controller to authenticate users.
 */
@RestController
@RequestMapping("/api")
public class AuthenticateController {

    private static final Logger log = LoggerFactory.getLogger(AuthenticateController.class);

    private final JwtEncoder jwtEncoder;

    @Value("${jhipster.security.authentication.jwt.token-validity-in-seconds:0}")
    private long tokenValidityInSeconds;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final TokenProvider tokenProvider;

    private final UserServiceImpl userServiceImpl;

    private final PasswordEncoder passwordEncoder;

    public AuthenticateController(JwtEncoder jwtEncoder, AuthenticationManagerBuilder authenticationManagerBuilder,
                                  UserServiceImpl userServiceImpl, PasswordEncoder passwordEncoder,
                                  TokenProvider tokenProvider) {
        this.jwtEncoder = jwtEncoder;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userServiceImpl = userServiceImpl;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    /**
     * Authenticates a user based on provided login credentials and generates a JWT token.
     *
     * <p>This endpoint processes a login request by verifying the user's email and password.
     * If the credentials are correct and the user is activated, a JWT token is generated and returned
     * in the response headers under the "Authorization" header as a Bearer token.</p>
     *
     * @param loginVM the login credentials (email and password) submitted by the user
     * @param request the HTTP request object
     * @param response the HTTP response object
     * @return a ResponseEntity containing a JWTToken object and the JWT in the Authorization header if successful;
     *         404 Not Found if the user is not activated or does not exist;
     *         401 Unauthorized if the password is incorrect
     */
    @PostMapping("/auth/login")
    public ResponseEntity<JWTToken> login(@Valid @RequestBody LoginVM loginVM,
                                              HttpServletRequest request, HttpServletResponse response) {
        log.debug("REST request to authorize user with email: {}", loginVM.getEmail());
        loginVM.setEmail(loginVM.getEmail().trim().toLowerCase());

        Optional<User> userOpt = userServiceImpl.findUserByEmail(loginVM.getEmail());

        if (userOpt.isEmpty())
            return ResponseEntity.notFound().build();

        if(userOpt.get().getActiveStatus() != ActiveStatus.ACTIVATED)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        User user = userOpt.get();
        if (!passwordEncoder.matches(loginVM.getPassword(), user.getPasswd()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String jwt = tokenProvider.createToken(user);
        SecurityUtils.setAuthentication(user, jwt);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(jwt);
        return new ResponseEntity<>(new JWTToken(jwt), httpHeaders, HttpStatus.OK);
    }

    @Secured({AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN, AuthoritiesConstants.ORGANIZATION})
    @GetMapping("/auth/refresh-token")
    public ResponseEntity<JWTToken> refreshToken() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();

        String newToken = tokenProvider.createToken(user);
        SecurityUtils.setAuthentication(user, newToken);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(newToken);
        return new ResponseEntity<>(new JWTToken(newToken), httpHeaders, HttpStatus.OK);
    }

    // TODO cleanup after testing
    @Secured({AuthoritiesConstants.USER})
    @GetMapping("/test/filter")
    public void testFilterWorkingOnApiCall() {
        log.debug("SUCCESFULL CALL");
    }

    @Secured({AuthoritiesConstants.USER})
    @GetMapping("/test/refresh-token")
    public void testFilterWorkingAfterRefreshToken() {
        log.debug("SUCCESFULL CALL AFTER TOKEN REFRESH");
    }

    /**
     * {@code GET /authenticate} : check if the user is authenticated, and return its login.
     *
     * @param principal the authentication principal.
     * @return the login if the user is authenticated.
     */
    @GetMapping(value = "/authenticate", produces = MediaType.TEXT_PLAIN_VALUE)
    public String isAuthenticated(Principal principal) {
        log.debug("REST request to check if the current user is authenticated");
        return principal == null ? null : principal.getName();
    }

    /**
     * Object to return as body in JWT Authentication.
     */
    static class JWTToken {

        private String idToken;

        JWTToken(String idToken) {
            this.idToken = idToken;
        }

        @JsonProperty("id_token")
        String getIdToken() {
            return idToken;
        }

        void setIdToken(String idToken) {
            this.idToken = idToken;
        }
    }
}
