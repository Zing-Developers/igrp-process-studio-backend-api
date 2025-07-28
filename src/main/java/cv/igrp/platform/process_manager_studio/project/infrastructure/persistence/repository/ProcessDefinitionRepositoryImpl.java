package cv.igrp.platform.process_manager_studio.project.infrastructure.persistence.repository;

import cv.igrp.platform.process_manager_studio.project.domain.models.ProcessDefinition;
import cv.igrp.platform.process_manager_studio.project.domain.repository.ProcessDefinitionRepository;
import cv.igrp.platform.process_manager_studio.project.infrastructure.mappers.ProcessDefinitionMapper;
import cv.igrp.platform.process_manager_studio.shared.application.constants.ProcessDefinitionState;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProcessDefinitionId;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProjectId;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.repository.ProcessDefinitionEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ProcessDefinitionRepositoryImpl implements ProcessDefinitionRepository {

  private final ProcessDefinitionEntityRepository processDefinitionEntityRepository;
  private final ProcessDefinitionMapper processDefinitionMapper;

  @Transactional(readOnly = true)
  @Override
  public Optional<ProcessDefinition> findById(ProcessDefinitionId id) {
    if (id == null) return Optional.empty();
    return processDefinitionEntityRepository.findById(id.getIdentifier().getValue())
        .map(processDefinitionMapper::toDomain);
  }
  @Transactional(readOnly = true)
  @Override
  public Optional<ProcessDefinition> findByKey(String key) {
    if (key == null || key.isBlank()) return Optional.empty();
    return processDefinitionEntityRepository.findByProcessKey(key)
        .map(processDefinitionMapper::toDomain);
  }

  @Override
  public boolean existsByKey(String key) {
    return processDefinitionEntityRepository.existsByProcessKey(key);
  }

  @Transactional(readOnly = true)
  @Override
  public List<ProcessDefinition> findAll() {
    return processDefinitionEntityRepository.findAll().stream()
        .map(processDefinitionMapper::toDomain)
        .collect(Collectors.toList());
  }
  @Transactional(readOnly = true)
  @Override
  public List<ProcessDefinition> findByProjectId(ProjectId projectId) {
    if (projectId == null) return List.of();
    return processDefinitionEntityRepository.findByProjectId_Id(projectId.getIdentifier().getValue())
        .stream()
        .map(processDefinitionMapper::toDomain)
        .collect(Collectors.toList());
  }

  @Transactional
  @Override
  public ProcessDefinition save(ProcessDefinition processDefinition) {
    if (processDefinition == null) throw new IllegalArgumentException("processDefinition cannot be null");
    var entity = processDefinitionMapper.toEntity(processDefinition);
    var savedEntity = processDefinitionEntityRepository.save(entity);
    return processDefinitionMapper.toDomain(savedEntity);
  }

  @Override
  public void delete(ProcessDefinitionId id) {
    // todo: Implement delete later
  }

  @Override
  public Optional<Integer> findLatestPublishedVersionByProcessKey(String processKey, ProcessDefinitionState state) {
    return processDefinitionEntityRepository.findLatestPublishedVersionByProcessKey(processKey, state);
  }
}
