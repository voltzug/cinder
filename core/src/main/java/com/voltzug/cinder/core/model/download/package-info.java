/**
 * Models for the secure file download flow.
 *
 * <p>
 * This package defines the data structures used during the file download process,
 * which involves a handshake, quiz verification, and final file retrieval.
 * </p>
 *
 * <ul>
 *   <li>{@link com.voltzug.cinder.core.model.download.DownloadHandshakeContext} — Context for initiating the handshake (link ID)</li>
 *   <li>{@link com.voltzug.cinder.core.model.download.DownloadHandshakeChallenge} — Server challenge containing encrypted questions</li>
 *   <li>{@link com.voltzug.cinder.core.model.download.DownloadVerifyRequest} — Client verification payload with quiz answer hash</li>
 *   <li>{@link com.voltzug.cinder.core.model.download.DownloadVerifyResult} — Successful verification result with encrypted file</li>
 *   <li>{@link com.voltzug.cinder.core.model.download.DownloadAcknowledgment} — Client confirmation to trigger file burning</li>
 * </ul>
 */
package com.voltzug.cinder.core.model.download;
