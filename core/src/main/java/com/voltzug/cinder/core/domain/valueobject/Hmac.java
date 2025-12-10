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
 * HMAC signature for request/response integrity verification
 */
public final class Hmac extends SafeBlobSized {

  /** 256b */
  private static final int _SIZE_BASE = 32;
  /** 256b */
  public static final int SIZE_MIN = 32;
  /** 512b */
  public static final int SIZE_MAX = 64;

  /**
   * Constructs a SessionSecret.
   *
   * @param value the secret value; must be a power-of-two length
   */
  public Hmac(final byte[] value) {
    super(value, SIZE_MIN, SIZE_MAX, _SIZE_BASE);
  }

  /**
   * Constructs a Salt with a specified size constraint.
   *
   * @param value the salt value as a byte array
   * @param size the required size of the salt
   */
  public Hmac(final byte[] value, int size) {
    super(value, size, size, _SIZE_BASE);
  }
}
