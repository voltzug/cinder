package com.voltzug.cinder.core.domain.valueobject;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Comprehensive tests for cryptographic value objects: Salt, Hmac, and SessionSecret.
 * Tests creation, validation, size constraints, and secure memory handling.
 */
class CryptoValueObjectsTest {

  // ==================== SALT CONSTRUCTION TESTS ====================

  @Test
  void shouldCreateSaltWithMinSize() {
    // Given - SIZE_MIN is 16 bytes (128 bits)
    byte[] saltValue = new byte[16];

    // When
    Salt salt = new Salt(saltValue);

    // Then
    assertNotNull(salt);
    assertEquals(16, salt.size());
  }

  @Test
  void shouldCreateSaltWithMaxSize() {
    // Given - SIZE_MAX is 64 bytes (512 bits)
    byte[] saltValue = new byte[64];

    // When
    Salt salt = new Salt(saltValue);

    // Then
    assertNotNull(salt);
    assertEquals(64, salt.size());
  }

  @Test
  void shouldCreateSaltWith32Bytes() {
    // Given - Common 256-bit salt
    byte[] saltValue = new byte[32];
    for (int i = 0; i < 32; i++) {
      saltValue[i] = (byte) i;
    }

    // When
    Salt salt = new Salt(saltValue);

    // Then
    assertEquals(32, salt.size());
  }

  @Test
  void shouldCreateSaltWithFixedSize() {
    // Given
    byte[] saltValue = new byte[32];

    // When
    Salt salt = new Salt(saltValue, 32);

    // Then
    assertEquals(32, salt.size());
  }

  // ==================== SALT VALIDATION TESTS ====================

  @Test
  void shouldThrowExceptionForNullSalt() {
    // When/Then
    assertThrows(IllegalArgumentException.class, () -> new Salt(null));
  }

  @Test
  void shouldThrowExceptionForEmptySalt() {
    // Given
    byte[] empty = new byte[0];

    // When/Then
    assertThrows(IllegalArgumentException.class, () -> new Salt(empty));
  }

  @Test
  void shouldThrowExceptionForSaltBelowMinSize() {
    // Given - SIZE_MIN is 16 bytes
    byte[] tooSmall = new byte[15];

    // When/Then
    assertThrows(IllegalArgumentException.class, () -> new Salt(tooSmall));
  }

  @Test
  void shouldThrowExceptionForSaltAboveMaxSize() {
    // Given - SIZE_MAX is 64 bytes
    byte[] tooLarge = new byte[65];

    // When/Then
    assertThrows(IllegalArgumentException.class, () -> new Salt(tooLarge));
  }

  @Test
  void shouldThrowExceptionForSaltNotMultipleOf8() {
    // Given - Salt requires length to be multiple of 8 bytes
    byte[] notMultipleOf8 = new byte[20];

    // When/Then
    assertThrows(IllegalArgumentException.class, () -> new Salt(notMultipleOf8));
  }

  @Test
  void shouldThrowExceptionForWrongFixedSaltSize() {
    // Given
    byte[] saltValue = new byte[32];

    // When/Then
    assertThrows(IllegalArgumentException.class, () -> new Salt(saltValue, 64));
  }

  // ==================== SALT CONSTANTS TESTS ====================

  @Test
  void shouldSaltHaveMinSizeOf16() {
    // Then
    assertEquals(16, Salt.SIZE_MIN);
  }

  @Test
  void shouldSaltHaveMaxSizeOf64() {
    // Then
    assertEquals(64, Salt.SIZE_MAX);
  }

  // ==================== HMAC CONSTRUCTION TESTS ====================

  @Test
  void shouldCreateHmacWithMinSize() {
    // Given - SIZE_MIN is 32 bytes (256 bits)
    byte[] hmacValue = new byte[32];

    // When
    Hmac hmac = new Hmac(hmacValue);

    // Then
    assertNotNull(hmac);
    assertEquals(32, hmac.size());
  }

  @Test
  void shouldCreateHmacWithMaxSize() {
    // Given - SIZE_MAX is 64 bytes (512 bits)
    byte[] hmacValue = new byte[64];

    // When
    Hmac hmac = new Hmac(hmacValue);

    // Then
    assertNotNull(hmac);
    assertEquals(64, hmac.size());
  }

  @Test
  void shouldCreateHmacWith512Bits() {
    // Given - HMAC-SHA512 produces 64 bytes
    byte[] hmacValue = new byte[64];
    for (int i = 0; i < 64; i++) {
      hmacValue[i] = (byte) (i * 2);
    }

    // When
    Hmac hmac = new Hmac(hmacValue);

    // Then
    assertEquals(64, hmac.size());
  }

