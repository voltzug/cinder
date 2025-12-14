package com.voltzug.cinder.core.domain.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;

import com.voltzug.cinder.core.domain.valueobject.id.LinkId;

/**
 * Comprehensive tests for DownloadLimit entity.
 * Tests download attempt tracking, expiration logic, and validation.
 */
class DownloadLimitTest {

  // ==================== CONSTRUCTION TESTS ====================

  @Test
  void shouldCreateDownloadLimitWithValidParameters() {
    // Given
    LinkId linkId = LinkId.generate();
    int remainingAttempts = 5;
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);
    Instant lastAttemptAt = Instant.now();

    // When
    DownloadLimit limit = new DownloadLimit(
      linkId,
      remainingAttempts,
      expiryDate,
      lastAttemptAt
    );

    // Then
    assertNotNull(limit);
    assertEquals(linkId, limit.linkId());
    assertEquals(remainingAttempts, limit.remainingAttempts());
    assertEquals(expiryDate, limit.expiryDate());
    assertEquals(lastAttemptAt, limit.lastAttemptAt());
  }

  @Test
  void shouldCreateDownloadLimitWithNullLastAttemptAt() {
    // Given
    LinkId linkId = LinkId.generate();
    int remainingAttempts = 3;
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.HOURS);

    // When
    DownloadLimit limit = new DownloadLimit(
      linkId,
      remainingAttempts,
      expiryDate,
      null
    );

    // Then
    assertNotNull(limit);
    assertNull(limit.lastAttemptAt());
  }

  @Test
  void shouldCreateDownloadLimitWithZeroRemainingAttempts() {
    // Given
    LinkId linkId = LinkId.generate();
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);

    // When
    DownloadLimit limit = new DownloadLimit(linkId, 0, expiryDate, null);

    // Then
    assertNotNull(limit);
    assertEquals(0, limit.remainingAttempts());
  }

  // ==================== VALIDATION TESTS ====================

  @Test
  void shouldThrowExceptionForNullLinkId() {
    // Given
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);

    // When/Then
    NullPointerException exception = assertThrows(
      NullPointerException.class,
      () -> new DownloadLimit(null, 5, expiryDate, null)
    );
    assertEquals("linkId must not be null", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionForNullExpiryDate() {
    // Given
    LinkId linkId = LinkId.generate();

    // When/Then
    NullPointerException exception = assertThrows(
      NullPointerException.class,
      () -> new DownloadLimit(linkId, 5, null, null)
    );
    assertEquals("expiryDate must not be null", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionForNegativeRemainingAttempts() {
    // Given
    LinkId linkId = LinkId.generate();
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);

    // When/Then
    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> new DownloadLimit(linkId, -1, expiryDate, null)
    );
    assertEquals(
      "remainingAttempts cannot be negative",
      exception.getMessage()
    );
  }

  @Test
  void shouldThrowExceptionForLargeNegativeRemainingAttempts() {
    // Given
    LinkId linkId = LinkId.generate();
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);

    // When/Then
    assertThrows(IllegalArgumentException.class, () ->
      new DownloadLimit(linkId, -100, expiryDate, null)
    );
  }

  // ==================== EXPIRABLE INTERFACE TESTS ====================

  @Test
  void shouldReturnExpiryDateFromGetExpiryDate() {
    // Given
    LinkId linkId = LinkId.generate();
    Instant expiryDate = Instant.now().plus(2, ChronoUnit.HOURS);
    DownloadLimit limit = new DownloadLimit(linkId, 3, expiryDate, null);

    // When
    Instant result = limit.getExpiryDate();

    // Then
    assertEquals(expiryDate, result);
  }

  @Test
  void shouldReturnTrueWhenExpired() {
    // Given
    LinkId linkId = LinkId.generate();
    Instant expiryDate = Instant.now().minus(1, ChronoUnit.HOURS);
    DownloadLimit limit = new DownloadLimit(linkId, 3, expiryDate, null);

    // When
    boolean expired = limit.isExpired(Instant.now());

    // Then
    assertTrue(expired);
  }

  @Test
  void shouldReturnFalseWhenNotExpired() {
    // Given
    LinkId linkId = LinkId.generate();
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.HOURS);
    DownloadLimit limit = new DownloadLimit(linkId, 3, expiryDate, null);

    // When
    boolean expired = limit.isExpired(Instant.now());

    // Then
    assertFalse(expired);
  }

  @Test
  void shouldReturnFalseWhenExactlyAtExpiryTime() {
    // Given
    Instant now = Instant.now();
    LinkId linkId = LinkId.generate();
    DownloadLimit limit = new DownloadLimit(linkId, 3, now, null);

    // When
    boolean expired = limit.isExpired(now);

    // Then
    assertFalse(expired, "Should not be expired when exactly at expiry time");
  }

  @Test
  void shouldReturnTrueWhenOneMillisecondAfterExpiry() {
    // Given
    Instant expiryDate = Instant.now();
    LinkId linkId = LinkId.generate();
    DownloadLimit limit = new DownloadLimit(linkId, 3, expiryDate, null);
    Instant oneMilliAfter = expiryDate.plusMillis(1);

    // When
    boolean expired = limit.isExpired(oneMilliAfter);

    // Then
    assertTrue(expired);
  }

  @Test
  void shouldReturnFalseWhenOneMillisecondBeforeExpiry() {
    // Given
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);
    LinkId linkId = LinkId.generate();
    DownloadLimit limit = new DownloadLimit(linkId, 3, expiryDate, null);
    Instant oneMilliBefore = expiryDate.minusMillis(1);

    // When
    boolean expired = limit.isExpired(oneMilliBefore);

    // Then
    assertFalse(expired);
  }

  // ==================== EDGE CASE TESTS ====================

  @Test
  void shouldHandleMaxIntRemainingAttempts() {
    // Given
    LinkId linkId = LinkId.generate();
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);

    // When
    DownloadLimit limit = new DownloadLimit(
      linkId,
      Integer.MAX_VALUE,
      expiryDate,
      null
    );

    // Then
    assertEquals(Integer.MAX_VALUE, limit.remainingAttempts());
  }

  @Test
  void shouldHandleFarFutureExpiryDate() {
    // Given
    LinkId linkId = LinkId.generate();
    Instant farFuture = Instant.now().plus(365 * 100, ChronoUnit.DAYS);

    // When
    DownloadLimit limit = new DownloadLimit(linkId, 5, farFuture, null);

    // Then
    assertFalse(limit.isExpired(Instant.now()));
    assertEquals(farFuture, limit.expiryDate());
  }

  @Test
  void shouldHandlePastExpiryDate() {
    // Given
    LinkId linkId = LinkId.generate();
    Instant pastDate = Instant.now().minus(30, ChronoUnit.DAYS);

    // When
    DownloadLimit limit = new DownloadLimit(linkId, 5, pastDate, null);

    // Then
    assertTrue(limit.isExpired(Instant.now()));
  }

  @Test
  void shouldHandleLastAttemptAtInThePast() {
    // Given
    LinkId linkId = LinkId.generate();
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);
    Instant lastAttempt = Instant.now().minus(10, ChronoUnit.MINUTES);

    // When
    DownloadLimit limit = new DownloadLimit(linkId, 2, expiryDate, lastAttempt);

    // Then
    assertEquals(lastAttempt, limit.lastAttemptAt());
  }

  // ==================== RECORD FUNCTIONALITY TESTS ====================

  @Test
  void shouldHaveCorrectEqualsForSameValues() {
    // Given
    LinkId linkId = new LinkId("test-link-id");
    Instant expiryDate = Instant.parse("2025-01-01T12:00:00Z");
    Instant lastAttempt = Instant.parse("2025-01-01T10:00:00Z");

    // When
    DownloadLimit limit1 = new DownloadLimit(
      linkId,
      3,
      expiryDate,
      lastAttempt
    );
    DownloadLimit limit2 = new DownloadLimit(
      new LinkId("test-link-id"),
      3,
      expiryDate,
      lastAttempt
    );

    // Then
    assertEquals(limit1.remainingAttempts(), limit2.remainingAttempts());
    assertEquals(limit1.expiryDate(), limit2.expiryDate());
    assertEquals(limit1.lastAttemptAt(), limit2.lastAttemptAt());
  }

  @Test
  void shouldHaveCorrectHashCodeForSameValues() {
    // Given
    Instant expiryDate = Instant.parse("2025-01-01T12:00:00Z");
    Instant lastAttempt = Instant.parse("2025-01-01T10:00:00Z");

    // When
    DownloadLimit limit1 = new DownloadLimit(
      new LinkId("same-id"),
      5,
      expiryDate,
      lastAttempt
    );
    DownloadLimit limit2 = new DownloadLimit(
      new LinkId("same-id"),
      5,
      expiryDate,
      lastAttempt
    );

    // Then
    assertEquals(limit1.hashCode(), limit2.hashCode());
  }

  @Test
  void shouldImplementIExpirable() {
    // Given
    LinkId linkId = LinkId.generate();
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);

    // When
    DownloadLimit limit = new DownloadLimit(linkId, 3, expiryDate, null);

    // Then
    assertTrue(limit instanceof IExpirable);
  }
}
