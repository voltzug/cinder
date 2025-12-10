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
package com.voltzug.cinder.core.model.upload;

import com.voltzug.cinder.core.common.valueobject.Blob;
import com.voltzug.cinder.core.domain.valueobject.Envelope;
import com.voltzug.cinder.core.domain.valueobject.FileSpecs;
import com.voltzug.cinder.core.domain.valueobject.GateHash;
import com.voltzug.cinder.core.domain.valueobject.Hmac;
import com.voltzug.cinder.core.domain.valueobject.Salt;
import com.voltzug.cinder.core.domain.valueobject.Timestamp;
import com.voltzug.cinder.core.domain.valueobject.id.SessionId;
import java.util.Objects;

/**
 * Request payload for uploading an encrypted file.
 * Contains the encrypted blob and all associated cryptographic metadata required for
 * secure storage and subsequent retrieval.
 *
 * @param sessionId          the session identifier established during handshake
 * @param encryptedFile      the encrypted file content (blob)
 * @param envelope           the encrypted key envelope (fK||fNonce sealed with quizK)
 * @param salt               the random salt used for key derivation
 * @param gateHash           the hash of the quiz answer/password for verification
 * @param encryptedQuestions the encrypted quiz questions (blob)
 * @param fileSpecs          file configuration (expiry, retry count)
 * @param hmac               HMAC signature of the payload for integrity verification
 * @param timestamp          timestamp of the request for replay protection
 */
public record UploadRequest(
  SessionId sessionId,
  Blob encryptedFile,
  Envelope envelope,
  Salt salt,
  GateHash gateHash,
  Blob encryptedQuestions,
  FileSpecs fileSpecs,
  Hmac hmac,
  Timestamp timestamp
) {
  public UploadRequest {
    Objects.requireNonNull(sessionId, "sessionId must not be null");
    Objects.requireNonNull(encryptedFile, "encryptedFile must not be null");
    Objects.requireNonNull(envelope, "envelope must not be null");
    Objects.requireNonNull(salt, "salt must not be null");
    Objects.requireNonNull(gateHash, "gateHash must not be null");
    Objects.requireNonNull(
      encryptedQuestions,
      "encryptedQuestions must not be null"
    );
    Objects.requireNonNull(fileSpecs, "fileSpecs must not be null");
    Objects.requireNonNull(hmac, "hmac must not be null");
    Objects.requireNonNull(timestamp, "timestamp must not be null");
  }
}
