package cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.repository;

import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.entity.ProcessDefinitionEntity;
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
}
