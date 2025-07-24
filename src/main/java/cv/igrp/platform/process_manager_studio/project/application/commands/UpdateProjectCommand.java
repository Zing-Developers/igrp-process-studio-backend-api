package cv.igrp.platform.process_manager_studio.project.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.igrp.platform.process_manager_studio.project.application.dto.ProjectRequestDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProjectCommand implements Command {

  
  private ProjectRequestDTO projectrequest;

}