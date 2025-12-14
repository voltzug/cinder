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
import com.voltzug.cinder.core.exception.FileStorageException;

/**
 * Outbound port for binary file storage.
 * Abstraction for storing encrypted file blobs (local disk, S3, etc.).
 */
public interface FileStorePort {
  /** Checks if a file with the given {@link PathReference} exists in the storage */
  public boolean exists(PathReference path);

  /**
   * Saves the encrypted blob to storage.
   *
   * @param data the encrypted data to store
   * @return the reference path to the stored blob
   */
  PathReference save(Blob data) throws FileStorageException;

  /**
   * Loads the encrypted blob from storage.
   *
   * @param path the reference path to the blob
   * @return the blob if found
   */
  Blob load(PathReference path) throws FileStorageException;

  /**
   * Deletes the blob from storage.
   *
   * @param path the reference path to the blob
   */
  void delete(PathReference path) throws FileStorageException;
}
