/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.igrp.platform.process_manager_studio.project.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import cv.igrp.platform.process_manager_studio.shared.application.constants.ProcessDefinitionState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class ProcessDefinitionRequestDTO {



  private String title ;


  private String processKey ;


  private String description ;


  private ProcessDefinitionState Status ;


  private String projectId ;

}
