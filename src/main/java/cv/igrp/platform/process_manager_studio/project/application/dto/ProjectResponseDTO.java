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
// Audit user follows the platform pattern: raw principal string + enriched IAM profile (nullable).
// NOTE for iGRP Studio model owners: mirror these four fields in the generator model so a
// regeneration does not drop them.
public class ProjectResponseDTO  {



  private String projectId ;


  private String code ;


  private String name ;


  private String description ;


  private boolean active ;


  private String appCode ;

  private String createdBy ;

  private String lastModifiedBy ;

  private cv.igrp.platform.process_manager_studio.shared.security.m2m.UserProfileDTO userProfileCreatedBy ;

  private cv.igrp.platform.process_manager_studio.shared.security.m2m.UserProfileDTO userProfileLastModifiedBy ;

  @Valid
  private List<ProcessDefinitionResponseDTO> processDefinitions = new ArrayList<>();

}
