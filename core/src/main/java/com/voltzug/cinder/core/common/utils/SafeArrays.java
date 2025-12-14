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
package com.voltzug.cinder.core.common.utils;

import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.util.Arrays;

/**
 * Utility methods for securely handling sensitive array data.
 * Provides methods to zero or overwrite arrays ("seal") and to move contents
 * into a new array while wiping the original.
 */
public class SafeArrays {

  /**
   * Asserts that the provided Object is not null and is Array.
   * @param target the Object to check
   */
  public static void assertIsArray(final Object target)
    throws IllegalArgumentException {
    if (target == null) {
      throw new IllegalArgumentException("array must not be null");
    }
    if (!target.getClass().isArray()) {
      throw new IllegalArgumentException("target must be an array");
    }
  }

  /**
   * Asserts that the provided Object is not null and not empty Array.
   * @param target the array to check
   */
  public static void assertNotEmpty(final Object target)
    throws IllegalArgumentException {
    assertIsArray(target);
    int length = Array.getLength(target);
    if (length == 0) {
      throw new IllegalArgumentException("array must have content");
    }
  }

  /**
   * Overwrites all elements of the given byte array with zero.
   * Use to securely erase sensitive data.
   *
   * @param target the byte array to seal
   */
  public static void seal(final byte[] target) {
    Arrays.fill(target, (byte) 0);
  }

  /**
   * Overwrites all elements of the given char array with 'x'.
   * Use to securely erase sensitive data.
   *
   * @param target the char array to seal
   */
  public static void seal(final char[] target) {
    Arrays.fill(target, 'x');
  }

  /**
   * Overwrites all elements of the given int array with zero.
   * Use to securely erase sensitive data.
   *
   * @param target the int array to seal
   */
  public static void seal(final int[] target) {
    Arrays.fill(target, 0);
  }

  /**
   * Moves the contents of the given byte array into a new array,
   * zeroing out the original array for security.
   * This is useful for transferring sensitive key material while ensuring
   * the original array is wiped from memory.
   *
   * @param source  to move (will be zeroed)
   * @return a new byte array containing the original contents
   */
  public static byte[] move(byte[] source) {
    byte[] result = new byte[source.length];
    System.arraycopy(source, 0, result, 0, source.length);
    seal(source);
    return result;
  }

  /**
   * Moves the contents of the given char array into a new array,
   * overwriting the original array with 'x' for security.
   *
   * @param source to move (will be overwritten)
   * @return a new char array containing the original contents
   */
  public static char[] move(char[] source) {
    char[] result = new char[source.length];
    System.arraycopy(source, 0, result, 0, source.length);
    seal(source);
    return result;
  }

  /**
   * Moves the contents of the given int array into a new array,
   * zeroing out the original array for security.
   *
   * @param source to move (will be zeroed)
   * @return a new int array containing the original contents
   */
  public static int[] move(int[] source) {
    int[] result = new int[source.length];
    System.arraycopy(source, 0, result, 0, source.length);
    seal(source);
    return result;
  }

  /**
   * Compares two byte arrays for equality in a timing-safe manner.
   * This method is resistant to timing attacks by ensuring the comparison
   * takes the same amount of time regardless of the contents of the arrays.
   *
   * @param a the first byte array
   * @param b the second byte array
   * @return true if both arrays are equal in length and contents
   * @throws IllegalArgumentException if either array is null, empty, or of unequal length
   */
  public static boolean equals(final byte[] a, final byte[] b) {
    assertNotEmpty(a);
    assertNotEmpty(b);
    return MessageDigest.isEqual(a, b);
  }
}
