package cv.igrp.platform.process_manager_studio.shared.security.m2m;

import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.entity.IAMUserProfileEntity;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.repository.IAMUserProfileEntityRepository;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.entity.M2mApiKeyEntity;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.repository.M2mApiKeyEntityRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Management operations over M2M API keys (docs/SPEC_M2M_AUTHORIZATION.md §6.2). Super-admin only —
 * enforced at the route gate in SecurityConfig, structurally out of reach of any M2M key.
 */
@Service
public class M2mKeyService {

  /** MODULE:action — role/group strings can never be granted through the permissions column (M-11). */
  private static final Pattern PERMISSION_FORMAT = Pattern.compile("^[A-Z0-9_.]+:[a-z_]+$");
  private static final Pattern CLIENT_NAME_FORMAT = Pattern.compile("^[a-z0-9._-]+$");

  private final M2mApiKeyEntityRepository repository;
  private final M2mKeyCodec codec;
  private final IAMUserProfileEntityRepository userProfileRepository;
  private final Duration rotateGrace;

  // Audit users follow the platform pattern: raw principal string + enriched profile (null when
  // no IAM profile row matches).
  public record CreatedKey(UUID id, String clientName, String plaintextKey, String createdBy,
                           UserProfileDTO userProfileCreatedBy) { }
  public record KeySummary(UUID id, String clientName, String keyPrefix, String permissions,
                           String email, boolean active, Instant expiresAt, Instant createdAt,
                           String createdBy, UserProfileDTO userProfileCreatedBy,
                           Instant lastUsedAt, Instant revokedAt,
                           String revokedBy, UserProfileDTO userProfileRevokedBy) { }

  public M2mKeyService(M2mApiKeyEntityRepository repository,
                       M2mKeyCodec codec,
                       IAMUserProfileEntityRepository userProfileRepository,
                       @Value("${igrp.authorization.m2m.rotate-grace:7d}") Duration rotateGrace) {
    this.repository = repository;
    this.codec = codec;
    this.userProfileRepository = userProfileRepository;
    this.rotateGrace = rotateGrace;
  }

  @Transactional
  public CreatedKey create(String clientName, List<String> permissions, String email,
                           Instant expiresAt, String createdBy) {

    if (clientName == null || !CLIENT_NAME_FORMAT.matcher(clientName).matches()) {
      throw new IllegalArgumentException("client_name must be a slug (lowercase letters, digits, . _ -)");
    }
    if (permissions == null || permissions.isEmpty()) {
      throw new IllegalArgumentException("at least one permission is required");
    }
    for (String permission : permissions) {
      if (permission == null || !PERMISSION_FORMAT.matcher(permission.trim()).matches()) {
        throw new IllegalArgumentException(
            "invalid permission '" + permission + "': expected MODULE:action (roles are not allowed)");
      }
    }

    final var plaintext = codec.newKey();
    final var entity = new M2mApiKeyEntity();
    entity.setId(UUID.randomUUID());
    entity.setClientName(clientName);
    entity.setKeyPrefix(codec.prefixOf(plaintext));
    entity.setKeyHash(codec.hash(plaintext));
    entity.setPermissions(String.join(",", permissions.stream().map(String::trim).toList()));
    entity.setEmail(email);
    entity.setActive(true);
    entity.setExpiresAt(expiresAt);
    entity.setCreatedBy(createdBy);
    entity.setCreatedAt(Instant.now());
    repository.save(entity);

    return new CreatedKey(entity.getId(), clientName, plaintext, createdBy, profileOf(createdBy));
  }

  @Transactional(readOnly = true)
  public List<KeySummary> list() {
    final var entities = repository.findAll();
    final var principals = entities.stream()
        .flatMap(e -> Stream.of(e.getCreatedBy(), e.getRevokedBy()))
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
    final var profiles = profilesOf(principals);
    return entities.stream()
        .map(e -> new KeySummary(e.getId(), e.getClientName(), e.getKeyPrefix(), e.getPermissions(),
            e.getEmail(), e.isActive(), e.getExpiresAt(), e.getCreatedAt(), e.getCreatedBy(),
            profiles.get(e.getCreatedBy()), e.getLastUsedAt(), e.getRevokedAt(),
            e.getRevokedBy(), profiles.get(e.getRevokedBy())))
        .toList();
  }

  /** Batch audit-user enrichment: a stored principal may be a sub or an email, so both are tried. */
  private Map<String, UserProfileDTO> profilesOf(Set<String> principals) {
    final var lookup = new HashMap<String, UserProfileDTO>();
    if (principals.isEmpty()) {
      return lookup;
    }
    for (IAMUserProfileEntity p : userProfileRepository.findBySubInOrEmailIn(principals)) {
      final var dto = new UserProfileDTO(p.getId(), p.getUsername(), p.getEmail(),
          p.getFirstName(), p.getLastName(), p.getFullName(), p.getSub());
      if (p.getSub() != null) lookup.put(p.getSub(), dto);
      if (p.getEmail() != null) lookup.put(p.getEmail(), dto);
    }
    return lookup;
  }

  private UserProfileDTO profileOf(String principal) {
    return principal == null ? null : profilesOf(Set.of(principal)).get(principal);
  }

  @Transactional
  public void revoke(UUID id, String revokedBy) {
    final var entity = repository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("m2m key not found: " + id));
    entity.setActive(false);
    entity.setRevokedAt(Instant.now());
    entity.setRevokedBy(revokedBy);
    repository.save(entity);
  }

  /**
   * Issues a fresh key for the same client/permissions and stamps the old one with a forced expiry
   * ({@code now + rotate-grace}) so the overlap cannot live forever (M-18).
   */
  @Transactional
  public CreatedKey rotate(UUID id, String createdBy) {
    final var old = repository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("m2m key not found: " + id));

    final var replacement = create(
        old.getClientName(),
        List.of(old.getPermissions().split(",")),
        old.getEmail(),
        null,
        createdBy);

    old.setExpiresAt(Instant.now().plus(rotateGrace));
    repository.save(old);

    return replacement;
  }

}
