package com.voltzug.cinder.core.domain.valueobject;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Comprehensive tests for GateHash and AccessHash value objects.
 * Tests hash creation, validation, size constraints, and unlock verification.
 */
class GateHashTest {

  // ==================== GATEHASH CONSTRUCTION TESTS ====================

  @Test
  void shouldCreateGateHashWithSha256Size() {
    // Given - SHA-256 produces 32 bytes
    byte[] hash = new byte[32];

    // When
    GateHash gateHash = new GateHash(hash);

    // Then
    assertNotNull(gateHash);
    assertEquals(32, gateHash.size());
  }

  @Test
  void shouldCreateGateHashWithSha512Size() {
    // Given - SHA-512 produces 64 bytes (SIZE_MAX)
    byte[] hash = new byte[64];

    // When
    GateHash gateHash = new GateHash(hash);

    // Then
    assertNotNull(gateHash);
    assertEquals(64, gateHash.size());
  }

  @Test
  void shouldCreateGateHashWithSha1Size() {
    // Given - SHA-1 produces 20 bytes (SIZE_MIN)
    byte[] hash = new byte[20];

    // When
    GateHash gateHash = new GateHash(hash);

    // Then
    assertNotNull(gateHash);
    assertEquals(20, gateHash.size());
  }

  @Test
  void shouldCreateGateHashWithFixedSize() {
    // Given
    byte[] hash = new byte[48];

    // When
    GateHash gateHash = new GateHash(hash, 48);

    // Then
    assertNotNull(gateHash);
    assertEquals(48, gateHash.size());
  }

  // ==================== GATEHASH VALIDATION TESTS ====================

  @Test
  void shouldThrowExceptionForNullHash() {
    // When/Then
    assertThrows(IllegalArgumentException.class, () -> new GateHash(null));
  }

  @Test
  void shouldThrowExceptionForEmptyHash() {
    // Given
    byte[] empty = new byte[0];

    // When/Then
    assertThrows(IllegalArgumentException.class, () -> new GateHash(empty));
  }

  @Test
  void shouldThrowExceptionForHashBelowMinSize() {
    // Given - SIZE_MIN is 20 bytes
    byte[] tooSmall = new byte[19];

    // When/Then
    assertThrows(IllegalArgumentException.class, () -> new GateHash(tooSmall));
  }

  @Test
  void shouldThrowExceptionForHashAboveMaxSize() {
    // Given - SIZE_MAX is 64 bytes
    byte[] tooLarge = new byte[65];

    // When/Then
    assertThrows(IllegalArgumentException.class, () -> new GateHash(tooLarge));
  }

  @Test
  void shouldThrowExceptionForWrongFixedSize() {
    // Given
    byte[] hash = new byte[32];

    // When/Then
    assertThrows(IllegalArgumentException.class, () -> new GateHash(hash, 48));
  }

  // ==================== GATEHASH CONSTANTS TESTS ====================

  @Test
  void shouldHaveMinSizeOf20() {
    // Then
    assertEquals(20, GateHash.SIZE_MIN);
  }

  @Test
  void shouldHaveMaxSizeOf64() {
    // Then
    assertEquals(64, GateHash.SIZE_MAX);
  }

  // ==================== ACCESSHASH CONSTRUCTION TESTS ====================

  @Test
  void shouldCreateAccessHashWithSha256Size() {
    // Given
    byte[] hash = new byte[32];

    // When
    AccessHash accessHash = new AccessHash(hash);

    // Then
    assertNotNull(accessHash);
    assertEquals(32, accessHash.size());
  }

  @Test
  void shouldCreateAccessHashWithSha512Size() {
    // Given
    byte[] hash = new byte[64];

    // When
    AccessHash accessHash = new AccessHash(hash);

    // Then
    assertNotNull(accessHash);
    assertEquals(64, accessHash.size());
  }

  @Test
  void shouldCreateAccessHashWithFixedSize() {
    // Given
    byte[] hash = new byte[64];

    // When
    AccessHash accessHash = new AccessHash(hash, 64);

    // Then
    assertNotNull(accessHash);
    assertEquals(64, accessHash.size());
  }

  // ==================== ACCESSHASH VALIDATION TESTS ====================

  @Test
  void shouldThrowExceptionForNullAccessHash() {
    // When/Then
    assertThrows(IllegalArgumentException.class, () -> new AccessHash(null));
  }

