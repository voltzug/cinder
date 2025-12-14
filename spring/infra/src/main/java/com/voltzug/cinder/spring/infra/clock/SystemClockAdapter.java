package com.voltzug.cinder.spring.infra.clock;

import com.voltzug.cinder.core.port.out.ClockPort;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * Infrastructure adapter providing system clock functionality.
 * @see ClockPort
 */
@Component
public class SystemClockAdapter implements ClockPort {

  /**
   * Returns the current instant in UTC from the system clock.
   *
   * @return the current instant
   */
  @Override
  public Instant now() {
    return Instant.now();
  }
}
