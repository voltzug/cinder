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
package com.voltzug.cinder.spring.infra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * NOTE: The infra module is intended as a library of adapters and Spring beans,
 * not as a standalone Spring Boot application. This entry point exists only for
 * integration testing and development purposes. The main application should be
 * started from the 'rest' module, which composes and wires infra's beans.
 */
@SpringBootApplication
public class InfraApplication {

  public static void main(String[] args) {
    SpringApplication.run(InfraApplication.class, args);
  }
}
