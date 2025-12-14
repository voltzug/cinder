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

import com.voltzug.cinder.core.common.valueobject.Blob;
import com.voltzug.cinder.core.common.valueobject.safe.SafeBlob;
import com.voltzug.cinder.core.domain.entity.SecureFile;
import com.voltzug.cinder.core.domain.valueobject.id.FileId;
import com.voltzug.cinder.core.domain.valueobject.id.LinkId;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Outbound port for SecureFile persistence.
 * Provides CRUD operations for encrypted file metadata.
 *
 * @param V gateBox type for {@link SecureFile}
 * @param C gateContext type for {@link SecureFile}
 */
public interface SecureFileRepositoryPort<V extends SafeBlob, C extends Blob> {
  /**
   * Persists a secure file entity.
   *
   * @param file the file entity to save
   */
  void save(SecureFile<V, C> file);

  /**
   * Finds a secure file by its public link identifier.
   *
   * @param linkId the link identifier
   * @return an Optional containing the file if found
   */
  Optional<SecureFile<V, C>> findByLinkId(LinkId linkId);

  /**
   * Deletes a secure file by its internal file identifier.
   *
   * @param fileId the file identifier
   */
  void deleteById(FileId fileId);

  /**
   * Deletes a secure file by related link identifier.
   *
   * @param linkId the link identifier
   */
  void deleteByLinkId(LinkId linkId);

  /**
   * Finds all files that have expired before the given timestamp.
   * Used for cleanup jobs.
   *
   * @param timestamp the cutoff timestamp
   * @return a list of expired files
   */
  List<SecureFile<V, C>> findExpiredBefore(Instant timestamp);
}
