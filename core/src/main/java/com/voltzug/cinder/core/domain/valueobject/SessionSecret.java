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

import com.voltzug.cinder.core.common.valueobject.safe.SafeBlobSized;

/**
 * Server-generated cryptographic secret for HMAC verification during upload/download
 */
public final class SessionSecret extends SafeBlobSized {

  /**
   * Constructs a SessionSecret.
   *
   * @param value the secret value; must be a power-of-two length
   */
  public SessionSecret(final byte[] value) {
    super(value, true);
  }
}
