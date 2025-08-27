package cv.igrp.platform.process_manager_studio.project.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.igrp.platform.process_manager_studio.project.application.dto.ProcessVariableRequestDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddVariablesToProcessCommand implements Command {

  
  private ProcessVariableRequestDTO processvariablerequest;
  @NotBlank(message = "The field <processId> is required")
  private String processId;

}