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

import com.voltzug.cinder.core.domain.valueobject.Hmac;
import com.voltzug.cinder.core.domain.valueobject.SessionSecret;
import com.voltzug.cinder.core.domain.valueobject.Timestamp;
import com.voltzug.cinder.core.domain.valueobject.id.SessionId;
import java.util.Objects;

/**
 * Request payload for verifying a download attempt (e.g., solving the quiz or authenticating via advanced mode).
 * Contains the client's mode-agnostic access key (such as a quiz answer hash or password proof) and a new secret for securing the response.
 *
 * @param sessionId      the session identifier established during handshake
 * @param accessKey      the mode-agnostic access key computed by the client (e.g., quiz answer hash in lite mode, password proof in advanced mode)
 * @param downloadSecret the client-generated secret for HMAC signing of the response
 * @param hmac           HMAC signature of the request for integrity verification
 * @param timestamp      timestamp of the request for replay protection
 */
public record DownloadVerifyRequest<A>(
  SessionId sessionId,
  A accessKey,
  SessionSecret downloadSecret,
  Hmac hmac,
  Timestamp timestamp
) {
  public DownloadVerifyRequest {
    Objects.requireNonNull(sessionId, "sessionId must not be null");
    Objects.requireNonNull(accessKey, "accessKey must not be null");
    Objects.requireNonNull(downloadSecret, "downloadSecret must not be null");
    Objects.requireNonNull(hmac, "hmac must not be null");
    Objects.requireNonNull(timestamp, "timestamp must not be null");
  }
}
