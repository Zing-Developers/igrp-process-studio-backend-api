package cv.igrp.platform.process_manager_studio.project.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetProcessDefinitionQuery implements Query {

  @NotBlank(message = "The field <appCode> is required")
  private String appCode;
  @NotBlank(message = "The field <processKey> is required")
  private String processKey;
  @NotBlank(message = "The field <processName> is required")
  private String processName;
  @NotBlank(message = "The field <projectCode> is required")
  private String projectCode;
  @NotBlank(message = "The field <projectName> is required")
  private String projectName;
  @NotBlank(message = "The field <state> is required")
  private String state;
  @NotBlank(message = "The field <pageNumber> is required")
  private String pageNumber;
  @NotBlank(message = "The field <pageSize> is required")
  private String pageSize;

}
