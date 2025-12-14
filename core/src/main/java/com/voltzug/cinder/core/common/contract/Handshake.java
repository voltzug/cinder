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
package com.voltzug.cinder.core.common.contract;

/**
 * Handshake contract for challenge-response flows in Cinder.
 *
 * <p>
 * This abstraction defines the protocol for issuing a challenge and verifying a solution,
 * supporting both advanced (e.g., OPAQUE) and lite (e.g., quiz) security modes.
 * <p>
 */
public final class Handshake {

  /**
   * Generic request interface for a PoW agreement.
   *
   * @param <C> Context type for challenge generation.
   * @param <T> Challenge type.
   */
  public static interface IAgreeableRequest<C extends Object, T> {
    /**
     * Issues a challenge based on the provided context.
     *
     * @param context Context for challenge generation (may be null).
     * @return The generated challenge.
     */
    T challenge(C context);
  }

  /**
   * Generic response interface for a PoW agreement.
   *
   * @param <S> Solution type.
   * @param <R> Verification result type.
   */
  public static interface IAgreeableResponse<S, R> {
    /**
     * Verifies the provided solution to the challenge.
     *
     * @param solution The solution submitted by the client.
     * @return The result of verification (e.g., Boolean, token).
     */
    R verify(S solution);
  }
}
