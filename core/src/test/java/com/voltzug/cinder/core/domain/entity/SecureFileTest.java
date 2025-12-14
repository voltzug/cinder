package com.voltzug.cinder.core.domain.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.voltzug.cinder.core.domain.valueobject.FileSpecs;
import com.voltzug.cinder.core.domain.valueobject.PathReference;
import com.voltzug.cinder.core.domain.valueobject.SealedBlob;
import com.voltzug.cinder.core.domain.valueobject.id.FileId;
import com.voltzug.cinder.core.domain.valueobject.id.LinkId;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive tests for SecureFile<byte[], Object> entity.
 * Tests secure file creation, validation, and expiration logic.
 */
class SecureFileTest {

  // ==================== HELPER METHODS ====================

  private FileId createFileId() {
    return new FileId("test-file-id");
  }

  private LinkId createLinkId() {
    return LinkId.generate();
  }

  private PathReference createBlobPath() {
    return PathReference.from("/tmp/test/blob.bin");
  }

  private SealedBlob createSealedEnvelope() {
    return SealedBlob.build(
      new byte[] { 0x01, 0x02, 0x03, 0x04 },
      new byte[] { 0x10, 0x20, 0x30, 0x40 },
      (short) 1
    );
  }

  private SealedBlob createSealedSalt() {
    return SealedBlob.build(
      new byte[] { 0x05, 0x06, 0x07, 0x08 },
      new byte[] { 0x15, 0x25, 0x35, 0x45 },
      (short) 1
    );
  }

  private byte[] createGateHash() {
    return new byte[32];
  }

  private Object createGateContext() {
    return null;
  }

  private FileSpecs createFileSpecs(Instant expiryDate) {
    return new FileSpecs(expiryDate, 5);
  }

  private SecureFile<byte[], Object> createValidSecureFile(Instant expiryDate) {
    return new SecureFile<byte[], Object>(
      createFileId(),
      createLinkId(),
      createBlobPath(),
      createSealedEnvelope(),
      createSealedSalt(),
      createFileSpecs(expiryDate),
      5,
      Instant.now(),
      createGateHash(),
      createGateContext()
    );
  }

  // ==================== CONSTRUCTION TESTS ====================

  @Test
  void shouldCreateSecureFileWithValidParameters() {
    // Given
    FileId fileId = createFileId();
    LinkId linkId = createLinkId();
    PathReference blobPath = createBlobPath();
    SealedBlob sealedEnvelope = createSealedEnvelope();
    SealedBlob sealedSalt = createSealedSalt();
    byte[] gateBox = createGateHash();
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);
    FileSpecs specs = createFileSpecs(expiryDate);
    int remainingAttempts = 5;
    Instant createdAt = Instant.now();

    // When
    SecureFile<byte[], Object> secureFile = new SecureFile<byte[], Object>(
      fileId,
      linkId,
      blobPath,
      sealedEnvelope,
      sealedSalt,
      specs,
      remainingAttempts,
      createdAt,
      gateBox,
      null
    );