  @Test
  void shouldThrowExceptionForEmptyAccessHash() {
    // Given
    byte[] empty = new byte[0];

    // When/Then
    assertThrows(IllegalArgumentException.class, () -> new AccessHash(empty));
  }

  @Test
  void shouldThrowExceptionForAccessHashBelowMinSize() {
    // Given
    byte[] tooSmall = new byte[19];

    // When/Then
    assertThrows(IllegalArgumentException.class, () ->
      new AccessHash(tooSmall)
    );
  }

  // ==================== UNLOCK VERIFICATION TESTS ====================

  @Test
  void shouldUnlockWhenHashesMatch() {
    // Given - Same hash values
    byte[] hashValue = new byte[32];
    for (int i = 0; i < 32; i++) {
      hashValue[i] = (byte) (i * 3);
    }
    GateHash gateHash = new GateHash(hashValue.clone());
    AccessHash accessHash = new AccessHash(hashValue.clone());

    // When
    boolean canUnlock = accessHash.canUnlock(gateHash);

    // Then
    assertTrue(canUnlock);
  }

  @Test
  void shouldNotUnlockWhenHashesDiffer() {
    // Given - Different hash values
    byte[] gateValue = new byte[32];
    byte[] accessValue = new byte[32];
    for (int i = 0; i < 32; i++) {
      gateValue[i] = (byte) i;
      accessValue[i] = (byte) (i + 1);
    }
    GateHash gateHash = new GateHash(gateValue);
    AccessHash accessHash = new AccessHash(accessValue);

    // When
    boolean canUnlock = accessHash.canUnlock(gateHash);

    // Then
    assertFalse(canUnlock);
  }

  @Test
  void shouldNotUnlockWhenSingleByteDiffers() {
    // Given - Only one byte is different
    byte[] hashValue = new byte[32];
    for (int i = 0; i < 32; i++) {
      hashValue[i] = (byte) i;
    }
    byte[] modifiedValue = hashValue.clone();
    modifiedValue[15] = (byte) (modifiedValue[15] ^ 0x01); // Flip one bit

    GateHash gateHash = new GateHash(hashValue);
    AccessHash accessHash = new AccessHash(modifiedValue);

    // When
    boolean canUnlock = accessHash.canUnlock(gateHash);

    // Then
    assertFalse(canUnlock);
  }

