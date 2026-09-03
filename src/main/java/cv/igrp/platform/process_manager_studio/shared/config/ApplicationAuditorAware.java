package cv.igrp.platform.process_manager_studio.shared.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Optional;

/**
 * Audits the raw JWT {@code sub} — the stable identifier, mirroring the management API's auditor.
 * The human-readable display comes from the userProfile* enrichment, never from this raw value.
 */
public class ApplicationAuditorAware implements AuditorAware<String> {

  @Override
  public Optional<String> getCurrentAuditor() {
    return Optional.of(getCurrentUserName());
  }

  /**
   * @return the JWT {@code sub} when present, otherwise the authentication name (covers M2M's
   * {@code m2m:<client>} principals), or {@code "system"} for unauthenticated calls such as
   * server-generated records
   */
  public String getCurrentUserName() {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()
        || "anonymousUser".equals(authentication.getPrincipal())) {
      return "system";
    }

    if (authentication instanceof JwtAuthenticationToken jwtAuth) {
      String sub = jwtAuth.getToken().getSubject();
      if (sub != null && !sub.isBlank()) {
        return sub;
      }
    }

    String name = authentication.getName();
    return (name == null || name.isBlank()) ? "system" : name;
  }

}
