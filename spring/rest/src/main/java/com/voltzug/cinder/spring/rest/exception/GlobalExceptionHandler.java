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
import com.voltzug.cinder.spring.infra.logging.InfraLogger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Global exception handler for the Cinder REST API.
 *
 * <p>Provides centralized exception handling for all controllers, converting
 * domain exceptions to appropriate HTTP responses with standardized {@link ApiError} bodies.
 *
 * <p><strong>Exception Mapping:</strong>
 * <table border="1">
 *   <tr><th>Exception</th><th>HTTP Status</th><th>Error Code</th></tr>
 *   <tr><td>{@link InvalidSessionException}</td><td>401 Unauthorized</td><td>INVALID_SESSION</td></tr>
 *   <tr><td>{@link MaxAttemptsExceededException}</td><td>429 Too Many Requests</td><td>MAX_ATTEMPTS</td></tr>
 *   <tr><td>{@link FileExpiredException}</td><td>410 Gone</td><td>LINK_EXPIRED</td></tr>
 *   <tr><td>{@link FileNotFoundException}</td><td>404 Not Found</td><td>FILE_NOT_FOUND</td></tr>
 *   <tr><td>{@link AccessVerificationException}</td><td>403 Forbidden</td><td>ACCESS_VERIFICATION_FAILED</td></tr>
 *   <tr><td>{@link InvalidLinkException}</td><td>400 Bad Request</td><td>INVALID_LINK</td></tr>
 *   <tr><td>{@link HmacVerificationException}</td><td>403 Forbidden</td><td>HMAC_VERIFICATION_FAILED</td></tr>
 *   <tr><td>{@link FileStorageException}</td><td>500 Internal Server Error</td><td>STORAGE_ERROR</td></tr>
 *   <tr><td>{@link CryptoOperationException}</td><td>500 Internal Server Error</td><td>CRYPTO_ERROR</td></tr>
 *   <tr><td>{@link TimestampSkewException}</td><td>400 Bad Request</td><td>TIMESTAMP_SKEW</td></tr>
 *   <tr><td>{@link IllegalArgumentException}</td><td>400 Bad Request</td><td>INVALID_REQUEST</td></tr>
 *   <tr><td>{@link MaxUploadSizeExceededException}</td><td>413 Payload Too Large</td><td>PAYLOAD_TOO_LARGE</td></tr>
 *   <tr><td>{@link Exception}</td><td>500 Internal Server Error</td><td>INTERNAL_ERROR</td></tr>
 * </table>
 *
 * <p><strong>Security Notes:</strong>
 * <ul>
 *   <li>Error messages are sanitized to prevent information leakage</li>
 *   <li>Sensitive data (keys, salts, etc.) is never included in responses</li>
 *   <li>Internal errors are logged with full details but return generic messages to clients</li>
 * </ul>
 */
@ControllerAdvice
public class GlobalExceptionHandler {

  private static final InfraLogger LOG = InfraLogger.of(
    GlobalExceptionHandler.class
  );

