# 💳 Bill Payment System

A production-ready **fintech REST API** built with Java and Spring Boot that enables users to fund a wallet and pay utility bills including electricity, airtime, data and cable TV subscriptions across major Nigerian providers.

---

## 🚀 Live Features

- **JWT Authentication** — secure registration and login
- **Wallet System** — fund wallet via Paystack payment gateway
- **Electricity Payments** — 10 major providers across Nigeria
- **Airtime & Data** — all major networks (MTN, Glo, Airtel, Etisalat)
- **Cable TV** — DSTV, GoTV, Startimes subscriptions
- **Transaction History** — full audit trail with filtering

---

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| Java 21 | Programming language |
| Spring Boot 4.0.3 | Backend framework |
| Spring Security + JWT | Authentication & authorization |
| Spring Data JPA | Database ORM |
| PostgreSQL | Relational database |
| Paystack API | Wallet funding / payment processing |
| VTPass API | Bill payments & utility vending |
| Unirest | HTTP client for external API calls |
| Gson | JSON parsing |
| Lombok | Boilerplate reduction |
| Maven | Dependency management |

---

## 📐 Architecture

```
Client (Postman / Frontend)
        ↓
Spring Boot REST API
    ├── AuthController
    ├── WalletController
    ├── BillPaymentController
    └── TransactionController
        ↓
Service Layer (Business Logic)
    ├── AuthService
    ├── WalletService      → Paystack API
    ├── BillPaymentService → VTPass API
    └── TransactionService
        ↓
Repository Layer (JPA)
        ↓
PostgreSQL Database
```

---

## 📁 Project Structure

```
src/main/java/com/billpayments/
│
├── controller/
│   ├── AuthController.java
│   ├── WalletController.java
│   ├── BillPaymentController.java
│   └── TransactionController.java
│
├── models/
│   ├── User.java
│   ├── Wallet.java
│   └── Transaction.java
│
├── payload/
│   ├── request/
│   │   ├── RegisterRequest.java
│   │   ├── LoginRequest.java
│   │   ├── FundWalletRequest.java
│   │   └── BillPaymentRequest.java
│   └── response/
│       ├── AuthResponse.java
│       ├── WalletResponse.java
│       ├── PaystackInitResponse.java
│       ├── BillPaymentResponse.java
│       └── TransactionResponse.java
│
├── service/
│   ├── AuthService.java
│   ├── WalletService.java
│   ├── BillPaymentService.java
│   ├── TransactionService.java
│   └── impl/
│       ├── AuthServiceImpl.java
│       ├── WalletServiceImpl.java
│       ├── BillPaymentServiceImpl.java
│       └── TransactionServiceImpl.java
│
├── repository/
│   ├── UserRepository.java
│   ├── WalletRepository.java
│   └── TransactionRepository.java
│
├── security/
│   ├── JwtUtil.java
│   ├── JwtFilter.java
│   ├── SecurityConfig.java
│   └── UserDetailsServiceImpl.java
│
├── enums/
│   ├── Role.java
│   ├── TransactionType.java
│   ├── TransactionStatus.java
│   └── ServiceProvider.java
│
├── exceptions/
│   ├── BadRequestException.java
│   ├── ResourceNotFoundException.java
│   └── GlobalExceptionHandler.java
│
└── utils/
    └── AppConfig.java
```

---

## ⚙️ Setup & Installation

### Prerequisites
- Java 21
- Maven
- PostgreSQL

### 1. Clone the repository
```bash
git clone https://github.com/nne-nna/bill-payment-system
cd bill-payment-system
```

### 2. Create the database
```sql
CREATE DATABASE bill_payment_db;
```

### 3. Configure environment
Create `src/main/resources/application-dev.properties` using the example file:
```bash
cp src/main/resources/application-dev.properties.example src/main/resources/application-dev.properties
```

Then fill in your credentials:
```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/bill_payment_db
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD

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
```

