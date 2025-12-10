/**
 * Common utilities and lightweight primitives used across the core module.
 *
 * <p>
 * The classes in this package are intentionally small, framework-free building
 * blocks that support the domain, ports, and tests. Typical responsibilities
 * include:
 * </p>
 *
 * <ul>
 *   <li>Safe, immutable value objects for small binary or identifier types</li>
 *   <li>Memory-safe helpers for working with byte arrays and sensitive data</li>
 *   <li>Lightweight contract/utility interfaces used by multiple packages</li>
 *   <li>Assertion helpers to centralize precondition checks used in domain objects</li>
 * </ul>
 *
 * <p>Notable subpackages and classes:</p>
 * <ul>
 *   <li>{@link com.voltzug.cinder.core.common.valueobject.Blob} — immutable binary wrapper</li>
 *   <li>{@link com.voltzug.cinder.core.common.valueobject.Id} — typed identifier helper</li>
 *   <li>{@link com.voltzug.cinder.core.common.valueobject.safe.SafeBlob} — memory-zeroing sensitive blob</li>
 *   <li>{@link com.voltzug.cinder.core.common.valueobject.safe.SafeString} — safe, closable string-like value</li>
 *   <li>{@link com.voltzug.cinder.core.common.utils.SafeArrays} — byte-array utilities and secure copying</li>
 *   <li>{@link com.voltzug.cinder.core.common.utils.Assert} — precondition and invariant checks</li>
 *   <li>{@link com.voltzug.cinder.core.common.contract.IResolvable} — tiny contract used by factories/resolvers</li>
 * </ul>
 *
 * @see com.voltzug.cinder.core.common.valueobject
 * @see com.voltzug.cinder.core.common.valueobject.safe
 * @see com.voltzug.cinder.core.common.utils
 * @see com.voltzug.cinder.core.common.contract
 */
package com.voltzug.cinder.core.common;
