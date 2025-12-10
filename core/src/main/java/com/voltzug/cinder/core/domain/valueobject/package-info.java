/**
 * Value objects for the secure file exchange domain.
 *
 * <p>
 * This package contains immutable, type-safe wrappers for both sensitive and non-sensitive
 * data used throughout the Cinder core domain. Value objects here enforce validation,
 * memory safety, and domain invariants for identifiers, cryptographic keys, hashes,
 * file references, and binary blobs.
 * </p>
 *
 * <ul>
 *   <li>{@link Blob} – Immutable binary data (non-sensitive)</li>
 *   <li>{@link SafeBlob} – Secure binary data with automatic memory zeroing</li>
 *   <li>{@link PathReference} – File or cloud storage path abstraction</li>
 *   <li>{@link GateHash}, {@link AccessHash}, {@link Hmac}, {@link Salt}, {@link SessionSecret} – Cryptographic primitives</li>
 *   <li>{@link FileSpecs}, {@link Timestamp} – Domain-specific value objects</li>
 * </ul>
 *
 * <p>
 * All sensitive value objects implement {@link AutoCloseable} and should be used
 * within try-with-resources or explicitly closed to guarantee memory zeroing.
 * </p>
 *
 * @see com.voltzug.cinder.core.common.utils.SafeArrays
 * @see com.voltzug.cinder.core.common.valueobject.safe.SafeString
 */
package com.voltzug.cinder.core.domain.valueobject;
