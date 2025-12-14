package com.voltzug.cinder.spring.infra.filestore;

import com.voltzug.cinder.core.common.valueobject.Blob;
import com.voltzug.cinder.core.domain.valueobject.PathReference;
import com.voltzug.cinder.core.exception.FileStorageException;
import com.voltzug.cinder.core.port.out.FileStorePort;
import com.voltzug.cinder.spring.infra.config.CinderProperties;
import com.voltzug.cinder.spring.infra.logging.InfraLogger;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.stereotype.Component;

/**
 * Infrastructure adapter implementing file storage operations on the local filesystem.
 *
 * <p>This adapter provides:
 * <ul>
 *   <li><strong>Store:</strong> Writes encrypted blobs to unique UUID-named files</li>
 *   <li><strong>Retrieve:</strong> Reads blobs from the filesystem by path reference</li>
 *   <li><strong>Delete:</strong> Removes files from the filesystem</li>
 *   <li><strong>Exists:</strong> File metadata operations</li>
 * </ul>
 *
 * <p><strong>Configuration:</strong> The base storage directory is configured via
 * {@code cinder.storage.local.directory} in application.properties.
 *
 * <p><strong>Security Notes:</strong>
 * <ul>
 *   <li>Only encrypted blobs are stored; the adapter has no knowledge of file contents</li>
 *   <li>File names are UUIDs, preventing any correlation with original filenames</li>
 *   <li>Directory is created on startup if it doesn't exist</li>
 * </ul>
 *
 * @see FileStorePort
 * @see CinderProperties
 */
@Component
public class LocalFileStoreAdapter implements FileStorePort {

  /** File extension for stored encrypted blobs. */
  private static final String BLOB_EXTENSION = ".cinder";
  private static final InfraLogger LOG = InfraLogger.of(
    LocalFileStoreAdapter.class
  );

  private final Path _storageDirectory;

  /**
   * Constructs the LocalFileStoreAdapter with configuration from CinderProperties.
   *
   * @param properties the Cinder configuration properties
   */
  public LocalFileStoreAdapter(CinderProperties properties) {
    String directory = properties.getStorage().getLocal().getDirectory();
    _storageDirectory = Paths.get(directory).toAbsolutePath().normalize();
  }

  /**
   * Initializes the storage directory on application startup.
   * Creates the directory and any necessary parent directories if they don't exist.
   *
   * @throws FileStorageException if directory creation fails
   */
  @PostConstruct
  public void init() throws FileStorageException {
    try {
      if (!Files.exists(_storageDirectory)) {
        Files.createDirectories(_storageDirectory);
        LOG.info("Created storage directory: {}", _storageDirectory.toString());
      } else {
        LOG.info(
          "Using existing storage directory: {}",
          _storageDirectory.toString()
        );
      }
    } catch (IOException e) {
      LOG.error(
        "Failed to create storage directory: {}",
        _storageDirectory.toString(),
        e
      );
      throw new FileStorageException(
        "Failed to create storage directory: " + _storageDirectory,
        e
      );
    }
  }

  @Override
  public boolean exists(PathReference path) {
    Path filePath = _resolvePath(path);
    boolean exists = Files.exists(filePath) && Files.isRegularFile(filePath);

    if (LOG.isTraceEnabled()) {
      LOG.trace("Existence check: path={}, exists={}", path.value(), exists);
    }

    return exists;
  }

