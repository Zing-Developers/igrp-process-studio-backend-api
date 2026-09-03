package cv.igrp.platform.process_manager_studio.shared.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/** POST /m2m-keys body (SPEC_M2M_AUTHORIZATION.md §6.2, management API repo). Modelled in .igrpstudio/shared/dto. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class M2mKeyRequestDTO {

  private String clientName;

  private List<String> permissions;

  private String email;

  // zone-less LocalDateTime, the datetime shape of every other endpoint
  private LocalDateTime expiresAt;

}
