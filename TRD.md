# Technical Requirements Document (TRD) - XLMS Library Management System

## 1. System Architecture Overview
The XLMS application follows a decoupled three-tier system architecture:

```
[ Android Mobile Client ] ◄---(HTTP / JSON / JWT)---► [ Node.js/Express API ] ◄---(mssql driver)---► [ SQL Server DB ]
  - Retrofit 2 / OkHttp3                                - Session/Auth Middleware                     - Cloud Host (Somee)
  - ViewPager2 / View Canvas                            - Email (Gmail API)                          - Centralized Schema
```

### 1.1. Client Tier (Frontend)
- **Environment**: Android native (Java SDK).
- **Core Dependencies**: Retrofit 2 (network layer), OkHttp 3 (interceptor pipelines), Facebook Shimmer (placeholder loading animations).
- **Session Layer**: Android `SharedPreferences` managed via `SessionManager`.

### 1.2. Application Tier (Backend)
- **Environment**: Node.js v18+, Express.js v5.1.0.
- **Key Modules**: `cookie-parser` (session cookies), `jsonwebtoken` (token authentication), `bcrypt` (password salting), Google OAuth2 / Gmail API (OTP/ticket mailing pipelines).

### 1.3. Database Tier (Storage)
- **Environment**: Microsoft SQL Server.
- **Connection Engine**: Express server pool pooling via `mssql`. Hosted on Somee.com cloud platform.

---

## 2. Database Schema Design (MS SQL Server)

### 2.1. `users` Table
Stores user records and credentials.
```sql
CREATE TABLE users (
    User_id VARCHAR(50) PRIMARY KEY,
    User_Name NVARCHAR(100) NOT NULL,
    Email NVARCHAR(100) UNIQUE NOT NULL,
    Role VARCHAR(20) DEFAULT 'User', -- 'Admin', 'User'
    Membership_Type VARCHAR(50) DEFAULT 'English',
    Password VARCHAR(255) NOT NULL, -- bcrypt hashed
    Cost INT DEFAULT 0, -- accumulated active rental costs
    Status VARCHAR(20) DEFAULT 'Active' -- 'Active', 'Deactivated'
);
```

### 2.2. `books` Table
Maintains catalog data and inventory levels.
```sql
CREATE TABLE books (
    Book_ID VARCHAR(50) PRIMARY KEY,
    Book_Title NVARCHAR(255) NOT NULL,
    Author NVARCHAR(255) NOT NULL,
    Category NVARCHAR(100) NOT NULL,
    Language NVARCHAR(100) NOT NULL,
    Total_Copies INT NOT NULL,
    Status VARCHAR(30) NOT NULL, -- 'Available', 'Borrowed', 'Reserved', 'Out of stock'
    Pages INT NOT NULL,
    Price INT NOT NULL,
    Available INT NOT NULL -- current stock count on shelves
);
```

### 2.3. `borrower` Table
Logs book loans and payment statuses.
```sql
CREATE TABLE borrower (
    Borrower_ID INT IDENTITY(1,1) PRIMARY KEY,
    user_id VARCHAR(50) FOREIGN KEY REFERENCES users(User_id),
    Name NVARCHAR(100) NOT NULL,
    PhoneNumber VARCHAR(30) NULL,
    BookTitle NVARCHAR(255) NOT NULL,
    Author NVARCHAR(255) NOT NULL,
    Category NVARCHAR(100) NOT NULL,
    IssuedDate DATETIME NOT NULL,
    DueDate DATETIME NOT NULL,
    CopiesLent INT NOT NULL DEFAULT 1,
    FinePerDay INT NOT NULL DEFAULT 0,
    Price INT NOT NULL,
    Book_ID VARCHAR(50) FOREIGN KEY REFERENCES books(Book_ID),
    Status VARCHAR(30) DEFAULT 'not returned' -- 'not returned', 'Returned'
);
```

### 2.4. `reserved` Table
Manages queues for unavailable books.
```sql
CREATE TABLE reserved (
    Reservation_ID INT IDENTITY(1,1) PRIMARY KEY,
    User_ID VARCHAR(50) FOREIGN KEY REFERENCES users(User_id),
    Book_ID VARCHAR(50) FOREIGN KEY REFERENCES books(Book_ID),
    Reserved_Date DATE NOT NULL
);
```

