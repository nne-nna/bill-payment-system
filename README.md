# Bill Payment System

A production-style fintech REST API built with Java and Spring Boot for wallet funding and utility bill payments including electricity, airtime, data, and cable TV subscriptions across major Nigerian providers.
It powers the PayEase frontend with secure authentication, payment processing, notifications, and transaction tracking.

---

## Live Links

- Frontend: [PayEase Web](https://payease-web.vercel.app)
- API Docs (Swagger): [Bill Payment API Docs](https://bill-payment-system.onrender.com/swagger-ui/index.html)

---

## Live Features

- JWT authentication for registration and login
- Password reset flow via email
- Wallet funding via Paystack
- Paystack redirect and webhook support
- Electricity bill payments across major Nigerian providers
- Airtime and data purchases for major networks
- Cable TV subscriptions
- Transaction history and filtering
- Notification center with read and read-all actions
- Profile management and password change

---

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 21 | Programming language |
| Spring Boot 4.0.3 | Backend framework |
| Spring Security + JWT | Authentication and authorization |
| Spring Data JPA | ORM and database access |
| PostgreSQL | Relational database |
| Paystack API | Wallet funding and payment verification |
| VTPass API | Bill payment delivery |
| Brevo API | Password reset email delivery |
| Unirest | HTTP client for external APIs |
| Gson | JSON parsing |
| Springdoc OpenAPI | API docs in development |
| Lombok | Boilerplate reduction |
| Maven | Dependency management |
| Docker | Containerized deployment |

---

## Architecture

```text
Client (Postman / Frontend)
        |
Spring Boot REST API
    |-- AuthController
    |-- PasswordResetController
    |-- WalletController
    |-- BillPaymentController
    |-- NotificationController
    |-- UserController
    `-- TransactionController
        |
Service Layer
    |-- AuthService
    |-- PasswordResetService -> Brevo API
    |-- WalletService -> Paystack API
    |-- BillPaymentService -> VTPass API
    |-- NotificationService
    |-- UserService
    `-- TransactionService
        |
Repository Layer (JPA)
        |
PostgreSQL Database
```

---

## Project Structure

```text
src/main/java/com/billpayments/
|
|-- controller/
|   |-- AuthController.java
|   |-- PasswordResetController.java
|   |-- WalletController.java
|   |-- BillPaymentController.java
|   |-- NotificationController.java
|   |-- UserController.java
|   `-- TransactionController.java
|
|-- models/
|   |-- User.java
|   |-- Wallet.java
|   |-- Transaction.java
|   |-- Notification.java
|   `-- PasswordResetToken.java
|
|-- payload/
|   |-- request/
|   |   |-- RegisterRequest.java
|   |   |-- LoginRequest.java
|   |   |-- FundWalletRequest.java
|   |   |-- BillPaymentRequest.java
|   |   |-- UpdateProfileRequest.java
|   |   |-- ChangePasswordRequest.java
|   |   |-- ForgotPasswordRequest.java
|   |   `-- ResetPasswordRequest.java
|   `-- response/
|       |-- AuthResponse.java
|       |-- WalletResponse.java
|       |-- PaystackInitResponse.java
|       |-- BillPaymentResponse.java
|       |-- TransactionResponse.java
|       |-- ProfileResponse.java
|       |-- NotificationResponse.java
|       `-- NotificationSummaryResponse.java
|
|-- service/
|   |-- AuthService.java
|   |-- PasswordResetService.java
|   |-- WalletService.java
|   |-- BillPaymentService.java
|   |-- NotificationService.java
|   |-- UserService.java
|   |-- TransactionService.java
|   `-- impl/
|       |-- AuthServiceImpl.java
|       |-- PasswordResetServiceImpl.java
|       |-- WalletServiceImpl.java
|       |-- BillPaymentServiceImpl.java
|       |-- NotificationServiceImpl.java
|       |-- UserServiceImpl.java
|       |-- EmailServiceImpl.java
|       `-- TransactionServiceImpl.java
|
|-- repository/
|   |-- UserRepository.java
|   |-- WalletRepository.java
|   |-- TransactionRepository.java
|   |-- NotificationRepository.java
|   `-- PasswordResetTokenRepository.java
|
|-- security/
|   |-- JwtUtil.java
|   |-- JwtFilter.java
|   |-- SecurityConfig.java
|   `-- UserDetailsServiceImpl.java
|
|-- enums/
|   |-- Role.java
|   |-- TransactionType.java
|   |-- TransactionStatus.java
|   |-- NotificationType.java
|   `-- ServiceProvider.java
|
|-- exceptions/
|   |-- BadRequestException.java
|   |-- ResourceNotFoundException.java
|   `-- GlobalExceptionHandler.java
|
`-- utils/
    `-- AppConfig.java
```

---

## Setup and Installation

### Prerequisites

- Java 21
- Maven
- PostgreSQL
- Brevo account for password reset emails

### 1. Clone the repository

```bash
git clone https://github.com/nne-nna/bill-payment-system
cd bill-payment-system
```

### 2. Create the database

```sql
CREATE DATABASE bill_payment_db;
```

### 3. Configure local development

`src/main/resources/application.properties` activates the `prod` profile for deployment. For local development, set your environment or IDE to use `dev`, or run with:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Create your own `src/main/resources/application-dev.properties` with values like:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/bill_payment_db
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# JWT
jwt.secret=YOUR_JWT_SECRET
jwt.expiration=86400000

# Paystack
paystack.secret.key=YOUR_PAYSTACK_TEST_SECRET_KEY
paystack.base.url=https://api.paystack.co

# VTPass
vtpass.base.url=https://sandbox.vtpass.com/api
vtpass.api.key=YOUR_VTPASS_API_KEY
vtpass.secret.key=YOUR_VTPASS_SECRET_KEY
vtpass.public.key=YOUR_VTPASS_PUBLIC_KEY

# Brevo
brevo.api.key=YOUR_BREVO_API_KEY
brevo.sender.email=YOUR_VERIFIED_SENDER_EMAIL
brevo.sender.name=PayEase

# Frontend URL
app.frontend.url=http://localhost:5173
```

### 4. Get your API keys

- Paystack: create an account and use your test secret key
- VTPass: create a sandbox account and use sandbox credentials
- Brevo: validate a sender email and generate an API key

### 5. Run the application

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

The API will be available at `http://localhost:8080`.

---

## Docker and Render Deployment

This project includes a root `Dockerfile` for Render deployment.

Production uses:

- `spring.profiles.active=prod`
- environment variables loaded through `application-prod.properties`
- Swagger/OpenAPI disabled in production

Required Render environment variables:

```properties
DATABASE_URL=jdbc:postgresql://<host>:5432/<database>
DATABASE_USERNAME=<db_user>
DATABASE_PASSWORD=<db_password>
JWT_SECRET=<jwt_secret>
JWT_EXPIRATION=86400000
PAYSTACK_SECRET_KEY=<paystack_secret_key>
PAYSTACK_BASE_URL=https://api.paystack.co
VTPASS_BASE_URL=https://sandbox.vtpass.com/api
VTPASS_API_KEY=<vtpass_api_key>
VTPASS_SECRET_KEY=<vtpass_secret_key>
VTPASS_PUBLIC_KEY=<vtpass_public_key>
BREVO_API_KEY=<brevo_api_key>
BREVO_SENDER_EMAIL=<validated_sender_email>
BREVO_SENDER_NAME=PayEase
APP_FRONTEND_URL=https://payease-web.vercel.app
```

---

## API Endpoints

### Authentication

| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| POST | `/api/v1/auth/register` | Register new user | No |
| POST | `/api/v1/auth/login` | Login | No |
| POST | `/api/v1/auth/forgot-password` | Request password reset email | No |
| POST | `/api/v1/auth/reset-password` | Reset password with token | No |

#### Register

```json
POST /api/v1/auth/register
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "password": "Password123!",
  "phone": "08012345678"
}
```

#### Login

```json
POST /api/v1/auth/login
{
  "email": "john@example.com",
  "password": "Password123!"
}
```

#### Forgot Password

```json
POST /api/v1/auth/forgot-password
{
  "email": "john@example.com"
}
```

#### Reset Password

```json
POST /api/v1/auth/reset-password
{
  "token": "reset-token-from-email",
  "newPassword": "NewPassword123!",
  "confirmPassword": "NewPassword123!"
}
```

---

### Wallet

| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| GET | `/api/v1/wallet/balance` | Get wallet balance | Yes |
| POST | `/api/v1/wallet/fund` | Initialize wallet funding | Yes |
| GET | `/api/v1/wallet/verify/{reference}` | Verify and credit wallet | No |
| POST | `/api/v1/wallet/webhook/paystack` | Paystack webhook endpoint | No |

#### Fund Wallet

```json
POST /api/v1/wallet/fund
Authorization: Bearer {token}
{
  "amount": 5000
}
```

#### Response

```json
{
  "authorizationUrl": "https://checkout.paystack.com/xxx",
  "reference": "FUND-XXXXXXXXXXXXXXXX",
  "message": "Payment initialized. Complete payment at the authorization URL."
}
```

#### Wallet Funding Flow

```text
1. Call POST /api/v1/wallet/fund
2. Receive authorizationUrl from Paystack
3. User is redirected to Paystack checkout
4. Paystack redirects back to the frontend /bill-payment route
5. Frontend extracts the payment reference from the URL
6. Frontend calls GET /api/v1/wallet/verify/{reference}
7. Wallet balance updates automatically
```

#### Paystack test card

```text
Card Number : 4084 0840 8408 4081
Expiry      : 01/25
CVV         : 408
PIN         : 0000
OTP         : 123456
```

---

### Bill Payments

| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| POST | `/api/v1/bills/pay` | Pay any bill | Yes |

```json
POST /api/v1/bills/pay
Authorization: Bearer {token}
{
  "serviceID": "ikeja-electric",
  "billersCode": "1111111111111",
  "variationCode": "prepaid",
  "amount": 1000,
  "phone": "08012345678"
}
```

#### Supported Services

**Electricity**

| Provider | serviceID | Prepaid billersCode | Postpaid billersCode |
|---|---|---|---|
| Ikeja Electric | `ikeja-electric` | `1111111111111` | `1010101010101` |
| Eko Electric | `eko-electric` | `1111111111111` | `1010101010101` |
| Abuja Electric | `abuja-electric` | `1111111111111` | `1010101010101` |
| Benin Electric | `benin-electric` | `1111111111111` | `1010101010101` |
| Enugu Electric | `enugu-electric` | `1111111111111` | `1010101010101` |
| Ibadan Electric | `ibadan-electric` | `1111111111111` | `1010101010101` |
| Jos Electric | `jos-electric` | `1111111111111` | `1010101010101` |
| Kaduna Electric | `kaduna-electric` | `1111111111111` | `1010101010101` |
| Kano Electric | `kano-electric` | `1111111111111` | `1010101010101` |
| Port Harcourt Electric | `phed` | `1111111111111` | `1010101010101` |

`variationCode`: `prepaid` or `postpaid`

**Airtime**

| Network | serviceID | variationCode |
|---|---|---|
| MTN | `mtn` | `mtn-airtime-VTU` |
| Glo | `glo` | `glo-airtime-VTU` |
| Airtel | `airtel` | `airtel-airtime-VTU` |
| Etisalat | `etisalat` | `etisalat-airtime-VTU` |

`billersCode`: phone number to recharge

**Data**

| Network | serviceID |
|---|---|
| MTN | `mtn-data` |
| Glo | `glo-data` |
| Airtel | `airtel-data` |
| Etisalat | `etisalat-data` |

Get data plan variation codes:

```text
GET https://sandbox.vtpass.com/api/service-variations?serviceID=mtn-data
```

**Cable TV**

| Provider | serviceID | Test smartcard |
|---|---|---|
| DSTV | `dstv` | `1212121212` |
| GoTV | `gotv` | `1212121212` |
| Startimes | `startimes` | `1212121212` |

Get package variation codes:

```text
GET https://sandbox.vtpass.com/api/service-variations?serviceID=dstv
```

---

### Notifications

| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| GET | `/api/v1/notifications` | Get all notifications and unread count | Yes |
| GET | `/api/v1/notifications/unread` | Get unread notifications | Yes |
| POST | `/api/v1/notifications/{id}/read` | Mark a notification as read | Yes |
| POST | `/api/v1/notifications/read-all` | Mark all notifications as read | Yes |

---

### User / Profile

| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| GET | `/api/v1/user/profile` | Get current user profile | Yes |
| POST | `/api/v1/user/profile` | Update profile | Yes |
| POST | `/api/v1/user/change-password` | Change password | Yes |

`POST /api/v1/user/profile` supports partial updates:

- omitted fields are ignored
- blank values are rejected
- at least one field must be provided

---

### Transaction History

| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| GET | `/api/v1/transactions` | Get all transactions | Yes |
| GET | `/api/v1/transactions/filter?type={type}` | Filter by type | Yes |
| GET | `/api/v1/transactions/{referenceId}` | Get by reference | Yes |

Transaction types:

`WALLET_FUNDING`, `ELECTRICITY`, `AIRTIME`, `DATA`, `CABLE_TV`

#### Example Response

```json
{
  "id": 17,
  "type": "ELECTRICITY",
  "status": "SUCCESS",
  "amount": 1000.00,
  "referenceId": "ELECT-20260320215808-4468",
  "billerReferenceId": "ELECT-20260320215808-4468",
  "description": "ikeja-electric payment for 1111111111111",
  "details": "{\"token\":\"26362054405982757802\",\"units\":\"79.9 kWh\",\"tariff\":\"R2 SINGLE PHASE RESIDENTIAL\"}",
  "createdAt": "2026-03-20T21:58:08.857715"
}
```

---

## Authentication

All protected endpoints require a JWT token in the Authorization header:

```text
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

Tokens are obtained from the register or login endpoints and expire after 24 hours.

---

## How the Payment Flow Works

### Wallet Funding

```text
1. Call POST /api/v1/wallet/fund with amount
2. Receive Paystack authorization URL
3. User completes payment on Paystack
4. Paystack redirects back to the frontend bill-payment page
5. Frontend verifies the transaction using GET /api/v1/wallet/verify/{reference}
6. Wallet is credited automatically
```

### Bill Payment

```text
1. Ensure wallet has sufficient balance
2. Call POST /api/v1/bills/pay with service details
3. App deducts amount from wallet
4. App calls VTPass to deliver the service
5. On success, token or confirmation is returned
6. On failure, amount is refunded to wallet
```

### Password Reset

```text
1. User submits email to POST /api/v1/auth/forgot-password
2. App generates a secure reset token
3. Brevo sends the reset link to the user
4. User opens /reset-password?token=...
5. Frontend submits POST /api/v1/auth/reset-password
6. Password is updated and token is marked as used
```

---

## Database Schema

```text
users
|-- id (PK)
|-- first_name
|-- last_name
|-- email (unique)
|-- password (BCrypt encrypted)
|-- phone (unique)
|-- role
|-- enabled
|-- created_at
`-- updated_at

wallets
|-- id (PK)
|-- user_id (FK -> users)
|-- balance
|-- created_at
`-- updated_at

transactions
|-- id (PK)
|-- user_id (FK -> users)
|-- type (WALLET_FUNDING/ELECTRICITY/AIRTIME/DATA/CABLE_TV)
|-- status (PENDING/SUCCESS/FAILED)
|-- amount
|-- reference_id (unique)
|-- biller_reference_id
|-- description
|-- details (JSON)
`-- created_at

notifications
|-- id (PK)
|-- user_id (FK -> users)
|-- type
|-- title
|-- message
|-- is_read
|-- reference_id
`-- created_at

password_reset_tokens
|-- id (PK)
|-- user_id (FK -> users)
|-- token (unique)
|-- expires_at
|-- used
`-- created_at
```

---

## Running Tests in Postman

Test in this order:

1. Register a new user
2. Login and copy the JWT token
3. Request a password reset email
4. Reset password using the emailed token
5. Fund wallet and complete Paystack payment
6. Verify wallet balance update
7. Pay an electricity bill
8. Buy airtime
9. Subscribe to cable TV
10. Check transaction history
11. Read notifications

---

## Notes

- This project uses sandbox/test environments for both Paystack and VTPass
- No real money is processed in test mode
- Password reset emails are delivered through Brevo
- Paystack redirects back to the frontend after payment initialization
- Paystack webhook endpoint is available for server-side verification
- Swagger/OpenAPI is enabled in development and disabled in production
- VTPass sandbox requires specific test meter numbers
- Each bill payment `referenceId` must be unique

---

## Author

**Nnenna Ezidiegwu**

- GitHub: [@nne_nna](https://github.com/nne-nna)
- LinkedIn: [Ezidiegwu Nnenna](https://www.linkedin.com/in/nnenna-ezidiegwu-23404124b/)

---

## License

This project is open source.
