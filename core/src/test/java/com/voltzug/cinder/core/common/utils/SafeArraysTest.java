package com.voltzug.cinder.core.common.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Comprehensive tests for SafeArrays utility class.
 * Focuses on memory sealing operations critical for zero-knowledge security.
 */
class SafeArraysTest {

  @Test
  void shouldSealByteArrayWithZeros() {
    // Given
    byte[] sensitiveData = { 0x01, 0x02, 0x03, 0x04, 0x05 };
    byte[] expected = { 0, 0, 0, 0, 0 };

    // When
    SafeArrays.seal(sensitiveData);

    // Then
    assertArrayEquals(
      expected,
      sensitiveData,
      "Byte array should be filled with zeros"
    );
  }

  @Test
  void shouldSealCharArrayWithX() {
    // Given
    char[] password = { 'p', 'a', 's', 's', 'w', 'o', 'r', 'd' };
    char[] expected = { 'x', 'x', 'x', 'x', 'x', 'x', 'x', 'x' };

    // When
    SafeArrays.seal(password);

    // Then
    assertArrayEquals(
      expected,
      password,
      "Char array should be filled with 'x'"
    );
  }

  @Test
  void shouldSealIntArrayWithZeros() {
    // Given
    int[] numbers = { 1, 2, 3, 4, 5 };
    int[] expected = { 0, 0, 0, 0, 0 };

    // When
    SafeArrays.seal(numbers);

    // Then
    assertArrayEquals(
      expected,
      numbers,
      "Int array should be filled with zeros"
    );
  }

  @Test
  void shouldHandleEmptyByteArray() {
    // Given
    byte[] empty = {};

    // When/Then - Should not throw exception
    assertDoesNotThrow(() -> SafeArrays.seal(empty));
  }

  @Test
  void shouldHandleEmptyCharArray() {
    // Given
    char[] empty = {};

    // When/Then - Should not throw exception
    assertDoesNotThrow(() -> SafeArrays.seal(empty));
  }

  @Test
  void shouldHandleEmptyIntArray() {
    // Given
    int[] empty = {};

    // When/Then - Should not throw exception
    assertDoesNotThrow(() -> SafeArrays.seal(empty));
  }

  @Test
  void shouldHandleSingleElementByteArray() {
    // Given
    byte[] single = { 0x42 };

    // When
    SafeArrays.seal(single);

    // Then
    assertEquals(0, single[0], "Single byte should be zeroed");
  }

  @Test
  void shouldHandleLargeByteArray() {
    // Given
    byte[] large = new byte[99999];
    for (int i = 0; i < large.length; i++) {
      large[i] = (byte) (i % 256);
    }

    // When
    SafeArrays.seal(large);

    // Then
    for (int i = 0; i < large.length; i++) {
      assertEquals(0, large[i], "All bytes should be zeroed at index " + i);
    }
  }

  @Test
  void shouldMoveByteArrayAndSealOriginal() {
    // Given
    byte[] original = { 0x01, 0x02, 0x03, 0x04, 0x05 };
    byte[] expectedCopy = { 0x01, 0x02, 0x03, 0x04, 0x05 };
    byte[] expectedSealed = { 0, 0, 0, 0, 0 };

    // When
    byte[] moved = SafeArrays.move(original);

    // Then
    assertArrayEquals(
      expectedCopy,
      moved,
      "Moved array should contain original data"
    );
    assertArrayEquals(
      expectedSealed,
      original,
      "Original array should be sealed (zeroed)"
    );
    assertNotSame(
      original,
      moved,
      "Moved array should be a different instance"
    );
  }

  @Test
  void shouldMoveCharArrayAndSealOriginal() {
    // Given
    char[] original = { 's', 'e', 'c', 'r', 'e', 't' };
    char[] expectedCopy = { 's', 'e', 'c', 'r', 'e', 't' };
    char[] expectedSealed = { 'x', 'x', 'x', 'x', 'x', 'x' };

    // When
    char[] moved = SafeArrays.move(original);

    // Then
    assertArrayEquals(
      expectedCopy,
      moved,
      "Moved array should contain original data"
    );
    assertArrayEquals(
      expectedSealed,
      original,
      "Original array should be sealed (filled with 'x')"
    );
    assertNotSame(
      original,
      moved,
      "Moved array should be a different instance"
    );
  }

  @Test
  void shouldMoveIntArrayAndSealOriginal() {
    // Given
    int[] original = { 100, 200, 300, 400, 500 };
    int[] expectedCopy = { 100, 200, 300, 400, 500 };
    int[] expectedSealed = { 0, 0, 0, 0, 0 };

    // When
    int[] moved = SafeArrays.move(original);

    // Then
    assertArrayEquals(
      expectedCopy,
      moved,
      "Moved array should contain original data"
    );
    assertArrayEquals(
      expectedSealed,
      original,
      "Original array should be sealed (zeroed)"
    );
    assertNotSame(
      original,
      moved,
      "Moved array should be a different instance"
    );
  }

