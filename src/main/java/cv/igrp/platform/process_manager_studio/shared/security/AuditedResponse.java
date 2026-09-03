package cv.igrp.platform.process_manager_studio.shared.security;

import cv.igrp.platform.process_manager_studio.shared.security.m2m.UserProfileDTO;

import java.time.LocalDateTime;

/**
 * A response DTO carrying the platform audit trio — time, raw principal, enriched profile. Lombok's
 * {@code @Data} on the DTOs provides every method; the interface only lets one enricher and one mapper
 * helper treat all of them alike.
 */
public interface AuditedResponse {

  String getCreatedBy();
  void setCreatedBy(String createdBy);

  String getLastModifiedBy();
  void setLastModifiedBy(String lastModifiedBy);

  LocalDateTime getCreatedAt();
  void setCreatedAt(LocalDateTime createdAt);

  LocalDateTime getUpdatedAt();
  void setUpdatedAt(LocalDateTime updatedAt);

  void setUserProfileCreatedBy(UserProfileDTO profile);
  void setUserProfileLastModifiedBy(UserProfileDTO profile);

}
