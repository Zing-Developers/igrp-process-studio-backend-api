package cv.igrp.platform.process_manager_studio.project.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisableProjectCommand implements Command {

  @NotBlank(message = "The field <projectId> is required")
  private String projectId;

}
