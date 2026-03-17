package cv.igrp.platform.process_manager_studio.project.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.process.management.integration.core.adapter.IProcessDefinitionAdapter;
import cv.igrp.framework.process.management.integration.core.model.BpmnSourceType;
import cv.igrp.framework.process.management.integration.core.model.IgrpProcessDefinitionRepresentation;
import cv.igrp.framework.process.management.integration.core.model.ProcessDefinitionRepresentation;
import cv.igrp.framework.process.studio.sdk.client.ProcessDefinitionClient;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.igrp.platform.process_manager_studio.project.application.dto.ProcessDefinitionResponseDTO;
import cv.igrp.platform.process_manager_studio.project.domain.models.ArtifactVariable;
import cv.igrp.platform.process_manager_studio.project.domain.models.ProcessDefinition;
import cv.igrp.platform.process_manager_studio.project.domain.models.ProcessArtifact;
import cv.igrp.platform.process_manager_studio.project.domain.repository.ProcessDefinitionRepository;
import cv.igrp.platform.process_manager_studio.project.domain.repository.ProjectRepository;
import cv.igrp.platform.process_manager_studio.project.infrastructure.mappers.ProcessDefinitionMapper;
import cv.igrp.platform.process_manager_studio.shared.domain.exceptions.IgrpResponseStatusException;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.BpmDriagram;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.bpmn.BpmnProcessReader;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.bpmn.ParsedProcess;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class DeployProcessDefinitionCommandHandler
    implements CommandHandler<DeployProcessDefinitionCommand, ResponseEntity<ProcessDefinitionResponseDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeployProcessDefinitionCommandHandler.class);

  private final ProjectRepository projectRepository;
  private final ProcessDefinitionRepository processDefinitionRepository;
  private final ProcessDefinitionMapper processDefinitionMapper;

  private final IProcessDefinitionAdapter processDefinitionAdapter;
  private final ProcessDefinitionClient client;
  private final BpmnProcessReader bpmnProcessReader;

  public DeployProcessDefinitionCommandHandler(ProjectRepository projectRepository,
      ProcessDefinitionRepository processDefinitionRepository, ProcessDefinitionMapper processDefinitionMapper,
      IProcessDefinitionAdapter processDefinitionAdapter, ProcessDefinitionClient client,
      BpmnProcessReader bpmnProcessReader) {
    this.projectRepository = projectRepository;

    this.processDefinitionRepository = processDefinitionRepository;
    this.processDefinitionMapper = processDefinitionMapper;
    this.processDefinitionAdapter = processDefinitionAdapter;
    this.client = client;
    this.bpmnProcessReader = bpmnProcessReader;
  }

  @IgrpCommandHandler
  public ResponseEntity<ProcessDefinitionResponseDTO> handle(DeployProcessDefinitionCommand command) {

    var processKey = command.getProcessKey();

    var content = command.getBpmdiagram().getContent();
    ParsedProcess parsedProcess = bpmnProcessReader.readFromXml(content);

    var processDefinition = processDefinitionRepository.findDraftByProcessKey(processKey)
        .or(() -> processDefinitionRepository.findLastestByProcessKey(processKey))
        .orElseThrow(() -> IgrpResponseStatusException
            .badRequest("No process found in draft or published state for the given process key: " + processKey));

    // Verifica estado DRAF

    if (processDefinition.isDraft()) {
      LOGGER.debug("processDefinition is draft: {}", processDefinition);
      processDefinition.cleanArtifacts(); // Limpa artifacts antigos
      processDefinition.updateBpmnContent(BpmDriagram.of(content)); // Atualiza o conteúdo do BPMN
    } else {
      processDefinition = ProcessDefinition.create(processDefinition.getProjectId(), processDefinition.getProcessKey(),
          BpmDriagram.of(content),
          processDefinition.getTitle(), processDefinition.getDescription());

      LOGGER.debug("processDefinition new: {}", processDefinition);
    }

    // Processa user tasks e adiciona artifacts e variáveis
    if (parsedProcess.getUserTasks() != null && !parsedProcess.getUserTasks().isEmpty()) {
      for (var userTask : parsedProcess.getUserTasks()) {
        ProcessArtifact artifact;
        if (userTask.isSubProcessTask()) {
          artifact = ProcessArtifact.createFromSubProcess(
              processDefinition.getId(),
              userTask.getId(),
              userTask.getName(),
              userTask.getFormKey(),
              userTask.getSubProcessId(),
              userTask.getSubProcessName());
        } else {
          artifact = ProcessArtifact.create(
              processDefinition.getId(),
              userTask.getId(),
              userTask.getName(),
              userTask.getFormKey());
        }

        if (userTask.getVariables() != null && !userTask.getVariables().isEmpty()) {
          LOGGER.debug("Adding variables to artifact: {}", artifact.getId());
          for (var variable : userTask.getVariables()) {
            var artifactVariable = ArtifactVariable.create(
                artifact.getId(),
                variable.getId(),
                variable.getLabel(),
                variable.getType(),
                variable.getDefaultValue(),
                variable.isRequired());
            artifact.addVariable(artifactVariable);
          }
        }

        processDefinition.addArtifact(artifact);
      }
    }

    String fileName = processDefinition.getProcessKey().concat(".bpmn20.xml");
    String applicationBase = projectRepository.getApplicationBaseByProjectId(processDefinition.getProjectId());
    String sanitizedContent = bpmnProcessReader.sanitizeBpmnXml(content);
    IgrpProcessDefinitionRepresentation definitionToDeploy = IgrpProcessDefinitionRepresentation.builder()

        .key(processDefinition.getProcessKey())
        .name(processDefinition.getTitle())
        .description(processDefinition.getDescription())
        .resourceName(fileName)
        .bpmnXml(sanitizedContent)
        .bpmnSourceType(BpmnSourceType.INLINE_XML)
        .applicationBase(applicationBase)
        .build();

    LOGGER.info("Attempting to deploy process with key: {}", definitionToDeploy.getKey());

    var headers = getRequestHeaders();
    ProcessDefinitionRepresentation deployResult = processDefinitionAdapter.deploy(definitionToDeploy);

    LOGGER.info("Process deployed successfully. Deployment ID: {}", deployResult.getDeploymentId());

    if (deployResult.isDeployed()) {
      processDefinition.deploy(
          deployResult.getDeploymentId(),
          deployResult.getDeployedAt(),
          deployResult.getVersion());
    } else {
      throw IgrpResponseStatusException.badRequest("Process not deployed successfully.");
    }

    processDefinitionRepository.unsetLatestForOtherVersions(
        processDefinition.getProcessKey(),
        processDefinition.getId());

    processDefinitionRepository.save(processDefinition);

    var response = processDefinitionMapper.toResponseDTO(processDefinition, true);
    return ResponseEntity.ok(response);

  }

  private Map<String, String> getRequestHeaders() {
    var attributes = RequestContextHolder.getRequestAttributes();
    if (!(attributes instanceof ServletRequestAttributes servletAttributes)) {
      return Map.of();
    }

    HttpServletRequest request = servletAttributes.getRequest();
    Enumeration<String> headerNames = request.getHeaderNames();
    if (headerNames == null) {
      return Map.of();
    }
    return Collections.list(headerNames).stream()
        .collect(Collectors.toMap(
            name -> name.toLowerCase(Locale.ROOT),
            request::getHeader
        ));
  }


}
