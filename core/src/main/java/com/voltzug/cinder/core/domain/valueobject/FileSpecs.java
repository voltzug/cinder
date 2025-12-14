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
import com.voltzug.cinder.core.domain.entity.IExpirable;
import java.time.Instant;
import java.util.Objects;

/**
 * Value object representing file specification parameters.
 * Contains expiry date and retry count configuration for file access.
 *
 * @param expiryDate the date and time when the file expires
 * @param retryCount the maximum number of download attempts allowed
 */
public record FileSpecs(Instant expiryDate, int retryCount) implements
  IExpirable {
  /** Minimum allowed retry count. */
  public static final int MIN_RETRY_COUNT = 1;
  /** Maximum allowed retry count. */
  public static final int MAX_RETRY_COUNT = 99;

  public FileSpecs {
    Assert.truly(
      retryCount >= MIN_RETRY_COUNT && retryCount <= MAX_RETRY_COUNT,
      "retryCount must be between " +
        MIN_RETRY_COUNT +
        " and " +
        MAX_RETRY_COUNT
    );
    Objects.requireNonNull(expiryDate, "expiryDate cannot be null");
  }

  public static FileSpecs from(FileSpecs other) {
    return new FileSpecs(other.expiryDate, other.retryCount);
  }

  @Override
  public Instant getExpiryDate() {
    return expiryDate;
  }

  /**
   * Checks if the file has expired at the given reference time.
   *
   * @param referenceTime the time to check against
   * @return true if expired, false otherwise
   */
  @Override
  public boolean isExpired(Instant now) {
    return now.isAfter(expiryDate);
  }

  @Override
  public final int hashCode() {
    return Objects.hash(retryCount, expiryDate);
  }

  @Override
  public final boolean equals(Object other) {
    if (this == other) return true;
    if (getClass() != other.getClass()) return false;
    FileSpecs o = (FileSpecs) other;
    return retryCount == o.retryCount && expiryDate.equals(o.expiryDate);
  }
}
