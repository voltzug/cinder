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
 * REST controllers for the Cinder file sharing API.
 *
 * <p>This package contains Spring MVC controllers that expose HTTP endpoints
 * for file upload and download operations. Controllers delegate business logic
 * to use cases in the infrastructure layer and handle HTTP-specific concerns
 * such as request/response mapping, content negotiation, and header management.
 *
 * <h2>Alpha Controllers</h2>
 * <ul>
 *   <li>{@link com.voltzug.cinder.spring.rest.controller.AlphaQuizController}
 *       — Simplified quiz mode endpoints for alpha testing (bypasses HMAC/session handshake)</li>
 * </ul>
 *
 * <h2>API Endpoints</h2>
 *
 * <h3>Alpha Quiz Mode</h3>
 * <table border="1">
 *   <tr>
 *     <th>Method</th>
 *     <th>Path</th>
 *     <th>Description</th>
 *   </tr>
 *   <tr>
 *     <td>POST</td>
 *     <td>/api/alpha/upload</td>
 *     <td>Upload encrypted file with quiz metadata (multipart/form-data)</td>
 *   </tr>
 *   <tr>
 *     <td>GET</td>
 *     <td>/api/alpha/download/{linkId}/init</td>
 *     <td>Initialize download session, returns encrypted questions</td>
 *   </tr>
 *   <tr>
 *     <td>POST</td>
 *     <td>/api/alpha/download/{linkId}</td>
 *     <td>Verify access hash and retrieve encrypted file</td>
 *   </tr>
 * </table>
 *
 * <h2>Error Handling</h2>
 * <p>All controllers rely on {@link com.voltzug.cinder.spring.rest.exception.GlobalExceptionHandler}
 * for centralized exception handling. Domain exceptions are mapped to appropriate
 * HTTP status codes and {@link com.voltzug.cinder.spring.rest.exception.ApiError} responses.</p>
 *
 * <h2>Security Notes</h2>
 * <ul>
 *   <li>Alpha endpoints intentionally skip HMAC and session handshake for testing</li>
 *   <li>Production endpoints should implement full protocol security</li>
 *   <li>Sensitive data (keys, salts) is Base64-encoded in headers, never logged</li>
 * </ul>
 *
 * @see com.voltzug.cinder.spring.rest.dto.alpha
 * @see com.voltzug.cinder.spring.rest.exception.GlobalExceptionHandler
 */
package com.voltzug.cinder.spring.rest.controller;