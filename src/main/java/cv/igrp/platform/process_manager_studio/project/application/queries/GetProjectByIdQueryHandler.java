package cv.igrp.platform.process_manager_studio.project.application.queries;

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


  public GetProjectByIdQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<ProjectResponseDTO> handle(GetProjectByIdQuery query) {
    // TODO: Implement the query handling logic here
    return null;
  }

}