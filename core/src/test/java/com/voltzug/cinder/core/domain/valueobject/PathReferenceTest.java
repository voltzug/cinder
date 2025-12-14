package com.voltzug.cinder.core.domain.valueobject;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Comprehensive tests for PathReference value object.
 * Tests file storage path reference creation, validation, and type detection.
 */
class PathReferenceTest {

  // ==================== CONSTRUCTION TESTS ====================

  @Test
  void shouldCreatePathReferenceFromLocalAbsolutePath() {
    // Given
    String path = "/tmp/cinder/files/blob.bin";

    // When
    PathReference ref = new PathReference(path);

    // Then
    assertNotNull(ref);
    assertEquals(path, ref.value());
  }

  @Test
  void shouldCreatePathReferenceFromS3Uri() {
    // Given
    String s3Path = "s3://my-bucket/files/blob.bin";

    // When
    PathReference ref = new PathReference(s3Path);

    // Then
    assertNotNull(ref);
    assertEquals(s3Path, ref.value());
  }

  @Test
  void shouldCreatePathReferenceUsingFromFactory() {
    // Given
    String path = "/var/data/file.dat";

    // When
    PathReference ref = PathReference.from(path);

    // Then
    assertNotNull(ref);
    assertEquals(path, ref.value());
  }

  @Test
  void shouldCreatePathReferenceUsingForLocalFileFactory() {
    // Given
    String directory = "/tmp/cinder";
    String filename = "blob.bin";

    // When
    PathReference ref = PathReference.forLocalFile(directory, filename);

    // Then
    assertNotNull(ref);
    assertTrue(ref.value().contains(directory));
    assertTrue(ref.value().contains(filename));
  }

  @Test
  void shouldTrimWhitespaceFromPath() {
    // Given
    String pathWithWhitespace = "  /tmp/test/file.bin  ";

    // When
    PathReference ref = new PathReference(pathWithWhitespace);

    // Then
    assertEquals("/tmp/test/file.bin", ref.value());
  }

  // ==================== VALIDATION TESTS ====================

