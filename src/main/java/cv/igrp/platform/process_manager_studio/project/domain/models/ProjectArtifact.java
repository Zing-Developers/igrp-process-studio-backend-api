package cv.igrp.platform.process_manager_studio.project.domain.models;

import cv.igrp.platform.process_manager_studio.shared.domain.exceptions.IgrpResponseStatusException;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ArtifactVariableId;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProjectArtifactId;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProcessDefinitionId;
import lombok.Getter;

import java.util.*;

@Getter
public class ProjectArtifact {

  private final ProjectArtifactId id;
  private final ProcessDefinitionId processDefinitionId;

  private String taskKey;
  private String name;

  private final List<ArtifactVariable> variables;

  private ProjectArtifact(ProjectArtifactId id, ProcessDefinitionId processDefinitionId, String taskKey, String name, List<ArtifactVariable> variables) {
    this.id = Objects.requireNonNull(id, "id cannot be null");
    this.processDefinitionId = Objects.requireNonNull(processDefinitionId, "processDefinitionId cannot be null");
    this.taskKey = taskKey;
    this.name = name;
    this.variables = variables != null ? new ArrayList<>(variables) : new ArrayList<>();
  }

  public static ProjectArtifact create(ProcessDefinitionId processDefinitionId, String taskKey, String name) {
    if (taskKey == null || taskKey.isBlank()) {
      throw IgrpResponseStatusException.badRequest("TaskKey cannot be null or blank");
    }
    /*if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Name cannot be null or blank");
    }*/
    return new ProjectArtifact(
        ProjectArtifactId.generate(),
        processDefinitionId,
        taskKey,
        name,
        new ArrayList<>()
    );
  }

  public static ProjectArtifact rebuild(ProjectArtifactId id, ProcessDefinitionId processDefinitionId, String taskKey, String name, List<ArtifactVariable> variables) {
    return new ProjectArtifact(id, processDefinitionId, taskKey, name, variables);
  }

  public void updateInfo(String taskKey, String name) {
    if (taskKey == null || taskKey.isBlank()) {
      throw IgrpResponseStatusException.badRequest("TaskKey cannot be null or blank");
    }
    /*if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Name cannot be null or blank");
    }*/
    this.taskKey = taskKey;
    this.name = name;
  }

  public void addVariable(ArtifactVariable variable) {
    if (variable == null) throw new IllegalArgumentException("Variable cannot be null");
    this.variables.add(variable);
  }

  public void addVariables(List<ArtifactVariable> variables) {
    if (variables == null || variables.isEmpty()) return;
    this.variables.addAll(variables);
  }

  public void removeVariable(ArtifactVariable variable) {
    this.variables.remove(variable);
  }

  public void updateVariables(List<ArtifactVariable> updatedVariables) {
    if (updatedVariables == null) return;

    for (ArtifactVariable updatedVar : updatedVariables) {
      this.variables.stream()
          .filter(v -> v.getId().equals(updatedVar.getId()))
          .findFirst()
          .ifPresent(v -> v.updateInfo(updatedVar.getName(), updatedVar.getType(), updatedVar.getDefaultValue(), updatedVar.isRequired()));
    }
  }



  public Optional<ArtifactVariable> getVariableById(ArtifactVariableId id) {
    if (id == null) return Optional.empty();
    return variables.stream()
        .filter(v -> v.getId().equals(id))
        .findFirst();
  }

  public Optional<ArtifactVariable> getVariableByName(String name) {
    if (name == null || name.isBlank()) return Optional.empty();
    return variables.stream()
        .filter(v -> name.equals(v.getName()))
        .findFirst();
  }

}
