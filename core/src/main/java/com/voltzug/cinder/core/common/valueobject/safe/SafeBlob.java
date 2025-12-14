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
package com.voltzug.cinder.core.common.valueobject.safe;

import com.voltzug.cinder.core.common.contract.IResolvable;
import com.voltzug.cinder.core.common.utils.SafeArrays;
import com.voltzug.cinder.core.common.valueobject.Blob;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;

/**
 * Secure blob with automatic memory sealing. Use for:
 * - Encryption keys (K1, K2)
 * - Salts (S1, S2)
 * - Derived key material
 * - Any unencrypted sensitive data
 *
 * Always use try-with-resources or explicit close()
 */
public class SafeBlob
  extends Blob
  implements AutoCloseable, IResolvable<byte[]> {

  protected boolean _wasResolved = false;

  private static byte[] _validateBuffer(final byte[] value) {
    SafeArrays.assertIsArray(value);
    return SafeArrays.move(value);
  }

  /**
   * Constructs a new {@link SafeBlob} by moving the provided byte array.
   * The input array is zeroed out after being moved.
   *
   * @param value the byte array to wrap (will be moved and zeroed)
   */
  public SafeBlob(final byte[] value) {
    super(_validateBuffer(value));
  }

  private SafeBlob(final byte[] buffer, boolean _wasMoved) {
    super(buffer);
  }

  /**
   * Decodes a Base64-encoded string into a new {@link SafeBlob}.
   *
   * @param base64 the Base64-encoded string to decode
   * @return a new {@link SafeBlob} containing the decoded bytes
   * @throws IllegalArgumentException if {@code base64} is null
   */
  public static SafeBlob fromBase64(String base64) {
    if (base64 == null) {
      throw new IllegalArgumentException("base64 cannot be null");
    }
    return new SafeBlob(Base64.getDecoder().decode(base64), true);
  }

  /**
   * Returns the underlying byte array buffer.
   * <p>
   * <b>Security Note:</b> Use with care! This exposes the internal buffer directly.
   * Modifying or leaking this array may compromise sensitive data.
   *
   * @return the internal byte array buffer
   */
  public byte[] getBytes() {
    if (_wasResolved) {
      throw new IllegalStateException("has been resolved");
    }
    return buffer;
  }

  @Override
  public boolean isResolved() {
    return _wasResolved;
  }

  @Override
  public ByteBuffer getBuffer() {
    if (_wasResolved) {
      throw new IllegalStateException("has been resolved");
    }
    return super.getBuffer();
  }

  /**
   * Encodes the blob's contents to a Base64 string, wrapped in a {@link SafeString}.
   *
   * @return a {@link SafeString} containing the Base64-encoded representation of the blob
   */
  @Override
  public SafeString toBase64() {
    if (_wasResolved) {
      throw new IllegalStateException("has been resolved");
    }
    byte[] bytes = Base64.getEncoder().encode(buffer);
    return new SafeString(bytes);
  }

  @Override
  public byte[] resolve() {
    if (_wasResolved) {
      throw new IllegalStateException("has been resolved");
    }
    _wasResolved = true;
    return buffer;
  }

  /**
   * Seals (zeroes out) the underlying byte array buffer for security.
   * This method should be called when the blob is no longer needed.
   *
   * @throws Exception if sealing fails
   */
  @Override
  public void close() {
    SafeArrays.seal(buffer);
    _wasResolved = true;
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(buffer);
  }
}
