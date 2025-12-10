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
package com.voltzug.cinder.core.domain;

import com.voltzug.cinder.core.common.valueobject.Id;
import com.voltzug.cinder.core.domain.valueobject.id.FileId;
import com.voltzug.cinder.core.domain.valueobject.id.IdPrefix;
import com.voltzug.cinder.core.domain.valueobject.id.LinkId;
import com.voltzug.cinder.core.domain.valueobject.id.SessionId;
import com.voltzug.cinder.core.domain.valueobject.id.UserId;

public final class IdManager {

  public static Id from(final String value) {
    if (value == null || value.length() < IdPrefix.PREFIX_LENGTH + 1) {
      throw new IllegalArgumentException("value is too short or null");
    }
    String prefixRaw = value.substring(0, IdPrefix.PREFIX_LENGTH);
    try {
      IdPrefix prefix = IdPrefix.fromCode(prefixRaw);
      String val = value.substring(IdPrefix.PREFIX_LENGTH);
      switch (prefix) {
        case SESSION:
          return new SessionId(val);
        case LINK:
          return new LinkId(val);
        case FILE:
          return new FileId(val);
        case USER:
          return new UserId(val);
      }
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid Id prefix: " + prefixRaw, e);
    }
    throw new IllegalArgumentException("Unsupported Id prefix: " + prefixRaw);
  }
}
