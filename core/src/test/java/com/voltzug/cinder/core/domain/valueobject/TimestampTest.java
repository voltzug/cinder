package com.voltzug.cinder.core.domain.valueobject;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive tests for Timestamp value object.
 * Tests timestamp creation, skew validation, and comparison operations.
 */
class TimestampTest {

  // ==================== CONSTRUCTION TESTS ====================

  @Test
  void shouldCreateTimestampFromInstant() {
    // Given
    Instant instant = Instant.now();

    // When
    Timestamp timestamp = new Timestamp(instant);

    // Then
    assertNotNull(timestamp);
    assertEquals(instant, timestamp.value());
  }

  @Test
  void shouldCreateTimestampUsingFromFactory() {
    // Given
    Instant instant = Instant.now();

    // When
    Timestamp timestamp = Timestamp.from(instant);

    // Then
    assertNotNull(timestamp);
    assertEquals(instant, timestamp.value());
  }

  @Test
  void shouldCreateTimestampUsingNowFactory() {
    // Given
    Instant before = Instant.now();

    // When
    Timestamp timestamp = Timestamp.now();
    Instant after = Instant.now();

    // Then
    assertNotNull(timestamp);
    assertFalse(timestamp.value().isBefore(before));
    assertFalse(timestamp.value().isAfter(after));
  }

  @Test
  void shouldCreateTimestampFromEpochMilli() {
    // Given
    long epochMilli = 1700000000000L;

    // When
    Timestamp timestamp = Timestamp.ofEpochMilli(epochMilli);

    // Then
    assertNotNull(timestamp);
    assertEquals(epochMilli, timestamp.toEpochMilli());
  }

  // ==================== VALIDATION TESTS ====================

