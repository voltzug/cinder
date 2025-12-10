package com.voltzug.cinder.core.domain.valueobject;

import static org.junit.jupiter.api.Assertions.*;

import com.voltzug.cinder.core.common.valueobject.Id;
import com.voltzug.cinder.core.domain.valueobject.id.FileId;
import com.voltzug.cinder.core.domain.valueobject.id.IdPrefix;
import com.voltzug.cinder.core.domain.valueobject.id.LinkId;
import com.voltzug.cinder.core.domain.valueobject.id.SessionId;
import com.voltzug.cinder.core.domain.valueobject.id.UserId;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive tests for Id subclasses (SessionId, LinkId, FileId, UserId).
 * Tests identifier creation, validation, prefix handling, and generation.
 */
class IdTest {

  // ==================== SESSIONID CONSTRUCTION TESTS ====================

  @Test
  void shouldCreateSessionIdWithValidValue() {
    // Given
    String value = "test-session-id";

    // When
    SessionId sessionId = new SessionId(value);

    // Then
    assertNotNull(sessionId);
    assertEquals(value, sessionId.value());
  }

  @Test
  void shouldCreateSessionIdWithUuidValue() {
    // Given
    String uuid = "550e8400-e29b-41d4-a716-446655440000";

    // When
    SessionId sessionId = new SessionId(uuid);

    // Then
    assertEquals(uuid, sessionId.value());
  }

  @Test
  void shouldGenerateSessionIdWithRandomUuid() {
    // When
    SessionId sessionId = SessionId.generate();

    // Then
    assertNotNull(sessionId);
    assertNotNull(sessionId.value());
    assertFalse(sessionId.value().isEmpty());
  }

  @Test
  void shouldGenerateUniqueSessionIds() {
    // When
    SessionId sessionId1 = SessionId.generate();
    SessionId sessionId2 = SessionId.generate();

    // Then
    assertNotEquals(sessionId1.value(), sessionId2.value());
  }

  @Test
  void shouldHaveSessionPrefixForSessionId() {
    // Given
    SessionId sessionId = new SessionId("test");

    // When
    Id.Prefix prefix = sessionId.prefix();

    // Then
    assertEquals(IdPrefix.SESSION, prefix);
    assertEquals("SN", prefix.code());
  }

  // ==================== LINKID CONSTRUCTION TESTS ====================

  @Test
  void shouldCreateLinkIdWithValidValue() {
    // Given
    String value = "test-link-id";

    // When
    LinkId linkId = new LinkId(value);

    // Then
    assertNotNull(linkId);
    assertEquals(value, linkId.value());
  }

  @Test
  void shouldCreateLinkIdWithUuidValue() {
    // Given
    String uuid = "550e8400-e29b-41d4-a716-446655440000";

    // When
    LinkId linkId = new LinkId(uuid);

    // Then
    assertEquals(uuid, linkId.value());
  }

  @Test
  void shouldGenerateLinkIdWithRandomUuid() {
    // When
    LinkId linkId = LinkId.generate();

    // Then
    assertNotNull(linkId);
    assertNotNull(linkId.value());
    assertFalse(linkId.value().isEmpty());
  }

  @Test
  void shouldGenerateUniqueLinkIds() {
    // When
    LinkId linkId1 = LinkId.generate();
    LinkId linkId2 = LinkId.generate();

    // Then
    assertNotEquals(linkId1.value(), linkId2.value());
  }

  @Test
  void shouldHaveLinkPrefixForLinkId() {
    // Given
    LinkId linkId = new LinkId("test");

    // When
    Id.Prefix prefix = linkId.prefix();

    // Then
    assertEquals(IdPrefix.LINK, prefix);
    assertEquals("LK", prefix.code());
  }

  // ==================== FILEID CONSTRUCTION TESTS ====================

  @Test
  void shouldCreateFileIdWithValidValue() {
    // Given
    String value = "test-file-id";

    // When
    FileId fileId = new FileId(value);

    // Then
    assertNotNull(fileId);
    assertEquals(value, fileId.value());
  }

  @Test
  void shouldCreateFileIdWithUuidValue() {
    // Given
    String uuid = "550e8400-e29b-41d4-a716-446655440000";

    // When
    FileId fileId = new FileId(uuid);

    // Then
    assertEquals(uuid, fileId.value());
  }

  @Test
  void shouldHaveFilePrefixForFileId() {
    // Given
    FileId fileId = new FileId("test");

    // When
    Id.Prefix prefix = fileId.prefix();

    // Then
    assertEquals(IdPrefix.FILE, prefix);
    assertEquals("FL", prefix.code());
  }

