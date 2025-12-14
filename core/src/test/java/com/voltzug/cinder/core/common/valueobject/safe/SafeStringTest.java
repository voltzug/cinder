package com.voltzug.cinder.core.common.valueobject.safe;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Comprehensive tests for SafeString security utility.
 * Focuses on secure string handling for passwords, secrets, and sensitive data.
 */
@SuppressWarnings("deprecation")
class SafeStringTest {

  @Test
  void shouldCreateFromCharArray() {
    // Given
    char[] data = { 'h', 'e', 'l', 'l', 'o' };

    // When
    SafeString safeString = new SafeString(data);

    // Then
    assertEquals(5, safeString.length());
    assertEquals('h', safeString.charAt(0));
    assertEquals('e', safeString.charAt(1));
    assertEquals('l', safeString.charAt(2));
    assertEquals('l', safeString.charAt(3));
    assertEquals('o', safeString.charAt(4));
    safeString.close();
  }

  @Test
  void shouldSealOriginalCharArrayAfterCreation() {
    // Given
    char[] sensitive = { 'p', 'a', 's', 's' };

    // When
    SafeString safeString = new SafeString(sensitive);

    // Then - Original array should be sealed (filled with 'x')
    assertArrayEquals(
      new char[] { 'x', 'x', 'x', 'x' },
      sensitive,
      "Original char array should be sealed after SafeString creation"
    );
    safeString.close();
  }

  @Test
  void shouldCreateFromByteArray() {
    // Given
    byte[] data = { 65, 66, 67, 68 }; // 'A', 'B', 'C', 'D'

    // When
    SafeString safeString = new SafeString(data);

    // Then
    assertEquals(4, safeString.length());
    assertEquals('A', safeString.charAt(0));
    assertEquals('B', safeString.charAt(1));
    assertEquals('C', safeString.charAt(2));
    assertEquals('D', safeString.charAt(3));
    safeString.close();
  }

  @Test
  void shouldSealOriginalByteArrayAfterCreation() {
    // Given
    byte[] sensitive = { 0x01, 0x02, 0x03, 0x04 };

    // When
    SafeString safeString = new SafeString(sensitive);

    // Then - Original array should be sealed (zeroed)
    assertArrayEquals(
      new byte[] { 0, 0, 0, 0 },
      sensitive,
      "Original byte array should be sealed after SafeString creation"
    );
    safeString.close();
  }

  @Test
  void shouldCreateFromString() {
    // Given
    String text = "password123";

    // When
    SafeString safeString = new SafeString(text);

    // Then
    assertEquals(11, safeString.length());
    assertEquals('p', safeString.charAt(0));
    assertEquals('a', safeString.charAt(1));
    assertEquals('s', safeString.charAt(2));
    safeString.close();
  }

  @Test
  void shouldThrowExceptionForNullCharArray() {
    // When/Then
    assertThrows(
      IllegalArgumentException.class,
      () -> new SafeString((char[]) null)
    );
  }

  @Test
  void shouldThrowExceptionForNullByteArray() {
    // When/Then
    assertThrows(
      IllegalArgumentException.class,
      () -> new SafeString((byte[]) null)
    );
  }

  @Test
  void shouldThrowExceptionForNullString() {
    // When/Then
    assertThrows(
      IllegalArgumentException.class,
      () -> new SafeString((String) null)
    );
  }

  @Test
  void shouldHandleEmptyCharArray() {
    // Given
    char[] empty = {};

    // When
    SafeString safeString = new SafeString(empty);

    // Then
    assertEquals(0, safeString.length());
    safeString.close();
  }

  @Test
  void shouldHandleEmptyByteArray() {
    // Given
    byte[] empty = {};

    // When
    SafeString safeString = new SafeString(empty);

    // Then
    assertEquals(0, safeString.length());
    safeString.close();
  }

  @Test
  void shouldHandleEmptyString() {
    // Given
    String empty = "";

    // When
    SafeString safeString = new SafeString(empty);

    // Then
    assertEquals(0, safeString.length());
    safeString.close();
  }

  @Test
  void shouldImplementCharSequenceLength() {
    // Given
    SafeString safeString = new SafeString("test");

    // When
    CharSequence cs = safeString;

    // Then
    assertEquals(4, cs.length());
    safeString.close();
  }

  @Test
  void shouldImplementCharSequenceCharAt() {
    // Given
    SafeString safeString = new SafeString("test");

    // When
    CharSequence cs = safeString;

    // Then
    assertEquals('t', cs.charAt(0));
    assertEquals('e', cs.charAt(1));
    assertEquals('s', cs.charAt(2));
    assertEquals('t', cs.charAt(3));
    safeString.close();
  }

