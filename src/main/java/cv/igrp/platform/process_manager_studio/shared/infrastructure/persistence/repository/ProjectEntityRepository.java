package cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.repository;

import cv.igrp.platform.process_manager_studio.shared.application.constants.ProcessDefinitionState;
import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.entity.ProjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.history.RevisionRepository;

@Repository
public interface ProjectEntityRepository extends
    JpaRepository<ProjectEntity, UUID>,
    JpaSpecificationExecutor<ProjectEntity>,
    RevisionRepository<ProjectEntity, UUID, Integer>
{

  Optional<ProjectEntity> findByCode(String code);

  boolean existsById(UUID id);

  @Query("SELECT p.appCode FROM ProjectEntity p WHERE p.id = :id")
  String findAppCodeById(@Param("id") UUID id);


  @Query("""
    SELECT p FROM ProjectEntity p
    LEFT JOIN FETCH p.processdefinitions pd
    WHERE p.id = :projectId
      AND pd.isLatest = true
      AND pd.state = :state
""")
  Optional<ProjectEntity> findByIdWithLatestPublishedProcessDefinitions(
      @Param("projectId") UUID projectId,
      @Param("state") ProcessDefinitionState state
  );

  @Query("""
    SELECT DISTINCT p FROM ProjectEntity p
    LEFT JOIN FETCH p.processdefinitions pd
    WHERE p.id = :projectId
      AND (pd IS NULL OR NOT (pd.state = :state AND pd.isLatest = false))
""")
  Optional<ProjectEntity> findByIdExcludingByStateAndNotLatest(
      @Param("projectId") UUID projectId,
      @Param("state") ProcessDefinitionState state
  );





}
