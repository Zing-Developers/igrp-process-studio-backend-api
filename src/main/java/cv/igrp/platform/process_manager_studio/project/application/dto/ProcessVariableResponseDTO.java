/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.igrp.platform.process_manager_studio.project.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
// Audit trio (time, raw principal, enriched profile) follows the platform pattern. NOTE for iGRP
// Studio model owners: mirror these six fields in the generator model so a regeneration keeps them.
public class ProcessVariableResponseDTO implements cv.igrp.platform.process_manager_studio.shared.security.AuditedResponse {

  private String createdBy ;

  private String lastModifiedBy ;

  private java.time.LocalDateTime createdAt ;

  private java.time.LocalDateTime updatedAt ;

  private cv.igrp.platform.process_manager_studio.shared.security.m2m.UserProfileDTO userProfileCreatedBy ;

  private cv.igrp.platform.process_manager_studio.shared.security.m2m.UserProfileDTO userProfileLastModifiedBy ;

  
  
  private String id ;
  
  
  private String name ;
  
  
  private String type ;
  
  
  private String defaultValue ;
  
  
  private boolean required ;
  
  
  private String processDefinitionId ;

}