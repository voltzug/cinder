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
package com.voltzug.cinder.core.common.valueobject;

import java.util.Objects;

/**
 * Value object representing an identifier with a typed prefix.
 */
public abstract class Id {

  private final String _value;
  private final Prefix _prefix;

  protected Id(final String value, final Prefix prefix) {
    if (value == null || prefix == null) {
      throw new IllegalArgumentException("value and prefix must not be null");
    }
    this._value = value;
    this._prefix = prefix;
  }

  /** Returns the value of the identifier. */
  public String value() {
    return _value;
  }

  /** Returns the prefix of the identifier. */
  public Prefix prefix() {
    return _prefix;
  }

  @Override
  public String toString() {
    return _prefix.code() + _value;
  }

  @Override
  public int hashCode() {
    return Objects.hash(_prefix, _value);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) return true;
    if (false == (other instanceof Id)) return false;
    Id o = (Id) other;
    return _prefix == o._prefix && _value == o._value;
  }

  /** Prefix for an identifier. */
  public interface Prefix {
    /** Returns the code of the prefix. */
    String code();
  }
}
