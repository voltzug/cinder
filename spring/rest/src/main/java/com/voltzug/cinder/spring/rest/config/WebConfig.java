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
import java.io.IOException;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

/**
 * Web MVC configuration for the Cinder REST API.
 *
 * <p>Configures:
 * <ul>
 *   <li><strong>Static Resource Handling:</strong> Serves Svelte SPA from classpath:/static/</li>
 *   <li><strong>SPA Routing Fallback:</strong> Redirects non-API 404s to index.html for client-side routing</li>
 *   <li><strong>CORS:</strong> Cross-origin resource sharing for API endpoints</li>
 * </ul>
 *
 * <p><strong>SPA Fallback Strategy:</strong>
 * The {@link PathResourceResolver} is configured to serve {@code index.html} for any
 * non-existent resource path that doesn't start with {@code api/}. This enables
 * client-side routing in the Svelte SPA while preserving proper 404 responses for
 * missing API endpoints.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

  private static final InfraLogger LOG = InfraLogger.of(WebConfig.class);

  /**
   * Configures resource handlers for serving static assets and SPA fallback.
   *
   * <p>The configuration:
   * <ol>
   *   <li>Serves static resources from {@code classpath:/static/}</li>
   *   <li>Enables resource chain for efficient resolution</li>
   *   <li>Falls back to {@code index.html} for non-API routes (SPA routing)</li>
   * </ol>
   *
   * @param registry the resource handler registry
   */
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry
      .addResourceHandler("/**")
      .addResourceLocations("classpath:/static/")
      .resourceChain(true)
      .addResolver(new SpaFallbackResourceResolver());

    LOG.info("Configured static resource handlers with SPA fallback");
  }

  /**
   * Configures CORS mappings for API endpoints.
   *
   * <p>Allows cross-origin requests to {@code /api/**} endpoints.
   * Production deployments should restrict {@code allowedOrigins} to specific domains.
   *
   * @param registry the CORS registry
   */
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
      .addMapping("/api/**")
      .allowedOrigins("*") //-TO-DO: Tighten in production
      .allowedMethods("GET", "POST", "OPTIONS")
      .allowedHeaders("*")
      .exposedHeaders(
        "X-Cinder-Protocol-Version",
        "X-Cinder-Salt1-Length",
        "Content-Length"
      )
      .maxAge(3600);

    LOG.info("Configured CORS for /api/** endpoints");
  }

  /**
   * Custom resource resolver that implements SPA fallback logic.
   *
   * <p>For any request that:
   * <ul>
   *   <li>Does NOT start with {@code api/}</li>
   *   <li>Does NOT resolve to an existing static resource</li>
   * </ul>
   * This resolver returns {@code index.html} to enable client-side routing.
   */
  private static class SpaFallbackResourceResolver
    extends PathResourceResolver {

    private static final InfraLogger LOG = InfraLogger.of(
      SpaFallbackResourceResolver.class
    );

    @Override
    protected Resource getResource(String resourcePath, Resource location)
      throws IOException {
      // Try to resolve the actual resource first
      Resource resource = location.createRelative(resourcePath);

      if (resource.exists() && resource.isReadable()) {
        if (LOG.isTraceEnabled()) {
          LOG.trace("Serving static resource: {}", resourcePath);
        }
        return resource;
      }

      // Don't fallback for API routes — let them 404 properly
      if (resourcePath.startsWith("api/") || resourcePath.startsWith("api")) {
        LOG.debug("API route not found, returning null: {}", resourcePath);
        return null;
      }

      // SPA fallback: serve index.html for client-side routing
      Resource indexHtml = new ClassPathResource("/static/index.html");

      if (indexHtml.exists() && indexHtml.isReadable()) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("SPA fallback to index.html for path: {}", resourcePath);
        }
        return indexHtml;
      }

      // No index.html available (likely during development without UI build)
      LOG.warn(
        "SPA fallback requested but index.html not found. Path: {}",
        resourcePath
      );
      return null;
    }
  }
}
