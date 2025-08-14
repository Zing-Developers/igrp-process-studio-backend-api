package cv.igrp.platform.process_manager_studio.project.infrastructure.mappers;

import cv.igrp.platform.process_manager_studio.project.application.dto.ArtifactVariableResponseDTO;
import cv.igrp.platform.process_manager_studio.project.domain.models.ArtifactVariable;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ArtifactVariableId;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProjectArtifactId;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.entity.ArtifactVariableEntity;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.entity.ProjectArtifactEntity;
import org.springframework.stereotype.Component;

@Component
public class ArtifactVariableMapper {

  public ArtifactVariable toDomain(ArtifactVariableEntity entity) {
    if (entity == null) {
      return null;
    }
    return ArtifactVariable.rebuild(
        ArtifactVariableId.of(entity.getId().toString()),
        entity.getProjectArtifactId() != null ? ProjectArtifactId.of(entity.getProjectArtifactId().getId().toString()) : null,
        entity.getKey(),
        entity.getName(),
        entity.getType(),
        entity.getDefaultValue(),
        entity.isRequired()
    );
  }

  public ArtifactVariableEntity toEntity(ArtifactVariable domain) {
    if (domain == null) {
      return null;
    }

    ArtifactVariableEntity entity = new ArtifactVariableEntity();
    entity.setId(domain.getId().identifier().value());
    entity.setName(domain.getName());
    entity.setType(domain.getType());
    entity.setDefaultValue(domain.getDefaultValue());
    entity.setRequired(domain.isRequired());
    entity.setKey(domain.getArtifactVariableKey());

    if (domain.getArtifactId() != null) {
      var projectArtifactEntity = new ProjectArtifactEntity();
      projectArtifactEntity.setId(domain.getArtifactId().identifier().value());
      entity.setProjectArtifactId(projectArtifactEntity);
    }
    return entity;
  }

  public ArtifactVariableResponseDTO toResponseDTO(ArtifactVariable artifactVariable) {

    if( artifactVariable == null) {
      return null;
    }

    var responseDTO = new ArtifactVariableResponseDTO();
    responseDTO.setArtifactVariableId(artifactVariable.getId().identifier().getValueAsString());
    responseDTO.setKey(artifactVariable.getArtifactVariableKey());
    responseDTO.setName(artifactVariable.getName());
    responseDTO.setType(artifactVariable.getType());
    responseDTO.setDefaultValue(artifactVariable.getDefaultValue());
    responseDTO.setRequired(artifactVariable.isRequired());

    return responseDTO;
  }
}
