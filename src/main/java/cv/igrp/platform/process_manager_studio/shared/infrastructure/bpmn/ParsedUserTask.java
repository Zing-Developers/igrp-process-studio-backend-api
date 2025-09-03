package cv.igrp.platform.process_manager_studio.shared.infrastructure.bpmn;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ParsedUserTask {

  private String id;
  private String name;
  private List<ParsedVariable> variables;
  private boolean isSubProcessTask;
  private String subProcessId;
  private String subProcessName;



  public ParsedUserTask() {
    this.variables = new ArrayList<>();
  }

  public ParsedUserTask(String id, String name, List<ParsedVariable> variables) {
    this.id = id;
    this.name = name;
    this.variables = variables!= null ? variables : new ArrayList<>();
  }

  public ParsedUserTask(String id, String name, List<ParsedVariable> variables, boolean isSubProcessTask, String subProcessId, String subProcessName) {
    this.isSubProcessTask = isSubProcessTask;
    this.subProcessId = subProcessId;
    this.subProcessName = subProcessName;
    this.id = id;
    this.name = name;
    this.variables = variables!= null ? variables : new ArrayList<>();
  }

  @Override
  public String toString() {
    return "ParsedUserTask{" +
        "id='" + id + '\'' +
        ", name='" + name + '\'' +
        ", variables=" + variables +
        ", isSubProcessTask=" + isSubProcessTask +
        ", subProcessId='" + subProcessId + '\'' +
        ", subProcessName='" + subProcessName + '\'' +
        '}';
  }
}
