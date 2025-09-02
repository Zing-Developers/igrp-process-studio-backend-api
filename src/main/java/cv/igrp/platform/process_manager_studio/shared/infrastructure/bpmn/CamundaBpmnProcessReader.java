package cv.igrp.platform.process_manager_studio.shared.infrastructure.bpmn;

import cv.igrp.platform.process_manager_studio.shared.domain.exceptions.IgrpResponseStatusException;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SubProcess;
import org.camunda.bpm.model.bpmn.instance.UserTask;
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

      // UserTasks do processo principal
      parsedUserTasks.addAll(extractUserTasks(process.getChildElementsByType(UserTask.class)));

      // UserTasks dos sub-processos
      for (SubProcess subProcess : process.getChildElementsByType(SubProcess.class)) {
        parsedUserTasks.addAll(extractUserTasks(subProcess.getChildElementsByType(UserTask.class)));
      }

      ParsedProcess parsedProcess = new ParsedProcess(processKey, processName, parsedUserTasks);
      LOGGER.info("Parsed process object: {}", parsedProcess);
      return parsedProcess;

    } catch (Exception e) {
      LOGGER.error("Erro ao ler processo BPMN", e);
      throw new RuntimeException("Erro ao ler processo BPMN: " + e.getMessage(), e);
    }
  }

  private List<ParsedUserTask> extractUserTasks(Collection<UserTask> userTasks) {
    List<ParsedUserTask> parsedUserTasks = new ArrayList<>();

    for (UserTask userTask : userTasks) {
      String taskId = userTask.getId();
      String taskName = userTask.getName();
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

      parsedUserTasks.add(new ParsedUserTask(taskId, taskName, variables));
    }

    return parsedUserTasks;
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


