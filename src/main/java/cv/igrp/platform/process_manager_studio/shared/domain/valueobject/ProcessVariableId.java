package cv.igrp.platform.process_manager_studio.shared.domain.valueobject;

import java.util.UUID;

public record ProcessVariableId(Identifier identifier) {

  public static ProcessVariableId of(String id) {
    return new ProcessVariableId(Identifier.from(id));
  }

  public static ProcessVariableId of(UUID id) {
    return new ProcessVariableId(Identifier.from(id));
  }

  public static ProcessVariableId generate() {
    return new ProcessVariableId(Identifier.generateNew());
  }

  @Override
  public String toString() {
    return identifier.toString();
  }
}