  @Test
  void shouldThrowExceptionForNullValue() {
    // When/Then
    NullPointerException exception = assertThrows(
      NullPointerException.class,
      () -> new Timestamp(null)
    );
    assertEquals("value cannot be null", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionForNullInstantInFromFactory() {
    // When/Then
    assertThrows(NullPointerException.class, () -> Timestamp.from(null));
  }

  // ==================== SKEW VALIDATION TESTS ====================

  @Test
  void shouldReturnTrueWhenWithinSkew() {
    // Given
    Instant now = Instant.now();
    Timestamp timestamp = new Timestamp(now);
    long skewMs = 19000L; // 19 seconds as mentioned in context

    // When
    boolean result = timestamp.isWithinSkew(now, skewMs);

    // Then
    assertTrue(result);
  }

  @Test
  void shouldReturnTrueWhenExactlyAtSkewBoundary() {
    // Given
    Instant reference = Instant.now();
    long skewMs = 5000L;
    Instant timestampValue = reference.plusMillis(skewMs);
    Timestamp timestamp = new Timestamp(timestampValue);

    // When
    boolean result = timestamp.isWithinSkew(reference, skewMs);

    // Then
    assertTrue(result, "Should be within skew when exactly at boundary");
  }

  @Test
  void shouldReturnFalseWhenOutsideSkewInFuture() {
    // Given
    Instant reference = Instant.now();
    long skewMs = 5000L;
    Instant timestampValue = reference.plusMillis(skewMs + 1);
    Timestamp timestamp = new Timestamp(timestampValue);

    // When
    boolean result = timestamp.isWithinSkew(reference, skewMs);

    // Then
    assertFalse(result);
  }

  @Test
  void shouldReturnFalseWhenOutsideSkewInPast() {
    // Given
    Instant reference = Instant.now();
    long skewMs = 5000L;
    Instant timestampValue = reference.minusMillis(skewMs + 1);
    Timestamp timestamp = new Timestamp(timestampValue);

    // When
    boolean result = timestamp.isWithinSkew(reference, skewMs);

    // Then
    assertFalse(result);
  }

  @Test
  void shouldReturnTrueWhenExactlyAtNegativeSkewBoundary() {
    // Given
    Instant reference = Instant.now();
    long skewMs = 5000L;
    Instant timestampValue = reference.minusMillis(skewMs);
    Timestamp timestamp = new Timestamp(timestampValue);

    // When
    boolean result = timestamp.isWithinSkew(reference, skewMs);

    // Then
    assertTrue(
      result,
      "Should be within skew when exactly at negative boundary"
    );
  }

  @Test
  void shouldThrowExceptionForZeroSkew() {
    // Given
    Timestamp timestamp = Timestamp.now();
    Instant reference = Instant.now();

    // When/Then
    assertThrows(AssertionError.class, () ->
      timestamp.isWithinSkew(reference, 0)
    );
  }

  @Test
  void shouldThrowExceptionForNegativeSkew() {
    // Given
    Timestamp timestamp = Timestamp.now();
    Instant reference = Instant.now();

    // When/Then
    assertThrows(AssertionError.class, () ->
      timestamp.isWithinSkew(reference, -1000L)
    );
  }

  @Test
  void shouldThrowExceptionForNullReferenceInSkewCheck() {
    // Given
    Timestamp timestamp = Timestamp.now();

    // When/Then
    NullPointerException exception = assertThrows(
      NullPointerException.class,
      () -> timestamp.isWithinSkew(null, 5000L)
    );
    assertEquals("reference cannot be null", exception.getMessage());
  }

  @Test
  void shouldHandleLargeSkewValue() {
    // Given
    Instant reference = Instant.now();
    Timestamp timestamp = new Timestamp(reference.minus(1, ChronoUnit.DAYS));
    long skewMs = 2 * 24 * 60 * 60 * 1000L; // 2 days

    // When
    boolean result = timestamp.isWithinSkew(reference, skewMs);

    // Then
    assertTrue(result);
  }

  @Test
  void shouldHandle19SecondSkewAsPerProtocol() {
    // Given - Protocol specifies +-19sec skew
    Instant reference = Instant.now();
    long skewMs = 19000L;

    // When - Timestamp 18 seconds in the past
    Timestamp withinSkew = new Timestamp(reference.minusMillis(18000L));
    // Timestamp 20 seconds in the past
    Timestamp outsideSkew = new Timestamp(reference.minusMillis(20000L));

    // Then
    assertTrue(withinSkew.isWithinSkew(reference, skewMs));
    assertFalse(outsideSkew.isWithinSkew(reference, skewMs));
  }

  // ==================== COMPARISON TESTS ====================

  @Test
  void shouldReturnTrueWhenIsBefore() {
    // Given
    Instant past = Instant.now().minus(1, ChronoUnit.HOURS);
    Instant future = Instant.now().plus(1, ChronoUnit.HOURS);
    Timestamp timestamp = new Timestamp(past);

    // When
    boolean result = timestamp.isBefore(future);

    // Then
    assertTrue(result);
  }

  @Test
  void shouldReturnFalseWhenIsNotBefore() {
    // Given
    Instant future = Instant.now().plus(1, ChronoUnit.HOURS);
    Instant past = Instant.now().minus(1, ChronoUnit.HOURS);
    Timestamp timestamp = new Timestamp(future);

    // When
    boolean result = timestamp.isBefore(past);

    // Then
    assertFalse(result);
  }

  @Test
  void shouldReturnFalseWhenIsBeforeWithSameInstant() {
    // Given
    Instant now = Instant.now();
    Timestamp timestamp = new Timestamp(now);

    // When
    boolean result = timestamp.isBefore(now);

    // Then
    assertFalse(result, "Should not be before the same instant");
  }

  @Test
  void shouldReturnTrueWhenIsAfter() {
    // Given
    Instant future = Instant.now().plus(1, ChronoUnit.HOURS);
    Instant past = Instant.now().minus(1, ChronoUnit.HOURS);
    Timestamp timestamp = new Timestamp(future);

    // When
    boolean result = timestamp.isAfter(past);

    // Then
    assertTrue(result);
  }

  @Test
  void shouldReturnFalseWhenIsNotAfter() {
    // Given
    Instant past = Instant.now().minus(1, ChronoUnit.HOURS);
    Instant future = Instant.now().plus(1, ChronoUnit.HOURS);
    Timestamp timestamp = new Timestamp(past);

    // When
    boolean result = timestamp.isAfter(future);

    // Then
    assertFalse(result);
  }

  @Test
  void shouldReturnFalseWhenIsAfterWithSameInstant() {
    // Given
    Instant now = Instant.now();
    Timestamp timestamp = new Timestamp(now);

    // When
    boolean result = timestamp.isAfter(now);

    // Then
    assertFalse(result, "Should not be after the same instant");
  }

  @Test
  void shouldThrowExceptionForNullInIsBefore() {
    // Given
    Timestamp timestamp = Timestamp.now();

    // When/Then
    NullPointerException exception = assertThrows(
      NullPointerException.class,
      () -> timestamp.isBefore(null)
    );
    assertEquals("other cannot be null", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionForNullInIsAfter() {
    // Given
    Timestamp timestamp = Timestamp.now();

    // When/Then
    NullPointerException exception = assertThrows(
      NullPointerException.class,
      () -> timestamp.isAfter(null)
    );
    assertEquals("other cannot be null", exception.getMessage());
  }

  // ==================== EPOCH MILLI CONVERSION TESTS ====================

  @Test
  void shouldReturnCorrectEpochMilli() {
    // Given
    long expectedMilli = 1700000000000L;
    Timestamp timestamp = Timestamp.ofEpochMilli(expectedMilli);

    // When
    long result = timestamp.toEpochMilli();

    // Then
    assertEquals(expectedMilli, result);
  }

  @Test
  void shouldRoundTripThroughEpochMilli() {
    // Given
    Instant original = Instant.now();
    Timestamp timestamp = new Timestamp(original);

    // When
    long epochMilli = timestamp.toEpochMilli();
    Timestamp recreated = Timestamp.ofEpochMilli(epochMilli);

    // Then
    assertEquals(
      timestamp.value().toEpochMilli(),
      recreated.value().toEpochMilli()
    );
  }

  @Test
  void shouldHandleEpochZero() {
    // Given
    long epochZero = 0L;

    // When
    Timestamp timestamp = Timestamp.ofEpochMilli(epochZero);

    // Then
    assertEquals(Instant.EPOCH, timestamp.value());
    assertEquals(0L, timestamp.toEpochMilli());
  }

  // ==================== EDGE CASE TESTS ====================

  @Test
  void shouldHandleVeryOldTimestamp() {
    // Given
    Instant veryOld = Instant.parse("1970-01-01T00:00:01Z");

    // When
    Timestamp timestamp = new Timestamp(veryOld);

    // Then
    assertEquals(veryOld, timestamp.value());
    assertEquals(1000L, timestamp.toEpochMilli());
  }

  @Test
  void shouldHandleFarFutureTimestamp() {
    // Given
    Instant farFuture = Instant.now().plus(365 * 100, ChronoUnit.DAYS);

    // When
    Timestamp timestamp = new Timestamp(farFuture);

    // Then
    assertEquals(farFuture, timestamp.value());
  }

  @Test
  void shouldHandleEpochInstant() {
    // Given
    Instant epoch = Instant.EPOCH;

    // When
    Timestamp timestamp = new Timestamp(epoch);

    // Then
    assertEquals(epoch, timestamp.value());
    assertEquals(0L, timestamp.toEpochMilli());
  }

  // ==================== RECORD FUNCTIONALITY TESTS ====================

  @Test
  void shouldHaveCorrectEqualsForSameValues() {
    // Given
    Instant instant = Instant.parse("2025-06-01T12:00:00Z");

    // When
    Timestamp timestamp1 = new Timestamp(instant);
    Timestamp timestamp2 = new Timestamp(instant);

    // Then
    assertEquals(timestamp1, timestamp2);
  }

  @Test
  void shouldHaveCorrectHashCodeForSameValues() {
    // Given
    Instant instant = Instant.parse("2025-06-01T12:00:00Z");

    // When
    Timestamp timestamp1 = new Timestamp(instant);
    Timestamp timestamp2 = new Timestamp(instant);

    // Then
    assertEquals(timestamp1.hashCode(), timestamp2.hashCode());
  }

  @Test
  void shouldNotBeEqualForDifferentValues() {
    // Given
    Instant instant1 = Instant.now();
    Instant instant2 = instant1.plusMillis(1);

    // When
    Timestamp timestamp1 = new Timestamp(instant1);
    Timestamp timestamp2 = new Timestamp(instant2);

    // Then
    assertNotEquals(timestamp1, timestamp2);
  }

  @Test
  void shouldHaveCorrectToString() {
    // Given
    Instant instant = Instant.parse("2025-06-01T12:00:00Z");
    Timestamp timestamp = new Timestamp(instant);

    // When
    String result = timestamp.toString();

    // Then
    assertTrue(result.contains("Timestamp"));
    assertTrue(result.contains("value"));
    assertTrue(result.contains("2025-06-01"));
  }
}
