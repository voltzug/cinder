package com.voltzug.cinder.core.domain.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.voltzug.cinder.core.domain.valueobject.SessionSecret;
import com.voltzug.cinder.core.domain.valueobject.id.LinkId;
import com.voltzug.cinder.core.domain.valueobject.id.SessionId;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive tests for Session entity.
 * Tests session creation, expiration, mode handling, and validation.
 */
class SessionTest {

  // ==================== CONSTRUCTION TESTS ====================

  @Test
  void shouldCreateUploadSessionWithValidParameters() {
    // Given
    SessionId id = SessionId.generate();
    SessionSecret secret = new SessionSecret(new byte[32]);
    Instant createdAt = Instant.now();
    Instant expiresAt = createdAt.plus(15, ChronoUnit.MINUTES);

    // When
    Session session = new Session(
      id,
      secret,
      null,
      Session.Mode.UPLOAD,
      createdAt,
      expiresAt
    );

    // Then
    assertNotNull(session);
    assertEquals(id, session.id());
    assertNotNull(session.sessionSecret());
    assertNull(session.linkId());
    assertEquals(Session.Mode.UPLOAD, session.mode());
    assertEquals(createdAt, session.createdAt());
    assertEquals(expiresAt, session.expiresAt());
  }

  @Test
  void shouldCreateDownloadSessionWithLinkId() {
    // Given
    SessionId id = SessionId.generate();
    SessionSecret secret = new SessionSecret(new byte[32]);
    LinkId linkId = LinkId.generate();
    Instant createdAt = Instant.now();
    Instant expiresAt = createdAt.plus(10, ChronoUnit.MINUTES);

    // When
    Session session = new Session(
      id,
      secret,
      linkId,
      Session.Mode.DOWNLOAD,
      createdAt,
      expiresAt
    );

    // Then
    assertNotNull(session);
    assertEquals(linkId, session.linkId());
    assertEquals(Session.Mode.DOWNLOAD, session.mode());
  }

  @Test
  void shouldCreateSessionWithNullSessionSecret() {
    // Given
    SessionId id = SessionId.generate();
    Instant createdAt = Instant.now();
    Instant expiresAt = createdAt.plus(15, ChronoUnit.MINUTES);

    // When
    Session session = new Session(
      id,
      null,
      null,
      Session.Mode.UPLOAD,
      createdAt,
      expiresAt
    );

    // Then
    assertNotNull(session);
    assertNull(session.sessionSecret());
  }

  @Test
  void shouldCreateSessionWithNullLinkId() {
    // Given
    SessionId id = SessionId.generate();
    SessionSecret secret = new SessionSecret(new byte[32]);
    Instant createdAt = Instant.now();
    Instant expiresAt = createdAt.plus(15, ChronoUnit.MINUTES);

    // When
    Session session = new Session(
      id,
      secret,
      null,
      Session.Mode.UPLOAD,
      createdAt,
      expiresAt
    );

    // Then
    assertNotNull(session);
    assertNull(session.linkId());
  }

  // ==================== VALIDATION TESTS ====================

