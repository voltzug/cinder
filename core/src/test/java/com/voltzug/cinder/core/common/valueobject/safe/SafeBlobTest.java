package com.voltzug.cinder.core.common.valueobject.safe;

import static org.junit.jupiter.api.Assertions.*;

import com.voltzug.cinder.core.common.valueobject.safe.SafeBlob;
import com.voltzug.cinder.core.common.valueobject.safe.SafeString;

import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive tests for SafeBlob value object.
 * Tests secure blob handling, memory sealing, and Base64 operations.
 */
class SafeBlobTest {

  @Test
  void shouldCreateSafeBlobFromByteArray() {
    // Given
    byte[] data = { 0x01, 0x02, 0x03, 0x04, 0x05 };

    // When
    SafeBlob safeBlob = new SafeBlob(data);

    // Then
    assertNotNull(safeBlob);
    assertEquals(5, safeBlob.size());
  }

  @Test
  void shouldSealOriginalArrayAfterCreation() {
    // Given
    byte[] original = { 0x01, 0x02, 0x03, 0x04, 0x05 };

    // When
    SafeBlob safeBlob = new SafeBlob(original);

    // Then - Original array should be sealed (zeroed)
    assertArrayEquals(
      new byte[] { 0, 0, 0, 0, 0 },
      original,
      "Original array should be sealed after SafeBlob creation"
    );
    safeBlob.close();
  }

  @Test
  void shouldThrowExceptionForNullByteArray() {
    // When/Then
    assertThrows(IllegalArgumentException.class, () -> new SafeBlob(null));
  }

  @Test
  void shouldThrowExceptionForEmptyArray() {
    // Given
    byte[] empty = {};

    // When/Then
    assertThrows(IllegalArgumentException.class, () -> new SafeBlob(empty));
  }

  @Test
  void shouldHandleSingleByteArray() {
    // Given
    byte[] single = { 0x42 };

    // When
    SafeBlob safeBlob = new SafeBlob(single);

    // Then
    assertEquals(1, safeBlob.size());
    safeBlob.close();
  }

  @Test
  void shouldHandleLargeBlob() {
    // Given - 1MB blob
    byte[] large = new byte[1024 * 1024];
    for (int i = 0; i < large.length; i++) {
      large[i] = (byte) (i % 256);
    }

    // When
    SafeBlob safeBlob = new SafeBlob(large);

    // Then
    assertEquals(1024 * 1024, safeBlob.size());
    // Original should be sealed
    assertEquals(0, large[0]);
    assertEquals(0, large[1000]);
    safeBlob.close();
  }

  @Test
  void shouldReturnReadOnlyByteBufferView() {
    // Given
    byte[] data = { 0x10, 0x20, 0x30, 0x40 };
    SafeBlob safeBlob = new SafeBlob(data);

    // When
    ByteBuffer buffer = safeBlob.getBuffer();

    // Then
    assertNotNull(buffer);
    assertEquals(4, buffer.remaining());
    assertEquals(0x10, buffer.get(0));
    assertEquals(0x20, buffer.get(1));
    assertEquals(0x30, buffer.get(2));
    assertEquals(0x40, buffer.get(3));
    assertTrue(buffer.isReadOnly(), "Buffer should be read-only");

    // Attempt to modify should throw ReadOnlyBufferException
    assertThrows(ReadOnlyBufferException.class, () ->
      buffer.put(0, (byte) 0x55)
    );

    safeBlob.close();
  }

  @Test
  void shouldReflectUnderlyingBlobContentsInByteBuffer() {
    // Given
    byte[] data = { 0x01, 0x02, 0x03 };
    SafeBlob safeBlob = new SafeBlob(data);

    // When
    ByteBuffer buffer = safeBlob.getBuffer();

    // Then
    assertEquals(3, buffer.remaining());
    assertEquals(0x01, buffer.get(0));
    assertEquals(0x02, buffer.get(1));
    assertEquals(0x03, buffer.get(2));

    safeBlob.close();
  }

  @Test
  void shouldReturnIndependentReadOnlyBuffersOnEachCall() {
    // Given
    byte[] data = { 0x11, 0x22, 0x33 };
    SafeBlob safeBlob = new SafeBlob(data);

    // When
    ByteBuffer buffer1 = safeBlob.getBuffer();
    ByteBuffer buffer2 = safeBlob.getBuffer();

    // Then
    assertNotSame(buffer1, buffer2);
    assertEquals(buffer1, buffer2);
    assertTrue(buffer1.isReadOnly());
    assertTrue(buffer2.isReadOnly());

    safeBlob.close();
  }

