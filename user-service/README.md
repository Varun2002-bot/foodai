# User Service Auth API

## What this service does

`user-service` provides signup, login, JWT-based authentication, and a protected profile endpoint.

## JWT concepts used here

- Subject: the primary identity the token is issued for. In this service the subject is the user's email.
- Claims: extra key-value data stored inside the token. This service includes `userId`, `email`, and `role`.
- Expiration: the point after which the token is no longer accepted. This service defaults to `3600000` ms, which is 1 hour.
- Signing secret: the server-only secret key used to sign and verify the token. If this secret changes, previously issued tokens become invalid.

## Endpoints

### `POST /api/v1/auth/signup`

Request:

```json
{
  "name": "Varun",
  "email": "varun@example.com",
  "password": "password123",
  "role": "CUSTOMER"
}
```

### `POST /api/v1/auth/login`

Request:

```json
{
  "email": "varun@example.com",
  "password": "password123"
}
```

Successful response returns a Bearer token in `data.accessToken`.

### `GET /api/v1/users/me`

Headers:

```text
Authorization: Bearer <jwt>
```

Returns the logged-in user's identity derived from the JWT and current user record.

## Local run

1. Start PostgreSQL on port `5433` with database `foodai`.
2. Run:

```bash
mvn -pl user-service -am spring-boot:run
```

## Docker run

From the repo root:

```bash
docker compose up --build
```

Services:

- API: `http://localhost:8082`
- Postgres: `localhost:5433`

## Postman / manual flow

1. Call `POST /api/v1/auth/signup`.
2. Call `POST /api/v1/auth/login`.
3. Copy `data.accessToken` from the login response.
4. Call `GET /api/v1/users/me` with `Authorization: Bearer <token>`.

## Error behavior

- Duplicate email returns `409 Conflict`
- Invalid login returns `401 Unauthorized`
- Validation failures return `400 Bad Request`
- Missing or invalid JWT on protected endpoints returns `401 Unauthorized`
