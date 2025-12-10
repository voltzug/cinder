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

import com.voltzug.cinder.core.model.upload.UploadRequest;
import com.voltzug.cinder.core.model.upload.UploadResult;

/**
 * Use case for processing an encrypted file upload.
 * This step verifies the integrity of the upload payload using the session secret,
 * stores the encrypted file and metadata, and generates a public access link.
 */
public interface ProcessUploadUseCase {
  /**
   * Processes the upload request.
   *
   * @param request the upload request containing the encrypted file and metadata
   * @return the result containing the generated access link ID
   */
  UploadResult processUpload(UploadRequest request);
}
