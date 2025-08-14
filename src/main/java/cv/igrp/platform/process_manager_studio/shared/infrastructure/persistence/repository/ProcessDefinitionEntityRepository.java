package cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.repository;

import cv.igrp.platform.process_manager_studio.shared.application.constants.ProcessDefinitionState;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.entity.ProcessDefinitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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

  boolean existsByProcessKey(String processKey);

  @Modifying
  @Query("""
  UPDATE ProcessDefinitionEntity p
  SET p.isLatest = false
  WHERE p.processKey = :processKey
    AND p.id != :excludeId
    AND p.isLatest = true
""")
  void unsetLatestForOtherVersions(
      @Param("processKey") String processKey,
      @Param("excludeId") UUID excludeId
  );


  @Query("SELECT MAX(pd.version) FROM ProcessDefinitionEntity pd WHERE pd.processKey = :processKey")
  Optional<Integer> findLatestVersionByProcessKey(String processKey);

  /*@Query("SELECT MAX(pd.version) FROM ProcessDefinitionEntity pd " +
      "WHERE pd.processKey = :processKey AND pd.state = :state")
  Optional<Integer> findLatestPublishedVersionByProcessKey(@Param("processKey") String processKey, @Param("state") ProcessDefinitionState state);*/

  @Query("SELECT MAX(pd.version) FROM ProcessDefinitionEntity pd " +
      "WHERE pd.processKey = :processKey AND pd.state = :state and pd.isLatest=true")
  Optional<Integer> findLatestPublishedVersionByProcessKey(@Param("processKey") String processKey, @Param("state") ProcessDefinitionState state);


  @Query("SELECT pd FROM ProcessDefinitionEntity pd " +
      "WHERE pd.processKey = :processKey AND pd.state = :state and pd.isLatest=true")
  Optional<ProcessDefinitionEntity> findLatestPublishedByProcessKey(@Param("processKey") String processKey, @Param("state") ProcessDefinitionState state);

  @Query("""
        SELECT p FROM ProcessDefinitionEntity p
        WHERE p.processKey = :processKey
          AND p.state = :state
        ORDER BY p.createdDate DESC
    """)
  Optional<ProcessDefinitionEntity> findDraftByProcessKey(@Param("processKey") String processKey, @Param("state") ProcessDefinitionState state);


}
