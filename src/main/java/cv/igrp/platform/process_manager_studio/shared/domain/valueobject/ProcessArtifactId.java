package cv.igrp.platform.process_manager_studio.shared.domain.valueobject;

import java.util.UUID;

public record ProcessArtifactId(Identifier identifier) {

  public static ProcessArtifactId of(String id) {
    return new ProcessArtifactId(Identifier.from(id));
  }

  public static ProcessArtifactId of(UUID id) {
    return new ProcessArtifactId(Identifier.from(id));
  }

  public static ProcessArtifactId generate() {
    return new ProcessArtifactId(Identifier.generateNew());
  }

  @Override
  public String toString() {
    return identifier.toString();
  }
}
