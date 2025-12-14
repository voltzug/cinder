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
package com.voltzug.cinder.core.domain.entity;

import com.voltzug.cinder.core.domain.valueobject.FileSpecs;
import com.voltzug.cinder.core.domain.valueobject.PathReference;
import com.voltzug.cinder.core.domain.valueobject.SealedBlob;
import com.voltzug.cinder.core.domain.valueobject.id.FileId;
import com.voltzug.cinder.core.domain.valueobject.id.LinkId;
import com.voltzug.cinder.core.domain.valueobject.id.UserId;
import java.time.Instant;
import java.util.Objects;

/**
 * Represents a stored encrypted file with all associated cryptographic metadata.
 * Contains server-sealed sensitive data and download attempt tracking.
 *
 * @param fileId             Unique file identifier
 * @param linkId             Unique link identifier
 * @param linkId             Unique fileowner identifier
 * @param blobPath           Reference to the file's storage location
 * @param sealedEnvelope     Server-sealed envelope containing file key and nonce
 * @param sealedSalt         Server-sealed salt for key derivation
 * @param specs              File specification parameters (expiry, retry count)
 * @param remainingAttempts  Remaining download attempts
 * @param createdAt          Creation timestamp
 * @param gateBox            Generic gate mechanism for access control (type V), such as a hash for quiz/answer verification or other challenge data
 * @param gateContext        Generic gate context (type C), may be null depending on the gate mechanism (eg. encrypted quiz questions)
 */
public record SecureFile<V, C>(
  FileId fileId,
  LinkId linkId,
  UserId userId,
  PathReference blobPath,
  SealedBlob sealedEnvelope,
  SealedBlob sealedSalt,
  FileSpecs specs,
  int remainingAttempts,
  Instant createdAt,
  V gateBox,
  C gateContext
) implements IExpirable {
  public SecureFile {
    Objects.requireNonNull(fileId, "fileId must not be null");
    Objects.requireNonNull(linkId, "linkId must not be null");
    Objects.requireNonNull(userId, "userId must not be null");
    Objects.requireNonNull(blobPath, "blobPath must not be null");
    Objects.requireNonNull(sealedEnvelope, "sealedEnvelope must not be null");
    Objects.requireNonNull(sealedSalt, "sealedSalt must not be null");
    Objects.requireNonNull(specs, "specs must not be null");
    Objects.requireNonNull(createdAt, "createdAt must not be null");
    Objects.requireNonNull(gateBox, "gateBox must not be null");
    // gateContext can be null
  }

  @Override
  public Instant getExpiryDate() {
    return specs.getExpiryDate();
  }

  @Override
  public boolean isExpired(Instant now) {
    return specs.isExpired(now);
  }

  @Override
  public final int hashCode() {
    return Objects.hash(
      fileId,
      linkId,
      blobPath,
      sealedEnvelope,
      sealedSalt,
      specs,
      remainingAttempts,
      createdAt,
      gateBox,
      gateContext
    );
  }

  @Override
  public final boolean equals(Object other) {
    if (this == other) return true;
    if (getClass() != other.getClass()) return false;
    SecureFile<?, ?> o = (SecureFile<?, ?>) other;
    return (
      remainingAttempts == o.remainingAttempts &&
      Objects.equals(createdAt, o.createdAt) &&
      Objects.equals(specs, o.specs) &&
      Objects.equals(fileId, o.fileId) &&
      Objects.equals(linkId, o.linkId) &&
      Objects.equals(blobPath, o.blobPath) &&
      Objects.equals(gateBox, o.gateBox) &&
      Objects.equals(sealedSalt, o.sealedSalt) &&
      Objects.equals(sealedEnvelope, o.sealedEnvelope) &&
      Objects.equals(gateContext, o.gateContext)
    );
  }
}
