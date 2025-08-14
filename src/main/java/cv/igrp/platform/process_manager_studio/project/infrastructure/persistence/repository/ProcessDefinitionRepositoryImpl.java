package cv.igrp.platform.process_manager_studio.project.infrastructure.persistence.repository;

import cv.igrp.platform.process_manager_studio.project.domain.filter.ProcessDefinitionFilter;
import cv.igrp.platform.process_manager_studio.project.domain.models.ProcessDefinition;
import cv.igrp.platform.process_manager_studio.project.domain.repository.ProcessDefinitionRepository;
import cv.igrp.platform.process_manager_studio.project.infrastructure.mappers.ProcessDefinitionMapper;
import cv.igrp.platform.process_manager_studio.shared.application.constants.ProcessDefinitionState;
import cv.igrp.platform.process_manager_studio.shared.domain.exceptions.IgrpResponseStatusException;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProcessDefinitionId;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProjectId;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.entity.ProcessDefinitionEntity;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.repository.ProcessDefinitionEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
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
    return processDefinitionEntityRepository.findById(id.identifier().value())
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
  public Optional<ProcessDefinition> findDraftByProcessKey(String processKey) {
    return processDefinitionEntityRepository.findDraftByProcessKey(processKey, ProcessDefinitionState.DRAFT)
        .map(processDefinitionMapper::toDomain);
  }

  @Transactional(readOnly = true)
  @Override
  public Optional<ProcessDefinition> findLastestByProcessKey(String processKey) {
    return processDefinitionEntityRepository.findLatestPublishedByProcessKey(processKey, ProcessDefinitionState.PUBLISHED)
        .map(processDefinitionMapper::toDomain);
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
  public List<ProcessDefinition> findAll(ProcessDefinitionFilter filter) {
    var pageable = PageRequest.of(
        filter.getPageNumber() != null ? filter.getPageNumber() : 0,
        filter.getPageSize() != null ? filter.getPageSize() : 20
    );

    Specification<ProcessDefinitionEntity> spec = (root, query, cb) -> {
      var predicates = cb.conjunction();

      if (filter.getProcessKey() != null && !filter.getProcessKey().isBlank()) {
        predicates = cb.and(predicates,
            cb.equal(cb.lower(root.get("processKey")), filter.getProcessKey().trim()));
      }

      if (filter.getProcessName() != null && !filter.getProcessName().isBlank()) {
        predicates = cb.and(predicates,
            cb.like(cb.lower(root.get("title")), "%" + filter.getProcessName().trim() + "%"));
      }

      if (filter.getAppCode() != null && !filter.getAppCode().isBlank()) {
        predicates = cb.and(predicates,
            cb.equal(cb.lower(root.get("projectId").get("appCode")), filter.getAppCode()));
      }

      if (filter.getProjectCode() != null && !filter.getProjectCode().isBlank()) {
        predicates = cb.and(predicates,
            cb.equal(cb.lower(root.get("projectId").get("code")), filter.getProjectCode()));
      }

      if (filter.getProjectName() != null && !filter.getProjectName().isBlank()) {
        predicates = cb.and(predicates,
            cb.like(cb.lower(root.get("projectId").get("name")), "%" + filter.getProjectName().trim() + "%"));
      }

      if (filter.getState() != null) {
        predicates = cb.and(predicates,
            cb.equal(root.get("state"), filter.getState()));
      }


      predicates = cb.and(predicates,
          cb.notEqual(root.get("state"), ProcessDefinitionState.DELETED));


      predicates = cb.and(predicates, cb.or(
          cb.notEqual(root.get("state"), ProcessDefinitionState.PUBLISHED),
          cb.isTrue(root.get("isLatest"))
      ));

      return predicates;
    };

    var page = processDefinitionEntityRepository.findAll(spec, pageable);

    return page.stream()
        .map(processDefinitionMapper::toDomain)
        .toList();
  }


  @Transactional(readOnly = true)
  @Override
  public List<ProcessDefinition> findByProjectId(ProjectId projectId) {
    if (projectId == null) return List.of();
    return processDefinitionEntityRepository.findByProjectId_Id(projectId.identifier().value())
        .stream()
        .map(processDefinitionMapper::toDomain)
        .collect(Collectors.toList());
  }

  @Transactional
  @Override
  public void unsetLatestForOtherVersions(String processKey, ProcessDefinitionId excludeId) {
    processDefinitionEntityRepository.unsetLatestForOtherVersions(processKey, excludeId.identifier().value());
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

    var uuid = id.identifier().value();

    var processDefinition = processDefinitionEntityRepository.findById(uuid)
        .orElseThrow(() ->
            IgrpResponseStatusException.notFound("Process Definition not found with id: " + id));

    processDefinition.setState(ProcessDefinitionState.DELETED);

    processDefinitionEntityRepository.save(processDefinition);
  }

  @Override
  public Optional<Integer> findLatestPublishedVersionByProcessKey(String processKey, ProcessDefinitionState state) {
    return processDefinitionEntityRepository.findLatestPublishedVersionByProcessKey(processKey, state);
  }
}