  @Test
  void shouldMoveEmptyByteArray() {
    // Given
    byte[] empty = {};

    // When
    byte[] moved = SafeArrays.move(empty);

    // Then
    assertEquals(0, moved.length, "Moved array should be empty");
    assertNotSame(empty, moved, "Moved array should be a different instance");
  }

  @Test
  void shouldHandleConsecutiveSealOperations() {
    // Given
    byte[] data = { 0x01, 0x02, 0x03 };

    // When - Seal multiple times
    SafeArrays.seal(data);
    SafeArrays.seal(data);
    SafeArrays.seal(data);

    // Then - Should remain sealed
    assertArrayEquals(
      new byte[] { 0, 0, 0 },
      data,
      "Array should remain sealed"
    );
  }

  @Test
  void shouldHandleConsecutiveMoveOperations() {
    // Given
    byte[] original = { 0x01, 0x02, 0x03 };

    // When
    byte[] first = SafeArrays.move(original);
    byte[] second = SafeArrays.move(first);
    byte[] third = SafeArrays.move(second);

    // Then
    assertArrayEquals(
      new byte[] { 0x01, 0x02, 0x03 },
      third,
      "Final array should have original data"
    );
    assertArrayEquals(
      new byte[] { 0, 0, 0 },
      original,
      "Original should be sealed"
    );
    assertArrayEquals(
      new byte[] { 0, 0, 0 },
      first,
      "First move should be sealed"
    );
    assertArrayEquals(
      new byte[] { 0, 0, 0 },
      second,
      "Second move should be sealed"
    );
  }

