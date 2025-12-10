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

import com.voltzug.cinder.core.domain.entity.Session;
import com.voltzug.cinder.core.domain.valueobject.id.SessionId;
import java.util.Optional;

/**
 * Outbound port for session caching.
 * Provides an abstraction for storing and retrieving short-lived session data.
 * Implementations should handle expiration and eviction policies.
 */
public interface SessionCachePort {
  /**
   * Saves a session to the cache.
   */
  void save(Session session);

  /**
   * Retrieves a session from the cache by its identifier.
   *
   * @param sessionId the unique session identifier
   * @return an Optional containing the session if found, or empty otherwise
   */
  Optional<Session> get(SessionId sessionId);

  /**
   * Removes a session from the cache.
   */
  void delete(SessionId sessionId);
}
