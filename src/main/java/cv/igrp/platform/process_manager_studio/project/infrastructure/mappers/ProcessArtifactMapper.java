package cv.igrp.platform.process_manager_studio.project.infrastructure.mappers;

import cv.igrp.platform.process_manager_studio.project.application.dto.ArtifactVariableResponseDTO;
import cv.igrp.platform.process_manager_studio.project.application.dto.ProjectArtifactResponseDTO;
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

  public ProjectArtifactResponseDTO toResponseDTO(ProcessArtifact projectArtifact) {
    ProjectArtifactResponseDTO paDto = new ProjectArtifactResponseDTO();
    paDto.setProjectArtifactId(projectArtifact.getId().identifier().getValueAsString());
    paDto.setTaskKey(projectArtifact.getTaskKey());
    paDto.setName(projectArtifact.getName());

    if (projectArtifact.getVariables() != null && !projectArtifact.getVariables().isEmpty()) {
      List<ArtifactVariableResponseDTO> variableDtos = projectArtifact.getVariables().stream()
          .map(artifactVariableMapper::toResponseDTO)
          .collect(Collectors.toList());
      paDto.setArtifactVariables(variableDtos);
    } else {
      paDto.setArtifactVariables(Collections.emptyList());
    }


    return paDto;
  }

}
