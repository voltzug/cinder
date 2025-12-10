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

import com.voltzug.cinder.core.model.download.DownloadAcknowledgment;

/**
 * Use case for acknowledging a successful download.
 * This step confirms that the client has successfully received and decrypted the file,
 * triggering the immediate deletion (burning) of the file from storage.
 */
public interface AcknowledgeDownloadUseCase {
  /**
   * Acknowledges the successful download of a file.
   *
   * @param acknowledgment the acknowledgment containing the session ID
   */
  void acknowledgeDownload(DownloadAcknowledgment acknowledgment);
}
