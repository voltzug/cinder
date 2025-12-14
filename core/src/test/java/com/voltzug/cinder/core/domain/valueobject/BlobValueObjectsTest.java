package com.voltzug.cinder.core.domain.valueobject;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Comprehensive tests for Envelope and SealedBlob value objects.
 * Tests creation, validation, structure extraction, and secure memory handling.
 */
class BlobValueObjectsTest {

  // ==================== ENVELOPE CONSTRUCTION TESTS ====================

  @Test
  void shouldCreateEnvelopeWithValidData() {
    // Given
    byte[] envelopeData = new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05 };

    // When
    Envelope envelope = new Envelope(envelopeData);

    // Then
    assertNotNull(envelope);
    assertEquals(5, envelope.size());
  }

  @Test
  void shouldCreateEnvelopeWithLargeData() {
    // Given - Large encrypted envelope
    byte[] largeData = new byte[1024];
    for (int i = 0; i < largeData.length; i++) {
      largeData[i] = (byte) (i % 256);
    }

    // When
    Envelope envelope = new Envelope(largeData);

    // Then
    assertEquals(1024, envelope.size());
    envelope.close();
  }

  @Test
  void shouldCreateEnvelopeWithSingleByte() {
    // Given
    byte[] singleByte = new byte[] { 0x42 };

    // When
    Envelope envelope = new Envelope(singleByte);

    // Then
    assertEquals(1, envelope.size());
    envelope.close();
  }

  // ==================== ENVELOPE VALIDATION TESTS ====================

  @Test
  void shouldThrowExceptionForNullEnvelope() {
    // When/Then
    assertThrows(IllegalArgumentException.class, () -> new Envelope(null));
  }

  @Test
  void shouldThrowExceptionForEmptyEnvelope() {
    // Given
    byte[] empty = new byte[0];

    // When/Then
    assertThrows(IllegalArgumentException.class, () -> new Envelope(empty));
  }

  // ==================== ENVELOPE SECURE MEMORY TESTS ====================

  @Test
  void shouldSealOriginalEnvelopeArrayAfterCreation() {
    // Given
    byte[] original = new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05 };

    // When
    Envelope envelope = new Envelope(original);

    // Then - Original array should be sealed (zeroed)
    for (byte b : original) {
      assertEquals(
        0,
        b,
        "Original array should be sealed after Envelope creation"
      );
    }
    envelope.close();
  }

  @Test
  void shouldEnvelopeBeCloseable() {
    // Given
    byte[] data = new byte[] { 0x01, 0x02, 0x03 };
    Envelope envelope = new Envelope(data);

    // When/Then - Should not throw
    assertDoesNotThrow(() -> envelope.close());
  }

  @Test
  void shouldEnvelopeWorkWithTryWithResources() {
    // Given
    byte[] data = new byte[] { 0x01, 0x02, 0x03, 0x04 };

    // When/Then - Should auto-close without exception
    assertDoesNotThrow(() -> {
      try (Envelope envelope = new Envelope(data)) {
        assertEquals(4, envelope.size());
      }
    });
  }

  @Test
  void shouldEnvelopeImplementAutoCloseable() {
    // Given
    byte[] data = new byte[] { 0x01, 0x02, 0x03 };
    Envelope envelope = new Envelope(data);

    // Then
    assertTrue(envelope instanceof AutoCloseable);
    envelope.close();
  }

  @Test
  void shouldEnvelopeThrowOnGetBytesAfterClose() {
    // Given
    byte[] data = new byte[] { 0x01, 0x02, 0x03 };
    Envelope envelope = new Envelope(data);

    // When
    envelope.close();

    // Then
    assertThrows(IllegalStateException.class, () -> envelope.getBytes());
  }

  @Test
  void shouldEnvelopeBeResolvedAfterClose() {
    // Given
    byte[] data = new byte[] { 0x01, 0x02, 0x03 };
    Envelope envelope = new Envelope(data);

    // When
    envelope.close();

    // Then
    assertTrue(envelope.isResolved());
  }

  // ==================== SEALEDBLOB CONSTRUCTION TESTS ====================

  @Test
  void shouldCreateSealedBlobWithMinimalSize() {
    // Given - Minimum valid size: 2 (pepperVersion) + 2 (nonceLength) + 1 (nonce) + 1 (value) = 6
    byte[] data = new byte[] { 0x00, 0x01, 0x00, 0x01, 0x10, 0x20 };

    // When
    SealedBlob sealedBlob = new SealedBlob(data);

    // Then
    assertNotNull(sealedBlob);
    assertEquals(6, sealedBlob.size());
  }

  @Test
  void shouldCreateSealedBlobWithBuildMethod() {
    // Given
    byte[] value = new byte[] { 0x01, 0x02, 0x03, 0x04 };
    byte[] nonce = new byte[] { 0x10, 0x20, 0x30, 0x40 };
    short pepperVersion = 1;

    // When
    SealedBlob sealedBlob = SealedBlob.build(value, nonce, pepperVersion);

    // Then
    assertNotNull(sealedBlob);
    assertEquals(pepperVersion, sealedBlob.getPepperVersion());
    assertEquals(nonce.length, sealedBlob.getNonce().length);
    assertEquals(value.length, sealedBlob.getValue().length);
  }

  @Test
  void shouldCreateSealedBlobWithLargeValue() {
    // Given
    byte[] value = new byte[256];
    byte[] nonce = new byte[12];
    for (int i = 0; i < value.length; i++) {
      value[i] = (byte) i;
    }
    for (int i = 0; i < nonce.length; i++) {
      nonce[i] = (byte) (i * 10);
    }
    short pepperVersion = 5;

    // When
    SealedBlob sealedBlob = SealedBlob.build(value, nonce, pepperVersion);

    // Then
    assertEquals(256 + 12 + 4, sealedBlob.size());
    assertEquals(pepperVersion, sealedBlob.getPepperVersion());
  }

  @Test
  void shouldCreateSealedBlobWithLargeNonce() {
    // Given
    byte[] value = new byte[] { 0x01, 0x02 };
    byte[] nonce = new byte[64];
    for (int i = 0; i < nonce.length; i++) {
      nonce[i] = (byte) i;
    }
    short pepperVersion = 3;

    // When
    SealedBlob sealedBlob = SealedBlob.build(value, nonce, pepperVersion);

    // Then
    assertNotNull(sealedBlob);
    assertEquals(64, sealedBlob.getNonce().length);
  }

  // ==================== SEALEDBLOB VALIDATION TESTS ====================

  @Test
  void shouldThrowExceptionForNullSealedBlob() {
    // When/Then
    assertThrows(IllegalArgumentException.class, () -> new SealedBlob(null));
  }

  @Test
  void shouldThrowExceptionForEmptySealedBlob() {
    // Given
    byte[] empty = new byte[0];

    // When/Then
    assertThrows(IllegalArgumentException.class, () -> new SealedBlob(empty));
  }

  @Test
  void shouldThrowExceptionForSealedBlobBelowMinSize() {
    // Given - Less than 6 bytes
    byte[] tooSmall = new byte[] { 0x00, 0x01, 0x00, 0x01, 0x10 };

    // When/Then
    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> new SealedBlob(tooSmall)
    );
    assertEquals("Value must be at least 6 bytes", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionForInvalidNonceLength() {
    // Given - Nonce length larger than remaining bytes
    byte[] invalid = new byte[] {
      0x00,
      0x01, // pepperVersion = 1
      0x00,
      0x10, // nonceLength = 16 (but only 2 bytes remain)
      0x10,
      0x20,
    };

    // When/Then
    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> new SealedBlob(invalid)
    );
    assertTrue(
      exception.getMessage().contains("Value length must be at least 1 byte")
    );
  }

  // ==================== SEALEDBLOB STRUCTURE EXTRACTION TESTS ====================

  @Test
  void shouldExtractPepperVersionCorrectly() {
    // Given
    byte[] value = new byte[] { 0x01, 0x02, 0x03 };
    byte[] nonce = new byte[] { 0x10, 0x20 };
    short pepperVersion = 42;

    // When
    SealedBlob sealedBlob = SealedBlob.build(value, nonce, pepperVersion);

    // Then
    assertEquals(42, sealedBlob.getPepperVersion());
  }

  @Test
  void shouldExtractNonceCorrectly() {
    // Given
    byte[] value = new byte[] { 0x01, 0x02, 0x03 };
    byte[] nonce = new byte[] { 0x10, 0x20, 0x30, 0x40 };
    short pepperVersion = 1;

    // When
    SealedBlob sealedBlob = SealedBlob.build(value, nonce, pepperVersion);
    byte[] extractedNonce = sealedBlob.getNonce();

    // Then
    assertArrayEquals(nonce, extractedNonce);
  }

  @Test
  void shouldExtractValueCorrectly() {
    // Given
    byte[] value = new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05 };
    byte[] nonce = new byte[] { 0x10, 0x20 };
    short pepperVersion = 1;

    // When
    SealedBlob sealedBlob = SealedBlob.build(value, nonce, pepperVersion);
    byte[] extractedValue = sealedBlob.getValue();

    // Then
    assertArrayEquals(value, extractedValue);
  }

  @Test
  void shouldExtractCorrectValuesFromManuallyConstructedBlob() {
    // Given - Manually construct: pepperVersion=2, nonceLength=3, nonce=[A,B,C], value=[1,2]
    byte[] rawBlob = new byte[] {
      0x00,
      0x02, // pepperVersion = 2
      0x00,
      0x03, // nonceLength = 3
      0x0A,
      0x0B,
      0x0C, // nonce = [10, 11, 12]
      0x01,
      0x02, // value = [1, 2]
    };

    // When
    SealedBlob sealedBlob = new SealedBlob(rawBlob);

    // Then
    assertEquals(2, sealedBlob.getPepperVersion());
    assertArrayEquals(new byte[] { 0x0A, 0x0B, 0x0C }, sealedBlob.getNonce());
    assertArrayEquals(new byte[] { 0x01, 0x02 }, sealedBlob.getValue());
  }

  @Test
  void shouldHandleZeroPepperVersion() {
    // Given
    byte[] value = new byte[] { 0x01 };
    byte[] nonce = new byte[] { 0x10 };
    short pepperVersion = 0;

    // When
    SealedBlob sealedBlob = SealedBlob.build(value, nonce, pepperVersion);

    // Then
    assertEquals(0, sealedBlob.getPepperVersion());
  }

  @Test
  void shouldHandleMaxPepperVersion() {
    // Given
    byte[] value = new byte[] { 0x01 };
    byte[] nonce = new byte[] { 0x10 };
    short pepperVersion = Short.MAX_VALUE;

    // When
    SealedBlob sealedBlob = SealedBlob.build(value, nonce, pepperVersion);

    // Then
    assertEquals(Short.MAX_VALUE, sealedBlob.getPepperVersion());
  }

  @Test
  void shouldHandleNegativePepperVersion() {
    // Given - Negative short value
    byte[] value = new byte[] { 0x01 };
    byte[] nonce = new byte[] { 0x10 };
    short pepperVersion = -1;

    // When
    SealedBlob sealedBlob = SealedBlob.build(value, nonce, pepperVersion);

    // Then
    assertEquals(-1, sealedBlob.getPepperVersion());
  }

  // ==================== SEALEDBLOB SIZE CALCULATIONS ====================

  @Test
  void shouldCalculateTotalSizeCorrectly() {
    // Given
    byte[] value = new byte[100];
    byte[] nonce = new byte[16];
    short pepperVersion = 1;

    // When
    SealedBlob sealedBlob = SealedBlob.build(value, nonce, pepperVersion);

    // Then - Total size = 2 (pepperVersion) + 2 (nonceLength) + 16 (nonce) + 100 (value)
    assertEquals(120, sealedBlob.size());
  }

  @Test
  void shouldExtractCorrectNonceLengthFromSize() {
    // Given
    byte[] value = new byte[50];
    byte[] nonce = new byte[24];
    short pepperVersion = 1;

    // When
    SealedBlob sealedBlob = SealedBlob.build(value, nonce, pepperVersion);

    // Then
    assertEquals(24, sealedBlob.getNonce().length);
  }

  @Test
  void shouldExtractCorrectValueLengthFromSize() {
    // Given
    byte[] value = new byte[75];
    byte[] nonce = new byte[12];
    short pepperVersion = 1;

    // When
    SealedBlob sealedBlob = SealedBlob.build(value, nonce, pepperVersion);

    // Then
    assertEquals(75, sealedBlob.getValue().length);
  }

  // ==================== ROUND-TRIP TESTS ====================

  @Test
  void shouldRoundTripDataThroughSealedBlob() {
    // Given
    byte[] originalValue = new byte[64];
    byte[] originalNonce = new byte[16];
    for (int i = 0; i < 64; i++) {
      originalValue[i] = (byte) (i * 3);
    }
    for (int i = 0; i < 16; i++) {
      originalNonce[i] = (byte) (i * 7);
    }
    short originalPepperVersion = 42;

    // When
    SealedBlob sealedBlob = SealedBlob.build(
      originalValue,
      originalNonce,
      originalPepperVersion
    );

    // Then
    assertEquals(originalPepperVersion, sealedBlob.getPepperVersion());
    assertArrayEquals(originalNonce, sealedBlob.getNonce());
    assertArrayEquals(originalValue, sealedBlob.getValue());
  }

  @Test
  void shouldPreserveAllBytesInRoundTrip() {
    // Given - All possible byte values
    byte[] value = new byte[256];
    byte[] nonce = new byte[256];
    for (int i = 0; i < 256; i++) {
      value[i] = (byte) i;
      nonce[i] = (byte) (255 - i);
    }

    // When
    SealedBlob sealedBlob = SealedBlob.build(value, nonce, (short) 99);

    // Then
    byte[] extractedValue = sealedBlob.getValue();
    byte[] extractedNonce = sealedBlob.getNonce();

    for (int i = 0; i < 256; i++) {
      assertEquals((byte) i, extractedValue[i]);
      assertEquals((byte) (255 - i), extractedNonce[i]);
    }
  }

  // ==================== EDGE CASE TESTS ====================

  @Test
  void shouldHandleSingleByteValue() {
    // Given
    byte[] value = new byte[] { 0x42 };
    byte[] nonce = new byte[] { 0x10 };
    short pepperVersion = 1;

    // When
    SealedBlob sealedBlob = SealedBlob.build(value, nonce, pepperVersion);

    // Then
    assertEquals(1, sealedBlob.getValue().length);
    assertEquals(0x42, sealedBlob.getValue()[0]);
  }

  @Test
  void shouldHandleSingleByteNonce() {
    // Given
    byte[] value = new byte[] { 0x01, 0x02 };
    byte[] nonce = new byte[] { (byte) 0xAB };
    short pepperVersion = 1;

    // When
    SealedBlob sealedBlob = SealedBlob.build(value, nonce, pepperVersion);

    // Then
    assertEquals(1, sealedBlob.getNonce().length);
    assertEquals((byte) 0xAB, sealedBlob.getNonce()[0]);
  }

  @Test
  void shouldHandleAllZerosValue() {
    // Given
    byte[] value = new byte[32];
    byte[] nonce = new byte[16];
    short pepperVersion = 0;

    // When
    SealedBlob sealedBlob = SealedBlob.build(value, nonce, pepperVersion);

    // Then
    byte[] extractedValue = sealedBlob.getValue();
    for (byte b : extractedValue) {
      assertEquals(0, b);
    }
  }

  @Test
  void shouldHandleAllOnesValue() {
    // Given
    byte[] value = new byte[32];
    byte[] nonce = new byte[16];
    for (int i = 0; i < value.length; i++) {
      value[i] = (byte) 0xFF;
    }
    short pepperVersion = 1;

    // When
    SealedBlob sealedBlob = SealedBlob.build(value, nonce, pepperVersion);

    // Then
    byte[] extractedValue = sealedBlob.getValue();
    for (byte b : extractedValue) {
      assertEquals((byte) 0xFF, b);
    }
  }

  // ==================== BUFFER ACCESS TESTS ====================

  @Test
  void shouldEnvelopeProvideByteBufferAccess() {
    // Given
    byte[] data = new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05 };
    Envelope envelope = new Envelope(data.clone());

    // When
    var buffer = envelope.getBuffer();

    // Then
    assertNotNull(buffer);
    assertEquals(5, buffer.remaining());
    envelope.close();
  }

  @Test
  void shouldSealedBlobProvideByteBufferAccess() {
    // Given
    byte[] value = new byte[] { 0x01, 0x02, 0x03 };
    byte[] nonce = new byte[] { 0x10, 0x20 };
    SealedBlob sealedBlob = SealedBlob.build(value, nonce, (short) 1);

    // When
    var buffer = sealedBlob.getBuffer();

    // Then
    assertNotNull(buffer);
    assertEquals(9, buffer.remaining()); // 2 + 2 + 2 + 3
  }

  // ==================== INHERITANCE TESTS ====================

  @Test
  void shouldEnvelopeExtendSafeBlob() {
    // Given
    byte[] data = new byte[] { 0x01, 0x02, 0x03 };
    Envelope envelope = new Envelope(data);

    // Then
    assertTrue(
      envelope instanceof
        com.voltzug.cinder.core.common.valueobject.safe.SafeBlob
    );
    envelope.close();
  }

  @Test
  void shouldSealedBlobExtendBlob() {
    // Given
    byte[] value = new byte[] { 0x01 };
    byte[] nonce = new byte[] { 0x10 };
    SealedBlob sealedBlob = SealedBlob.build(value, nonce, (short) 1);

    // Then
    assertTrue(
      sealedBlob instanceof com.voltzug.cinder.core.common.valueobject.Blob
    );
  }
}
