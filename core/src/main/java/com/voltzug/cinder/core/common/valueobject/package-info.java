/**
 * Common value objects used across the Cinder core module.
 *
 * <p>
 * This package provides small, immutable and serializable value objects that
 * are shared by domain entities and ports. It contains both simple, non-sensitive
 * wrappers (for example {@link Blob} and {@link Id}) and "safe" variants that
 * hold sensitive data and offer explicit lifecycle management (see
 * {@link com.voltzug.cinder.core.common.valueobject.safe.SafeBlob},
 * {@link com.voltzug.cinder.core.common.valueobject.safe.SafeBlobSized} and
 * {@link com.voltzug.cinder.core.common.valueobject.safe.SafeString}).
 * </p>
 *
 * <p>
 * Conventions and guarantees provided by types in this package:
 * <ul>
 *   <li>Immutability and value-based equality where practical.</li>
 *   <li>Validation of invariants at construction time (non-null, non-empty, length limits).</li>
 *   <li>Clear distinction between non-sensitive and sensitive value objects.
 *       Sensitive objects implement {@link AutoCloseable} and must be closed to
 *       zero out memory when no longer needed.</li>
 *   <li>Small, focused types that keep the core module free of framework or
 *       infrastructure concerns.</li>
 * </ul>
 * </p>
 *
 * @see Blob
 * @see Id
 * @see com.voltzug.cinder.core.common.valueobject.safe.SafeBlob
 * @see com.voltzug.cinder.core.common.valueobject.safe.SafeBlobSized
 * @see com.voltzug.cinder.core.common.valueobject.safe.SafeString
 */
package com.voltzug.cinder.core.common.valueobject;
