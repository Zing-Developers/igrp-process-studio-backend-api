package cv.igrp.platform.process_manager_studio.project.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestoreProcessDefinitionCommand implements Command {

  @NotBlank(message = "The field <processId> is required.")
  private String processId;

}