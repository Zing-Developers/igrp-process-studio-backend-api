package cv.igrp.platform.process_manager_studio.shared.security;

import cv.igrp.platform.process_manager_studio.project.application.dto.ProcessArtifactResponseDTO;
import cv.igrp.platform.process_manager_studio.project.application.dto.ProcessDefinitionResponseDTO;
import cv.igrp.platform.process_manager_studio.project.application.dto.ProjectResponseDTO;
import cv.igrp.platform.process_manager_studio.project.application.dto.ProjectResponseLigthDTO;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.entity.IAMUserProfileEntity;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.repository.IAMUserProfileEntityRepository;
import cv.igrp.platform.process_manager_studio.shared.security.m2m.UserProfileDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Fills the {@code userProfileCreatedBy}/{@code userProfileLastModifiedBy} fields of response DTOs
 * from the IAM profile store — the platform's audit-user pattern (raw principal string + enriched
 * profile, null when no profile matches). Walks the whole response tree (project → definitions →
 * artifacts → variables) and does one batch lookup, whatever the tree size.
 */
@Component
public class AuditUserEnricher {

  private final IAMUserProfileEntityRepository repository;

  public AuditUserEnricher(IAMUserProfileEntityRepository repository) {
    this.repository = repository;
  }

  public void enrich(Collection<? extends AuditedResponse> roots) {
    if (roots == null || roots.isEmpty()) return;

    final var all = new ArrayList<AuditedResponse>();
    for (var root : roots) collect(root, all);

    final var principals = new HashSet<String>();
    for (var node : all) {
      addIfNotNull(principals, node.getCreatedBy());
      addIfNotNull(principals, node.getLastModifiedBy());
    }
    final var profiles = lookup(principals);
    for (var node : all) {
      node.setUserProfileCreatedBy(profiles.get(node.getCreatedBy()));
      node.setUserProfileLastModifiedBy(profiles.get(node.getLastModifiedBy()));
    }
  }

  private static void collect(AuditedResponse node, List<AuditedResponse> out) {
    if (node == null) return;
    out.add(node);
    final Collection<? extends AuditedResponse> children = switch (node) {
      case ProjectResponseDTO p -> p.getProcessDefinitions();
      case ProjectResponseLigthDTO p -> p.getProcessDefinitions();
      case ProcessDefinitionResponseDTO pd -> pd.getProcessArtifacts();
      case ProcessArtifactResponseDTO a -> a.getArtifactVariables();
      default -> List.of();
    };
    if (children != null) for (var child : children) collect(child, out);
  }

  /** A stored principal may be a sub, an email or a username — all three are tried. */
  private Map<String, UserProfileDTO> lookup(Set<String> principals) {
    final var result = new HashMap<String, UserProfileDTO>();
    if (principals.isEmpty()) return result;
    final List<IAMUserProfileEntity> found = repository.findBySubInOrEmailInOrUsernameIn(principals);
    for (var p : found) {
      final var dto = new UserProfileDTO(p.getId(), p.getUsername(), p.getEmail(),
          p.getFirstName(), p.getLastName(), p.getFullName(), p.getSub());
      if (p.getSub() != null) result.put(p.getSub(), dto);
      if (p.getEmail() != null) result.put(p.getEmail(), dto);
      if (p.getUsername() != null) result.put(p.getUsername(), dto);
    }
    return result;
  }

  private static void addIfNotNull(Set<String> set, String value) {
    if (value != null && !value.isBlank()) set.add(value);
  }

}
