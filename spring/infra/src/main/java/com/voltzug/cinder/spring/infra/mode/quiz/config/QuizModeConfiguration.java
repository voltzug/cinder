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
package com.voltzug.cinder.spring.infra.mode.quiz.config;

import com.voltzug.cinder.core.port.out.SecureFileRepositoryPort;
import com.voltzug.cinder.spring.infra.db.adapter.SecureFileRepositoryAdapter;
import com.voltzug.cinder.spring.infra.db.repository.SecureFileRepository;
import com.voltzug.cinder.spring.infra.mode.quiz.entity.GateHash;
import com.voltzug.cinder.spring.infra.mode.quiz.entity.QuizQuestions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Spring configuration for quiz-mode specific beans.
 *
 * <p>This configuration provides typed bean instances for the quiz-based
 * file sharing mode, including:
 * <ul>
 *   <li>SecureFileRepositoryPort with GateHash and QuizQuestions generics</li>
 * </ul>
 *
 * <p>The beans defined here are marked as {@link Primary} to be preferred
 * over generic alternatives when multiple candidates exist.
 */
@Configuration
public class QuizModeConfiguration {

  /**
   * Provides a quiz-typed SecureFileRepositoryPort bean.
   *
   * <p>This bean is parameterized with:
   * <ul>
   *   <li>{@code V = GateHash} — the gate verification hash (SHA256 of answers||nonce)</li>
   *   <li>{@code C = QuizQuestions} — the encrypted quiz questions context</li>
   * </ul>
   *
   * @param repository the JPA repository for SecureFileEntity
   * @return a typed SecureFileRepositoryPort for quiz mode
   */
  @Bean
  @Primary
  public SecureFileRepositoryPort<GateHash, QuizQuestions> quizSecureFileRepository(
    SecureFileRepository repository
  ) {
    return new SecureFileRepositoryAdapter<>(repository);
  }
}