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
package com.voltzug.cinder.spring.infra.db.repository;

import com.voltzug.cinder.spring.infra.db.entity.AccessLinkEntity;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * JPA repository for {@link AccessLinkEntity}.
 * Provides persistence operations for access link metadata including download limits.
 */
public interface AccessLinkRepository
  extends JpaRepository<AccessLinkEntity, String> {
  /**
   * Atomically decrements the remaining attempts counter for a link.
   * Only decrements if remaining attempts is greater than zero.
   * Also updates the updatedAt timestamp.
   *
   * @param linkId the link identifier
   * @param now    the current timestamp for updatedAt
   * @return the number of rows updated (0 if no attempts remaining or link not found)
   */
  @Modifying
  @Query(
    "UPDATE AccessLinkEntity a SET a.remainingAttempts = a.remainingAttempts - 1, " +
      "a.updatedAt = :now WHERE a.id = :linkId AND a.remainingAttempts > 0"
  )
  int decrementAttempts(
    @Param("linkId") String linkId,
    @Param("now") Instant now
  );
}
