package cv.igrp.platform.process_manager_studio.shared.domain.valueobject;

import java.util.UUID;

/**
 * Value Object representing a unique identifier based on UUID.
 */
public class Identifier {

  private final UUID value;

  /**
   * Private constructor to enforce creation through factory methods.
   * @param value The UUID to encapsulate.
   */
  private Identifier(UUID value) {
    if (value == null) {
      throw new IllegalArgumentException("Identifier value cannot be null.");
    }
    this.value = value;
  }

  /**
   * Creates an Identifier instance from an existing UUID.
   * @param uuid The UUID.
   * @return A new Identifier instance.
   */
  public static Identifier from(UUID uuid) {
    return new Identifier(uuid);
  }

  /**
   * Creates an Identifier instance from a UUID string.
   * @param uuidString The UUID string.
   * @return A new Identifier instance.
   * @throws IllegalArgumentException if the string is not a valid UUID.
   */
  public static Identifier from(String uuidString) {
    if (uuidString == null || uuidString.trim().isEmpty()) {
      throw new IllegalArgumentException("UUID string cannot be null or empty.");
    }
    try {
      return new Identifier(UUID.fromString(uuidString));
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Provided string is not a valid UUID: " + uuidString, e);
    }
  }

  /**
   * Generates a new Identifier with a random UUID.
   * @return A new Identifier instance.
   */
  public static Identifier generateNew() {
    return new Identifier(UUID.randomUUID());
  }

  /**
   * Returns the encapsulated UUID.
   * @return The UUID.
   */
  public UUID getValue() {
    return value;
  }

  /**
   * Returns the string representation of the UUID.
   * @return The UUID string.
   */
  public String getValueAsString() {
    return value.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Identifier)) return false;
    Identifier that = (Identifier) o;
    return value.equals(that.value);
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }
}
