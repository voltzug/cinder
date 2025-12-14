package com.voltzug.cinder.core.domain.valueobject;

import static org.junit.jupiter.api.Assertions.*;

import com.voltzug.cinder.core.domain.entity.IExpirable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive tests for FileSpecs value object.
 * Tests file specification parameters including expiry date and retry count validation.
 */
class FileSpecsTest {

  // ==================== CONSTRUCTION TESTS ====================

  @Test
  void shouldCreateFileSpecsWithValidParameters() {
    // Given
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);
    int retryCount = 5;

    // When
    FileSpecs specs = new FileSpecs(expiryDate, retryCount);

    // Then
    assertNotNull(specs);
    assertEquals(expiryDate, specs.expiryDate());
    assertEquals(retryCount, specs.retryCount());
  }

  @Test
  void shouldCreateFileSpecsWithMinRetryCount() {
    // Given
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);

    // When
    FileSpecs specs = new FileSpecs(expiryDate, FileSpecs.MIN_RETRY_COUNT);

    // Then
    assertEquals(FileSpecs.MIN_RETRY_COUNT, specs.retryCount());
  }

  @Test
  void shouldCreateFileSpecsWithMaxRetryCount() {
    // Given
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);

    // When
    FileSpecs specs = new FileSpecs(expiryDate, FileSpecs.MAX_RETRY_COUNT);

    // Then
    assertEquals(FileSpecs.MAX_RETRY_COUNT, specs.retryCount());
  }

  // ==================== VALIDATION TESTS ====================

  @Test
  void shouldThrowExceptionForNullExpiryDate() {
    // When/Then
    NullPointerException exception = assertThrows(
      NullPointerException.class,
      () -> new FileSpecs(null, 5)
    );
    assertEquals("expiryDate cannot be null", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionForRetryCountBelowMin() {
    // Given
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);

    // When/Then
    AssertionError exception = assertThrows(AssertionError.class, () ->
      new FileSpecs(expiryDate, FileSpecs.MIN_RETRY_COUNT - 1)
    );
    assertTrue(exception.getMessage().contains("retryCount must be between"));
  }

  @Test
  void shouldThrowExceptionForZeroRetryCount() {
    // Given
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);

    // When/Then
    assertThrows(AssertionError.class, () -> new FileSpecs(expiryDate, 0));
  }

  @Test
  void shouldThrowExceptionForNegativeRetryCount() {
    // Given
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);

    // When/Then
    assertThrows(AssertionError.class, () -> new FileSpecs(expiryDate, -1));
  }

  @Test
  void shouldThrowExceptionForRetryCountAboveMax() {
    // Given
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);

    // When/Then
    AssertionError exception = assertThrows(AssertionError.class, () ->
      new FileSpecs(expiryDate, FileSpecs.MAX_RETRY_COUNT + 1)
    );
    assertTrue(exception.getMessage().contains("retryCount must be between"));
  }

  @Test
  void shouldThrowExceptionForLargeRetryCount() {
    // Given
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);

    // When/Then
    assertThrows(AssertionError.class, () -> new FileSpecs(expiryDate, 1000));
  }

  // ==================== CONSTANTS TESTS ====================

  @Test
  void shouldHaveMinRetryCountOfOne() {
    // Then
    assertEquals(1, FileSpecs.MIN_RETRY_COUNT);
  }

  @Test
  void shouldHaveMaxRetryCountOf99() {
    // Then
    assertEquals(99, FileSpecs.MAX_RETRY_COUNT);
  }

  // ==================== EXPIRABLE INTERFACE TESTS ====================

  @Test
  void shouldReturnExpiryDateFromGetExpiryDate() {
    // Given
    Instant expiryDate = Instant.now().plus(2, ChronoUnit.HOURS);
    FileSpecs specs = new FileSpecs(expiryDate, 3);

    // When
    Instant result = specs.getExpiryDate();

    // Then
    assertEquals(expiryDate, result);
  }

  @Test
  void shouldReturnTrueWhenExpired() {
    // Given
    Instant expiryDate = Instant.now().minus(1, ChronoUnit.HOURS);
    FileSpecs specs = new FileSpecs(expiryDate, 3);

    // When
    boolean expired = specs.isExpired(Instant.now());

    // Then
    assertTrue(expired);
  }

  @Test
  void shouldReturnFalseWhenNotExpired() {
    // Given
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.HOURS);
    FileSpecs specs = new FileSpecs(expiryDate, 3);

    // When
    boolean expired = specs.isExpired(Instant.now());

    // Then
    assertFalse(expired);
  }

  @Test
  void shouldReturnFalseWhenExactlyAtExpiryTime() {
    // Given
    Instant now = Instant.now();
    FileSpecs specs = new FileSpecs(now, 3);

    // When
    boolean expired = specs.isExpired(now);

    // Then
    assertFalse(expired, "Should not be expired when exactly at expiry time");
  }

  @Test
  void shouldReturnTrueWhenOneMillisecondAfterExpiry() {
    // Given
    Instant expiryDate = Instant.now();
    FileSpecs specs = new FileSpecs(expiryDate, 3);
    Instant oneMilliAfter = expiryDate.plusMillis(1);

    // When
    boolean expired = specs.isExpired(oneMilliAfter);

    // Then
    assertTrue(expired);
  }

  @Test
  void shouldReturnFalseWhenOneMillisecondBeforeExpiry() {
    // Given
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);
    FileSpecs specs = new FileSpecs(expiryDate, 3);
    Instant oneMilliBefore = expiryDate.minusMillis(1);

    // When
    boolean expired = specs.isExpired(oneMilliBefore);

    // Then
    assertFalse(expired);
  }

  // ==================== FACTORY METHOD TESTS ====================

  @Test
  void shouldCreateCopyUsingFromMethod() {
    // Given
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);
    FileSpecs original = new FileSpecs(expiryDate, 7);

    // When
    FileSpecs copy = FileSpecs.from(original);

    // Then
    assertNotNull(copy);
    assertEquals(original.expiryDate(), copy.expiryDate());
    assertEquals(original.retryCount(), copy.retryCount());
  }

  @Test
  void shouldCreateIndependentCopyFromOriginal() {
    // Given
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);
    FileSpecs original = new FileSpecs(expiryDate, 5);

    // When
    FileSpecs copy = FileSpecs.from(original);

    // Then - They should be equal but not the same instance
    assertEquals(original, copy);
    assertNotSame(original, copy);
  }

  // ==================== INTERFACE IMPLEMENTATION TESTS ====================

  @Test
  void shouldImplementIExpirable() {
    // Given
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);

    // When
    FileSpecs specs = new FileSpecs(expiryDate, 3);

    // Then
    assertTrue(specs instanceof IExpirable);
  }

  // ==================== EDGE CASE TESTS ====================

  @Test
  void shouldHandleFarFutureExpiryDate() {
    // Given
    Instant farFuture = Instant.now().plus(365 * 100, ChronoUnit.DAYS);

    // When
    FileSpecs specs = new FileSpecs(farFuture, 5);

    // Then
    assertFalse(specs.isExpired(Instant.now()));
    assertEquals(farFuture, specs.expiryDate());
  }

  @Test
  void shouldHandlePastExpiryDate() {
    // Given
    Instant pastDate = Instant.now().minus(30, ChronoUnit.DAYS);

    // When
    FileSpecs specs = new FileSpecs(pastDate, 5);

    // Then
    assertTrue(specs.isExpired(Instant.now()));
  }

  @Test
  void shouldHandleEpochExpiryDate() {
    // Given
    Instant epoch = Instant.EPOCH;

    // When
    FileSpecs specs = new FileSpecs(epoch, 1);

    // Then
    assertTrue(specs.isExpired(Instant.now()));
    assertEquals(epoch, specs.expiryDate());
  }

  @Test
  void shouldHandleAllValidRetryCountsInRange() {
    // Given
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);

    // Then - All values from MIN to MAX should be valid
    for (
      int i = FileSpecs.MIN_RETRY_COUNT;
      i <= FileSpecs.MAX_RETRY_COUNT;
      i++
    ) {
      final int retryCount = i;
      assertDoesNotThrow(() -> new FileSpecs(expiryDate, retryCount));
    }
  }

  // ==================== RECORD FUNCTIONALITY TESTS ====================

  @Test
  void shouldHaveCorrectEqualsForSameValues() {
    // Given
    Instant expiryDate = Instant.parse("2025-06-01T12:00:00Z");

    // When
    FileSpecs specs1 = new FileSpecs(expiryDate, 5);
    FileSpecs specs2 = new FileSpecs(expiryDate, 5);

    // Then
    assertEquals(specs1, specs2);
  }

  @Test
  void shouldHaveCorrectHashCodeForSameValues() {
    // Given
    Instant expiryDate = Instant.parse("2025-06-01T12:00:00Z");

    // When
    FileSpecs specs1 = new FileSpecs(expiryDate, 5);
    FileSpecs specs2 = new FileSpecs(expiryDate, 5);

    // Then
    assertEquals(specs1.hashCode(), specs2.hashCode());
  }

  @Test
  void shouldNotBeEqualForDifferentRetryCount() {
    // Given
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);

    // When
    FileSpecs specs1 = new FileSpecs(expiryDate, 3);
    FileSpecs specs2 = new FileSpecs(expiryDate, 5);

    // Then
    assertNotEquals(specs1, specs2);
  }

  @Test
  void shouldNotBeEqualForDifferentExpiryDate() {
    // Given
    Instant expiryDate1 = Instant.now().plus(1, ChronoUnit.DAYS);
    Instant expiryDate2 = Instant.now().plus(2, ChronoUnit.DAYS);

    // When
    FileSpecs specs1 = new FileSpecs(expiryDate1, 5);
    FileSpecs specs2 = new FileSpecs(expiryDate2, 5);

    // Then
    assertNotEquals(specs1, specs2);
  }

  @Test
  void shouldHaveCorrectToString() {
    // Given
    Instant expiryDate = Instant.parse("2025-06-01T12:00:00Z");
    FileSpecs specs = new FileSpecs(expiryDate, 5);

    // When
    String result = specs.toString();

    // Then
    assertTrue(result.contains("FileSpecs"));
    assertTrue(result.contains("expiryDate"));
    assertTrue(result.contains("retryCount"));
    assertTrue(result.contains("5"));
  }
}
