package cv.igrp.platform.process_manager_studio.shared.security.m2m;

import java.util.UUID;

/** Enriched audit user — same shape as the management API's UserProfileDTO, for UI consistency. */
public record UserProfileDTO(UUID id, String username, String email, String firstName,
                             String lastName, String fullName, String sub) {
}
