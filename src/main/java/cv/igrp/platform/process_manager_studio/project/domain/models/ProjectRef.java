package cv.igrp.platform.process_manager_studio.project.domain.models;

/** The owning project as seen from a process definition — identity and labels only, no children. */
public record ProjectRef(String id, String code, String name, String appCode, boolean active) { }
