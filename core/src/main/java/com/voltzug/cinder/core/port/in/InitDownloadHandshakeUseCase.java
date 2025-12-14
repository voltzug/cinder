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
import com.voltzug.cinder.core.exception.CinderException;
import com.voltzug.cinder.core.exception.InvalidLinkException;
import com.voltzug.cinder.core.exception.InvalidSessionException;
import com.voltzug.cinder.core.model.download.DownloadHandshakeChallenge;
import com.voltzug.cinder.core.model.download.DownloadHandshakeContext;

/**
 * Use case for initiating the file download handshake.
 * This step validates the link ID, checks download limits, establishes a session,
 * and retrieves the encrypted quiz questions for the client to solve.
 *
 * @param C optional context returned to the downloader
 */
public interface InitDownloadHandshakeUseCase<C>
  extends
    Handshake.IAgreeableRequest<
      DownloadHandshakeContext<C>,
      DownloadHandshakeChallenge
    > {
  /**
   * Initiates the download handshake.
   *
   * @param context the context containing the link ID
   * @return the challenge containing the session ID and encrypted questions
   */
  @Override
  DownloadHandshakeChallenge challenge(DownloadHandshakeContext<C> context)
    throws InvalidLinkException, InvalidSessionException, CinderException;
}
