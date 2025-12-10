/**
 * Core contracts for the Cinder domain layer.
 *
 * <p>
 * This package defines minimal, framework-agnostic interfaces and abstractions
 * that describe core capabilities and extension points within the Cinder system.
 * Contracts here are intentionally lightweight and free of infrastructure or
 * runtime dependencies, ensuring the domain remains easy to test and reason about.
 * </p>
 *
 * <p>Representative types in this package include:</p>
 * <ul>
 *   <li>{@link IResolvable} — A generic resolver/lookup contract for translating or
 *       providing values without coupling to infrastructure.</li>
 *   <li>{@link Handshake} — Abstractions for challenge-response protocols, supporting
 *       both advanced and lightweight authentication flows.</li>
 * </ul>
 *
 * @see com.voltzug.cinder.core.common.contract.IResolvable
 * @see com.voltzug.cinder.core.common.contract.Handshake
 */
package com.voltzug.cinder.core.common.contract;
