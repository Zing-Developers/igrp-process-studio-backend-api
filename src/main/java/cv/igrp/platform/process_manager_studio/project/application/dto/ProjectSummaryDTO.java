package cv.igrp.platform.process_manager_studio.project.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Owning project of a process definition, without its process definitions (no cycle). */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class ProjectSummaryDTO {

  private String projectId;

  private String code;

  private String name;

  private String appCode;

  private boolean active;

}
