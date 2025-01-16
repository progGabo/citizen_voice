package sk.tuke.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import sk.tuke.security.JwtAutenticationFilter;
import tech.jhipster.config.JHipsterProperties;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

    private final JHipsterProperties jHipsterProperties;

    private final JwtAutenticationFilter jwtAutenticationFilter;

    public SecurityConfiguration(JHipsterProperties jHipsterProperties,
                                 JwtAutenticationFilter jwtAutenticationFilter) {
        this.jHipsterProperties = jHipsterProperties;
        this.jwtAutenticationFilter = jwtAutenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        http
            .cors(cors -> cors.disable())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz ->
                authz
                    .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/authenticate")).permitAll()
                    .requestMatchers(mvc.pattern("/api/**")).permitAll()
                    .anyRequest().permitAll()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .accessDeniedHandler(new CustomAccessDeniedHandler()))
            .httpBasic(httpBasic -> httpBasic.disable())
            .addFilterAt(jwtAutenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
//        http
//            .cors(withDefaults())
//            .csrf(csrf -> csrf.disable())
//            .authorizeHttpRequests(authz ->
//                // prettier-ignore
//                authz
//                    .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/authenticate")).permitAll()
//                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/authenticate")).permitAll()
//                    .requestMatchers(mvc.pattern("/api/register")).permitAll()
//                    .requestMatchers(mvc.pattern("/api/activate")).permitAll()
//                    .requestMatchers(mvc.pattern("/api/account/reset-password/init")).permitAll()
//                    .requestMatchers(mvc.pattern("/api/account/reset-password/finish")).permitAll()
//                    .requestMatchers(mvc.pattern("/api/admin/**")).hasAuthority(AuthoritiesConstants.ADMIN)
//                    .requestMatchers(mvc.pattern("/api/**")).authenticated()
//                    .requestMatchers(mvc.pattern("/v3/api-docs/**")).hasAuthority(AuthoritiesConstants.ADMIN)
//                    .requestMatchers(mvc.pattern("/management/health")).permitAll()
//                    .requestMatchers(mvc.pattern("/management/health/**")).permitAll()
//                    .requestMatchers(mvc.pattern("/management/info")).permitAll()
//                    .requestMatchers(mvc.pattern("/management/prometheus")).permitAll()
//                    .requestMatchers(mvc.pattern("/management/**")).hasAuthority(AuthoritiesConstants.ADMIN)
//            )
//            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//            .exceptionHandling(exceptions ->
//                exceptions
//                    .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
//                    .accessDeniedHandler(new BearerTokenAccessDeniedHandler())
//            )
//            .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()));
//        return http.build();
//    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }
}
