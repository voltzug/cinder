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
package com.voltzug.cinder.core.domain.valueobject;

import com.voltzug.cinder.core.common.utils.Assert;
import java.time.Instant;
import java.util.Objects;

/**
 * Value object representing a timestamp with skew validation capabilities.
 * Used for time-based security checks in request validation.
 */
public record Timestamp(Instant value) {
  public Timestamp {
    Objects.requireNonNull(value, "value cannot be null");
  }

  /**
   * Creates a Timestamp from an Instant.
   *
   * @param instant the instant to wrap
   */
  public static Timestamp from(Instant instant) {
    return new Timestamp(instant);
  }

  /**
   * Creates a Timestamp representing the current moment.
   */
  public static Timestamp now() {
    return new Timestamp(Instant.now());
  }

  /**
   * Creates a Timestamp from epoch milliseconds.
   */
  public static Timestamp ofEpochMilli(long epochMilli) {
    return new Timestamp(Instant.ofEpochMilli(epochMilli));
  }

  /**
   * Determines if this timestamp is within the allowed skew of a reference time.
   * The timestamp is considered valid if it falls within [reference - skewMs, reference + skewMs].
   *
   * @param reference the reference instant to check against
   * @param skewMs the allowed skew in milliseconds (must be non-negative)
   * @return {@code true} if within allowed skew, {@code false} otherwise
   */
  public boolean isWithinSkew(Instant reference, long skewMs) {
    Assert.truly(skewMs > 0, "skewMs cannot be negative");
    Objects.requireNonNull(reference, "reference cannot be null");

    long diff = Math.abs(value.toEpochMilli() - reference.toEpochMilli());
    return diff <= skewMs;
  }

  /**
   * Determines if this timestamp is before the specified instant.
   *
   * @param other the instant to compare against
   * @return {@code true} if this timestamp is before the other instant
   */
  public boolean isBefore(Instant other) {
    Objects.requireNonNull(other, "other cannot be null");
    return value.isBefore(other);
  }

  /**
   * Determines if this timestamp is after the specified instant.
   *
   * @param other the instant to compare against
   * @return {@code true} if this timestamp is after the other instant
   */
  public boolean isAfter(Instant other) {
    Objects.requireNonNull(other, "other cannot be null");
    return value.isAfter(other);
  }

  /**
   * Returns the epoch milliseconds representation of this timestamp.
   */
  public long toEpochMilli() {
    return value.toEpochMilli();
  }
}
