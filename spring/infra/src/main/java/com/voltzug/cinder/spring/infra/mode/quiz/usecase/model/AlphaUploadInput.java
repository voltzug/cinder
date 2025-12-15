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
package com.voltzug.cinder.spring.infra.mode.quiz.usecase.model;

import com.voltzug.cinder.core.common.valueobject.Blob;
import com.voltzug.cinder.spring.infra.mode.quiz.entity.GateHash;
import com.voltzug.cinder.spring.infra.mode.quiz.entity.QuizQuestions;
import java.time.Instant;
import java.util.Objects;

/**
 * Input model for alpha quiz upload use case.
 *
 * <p>Contains all the data required to store an encrypted file with quiz-based access control.
 * This is a simplified version that bypasses HMAC and session handshake for alpha testing.
 *
 * @param blob               the encrypted file content
 * @param envelope           the encrypted key envelope (fK||fNonce sealed with quizK)
 * @param salt               the argon2 salt for key derivation
 * @param gateHash           the hash of answers||nonce for quiz verification
 * @param encryptedQuestions the encrypted quiz questions
 * @param expiryDate         when the file should expire
 * @param retryCount         maximum download attempts allowed
 */
public record AlphaUploadInput(
  Blob blob,
  byte[] envelope,
  byte[] salt,
  GateHash gateHash,
  QuizQuestions encryptedQuestions,
  Instant expiryDate,
  int retryCount
) {
  public AlphaUploadInput {
    Objects.requireNonNull(blob, "blob must not be null");
    Objects.requireNonNull(envelope, "envelope must not be null");
    Objects.requireNonNull(salt, "salt must not be null");
    Objects.requireNonNull(gateHash, "gateHash must not be null");
    Objects.requireNonNull(encryptedQuestions, "encryptedQuestions must not be null");
    Objects.requireNonNull(expiryDate, "expiryDate must not be null");
    if (retryCount < 1 || retryCount > 99) {
      throw new IllegalArgumentException("retryCount must be between 1 and 99");
    }
  }
}