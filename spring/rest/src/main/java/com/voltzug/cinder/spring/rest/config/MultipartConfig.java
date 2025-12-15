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
package com.voltzug.cinder.spring.rest.config;

import com.voltzug.cinder.spring.infra.logging.InfraLogger;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for multipart file upload handling.
 *
 * <p>Multipart settings are configured via {@code application.properties}:
 * <ul>
 *   <li>{@code spring.servlet.multipart.enabled=true}</li>
 *   <li>{@code spring.servlet.multipart.max-file-size=100MB}</li>
 *   <li>{@code spring.servlet.multipart.max-request-size=150MB}</li>
 *   <li>{@code spring.servlet.multipart.file-size-threshold=2KB}</li>
 * </ul>
 *
 * <p><strong>Security Notes:</strong>
 * <ul>
 *   <li>These limits prevent denial-of-service via large file uploads</li>
 *   <li>Files exceeding the threshold are written to temp storage, not held in memory</li>
 *   <li>Adjust limits based on deployment environment and expected file sizes</li>
 * </ul>
 *
 * @see org.springframework.web.multipart.MultipartFile
 */
@Configuration
public class MultipartConfig {

  private static final InfraLogger LOG = InfraLogger.of(MultipartConfig.class);

  @Value("${spring.servlet.multipart.max-file-size:9MB}")
  private String maxFileSize;

  @Value("${spring.servlet.multipart.max-request-size:11MB}")
  private String maxRequestSize;

  @Value("${spring.servlet.multipart.file-size-threshold:2KB}")
  private String fileSizeThreshold;

  /**
   * Logs the configured multipart settings on startup.
   */
  @PostConstruct
  public void logConfiguration() {
    LOG.info(
      "Multipart configuration: maxFileSize={}, maxRequestSize={}, threshold={}",
      maxFileSize,
      maxRequestSize,
      fileSizeThreshold
    );
  }
}
