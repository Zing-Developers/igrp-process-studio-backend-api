package cv.igrp.platform.process_manager_studio.project.infrastructure.mappers;

import cv.igrp.platform.process_manager_studio.project.application.dto.ProcessDefinitionResponseDTO;
import cv.igrp.platform.process_manager_studio.project.application.dto.ProjectResponseDTO;
import cv.igrp.platform.process_manager_studio.project.application.dto.ProjectResponseLigthDTO;
import cv.igrp.platform.process_manager_studio.project.domain.models.ProcessDefinition;
import cv.igrp.platform.process_manager_studio.project.domain.models.Project;
import cv.igrp.platform.process_manager_studio.shared.application.constants.ProcessDefinitionState;
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
    entity.setId(domain.getId().identifier().value());
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

    var pdEntities = entity.getProcessdefinitions();

    List<ProcessDefinition> processDefinitions = pdEntities == null ?
        List.of() :
        pdEntities.stream()
            .filter(pdEntity -> pdEntity.getState() != ProcessDefinitionState.DELETED)
            .map(processDefinitionMapper::toDomain)
            .toList();

    var project = Project.rebuild(
        ProjectId.of(entity.getId()),
        entity.getCode(),
        entity.getName(),
        entity.getDescription(),
        entity.isActive(),
        entity.getAppCode(),
        processDefinitions
    );
    project.setAudit(AuditMapping.trail(entity));
    return project;
  }

  public ProjectResponseDTO toResponseDTO(Project project) {
    if (project == null) return null;

    var dto = new ProjectResponseDTO();
    dto.setProjectId(project.getId().identifier().getValueAsString());
    dto.setCode(project.getCode());
    dto.setName(project.getName());
    dto.setDescription(project.getDescription());
    dto.setActive(project.isActive());
    dto.setAppCode(project.getAppCode());
    AuditMapping.apply(dto, project.getAudit());

    if (project.getProcessDefinitions() != null) {

      List<ProcessDefinitionResponseDTO> processDefinitionDTOs = project.getAllProcessAndLastestForProcessKey().stream()
          .map(pd -> {
            // nested inside its own project: don't repeat the project
            var pdDto = processDefinitionMapper.toResponseDTO(pd, false);
            pdDto.setProject(null);
            return pdDto;
          })
          .collect(Collectors.toList());
      dto.setProcessDefinitions(processDefinitionDTOs);
    } else {
      dto.setProcessDefinitions(Collections.emptyList());
    }

    return dto;
  }

  public ProjectResponseDTO toResponseDTOWithLatestProcessDeployed(Project project) {
    if (project == null) return null;

    var dto = new ProjectResponseDTO();
    dto.setProjectId(project.getId().identifier().getValueAsString());
    dto.setCode(project.getCode());
    dto.setName(project.getName());
    dto.setDescription(project.getDescription());
    dto.setActive(project.isActive());
    dto.setAppCode(project.getAppCode());
    AuditMapping.apply(dto, project.getAudit());

    if (project.getProcessDefinitions() != null) {

      List<ProcessDefinitionResponseDTO> processDefinitionDTOs = project.getLatestPublishedProcesses().stream()
          .map(pd -> {
            // nested inside its own project: don't repeat the project
            var pdDto = processDefinitionMapper.toResponseDTO(pd, false);
            pdDto.setProject(null);
            return pdDto;
          })
          .collect(Collectors.toList());
      dto.setProcessDefinitions(processDefinitionDTOs);
    } else {
      dto.setProcessDefinitions(Collections.emptyList());
    }

    return dto;
  }


  public ProjectResponseLigthDTO toResponseDTOLight(Project project) {
    if (project == null) return null;

    var dto = new ProjectResponseLigthDTO();
    dto.setProjectId(project.getId().identifier().getValueAsString());
    dto.setCode(project.getCode());
    dto.setName(project.getName());
    dto.setDescription(project.getDescription());
    dto.setActive(project.isActive());
    dto.setAppCode(project.getAppCode());
    AuditMapping.apply(dto, project.getAudit());

    if (project.getProcessDefinitions() != null) {

      var processDefinitionDTOs = project.getAllProcessAndLastestForProcessKey().stream()
          .map(pd -> {
            var pdDto = processDefinitionMapper.toResponseDTOLight(pd);
            pdDto.setProject(null);
            return pdDto;
          })
          .collect(Collectors.toList());
      dto.setProcessDefinitions(processDefinitionDTOs);
    } else {
      dto.setProcessDefinitions(Collections.emptyList());
    }

    return dto;
  }


}
