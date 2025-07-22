package cv.igrp.platform.process_manager_studio.project.infrastructure.mappers;

import cv.igrp.platform.process_manager_studio.project.domain.models.ProjectArtifact;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProcessDefinitionId;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProjectArtifactId;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.entity.ArtifactVariableEntity;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.entity.ProcessDefinitionEntity;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.entity.ProjectArtifactEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProjectArtifactMapper {

  private final ArtifactVariableMapper artifactVariableMapper;

  public ProjectArtifactMapper(ArtifactVariableMapper artifactVariableMapper) {
    this.artifactVariableMapper = artifactVariableMapper;
  }

  public ProjectArtifact toDomain(ProjectArtifactEntity entity) {
    if (entity == null) {
      return null;
    }

    List<ArtifactVariableEntity> variableEntities = entity.getVariables() != null ? entity.getVariables() : Collections.emptyList();

    return ProjectArtifact.rebuild(
        ProjectArtifactId.of(entity.getId().toString()),
        ProcessDefinitionId.of(entity.getProcesDefinitionId().getId().toString()),
        entity.getTaskKey(),
        entity.getName(),
        variableEntities.stream()
            .map(artifactVariableMapper::toDomain)
            .collect(Collectors.toList())
    );
  }


  public ProjectArtifactEntity toEntity(ProjectArtifact domain) {
    if (domain == null) {
      return null;
    }

    ProjectArtifactEntity entity = new ProjectArtifactEntity();
    entity.setId(domain.getId().getIdentifier().getValue());
    entity.setTaskKey(domain.getTaskKey());
    entity.setName(domain.getName());

    ProcessDefinitionEntity processDefinitionEntity = new ProcessDefinitionEntity();
    processDefinitionEntity.setId(domain.getProcessDefinitionId().getIdentifier().getValue());
    entity.setProcesDefinitionId(processDefinitionEntity);

    if (domain.getVariables() != null && !domain.getVariables().isEmpty()) {
      List<ArtifactVariableEntity> variableEntities = domain.getVariables().stream()
          .map(artifactVariableMapper::toEntity)
          .peek(ve -> ve.setProjectArtifactId(entity)) // seta pai no filho
          .collect(Collectors.toList());
      entity.setVariables(variableEntities);
    } else {
      entity.setVariables(Collections.emptyList());
    }

    return entity;
  }

}
