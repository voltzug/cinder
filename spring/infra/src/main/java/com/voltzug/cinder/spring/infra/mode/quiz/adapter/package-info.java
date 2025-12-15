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
 * Infrastructure adapters for the quiz-based (lite) file sharing mode.
 *
 * <p>This package contains quiz-specific adapter implementations that extend
 * or specialize the generic infrastructure adapters to handle quiz mode's
 * unique requirements for gate verification and encrypted question storage.
 *
 * <h2>Adapters</h2>
 * <ul>
 *   <li>{@link com.voltzug.cinder.spring.infra.mode.quiz.adapter.QuizDownloadLimitAdapter}
 *       — Extends {@link com.voltzug.cinder.spring.infra.session.DownloadLimitAdapter}
 *       to initialize download limits with GateHash and QuizQuestions gate data</li>
 * </ul>
 *
 * <h2>Gate Mechanism</h2>
 * <p>In quiz mode, access control is implemented via:</p>
 * <ul>
 *   <li><strong>GateHash (gateBox):</strong> SHA256 hash of (plain_answers || quizANonce),
 *       stored server-side for verification</li>
 *   <li><strong>QuizQuestions (gateContext):</strong> Encrypted quiz questions that the
 *       downloader must decrypt and answer correctly</li>
 * </ul>
 *
 * <p>The adapters in this package ensure these quiz-specific values are properly
 * persisted to and retrieved from the AccessLinkEntity.</p>
 *
 * @see com.voltzug.cinder.spring.infra.mode.quiz.entity.GateHash
 * @see com.voltzug.cinder.spring.infra.mode.quiz.entity.QuizQuestions
 * @see com.voltzug.cinder.spring.infra.session.DownloadLimitAdapter
 */
package com.voltzug.cinder.spring.infra.mode.quiz.adapter;