  @Test
  void shouldCreateHmacWithFixedSize() {
    // Given
    byte[] hmacValue = new byte[32];

    // When
    Hmac hmac = new Hmac(hmacValue, 32);

    // Then
    assertEquals(32, hmac.size());
  }

  // ==================== HMAC VALIDATION TESTS ====================

  @Test
  void shouldThrowExceptionForNullHmac() {
    // When/Then
    assertThrows(IllegalArgumentException.class, () -> new Hmac(null));
  }

  @Test
  void shouldThrowExceptionForEmptyHmac() {
    // Given
    byte[] empty = new byte[0];

    // When/Then
    assertThrows(IllegalArgumentException.class, () -> new Hmac(empty));
  }

  @Test
  void shouldThrowExceptionForHmacBelowMinSize() {
    // Given - SIZE_MIN is 32 bytes
    byte[] tooSmall = new byte[16];

    // When/Then
    assertThrows(IllegalArgumentException.class, () -> new Hmac(tooSmall));
  }

  @Test
  void shouldThrowExceptionForHmacAboveMaxSize() {
    // Given - SIZE_MAX is 64 bytes
    byte[] tooLarge = new byte[128];

    // When/Then
    assertThrows(IllegalArgumentException.class, () -> new Hmac(tooLarge));
  }

  @Test
  void shouldThrowExceptionForHmacNotMultipleOf32() {
    // Given - Hmac requires length to be multiple of 32 bytes
    byte[] notMultipleOf32 = new byte[48];

    // When/Then
    assertThrows(IllegalArgumentException.class, () -> new Hmac(notMultipleOf32));
  }

  @Test
  void shouldThrowExceptionForWrongFixedHmacSize() {
    // Given
    byte[] hmacValue = new byte[32];

    // When/Then
    assertThrows(IllegalArgumentException.class, () -> new Hmac(hmacValue, 64));
  }

  // ==================== HMAC CONSTANTS TESTS ====================

  @Test
  void shouldHmacHaveMinSizeOf32() {
    // Then
    assertEquals(32, Hmac.SIZE_MIN);
  }

  @Test
  void shouldHmacHaveMaxSizeOf64() {
    // Then
    assertEquals(64, Hmac.SIZE_MAX);
  }

  // ==================== SESSIONSECRET CONSTRUCTION TESTS ====================

  @Test
  void shouldCreateSessionSecretWith32Bytes() {
    // Given
    byte[] secretValue = new byte[32];
    for (int i = 0; i < 32; i++) {
      secretValue[i] = (byte) (i * 3);
    }

    // When
    SessionSecret secret = new SessionSecret(secretValue);

    // Then
    assertNotNull(secret);
    assertEquals(32, secret.size());
  }

  @Test
  void shouldCreateSessionSecretWith64Bytes() {
    // Given
    byte[] secretValue = new byte[64];

    // When
    SessionSecret secret = new SessionSecret(secretValue);

    // Then
    assertEquals(64, secret.size());
  }

  @Test
  void shouldCreateSessionSecretWith16Bytes() {
    // Given
    byte[] secretValue = new byte[16];

    // When
    SessionSecret secret = new SessionSecret(secretValue);

    // Then
    assertEquals(16, secret.size());
  }

  // ==================== SESSIONSECRET VALIDATION TESTS ====================

  @Test
  void shouldThrowExceptionForNullSessionSecret() {
    // When/Then
    assertThrows(IllegalArgumentException.class, () -> new SessionSecret(null));
  }

  @Test
  void shouldThrowExceptionForEmptySessionSecret() {
    // Given
    byte[] empty = new byte[0];

    // When/Then
    assertThrows(IllegalArgumentException.class, () ->
      new SessionSecret(empty)
    );
  }

  @Test
  void shouldThrowExceptionForSessionSecretNotMultipleOf4() {
    // Given - SessionSecret requires length to be multiple of 4 bytes
    byte[] notMultipleOf4 = new byte[30];

    // When/Then
    assertThrows(IllegalArgumentException.class, () ->
      new SessionSecret(notMultipleOf4)
    );
  }

  @Test
  void shouldThrowExceptionForOddLengthSessionSecret() {
    // Given - Odd length is not multiple of 4
    byte[] oddLength = new byte[33];

    // When/Then
    assertThrows(IllegalArgumentException.class, () ->
      new SessionSecret(oddLength)
    );
  }

