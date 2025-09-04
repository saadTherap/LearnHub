# API Documentation

This documentation provides a comprehensive overview of the available API endpoints. The API is divided into public-facing endpoints for general users and administrative endpoints for managing the system.

-----

## Public API Endpoints

### 1\. Register User

`POST /api/register`

This endpoint creates a new user account.

**Request Body:**

```json
{
  "email": "string",
  "password": "string",
  "role": "string"
}
```

* **password:** Must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character.

**Response (200 OK):**

```json
{
  "accessToken": "string",
  "refreshToken": "string",
  "tokenType": "Bearer",
  "message": null
}
```

**Error Response (400 Bad Request):**

```json
{
  "timestamp": "2025-09-04T09:00:00.000+06:00",
  "message": "Validation failed",
  "error": null,
  "formErrors": {
    "field_name": "Error message"
  }
}
```

-----

### 2\. Login User

`POST /api/login`

Authenticates a user and returns access and refresh tokens.

**Request Body:**

```json
{
  "email": "string",
  "password": "string"
}
```

**Response (200 OK):**

```json
{
  "email": "string",
  "role": "string",
  "accessToken": "string",
  "refreshToken": "string"
}
```

**Error Response (401 Unauthorized):**

```json
{
  "timestamp": "2025-09-04T09:00:00.000+06:00",
  "message": "An unexpected error occurred",
  "error": "Bad credentials.",
  "formErrors": null
}
```

-----

### 3\. Refresh Token

`POST /api/refresh`

Generates a new access token using a valid refresh token.

**Request Body:**

```json
{
  "refreshToken": "string"
}
```

**Response (200 OK):**

```json
{
  "accessToken": "string",
  "refreshToken": "string",
  "tokenType": "Bearer",
  "message": null
}
```

-----

### 4\. Update User

`PUT /api/update-user`

Updates a user's profile information. The `id` and `role` are required fields.

**Request Body:**

```json
{
  "id": 0,
  "password": "string",
  "role": "string",
  "enabled": true
}
```

* **password:** Optional. If provided, it must meet the same complexity requirements as registration.

**Response (200 OK):**

```json
{
  "accessToken": "string",
  "refreshToken": "string",
  "tokenType": "Bearer",
  "message": null
}
```

-----

### 5\. Verify Email

`GET /api/verify-email`

Verifies a user's email using a token sent to their email address.

**Request Parameters:**

* `token` (query): The verification token.

**Response (200 OK):**

```json
{
  "accessToken": "string",
  "refreshToken": "string",
  "tokenType": "Bearer",
  "message": null
}
```

**Error Response (400 Bad Request):**

```
token parameter is missing
```

-----

### 6\. Delete Account

`DELETE /api/delete`

Deletes the currently authenticated user's account.

**Request Body:**

```json
{
  "accessToken": "string"
}
```

**Response (200 OK):**

```json
{
  "accessToken": null,
  "refreshToken": null,
  "tokenType": "Bearer",
  "message": "User deleted successfully."
}
```

-----

## Admin API Endpoints

### 1\. Get All Users

`GET /admin/users`

Retrieves a list of all users. Accessible only by administrators.

**Response (200 OK):**

```json
[
  {
    "createdAt": "2025-09-04T08:55:49.000+06:00",
    "updatedAt": "2025-09-04T08:55:49.000+06:00",
    "version": 1,
    "id": 1,
    "email": "string",
    "role": "STUDENT",
    "enabled": true,
    "deleted": false
  }
]
```

-----

### 2\. Get User by ID

`GET /admin/user/{userId}`

Retrieves a single user's details by their ID.

**Path Parameters:**

* `userId` (path): The ID of the user.

**Response (200 OK):**

```json
{
  "createdAt": "2025-09-04T08:55:49.000+06:00",
  "updatedAt": "2025-09-04T08:55:49.000+06:00",
  "version": 1,
  "id": 1,
  "email": "string",
  "role": "STUDENT",
  "enabled": true,
  "deleted": false
}
```

-----

### 3\. Toggle User Status

`PUT /admin/user/{userId}/toggle-status`

Toggles a user's `enabled` status (active/inactive).

**Path Parameters:**

* `userId` (path): The ID of the user.

**Response (200 OK):**

```json
{
  "createdAt": "2025-09-04T08:55:49.000+06:00",
  "updatedAt": "2025-09-04T08:55:49.000+06:00",
  "version": 1,
  "id": 1,
  "email": "string",
  "role": "STUDENT",
  "enabled": true,
  "deleted": false
}
```

-----

### 4\. Delete User (Admin)

`DELETE /admin/delete-user/{id}`

Deletes a user account by their ID.

**Path Parameters:**

* `id` (path): The ID of the user to delete.

**Response (200 OK):**
No content is returned in the response body.

-----

### 5\. Force Logout

`POST /admin/logout-force`

Forces a user to log out by invalidating their authentication tokens.

**Request Body:**

```json
{}
```

**Response (200 OK):**

```json
{
  "accessToken": "string",
  "refreshToken": "string",
  "tokenType": "Bearer",
  "message": "User logged out successfully."
}
```

-----

### 6\. Get Admin Profile

`GET /admin/me`

Retrieves the profile of the authenticated administrator.

**Response (200 OK):**

```json
{
  "createdAt": "2025-09-04T08:55:49.000+06:00",
  "updatedAt": "2025-09-04T08:55:49.000+06:00",
  "version": 1,
  "id": 1,
  "email": "string",
  "role": "ADMIN",
  "enabled": true,
  "deleted": false
}
```

-----

## App Status

### 1\. Check App Status

`GET /appStatus`

Provides the current health and status of the application.

**Response (200 OK):**

```json
{
  "status": "UP"
}
```