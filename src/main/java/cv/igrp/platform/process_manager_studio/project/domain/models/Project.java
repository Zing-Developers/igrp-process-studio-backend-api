package cv.igrp.platform.process_manager_studio.project.domain.models;

import cv.igrp.platform.process_manager_studio.shared.application.constants.ProcessDefinitionState;
import cv.igrp.platform.process_manager_studio.shared.domain.exceptions.IgrpResponseStatusException;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProcessDefinitionId;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProjectId;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public class Project {

  private final ProjectId id;
  private String code;
  private String name;
  private String description;
  private boolean active;
  private String appCode;

  private final List<ProcessDefinition> processDefinitions;

  private Project(ProjectId id, String code, String name, String description, boolean active, String appCode, List<ProcessDefinition> processDefinitions) {
    this.id = Objects.requireNonNull(id, "id cannot be null");
    this.code = Objects.requireNonNull(code, "oode cannot be null");
    this.name = name;
    this.description = description;
    this.active = active;
    this.appCode = appCode;
    this.processDefinitions = processDefinitions != null ? new ArrayList<>(processDefinitions) : new ArrayList<>();
  }


  public static Project rebuild(ProjectId id, String code, String name, String description, boolean active,  String appCode, List<ProcessDefinition> processDefinitions) {
    return new Project(id, code, name, description, active, appCode, processDefinitions);
  }

  public static Project create(String code, String name, String description, String appCode) {

    if (code == null || code.isBlank()) {
      throw IgrpResponseStatusException.badRequest("Project code cannot be null or empty");
    }
    var applicationBase = appCode!= null  && !appCode.isEmpty() ? appCode : code; // todo figure out how to handle appCode properly

    return new Project(
        ProjectId.generate(),
        code,
        name,
        description,
        true,
        applicationBase,
        new ArrayList<>()
    );
  }

  public void updateInfo(String code, String name, String description, String appCode) {
    this.code = code;
    this.name = name;
    this.description = description;
    this.appCode = appCode;
  }


  public void activate() {
    this.active = true;
  }

  public void deactivate() {
    this.active = false;
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

  public Optional<ProcessDefinition> getDraftProcessDefinitionByKey(String processKey) {
    return processDefinitions.stream()
        .filter(pd -> processKey.equals(pd.getProcessKey()) && pd.getState() == ProcessDefinitionState.DRAFT)
        .findFirst();
  }

  public Optional<ProcessDefinition> getDraftProcessDefinitionById(ProcessDefinitionId id) {
    if (id == null) return Optional.empty();
    return processDefinitions.stream()
        .filter(pd -> pd.getId().equals(id)  && pd.getState() == ProcessDefinitionState.DRAFT)
        .findFirst();
  }


    public void disable() {
    this.active = false;
    }

  public void enable() {
    this.active = true;
  }

  public boolean hasAtLeastOnePublishedProcess() {
    return processDefinitions.stream()
        .anyMatch(pd -> pd.getState() == ProcessDefinitionState.PUBLISHED);
  }

  public List<ProcessDefinition> getAllProcessAndLastestForProcessKey(){
    if (this.getProcessDefinitions() == null || this.getProcessDefinitions().isEmpty())
      return new ArrayList<>();

    return processDefinitions.stream()
        .filter(pd -> !(pd.getState() == ProcessDefinitionState.PUBLISHED && !pd.isLatest()))
        .collect(Collectors.toList());
  }

  public List<ProcessDefinition> getLatestPublishedProcesses() {
    return processDefinitions.stream()
        .filter(pd -> pd.getState() == ProcessDefinitionState.PUBLISHED && pd.isLatest())
        .collect(Collectors.toList());
  }

}
