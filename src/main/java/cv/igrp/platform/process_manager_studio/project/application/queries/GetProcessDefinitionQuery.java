package cv.igrp.platform.process_manager_studio.project.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetProcessDefinitionQuery implements Query {

  @NotBlank(message = "The field <pageNumber> is required.")
  private String pageNumber;
  @NotBlank(message = "The field <pageSize> is required.")
  private String pageSize;

}