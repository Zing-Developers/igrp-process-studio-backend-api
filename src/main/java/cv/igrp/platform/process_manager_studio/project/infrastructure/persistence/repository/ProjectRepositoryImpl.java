package cv.igrp.platform.process_manager_studio.project.infrastructure.persistence.repository;

import cv.igrp.platform.process_manager_studio.project.domain.models.Project;
import cv.igrp.platform.process_manager_studio.project.domain.repository.ProjectRepository;
import cv.igrp.platform.process_manager_studio.project.infrastructure.mappers.ProjectMapper;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProjectId;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.repository.ProjectEntityRepository;
import lombok.RequiredArgsConstructor;
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
}
