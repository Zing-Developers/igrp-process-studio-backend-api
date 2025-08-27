package cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.repository;

import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.entity.ProcessArtifactEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface ProcessArtifactEntityRepository extends
    JpaRepository<ProcessArtifactEntity, UUID>,
    JpaSpecificationExecutor<ProcessArtifactEntity>
{

}
