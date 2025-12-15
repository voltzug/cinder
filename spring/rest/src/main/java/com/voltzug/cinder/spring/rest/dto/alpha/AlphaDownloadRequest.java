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
package com.voltzug.cinder.spring.rest.dto.alpha;

import java.util.Objects;

/**
 * Request DTO for alpha download verification.
 *
 * <p>Used to submit the access hash for quiz verification after the client
 * has decrypted and answered the quiz questions.
 *
 * <p><strong>Request Body Example:</strong>
 * <pre>{@code
 * {
 *   "sessionId": "SN<uuid>",
 *   "accessHash": "<base64-encoded SHA256 of answers||nonce>"
 * }
 * }</pre>
 *
 * @param sessionId  the session identifier returned from download init
 * @param accessHash Base64-encoded SHA256 hash of (plain_answers || quizANonce)
 */
public record AlphaDownloadRequest(
  String sessionId,
  String accessHash
) {
  public AlphaDownloadRequest {
    Objects.requireNonNull(sessionId, "sessionId must not be null");
    Objects.requireNonNull(accessHash, "accessHash must not be null");
    if (sessionId.isBlank()) {
      throw new IllegalArgumentException("sessionId must not be blank");
    }
    if (accessHash.isBlank()) {
      throw new IllegalArgumentException("accessHash must not be blank");
    }
  }
}