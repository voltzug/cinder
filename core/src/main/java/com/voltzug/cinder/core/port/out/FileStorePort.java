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
import com.voltzug.cinder.core.domain.valueobject.PathReference;
import com.voltzug.cinder.core.domain.valueobject.id.FileId;
import java.util.Optional;

/**
 * Outbound port for binary file storage.
 * Abstraction for storing encrypted file blobs (local disk, S3, etc.).
 */
public interface FileStorePort {
  /**
   * Saves the encrypted blob to storage.
   *
   * @param fileId the unique file identifier (can be used to generate path)
   * @param data the encrypted data to store
   * @return the reference path to the stored blob
   */
  PathReference save(FileId fileId, Blob data);

  /**
   * Loads the encrypted blob from storage.
   *
   * @param path the reference path to the blob
   * @return an Optional containing the blob if found
   */
  Optional<Blob> load(PathReference path);

  /**
   * Deletes the blob from storage.
   *
   * @param path the reference path to the blob
   */
  void delete(PathReference path);
}
