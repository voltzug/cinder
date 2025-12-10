// Cinder - zero-knowledge file transfer that burns after access
// Copyright (C) 2025  voltzug
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Affero General Public License as published
// by the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Affero General Public License for more details.
//
// You should have received a copy of the GNU Affero General Public License
// along with this program.  If not, see <https://www.gnu.org/licenses/>.
package com.voltzug.cinder.core.common.valueobject.safe;

import com.voltzug.cinder.core.common.utils.SafeArrays;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * A secure, mutable string-like object that stores its contents in a char array,
 * allowing explicit zeroization of sensitive data (such as passwords or secrets)
 * after use. Implements {@link CharSequence} for compatibility, and {@link AutoCloseable}
 * to enable use in try-with-resources blocks for automatic memory sealing.
 */
public class SafeString implements CharSequence, AutoCloseable {

  private final char[] buffer;

  /**
   * Constructs a SafeString from a char array. The input array is "moved" (ownership transferred)
   * and should not be used after this call. If the input is null, throws IllegalArgumentException.
   *
   * @param value the char array containing sensitive data
   * @throws IllegalArgumentException if value is null
   */
  public SafeString(final char[] value) throws IllegalArgumentException {
    SafeArrays.assertIsArray(value);
    buffer = SafeArrays.move(value);
  }

  /**
   * Constructs a SafeString from a byte array. Each byte is converted to a char (unsigned).
   * The input array is sealed (zeroed) after use. If the input is null, throws IllegalArgumentException.
   *
   * @param value the byte array containing sensitive data
   * @throws IllegalArgumentException if value is null
   */
  public SafeString(final byte[] value) throws IllegalArgumentException {
    SafeArrays.assertIsArray(value);
    buffer = new char[value.length];
    for (int i = 0; i < buffer.length; i++) {
      buffer[i] = (char) (value[i] & 0xFF);
    }
    SafeArrays.seal(value);
  }

  /**
   * Constructs a SafeString from a Java String.
   * **Security Note:** This approach is *unsafe* as using the direct char[] or byte[] constructors,
   * because Java Strings are immutable and may remain in memory (such as in String pools or GC roots)
   * after this constructor completes. Sensitive data passed as a String cannot be reliably zeroed out.
   * Prefer using the char[] or byte[] constructors for maximum security.
   *
   * @param value the String containing sensitive data
   * @throws IllegalArgumentException if value is null
   */
  @Deprecated(forRemoval = false, since = "0.1")
  public SafeString(final String unsafe) throws IllegalArgumentException {
    if (unsafe == null) {
      throw new IllegalArgumentException("value cannot be null");
    }
    buffer = unsafe.toCharArray();
  }

  @Override
  public int length() {
    return buffer.length;
  }

  @Override
  public char charAt(final int index) {
    return buffer[index];
  }

  @Override
  public CharSequence subSequence(final int start, final int end) {
    throw new UnsupportedOperationException("is solid");
  }

  public boolean matches(String regex) {
    return Pattern.matches(regex, this);
  }

  /**
   * Seals (zeroes out) the internal buffer, erasing all sensitive data from memory.
   * This method is called automatically when used in a try-with-resources block.
   */
  @Override
  public void close() {
    SafeArrays.seal(buffer);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(buffer);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other instanceof SafeString) {
      SafeString o = (SafeString) other;
      return Arrays.equals(buffer, o.buffer);
    } else if (other instanceof String) {
      String o = (String) other;
      return Arrays.equals(buffer, o.toCharArray());
    }
    return false;
  }
}
