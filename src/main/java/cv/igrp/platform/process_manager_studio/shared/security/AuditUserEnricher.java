package cv.igrp.platform.process_manager_studio.shared.security;

import cv.igrp.platform.process_manager_studio.project.application.dto.ProcessDefinitionResponseDTO;
import cv.igrp.platform.process_manager_studio.project.application.dto.ProcessDefinitionResponseLightDTO;
import cv.igrp.platform.process_manager_studio.project.application.dto.ProjectResponseDTO;
import cv.igrp.platform.process_manager_studio.project.application.dto.ProjectResponseLigthDTO;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.entity.IAMUserProfileEntity;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.repository.IAMUserProfileEntityRepository;
import cv.igrp.platform.process_manager_studio.shared.security.m2m.UserProfileDTO;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Fills the {@code userProfileCreatedBy}/{@code userProfileLastModifiedBy} fields of response DTOs
 * from the IAM profile store — the platform's audit-user pattern (raw principal string + enriched
 * profile, null when no profile matches). One batch lookup per call, whatever the tree size.
 */
@Component
public class AuditUserEnricher {

  private final IAMUserProfileEntityRepository repository;

  public AuditUserEnricher(IAMUserProfileEntityRepository repository) {
    this.repository = repository;
  }

  public void enrichProjects(Collection<ProjectResponseDTO> projects) {
    if (projects == null || projects.isEmpty()) return;
    final var principals = new HashSet<String>();
    for (var p : projects) {
      addIfNotNull(principals, p.getCreatedBy());
      addIfNotNull(principals, p.getLastModifiedBy());
      if (p.getProcessDefinitions() != null) {
        for (var pd : p.getProcessDefinitions()) {
          addIfNotNull(principals, pd.getCreatedBy());
          addIfNotNull(principals, pd.getLastModifiedBy());
        }
      }
    }
    final var profiles = lookup(principals);
    for (var p : projects) {
      p.setUserProfileCreatedBy(profiles.get(p.getCreatedBy()));
      p.setUserProfileLastModifiedBy(profiles.get(p.getLastModifiedBy()));
      if (p.getProcessDefinitions() != null) {
        for (var pd : p.getProcessDefinitions()) {
          pd.setUserProfileCreatedBy(profiles.get(pd.getCreatedBy()));
          pd.setUserProfileLastModifiedBy(profiles.get(pd.getLastModifiedBy()));
        }
      }
    }
  }

  public void enrichProcessDefinitions(Collection<ProcessDefinitionResponseDTO> definitions) {
    if (definitions == null || definitions.isEmpty()) return;
    final var principals = new HashSet<String>();
    for (var pd : definitions) {
      addIfNotNull(principals, pd.getCreatedBy());
      addIfNotNull(principals, pd.getLastModifiedBy());
    }
    final var profiles = lookup(principals);
    for (var pd : definitions) {
      pd.setUserProfileCreatedBy(profiles.get(pd.getCreatedBy()));
      pd.setUserProfileLastModifiedBy(profiles.get(pd.getLastModifiedBy()));
    }
  }

  public void enrichLightProjects(Collection<ProjectResponseLigthDTO> projects) {
    if (projects == null || projects.isEmpty()) return;
    final var principals = new HashSet<String>();
    for (var p : projects) {
      addIfNotNull(principals, p.getCreatedBy());
      addIfNotNull(principals, p.getLastModifiedBy());
      if (p.getProcessDefinitions() != null) {
        for (var pd : p.getProcessDefinitions()) {
          addIfNotNull(principals, pd.getCreatedBy());
          addIfNotNull(principals, pd.getLastModifiedBy());
        }
      }
    }
    final var profiles = lookup(principals);
    for (var p : projects) {
      p.setUserProfileCreatedBy(profiles.get(p.getCreatedBy()));
      p.setUserProfileLastModifiedBy(profiles.get(p.getLastModifiedBy()));
      if (p.getProcessDefinitions() != null) {
        for (var pd : p.getProcessDefinitions()) {
          pd.setUserProfileCreatedBy(profiles.get(pd.getCreatedBy()));
          pd.setUserProfileLastModifiedBy(profiles.get(pd.getLastModifiedBy()));
        }
      }
    }
  }

  public void enrichLightProcessDefinitions(Collection<ProcessDefinitionResponseLightDTO> definitions) {
    if (definitions == null || definitions.isEmpty()) return;
    final var principals = new HashSet<String>();
    for (var pd : definitions) {
      addIfNotNull(principals, pd.getCreatedBy());
      addIfNotNull(principals, pd.getLastModifiedBy());
    }
    final var profiles = lookup(principals);
    for (var pd : definitions) {
      pd.setUserProfileCreatedBy(profiles.get(pd.getCreatedBy()));
      pd.setUserProfileLastModifiedBy(profiles.get(pd.getLastModifiedBy()));
    }
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