  // ==================== USERID CONSTRUCTION TESTS ====================

  @Test
  void shouldCreateUserIdWithValidValue() {
    // Given
    String value = "test-user-id";

    // When
    UserId userId = new UserId(value);

    // Then
    assertNotNull(userId);
    assertEquals(value, userId.value());
  }

  @Test
  void shouldCreateUserIdWithUuidValue() {
    // Given
    String uuid = "550e8400-e29b-41d4-a716-446655440000";

    // When
    UserId userId = new UserId(uuid);

    // Then
    assertEquals(uuid, userId.value());
  }

  @Test
  void shouldHaveUserPrefixForUserId() {
    // Given
    UserId userId = new UserId("test");

    // When
    Id.Prefix prefix = userId.prefix();

    // Then
    assertEquals(IdPrefix.USER, prefix);
    assertEquals("US", prefix.code());
  }

  // ==================== VALIDATION TESTS ====================

  @Test
  void shouldThrowExceptionForNullSessionIdValue() {
    // When/Then
    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> new SessionId(null)
    );
    assertEquals("value and prefix must not be null", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionForNullLinkIdValue() {
    // When/Then
    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> new LinkId(null)
    );
    assertEquals("value and prefix must not be null", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionForNullFileIdValue() {
    // When/Then
    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> new FileId(null)
    );
    assertEquals("value and prefix must not be null", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionForNullUserIdValue() {
    // When/Then
    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> new UserId(null)
    );
    assertEquals("value and prefix must not be null", exception.getMessage());
  }

  // ==================== IDPREFIX TESTS ====================

  @Test
  void shouldHaveCorrectSessionPrefixCode() {
    // Then
    assertEquals("SN", IdPrefix.SESSION.code());
  }

  @Test
  void shouldHaveCorrectFilePrefixCode() {
    // Then
    assertEquals("FL", IdPrefix.FILE.code());
  }

  @Test
  void shouldHaveCorrectLinkPrefixCode() {
    // Then
    assertEquals("LK", IdPrefix.LINK.code());
  }

  @Test
  void shouldHaveCorrectUserPrefixCode() {
    // Then
    assertEquals("US", IdPrefix.USER.code());
  }

  @Test
  void shouldHavePrefixLengthOfTwo() {
    // Then
    assertEquals(2, IdPrefix.PREFIX_LENGTH);
  }

  @Test
  void shouldHaveFourPrefixValues() {
    // When
    IdPrefix[] prefixes = IdPrefix.values();

    // Then
    assertEquals(4, prefixes.length);
  }

  @Test
  void shouldRetrievePrefixFromCode() {
    // When/Then
    assertEquals(IdPrefix.SESSION, IdPrefix.fromCode("SN"));
    assertEquals(IdPrefix.FILE, IdPrefix.fromCode("FL"));
    assertEquals(IdPrefix.LINK, IdPrefix.fromCode("LK"));
    assertEquals(IdPrefix.USER, IdPrefix.fromCode("US"));
  }

  @Test
  void shouldThrowExceptionForUnknownPrefixCode() {
    // When/Then
    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> IdPrefix.fromCode("XX")
    );
    assertEquals("Unknown Id prefix: XX", exception.getMessage());
  }

  @Test
  void shouldReturnCodeFromToString() {
    // Then
    assertEquals("SN", IdPrefix.SESSION.toString());
    assertEquals("FL", IdPrefix.FILE.toString());
    assertEquals("LK", IdPrefix.LINK.toString());
    assertEquals("US", IdPrefix.USER.toString());
  }

  // ==================== TOSTRING TESTS ====================

  @Test
  void shouldFormatSessionIdToStringWithPrefix() {
    // Given
    SessionId sessionId = new SessionId("test-id");

    // When
    String result = sessionId.toString();

    // Then
    assertEquals("SNtest-id", result);
  }

  @Test
  void shouldFormatLinkIdToStringWithPrefix() {
    // Given
    LinkId linkId = new LinkId("test-id");

    // When
    String result = linkId.toString();

    // Then
    assertEquals("LKtest-id", result);
  }

  @Test
  void shouldFormatFileIdToStringWithPrefix() {
    // Given
    FileId fileId = new FileId("test-id");

    // When
    String result = fileId.toString();

    // Then
    assertEquals("FLtest-id", result);
  }

  @Test
  void shouldFormatUserIdToStringWithPrefix() {
    // Given
    UserId userId = new UserId("test-id");

    // When
    String result = userId.toString();

    // Then
    assertEquals("UStest-id", result);
  }

  // ==================== PREFIX CONSTANT TESTS ====================

  @Test
  void shouldSessionIdHaveSessionPrefix() {
    // Then
    assertEquals(IdPrefix.SESSION, SessionId.PREFIX);
  }

  @Test
  void shouldLinkIdHaveLinkPrefix() {
    // Then
    assertEquals(IdPrefix.LINK, LinkId.PREFIX);
  }

  @Test
  void shouldFileIdHaveFilePrefix() {
    // Then
    assertEquals(IdPrefix.FILE, FileId.PREFIX);
  }

  @Test
  void shouldUserIdHaveUserPrefix() {
    // Then
    assertEquals(IdPrefix.USER, UserId.PREFIX);
  }

  // ==================== EDGE CASE TESTS ====================

  @Test
  void shouldHandleEmptyStringValue() {
    // Given
    String emptyValue = "";

    // When
    SessionId sessionId = new SessionId(emptyValue);

    // Then
    assertEquals("", sessionId.value());
    assertEquals("SN", sessionId.toString());
  }

  @Test
  void shouldHandleLongValue() {
    // Given
    StringBuilder longValue = new StringBuilder();
    for (int i = 0; i < 1000; i++) {
      longValue.append("a");
    }

    // When
    LinkId linkId = new LinkId(longValue.toString());

    // Then
    assertEquals(1000, linkId.value().length());
  }

  @Test
  void shouldHandleSpecialCharactersInValue() {
    // Given
    String specialValue = "test-id_with.special:chars";

    // When
    FileId fileId = new FileId(specialValue);

    // Then
    assertEquals(specialValue, fileId.value());
  }

  @Test
  void shouldHandleUnicodeCharactersInValue() {
    // Given
    String unicodeValue = "test-идентификатор-日本語";

    // When
    SessionId sessionId = new SessionId(unicodeValue);

    // Then
    assertEquals(unicodeValue, sessionId.value());
  }

  @Test
  void shouldHandleWhitespaceInValue() {
    // Given
    String whitespaceValue = "  test id  ";

    // When
    LinkId linkId = new LinkId(whitespaceValue);

    // Then
    assertEquals(whitespaceValue, linkId.value());
  }

  // ==================== INHERITANCE TESTS ====================

  @Test
  void shouldSessionIdExtendId() {
    // Given
    SessionId sessionId = new SessionId("test");

    // Then
    assertTrue(sessionId instanceof Id);
  }

  @Test
  void shouldLinkIdExtendId() {
    // Given
    LinkId linkId = new LinkId("test");

    // Then
    assertTrue(linkId instanceof Id);
  }

  @Test
  void shouldFileIdExtendId() {
    // Given
    FileId fileId = new FileId("test");

    // Then
    assertTrue(fileId instanceof Id);
  }

  @Test
  void shouldUserIdExtendId() {
    // Given
    UserId userId = new UserId("test");

    // Then
    assertTrue(userId instanceof Id);
  }

  @Test
  void shouldIdPrefixImplementPrefix() {
    // Then
    assertTrue(IdPrefix.SESSION instanceof Id.Prefix);
    assertTrue(IdPrefix.FILE instanceof Id.Prefix);
    assertTrue(IdPrefix.LINK instanceof Id.Prefix);
    assertTrue(IdPrefix.USER instanceof Id.Prefix);
  }

  // ==================== GENERATION CONSISTENCY TESTS ====================

  @Test
  void shouldGeneratedSessionIdHaveValidUuidFormat() {
    // When
    SessionId sessionId = SessionId.generate();

    // Then - UUID format: 8-4-4-4-12 (36 chars with dashes)
    String value = sessionId.value();
    assertEquals(36, value.length());
    assertEquals('-', value.charAt(8));
    assertEquals('-', value.charAt(13));
    assertEquals('-', value.charAt(18));
    assertEquals('-', value.charAt(23));
  }

  @Test
  void shouldGeneratedLinkIdHaveValidUuidFormat() {
    // When
    LinkId linkId = LinkId.generate();

    // Then
    String value = linkId.value();
    assertEquals(36, value.length());
    assertEquals('-', value.charAt(8));
    assertEquals('-', value.charAt(13));
    assertEquals('-', value.charAt(18));
    assertEquals('-', value.charAt(23));
  }
}
