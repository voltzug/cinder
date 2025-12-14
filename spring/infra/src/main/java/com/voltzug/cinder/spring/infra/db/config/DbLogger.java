package com.voltzug.cinder.spring.infra.db.config;

import com.voltzug.cinder.spring.infra.db.entity.SecureFileEntity;
import com.voltzug.cinder.spring.infra.logging.InfraLogger;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import org.springframework.stereotype.Component;

/**
 * JPA entity listener for logging database operations.
 *
 * <p>This logger tracks lifecycle events for database entities, providing:
 * <ul>
 *   <li><strong>Creation Tracking:</strong> Logs when new entities are persisted</li>
 *   <li><strong>Update Tracking:</strong> Logs when entities are modified</li>
 *   <li><strong>Deletion Tracking:</strong> Logs when entities are removed</li>
 *   <li><strong>Security Audit:</strong> Provides audit trail for sensitive operations</li>
 * </ul>
 *
 * <p><strong>Usage:</strong>
 * Add this listener to entities using the {@code @EntityListeners} annotation:
 * <pre>{@code
 * @Entity
 * @EntityListeners(DbLogger.class)
 * public class MyEntity { ... }
 * }</pre>
 *
 * <p><strong>Log Levels:</strong>
 * <ul>
 *   <li>{@code INFO}: Entity creation and deletion</li>
 *   <li>{@code DEBUG}: Entity updates and detailed change information</li>
 *   <li>{@code TRACE}: Pre-operation events</li>
 * </ul>
 *
 * <p><strong>Security Notes:</strong>
 * <ul>
 *   <li>Logs contain entity IDs but never sensitive data (keys, salts, etc.)</li>
 *   <li>Suitable for compliance and security auditing</li>
 *   <li>Can be disabled by setting log level to WARN or higher</li>
 * </ul>
 *
 * @see SecureFileEntity
 */
@Component
public class DbLogger {

  private static final InfraLogger LOG = InfraLogger.of(DbLogger.class);

  /**
   * Called before an entity is persisted to the database.
   */
  @PrePersist
  public void prePersist(Object entity) {
    if (LOG.isTraceEnabled()) {
      LOG.trace(
        "Pre-persist: {} [{}]",
        entity.getClass().getSimpleName(),
        getEntityId(entity)
      );
    }
  }

  /**
   * Called after an entity has been persisted to the database.
   */
  @PostPersist
  public void postPersist(Object entity) {
    String entityType = entity.getClass().getSimpleName();
    String entityId = getEntityId(entity);

    LOG.info("Created {}: {}", entityType, entityId);

    // Special logging for SecureFileEntity
    if (entity instanceof SecureFileEntity secureFile) {
      LOG.info(
        "SecureFile created: id={}, owner={}, expiry={}",
        secureFile.getId(),
        secureFile.getOwnerId(),
        secureFile.getExpiryDate()
      );
      LOG.debug(
        "SecureFile storage: id={}, pathReference={}",
        secureFile.getId(),
        secureFile.getPathReference()
      );
    }
  }

  /**
   * Called before an entity is updated in the database.
   *
   * @param entity the entity about to be updated
   */
  @PreUpdate
  public void preUpdate(Object entity) {
    if (LOG.isTraceEnabled()) {
      LOG.trace(
        "Pre-update: {} [{}]",
        entity.getClass().getSimpleName(),
        getEntityId(entity)
      );
    }
  }

  /**
   * Called after an entity has been updated in the database.
   *
   * @param entity the updated entity
   */
  @PostUpdate
  public void postUpdate(Object entity) {
    String entityType = entity.getClass().getSimpleName();
    String entityId = getEntityId(entity);

    LOG.debug("Updated {}: {}", entityType, entityId);

    // Special logging for SecureFileEntity updates
    if (entity instanceof SecureFileEntity secureFile) {
      LOG.debug(
        "SecureFile updated: id={}, pathReference={}",
        secureFile.getId(),
        secureFile.getPathReference()
      );
    }
  }

  /**
   * Called before an entity is removed from the database.
   *
   * @param entity the entity about to be removed
   */
  @PreRemove
  public void preRemove(Object entity) {
    if (LOG.isTraceEnabled()) {
      LOG.trace(
        "Pre-remove: {} [{}]",
        entity.getClass().getSimpleName(),
        getEntityId(entity)
      );
    }
  }

  /**
   * Called after an entity has been removed from the database.
   *
   * @param entity the removed entity
   */
  @PostRemove
  public void postRemove(Object entity) {
    String entityType = entity.getClass().getSimpleName();
    String entityId = getEntityId(entity);

    LOG.info("Deleted {}: {}", entityType, entityId);

    // Special logging for SecureFileEntity deletion
    if (entity instanceof SecureFileEntity secureFile) {
      LOG.info(
        "SecureFile deleted: id={}, owner={}, expiry={}",
        secureFile.getId(),
        secureFile.getOwnerId(),
        secureFile.getExpiryDate()
      );
    }
  }

  private String getEntityId(Object entity) {
    try {
      if (entity instanceof SecureFileEntity secureFile) {
        return secureFile.getId() != null ? secureFile.getId() : "null-id";
      }

      // Try to get ID via reflection for other entities
      var method = entity.getClass().getMethod("getId");
      Object id = method.invoke(entity);
      return id != null ? id.toString() : "null-id";
    } catch (Exception e) {
      return "unknown";
    }
  }
}
