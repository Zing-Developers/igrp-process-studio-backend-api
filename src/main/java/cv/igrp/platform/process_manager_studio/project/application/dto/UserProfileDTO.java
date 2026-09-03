package cv.igrp.platform.process_manager_studio.project.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/** Enriched audit user — same shape as the management API's UserProfileDTO. Modelled in .igrpstudio/project/dto. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class UserProfileDTO {

  private UUID id;

  private String username;

  private String email;

  private String firstName;

  private String lastName;

  private String fullName;

  private String sub;

}
