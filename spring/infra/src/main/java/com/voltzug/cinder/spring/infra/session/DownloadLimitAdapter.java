package com.voltzug.cinder.spring.infra.session;

import com.voltzug.cinder.core.domain.entity.DownloadLimit;
import com.voltzug.cinder.core.domain.valueobject.FileSpecs;
import com.voltzug.cinder.core.domain.valueobject.id.LinkId;
import com.voltzug.cinder.core.exception.InvalidLinkException;
import com.voltzug.cinder.core.exception.MaxAttemptsExceededException;
import com.voltzug.cinder.core.port.out.DownloadLimitPort;
import com.voltzug.cinder.spring.infra.clock.SystemClockAdapter;
import com.voltzug.cinder.spring.infra.db.entity.AccessLinkEntity;
import com.voltzug.cinder.spring.infra.db.repository.AccessLinkRepository;
import com.voltzug.cinder.spring.infra.db.repository.SecureFileRepository;
import com.voltzug.cinder.spring.infra.logging.InfraLogger;
import java.time.Instant;
import java.util.Optional;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Infrastructure adapter implementing {@link DownloadLimitPort}.
 * Manages download limits by coordinating between AccessLink and SecureFile entities.
 *
 * <p>This adapter works with the flipped 1:1 relationship where AccessLinkEntity
 * owns the FK to SecureFileEntity, allowing SafeFile-first creation flow.</p>
 */
@Component
public class DownloadLimitAdapter implements DownloadLimitPort {

  private static final InfraLogger LOG = InfraLogger.of(
    DownloadLimitAdapter.class
  );

  private final SystemClockAdapter _clock;
  protected final AccessLinkRepository _accessLinkRepository;
  protected final SecureFileRepository _secureFileRepository;

  public DownloadLimitAdapter(
    AccessLinkRepository accessLinkRepository,
    SecureFileRepository secureFileRepository,
    SystemClockAdapter clock
  ) {
    this._accessLinkRepository = accessLinkRepository;
    this._secureFileRepository = secureFileRepository;
    this._clock = clock;
  }

  /**
   * Initializes download limits for a new link after SecureFile has been saved.
   * Creates the AccessLinkEntity with FK pointing to the existing SecureFileEntity.
   *
   * @param linkId the link identifier (must match SecureFileEntity.linkId)
   * @param specs  the file specifications containing retry count
   * @throws InvalidLinkException if SecureFile not found for the given linkId
   */
  @Override
  @Transactional
  public void initialize(@NonNull LinkId linkId, @NonNull FileSpecs specs)
    throws InvalidLinkException {
    var linkIdStr = linkId.value();

    // Check if AccessLink already exists (update case)
    var existingLink = _accessLinkRepository.findById(linkIdStr);
    if (existingLink.isPresent()) {
      var accessLink = existingLink.get();
      accessLink.setRemainingAttempts(specs.retryCount());
      accessLink.setUpdatedAt(_clock.now());
      _accessLinkRepository.save(accessLink);
      LOG.debug("Updated download limit for linkId: {}", linkIdStr);
      return;
    }

    // Find the SecureFile by linkId (must exist before AccessLink)
    var secureFile = _secureFileRepository
      .findByLinkId(linkIdStr)
      .orElseThrow(() ->
        new InvalidLinkException(
          linkId,
          "SecureFile not found for linkId: " +
            linkIdStr +
            ". SecureFile must be created before initializing download limits."
        )
      );

    // Create new AccessLink with FK to SecureFile
    var now = _clock.now();
    var accessLink = AccessLinkEntity.forInitialization(
      linkIdStr,
      specs.retryCount(),
      secureFile.getAccessLink() != null
        ? secureFile.getAccessLink().getGateBox()
        : new byte[0],
      secureFile.getAccessLink() != null
        ? secureFile.getAccessLink().getGateContext()
        : null,
      now,
      secureFile
    );

    _accessLinkRepository.save(accessLink);
    LOG.debug("Initialized download limit for linkId: {}", linkIdStr);
  }

  /**
   * Retrieves the current download limit state for a link.
   *
   * @param linkId the link identifier
   * @return an Optional containing the download limit if found
   */
  @Override
  @Transactional(readOnly = true)
  public Optional<DownloadLimit> get(LinkId linkId) {
    var linkIdStr = linkId.value();
    var accessLinkOpt = _accessLinkRepository.findById(linkIdStr);
    if (accessLinkOpt.isEmpty()) {
      return Optional.empty();
    }
    var accessLink = accessLinkOpt.get();

    // Get expiry date from SecureFile (via FK relationship or direct lookup)
    var secureFile = _secureFileRepository
      .findByLinkId(linkIdStr)
      .orElseThrow(() ->
        new IllegalStateException(
          "SecureFile not found for linkId: " + linkIdStr
        )
      );

    Instant lastAttemptAt = accessLink
        .getUpdatedAt()
        .equals(accessLink.getCreatedAt())
      ? null
      : accessLink.getUpdatedAt();

    var downloadLimit = new DownloadLimit(
      linkId,
      accessLink.getRemainingAttempts(),
      secureFile.getExpiryDate(),
      lastAttemptAt
    );
    LOG.debug("Retrieved download limit for linkId: {}", linkIdStr);
    return Optional.of(downloadLimit);
  }

  /**
   * Atomically decrements the remaining attempts counter for a link.
   *
   * @param linkId the link identifier
   * @return the updated DownloadLimit
   * @throws MaxAttemptsExceededException when attempts reach zero
   */
  @Override
  @Transactional
  public DownloadLimit decrementAttempts(LinkId linkId)
    throws MaxAttemptsExceededException {
    var linkIdStr = linkId.value();
    var now = _clock.now();
    var updatedRows = _accessLinkRepository.decrementAttempts(linkIdStr, now);

    if (updatedRows == 0) {
      LOG.warn("Max attempts exceeded for linkId: {}", linkIdStr);
      throw new MaxAttemptsExceededException(linkId);
    }

    var accessLink = _accessLinkRepository
      .findById(linkIdStr)
      .orElseThrow(() ->
        new IllegalStateException(
          "AccessLink not found after decrement for linkId: " + linkIdStr
        )
      );

    var secureFile = _secureFileRepository
      .findByLinkId(linkIdStr)
      .orElseThrow(() ->
        new IllegalStateException(
          "SecureFile not found for linkId: " + linkIdStr
        )
      );

    var downloadLimit = new DownloadLimit(
      linkId,
      accessLink.getRemainingAttempts(),
      secureFile.getExpiryDate(),
      accessLink.getUpdatedAt()
    );
    LOG.info(
      "Decremented attempts for linkId: {}, remaining: {}",
      linkIdStr,
      accessLink.getRemainingAttempts()
    );
    return downloadLimit;
  }

  /**
   * Deletes the download limit record for a link.
   *
   * @param linkId the link identifier
   */
  @Override
  @Transactional
  public void delete(LinkId linkId) {
    var linkIdStr = linkId.value();
    _accessLinkRepository.deleteById(linkIdStr);
    LOG.info("Deleted download limit for linkId: {}", linkIdStr);
  }
}
