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
import jakarta.persistence.JoinColumn;
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
 * encrypted salt (S1), owner information, and expiry date.
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

  @OneToOne(
    cascade = CascadeType.ALL,
    fetch = FetchType.EAGER,
    optional = false
  )
  @JoinColumn(name = "link_id", nullable = false, unique = true)
  private AccessLinkEntity accessLink;

  /**
   * Constructs a new SecureFileEntity with all required fields.
   *
   * @param id               the unique file identifier
   * @param ownerId          the unique fileowner identifier
   * @param pathReference    the storage path reference (local path or cloud URI)
   * @param sealedEnvelope   the server-sealed envelope (file key and nonce)
   * @param sealedSalt       the server-sealed salt for key derivation
   * @param expiryDate       the expiration timestamp
   * @param createdAt        the creation timestamp
   * @param accessLink       the related link
   */
  public SecureFileEntity(
    String id,
    String ownerId,
    String pathReference,
    byte[] sealedEnvelope,
    byte[] sealedSalt,
    Instant expiryDate,
    Instant createdAt,
    AccessLinkEntity accessLink
  ) {
    this.id = id;
    this.ownerId = ownerId;
    this.pathReference = pathReference;
    this.sealedEnvelope = sealedEnvelope;
    this.sealedSalt = sealedSalt;
    this.expiryDate = expiryDate;
    this.createdAt = createdAt;
    this.accessLink = accessLink;
  }
}