  @Test
  void shouldReturnBufferWithCorrectPositionAndLimit() {
    // Given
    byte[] data = { 0x01, 0x02, 0x03, 0x04, 0x05 };
    SafeBlob safeBlob = new SafeBlob(data);

    // When
    ByteBuffer buffer = safeBlob.getBuffer();

    // Then
    assertEquals(0, buffer.position());
    assertEquals(5, buffer.limit());
    assertEquals(5, buffer.capacity());

    safeBlob.close();
  }

  @Test
  void shouldConvertToBase64() throws Exception {
    // Given
    byte[] data = { 0x01, 0x02, 0x03, 0x04, 0x05 };
    SafeBlob safeBlob = new SafeBlob(data.clone());

    // When
    SafeString base64 = safeBlob.toBase64();

    // Then
    assertNotNull(base64);
    assertTrue(base64.length() > 0);
    safeBlob.close();
  }

  @Test
  void shouldCreateFromBase64String() {
    // Given
    byte[] original = { 0x01, 0x02, 0x03, 0x04, 0x05 };
    String base64 = java.util.Base64.getEncoder().encodeToString(original);

    // When
    SafeBlob safeBlob = SafeBlob.fromBase64(base64);

    // Then
    assertNotNull(safeBlob);
    assertEquals(5, safeBlob.size());
  }

  @Test
  void shouldRoundTripThroughBase64() throws Exception {
    // Given
    byte[] original = new byte[32];
    for (int i = 0; i < 32; i++) {
      original[i] = (byte) (i * 3);
    }
    SafeBlob originalBlob = new SafeBlob(original);

    // When - Convert to Base64 and back
    try (SafeString base64 = originalBlob.toBase64()) {
      // Extract characters properly without using toString()
      StringBuilder sb = new StringBuilder(base64.length());
      for (int i = 0; i < base64.length(); i++) {
        sb.append(base64.charAt(i));
      }
      String base64String = sb.toString();
      SafeBlob decoded = SafeBlob.fromBase64(base64String);

      // Then
      assertEquals(originalBlob.size(), decoded.size());
      decoded.close();
    }
    originalBlob.close();
  }

  @Test
  void shouldThrowExceptionForNullBase64() {
    // When/Then
    assertThrows(IllegalArgumentException.class, () ->
      SafeBlob.fromBase64(null)
    );
  }

  @Test
  void shouldThrowExceptionForInvalidBase64() {
    // Given
    String invalidBase64 = "not-valid-base64!!!";

    // When/Then
    assertThrows(IllegalArgumentException.class, () ->
      SafeBlob.fromBase64(invalidBase64)
    );
  }

  @Test
  void shouldBeCloseable() throws Exception {
    // Given
    byte[] data = { 0x01, 0x02, 0x03 };
    SafeBlob safeBlob = new SafeBlob(data);

    // When/Then - Should not throw
    assertDoesNotThrow(() -> safeBlob.close());
  }

  @Test
  void shouldWorkWithTryWithResources() {
    // Given
    byte[] data = { 0x01, 0x02, 0x03, 0x04 };

    // When/Then - Should auto-close without exception
    assertDoesNotThrow(() -> {
      try (SafeBlob safeBlob = new SafeBlob(data)) {
        assertEquals(4, safeBlob.size());
      }
    });
  }

  @Test
  void shouldHandleConsecutiveCloseOperations() throws Exception {
    // Given
    byte[] data = { 0x01, 0x02, 0x03 };
    SafeBlob safeBlob = new SafeBlob(data);

    // When - Close multiple times
    safeBlob.close();
    safeBlob.close();
    safeBlob.close();

    // Then - Should not throw exception
    assertDoesNotThrow(() -> safeBlob.close());
  }

  @Test
  void shouldImplementAutoCloseable() {
    // Given
    byte[] data = { 0x01, 0x02, 0x03 };
    SafeBlob safeBlob = new SafeBlob(data);

    // Then
    assertTrue(safeBlob instanceof AutoCloseable);
    safeBlob.close();
  }

  @Test
  void shouldReturnCorrectSize() {
    // Given
    byte[] data = new byte[100];
    SafeBlob safeBlob = new SafeBlob(data);

    // When
    int size = safeBlob.size();

    // Then
    assertEquals(100, size);
    safeBlob.close();
  }

