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
package com.voltzug.cinder.core.exception;

import com.voltzug.cinder.core.domain.valueobject.id.FileId;

/**
 * Exception thrown when a requested file cannot be found in the system.
 */
public final class FileNotFoundException extends FileStorageException {

  private final FileId fileId;

  public FileNotFoundException(FileId fileId) {
    super("File not found: " + fileId.value());
    this.fileId = fileId;
  }

  public FileNotFoundException(FileId fileId, String message) {
    super(message);
    this.fileId = fileId;
  }

  public FileId getFileId() {
    return fileId;
  }
}
