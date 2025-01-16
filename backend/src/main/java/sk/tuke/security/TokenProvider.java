package sk.tuke.security;

import com.google.common.base.Enums;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import sk.tuke.domain.User;
import sk.tuke.domain.enumeration.AuthRole;
import sk.tuke.service.dto.specific.UserTokenDetails;

import javax.crypto.SecretKey;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import static sk.tuke.service.utils.Constants.*;

@Component
@Slf4j
public class TokenProvider {

    private final SecretKey key;

    @Value("${jhipster.security.authentication.jwt.token-validity-in-seconds:86400}")
    private long tokenValidityInSeconds;

    public TokenProvider(@Value("${jhipster.security.authentication.jwt.base64-secret}") String base64Secret) {
        this.key = Keys.hmacShaKeyFor(java.util.Base64.getDecoder().decode(base64Secret));
    }

    public String createToken(String email, Long userId, AuthRole role) {

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + tokenValidityInSeconds * 1000);

        return Jwts.builder()
            .subject(email)
            .claim(ROLE_KEY, role.toString())
            .claim(USERID_KEY, userId)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(key)
            .compact();
    }

    public String createToken(User user) {

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + tokenValidityInSeconds * 1000);

        return Jwts.builder()
            .subject(user.getEmail())
            .claim(ROLE_KEY, user.getRole().toString())
            .claim(USERID_KEY, user.getId())
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(key)
            .compact();
    }

    private String getUserRole(String role) {
        String fixedRole = "ROLE_" + role;
        if (fixedRole.equals(AuthoritiesConstants.USER))
            return AuthoritiesConstants.USER;
        if (fixedRole.equals(AuthoritiesConstants.ADMIN))
            return AuthoritiesConstants.ADMIN;
        return "";
    }

    /**
     * Extract token from http request.
     *
     * @param request
     * @return the token
     */
    public String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean isTokenValid(String token) {
        if (!StringUtils.hasLength(token)) {
            log.debug("JWT si null or empty.");
            return false;
        }
        try {

            Jws<Claims> claims = Jwts.parser()
                .verifyWith(key)
                .build().parseSignedClaims(token);

            ZonedDateTime valid = ZonedDateTime
                .ofInstant(claims.getPayload().getExpiration().toInstant(), ZoneId.systemDefault());
            return !valid.isBefore(ZonedDateTime.now())
                && claims.getPayload().get(ROLE_KEY) != null
                && claims.getPayload().getSubject() != null;
        } catch (Exception e) {
            log.debug("Token validation failed: {}", e.getMessage());
            log.info("Invalid JWT token.");
            log.trace("Invalid JWT token trace.", e);
            return false;
        }
    }

    public UserTokenDetails getTokenClaims(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token).getPayload();

        UserTokenDetails details = new UserTokenDetails();
        details.setEmail(claims.getSubject());
        String role = claims.get(ROLE_KEY, String.class) != null ? claims.get(ROLE_KEY, String.class) : "";
        details.setRole(Enums.getIfPresent(AuthRole.class, role).orNull());
        details.setUserId(claims.get(USERID_KEY, Long.class));
        details.setPetitionId(claims.get(PETITION_ID_KEY, Integer.class));
        details.setSigneeId(claims.get(SIGNEE_ID_KEY, Integer.class));
        return details;
    }

    public String createTokenForUserRegistration(Long userId, String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + tokenValidityInSeconds * 10000);

        return Jwts.builder()
            .subject(email)
            .claim(USERID_KEY, userId)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(key)
            .compact();
    }

    public boolean isRegisterTokenValid(String token) {
        if (!StringUtils.hasLength(token)) {
            log.debug("JWT si null or empty.");
            return false;
        }
        try {

            Jws<Claims> claims = Jwts.parser()
                .verifyWith(key)
                .build().parseSignedClaims(token);

            ZonedDateTime valid = ZonedDateTime
                .ofInstant(claims.getPayload().getExpiration().toInstant(), ZoneId.systemDefault());
            return !valid.isBefore(ZonedDateTime.now())
                && claims.getPayload().get(USERID_KEY) != null
                && claims.getPayload().getSubject() != null;
        } catch (Exception e) {
            log.debug("Token validation failed: {}", e.getMessage());
            log.info("Invalid JWT token.");
            log.trace("Invalid JWT token trace.", e);
            return false;
        }
    }

    public String createPetitionSignToken(Long userId, String email, Integer petitionId, Integer signeeId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + tokenValidityInSeconds * 10000);

        return Jwts.builder()
            .subject(email)
            .claim(USERID_KEY, userId)
            .claim(PETITION_ID_KEY, petitionId)
            .claim(SIGNEE_ID_KEY, signeeId)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(key)
            .compact();
    }

    public boolean isPetitionSignTokenValid(String token) {
        if (!StringUtils.hasLength(token)) {
            log.debug("JWT si null or empty.");
            return false;
        }
        try {
            Jws<Claims> claims = Jwts.parser()
                .verifyWith(key)
                .build().parseSignedClaims(token);

            ZonedDateTime valid = ZonedDateTime
                .ofInstant(claims.getPayload().getExpiration().toInstant(), ZoneId.systemDefault());
            return !valid.isBefore(ZonedDateTime.now())
                && claims.getPayload().get(USERID_KEY) != null
                && claims.getPayload().get(PETITION_ID_KEY) != null;
        } catch (Exception e) {
            log.debug("Token validation failed: {}", e.getMessage());
            log.info("Invalid JWT token.");
            return false;
        }
    }

}
