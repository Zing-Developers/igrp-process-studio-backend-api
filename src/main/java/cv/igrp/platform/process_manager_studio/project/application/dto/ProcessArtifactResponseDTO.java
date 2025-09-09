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
public class ProcessArtifactResponseDTO  {

  
  
  private String projectArtifactId ;
  
  
  private String taskKey ;
  
  
  private String name ;
  
  
  private boolean isSubProcessTask ;
  
  
  private String subProcessId ;
  
  
  private String subProcessName ;
  
  @Valid
  private List<ArtifactVariableResponseDTO> artifactVariables = new ArrayList<>();

}