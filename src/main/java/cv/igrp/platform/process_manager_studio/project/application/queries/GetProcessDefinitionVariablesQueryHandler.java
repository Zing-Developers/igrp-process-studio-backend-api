package cv.igrp.platform.process_manager_studio.project.application.queries;

import cv.igrp.platform.process_manager_studio.project.domain.repository.ProcessDefinitionRepository;
import cv.igrp.platform.process_manager_studio.project.infrastructure.mappers.ProcessVariableMapper;
import cv.igrp.platform.process_manager_studio.shared.domain.exceptions.IgrpResponseStatusException;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProcessDefinitionId;
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

  private final ProcessDefinitionRepository processDefinitionRepository;
  private final ProcessVariableMapper processVariableMapper;
  private final cv.igrp.platform.process_manager_studio.shared.security.AuditUserEnricher auditUserEnricher;


  public GetProcessDefinitionVariablesQueryHandler(ProcessDefinitionRepository processDefinitionRepository, ProcessVariableMapper processVariableMapper,
                                                   cv.igrp.platform.process_manager_studio.shared.security.AuditUserEnricher auditUserEnricher) {

    this.processDefinitionRepository = processDefinitionRepository;
    this.processVariableMapper = processVariableMapper;
    this.auditUserEnricher = auditUserEnricher;
  }

   @IgrpQueryHandler
  public ResponseEntity<List<ProcessVariableResponseDTO>> handle(GetProcessDefinitionVariablesQuery query) {

     var processId = ProcessDefinitionId.of(query.getProcessId());

     var process = processDefinitionRepository.findById(processId).orElseThrow(
         () -> IgrpResponseStatusException.notFound("Process Definition not found with id: " + processId.identifier().value())

     );


     var variables = processVariableMapper.toDTO(process.getProcessVariables());
     auditUserEnricher.enrich(variables);
     return ResponseEntity.ok(variables);
  }

}
