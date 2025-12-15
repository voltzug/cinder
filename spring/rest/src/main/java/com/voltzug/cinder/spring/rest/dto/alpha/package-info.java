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
 * Data Transfer Objects for the alpha version quiz mode REST API.
 *
 * <p>This package contains request and response DTOs for the simplified
 * alpha endpoints that bypass HMAC and full session handshake verification.
 *
 * <h2>DTOs</h2>
 * <ul>
 *   <li>{@link com.voltzug.cinder.spring.rest.dto.alpha.AlphaUploadResponse}
 *       — Response containing generated link ID after upload</li>
 *   <li>{@link com.voltzug.cinder.spring.rest.dto.alpha.AlphaDownloadInitResponse}
 *       — Response containing session ID and encrypted questions</li>
 *   <li>{@link com.voltzug.cinder.spring.rest.dto.alpha.AlphaDownloadRequest}
 *       — Request containing session ID and access hash for verification</li>
 * </ul>
 *
 * <h2>Upload Request</h2>
 * <p>The upload endpoint uses multipart form data with {@code @RequestParam} annotations
 * rather than a dedicated request DTO, accepting:</p>
 * <ul>
 *   <li>{@code blob} — MultipartFile containing encrypted file content</li>
 *   <li>{@code envelope} — Base64-encoded encrypted envelope</li>
 *   <li>{@code salt} — Base64-encoded argon2 salt</li>
 *   <li>{@code gateHash} — Base64-encoded SHA256 of answers||nonce</li>
 *   <li>{@code encryptedQuestions} — Base64-encoded encrypted quiz questions</li>
 *   <li>{@code expiryDate} — ISO-8601 timestamp for file expiry</li>
 *   <li>{@code retryCount} — Maximum download attempts (1-99)</li>
 * </ul>
 *
 * @see com.voltzug.cinder.spring.rest.controller.AlphaQuizController
 */
package com.voltzug.cinder.spring.rest.dto.alpha;