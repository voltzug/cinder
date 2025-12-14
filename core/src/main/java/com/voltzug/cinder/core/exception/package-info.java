/**
 * Domain exceptions for the Cinder secure file exchange system.
 *
 * <p>
 * This package defines a structured hierarchy of runtime exceptions representing
 * all error conditions in the Cinder core domain. Each exception type is
 * purpose-specific, immutable, and typically carries relevant domain value objects
 * (such as {@code LinkId}, {@code SessionId}, or {@code FileId}) for context.
 * </p>
 *
 * <ul>
 *   <li>{@link CinderException} – Abstract base for all domain exceptions</li>
 *   <li>{@link CryptoOperationException} – Cryptographic errors (encryption, HMAC, etc.)</li>
 *   <li>{@link FileStorageException} – File storage and retrieval failures</li>
 *   <li>{@link InvalidLinkException}, {@link FileExpiredException}, {@link MaxAttemptsExceededException} – Link access violations</li>
 *   <li>{@link InvalidSessionException}, {@link TimestampSkewException} – Session and timestamp errors</li>
 *   <li>{@link IAbuseException} – Marker for abuse/misuse detection</li>
 * </ul>
 *
 * <p>
 * All exceptions are unchecked and designed for precise error handling at API boundaries.
 * Many exceptions expose domain identifiers for traceability and security auditing.
 * </p>
 */
package com.voltzug.cinder.core.exception;
