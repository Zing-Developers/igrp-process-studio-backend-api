package cv.igrp.platform.process_manager_studio.shared.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * IAM user profile, synced from JWT claims by {@code IAMUserProfileSyncFilter}. Lean port of the
 * management API's profile store so audit users (e.g. {@code userProfileCreatedBy} on /m2m-keys)
 * come back enriched here too.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "t_iam_user_profile",
    indexes = @Index(name = "idx_iam_user_profile_sub", columnList = "sub"))
public class IAMUserProfileEntity {

  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "username", unique = true, nullable = false)
  private String username;

  @Column(name = "email", unique = true)
  private String email;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "full_name")
  private String fullName;

  @Column(name = "sub", unique = true, nullable = false)
  private String sub;

}