  @Test
  void shouldHandleAllZeroData() {
    // Given
    byte[] zeros = new byte[32];

    // When
    SafeBlob safeBlob = new SafeBlob(zeros);

    // Then
    assertEquals(32, safeBlob.size());
    safeBlob.close();
  }

  @Test
  void shouldHandleAllOnesData() {
    // Given
    byte[] ones = new byte[32];
    for (int i = 0; i < 32; i++) {
      ones[i] = (byte) 0xFF;
    }

    // When
    SafeBlob safeBlob = new SafeBlob(ones);

    // Then
    assertEquals(32, safeBlob.size());
    // Original should be sealed
    for (byte b : ones) {
      assertEquals(0, b);
    }
    safeBlob.close();
  }

  @RepeatedTest(9)
  void shouldHandleRandomData() {
    // Given
    byte[] random = new byte[64];
    for (int i = 0; i < 64; i++) {
      random[i] = (byte) (Math.random() * 256);
    }

    // When
    SafeBlob safeBlob = new SafeBlob(random);

    // Then
    assertEquals(64, safeBlob.size());
    // Original should be sealed
    for (byte b : random) {
      assertEquals(0, b);
    }
    safeBlob.close();
  }

  @Test
  void shouldConvertToBase64WithPadding() throws Exception {
    // Given - Data that requires padding
    byte[] data = new byte[31]; // Not divisible by 3
    SafeBlob safeBlob = new SafeBlob(data);

    // When
    SafeString base64 = safeBlob.toBase64();

    // Then
    assertNotNull(base64);
    assertTrue(base64.length() > 0);
    safeBlob.close();
  }

  @Test
  void shouldConvertToBase64WithoutPadding() throws Exception {
    // Given - Data divisible by 3 (no padding needed)
    byte[] data = new byte[30];
    SafeBlob safeBlob = new SafeBlob(data);

    // When
    SafeString base64 = safeBlob.toBase64();

    // Then
    assertNotNull(base64);
    assertTrue(base64.length() > 0);
    safeBlob.close();
  }

  @Test
  void shouldReturnSafeStringFromToBase64() throws Exception {
    // Given
    byte[] data = { 0x01, 0x02, 0x03 };
    SafeBlob safeBlob = new SafeBlob(data);

    // When
    Object result = safeBlob.toBase64();

    // Then
    assertTrue(result instanceof SafeString);
    safeBlob.close();
  }

  @Test
  void shouldPreventDataLeakageAfterClose() throws Exception {
    // Given
    byte[] sensitive = new byte[32];
    for (int i = 0; i < 32; i++) {
      sensitive[i] = (byte) i;
    }

    // When
    SafeBlob safeBlob = new SafeBlob(sensitive);
    safeBlob.close();

    // Then - Original is sealed
    for (byte b : sensitive) {
      assertEquals(0, b, "Sensitive data should be sealed");
    }
  }

  @Test
  void shouldHandleTypicalSaltSize() {
    // Given - 16 bytes (typical salt size)
    byte[] salt = new byte[16];
    for (int i = 0; i < 16; i++) {
      salt[i] = (byte) (i * 7);
    }

    // When
    SafeBlob safeBlob = new SafeBlob(salt);

    // Then
    assertEquals(16, safeBlob.size());
    safeBlob.close();
  }

  @Test
  void shouldHandleTypicalKeySize() {
    // Given - 32 bytes (typical key size)
    byte[] key = new byte[32];

    // When
    SafeBlob safeBlob = new SafeBlob(key);

    // Then
    assertEquals(32, safeBlob.size());
    safeBlob.close();
  }

  @RepeatedTest(9)
  void shouldVerifySecureLifecycle() throws Exception {
    // Given - Simulate S1 salt lifecycle
    byte[] s1 = new byte[16];
    for (int i = 0; i < 16; i++) {
      s1[i] = (byte) (0xAA + i);
    }

    // When - Create SafeBlob, use it, close it
    try (SafeBlob secureSalt = new SafeBlob(s1)) {
      assertEquals(16, secureSalt.size());

      // Original should be sealed immediately
      for (byte b : s1) {
        assertEquals(0, b, "S1 should be sealed in original array");
      }
    } // Auto-close seals internal buffer

    // Then - No memory leak
    assertNotNull(s1); // Array exists but is zeroed
  }