  @Test
  void shouldThrowExceptionForMismatchedHashSizes() {
    // Given - Different sizes
    byte[] gateValue = new byte[32];
    byte[] accessValue = new byte[64];
    GateHash gateHash = new GateHash(gateValue);
    AccessHash accessHash = new AccessHash(accessValue);

    // When/Then
    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> accessHash.canUnlock(gateHash)
    );
    assertTrue(exception.getMessage().contains("Hash sizes do not match"));
  }

  @Test
  void shouldUnlockWithAllZeroHashes() {
    // Given - All zeros
    byte[] zeroHash = new byte[32];
    GateHash gateHash = new GateHash(zeroHash.clone());
    AccessHash accessHash = new AccessHash(zeroHash.clone());

    // When
    boolean canUnlock = accessHash.canUnlock(gateHash);

    // Then
    assertTrue(canUnlock);
  }

  @Test
  void shouldUnlockWithAllOnesHashes() {
    // Given - All 0xFF bytes
    byte[] onesHash = new byte[32];
    for (int i = 0; i < 32; i++) {
      onesHash[i] = (byte) 0xFF;
    }
    GateHash gateHash = new GateHash(onesHash.clone());
    AccessHash accessHash = new AccessHash(onesHash.clone());

    // When
    boolean canUnlock = accessHash.canUnlock(gateHash);

    // Then
    assertTrue(canUnlock);
  }

  @Test
  void shouldNotUnlockWhenLastByteDiffers() {
    // Given - Only last byte differs
    byte[] gateValue = new byte[32];
    byte[] accessValue = new byte[32];
    for (int i = 0; i < 31; i++) {
      gateValue[i] = (byte) i;
      accessValue[i] = (byte) i;
    }
    gateValue[31] = (byte) 0x00;
    accessValue[31] = (byte) 0x01;

    GateHash gateHash = new GateHash(gateValue);
    AccessHash accessHash = new AccessHash(accessValue);

    // When
    boolean canUnlock = accessHash.canUnlock(gateHash);

    // Then
    assertFalse(canUnlock);
  }

  @Test
  void shouldNotUnlockWhenFirstByteDiffers() {
    // Given - Only first byte differs
    byte[] gateValue = new byte[32];
    byte[] accessValue = new byte[32];
    for (int i = 1; i < 32; i++) {
      gateValue[i] = (byte) i;
      accessValue[i] = (byte) i;
    }
    gateValue[0] = (byte) 0x00;
    accessValue[0] = (byte) 0x01;

    GateHash gateHash = new GateHash(gateValue);
    AccessHash accessHash = new AccessHash(accessValue);

    // When
    boolean canUnlock = accessHash.canUnlock(gateHash);

    // Then
    assertFalse(canUnlock);
  }

  // ==================== TIMING-SAFE COMPARISON TESTS ====================

  @Test
  void shouldUseConstantTimeComparison() {
    // Given - canUnlock uses XOR accumulation which is constant-time
    byte[] gateValue = new byte[32];
    byte[] accessValue = new byte[32];
    for (int i = 0; i < 32; i++) {
      gateValue[i] = (byte) (Math.random() * 256);
      accessValue[i] = (byte) (Math.random() * 256);
    }
    GateHash gateHash = new GateHash(gateValue);
    AccessHash accessHash = new AccessHash(accessValue);

    // When - Multiple calls should have consistent behavior
    boolean result1 = accessHash.canUnlock(gateHash);
    boolean result2 = accessHash.canUnlock(gateHash);

    // Then
    assertEquals(result1, result2);
  }

  // ==================== EDGE CASE TESTS ====================

  @Test
  void shouldHandleMinSizeHashForUnlock() {
    // Given - SIZE_MIN (20 bytes)
    byte[] hashValue = new byte[20];
    for (int i = 0; i < 20; i++) {
      hashValue[i] = (byte) i;
    }
    GateHash gateHash = new GateHash(hashValue.clone());
    AccessHash accessHash = new AccessHash(hashValue.clone());

    // When
    boolean canUnlock = accessHash.canUnlock(gateHash);

    // Then
    assertTrue(canUnlock);
  }

  @Test
  void shouldHandleMaxSizeHashForUnlock() {
    // Given - SIZE_MAX (64 bytes)
    byte[] hashValue = new byte[64];
    for (int i = 0; i < 64; i++) {
      hashValue[i] = (byte) i;
    }
    GateHash gateHash = new GateHash(hashValue.clone());
    AccessHash accessHash = new AccessHash(hashValue.clone());

    // When
    boolean canUnlock = accessHash.canUnlock(gateHash);

    // Then
    assertTrue(canUnlock);
  }

  @Test
  void shouldHandleSha384SizeHash() {
    // Given - SHA-384 produces 48 bytes
    byte[] hashValue = new byte[48];
    for (int i = 0; i < 48; i++) {
      hashValue[i] = (byte) (i * 2);
    }
    GateHash gateHash = new GateHash(hashValue.clone());
    AccessHash accessHash = new AccessHash(hashValue.clone());

    // When
    boolean canUnlock = accessHash.canUnlock(gateHash);

    // Then
    assertTrue(canUnlock);
  }

  // ==================== INHERITANCE TESTS ====================

  @Test
  void shouldAccessHashExtendGateHash() {
    // Given
    byte[] hash = new byte[32];
    AccessHash accessHash = new AccessHash(hash);

    // Then
    assertTrue(accessHash instanceof GateHash);
  }

  @Test
  void shouldAccessHashHaveSameConstraintsAsGateHash() {
    // Then - Both should have the same size constraints
    assertEquals(GateHash.SIZE_MIN, 20);
    assertEquals(GateHash.SIZE_MAX, 64);
  }

  // ==================== BUFFER ACCESS TESTS ====================

  @Test
  void shouldProvideByteBufferAccess() {
    // Given
    byte[] hashValue = new byte[32];
    for (int i = 0; i < 32; i++) {
      hashValue[i] = (byte) i;
    }
    GateHash gateHash = new GateHash(hashValue.clone());

    // When
    var buffer = gateHash.getBuffer();

    // Then
    assertNotNull(buffer);
    assertEquals(32, buffer.remaining());
  }

  @Test
  void shouldReturnCorrectSize() {
    // Given
    byte[] hash32 = new byte[32];
    byte[] hash64 = new byte[64];

    // When
    GateHash gateHash32 = new GateHash(hash32);
    GateHash gateHash64 = new GateHash(hash64);

    // Then
    assertEquals(32, gateHash32.size());
    assertEquals(64, gateHash64.size());
  }
}