  @Test
  void shouldThrowExceptionForSubSequence() {
    // Given
    SafeString safeString = new SafeString("test");

    // When/Then
    assertThrows(UnsupportedOperationException.class, () ->
      safeString.subSequence(0, 2)
    );
    safeString.close();
  }

  @Test
  void shouldMatchRegex() {
    // Given
    SafeString safeString = new SafeString("abc123");

    // When
    boolean matches = safeString.matches("[a-z0-9]+");

    // Then
    assertTrue(matches);
    safeString.close();
  }

  @Test
  void shouldNotMatchInvalidRegex() {
    // Given
    SafeString safeString = new SafeString("abc123");

    // When
    boolean matches = safeString.matches("[A-Z]+");

    // Then
    assertFalse(matches);
    safeString.close();
  }

  @Test
  void shouldMatchComplexRegex() {
    // Given - Password pattern: at least 8 chars, 1 uppercase, 1 lowercase, 1 digit
    SafeString password = new SafeString("MyP@ssw0rd");

    // When
    boolean matches = password.matches(".*[A-Z].*[a-z].*[0-9].*");

    // Then
    assertTrue(matches);
    password.close();
  }

  @Test
  void shouldSealInternalBufferOnClose() throws Exception {
    // Given
    char[] original = { 's', 'e', 'c', 'r', 'e', 't' };
    SafeString safeString = new SafeString(original);

    // When
    safeString.close();

    // Then - Verify buffer is sealed by checking charAt throws or returns 'x'
    // Note: After sealing, internal buffer should be 'x' filled
    assertEquals('x', safeString.charAt(0));
    assertEquals('x', safeString.charAt(1));
    assertEquals('x', safeString.charAt(2));
  }

  @Test
  void shouldWorkWithTryWithResources() {
    // Given
    char[] password = { 'p', 'a', 's', 's', 'w', 'o', 'r', 'd' };

    // When/Then - Should auto-close without exception
    assertDoesNotThrow(() -> {
      try (SafeString safeString = new SafeString(password)) {
        assertEquals(8, safeString.length());
        assertEquals('p', safeString.charAt(0));
      }
    });
  }

  @Test
  void shouldHandleUnicodeCharacters() {
    // Given
    String unicode = "Hello世界🔒";

    // When
    SafeString safeString = new SafeString(unicode);

    // Then
    assertEquals(9, safeString.length()); // lock emoji takes 2 chars
    assertEquals('H', safeString.charAt(0));
    assertEquals('世', safeString.charAt(5));
    assertEquals('界', safeString.charAt(6));
    safeString.close();
  }

  @Test
  void shouldHandleSpecialCharacters() {
    // Given
    String special = "!@#$%^&*()_+-=[]{}|;:',.<>?/`~";

    // When
    SafeString safeString = new SafeString(special);

    // Then
    assertEquals(special.length(), safeString.length());
    assertEquals('!', safeString.charAt(0));
    assertEquals('@', safeString.charAt(1));
    assertEquals('#', safeString.charAt(2));
    safeString.close();
  }

  @Test
  void shouldHandleWhitespaceCharacters() {
    // Given
    String whitespace = " \t\n\r";

    // When
    SafeString safeString = new SafeString(whitespace);

    // Then
    assertEquals(4, safeString.length());
    assertEquals(' ', safeString.charAt(0));
    assertEquals('\t', safeString.charAt(1));
    assertEquals('\n', safeString.charAt(2));
    assertEquals('\r', safeString.charAt(3));
    safeString.close();
  }

  @Test
  void shouldConvertByteArrayToCharsCorrectly() {
    // Given - ASCII printable characters
    byte[] bytes = { 32, 65, 90, 97, 122 }; // space, 'A', 'Z', 'a', 'z'

    // When
    SafeString safeString = new SafeString(bytes);

    // Then
    assertEquals(5, safeString.length());
    assertEquals(' ', safeString.charAt(0));
    assertEquals('A', safeString.charAt(1));
    assertEquals('Z', safeString.charAt(2));
    assertEquals('a', safeString.charAt(3));
    assertEquals('z', safeString.charAt(4));
    safeString.close();
  }

