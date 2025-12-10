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
package com.voltzug.cinder.core.domain.entity;

import java.time.Instant;

/**
 * Represents an entity that has an expiration date.
 * Implementations should provide logic to determine if the entity is expired
 * based on the current time.
 */
public interface IExpirable {
  /** Returns the expiration date of the entity */
  public Instant getExpiryDate();

  /** Determines if the entity is expired at the given current time */
  public boolean isExpired(Instant now);
}
