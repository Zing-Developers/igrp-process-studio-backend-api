package cv.igrp.platform.process_manager_studio.shared.domain.valueobject;

import lombok.Value;

import java.util.UUID;

@Value
public class ArtifactVariableId {

  Identifier identifier;

  public static ArtifactVariableId of(String id) {
    return new ArtifactVariableId(Identifier.from(id));
  }

  public static ArtifactVariableId of(UUID id) {
    return new ArtifactVariableId(Identifier.from(id));
  }

  public static ArtifactVariableId generate() {
    return new ArtifactVariableId(Identifier.generateNew());
  }

  @Override
  public String toString() {
    return identifier.toString();
  }

}
