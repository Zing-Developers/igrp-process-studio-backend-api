package cv.igrp.platform.process_manager_studio.project.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.igrp.platform.process_manager_studio.project.application.dto.ProcessDefinitionRequestDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProcessDefinitionCommand implements Command {


  private ProcessDefinitionRequestDTO processdefinitionrequest;
  @NotBlank(message = "The field <processId> is required")
  private String processId;

}
