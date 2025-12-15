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
package com.voltzug.cinder.spring.infra.mode.quiz.usecase;

import com.voltzug.cinder.core.common.valueobject.Blob;
import com.voltzug.cinder.core.domain.entity.SecureFile;
import com.voltzug.cinder.core.domain.entity.Session;
import com.voltzug.cinder.core.domain.valueobject.id.LinkId;
import com.voltzug.cinder.core.domain.valueobject.id.SessionId;
import com.voltzug.cinder.core.exception.AccessVerificationException;
import com.voltzug.cinder.core.exception.FileExpiredException;
import com.voltzug.cinder.core.exception.FileNotFoundException;
import com.voltzug.cinder.core.exception.InvalidSessionException;
import com.voltzug.cinder.core.port.out.ClockPort;
import com.voltzug.cinder.core.port.out.DownloadLimitPort;
import com.voltzug.cinder.core.port.out.FileStorePort;
import com.voltzug.cinder.core.port.out.PepperPort;
import com.voltzug.cinder.core.port.out.SecureFileRepositoryPort;
import com.voltzug.cinder.core.port.out.SessionCachePort;
import com.voltzug.cinder.spring.infra.config.CinderProperties;
import com.voltzug.cinder.spring.infra.logging.InfraLogger;
import com.voltzug.cinder.spring.infra.mode.quiz.entity.AccessHash;
import com.voltzug.cinder.spring.infra.mode.quiz.entity.GateHash;
import com.voltzug.cinder.spring.infra.mode.quiz.entity.QuizQuestions;
import com.voltzug.cinder.spring.infra.mode.quiz.usecase.model.DownloadInitResult;
import com.voltzug.cinder.spring.infra.mode.quiz.usecase.model.DownloadResult;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Alpha version download use case for quiz mode.
 *
 * <p>This is a simplified implementation that bypasses HMAC and full session handshake
 * verification for alpha testing purposes. It provides a two-step download flow:
 *
 * <ol>
 *   <li><strong>Init Session:</strong> Create a session and return encrypted questions</li>
 *   <li><strong>Verify and Download:</strong> Verify access hash and return file data</li>
 * </ol>
 *
 * <p><strong>Security Notes (Alpha):</strong>
 * <ul>
 *   <li>No HMAC verification — response integrity not signed</li>
 *   <li>No downloadSecret exchange — simplified session</li>
 *   <li>Session still required for tracking download attempts</li>
 *   <li>GateHash/AccessHash comparison is timing-safe</li>
 * </ul>
 */
@Service
public class AlphaQuizDownloadUseCase {

  private static final InfraLogger LOG = InfraLogger.of(AlphaQuizDownloadUseCase.class);

  private final SecureFileRepositoryPort<GateHash, QuizQuestions> _repository;
  private final FileStorePort _fileStore;
  private final PepperPort _pepper;
  private final SessionCachePort _sessionCache;
  private final DownloadLimitPort _downloadLimit;
  private final ClockPort _clock;
  private final CinderProperties _properties;

  /**
   * Constructs the AlphaQuizDownloadUseCase with required dependencies.
   *
   * @param repository    port for SecureFile persistence
   * @param fileStore     port for file blob storage
   * @param pepper        port for unsealing envelope and salt
   * @param sessionCache  port for session caching
   * @param downloadLimit port for download limit tracking
   * @param clock         port for time operations
   * @param properties    configuration properties
   */
  public AlphaQuizDownloadUseCase(
    SecureFileRepositoryPort<GateHash, QuizQuestions> repository,
    FileStorePort fileStore,
    PepperPort pepper,
    SessionCachePort sessionCache,
    DownloadLimitPort downloadLimit,
    ClockPort clock,
    CinderProperties properties
  ) {
    this._repository = repository;
    this._fileStore = fileStore;
    this._pepper = pepper;
    this._sessionCache = sessionCache;
    this._downloadLimit = downloadLimit;
    this._clock = clock;
    this._properties = properties;
  }

