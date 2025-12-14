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

import com.voltzug.cinder.core.domain.valueobject.SealedBlob;
import com.voltzug.cinder.core.exception.CryptoOperationException;

/**
 * Outbound port for pepper (server-side secret) operations.
 * Handles the encryption and decryption of sensitive metadata using versioned server-side secrets.
 */
public interface PepperPort {
  /** Returns the current active pepper version */
  short actualPepperVersion();

  /**
   * Seals (encrypts) the given data using the current active pepper.
   *
   * @param data the plain data to seal
   * @return a SealedBlob containing the encrypted data, nonce, and pepper version
   */
  SealedBlob seal(byte[] data) throws CryptoOperationException;

  /**
   * Unseals (decrypts) the given SealedBlob using the appropriate pepper version.
   *
   * @param sealedBlob the sealed blob to decrypt
   * @return the decrypted plain data
   */
  byte[] unseal(SealedBlob sealedBlob) throws CryptoOperationException;
}
