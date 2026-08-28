package cv.igrp.platform.process_manager_studio.project.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.igrp.platform.process_manager_studio.project.application.dto.ProjectResponseDTO;
import cv.igrp.platform.process_manager_studio.project.domain.filter.ProjectFilter;
import cv.igrp.platform.process_manager_studio.project.domain.models.ProcessDefinition;
import cv.igrp.platform.process_manager_studio.project.domain.models.Project;
import cv.igrp.platform.process_manager_studio.project.domain.repository.ProjectRepository;
import cv.igrp.platform.process_manager_studio.project.infrastructure.mappers.ProjectMapper;
import cv.igrp.platform.process_manager_studio.shared.application.constants.ProcessDefinitionState;
import cv.igrp.platform.process_manager_studio.shared.domain.exceptions.IgrpResponseStatusException;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import cv.igrp.platform.process_manager_studio.shared.security.AuditUserEnricher;

@Component
public class GetDeployedProcessByProjectIdQueryHandler implements QueryHandler<GetDeployedProcessByProjectIdQuery, ResponseEntity<ProjectResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetDeployedProcessByProjectIdQueryHandler.class);

  private final ProjectMapper projectMapper;
  private final ProjectRepository projectRepository;

  private final AuditUserEnricher auditUserEnricher;

  public GetDeployedProcessByProjectIdQueryHandler(ProjectMapper projectMapper, ProjectRepository projectRepository, AuditUserEnricher auditUserEnricher) {
    this.auditUserEnricher = auditUserEnricher;

    this.projectMapper = projectMapper;
    this.projectRepository = projectRepository;
  }

   @IgrpQueryHandler
  public ResponseEntity<ProjectResponseDTO> handle(GetDeployedProcessByProjectIdQuery query) {
     var projectId = ProjectId.of(query.getProjectId());

     var processName = query.getProcessName();
     var processKey = query.getProcessKey();
     var pageSize = Integer.parseInt(query.getPageSize());
     var pageNumber = Integer.parseInt(query.getPageNumber());


     var project = projectRepository.findByIdWithLatestDeployedProcess(projectId)
         .orElseThrow(() -> IgrpResponseStatusException.notFound("Project not found with id: " + projectId.identifier().value()));

     // filtra os processos no objeto Project
     List<ProcessDefinition> filteredProcesses = project.getProcessDefinitions().stream()
         .filter(pd -> pd.isLatest() && pd.getState() == ProcessDefinitionState.PUBLISHED)
         .filter(pd -> processKey == null || pd.getProcessKey().equals(processKey))
         .filter(pd -> processName == null || pd.getTitle().toLowerCase().contains(processName.toLowerCase()))
         .toList();

     // cria uma cópia do projeto com a lista filtrada
     var filteredProject = Project.rebuild(
         project.getId(),
         project.getCode(),
         project.getName(),
         project.getDescription(),
         project.isActive(),
         project.getAppCode(),
         filteredProcesses
     );

     var dto = projectMapper.toResponseDTO(filteredProject);
     auditUserEnricher.enrichProjects(java.util.List.of(dto));
     return ResponseEntity.ok(dto);
  }

}
