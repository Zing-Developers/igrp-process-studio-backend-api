package cv.igrp.platform.process_manager_studio.shared.security;

import cv.igrp.platform.process_manager_studio.project.application.dto.UserProfileDTO;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.entity.IAMUserProfileEntity;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.repository.IAMUserProfileEntityRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Audit-user enrichment shared by the security consoles (M2M keys, email access mappings): a stored
 * principal may be a sub or an email, so both are tried in one batch lookup. Dates follow the platform's
 * zone-less LocalDateTime (see AuditEntity).
 */
@Component
public class AuditPrincipals {

  private final IAMUserProfileEntityRepository userProfileRepository;

  public AuditPrincipals(IAMUserProfileEntityRepository userProfileRepository) {
    this.userProfileRepository = userProfileRepository;
  }

  public static LocalDateTime local(Instant instant) {
    return instant == null ? null : LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
  }

  /** Profiles keyed by both sub and email; absent principals simply have no entry. */
  public Map<String, UserProfileDTO> profilesOf(Set<String> principals) {
    final var lookup = new HashMap<String, UserProfileDTO>();
    final var keys = principals.stream().filter(Objects::nonNull).collect(Collectors.toSet());
    if (keys.isEmpty()) {
      return lookup;
    }
    for (IAMUserProfileEntity p : userProfileRepository.findBySubInOrEmailIn(keys)) {
      final var dto = new UserProfileDTO(p.getId(), p.getUsername(), p.getEmail(),
          p.getFirstName(), p.getLastName(), p.getFullName(), p.getSub());
      if (p.getSub() != null) lookup.put(p.getSub(), dto);
      if (p.getEmail() != null) lookup.put(p.getEmail(), dto);
    }
    return lookup;
  }

  public UserProfileDTO profileOf(String principal) {
    return principal == null ? null : profilesOf(Set.of(principal)).get(principal);
  }

}
