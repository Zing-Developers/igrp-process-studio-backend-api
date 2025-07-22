package cv.igrp.platform.process_manager_studio.project.application.queries;

import cv.igrp.platform.process_manager_studio.project.domain.repository.ProjectRepository;
import cv.igrp.platform.process_manager_studio.project.infrastructure.mappers.ProjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import cv.igrp.platform.process_manager_studio.project.application.dto.ProjectResponseDTO;

@Component
public class GetProjectsQueryHandler implements QueryHandler<GetProjectsQuery, ResponseEntity<List<ProjectResponseDTO>>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetProjectsQueryHandler.class);

  private final ProjectMapper projectMapper;
  private final ProjectRepository projectRepository;

  public GetProjectsQueryHandler(ProjectMapper projectMapper, ProjectRepository projectRepository) {

    this.projectMapper = projectMapper;
    this.projectRepository = projectRepository;
  }

   @IgrpQueryHandler
  public ResponseEntity<List<ProjectResponseDTO>> handle(GetProjectsQuery query) {

    List<ProjectResponseDTO> projects = projectRepository.findAll().stream()
        .map(projectMapper::toResponseDTO)
        .toList();
    
    return ResponseEntity.ok(projects);
  }

}
