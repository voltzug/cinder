package com.voltzug.cinder.core.domain;

import static org.junit.jupiter.api.Assertions.*;

import com.voltzug.cinder.core.common.valueobject.Id;
import com.voltzug.cinder.core.domain.valueobject.id.FileId;
import com.voltzug.cinder.core.domain.valueobject.id.IdPrefix;
import com.voltzug.cinder.core.domain.valueobject.id.LinkId;
import com.voltzug.cinder.core.domain.valueobject.id.SessionId;
import com.voltzug.cinder.core.domain.valueobject.id.UserId;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for IdManager.
 */
class IdManagerTest {

  @Test
  void shouldParseLinkIdWithPrefix() {
    // Given
    String value = "LKtest-link-id";

    // When
    Id id = IdManager.from(value);

    // Then
    assertNotNull(id);
    assertTrue(id instanceof LinkId);
    assertEquals("test-link-id", id.value());
    assertEquals(IdPrefix.LINK, id.prefix());
    assertEquals("LKtest-link-id", id.toString());
  }

  @Test
  void shouldParseLinkIdWithUuidValue() {
    // Given
    String uuid = "550e8400-e29b-41d4-a716-446655440000";
    String value = "LK" + uuid;

    // When
    Id id = IdManager.from(value);

    // Then
    assertNotNull(id);
    assertTrue(id instanceof LinkId);
    assertEquals(uuid, id.value());
    assertEquals(IdPrefix.LINK, id.prefix());
    assertEquals("LK" + uuid, id.toString());
  }

  @Test
  void shouldParseFileId() {
    // Given
    String value = "FLfile-123";
    // When
    Id id = IdManager.from(value);
    // Then
    assertNotNull(id);
    assertTrue(id instanceof FileId);
    assertEquals("file-123", id.value());
    assertEquals(IdPrefix.FILE, id.prefix());
    assertEquals("FLfile-123", id.toString());
  }

  @Test
  void shouldParseUserId() {
    // Given
    String value = "USuser-abc";
    // When
    Id id = IdManager.from(value);
    // Then
    assertNotNull(id);
    assertTrue(id instanceof UserId);
    assertEquals("user-abc", id.value());
    assertEquals(IdPrefix.USER, id.prefix());
    assertEquals("USuser-abc", id.toString());
  }

  @Test
  void shouldParseSessionId() {
    // Given
    String value = "SNsession-xyz";
    // When
    Id id = IdManager.from(value);
    // Then
    assertNotNull(id);
    assertTrue(id instanceof SessionId);
    assertEquals("session-xyz", id.value());
    assertEquals(IdPrefix.SESSION, id.prefix());
    assertEquals("SNsession-xyz", id.toString());
  }

  @Test
  void shouldThrowForNullValue() {
    // When/Then
    IllegalArgumentException ex = assertThrows(
      IllegalArgumentException.class,
      () -> IdManager.from(null)
    );
    assertEquals("value is too short or null", ex.getMessage());
  }

  @Test
  void shouldThrowForTooShortValue() {
    // Given
    String value = "L"; // less than prefix length + 1

    // When/Then
    IllegalArgumentException ex = assertThrows(
      IllegalArgumentException.class,
      () -> IdManager.from(value)
    );
    assertEquals("value is too short or null", ex.getMessage());
  }

  @Test
  void shouldThrowForUnknownPrefix() {
    // Given
    String value = "XXsomeid";

    // When/Then
    IllegalArgumentException ex = assertThrows(
      IllegalArgumentException.class,
      () -> IdManager.from(value)
    );
    assertTrue(ex.getMessage().startsWith("Invalid Id prefix: XX"));
    assertNotNull(ex.getCause());
    assertEquals("Unknown Id prefix: XX", ex.getCause().getMessage());
  }

  @Test
  void shouldThrowForUnsupportedPrefix() {
    // Given
    String value = "ZZtest-unsupported-id"; // ZZ is not a valid prefix

    // When/Then
    IllegalArgumentException ex = assertThrows(
      IllegalArgumentException.class,
      () -> IdManager.from(value)
    );
    assertTrue(
      ex.getMessage().startsWith("Invalid Id prefix: ZZ") ||
        ex.getMessage().startsWith("Unsupported Id prefix: ZZ")
    );
  }

  @Test
  void shouldHandleLinkIdWithEmptyValue() {
    // Given
    String value = "LK";

    // When/Then
    IllegalArgumentException ex = assertThrows(
      IllegalArgumentException.class,
      () -> IdManager.from(value)
    );
    // Because value.length() == 2, which is not >= PREFIX_LENGTH + 1 (3)
    assertEquals("value is too short or null", ex.getMessage());
  }

  @Test
  void shouldHandleLinkIdWithWhitespaceValue() {
    // Given
    String value = "LK  test id  ";

    // When
    Id id = IdManager.from(value);

    // Then
    assertTrue(id instanceof LinkId);
    assertEquals("  test id  ", id.value());
  }

  @Test
  void shouldHandleLinkIdWithSpecialCharacters() {
    // Given
    String special = "id_with.special:chars";
    String value = "LK" + special;

    // When
    Id id = IdManager.from(value);

    // Then
    assertTrue(id instanceof LinkId);
    assertEquals(special, id.value());
  }
}
