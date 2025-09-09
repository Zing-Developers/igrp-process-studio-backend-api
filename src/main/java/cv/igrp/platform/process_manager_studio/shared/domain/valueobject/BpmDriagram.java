package cv.igrp.platform.process_manager_studio.shared.domain.valueobject;

public record BpmDriagram(String content) {

  public BpmDriagram {
    if (content == null || content.isBlank()) {
      throw new IllegalArgumentException("BPMN content must not be null or empty.");
    }
  }

  public static BpmDriagram of(String content) {
    return new BpmDriagram(content);
  }
}
