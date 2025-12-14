/**
 * Infrastructure logging utilities providing unified logging across the infra and rest modules.
 *
 * <h2>Overview</h2>
 * <p>This package provides centralized logging facilities that combine:
 * <ul>
 *   <li><strong>SLF4J:</strong> Industry-standard logging facade with Spring Boot integration</li>
 *   <li><strong>java.util.logging (JUL):</strong> Native JDK logging for broader compatibility</li>
 * </ul>
 *
 * <h2>Quick Start</h2>
 * <pre>{@code
 * public class MyAdapter {
 *   private static final InfraLogger LOG = InfraLogger.of(MyAdapter.class);
 *
 *   public void processRequest() {
 *     LOG.info("Processing request");
 *     LOG.debug("Request details: userId={}, action={}", userId, action);
 *
 *     try {
 *       // ... do work
 *     } catch (Exception e) {
 *       LOG.error("Failed to process request", e);
 *     }
 *   }
 * }
 * }</pre>
 *
 * <h2>Core Components</h2>
 *
 * <h3>InfraLogger</h3>
 * <p>Main logging utility that wraps both SLF4J and JUL loggers. Use this for all application logging.</p>
 * <ul>
 *   <li><strong>Factory Methods:</strong> {@code InfraLogger.of(Class)} or {@code InfraLogger.of(String)}</li>
 *   <li><strong>Log Levels:</strong> ERROR, WARN, INFO, DEBUG, TRACE</li>
 *   <li><strong>Parameterized Messages:</strong> Supports {@code {}} placeholders like SLF4J</li>
 * </ul>
 *
 * <h3>DbLogger</h3>
 * <p>JPA entity listener for automatic database operation logging. Tracks entity lifecycle events:</p>
 * <ul>
 *   <li><strong>@PrePersist/@PostPersist:</strong> Entity creation</li>
 *   <li><strong>@PreUpdate/@PostUpdate:</strong> Entity modification</li>
 *   <li><strong>@PreRemove/@PostRemove:</strong> Entity deletion</li>
 * </ul>
 *
 * <p><strong>Usage:</strong></p>
 * <pre>{@code
 * @Entity
 * @EntityListeners(DbLogger.class)
 * public class MyEntity { ... }
 * }</pre>
 *
 * <h2>Log Levels Guide</h2>
 * <table border="1">
 *   <tr>
 *     <th>Level</th>
 *     <th>Purpose</th>
 *     <th>Examples</th>
 *   </tr>
 *   <tr>
 *     <td>ERROR</td>
 *     <td>System errors requiring immediate attention</td>
 *     <td>Database connection failures, file I/O errors, unhandled exceptions</td>
 *   </tr>
 *   <tr>
 *     <td>WARN</td>
 *     <td>Potentially harmful situations</td>
 *     <td>Expired sessions, invalid input, security warnings, path traversal attempts</td>
 *   </tr>
 *   <tr>
 *     <td>INFO</td>
 *     <td>Important business events and milestones</td>
 *     <td>File uploads/downloads, session creation, entity creation/deletion</td>
 *   </tr>
 *   <tr>
 *     <td>DEBUG</td>
 *     <td>Detailed diagnostic information for development</td>
 *     <td>Method parameters, intermediate values, session details</td>
 *   </tr>
 *   <tr>
 *     <td>TRACE</td>
 *     <td>Very detailed diagnostic information</td>
 *     <td>Pre-operation events, fine-grained flow tracking</td>
 *   </tr>
 * </table>
 *
 * <h2>Best Practices</h2>
 *
 * <h3>1. Use Static Final Fields</h3>
 * <pre>{@code
 * private static final InfraLogger LOG = InfraLogger.of(MyClass.class);
 * }</pre>
 *
 * <h3>2. Use Parameterized Messages</h3>
 * <pre>{@code
 * // Good - efficient, avoids string concatenation
 * LOG.info("User {} performed action {}", userId, action);
 *
 * // Bad - always creates string even if logging is disabled
 * LOG.info("User " + userId + " performed action " + action);
 * }</pre>
 *
 * <h3>3. Check Log Level for Complex Operations</h3>
 * <pre>{@code
 * if (LOG.isDebugEnabled()) {
 *   String complexData = expensiveOperation();
 *   LOG.debug("Complex data: {}", complexData);
 * }
 * }</pre>
 *
 * <h3>4. Never Log Sensitive Data</h3>
 * <pre>{@code
 * // Good - log only IDs and metadata
 * LOG.info("File uploaded: fileId={}, size={}", fileId, size);
 *
 * // Bad - logs encryption keys
 * LOG.debug("Key: {}", encryptionKey);
 * }</pre>
 *
 * <h3>5. Include Context in Error Messages</h3>
 * <pre>{@code
 * try {
 *   processFile(fileId);
 * } catch (Exception e) {
 *   LOG.error("Failed to process file: fileId={}", fileId, e);
 * }
 * }</pre>
 *
 * <h2>Security Considerations</h2>
 * <ul>
 *   <li><strong>Never log:</strong> Passwords, encryption keys, salts, private user data</li>
 *   <li><strong>Always log:</strong> Entity IDs, timestamps, operation types, error messages</li>
 *   <li><strong>Security events:</strong> Use WARN level for suspicious activities (path traversal, invalid sessions)</li>
 *   <li><strong>Audit trail:</strong> Use INFO level for important business events</li>
 * </ul>
 *
 * <h2>Configuration</h2>
 * <p>Configure logging levels in {@code application.properties}:</p>
 * <pre>
 * # Root logging level
 * logging.level.root=INFO
 *
 * # Infra module logging
 * logging.level.com.voltzug.cinder.spring.infra=DEBUG
 *
 * # Specific component logging
 * logging.level.com.voltzug.cinder.spring.infra.session=TRACE
 * logging.level.com.voltzug.cinder.spring.infra.db=DEBUG
 *
 * # Disable database lifecycle logging in production
 * logging.level.com.voltzug.cinder.spring.infra.db.config.DbLogger=INFO
 * </pre>
 *
 * <h2>Module Usage</h2>
 * <p>This logging package is designed to be reusable across both infra and rest modules:</p>
 * <ul>
 *   <li><strong>infra module:</strong> Already uses {@code InfraLogger} in all adapters and services</li>
 *   <li><strong>rest module:</strong> Import {@code InfraLogger} for consistent logging in controllers and services</li>
 *   <li><strong>Shared dependency:</strong> No additional dependencies required</li>
 * </ul>
 *
 * <h2>Testing</h2>
 * <p>The logging system does not require mocking in unit tests:</p>
 * <ul>
 *   <li>Loggers are static final fields that don't interfere with test isolation</li>
 *   <li>Logging output can be captured via SLF4J test frameworks if needed</li>
 *   <li>No need to inject or mock loggers in most cases</li>
 * </ul>
 *
 * <h2>Examples from the Codebase</h2>
 *
 * <h3>Session Management (DownloadSessionManager)</h3>
 * <pre>{@code
 * LOG.info(
 *   "Created download session: sessionId={}, linkId={}, maxAttempts={}, timeout={}s",
 *   sessionId.value(),
 *   linkId.value(),
 *   maxAttempts,
 *   timeoutSeconds
 * );
 *
 * LOG.warn(
 *   "Download attempt with expired session: sessionId={}, createdAt={}, now={}",
 *   sessionId.value(),
 *   session.creationTime(),
 *   now
 * );
 * }</pre>
 *
 * <h3>File Storage (LocalFileStoreAdapter)</h3>
 * <pre>{@code
 * LOG.info(
 *   "Stored encrypted blob: filename={}, size={} bytes",
 *   filename,
 *   data.length
 * );
 *
 * LOG.warn(
 *   "SECURITY: Path traversal attempt detected! Requested path: {}, Resolved path: {}",
 *   pathRef.value(),
 *   absolutePath.toString()
 * );
 * }</pre>
 *
 * <h3>Database Operations (DbLogger)</h3>
 * <pre>{@code
 * @PostPersist
 * public void postPersist(Object entity) {
 *   LOG.info("Created {}: {}", entityType, entityId);
 *
 *   if (entity instanceof SecureFileEntity secureFile) {
 *     LOG.info(
 *       "SecureFile created: id={}, owner={}, expiry={}",
 *       secureFile.getId(),
 *       secureFile.getOwnerId(),
 *       secureFile.getFileExpiryDate()
 *     );
 *   }
 * }
 * }</pre>
 *
 * <h3>Scheduled Tasks (CleanupScheduler)</h3>
 * <pre>{@code
 * LOG.debug("Starting scheduled cleanup task");
 *
 * int sessionsRemoved = _cleanupSessions();
 *
 * if (sessionsRemoved > 0) {
 *   LOG.info("Cleanup completed: removed {} expired sessions", sessionsRemoved);
 * } else {
 *   LOG.debug("Cleanup completed: no expired sessions found");
 * }
 * }</pre>
 *
 * @since 0.1
 * @see com.voltzug.cinder.spring.infra.logging.InfraLogger
 * @see com.voltzug.cinder.spring.infra.db.config.DbLogger
 */
package com.voltzug.cinder.spring.infra.logging;
