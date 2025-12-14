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

import java.nio.ByteBuffer;

/**
 * Hash computed by downloader for verification against GateHash
 */
public final class AccessHash extends GateHash {

  /**
   * Constructs an AccessHash.
   *
   * @param value the hash value; must be within allowed size range
   */
  public AccessHash(final byte[] value) {
    super(value);
  }

  /**
   * Constructs an AccessHash with a specified size constraint.
   *
   * @param value the hash value as a byte array
   * @param size the required size of the hash
   */
  public AccessHash(final byte[] value, int size) {
    super(value, size);
  }

  /**
   * Checks if this AccessHash can unlock the provided GateHash.
   * Note: Throws if the sizes do not match.
   *
   * @param gate the GateHash to compare against
   * @return true if the hashes match and can unlock; false otherwise
   */
  public boolean canUnlock(final GateHash gate)
    throws IllegalArgumentException {
    int thisSize = size();
    if (thisSize != gate.size()) {
      throw new IllegalArgumentException(
        "Hash sizes do not match: " + thisSize + " != " + gate.size()
      );
    }
    ByteBuffer thisBuffer = getBuffer();
    ByteBuffer gateBuffer = gate.getBuffer();

    int diff = 0;
    for (int i = 0; i < thisSize; i++) {
      diff |= thisBuffer.get() ^ gateBuffer.get();
    }
    return diff == 0;
  }
}
