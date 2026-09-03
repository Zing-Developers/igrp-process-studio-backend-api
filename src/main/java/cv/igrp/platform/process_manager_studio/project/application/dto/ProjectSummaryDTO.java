package cv.igrp.platform.process_manager_studio.project.application.dto;

/** Owning project of a process definition, without its process definitions (no cycle). */
public record ProjectSummaryDTO(String projectId, String code, String name, String appCode, boolean active) { }
