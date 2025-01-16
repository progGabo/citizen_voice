package sk.tuke.security;

import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sk.tuke.domain.User;
import sk.tuke.repository.UserRepository;

import java.util.Locale;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
public class DomainUserDetailsService implements UserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(DomainUserDetailsService.class);

    private final UserRepository userRepository;

    public DomainUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(final String email) {
        LOG.debug("Authenticating {}", email);

        if (new EmailValidator().isValid(email, null)) {
            return userRepository
                .findOneWithRoleByEmailIgnoreCase(email)
                .map(user -> createSpringSecurityUser(email, user))
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " was not found in the database"));
        }

        String lowercaseLogin = email.toLowerCase(Locale.ENGLISH);
        return userRepository
            .findOneWithRoleByEmail(lowercaseLogin)
            .map(user -> createSpringSecurityUser(lowercaseLogin, user))
            .orElseThrow(() -> new UsernameNotFoundException("User " + lowercaseLogin + " was not found in the database"));
    }

    private org.springframework.security.core.userdetails.User createSpringSecurityUser(String lowercaseLogin, User user) {
//        if (!user.isActivated()) {
//            throw new UserNotActivatedException("User " + lowercaseLogin + " was not activated");
//        }
//        List<SimpleGrantedAuthority> authority = List.of(new SimpleGrantedAuthority(user.getRole()));
//
//        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPasswd(), authority);
        return null;
    }
}
