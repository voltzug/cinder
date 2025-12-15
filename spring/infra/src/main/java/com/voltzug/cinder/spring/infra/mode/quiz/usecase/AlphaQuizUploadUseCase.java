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

import com.voltzug.cinder.core.domain.entity.SecureFile;
import com.voltzug.cinder.core.domain.valueobject.FileSpecs;
import com.voltzug.cinder.core.domain.valueobject.PathReference;
import com.voltzug.cinder.core.domain.valueobject.SealedBlob;
import com.voltzug.cinder.core.domain.valueobject.id.FileId;
import com.voltzug.cinder.core.domain.valueobject.id.LinkId;
import com.voltzug.cinder.core.domain.valueobject.id.UserId;
import com.voltzug.cinder.core.model.upload.UploadResult;
import com.voltzug.cinder.core.port.out.ClockPort;
import com.voltzug.cinder.core.port.out.FileStorePort;
import com.voltzug.cinder.core.port.out.PepperPort;
import com.voltzug.cinder.core.port.out.SecureFileRepositoryPort;
import com.voltzug.cinder.spring.infra.logging.InfraLogger;
import com.voltzug.cinder.spring.infra.mode.quiz.adapter.QuizDownloadLimitAdapter;
import com.voltzug.cinder.spring.infra.mode.quiz.entity.GateHash;
import com.voltzug.cinder.spring.infra.mode.quiz.entity.QuizQuestions;
import com.voltzug.cinder.spring.infra.mode.quiz.usecase.model.AlphaUploadInput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Alpha version upload use case for quiz mode.
 *
 * <p>This is a simplified implementation that bypasses HMAC and session handshake
 * verification for alpha testing purposes. It provides a single-step upload flow:
 *
 * <ol>
 *   <li>Store the encrypted blob to file storage</li>
 *   <li>Seal envelope and salt with server pepper</li>
 *   <li>Generate file and link identifiers</li>
 *   <li>Persist SecureFile entity with quiz metadata</li>
 *   <li>Initialize download limits with gate data</li>
 *   <li>Return the generated link ID</li>
 * </ol>
 *
 * <p><strong>Security Notes (Alpha):</strong>
 * <ul>
 *   <li>No HMAC verification — payload integrity not verified</li>
 *   <li>No session handshake — no uploadSecret exchange</li>
 *   <li>No timestamp validation — no replay protection</li>
 * </ul>
 */
@Service
public class AlphaQuizUploadUseCase {

  private static final InfraLogger LOG = InfraLogger.of(AlphaQuizUploadUseCase.class);
  private static final String ALPHA_USER_ID = "alpha-anonymous";

  private final FileStorePort _fileStore;
  private final SecureFileRepositoryPort<GateHash, QuizQuestions> _repository;
  private final PepperPort _pepper;
  private final QuizDownloadLimitAdapter _downloadLimit;
  private final ClockPort _clock;

  /**
   * Constructs the AlphaQuizUploadUseCase with required dependencies.
   *
   * @param fileStore     port for file blob storage
   * @param repository    port for SecureFile persistence
   * @param pepper        port for sealing envelope and salt
   * @param downloadLimit quiz-specific adapter for initializing download limits with gate data
   * @param clock         port for time operations
   */
  public AlphaQuizUploadUseCase(
    FileStorePort fileStore,
    SecureFileRepositoryPort<GateHash, QuizQuestions> repository,
    PepperPort pepper,
    QuizDownloadLimitAdapter downloadLimit,
    ClockPort clock
  ) {
    this._fileStore = fileStore;
    this._repository = repository;
    this._pepper = pepper;
    this._downloadLimit = downloadLimit;
    this._clock = clock;
  }

  /**
   * Processes an alpha upload request.
   *
   * @param input the upload input containing blob and metadata
   * @return the upload result containing the generated link ID
   */
  @Transactional
  public UploadResult upload(AlphaUploadInput input) {
    LOG.info("Processing alpha upload: blobSize={} bytes", input.blob().size());

    PathReference blobPath = _fileStore.save(input.blob());
    LOG.debug("Stored blob at: {}", blobPath.value());

    SealedBlob sealedEnvelope = _pepper.seal(input.envelope());
    SealedBlob sealedSalt = _pepper.seal(input.salt());
    LOG.debug("Sealed envelope and salt with pepper version: {}", _pepper.actualPepperVersion());

    FileId fileId = FileId.generate();
    LinkId linkId = LinkId.generate();
    UserId userId = new UserId(ALPHA_USER_ID);

    FileSpecs specs = new FileSpecs(input.expiryDate(), input.retryCount());

    SecureFile<GateHash, QuizQuestions> secureFile = new SecureFile<>(
      fileId,
      linkId,
      userId,
      blobPath,
      sealedEnvelope,
      sealedSalt,
      specs,
      input.retryCount(),
      _clock.now(),
      input.gateHash(),
      input.encryptedQuestions()
    );

    _repository.save(secureFile);
    LOG.info("Saved SecureFile: fileId={}, linkId={}", fileId.value(), linkId.value());

    _downloadLimit.initializeWithGate(linkId, specs, input.gateHash(), input.encryptedQuestions());
    LOG.debug("Initialized download limits with gate: linkId={}, retryCount={}", linkId.value(), input.retryCount());

    return new UploadResult(linkId);
  }
}