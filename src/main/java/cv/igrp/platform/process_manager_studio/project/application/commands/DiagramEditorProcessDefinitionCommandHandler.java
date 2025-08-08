package cv.igrp.platform.process_manager_studio.project.application.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.igrp.platform.process_manager_studio.project.application.dto.ProcessDefinitionResponseDTO;
import cv.igrp.platform.process_manager_studio.project.domain.models.ArtifactVariable;
import cv.igrp.platform.process_manager_studio.project.domain.models.ProcessDefinition;
import cv.igrp.platform.process_manager_studio.project.domain.models.ProjectArtifact;
import cv.igrp.platform.process_manager_studio.project.domain.repository.ProcessDefinitionRepository;
import cv.igrp.platform.process_manager_studio.project.domain.repository.ProjectRepository;
import cv.igrp.platform.process_manager_studio.project.infrastructure.mappers.ProcessDefinitionMapper;
import cv.igrp.platform.process_manager_studio.shared.domain.exceptions.IgrpResponseStatusException;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProcessDefinitionId;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProjectId;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.BpmDriagram;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.bpmn.BpmnProcessReader;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.bpmn.ParsedProcess;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormData;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormField;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

@Component
public class DiagramEditorProcessDefinitionCommandHandler implements CommandHandler<DiagramEditorProcessDefinitionCommand, ResponseEntity<ProcessDefinitionResponseDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(DiagramEditorProcessDefinitionCommandHandler.class);


  private final ProcessDefinitionRepository processDefinitionRepository;
  private final ProcessDefinitionMapper processDefinitionMapper;
  private final BpmnProcessReader bpmnProcessReader;

  public DiagramEditorProcessDefinitionCommandHandler(ProcessDefinitionRepository processDefinitionRepository, ProcessDefinitionMapper processDefinitionMapper, BpmnProcessReader bpmnProcessReader) {

    this.processDefinitionRepository = processDefinitionRepository;
    this.processDefinitionMapper = processDefinitionMapper;
    this.bpmnProcessReader = bpmnProcessReader;
  }


  @IgrpCommandHandler
  public ResponseEntity<ProcessDefinitionResponseDTO> handle(DiagramEditorProcessDefinitionCommand command) {

    var contentDto = command.getBpmdiagram().getContent(); // todo, method to clean xml, remove space, and enter space.

    ParsedProcess parsedProcess = bpmnProcessReader.readFromXml(contentDto);

    LOGGER.debug("content : {}", contentDto);

    var processDefinitionId = ProcessDefinitionId.of(command.getProcessId());

    var processDefinition = processDefinitionRepository.findById(processDefinitionId)
        .orElseThrow(() ->
            IgrpResponseStatusException.notFound("Process Definition not found with id: " + processDefinitionId.getIdentifier().getValue()));

    // Verifica estado DRAF
    if (processDefinition.isDraft()) {
      LOGGER.debug("processDefinition is draft: {}", processDefinition);
      processDefinition.cleanArtifacts(); // Limpa artifacts antigos
      processDefinition.updateBpmnContent(BpmDriagram.of(contentDto)); // Atualiza o conteúdo do BPMN
    } else {
      processDefinition = ProcessDefinition.create(processDefinition.getProjectId(), processDefinition.getProcessKey(), BpmDriagram.of(contentDto));
      LOGGER.debug("processDefinition new: {}", processDefinition);
    }

    if (parsedProcess.getUserTasks() != null && !parsedProcess.getUserTasks().isEmpty()) {
      // Processa user tasks e adiciona artifacts e variáveis
      for (var userTask : parsedProcess.getUserTasks()) {
        var artifact = ProjectArtifact.create(processDefinition.getId(), userTask.getId(), userTask.getName());

        if (userTask.getVariables() != null && !userTask.getVariables().isEmpty()) {
          LOGGER.debug("Adding variables to artifact: {}", artifact.getId());
          for (var variable : userTask.getVariables()) {
            var artifactVariable = ArtifactVariable.create(
                artifact.getId(),
                variable.getId(),
                variable.getLabel(),
                variable.getType(),
                variable.getDefaultValue(),
                variable.isRequired()
            );
            artifact.addVariable(artifactVariable);
          }
        }

        processDefinition.addArtifact(artifact);
      }
    }

    processDefinitionRepository.save(processDefinition);

    var response = processDefinitionMapper.toResponseDTO(processDefinition);
    return ResponseEntity.ok(response);


  }

}
