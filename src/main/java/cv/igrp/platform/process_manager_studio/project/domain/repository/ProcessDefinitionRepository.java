package cv.igrp.platform.process_manager_studio.project.domain.repository;

import cv.igrp.platform.process_manager_studio.project.domain.filter.ProcessDefinitionFilter;
import cv.igrp.platform.process_manager_studio.project.domain.models.ProcessDefinition;
import cv.igrp.platform.process_manager_studio.shared.application.constants.ProcessDefinitionState;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProcessDefinitionId;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProjectId;

import java.util.List;
import java.util.Optional;

public interface ProcessDefinitionRepository {

  Optional<ProcessDefinition> findById(ProcessDefinitionId id);

  Optional<ProcessDefinition> findByKey(String key);

  boolean existsByKey(String key);

  Optional<ProcessDefinition> findDraftByProcessKey(String processKey);

  Optional<ProcessDefinition> findLastestByProcessKey(String processKey);

  List<ProcessDefinition> findAll();

  List<ProcessDefinition> findAll(ProcessDefinitionFilter processDefinitionFilter );

  List<ProcessDefinition> findByProjectId(ProjectId projectId);

  void unsetLatestForOtherVersions(String processKey, ProcessDefinitionId excludeId);

  ProcessDefinition save(ProcessDefinition processDefinition);

  void delete(ProcessDefinitionId id);

  Optional<Integer> findLatestPublishedVersionByProcessKey(String processKey, ProcessDefinitionState state);
}
