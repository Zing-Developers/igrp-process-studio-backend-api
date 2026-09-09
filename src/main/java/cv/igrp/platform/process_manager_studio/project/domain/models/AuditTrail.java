package cv.igrp.platform.process_manager_studio.project.domain.models;

import java.time.LocalDateTime;

/**
 * Audit metadata carried from the entity's Spring auditing columns; set on a model after rebuild so
 * the factory signatures stay untouched. Null on freshly created models that were never persisted.
 */
public record AuditTrail(String createdBy, LocalDateTime createdAt,
                         String lastModifiedBy, LocalDateTime updatedAt) { }
