package cv.igrp.platform.process_manager_studio.project.application.queries;

import cv.igrp.platform.process_manager_studio.project.application.dto.ProcessDefinitionResponseLightDTO;
import cv.igrp.platform.process_manager_studio.project.application.dto.ProjectResponseDTO;
import cv.igrp.platform.process_manager_studio.project.application.dto.WrapperListaProjectDTO;
import cv.igrp.platform.process_manager_studio.project.domain.repository.ProcessDefinitionRepository;
import cv.igrp.platform.process_manager_studio.project.infrastructure.mappers.ProcessDefinitionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.igrp.platform.process_manager_studio.project.application.dto.WrapperListaProcessDefinitionDTO;

import java.util.List;

@Component
public class GetProcessDefinitionQueryHandler implements QueryHandler<GetProcessDefinitionQuery, ResponseEntity<WrapperListaProcessDefinitionDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetProcessDefinitionQueryHandler.class);

  private final ProcessDefinitionRepository processDefinitionRepository;
  private final ProcessDefinitionMapper processDefinitionMapper;

  public GetProcessDefinitionQueryHandler(ProcessDefinitionRepository processDefinitionRepository, ProcessDefinitionMapper processDefinitionMapper) {

    this.processDefinitionRepository = processDefinitionRepository;
    this.processDefinitionMapper = processDefinitionMapper;
  }

   @IgrpQueryHandler
  public ResponseEntity<WrapperListaProcessDefinitionDTO> handle(GetProcessDefinitionQuery query) {

    var pageNumber=  Integer.parseInt(query.getPageNumber());
     var pageSize = Integer.parseInt(query.getPageSize());

     List<ProcessDefinitionResponseLightDTO> projects = processDefinitionRepository.findAll().stream()
         .map(processDefinitionMapper::toResponseDTOLight)
         .toList();

     var wrapperListaProcessDefinitionDTO= new WrapperListaProcessDefinitionDTO();
     wrapperListaProcessDefinitionDTO.setContent(projects);
     wrapperListaProcessDefinitionDTO.setPageNumber(pageNumber);
     wrapperListaProcessDefinitionDTO.setPageSize(pageSize);
     wrapperListaProcessDefinitionDTO.setTotalElements((long) projects.size());

     return ResponseEntity.ok(wrapperListaProcessDefinitionDTO);

  }

}
