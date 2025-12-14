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

import com.voltzug.cinder.spring.infra.db.entity.SecureFileEntity;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for SecureFileEntity.
 * Provides CRUD operations and custom queries for secure file persistence.
 */
public interface SecureFileRepository
  extends JpaRepository<SecureFileEntity, String> {
  /**
   * Finds a secure file by its associated link identifier.
   *
   * @param linkId the link identifier
   * @return an Optional containing the file if found
   */
  Optional<SecureFileEntity> findByLinkId(String linkId);

  /**
   * Deletes a secure file by its associated link identifier.
   *
   * @param linkId the link identifier
   */
  void deleteByLinkId(String linkId);

  /**
   * Finds all files that have expired before the given timestamp.
   * Used for cleanup jobs to identify files ready for burning.
   *
   * @param timestamp the cutoff timestamp
   * @return a list of expired files
   */
  List<SecureFileEntity> findByExpiryDateBefore(Instant timestamp);
}
