package sk.tuke.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sk.tuke.domain.User;
import sk.tuke.repository.UserRepository;
import sk.tuke.service.dto.specific.UserTokenDetails;

import java.io.IOException;
import java.util.Optional;

@Component
@Slf4j
public class JwtAutenticationFilter extends OncePerRequestFilter {


    private final TokenProvider tokenProvider;

    private final UserRepository userRepository;

    public JwtAutenticationFilter (TokenProvider tokenProvider,
                                   UserRepository userRepository) {
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String jwt = tokenProvider.extractToken(request);

        if (!tokenProvider.isTokenValid(jwt)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            UserTokenDetails details = tokenProvider.getTokenClaims(jwt);
            Optional<User> user = userRepository.findOneByEmailIgnoreCase(details.getEmail());
            user.ifPresent(u -> SecurityUtils.setAuthentication(u, jwt));
            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            log.error("Unexpected error occurred on doFilterInternal JWT auth.");
            log.debug("Message: " + exception.getMessage());
        }
    }

}
