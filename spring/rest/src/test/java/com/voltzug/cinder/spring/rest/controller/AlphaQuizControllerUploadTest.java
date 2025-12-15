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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.voltzug.cinder.core.domain.valueobject.id.LinkId;
import com.voltzug.cinder.core.model.upload.UploadResult;
import com.voltzug.cinder.spring.infra.mode.quiz.usecase.AlphaQuizDownloadUseCase;
import com.voltzug.cinder.spring.infra.mode.quiz.usecase.AlphaQuizUploadUseCase;
import com.voltzug.cinder.spring.infra.mode.quiz.usecase.model.AlphaUploadInput;
import com.voltzug.cinder.spring.rest.exception.GlobalExceptionHandler;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

/**
 * Unit tests for {@link AlphaQuizController} upload endpoint.
 *
 * <p>Uses standalone MockMvc setup for fast, isolated controller testing
 * without requiring Spring Boot autoconfiguration.
 *
 * <p>Tests the POST /api/alpha/upload endpoint with various scenarios including:
 * <ul>
 *   <li>Successful uploads with valid data</li>
 *   <li>Missing required fields</li>
 *   <li>Invalid Base64 encoding</li>
 *   <li>Invalid retry count values</li>
 *   <li>Invalid expiry dates</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class AlphaQuizControllerUploadTest {

  private static final String UPLOAD_URL = "/api/alpha/upload";

  private MockMvc mockMvc;

  @Mock
  private AlphaQuizUploadUseCase uploadUseCase;

  @Mock
  private AlphaQuizDownloadUseCase downloadUseCase;

  // Test data
  private byte[] testBlobContent;
  private String testEnvelopeBase64;
  private String testSaltBase64;
  private String testGateHashBase64;
  private String testEncryptedQuestionsBase64;
  private Instant testExpiryDate;
  private int testRetryCount;
  private LinkId testLinkId;

  @BeforeEach
  void setUp() {
    // Initialize standalone MockMvc with controller and exception handler
    AlphaQuizController controller = new AlphaQuizController(
      uploadUseCase,
      downloadUseCase
    );

    // Configure exception resolver to properly handle @ControllerAdvice
    ExceptionHandlerExceptionResolver exceptionResolver =
      new ExceptionHandlerExceptionResolver();
    exceptionResolver
      .getMessageConverters()
      .add(new JacksonJsonHttpMessageConverter() {});
    exceptionResolver.afterPropertiesSet();

    mockMvc = MockMvcBuilders.standaloneSetup(controller)
      .setControllerAdvice(new GlobalExceptionHandler())
      .setHandlerExceptionResolvers(exceptionResolver)
      .build();

    // Initialize test data
    testBlobContent = "encrypted-file-content-here".getBytes();
    testEnvelopeBase64 = Base64.getEncoder().encodeToString(
      "test-envelope-32-bytes-padding!".getBytes()
    );
    testSaltBase64 = Base64.getEncoder().encodeToString(
      "test-salt-16bytes".getBytes()
    );
    testGateHashBase64 = Base64.getEncoder().encodeToString(
      "test-gate-hash-32-bytes-padding!".getBytes()
    );
    testEncryptedQuestionsBase64 = Base64.getEncoder().encodeToString(
      "encrypted-quiz-questions".getBytes()
    );
    testExpiryDate = Instant.now()
      .plus(7, ChronoUnit.DAYS)
      .truncatedTo(ChronoUnit.SECONDS);
    testRetryCount = 3;
    testLinkId = LinkId.generate();
  }

  @Nested
  @DisplayName("Successful Upload Tests")
  class SuccessfulUploadTests {

    @Test
    @DisplayName("Should return 200 OK with linkId when all fields are valid")
    void upload_withValidData_returnsOkWithLinkId() throws Exception {
      // Arrange
      when(uploadUseCase.upload(any(AlphaUploadInput.class))).thenReturn(
        new UploadResult(testLinkId)
      );

      MockMultipartFile blob = new MockMultipartFile(
        "blob",
        "test-file.bin",
        MediaType.APPLICATION_OCTET_STREAM_VALUE,
        testBlobContent
      );

      // Act & Assert
      mockMvc
        .perform(
          multipart(UPLOAD_URL)
            .file(blob)
            .param("envelope", testEnvelopeBase64)
            .param("salt", testSaltBase64)
            .param("gateHash", testGateHashBase64)
            .param("encryptedQuestions", testEncryptedQuestionsBase64)
            .param("expiryDate", testExpiryDate.toString())
            .param("retryCount", String.valueOf(testRetryCount))
            .contentType(MediaType.MULTIPART_FORM_DATA)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.linkId").value(testLinkId.toString()))
        .andExpect(jsonPath("$.expiryDate").exists());
    }

    @Test
    @DisplayName("Should correctly pass decoded data to use case")
    void upload_withValidData_passesDecodedDataToUseCase() throws Exception {
      // Arrange
      ArgumentCaptor<AlphaUploadInput> inputCaptor = ArgumentCaptor.forClass(
        AlphaUploadInput.class
      );
      when(uploadUseCase.upload(inputCaptor.capture())).thenReturn(
        new UploadResult(testLinkId)
      );

      MockMultipartFile blob = new MockMultipartFile(
        "blob",
        "test-file.bin",
        MediaType.APPLICATION_OCTET_STREAM_VALUE,
        testBlobContent
      );

      // Act
      mockMvc
        .perform(
          multipart(UPLOAD_URL)
            .file(blob)
            .param("envelope", testEnvelopeBase64)
            .param("salt", testSaltBase64)
            .param("gateHash", testGateHashBase64)
            .param("encryptedQuestions", testEncryptedQuestionsBase64)
            .param("expiryDate", testExpiryDate.toString())
            .param("retryCount", String.valueOf(testRetryCount))
            .contentType(MediaType.MULTIPART_FORM_DATA)
        )
        .andExpect(status().isOk());

      // Assert
      verify(uploadUseCase).upload(any(AlphaUploadInput.class));
      AlphaUploadInput capturedInput = inputCaptor.getValue();

      assertThat(capturedInput.blob().getBytes()).isEqualTo(testBlobContent);
      assertThat(capturedInput.envelope()).isEqualTo(
        Base64.getDecoder().decode(testEnvelopeBase64)
      );
      assertThat(capturedInput.salt()).isEqualTo(
        Base64.getDecoder().decode(testSaltBase64)
      );
      assertThat(capturedInput.gateHash().getBytes()).isEqualTo(
        Base64.getDecoder().decode(testGateHashBase64)
      );
      assertThat(capturedInput.encryptedQuestions().getBytes()).isEqualTo(
        Base64.getDecoder().decode(testEncryptedQuestionsBase64)
      );
      assertThat(capturedInput.expiryDate()).isEqualTo(testExpiryDate);
      assertThat(capturedInput.retryCount()).isEqualTo(testRetryCount);
    }

    @Test
    @DisplayName("Should accept minimum retry count of 1")
    void upload_withRetryCountOne_succeeds() throws Exception {
      // Arrange
      when(uploadUseCase.upload(any(AlphaUploadInput.class))).thenReturn(
        new UploadResult(testLinkId)
      );

      MockMultipartFile blob = new MockMultipartFile(
        "blob",
        "test-file.bin",
        MediaType.APPLICATION_OCTET_STREAM_VALUE,
        testBlobContent
      );

      // Act & Assert
      mockMvc
        .perform(
          multipart(UPLOAD_URL)
            .file(blob)
            .param("envelope", testEnvelopeBase64)
            .param("salt", testSaltBase64)
            .param("gateHash", testGateHashBase64)
            .param("encryptedQuestions", testEncryptedQuestionsBase64)
            .param("expiryDate", testExpiryDate.toString())
            .param("retryCount", "1")
            .contentType(MediaType.MULTIPART_FORM_DATA)
        )
        .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should accept large blob content")
    void upload_withLargeBlobContent_succeeds() throws Exception {
      // Arrange
      byte[] largeContent = new byte[1024 * 1024]; // 1MB
      java.util.Arrays.fill(largeContent, (byte) 0x42);

      when(uploadUseCase.upload(any(AlphaUploadInput.class))).thenReturn(
        new UploadResult(testLinkId)
      );

      MockMultipartFile blob = new MockMultipartFile(
        "blob",
        "large-file.bin",
        MediaType.APPLICATION_OCTET_STREAM_VALUE,
        largeContent
      );

      // Act & Assert
      mockMvc
        .perform(
          multipart(UPLOAD_URL)
            .file(blob)
            .param("envelope", testEnvelopeBase64)
            .param("salt", testSaltBase64)
            .param("gateHash", testGateHashBase64)
            .param("encryptedQuestions", testEncryptedQuestionsBase64)
            .param("expiryDate", testExpiryDate.toString())
            .param("retryCount", String.valueOf(testRetryCount))
            .contentType(MediaType.MULTIPART_FORM_DATA)
        )
        .andExpect(status().isOk());
    }
  }

  @Nested
  @DisplayName("Missing Field Tests")
  class MissingFieldTests {

    @Test
    @DisplayName("Should return 400 when blob is missing")
    void upload_withoutBlob_returnsBadRequest() throws Exception {
      // Act & Assert
      mockMvc
        .perform(
          multipart(UPLOAD_URL)
            .param("envelope", testEnvelopeBase64)
            .param("salt", testSaltBase64)
            .param("gateHash", testGateHashBase64)
            .param("encryptedQuestions", testEncryptedQuestionsBase64)
            .param("expiryDate", testExpiryDate.toString())
            .param("retryCount", String.valueOf(testRetryCount))
            .contentType(MediaType.MULTIPART_FORM_DATA)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when envelope is missing")
    void upload_withoutEnvelope_returnsBadRequest() throws Exception {
      // Arrange
      MockMultipartFile blob = new MockMultipartFile(
        "blob",
        "test-file.bin",
        MediaType.APPLICATION_OCTET_STREAM_VALUE,
        testBlobContent
      );

      // Act & Assert
      mockMvc
        .perform(
          multipart(UPLOAD_URL)
            .file(blob)
            .param("salt", testSaltBase64)
            .param("gateHash", testGateHashBase64)
            .param("encryptedQuestions", testEncryptedQuestionsBase64)
            .param("expiryDate", testExpiryDate.toString())
            .param("retryCount", String.valueOf(testRetryCount))
            .contentType(MediaType.MULTIPART_FORM_DATA)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when salt is missing")
    void upload_withoutSalt_returnsBadRequest() throws Exception {
      // Arrange
      MockMultipartFile blob = new MockMultipartFile(
        "blob",
        "test-file.bin",
        MediaType.APPLICATION_OCTET_STREAM_VALUE,
        testBlobContent
      );

      // Act & Assert
      mockMvc
        .perform(
          multipart(UPLOAD_URL)
            .file(blob)
            .param("envelope", testEnvelopeBase64)
            .param("gateHash", testGateHashBase64)
            .param("encryptedQuestions", testEncryptedQuestionsBase64)
            .param("expiryDate", testExpiryDate.toString())
            .param("retryCount", String.valueOf(testRetryCount))
            .contentType(MediaType.MULTIPART_FORM_DATA)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when gateHash is missing")
    void upload_withoutGateHash_returnsBadRequest() throws Exception {
      // Arrange
      MockMultipartFile blob = new MockMultipartFile(
        "blob",
        "test-file.bin",
        MediaType.APPLICATION_OCTET_STREAM_VALUE,
        testBlobContent
      );

      // Act & Assert
      mockMvc
        .perform(
          multipart(UPLOAD_URL)
            .file(blob)
            .param("envelope", testEnvelopeBase64)
            .param("salt", testSaltBase64)
            .param("encryptedQuestions", testEncryptedQuestionsBase64)
            .param("expiryDate", testExpiryDate.toString())
            .param("retryCount", String.valueOf(testRetryCount))
            .contentType(MediaType.MULTIPART_FORM_DATA)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when encryptedQuestions is missing")
    void upload_withoutEncryptedQuestions_returnsBadRequest() throws Exception {
      // Arrange
      MockMultipartFile blob = new MockMultipartFile(
        "blob",
        "test-file.bin",
        MediaType.APPLICATION_OCTET_STREAM_VALUE,
        testBlobContent
      );

      // Act & Assert
      mockMvc
        .perform(
          multipart(UPLOAD_URL)
            .file(blob)
            .param("envelope", testEnvelopeBase64)
            .param("salt", testSaltBase64)
            .param("gateHash", testGateHashBase64)
            .param("expiryDate", testExpiryDate.toString())
            .param("retryCount", String.valueOf(testRetryCount))
            .contentType(MediaType.MULTIPART_FORM_DATA)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when expiryDate is missing")
    void upload_withoutExpiryDate_returnsBadRequest() throws Exception {
      // Arrange
      MockMultipartFile blob = new MockMultipartFile(
        "blob",
        "test-file.bin",
        MediaType.APPLICATION_OCTET_STREAM_VALUE,
        testBlobContent
      );

      // Act & Assert
      mockMvc
        .perform(
          multipart(UPLOAD_URL)
            .file(blob)
            .param("envelope", testEnvelopeBase64)
            .param("salt", testSaltBase64)
            .param("gateHash", testGateHashBase64)
            .param("encryptedQuestions", testEncryptedQuestionsBase64)
            .param("retryCount", String.valueOf(testRetryCount))
            .contentType(MediaType.MULTIPART_FORM_DATA)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when retryCount is missing")
    void upload_withoutRetryCount_returnsBadRequest() throws Exception {
      // Arrange
      MockMultipartFile blob = new MockMultipartFile(
        "blob",
        "test-file.bin",
        MediaType.APPLICATION_OCTET_STREAM_VALUE,
        testBlobContent
      );

      // Act & Assert
      mockMvc
        .perform(
          multipart(UPLOAD_URL)
            .file(blob)
            .param("envelope", testEnvelopeBase64)
            .param("salt", testSaltBase64)
            .param("gateHash", testGateHashBase64)
            .param("encryptedQuestions", testEncryptedQuestionsBase64)
            .param("expiryDate", testExpiryDate.toString())
            .contentType(MediaType.MULTIPART_FORM_DATA)
        )
        .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("Invalid Data Tests")
  class InvalidDataTests {

    @Test
    @DisplayName("Should return 400 when envelope is not valid Base64")
    void upload_withInvalidEnvelopeBase64_returnsBadRequest() throws Exception {
      // Arrange
      MockMultipartFile blob = new MockMultipartFile(
        "blob",
        "test-file.bin",
        MediaType.APPLICATION_OCTET_STREAM_VALUE,
        testBlobContent
      );

      // Act & Assert
      mockMvc
        .perform(
          multipart(UPLOAD_URL)
            .file(blob)
            .param("envelope", "not-valid-base64!!!")
            .param("salt", testSaltBase64)
            .param("gateHash", testGateHashBase64)
            .param("encryptedQuestions", testEncryptedQuestionsBase64)
            .param("expiryDate", testExpiryDate.toString())
            .param("retryCount", String.valueOf(testRetryCount))
            .contentType(MediaType.MULTIPART_FORM_DATA)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when salt is not valid Base64")
    void upload_withInvalidSaltBase64_returnsBadRequest() throws Exception {
      // Arrange
      MockMultipartFile blob = new MockMultipartFile(
        "blob",
        "test-file.bin",
        MediaType.APPLICATION_OCTET_STREAM_VALUE,
        testBlobContent
      );

      // Act & Assert
      mockMvc
        .perform(
          multipart(UPLOAD_URL)
            .file(blob)
            .param("envelope", testEnvelopeBase64)
            .param("salt", "invalid-base64!!!")
            .param("gateHash", testGateHashBase64)
            .param("encryptedQuestions", testEncryptedQuestionsBase64)
            .param("expiryDate", testExpiryDate.toString())
            .param("retryCount", String.valueOf(testRetryCount))
            .contentType(MediaType.MULTIPART_FORM_DATA)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when gateHash is not valid Base64")
    void upload_withInvalidGateHashBase64_returnsBadRequest() throws Exception {
      // Arrange
      MockMultipartFile blob = new MockMultipartFile(
        "blob",
        "test-file.bin",
        MediaType.APPLICATION_OCTET_STREAM_VALUE,
        testBlobContent
      );

      // Act & Assert
      mockMvc
        .perform(
          multipart(UPLOAD_URL)
            .file(blob)
            .param("envelope", testEnvelopeBase64)
            .param("salt", testSaltBase64)
            .param("gateHash", "invalid-base64!!!")
            .param("encryptedQuestions", testEncryptedQuestionsBase64)
            .param("expiryDate", testExpiryDate.toString())
            .param("retryCount", String.valueOf(testRetryCount))
            .contentType(MediaType.MULTIPART_FORM_DATA)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when expiryDate is not valid ISO-8601")
    void upload_withInvalidExpiryDate_returnsBadRequest() throws Exception {
      // Arrange
      MockMultipartFile blob = new MockMultipartFile(
        "blob",
        "test-file.bin",
        MediaType.APPLICATION_OCTET_STREAM_VALUE,
        testBlobContent
      );

      // Act & Assert
      mockMvc
        .perform(
          multipart(UPLOAD_URL)
            .file(blob)
            .param("envelope", testEnvelopeBase64)
            .param("salt", testSaltBase64)
            .param("gateHash", testGateHashBase64)
            .param("encryptedQuestions", testEncryptedQuestionsBase64)
            .param("expiryDate", "not-a-date")
            .param("retryCount", String.valueOf(testRetryCount))
            .contentType(MediaType.MULTIPART_FORM_DATA)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when retryCount is not a number")
    void upload_withNonNumericRetryCount_returnsBadRequest() throws Exception {
      // Arrange
      MockMultipartFile blob = new MockMultipartFile(
        "blob",
        "test-file.bin",
        MediaType.APPLICATION_OCTET_STREAM_VALUE,
        testBlobContent
      );

      // Act & Assert
      mockMvc
        .perform(
          multipart(UPLOAD_URL)
            .file(blob)
            .param("envelope", testEnvelopeBase64)
            .param("salt", testSaltBase64)
            .param("gateHash", testGateHashBase64)
            .param("encryptedQuestions", testEncryptedQuestionsBase64)
            .param("expiryDate", testExpiryDate.toString())
            .param("retryCount", "not-a-number")
            .contentType(MediaType.MULTIPART_FORM_DATA)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when retryCount is negative")
    void upload_withNegativeRetryCount_returnsBadRequest() throws Exception {
      // Arrange
      MockMultipartFile blob = new MockMultipartFile(
        "blob",
        "test-file.bin",
        MediaType.APPLICATION_OCTET_STREAM_VALUE,
        testBlobContent
      );

      // Use lenient stubbing since the use case may or may not be called
      // depending on whether validation happens in controller or use case
      org.mockito.Mockito.lenient()
        .when(uploadUseCase.upload(any(AlphaUploadInput.class)))
        .thenThrow(new IllegalArgumentException("retryCount must be positive"));

      // Act & Assert
      mockMvc
        .perform(
          multipart(UPLOAD_URL)
            .file(blob)
            .param("envelope", testEnvelopeBase64)
            .param("salt", testSaltBase64)
            .param("gateHash", testGateHashBase64)
            .param("encryptedQuestions", testEncryptedQuestionsBase64)
            .param("expiryDate", testExpiryDate.toString())
            .param("retryCount", "-1")
            .contentType(MediaType.MULTIPART_FORM_DATA)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when blob is empty")
    void upload_withEmptyBlob_returnsBadRequest() throws Exception {
      // Arrange
      MockMultipartFile emptyBlob = new MockMultipartFile(
        "blob",
        "empty-file.bin",
        MediaType.APPLICATION_OCTET_STREAM_VALUE,
        new byte[0]
      );

      // Use lenient stubbing since the use case may or may not be called
      // depending on whether validation happens in controller or use case
      org.mockito.Mockito.lenient()
        .when(uploadUseCase.upload(any(AlphaUploadInput.class)))
        .thenThrow(new IllegalArgumentException("blob must not be empty"));

      // Act & Assert
      mockMvc
        .perform(
          multipart(UPLOAD_URL)
            .file(emptyBlob)
            .param("envelope", testEnvelopeBase64)
            .param("salt", testSaltBase64)
            .param("gateHash", testGateHashBase64)
            .param("encryptedQuestions", testEncryptedQuestionsBase64)
            .param("expiryDate", testExpiryDate.toString())
            .param("retryCount", String.valueOf(testRetryCount))
            .contentType(MediaType.MULTIPART_FORM_DATA)
        )
        .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("HTTP Method Tests")
  class HttpMethodTests {

    @Test
    @DisplayName("Should return 405 when using GET method")
    void upload_withGetMethod_returnsMethodNotAllowed() throws Exception {
      // Act & Assert
      mockMvc.perform(get(UPLOAD_URL)).andExpect(status().isMethodNotAllowed());
    }

    @Test
    @DisplayName("Should return 405 when using PUT method")
    void upload_withPutMethod_returnsMethodNotAllowed() throws Exception {
      // Act & Assert
      mockMvc.perform(put(UPLOAD_URL)).andExpect(status().isMethodNotAllowed());
    }

    @Test
    @DisplayName("Should return 405 when using DELETE method")
    void upload_withDeleteMethod_returnsMethodNotAllowed() throws Exception {
      // Act & Assert
      mockMvc
        .perform(delete(UPLOAD_URL))
        .andExpect(status().isMethodNotAllowed());
    }
  }

  @Nested
  @DisplayName("Content Type Tests")
  class ContentTypeTests {

    @Test
    @DisplayName("Should return 415 when Content-Type is application/json")
    void upload_withJsonContentType_returnsUnsupportedMediaType()
      throws Exception {
      // Act & Assert
      mockMvc
        .perform(
          post(UPLOAD_URL).contentType(MediaType.APPLICATION_JSON).content("{}")
        )
        .andExpect(status().isUnsupportedMediaType());
    }
  }
}
