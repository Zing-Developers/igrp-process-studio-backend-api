package cv.igrp.platform.process_manager_studio.project.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.igrp.platform.process_manager_studio.project.application.dto.ProjectRequestDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProjectCommand implements Command {


  private ProjectRequestDTO projectrequest;
  @NotBlank(message = "The field <projectId> is required")
  private String projectId;

}
