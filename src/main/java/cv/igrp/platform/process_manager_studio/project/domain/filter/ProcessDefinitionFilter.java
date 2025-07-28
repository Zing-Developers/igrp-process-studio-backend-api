package cv.igrp.platform.process_manager_studio.project.domain.filter;

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
  private Integer pageNumber;
  private Integer pageSize;
}
