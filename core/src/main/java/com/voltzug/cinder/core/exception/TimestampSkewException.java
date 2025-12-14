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
package com.voltzug.cinder.core.exception;

import com.voltzug.cinder.core.domain.valueobject.Timestamp;
import com.voltzug.cinder.core.domain.valueobject.id.SessionId;

/**
 * Exception thrown when a timestamp exceeds the allowed skew tolerance.
 * This prevents replay attacks and ensures request freshness.
 */
public final class TimestampSkewException
  extends InvalidSessionException
  implements IAbuseException {

  private final Timestamp timestamp;
  private final long allowedSkewMs;

  public TimestampSkewException(
    SessionId id,
    Timestamp timestamp,
    long allowedSkewMs
  ) {
    super(id, "Timestamp exceeds allowed skew of " + allowedSkewMs + "ms");
    this.timestamp = timestamp;
    this.allowedSkewMs = allowedSkewMs;
  }

  public TimestampSkewException(
    SessionId id,
    Timestamp timestamp,
    long allowedSkewMs,
    String message
  ) {
    super(id, message);
    this.timestamp = timestamp;
    this.allowedSkewMs = allowedSkewMs;
  }

  public Timestamp getTimestamp() {
    return timestamp;
  }

  public long getAllowedSkewMs() {
    return allowedSkewMs;
  }
}
