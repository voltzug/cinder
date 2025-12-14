package com.voltzug.cinder.core.common.valueobject.safe;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Comprehensive tests for SafeBlobSized value object.
 * Tests size-constrained secure blob handling with min/max, fixed, and power-of-two validation.
 */
class SafeBlobSizedTest {

  // ==================== UNCONSTRAINED CONSTRUCTION TESTS ====================

  @Test
  void shouldCreateSafeBlobSizedWithNoConstraints() {
    // Given
    byte[] data = { 0x01, 0x02, 0x03, 0x04, 0x05 };

    // When
    SafeBlobSized blob = new SafeBlobSized(data);

    // Then
    assertEquals(5, blob.size());
    blob.close();
  }

  @Test
  void shouldSealOriginalArrayAfterUnconstrainedCreation() {
    // Given
    byte[] original = { 0x01, 0x02, 0x03, 0x04, 0x05 };

    // When
    SafeBlobSized blob = new SafeBlobSized(original);

    // Then
    assertArrayEquals(
      new byte[] { 0, 0, 0, 0, 0 },
      original,
      "Original array should be sealed after SafeBlobSized creation"
    );
    blob.close();
  }

  @Test
  void shouldThrowForNullByteArrayUnconstrained() {
    // When/Then
    assertThrows(IllegalArgumentException.class, () -> new SafeBlobSized(null));
  }

  @Test
  void shouldThrowOnEmptyArrayWithNoConstraints() {
    // Given
    byte[] empty = {};

    // Then
    assertThrows(IllegalArgumentException.class, () ->
      new SafeBlobSized(empty)
    );
  }

  // ==================== MIN/MAX LENGTH TESTS ====================

  @Test
  void shouldCreateBlobWithinMinMaxRange() {
    // Given
    byte[] data = new byte[50];
    int minLength = 10;
    int maxLength = 100;

    // When
    SafeBlobSized blob = new SafeBlobSized(data, minLength, maxLength, 0);

    // Then
    assertEquals(50, blob.size());
    blob.close();
  }

  @Test
  void shouldCreateBlobAtMinimumLength() {
    // Given
    byte[] data = new byte[10];
    int minLength = 10;
    int maxLength = 100;

    // When
    SafeBlobSized blob = new SafeBlobSized(data, minLength, maxLength, 0);

    // Then
    assertEquals(10, blob.size());
    blob.close();
  }

  @Test
  void shouldCreateBlobAtMaximumLength() {
    // Given
    byte[] data = new byte[100];
    int minLength = 10;
    int maxLength = 100;

    // When
    SafeBlobSized blob = new SafeBlobSized(data, minLength, maxLength, 0);

    // Then
    assertEquals(100, blob.size());
    blob.close();
  }

  @Test
  void shouldThrowWhenBelowMinimumLength() {
    // Given
    byte[] data = new byte[5];
    int minLength = 10;
    int maxLength = 100;

    // When/Then
    IllegalArgumentException ex = assertThrows(
      IllegalArgumentException.class,
      () -> new SafeBlobSized(data, minLength, maxLength, 0)
    );
    assertTrue(ex.getMessage().contains("10"));
  }

  @Test
  void shouldThrowWhenAboveMaximumLength() {
    // Given
    byte[] data = new byte[150];
    int minLength = 10;
    int maxLength = 100;

    // When/Then
    IllegalArgumentException ex = assertThrows(
      IllegalArgumentException.class,
      () -> new SafeBlobSized(data, minLength, maxLength, 0)
    );
    assertTrue(ex.getMessage().contains("100"));
  }

  @Test
  void shouldAllowMinEqualsMax() {
    // Given
    byte[] data = new byte[32];
    int minLength = 32;
    int maxLength = 32;

    // When
    SafeBlobSized blob = new SafeBlobSized(data, minLength, maxLength, 0);

    // Then
    assertEquals(32, blob.size());
    blob.close();
  }

  @Test
  void shouldThrowWhenMinGreaterThanMax() {
    // Given
    byte[] data = new byte[50];
    int minLength = 100;
    int maxLength = 10;

    // When/Then
    assertThrows(AssertionError.class, () ->
      new SafeBlobSized(data, minLength, maxLength, 0)
    );
  }

