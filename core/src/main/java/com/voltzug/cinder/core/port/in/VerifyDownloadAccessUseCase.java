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
import com.voltzug.cinder.core.model.download.DownloadVerifyRequest;
import com.voltzug.cinder.core.model.download.DownloadVerifyResult;

/**
 * Use case for verifying the downloader's access (eg. solving the quiz).
 * This step authenticates the downloader, enforces download limits,
 * and if successful, returns the encrypted file and cryptographic metadata.
 *
 * @param A mode-agnostic access key type provided by the downloader
 */
public interface VerifyDownloadAccessUseCase<A>
  extends
    Handshake.IAgreeableResponse<
      DownloadVerifyRequest<A>,
      DownloadVerifyResult
    > {
  /**
   * Verifies the download access request.
   *
   * @param request the verification request containing the quiz answer hash
   * @return the result containing the encrypted file and metadata
   */
  @Override
  DownloadVerifyResult verify(DownloadVerifyRequest<A> request);
}
