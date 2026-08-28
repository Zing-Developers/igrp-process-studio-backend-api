-- IAM user profiles, synced from JWT claims on each authenticated request (IAMUserProfileSyncFilter).
-- Brings the Studio in line with the management API so audit users (e.g. userProfileCreatedBy on
-- /m2m-keys) come back as enriched objects instead of raw principal strings.
CREATE TABLE IF NOT EXISTS t_iam_user_profile (
  id UUID NOT NULL,
  username VARCHAR(255) NOT NULL,
  email VARCHAR(255),
  first_name VARCHAR(255),
  last_name VARCHAR(255),
  full_name VARCHAR(255),
  sub VARCHAR(255) NOT NULL,
  CONSTRAINT pk_t_iam_user_profile PRIMARY KEY (id),
  CONSTRAINT uk_t_iam_user_profile_username UNIQUE (username),
  CONSTRAINT uk_t_iam_user_profile_email UNIQUE (email),
  CONSTRAINT uk_t_iam_user_profile_sub UNIQUE (sub)
);
CREATE INDEX IF NOT EXISTS idx_iam_user_profile_sub ON t_iam_user_profile (sub);
