// Cinder - zero-knowledge file transfer that burns after access
// Copyright (C) 2025  voltzug
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Affero General Public License as published
// by the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Affero General Public License for more details.
//
// You should have received a copy of the GNU Affero General Public License
// along with this program.  If not, see <https://www.gnu.org/licenses/>.
package com.voltzug.cinder.spring.infra.config;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Type-safe configuration properties for the Cinder infrastructure module.
 * Binds properties prefixed with "cinder".
 *
 * <p>Properties are loaded from (in order of precedence):
 * <ol>
 *   <li>Spring Boot Credential Store (~/.config/spring-boot/credentials.properties)</li>
 *   <li>Environment variables (e.g., CINDER_PEPPER_HEX_1)</li>
 *   <li>application.properties (for non-sensitive values only)</li>
 * </ol>
 *
 * @see org.springframework.boot.context.properties.ConfigurationProperties
 */
@ConfigurationProperties(prefix = "cinder")
@Getter
@Setter
public class CinderProperties {

  /** File storage configuration. */
  private Storage storage = new Storage();

  /** Scheduler configuration. */
  private Scheduler scheduler = new Scheduler();

  /** Session management configuration. */
  private Session session = new Session();

  /** Pepper configuration. */
  private Pepper pepper = new Pepper();

  /** File storage settings. */
  public static class Storage {

    /** Local file storage configuration. */
    @Getter
    @Setter
    private Local local = new Local();

    /** Local file system storage settings. */
    public static class Local {

      /** Directory for encrypted file blobs. Defaults to "./data/files". */
      @Getter
      @Setter
      private String directory = "./data/files";
    }
  }

  /** Scheduled tasks configuration. */
  @Getter
  @Setter
  public static class Scheduler {

    /** Cron for expired files cleanup. Defaults to hourly. */
    private String cleanupCron = "0 0 * * * *";

    /** Whether cleanup scheduler is enabled. Defaults to true. */
    private boolean enabled = true;
  }

  /** Download session settings. */
  @Getter
  @Setter
  public static class Session {

    /** Session timeout in seconds. Defaults to 300. */
    private int timeoutSeconds = 300;

    /** Max download attempts per session. Defaults to 5. */
    private int maxAttempts = 5;
  }

  /** Pepper settings for encryption. */
  @Getter
  @Setter
  public static class Pepper {

    /**
     * Map of pepper versions to hex strings (32 bytes = 64 hex chars).
     * Key is version (e.g., "1", "2").
     *
     * <p><strong>Security:</strong> Must be loaded from credential store or environment variable.
     * Never commit to source control.
     */
    private Map<String, String> hex;

    /** Version to use for new encryptions. Must exist as key in {@link #hex}. */
    private Short version;
  }
}
