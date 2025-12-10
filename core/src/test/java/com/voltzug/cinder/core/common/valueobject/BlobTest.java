package com.voltzug.cinder.core.common.valueobject;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.ByteBuffer;
import java.util.Base64;

import org.junit.jupiter.api.Test;

/**
 * Comprehensive tests for Blob value object.
 * Focuses on immutable binary data handling for encrypted file contents.
 */
class BlobTest {

  // ==================== CONSTRUCTION TESTS ====================

  @Test
  void shouldCreateBlobFromByteArray() {
    // Given
    byte[] data = { 0x01, 0x02, 0x03, 0x04, 0x05 };

    // When
    Blob blob = new Blob(data);

    // Then
    assertEquals(5, blob.size());
  }

  @Test
  void shouldCreateBlobFromSingleByte() {
    // Given
    byte[] data = { 0x42 };

    // When
    Blob blob = new Blob(data);

    // Then
    assertEquals(1, blob.size());
  }

  @Test
  void shouldThrowForNullByteArray() {
    // When/Then
    assertThrows(
      IllegalArgumentException.class,
      () -> new Blob(null)
    );
  }

  @Test
  void shouldThrowForEmptyByteArray() {
    // Given
    byte[] empty = {};

    // When/Then
    assertThrows(
      IllegalArgumentException.class,
      () -> new Blob(empty)
    );
  }

  @Test
  void shouldHandleLargeByteArray() {
    // Given
    byte[] large = new byte[100000];
    for (int i = 0; i < large.length; i++) {
      large[i] = (byte) (i % 256);
    }

    // When
    Blob blob = new Blob(large);

    // Then
    assertEquals(100000, blob.size());
  }

  // ==================== BUFFER TESTS ====================

  @Test
  void shouldReturnReadOnlyByteBuffer() {
    // Given
    byte[] data = { 0x01, 0x02, 0x03, 0x04, 0x05 };
    Blob blob = new Blob(data);

    // When
    ByteBuffer buffer = blob.getBuffer();

    // Then
    assertTrue(buffer.isReadOnly());
    assertEquals(5, buffer.remaining());
  }

  @Test
  void shouldReturnCorrectBufferContent() {
    // Given
    byte[] data = { 0x10, 0x20, 0x30 };
    Blob blob = new Blob(data);

    // When
    ByteBuffer buffer = blob.getBuffer();

    // Then
    assertEquals(0x10, buffer.get(0));
    assertEquals(0x20, buffer.get(1));
    assertEquals(0x30, buffer.get(2));
  }

  @Test
  void shouldReturnIndependentBufferInstances() {
    // Given
    byte[] data = { 0x01, 0x02, 0x03 };
    Blob blob = new Blob(data);

    // When
    ByteBuffer buffer1 = blob.getBuffer();
    ByteBuffer buffer2 = blob.getBuffer();

    // Then
    assertNotSame(buffer1, buffer2);
  }

  @Test
  void shouldPreventBufferModification() {
    // Given
    byte[] data = { 0x01, 0x02, 0x03 };
    Blob blob = new Blob(data);
    ByteBuffer buffer = blob.getBuffer();

    // When/Then
    assertThrows(
      Exception.class,
      () -> buffer.put((byte) 0xFF)
    );
  }

  // ==================== BASE64 ENCODING/DECODING TESTS ====================

  @Test
  void shouldEncodeToBase64() {
    // Given
    byte[] data = { 0x48, 0x65, 0x6C, 0x6C, 0x6F }; // "Hello" in ASCII
    Blob blob = new Blob(data);

    // When
    CharSequence base64 = blob.toBase64();

    // Then
    assertEquals("SGVsbG8=", base64.toString());
  }

  @Test
  void shouldDecodeFromBase64() {
    // Given
    String base64 = "SGVsbG8="; // "Hello" encoded

    // When
    Blob blob = Blob.fromBase64(base64);

    // Then
    assertEquals(5, blob.size());
    ByteBuffer buffer = blob.getBuffer();
    assertEquals(0x48, buffer.get(0)); // 'H'
    assertEquals(0x65, buffer.get(1)); // 'e'
    assertEquals(0x6C, buffer.get(2)); // 'l'
    assertEquals(0x6C, buffer.get(3)); // 'l'
    assertEquals(0x6F, buffer.get(4)); // 'o'
  }

  @Test
  void shouldThrowForNullBase64String() {
    // When/Then
    assertThrows(
      IllegalArgumentException.class,
      () -> Blob.fromBase64(null)
    );
  }

  @Test
  void shouldThrowForInvalidBase64String() {
    // Given
    String invalidBase64 = "!!!invalid!!!";

    // When/Then
    assertThrows(
      IllegalArgumentException.class,
      () -> Blob.fromBase64(invalidBase64)
    );
  }

  @Test
  void shouldRoundTripBase64Encoding() {
    // Given
    byte[] originalData = { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08 };
    Blob originalBlob = new Blob(originalData);

    // When
    CharSequence base64 = originalBlob.toBase64();
    Blob decodedBlob = Blob.fromBase64(base64.toString());

    // Then
    assertEquals(originalBlob.size(), decodedBlob.size());
    ByteBuffer originalBuffer = originalBlob.getBuffer();
    ByteBuffer decodedBuffer = decodedBlob.getBuffer();
    for (int i = 0; i < originalData.length; i++) {
      assertEquals(originalBuffer.get(i), decodedBuffer.get(i));
    }
  }

