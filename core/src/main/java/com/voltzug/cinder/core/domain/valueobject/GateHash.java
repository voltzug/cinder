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
 * Hash of `plain_answers||quizANonce` stored server-side for quiz verification
 */
public class GateHash extends SafeBlobSized {

  /** 32b */
  private static final int _SIZE_BASE = 4;
  /** 160b - SHA-1 */
  public static final int SIZE_MIN = 20;
  /** 512b - SHA-512 */
  public static final int SIZE_MAX = 64;

  /**
   * Constructs a GateHash.
   *
   * @param value the hash value; must be within allowed size range
   */
  public GateHash(final byte[] value) {
    super(value, true);
  }

  /**
   * Constructs a GateHash with a specified size constraint.
   *
   * @param value the hash value as a byte array
   * @param size the required size of the hash
   */
  public GateHash(final byte[] value, int size) {
    super(value, size, size, _SIZE_BASE);
  }
}
