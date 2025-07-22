package cv.igrp.platform.process_manager_studio.project.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.igrp.platform.process_manager_studio.project.domain.models.ProcessDefinition;
import cv.igrp.platform.process_manager_studio.project.domain.models.ProjectArtifact;
import cv.igrp.platform.process_manager_studio.project.domain.repository.ProjectRepository;
import cv.igrp.platform.process_manager_studio.project.infrastructure.mappers.ProjectMapper;
import cv.igrp.platform.process_manager_studio.shared.domain.exceptions.IgrpResponseStatusException;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProjectId;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cv.igrp.platform.process_manager_studio.project.application.dto.ProcessDefinitionResponseDTO;
import org.camunda.bpm.model.bpmn.instance.Process;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

@Component
public class CreateProcessDefinitionCommandHandler implements CommandHandler<CreateProcessDefinitionCommand, ResponseEntity<ProcessDefinitionResponseDTO>> {

   private static final Logger LOGGER = LoggerFactory.getLogger(CreateProcessDefinitionCommandHandler.class);

  private final ProjectRepository projectRepository;
  private final ProjectMapper projectMapper;

   public CreateProcessDefinitionCommandHandler(ProjectRepository projectRepository, ProjectMapper projectMapper) {

     this.projectRepository = projectRepository;
     this.projectMapper = projectMapper;
   }

   @IgrpCommandHandler
   public ResponseEntity<ProcessDefinitionResponseDTO> handle(CreateProcessDefinitionCommand command) {
     var projectId = ProjectId.of(command.getProjectId());

     var project = projectRepository.findById(projectId)
         .orElseThrow(() ->
             IgrpResponseStatusException.notFound("Project not found with id: " + projectId.getIdentifier().getValue()));

     var file = command.getFile();

     if (file.isEmpty()) {
       throw IgrpResponseStatusException.badRequest("File is empty.");
     }

     try (InputStream inputStream = file.getInputStream()) {
       BpmnModelInstance modelInstance = Bpmn.readModelFromStream(inputStream);

       // Pegando o processo principal do modelo
       Collection<Process> processes = modelInstance.getModelElementsByType(Process.class);

       if (processes.isEmpty()) {
         throw IgrpResponseStatusException.badRequest("No process found in BPMN file.");
       }

       Process process = processes.iterator().next();

       String processId = process.getId();
       String processName = process.getName();

       LOGGER.debug("Process ID: {}", processId);
       LOGGER.debug("Process Name: {}", processName);


       var processDefinition = ProcessDefinition.create(projectId,processId,"url_minion");
       project.addProcessDefinition(processDefinition);

       Collection<UserTask> userTasks =
           modelInstance.getModelElementsByType(UserTask.class);

       for (org.camunda.bpm.model.bpmn.instance.UserTask userTask : userTasks) {
         String taskId = userTask.getId();
         String taskName = userTask.getName();

         var artifact = ProjectArtifact.create(processDefinition.getId(),taskId, taskName);
         processDefinition.addArtifact(artifact);
       }

       projectRepository.save(project);

       var response = new ProcessDefinitionResponseDTO();
       response.setProcessKey(processId);

       return ResponseEntity.ok(response);

     } catch (IOException e) {
       LOGGER.error("Error reading BPMN file", e);
       throw IgrpResponseStatusException.internalServerError("Error processing BPMN file.");
     }
   }

}