  @Test
  void shouldThrowForNegativeMinLength() {
    // Given
    byte[] data = new byte[50];
    int minLength = -1;
    int maxLength = 100;

    // When/Then
    assertThrows(AssertionError.class, () ->
      new SafeBlobSized(data, minLength, maxLength, 0)
    );
  }

  @Test
  void shouldThrowForNegativeMaxLength() {
    // Given
    byte[] data = new byte[50];
    int minLength = 0;
    int maxLength = -1;

    // When/Then
    assertThrows(AssertionError.class, () ->
      new SafeBlobSized(data, minLength, maxLength, 0)
    );
  }

  // ==================== FIXED LENGTH TESTS ====================

  @Test
  void shouldCreateBlobWithExactFixedLength() {
    // Given
    byte[] data = new byte[32];
    int fixedLength = 32;

    // When
    SafeBlobSized blob = new SafeBlobSized(data, fixedLength);

    // Then
    assertEquals(32, blob.size());
    blob.close();
  }

  @Test
  void shouldThrowWhenNotExactFixedLength() {
    // Given
    byte[] data = new byte[30];
    int fixedLength = 32;

    // When/Then
    IllegalArgumentException ex = assertThrows(
      IllegalArgumentException.class,
      () -> new SafeBlobSized(data, fixedLength)
    );
    assertTrue(ex.getMessage().contains("32"));
  }

  @Test
  void shouldThrowWhenExceedsFixedLength() {
    // Given
    byte[] data = new byte[40];
    int fixedLength = 32;

    // When/Then
    IllegalArgumentException ex = assertThrows(
      IllegalArgumentException.class,
      () -> new SafeBlobSized(data, fixedLength)
    );
    assertTrue(ex.getMessage().contains("exactly"));
  }

  @Test
  void shouldThrowAsserionOnZeroFixedLength() {
    // Given
    byte[] data = new byte[1];
    int fixedLength = 0;

    // Then
    assertThrows(AssertionError.class, () ->
      new SafeBlobSized(data, fixedLength)
    );
  }

  // ==================== SINGLE CONSTRAINT (isMax) TESTS ====================

  @Test
  void shouldCreateBlobWithMaxConstraint() {
    // Given
    byte[] data = new byte[50];
    int maxLength = 100;
    boolean isMax = true;

    // When
    SafeBlobSized blob = new SafeBlobSized(data, maxLength, isMax);

    // Then
    assertEquals(50, blob.size());
    blob.close();
  }

  @Test
  void shouldCreateBlobAtMaxConstraintBoundary() {
    // Given
    byte[] data = new byte[100];
    int maxLength = 100;
    boolean isMax = true;

    // When
    SafeBlobSized blob = new SafeBlobSized(data, maxLength, isMax);

    // Then
    assertEquals(100, blob.size());
    blob.close();
  }

  @Test
  void shouldThrowWhenExceedsMaxConstraint() {
    // Given
    byte[] data = new byte[150];
    int maxLength = 100;
    boolean isMax = true;

    // When/Then
    IllegalArgumentException ex = assertThrows(
      IllegalArgumentException.class,
      () -> new SafeBlobSized(data, maxLength, isMax)
    );
    assertTrue(ex.getMessage().contains("at most"));
  }

  @Test
  void shouldThrowAssertOnZeroLengthWithMaxConstraint() {
    // Given
    byte[] data = new byte[0];
    int maxLength = 100;
    boolean isMax = true;

    // Then
    assertThrows(IllegalArgumentException.class, () ->
      new SafeBlobSized(data, maxLength, isMax)
    );
  }

  @Test
  void shouldCreateBlobWithMinConstraint() {
    // Given
    byte[] data = new byte[50];
    int minLength = 10;
    boolean isMax = false;

    // When
    SafeBlobSized blob = new SafeBlobSized(data, minLength, isMax);

    // Then
    assertEquals(50, blob.size());
    blob.close();
  }

  @Test
  void shouldCreateBlobAtMinConstraintBoundary() {
    // Given
    byte[] data = new byte[10];
    int minLength = 10;
    boolean isMax = false;

    // When
    SafeBlobSized blob = new SafeBlobSized(data, minLength, isMax);

    // Then
    assertEquals(10, blob.size());
    blob.close();
  }

