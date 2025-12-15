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
package com.voltzug.cinder.spring.rest.controller;

import com.voltzug.cinder.core.common.valueobject.Blob;
import com.voltzug.cinder.core.domain.valueobject.id.LinkId;
import com.voltzug.cinder.core.domain.valueobject.id.SessionId;
import com.voltzug.cinder.spring.infra.logging.InfraLogger;
import com.voltzug.cinder.spring.infra.mode.quiz.entity.AccessHash;
import com.voltzug.cinder.spring.infra.mode.quiz.entity.GateHash;
import com.voltzug.cinder.spring.infra.mode.quiz.entity.QuizQuestions;
import com.voltzug.cinder.spring.infra.mode.quiz.usecase.AlphaQuizDownloadUseCase;
import com.voltzug.cinder.spring.infra.mode.quiz.usecase.AlphaQuizUploadUseCase;
import com.voltzug.cinder.spring.infra.mode.quiz.usecase.model.AlphaUploadInput;
import com.voltzug.cinder.spring.infra.mode.quiz.usecase.model.DownloadInitResult;
import com.voltzug.cinder.spring.infra.mode.quiz.usecase.model.DownloadResult;
import com.voltzug.cinder.spring.rest.dto.alpha.AlphaDownloadInitResponse;
import com.voltzug.cinder.spring.rest.dto.alpha.AlphaDownloadRequest;
import com.voltzug.cinder.spring.rest.dto.alpha.AlphaUploadResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Alpha version REST controller for quiz-based file sharing.
 *
 * <p>This controller provides simplified endpoints for the quiz mode that bypass
 * HMAC and full session handshake verification for alpha testing purposes.
 *
 * <h2>Endpoints</h2>
 * <ul>
 *   <li><strong>POST /api/alpha/upload</strong> — Upload encrypted file with quiz metadata</li>
 *   <li><strong>GET /api/alpha/download/{linkId}/init</strong> — Initialize download session</li>
 *   <li><strong>POST /api/alpha/download/{linkId}</strong> — Verify access and retrieve file</li>
 * </ul>
 *
 * <h2>Security Notes (Alpha)</h2>
 * <ul>
 *   <li>No HMAC verification on upload or download</li>
 *   <li>No session secret exchange</li>
 *   <li>No timestamp validation</li>
 * </ul>
 *
 * @see AlphaQuizUploadUseCase
 * @see AlphaQuizDownloadUseCase
 */
@RestController
@RequestMapping("/api/alpha")
public class AlphaQuizController {

  private static final InfraLogger LOG = InfraLogger.of(AlphaQuizController.class);

  private static final String HEADER_ENVELOPE = "X-Cinder-Envelope";
  private static final String HEADER_SALT = "X-Cinder-Salt";

  private final AlphaQuizUploadUseCase _uploadUseCase;
  private final AlphaQuizDownloadUseCase _downloadUseCase;

  /**
   * Constructs the AlphaQuizController with required use cases.
   *
   * @param uploadUseCase   the upload use case
   * @param downloadUseCase the download use case
   */
  public AlphaQuizController(
    AlphaQuizUploadUseCase uploadUseCase,
    AlphaQuizDownloadUseCase downloadUseCase
  ) {
    this._uploadUseCase = uploadUseCase;
    this._downloadUseCase = downloadUseCase;
  }

  /**
   * Uploads an encrypted file with quiz metadata.
   *
   * <p><strong>Request:</strong> {@code POST /api/alpha/upload}
   * <br><strong>Content-Type:</strong> {@code multipart/form-data}
   *
   * <h3>Form Fields</h3>
   * <ul>
   *   <li>{@code blob} — The encrypted file content (MultipartFile)</li>
   *   <li>{@code envelope} — Base64-encoded encrypted envelope (fK||fNonce sealed with quizK)</li>
   *   <li>{@code salt} — Base64-encoded argon2 salt</li>
   *   <li>{@code gateHash} — Base64-encoded SHA256 of (answers||nonce)</li>
   *   <li>{@code encryptedQuestions} — Base64-encoded encrypted quiz questions</li>
   *   <li>{@code expiryDate} — ISO-8601 timestamp for file expiry</li>
   *   <li>{@code retryCount} — Maximum download attempts (1-99)</li>
   * </ul>
   *
   * @param blob               the encrypted file content
   * @param envelope           Base64-encoded encrypted envelope
   * @param salt               Base64-encoded argon2 salt
   * @param gateHash           Base64-encoded gate hash
   * @param encryptedQuestions Base64-encoded encrypted quiz questions
   * @param expiryDate         file expiry timestamp
   * @param retryCount         maximum download attempts
   * @return response containing the generated link ID
   * @throws IOException if reading the multipart file fails
   */
  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<AlphaUploadResponse> upload(
    @RequestParam("blob") MultipartFile blob,
    @RequestParam("envelope") String envelope,
    @RequestParam("salt") String salt,
    @RequestParam("gateHash") String gateHash,
    @RequestParam("encryptedQuestions") String encryptedQuestions,
    @RequestParam("expiryDate") Instant expiryDate,
    @RequestParam("retryCount") int retryCount
  ) throws IOException {
    LOG.info(
      "Alpha upload request: blobSize={}, expiryDate={}, retryCount={}",
      blob.getSize(),
      expiryDate,
      retryCount
    );

    byte[] envelopeBytes = Base64.getDecoder().decode(envelope);
    byte[] saltBytes = Base64.getDecoder().decode(salt);
    byte[] gateHashBytes = Base64.getDecoder().decode(gateHash);
    byte[] questionsBytes = Base64.getDecoder().decode(encryptedQuestions);

    AlphaUploadInput input = new AlphaUploadInput(
      new Blob(blob.getBytes()),
      envelopeBytes,
      saltBytes,
      new GateHash(gateHashBytes),
      new QuizQuestions(questionsBytes),
      expiryDate,
      retryCount
    );

    var result = _uploadUseCase.upload(input);

    LOG.info("Alpha upload successful: linkId={}", result.linkId().toString());

    return ResponseEntity.ok(new AlphaUploadResponse(
      result.linkId().toString(),
      expiryDate
    ));
  }

