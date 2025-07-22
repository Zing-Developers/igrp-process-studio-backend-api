package cv.igrp.platform.process_manager_studio.project.domain.models;

import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.Identifier;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProcessDefinitionId;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProjectId;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Getter
public class Project {

  private final ProjectId id;
  private String code;
  private String name;
  private String description;
  private boolean active;
  private Integer currentVersion;

  private final List<ProcessDefinition> processDefinitions;

  private Project(ProjectId id, String code, String name, String description, boolean active, Integer currentVersion, List<ProcessDefinition> processDefinitions) {
    this.id = Objects.requireNonNull(id, "id cannot be null");
    this.code = code;
    this.name = name;
    this.description = description;
    this.active = active;
    this.currentVersion = currentVersion;
    this.processDefinitions = processDefinitions != null ? new ArrayList<>(processDefinitions) : new ArrayList<>();
  }


  public static Project rebuild(ProjectId id, String code, String name, String description, boolean active, Integer currentVersion, List<ProcessDefinition> processDefinitions) {
    return new Project(id, code, name, description, active, currentVersion, processDefinitions);
  }

  public static Project create(String code, String name, String description) {
    return new Project(
        ProjectId.generate(),
        code,
        name,
        description,
        true,
        1,
        new ArrayList<>()
    );
  }

  public void updateInfo(String code, String name, String description, boolean active) {
    this.code = code;
    this.name = name;
    this.description = description;
    this.active = active;
  }


  public void activate() {
    this.active = true;
  }

  public void deactivate() {
    this.active = false;
  }

  public void updateVersion(Integer newVersion) {
    if (newVersion == null || newVersion <= 0) {
      throw new IllegalArgumentException("Version must be positive");
    }
    if (this.currentVersion == null || newVersion > this.currentVersion) {
      this.currentVersion = newVersion;
    }
  }

  public void addProcessDefinition(ProcessDefinition processDefinition) {
    if (processDefinition == null) throw new IllegalArgumentException("ProcessDefinition cannot be null");
    this.processDefinitions.add(processDefinition);
  }

  public void removeProcessDefinition(ProcessDefinition processDefinition) {
    this.processDefinitions.remove(processDefinition);
  }

  public Optional<ProcessDefinition> getProcessDefinitionById(ProcessDefinitionId id) {
    if (id == null) return Optional.empty();
    return processDefinitions.stream()
        .filter(pd -> pd.getId().equals(id))
        .findFirst();
  }

  public Optional<ProcessDefinition> getProcessDefinitionByKey(String processKey) {
    if (processKey == null || processKey.isBlank()) return Optional.empty();
    return processDefinitions.stream()
        .filter(pd -> processKey.equals(pd.getProcessKey()))
        .findFirst();
  }

}
