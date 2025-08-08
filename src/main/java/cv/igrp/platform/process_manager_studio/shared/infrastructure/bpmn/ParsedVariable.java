package cv.igrp.platform.process_manager_studio.shared.infrastructure.bpmn;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParsedVariable {

  private String id;
  private String label;
  private String type;
  private String defaultValue;
  private boolean required;

  public ParsedVariable() {
  }

  public ParsedVariable(String id, String label, String type, String defaultValue, boolean required) {
    this.id = id;
    this.label = label;
    this.type = type;
    this.defaultValue = defaultValue;
    this.required = required;
  }

  @Override
  public String toString() {
    return "ParsedVariable{" +
        "id='" + id + '\'' +
        ", label='" + label + '\'' +
        ", type='" + type + '\'' +
        ", defaultValue='" + defaultValue + '\'' +
        ", required=" + required +
        '}';
  }
}
