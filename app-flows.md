# Application Flows Spec

This document details the critical sequence flows, client-server API contracts, and database modifications across all main features of the XLMS application.

---

## 1. Authentication & Session Routing Flow
Determines user session states during launch and routes users to the corresponding portal interface.

```mermaid
sequenceDiagram
    autonumber
    participant App as SplashActivity
    participant SM as SessionManager
    participant API as ApiService /auth-users
    participant Main as DashboardActivity

    App->>SM: check isLoggedIn()
    alt Not Logged In
        App->>Main: Launch LoginActivity
    else Logged In
        App->>API: Post Verification (Cookie/JWT)
        alt Session Valid (Role ADMIN)
            API-->>App: Status Success (Role = ADMIN)
            App->>Main: Launch AdminDashboardActivity
        else Session Valid (Role CLIENT)
            API-->>App: Status Success (Role = CLIENT)
            App->>Main: Launch ClientDashboardActivity
        else Session Invalid
            API-->>App: Status 401 Unauthorized
            App->>SM: clearSession()
            App->>Main: Launch LoginActivity
        end
    end
```

---

## 2. Member Signup & OTP Verification Flow
Enforces email validation before allowing users to register as Clients.

```mermaid
sequenceDiagram
    autonumber
    participant Client as SignupActivity
    participant Server as userController.js
    participant DB as MS SQL Server
    participant SMTP as Gmail API Mailer

    Client->>Server: POST /users/exist { Email }
    Server->>DB: Check if Email exists
    DB-->>Server: Count matching records
    alt Email Exists
        Server-->>Client: Response: { exists: true }
        Note over Client: Block registration
    else Email Unique
        Server-->>Client: Response: { exists: false }
        Client->>Server: POST /mail/otp { Name, Email }
        Note over Server: Generate 6-digit numeric OTP
        Server->>DB: INSERT INTO OTPS (Email, OTPCode)
        Server->>SMTP: Dispatch email with verification template
        Server-->>Client: Response: OTP sent successfully
        Note over Client: Open Verification Popup (3 min expiry)
        Client->>Server: POST /mail/verify { Email, OTP }
        Server->>DB: SELECT * FROM OTPS WHERE Email & OTPCode
        alt OTP Invalid / Expired
            DB-->>Server: Zero matches
            Server-->>Client: Response: Invalid OTP (status 400)
        else OTP Valid
            DB-->>Server: Matching record
            Server->>DB: DELETE FROM OTPS WHERE Email
            Server-->>Client: Response: OTP verified successfully (status 200)
            Client->>Server: POST /users/signup { User_Name, Email, Password }
            Server->>DB: INSERT INTO users (Active)
            Server-->>Client: Response: Signup successful
        end
    end
```

---

## 3. Book Rental/Lending Flows

### 3.1. Client Self-Checkout
Allows users to borrow items directly from the catalog.

```mermaid
sequenceDiagram
    autonumber
    participant App as CheckoutFragment
    participant Server as bookscontroller.js
    participant DB as MS SQL Server

    App->>Server: POST /books/lend { book_id, IssuedDate, DueDate, CopiesLent, FinePerDay }
    Server->>DB: SELECT Available, Price, Book_Title FROM books
    DB-->>Server: Returns book details
    Server->>DB: INSERT INTO borrower (Status = 'not returned')
    Server->>DB: UPDATE books SET Available = Available - CopiesLent
    Note over Server: Calculate rental charge (Price * Copies * Days)
    Server->>DB: UPDATE users SET Cost = Cost + rental charge
    Server->>DB: INSERT INTO notifications (Notification message)
    Server-->>App: Response: Book lent successfully
```

### 3.2. Admin Manual Lending
Enables administrators to assign books to users (creating guest accounts if necessary).
- Admin posts payload to `/api/lenders/insert`.
- Backend checks if user exists in database by email:
  - **New User**: Auto-generates a `User_id`, hashes a secure default password, registers user as "Active", and calculates lending charges. Dispatches account credentials and verification links via SMTP.
  - **Existing User**: Retrieves `User_id`, checks if account is "Active", and updates their cost balance.
- Backend decrements book availability stock and saves the loan log.

---

## 4. Book Return & Reservation Allocation Flow
Recalculates balances, checks pending reserves, and implements FIFO queue hand-offs.

```mermaid
sequenceDiagram
    autonumber
    participant App as ClientAccountFragment
    participant Server as lendersControllers.js
    participant DB as MS SQL Server

    App->>Server: POST /lenders/return { book_id, borrower_id }
    Server->>DB: UPDATE borrower SET Status = 'Returned'
    Server->>DB: SELECT Cost FROM users WHERE User_id
    Server->>DB: SELECT IssuedDate, DueDate, Price, CopiesLent FROM borrower
    Note over Server: Compute loan days & refund active balance
    Server->>DB: UPDATE users SET Cost = Cost - (Price * Copies * Days)
    Server->>DB: SELECT TOP 1 * FROM reserved WHERE Book_ID ORDER BY Reservation_ID ASC
    alt Reservation Queue Empty
        DB-->>Server: No reservations
        Server->>DB: UPDATE books SET Available = Available + Copies, Status = 'Available'
        Server->>DB: INSERT INTO notifications (Return log)
    else Reservation Queue Active
        DB-->>Server: Returns oldest Reservation (User_ID = Reserver_ID)
        Server->>DB: INSERT INTO borrower (User_ID = Reserver_ID, Status = 'not returned')
        Server->>DB: DELETE FROM reserved WHERE Reservation_ID
        Server->>DB: UPDATE books SET Available = Available + Copies - 1
        Server->>DB: INSERT INTO notifications (Reserver notification)
    end
    Server-->>App: Response: Book returned successfully
```

---

## 5. Book Reservation Flow
Enables users to reserve currently checked-out materials.
1. Client selects out-of-stock item and submits a reserve request.
2. App sends a POST request to `/api/reservations/reserve` containing `{ book_id, reservation_date }`.
3. Backend processes the request:
   - Registers reservation entry: `INSERT INTO reserved (User_ID, Book_ID, Reserved_Date)`.
   - Modifies book status: `UPDATE books SET Status = 'Reserved' WHERE Book_ID = @Book_ID`.
   - Generates a confirmation notification: `INSERT INTO notifications`.
   - Sends a confirmation email to the client.

---

## 6. JWT Token Auto-Refresh Network Flow
Handles JWT validations and silent updates on the client.

```mermaid
sequenceDiagram
    autonumber
    participant App as ApiClient (OkHttp)
    participant Mid as app.js Middleware
    participant Server as Router
    
    App->>Mid: API request with expired/near-expiry token
    Note over Mid: Check decoded.exp vs Current Time
    alt Token expires in > 5 mins
        Mid->>Server: Authenticated, proceed to router (next())
    else Token expires in < 5 mins OR Expired
        Note over Mid: Check refreshToken (Cookie/Header)
        alt Refresh Token Valid
            Mid->>Mid: Generate new Access Token (1h duration)
            Mid->>App: Set Cookie & Header X-New-Token = new token
            Mid->>Server: Authenticated, proceed to router (next())
            Note over App: OkHttp Interceptor detects X-New-Token
            Note over App: Updates SessionManager SharedPreferences
        else Refresh Token Expired / Invalid
            Mid-->>App: Response: Invalid or expired token (status 403)
            Note over App: Redirect to LoginActivity
        end
    end
```
