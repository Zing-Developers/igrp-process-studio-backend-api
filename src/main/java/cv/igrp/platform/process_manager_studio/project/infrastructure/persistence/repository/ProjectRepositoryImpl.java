package cv.igrp.platform.process_manager_studio.project.infrastructure.persistence.repository;

import cv.igrp.platform.process_manager_studio.project.domain.filter.ProjectFilter;
import cv.igrp.platform.process_manager_studio.project.domain.models.Project;
import cv.igrp.platform.process_manager_studio.project.domain.repository.ProjectRepository;
import cv.igrp.platform.process_manager_studio.project.infrastructure.mappers.ProjectMapper;
import cv.igrp.platform.process_manager_studio.shared.application.constants.ProcessDefinitionState;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProjectId;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.entity.ProjectEntity;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.repository.ProjectEntityRepository;
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
public class ProjectRepositoryImpl implements ProjectRepository {

  private final ProjectEntityRepository projectEntityRepository;
  private final ProjectMapper projectMapper;

  @Transactional(readOnly = true)
  @Override
  public Optional<Project> findById(ProjectId id) {
    if (id == null) return Optional.empty();
    return projectEntityRepository.findById(id.getIdentifier().getValue())
        .map(projectMapper::toDomain);
  }

  @Override
  public Optional<Project> findById(ProjectId id, ProjectFilter filter) {
    return Optional.empty();
  }

  @Transactional(readOnly = true)
  @Override
  public Optional<Project> findByIdWithLatestDeployedProcess(ProjectId id) {
    if (id == null) return Optional.empty();
    return projectEntityRepository.findByIdWithLatestPublishedProcessDefinitions(id.getIdentifier().getValue(), ProcessDefinitionState.PUBLISHED)
        .map(projectMapper::toDomain);
  }

  @Transactional(readOnly = true)
  @Override
  public Optional<Project> findByIdWithAllProcessAndLatestDeployedProcess(ProjectId id) {
    if (id == null) return Optional.empty();
    return projectEntityRepository.findByIdExcludingByStateAndNotLatest(id.getIdentifier().getValue(), ProcessDefinitionState.PUBLISHED)
        .map(projectMapper::toDomain);
  }

  @Override
  public boolean existsById(ProjectId id) {
    return id != null && projectEntityRepository.existsById(id.getIdentifier().getValue());
  }

  @Transactional(readOnly = true)
  @Override
  public Optional<Project> findByCode(String code) {
    if (code == null || code.isBlank()) return Optional.empty();
    return projectEntityRepository.findByCode(code)
        .map(projectMapper::toDomain);
  }

  @Transactional(readOnly = true)
  @Override
  public List<Project> findAll() {
    return projectEntityRepository.findAll().stream()
        .map(projectMapper::toDomain)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  @Override
  public List<Project> findAll(ProjectFilter filter) {

    var pageable = PageRequest.of(
        filter.getPageNumber() != null ? filter.getPageNumber() : 0,
        filter.getPageSize() != null ? filter.getPageSize() : 20
    );

    Specification<ProjectEntity> spec = (root, query, cb) -> {

      var predicates = cb.conjunction();

      if (filter.getCode() != null && !filter.getCode().isBlank()) {
        predicates = cb.and(predicates,
            cb.equal(root.get("code"), filter.getCode().trim()));
      }

      if (filter.getName() != null && !filter.getName().isBlank()) {
        predicates = cb.and(predicates,
            cb.like(cb.lower(root.get("name")), "%" + filter.getName().trim() + "%"));
      }

      if (filter.getDescription() != null && !filter.getDescription().isBlank()) {
        predicates = cb.and(predicates,
            cb.like(cb.lower(root.get("description")), "%" + filter.getDescription().trim() + "%"));
      }

      if (filter.getAppCode() != null && !filter.getAppCode().isBlank()) {
        predicates = cb.and(predicates,
            cb.equal(cb.lower(root.get("appCode")), filter.getAppCode()));
      }

      cb.and(predicates, cb.notEqual(root.get("state"), ProcessDefinitionState.DELETED));

      return cb.and(predicates, cb.isTrue(root.get("active")));
    };

    var page = projectEntityRepository.findAll(spec, pageable);

    return page.stream()
        .map(projectMapper::toDomain)
        .toList();
  }



  @Transactional()
  @Override
  public Project save(Project project) {
    if (project == null) throw new IllegalArgumentException("project cannot be null");
    var entity = projectMapper.toEntity(project);
    var savedEntity = projectEntityRepository.save(entity);
    return projectMapper.toDomain(savedEntity);
  }

  @Override
  public void delete(ProjectId id) {
   // todo implement later
  }

  @Override
  public String getApplicationBaseByProjectId(ProjectId projectId) {
    return projectEntityRepository.findAppCodeById(projectId.getIdentifier().getValue());
  }
}
