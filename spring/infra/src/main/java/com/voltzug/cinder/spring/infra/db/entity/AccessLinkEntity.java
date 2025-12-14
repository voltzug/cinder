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
package com.voltzug.cinder.spring.infra.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA entity mapping for the access_link table.
 * Stores link-specific metadata such as remaining attempts, gate box, and gate context.
 * Has a 1:1 relationship with SecureFileEntity, owning the relationship via FK.
 *
 * <p>This entity is created AFTER SecureFileEntity, allowing SafeFile to be
 * persisted first before download limits are configured.</p>
 */
@Entity
@Table(name = "access_link")
@Getter
@NoArgsConstructor
public class AccessLinkEntity {

  @Id
  @Column(name = "link_id", nullable = false, updatable = false, length = 64)
  private String id;

  @Column(name = "remaining_attempts", nullable = false)
  @Setter
  private int remainingAttempts;

  @Lob
  @Column(name = "gate_box", nullable = false)
  @Setter
  private byte[] gateBox;

  @Lob
  @Column(name = "gate_context")
  @Setter
  private byte[] gateContext;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  @Setter
  private Instant updatedAt;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
    name = "file_id",
    nullable = false,
    unique = true,
    updatable = false
  )
  private SecureFileEntity secureFile;

  /**
   * Constructs a new AccessLinkEntity with all required fields.
   *
   * @param id                the unique link identifier
   * @param remainingAttempts remaining download attempts
   * @param gateBox           the gate mechanism data (e.g., hash or challenge)
   * @param gateContext       the gate context data (may be null, eg. encrypted quiz questions)
   * @param createdAt         the creation timestamp
   * @param updatedAt         the last updated timestamp
   * @param secureFile        the associated SecureFileEntity (must exist before this entity)
   */
  public AccessLinkEntity(
    String id,
    int remainingAttempts,
    byte[] gateBox,
    byte[] gateContext,
    Instant createdAt,
    Instant updatedAt,
    SecureFileEntity secureFile
  ) {
    this.id = id;
    this.remainingAttempts = remainingAttempts;
    this.gateBox = gateBox;
    this.gateContext = gateContext;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.secureFile = secureFile;
  }

  /**
   * Creates a new AccessLinkEntity for initializing download limits.
   * Used when configuring download limits after SecureFileEntity is already saved.
   *
   * @param linkId            the unique link identifier (matches SecureFileEntity.linkId)
   * @param remainingAttempts initial remaining download attempts from FileSpecs
   * @param gateBox           the gate mechanism data
   * @param gateContext       the gate context data (may be null)
   * @param now               the current timestamp for created/updated
   * @param secureFile        the associated SecureFileEntity
   * @return a new AccessLinkEntity ready to be persisted
   */
  public static AccessLinkEntity forInitialization(
    String linkId,
    int remainingAttempts,
    byte[] gateBox,
    byte[] gateContext,
    Instant now,
    SecureFileEntity secureFile
  ) {
    return new AccessLinkEntity(
      linkId,
      remainingAttempts,
      gateBox,
      gateContext,
      now,
      now,
      secureFile
    );
  }
}
