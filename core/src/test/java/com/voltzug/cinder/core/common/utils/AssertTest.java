package com.voltzug.cinder.core.common.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Comprehensive tests for Assert utility class.
 * Focuses on range validation for int, long, float, and double types.
 */
class AssertTest {

  // ==================== TRULY TESTS ====================

  @Test
  void shouldNotThrowWhenTrulyConditionIsTrue() {
    assertDoesNotThrow(() -> Assert.truly(1 + 1 == 2, "Math is broken"));
    assertDoesNotThrow(() -> Assert.truly(true, "Should not throw for true"));
  }

  @Test
  void shouldThrowAssertionErrorWhenTrulyConditionIsFalse() {
    AssertionError error = assertThrows(AssertionError.class, () ->
      Assert.truly(false, "Expected failure")
    );
    assertEquals("Expected failure", error.getMessage());
  }

  @Test
  void shouldThrowAssertionErrorWithCustomMessage() {
    String msg = "Custom assertion message";
    AssertionError error = assertThrows(AssertionError.class, () ->
      Assert.truly(2 > 3, msg)
    );
    assertEquals(msg, error.getMessage());
  }

  // ==================== INT RANGE TESTS ====================

  @Test
  void shouldPassValidIntRange() {
    int min = 0;
    int max = 100;
    assertDoesNotThrow(() -> Assert.range(min, max));
  }

  @Test
  void shouldPassIntRangeWhenMinEqualsMax() {
    int min = 50;
    int max = 50;
    assertDoesNotThrow(() -> Assert.range(min, max));
  }

  @Test
  void shouldPassIntRangeWithZeroMinAndMax() {
    int min = 0;
    int max = 0;
    assertDoesNotThrow(() -> Assert.range(min, max));
  }

  @Test
  void shouldPassIntRangeWithMaxIntValue() {
    int min = 0;
    int max = Integer.MAX_VALUE;
    assertDoesNotThrow(() -> Assert.range(min, max));
  }

  @Test
  void shouldThrowForNegativeIntMin() {
    int min = -1;
    int max = 100;

    // When/Then
    AssertionError ex = assertThrows(AssertionError.class, () ->
      Assert.range(min, max)
    );
    assertTrue(ex.getMessage().contains("min must be >= 0"));
  }

  @Test
  void shouldThrowForNegativeIntMax() {
    // Given
    int min = 0;
    int max = -1;

    // When/Then
    AssertionError ex = assertThrows(AssertionError.class, () ->
      Assert.range(min, max)
    );
    assertTrue(ex.getMessage().contains("max must be >= 0"));
  }

  @Test
  void shouldThrowWhenIntMaxLessThanMin() {
    // Given
    int min = 100;
    int max = 50;

    // When/Then
    AssertionError ex = assertThrows(AssertionError.class, () ->
      Assert.range(min, max)
    );
    assertTrue(ex.getMessage().contains("max"));
    assertTrue(ex.getMessage().contains("must be >= min"));
  }

  @Test
  void shouldThrowForBothNegativeIntValues() {
    // Given
    int min = -10;
    int max = -5;

    // When/Then
    AssertionError ex = assertThrows(AssertionError.class, () ->
      Assert.range(min, max)
    );
    assertTrue(ex.getMessage().contains("min must be >= 0"));
  }

  // ==================== LONG RANGE TESTS ====================

  @Test
  void shouldPassValidLongRange() {
    // Given
    long min = 0L;
    long max = 1000000000000L;

    // When/Then
    assertDoesNotThrow(() -> Assert.range(min, max));
  }

  @Test
  void shouldPassLongRangeWhenMinEqualsMax() {
    // Given
    long min = 999999999999L;
    long max = 999999999999L;

    // When/Then
    assertDoesNotThrow(() -> Assert.range(min, max));
  }

  @Test
  void shouldPassLongRangeWithZeroMinAndMax() {
    // Given
    long min = 0L;
    long max = 0L;

    // When/Then
    assertDoesNotThrow(() -> Assert.range(min, max));
  }

