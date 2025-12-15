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
package com.voltzug.cinder.spring.rest.exception;

import com.voltzug.cinder.core.exception.*;
import java.time.Instant;

/**
 * Standardized error response DTO for API errors.
 *
 * <p>Provides a consistent structure for all error responses returned by the REST API:
 * <ul>
 *   <li><strong>code:</strong> Machine-readable error code (e.g., "INVALID_SESSION", "MAX_ATTEMPTS")</li>
 *   <li><strong>message:</strong> Human-readable error description</li>
 *   <li><strong>timestamp:</strong> When the error occurred (UTC)</li>
 * </ul>
 *
 * <p><strong>Usage Example:</strong>
 * <pre>{@code
 * return ResponseEntity
 *     .status(HttpStatus.UNAUTHORIZED)
 *     .body(new ApiError("INVALID_SESSION", "Session is invalid or expired"));
 * }</pre>
 *
 * <p><strong>Error Codes:</strong>
 * <table border="1">
 *   <tr><th>Code</th><th>HTTP Status</th><th>Description</th></tr>
 *   <tr><td>INVALID_SESSION</td><td>401</td><td>Session not found or expired</td></tr>
 *   <tr><td>MAX_ATTEMPTS</td><td>429</td><td>Download attempts exceeded</td></tr>
 *   <tr><td>LINK_EXPIRED</td><td>410</td><td>Access link has expired</td></tr>
 *   <tr><td>FILE_NOT_FOUND</td><td>404</td><td>Requested file not found</td></tr>
 *   <tr><td>INVALID_KEY</td><td>403</td><td>Invalid passphrase key (K2)</td></tr>
 *   <tr><td>CRYPTO_ERROR</td><td>500</td><td>Cryptographic operation failed</td></tr>
 *   <tr><td>INTERNAL_ERROR</td><td>500</td><td>Unexpected server error</td></tr>
 *   <tr><td>INVALID_REQUEST</td><td>400</td><td>Malformed request</td></tr>
 * </table>
 *
 * @param code machine-readable error code
 * @param message human-readable error description
 * @param timestamp when the error occurred (UTC)
 */
public record ApiError(String code, String message, Instant timestamp) {
  /**
   * Constructs an ApiError with the current timestamp.
   *
   * @param code machine-readable error code
   * @param message human-readable error description
   */
  public ApiError(String code, String message) {
    this(code, message, Instant.now());
  }

  /**
   * Canonical constructor with validation.
   *
   * @param code machine-readable error code (must not be null or blank)
   * @param message human-readable error description (must not be null)
   * @param timestamp when the error occurred (must not be null)
   * @throws IllegalArgumentException if any parameter is invalid
   */
  public ApiError {
    if (code == null || code.isBlank()) {
      throw new IllegalArgumentException(
        "Error code must not be null or blank"
      );
    }
    if (message == null) {
      throw new IllegalArgumentException("Error message must not be null");
    }
    if (timestamp == null) {
      throw new IllegalArgumentException("Timestamp must not be null");
    }
  }

  // Common error codes as constants for consistency
  public static final String CODE_INVALID_SESSION = "INVALID_SESSION";
  public static final String CODE_MAX_ATTEMPTS = "MAX_ATTEMPTS";
  public static final String CODE_LINK_EXPIRED = "LINK_EXPIRED";
  public static final String CODE_FILE_NOT_FOUND = "FILE_NOT_FOUND";
  public static final String CODE_INVALID_KEY = "INVALID_KEY";
  public static final String CODE_CRYPTO_ERROR = "CRYPTO_ERROR";
  public static final String CODE_INTERNAL_ERROR = "INTERNAL_ERROR";
  public static final String CODE_INVALID_REQUEST = "INVALID_REQUEST";
  public static final String CODE_HMAC_VERIFICATION_FAILED =
    "HMAC_VERIFICATION_FAILED";