  @Test
  void shouldHandleBase64WithPadding() {
    // Given - different padding scenarios
    byte[] oneByteData = { 0x01 };
    byte[] twoBytesData = { 0x01, 0x02 };
    byte[] threeBytesData = { 0x01, 0x02, 0x03 };

    // When
    Blob blob1 = new Blob(oneByteData);
    Blob blob2 = new Blob(twoBytesData);
    Blob blob3 = new Blob(threeBytesData);

    // Then - verify round-trip works with different padding
    assertEquals(blob1.size(), Blob.fromBase64(blob1.toBase64().toString()).size());
    assertEquals(blob2.size(), Blob.fromBase64(blob2.toBase64().toString()).size());
    assertEquals(blob3.size(), Blob.fromBase64(blob3.toBase64().toString()).size());
  }

  @Test
  void shouldHandleLargeBase64EncodeDecode() {
    // Given
    byte[] largeData = new byte[10000];
    for (int i = 0; i < largeData.length; i++) {
      largeData[i] = (byte) (i % 256);
    }
    Blob originalBlob = new Blob(largeData);

    // When
    CharSequence base64 = originalBlob.toBase64();
    Blob decodedBlob = Blob.fromBase64(base64.toString());

    // Then
    assertEquals(originalBlob.size(), decodedBlob.size());
  }

  // ==================== SIZE TESTS ====================

  @Test
  void shouldReturnCorrectSize() {
    // Given
    byte[] data = new byte[256];

    // When
    Blob blob = new Blob(data);

    // Then
    assertEquals(256, blob.size());
  }

  @Test
  void shouldReturnSizeOfOne() {
    // Given
    byte[] data = { 0x00 };

    // When
    Blob blob = new Blob(data);

    // Then
    assertEquals(1, blob.size());
  }

  // ==================== BINARY DATA TESTS ====================

  @Test
  void shouldHandleAllByteValues() {
    // Given - array with all possible byte values
    byte[] allBytes = new byte[256];
    for (int i = 0; i < 256; i++) {
      allBytes[i] = (byte) i;
    }

    // When
    Blob blob = new Blob(allBytes);

    // Then
    assertEquals(256, blob.size());
    ByteBuffer buffer = blob.getBuffer();
    for (int i = 0; i < 256; i++) {
      assertEquals((byte) i, buffer.get(i));
    }
  }

  @Test
  void shouldHandleNegativeByteValues() {
    // Given
    byte[] negatives = { -128, -64, -32, -16, -8, -4, -2, -1 };

    // When
    Blob blob = new Blob(negatives);

    // Then
    assertEquals(8, blob.size());
    ByteBuffer buffer = blob.getBuffer();
    assertEquals(-128, buffer.get(0));
    assertEquals(-1, buffer.get(7));
  }

  @Test
  void shouldHandleZeroFilledArray() {
    // Given
    byte[] zeros = new byte[100];

    // When
    Blob blob = new Blob(zeros);

    // Then
    assertEquals(100, blob.size());
    ByteBuffer buffer = blob.getBuffer();
    for (int i = 0; i < 100; i++) {
      assertEquals(0, buffer.get(i));
    }
  }

  @Test
  void shouldHandleMaxValueFilledArray() {
    // Given
    byte[] maxValues = new byte[50];
    for (int i = 0; i < maxValues.length; i++) {
      maxValues[i] = Byte.MAX_VALUE;
    }

    // When
    Blob blob = new Blob(maxValues);

    // Then
    assertEquals(50, blob.size());
    ByteBuffer buffer = blob.getBuffer();
    for (int i = 0; i < 50; i++) {
      assertEquals(Byte.MAX_VALUE, buffer.get(i));
    }
  }

  @Test
  void shouldHandleMinValueFilledArray() {
    // Given
    byte[] minValues = new byte[50];
    for (int i = 0; i < minValues.length; i++) {
      minValues[i] = Byte.MIN_VALUE;
    }

    // When
    Blob blob = new Blob(minValues);

    // Then
    assertEquals(50, blob.size());
    ByteBuffer buffer = blob.getBuffer();
    for (int i = 0; i < 50; i++) {
      assertEquals(Byte.MIN_VALUE, buffer.get(i));
    }
  }

  // ==================== REFERENCE BEHAVIOR TESTS ====================

  @Test
  void shouldShareReferenceWithOriginalArray() {
    // Given
    byte[] data = { 0x01, 0x02, 0x03 };
    Blob blob = new Blob(data);

    // When - modify original array
    data[0] = (byte) 0xFF;

    // Then - blob reflects change (shares reference)
    ByteBuffer buffer = blob.getBuffer();
    assertEquals((byte) 0xFF, buffer.get(0));
  }

  // ==================== SPECIAL CONTENT TESTS ====================

  @Test
  void shouldHandleRandomLikeData() {
    // Given - simulated random data pattern
    byte[] random = {
      0x3A, (byte) 0xF2, 0x1B, (byte) 0xC4, 0x7E, (byte) 0x9D, 0x05, (byte) 0xAB,
      (byte) 0xE8, 0x42, (byte) 0xD1, 0x6F, (byte) 0x83, 0x29, (byte) 0xBA, 0x54
    };

    // When
    Blob blob = new Blob(random);

    // Then
    assertEquals(16, blob.size());
  }

  @Test
  void shouldHandleEncryptedDataPattern() {
    // Given - typical encrypted data (appears random)
    byte[] encrypted = new byte[32];
    for (int i = 0; i < encrypted.length; i++) {
      encrypted[i] = (byte) ((i * 17 + 31) % 256);
    }

    // When
    Blob blob = new Blob(encrypted);

    // Then
    assertEquals(32, blob.size());
    CharSequence base64 = blob.toBase64();
    assertNotNull(base64);
    assertTrue(base64.length() > 0);
  }
}