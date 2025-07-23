package cv.igrp.platform.process_manager_studio.project.domain.models;

import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ArtifactVariableId;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProjectArtifactId;
import lombok.Getter;

import java.util.Objects;

@Getter
public class ArtifactVariable {

  private final ArtifactVariableId id;
  private final ProjectArtifactId artifactId;
  private final String artifactVariableKey;
  private String name;
  private String type;
  private String defaultValue;
  private boolean isRequired;

  private ArtifactVariable(
      ArtifactVariableId id,
      ProjectArtifactId artifactId,
      String artifactVariableKey,
      String name,
      String type,
      String defaultValue,
      boolean isRequired) {
    this.id = Objects.requireNonNull(id, "id cannot be null");
    this.artifactId = Objects.requireNonNull(artifactId, "artifactId cannot be null");
    this.name = name;
    this.type = type;
    this.defaultValue = defaultValue;
    this.isRequired = isRequired;
    this.artifactVariableKey = artifactVariableKey;
  }

  public static ArtifactVariable create(
      ProjectArtifactId artifactId,
      String artifactVariableKey,
      String name,
      String type,
      String defaultValue,
      boolean isRequired) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Name cannot be null or blank");
    }
    return new ArtifactVariable(
        ArtifactVariableId.generate(),
        artifactId,
        artifactVariableKey,
        name,
        type,
        defaultValue,
        isRequired
    );
  }

  public static ArtifactVariable rebuild(
      ArtifactVariableId id,
      ProjectArtifactId artifactId,
      String artifactVariableKey,
      String name,
      String type,
      String defaultValue,
      boolean isRequired) {
    return new ArtifactVariable(id, artifactId,artifactVariableKey, name, type, defaultValue, isRequired);
  }

  public void updateInfo(String name, String type, String defaultValue, boolean isRequired) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Name cannot be null or blank");
    }
    this.name = name;
    this.type = type;
    this.defaultValue = defaultValue;
    this.isRequired = isRequired;
  }

  // Business logic helpers

  public void markRequired() {
    this.isRequired = true;
  }

  public void markOptional() {
    this.isRequired = false;
  }

  public void updateDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }
}
