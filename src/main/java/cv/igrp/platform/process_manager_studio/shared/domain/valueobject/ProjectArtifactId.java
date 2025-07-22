package cv.igrp.platform.process_manager_studio.shared.domain.valueobject;

import lombok.Value;

import java.util.UUID;

@Value
public class ProjectArtifactId {

  Identifier identifier;

  public static ProjectArtifactId of(String id) {
    return new ProjectArtifactId(Identifier.from(id));
  }

  public static ProjectArtifactId of(UUID id) {
    return new ProjectArtifactId(Identifier.from(id));
  }

  public static ProjectArtifactId generate() {
    return new ProjectArtifactId(Identifier.generateNew());
  }

  @Override
  public String toString() {
    return identifier.toString();
  }
}
