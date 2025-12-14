/**
 * Value objects for strongly-typed resource identifiers in the secure file exchange domain.
 *
 * <p>
 * This package defines immutable, type-safe wrappers for unique identifiers used throughout
 * the Cinder core domain. Each identifier is associated with a specific {@link IdPrefix},
 * which encodes the resource type (such as session, file, link, or user) and is enforced
 * at construction time for safety and clarity.
 * </p>
 *
 * <ul>
 *   <li>{@link IdPrefix} – Enum of all supported identifier prefixes. <b>Critical for distinguishing resource types and enforcing domain invariants.</b></li>
 *   <li>{@link SessionId} – Identifies an upload or download session. Can be generated via {@code SessionId.generate()}.</li>
 *   <li>{@link FileId} – Identifies a file or quiz resource. Can be generated via {@code FileId.generate()}.</li>
 *   <li>{@link LinkId} – Identifies a public or private access link. Can be generated via {@code LinkId.generate()}.</li>
 *   <li>{@link UserId} – Identifies a user. <b>Cannot be generated</b>; must be constructed from an existing value.</li>
 * </ul>
 *
 * <p>
 * <b>Note:</b> All identifier value objects are immutable and type-safe. The {@link IdPrefix}
 * mechanism ensures that identifiers cannot be confused or misused across resource boundaries.
 * Attempting to generate a {@link UserId} is not supported, reflecting the fact that user
 * identities are provisioned externally and not by the core system.
 * </p>
 *
 * @see com.voltzug.cinder.core.common.valueobject.Id
 * @see com.voltzug.cinder.core.domain.valueobject.id.IdPrefix
 */
package com.voltzug.cinder.core.domain.valueobject.id;
