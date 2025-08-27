package cv.igrp.platform.process_manager_studio.project.domain.models;

import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProcessDefinitionId;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProcessVariableId;
import lombok.Getter;

import java.util.Objects;

@Getter
public class ProcessVariable {

  private final ProcessVariableId id;
  private String name;
  private String type;
  private String defaultValue;
  private boolean required;
  private final ProcessDefinitionId processDefinitionId;

  private ProcessVariable(ProcessVariableId id, String name, String type, String defaultValue, boolean required, ProcessDefinitionId processDefinitionId) {
    this.id = Objects.requireNonNull(id);
    this.name = Objects.requireNonNull(name, "Variable name cannot be null");
    this.type = type;
    this.defaultValue = defaultValue;
    this.required = required;
    this.processDefinitionId = processDefinitionId;
  }

  public static ProcessVariable create(String name, String type, String defaultValue, boolean required, ProcessDefinitionId processDefinitionId) {
    return new ProcessVariable(ProcessVariableId.generate(), name, type, defaultValue, required, processDefinitionId);
  }

  public static ProcessVariable rebuild(ProcessVariableId id, String name, String type, String defaultValue,
                                        boolean required, ProcessDefinitionId processDefinitionId) {
    return new ProcessVariable(id, name, type, defaultValue, required, processDefinitionId);
  }

  public void updateInfo(String name, String type, String defaultValue, boolean required) {
    this.name = name;
    this.type = type;
    this.defaultValue = defaultValue;
    this.required = required;
  }
}