  @Test
  void shouldPassLongRangeWithMaxLongValue() {
    // Given
    long min = 0L;
    long max = Long.MAX_VALUE;

    // When/Then
    assertDoesNotThrow(() -> Assert.range(min, max));
  }

  @Test
  void shouldThrowForNegativeLongMin() {
    // Given
    long min = -1L;
    long max = 100L;

    // When/Then
    AssertionError ex = assertThrows(AssertionError.class, () ->
      Assert.range(min, max)
    );
    assertTrue(ex.getMessage().contains("min must be >= 0"));
  }

  @Test
  void shouldThrowForNegativeLongMax() {
    // Given
    long min = 0L;
    long max = -1L;

    // When/Then
    AssertionError ex = assertThrows(AssertionError.class, () ->
      Assert.range(min, max)
    );
    assertTrue(ex.getMessage().contains("max must be >= 0"));
  }

  @Test
  void shouldThrowWhenLongMaxLessThanMin() {
    // Given
    long min = 1000L;
    long max = 500L;

    // When/Then
    AssertionError ex = assertThrows(AssertionError.class, () ->
      Assert.range(min, max)
    );
    assertTrue(ex.getMessage().contains("max"));
    assertTrue(ex.getMessage().contains("must be >= min"));
  }

  @Test
  void shouldThrowForBothNegativeLongValues() {
    // Given
    long min = -100L;
    long max = -50L;

    // When/Then
    AssertionError ex = assertThrows(AssertionError.class, () ->
      Assert.range(min, max)
    );
    assertTrue(ex.getMessage().contains("min must be >= 0"));
  }

  // ==================== FLOAT RANGE TESTS ====================

  @Test
  void shouldPassValidFloatRange() {
    // Given
    float min = 0.0f;
    float max = 100.5f;

    // When/Then
    assertDoesNotThrow(() -> Assert.range(min, max));
  }

  @Test
  void shouldPassFloatRangeWhenMinEqualsMax() {
    // Given
    float min = 50.5f;
    float max = 50.5f;

    // When/Then
    assertDoesNotThrow(() -> Assert.range(min, max));
  }

  @Test
  void shouldPassFloatRangeWithZeroMinAndMax() {
    // Given
    float min = 0.0f;
    float max = 0.0f;

    // When/Then
    assertDoesNotThrow(() -> Assert.range(min, max));
  }

  @Test
  void shouldPassFloatRangeWithMaxFloatValue() {
    // Given
    float min = 0.0f;
    float max = Float.MAX_VALUE;

    // When/Then
    assertDoesNotThrow(() -> Assert.range(min, max));
  }

  @Test
  void shouldPassFloatRangeWithSmallPositiveValues() {
    // Given
    float min = 0.0001f;
    float max = 0.0002f;

    // When/Then
    assertDoesNotThrow(() -> Assert.range(min, max));
  }

  @Test
  void shouldThrowForNegativeFloatMin() {
    // Given
    float min = -0.1f;
    float max = 100.0f;

    // When/Then
    AssertionError ex = assertThrows(AssertionError.class, () ->
      Assert.range(min, max)
    );
    assertTrue(ex.getMessage().contains("min must be >= 0.0"));
  }

  @Test
  void shouldThrowForNegativeFloatMax() {
    // Given
    float min = 0.0f;
    float max = -0.1f;

    // When/Then
    AssertionError ex = assertThrows(AssertionError.class, () ->
      Assert.range(min, max)
    );
    assertTrue(ex.getMessage().contains("max must be >= 0.0"));
  }

  @Test
  void shouldThrowWhenFloatMaxLessThanMin() {
    // Given
    float min = 100.0f;
    float max = 50.0f;

    // When/Then
    AssertionError ex = assertThrows(AssertionError.class, () ->
      Assert.range(min, max)
    );
    assertTrue(ex.getMessage().contains("max"));
    assertTrue(ex.getMessage().contains("must be >= min"));
  }

  @Test
  void shouldThrowForBothNegativeFloatValues() {
    // Given
    float min = -10.0f;
    float max = -5.0f;

    // When/Then
    AssertionError ex = assertThrows(AssertionError.class, () ->
      Assert.range(min, max)
    );
    assertTrue(ex.getMessage().contains("min must be >= 0.0"));
  }

