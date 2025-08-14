package cv.igrp.platform.process_manager_studio.project.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.igrp.platform.process_manager_studio.project.application.dto.ProjectResponseDTO;
import cv.igrp.platform.process_manager_studio.project.domain.repository.ProjectRepository;
import cv.igrp.platform.process_manager_studio.project.infrastructure.mappers.ProjectMapper;
import cv.igrp.platform.process_manager_studio.shared.domain.exceptions.IgrpResponseStatusException;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetDeployedProcessByProjectIdQueryHandler implements QueryHandler<GetDeployedProcessByProjectIdQuery, ResponseEntity<ProjectResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetDeployedProcessByProjectIdQueryHandler.class);

  private final ProjectMapper projectMapper;
  private final ProjectRepository projectRepository;

  public GetDeployedProcessByProjectIdQueryHandler(ProjectMapper projectMapper, ProjectRepository projectRepository) {

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
         .orElseThrow(() -> IgrpResponseStatusException.notFound("Project not found with id: " + projectId.getIdentifier().getValue()));

     return ResponseEntity.ok(projectMapper.toResponseDTO(project));
  }

}
