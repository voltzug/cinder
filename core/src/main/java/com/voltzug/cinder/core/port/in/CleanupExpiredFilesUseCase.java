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
package com.voltzug.cinder.core.port.in;

import com.voltzug.cinder.core.exception.FileStorageException;

/**
 * Use case for cleaning up expired files and links from the system.
 * This is typically executed by a scheduled job to remove expired content.
 */
public interface CleanupExpiredFilesUseCase {
  /**
   * Cleans up all expired files and their associated links.
   *
   * @return the number of files cleaned up
   */
  int cleanupExpiredFiles() throws FileStorageException;
}
