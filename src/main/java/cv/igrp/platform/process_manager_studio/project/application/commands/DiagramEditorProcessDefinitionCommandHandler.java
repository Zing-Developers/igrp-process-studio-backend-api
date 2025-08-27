package cv.igrp.platform.process_manager_studio.project.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.igrp.platform.process_manager_studio.project.application.dto.ProcessDefinitionResponseDTO;
import cv.igrp.platform.process_manager_studio.project.domain.models.ArtifactVariable;
import cv.igrp.platform.process_manager_studio.project.domain.models.ProcessDefinition;
import cv.igrp.platform.process_manager_studio.project.domain.models.ProcessArtifact;
import cv.igrp.platform.process_manager_studio.project.domain.repository.ProcessDefinitionRepository;
import cv.igrp.platform.process_manager_studio.project.infrastructure.mappers.ProcessDefinitionMapper;
import cv.igrp.platform.process_manager_studio.shared.domain.exceptions.IgrpResponseStatusException;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.BpmDriagram;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.bpmn.BpmnProcessReader;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.bpmn.ParsedProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

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

     var processKey = command.getProcessKey();

    var processDefinition = processDefinitionRepository.findDraftByProcessKey(processKey)
        .or(() -> processDefinitionRepository.findLastestByProcessKey(processKey))
        .orElseThrow(() ->  IgrpResponseStatusException.badRequest("No process found in draft or published state for the given process key: " + processKey));

    // Verifica estado DRAF
    if (processDefinition.isDraft()) {
      LOGGER.debug("processDefinition is draft: {}", processDefinition);
      processDefinition.cleanArtifacts(); // Limpa artifacts antigos
      processDefinition.updateBpmnContent(BpmDriagram.of(contentDto)); // Atualiza o conteúdo do BPMN
    } else {
      processDefinition = ProcessDefinition.create(processDefinition.getProjectId(), processDefinition.getProcessKey(), BpmDriagram.of(contentDto),
          processDefinition.getTitle(), processDefinition.getDescription());
      LOGGER.debug("processDefinition new: {}", processDefinition);
    }

    if (parsedProcess.getUserTasks() != null && !parsedProcess.getUserTasks().isEmpty()) {
      // Processa user tasks e adiciona artifacts e variáveis
      for (var userTask : parsedProcess.getUserTasks()) {
        var artifact = ProcessArtifact.create(processDefinition.getId(), userTask.getId(), userTask.getName());

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

    var response = processDefinitionMapper.toResponseDTO(processDefinition, true);
    return ResponseEntity.ok(response);


  }

}
