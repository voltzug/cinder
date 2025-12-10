/**
 * Request and response models for the Cinder core domain.
 *
 * <p>
 * This package contains the data structures used to exchange information between
 * the core domain and external layers (such as the REST API or infrastructure adapters).
 * These models define the payloads for the secure file transfer protocols, including
 * handshake challenges, upload requests, and download verification.
 * </p>
 *
 * <p>
 * The models are organized by flow:
 * <ul>
 *   <li>{@link com.voltzug.cinder.core.model.upload} — Models for the file upload process</li>
 *   <li>{@link com.voltzug.cinder.core.model.download} — Models for the file download and verification process</li>
 * </ul>
 * </p>
 */
package com.voltzug.cinder.core.model;
