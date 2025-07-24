package cv.igrp.platform.process_manager_studio.project.domain.filter;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FilterProject {

  private String code;
  private String name;
  private String description;
  private Boolean active;
  private String appCode;
  private Integer pageNumber;
  private Integer pageSize;
}
