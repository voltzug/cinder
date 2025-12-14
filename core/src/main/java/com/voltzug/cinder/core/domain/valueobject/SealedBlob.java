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

import com.voltzug.cinder.core.common.valueobject.Blob;
import java.nio.ByteBuffer;

public class SealedBlob extends Blob {

  protected final short pepperVersion, nonceLength;
  protected final int valueLength;

  public SealedBlob(final byte[] value) {
    super(value);
    int lng = value.length;
    // 2+2 + >1+>1
    if (lng < 6) {
      throw new IllegalArgumentException("Value must be at least 6 bytes");
    }
    ByteBuffer bb = ByteBuffer.wrap(value);
    pepperVersion = bb.getShort();
    nonceLength = bb.getShort();
    valueLength = lng - nonceLength - 4;
    if (valueLength < 1) {
      throw new IllegalArgumentException(
        "Value length must be at least 1 byte after nonce and header"
      );
    }
  }

  /**
   * Builds a SealedBlob from value, nonce, and pepperVersion.
   */
  public static SealedBlob build(
    final byte[] value,
    final byte[] nonce,
    final short pepperVersion
  ) {
    short nonceLng = (short) nonce.length;
    ByteBuffer bb = ByteBuffer.allocate(value.length + nonceLng + 4);
    bb.putShort(pepperVersion);
    bb.putShort(nonceLng);
    bb.put(nonce);
    bb.put(value);
    return new SealedBlob(bb.array());
  }

  public short getPepperVersion() {
    return pepperVersion;
  }

  public byte[] getNonce() {
    byte[] nonce = new byte[nonceLength];
    ByteBuffer bb = ByteBuffer.wrap(buffer, 4, nonceLength);
    bb.get(nonce);
    return nonce;
  }

  public byte[] getValue() {
    byte[] value = new byte[valueLength];
    ByteBuffer bb = ByteBuffer.wrap(buffer, 4 + nonceLength, valueLength);
    bb.get(value);
    return value;
  }
}
