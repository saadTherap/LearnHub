# 🚀 Authentication & User Management API Documentation

This API provides **public-facing endpoints** for authentication and account management, and **admin-only endpoints** for managing users and authentication keys.

All endpoints follow REST principles and return JSON responses.

---

## 🌐 Public API Endpoints

### 1️⃣ Register User

**`POST /api/register`**

Creates a new user account.

#### 📥 Request Body

```json
{
  "email": "user@example.com",
  "password": "P@ssw0rd123!",
  "role": "STUDENT"
}
```

* **email**: Must be a valid email format.
* **password**:

    * Minimum 8 characters
    * At least **1 uppercase**, **1 lowercase**, **1 number**, **1 special character**
* **role**: Must be a valid enum (`STUDENT`, `TEACHER`, `ADMIN`).

#### 📤 Success Response (200 OK)

```json
{
  "accessToken": "jwt_access_token_here",
  "refreshToken": "jwt_refresh_token_here",
  "tokenType": "Bearer",
  "message": null
}
```

* **accessToken**: Used for authentication in `Authorization: Bearer <token>`.
* **refreshToken**: Used to acquire a new access token.

#### ⚠️ Error Response (400 Bad Request)

```json
{
  "timestamp": "2025-09-04T09:00:00.000+06:00",
  "message": "Validation failed",
  "error": null,
  "formErrors": {
    "password": "Password must contain uppercase, lowercase, number, special character"
  }
}
```

---

### 2️⃣ Login User

**`POST /api/login`**

Authenticates a user and issues tokens.

#### 📥 Request Body

```json
{
  "email": "user@example.com",
  "password": "P@ssw0rd123!"
}
```

#### 📤 Success Response (200 OK)

```json
{
  "email": "user@example.com",
  "role": "STUDENT",
  "accessToken": "jwt_access_token_here",
  "refreshToken": "jwt_refresh_token_here"
}
```

#### ⚠️ Error Response (401 Unauthorized)

```json
{
  "timestamp": "2025-09-04T09:00:00.000+06:00",
  "message": "Authentication failed",
  "error": "Bad credentials",
  "formErrors": null
}
```

---

### 3️⃣ Refresh Token

**`POST /api/refresh`**

Issues a new access token using a valid refresh token.

#### 📥 Request Body

```json
{
  "refreshToken": "jwt_refresh_token_here"
}
```

#### 📤 Success Response (200 OK)

```json
{
  "accessToken": "new_jwt_access_token",
  "refreshToken": "new_jwt_refresh_token",
  "tokenType": "Bearer",
  "message": null
}
```

---

### 4️⃣ Verify Email

**`GET /api/verify-email`**

Verifies a user’s email via token sent to their inbox.

#### 🔗 Query Parameter

* `token` (required): Verification token

#### 📤 Success Response (200 OK)

```json
{
  "accessToken": "jwt_access_token_here",
  "refreshToken": "jwt_refresh_token_here",
  "tokenType": "Bearer",
  "message": null
}
```

#### ⚠️ Error Response (400 Bad Request)

```
token parameter is missing
```

---

### 5️⃣ Update User

**`PUT /api/update-user`**

Updates user’s details (requires special update token).

#### 📥 Request Body

```json
{
  "id": 1,
  "password": "N3wP@ssword!",
  "role": "TEACHER",
  "enabled": true
}
```

* **id**: User ID
* **password** *(optional)*: Must follow registration rules if provided
* **role**: Must be valid enum
* **enabled**: Boolean

#### 📤 Success Response (200 OK)

```json
{
  "accessToken": "jwt_access_token_here",
  "refreshToken": "jwt_refresh_token_here",
  "tokenType": "Bearer",
  "message": "User updated successfully"
}
```

---

### 6️⃣ Acquire Update-User Token

**`POST /api/acquire-update-user-token`**

Generates a temporary token to authorize a user update.

#### 📥 Request Body

```json
{
  "email": "user@example.com"
}
```

#### 📤 Success Response (200 OK)

```json
{
  "accessToken": "temporary_update_access_token",
  "refreshToken": null,
  "tokenType": "Bearer",
  "message": "Update token acquired"
}
```

---

### 7️⃣ Delete Account

**`DELETE /api/delete`**

Deletes the currently authenticated user account.