  @Test
  void shouldThrowExceptionForNullId() {
    // Given
    SessionSecret secret = new SessionSecret(new byte[32]);
    Instant createdAt = Instant.now();
    Instant expiresAt = createdAt.plus(15, ChronoUnit.MINUTES);

    // When/Then
    NullPointerException exception = assertThrows(
      NullPointerException.class,
      () ->
        new Session(
          null,
          secret,
          null,
          Session.Mode.UPLOAD,
          createdAt,
          expiresAt
        )
    );
    assertEquals("id cannot be null", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionForNullMode() {
    // Given
    SessionId id = SessionId.generate();
    SessionSecret secret = new SessionSecret(new byte[32]);
    Instant createdAt = Instant.now();
    Instant expiresAt = createdAt.plus(15, ChronoUnit.MINUTES);

    // When/Then
    NullPointerException exception = assertThrows(
      NullPointerException.class,
      () -> new Session(id, secret, null, null, createdAt, expiresAt)
    );
    assertEquals("mode cannot be null", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionForNullCreatedAt() {
    // Given
    SessionId id = SessionId.generate();
    SessionSecret secret = new SessionSecret(new byte[32]);
    Instant expiresAt = Instant.now().plus(15, ChronoUnit.MINUTES);

    // When/Then
    NullPointerException exception = assertThrows(
      NullPointerException.class,
      () -> new Session(id, secret, null, Session.Mode.UPLOAD, null, expiresAt)
    );
    assertEquals("createdAt cannot be null", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionForNullExpiresAt() {
    // Given
    SessionId id = SessionId.generate();
    SessionSecret secret = new SessionSecret(new byte[32]);
    Instant createdAt = Instant.now();

    // When/Then
    NullPointerException exception = assertThrows(
      NullPointerException.class,
      () -> new Session(id, secret, null, Session.Mode.UPLOAD, createdAt, null)
    );
    assertEquals("expiresAt cannot be null", exception.getMessage());
  }

  // ==================== EXPIRABLE INTERFACE TESTS ====================

  @Test
  void shouldReturnExpiresAtFromGetExpiryDate() {
    // Given
    SessionId id = SessionId.generate();
    Instant createdAt = Instant.now();
    Instant expiresAt = createdAt.plus(30, ChronoUnit.MINUTES);
    Session session = new Session(
      id,
      null,
      null,
      Session.Mode.UPLOAD,
      createdAt,
      expiresAt
    );

    // When
    Instant result = session.getExpiryDate();

    // Then
    assertEquals(expiresAt, result);
  }

  @Test
  void shouldReturnTrueWhenSessionExpired() {
    // Given
    SessionId id = SessionId.generate();
    Instant createdAt = Instant.now().minus(1, ChronoUnit.HOURS);
    Instant expiresAt = createdAt.plus(15, ChronoUnit.MINUTES);
    Session session = new Session(
      id,
      null,
      null,
      Session.Mode.UPLOAD,
      createdAt,
      expiresAt
    );

    // When
    boolean expired = session.isExpired(Instant.now());

    // Then
    assertTrue(expired);
  }

  @Test
  void shouldReturnFalseWhenSessionNotExpired() {
    // Given
    SessionId id = SessionId.generate();
    Instant createdAt = Instant.now();
    Instant expiresAt = createdAt.plus(1, ChronoUnit.HOURS);
    Session session = new Session(
      id,
      null,
      null,
      Session.Mode.UPLOAD,
      createdAt,
      expiresAt
    );

    // When
    boolean expired = session.isExpired(Instant.now());

    // Then
    assertFalse(expired);
  }

  @Test
  void shouldReturnFalseWhenExactlyAtExpiryTime() {
    // Given
    SessionId id = SessionId.generate();
    Instant now = Instant.now();
    Session session = new Session(
      id,
      null,
      null,
      Session.Mode.UPLOAD,
      now.minus(15, ChronoUnit.MINUTES),
      now
    );

    // When
    boolean expired = session.isExpired(now);

    // Then
    assertFalse(expired, "Should not be expired when exactly at expiry time");
  }

  @Test
  void shouldReturnTrueWhenOneMillisecondAfterExpiry() {
    // Given
    SessionId id = SessionId.generate();
    Instant expiresAt = Instant.now();
    Session session = new Session(
      id,
      null,
      null,
      Session.Mode.UPLOAD,
      expiresAt.minus(15, ChronoUnit.MINUTES),
      expiresAt
    );
    Instant oneMilliAfter = expiresAt.plusMillis(1);

    // When
    boolean expired = session.isExpired(oneMilliAfter);

    // Then
    assertTrue(expired);
  }

  // ==================== MODE TESTS ====================

  @Test
  void shouldHaveUploadMode() {
    // Given
    SessionId id = SessionId.generate();
    Instant createdAt = Instant.now();
    Instant expiresAt = createdAt.plus(15, ChronoUnit.MINUTES);

    // When
    Session session = new Session(
      id,
      null,
      null,
      Session.Mode.UPLOAD,
      createdAt,
      expiresAt
    );

    // Then
    assertEquals(Session.Mode.UPLOAD, session.mode());
  }

  @Test
  void shouldHaveDownloadMode() {
    // Given
    SessionId id = SessionId.generate();
    LinkId linkId = LinkId.generate();
    Instant createdAt = Instant.now();
    Instant expiresAt = createdAt.plus(15, ChronoUnit.MINUTES);

    // When
    Session session = new Session(
      id,
      null,
      linkId,
      Session.Mode.DOWNLOAD,
      createdAt,
      expiresAt
    );

    // Then
    assertEquals(Session.Mode.DOWNLOAD, session.mode());
  }

  @Test
  void shouldHaveTwoModeValues() {
    // When
    Session.Mode[] modes = Session.Mode.values();

    // Then
    assertEquals(2, modes.length);
    assertEquals(Session.Mode.UPLOAD, modes[0]);
    assertEquals(Session.Mode.DOWNLOAD, modes[1]);
  }

  @Test
  void shouldRetrieveModeFromValueOf() {
    // When/Then
    assertEquals(Session.Mode.UPLOAD, Session.Mode.valueOf("UPLOAD"));
    assertEquals(Session.Mode.DOWNLOAD, Session.Mode.valueOf("DOWNLOAD"));
  }

  // ==================== TOSTRING TESTS ====================

  @Test
  void shouldMaskSessionSecretInToString() {
    // Given
    SessionId id = SessionId.generate();
    SessionSecret secret = new SessionSecret(new byte[32]);
    Instant createdAt = Instant.now();
    Instant expiresAt = createdAt.plus(15, ChronoUnit.MINUTES);
    Session session = new Session(
      id,
      secret,
      null,
      Session.Mode.UPLOAD,
      createdAt,
      expiresAt
    );

    // When
    String result = session.toString();

    // Then
    assertTrue(result.contains("[PROTECTED]"));
    assertFalse(result.contains(secret.toString()));
  }

  @Test
  void shouldShowNullForNullSessionSecretInToString() {
    // Given
    SessionId id = SessionId.generate();
    Instant createdAt = Instant.now();
    Instant expiresAt = createdAt.plus(15, ChronoUnit.MINUTES);
    Session session = new Session(
      id,
      null,
      null,
      Session.Mode.UPLOAD,
      createdAt,
      expiresAt
    );

    // When
    String result = session.toString();

    // Then
    assertTrue(result.contains("sessionSecret=null"));
  }

  @Test
  void shouldIncludeAllFieldsInToString() {
    // Given
    SessionId id = SessionId.generate();
    LinkId linkId = LinkId.generate();
    Instant createdAt = Instant.now();
    Instant expiresAt = createdAt.plus(15, ChronoUnit.MINUTES);
    Session session = new Session(
      id,
      null,
      linkId,
      Session.Mode.DOWNLOAD,
      createdAt,
      expiresAt
    );

    // When
    String result = session.toString();

    // Then
    assertTrue(result.contains("Session{"));
    assertTrue(result.contains("id="));
    assertTrue(result.contains("linkId="));
    assertTrue(result.contains("mode=DOWNLOAD"));
    assertTrue(result.contains("createdAt="));
    assertTrue(result.contains("expiresAt="));
  }

  // ==================== EDGE CASE TESTS ====================

  @Test
  void shouldHandleExpiresAtBeforeCreatedAt() {
    // Given - This is an unusual but valid case
    SessionId id = SessionId.generate();
    Instant createdAt = Instant.now();
    Instant expiresAt = createdAt.minus(1, ChronoUnit.HOURS);

    // When
    Session session = new Session(
      id,
      null,
      null,
      Session.Mode.UPLOAD,
      createdAt,
      expiresAt
    );

    // Then - Session is immediately expired
    assertTrue(session.isExpired(Instant.now()));
  }

  @Test
  void shouldHandleVeryShortSessionDuration() {
    // Given
    SessionId id = SessionId.generate();
    Instant createdAt = Instant.now();
    Instant expiresAt = createdAt.plusMillis(1);

    // When
    Session session = new Session(
      id,
      null,
      null,
      Session.Mode.UPLOAD,
      createdAt,
      expiresAt
    );

    // Then
    assertNotNull(session);
    assertEquals(
      1,
      java.time.Duration.between(createdAt, expiresAt).toMillis()
    );
  }

  @Test
  void shouldHandleLongSessionDuration() {
    // Given
    SessionId id = SessionId.generate();
    Instant createdAt = Instant.now();
    Instant expiresAt = createdAt.plus(365, ChronoUnit.DAYS);

    // When
    Session session = new Session(
      id,
      null,
      null,
      Session.Mode.UPLOAD,
      createdAt,
      expiresAt
    );

    // Then
    assertFalse(session.isExpired(Instant.now()));
  }

  // ==================== INTERFACE IMPLEMENTATION TESTS ====================

  @Test
  void shouldImplementIExpirable() {
    // Given
    SessionId id = SessionId.generate();
    Instant createdAt = Instant.now();
    Instant expiresAt = createdAt.plus(15, ChronoUnit.MINUTES);

    // When
    Session session = new Session(
      id,
      null,
      null,
      Session.Mode.UPLOAD,
      createdAt,
      expiresAt
    );

    // Then
    assertTrue(session instanceof IExpirable);
  }
}
