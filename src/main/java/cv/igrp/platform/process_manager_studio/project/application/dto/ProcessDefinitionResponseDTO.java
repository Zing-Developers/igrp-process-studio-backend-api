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


  private String deploymentDate ;

  @Valid
  private List<ProcessArtifactResponseDTO> processArtifacts = new ArrayList<>();

}
