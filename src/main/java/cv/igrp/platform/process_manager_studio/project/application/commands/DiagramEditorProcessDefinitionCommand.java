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
public class DiagramEditorProcessDefinitionCommand implements Command {

  
  private BpmDiagramDTO bpmdiagram;
  @NotBlank(message = "The field <processId> is required.")
  private String processId;

}