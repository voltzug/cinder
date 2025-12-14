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

import com.voltzug.cinder.core.common.valueobject.safe.SafeBlob;
import com.voltzug.cinder.core.domain.valueobject.SealedBlob;
import com.voltzug.cinder.core.port.out.PepperPort;
import com.voltzug.cinder.spring.infra.config.CinderProperties;
import com.voltzug.cinder.spring.infra.logging.InfraLogger;
import jakarta.annotation.PreDestroy;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.NonNull;
import org.springframework.stereotype.Component;

/**
 * PepperAdapter is a Spring component that manages cryptographic "peppers" for
 * encryption and decryption operations using AES-GCM. It loads pepper values from
 * configuration, validates them, and provides methods to seal (encrypt) and unseal
 * (decrypt) data using the active pepper version.
 * <p>
 * Peppers are stored securely in memory and wiped on application shutdown.
 * This adapter implements the {@link PepperPort} interface
 * </p>
 */
@Component
public class PepperAdapter implements PepperPort, AutoCloseable {

  /** Expected length of pepper (256b) */
  public static final int PEPPER_LENGTH = 32;

  /** 64 chars */
  private static final int PEPPER_LENGTH_HEX = PEPPER_LENGTH * 2;
  private static final int GCM_NONCE_LENGTH = 12;
  private static final int GCM_TAG_LENGTH = 128;
  private static final String ALGORITHM = "AES/GCM/NoPadding";
  private static final String KEY_ALGORITHM = "AES";
  private static final InfraLogger LOG = InfraLogger.of(PepperAdapter.class);

  private final short _activeVersion;
  private final HashMap<Short, SafeBlob> _pepperMap = new HashMap<>();
  private final SecureRandom _random = new SecureRandom();
  private final Cipher _cipher;

  /**
   * Constructs the PepperAdapter, loading and validating the pepper from configuration.
   *
   * @param properties the Cinder configuration properties containing the pepper hex
   * @throws IllegalStateException if the pepper is missing or invalid
   */
  public PepperAdapter(CinderProperties properties) {
    var pepper = properties.getPepper();
    Map<String, String> hexMap = pepper.getHex();
    Short activeVer = pepper.getVersion();

    if (hexMap == null || hexMap.isEmpty()) {
      throw new IllegalStateException(
        "No Cinder peppers configured. " +
          "Set 'cinder.pepper-hex.[version]' in the Spring Boot credential store."
      );
    }
    if (activeVer == null) {
      throw new IllegalStateException(
        "Active pepper version is not configured (cinder.active-pepper-version)."
      );
    }
    this._activeVersion = activeVer;

    String hex;
    Iterator<Map.Entry<String, String>> it = hexMap.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<String, String> entry = it.next();
      String versionStr = entry.getKey();
      hex = entry.getValue();

      try {
        short version = Short.parseShort(versionStr);
        _validatePepperHex(hex, version);

        byte[] bytes = _hex2B(hex);
        _pepperMap.put(version, new SafeBlob(bytes));

        try {
          entry.setValue(null);
          it.remove();
        } catch (UnsupportedOperationException ignored) {
          // Map might be immutable, proceed
        }
      } catch (NumberFormatException e) {
        throw new IllegalStateException(
          "Invalid pepper version number: " + versionStr,
          e
        );
      }
    }

    if (!_pepperMap.containsKey(_activeVersion)) {
      throw new IllegalStateException(
        "Active pepper version " +
          _activeVersion +
          " was not found in configured peppers."
      );
    }
    short nextVersion = (short) (_activeVersion + 1);
    if (!_pepperMap.containsKey(nextVersion)) {
      LOG.warn(
        "No next pepper version v{} found in configuration. Consider updating peppers.",
        nextVersion
      );
    }

    try {
      hexMap.clear();
    } catch (UnsupportedOperationException ignored) {
      LOG.debug("Cannot clear pepper properties map (may be immutable)");
    }
    pepper.setHex(null);
    properties.setPepper(pepper);

