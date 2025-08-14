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
public class ProcessDefinitionResponseLightDTO {



  private String processDefinitionId ;


  private String projectId ;


  private String processKey ;


  private String bpmnDiagramUrl ;


  private String title ;


  private String descripiton ;


  private Integer version ;


  private String status ;


  private String statusDesc ;


  private String deploymentId ;


  private String deploymentDate ;

}