### 4. Get your API keys
- **Paystack** — sign up at [paystack.com](https://paystack.com) → Settings → API Keys → copy test secret key
- **VTPass** — sign up at [vtpass.com](https://vtpass.com) → sandbox dashboard → API Keys section

### 5. Run the application
```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

---

## 📡 API Endpoints

### 🔐 Authentication
| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| POST | `/api/v1/auth/register` | Register new user | No |
| POST | `/api/v1/auth/login` | Login | No |

#### Register
```json
POST /api/v1/auth/register
{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "password": "password123",
    "phone": "08012345678"
}
```

#### Login
```json
POST /api/v1/auth/login
{
    "email": "john@example.com",
    "password": "password123"
}
```

---

### 💰 Wallet
| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| GET | `/api/v1/wallet/balance` | Get wallet balance | Yes |
| POST | `/api/v1/wallet/fund` | Initialize wallet funding | Yes |
| GET | `/api/v1/wallet/verify/{reference}` | Verify & credit wallet | Yes |

#### Fund Wallet
```json
POST /api/v1/wallet/fund
Authorization: Bearer {token}
{
    "amount": 5000
}
```

**Response:**
```json
{
    "authorizationUrl": "https://checkout.paystack.com/xxx",
    "reference": "FUND-XXXXXXXXXXXXXXXX",
    "message": "Payment initialized. Complete payment at the authorization URL."
}
```

Open `authorizationUrl` in browser → complete payment → call verify endpoint with the reference.

**Paystack test card:**
```
Card Number : 4084 0840 8408 4081
Expiry      : 01/25
CVV         : 408
PIN         : 0000
OTP         : 123456
```

---

### ⚡ Bill Payments
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

**variationCode:** `prepaid` or `postpaid`

---

**Airtime**
| Network | serviceID | variationCode |
|---|---|---|
| MTN | `mtn` | `mtn-airtime-VTU` |
| Glo | `glo` | `glo-airtime-VTU` |
| Airtel | `airtel` | `airtel-airtime-VTU` |
| Etisalat | `etisalat` | `etisalat-airtime-VTU` |

**billersCode:** phone number to recharge

---

**Data**
| Network | serviceID |
|---|---|
| MTN | `mtn-data` |
| Glo | `glo-data` |
| Airtel | `airtel-data` |
| Etisalat | `etisalat-data` |

Get available data bundles (variation codes):
```
GET https://sandbox.vtpass.com/api/service-variations?serviceID=mtn-data
```

---

**Cable TV**
| Provider | serviceID | Test smartcard |
|---|---|---|
| DSTV | `dstv` | `1212121212` |
| GoTV | `gotv` | `1212121212` |
| Startimes | `startimes` | `1212121212` |

Get available packages (variation codes):
```
GET https://sandbox.vtpass.com/api/service-variations?serviceID=dstv
```

---

### 📋 Transaction History
| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| GET | `/api/v1/transactions` | Get all transactions | Yes |
| GET | `/api/v1/transactions/filter?type={type}` | Filter by type | Yes |
| GET | `/api/v1/transactions/{referenceId}` | Get by reference | Yes |

**Transaction types:** `WALLET_FUNDING`, `ELECTRICITY`, `AIRTIME`, `DATA`, `CABLE_TV`

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

## 🔒 Authentication

All protected endpoints require a JWT token in the Authorization header:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

Tokens are obtained from the register or login endpoints and expire after **24 hours**.

---

## 💡 How the Payment Flow Works

### Wallet Funding
```
1. Call POST /wallet/fund with amount
2. Receive Paystack authorization URL
3. Open URL in browser and complete payment
4. Call GET /wallet/verify/{reference}
5. Wallet is credited automatically
```

### Bill Payment
```
1. Ensure wallet has sufficient balance
2. Call POST /bills/pay with service details
3. App deducts amount from wallet
4. App calls VTPass to deliver the service
5. On success → token/confirmation returned
6. On failure → amount automatically refunded to wallet
```

---

## 🗄️ Database Schema

```
users
├── id (PK)
├── first_name
├── last_name
├── email (unique)
├── password (BCrypt encrypted)
├── phone (unique)
├── role
├── enabled
├── created_at
└── updated_at

wallets
├── id (PK)
├── user_id (FK → users)
├── balance
├── created_at
└── updated_at

transactions
├── id (PK)
├── user_id (FK → users)
├── type (WALLET_FUNDING/ELECTRICITY/AIRTIME/DATA/CABLE_TV)
├── status (PENDING/SUCCESS/FAILED)
├── amount
├── reference_id (unique)
├── biller_reference_id
├── description
├── details (JSON)
└── created_at
```

---

## 🧪 Running Tests in Postman

Import the collection or manually test endpoints in this order:

1. Register a new user
2. Login and copy the JWT token
3. Fund wallet → complete Paystack payment → verify
4. Pay an electricity bill
5. Buy airtime
6. Subscribe to cable TV
7. Check transaction history

---

## 📝 Notes

- This project uses **sandbox/test environments** for both Paystack and VTPass
- No real money is processed in test mode
- VTPass sandbox requires specific test meter numbers (see Supported Services above)
- Each bill payment `referenceId` must be unique — duplicate references are rejected

---

## 👤 Author

**Nnenna Ezidiegwu**
- GitHub: [@nne_nna](https://github.com/nne-nna)
- LinkedIn: [Ezidiegwu Nnenna](https://www.linkedin.com/in/nnenna-ezidiegwu-23404124b/)

---

## 📄 License

This project is open source.