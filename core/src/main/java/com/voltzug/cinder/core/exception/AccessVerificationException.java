package com.voltzug.cinder.core.exception;

import com.voltzug.cinder.core.domain.valueobject.id.LinkId;

/**
 * Exception thrown when quiz verification fails.
 * This occurs when the access hash provided by the downloader
 * does not match the expected gate hash.
 */
public class AccessVerificationException extends InvalidLinkException {

  public AccessVerificationException(LinkId id) {
    super(id, "Access verification failed for link: " + id.value());
  }

  public AccessVerificationException(LinkId id, String message) {
    super(id, message);
  }
}
