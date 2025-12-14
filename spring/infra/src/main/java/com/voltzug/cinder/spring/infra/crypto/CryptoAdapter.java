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
package com.voltzug.cinder.spring.infra.crypto;

import com.voltzug.cinder.core.common.utils.Assert;
import com.voltzug.cinder.core.common.utils.SafeArrays;
import com.voltzug.cinder.core.domain.valueobject.Hmac;
import com.voltzug.cinder.core.domain.valueobject.SessionSecret;
import com.voltzug.cinder.core.exception.CryptoOperationException;
import com.voltzug.cinder.core.port.out.CryptoPort;
import com.voltzug.cinder.spring.infra.logging.InfraLogger;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Component;

/**
 * Infrastructure adapter implementing cryptographic operations for the Zero-Knowledge architecture.
 *
 * <p>This adapter provides:
 * <ul>
 *   <li><strong>Hashing:</strong> SHA256</li>
 *   <li><strong>HMAC Verification:</strong> HMAC-SHA256 for upload integrity</li>
 *   <li><strong>Secure Random Generation:</strong> For salts, secrets, and session IDs</li>
 * </ul>
 *
 * @see CryptoPort
 * @see PepperAdapter
 */
@Component
public class CryptoAdapter implements CryptoPort {

  /** 1024^2 */
  private static final int RANDOM_MAX_BYTES = 1048576;
  private static final int SHA256_LENGTH = 32;
  private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
  private static final InfraLogger LOG = InfraLogger.of(CryptoAdapter.class);

  private final SecureRandom _secureRandom = new SecureRandom();
  private final Mac _mac;

  /**
   * Constructs the CryptoAdapter with required dependencies.
   *
   * @param pepperService the service providing access to the cryptographic pepper
   */
  public CryptoAdapter() {
    try {
      _mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
    } catch (GeneralSecurityException e) {
      throw new IllegalStateException("Failed to initialize mac", e);
    }
  }

  private void _resetMac() {
    try {
      _mac.init(
        new SecretKeySpec(new byte[SHA256_LENGTH], HMAC_SHA256_ALGORITHM)
      );
    } catch (GeneralSecurityException e) {
      LOG.warn("Cannot reset mac {}", e);
    }
  }

  @Override
  public byte[] randomBytes(int length) {
    Assert.range(1, RANDOM_MAX_BYTES);
    byte[] bytes = new byte[length];
    _secureRandom.nextBytes(bytes);
    return bytes;
  }

  @Override
  public Hmac hmac(final SessionSecret secret, final byte[] data)
    throws CryptoOperationException {
    try (secret) {
      if (secret.size() != SHA256_LENGTH) {
        throw new IllegalArgumentException(
          "SessionSecret must be 32 bytes for HMAC-SHA256"
        );
      }
      try {
        SecretKeySpec keySpec = new SecretKeySpec(
          secret.getBytes(),
          HMAC_SHA256_ALGORITHM
        );
        _mac.init(keySpec);
        byte[] hmacBytes = _mac.doFinal(data);
        return new Hmac(hmacBytes);
      } catch (GeneralSecurityException e) {
        _resetMac();
        throw new CryptoOperationException("Failed to compute HMAC", e);
      }
    }
  }

  @Override
  public boolean verifyHmac(
    final SessionSecret secret,
    final byte[] data,
    final Hmac expectedHmac
  ) throws CryptoOperationException {
    try (expectedHmac; var actualHmac = hmac(secret, data)) {
      return SafeArrays.equals(expectedHmac.getBytes(), actualHmac.getBytes());
    }
  }
}