  @Test
  void shouldThrowWhenBelowMinConstraint() {
    // Given
    byte[] data = new byte[5];
    int minLength = 10;
    boolean isMax = false;

    // When/Then
    IllegalArgumentException ex = assertThrows(
      IllegalArgumentException.class,
      () -> new SafeBlobSized(data, minLength, isMax)
    );
    assertTrue(ex.getMessage().contains("at least"));
  }

  @Test
  void shouldAllowLargeArrayWithMinConstraint() {
    // Given
    byte[] data = new byte[10000];
    int minLength = 10;
    boolean isMax = false;

    // When
    SafeBlobSized blob = new SafeBlobSized(data, minLength, isMax);

    // Then
    assertEquals(10000, blob.size());
    blob.close();
  }

  // ==================== POWER OF TWO TESTS ====================

  @Test
  void shouldCreateBlobWithPowerOfTwoLength() {
    // Given
    byte[] data = new byte[32]; // 2^5
    int minLength = 1;
    int maxLength = 1024;

    // When
    SafeBlobSized blob = new SafeBlobSized(data, minLength, maxLength, 2);

    // Then
    assertEquals(32, blob.size());
    blob.close();
  }

  @Test
  void shouldThrowForNonPowerOfTwoLength() {
    // Given
    byte[] data = new byte[30]; // Not a power of two
    int minLength = 1;
    int maxLength = 100;

    // When/Then
    IllegalArgumentException ex = assertThrows(
      IllegalArgumentException.class,
      () -> new SafeBlobSized(data, minLength, maxLength, 4)
    );
  }

  @Test
  void shouldAllowNonPowerOfTwoWhenNotRequired() {
    // Given
    byte[] data = new byte[30];
    int minLength = 1;
    int maxLength = 100;

    // When
    SafeBlobSized blob = new SafeBlobSized(data, minLength, maxLength, 0);

    // Then
    assertEquals(30, blob.size());
    blob.close();
  }

  @Test
  void shouldCombinePowerOfTwoWithMinMaxConstraints() {
    // Given - Power of two but below minimum
    byte[] data = new byte[8];
    int minLength = 16;
    int maxLength = 256;

    // When/Then
    assertThrows(IllegalArgumentException.class, () ->
      new SafeBlobSized(data, minLength, maxLength, 2)
    );
  }

  @Test
  void shouldCombinePowerOfTwoWithMaxConstraint() {
    // Given - Power of two but above maximum
    byte[] data = new byte[512];
    int minLength = 16;
    int maxLength = 256;

    // When/Then
    assertThrows(IllegalArgumentException.class, () ->
      new SafeBlobSized(data, minLength, maxLength, 2)
    );
  }

  // ==================== CRYPTOGRAPHIC SIZE TESTS ====================

  @Test
  void shouldAccept128BitKey() {
    // Given - 16 bytes = 128 bits
    byte[] key = new byte[16];

    // When
    SafeBlobSized blob = new SafeBlobSized(key, 16);

    // Then
    assertEquals(16, blob.size());
    blob.close();
  }

  @Test
  void shouldAccept256BitKey() {
    // Given - 32 bytes = 256 bits
    byte[] key = new byte[32];

    // When
    SafeBlobSized blob = new SafeBlobSized(key, 32);

    // Then
    assertEquals(32, blob.size());
    blob.close();
  }

  @Test
  void shouldAccept512BitKey() {
    // Given - 64 bytes = 512 bits
    byte[] key = new byte[64];

    // When
    SafeBlobSized blob = new SafeBlobSized(key, 64);

    // Then
    assertEquals(64, blob.size());
    blob.close();
  }

  @Test
  void shouldValidateNonceSize() {
    // Given - 12 bytes nonce (typical for GCM)
    byte[] nonce = new byte[12];

    // When
    SafeBlobSized blob = new SafeBlobSized(nonce, 12);

    // Then
    assertEquals(12, blob.size());
    blob.close();
  }

  @Test
  void shouldValidateSaltSize() {
    // Given - 16 bytes salt (typical for Argon2)
    byte[] salt = new byte[16];

    // When
    SafeBlobSized blob = new SafeBlobSized(salt, 16);

    // Then
    assertEquals(16, blob.size());
    blob.close();
  }

  // ==================== INHERITANCE BEHAVIOR TESTS ====================

