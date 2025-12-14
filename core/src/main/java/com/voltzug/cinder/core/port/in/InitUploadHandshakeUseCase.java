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
package com.voltzug.cinder.core.port.in;

import com.voltzug.cinder.core.common.contract.Handshake;
import com.voltzug.cinder.core.exception.CryptoOperationException;
import com.voltzug.cinder.core.model.upload.UploadHandshakeChallenge;

/**
 * Use case for initiating the file upload handshake.
 * This step establishes a session and provides the necessary cryptographic secrets
 * (challenge) for the client to prepare the encrypted payload.
 */
public interface InitUploadHandshakeUseCase
  extends Handshake.IAgreeableRequest<Void, UploadHandshakeChallenge> {
  /**
   * Initiates the upload handshake.
   *
   * @param context the context for the handshake (e.g. PoW parameters)
   * @return the challenge containing the session ID and upload secret
   * @throws CryptoOperationException when a cryptographic operation (random generation, HMAC) fails
   */
  @Override
  UploadHandshakeChallenge challenge(Void _void)
    throws CryptoOperationException;
}