  /**
   * Stores an encrypted blob in the local filesystem.
   * Generates a unique UUID-based filename for the stored file.
   *
   * @param fileId the encrypted blob to store
   * @param blob the encrypted blob to store
   * @return a PathReference to the stored blob
   * @throws FileStorageException if the write operation fails
   */
  @Override
  public PathReference save(@NonNull Blob blob) throws FileStorageException {
    String filename = UUID.randomUUID().toString() + BLOB_EXTENSION;
    Path filePath = _storageDirectory.resolve(filename);

    try {
      Files.write(
        filePath,
        blob.getBytes(),
        StandardOpenOption.CREATE_NEW,
        StandardOpenOption.WRITE
      );

      LOG.info(
        "Stored encrypted blob: filename={}, size={} bytes",
        filename,
        blob.size()
      );
      LOG.debug("Blob storage path: {}", filePath.toString());

      return PathReference.from(filePath.toString());
    } catch (IOException e) {
      LOG.error(
        "Failed to store blob: filename={}, size={} bytes, error={}",
        filename,
        blob.getBytes(),
        e.getMessage()
      );
      throw new FileStorageException("Failed to store blob at: " + filePath, e);
    }
  }

  /**
   * Retrieves an encrypted blob from the local filesystem.
   *
   * @param path the path reference to the blob
   * @return the encrypted blob
   * @throws FileStorageException if the read operation fails or file doesn't exist
   */
  @Override
  public Blob load(PathReference path) throws FileStorageException {
    Path filePath = _resolvePath(path);

    if (!Files.exists(filePath)) {
      LOG.warn("Retrieve attempt for non-existent file: {}", path.value());
      throw new FileStorageException("File not found: " + path.value());
    }

    try {
      byte[] data = Files.readAllBytes(filePath);
      LOG.info(
        "Retrieved encrypted blob: path={}, size={} bytes",
        path.value(),
        data.length
      );
      LOG.debug("Blob retrieved from: {}", filePath.toString());
      return new Blob(data);
    } catch (IOException e) {
      LOG.error(
        "Failed to retrieve blob: path={}, error={}",
        path.value(),
        e.getMessage()
      );
      throw new FileStorageException(
        "Failed to retrieve blob from: " + path.value(),
        e
      );
    }
  }

  /**
   * Deletes an encrypted blob from the local filesystem.
   *
   * @param path the path reference to the blob to delete
   * @throws FileStorageException if the delete operation fails
   */
  @Override
  public void delete(PathReference path) throws FileStorageException {
    Path filePath = _resolvePath(path);

    try {
      boolean deleted = Files.deleteIfExists(filePath);
      if (deleted) {
        LOG.info("Deleted encrypted blob: path={}", path.value());
        LOG.debug("Blob deleted from: {}", filePath.toString());
      } else {
        LOG.debug("Delete called for non-existent file: {}", path.value());
      }
    } catch (IOException e) {
      LOG.error(
        "Failed to delete blob: path={}, error={}",
        path.value(),
        e.getMessage()
      );
      throw new FileStorageException(
        "Failed to delete blob at: " + path.value(),
        e
      );
    }
  }

  /**
   * Resolves a PathReference to an absolute filesystem Path.
   * Handles both absolute paths and relative paths (relative to storage directory).
   *
   * @param pathRef the path reference to resolve
   * @return the resolved absolute Path
   */
  private Path _resolvePath(PathReference pathRef) {
    Path path = Paths.get(pathRef.value()).normalize();
    Path absolutePath = _storageDirectory.resolve(path).normalize();

    if (!absolutePath.startsWith(_storageDirectory)) {
      LOG.warn(
        "SECURITY: Path traversal attempt detected! Requested path: {}, Resolved path: {}, Storage dir: {}",
        pathRef.value(),
        absolutePath.toString(),
        _storageDirectory.toString()
      );
      throw new FileStorageException(
        "Path traversal detected: " + pathRef.value()
      );
    }

    // Detect suspicious patterns that might indicate abuse
    String pathValue = pathRef.value();
    if (pathValue.contains("..") || pathValue.contains("~")) {
      LOG.warn("SECURITY: Suspicious path pattern detected: {}", pathValue);
    }

    // Check for unusual file extensions or patterns
    if (
      !pathValue.endsWith(BLOB_EXTENSION) &&
      !pathValue.equals(path.getFileName().toString())
    ) {
      LOG.debug("Non-standard file reference detected: {}", pathValue);
    }

    return absolutePath;
  }
}