  /**
   * Handles invalid or expired session exceptions.
   *
   * @param ex the invalid session exception
   * @return 401 Unauthorized response
   */
  @ExceptionHandler(InvalidSessionException.class)
  public ResponseEntity<ApiError> handleInvalidSession(
    InvalidSessionException ex
  ) {
    LOG.warn("Invalid session: {}", ex.getSessionId().value());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
      ApiError.invalidSession()
    );
  }

  /**
   * Handles maximum download attempts exceeded.
   *
   * @param ex the max attempts exception
   * @return 429 Too Many Requests response
   */
  @ExceptionHandler(MaxAttemptsExceededException.class)
  public ResponseEntity<ApiError> handleMaxAttempts(
    MaxAttemptsExceededException ex
  ) {
    LOG.warn("Max attempts exceeded: linkId={}", ex.getId().value());
    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(
      ApiError.maxAttemptsExceeded()
    );
  }

  /**
   * Handles expired access link exceptions.
   *
   * @param ex the link expired exception
   * @return 410 Gone response
   */
  @ExceptionHandler(FileExpiredException.class)
  public ResponseEntity<ApiError> handleLinkExpired(FileExpiredException ex) {
    LOG.info("Link expired: linkId={}", ex.getId().value());
    return ResponseEntity.status(HttpStatus.GONE).body(ApiError.linkExpired());
  }

  /**
   * Handles file not found exceptions.
   *
   * @param ex the file not found exception
   * @return 404 Not Found response
   */
  @ExceptionHandler(FileNotFoundException.class)
  public ResponseEntity<ApiError> handleFileNotFound(FileNotFoundException ex) {
    if (ex.getFileId() != null) {
      LOG.warn("File not found: fileId={}", ex.getFileId().value());
    } else {
      LOG.warn("File not found: {}", ex.getMessage());
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
      ApiError.fileNotFound()
    );
  }

  /**
   * Handles access verification failures (e.g., gate hash mismatch).
   *
   * @param ex the access verification exception
   * @return 403 Forbidden response
   */
  @ExceptionHandler(AccessVerificationException.class)
  public ResponseEntity<ApiError> handleAccessVerification(
    AccessVerificationException ex
  ) {
    LOG.warn("Access verification failed: linkId={}", ex.getId().value());
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
      new ApiError("ACCESS_VERIFICATION_FAILED", "Access verification failed")
    );
  }

  /**
   * Handles invalid link exceptions (malformed or tampered links).
   *
   * @param ex the invalid link exception
   * @return 400 Bad Request response
   */
  @ExceptionHandler(InvalidLinkException.class)
  public ResponseEntity<ApiError> handleInvalidLink(InvalidLinkException ex) {
    LOG.warn(
      "Invalid link: linkId={}, msg={}",
      ex.getId() != null ? ex.getId().value() : "null",
      ex.getMessage()
    );
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
      new ApiError("INVALID_LINK", "Invalid or expired link")
    );
  }

  /**
   * Handles HMAC verification failures.
   *
   * @param ex the HMAC verification exception
   * @return 403 Forbidden response
   */
  @ExceptionHandler(HmacVerificationException.class)
  public ResponseEntity<ApiError> handleHmacVerification(
    HmacVerificationException ex
  ) {
    LOG.warn(
      "HMAC verification failed: sessionId={}",
      ex.getSessionId().value()
    );
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
      new ApiError("HMAC_VERIFICATION_FAILED", "Message authentication failed")
    );
  }

  /**
   * Handles timestamp skew exceptions.
   *
   * @param ex the timestamp skew exception
   * @return 400 Bad Request response
   */
  @ExceptionHandler(TimestampSkewException.class)
  public ResponseEntity<ApiError> handleTimestampSkew(
    TimestampSkewException ex
  ) {
    LOG.warn(
      "Timestamp skew: sessionId={}, timestamp={}, allowedSkewMs={}",
      ex.getSessionId() != null ? ex.getSessionId().value() : "null",
      ex.getTimestamp() != null ? ex.getTimestamp().toString() : "null",
      ex.getAllowedSkewMs()
    );
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
      new ApiError("TIMESTAMP_SKEW", "Timestamp is outside allowed window")
    );
  }

  /**
   * Handles file storage operation failures.
   *
   * @param ex the file storage exception
   * @return 500 Internal Server Error response
   */
  @ExceptionHandler(FileStorageException.class)
  public ResponseEntity<ApiError> handleFileStorage(FileStorageException ex) {
    LOG.error("File storage error: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
      new ApiError("STORAGE_ERROR", "File storage error")
    );
  }

  /**
   * Handles cryptographic operation failures.
   *
   * @param ex the crypto operation exception
   * @return 500 Internal Server Error response
   */
  @ExceptionHandler(CryptoOperationException.class)
  public ResponseEntity<ApiError> handleCryptoError(
    CryptoOperationException ex
  ) {
    LOG.error("Crypto operation failed: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
      ApiError.cryptoError()
    );
  }

  /**
   * Handles illegal argument exceptions (malformed requests).
   *
   * @param ex the illegal argument exception
   * @return 400 Bad Request response
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiError> handleIllegalArgument(
    IllegalArgumentException ex
  ) {
    LOG.warn("Invalid request argument: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
      ApiError.invalidRequest(ex.getMessage())
    );
  }

  /**
   * Handles multipart upload size exceeded exceptions.
   *
   * @param ex the max upload size exception
   * @return 413 Content Too Large response
   */
  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<ApiError> handleMaxUploadSize(
    MaxUploadSizeExceededException ex
  ) {
    LOG.warn("Upload size exceeded: maxSize={}", ex.getMaxUploadSize());
    return ResponseEntity.status(HttpStatus.CONTENT_TOO_LARGE).body(
      new ApiError(
        "PAYLOAD_TOO_LARGE",
        "File upload exceeds maximum allowed size"
      )
    );
  }

  /**
   * Handles missing request parameter exceptions.
   *
   * @param ex the missing parameter exception
   * @return 400 Bad Request response
   */
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ApiError> handleMissingParameter(
    MissingServletRequestParameterException ex
  ) {
    LOG.debug("Missing request parameter: {}", ex.getParameterName());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
      new ApiError(
        "MISSING_PARAMETER",
        "Required parameter '" + ex.getParameterName() + "' is missing"
      )
    );
  }

  /**
   * Handles missing multipart file/part exceptions.
   *
   * @param ex the missing request part exception
   * @return 400 Bad Request response
   */
  @ExceptionHandler(MissingServletRequestPartException.class)
  public ResponseEntity<ApiError> handleMissingRequestPart(
    MissingServletRequestPartException ex
  ) {
    LOG.debug("Missing request part: {}", ex.getRequestPartName());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
      new ApiError(
        "MISSING_PART",
        "Required part '" + ex.getRequestPartName() + "' is missing"
      )
    );
  }

  /**
   * Handles method argument type mismatch exceptions (e.g., invalid date format).
   *
   * @param ex the type mismatch exception
   * @return 400 Bad Request response
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiError> handleTypeMismatch(
    MethodArgumentTypeMismatchException ex
  ) {
    LOG.debug(
      "Type mismatch for parameter '{}': value='{}', requiredType={}",
      ex.getName(),
      ex.getValue(),
      ex.getRequiredType() != null
        ? ex.getRequiredType().getSimpleName()
        : "unknown"
    );
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
      new ApiError(
        "TYPE_MISMATCH",
        "Invalid value for parameter '" + ex.getName() + "'"
      )
    );
  }

  /**
   * Handles unsupported media type exceptions.
   *
   * @param ex the media type not supported exception
   * @return 415 Unsupported Media Type response
   */
  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<ApiError> handleMediaTypeNotSupported(
    HttpMediaTypeNotSupportedException ex
  ) {
    LOG.debug(
      "Unsupported media type: contentType={}, supported={}",
      ex.getContentType(),
      ex.getSupportedMediaTypes()
    );
    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(
      new ApiError(
        "UNSUPPORTED_MEDIA_TYPE",
        "Content type '" + ex.getContentType() + "' is not supported"
      )
    );
  }

  /**
   * Handles HTTP message not readable exceptions (malformed JSON, missing body, etc.).
   *
   * @param ex the message not readable exception
   * @return 400 Bad Request response
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiError> handleMessageNotReadable(
    HttpMessageNotReadableException ex
  ) {
    LOG.debug("HTTP message not readable: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
      new ApiError("INVALID_REQUEST_BODY", "Request body is invalid or malformed")
    );
  }

  /**
   * Handles NullPointerException (often from DTO validation via Objects.requireNonNull).
   *
   * @param ex the null pointer exception
   * @return 400 Bad Request response
   */
  @ExceptionHandler(NullPointerException.class)
  public ResponseEntity<ApiError> handleNullPointer(NullPointerException ex) {
    LOG.debug("Null pointer exception: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
      new ApiError("MISSING_REQUIRED_FIELD", ex.getMessage() != null ? ex.getMessage() : "Required field is missing")
    );
  }

  /**
   * Handles HTTP method not supported exceptions.
   *
   * @param ex the method not supported exception
   * @return 405 Method Not Allowed response
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ApiError> handleMethodNotSupported(
    HttpRequestMethodNotSupportedException ex
  ) {
    LOG.debug(
      "Method not supported: method={}, supportedMethods={}",
      ex.getMethod(),
      ex.getSupportedHttpMethods()
    );
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(
      new ApiError(
        "METHOD_NOT_ALLOWED",
        "Request method '" + ex.getMethod() + "' is not supported"
      )
    );
  }

  /**
   * Handles resource not found exceptions for API routes.
   *
   * @param ex the no resource found exception
   * @return 404 Not Found response for API routes, or rethrows for SPA fallback
   */
  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ApiError> handleNoResourceFound(
    NoResourceFoundException ex
  ) {
    String path = ex.getResourcePath();
    // Only handle API routes; SPA routes are handled by WebConfig
    if (path != null && path.startsWith("api")) {
      LOG.debug("API endpoint not found: {}", path);
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
        new ApiError("ENDPOINT_NOT_FOUND", "API endpoint not found: " + path)
      );
    }
    // For non-API routes, return null to let Spring continue with default handling
    // which will eventually trigger the SPA fallback
    LOG.debug("Resource not found, delegating to SPA fallback: {}", path);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
      new ApiError("NOT_FOUND", "Resource not found")
    );
  }

  /**
   * Handles all other unexpected exceptions.
   *
   * <p><strong>Security:</strong> Returns a generic error message to prevent
   * information leakage. Full exception details are logged server-side.
   *
   * @param ex the unexpected exception
   * @return 500 Internal Server Error response
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleGeneric(Exception ex) {
    LOG.error("Unexpected error: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
      ApiError.internalError()
    );
  }
}
