package cv.igrp.platform.process_manager_studio.project.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.igrp.platform.process_manager_studio.project.domain.models.ProcessDefinition;
import cv.igrp.platform.process_manager_studio.project.domain.repository.ProcessDefinitionRepository;
import cv.igrp.platform.process_manager_studio.project.domain.repository.ProjectRepository;
import cv.igrp.platform.process_manager_studio.project.infrastructure.mappers.ProcessDefinitionMapper;
import cv.igrp.platform.process_manager_studio.shared.domain.exceptions.IgrpResponseStatusException;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cv.igrp.platform.process_manager_studio.project.application.dto.ProcessDefinitionResponseDTO;

@Component
public class SaveProcessDefinitionCommandHandler implements CommandHandler<SaveProcessDefinitionCommand, ResponseEntity<ProcessDefinitionResponseDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(SaveProcessDefinitionCommandHandler.class);

   private final ProcessDefinitionRepository processDefinitionRepository;
   private final ProcessDefinitionMapper processDefinitionMapper;

   private final ProjectRepository projectRepository;

   public SaveProcessDefinitionCommandHandler(ProcessDefinitionRepository processDefinitionRepository, ProcessDefinitionMapper processDefinitionMapper, ProjectRepository projectRepository) {

     this.processDefinitionRepository = processDefinitionRepository;
     this.processDefinitionMapper = processDefinitionMapper;
     this.projectRepository = projectRepository;
   }

   @IgrpCommandHandler
   public ResponseEntity<ProcessDefinitionResponseDTO> handle(SaveProcessDefinitionCommand command) {

       var dto = command.getProcessdefinitionrequest();

       var projectId = ProjectId.of(command.getProjectId());

       if(processDefinitionRepository.existsByKey(dto.getProcessKey())){
         throw IgrpResponseStatusException.conflict("A process with the key '" + dto.getProcessKey() + "' already exists.");
       }

       if (!projectRepository.existsById(projectId)){
         throw IgrpResponseStatusException.notFound("Project not found with id: " + projectId.getIdentifier().getValue());
       }

     var processDefinition = ProcessDefinition.createNew(projectId, dto.getProcessKey(), dto.getTitle(), dto.getDescription());

    processDefinitionRepository.save(processDefinition);

    LOGGER.info("Process definition saved with ID: {}", processDefinition.getId().getIdentifier().getValue());

    ProcessDefinitionResponseDTO responseDTO = processDefinitionMapper.toResponseDTO(processDefinition, true);

     return ResponseEntity.ok(responseDTO);
   }

}
