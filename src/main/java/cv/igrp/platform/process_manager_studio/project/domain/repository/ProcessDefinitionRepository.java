package cv.igrp.platform.process_manager_studio.project.domain.repository;

import cv.igrp.platform.process_manager_studio.project.domain.models.ProcessDefinition;
import cv.igrp.platform.process_manager_studio.shared.application.constants.ProcessDefinitionState;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProcessDefinitionId;
import cv.igrp.platform.process_manager_studio.shared.domain.valueobject.ProjectId;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProcessDefinitionRepository {

  Optional<ProcessDefinition> findById(ProcessDefinitionId id);

  Optional<ProcessDefinition> findByKey(String key);

  List<ProcessDefinition> findAll();

  List<ProcessDefinition> findByProjectId(ProjectId projectId);

  ProcessDefinition save(ProcessDefinition processDefinition);

  void delete(ProcessDefinitionId id);

  Optional<Integer> findLatestPublishedVersionByProcessKey(@Param("processKey") String processKey, @Param("state") ProcessDefinitionState state);
}
