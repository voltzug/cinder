## 1) API Documentation and Curl Commands

### Alpha Quiz API Documentation

#### Base URL
```/dev/null/path.txt#L1
http://localhost:8088/api/alpha
```

---

### **POST /api/alpha/upload**

Upload an encrypted file with quiz metadata.

**Content-Type:** `multipart/form-data`

**Form Fields:**
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `blob` | File | Yes | The encrypted file content |
| `envelope` | String | Yes | Base64-encoded encrypted envelope (fK\|\|fNonce sealed with quizK) |
| `salt` | String | Yes | Base64-encoded argon2 salt |
| `gateHash` | String | Yes | Base64-encoded SHA256 of (answers\|\|nonce) |
| `encryptedQuestions` | String | Yes | Base64-encoded encrypted quiz questions |
| `expiryDate` | String | Yes | ISO-8601 timestamp for file expiry |
| `retryCount` | Integer | Yes | Maximum download attempts (1-99) |

**Response:** `200 OK`
```/dev/null/example.json#L1-4
{
  "linkId": "LK<uuid>",
  "expiryDate": "2025-12-31T23:59:59Z"
}
```

**Curl Example:**
```/dev/null/upload.sh#L1-11
curl -X POST http://localhost:8088/api/alpha/upload \
  -F "blob=@test.bin" \
  -F "envelope=YmFzZTY0LWVudmVsb3BlLWRhdGE=" \
  -F "salt=YmFzZTY0LXNhbHQtZGF0YQ==" \
  -F "gateHash=YmFzZTY0LWdhdGUtaGFzaA==" \
  -F "encryptedQuestions=YmFzZTY0LXF1ZXN0aW9ucw==" \
  -F "expiryDate=2025-12-31T23:59:59Z" \
  -F "retryCount=3"

# With inline blob data (no file):
curl -X POST http://localhost:8088/api/alpha/upload \
  -F "blob=@-;filename=data.bin" <<< "encrypted content here" \
  -F "envelope=$(echo -n 'envelope-bytes' | base64)" \
  -F "salt=$(echo -n 'salt-bytes' | base64)" \
  -F "gateHash=$(echo -n 'gate-hash-bytes0' | base64)" \
  -F "encryptedQuestions=$(echo -n 'questions-bytes' | base64)" \
  -F "expiryDate=2025-12-31T23:59:59Z" \
  -F "retryCount=3"
```

---

### **GET /api/alpha/download/{linkId}/init**

Initialize a download session. Returns encrypted quiz questions.

**Path Parameters:**
| Parameter | Description |
|-----------|-------------|
| `linkId` | The link identifier (with or without LK prefix) |

**Response:** `200 OK`
```/dev/null/example.json#L1-4
{
  "sessionId": "SN<uuid>",
  "encryptedQuestions": "<base64-encoded-questions>"
}
```

**Curl Example:**
```/dev/null/init.sh#L1-2
curl -X GET "http://localhost:8088/api/alpha/download/LKe33788fb-ee68-49d3-82de-262106b9181d/init"
```

---

### **POST /api/alpha/download/{linkId}**

Verify access hash and download the encrypted file.

**Content-Type:** `application/json`

**Path Parameters:**
| Parameter | Description |
|-----------|-------------|
| `linkId` | The link identifier |

**Request Body:**
```/dev/null/example.json#L1-4
{
  "sessionId": "SN<uuid>",
  "accessHash": "<base64-encoded SHA256 of answers||nonce>"
}
```

**Response:** `200 OK`
- **Content-Type:** `application/octet-stream`
- **Body:** Raw encrypted blob bytes
- **Headers:**
  - `X-Cinder-Envelope`: Base64-encoded unsealed envelope
  - `X-Cinder-Salt`: Base64-encoded unsealed salt

**Curl Example:**
```/dev/null/download.sh#L1-8
curl -X POST "http://localhost:8088/api/alpha/download/LK12345678-1234-1234-1234-123456789abc" \
  -H "Content-Type: application/json" \
  -d '{
    "sessionId": "SN98765432-4321-4321-4321-cba987654321",
    "accessHash": "YmFzZTY0LWFjY2Vzcy1oYXNo"
  }' \
  --output downloaded_file.bin \
  -D response_headers.txt
```
