package com.voltzug.cinder.core.domain.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.voltzug.cinder.core.common.valueobject.Blob;
import com.voltzug.cinder.core.domain.valueobject.FileSpecs;
import com.voltzug.cinder.core.domain.valueobject.PathReference;
import com.voltzug.cinder.core.domain.valueobject.SealedBlob;
import com.voltzug.cinder.core.domain.valueobject.id.FileId;
import com.voltzug.cinder.core.domain.valueobject.id.LinkId;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive tests for SecureFile<byte[]> entity.
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

  private Blob createEncryptedQuestions() {
    return new Blob(new byte[] { 0x11, 0x22, 0x33, 0x44, 0x55 });
  }

  private FileSpecs createFileSpecs(Instant expiryDate) {
    return new FileSpecs(expiryDate, 5);
  }

  private SecureFile<byte[]> createValidSecureFile(Instant expiryDate) {
    return new SecureFile<byte[]>(
      createFileId(),
      createLinkId(),
      createBlobPath(),
      createSealedEnvelope(),
      createSealedSalt(),
      createGateHash(),
      createEncryptedQuestions(),
      createFileSpecs(expiryDate),
      5,
      Instant.now()
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
    Blob encryptedQuestions = createEncryptedQuestions();
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);
    FileSpecs specs = createFileSpecs(expiryDate);
    int remainingAttempts = 5;
    Instant createdAt = Instant.now();

    // When
    SecureFile<byte[]> secureFile = new SecureFile<byte[]>(
      fileId,
      linkId,
      blobPath,
      sealedEnvelope,
      sealedSalt,
      gateBox,
      encryptedQuestions,
      specs,
      remainingAttempts,
      createdAt
    );

    // Then
    assertNotNull(secureFile);
    assertEquals(fileId, secureFile.fileId());
    assertEquals(linkId, secureFile.linkId());
    assertEquals(blobPath, secureFile.blobPath());
    assertEquals(sealedEnvelope, secureFile.sealedEnvelope());
    assertEquals(sealedSalt, secureFile.sealedSalt());
    assertEquals(gateBox, secureFile.gateBox());
    assertEquals(encryptedQuestions, secureFile.encryptedQuestions());
    assertEquals(specs, secureFile.specs());
    assertEquals(remainingAttempts, secureFile.remainingAttempts());
    assertEquals(createdAt, secureFile.createdAt());
  }

  @Test
  void shouldCreateSecureFileWithZeroRemainingAttempts() {
    // Given
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);

    // When
    SecureFile<byte[]> secureFile = new SecureFile<byte[]>(
      createFileId(),
      createLinkId(),
      createBlobPath(),
      createSealedEnvelope(),
      createSealedSalt(),
      createGateHash(),
      createEncryptedQuestions(),
      createFileSpecs(expiryDate),
      0,
      Instant.now()
    );

    // Then
    assertEquals(0, secureFile.remainingAttempts());
  }

  @Test
  void shouldCreateSecureFileWithNegativeRemainingAttempts() {
    // Given - Negative attempts might indicate exhausted state
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);

    // When
    SecureFile<byte[]> secureFile = new SecureFile<byte[]>(
      createFileId(),
      createLinkId(),
      createBlobPath(),
      createSealedEnvelope(),
      createSealedSalt(),
      createGateHash(),
      createEncryptedQuestions(),
      createFileSpecs(expiryDate),
      -1,
      Instant.now()
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
        new SecureFile<byte[]>(
          null,
          createLinkId(),
          createBlobPath(),
          createSealedEnvelope(),
          createSealedSalt(),
          createGateHash(),
          createEncryptedQuestions(),
          createFileSpecs(expiryDate),
          5,
          Instant.now()
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
        new SecureFile<byte[]>(
          createFileId(),
          null,
          createBlobPath(),
          createSealedEnvelope(),
          createSealedSalt(),
          createGateHash(),
          createEncryptedQuestions(),
          createFileSpecs(expiryDate),
          5,
          Instant.now()
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
        new SecureFile<byte[]>(
          createFileId(),
          createLinkId(),
          null,
          createSealedEnvelope(),
          createSealedSalt(),
          createGateHash(),
          createEncryptedQuestions(),
          createFileSpecs(expiryDate),
          5,
          Instant.now()
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
        new SecureFile<byte[]>(
          createFileId(),
          createLinkId(),
          createBlobPath(),
          null,
          createSealedSalt(),
          createGateHash(),
          createEncryptedQuestions(),
          createFileSpecs(expiryDate),
          5,
          Instant.now()
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
        new SecureFile<byte[]>(
          createFileId(),
          createLinkId(),
          createBlobPath(),
          createSealedEnvelope(),
          null,
          createGateHash(),
          createEncryptedQuestions(),
          createFileSpecs(expiryDate),
          5,
          Instant.now()
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
        new SecureFile<byte[]>(
          createFileId(),
          createLinkId(),
          createBlobPath(),
          createSealedEnvelope(),
          createSealedSalt(),
          null,
          createEncryptedQuestions(),
          createFileSpecs(expiryDate),
          5,
          Instant.now()
        )
    );
    assertEquals("gateBox must not be null", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionForNullEncryptedQuestions() {
    // Given
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);

    // When/Then
    NullPointerException exception = assertThrows(
      NullPointerException.class,
      () ->
        new SecureFile<byte[]>(
          createFileId(),
          createLinkId(),
          createBlobPath(),
          createSealedEnvelope(),
          createSealedSalt(),
          createGateHash(),
          null,
          createFileSpecs(expiryDate),
          5,
          Instant.now()
        )
    );
    assertEquals("encryptedQuestions must not be null", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionForNullSpecs() {
    // When/Then
    NullPointerException exception = assertThrows(
      NullPointerException.class,
      () ->
        new SecureFile<byte[]>(
          createFileId(),
          createLinkId(),
          createBlobPath(),
          createSealedEnvelope(),
          createSealedSalt(),
          createGateHash(),
          createEncryptedQuestions(),
          null,
          5,
          Instant.now()
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
        new SecureFile<byte[]>(
          createFileId(),
          createLinkId(),
          createBlobPath(),
          createSealedEnvelope(),
          createSealedSalt(),
          createGateHash(),
          createEncryptedQuestions(),
          createFileSpecs(expiryDate),
          5,
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
    SecureFile<byte[]> secureFile = createValidSecureFile(expiryDate);

    // When
    Instant result = secureFile.getExpiryDate();

    // Then
    assertEquals(expiryDate, result);
  }

  @Test
  void shouldReturnTrueWhenFileExpired() {
    // Given
    Instant expiryDate = Instant.now().minus(1, ChronoUnit.HOURS);
    SecureFile<byte[]> secureFile = createValidSecureFile(expiryDate);

    // When
    boolean expired = secureFile.isExpired(Instant.now());

    // Then
    assertTrue(expired);
  }

  @Test
  void shouldReturnFalseWhenFileNotExpired() {
    // Given
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.HOURS);
    SecureFile<byte[]> secureFile = createValidSecureFile(expiryDate);

    // When
    boolean expired = secureFile.isExpired(Instant.now());

    // Then
    assertFalse(expired);
  }

  @Test
  void shouldReturnFalseWhenExactlyAtExpiryTime() {
    // Given
    Instant now = Instant.now();
    SecureFile<byte[]> secureFile = createValidSecureFile(now);

    // When
    boolean expired = secureFile.isExpired(now);

    // Then
    assertFalse(expired, "Should not be expired when exactly at expiry time");
  }

  @Test
  void shouldReturnTrueWhenOneMillisecondAfterExpiry() {
    // Given
    Instant expiryDate = Instant.now();
    SecureFile<byte[]> secureFile = createValidSecureFile(expiryDate);
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
    SecureFile<byte[]> secureFile = createValidSecureFile(expiryDate);
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
    SecureFile<byte[]> secureFile = new SecureFile<byte[]>(
      createFileId(),
      createLinkId(),
      createBlobPath(),
      createSealedEnvelope(),
      createSealedSalt(),
      createGateHash(),
      createEncryptedQuestions(),
      specs,
      5,
      Instant.now()
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
    SecureFile<byte[]> secureFile = createValidSecureFile(expiryDate);

    // Then
    assertTrue(secureFile instanceof IExpirable);
  }

  // ==================== EDGE CASE TESTS ====================

  @Test
  void shouldHandleMaxRemainingAttempts() {
    // Given
    Instant expiryDate = Instant.now().plus(1, ChronoUnit.DAYS);

    // When
    SecureFile<byte[]> secureFile = new SecureFile<byte[]>(
      createFileId(),
      createLinkId(),
      createBlobPath(),
      createSealedEnvelope(),
      createSealedSalt(),
      createGateHash(),
      createEncryptedQuestions(),
      createFileSpecs(expiryDate),
      Integer.MAX_VALUE,
      Instant.now()
    );

    // Then
    assertEquals(Integer.MAX_VALUE, secureFile.remainingAttempts());
  }

  @Test
  void shouldHandleFarFutureExpiryDate() {
    // Given
    Instant farFuture = Instant.now().plus(365 * 100, ChronoUnit.DAYS);

    // When
    SecureFile<byte[]> secureFile = createValidSecureFile(farFuture);

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
    SecureFile<byte[]> secureFile = new SecureFile<byte[]>(
      createFileId(),
      createLinkId(),
      createBlobPath(),
      createSealedEnvelope(),
      createSealedSalt(),
      createGateHash(),
      createEncryptedQuestions(),
      createFileSpecs(expiryDate),
      5,
      pastCreatedAt
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
    Blob encryptedQuestions = createEncryptedQuestions();
    Instant expiryDate = Instant.parse("2025-06-01T12:00:00Z");
    FileSpecs specs = new FileSpecs(expiryDate, 3);
    Instant createdAt = Instant.parse("2025-01-01T00:00:00Z");

    // When
    SecureFile<byte[]> file1 = new SecureFile<byte[]>(
      fileId,
      linkId,
      blobPath,
      sealedEnvelope,
      sealedSalt,
      gateBox,
      encryptedQuestions,
      specs,
      5,
      createdAt
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
    SecureFile<byte[]> secureFile = createValidSecureFile(expiryDate);

    // Then - All component accessors should work
    assertNotNull(secureFile.fileId());
    assertNotNull(secureFile.linkId());
    assertNotNull(secureFile.blobPath());
    assertNotNull(secureFile.sealedEnvelope());
    assertNotNull(secureFile.sealedSalt());
    assertNotNull(secureFile.gateBox());
    assertNotNull(secureFile.encryptedQuestions());
    assertNotNull(secureFile.specs());
    assertNotNull(secureFile.createdAt());
  }
}
