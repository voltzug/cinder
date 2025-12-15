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
 * Use cases for the quiz-based (lite) file sharing mode.
 *
 * <p>This package contains application service implementations for the simplified
 * quiz-based file upload and download flow. The alpha implementations bypass
 * HMAC and full session handshake verification for testing purposes.
 *
 * <h2>Alpha Use Cases</h2>
 * <ul>
 *   <li>{@link com.voltzug.cinder.spring.infra.mode.quiz.usecase.AlphaQuizUploadUseCase}
 *       — Single-step upload: store blob, seal metadata, persist file</li>
 *   <li>{@link com.voltzug.cinder.spring.infra.mode.quiz.usecase.AlphaQuizDownloadUseCase}
 *       — Two-step download: init session, verify access hash, return file</li>
 * </ul>
 *
 * <h2>Model Types</h2>
 * <p>The {@code model} subpackage contains input/output records for the use cases:</p>
 * <ul>
 *   <li>{@link com.voltzug.cinder.spring.infra.mode.quiz.usecase.model.AlphaUploadInput}
 *       — Input for alpha upload</li>
 *   <li>{@link com.voltzug.cinder.spring.infra.mode.quiz.usecase.model.DownloadInitResult}
 *       — Result of session initialization</li>
 *   <li>{@link com.voltzug.cinder.spring.infra.mode.quiz.usecase.model.DownloadResult}
 *       — Result of successful download verification</li>
 * </ul>
 *
 * <h2>Security Notes (Alpha)</h2>
 * <p>The alpha implementations intentionally skip certain security measures for
 * development and testing:</p>
 * <ul>
 *   <li>No HMAC verification on upload or download</li>
 *   <li>No session secret exchange (uploadSecret/downloadSecret)</li>
 *   <li>No timestamp skew validation</li>
 * </ul>
 * <p>Production implementations should use the full protocol as defined in the
 * core port interfaces.</p>
 *
 * @see com.voltzug.cinder.spring.infra.mode.quiz.entity
 * @see com.voltzug.cinder.spring.infra.mode.quiz.port
 */
package com.voltzug.cinder.spring.infra.mode.quiz.usecase;