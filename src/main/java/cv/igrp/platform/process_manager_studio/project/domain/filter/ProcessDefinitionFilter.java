package cv.igrp.platform.process_manager_studio.project.domain.filter;

import cv.igrp.platform.process_manager_studio.shared.application.constants.ProcessDefinitionState;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProcessDefinitionFilter {

  private String processKey;
  private String processName;
  private String appCode;
  private String projectCode;
  private String projectName;
  private ProcessDefinitionState state;
  private Integer pageNumber;
  private Integer pageSize;
}
