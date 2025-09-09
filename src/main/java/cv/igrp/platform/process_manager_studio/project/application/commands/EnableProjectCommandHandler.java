package cv.igrp.platform.process_manager_studio.project.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.igrp.platform.process_manager_studio.project.domain.repository.ProjectRepository;
import cv.igrp.platform.process_manager_studio.shared.domain.exceptions.IgrpResponseStatusException;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EnableProjectCommandHandler implements CommandHandler<EnableProjectCommand, ResponseEntity<Map<String, ?>>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(EnableProjectCommandHandler.class);

  private final ProjectRepository projectRepository;
   public EnableProjectCommandHandler(ProjectRepository projectRepository) {

     this.projectRepository = projectRepository;
   }

   @IgrpCommandHandler
   public ResponseEntity<Map<String, ?>> handle(EnableProjectCommand command) {
     var projectId = ProjectId.of(command.getProjectId());

     var project = projectRepository.findById(projectId)
         .orElseThrow(() -> IgrpResponseStatusException.notFound("Project not found with id: " + projectId.identifier().value()));

     project.enable();
     projectRepository.save(project);

     return ResponseEntity.ok(Map.of("message", "Project enabled successfully."));
   }

}
