package cv.igrp.platform.process_manager_studio.project.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.igrp.platform.process_manager_studio.project.application.dto.ProjectResponseDTO;
import cv.igrp.platform.process_manager_studio.project.domain.models.ArtifactVariable;
import cv.igrp.platform.process_manager_studio.project.domain.models.ProcessDefinition;
import cv.igrp.platform.process_manager_studio.project.domain.models.ProjectArtifact;
import cv.igrp.platform.process_manager_studio.project.domain.repository.ProjectRepository;
import cv.igrp.platform.process_manager_studio.project.infrastructure.mappers.ProjectMapper;
import cv.igrp.platform.process_manager_studio.shared.domain.exceptions.IgrpResponseStatusException;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProjectId;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormData;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormField;
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
public class CreateProcessDefinitionCommandHandler implements CommandHandler<CreateProcessDefinitionCommand, ResponseEntity<ProjectResponseDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(CreateProcessDefinitionCommandHandler.class);

  private final ProjectRepository projectRepository;
  private final ProjectMapper projectMapper;

  public CreateProcessDefinitionCommandHandler(ProjectRepository projectRepository, ProjectMapper projectMapper) {

    this.projectRepository = projectRepository;
    this.projectMapper = projectMapper;
  }

  @IgrpCommandHandler
  public ResponseEntity<ProjectResponseDTO> handle(CreateProcessDefinitionCommand command) {
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

      Collection<Process> processes = modelInstance.getModelElementsByType(Process.class);

      if (processes.isEmpty()) {
        throw IgrpResponseStatusException.badRequest("No process found in BPMN file.");
      }

      for (Process process : processes) {

        String processKey  = process.getId(); //processkey
        String processName = process.getName();

        LOGGER.debug("Process ID: {}", processKey);
        LOGGER.debug("Process Name: {}", processName);

        // Verifica se já existe um ProcessDefinition com mesmo processKey e estado DRAFT
        var draftProcessDefinition = project.getDraftProcessDefinitionByKey(processKey);

        ProcessDefinition processDefinition;

        if (draftProcessDefinition.isPresent()) {
          processDefinition = draftProcessDefinition.get();
          LOGGER.debug("processDefinition found: {}", processDefinition);
          processDefinition.cleanArtifacts(); // Limpa artifacts antigos
        } else {
          processDefinition = ProcessDefinition.create(projectId, processKey, "url_minio"); // Ajustar URL real
          LOGGER.debug("processDefinition new: {}", processDefinition);
          project.addProcessDefinition(processDefinition);
        }

        Collection<UserTask> userTasks = process.getChildElementsByType(UserTask.class);

        // processing user tasks
        for (UserTask userTask : userTasks) {
          String taskId = userTask.getId();
          String taskName = userTask.getName();

          var artifact = ProjectArtifact.create(processDefinition.getId(), taskId, taskName);
          processDefinition.addArtifact(artifact);

          // Extrair variáveis do user task
          ExtensionElements extensionElements = userTask.getExtensionElements();

          if (extensionElements != null ) {
            Collection<CamundaFormData> formDataList = extensionElements.getElementsQuery()
                .filterByType(CamundaFormData.class)
                .list();

            if (formDataList != null && !formDataList.isEmpty()) {

              for (CamundaFormData formData : formDataList) {
                Collection<CamundaFormField> formFields = formData.getCamundaFormFields();

                for (CamundaFormField field : formFields) {
                  String varKey = field.getCamundaId();
                  String varName = field.getCamundaLabel();
                  String varType = field.getCamundaType();
                  String varDefault = field.getCamundaDefaultValue();
                  boolean isRequired = "true".equalsIgnoreCase(field.getAttributeValue("required"));

                  var variable = ArtifactVariable.create(
                      artifact.getId(),
                      varKey,
                      varName,
                      varType,
                      varDefault,
                      isRequired
                  );

                  artifact.addVariable(variable);
                }
              }
            }
          }
        }

      }

      projectRepository.save(project);

      var response = projectMapper.toResponseDTO(project);

      return ResponseEntity.ok(response);

    } catch (IOException e) {
      LOGGER.error("Error reading BPMN file", e);
      throw IgrpResponseStatusException.internalServerError("Error processing BPMN file.");
    }
  }

}
