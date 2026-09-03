/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.igrp.platform.process_manager_studio.project.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.igrp.platform.process_manager_studio.project.application.dto.ArtifactVariableResponseDTO;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
// Audit trio (time, raw principal, enriched profile) follows the platform pattern. NOTE for iGRP
// Studio model owners: mirror these six fields in the generator model so a regeneration keeps them.
public class ProcessArtifactResponseDTO implements cv.igrp.platform.process_manager_studio.shared.security.AuditedResponse {

  private String createdBy ;

  private String lastModifiedBy ;

  private java.time.LocalDateTime createdAt ;

  private java.time.LocalDateTime updatedAt ;

  private cv.igrp.platform.process_manager_studio.project.application.dto.UserProfileDTO userProfileCreatedBy ;

  private cv.igrp.platform.process_manager_studio.project.application.dto.UserProfileDTO userProfileLastModifiedBy ;

  
  
  private String projectArtifactId ;
  
  
  private String taskKey ;
  
  
  private String name ;

  
  private String formKey ;
  
  
  private boolean isSubProcessTask ;
  
  
  private String subProcessId ;
  
  
  private String subProcessName ;
  
  @Valid
  private List<ArtifactVariableResponseDTO> artifactVariables = new ArrayList<>();

}
