package cv.igrp.platform.process_manager_studio.project.infrastructure.mappers;

import cv.igrp.platform.process_manager_studio.project.application.dto.ProcessDefinitionResponseDTO;
import cv.igrp.platform.process_manager_studio.project.application.dto.ProcessDefinitionResponseLightDTO;
import cv.igrp.platform.process_manager_studio.project.application.dto.ProjectArtifactResponseDTO;
import cv.igrp.platform.process_manager_studio.project.domain.models.ProcessDefinition;
import cv.igrp.platform.process_manager_studio.project.domain.models.ProjectArtifact;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProcessDefinitionId;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProjectId;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.BpmDriagram;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.entity.ProcessDefinitionEntity;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.entity.ProjectArtifactEntity;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.entity.ProjectEntity;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
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

    //System.out.println("bpm content " +domain.getBpmDriagram()!=null ? domain.getBpmDriagram().getContent() : "sem conteudo");

    ProcessDefinitionEntity entity = new ProcessDefinitionEntity();
    entity.setId(domain.getId().getIdentifier().getValue());
    entity.setProcessKey(domain.getProcessKey());
    entity.setBpmnDiagramUrl(domain.getBpmnDiagramUrl());
    entity.setVersion(domain.getVersion());
    entity.setBpmFileContent(domain.getBpmDriagram()!=null ? domain.getBpmDriagram().getContent() : null);
    entity.setState(domain.getState());
    entity.setDeploymentDate(domain.getDeploymentDate());
    entity.setDeploymentId(domain.getDeploymentId());
    entity.setTitle(domain.getTitle());
    entity.setDescription(domain.getDescription());
    entity.setLatest(domain.isLatest());
    ProjectEntity pr = new ProjectEntity();
    pr.setId(domain.getProjectId().getIdentifier().getValue());
    entity.setProjectId(pr);

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
        entity.getBpmFileContent()!=null ? BpmDriagram.of(entity.getBpmFileContent()) : null,
        entity.getVersion(),
        artifacts,
        entity.getState(),
        entity.getDeploymentDate(),
        entity.getDeploymentId(),
        entity.getTitle(),
        entity.getDescription(),
        entity.isLatest()
    );
  }

  public ProcessDefinitionResponseDTO toResponseDTO(ProcessDefinition processDefinition) {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    ProcessDefinitionResponseDTO pdDto = new ProcessDefinitionResponseDTO();
    pdDto.setProcessDefinitionId(processDefinition.getId().getIdentifier().getValueAsString());
    pdDto.setProcessKey(processDefinition.getProcessKey());
    pdDto.setBpmnDiagramUrl(processDefinition.getBpmnDiagramUrl());
    pdDto.setVersion(processDefinition.getVersion());
    pdDto.setStatus(processDefinition.getState()!= null ? processDefinition.getState().getCode() : null);
    pdDto.setStatusDesc(processDefinition.getState()!= null ? processDefinition.getState().getDescription() : null);
    pdDto.setDeploymentId(processDefinition.getDeploymentId()!= null ? processDefinition.getDeploymentId() : null);
    pdDto.setDeploymentDate(processDefinition.getDeploymentDate()!= null ? processDefinition.getDeploymentDate().format(formatter) : null);
    pdDto.setBpmFileContent(processDefinition.getBpmDriagram()!=null ? processDefinition.getBpmDriagram().getContent(): null);
    pdDto.setTitle(processDefinition.getTitle());
    pdDto.setDescripiton(processDefinition.getDescription());
    pdDto.setProjectId(processDefinition.getProjectId().getIdentifier().getValueAsString());


    if (processDefinition.getArtifacts() != null && !processDefinition.getArtifacts().isEmpty()) {
      List<ProjectArtifactResponseDTO> projectArtifactResponseDTOS = processDefinition.getArtifacts().stream()
          .map(projectArtifactMapper::toResponseDTO)
          .collect(Collectors.toList());

      pdDto.setProjectArtifacts(projectArtifactResponseDTOS);
    }


    return pdDto;
  }

  public ProcessDefinitionResponseLightDTO toResponseDTOLight(ProcessDefinition processDefinition) {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    var pdDto = new ProcessDefinitionResponseLightDTO();
    pdDto.setProcessDefinitionId(processDefinition.getId().getIdentifier().getValueAsString());
    pdDto.setProcessKey(processDefinition.getProcessKey());
    pdDto.setBpmnDiagramUrl(processDefinition.getBpmnDiagramUrl());
    pdDto.setVersion(processDefinition.getVersion());
    pdDto.setStatus(processDefinition.getState()!= null ? processDefinition.getState().getCode() : null);
    pdDto.setStatusDesc(processDefinition.getState()!= null ? processDefinition.getState().getDescription() : null);
    pdDto.setDeploymentId(processDefinition.getDeploymentId()!= null ? processDefinition.getDeploymentId() : null);
    pdDto.setDeploymentDate(processDefinition.getDeploymentDate()!= null ? processDefinition.getDeploymentDate().format(formatter) : null);
    pdDto.setTitle(processDefinition.getTitle());
    pdDto.setDescripiton(processDefinition.getDescription());
    pdDto.setProjectId(processDefinition.getProjectId().getIdentifier().getValueAsString());

    return pdDto;
  }

}
