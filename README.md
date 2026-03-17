# Bill Payment System

A Spring Boot REST API for bill payments including electricity, airtime, data and cable TV.

## Tech Stack
- Java 21
- Spring Boot 4.0.3
- Spring Security + JWT
- PostgreSQL
- Paystack API (wallet funding)
- VTPass API (bill payments)

## Features
- User registration and login with JWT authentication
- Wallet system
- Fund wallet via Paystack
- Pay electricity bills
- Buy airtime and data
- Cable TV subscription
- Transaction history

## Setup
1. Clone the repository
2. Create a PostgreSQL database named `bill_payment_db`
3. Create `application-dev.properties` in `src/main/resources` with your credentials
4. Run the application

## API Endpoints
### Auth
- POST `/api/v1/auth/register` - Register a new user
- POST `/api/v1/auth/login` - Login

### Wallet
- GET `/api/v1/wallet/balance` - Get wallet balance
- POST `/api/v1/wallet/fund` - Fund wallet via Paystack
- GET `/api/v1/wallet/verify/{reference}` - Verify payment