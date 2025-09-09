package cv.igrp.platform.process_manager_studio.shared.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class ApplicationAuditorAware implements AuditorAware<String> {

  @Value("${spring.profiles.active}")
  private String activeProfile;

  @Override
  public Optional<String> getCurrentAuditor() {
    var preferredUsername = getPreferredUsername();
    return Optional.of(preferredUsername);
    }

    /**
    * Retrieves the preferred username from the JWT token in the security context.
    *
    * @return preferred username
    * @throws IllegalStateException if the JWT token is not found in the security context
    */
    public String getPreferredUsername() {

    if ("development".equals(activeProfile) || "staging".equals(activeProfile)) {
    return "";
    }

    var authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()
    || "anonymousUser".equals(authentication.getPrincipal())) {
    // Fallback when no user is authenticated (e.g., server-generated records)
    return "system";
    }

    return authentication.getName();
    }

    }
