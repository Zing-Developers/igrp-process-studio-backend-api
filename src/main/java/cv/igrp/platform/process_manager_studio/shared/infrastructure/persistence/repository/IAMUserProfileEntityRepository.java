package cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.repository;

import cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.entity.IAMUserProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface IAMUserProfileEntityRepository extends JpaRepository<IAMUserProfileEntity, UUID> {

  @Query("SELECT e FROM IAMUserProfileEntity e WHERE e.sub = :sub OR e.email = :email")
  Optional<IAMUserProfileEntity> findBySubOrEmail(@Param("sub") String sub, @Param("email") String email);

  /** Batch audit-user enrichment: a stored principal may be a sub or an email. */
  @Query("SELECT e FROM IAMUserProfileEntity e WHERE e.sub IN :identifiers OR e.email IN :identifiers")
  List<IAMUserProfileEntity> findBySubInOrEmailIn(@Param("identifiers") Set<String> identifiers);

}
