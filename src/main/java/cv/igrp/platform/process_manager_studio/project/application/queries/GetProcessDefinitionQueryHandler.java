package cv.igrp.platform.process_manager_studio.project.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.igrp.platform.process_manager_studio.project.application.dto.ProcessDefinitionResponseLightDTO;
import cv.igrp.platform.process_manager_studio.project.application.dto.WrapperListaProcessDefinitionDTO;
import cv.igrp.platform.process_manager_studio.project.domain.filter.ProcessDefinitionFilter;
import cv.igrp.platform.process_manager_studio.project.domain.repository.ProcessDefinitionRepository;
import cv.igrp.platform.process_manager_studio.project.infrastructure.mappers.ProcessDefinitionMapper;
import cv.igrp.platform.process_manager_studio.shared.application.constants.ProcessDefinitionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import cv.igrp.platform.process_manager_studio.shared.security.AuditUserEnricher;

@Component
public class GetProcessDefinitionQueryHandler implements QueryHandler<GetProcessDefinitionQuery, ResponseEntity<WrapperListaProcessDefinitionDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetProcessDefinitionQueryHandler.class);

  private final ProcessDefinitionRepository processDefinitionRepository;
  private final ProcessDefinitionMapper processDefinitionMapper;

  private final AuditUserEnricher auditUserEnricher;

  public GetProcessDefinitionQueryHandler(ProcessDefinitionRepository processDefinitionRepository, ProcessDefinitionMapper processDefinitionMapper, AuditUserEnricher auditUserEnricher) {
    this.auditUserEnricher = auditUserEnricher;

    this.processDefinitionRepository = processDefinitionRepository;
    this.processDefinitionMapper = processDefinitionMapper;
  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListaProcessDefinitionDTO> handle(GetProcessDefinitionQuery query) {

    var pageNumber=  Integer.parseInt(query.getPageNumber());
     var pageSize = Integer.parseInt(query.getPageSize());

     var filter = ProcessDefinitionFilter.builder()
         .processName(query.getProcessName())
         .processKey(query.getProcessKey())
         .appCode(query.getAppCode())
         .projectCode(query.getProjectCode())
         .projectName(query.getProjectName())
         .state(query.getState() != null ? ProcessDefinitionState.fromCodeOrThrow(query.getState()) : null)
         .pageNumber(pageNumber)
         .pageSize(pageSize)
         .build();

     List<ProcessDefinitionResponseLightDTO> projects = processDefinitionRepository
         .findAll(filter).stream()
         .map(processDefinitionMapper::toResponseDTOLight)
         .toList();

     var wrapperListaProcessDefinitionDTO= new WrapperListaProcessDefinitionDTO();
     wrapperListaProcessDefinitionDTO.setContent(projects);
     wrapperListaProcessDefinitionDTO.setPageNumber(pageNumber);
     wrapperListaProcessDefinitionDTO.setPageSize(pageSize);
     wrapperListaProcessDefinitionDTO.setTotalElements((long) projects.size());

     auditUserEnricher.enrichLightProcessDefinitions(wrapperListaProcessDefinitionDTO.getContent());
     return ResponseEntity.ok(wrapperListaProcessDefinitionDTO);

  }

}
