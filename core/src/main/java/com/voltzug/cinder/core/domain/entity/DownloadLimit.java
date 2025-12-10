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
package com.voltzug.cinder.core.domain.entity;

import com.voltzug.cinder.core.domain.valueobject.id.LinkId;
import java.time.Instant;
import java.util.Objects;

/**
 * Tracks download attempt limits and expiration for a specific link.
 * Used to enforce retry count and time-based access restrictions.
 *
 * @param linkId            the link identifier
 * @param remainingAttempts number of remaining download attempts
 * @param expiryDate        the expiration date/time
 * @param lastAttemptAt     the timestamp of the last download attempt
 */
public record DownloadLimit(
  LinkId linkId,
  int remainingAttempts,
  Instant expiryDate,
  Instant lastAttemptAt
) implements IExpirable {
  public DownloadLimit {
    Objects.requireNonNull(linkId, "linkId must not be null");
    Objects.requireNonNull(expiryDate, "expiryDate must not be null");
    // lastAttemptAt may be null if no attempts yet
    if (remainingAttempts < 0) {
      throw new IllegalArgumentException(
        "remainingAttempts cannot be negative"
      );
    }
  }

  @Override
  public Instant getExpiryDate() {
    return expiryDate;
  }

  @Override
  public boolean isExpired(Instant now) {
    return now.isAfter(expiryDate);
  }

  @Override
  public final int hashCode() {
    return Objects.hash(linkId, remainingAttempts, expiryDate, lastAttemptAt);
  }

  @Override
  public final boolean equals(Object other) {
    if (this == other) return true;
    if (getClass() != other.getClass()) return false;
    DownloadLimit o = (DownloadLimit) other;
    return (
      remainingAttempts == o.remainingAttempts &&
      expiryDate.equals(o.expiryDate) &&
      lastAttemptAt.equals(o.lastAttemptAt) &&
      linkId.equals(o.linkId)
    );
  }
}
