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

import com.voltzug.cinder.core.domain.valueobject.SessionSecret;
import com.voltzug.cinder.core.domain.valueobject.id.LinkId;
import com.voltzug.cinder.core.domain.valueobject.id.SessionId;
import java.time.Instant;
import java.util.Objects;

/**
 * Represents an active upload or download session.
 * Tracks session state, expiration, and associated secrets.
 * For upload sessions, linkId is null until upload completes.
 * For download sessions, linkId references the target file.
 */
public record Session(
  SessionId id,
  SessionSecret sessionSecret,
  LinkId linkId,
  Mode mode,
  Instant createdAt,
  Instant expiresAt
) implements IExpirable {
  public Session {
    Objects.requireNonNull(id, "id cannot be null");
    // sessionSecret may be null (optional)
    Objects.requireNonNull(mode, "mode cannot be null");
    // linkId may be null for upload sessions
    Objects.requireNonNull(createdAt, "createdAt cannot be null");
    Objects.requireNonNull(expiresAt, "expiresAt cannot be null");
  }

  @Override
  public Instant getExpiryDate() {
    return expiresAt;
  }

  @Override
  public boolean isExpired(Instant now) {
    return now.isAfter(expiresAt);
  }

  /**
   * For upload sessions, linkId is null until upload completes.
   * For download sessions, linkId references the target file.
   */
  @Override
  public String toString() {
    return (
      "Session{" +
      "id=" +
      id +
      ", sessionSecret=" +
      (sessionSecret != null ? "[PROTECTED]" : "null") +
      ", linkId=" +
      linkId +
      ", mode=" +
      mode +
      ", createdAt=" +
      createdAt +
      ", expiresAt=" +
      expiresAt +
      '}'
    );
  }

  @Override
  public final int hashCode() {
    return Objects.hash(id, sessionSecret, linkId, mode, createdAt, expiresAt);
  }

  @Override
  public final boolean equals(Object other) {
    if (this == other) return true;
    if (getClass() != other.getClass()) return false;
    Session o = (Session) other;
    return (
      mode == o.mode &&
      Objects.equals(createdAt, o.createdAt) &&
      Objects.equals(expiresAt, o.expiresAt) &&
      Objects.equals(id, o.id) &&
      Objects.equals(linkId, o.linkId) &&
      Objects.equals(sessionSecret, o.sessionSecret)
    );
  }

  /** Session mode */
  public enum Mode {
    UPLOAD,
    DOWNLOAD,
  }
}
