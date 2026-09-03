/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.igrp.platform.process_manager_studio.project.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class ProjectResponseLigthDTO implements cv.igrp.platform.process_manager_studio.shared.security.AuditedResponse {



  private String projectId ;


  private String code ;


  private String name ;


  private String description ;


  private boolean active ;


  private String appCode ;

  private String createdBy ;

  private String lastModifiedBy ;

  private java.time.LocalDateTime createdAt ;

  private java.time.LocalDateTime updatedAt ;

  private cv.igrp.platform.process_manager_studio.shared.security.m2m.UserProfileDTO userProfileCreatedBy ;

  private cv.igrp.platform.process_manager_studio.shared.security.m2m.UserProfileDTO userProfileLastModifiedBy ;

  @Valid
  private List<ProcessDefinitionResponseLightDTO> processDefinitions = new ArrayList<>();

}
