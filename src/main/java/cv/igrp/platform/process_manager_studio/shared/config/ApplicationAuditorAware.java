package cv.igrp.platform.process_manager_studio.shared.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Optional;

public class ApplicationAuditorAware implements AuditorAware<String> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationAuditorAware.class);

  @Override
  public Optional<String> getCurrentAuditor() {
    return Optional.of(getCurrentUserName());
  }

  /**
   * Resolves the current user name from the authentication principal.
   * The principal is set to the claim named by {@code igrp.security.principal-claim-name}
   * by JwtAuthenticationConverter. If that claim is missing from the JWT, falls back to:
   * preferred_username → sub.
   *
   * @return the current user name, or {@code "system"} for unauthenticated calls such as
   * server-generated records
   */
  public String getCurrentUserName() {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()
        || "anonymousUser".equals(authentication.getPrincipal())) {
      // Fallback when no user is authenticated (e.g., server-generated records)
      return "system";
    }

    // Primary: principal name (set from the configured principal claim by JwtAuthenticationConverter)
    String name = authentication.getName();
    if (name != null && !name.isBlank()) {
      return name;
    }

    // Fallback: extract from JWT claims when the configured claim is missing
    if (authentication instanceof JwtAuthenticationToken jwtAuth) {
      Jwt jwt = jwtAuth.getToken();

      String preferredUsername = jwt.getClaimAsString("preferred_username");
      if (preferredUsername != null && !preferredUsername.isBlank()) {
        LOGGER.warn("JWT missing the configured principal claim, falling back to 'preferred_username'");
        return preferredUsername;
      }

      String sub = jwt.getSubject();
      LOGGER.warn("JWT missing the configured principal claim and 'preferred_username', falling back to 'sub' [{}]",
          sub);
      return sub;
    }

    return name;
  }

}
