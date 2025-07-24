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
public class ProcessDefinitionResponseLightDTO {

  
  
  private String processDefinitionId ;
  
  
  private String projectId ;
  
  
  private String processKey ;
  
  
  private String code ;
  
  
  private String bpmnDiagramUrl ;
  
  
  private String title ;
  
  
  private String descripiton ;
  
  
  private Integer version ;
  
  
  private String status ;
  
  
  private String statusDesc ;
  
  
  private String deploymentId ;
  
  
  private String deploymentDate ;

}