  @Test
  void shouldThrowExceptionForNullPath() {
    // When/Then
    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> new PathReference(null)
    );
    assertEquals(
      "PathReference value must not be null or blank",
      exception.getMessage()
    );
  }

  @Test
  void shouldThrowExceptionForEmptyPath() {
    // When/Then
    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> new PathReference("")
    );
    assertEquals(
      "PathReference value must not be null or blank",
      exception.getMessage()
    );
  }

  @Test
  void shouldThrowExceptionForBlankPath() {
    // When/Then
    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> new PathReference("   ")
    );
    assertEquals(
      "PathReference value must not be null or blank",
      exception.getMessage()
    );
  }

  @Test
  void shouldThrowExceptionForRelativePath() {
    // When/Then
    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> new PathReference("relative/path/file.bin")
    );
    assertEquals("PathReference value is invalid", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionForInvalidS3Uri() {
    // When/Then
    assertThrows(IllegalArgumentException.class, () ->
      new PathReference("s3://")
    );
  }

  @Test
  void shouldThrowExceptionForNullPathInFromFactory() {
    // When/Then
    assertThrows(NullPointerException.class, () -> PathReference.from(null));
  }

  @Test
  void shouldThrowExceptionForNullDirectoryInForLocalFile() {
    // When/Then
    assertThrows(NullPointerException.class, () ->
      PathReference.forLocalFile(null, "file.bin")
    );
  }

  @Test
  void shouldThrowExceptionForNullFilenameInForLocalFile() {
    // When/Then
    assertThrows(NullPointerException.class, () ->
      PathReference.forLocalFile("/tmp", null)
    );
  }

  // ==================== LOCAL PATH DETECTION TESTS ====================

  @Test
  void shouldReturnTrueForLocalAbsolutePath() {
    // Given
    PathReference ref = new PathReference("/tmp/test/file.bin");

    // When
    boolean isLocal = ref.isLocal();

    // Then
    assertTrue(isLocal);
  }

  @Test
  void shouldReturnFalseForS3Path() {
    // Given
    PathReference ref = new PathReference("s3://bucket/file.bin");

    // When
    boolean isLocal = ref.isLocal();

    // Then
    assertFalse(isLocal);
  }

  @Test
  void shouldDetectLinuxAbsolutePath() {
    // Given
    PathReference ref = new PathReference("/home/user/data/file.dat");

    // When
    boolean isLocal = ref.isLocal();

    // Then
    assertTrue(isLocal);
  }

  @Test
  void shouldDetectRootPath() {
    // Given
    PathReference ref = new PathReference("/file.bin");

    // When
    boolean isLocal = ref.isLocal();

    // Then
    assertTrue(isLocal);
  }

  // ==================== CLOUD PATH DETECTION TESTS ====================

  @Test
  void shouldReturnTrueForS3Uri() {
    // Given
    PathReference ref = new PathReference("s3://my-bucket/path/to/file.bin");

    // When
    boolean isCloud = ref.isCloud();

    // Then
    assertTrue(isCloud);
  }

  @Test
  void shouldReturnFalseForLocalPathOnIsCloud() {
    // Given
    PathReference ref = new PathReference("/tmp/file.bin");

    // When
    boolean isCloud = ref.isCloud();

    // Then
    assertFalse(isCloud);
  }

  @Test
  void shouldDetectS3UriWithDots() {
    // Given
    PathReference ref = new PathReference("s3://my.bucket.name/files/data.bin");

    // When
    boolean isCloud = ref.isCloud();

    // Then
    assertTrue(isCloud);
  }

  @Test
  void shouldDetectS3UriWithHyphens() {
    // Given
    PathReference ref = new PathReference("s3://my-bucket-name/files/data.bin");

    // When
    boolean isCloud = ref.isCloud();

    // Then
    assertTrue(isCloud);
  }

  @Test
  void shouldDetectS3UriWithUnderscores() {
    // Given
    PathReference ref = new PathReference("s3://my_bucket_name/files/data.bin");

    // When
    boolean isCloud = ref.isCloud();

    // Then
    assertTrue(isCloud);
  }

  @Test
  void shouldDetectS3UriWithNestedPath() {
    // Given
    PathReference ref = new PathReference(
      "s3://bucket/level1/level2/level3/file.bin"
    );

    // When
    boolean isCloud = ref.isCloud();

    // Then
    assertTrue(isCloud);
  }

  // ==================== MUTUAL EXCLUSIVITY TESTS ====================

  @Test
  void shouldNotBeLocalAndCloudSimultaneously() {
    // Given
    PathReference localRef = new PathReference("/tmp/file.bin");
    PathReference cloudRef = new PathReference("s3://bucket/file.bin");

    // Then
    assertTrue(localRef.isLocal() ^ localRef.isCloud());
    assertTrue(cloudRef.isLocal() ^ cloudRef.isCloud());
  }

  // ==================== EDGE CASE TESTS ====================

  @Test
  void shouldHandleLongPath() {
    // Given
    StringBuilder longPath = new StringBuilder("/");
    for (int i = 0; i < 50; i++) {
      longPath.append("directory").append(i).append("/");
    }
    longPath.append("file.bin");

    // When
    PathReference ref = new PathReference(longPath.toString());

    // Then
    assertTrue(ref.isLocal());
    assertEquals(longPath.toString(), ref.value());
  }

  @Test
  void shouldHandlePathWithSpecialCharacters() {
    // Given
    String path = "/tmp/files-with_special.chars/data.bin";

    // When
    PathReference ref = new PathReference(path);

    // Then
    assertTrue(ref.isLocal());
    assertEquals(path, ref.value());
  }

  @Test
  void shouldHandlePathWithSpacesInName() {
    // Given
    String path = "/tmp/my files/data file.bin";

    // When
    PathReference ref = new PathReference(path);

    // Then
    assertTrue(ref.isLocal());
    assertEquals(path, ref.value());
  }

  @Test
  void shouldHandlePathWithUnicodeCharacters() {
    // Given
    String path = "/tmp/données/fichier.bin";

    // When
    PathReference ref = new PathReference(path);

    // Then
    assertTrue(ref.isLocal());
    assertEquals(path, ref.value());
  }

  @Test
  void shouldHandleMinimalValidLocalPath() {
    // Given
    String path = "/a";

    // When
    PathReference ref = new PathReference(path);

    // Then
    assertTrue(ref.isLocal());
    assertEquals(path, ref.value());
  }

  @Test
  void shouldHandleMinimalValidS3Uri() {
    // Given
    String path = "s3://b/f";

    // When
    PathReference ref = new PathReference(path);

    // Then
    assertTrue(ref.isCloud());
    assertEquals(path, ref.value());
  }

  // ==================== RECORD FUNCTIONALITY TESTS ====================

  @Test
  void shouldHaveCorrectEqualsForSameValues() {
    // Given
    String path = "/tmp/test/file.bin";

    // When
    PathReference ref1 = new PathReference(path);
    PathReference ref2 = new PathReference(path);

    // Then
    assertEquals(ref1, ref2);
  }

  @Test
  void shouldHaveCorrectHashCodeForSameValues() {
    // Given
    String path = "/tmp/test/file.bin";

    // When
    PathReference ref1 = new PathReference(path);
    PathReference ref2 = new PathReference(path);

    // Then
    assertEquals(ref1.hashCode(), ref2.hashCode());
  }

  @Test
  void shouldNotBeEqualForDifferentPaths() {
    // Given
    PathReference ref1 = new PathReference("/tmp/file1.bin");
    PathReference ref2 = new PathReference("/tmp/file2.bin");

    // Then
    assertNotEquals(ref1, ref2);
  }

  @Test
  void shouldHaveCorrectToString() {
    // Given
    String path = "/tmp/test/file.bin";
    PathReference ref = new PathReference(path);

    // When
    String result = ref.toString();

    // Then
    assertTrue(result.contains("PathReference"));
    assertTrue(result.contains(path));
  }

  @Test
  void shouldHandleS3PathEquality() {
    // Given
    String s3Path = "s3://bucket/files/data.bin";

    // When
    PathReference ref1 = new PathReference(s3Path);
    PathReference ref2 = new PathReference(s3Path);

    // Then
    assertEquals(ref1, ref2);
    assertEquals(ref1.hashCode(), ref2.hashCode());
  }

  @Test
  void shouldNotBeEqualForLocalVsCloud() {
    // Given
    PathReference localRef = new PathReference("/bucket/files/data.bin");
    PathReference cloudRef = new PathReference("s3://bucket/files/data.bin");

    // Then
    assertNotEquals(localRef, cloudRef);
  }
}
