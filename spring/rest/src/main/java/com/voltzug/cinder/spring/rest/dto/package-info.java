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

/**
 * Data Transfer Objects for the Cinder REST API.
 *
 * <p>This package contains request and response DTOs used by the REST controllers
 * to exchange data with clients. DTOs provide a clean separation between the HTTP
 * layer and the domain model, enabling:
 *
 * <ul>
 *   <li>JSON serialization/deserialization via Jackson</li>
 *   <li>Request validation via Bean Validation annotations</li>
 *   <li>API versioning and backwards compatibility</li>
 *   <li>Documentation generation via OpenAPI/Swagger</li>
 * </ul>
 *
 * <h2>Subpackages</h2>
 * <ul>
 *   <li>{@link com.voltzug.cinder.spring.rest.dto.alpha} — DTOs for alpha version
 *       quiz mode endpoints (simplified, bypasses HMAC/session handshake)</li>
 * </ul>
 *
 * <h2>Conventions</h2>
 * <ul>
 *   <li>Request DTOs are suffixed with {@code Request}</li>
 *   <li>Response DTOs are suffixed with {@code Response}</li>
 *   <li>All DTOs are immutable Java records</li>
 *   <li>Validation is performed in compact constructors</li>
 *   <li>Binary data is Base64-encoded in JSON representations</li>
 * </ul>
 *
 * @see com.voltzug.cinder.spring.rest.controller
 * @see com.voltzug.cinder.spring.rest.exception.ApiError
 */
package com.voltzug.cinder.spring.rest.dto;