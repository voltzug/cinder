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
import com.voltzug.cinder.core.exception.AccessVerificationException;
import com.voltzug.cinder.core.exception.CryptoOperationException;
import com.voltzug.cinder.core.exception.FileStorageException;
import com.voltzug.cinder.core.exception.HmacVerificationException;
import com.voltzug.cinder.core.exception.InvalidSessionException;
import com.voltzug.cinder.core.exception.MaxAttemptsExceededException;
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
   * @throws HmacVerificationException when HMAC validation of the request fails
   * @throws AccessVerificationException when the access/hash check fails (wrong quiz answer)
   * @throws MaxAttemptsExceededException when download attempts are exhausted
   * @throws InvalidSessionException when the session is invalid or expired
   * @throws FileStorageException when an error occurs reading the stored file blob
   * @throws CryptoOperationException when a cryptographic operation (unseal, decrypt, hmac) fails
   */
  @Override
  DownloadVerifyResult verify(DownloadVerifyRequest<A> request)
    throws InvalidSessionException, HmacVerificationException, AccessVerificationException, MaxAttemptsExceededException, FileStorageException, CryptoOperationException;
}
