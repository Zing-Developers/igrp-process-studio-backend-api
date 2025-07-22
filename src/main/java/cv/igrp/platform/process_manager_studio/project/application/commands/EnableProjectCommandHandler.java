package cv.igrp.platform.process_manager_studio.project.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component
public class EnableProjectCommandHandler implements CommandHandler<EnableProjectCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(EnableProjectCommandHandler.class);

   public EnableProjectCommandHandler() {

   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(EnableProjectCommand command) {
      // TODO: Implement the command handling logic here
      return null;
   }

}