### 2.5. `OTPS` Table
Stores temporary codes for validation.
```sql
CREATE TABLE OTPS (
    Email NVARCHAR(100) PRIMARY KEY,
    OTPCode VARCHAR(6) NOT NULL
);
```

### 2.6. `Resource` Table
Houses links for third-party documents.
```sql
CREATE TABLE Resource (
    ID INT IDENTITY(1,1) PRIMARY KEY,
    Name NVARCHAR(100) NOT NULL,
    Email NVARCHAR(100) NOT NULL,
    Website NVARCHAR(255) NOT NULL
);
```

---

## 3. JWT Authentication & Refresh Network Protocol
Access is governed via access tokens (duration: 1 hour) and refresh tokens.

```
[ Client Request ] ------- Bearer Token Header -------> [ Express Server app.js Middleware ]
                                                                   │
                                                      [ Check JWT Expiration ]
                                                                   │
        ┌──────────────────────────────────────────────────────────┴──────────────────────────┐
 [ Access Token Valid ]                                                              [ Near Expiry (< 5m) or Expired ]
        │                                                                                     │
    (next())                                                                     [ Validate refresh token ]
                                                                                              │
                                                                               ┌──────────────┴──────────────┐
                                                                           [ Valid ]                     [ Expired/Missing ]
                                                                              │                                 │
                                                                 [ Sign new Access Token ]               (Return 403 Forbidden)
                                                                 [ Set cookie & Header X-New-Token ]
                                                                              │
                                                                           (next())
```

### Frontend Interceptor Sync
The Client OkHttp client includes an interceptor matching:
1. Validates all response headers.
2. If the header `X-New-Token` is present, extracts the value.
3. Overwrites the outdated token inside `SessionManager` SharedPreferences.
4. Updates future Bearer requests with the new key without breaking the UI thread.

---

## 4. REST API Spec Mapping

| Endpoint Route | HTTP Method | Route Controller File | Unprotected? | Description |
|----------------|-------------|-----------------------|--------------|-------------|
| `/api/auth/login` | POST | `authController.js` | Yes | Verifies credentials, returns tokens/role. |
| `/api/auth/logout` | POST | `authController.js` | Yes | Invalidates and clears authorization cookies. |
| `/api/users/signup` | POST | `userController.js` | Yes | Self-registers client account. |
| `/api/users/exist` | POST | `userController.js` | Yes | Checks for unique email matches. |
| `/api/users/auth-users` | POST | `userController.js` | Yes | Validates credentials and existence check. |
| `/api/users/register` | POST | `userController.js` | No (Admin) | Manually creates a user in DB. |
| `/api/users/all` | POST | `userController.js` | No (Admin) | Retrieves all user records. |
| `/api/users/getbyid` | GET | `userController.js` | No | Retrieves profile matching JWT payload. |
| `/api/users/update` | POST | `userController.js` | No | Updates name, role, membership attributes. |
| `/api/users/activate` | POST | `userController.js` | No (Admin) | Activates a deactivated user. |
| `/api/users/deactivate`| POST | `userController.js` | No (Admin) | Deactivates an array of user IDs. |
| `/api/users/delete` | DELETE | `userController.js` | No | Deletes accounts in bulk. |
| `/api/users/changepassword` | PUT | `userController.js` | No | Modifies current password (bcrypt). |
| `/api/users/forgotpassword` | POST | `userController.js` | Yes | Emails OTP token to verify user. |
| `/api/users/resetpassword` | POST | `userController.js` | Yes | Updates password after OTP verification. |
| `/api/books/insert` | POST | `bookscontroller.js` | No (Admin) | Inserts new book, sets stock = Available. |
| `/api/books/get` | POST | `bookscontroller.js` | No | Fetches catalog database array. |
| `/api/books/getbyID` | POST | `bookscontroller.js` | No | Looks up singular book details. |
| `/api/books/update` | PUT | `bookscontroller.js` | No (Admin) | Updates metadata & stock (recalculates shelf availability). |
| `/api/books/delete` | DELETE | `bookscontroller.js` | No (Admin) | Deletes an array of books in bulk. |
| `/api/books/col` | POST | `bookscontroller.js` | No | Distinct query for dropdown fields. |
| `/api/books/lend` | POST | `bookscontroller.js` | No (Client)| Allows client self-checkout. |
| `/api/lenders/all` | GET | `lendersControllers.js`| No (Admin) | Lists all borrower lending files. |
| `/api/lenders/getlenderbyid` | POST | `lendersControllers.js`| No (Admin) | Gets lender info by Borrower_ID. |
| `/api/lenders/insert` | POST | `lendersControllers.js`| No (Admin) | Admin manual lending creation. |
| `/api/lenders/mylendings` | GET | `lendersControllers.js`| No (Client)| Retrieves user active lending lists. |
| `/api/lenders/return` | POST | `lendersControllers.js`| No (Client)| Initiates return, handles reservation checks. |
| `/api/mail/otp` | POST | `mails.js` | Yes | Sends initial validation code. |
| `/api/mail/verify` | POST | `mails.js` | Yes | Confirms OTP database match. |
| `/api/mail/resend` | POST | `mails.js` | Yes | Flushes old records, resends OTP. |
| `/api/mail/issue-mail`| POST | `mails.js` | No (Client)| Dispatches support request emails. |
| `/api/reservations/reserve` | POST | `reservationController.js`| No (Client)| Places a hold on a borrowed book. |
| `/api/reservations/myreservations`| GET| `reservationController.js`| No (Client)| Returns client reservations array. |
| `/api/resource/add` | POST | `resourcecontroller.js` | No (Admin) | Registers resource links. |
| `/api/other/getbookdata` | GET | `other.js` | No (Admin) | Aggregated counters. |
| `/api/other/mystats` | GET | `other.js` | No (Client)| Active metrics totals for users. |
| `/api/other/chartdetails` | GET | `other.js` | No (Client)| Compiles ratio stats. |
| `/api/other/lendingactivity`| GET | `other.js` | No (Client)| Compiles 12-month activity counts. |