  /**
   * Initializes a download session for the given link.
   *
   * <p><strong>Request:</strong> {@code GET /api/alpha/download/{linkId}/init}
   *
   * <p>Returns a session ID and the encrypted quiz questions. The client decrypts
   * the questions using the questionK from the URL fragment, answers them, then
   * computes the access hash to verify the download.
   *
   * @param linkId the link identifier (without LK prefix)
   * @return response containing session ID and encrypted questions
   */
  @GetMapping("/download/{linkId}/init")
  public ResponseEntity<AlphaDownloadInitResponse> initDownload(
    @PathVariable String linkId
  ) {
    LOG.info("Alpha download init request: linkId={}", linkId);

    LinkId link = new LinkId(linkId);
    DownloadInitResult result = _downloadUseCase.initSession(link);

    String encodedQuestions = Base64.getEncoder().encodeToString(
      result.encryptedQuestions().getBytes()
    );

    LOG.info(
      "Alpha download init successful: sessionId={}, linkId={}",
      result.sessionId().toString(),
      linkId
    );

    return ResponseEntity.ok(new AlphaDownloadInitResponse(
      result.sessionId().toString(),
      encodedQuestions
    ));
  }

  /**
   * Verifies the access hash and returns the encrypted file.
   *
   * <p><strong>Request:</strong> {@code POST /api/alpha/download/{linkId}}
   * <br><strong>Content-Type:</strong> {@code application/json}
   *
   * <h3>Request Body</h3>
   * <pre>{@code
   * {
   *   "sessionId": "SN<uuid>",
   *   "accessHash": "<base64-encoded SHA256 of answers||nonce>"
   * }
   * }</pre>
   *
   * <h3>Response</h3>
   * <p><strong>Content-Type:</strong> {@code application/octet-stream}
   * <br>Response body contains the encrypted blob bytes.
   * <br>Headers include Base64-encoded envelope and salt:
   * <ul>
   *   <li>{@code X-Cinder-Envelope} — Base64-encoded unsealed envelope</li>
   *   <li>{@code X-Cinder-Salt} — Base64-encoded unsealed salt</li>
   * </ul>
   *
   * @param linkId  the link identifier (without LK prefix)
   * @param request the download request containing session ID and access hash
   * @return the encrypted file blob with envelope and salt in headers
   */
  @PostMapping(
    value = "/download/{linkId}",
    consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<byte[]> download(
    @PathVariable String linkId,
    @RequestBody AlphaDownloadRequest request
  ) {
    LOG.info(
      "Alpha download request: linkId={}, sessionId={}",
      linkId,
      request.sessionId()
    );

    String sessionIdRaw = request.sessionId();
    if (sessionIdRaw.startsWith("SN")) {
      sessionIdRaw = sessionIdRaw.substring(2);
    }
    SessionId sessionId = new SessionId(sessionIdRaw);

    byte[] accessHashBytes = Base64.getDecoder().decode(request.accessHash());
    AccessHash accessHash = new AccessHash(accessHashBytes);

    DownloadResult result = _downloadUseCase.verifyAndDownload(sessionId, accessHash);

    String encodedEnvelope = Base64.getEncoder().encodeToString(result.envelope());
    String encodedSalt = Base64.getEncoder().encodeToString(result.salt());

    LOG.info("Alpha download successful: linkId={}", linkId);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    headers.set(HEADER_ENVELOPE, encodedEnvelope);
    headers.set(HEADER_SALT, encodedSalt);
    headers.setContentLength(result.blob().size());

    return ResponseEntity.ok()
      .headers(headers)
      .body(result.blob().getBytes());
  }
}