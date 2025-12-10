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

public class Assert {

  /**
   * Asserts that the given condition is true.
   *
   * @param condition the condition to check
   * @param msg the message to include in the AssertionError if the condition is false
   * @throws AssertionError if the condition is false
   */
  public static void truly(boolean condition, String msg) {
    if (!condition) {
      throw new AssertionError(msg);
    }
  }

  /**
   * Asserts that the given min and max values define a valid range (min >= 0, max >= 0, max >= min).
   *
   * @param min the minimum value (inclusive)
   * @param max the maximum value (inclusive)
   * @throws AssertionError if the range is invalid
   */
  public static void range(int min, int max) {
    if (min < 0) {
      throw new AssertionError("min must be >= 0, got " + min);
    }
    if (max < 0) {
      throw new AssertionError("max must be >= 0, got " + max);
    }
    if (max < min) {
      throw new AssertionError(
        "max (" + max + ") must be >= min (" + min + ")"
      );
    }
  }

  /**
   * Asserts that the given min and max values define a valid range (min >= 0, max >= 0, max >= min).
   *
   * @param min the minimum value (inclusive)
   * @param max the maximum value (inclusive)
   * @throws AssertionError if the range is invalid
   */
  public static void range(long min, long max) {
    if (min < 0) {
      throw new AssertionError("min must be >= 0, got " + min);
    }
    if (max < 0) {
      throw new AssertionError("max must be >= 0, got " + max);
    }
    if (max < min) {
      throw new AssertionError(
        "max (" + max + ") must be >= min (" + min + ")"
      );
    }
  }

  /**
   * Asserts that the given min and max values define a valid range for decimal floats
   * (min >= 0.0, max >= 0.0, max >= min).
   *
   * @param min the minimum value (inclusive)
   * @param max the maximum value (inclusive)
   * @throws AssertionError if the range is invalid
   */
  public static void range(float min, float max) {
    if (min < 0.0f) {
      throw new AssertionError("min must be >= 0.0, got " + min);
    }
    if (max < 0.0f) {
      throw new AssertionError("max must be >= 0.0, got " + max);
    }
    if (max < min) {
      throw new AssertionError(
        "max (" + max + ") must be >= min (" + min + ")"
      );
    }
  }

  /**
   * Asserts that the given min and max values define a valid range for decimal doubles
   * (min >= 0.0, max >= 0.0, max >= min).
   *
   * @param min the minimum value (inclusive)
   * @param max the maximum value (inclusive)
   * @throws AssertionError if the range is invalid
   */
  public static void range(double min, double max) {
    if (min < 0.0) {
      throw new AssertionError("min must be >= 0.0, got " + min);
    }
    if (max < 0.0) {
      throw new AssertionError("max must be >= 0.0, got " + max);
    }
    if (max < min) {
      throw new AssertionError(
        "max (" + max + ") must be >= min (" + min + ")"
      );
    }
  }
}
