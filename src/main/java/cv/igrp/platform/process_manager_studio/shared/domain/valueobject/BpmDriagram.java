package cv.igrp.platform.process_manager_studio.shared.domain.valueobject;

import lombok.Getter;

import java.util.Objects;

@Getter
public class BpmDriagram {

  private final String content;

  private BpmDriagram(String content) {
    if (content == null || content.isBlank()) {
      throw new IllegalArgumentException("BPMN content must not be null or empty.");
    }
    this.content = content;
  }

  public static BpmDriagram of(String content) {
    return new BpmDriagram(content);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof BpmDriagram)) return false;
    BpmDriagram that = (BpmDriagram) o;
    return Objects.equals(content, that.content);
  }

  @Override
  public int hashCode() {
    return Objects.hash(content);
  }

  @Override
  public String toString() {
    return content;
  }


}
