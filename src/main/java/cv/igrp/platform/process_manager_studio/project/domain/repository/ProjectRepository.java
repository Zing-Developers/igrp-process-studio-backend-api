package cv.igrp.platform.process_manager_studio.project.domain.repository;

import cv.igrp.platform.process_manager_studio.project.domain.models.Project;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProjectId;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository {

  Optional<Project> findById(ProjectId id);

  Optional<Project> findByCode(String code);

  List<Project> findAll();

  Project save(Project project);

  void delete(ProjectId id);
}
