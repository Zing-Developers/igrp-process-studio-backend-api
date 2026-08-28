package cv.igrp.platform.process_manager_studio.project.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.igrp.platform.process_manager_studio.project.application.dto.ProcessDefinitionResponseDTO;
import cv.igrp.platform.process_manager_studio.project.domain.repository.ProcessDefinitionRepository;
import cv.igrp.platform.process_manager_studio.project.infrastructure.mappers.ProcessDefinitionMapper;
import cv.igrp.platform.process_manager_studio.shared.domain.exceptions.IgrpResponseStatusException;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProcessDefinitionId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import cv.igrp.platform.process_manager_studio.shared.security.AuditUserEnricher;

@Component
public class GetProcessDefinitionByIdQueryHandler implements QueryHandler<GetProcessDefinitionByIdQuery, ResponseEntity<ProcessDefinitionResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetProcessDefinitionByIdQueryHandler.class);


  private final ProcessDefinitionRepository processDefinitionRepository;
  private final ProcessDefinitionMapper processDefinitionMapper;

  private final AuditUserEnricher auditUserEnricher;

  public GetProcessDefinitionByIdQueryHandler(ProcessDefinitionRepository processDefinitionRepository, ProcessDefinitionMapper processDefinitionMapper, AuditUserEnricher auditUserEnricher) {
    this.auditUserEnricher = auditUserEnricher;

    this.processDefinitionRepository = processDefinitionRepository;
    this.processDefinitionMapper = processDefinitionMapper;
  }

   @IgrpQueryHandler
  public ResponseEntity<ProcessDefinitionResponseDTO> handle(GetProcessDefinitionByIdQuery query) {

     var processDefinitionId = ProcessDefinitionId.of(query.getProcessId());

     var processDefinition = processDefinitionRepository.findById(processDefinitionId)
         .orElseThrow(() ->
             IgrpResponseStatusException.notFound("Process Definition not found with id: " + processDefinitionId.identifier().value()));

     var response = processDefinitionMapper.toResponseDTO(processDefinition, true);
     auditUserEnricher.enrichProcessDefinitions(java.util.List.of(response));
     return ResponseEntity.ok(response);
  }

}
