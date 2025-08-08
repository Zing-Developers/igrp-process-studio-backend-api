package cv.igrp.platform.process_manager_studio.project.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetProcessHistoryByProjectIdQuery implements Query {

  @NotBlank(message = "The field <processName> is required")
  private String processName;
  @NotBlank(message = "The field <processKey> is required")
  private String processKey;
  @NotBlank(message = "The field <pageSize> is required")
  private String pageSize;
  @NotBlank(message = "The field <pageNumber> is required")
  private String pageNumber;
  @NotBlank(message = "The field <projectId> is required")
  private String projectId;

}