    // Then
    assertNotNull(secureFile);
    assertEquals(fileId, secureFile.fileId());
    assertEquals(linkId, secureFile.linkId());
    assertEquals(blobPath, secureFile.blobPath());
    assertEquals(sealedEnvelope, secureFile.sealedEnvelope());
    assertEquals(sealedSalt, secureFile.sealedSalt());
    assertEquals(gateBox, secureFile.gateBox());
    assertEquals(specs, secureFile.specs());
    assertEquals(remainingAttempts, secureFile.remainingAttempts());
    assertEquals(createdAt, secureFile.createdAt());
  }

  @Test
  void shouldCreateSecureFileWithZeroRemainingAttempts() {
    // Given
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);

    // When
    SecureFile<byte[], Object> secureFile = new SecureFile<byte[], Object>(
      createFileId(),
      createLinkId(),
      createBlobPath(),
      createSealedEnvelope(),
      createSealedSalt(),
      createFileSpecs(expiryDate),
      0,
      Instant.now(),
      createGateHash(),
      createGateContext()
    );

    // Then
    assertEquals(0, secureFile.remainingAttempts());
  }

  @Test
  void shouldCreateSecureFileWithNegativeRemainingAttempts() {
    // Given - Negative attempts might indicate exhausted state
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);

    // When
    SecureFile<byte[], Object> secureFile = new SecureFile<byte[], Object>(
      createFileId(),
      createLinkId(),
      createBlobPath(),
      createSealedEnvelope(),
      createSealedSalt(),
      createFileSpecs(expiryDate),
      -1,
      Instant.now(),
      createGateHash(),
      createGateContext()
    );

    // Then
    assertEquals(-1, secureFile.remainingAttempts());
  }

  // ==================== VALIDATION TESTS ====================

  @Test
  void shouldThrowExceptionForNullFileId() {
    // Given
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);

    // When/Then
    NullPointerException exception = assertThrows(
      NullPointerException.class,
      () ->
        new SecureFile<byte[], Object>(
          null,
          createLinkId(),
          createBlobPath(),
          createSealedEnvelope(),
          createSealedSalt(),
          createFileSpecs(expiryDate),
          5,
          Instant.now(),
          createGateHash(),
          null
        )
    );
    assertEquals("fileId must not be null", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionForNullLinkId() {
    // Given
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);

    // When/Then
    NullPointerException exception = assertThrows(
      NullPointerException.class,
      () ->
        new SecureFile<byte[], Object>(
          createFileId(),
          null,
          createBlobPath(),
          createSealedEnvelope(),
          createSealedSalt(),
          createFileSpecs(expiryDate),
          5,
          Instant.now(),
          createGateHash(),
          null
        )
    );
    assertEquals("linkId must not be null", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionForNullBlobPath() {
    // Given
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);

    // When/Then
    NullPointerException exception = assertThrows(
      NullPointerException.class,
      () ->
        new SecureFile<byte[], Object>(
          createFileId(),
          createLinkId(),
          null,
          createSealedEnvelope(),
          createSealedSalt(),
          createFileSpecs(expiryDate),
          5,
          Instant.now(),
          createGateHash(),
          null
        )
    );
    assertEquals("blobPath must not be null", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionForNullSealedEnvelope() {
    // Given
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);

    // When/Then
    NullPointerException exception = assertThrows(
      NullPointerException.class,
      () ->
        new SecureFile<byte[], Object>(
          createFileId(),
          createLinkId(),
          createBlobPath(),
          null,
          createSealedSalt(),
          createFileSpecs(expiryDate),
          5,
          Instant.now(),
          createGateHash(),
          null
        )
    );
    assertEquals("sealedEnvelope must not be null", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionForNullSealedSalt() {
    // Given
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);

    // When/Then
    NullPointerException exception = assertThrows(
      NullPointerException.class,
      () ->
        new SecureFile<byte[], Object>(
          createFileId(),
          createLinkId(),
          createBlobPath(),
          createSealedEnvelope(),
          null,
          createFileSpecs(expiryDate),
          5,
          Instant.now(),
          createGateHash(),
          null
        )
    );
    assertEquals("sealedSalt must not be null", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionForNullGateHash() {
    // Given
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);

    // When/Then
    NullPointerException exception = assertThrows(
      NullPointerException.class,
      () ->
        new SecureFile<byte[], Object>(
          createFileId(),
          createLinkId(),
          createBlobPath(),
          createSealedEnvelope(),
          createSealedSalt(),
          createFileSpecs(expiryDate),
          5,
          Instant.now(),
          null,
          null
        )
    );
    assertEquals("gateBox must not be null", exception.getMessage());
  }

  @Test
  void shouldAllowNullGateContext() {
    // Given
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);

    // When/Then
    assertDoesNotThrow(() ->
      new SecureFile<byte[], Object>(
        createFileId(),
        createLinkId(),
        createBlobPath(),
        createSealedEnvelope(),
        createSealedSalt(),
        createFileSpecs(expiryDate),
        5,
        Instant.now(),
        createGateHash(),
        null
      )
    );
  }

  @Test
  void shouldThrowExceptionForNullSpecs() {
    // When/Then
    NullPointerException exception = assertThrows(
      NullPointerException.class,
      () ->
        new SecureFile<byte[], Object>(
          createFileId(),
          createLinkId(),
          createBlobPath(),
          createSealedEnvelope(),
          createSealedSalt(),
          null,
          5,
          Instant.now(),
          createGateHash(),
          null
        )
    );
    assertEquals("specs must not be null", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionForNullCreatedAt() {
    // Given
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);

    // When/Then
    NullPointerException exception = assertThrows(
      NullPointerException.class,
      () ->
        new SecureFile<byte[], Object>(
          createFileId(),
          createLinkId(),
          createBlobPath(),
          createSealedEnvelope(),
          createSealedSalt(),
          createFileSpecs(expiryDate),
          5,
          null,
          createGateHash(),
          null
        )
    );
    assertEquals("createdAt must not be null", exception.getMessage());
  }

  // ==================== EXPIRABLE INTERFACE TESTS ====================

  @Test
  void shouldReturnExpiryDateFromSpecs() {
    // Given
    Instant expiryDate = Instant.now().plus(2, ChronoUnit.HOURS);
    SecureFile<byte[], Object> secureFile = createValidSecureFile(expiryDate);

    // When
    Instant result = secureFile.getExpiryDate();

    // Then
    assertEquals(expiryDate, result);
  }

  @Test
  void shouldReturnTrueWhenFileExpired() {
    // Given
    Instant expiryDate = Instant.now().minus(1, ChronoUnit.HOURS);
    SecureFile<byte[], Object> secureFile = createValidSecureFile(expiryDate);

    // When
    boolean expired = secureFile.isExpired(Instant.now());

    // Then
    assertTrue(expired);
  }

  @Test
  void shouldReturnFalseWhenFileNotExpired() {
    // Given
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.HOURS);
    SecureFile<byte[], Object> secureFile = createValidSecureFile(expiryDate);

    // When
    boolean expired = secureFile.isExpired(Instant.now());

    // Then
    assertFalse(expired);
  }

  @Test
  void shouldReturnFalseWhenExactlyAtExpiryTime() {
    // Given
    Instant now = Instant.now();
    SecureFile<byte[], Object> secureFile = createValidSecureFile(now);

    // When
    boolean expired = secureFile.isExpired(now);

    // Then
    assertFalse(expired, "Should not be expired when exactly at expiry time");
  }

  @Test
  void shouldReturnTrueWhenOneMillisecondAfterExpiry() {
    // Given
    Instant expiryDate = Instant.now();
    SecureFile<byte[], Object> secureFile = createValidSecureFile(expiryDate);
    Instant oneMilliAfter = expiryDate.plusMillis(1);

    // When
    boolean expired = secureFile.isExpired(oneMilliAfter);

    // Then
    assertTrue(expired);
  }

  @Test
  void shouldReturnFalseWhenOneMillisecondBeforeExpiry() {
    // Given
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);
    SecureFile<byte[], Object> secureFile = createValidSecureFile(expiryDate);
    Instant oneMilliBefore = expiryDate.minusMillis(1);

    // When
    boolean expired = secureFile.isExpired(oneMilliBefore);

    // Then
    assertFalse(expired);
  }

  @Test
  void shouldDelegateExpiryCheckToSpecs() {
    // Given
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);
    FileSpecs specs = createFileSpecs(expiryDate);
    SecureFile<byte[], Object> secureFile = new SecureFile<byte[], Object>(
      createFileId(),
      createLinkId(),
      createBlobPath(),
      createSealedEnvelope(),
      createSealedSalt(),
      specs,
      5,
      Instant.now(),
      createGateHash(),
      createGateContext()
    );
    Instant checkTime = Instant.now();

    // When/Then
    assertEquals(specs.isExpired(checkTime), secureFile.isExpired(checkTime));
    assertEquals(specs.getExpiryDate(), secureFile.getExpiryDate());
  }

  // ==================== INTERFACE IMPLEMENTATION TESTS ====================

  @Test
  void shouldImplementIExpirable() {
    // Given
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);

    // When
    SecureFile<byte[], Object> secureFile = createValidSecureFile(expiryDate);

    // Then
    assertTrue(secureFile instanceof IExpirable);
  }

  // ==================== EDGE CASE TESTS ====================

  @Test
  void shouldHandleMaxRemainingAttempts() {
    // Given
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);

    // When
    SecureFile<byte[], Object> secureFile = new SecureFile<byte[], Object>(
      createFileId(),
      createLinkId(),
      createBlobPath(),
      createSealedEnvelope(),
      createSealedSalt(),
      createFileSpecs(expiryDate),
      Integer.MAX_VALUE,
      Instant.now(),
      createGateHash(),
      createGateContext()
    );

    // Then
    assertEquals(Integer.MAX_VALUE, secureFile.remainingAttempts());
  }

  @Test
  void shouldHandleFarFutureExpiryDate() {
    // Given
    Instant farFuture = Instant.now().plus(365 * 100, ChronoUnit.DAYS);

    // When
    SecureFile<byte[], Object> secureFile = createValidSecureFile(farFuture);

    // Then
    assertFalse(secureFile.isExpired(Instant.now()));
    assertEquals(farFuture, secureFile.getExpiryDate());
  }

  @Test
  void shouldHandlePastCreatedAt() {
    // Given
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);
    Instant pastCreatedAt = Instant.now().minus(30, ChronoUnit.DAYS);

    // When
    SecureFile<byte[], Object> secureFile = new SecureFile<byte[], Object>(
      createFileId(),
      createLinkId(),
      createBlobPath(),
      createSealedEnvelope(),
      createSealedSalt(),
      createFileSpecs(expiryDate),
      5,
      pastCreatedAt,
      createGateHash(),
      createGateContext()
    );

    // Then
    assertEquals(pastCreatedAt, secureFile.createdAt());
  }

  // ==================== RECORD FUNCTIONALITY TESTS ====================

  @Test
  void shouldHaveCorrectEqualsForSameFieldValues() {
    // Given
    FileId fileId = new FileId("same-file-id");
    LinkId linkId = new LinkId("same-link-id");
    PathReference blobPath = PathReference.from("/tmp/same/path.bin");
    SealedBlob sealedEnvelope = createSealedEnvelope();
    SealedBlob sealedSalt = createSealedSalt();
    byte[] gateBox = createGateHash();
    Instant expiryDate = Instant.parse("2025-06-01T12:00:00Z");
    FileSpecs specs = new FileSpecs(expiryDate, 3);
    Instant createdAt = Instant.parse("2025-01-01T00:00:00Z");

    // When
    SecureFile<byte[], Object> file1 = new SecureFile<byte[], Object>(
      fileId,
      linkId,
      blobPath,
      sealedEnvelope,
      sealedSalt,
      specs,
      5,
      createdAt,
      gateBox,
      null
    );

    // Then - Check individual field equality
    assertEquals(fileId, file1.fileId());
    assertEquals(linkId, file1.linkId());
    assertEquals(blobPath, file1.blobPath());
    assertEquals(specs, file1.specs());
    assertEquals(createdAt, file1.createdAt());
  }

  @Test
  void shouldAccessAllRecordComponents() {
    // Given
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);
    SecureFile<byte[], Object> secureFile = createValidSecureFile(expiryDate);

    // Then - All component accessors should work
    assertNotNull(secureFile.fileId());
    assertNotNull(secureFile.linkId());
    assertNotNull(secureFile.blobPath());
    assertNotNull(secureFile.sealedEnvelope());
    assertNotNull(secureFile.sealedSalt());
    assertNotNull(secureFile.gateBox());
    assertNotNull(secureFile.specs());
    assertNotNull(secureFile.createdAt());
  }
}