  @Test
  void shouldSealByteArrayWithNegativeValues() {
    // Given
    byte[] data = { -128, -64, -32, -16, -8, -4, -2, -1 };

    // When
    SafeArrays.seal(data);

    // Then
    assertArrayEquals(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 }, data);
  }

  @Test
  void shouldSealIntArrayWithNegativeValues() {
    // Given
    int[] data = { -1000, -500, -100, -50, -10 };

    // When
    SafeArrays.seal(data);

    // Then
    assertArrayEquals(new int[] { 0, 0, 0, 0, 0 }, data);
  }

  @Test
  void shouldMaintainArrayLengthAfterSealing() {
    // Given
    byte[] data = new byte[100];
    int originalLength = data.length;

    // When
    SafeArrays.seal(data);

    // Then
    assertEquals(originalLength, data.length, "Array length should not change");
  }

  @Test
  void shouldMaintainArrayLengthAfterMoving() {
    // Given
    byte[] original = new byte[50];
    int originalLength = original.length;

    // When
    byte[] moved = SafeArrays.move(original);

    // Then
    assertEquals(
      originalLength,
      moved.length,
      "Moved array should have same length"
    );
    assertEquals(
      originalLength,
      original.length,
      "Original array length should not change"
    );
  }

  @Test
  void shouldHandleUnicodeCharactersInCharArray() {
    // Given
    char[] unicode = { 'α', 'β', 'γ', '中', '文', '日', '本' };

    // When
    SafeArrays.seal(unicode);

    // Then
    for (char c : unicode) {
      assertEquals('x', c, "Unicode char should be sealed with 'x'");
    }
  }

  @Test
  void shouldHandleMaxIntValue() {
    // Given
    int[] maxValues = {
      Integer.MAX_VALUE,
      Integer.MAX_VALUE,
      Integer.MAX_VALUE,
    };

    // When
    SafeArrays.seal(maxValues);

    // Then
    assertArrayEquals(new int[] { 0, 0, 0 }, maxValues);
  }

  @Test
  void shouldHandleMinIntValue() {
    // Given
    int[] minValues = {
      Integer.MIN_VALUE,
      Integer.MIN_VALUE,
      Integer.MIN_VALUE,
    };

    // When
    SafeArrays.seal(minValues);

    // Then
    assertArrayEquals(new int[] { 0, 0, 0 }, minValues);
  }

  @Test
  void shouldAssertArrayIsNotNull() {
    // When/Then
    assertThrows(IllegalArgumentException.class, () ->
      SafeArrays.assertIsArray(null)
    );
  }

  @Test
  void shouldAssertTargetIsArray() {
    // Given
    String notAnArray = "not an array";

    // When/Then
    assertThrows(IllegalArgumentException.class, () ->
      SafeArrays.assertIsArray(notAnArray)
    );
  }

  @Test
  void shouldAssertArrayNotEmpty() {
    // Given
    byte[] empty = {};

    // When/Then
    assertThrows(IllegalArgumentException.class, () ->
      SafeArrays.assertNotEmpty(empty)
    );
  }

  @Test
  void shouldPassAssertIsArrayForValidArray() {
    // Given
    byte[] validArray = { 1, 2, 3 };

    // When/Then - Should not throw
    assertDoesNotThrow(() -> SafeArrays.assertIsArray(validArray));
  }

  @Test
  void shouldPassAssertNotEmptyForValidArray() {
    // Given
    byte[] validArray = { 1, 2, 3 };

    // When/Then - Should not throw
    assertDoesNotThrow(() -> SafeArrays.assertNotEmpty(validArray));
  }

  @Test
  void shouldReturnTrueForEqualByteArrays() {
    // Given
    byte[] a = { 10, 20, 30, 40, 50 };
    byte[] b = { 10, 20, 30, 40, 50 };

    // When
    boolean result = SafeArrays.equals(a, b);

    // Then
    assertTrue(result, "equals should return true for identical arrays");
  }

  @Test
  void shouldReturnFalseForDifferentByteArrays() {
    // Given
    byte[] a = { 1, 2, 3, 4, 5 };
    byte[] b = { 1, 2, 3, 4, 6 };

    // When
    boolean result = SafeArrays.equals(a, b);

    // Then
    assertFalse(
      result,
      "equals should return false for arrays with different contents"
    );
  }

  @Test
  void shouldReturnFalseIfArraysAreOfDifferentLength() {
    // Given
    byte[] a = { 1, 2, 3 };
    byte[] b = { 1, 2, 3, 4 };

    // When/Then
    assertFalse(
      SafeArrays.equals(a, b),
      "equals should return false if arrays are of different length"
    );
  }

  @Test
  void shouldThrowIfEitherArrayIsNull() {
    // Given
    byte[] a = null;
    byte[] b = { 1, 2, 3 };

    // When/Then
    assertThrows(
      IllegalArgumentException.class,
      () -> SafeArrays.equals(a, b),
      "equals should throw if first array is null"
    );
    assertThrows(
      IllegalArgumentException.class,
      () -> SafeArrays.equals(b, null),
      "equals should throw if second array is null"
    );
  }

  @Test
  void shouldThrowIfEitherArrayIsEmpty() {
    // Given
    byte[] a = {};
    byte[] b = { 1, 2, 3 };

    // When/Then
    assertThrows(
      IllegalArgumentException.class,
      () -> SafeArrays.equals(a, b),
      "equals should throw if first array is empty"
    );
    assertThrows(
      IllegalArgumentException.class,
      () -> SafeArrays.equals(b, new byte[0]),
      "equals should throw if second array is empty"
    );
  }

  @Test
  void shouldReturnTrueForEqualArraysWithNegativeValues() {
    // Given
    byte[] a = { -1, -2, -3, -128, 0 };
    byte[] b = { -1, -2, -3, -128, 0 };

    // When
    boolean result = SafeArrays.equals(a, b);

    // Then
    assertTrue(
      result,
      "equals should return true for equal arrays with negative values"
    );
  }

  @Test
  void shouldReturnFalseForArraysDifferingOnlyInLastElement() {
    // Given
    byte[] a = { 1, 2, 3, 4, 5 };
    byte[] b = { 1, 2, 3, 4, 6 };

    // When
    boolean result = SafeArrays.equals(a, b);

    // Then
    assertFalse(
      result,
      "equals should return false if only last element differs"
    );
  }

  @Test
  void shouldHaveEqualsTimingForLargeArraysWithOneDifference() {
    // Given
    int size = 1_000_000;
    byte[] a = new byte[size];
    byte[] a2 = new byte[size];
    byte[] b = new byte[size];
    byte[] c = new byte[size];
    for (int i = 0; i < size; i++) {
      a[i] = (byte) (i % 256);
      a2[i] = (byte) (i % 256);
      b[i] = (byte) (i % 256);
      c[i] = (byte) (i % 256);
    }
    b[0] = (byte) (b[0] + 1);
    c[size - 1] = (byte) (c[size - 1] + 1);

    // warmup loops
    for (int i = 0; i < 999; i++) {
      SafeArrays.equals(a, a2);
      SafeArrays.equals(a, b);
      SafeArrays.equals(a2, c);
    }

    // When
    long startAA = System.nanoTime();
    boolean resultAA = SafeArrays.equals(a, a2);
    long endAA = System.nanoTime();
    long nanoAA = endAA - startAA;

    long startAB = System.nanoTime();
    boolean resultAB = SafeArrays.equals(a, b);
    long endAB = System.nanoTime();
    long nanoAB = endAB - startAB;

    long startAC = System.nanoTime();
    boolean resultAC = SafeArrays.equals(a2, c);
    long endAC = System.nanoTime();
    long nanoAC = endAC - startAC;

    // Then
    assertTrue(resultAA, "equals should return true for identical arrays");
    assertFalse(
      resultAB,
      "equals should return false for arrays differing in first element"
    );
    assertFalse(
      resultAC,
      "equals should return false for arrays differing in last element"
    );

    double maxMs = Math.max(nanoAA, Math.max(nanoAB, nanoAC)) / 1_000_000.0;
    double minMs = Math.min(nanoAA, Math.min(nanoAB, nanoAC)) / 1_000_000.0;
    double ratio = maxMs / minMs;

    // Accept only a small timing difference (e.g., within 1.2x)
    assertTrue(
      ratio < 1.2,
      "Timing difference between a-b and a-c is NOT timing safe. " +
        "Expected ratio < 1.2, got: " +
        String.format(
          "nanoAA=%.3fms, nanoAB=%.3fms, nanoAC=%.3fms (ratio=%.2f)",
          nanoAA / 1_000_000.0,
          nanoAB / 1_000_000.0,
          nanoAC / 1_000_000.0,
          ratio
        )
    );
  }
}
