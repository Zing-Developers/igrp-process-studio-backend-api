/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.igrp.platform.process_manager_studio.project.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.igrp.platform.process_manager_studio.shared.application.constants.ProcessDefinitionState;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class ProcessDefinitionRequestDTO {

  
  
  private String title ;
  
  
  private String code ;
  
  
  private String description ;
  
  
  private ProcessDefinitionState Status ;
  
  
  private String projectId ;

}