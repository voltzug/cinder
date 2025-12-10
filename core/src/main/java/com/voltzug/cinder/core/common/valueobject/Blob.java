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
package com.voltzug.cinder.core.common.valueobject;

import com.voltzug.cinder.core.common.utils.SafeArrays;
import java.nio.ByteBuffer;
import java.util.Base64;

/**
 * Regular immutable blob. Use for:
 * <ul>
 *   <li>Encrypted file contents (already encrypted, so safe to hold)</li>
 *   <li>Non-sensitive binary data</li>
 *   <li>When you don't need automatic memory wiping</li>
 * </ul>
 */
public class Blob {

  protected final byte[] buffer;

  public Blob(final byte[] value) {
    SafeArrays.assertNotEmpty(value);
    buffer = value;
  }

  /**
   * Creates an Blob from a Base64 encoded string.
   *
   * @param base64 the Base64 encoded blob
   * @return a Blob instance
   */
  public static Blob fromBase64(String base64) {
    if (base64 == null) {
      throw new IllegalArgumentException("Base64 string must not be null");
    }
    byte[] decoded = Base64.getDecoder().decode(base64);
    return new Blob(decoded);
  }

  /**
   * Returns a read-only {@link ByteBuffer} view of the underlying blob bytes.
   * The returned buffer reflects the current contents of the blob, but cannot be modified.
   *
   * @return a read-only {@link ByteBuffer} containing the blob data
   */
  public ByteBuffer getBuffer() {
    return ByteBuffer.wrap(buffer).asReadOnlyBuffer();
  }

  /**
   * Converts the blob to a Base64 encoded string.
   *
   * @return Base64 encoded blob
   */
  public CharSequence toBase64() {
    return Base64.getEncoder().encodeToString(buffer);
  }

  /**
   * Returns the size of the blob in bytes.
   *
   * @return the size in bytes
   */
  public int size() {
    return buffer.length;
  }
}