  /**
   * Initializes a download session for the given link.
   *
   * <p>Creates a session and returns the encrypted quiz questions for the client
   * to decrypt (using questionK from URL fragment) and answer.
   *
   * @param linkId the link identifier from the access URL
   * @return the session ID and encrypted questions
   * @throws FileNotFoundException if no file exists for the link
   * @throws FileExpiredException  if the file has expired
   */
  @Transactional(readOnly = true)
  public DownloadInitResult initSession(LinkId linkId) {
    LOG.info("Initializing download session for linkId: {}", linkId.toString());

    SecureFile<GateHash, QuizQuestions> file = _repository.findByLinkId(linkId)
      .orElseThrow(() -> {
        LOG.warn("File not found for linkId: {}", linkId.toString());
        return new FileNotFoundException(null, "Link not found: " + linkId.toString());
      });

    Instant now = _clock.now();
    if (file.isExpired(now)) {
      LOG.warn("File expired for linkId: {}, expiryDate: {}", linkId.toString(), file.getExpiryDate());
      throw new FileExpiredException(linkId);
    }

    SessionId sessionId = SessionId.generate();
    Instant expiresAt = now.plusSeconds(_properties.getSession().getTimeoutSeconds());

    Session session = new Session(
      sessionId,
      null,
      linkId,
      Session.Mode.DOWNLOAD,
      now,
      expiresAt
    );
    _sessionCache.save(session);
    LOG.debug(
      "Created download session: sessionId={}, linkId={}, expiresAt={}",
      sessionId.toString(),
      linkId.toString(),
      expiresAt
    );

    QuizQuestions encryptedQuestions = file.gateContext();
    if (encryptedQuestions == null) {
      LOG.error("No encrypted questions found for linkId: {}", linkId.toString());
      throw new IllegalStateException("Encrypted questions not found for link: " + linkId.value());
    }

    return new DownloadInitResult(sessionId, encryptedQuestions);
  }

  /**
   * Verifies the access hash and returns the file data if verification succeeds.
   *
   * <p>Performs timing-safe comparison of the provided access hash against the stored
   * gate hash. On success, decrements the download counter and returns the unsealed
   * file data.
   *
   * @param sessionId  the session identifier from {@link #initSession(LinkId)}
   * @param accessHash the hash computed by the client (SHA256 of answers||nonce)
   * @return the encrypted blob and unsealed envelope/salt
   * @throws InvalidSessionException       if the session is invalid or expired
   * @throws AccessVerificationException   if the access hash doesn't match
   */
  @Transactional
  public DownloadResult verifyAndDownload(SessionId sessionId, AccessHash accessHash) {
    LOG.info("Verifying download access for sessionId: {}", sessionId.toString());

    Session session = _sessionCache.get(sessionId)
      .orElseThrow(() -> {
        LOG.warn("Invalid or expired session: {}", sessionId.toString());
        return new InvalidSessionException(sessionId);
      });

    LinkId linkId = session.linkId();
    if (linkId == null) {
      LOG.error("Session has no associated linkId: {}", sessionId.toString());
      throw new InvalidSessionException(sessionId, "Session has no associated link");
    }

    SecureFile<GateHash, QuizQuestions> file = _repository.findByLinkId(linkId)
      .orElseThrow(() -> {
        LOG.warn("File not found for linkId: {}", linkId.toString());
        return new FileNotFoundException(null, "Link not found: " + linkId.toString());
      });

    _downloadLimit.decrementAttempts(linkId);
    LOG.debug("Decremented download attempts for linkId: {}", linkId.toString());

    GateHash gateHash = file.gateBox();
    if (!accessHash.canUnlock(gateHash)) {
      LOG.warn("Access verification failed for linkId: {}", linkId.toString());
      throw new AccessVerificationException(linkId);
    }
    LOG.debug("Access hash verified successfully for linkId: {}", linkId.toString());

    Blob blob = _fileStore.load(file.blobPath());
    LOG.debug("Loaded blob from storage: path={}, size={} bytes", file.blobPath().value(), blob.size());

    byte[] envelope = _pepper.unseal(file.sealedEnvelope());
    byte[] salt = _pepper.unseal(file.sealedSalt());
    LOG.debug("Unsealed envelope and salt for linkId: {}", linkId.toString());

    _sessionCache.delete(sessionId);
    LOG.debug("Deleted session after successful download: {}", sessionId.toString());

    LOG.info("Download completed successfully for linkId: {}", linkId.toString());
    return new DownloadResult(blob, envelope, salt);
  }
}
