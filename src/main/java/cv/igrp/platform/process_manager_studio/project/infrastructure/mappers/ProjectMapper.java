package cv.igrp.platform.process_manager_studio.project.infrastructure.mappers;

import cv.igrp.platform.process_manager_studio.project.application.dto.ProcessDefinitionResponseDTO;
import cv.igrp.platform.process_manager_studio.project.application.dto.ProcessDefinitionResponseLightDTO;
import cv.igrp.platform.process_manager_studio.project.application.dto.ProjectResponseDTO;
import cv.igrp.platform.process_manager_studio.project.domain.models.ProcessDefinition;
import cv.igrp.platform.process_manager_studio.project.domain.models.Project;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProjectId;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.entity.ProcessDefinitionEntity;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.entity.ProjectEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProjectMapper {


  private final ProcessDefinitionMapper processDefinitionMapper;

  public ProjectMapper(ProcessDefinitionMapper processDefinitionMapper) {
    this.processDefinitionMapper = processDefinitionMapper;
  }

  public ProjectEntity toEntity(Project domain) {
    if (domain == null) return null;

    ProjectEntity entity = new ProjectEntity();
    entity.setId(domain.getId().getIdentifier().getValue());
    entity.setCode(domain.getCode());
    entity.setName(domain.getName());
    entity.setDescription(domain.getDescription());
    entity.setActive(domain.isActive());
    entity.setAppCode(domain.getAppCode());

    List<ProcessDefinitionEntity> processDefinitionEntities = Collections.emptyList();
    if (domain.getProcessDefinitions() != null && !domain.getProcessDefinitions().isEmpty()) {
      processDefinitionEntities = domain.getProcessDefinitions().stream()
          .map(pd -> {
            ProcessDefinitionEntity pdEntity = processDefinitionMapper.toEntity(pd);
            pdEntity.setProjectId(entity);
            return pdEntity;
          })
          .collect(Collectors.toList());
    }
    entity.setProcessdefinitions(processDefinitionEntities);

    return entity;
  }

  public Project toDomain(ProjectEntity entity) {
    if (entity == null) return null;

    List<ProcessDefinitionEntity> pdEntities = entity.getProcessdefinitions();
    List<ProcessDefinition> processDefinitions = Collections.emptyList();

    if (pdEntities != null && !pdEntities.isEmpty()) {
      processDefinitions = pdEntities.stream()
          .map(processDefinitionMapper::toDomain)
          .collect(Collectors.toList());
    }

    return Project.rebuild(
        ProjectId.of(entity.getId()),
        entity.getCode(),
        entity.getName(),
        entity.getDescription(),
        entity.isActive(),
        entity.getAppCode(),
        processDefinitions
    );
  }

  public ProjectResponseDTO toResponseDTO(Project project) {
    if (project == null) return null;

    var dto = new ProjectResponseDTO();
    dto.setProjectId(project.getId().getIdentifier().getValueAsString());
    dto.setCode(project.getCode());
    dto.setName(project.getName());
    dto.setDescription(project.getDescription());
    dto.setActive(project.isActive());
    dto.setAppCode(project.getAppCode());

    if (project.getProcessDefinitions() != null && !project.getProcessDefinitions().isEmpty()) {

      List<ProcessDefinitionResponseLightDTO> processDefinitionDTOs = project.getAllProcessAndLastestForProcessKey().stream()
          .map(processDefinitionMapper::toResponseDTOLight)
          .collect(Collectors.toList());
      dto.setProcessDefinitions(processDefinitionDTOs);
    } else {
      dto.setProcessDefinitions(Collections.emptyList());
    }

    return dto;
  }


  public ProjectResponseDTO toResponseDTOLight(Project project) {
    if (project == null) return null;

    var dto = new ProjectResponseDTO();
    dto.setProjectId(project.getId().getIdentifier().getValueAsString());
    dto.setCode(project.getCode());
    dto.setName(project.getName());
    dto.setDescription(project.getDescription());
    dto.setActive(project.isActive());
    dto.setAppCode(project.getAppCode());

    return dto;
  }




}
