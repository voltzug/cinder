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

import com.voltzug.cinder.core.domain.valueobject.id.LinkId;
import java.util.Objects;

/**
 * Context for initiating a download handshake.
 * Contains the link identifier extracted from the access URL.
 *
 * @param linkId the unique link identifier for the file to be downloaded
 * @param context additional context for the handshake, may be null
 */
public record DownloadHandshakeContext<C extends Object>(
  LinkId linkId,
  C context
) {
  public DownloadHandshakeContext {
    Objects.requireNonNull(linkId, "linkId must not be null");
    // context can be null
  }
}
