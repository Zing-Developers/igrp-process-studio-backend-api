package cv.igrp.platform.process_manager_studio.shared.domain.valueobject;

import lombok.Value;

import java.util.UUID;

@Value
public class ProcessDefinitionId {
  Identifier identifier;

  public static ProcessDefinitionId of(String id) {
    return new ProcessDefinitionId(Identifier.from(id));
  }

  public static ProcessDefinitionId of(UUID id) {
    return new ProcessDefinitionId(Identifier.from(id));
  }

  public static ProcessDefinitionId generate() {
    return new ProcessDefinitionId(Identifier.generateNew());
  }

  @Override
  public String toString() {
    return identifier.toString();
  }
}