    try {
      _cipher = Cipher.getInstance(ALGORITHM);
    } catch (GeneralSecurityException e) {
      throw new IllegalStateException("Failed to initialize cipher", e);
    }
  }

  private void _resetCipher() {
    try {
      _cipher.init(
        Cipher.ENCRYPT_MODE,
        new SecretKeySpec(new byte[PEPPER_LENGTH], KEY_ALGORITHM),
        new GCMParameterSpec(GCM_TAG_LENGTH, new byte[GCM_NONCE_LENGTH])
      );
    } catch (GeneralSecurityException e) {
      LOG.warn("Cannot reset cipher {}", e);
    }
  }

  @Override
  public short actualPepperVersion() {
    return _activeVersion;
  }

  @Override
  public SealedBlob seal(@NonNull byte[] data) {
    SafeBlob pepperBlob = _pepperMap.get(_activeVersion);
    if (pepperBlob == null || pepperBlob.isResolved()) {
      throw new IllegalStateException(
        "Active pepper " + _activeVersion + " is not available"
      );
    }

    byte[] nonce = new byte[GCM_NONCE_LENGTH];
    _random.nextBytes(nonce);

    try {
      // Security: getBytes() exposes the internal array
      SecretKey key = new SecretKeySpec(pepperBlob.getBytes(), KEY_ALGORITHM);
      GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, nonce);

      _cipher.init(Cipher.ENCRYPT_MODE, key, spec);
      byte[] ciphertext = _cipher.doFinal(data);

      return SealedBlob.build(ciphertext, nonce, _activeVersion);
    } catch (GeneralSecurityException e) {
      _resetCipher();
      throw new RuntimeException("Encryption failed", e);
    }
  }

  @Override
  public byte[] unseal(@NonNull SealedBlob sealedBlob) {
    short version = sealedBlob.getPepperVersion();
    SafeBlob pepperBlob = _pepperMap.get(version);

    if (pepperBlob == null) {
      throw new IllegalArgumentException(
        "Pepper version " + version + " not found. Cannot unseal."
      );
    }
    if (pepperBlob.isResolved()) {
      throw new IllegalStateException(
        "Pepper version " + version + " has been wiped."
      );
    }

    try {
      byte[] nonce = sealedBlob.getNonce();
      byte[] ciphertext = sealedBlob.getValue();

      SecretKey key = new SecretKeySpec(pepperBlob.getBytes(), KEY_ALGORITHM);
      GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, nonce);

      _cipher.init(Cipher.DECRYPT_MODE, key, spec);
      return _cipher.doFinal(ciphertext);
    } catch (GeneralSecurityException e) {
      _resetCipher();
      throw new RuntimeException(
        "Decryption failed (integrity check failed or invalid key)",
        e
      );
    }
  }

  /**
   * Securely wipes the pepper from memory.
   * Called automatically by Spring on application shutdown via {@link PreDestroy}.
   */
  @PreDestroy
  @Override
  public void close() {
    _pepperMap.forEach((k, v) -> v.close());
    _pepperMap.clear();
  }

  /**
   * Validates that the pepper hex string is present and has the correct format.
   *
   * @param pepperHex the pepper as a hexadecimal string
   * @param version the version number for error reporting
   */
  private static void _validatePepperHex(final String pepperHex, short version)
    throws IllegalStateException {
    if (pepperHex == null) {
      throw new IllegalStateException(
        "Pepper hex for version " + version + " is null"
      );
    }

    if (pepperHex.length() != PEPPER_LENGTH_HEX) {
      throw new IllegalStateException(
        String.format(
          "Invalid pepper length for version %d: expected %d hex characters (256 bits), got %d",
          version,
          PEPPER_LENGTH_HEX,
          pepperHex.length()
        )
      );
    }

    for (int i = 0; i < pepperHex.length(); i++) {
      char c = pepperHex.charAt(i);
      if (
        !((c >= '0' && c <= '9') ||
          (c >= 'a' && c <= 'f') ||
          (c >= 'A' && c <= 'F'))
      ) {
        throw new IllegalStateException(
          "Invalid pepper format for version " +
            version +
            ": must contain only hexadecimal characters"
        );
      }
    }
  }

  private static byte[] _hex2B(final String hex) {
    int len = hex.length();
    byte[] result = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      int high = Character.digit(hex.charAt(i), 16);
      int low = Character.digit(hex.charAt(i + 1), 16);
      result[i / 2] = (byte) ((high << 4) | low);
    }
    return result;
  }
}
