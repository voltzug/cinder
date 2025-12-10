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
package com.voltzug.cinder.core.domain.valueobject.id;

import com.voltzug.cinder.core.common.valueobject.Id;

public enum IdPrefix implements Id.Prefix {
  SESSION("SN"),
  FILE("FL"),
  LINK("LK"),
  USER("US");

  public static final int PREFIX_LENGTH = 2;

  private final String code;

  private IdPrefix(String code) {
    this.code = code;
  }

  public static IdPrefix fromCode(String code) {
    for (IdPrefix p : values()) {
      if (p.code.equals(code)) {
        return p;
      }
    }
    throw new IllegalArgumentException("Unknown Id prefix: " + code);
  }

  @Override
  public String code() {
    return code;
  }

  @Override
  public String toString() {
    return code;
  }
}
