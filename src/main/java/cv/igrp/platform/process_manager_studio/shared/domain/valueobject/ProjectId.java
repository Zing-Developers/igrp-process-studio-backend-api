package cv.igrp.platform.process_manager_studio.shared.domain.valueobject;

import java.util.UUID;

public record ProjectId(Identifier identifier) {

  public static ProjectId generate() {
    return new ProjectId(Identifier.generateNew());
  }

  public static ProjectId of(String id) {
    return new ProjectId(Identifier.from(id));
  }

  public static ProjectId of(UUID id) {
    return new ProjectId(Identifier.from(id));
  }

  @Override
  public String toString() {
    return identifier.toString();
  }
}
