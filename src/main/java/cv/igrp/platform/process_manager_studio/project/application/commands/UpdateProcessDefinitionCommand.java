package cv.igrp.platform.process_manager_studio.project.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.igrp.platform.process_manager_studio.project.application.dto.ProcessDefinitionRequestDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProcessDefinitionCommand implements Command {

  
  private ProcessDefinitionRequestDTO processdefinitionrequest;
  @NotBlank(message = "The field <processId> is required.")
  private String processId;

}