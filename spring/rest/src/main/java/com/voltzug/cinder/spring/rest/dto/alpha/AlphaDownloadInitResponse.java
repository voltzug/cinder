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
 * Response DTO for initializing a download session in alpha quiz mode.
 *
 * <p>Contains the session identifier and encrypted quiz questions.
 * The client uses the questionK from the URL fragment to decrypt the questions,
 * then computes the access hash from the answers to verify the download.
 *
 * @param sessionId          the unique session identifier for this download attempt
 * @param encryptedQuestions the encrypted quiz questions (Base64 encoded)
 */
public record AlphaDownloadInitResponse(
  String sessionId,
  String encryptedQuestions
) {
  public AlphaDownloadInitResponse {
    Objects.requireNonNull(sessionId, "sessionId must not be null");
    Objects.requireNonNull(encryptedQuestions, "encryptedQuestions must not be null");
    if (sessionId.isBlank()) {
      throw new IllegalArgumentException("sessionId must not be blank");
    }
    if (encryptedQuestions.isBlank()) {
      throw new IllegalArgumentException("encryptedQuestions must not be blank");
    }
  }
}