package com.voltzug.cinder.core.domain.valueobject;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value object representing a file storage path reference.
 * This could be a local file path, S3 URI, or other storage location identifier.
 */
public record PathReference(String value) {
  private static final Pattern S3_PATTERN = Pattern.compile(
    "^s3://[\\w\\-\\.]+/.+"
  );

  public PathReference {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(
        "PathReference value must not be null or blank"
      );
    }
    value = value.trim();
    if (!_isValidPath(value)) {
      throw new IllegalArgumentException("PathReference value is invalid");
    }
  }

  /**
   * Creates a PathReference from a string path.
   *
   * @param path the storage path
   * @return a PathReference instance
   */
  public static PathReference from(String path) {
    Objects.requireNonNull(path, "Path must not be null");
    return new PathReference(path);
  }

  /**
   * Creates a PathReference for local file storage.
   *
   * @param directory the base directory
   * @param filename the file name
   * @return a PathReference instance
   */
  public static PathReference forLocalFile(String directory, String filename) {
    Objects.requireNonNull(directory, "Directory must not be null");
    Objects.requireNonNull(filename, "Filename must not be null");
    Path path = Paths.get(directory, filename);
    return new PathReference(path.toString());
  }

  /**
   * Checks if this is a local file system path.
   *
   * @return true if local file path, false otherwise
   */
  public boolean isLocal() {
    boolean result;
    try {
      result = Paths.get(value).isAbsolute();
    } catch (InvalidPathException exc) {
      result = false;
    }
    return result;
  }

  /**
   * Checks if this is a cloud storage URI (e.g., S3, GCS).
   *
   * @return true if cloud URI, false otherwise
   */
  public boolean isCloud() {
    return S3_PATTERN.matcher(value).matches();
  }

  private static boolean _isValidPath(String path) {
    return _isCloudPath(path) || _isLocalPath(path);
  }

  private static boolean _isCloudPath(String path) {
    return S3_PATTERN.matcher(path).matches();
  }

  private static boolean _isLocalPath(String path) {
    try {
      return Paths.get(path).isAbsolute();
    } catch (InvalidPathException exc) {
      return false;
    }
  }
}
