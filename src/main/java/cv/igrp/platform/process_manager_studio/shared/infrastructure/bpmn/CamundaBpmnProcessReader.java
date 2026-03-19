package cv.igrp.platform.process_manager_studio.shared.infrastructure.bpmn;

import cv.igrp.platform.process_manager_studio.shared.domain.exceptions.IgrpResponseStatusException;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormData;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;



import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class CamundaBpmnProcessReader implements BpmnProcessReader {

  private static final Logger LOGGER = LoggerFactory.getLogger(CamundaBpmnProcessReader.class);


  @Override
  public ParsedProcess readFromXml(String bpmnXml) {
    try (InputStream inputStream = new ByteArrayInputStream(bpmnXml.getBytes(StandardCharsets.UTF_8))) {
      BpmnModelInstance modelInstance = Bpmn.readModelFromStream(inputStream);

      Collection<Process> processes = modelInstance.getModelElementsByType(Process.class);
      if (processes.isEmpty()) {
        throw new IllegalArgumentException("No process found in BPMN file.");
      }

      Process process = processes.iterator().next();
      String processKey = process.getId();
      String processName = process.getName();

      List<ParsedUserTask> parsedUserTasks = new ArrayList<>();

     // readGateways(process);

      // UserTasks do processo principal
      parsedUserTasks.addAll(extractUserTasks(process.getChildElementsByType(UserTask.class), false, null, null));

      // UserTasks dos sub-processos
      for (SubProcess subProcess : process.getChildElementsByType(SubProcess.class)) {
        extractSubProcessTasks(subProcess, parsedUserTasks);
      }

      ParsedProcess parsedProcess = new ParsedProcess(processKey, processName, parsedUserTasks);
      LOGGER.info("Parsed process object: {}", parsedProcess);
      return parsedProcess;

    } catch (Exception e) {
      LOGGER.error("Erro ao ler processo BPMN", e);
      throw new RuntimeException("Erro ao ler processo BPMN: " + e.getMessage(), e);
    }
  }

  private void extractSubProcessTasks(SubProcess subProcess, List<ParsedUserTask> allTasks) {
    // Extrair UserTasks deste subprocesso
    allTasks.addAll(
        extractUserTasks(
            subProcess.getChildElementsByType(UserTask.class),
            true,
            subProcess.getId(),
            subProcess.getName()
        )
    );

    // Se existirem nested subprocessos, continuar recursivamente
    for (SubProcess nested : subProcess.getChildElementsByType(SubProcess.class)) {
      extractSubProcessTasks(nested, allTasks);
    }
  }


  private List<ParsedUserTask> extractUserTasks(Collection<UserTask> userTasks, boolean isSubProcessTask, String subProcessId, String subProcessName) {
    List<ParsedUserTask> parsedUserTasks = new ArrayList<>();

    for (UserTask userTask : userTasks) {
      String taskId = userTask.getId();
      String taskName = userTask.getName();
      String formKey = userTask.getCamundaFormKey();
      List<ParsedVariable> variables = new ArrayList<>();

      ExtensionElements extensionElements = userTask.getExtensionElements();
      if (extensionElements != null) {
        Collection<CamundaFormData> formDataList = extensionElements.getElementsQuery()
            .filterByType(CamundaFormData.class)
            .list();

        for (CamundaFormData formData : formDataList) {
          for (CamundaFormField field : formData.getCamundaFormFields()) {
            ParsedVariable variable = new ParsedVariable(
                field.getCamundaId(),
                field.getCamundaLabel(),
                field.getCamundaType(),
                field.getCamundaDefaultValue(),
                "true".equalsIgnoreCase(field.getAttributeValue("required"))
            );
            variables.add(variable);
          }
        }
      }

      parsedUserTasks.add(
          new ParsedUserTask(taskId, taskName, formKey, variables, isSubProcessTask, subProcessId, subProcessName)
      );
    }

    return parsedUserTasks;
  }




  private void readGateways(Process process) {
    // Coletar todos os gateways (exclusive, inclusive, parallel)
    Collection<Gateway> gateways = process.getChildElementsByType(Gateway.class);

    for (Gateway gw : gateways) {
      String gwType = gw.getElementType().getTypeName(); // tipo (ExclusiveGateway, ParallelGateway, etc.)
      LOGGER.info("Gateway encontrado: {} (id={}, name={})", gwType, gw.getId(), gw.getName());

      for (SequenceFlow flow : gw.getOutgoing()) {
        String cond = (flow.getConditionExpression() != null)
            ? flow.getConditionExpression().getTextContent()
            : "(sem condição)";
        FlowNode target = flow.getTarget();
        LOGGER.info("  -> Saída para {} (id={}, name={}), condição: {}",
            target.getElementType().getTypeName(),
            target.getId(),
            target.getName(),
            cond);
      }
    }
  }


  @Override
  public String sanitizeBpmnXml(String rawXml) {
    try (InputStream is = new ByteArrayInputStream(rawXml.getBytes(StandardCharsets.UTF_8))) {
      BpmnModelInstance modelInstance = Bpmn.readModelFromStream(is);
      return Bpmn.convertToString(modelInstance);
    } catch (Exception e) {
      LOGGER.error("Failed to sanitize BPMN XML", e);
      throw IgrpResponseStatusException.badRequest("Invalid BPMN XML: " + e.getMessage());
    }
  }
}


