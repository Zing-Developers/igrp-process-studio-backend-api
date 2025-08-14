package cv.igrp.platform.process_manager_studio.project.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.igrp.platform.process_manager_studio.project.application.dto.BpmDiagramDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiagramEditorProcessDefinitionCommand implements Command {


  private BpmDiagramDTO bpmdiagram;
  @NotBlank(message = "The field <processKey> is required")
  private String processKey;

}