  // ==================== DOUBLE RANGE TESTS ====================

  @Test
  void shouldPassValidDoubleRange() {
    // Given
    double min = 0.0;
    double max = 100.5;

    // When/Then
    assertDoesNotThrow(() -> Assert.range(min, max));
  }

  @Test
  void shouldPassDoubleRangeWhenMinEqualsMax() {
    // Given
    double min = 50.5;
    double max = 50.5;

    // When/Then
    assertDoesNotThrow(() -> Assert.range(min, max));
  }

  @Test
  void shouldPassDoubleRangeWithZeroMinAndMax() {
    // Given
    double min = 0.0;
    double max = 0.0;

    // When/Then
    assertDoesNotThrow(() -> Assert.range(min, max));
  }

  @Test
  void shouldPassDoubleRangeWithMaxDoubleValue() {
    // Given
    double min = 0.0;
    double max = Double.MAX_VALUE;

    // When/Then
    assertDoesNotThrow(() -> Assert.range(min, max));
  }

  @Test
  void shouldPassDoubleRangeWithSmallPositiveValues() {
    // Given
    double min = 0.00000001;
    double max = 0.00000002;

    // When/Then
    assertDoesNotThrow(() -> Assert.range(min, max));
  }

  @Test
  void shouldThrowForNegativeDoubleMin() {
    // Given
    double min = -0.1;
    double max = 100.0;

    // When/Then
    AssertionError ex = assertThrows(AssertionError.class, () ->
      Assert.range(min, max)
    );
    assertTrue(ex.getMessage().contains("min must be >= 0.0"));
  }

  @Test
  void shouldThrowForNegativeDoubleMax() {
    // Given
    double min = 0.0;
    double max = -0.1;

    // When/Then
    AssertionError ex = assertThrows(AssertionError.class, () ->
      Assert.range(min, max)
    );
    assertTrue(ex.getMessage().contains("max must be >= 0.0"));
  }

  @Test
  void shouldThrowWhenDoubleMaxLessThanMin() {
    // Given
    double min = 100.0;
    double max = 50.0;

    // When/Then
    AssertionError ex = assertThrows(AssertionError.class, () ->
      Assert.range(min, max)
    );
    assertTrue(ex.getMessage().contains("max"));
    assertTrue(ex.getMessage().contains("must be >= min"));
  }

  @Test
  void shouldThrowForBothNegativeDoubleValues() {
    // Given
    double min = -10.0;
    double max = -5.0;

    // When/Then
    AssertionError ex = assertThrows(AssertionError.class, () ->
      Assert.range(min, max)
    );
    assertTrue(ex.getMessage().contains("min must be >= 0.0"));
  }

  @Test
  void shouldHandleDoubleWithVeryHighPrecision() {
    // Given
    double min = 0.123456789012345;
    double max = 0.123456789012346;

    // When/Then
    assertDoesNotThrow(() -> Assert.range(min, max));
  }

  // ==================== EDGE CASE TESTS ====================

  @Test
  void shouldHandleIntMinValueAsNegative() {
    // Given
    int min = Integer.MIN_VALUE;
    int max = 0;

    // When/Then
    AssertionError ex = assertThrows(AssertionError.class, () ->
      Assert.range(min, max)
    );
    assertTrue(ex.getMessage().contains("min must be >= 0"));
  }

  @Test
  void shouldHandleLongMinValueAsNegative() {
    // Given
    long min = Long.MIN_VALUE;
    long max = 0L;

    // When/Then
    AssertionError ex = assertThrows(AssertionError.class, () ->
      Assert.range(min, max)
    );
    assertTrue(ex.getMessage().contains("min must be >= 0"));
  }

  @Test
  void shouldPassLargeIntRange() {
    // Given
    int min = 0;
    int max = Integer.MAX_VALUE - 1;

    // When/Then
    assertDoesNotThrow(() -> Assert.range(min, max));
  }

  @Test
  void shouldPassLargeLongRange() {
    // Given
    long min = 0L;
    long max = Long.MAX_VALUE - 1;

    // When/Then
    assertDoesNotThrow(() -> Assert.range(min, max));
  }
}