  /**
   * Creates an ApiError from a Cinder domain exception.
   *
   * <p>Maps known exception types from the Cinder core domain to standardized API error codes and messages.
   * Falls back to INTERNAL_ERROR for unknown exceptions.
   *
   * @param ex the thrown exception (may be null)
   * @return ApiError representing the exception
   */
  public static ApiError fromException(Throwable ex) {
    if (ex == null) {
      return internalError();
    }

    // Import domain exceptions only here to avoid circular dependency in the record header
    // (Assume these are available on the classpath)
    // Use fully qualified names for clarity

    // File/session/link exceptions
    if (ex instanceof InvalidSessionException) {
      return new ApiError(
        CODE_INVALID_SESSION,
        ex.getMessage() != null
          ? ex.getMessage()
          : "Session is invalid or expired"
      );
    }
    if (ex instanceof MaxAttemptsExceededException) {
      return new ApiError(
        CODE_MAX_ATTEMPTS,
        ex.getMessage() != null
          ? ex.getMessage()
          : "Maximum download attempts exceeded"
      );
    }
    if (ex instanceof FileExpiredException) {
      return new ApiError(
        CODE_LINK_EXPIRED,
        ex.getMessage() != null ? ex.getMessage() : "Access link has expired"
      );
    }
    if (ex instanceof FileNotFoundException) {
      return new ApiError(
        CODE_FILE_NOT_FOUND,
        ex.getMessage() != null ? ex.getMessage() : "File not found"
      );
    }
    if (ex instanceof AccessVerificationException) {
      return new ApiError(
        CODE_INVALID_KEY,
        ex.getMessage() != null ? ex.getMessage() : "Invalid passphrase key"
      );
    }
    if (ex instanceof HmacVerificationException) {
      return new ApiError(
        CODE_HMAC_VERIFICATION_FAILED,
        ex.getMessage() != null
          ? ex.getMessage()
          : "Upload signature verification failed"
      );
    }
    if (ex instanceof CryptoOperationException) {
      return new ApiError(
        CODE_CRYPTO_ERROR,
        ex.getMessage() != null
          ? ex.getMessage()
          : "Cryptographic operation failed"
      );
    }
    if (ex instanceof FileStorageException) {
      return new ApiError(
        CODE_INTERNAL_ERROR,
        ex.getMessage() != null ? ex.getMessage() : "File storage error"
      );
    }
    if (ex instanceof InvalidLinkException) {
      return new ApiError(
        CODE_INVALID_KEY,
        ex.getMessage() != null ? ex.getMessage() : "Invalid access link"
      );
    }
    if (ex instanceof TimestampSkewException) {
      return new ApiError(
        CODE_INVALID_REQUEST,
        ex.getMessage() != null ? ex.getMessage() : "Timestamp skew exceeded"
      );
    }

    // Validation or bad request
    if (ex instanceof IllegalArgumentException) {
      return new ApiError(
        CODE_INVALID_REQUEST,
        ex.getMessage() != null ? ex.getMessage() : "Invalid request"
      );
    }

    // Fallback for unknown errors
    return new ApiError(
      CODE_INTERNAL_ERROR,
      ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred"
    );
  }

  /**
   * Creates an ApiError for invalid session scenarios.
   *
   * @return ApiError with INVALID_SESSION code
   */
  public static ApiError invalidSession() {
    return new ApiError(CODE_INVALID_SESSION, "Session is invalid or expired");
  }

  /**
   * Creates an ApiError for max attempts exceeded scenarios.
   *
   * @return ApiError with MAX_ATTEMPTS code
   */
  public static ApiError maxAttemptsExceeded() {
    return new ApiError(
      CODE_MAX_ATTEMPTS,
      "Maximum download attempts exceeded"
    );
  }

  /**
   * Creates an ApiError for expired link scenarios.
   *
   * @return ApiError with LINK_EXPIRED code
   */
  public static ApiError linkExpired() {
    return new ApiError(CODE_LINK_EXPIRED, "Access link has expired");
  }

  /**
   * Creates an ApiError for file not found scenarios.
   *
   * @return ApiError with FILE_NOT_FOUND code
   */
  public static ApiError fileNotFound() {
    return new ApiError(CODE_FILE_NOT_FOUND, "File not found");
  }

  /**
   * Creates an ApiError for invalid key scenarios.
   *
   * @return ApiError with INVALID_KEY code
   */
  public static ApiError invalidKey() {
    return new ApiError(CODE_INVALID_KEY, "Invalid passphrase key");
  }

  /**
   * Creates an ApiError for crypto operation failures.
   *
   * @return ApiError with CRYPTO_ERROR code
   */
  public static ApiError cryptoError() {
    return new ApiError(CODE_CRYPTO_ERROR, "Cryptographic operation failed");
  }

  /**
   * Creates an ApiError for unexpected internal errors.
   *
   * @return ApiError with INTERNAL_ERROR code
   */
  public static ApiError internalError() {
    return new ApiError(CODE_INTERNAL_ERROR, "An unexpected error occurred");
  }

  /**
   * Creates an ApiError for invalid request scenarios.
   *
   * @param details specific details about what was invalid
   * @return ApiError with INVALID_REQUEST code
   */
  public static ApiError invalidRequest(String details) {
    return new ApiError(CODE_INVALID_REQUEST, "Invalid request: " + details);
  }

  /**
   * Creates an ApiError for HMAC verification failure.
   *
   * @return ApiError with HMAC_VERIFICATION_FAILED code
   */
  public static ApiError hmacVerificationFailed() {
    return new ApiError(
      CODE_HMAC_VERIFICATION_FAILED,
      "Upload signature verification failed"
    );
  }
}
