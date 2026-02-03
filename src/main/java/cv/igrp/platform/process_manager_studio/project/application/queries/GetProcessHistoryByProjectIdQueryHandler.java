package cv.igrp.platform.process_manager_studio.project.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.igrp.platform.process_manager_studio.project.application.dto.ProjectResponseDTO;
import cv.igrp.platform.process_manager_studio.project.domain.filter.ProjectFilter;
import cv.igrp.platform.process_manager_studio.project.domain.repository.ProjectRepository;
import cv.igrp.platform.process_manager_studio.project.infrastructure.mappers.ProjectMapper;
import cv.igrp.platform.process_manager_studio.shared.domain.exceptions.IgrpResponseStatusException;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetProcessHistoryByProjectIdQueryHandler implements QueryHandler<GetProcessHistoryByProjectIdQuery, ResponseEntity<ProjectResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetProcessHistoryByProjectIdQueryHandler.class);


  private final ProjectMapper projectMapper;
  private final ProjectRepository projectRepository;

  public GetProcessHistoryByProjectIdQueryHandler(ProjectMapper projectMapper, ProjectRepository projectRepository) {

    this.projectMapper = projectMapper;
    this.projectRepository = projectRepository;
  }

   @IgrpQueryHandler
  public ResponseEntity<ProjectResponseDTO> handle(GetProcessHistoryByProjectIdQuery query) {
     var projectId = ProjectId.of(query.getProjectId());

     var filter = ProjectFilter.builder()
         .processName(query.getProcessName())
         .processKey(query.getProcessKey())
         .pageSize(Integer.parseInt(query.getPageSize()))
         .pageNumber(Integer.parseInt(query.getPageNumber()))
         .build();

     var project = projectRepository.findById(projectId, filter)
         .orElseThrow(() -> IgrpResponseStatusException.notFound("Project not found with id: " + projectId.identifier().value()));

     return ResponseEntity.ok(projectMapper.toResponseDTO(project));
  }

}
