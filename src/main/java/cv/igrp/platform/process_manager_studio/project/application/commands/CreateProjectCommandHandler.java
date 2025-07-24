package cv.igrp.platform.process_manager_studio.project.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.igrp.platform.process_manager_studio.project.domain.models.Project;
import cv.igrp.platform.process_manager_studio.project.domain.repository.ProjectRepository;
import cv.igrp.platform.process_manager_studio.project.infrastructure.mappers.ProjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cv.igrp.platform.process_manager_studio.project.application.dto.ProjectResponseDTO;

@Component
public class CreateProjectCommandHandler implements CommandHandler<CreateProjectCommand, ResponseEntity<ProjectResponseDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(CreateProjectCommandHandler.class);

   private final ProjectMapper projectMapper;
   private final ProjectRepository projectRepository;

   public CreateProjectCommandHandler(ProjectMapper projectMapper, ProjectRepository projectRepository) {

     this.projectMapper = projectMapper;
     this.projectRepository = projectRepository;
   }

   @IgrpCommandHandler
   public ResponseEntity<ProjectResponseDTO> handle(CreateProjectCommand command) {
     var dto = command.getProjectrequest();

     var project = Project.create(dto.getCode(), dto.getName(), dto.getDescription(), dto.getAppCode());

     var savedProject = projectRepository.save(project);

      return ResponseEntity.ok(projectMapper.toResponseDTO(savedProject));
   }

}
