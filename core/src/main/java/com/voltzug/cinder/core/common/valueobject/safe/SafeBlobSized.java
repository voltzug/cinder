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
package com.voltzug.cinder.core.common.valueobject.safe;

import com.voltzug.cinder.core.common.utils.Assert;
import com.voltzug.cinder.core.common.utils.SafeArrays;

public class SafeBlobSized extends SafeBlob {

  private static final int _DISABLE_FIXED_LENGTH = -1;
  private static final int _DISABLE_MOD_CONSTRAINT = 0;

  private static byte[] _validateBuffer(
    final byte[] value,
    int minLength,
    int maxLength,
    int modConstraint,
    int fixedLength
  ) {
    Assert.truly(
      minLength > 0 && maxLength > 0,
      "minLength and maxLength must be > 0, got minLength=" +
        minLength +
        ", maxLength=" +
        maxLength
    );
    Assert.range(minLength, maxLength);
    SafeArrays.assertNotEmpty(value);

    if (fixedLength == _DISABLE_FIXED_LENGTH) {
      if (value.length < minLength) {
        throw new IllegalArgumentException(
          "Value must be at least " + minLength + " bytes, got " + value.length
        );
      }
      if (value.length > maxLength) {
        throw new IllegalArgumentException(
          "Value must be at most " + maxLength + " bytes, got " + value.length
        );
      }
    } else {
      Assert.truly(
        fixedLength > 0,
        "fixedLength must be > 0, got " + fixedLength
      );
      if (value.length != fixedLength) {
        throw new IllegalArgumentException(
          "Value must be exactly " + fixedLength + " bytes, got " + value.length
        );
      }
    }

    if (modConstraint > 0 && (value.length % modConstraint) != 0) {
      throw new IllegalArgumentException(
        "Value length must be a multiple of " +
          modConstraint +
          ", got " +
          value.length
      );
    }

    return SafeArrays.move(value);
  }

  /**
   * Construct a SafeBlobSized with no size constraints.
   */
  public SafeBlobSized(final byte[] value) {
    super(
      _validateBuffer(
        value,
        1,
        Integer.MAX_VALUE,
        _DISABLE_MOD_CONSTRAINT,
        _DISABLE_FIXED_LENGTH
      )
    );
  }

  /**
   * Construct a SafeBlobSized with a mod 4 constraint on length.
   *
   * @param value the byte array to wrap
   * @param mod4Constraint if >0, the length of {@code value} must be a multiple of this value; 0 disables the constraint
   */
  public SafeBlobSized(final byte[] value, boolean mod4Constraint) {
    super(
      _validateBuffer(value, 1, Integer.MAX_VALUE, 4, _DISABLE_FIXED_LENGTH)
    );
  }

  /**
   * Construct a SafeBlobSized with min/max length and optional mod constraint.
   *
   * @param value the byte array to wrap
   * @param minLength minimum allowed length (inclusive)
   * @param maxLength maximum allowed length (inclusive)
   * @param modConstraint if >0, length must be a multiple of this value; 0 disables the constraint
   */
  public SafeBlobSized(
    final byte[] value,
    int minLength,
    int maxLength,
    int modConstraint
  ) {
    super(
      _validateBuffer(
        value,
        minLength,
        maxLength,
        modConstraint,
        _DISABLE_FIXED_LENGTH
      )
    );
  }

  /**
   * Construct a SafeBlobSized with fixed length.
   *
   * @param value the byte array to wrap
   * @param fixedLength required length
   */
  public SafeBlobSized(final byte[] value, int fixedLength) {
    super(
      _validateBuffer(
        value,
        fixedLength,
        fixedLength,
        _DISABLE_MOD_CONSTRAINT,
        fixedLength
      )
    );
  }

  /**
   * Construct a SafeBlobSized with a single length constraint.
   * <p>
   * If {@code isMax} is {@code true}, the {@code lengthConstraint} is treated as the maximum allowed length
   * (inclusive), and the minimum length is set to 0. If {@code isMax} is {@code false}, the {@code lengthConstraint}
   * is treated as the minimum allowed length (inclusive), and the maximum length is set to {@code Integer.MAX_VALUE}.
   * No mod or fixed-length constraint is enforced.
   *
   * @param value the byte array to wrap
   * @param lengthConstraint the minimum or maximum allowed length (inclusive), depending on {@code isMax}
   * @param isMax if {@code true}, {@code lengthConstraint} is the maximum length; if {@code false}, it is the minimum length
   * @throws IllegalArgumentException if the value does not meet the length constraint
   */
  public SafeBlobSized(
    final byte[] value,
    int lengthConstraint,
    boolean isMax
  ) {
    super(
      isMax
        ? _validateBuffer(
            value,
            1,
            lengthConstraint,
            _DISABLE_MOD_CONSTRAINT,
            _DISABLE_FIXED_LENGTH
          )
        : _validateBuffer(
            value,
            lengthConstraint,
            Integer.MAX_VALUE,
            _DISABLE_MOD_CONSTRAINT,
            _DISABLE_FIXED_LENGTH
          )
    );
  }
}
