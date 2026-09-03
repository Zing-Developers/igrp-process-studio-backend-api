/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.igrp.platform.process_manager_studio.project.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class ProcessDefinitionResponseLightDTO implements cv.igrp.platform.process_manager_studio.shared.security.AuditedResponse {



  private String processDefinitionId ;


  private String projectId ;

  // Owning project's labels; null when this definition is nested inside its project's response
  private ProjectSummaryDTO project ;


  private String processKey ;


  private String bpmnDiagramUrl ;


  private String title ;


  private String description ;


  private Integer version ;


  private String status ;


  private String statusDesc ;


  private String deploymentId ;


  private String deploymentDate ;

  private String createdBy ;

  private String lastModifiedBy ;

  private java.time.LocalDateTime createdAt ;

  private java.time.LocalDateTime updatedAt ;

  private cv.igrp.platform.process_manager_studio.shared.security.m2m.UserProfileDTO userProfileCreatedBy ;

  private cv.igrp.platform.process_manager_studio.shared.security.m2m.UserProfileDTO userProfileLastModifiedBy ;

}
