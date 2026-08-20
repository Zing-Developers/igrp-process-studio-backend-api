package cv.igrp.platform.process_manager_studio.shared.security;

import cv.igrp.framework.process.runtime.auth.core.adapter.IAuthorizationServiceAdapter;
import cv.igrp.framework.process.runtime.auth.core.adapter.IRouteAuthorizationAdapter;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authorization.AuthorizationEventPublisher;
import org.springframework.security.authorization.SpringAuthorizationEventPublisher;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.TokenExchangeOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.cors.CorsConfiguration;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Security configuration class for setting up OAuth2 and JWT authentication with Keycloak.
 * This class defines the security filter chain, authority enrichment, and the per-route permission rules.
 *
 * <p>Business routes are never listed here: the route table lives in configuration and is turned into
 * rules by the route authorization adapter. See docs/SPEC_ROUTE_AUTHORIZATION.md.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfig.class);

    private static final String ROLE_PREFIX = "ROLE_";
    private static final String SUPER_ADMIN_ROLE = "DEPT_IGRP.superadmin";

    private final IAuthorizationServiceAdapter authorizationService;

    private final IRouteAuthorizationAdapter routeAuthorization;

    private final String principalClaimName;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String jwtIssuer;

    public SecurityConfig(IAuthorizationServiceAdapter authorizationService,
                          IRouteAuthorizationAdapter routeAuthorization,
                          @Value("${igrp.security.principal-claim-name}") String principalClaimName) {
        this.authorizationService = authorizationService;
        this.routeAuthorization = routeAuthorization;
        this.principalClaimName = principalClaimName;
    }

    /**
     * Configures the security filter chain, enabling OAuth2 resource server with JWT and specifying
     * which requests require which permission.
     *
     * @param http the {@link HttpSecurity} object to configure security settings
     * @return the configured {@link SecurityFilterChain} instance
     * @throws Exception if an error occurs while configuring the security
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        /*
          Creates and configures a CORS filter.
          The filter allows requests from the specified origin, allows all headers and methods,
          and supports credentials in cross-origin requests.
        */
        http.cors(cors -> cors.configurationSource(request -> {
          var configuration = new CorsConfiguration();
          configuration.addAllowedOriginPattern(CorsConfiguration.ALL);
          configuration.addAllowedMethod(HttpMethod.GET);
          configuration.addAllowedMethod(HttpMethod.POST);
          configuration.addAllowedMethod(HttpMethod.PUT);
          configuration.addAllowedMethod(HttpMethod.PATCH);
          configuration.addAllowedMethod(HttpMethod.DELETE);
          configuration.addAllowedMethod(HttpMethod.HEAD);
          configuration.addAllowedMethod(HttpMethod.OPTIONS);
          configuration.addAllowedHeader(CorsConfiguration.ALL);
          configuration.setAllowCredentials(true);
          return configuration;
        }));

        // Configure OAuth2 Resource Server to use JWT tokens for authentication
        http.oauth2ResourceServer((oauth2ResourceServer) -> oauth2ResourceServer
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
        );

        // Configure authorization rules. No GET is public any more: every business route needs a permission.
        http
                .authorizeHttpRequests((authorize) -> {

                    // Error dispatches must stay reachable, otherwise denyAll() turns every error into a 403
                    authorize.requestMatchers(request -> request.getDispatcherType() == DispatcherType.ERROR).permitAll();

                    authorize.requestMatchers(
                            "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
                            "/swagger-resources/**", "/webjars/**",
                            "/actuator/health", "/actuator/health/**"
                    ).permitAll();

                    routeAuthorization.getRules().forEach(rule -> {
                        var matcher = rule.method() == null
                                ? authorize.requestMatchers(rule.pattern())
                                : authorize.requestMatchers(rule.method(), rule.pattern());
                        matcher.hasAnyAuthority(withSuperAdmin(rule.anyAuthority()));
                    });

                    if (routeAuthorization.denyUnmatched()) {
                        authorize.anyRequest().denyAll();
                    } else {
                        authorize.anyRequest().authenticated();
                    }
                })
                .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) -> {
                    // DEBUG, not WARN: anonymous probes and expired tokens are routine noise
                    LOGGER.debug("Unauthenticated request: {} {}", request.getMethod(), request.getRequestURI());
                    response.addHeader(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"Restricted Content\"");
                    response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
                }));

        // Set session management to stateless (no session created for API requests)
        http.sessionManagement(t -> t.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Stateless bearer API: CSRF tokens don't apply. Without this, every POST/PUT/PATCH is
        // rejected with 403 before the permission rules are even consulted.
        http.csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    /**
     * Publishes authorization decisions as application events. Spring Security only publishes denials
     * through this publisher, which {@link AuthorizationAuditListener} turns into structured audit logs.
     */
    @Bean
    public AuthorizationEventPublisher authorizationEventPublisher(ApplicationEventPublisher publisher) {
        return new SpringAuthorizationEventPublisher(publisher);
    }

    /**
     * Adds the super admin role to a rule's accepted authorities, so the role does not have to be
     * repeated in every entry of the route table.
     */
    private static String[] withSuperAdmin(Set<String> authorities) {
        var accepted = new LinkedHashSet<>(authorities);
        accepted.add(ROLE_PREFIX + SUPER_ADMIN_ROLE);
        return accepted.toArray(String[]::new);
    }

    /**
     * Configures a JWT authentication converter whose authorities come from the authorization adapter
     * rather than from the token's own claims.
     *
     * <p>The adapter receives the current request as well as the token, because the IRN adapter
     * identifies the caller by the session cookie and not by the bearer token.
     *
     * @return the {@link JwtAuthenticationConverter} used to convert JWT tokens to Spring Security authentication
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        var converter = new JwtAuthenticationConverter();

        converter.setPrincipalClaimName(principalClaimName);

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {

            HttpServletRequest request =
                    ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder
                            .getRequestAttributes()))
                            .getRequest();

            Set<GrantedAuthority> authorities = new HashSet<>();
            final String token = jwt.getTokenValue();

            try {

                authorizationService
                        .getActiveGroups(token, request)
                        .forEach(g -> authorities.add(new SimpleGrantedAuthority(
                                g.startsWith(ROLE_PREFIX) ? g : ROLE_PREFIX + g)));

                authorizationService
                        .getPermissions(token, request)
                        .forEach(p -> authorities.add(new SimpleGrantedAuthority(p)));

                if (authorizationService.isSuperAdmin(token, request)) {
                    authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + SUPER_ADMIN_ROLE));
                }

            } catch (Exception e) {
                // Fail closed: no authority at all, so every route answers 403 until the IdP recovers.
                // There is no Activiti engine here, so no minimal role has to be preserved.
                LOGGER.error("SECURITY: failed to enrich authorities for [sub={}]; denying all routes",
                        jwt.getSubject(), e);
                authorities.clear();
            }

            LOGGER.debug("Authorities: {}", authorities);

            return authorities;
        });

        return converter;
    }

    /**
     * Configures a JWT decoder to verify and decode JWT tokens.
     *
     * @return the {@link JwtDecoder} for JWT token validation
     */
    @Bean
    @Profile("!development & !staging")
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withIssuerLocation(jwtIssuer).build();
    }

    /**
     * Creates a bean for an OAuth2AuthorizedClientProvider that supports token exchange.
     *
     * <p>Token exchange allows one token to be exchanged for another,
     * typically used in scenarios where a client needs to act on behalf
     * of a user or service in a federated identity environment.</p>
     *
     * @return An instance of TokenExchangeOAuth2AuthorizedClientProvider.
    */
    @Bean
    public OAuth2AuthorizedClientProvider tokenExchange() {
        return new TokenExchangeOAuth2AuthorizedClientProvider();
    }

}