  // ==================== SECURE MEMORY HANDLING TESTS ====================

  @Test
  void shouldSealOriginalSaltArrayAfterCreation() {
    // Given
    byte[] original = new byte[32];
    for (int i = 0; i < 32; i++) {
      original[i] = (byte) i;
    }

    // When
    Salt salt = new Salt(original);

    // Then - Original array should be sealed (zeroed)
    for (byte b : original) {
      assertEquals(0, b, "Original array should be sealed after Salt creation");
    }
    salt.close();
  }

  @Test
  void shouldSealOriginalHmacArrayAfterCreation() {
    // Given
    byte[] original = new byte[32];
    for (int i = 0; i < 32; i++) {
      original[i] = (byte) (i * 2);
    }

    // When
    Hmac hmac = new Hmac(original);

    // Then - Original array should be sealed (zeroed)
    for (byte b : original) {
      assertEquals(0, b, "Original array should be sealed after Hmac creation");
    }
    hmac.close();
  }

  @Test
  void shouldSealOriginalSessionSecretArrayAfterCreation() {
    // Given
    byte[] original = new byte[32];
    for (int i = 0; i < 32; i++) {
      original[i] = (byte) (i * 3);
    }

    // When
    SessionSecret secret = new SessionSecret(original);

    // Then - Original array should be sealed (zeroed)
    for (byte b : original) {
      assertEquals(
        0,
        b,
        "Original array should be sealed after SessionSecret creation"
      );
    }
    secret.close();
  }

  @Test
  void shouldSaltBeCloseable() {
    // Given
    byte[] saltValue = new byte[32];
    Salt salt = new Salt(saltValue);

    // When/Then - Should not throw
    assertDoesNotThrow(() -> salt.close());
  }

  @Test
  void shouldHmacBeCloseable() {
    // Given
    byte[] hmacValue = new byte[32];
    Hmac hmac = new Hmac(hmacValue);

    // When/Then - Should not throw
    assertDoesNotThrow(() -> hmac.close());
  }

  @Test
  void shouldSessionSecretBeCloseable() {
    // Given
    byte[] secretValue = new byte[32];
    SessionSecret secret = new SessionSecret(secretValue);

    // When/Then - Should not throw
    assertDoesNotThrow(() -> secret.close());
  }

  @Test
  void shouldSaltWorkWithTryWithResources() {
    // Given
    byte[] saltValue = new byte[32];

    // When/Then - Should auto-close without exception
    assertDoesNotThrow(() -> {
      try (Salt salt = new Salt(saltValue)) {
        assertEquals(32, salt.size());
      }
    });
  }

  @Test
  void shouldHmacWorkWithTryWithResources() {
    // Given
    byte[] hmacValue = new byte[64];

    // When/Then - Should auto-close without exception
    assertDoesNotThrow(() -> {
      try (Hmac hmac = new Hmac(hmacValue)) {
        assertEquals(64, hmac.size());
      }
    });
  }

  @Test
  void shouldSessionSecretWorkWithTryWithResources() {
    // Given
    byte[] secretValue = new byte[32];

    // When/Then - Should auto-close without exception
    assertDoesNotThrow(() -> {
      try (SessionSecret secret = new SessionSecret(secretValue)) {
        assertEquals(32, secret.size());
      }
    });
  }

  // ==================== AUTOCLOSEABLE INTERFACE TESTS ====================

  @Test
  void shouldSaltImplementAutoCloseable() {
    // Given
    byte[] saltValue = new byte[32];
    Salt salt = new Salt(saltValue);

    // Then
    assertTrue(salt instanceof AutoCloseable);
    salt.close();
  }

  @Test
  void shouldHmacImplementAutoCloseable() {
    // Given
    byte[] hmacValue = new byte[32];
    Hmac hmac = new Hmac(hmacValue);

    // Then
    assertTrue(hmac instanceof AutoCloseable);
    hmac.close();
  }

  @Test
  void shouldSessionSecretImplementAutoCloseable() {
    // Given
    byte[] secretValue = new byte[32];
    SessionSecret secret = new SessionSecret(secretValue);

    // Then
    assertTrue(secret instanceof AutoCloseable);
    secret.close();
  }

  // ==================== VALID SIZE RANGE TESTS ====================

