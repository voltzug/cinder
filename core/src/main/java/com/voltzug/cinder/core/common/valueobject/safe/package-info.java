/**
 * Safe value objects for sensitive in-memory data.
 *
 * <p>
 * This package provides small immutable wrappers and helpers for handling
 * sensitive binary and text data that must be zeroed from memory when no
 * longer needed. Types in this package intentionally implement {@link AutoCloseable}
 * and provide explicit clearing semantics so callers can reliably erase secrets,
 * cryptographic keys, and other sensitive blobs from the JVM heap.
 * </p>
 *
 * <p>Key classes:
 * <ul>
 *   <li>{@link SafeString} — A character sequence storing sensitive text with
 *       explicit zeroing on {@code close()}.</li>
 *   <li>{@link SafeBlob} — Fixed-size sensitive binary container that zeroes
 *       contents when closed.</li>
 *   <li>{@link SafeBlobSized} — Sized wrapper around sensitive binary data with
 *       length tracking and safe clearing semantics.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Usage notes:
 * <ul>
 *   <li>Prefer try-with-resources to ensure deterministic clearing</li>
 *   <li>Do not rely on finalizers or the garbage collector for erasing secrets.</li>
 *   <li>Avoid serializing or logging instances from this package; convert to
 *       non-sensitive representations only when absolutely necessary and safe.</li>
 * </ul>
 * </p>
 *
 * @see com.voltzug.cinder.core.common.utils.SafeArrays
 */
package com.voltzug.cinder.core.common.valueobject.safe;
