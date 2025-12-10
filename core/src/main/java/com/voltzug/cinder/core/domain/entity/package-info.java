/**
 * Domain entities for secure file exchange.
 *
 * <p>This package contains core entity classes representing
 * access links, secure files, download limits, and session management
 * used in the Cinder zero-knowledge file sharing system.
 *
 * <ul>
 *   <li>{@link Session} — Represents upload/download session with expiration</li>
 *   <li>{@link SecureFile} — Represents an encrypted file and its metadata</li>
 *   <li>{@link DownloadLimit} — Tracks download attempt limits and expiration for a link</li>
 *   <li>{@link IExpirable} — Interface for objects with expiration logic</li>
 * </ul>
 *
 * @see com.voltzug.cinder.core.domain.valueobject
 */
package com.voltzug.cinder.core.domain.entity;