---

## 5. Frontend Custom Views & Renderings
The application implements native drawing routines for charts, bypassing external dependencies:

### 5.1. `PieChartView` (Admin UI Canvas Drawing)
- **Base Class**: `android.view.View`
- **Render Engine**: Canvas path drawing inside `onDraw(Canvas canvas)`.
- Calculates angles: $\theta_i = (\text{value}_i / \text{total}) \times 360$.
- Uses `canvas.drawArc(rectF, startAngle, sweepAngle, true, paint)` to draw wedges.
- Applies standard paint configs with anti-aliasing to draw descriptions and legends.

### 5.2. `StackedAreaChartView` (Admin UI Canvas Drawing)
- **Base Class**: `android.view.View`
- Renders 12-month trends utilizing two filled vector spaces (Orange/Green colors).
- Loops through data inputs, maps coordinate values to scale limits inside the device viewport, and uses `Path.lineTo()` and `Path.close()` to draw regions.
- Draws path areas with transluscent linear shader gradients: `new LinearGradient(...)`.

### 5.3. Client Bar Chart UI
- Built programmatically inside `ClientDashboardContentFragment` using native `LinearLayout` blocks.
- Columns are inflated as `View` elements, where layout height is dynamically bound as a percentage of the screen space: `(monthlyValue * scaleFactor)`.
- Background layers apply pre-defined colors based on peak values.

---

## 6. Key Operations & DB Transactions

### 6.1. Return Book & Reservation Check Lifecycle
The endpoint `/api/lenders/return` manages several database mutations:
1. Updates the borrower record: `UPDATE borrower SET Status = 'Returned' WHERE ...`.
2. Computes the duration of the loan and subtracts it from the user's outstanding balance:
   $$\text{New Cost} = \text{Current Cost} - (\text{Price} \times \text{Copies} \times \text{Days Borrowed})$$
3. Checks if any client holds a reservation: `SELECT TOP 1 * FROM reserved WHERE Book_ID = @Book_ID ORDER BY Reservation_ID ASC`.
4. **Branch A (No reservations)**:
   - Increments book availability: `UPDATE books SET Available = Available + Copies, Status = 'Available'`.
5. **Branch B (Reservation matches)**:
   - Fetches the reservation and inserts a borrower record for the new reserver.
   - Deletes the reservation log: `DELETE FROM reserved WHERE Reservation_ID = @Res_ID`.
   - Modifies book status: `UPDATE books SET Available = Available + Copies - 1, Status = 'Available'`.
   - Dispatches a push notification to the new borrower.
