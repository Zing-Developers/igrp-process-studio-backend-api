package cv.igrp.platform.process_manager_studio.shared.security;

import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.entity.IAMUserProfileEntity;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.repository.IAMUserProfileEntityRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

/**
 * Keeps the local IAM profile table in sync with the JWT claims of authenticated users — lean port
 * of the management API's filter, so audit users resolve to enriched profiles here too. Skips
 * non-JWT auth (machines have no profile). Sync failures never fail the request.
 */
@Component
public class IAMUserProfileSyncFilter extends OncePerRequestFilter {

  private static final Logger LOGGER = LoggerFactory.getLogger(IAMUserProfileSyncFilter.class);

  private final IAMUserProfileEntityRepository repository;

  public IAMUserProfileSyncFilter(IAMUserProfileEntityRepository repository) {
    this.repository = repository;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain chain) throws ServletException, IOException {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth instanceof JwtAuthenticationToken jwtAuth) {
      try {
        syncUserProfile(jwtAuth.getToken());
      } catch (Exception e) {
        LOGGER.warn("Failed to sync IAM user profile. sub={}, error={}", jwtAuth.getToken().getSubject(), e.getMessage());
      }
    }
    chain.doFilter(request, response);
  }

  private void syncUserProfile(Jwt jwt) {
    String sub = jwt.getSubject();
    if (!StringUtils.hasText(sub)) {
      return;
    }
    String email = jwt.getClaimAsString("email");
    String username = resolveUsername(jwt);
    if (!StringUtils.hasText(username)) {
      return;
    }
    String firstName = jwt.getClaimAsString("given_name");
    String lastName = jwt.getClaimAsString("family_name");

    var existing = repository.findBySubOrEmail(sub, email).orElse(null);
    if (existing == null) {
      var entity = new IAMUserProfileEntity();
      entity.setId(resolveIdentifier(sub));
      entity.setSub(sub);
      entity.setUsername(username);
      entity.setEmail(email);
      entity.setFirstName(firstName);
      entity.setLastName(lastName);
      repository.save(entity);
      return;
    }

    boolean changed = !username.equals(existing.getUsername())
        || !sub.equals(existing.getSub())
        || !Objects.equals(email, existing.getEmail())
        || !Objects.equals(firstName, existing.getFirstName())
        || !Objects.equals(lastName, existing.getLastName());
    if (changed) {
      existing.setSub(sub);
      existing.setUsername(username);
      existing.setEmail(email);
      existing.setFirstName(firstName);
      existing.setLastName(lastName);
      repository.save(existing);
    }
  }

  private String resolveUsername(Jwt jwt) {
    String preferred = jwt.getClaimAsString("preferred_username");
    return StringUtils.hasText(preferred) ? preferred : jwt.getClaimAsString("email");
  }

  private UUID resolveIdentifier(String sub) {
    try {
      return UUID.fromString(sub);
    } catch (IllegalArgumentException ignored) {
      return UUID.randomUUID();
    }
  }

}
