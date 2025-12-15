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
 * Input and output models for quiz mode use cases.
 *
 * <p>This package contains immutable record types that serve as data transfer
 * objects between the REST controllers and the quiz use case implementations.
 * These models decouple the HTTP layer from the domain logic.
 *
 * <h2>Model Types</h2>
 * <ul>
 *   <li>{@link com.voltzug.cinder.spring.infra.mode.quiz.usecase.model.AlphaUploadInput}
 *       — Input for alpha upload containing blob, envelope, salt, gate hash, and questions</li>
 *   <li>{@link com.voltzug.cinder.spring.infra.mode.quiz.usecase.model.DownloadInitResult}
 *       — Result of session initialization containing session ID and encrypted questions</li>
 *   <li>{@link com.voltzug.cinder.spring.infra.mode.quiz.usecase.model.DownloadResult}
 *       — Result of successful download containing blob, envelope, and salt</li>
 * </ul>
 *
 * <h2>Design Notes</h2>
 * <ul>
 *   <li>All models are immutable Java records with validation in compact constructors</li>
 *   <li>Models use domain value objects (Blob, GateHash, QuizQuestions) rather than raw bytes</li>
 *   <li>Null-safety is enforced via {@code Objects.requireNonNull} checks</li>
 * </ul>
 *
 * @see com.voltzug.cinder.spring.infra.mode.quiz.usecase.AlphaQuizUploadUseCase
 * @see com.voltzug.cinder.spring.infra.mode.quiz.usecase.AlphaQuizDownloadUseCase
 */
package com.voltzug.cinder.spring.infra.mode.quiz.usecase.model;