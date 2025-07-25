package cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.repository;

import cv.igrp.platform.process_manager_studio.shared.application.constants.ProcessDefinitionState;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.entity.ProcessDefinitionEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface ProcessDefinitionEntityRepository extends
    JpaRepository<ProcessDefinitionEntity, UUID>,
    JpaSpecificationExecutor<ProcessDefinitionEntity>
{

  Optional<ProcessDefinitionEntity> findByProcessKey(String processKey);

  List<ProcessDefinitionEntity> findByProjectId_Id(UUID projectIdId);

  @Query("SELECT MAX(pd.version) FROM ProcessDefinitionEntity pd WHERE pd.processKey = :processKey")
  Optional<Integer> findLatestVersionByProcessKey(String processKey);

  @Query("SELECT MAX(pd.version) FROM ProcessDefinitionEntity pd " +
      "WHERE pd.processKey = :processKey AND pd.state = :state")
  Optional<Integer> findLatestPublishedVersionByProcessKey(@Param("processKey") String processKey, @Param("state") ProcessDefinitionState state);


}
