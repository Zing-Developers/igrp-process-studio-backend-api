package cv.igrp.platform.process_manager_studio.project.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetProcessDefinitionByIdQuery implements Query {

  @NotBlank(message = "The field <processId> is required.")
  private String processId;

}