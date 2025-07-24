package cv.igrp.platform.process_manager_studio.project.application.queries;

import cv.igrp.platform.process_manager_studio.project.domain.repository.ProcessDefinitionRepository;
import cv.igrp.platform.process_manager_studio.project.domain.repository.ProjectRepository;
import cv.igrp.platform.process_manager_studio.project.infrastructure.mappers.ProcessDefinitionMapper;
import cv.igrp.platform.process_manager_studio.shared.domain.exceptions.IgrpResponseStatusException;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProcessDefinitionId;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.igrp.platform.process_manager_studio.project.application.dto.ProcessDefinitionResponseDTO;

@Component
public class GetProcessDefinitionByIdQueryHandler implements QueryHandler<GetProcessDefinitionByIdQuery, ResponseEntity<ProcessDefinitionResponseDTO>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetProcessDefinitionByIdQueryHandler.class);

  private final ProjectRepository projectRepository;

  private final ProcessDefinitionRepository processDefinitionRepository;
  private final ProcessDefinitionMapper processDefinitionMapper;

  public GetProcessDefinitionByIdQueryHandler(ProjectRepository projectRepository, ProcessDefinitionRepository processDefinitionRepository, ProcessDefinitionMapper processDefinitionMapper) {

    this.projectRepository = projectRepository;
    this.processDefinitionRepository = processDefinitionRepository;
    this.processDefinitionMapper = processDefinitionMapper;
  }

   @IgrpQueryHandler
  public ResponseEntity<ProcessDefinitionResponseDTO> handle(GetProcessDefinitionByIdQuery query) {
     var projectId = ProjectId.of(query.getProjectId());
     var processDefinitionId = ProcessDefinitionId.of(query.getProcessId());

     if (!projectRepository.existsById(projectId))
       throw IgrpResponseStatusException.notFound("Project not found with id: " + projectId.getIdentifier().getValue());

     var processDefinition = processDefinitionRepository.findById(processDefinitionId)
         .orElseThrow(() ->
             IgrpResponseStatusException.notFound("Process Definition not found with id: " + processDefinitionId.getIdentifier().getValue()));

     var response = processDefinitionMapper.toResponseDTO(processDefinition);
     return ResponseEntity.ok(response);
  }

}
