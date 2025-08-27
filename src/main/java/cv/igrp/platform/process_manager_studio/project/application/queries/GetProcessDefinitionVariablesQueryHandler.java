package cv.igrp.platform.process_manager_studio.project.application.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import cv.igrp.platform.process_manager_studio.project.application.dto.ProcessVariableResponseDTO;

@Component
public class GetProcessDefinitionVariablesQueryHandler implements QueryHandler<GetProcessDefinitionVariablesQuery, ResponseEntity<List<ProcessVariableResponseDTO>>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetProcessDefinitionVariablesQueryHandler.class);


  public GetProcessDefinitionVariablesQueryHandler() {

  }

   @IgrpQueryHandler
  public ResponseEntity<List<ProcessVariableResponseDTO>> handle(GetProcessDefinitionVariablesQuery query) {
    // TODO: Implement the query handling logic here
    return null;
  }

}