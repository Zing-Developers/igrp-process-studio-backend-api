package cv.igrp.platform.process_manager_studio.project.infrastructure.mappers;

import cv.igrp.platform.process_manager_studio.project.application.dto.ProcessDefinitionResponseDTO;
import cv.igrp.platform.process_manager_studio.project.application.dto.ProcessDefinitionResponseLightDTO;
import cv.igrp.platform.process_manager_studio.project.application.dto.ProcessArtifactResponseDTO;
import cv.igrp.platform.process_manager_studio.project.domain.models.ProcessDefinition;
import cv.igrp.platform.process_manager_studio.project.domain.models.ProcessArtifact;
import cv.igrp.platform.process_manager_studio.project.domain.models.ProcessVariable;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.BpmDriagram;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProcessDefinitionId;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProjectId;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.entity.ProcessDefinitionEntity;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.entity.ProcessArtifactEntity;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.entity.ProcessVariableEntity;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.entity.ProjectEntity;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProcessDefinitionMapper {

  private final ProcessArtifactMapper projectArtifactMapper;
  private final ProcessVariableMapper processVariableMapper;

  public ProcessDefinitionMapper(ProcessArtifactMapper projectArtifactMapper, ProcessVariableMapper processVariableMapper) {
    this.projectArtifactMapper = projectArtifactMapper;
    this.processVariableMapper = processVariableMapper;
  }

  public ProcessDefinitionEntity toEntity(ProcessDefinition domain) {
    if (domain == null) return null;

    //System.out.println("bpm content " +domain.getBpmDriagram()!=null ? domain.getBpmDriagram().getContent() : "sem conteudo");

    ProcessDefinitionEntity entity = new ProcessDefinitionEntity();
    entity.setId(domain.getId().identifier().value());
    entity.setProcessKey(domain.getProcessKey());
    entity.setBpmnDiagramUrl(domain.getBpmnDiagramUrl());
    entity.setVersion(domain.getVersion());
    entity.setBpmFileContent(domain.getBpmDriagram()!=null ? domain.getBpmDriagram().content() : null);
    entity.setState(domain.getState());
    entity.setDeploymentDate(domain.getDeploymentDate());
    entity.setDeploymentId(domain.getDeploymentId());
    entity.setTitle(domain.getTitle());
    entity.setDescription(domain.getDescription());
    entity.setLatest(domain.isLatest());
    ProjectEntity pr = new ProjectEntity();
    pr.setId(domain.getProjectId().identifier().value());
    entity.setProjectId(pr);

     //process artifacts
    if (domain.getArtifacts() != null && !domain.getArtifacts().isEmpty()) {
      List<ProcessArtifactEntity> artifactEntities = domain.getArtifacts().stream()
          .map(pa -> {
            ProcessArtifactEntity paEntity = projectArtifactMapper.toEntity(pa);
            paEntity.setProcesDefinitionId(entity);
            return paEntity;
          })
          .collect(Collectors.toList());
      entity.setArtifacts(artifactEntities);
    } else {
      entity.setArtifacts(Collections.emptyList());
    }

    //process variables
    if (domain.getProcessVariables() != null && !domain.getProcessVariables().isEmpty()) {
      List<ProcessVariableEntity> processVariableEntities = domain.getProcessVariables().stream()
          .map(pv -> {
            ProcessVariableEntity pvEntity = processVariableMapper.toEntity(pv);
            pvEntity.setProcesDefinitionId(entity);
            return pvEntity;
          })
          .collect(Collectors.toList());
      entity.setProcessVariables(processVariableEntities);
    } else {
      entity.setProcessVariables(Collections.emptyList());
    }

    return entity;
  }

  public ProcessDefinition toDomain(ProcessDefinitionEntity entity) {
    if (entity == null) return null;

    List<ProcessArtifactEntity> artifactEntities = entity.getArtifacts() != null ? entity.getArtifacts() : Collections.emptyList();
    List<ProcessVariableEntity> processVariableEntities = entity.getProcessVariables() != null ? entity.getProcessVariables() : Collections.emptyList();

    List<ProcessArtifact> artifacts = artifactEntities.stream()
        .map(projectArtifactMapper::toDomain)
        .collect(Collectors.toList());

    List<ProcessVariable> processVariables = processVariableEntities.stream()
        .map(processVariableMapper::toDomain)
        .collect(Collectors.toList());

    var model = ProcessDefinition.rebuild(
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
        entity.isLatest(),
        processVariables
    );
    model.setAudit(entity.getCreatedBy(), entity.getLastModifiedBy());
    return model;
  }

  public ProcessDefinitionResponseDTO toResponseDTO(ProcessDefinition processDefinition, boolean showBpmContent) {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    ProcessDefinitionResponseDTO pdDto = new ProcessDefinitionResponseDTO();
    pdDto.setProcessDefinitionId(processDefinition.getId().identifier().getValueAsString());
    pdDto.setProcessKey(processDefinition.getProcessKey());
    pdDto.setBpmnDiagramUrl(processDefinition.getBpmnDiagramUrl());
    pdDto.setVersion(processDefinition.getVersion());
    pdDto.setStatus(processDefinition.getState()!= null ? processDefinition.getState().getCode() : null);
    pdDto.setCreatedBy(processDefinition.getCreatedBy());
    pdDto.setLastModifiedBy(processDefinition.getLastModifiedBy());
    pdDto.setStatusDesc(processDefinition.getState()!= null ? processDefinition.getState().getDescription() : null);
    pdDto.setDeploymentId(processDefinition.getDeploymentId()!= null ? processDefinition.getDeploymentId() : null);
    pdDto.setDeploymentDate(processDefinition.getDeploymentDate()!= null ? processDefinition.getDeploymentDate().format(formatter) : null);
    if (showBpmContent) {
      pdDto.setBpmFileContent(processDefinition.getBpmDriagram()!=null ? processDefinition.getBpmDriagram().content(): null);
    }
    pdDto.setTitle(processDefinition.getTitle());
    pdDto.setDescription(processDefinition.getDescription());
    pdDto.setProjectId(processDefinition.getProjectId().identifier().getValueAsString());


    if (processDefinition.getArtifacts() != null && !processDefinition.getArtifacts().isEmpty()) {
      List<ProcessArtifactResponseDTO> projectArtifactResponseDTOS = processDefinition.getArtifacts().stream()
          .map(projectArtifactMapper::toResponseDTO)
          .collect(Collectors.toList());

      pdDto.setProcessArtifacts(projectArtifactResponseDTOS);
    }



    return pdDto;
  }

  public ProcessDefinitionResponseLightDTO toResponseDTOLight(ProcessDefinition processDefinition) {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    var pdDto = new ProcessDefinitionResponseLightDTO();
    pdDto.setProcessDefinitionId(processDefinition.getId().identifier().getValueAsString());
    pdDto.setProcessKey(processDefinition.getProcessKey());
    pdDto.setBpmnDiagramUrl(processDefinition.getBpmnDiagramUrl());
    pdDto.setVersion(processDefinition.getVersion());
    pdDto.setStatus(processDefinition.getState()!= null ? processDefinition.getState().getCode() : null);
    pdDto.setStatusDesc(processDefinition.getState()!= null ? processDefinition.getState().getDescription() : null);
    pdDto.setDeploymentId(processDefinition.getDeploymentId()!= null ? processDefinition.getDeploymentId() : null);
    pdDto.setDeploymentDate(processDefinition.getDeploymentDate()!= null ? processDefinition.getDeploymentDate().format(formatter) : null);
    pdDto.setTitle(processDefinition.getTitle());
    pdDto.setDescription(processDefinition.getDescription());
    pdDto.setCreatedBy(processDefinition.getCreatedBy());
    pdDto.setLastModifiedBy(processDefinition.getLastModifiedBy());
    pdDto.setProjectId(processDefinition.getProjectId().identifier().getValueAsString());

    return pdDto;
  }

}
