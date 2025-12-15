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
package com.voltzug.cinder.spring.infra.mode.quiz.adapter;

import com.voltzug.cinder.core.domain.valueobject.FileSpecs;
import com.voltzug.cinder.core.domain.valueobject.id.FileId;
import com.voltzug.cinder.core.domain.valueobject.id.LinkId;
import com.voltzug.cinder.core.exception.InvalidLinkException;
import com.voltzug.cinder.spring.infra.clock.SystemClockAdapter;
import com.voltzug.cinder.spring.infra.db.entity.AccessLinkEntity;
import com.voltzug.cinder.spring.infra.db.repository.AccessLinkRepository;
import com.voltzug.cinder.spring.infra.db.repository.SecureFileRepository;
import com.voltzug.cinder.spring.infra.logging.InfraLogger;
import com.voltzug.cinder.spring.infra.mode.quiz.entity.GateHash;
import com.voltzug.cinder.spring.infra.mode.quiz.entity.QuizQuestions;
import com.voltzug.cinder.spring.infra.session.DownloadLimitAdapter;
import lombok.NonNull;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Quiz-specific download limit adapter that handles gateBox (GateHash) and gateContext (QuizQuestions).
 *
 * <p>This adapter extends {@link DownloadLimitAdapter} to provide an initialization method
 * that accepts the quiz-specific gate data. It's marked as {@link Primary} to override
 * the base adapter when quiz mode is active.
 *
 * <p>The gateBox stores the SHA256 hash of (answers||nonce) for quiz verification.
 * The gateContext stores the encrypted quiz questions that the downloader must answer.
 */
@Component
@Primary
public class QuizDownloadLimitAdapter extends DownloadLimitAdapter {

  private static final InfraLogger LOG = InfraLogger.of(QuizDownloadLimitAdapter.class);

  private final SystemClockAdapter _clock;

  /**
   * Constructs the QuizDownloadLimitAdapter with required dependencies.
   *
   * @param accessLinkRepository repository for AccessLinkEntity
   * @param secureFileRepository repository for SecureFileEntity
   * @param clock                clock for timestamp operations
   */
  public QuizDownloadLimitAdapter(
    AccessLinkRepository accessLinkRepository,
    SecureFileRepository secureFileRepository,
    SystemClockAdapter clock
  ) {
    super(accessLinkRepository, secureFileRepository, clock);
    this._clock = clock;
  }

  /**
   * Initializes download limits for a new link with quiz-specific gate data.
   *
   * <p>Creates the AccessLinkEntity with the provided gateHash and encryptedQuestions,
   * establishing the quiz verification mechanism for downloads.
   *
   * @param linkId             the link identifier (must match SecureFileEntity.linkId)
   * @param specs              the file specifications containing retry count
   * @param gateHash           the hash of (answers||nonce) for quiz verification
   * @param encryptedQuestions the encrypted quiz questions (may be null)
   * @throws InvalidLinkException if SecureFile not found for the given linkId
   */
  @Transactional
  public void initializeWithGate(
    @NonNull FileId fileId,
    @NonNull LinkId linkId,
    @NonNull FileSpecs specs,
    @NonNull GateHash gateHash,
    QuizQuestions encryptedQuestions
  ) throws InvalidLinkException {
    var linkIdStr = linkId.value();

    var existingLink = _accessLinkRepository.findById(linkIdStr);
    if (existingLink.isPresent()) {
      var accessLink = existingLink.get();
      accessLink.setRemainingAttempts(specs.retryCount());
      accessLink.setGateBox(gateHash.getBytes().clone());
      if (encryptedQuestions != null) {
        accessLink.setGateContext(encryptedQuestions.getBytes().clone());
      }
      accessLink.setUpdatedAt(_clock.now());
      _accessLinkRepository.save(accessLink);
      LOG.debug("Updated quiz download limit for linkId: {}", linkIdStr);
      return;
    }

    var secureFile = _secureFileRepository
      .findById(fileId.toString())
      .orElseThrow(() ->
        new InvalidLinkException(
          linkId,
          "SecureFile not found for linkId: " +
            linkIdStr +
            ". SecureFile must be created before initializing download limits."
        )
      );

    var now = _clock.now();
    var accessLink = AccessLinkEntity.forInitialization(
      linkIdStr,
      specs.retryCount(),
      gateHash.getBytes().clone(),
      encryptedQuestions != null ? encryptedQuestions.getBytes().clone() : null,
      now,
      secureFile
    );

    _accessLinkRepository.save(accessLink);
    LOG.info(
      "Initialized quiz download limit for linkId: {}, retryCount={}, gateHashSize={}",
      linkIdStr,
      specs.retryCount(),
      gateHash.size()
    );
  }
}
