package cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.repository;

import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.entity.ArtifactVariableEntity;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface ArtifactVariableEntityRepository extends
    JpaRepository<ArtifactVariableEntity, UUID>,
    JpaSpecificationExecutor<ArtifactVariableEntity>
{

}