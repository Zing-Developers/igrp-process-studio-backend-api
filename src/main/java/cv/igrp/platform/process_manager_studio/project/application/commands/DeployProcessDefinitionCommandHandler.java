package cv.igrp.platform.process_manager_studio.project.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cv.igrp.platform.process_manager_studio.project.application.dto.ProjectResponseDTO;

@Component
public class DeployProcessDefinitionCommandHandler implements CommandHandler<DeployProcessDefinitionCommand, ResponseEntity<ProjectResponseDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(DeployProcessDefinitionCommandHandler.class);

   public DeployProcessDefinitionCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<ProjectResponseDTO> handle(DeployProcessDefinitionCommand command) {
      // TODO: Implement the command handling logic here
      return null;
   }

}