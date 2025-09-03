package cv.igrp.platform.process_manager_studio.project.infrastructure.mappers;

import cv.igrp.platform.process_manager_studio.project.application.dto.ArtifactVariableResponseDTO;
import cv.igrp.platform.process_manager_studio.project.application.dto.ProcessArtifactResponseDTO;
import cv.igrp.platform.process_manager_studio.project.domain.models.ProcessArtifact;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProcessDefinitionId;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProcessArtifactId;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.entity.ArtifactVariableEntity;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.entity.ProcessDefinitionEntity;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.entity.ProcessArtifactEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProcessArtifactMapper {

  private final ArtifactVariableMapper artifactVariableMapper;

  public ProcessArtifactMapper(ArtifactVariableMapper artifactVariableMapper) {
    this.artifactVariableMapper = artifactVariableMapper;
  }

  public ProcessArtifact toDomain(ProcessArtifactEntity entity) {
    if (entity == null) {
      return null;
    }

    List<ArtifactVariableEntity> variableEntities = entity.getVariables() != null ? entity.getVariables() : Collections.emptyList();

    return ProcessArtifact.rebuild(
        ProcessArtifactId.of(entity.getId().toString()),
        ProcessDefinitionId.of(entity.getProcesDefinitionId().getId().toString()),
        entity.getTaskKey(),
        entity.getName(),
        Boolean.TRUE.equals(entity.isSubprocessTask()),
        entity.getSubprocessId(),
        entity.getSubprocessName(),
        variableEntities.stream()
            .map(artifactVariableMapper::toDomain)
            .collect(Collectors.toList())
    );
  }


  public ProcessArtifactEntity toEntity(ProcessArtifact domain) {
    if (domain == null) {
      return null;
    }

    ProcessArtifactEntity entity = new ProcessArtifactEntity();
    entity.setId(domain.getId().identifier().value());
    entity.setTaskKey(domain.getTaskKey());
    entity.setName(domain.getName());
    entity.setSubprocessTask(domain.isSubProcessTask());
    entity.setSubprocessId(domain.getSubProcessId());
    entity.setSubprocessName(domain.getSubProcessName());

    ProcessDefinitionEntity processDefinitionEntity = new ProcessDefinitionEntity();
    processDefinitionEntity.setId(domain.getProcessDefinitionId().identifier().value());
    entity.setProcesDefinitionId(processDefinitionEntity);

    if (domain.getVariables() != null && !domain.getVariables().isEmpty()) {
      List<ArtifactVariableEntity> variableEntities = domain.getVariables().stream()
          .map(artifactVariableMapper::toEntity)
          .peek(ve -> ve.setProcessArtifactId(entity))
          .collect(Collectors.toList());
      entity.setVariables(variableEntities);
    } else {
      entity.setVariables(Collections.emptyList());
    }


    return entity;
  }

  public ProcessArtifactResponseDTO toResponseDTO(ProcessArtifact processArtifact) {
    ProcessArtifactResponseDTO paDto = new ProcessArtifactResponseDTO();
    paDto.setProjectArtifactId(processArtifact.getId().identifier().getValueAsString());
    paDto.setTaskKey(processArtifact.getTaskKey());
    paDto.setName(processArtifact.getName());
    paDto.setSubProcessTask(processArtifact.isSubProcessTask());
    paDto.setSubProcessId(processArtifact.getSubProcessId());
    paDto.setSubProcessName(processArtifact.getSubProcessName());

    if (processArtifact.getVariables() != null && !processArtifact.getVariables().isEmpty()) {
      List<ArtifactVariableResponseDTO> variableDtos = processArtifact.getVariables().stream()
          .map(artifactVariableMapper::toResponseDTO)
          .collect(Collectors.toList());
      paDto.setArtifactVariables(variableDtos);
    } else {
      paDto.setArtifactVariables(Collections.emptyList());
    }


    return paDto;
  }



}
