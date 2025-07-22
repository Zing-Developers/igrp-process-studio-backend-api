package cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.repository;

import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.entity.ProjectEntity;
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
}
