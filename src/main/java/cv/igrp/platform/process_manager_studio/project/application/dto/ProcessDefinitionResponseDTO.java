/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.igrp.platform.process_manager_studio.project.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.igrp.platform.process_manager_studio.project.application.dto.ProcessArtifactResponseDTO;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
// Audit user follows the platform pattern: raw principal string + enriched IAM profile (nullable).
// NOTE for iGRP Studio model owners: mirror these four fields in the generator model so a
// regeneration does not drop them.
public class ProcessDefinitionResponseDTO  {



  private String processDefinitionId ;


  private String projectId ;


  private String processKey ;


  private String bpmnDiagramUrl ;


  private String bpmFileContent ;


  private String title ;


  private String description ;


  private Integer version ;


  private String status ;


  private String statusDesc ;


  private String deploymentId ;

  private String createdBy ;

  private String lastModifiedBy ;

  private cv.igrp.platform.process_manager_studio.shared.security.m2m.UserProfileDTO userProfileCreatedBy ;

  private cv.igrp.platform.process_manager_studio.shared.security.m2m.UserProfileDTO userProfileLastModifiedBy ;


  private String deploymentDate ;

  @Valid
  private List<ProcessArtifactResponseDTO> processArtifacts = new ArrayList<>();

}
