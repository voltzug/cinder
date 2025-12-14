/**
 * Models for the secure file upload flow.
 *
 * <p>
 * This package defines the data structures used during the file upload process,
 * which consists of a handshake phase followed by the encrypted payload transmission.
 * </p>
 *
 * <ul>
 *   <li>{@link com.voltzug.cinder.core.model.upload.UploadHandshakeChallenge} — Server challenge containing session ID and secret</li>
 *   <li>{@link com.voltzug.cinder.core.model.upload.UploadRequest} — The main upload payload with encrypted file and metadata</li>
 *   <li>{@link com.voltzug.cinder.core.model.upload.UploadResult} — Result containing the generated access link ID</li>
 * </ul>
 */
package com.voltzug.cinder.core.model.upload;
