package cv.igrp.platform.process_manager_studio.project.application.queries;

import cv.igrp.platform.process_manager_studio.project.domain.repository.ProjectRepository;
import cv.igrp.platform.process_manager_studio.project.infrastructure.mappers.ProjectMapper;
import cv.igrp.platform.process_manager_studio.shared.domain.exceptions.IgrpResponseStatusException;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.igrp.platform.process_manager_studio.project.application.dto.ProjectResponseDTO;

@Component
public class GetProjectByIdQueryHandler implements QueryHandler<GetProjectByIdQuery, ResponseEntity<ProjectResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetProjectByIdQueryHandler.class);

  private final ProjectMapper projectMapper;
  private final ProjectRepository projectRepository;


  public GetProjectByIdQueryHandler(ProjectMapper projectMapper, ProjectRepository projectRepository) {

    this.projectMapper = projectMapper;
    this.projectRepository = projectRepository;
  }

   @IgrpQueryHandler
  public ResponseEntity<ProjectResponseDTO> handle(GetProjectByIdQuery query) {
    var projectId = ProjectId.of(query.getProjectId());

    var project = projectRepository.findById(projectId)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Project not found with id: " + projectId.getIdentifier().getValue()));

    return ResponseEntity.ok(projectMapper.toResponseDTO(project));

  }

}
