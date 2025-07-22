package cv.igrp.platform.process_manager_studio.project.infrastructure.mappers;

import cv.igrp.platform.process_manager_studio.project.domain.models.ProcessDefinition;
import cv.igrp.platform.process_manager_studio.project.domain.models.ProjectArtifact;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProcessDefinitionId;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProjectId;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.entity.ProcessDefinitionEntity;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.entity.ProjectArtifactEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProcessDefinitionMapper {

  private final ProjectArtifactMapper projectArtifactMapper;

  public ProcessDefinitionMapper(ProjectArtifactMapper projectArtifactMapper) {
    this.projectArtifactMapper = projectArtifactMapper;
  }

  public ProcessDefinitionEntity toEntity(ProcessDefinition domain) {
    if (domain == null) return null;

    ProcessDefinitionEntity entity = new ProcessDefinitionEntity();
    entity.setId(domain.getId().getIdentifier().getValue());
    entity.setProcessKey(domain.getProcessKey());
    entity.setBpmnDiagramUrl(domain.getBpmnDiagramUrl());
    entity.setVersion(domain.getVersion());
    entity.setRejectedReason(domain.getRejectedReason());

    if (domain.getArtifacts() != null && !domain.getArtifacts().isEmpty()) {
      List<ProjectArtifactEntity> artifactEntities = domain.getArtifacts().stream()
          .map(pa -> {
            ProjectArtifactEntity paEntity = projectArtifactMapper.toEntity(pa);
            paEntity.setProcesDefinitionId(entity);
            return paEntity;
          })
          .collect(Collectors.toList());
      entity.setArtifacts(artifactEntities);
    } else {
      entity.setArtifacts(Collections.emptyList());
    }

    return entity;
  }

  public ProcessDefinition toDomain(ProcessDefinitionEntity entity) {
    if (entity == null) return null;

    List<ProjectArtifactEntity> artifactEntities = entity.getArtifacts() != null ? entity.getArtifacts() : Collections.emptyList();

    List<ProjectArtifact> artifacts = artifactEntities.stream()
        .map(projectArtifactMapper::toDomain)
        .collect(Collectors.toList());

    return ProcessDefinition.rebuild(
         ProcessDefinitionId.of(entity.getId().toString()),
        ProjectId.of(entity.getProjectId().getId().toString()),
        entity.getProcessKey(),
        entity.getBpmnDiagramUrl(),
        entity.getVersion(),
        entity.getRejectedReason(),
        artifacts
    );
  }
}
