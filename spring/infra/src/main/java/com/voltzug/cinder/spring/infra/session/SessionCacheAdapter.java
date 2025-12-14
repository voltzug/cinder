package com.voltzug.cinder.spring.infra.session;

import com.voltzug.cinder.core.domain.entity.Session;
import com.voltzug.cinder.core.domain.valueobject.id.SessionId;
import com.voltzug.cinder.core.exception.InvalidSessionException;
import com.voltzug.cinder.core.port.out.ClockPort;
import com.voltzug.cinder.core.port.out.SessionCachePort;
import com.voltzug.cinder.spring.infra.logging.InfraLogger;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import org.springframework.stereotype.Component;

/**
 * Service for managing in-memory session cache.
 *
 * <p><strong>Security Notes:</strong>
 * <ul>
 *   <li>Sessions are stored in a thread-safe ConcurrentHashMap</li>
 *   <li>Sessions expire after a configurable timeout (default: 5 minutes)</li>
 *   <li>Invalid or expired sessions are automatically cleaned up</li>
 * </ul>
 *
 * @see Session
 */
@Component
public class SessionCacheAdapter implements SessionCachePort {

  private static final InfraLogger LOG = InfraLogger.of(
    SessionCacheAdapter.class
  );

  private final Map<String, Session> _sessions = new ConcurrentHashMap<>();
  private final ClockPort _clock;

  /**
   * Constructs the DownloadSessionManager with a clock for time operations.
   *
   * @param clock the clock port for getting current time
   */
  public SessionCacheAdapter(ClockPort clock) {
    _clock = clock;
  }

  @Override
  public void save(@NonNull Session session) throws InvalidSessionException {
    SessionId sessionId = session.id();
    Instant now = _clock.now();
    if (session.isExpired(now)) {
      if (LOG.isTraceEnabled()) {
        LOG.trace("Tried to save expired session: {}", session.toString());
      }
      throw new InvalidSessionException(sessionId, "Session has expired");
    }
    String id = sessionId.value();
    _sessions.put(id, session);
    if (LOG.isDebugEnabled()) {
      LOG.debug(
        "Session saved to cache: sessionId={}, expiresAt={}",
        id,
        session.expiresAt()
      );
    }
  }

  @Override
  public Optional<Session> get(@NonNull SessionId sessionId) {
    String id = sessionId.value();
    Session session = _sessions.get(id);
    if (session == null) {
      return Optional.empty();
    }

    Instant now = _clock.now();
    if (session.isExpired(now)) {
      _sessions.remove(id);
      if (LOG.isDebugEnabled()) {
        LOG.debug("Session expired and removed from cache: sessionId={}", id);
      }
      return Optional.empty();
    }
    return Optional.of(session);
  }

  @Override
  public void delete(@NonNull SessionId sessionId) {
    String id = sessionId.value();
    Session removed = _sessions.remove(id);
    if (removed != null) {
      if (LOG.isTraceEnabled()) {
        LOG.trace("Session deleted from cache: sessionId={}", id);
      }
    }
  }
}
