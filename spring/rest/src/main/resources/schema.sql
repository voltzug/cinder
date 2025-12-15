-- Cinder SQLite Schema
-- Creates tables for secure file storage and access link management

-- secure_file: stores metadata about encrypted files
CREATE TABLE IF NOT EXISTS secure_file (
    file_id TEXT PRIMARY KEY NOT NULL,
    link_id TEXT NOT NULL UNIQUE,
    owner_id TEXT NOT NULL,
    path_reference TEXT NOT NULL UNIQUE,
    sealed_envelope BLOB NOT NULL,
    sealed_salt BLOB NOT NULL,
    expiry_date TEXT NOT NULL,
    created_at TEXT NOT NULL
);

-- Index for efficient lookup by link_id
CREATE INDEX IF NOT EXISTS idx_secure_file_link_id ON secure_file(link_id);

-- Index for expiry cleanup queries
CREATE INDEX IF NOT EXISTS idx_secure_file_expiry_date ON secure_file(expiry_date);

-- access_link: stores link-specific metadata for download limits and gate mechanisms
CREATE TABLE IF NOT EXISTS access_link (
    link_id TEXT PRIMARY KEY NOT NULL,
    remaining_attempts INTEGER NOT NULL,
    gate_box BLOB NOT NULL,
    gate_context BLOB,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL,
    file_id TEXT NOT NULL UNIQUE,
    FOREIGN KEY (file_id) REFERENCES secure_file(file_id) ON DELETE CASCADE
);

-- Index for efficient lookup by file_id
CREATE INDEX IF NOT EXISTS idx_access_link_file_id ON access_link(file_id);