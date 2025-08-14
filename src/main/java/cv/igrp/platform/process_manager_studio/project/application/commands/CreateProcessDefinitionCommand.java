package cv.igrp.platform.process_manager_studio.project.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProcessDefinitionCommand implements Command {

  @NotNull(message = "The field <file> is required.")
  private MultipartFile file;
  @NotBlank(message = "The field <projectId> is required.")
  private String projectId;

}
