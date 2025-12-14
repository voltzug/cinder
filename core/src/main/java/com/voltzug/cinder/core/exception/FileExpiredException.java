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

import com.voltzug.cinder.core.domain.valueobject.id.LinkId;

/**
 * Exception thrown when an access link has expired and can no longer be used.
 */
public class FileExpiredException extends InvalidLinkException {

  public FileExpiredException(LinkId id) {
    super(id, "Access link has expired: " + id.value());
  }

  public FileExpiredException(LinkId id, String message) {
    super(id, message);
  }
}