  @Test
  void shouldInheritSafeBlobBehavior() {
    // Given
    byte[] data = { 0x01, 0x02, 0x03, 0x04 };

    // When
    SafeBlobSized blob = new SafeBlobSized(data, 4);

    // Then
    assertTrue(blob instanceof SafeBlob);
    blob.close();
  }

  @Test
  void shouldSealOriginalArrayWithConstraints() {
    // Given
    byte[] original = { 0x01, 0x02, 0x03, 0x04 };

    // When
    SafeBlobSized blob = new SafeBlobSized(original, 1, 10, 0);

    // Then
    assertArrayEquals(
      new byte[] { 0, 0, 0, 0 },
      original,
      "Original array should be sealed"
    );
    blob.close();
  }

  @Test
  void shouldWorkWithTryWithResources() {
    // Given
    byte[] data = new byte[32];

    // When/Then
    assertDoesNotThrow(() -> {
      try (SafeBlobSized blob = new SafeBlobSized(data, 32)) {
        assertEquals(32, blob.size());
      }
    });
  }

  @Test
  void shouldBeAutoCloseable() {
    // Given
    byte[] data = new byte[16];
    SafeBlobSized blob = new SafeBlobSized(data, 16);

    // Then
    assertTrue(blob instanceof AutoCloseable);
    blob.close();
  }

  // ==================== EDGE CASE TESTS ====================

  @Test
  void shouldHandleMaxIntegerMaxLength() {
    // Given
    byte[] data = new byte[100];
    int minLength = 1;
    int maxLength = Integer.MAX_VALUE;

    // When
    SafeBlobSized blob = new SafeBlobSized(data, minLength, maxLength, 0);

    // Then
    assertEquals(100, blob.size());
    blob.close();
  }

  @Test
  void shouldThrowAssertionOnZeroMinAndMaxLength() {
    // Given
    byte[] data = new byte[1];
    int minLength = 0;
    int maxLength = 0;

    // Then
    assertThrows(AssertionError.class, () ->
      new SafeBlobSized(data, minLength, maxLength, 0)
    );
  }

  @Test
  void shouldHandleSingleByteWithConstraints() {
    // Given
    byte[] data = { 0x42 };

    // When
    SafeBlobSized blob = new SafeBlobSized(data, 1, 100, 0);

    // Then
    assertEquals(1, blob.size());
    blob.close();
  }

  @Test
  void shouldHandleSingleByteFixedLength() {
    // Given
    byte[] data = { 0x42 };

    // When
    SafeBlobSized blob = new SafeBlobSized(data, 1);

    // Then
    assertEquals(1, blob.size());
    blob.close();
  }

  // ==================== VALIDATION ORDER TESTS ====================

  @Test
  void shouldValidateNullBeforeSizeConstraints() {
    // When/Then - null should be caught first
    assertThrows(IllegalArgumentException.class, () ->
      new SafeBlobSized(null, 10, 100, 0)
    );
  }

  @Test
  void shouldValidateRangeBeforeArraySize() {
    // Given - invalid range
    byte[] data = new byte[50];

    // When/Then - range validation should happen
    assertThrows(AssertionError.class, () ->
      new SafeBlobSized(data, 100, 10, 0)
    );
  }

  // ==================== RESOLVE AND CLOSE BEHAVIOR TESTS ====================

  @Test
  void shouldResolveAndMarkAsResolved() {
    // Given
    byte[] data = new byte[16];
    SafeBlobSized blob = new SafeBlobSized(data, 16);

    // When
    byte[] resolved = blob.resolve();

    // Then
    assertNotNull(resolved);
    assertEquals(16, resolved.length);
    assertTrue(blob.isResolved());
    blob.close();
  }

  @Test
  void shouldThrowOnDoubleResolve() {
    // Given
    byte[] data = new byte[16];
    SafeBlobSized blob = new SafeBlobSized(data, 16);

    // When
    blob.resolve();

    // Then
    assertThrows(IllegalStateException.class, () -> blob.resolve());
    blob.close();
  }

  @Test
  void shouldBeResolvedAfterClose() {
    // Given
    byte[] data = new byte[16];
    SafeBlobSized blob = new SafeBlobSized(data, 16);

    // When
    blob.close();

    // Then
    assertTrue(blob.isResolved());
  }
}
