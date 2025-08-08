package cv.igrp.platform.process_manager_studio.project.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.igrp.platform.process_manager_studio.project.application.dto.BpmDiagramDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeployProcessDefinitionCommand implements Command {

  
  private BpmDiagramDTO bpmdiagram;
  @NotBlank(message = "The field <processKey> is required")
  private String processKey;

}