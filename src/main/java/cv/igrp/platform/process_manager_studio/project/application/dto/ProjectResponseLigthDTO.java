/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.igrp.platform.process_manager_studio.project.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class ProjectResponseLigthDTO  {



  private String projectId ;


  private String code ;


  private String name ;


  private String description ;


  private boolean active ;


  private String appCode ;

  @Valid
  private List<ProcessDefinitionResponseLightDTO> processDefinitions = new ArrayList<>();

}
