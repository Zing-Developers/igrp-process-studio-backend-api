package cv.igrp.platform.process_manager_studio.project.infrastructure.mappers;

import cv.igrp.platform.process_manager_studio.project.application.dto.ProcessVariableResponseDTO;
import cv.igrp.platform.process_manager_studio.project.domain.models.ProcessVariable;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProcessDefinitionId;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProcessVariableId;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.entity.ProcessDefinitionEntity;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.entity.ProcessVariableEntity;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProcessVariableMapper {


  private final EntityManager entityManager;

  public ProcessVariableMapper(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public ProcessVariable toDomain(ProcessVariableEntity entity) {
    if (entity == null) {
      return null;
    }

    var model = ProcessVariable.rebuild(
        ProcessVariableId.of(entity.getId()),
        entity.getName(),
        entity.getType(),
        entity.getDefaultValue(),
        entity.isRequired(),
        ProcessDefinitionId.of(entity.getProcesDefinitionId().getId())
    );
    model.setAudit(AuditMapping.trail(entity));
    return model;
  }


  public ProcessVariableEntity toEntity(ProcessVariable domain) {
    if (domain == null) {
      return null;
    }
    ProcessVariableEntity entity = new ProcessVariableEntity();
    entity.setId(domain.getId().identifier().value());
    entity.setName(domain.getName());
    entity.setType(domain.getType());
    entity.setDefaultValue(domain.getDefaultValue());
    entity.setRequired(domain.isRequired());

    entity.setProcesDefinitionId(entityManager
        .getReference(ProcessDefinitionEntity.class, domain.getProcessDefinitionId().identifier().value()));

    return entity;
  }

  public ProcessVariableResponseDTO toDTO(ProcessVariable domain) {
    if (domain == null) return null;

    ProcessVariableResponseDTO dto = new ProcessVariableResponseDTO();
    dto.setId(domain.getId().identifier().getValueAsString());
    dto.setName(domain.getName());
    dto.setType(domain.getType());
    dto.setDefaultValue(domain.getDefaultValue());
    dto.setRequired(domain.isRequired());
    dto.setProcessDefinitionId(domain.getProcessDefinitionId().identifier().getValueAsString());
    AuditMapping.apply(dto, domain.getAudit());

    return dto;
  }

  public List<ProcessVariableResponseDTO> toDTO(List<ProcessVariable> domains) {
    if (domains == null || domains.isEmpty()) {
      return Collections.emptyList();
    }

    return domains.stream()
        .map(this::toDTO)
        .collect(Collectors.toList());
  }

}