  @Test
  void shouldHandleNegativeBytes() {
    // Given - Bytes with negative values (as signed bytes)
    byte[] negatives = { -128, -64, -32, -16, -8, -4, -2, -1 };

    // When
    SafeBlob safeBlob = new SafeBlob(negatives);

    // Then
    assertEquals(8, safeBlob.size());
    // Original should be sealed
    for (byte b : negatives) {
      assertEquals(0, b);
    }
    safeBlob.close();
  }

  @Test
  void shouldMaintainSizeAfterClose() throws Exception {
    // Given
    byte[] data = new byte[50];
    SafeBlob safeBlob = new SafeBlob(data);

    // When
    safeBlob.close();

    // Then - Size should still be accessible
    assertEquals(50, safeBlob.size());
  }

  @Test
  void shouldHandleMultipleBase64Conversions() throws Exception {
    // Given
    byte[] data = { 0x01, 0x02, 0x03, 0x04 };
    SafeBlob safeBlob = new SafeBlob(data);

    // When - Convert multiple times
    SafeString base64_1 = safeBlob.toBase64();
    SafeString base64_2 = safeBlob.toBase64();

    // Then - Both should be valid
    assertNotNull(base64_1);
    assertNotNull(base64_2);
    safeBlob.close();
  }

  @RepeatedTest(9)
  void shouldPreventArrayReuseAttack() {
    // Given - Attacker tries to reuse array
    byte[] attackerData = { 0x01, 0x02, 0x03, 0x04 };

    // When - Create SafeBlob
    SafeBlob safeBlob = new SafeBlob(attackerData);

    // Then - Original array is sealed, attacker can't access data
    for (byte b : attackerData) {
      assertEquals(0, b, "Attacker's array should be zeroed");
    }
    safeBlob.close();
  }

  @Test
  void shouldReturnBytesOnResolveAndMarkAsResolved() {
    // Given
    byte[] data = { 0x01, 0x02, 0x03, 0x04 };
    SafeBlob safeBlob = new SafeBlob(data);

    // When
    byte[] resolved = safeBlob.resolve();

    // Then
    assertNotNull(resolved);
    assertEquals(4, resolved.length);
    assertTrue(safeBlob.isResolved());
    safeBlob.close();
  }

  @Test
  void shouldThrowOnGetBytesAfterResolve() {
    // Given
    byte[] data = { 0x01, 0x02, 0x03 };
    SafeBlob safeBlob = new SafeBlob(data);

    // When
    safeBlob.resolve();

    // Then
    assertThrows(IllegalStateException.class, () -> safeBlob.getBytes());
    safeBlob.close();
  }

  @Test
  void shouldThrowOnResolveTwice() {
    // Given
    byte[] data = { 0x01, 0x02, 0x03 };
    SafeBlob safeBlob = new SafeBlob(data);

    // When
    safeBlob.resolve();

    // Then
    assertThrows(IllegalStateException.class, () -> safeBlob.resolve());
    safeBlob.close();
  }

  @Test
  void shouldThrowOnGetBufferAfterResolve() {
    // Given
    byte[] data = { 0x01, 0x02, 0x03 };
    SafeBlob safeBlob = new SafeBlob(data);

    // When
    safeBlob.resolve();

    // Then
    assertThrows(IllegalStateException.class, () -> safeBlob.getBuffer());
    safeBlob.close();
  }

  @Test
  void shouldThrowOnToBase64AfterResolve() {
    // Given
    byte[] data = { 0x01, 0x02, 0x03 };
    SafeBlob safeBlob = new SafeBlob(data);

    // When
    safeBlob.resolve();

    // Then
    assertThrows(IllegalStateException.class, () -> safeBlob.toBase64());
    safeBlob.close();
  }

  @Test
  void shouldBeResolvedAfterClose() {
    // Given
    byte[] data = { 0x01, 0x02, 0x03 };
    SafeBlob safeBlob = new SafeBlob(data);

    // When
    safeBlob.close();

    // Then
    assertTrue(safeBlob.isResolved());
  }

  @Test
  void shouldThrowOnGetBytesAfterClose() {
    // Given
    byte[] data = { 0x01, 0x02, 0x03 };
    SafeBlob safeBlob = new SafeBlob(data);

    // When
    safeBlob.close();

    // Then
    assertThrows(IllegalStateException.class, () -> safeBlob.getBytes());
  }

  @Test
  void shouldThrowOnResolveAfterClose() {
    // Given
    byte[] data = { 0x01, 0x02, 0x03 };
    SafeBlob safeBlob = new SafeBlob(data);

    // When
    safeBlob.close();

    // Then
    assertThrows(IllegalStateException.class, () -> safeBlob.resolve());
  }
}
