package cv.igrp.platform.process_manager_studio.shared.infrastructure.bpmn;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ParsedProcess {

  private String key;
  private String name;
  private List<ParsedUserTask> userTasks;

  public ParsedProcess() {
  }

  public ParsedProcess(String key, String name, List<ParsedUserTask> userTasks) {
    this.key = key;
    this.name = name;
    this.userTasks = userTasks!=null ? userTasks : new ArrayList<>();
  }

  @Override
  public String toString() {
    return "ParsedProcess{" +
        "key='" + key + '\'' +
        ", name='" + name + '\'' +
        ", userTasks=" + userTasks +
        '}';
  }
}