  @Test
  void shouldAcceptSaltSizesMultipleOf8InRange() {
    // Given - Valid sizes that are multiples of 8 within range: 16, 24, 32, 40, 48, 56, 64
    int[] validSizes = { 16, 24, 32, 40, 48, 56, 64 };

    // Then
    for (int size : validSizes) {
      byte[] saltValue = new byte[size];
      assertDoesNotThrow(() -> {
        Salt salt = new Salt(saltValue);
        assertEquals(size, salt.size());
        salt.close();
      });
    }
  }

  @Test
  void shouldAcceptHmacSizesMultipleOf32InRange() {
    // Given - Valid sizes that are multiples of 32 within range: 32, 64
    int[] validSizes = { 32, 64 };

    // Then
    for (int size : validSizes) {
      byte[] hmacValue = new byte[size];
      assertDoesNotThrow(() -> {
        Hmac hmac = new Hmac(hmacValue);
        assertEquals(size, hmac.size());
        hmac.close();
      });
    }
  }

  @Test
  void shouldAcceptSessionSecretSizesMultipleOf4() {
    // Given - Valid sizes that are multiples of 4
    int[] validSizes = { 4, 8, 12, 16, 20, 24, 28, 32, 64, 128 };

    // Then
    for (int size : validSizes) {
      byte[] secretValue = new byte[size];
      assertDoesNotThrow(() -> {
        SessionSecret secret = new SessionSecret(secretValue);
        assertEquals(size, secret.size());
        secret.close();
      });
    }
  }

  // ==================== BUFFER ACCESS TESTS ====================

  @Test
  void shouldSaltProvideByteBufferAccess() {
    // Given
    byte[] saltValue = new byte[32];
    for (int i = 0; i < 32; i++) {
      saltValue[i] = (byte) i;
    }
    Salt salt = new Salt(saltValue.clone());

    // When
    var buffer = salt.getBuffer();

    // Then
    assertNotNull(buffer);
    assertEquals(32, buffer.remaining());
    salt.close();
  }

  @Test
  void shouldHmacProvideByteBufferAccess() {
    // Given
    byte[] hmacValue = new byte[64];
    for (int i = 0; i < 64; i++) {
      hmacValue[i] = (byte) (i * 2);
    }
    Hmac hmac = new Hmac(hmacValue.clone());

    // When
    var buffer = hmac.getBuffer();

    // Then
    assertNotNull(buffer);
    assertEquals(64, buffer.remaining());
    hmac.close();
  }

  @Test
  void shouldSessionSecretProvideByteBufferAccess() {
    // Given
    byte[] secretValue = new byte[32];
    for (int i = 0; i < 32; i++) {
      secretValue[i] = (byte) (i * 3);
    }
    SessionSecret secret = new SessionSecret(secretValue.clone());

    // When
    var buffer = secret.getBuffer();

    // Then
    assertNotNull(buffer);
    assertEquals(32, buffer.remaining());
    secret.close();
  }

  // ==================== RESOLVE AND STATE TESTS ====================

  @Test
  void shouldSaltThrowOnGetBytesAfterClose() {
    // Given
    byte[] saltValue = new byte[32];
    Salt salt = new Salt(saltValue);

    // When
    salt.close();

    // Then
    assertThrows(IllegalStateException.class, () -> salt.getBytes());
  }

  @Test
  void shouldHmacThrowOnGetBytesAfterClose() {
    // Given
    byte[] hmacValue = new byte[32];
    Hmac hmac = new Hmac(hmacValue);

    // When
    hmac.close();

    // Then
    assertThrows(IllegalStateException.class, () -> hmac.getBytes());
  }

  @Test
  void shouldSessionSecretThrowOnGetBytesAfterClose() {
    // Given
    byte[] secretValue = new byte[32];
    SessionSecret secret = new SessionSecret(secretValue);

    // When
    secret.close();

    // Then
    assertThrows(IllegalStateException.class, () -> secret.getBytes());
  }

  @Test
  void shouldSaltBeResolvedAfterClose() {
    // Given
    byte[] saltValue = new byte[32];
    Salt salt = new Salt(saltValue);

    // When
    salt.close();

    // Then
    assertTrue(salt.isResolved());
  }

  @Test
  void shouldHmacBeResolvedAfterClose() {
    // Given
    byte[] hmacValue = new byte[32];
    Hmac hmac = new Hmac(hmacValue);

    // When
    hmac.close();

    // Then
    assertTrue(hmac.isResolved());
  }

  @Test
  void shouldSessionSecretBeResolvedAfterClose() {
    // Given
    byte[] secretValue = new byte[32];
    SessionSecret secret = new SessionSecret(secretValue);

    // When
    secret.close();

    // Then
    assertTrue(secret.isResolved());
  }
}
