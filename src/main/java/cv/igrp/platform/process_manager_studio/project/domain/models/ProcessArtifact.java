package cv.igrp.platform.process_manager_studio.project.domain.models;

import cv.igrp.platform.process_manager_studio.shared.domain.exceptions.IgrpResponseStatusException;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ArtifactVariableId;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProcessDefinitionId;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProcessArtifactId;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Getter
public class ProcessArtifact {

  private final ProcessArtifactId id;
  private final ProcessDefinitionId processDefinitionId;

  private String taskKey;
  private String name;
  private String formKey;

  private boolean isSubProcessTask;
  private String subProcessId;
  private String subProcessName;

  private final List<ArtifactVariable> variables;

  private ProcessArtifact(ProcessArtifactId id,
      ProcessDefinitionId processDefinitionId,
      String taskKey,
      String name,
      String formKey,
      boolean isSubProcessTask,
      String subProcessId,
      String subProcessName,
      List<ArtifactVariable> variables) {
    this.id = Objects.requireNonNull(id, "id cannot be null");
    this.processDefinitionId = Objects.requireNonNull(processDefinitionId, "processDefinitionId cannot be null");
    this.taskKey = taskKey;
    this.name = name;
    this.formKey = formKey;
    this.isSubProcessTask = isSubProcessTask;
    this.subProcessId = subProcessId;
    this.subProcessName = subProcessName;
    this.variables = variables != null ? new ArrayList<>(variables) : new ArrayList<>();
  }

  public static ProcessArtifact create(ProcessDefinitionId processDefinitionId, String taskKey, String name,
      String formKey) {
    if (taskKey == null || taskKey.isBlank()) {
      throw IgrpResponseStatusException.badRequest("TaskKey cannot be null or blank");
    }
    return new ProcessArtifact(
        ProcessArtifactId.generate(),
        processDefinitionId,
        taskKey,
        name,
        formKey,
        false, // por default não é subprocesso
        null,
        null,
        new ArrayList<>());
  }

  public static ProcessArtifact createFromSubProcess(ProcessDefinitionId processDefinitionId,
      String taskKey,
      String name,
      String formKey,
      String subProcessId,
      String subProcessName) {
    if (taskKey == null || taskKey.isBlank()) {
      throw IgrpResponseStatusException.badRequest("TaskKey cannot be null or blank");
    }
    return new ProcessArtifact(
        ProcessArtifactId.generate(),
        processDefinitionId,
        taskKey,
        name,
        formKey,
        true,
        subProcessId,
        subProcessName,
        new ArrayList<>());
  }

  public static ProcessArtifact rebuild(ProcessArtifactId id,
      ProcessDefinitionId processDefinitionId,
      String taskKey,
      String name,
      String formKey,
      boolean isSubProcessTask,
      String subProcessId,
      String subProcessName,
      List<ArtifactVariable> variables) {
    return new ProcessArtifact(id, processDefinitionId, taskKey, name, formKey, isSubProcessTask, subProcessId,
        subProcessName, variables);
  }

  public void updateInfo(String taskKey, String name, String formKey) {
    if (taskKey == null || taskKey.isBlank()) {
      throw IgrpResponseStatusException.badRequest("TaskKey cannot be null or blank");
    }
    this.taskKey = taskKey;
    this.name = name;
    this.formKey = formKey;
  }

  public void addVariable(ArtifactVariable variable) {
    if (variable == null)
      throw new IllegalArgumentException("Variable cannot be null");
    this.variables.add(variable);
  }

  public void addVariables(List<ArtifactVariable> variables) {
    if (variables == null || variables.isEmpty())
      return;
    this.variables.addAll(variables);
  }

  public void removeVariable(ArtifactVariable variable) {
    this.variables.remove(variable);
  }

  public void updateVariables(List<ArtifactVariable> updatedVariables) {
    if (updatedVariables == null)
      return;

    for (ArtifactVariable updatedVar : updatedVariables) {
      this.variables.stream()
          .filter(v -> v.getId().equals(updatedVar.getId()))
          .findFirst()
          .ifPresent(v -> v.updateInfo(
              updatedVar.getName(),
              updatedVar.getType(),
              updatedVar.getDefaultValue(),
              updatedVar.isRequired()));
    }
  }

  public Optional<ArtifactVariable> getVariableById(ArtifactVariableId id) {
    if (id == null)
      return Optional.empty();
    return variables.stream()
        .filter(v -> v.getId().equals(id))
        .findFirst();
  }

  public Optional<ArtifactVariable> getVariableByName(String name) {
    if (name == null || name.isBlank())
      return Optional.empty();
    return variables.stream()
        .filter(v -> name.equals(v.getName()))
        .findFirst();
  }

  private AuditTrail audit;

  public void setAudit(AuditTrail audit) {
    this.audit = audit;
  }

}
