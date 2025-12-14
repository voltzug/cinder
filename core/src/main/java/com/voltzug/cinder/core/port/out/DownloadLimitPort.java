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
package com.voltzug.cinder.core.port.out;

import com.voltzug.cinder.core.domain.entity.DownloadLimit;
import com.voltzug.cinder.core.domain.valueobject.FileSpecs;
import com.voltzug.cinder.core.domain.valueobject.id.LinkId;
import java.util.Optional;

/**
 * Outbound port for managing download limits.
 * Tracks remaining attempts and expiration for access links.
 * Implementations should ensure atomic updates for attempt counters.
 */
public interface DownloadLimitPort {
  /**
   * Initializes download limits for a new link based on file specifications.
   *
   * @param linkId the link identifier
   * @param specs the file specifications containing retry count and expiry
   */
  void initialize(LinkId linkId, FileSpecs specs);

  /**
   * Retrieves the current download limit state for a link.
   *
   * @param linkId the link identifier
   * @return an Optional containing the download limit if found
   */
  Optional<DownloadLimit> get(LinkId linkId);

  /**
   * Decrements the remaining attempts counter for a link.
   * This operation should be atomic to prevent race conditions during concurrent access.
   *
   * @param linkId the link identifier
   * @return the updated DownloadLimit
   * @throws com.voltzug.cinder.core.exception.MaxAttemptsExceededException when attempts reach zero
   */
  DownloadLimit decrementAttempts(LinkId linkId)
    throws com.voltzug.cinder.core.exception.MaxAttemptsExceededException;

  /**
   * Deletes the download limit record for a link.
   *
   * @param linkId the link identifier
   */
  void delete(LinkId linkId);
}
