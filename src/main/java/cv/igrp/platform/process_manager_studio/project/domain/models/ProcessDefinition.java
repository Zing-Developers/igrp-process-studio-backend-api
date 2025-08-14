package cv.igrp.platform.process_manager_studio.project.domain.models;

import cv.igrp.platform.process_manager_studio.shared.application.constants.ProcessDefinitionState;
import cv.igrp.platform.process_manager_studio.shared.domain.exceptions.IgrpResponseStatusException;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.BpmDriagram;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProcessDefinitionId;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProjectArtifactId;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProjectId;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Getter
public class ProcessDefinition {

  private final ProcessDefinitionId id;
  private final ProjectId projectId;
  private String title;
  private String description;
  private String processKey;
  private final String bpmnDiagramUrl;
  private BpmDriagram bpmDriagram;
  private Integer version;
  private ProcessDefinitionState state;
  private LocalDateTime deploymentDate;
  private String deploymentId;
  private boolean isLatest;

  private final List<ProjectArtifact> artifacts;

  private ProcessDefinition(ProcessDefinitionId id, ProjectId projectId, String processKey, String bpmnDiagramUrl, BpmDriagram bpmDriagram, Integer version,
                            List<ProjectArtifact> artifacts, ProcessDefinitionState state, LocalDateTime deploymentDate, String deploymentId,
                            String title, String description, boolean isLatest) {
    this.id = Objects.requireNonNull(id, "id cannot be null");
    this.projectId = Objects.requireNonNull(projectId);
    this.processKey = processKey;
    this.bpmnDiagramUrl = bpmnDiagramUrl;
    this.bpmDriagram = bpmDriagram;
    this.version = version;
    this.artifacts = artifacts != null ? new ArrayList<>(artifacts) : new ArrayList<>();
    this.state = state;
    this.deploymentDate = deploymentDate;
    this.deploymentId = deploymentId;
    this.title = title;
    this.description = description;
    this.isLatest = isLatest;
  }


  public static ProcessDefinition create(ProjectId projectId, String processKey, BpmDriagram bpmDriagram, String title, String description) {

    if (processKey == null || processKey.isBlank()) {
      throw IgrpResponseStatusException.badRequest("Process key cannot be null or empty");
    }

    return new ProcessDefinition(
        ProcessDefinitionId.generate(),
        projectId,
        processKey,
        null,
        bpmDriagram,
        null,
        new ArrayList<>(),
        ProcessDefinitionState.DRAFT,
        null,
        null,
        title,
        description,
        false
    );
  }


  public static ProcessDefinition createNew(ProjectId projectId, String processKey, String title, String description) {

    if (processKey == null || processKey.isBlank()) {
      throw IgrpResponseStatusException.badRequest("Process key cannot be null or empty");
    }
    return new ProcessDefinition(
        ProcessDefinitionId.generate(),
        projectId,
        processKey,
        null,
        null,
        null,
        new ArrayList<>(),
        ProcessDefinitionState.DRAFT,
        null,
        null,
        title,
        description,
        false
    );
  }

  public static ProcessDefinition rebuild(ProcessDefinitionId id, ProjectId projectId, String processKey, String bpmnDiagramUrl, BpmDriagram bpmDriagram, Integer version,
                                          List<ProjectArtifact> artifacts, ProcessDefinitionState state, LocalDateTime deploymentDate,
                                          String deploymentId, String title, String description, boolean isLatest) {
    return new ProcessDefinition(id, projectId, processKey, bpmnDiagramUrl, bpmDriagram, version, artifacts, state, deploymentDate, deploymentId, title, description, isLatest);
  }


  public void upateState(ProcessDefinitionState newState) {
    if (newState == null) {
      throw new IllegalArgumentException("New state cannot be null");
    }
    this.state = newState;
  }


  public void addArtifact(ProjectArtifact artifact) {
    if (artifact == null) throw new IllegalArgumentException("Artifact cannot be null");
    this.artifacts.add(artifact);
  }

  public void addArtifacts(List<ProjectArtifact> artifacts) {
    if (artifacts == null || artifacts.isEmpty()) return;
    this.artifacts.addAll(artifacts);
  }

  public void removeArtifact(ProjectArtifact artifact) {
    this.artifacts.remove(artifact);
  }

  public void updateArtifacts(List<ProjectArtifact> updatedArtifacts) {
    if (updatedArtifacts == null) return;

    for (ProjectArtifact updatedArtifact : updatedArtifacts) {
      this.artifacts.stream()
          .filter(a -> a.getId().equals(updatedArtifact.getId()))
          .findFirst()
          .ifPresent(existingArtifact ->
                  existingArtifact.updateInfo(updatedArtifact.getTaskKey(), updatedArtifact.getName())
              // Se precisar atualizar variáveis, pode chamar um método do ProjectArtifact aqui também
          );
    }
  }


  public Optional<ProjectArtifact> getArtifactById(ProjectArtifactId id) {
    if (id == null) return Optional.empty();
    return artifacts.stream()
        .filter(artifact -> artifact.getId().equals(id))
        .findFirst();
  }

  public Optional<ProjectArtifact> getArtifactByTaskKey(String taskKey) {
    if (taskKey == null || taskKey.isBlank()) return Optional.empty();
    return artifacts.stream()
        .filter(artifact -> taskKey.equals(artifact.getTaskKey()))
        .findFirst();
  }

  public void replaceArtifacts(List<ProjectArtifact> newArtifacts) {
    this.artifacts.clear();
    this.artifacts.addAll(newArtifacts);
  }


  public void cleanArtifacts() {
    this.artifacts.clear();
  }


  public void updateBpmnContent(BpmDriagram bpmDriagram) {
    this.bpmDriagram = bpmDriagram;
  }

  public boolean isDraft() {
    return this.state == ProcessDefinitionState.DRAFT;
  }

  public boolean isPublish() {
    return this.state == ProcessDefinitionState.PUBLISHED;
  }


  public void deploy(String deploymentId, LocalDateTime deployedAt, String version) {
    this.deploymentId = deploymentId;
    this.deploymentDate = deployedAt;
    this.version = Integer.parseInt(version);
    this.state = ProcessDefinitionState.PUBLISHED;
    this.isLatest = true;
  }

  public void updateBaseInfo(String processKey, String title, String description) {
    this.processKey = processKey;
    this.title = title;
    this.description = description;
  }

  public void updateBaseInfo(String title, String description) {
    this.title = title;
    this.description = description;
  }
}
