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

import com.voltzug.cinder.spring.infra.db.config.DbLogger;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA entity mapping for the secure_file table.
 * Stores metadata about encrypted files including storage path,
 * sealed envelope, sealed salt, owner information, and expiry date.
 *
 * <p>This entity can be persisted independently before the associated
 * {@link AccessLinkEntity} is created. The 1:1 relationship is owned
 * by {@link AccessLinkEntity} via FK, enabling SafeFile-first creation flow.</p>
 */
@Entity
@Table(name = "secure_file")
@EntityListeners(DbLogger.class)
@Getter
@NoArgsConstructor
public class SecureFileEntity {

  @Id
  @Column(name = "file_id", nullable = false, updatable = false, length = 64)
  private String id;

  /**
   * Link identifier stored as a regular column for lookup purposes.
   * This is NOT a FK - the relationship is owned by AccessLinkEntity.
   */
  @Column(
    name = "link_id",
    nullable = false,
    unique = true,
    updatable = false,
    length = 64
  )
  private String linkId;

  @Column(name = "owner_id", nullable = false, updatable = false, length = 256)
  private String ownerId;

  @Column(name = "path_reference", nullable = false, unique = true)
  @Setter
  private String pathReference;

  @Lob
  @Column(name = "sealed_envelope", nullable = false)
  @Setter
  private byte[] sealedEnvelope;

  @Lob
  @Column(name = "sealed_salt", nullable = false)
  @Setter
  private byte[] sealedSalt;

  @Column(name = "expiry_date", nullable = false)
  private Instant expiryDate;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  /**
   * The associated access link entity (inverse side of 1:1 relationship).
   * May be null if the access link has not been created yet.
   */
  @OneToOne(
    mappedBy = "secureFile",
    cascade = CascadeType.ALL,
    fetch = FetchType.LAZY,
    optional = true,
    orphanRemoval = true
  )
  private AccessLinkEntity accessLink;

  /**
   * Constructs a new SecureFileEntity with all required fields.
   * The accessLink can be set separately after creation.
   *
   * @param id             the unique file identifier
   * @param linkId         the unique link identifier (for lookup)
   * @param ownerId        the unique fileowner identifier
   * @param pathReference  the storage path reference (local path or cloud URI)
   * @param sealedEnvelope the server-sealed envelope (file key and nonce)
   * @param sealedSalt     the server-sealed salt for key derivation
   * @param expiryDate     the expiration timestamp
   * @param createdAt      the creation timestamp
   */
  public SecureFileEntity(
    String id,
    String linkId,
    String ownerId,
    String pathReference,
    byte[] sealedEnvelope,
    byte[] sealedSalt,
    Instant expiryDate,
    Instant createdAt
  ) {
    this.id = id;
    this.linkId = linkId;
    this.ownerId = ownerId;
    this.pathReference = pathReference;
    this.sealedEnvelope = sealedEnvelope;
    this.sealedSalt = sealedSalt;
    this.expiryDate = expiryDate;
    this.createdAt = createdAt;
  }

  /**
   * Sets the associated access link entity.
   * Also updates the bidirectional relationship on the AccessLinkEntity side.
   *
   * @param accessLink the access link to associate with this file
   */
  public void setAccessLink(AccessLinkEntity accessLink) {
    this.accessLink = accessLink;
  }
}
