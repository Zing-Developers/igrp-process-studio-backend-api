package cv.igrp.platform.process_manager_studio.shared.infrastructure.adapter;

import cv.igrp.framework.process.management.integration.core.adapter.IProcessDefinitionAdapter;
import cv.igrp.framework.process.management.integration.core.exception.ProcessDefinitionException;
import cv.igrp.framework.process.management.integration.core.model.BpmnSourceType;
import cv.igrp.framework.process.management.integration.core.model.IgrpProcessDefinitionRepresentation;
import cv.igrp.framework.process.management.integration.core.model.ProcessDefinitionRepresentation;
import cv.igrp.framework.process.studio.sdk.client.exception.ProcessDefinitionClientException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;


public class MockProcessDefinitionClient implements IProcessDefinitionAdapter {

  private final Map<String, ProcessDefinitionRepresentation> deployedProcesses = new HashMap<>();
  private final Map<String, Integer> processVersions = new HashMap<>();

  @Override
  public ProcessDefinitionRepresentation deploy(ProcessDefinitionRepresentation processToDeploy) throws ProcessDefinitionException {
    if (processToDeploy == null || processToDeploy.getKey() == null) {
      throw new ProcessDefinitionClientException("A representação e a chave do processo não podem ser nulas.");
    }

    String processKey = processToDeploy.getKey();
    int newVersion = processVersions.getOrDefault(processKey, 0) + 1;
    processVersions.put(processKey, newVersion);

    String deploymentId = "mock-deployment-" + UUID.randomUUID().toString();
    String processId = "mock-proc-def-" + UUID.randomUUID().toString();

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

    // Usamos o deploymentId como chave, pois é único.
    deployedProcesses.put(deploymentId, deployedResult);

    return deployedResult;
  }

  /**
   * Simula o undeploy de TODAS as versões de um processo.
   * Ele usa o deploymentId fornecido para encontrar a chave do processo (processKey)
   * e, em seguida, remove todos os deployments associados a essa chave.
   * @param deploymentId O ID de qualquer um dos deployments do processo a ser removido.
   * @throws ProcessDefinitionException se o deploymentId não for encontrado.
   */
  @Override
  public void undeploy(String deploymentId) throws ProcessDefinitionException {
    if (deploymentId == null || deploymentId.isBlank()) {
      throw new ProcessDefinitionClientException("O ID do deploy não pode ser nulo ou vazio.");
    }

    // 1. Encontra o processo correspondente ao deploymentId para descobrir a chave.
    ProcessDefinitionRepresentation processToUndeploy = deployedProcesses.get(deploymentId);
    if (processToUndeploy == null) {
      throw new ProcessDefinitionClientException("Falha ao remover a definição de processo. Deploy não encontrado para o ID: " + deploymentId);
    }
    String processKeyToRemove = processToUndeploy.getKey();

    // 2. Encontra todos os deploymentIds que correspondem a essa chave.
    var idsToRemove = deployedProcesses.values().stream()
        .filter(p -> Objects.equals(p.getKey(), processKeyToRemove))
        .map(ProcessDefinitionRepresentation::getDeploymentId)
        .collect(Collectors.toList());

    // 3. Remove todos os deployments encontrados.
    idsToRemove.forEach(deployedProcesses::remove);

    // 4. Remove a chave do nosso mapa de versionamento.
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
