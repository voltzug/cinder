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
package com.voltzug.cinder.core.model.download;

import com.voltzug.cinder.core.common.valueobject.Blob;
import com.voltzug.cinder.core.domain.valueobject.Envelope;
import com.voltzug.cinder.core.domain.valueobject.Hmac;
import com.voltzug.cinder.core.domain.valueobject.Salt;
import java.util.Objects;

/**
 * Result of a successful download verification.
 * Contains the encrypted file and cryptographic metadata required for decryption,
 * signed with the client-provided download secret.
 *
 * @param encryptedFile the encrypted file content (blob)
 * @param salt          the random salt used for key derivation
 * @param envelope      the encrypted key envelope (fK||fNonce sealed with quizK)
 * @param hmac          HMAC signature of the response using the download secret
 */
public record DownloadVerifyResult(
  Blob encryptedFile,
  Salt salt,
  Envelope envelope,
  Hmac hmac
) {
  public DownloadVerifyResult {
    Objects.requireNonNull(encryptedFile, "encryptedFile must not be null");
    Objects.requireNonNull(salt, "salt must not be null");
    Objects.requireNonNull(envelope, "envelope must not be null");
    Objects.requireNonNull(hmac, "hmac must not be null");
  }
}
