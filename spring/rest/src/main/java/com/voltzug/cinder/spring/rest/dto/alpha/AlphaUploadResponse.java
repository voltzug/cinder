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

import java.time.Instant;
import java.util.Objects;

/**
 * Response DTO for alpha quiz upload endpoint.
 *
 * <p>Returned after a successful file upload, containing the generated link identifier
 * that the client uses to compose the access URL.
 *
 * @param linkId     the generated link identifier (with LK prefix)
 * @param expiryDate when the file will expire
 */
public record AlphaUploadResponse(
  String linkId,
  Instant expiryDate
) {
  public AlphaUploadResponse {
    Objects.requireNonNull(linkId, "linkId must not be null");
    Objects.requireNonNull(expiryDate, "expiryDate must not be null");
  }
}