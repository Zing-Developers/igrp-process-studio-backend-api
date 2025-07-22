package cv.igrp.platform.process_manager_studio.project.domain.models;

import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.Identifier;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProcessDefinitionId;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProjectArtifactId;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProjectId;
import lombok.Getter;

import java.util.*;

@Getter
public class ProcessDefinition {

  private final ProcessDefinitionId id;
  private final ProjectId projectId;
  private String processKey;
  private String bpmnDiagramUrl;
  private Integer version;
  private String rejectedReason;

  private final List<ProjectArtifact> artifacts;

  private ProcessDefinition(ProcessDefinitionId id, ProjectId projectId, String processKey, String bpmnDiagramUrl, Integer version, String rejectedReason, List<ProjectArtifact> artifacts) {
    this.id = Objects.requireNonNull(id, "id cannot be null");
    this.projectId = Objects.requireNonNull(projectId);
    this.processKey = processKey;
    this.bpmnDiagramUrl = bpmnDiagramUrl;
    this.version = version;
    this.rejectedReason = rejectedReason;
    this.artifacts = artifacts != null ? new ArrayList<>(artifacts) : new ArrayList<>();
  }

  public static ProcessDefinition create(ProjectId projectId, String processKey, String bpmnDiagramUrl) {

    Integer version = 1; // Default version for new process definitions

    return new ProcessDefinition(
        ProcessDefinitionId.generate(),
        projectId,
        processKey,
        bpmnDiagramUrl,
        version,
        null,
        new ArrayList<>()
    );
  }

  public static ProcessDefinition rebuild(ProcessDefinitionId id,ProjectId projectId, String processKey, String bpmnDiagramUrl, Integer version, String rejectedReason, List<ProjectArtifact> artifacts) {
    return new ProcessDefinition(id, projectId,processKey, bpmnDiagramUrl, version, rejectedReason, artifacts);
  }

  public void updateInfo(String processKey, String bpmnDiagramUrl, Integer version) {
    this.processKey = processKey;
    this.bpmnDiagramUrl = bpmnDiagramUrl;
    this.version = version;
  }

  public void approve() {
    this.rejectedReason = null;
  }

  public void reject(String reason) {
    if(reason == null || reason.isBlank()) {
      throw new IllegalArgumentException("Rejected reason cannot be blank");
    }
    this.rejectedReason = reason;
  }

  public void addArtifact(ProjectArtifact artifact) {
    if(artifact == null) throw new IllegalArgumentException("Artifact cannot be null");
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


}
