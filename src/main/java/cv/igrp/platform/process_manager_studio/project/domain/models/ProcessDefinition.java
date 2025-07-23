package cv.igrp.platform.process_manager_studio.project.domain.models;

import cv.igrp.platform.process_manager_studio.shared.application.constants.ProcessDefinitionState;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProcessDefinitionId;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProjectArtifactId;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProjectId;
import lombok.Getter;

import java.time.LocalDate;
import java.util.*;

@Getter
public class ProcessDefinition {

  private final ProcessDefinitionId id;
  private final ProjectId projectId;
  private String processKey;
  private String bpmnDiagramUrl;
  private Integer version;
  private ProcessDefinitionState state;
  private String rejectedReason;
  private final LocalDate deploymentDate;
  private final String deploymentId;

  private final List<ProjectArtifact> artifacts;

  private ProcessDefinition(ProcessDefinitionId id, ProjectId projectId, String processKey, String bpmnDiagramUrl, Integer version, String rejectedReason,
                            List<ProjectArtifact> artifacts, ProcessDefinitionState state, LocalDate deploymentDate, String deploymentId) {
    this.id = Objects.requireNonNull(id, "id cannot be null");
    this.projectId = Objects.requireNonNull(projectId);
    this.processKey = processKey;
    this.bpmnDiagramUrl = bpmnDiagramUrl;
    this.version = version;
    this.rejectedReason = rejectedReason;
    this.artifacts = artifacts != null ? new ArrayList<>(artifacts) : new ArrayList<>();
    this.state = state;
    this.deploymentDate = deploymentDate;
    this.deploymentId = deploymentId;
  }


  public static ProcessDefinition create(ProjectId projectId, String processKey, String bpmnDiagramUrl) {

    return new ProcessDefinition(
        ProcessDefinitionId.generate(),
        projectId,
        processKey,
        bpmnDiagramUrl,
        null,
        null,
        new ArrayList<>(),
        ProcessDefinitionState.DRAFT,
        null,
        null
    );
  }

  public static ProcessDefinition rebuild(ProcessDefinitionId id, ProjectId projectId, String processKey, String bpmnDiagramUrl, Integer version, String rejectedReason,
                                          List<ProjectArtifact> artifacts, ProcessDefinitionState state, LocalDate deploymentDate, String deploymentId) {
    return new ProcessDefinition(id, projectId, processKey, bpmnDiagramUrl, version, rejectedReason, artifacts, state, deploymentDate, deploymentId);
  }

  public void updateInfo(String processKey, String bpmnDiagramUrl, Integer version) {
    this.processKey = processKey;
    this.bpmnDiagramUrl = bpmnDiagramUrl;
    this.version = version;
  }

  public void updateInfo(String processKey, String bpmnDiagramUrl) {
    this.processKey = processKey;
    this.bpmnDiagramUrl = bpmnDiagramUrl;
  }

  public void approve() {
    this.rejectedReason = null;
  }

  public void upateState(ProcessDefinitionState newState) {
    if (newState == null) {
      throw new IllegalArgumentException("New state cannot be null");
    }
    this.state = newState;
  }

  public void upateVersion(Integer newVersion) {
    if (newVersion == null || newVersion <= 0) {
      throw new IllegalArgumentException("Version must be a positive integer");
    }
    this.version = newVersion;
  }

  public void reject(String reason) {
    if (reason == null || reason.isBlank()) {
      throw new IllegalArgumentException("Rejected reason cannot be blank");
    }
    this.rejectedReason = reason;
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

  public void publish() {
    this.state = ProcessDefinitionState.PUBLISHED;
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


}
