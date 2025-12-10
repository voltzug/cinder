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
import com.voltzug.cinder.core.domain.valueobject.id.SessionId;
import java.util.Objects;

/**
 * Challenge issued by the server during download handshake.
 * Contains the session identifier and the encrypted quiz questions that the client must answer.
 *
 * @param sessionId          the unique session identifier
 * @param encryptedQuestions the encrypted quiz questions (blob)
 */
public record DownloadHandshakeChallenge(
  SessionId sessionId,
  Blob encryptedQuestions
) {
  public DownloadHandshakeChallenge {
    Objects.requireNonNull(sessionId, "sessionId must not be null");
    Objects.requireNonNull(
      encryptedQuestions,
      "encryptedQuestions must not be null"
    );
  }
}
