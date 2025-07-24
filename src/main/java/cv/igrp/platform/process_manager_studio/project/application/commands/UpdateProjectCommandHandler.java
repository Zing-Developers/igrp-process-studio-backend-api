package cv.igrp.platform.process_manager_studio.project.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.igrp.platform.process_manager_studio.project.domain.repository.ProjectRepository;
import cv.igrp.platform.process_manager_studio.project.infrastructure.mappers.ProjectMapper;
import cv.igrp.platform.process_manager_studio.shared.domain.exceptions.IgrpResponseStatusException;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cv.igrp.platform.process_manager_studio.project.application.dto.ProjectResponseDTO;

@Component
public class UpdateProjectCommandHandler implements CommandHandler<UpdateProjectCommand, ResponseEntity<ProjectResponseDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(UpdateProjectCommandHandler.class);

  private final ProjectMapper projectMapper;
  private final ProjectRepository projectRepository;
   public UpdateProjectCommandHandler(ProjectMapper projectMapper, ProjectRepository projectRepository) {

     this.projectMapper = projectMapper;
     this.projectRepository = projectRepository;
   }

   @IgrpCommandHandler
   public ResponseEntity<ProjectResponseDTO> handle(UpdateProjectCommand command) {
     var projectId = ProjectId.of(command.getProjectId());
     var dto = command.getProjectrequest();

     var project = projectRepository.findById(projectId)
         .orElseThrow(() -> IgrpResponseStatusException.notFound("Project not found with id: " + projectId.getIdentifier().getValue()));

     project.updateInfo(dto.getCode(), dto.getName(), dto.getDescription(), dto.getAppCode());

     var projectSaved = projectRepository.save(project);
     return ResponseEntity.ok(projectMapper.toResponseDTO(projectSaved));
   }

}
