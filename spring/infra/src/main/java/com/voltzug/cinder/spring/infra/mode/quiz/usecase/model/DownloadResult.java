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
import java.util.Objects;

/**
 * Result of a successful download verification.
 * Contains the encrypted file blob and unsealed cryptographic metadata
 * required for client-side decryption.
 *
 * @param blob     the encrypted file content
 * @param envelope the unsealed envelope (fK||fNonce)
 * @param salt     the unsealed salt for key derivation
 */
public record DownloadResult(
  Blob blob,
  byte[] envelope,
  byte[] salt
) {
  public DownloadResult {
    Objects.requireNonNull(blob, "blob must not be null");
    Objects.requireNonNull(envelope, "envelope must not be null");
    Objects.requireNonNull(salt, "salt must not be null");
    if (envelope.length == 0) {
      throw new IllegalArgumentException("envelope must not be empty");
    }
    if (salt.length == 0) {
      throw new IllegalArgumentException("salt must not be empty");
    }
  }
}