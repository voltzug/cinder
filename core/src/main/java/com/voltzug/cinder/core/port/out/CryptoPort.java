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
package com.voltzug.cinder.core.port.out;

import com.voltzug.cinder.core.domain.valueobject.Hmac;
import com.voltzug.cinder.core.domain.valueobject.SessionSecret;
import com.voltzug.cinder.core.exception.CryptoOperationException;

/**
 * Outbound port for cryptographic operations.
 * Provides abstractions for random generation and HMAC operations.
 * Implementations must ensure cryptographic security and timing safety.
 */
public interface CryptoPort {
  /**
   * Generates a secure random byte array of the specified length.
   *
   * @param length the number of bytes to generate
   * @return a byte array containing random bytes
   */
  byte[] randomBytes(int length);

  /**
   * Computes an HMAC-SHA512 signature for the given data using the provided secret.
   *
   * @param secret the secret key
   * @param data   the data to sign
   * @return the computed HMAC
   * @throws CryptoOperationException when HMAC computation fails
   */
  Hmac hmac(SessionSecret secret, byte[] data) throws CryptoOperationException;

  /**
   * Verifies an HMAC signature in a timing-safe manner.
   *
   * @param secret       the secret key
   * @param data         the data to verify
   * @param expectedHmac the expected HMAC signature
   * @return true if the signature is valid, false otherwise
   * @throws CryptoOperationException when verification could not be performed (e.g. underlying crypto error)
   */
  boolean verifyHmac(SessionSecret secret, byte[] data, Hmac expectedHmac)
    throws CryptoOperationException;
}
