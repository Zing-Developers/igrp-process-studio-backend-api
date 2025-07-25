package cv.igrp.platform.process_manager_studio.project.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.igrp.platform.process_manager_studio.project.domain.repository.ProcessDefinitionRepository;
import cv.igrp.platform.process_manager_studio.project.infrastructure.mappers.ProcessDefinitionMapper;
import cv.igrp.platform.process_manager_studio.shared.domain.exceptions.IgrpResponseStatusException;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProcessDefinitionId;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cv.igrp.platform.process_manager_studio.project.application.dto.ProcessDefinitionResponseDTO;

@Component
public class UpdateProcessDefinitionCommandHandler implements CommandHandler<UpdateProcessDefinitionCommand, ResponseEntity<ProcessDefinitionResponseDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(UpdateProcessDefinitionCommandHandler.class);

  private final ProcessDefinitionRepository processDefinitionRepository;
  private final ProcessDefinitionMapper processDefinitionMapper;

   public UpdateProcessDefinitionCommandHandler(ProcessDefinitionRepository processDefinitionRepository, ProcessDefinitionMapper processDefinitionMapper) {

     this.processDefinitionRepository = processDefinitionRepository;
     this.processDefinitionMapper = processDefinitionMapper;
   }

   @IgrpCommandHandler
   public ResponseEntity<ProcessDefinitionResponseDTO> handle(UpdateProcessDefinitionCommand command) {
     var processDefinitionId = ProcessDefinitionId.of(command.getProcessId());

     var dto = command.getProcessdefinitionrequest();

     var processDefinition = processDefinitionRepository.findById(processDefinitionId)
         .orElseThrow(() ->
             IgrpResponseStatusException.notFound("Process Definition not found with id: " + processDefinitionId.getIdentifier().getValue()));

     processDefinition.updateBaseInfo(dto.getCode(), dto.getTitle(), dto.getDescription());

     var saved = processDefinitionRepository.save(processDefinition);

     var response = processDefinitionMapper.toResponseDTO(saved);

     return ResponseEntity.ok(response);
   }

}
