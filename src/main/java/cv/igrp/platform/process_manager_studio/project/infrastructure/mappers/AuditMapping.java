package cv.igrp.platform.process_manager_studio.project.infrastructure.mappers;

import cv.igrp.platform.process_manager_studio.project.domain.models.AuditTrail;
import cv.igrp.platform.process_manager_studio.shared.config.AuditEntity;
import cv.igrp.platform.process_manager_studio.shared.security.AuditedResponse;

/** Entity → model → DTO audit plumbing shared by every mapper in this package. */
final class AuditMapping {

  private AuditMapping() { }

  static AuditTrail trail(AuditEntity entity) {
    return new AuditTrail(entity.getCreatedBy(), entity.getCreatedDate(),
        entity.getLastModifiedBy(), entity.getLastModifiedDate());
  }

  /** No-op when the model was never persisted (null trail) — the fields simply stay null. */
  static void apply(AuditedResponse dto, AuditTrail trail) {
    if (trail == null) return;
    dto.setCreatedBy(trail.createdBy());
    dto.setCreatedAt(trail.createdAt());
    dto.setLastModifiedBy(trail.lastModifiedBy());
    dto.setUpdatedAt(trail.updatedAt());
  }

}
