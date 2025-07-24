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
  private String title;
  private String description;
  private String processKey;
  private String bpmnDiagramUrl;
  private byte[] bpmnFileContent;
  private Integer version;
  private ProcessDefinitionState state;
  private final LocalDate deploymentDate;
  private final String deploymentId;

  private final List<ProjectArtifact> artifacts;

  private ProcessDefinition(ProcessDefinitionId id, ProjectId projectId, String processKey, String bpmnDiagramUrl, byte[] bpmnFileContent, Integer version,
                            List<ProjectArtifact> artifacts, ProcessDefinitionState state, LocalDate deploymentDate, String deploymentId, String title, String description) {
    this.id = Objects.requireNonNull(id, "id cannot be null");
    this.projectId = Objects.requireNonNull(projectId);
    this.processKey = processKey;
    this.bpmnDiagramUrl = bpmnDiagramUrl;
    this.bpmnFileContent = bpmnFileContent;
    this.version = version;
    this.artifacts = artifacts != null ? new ArrayList<>(artifacts) : new ArrayList<>();
    this.state = state;
    this.deploymentDate = deploymentDate;
    this.deploymentId = deploymentId;
    this.title = title;
    this.description = description;
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
        null,
        null,
        null
    );
  }

  public static ProcessDefinition create(ProjectId projectId, String processKey, byte[] bpmnFileContent) {

    return new ProcessDefinition(
        ProcessDefinitionId.generate(),
        projectId,
        processKey,
        null,
        bpmnFileContent,
        null,
        new ArrayList<>(),
        ProcessDefinitionState.DRAFT,
        null,
        null,
        null,
        null
    );
  }

  public static ProcessDefinition create(ProjectId projectId, String processKey, String title, String description ) {

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
        description
    );
  }

  public static ProcessDefinition rebuild(ProcessDefinitionId id, ProjectId projectId, String processKey, String bpmnDiagramUrl, byte[] bpmnFileContent, Integer version,
                                          List<ProjectArtifact> artifacts, ProcessDefinitionState state, LocalDate deploymentDate, String deploymentId, String title, String description) {
    return new ProcessDefinition(id, projectId, processKey, bpmnDiagramUrl, bpmnFileContent, version, artifacts, state, deploymentDate, deploymentId, title, description);
  }

  public void updateInfo(String bpmnDiagramUrl, Integer version, byte[] bpmnFileContent) {
    this.bpmnFileContent = bpmnFileContent;
    this.bpmnDiagramUrl = bpmnDiagramUrl;
    this.version = version;
  }

  public void updateInfo(Integer version, byte[] bpmnFileContent) {
    this.bpmnFileContent = bpmnFileContent;
    this.version = version;
  }

  public void updateInfo(String processKey, String bpmnDiagramUrl) {
    this.processKey = processKey;
    this.bpmnDiagramUrl = bpmnDiagramUrl;
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

  public void replaceArtifacts(List<ProjectArtifact> newArtifacts) {
    this.artifacts.clear();
    this.artifacts.addAll(newArtifacts);
  }


  public void cleanArtifacts() {
    this.artifacts.clear();
  }


  public void updateBpmnContent(byte[] bpmnBytes) {
    this.bpmnFileContent = bpmnBytes;
  }

  public boolean isDraft() {
    return this.state == ProcessDefinitionState.DRAFT;
  }
}