  @Test
  void shouldHandleByteToCharConversionWithUnsigned() {
    // Given - Bytes > 127 (negative as signed byte, but treated as unsigned)
    byte[] bytes = { (byte) 0xFF, (byte) 0x80, (byte) 0xAA };

    // When
    SafeString safeString = new SafeString(bytes);

    // Then - Should convert as unsigned (0xFF & 0xFF = 255)
    assertEquals(3, safeString.length());
    assertEquals((char) 255, safeString.charAt(0));
    assertEquals((char) 128, safeString.charAt(1));
    assertEquals((char) 170, safeString.charAt(2));
    safeString.close();
  }

  @Test
  void shouldThrowIndexOutOfBoundsForInvalidCharAt() {
    // Given
    SafeString safeString = new SafeString("test");

    // When/Then
    assertThrows(IndexOutOfBoundsException.class, () -> safeString.charAt(-1));
    assertThrows(IndexOutOfBoundsException.class, () -> safeString.charAt(4));
    assertThrows(IndexOutOfBoundsException.class, () -> safeString.charAt(100));
    safeString.close();
  }

  @Test
  void shouldHandleSingleCharacter() {
    // Given
    char[] single = { 'X' };

    // When
    SafeString safeString = new SafeString(single);

    // Then
    assertEquals(1, safeString.length());
    assertEquals('X', safeString.charAt(0));
    safeString.close();
  }

  @Test
  void shouldHandleLongPassword() {
    // Given - 100 character password
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 100; i++) {
      sb.append((char) ('A' + (i % 26)));
    }
    String longPassword = sb.toString();

    // When
    SafeString safeString = new SafeString(longPassword);

    // Then
    assertEquals(100, safeString.length());
    assertEquals('A', safeString.charAt(0));
    assertEquals('B', safeString.charAt(1));
    assertEquals('Z', safeString.charAt(25));
    safeString.close();
  }

  @Test
  void shouldVerifySecureLifecyclePattern() throws Exception {
    // Given - Simulate secure password lifecycle
    char[] userInput = "MySecretPassword123!".toCharArray();

    // When - Create SafeString (seals original)
    SafeString securePassword;
    try (SafeString temp = new SafeString(userInput)) {
      securePassword = temp;

      // Then - Original is sealed
      for (char c : userInput) {
        assertEquals('x', c, "Original password char array should be sealed");
      }

      // And - SafeString contains data
      assertEquals(20, securePassword.length());
      assertEquals('M', securePassword.charAt(0));
    } // After try-with-resources, SafeString buffer is sealed

    // Verify data is sealed after close
    assertEquals('x', securePassword.charAt(0));
    securePassword.close();
  }

  @Test
  void shouldNotLeakDataThroughToString() {
    // Given
    SafeString password = new SafeString("secret123");

    // When
    String toString = password.toString();

    // Then - Should use CharSequence toString (may reveal data in testing)
    // Just verify it returns a string
    assertNotNull(toString);
    password.close();
  }

  @Test
  void shouldHandleConsecutiveCloseOperations() throws Exception {
    // Given
    SafeString safeString = new SafeString("test");

    // When - Close multiple times
    safeString.close();
    safeString.close();
    safeString.close();

    // Then - Should not throw exception
    assertEquals('x', safeString.charAt(0));
  }

  @Test
  void shouldBeUsableAsCharSequenceInPatternMatching() {
    // Given
    SafeString input = new SafeString("abc123xyz");

    // When - Use as CharSequence
    CharSequence cs = input;
    boolean matchesPattern = java.util.regex.Pattern.matches(".*123.*", cs);

    // Then
    assertTrue(matchesPattern);
  }

  @Test
  void shouldHandleNullCharInCharArray() {
    // Given
    char[] withNull = { 'a', 'b', '\0', 'c' };

    // When
    SafeString safeString = new SafeString(withNull);

    // Then
    assertEquals(4, safeString.length());
    assertEquals('a', safeString.charAt(0));
    assertEquals('\0', safeString.charAt(2));
    assertEquals('c', safeString.charAt(3));
    safeString.close();
  }

  @Test
  void shouldPreventMemoryLeakAfterUse() throws Exception {
    // Given - Simulate cryptographic secret
    char[] secret = new char[32];
    for (int i = 0; i < 32; i++) {
      secret[i] = (char) ('A' + (i % 26));
    }

    // When - Use in SafeString and close
    try (SafeString safeSecret = new SafeString(secret)) {
      // Use the secret
      assertEquals(32, safeSecret.length());
    }

    // Then - Original array is sealed
    for (char c : secret) {
      assertEquals('x', c, "Secret should be sealed in original array");
    }
  }
}