#### 🔑 Authentication

The user must be logged in; `userId` is extracted from request attribute.

#### 📤 Success Response (200 OK)

```json
{
  "accessToken": null,
  "refreshToken": null,
  "tokenType": "Bearer",
  "message": "User deleted successfully"
}
```

---

## 🔑 Admin API Endpoints

⚠️ All endpoints require the requester to be authenticated **ADMIN**. If a non-admin or unauthenticated request is made, the API throws:

```json
{
  "timestamp": "2025-09-07T10:00:00.000+06:00",
  "message": "Admin access required",
  "error": "Unauthorized",
  "formErrors": null
}
```

---

### 👤 Get Admin Profile

**`GET /admin/me`**

Returns authenticated admin’s details.

#### 📤 Success Response

```json
{
  "id": 1,
  "email": "admin@example.com",
  "role": "ADMIN",
  "enabled": true,
  "deleted": false,
  "createdAt": "2025-09-04T08:55:49.000+06:00",
  "updatedAt": "2025-09-04T08:55:49.000+06:00",
  "version": 1
}
```

---

### 👥 Get All Users

**`GET /admin/users`**

Retrieves a list of all registered users.

#### 📤 Success Response

```json
[
  {
    "id": 2,
    "email": "student@example.com",
    "role": "STUDENT",
    "enabled": true,
    "deleted": false,
    "createdAt": "2025-09-04T08:55:49.000+06:00",
    "updatedAt": "2025-09-04T08:55:49.000+06:00",
    "version": 1
  }
]
```

---

### 🔍 Get User by ID

**`GET /admin/user/{userId}`**

Fetch details of a specific user by ID.

#### 📤 Success Response

```json
{
  "id": 2,
  "email": "student@example.com",
  "role": "STUDENT",
  "enabled": true,
  "deleted": false,
  "createdAt": "2025-09-04T08:55:49.000+06:00",
  "updatedAt": "2025-09-04T08:55:49.000+06:00",
  "version": 1
}
```

---

### ❌ Delete User (Admin)

**`DELETE /admin/delete-user/{id}`**

Deletes a user by ID.

#### 📤 Success Response

```
HTTP 200 OK (empty body)
```

---

### 🔄 Toggle User Status

**`PUT /admin/user/{userId}/toggle-status`**

Activates/deactivates a user.

#### 📤 Success Response

```json
{
  "id": 2,
  "email": "student@example.com",
  "role": "STUDENT",
  "enabled": false,
  "deleted": false,
  "createdAt": "2025-09-04T08:55:49.000+06:00",
  "updatedAt": "2025-09-09T12:00:00.000+06:00",
  "version": 2
}
```

---

### 🚪 Force Logout User

**`POST /admin/logout-force`**

Invalidates a user’s tokens, forcing logout.

#### 📥 Request Body

```json
{
  "userId": 2
}
```

#### 📤 Success Response

```
HTTP 200 OK (empty body)
```

---

## 🔐 Authentication Keys API (Admin Only)

These endpoints manage **JWT signing keys** used by the system.

---

### 1️⃣ Generate New Key Pair

**`POST /admin/keys/generate`**

Generates a new public/private key pair and stores it.

#### 📤 Success Response

```json
{
  "id": 10,
  "kid": "key_20250907_123456",
  "publicKey": "-----BEGIN PUBLIC KEY----- ...",
  "privateKey": "-----BEGIN PRIVATE KEY----- ...",
  "status": "ACTIVE"
}
```

---

### 2️⃣ Get Active Key

**`GET /admin/keys/active`**

Retrieves the currently active key used for signing JWTs.

#### 📤 Success Response

```json
{
  "id": 10,
  "kid": "key_20250907_123456",
  "publicKey": "-----BEGIN PUBLIC KEY----- ...",
  "privateKey": "-----BEGIN PRIVATE KEY----- ...",
  "status": "ACTIVE"
}
```

---

### 3️⃣ Retire Key

**`PUT /admin/keys/{kid}/retire`**

Marks a key as retired so it is no longer used for signing.

#### 📤 Success Response

```
HTTP 200 OK (empty body)
```

---

## 📊 App Status

### Check App Status

**`GET /appStatus`**

Returns system health status.

#### 📤 Response

```json
{
  "status": "UP"
}
```