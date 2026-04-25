# Features

## Backend Features (Fully Implemented)

### Authentication
- Admin-only login with email + password + API key.
- JWT token generation (1h expiry) via HTTP-only cookie + returned in response.
- Role enforcement (Admin only for admin routes).
- Account status check (Deactivated accounts blocked).
- Logout clears session/cookie.
- Password change (verifies old password via bcrypt).

### User Management
- Create user with auto-generated UUID-based `User_id`.
- bcrypt password hashing (salt rounds: 10).
- Get all users / get user by ID.
- Update user details (name, email, role, membership type).
- Batch deactivate / single activate.
- Batch delete accounts.

### Book Management
- Full CRUD for books.
- Auto-generates UUID `Book_ID`.
- On update: adjusts `Available` count when `Total_Copies` changes.
- Batch delete by array of IDs.
- Status auto-set to "Out of stock" when Available = 0.

### Lending / Borrowing System
- Get all lenders / get lender by ID.
- **Core lending operation**:
  1. Validates book availability.
  2. Decrements available copies.
  3. Updates user cost.
  4. Inserts into `borrower` table with "Not Returned" status.
  5. Sends detailed email notification (text + HTML).

### Email & Notifications
- OTP-based and Token-based password resets.
- Gmail API via OAuth2 for system emails.
- In-app notifications with auto-deletion of old read records.

---

## Frontend Features (Integration Progress)

### Authentication (✅ Fully Integrated)
- **Login**: Authenticates against backend, saves JWT and User info in `SessionManager`.
- **Sign Up**: 3-step flow (Register -> OTP Verification -> Success) fully wired to API.
- **Session**: Automatically handles token injection via `ApiClient` interceptor.

### Dashboard Stats (✅ Fully Integrated)
- Fetches real-time counts for Total Books, Users, Borrowers, and Available/Overdue books.
- Programmatically updates the home tab cards.

### Notifications (✅ Fully Integrated)
- Fetches unread notifications for the logged-in user.
- Displays them in the Notifications tab and the quick-access PopupWindow.

### Manage Books (⚠️ Partial - UI Ready)
- UI built with search and filtering.
- **Integration Pending**: Currently loads dummy data. Needs to use `/api/books/get`.

### Members Tab (⚠️ Partial - UI Ready)
- UI built with status filtering.
- **Integration Pending**: Currently loads dummy data. Needs to use `/api/users/all`.

### Lending System (⚠️ Partial - UI Ready)
- `LendBookFragment` and `LendedBooksFragment` UIs are complete.
- **Integration Pending**: Needs to be wired to `/api/lenders/insert`.

### Profile (⚠️ Partial - UI Ready)
- UI for profile view and password change complete.
- **Integration Pending**: Needs to be wired to `/api/users/changepassword`.

### Client Dashboard (🎨 UI Only)
- Visual prototype with drawer navigation and metric cards.
- Not yet connected to backend APIs.
