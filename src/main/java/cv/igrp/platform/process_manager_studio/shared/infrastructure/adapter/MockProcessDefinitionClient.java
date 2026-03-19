package cv.igrp.platform.process_manager_studio.shared.infrastructure.adapter;

import cv.igrp.framework.process.management.integration.core.adapter.IProcessDefinitionAdapter;
import cv.igrp.framework.process.management.integration.core.exception.ProcessDefinitionException;
import cv.igrp.framework.process.management.integration.core.model.BpmnSourceType;
import cv.igrp.framework.process.management.integration.core.model.IgrpProcessDefinitionRepresentation;
import cv.igrp.framework.process.management.integration.core.model.ProcessDefinitionRepresentation;
import cv.igrp.framework.process.studio.sdk.client.exception.ProcessDefinitionClientException;
import cv.igrp.platform.process_manager_studio.project.domain.repository.ProcessDefinitionRepository;
import cv.igrp.platform.process_manager_studio.shared.application.constants.ProcessDefinitionState;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class MockProcessDefinitionClient implements IProcessDefinitionAdapter {

  private final Map<String, ProcessDefinitionRepresentation> deployedProcesses = new HashMap<>();
  private final Map<String, Integer> processVersions = new HashMap<>();

  private final ProcessDefinitionRepository processDefinitionRepository;

  public MockProcessDefinitionClient(ProcessDefinitionRepository processDefinitionRepository) {
    this.processDefinitionRepository = processDefinitionRepository;
  }

  @Override
  public ProcessDefinitionRepresentation deploy(ProcessDefinitionRepresentation processDefinitionRepresentation, Map<String, String> headers) throws ProcessDefinitionException {
    return null;
  }

  @Override
  public ProcessDefinitionRepresentation deploy(ProcessDefinitionRepresentation processToDeploy) throws ProcessDefinitionException {
    if (processToDeploy == null || processToDeploy.getKey() == null) {
      throw new ProcessDefinitionClientException("Process representation and key cannot be null.");
    }

    String processKey = processToDeploy.getKey();
    // 1. Consulta a BD para a última versão PUBLICADA.
    int latestPublishedVersion = processDefinitionRepository
        .findLatestPublishedVersionByProcessKey(processKey, ProcessDefinitionState.PUBLISHED)
        .orElse(0); // Se não encontrar nenhuma, a última versão publicada é 0.

    // 2. Pega a versão atual do nosso mapa em memória (que pode ser maior se já fizemos deploys nesta sessão).
    int currentInMemoryVersion = processVersions.getOrDefault(processKey, 0);

    // 3. A nova versão será 1 a mais que o MÁXIMO entre a BD e a memória.
    int newVersion = Math.max(latestPublishedVersion, currentInMemoryVersion) + 1;

    String deploymentId = "mock-deployment-" + UUID.randomUUID();
    String processId = "mock-proc-def-" + UUID.randomUUID();

    IgrpProcessDefinitionRepresentation deployedResult = IgrpProcessDefinitionRepresentation.builder()
        .id(processId)
        .key(processKey)
        .name(processToDeploy.getName())
        .description(processToDeploy.getDescription())
        .version(String.valueOf(newVersion))
        .bpmnXml(processToDeploy.getBpmnXml())
        .bpmnSourceType(BpmnSourceType.INLINE_XML)
        .deployed(true)
        .deploymentId(deploymentId)
        .deployedAt(LocalDateTime.now())
        .build();

    // We use the deploymentId as the key, since it's unique.
    deployedProcesses.put(deploymentId, deployedResult);

    return deployedResult;
  }

  @Override
  public void undeploy(String deploymentId, Map<String, String> headers) throws ProcessDefinitionException {

  }

  /**
   * Simulates undeploying ALL versions of a process.
   * It uses the given deploymentId to find the process key (processKey),
   * then removes all deployments associated with that key.
   *
   * @param deploymentId The ID of any of the deployments of the process to be removed.
   * @throws ProcessDefinitionException if the deploymentId is not found.
   */
  @Override
  public void undeploy(String deploymentId) throws ProcessDefinitionException {
    if (deploymentId == null || deploymentId.isBlank()) {
      throw new ProcessDefinitionClientException("Deployment ID cannot be null or blank.");
    }

    // 1. Find the process associated with the deploymentId to get the key.
    ProcessDefinitionRepresentation processToUndeploy = deployedProcesses.get(deploymentId);
    if (processToUndeploy == null) {
      throw new ProcessDefinitionClientException("Failed to undeploy process definition. No deployment found for ID: " + deploymentId);
    }
    String processKeyToRemove = processToUndeploy.getKey();

    // 2. Find all deploymentIds that match this key.
    var idsToRemove = deployedProcesses.values().stream()
        .filter(p -> Objects.equals(p.getKey(), processKeyToRemove))
        .map(ProcessDefinitionRepresentation::getDeploymentId)
        .toList();

    // 3. Remove all matching deployments.
    idsToRemove.forEach(deployedProcesses::remove);

    // 4. Remove the key from the version tracking map.
    processVersions.remove(processKeyToRemove);

    System.out.printf("--- MOCK: Undeployed %d version(s) for process key '%s' ---\n", idsToRemove.size(), processKeyToRemove);
  }

  public void reset() {
    deployedProcesses.clear();
    processVersions.clear();
  }

  public int getDeployedProcessCount() {
    return deployedProcesses.size();
  }
}
