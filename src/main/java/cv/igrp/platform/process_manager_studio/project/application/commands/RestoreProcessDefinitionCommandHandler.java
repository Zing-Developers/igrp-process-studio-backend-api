package cv.igrp.platform.process_manager_studio.project.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.igrp.platform.process_manager_studio.project.domain.repository.ProcessDefinitionRepository;
import cv.igrp.platform.process_manager_studio.shared.domain.exceptions.IgrpResponseStatusException;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProcessDefinitionId;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
public class RestoreProcessDefinitionCommandHandler implements CommandHandler<RestoreProcessDefinitionCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(RestoreProcessDefinitionCommandHandler.class);

  private final ProcessDefinitionRepository processDefinitionRepository;

  public RestoreProcessDefinitionCommandHandler(ProcessDefinitionRepository processDefinitionRepository) {

    this.processDefinitionRepository = processDefinitionRepository;
  }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(RestoreProcessDefinitionCommand command) {
     var processDefinitionId = ProcessDefinitionId.of(command.getProcessId());

     var processDefinition = processDefinitionRepository.findById(processDefinitionId)
         .orElseThrow(() ->
             IgrpResponseStatusException.notFound("Process Definition not found with id: " + processDefinitionId.getIdentifier().getValue()));

     //todo, when can i restore a process??

     return null;
   }

}
