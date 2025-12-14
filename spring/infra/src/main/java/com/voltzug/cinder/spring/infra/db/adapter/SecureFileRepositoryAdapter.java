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
package com.voltzug.cinder.spring.infra.db.adapter;

import com.voltzug.cinder.core.common.valueobject.Blob;
import com.voltzug.cinder.core.common.valueobject.safe.SafeBlob;
import com.voltzug.cinder.core.domain.entity.SecureFile;
import com.voltzug.cinder.core.domain.valueobject.FileSpecs;
import com.voltzug.cinder.core.domain.valueobject.PathReference;
import com.voltzug.cinder.core.domain.valueobject.SealedBlob;
import com.voltzug.cinder.core.domain.valueobject.id.FileId;
import com.voltzug.cinder.core.domain.valueobject.id.LinkId;
import com.voltzug.cinder.core.domain.valueobject.id.UserId;
import com.voltzug.cinder.core.port.out.SecureFileRepositoryPort;
import com.voltzug.cinder.spring.infra.db.entity.SecureFileEntity;
import com.voltzug.cinder.spring.infra.db.repository.SecureFileRepository;
import com.voltzug.cinder.spring.infra.logging.InfraLogger;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Infrastructure adapter implementing {@link SecureFileRepositoryPort}.
 * Maps between domain {@link SecureFile} entities and JPA {@link SecureFileEntity}.
 *
 * <p>This adapter handles the conversion of domain value objects to/from
 * database-friendly representations, including:</p>
 * <ul>
 *   <li>SealedBlob to/from byte arrays</li>
 *   <li>PathReference to/from String</li>
 *   <li>FileId/LinkId to/from String</li>
 *   <li>Generic gateBox (V) and gateContext (C) to/from byte arrays</li>
 * </ul>
 *
 * <p>The adapter supports the SafeFile-first creation flow by saving
 * SecureFileEntity independently, before AccessLinkEntity is created.</p>
 *
 * @param <V> gateBox type extending {@link SafeBlob}
 * @param <C> gateContext type extending {@link Blob}
 */
@Component
public class SecureFileRepositoryAdapter<V extends SafeBlob, C extends Blob>
  implements SecureFileRepositoryPort<V, C> {

  private static final InfraLogger LOG = InfraLogger.of(
    SecureFileRepositoryAdapter.class
  );

  private final SecureFileRepository _repository;

  public SecureFileRepositoryAdapter(SecureFileRepository repository) {
    this._repository = repository;
  }

  private SecureFileEntity _toEntity(SecureFile<V, C> domain) {
    return new SecureFileEntity(
      domain.fileId().toString(),
      domain.linkId().toString(),
      domain.userId().toString(),
      domain.blobPath().value(),
      domain.sealedEnvelope().getBuffer().array().clone(),
      domain.sealedSalt().getBuffer().array().clone(),
      domain.specs().expiryDate(),
      domain.createdAt()
    );
  }

  /**
   * Converts a JPA SecureFileEntity to domain SecureFile.
   * Reconstructs domain value objects from database representations.
   *
   * @param entity the JPA entity
   * @return the domain entity
   */
  @SuppressWarnings("unchecked")
  private SecureFile<V, C> _toDomain(SecureFileEntity entity) {
    var accessLink = entity.getAccessLink();

    // Reconstruct gateBox and gateContext from AccessLink if present
    V gateBox;
    C gateContext;

    if (accessLink != null) {
      gateBox = (V) new SafeBlob(accessLink.getGateBox().clone());
      gateContext = accessLink.getGateContext() != null
        ? (C) new Blob(accessLink.getGateContext().clone())
        : null;
    } else {
      // AccessLink not yet created - use empty placeholder
      gateBox = (V) new SafeBlob(new byte[32]);
      gateContext = null;
    }

    int remainingAttempts = accessLink != null
      ? accessLink.getRemainingAttempts()
      : 0;

    return new SecureFile<>(
      new FileId(entity.getId()),
      new LinkId(entity.getLinkId()),
      new UserId(entity.getOwnerId()),
      PathReference.from(entity.getPathReference()),
      new SealedBlob(entity.getSealedEnvelope().clone()),
      new SealedBlob(entity.getSealedSalt().clone()),
      new FileSpecs(entity.getExpiryDate(), Math.max(1, remainingAttempts)),
      remainingAttempts,
      entity.getCreatedAt(),
      gateBox,
      gateContext
    );
  }

  /**
   * Persists a secure file entity.
   * Converts domain entity to JPA entity and saves it.
   *
   * @param file the domain file entity to save
   */
  @Override
  @Transactional
  public void save(SecureFile<V, C> file) {
    var entity = _toEntity(file);
    _repository.save(entity);
    LOG.debug("Saved SecureFile with fileId: {}", file.fileId().value());
  }

  /**
   * Finds a secure file by its public link identifier.
   *
   * @param linkId the link identifier
   * @return an Optional containing the domain file if found
   */
  @Override
  @Transactional(readOnly = true)
  public Optional<SecureFile<V, C>> findByLinkId(LinkId linkId) {
    return _repository.findByLinkId(linkId.value()).map(this::_toDomain);
  }

  /**
   * Deletes a secure file by its internal file identifier.
   *
   * @param fileId the file identifier
   */
  @Override
  @Transactional
  public void deleteById(FileId fileId) {
    _repository.deleteById(fileId.value());
    LOG.debug("Deleted SecureFile with fileId: {}", fileId.value());
  }

  /**
   * Deletes a secure file by related link identifier.
   *
   * @param linkId the link identifier
   */
  @Override
  @Transactional
  public void deleteByLinkId(LinkId linkId) {
    _repository.deleteByLinkId(linkId.value());
    LOG.debug("Deleted SecureFile with linkId: {}", linkId.value());
  }

  /**
   * Finds all files that have expired before the given timestamp.
   * Used for cleanup jobs to burn expired files.
   *
   * @param timestamp the cutoff timestamp
   * @return a list of expired domain files
   */
  @Override
  @Transactional(readOnly = true)
  public List<SecureFile<V, C>> findExpiredBefore(Instant timestamp) {
    return _repository
      .findByExpiryDateBefore(timestamp)
      .stream()
      .map(this::_toDomain)
      .collect(Collectors.toList());
  }
}
