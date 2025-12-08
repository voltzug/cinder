package com.voltzug.cinder.spring.rest;
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
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main Spring Boot application entry point for the Cinder REST API.
 *
 * <p>This application serves:
 * <ul>
 *   <li>REST API endpoints for file upload and download operations</li>
 *   <li>Svelte SPA as static resources</li>
 * </ul>
 *
 * <p>Component scanning includes:
 * <ul>
 *   <li>{@code com.voltzug.cinder.spring.rest} — Controllers, DTOs, and REST configuration</li>
 *   <li>{@code com.voltzug.cinder.spring.infra} — Infrastructure adapters, repositories, and services</li>
 * </ul>
 */
@SpringBootApplication
@ComponentScan(
  basePackages = {
    "com.voltzug.cinder.spring.rest", "com.voltzug.cinder.spring.infra",
  }
)
public class RestApplication {

  public static void main(String[] args) {
    SpringApplication.run(RestApplication.class, args);
  }
}
