# Features

## Backend Features (Fully Implemented)

### Authentication
- Admin-only login with email + password + API key
- JWT token generation (1h expiry) via HTTP-only cookie
- Role enforcement (Admin only)
- Account status check (Deactivated blocked)
- Logout clears cookie
- Password change (verifies old password via bcrypt)

### User Management
- Create user with auto-generated UUID-based `User_id` (first letter + 8 random chars)
- bcrypt password hashing (salt rounds: 10)
- Get all users / get user by ID / get member by ID
- Update user details (name, email, role, membership type)
- Batch deactivate / single activate
- Batch delete accounts
- API key validation on specific operations (Create, List)

### Book Management
- Full CRUD for books
- Auto-generates UUID `Book_ID`
- On update: adjusts `Available` count when `Total_Copies` changes
- Batch delete by array of IDs
- Dynamic column selection query
- Status auto-set to "Out of stock" when Available = 0
- API key required for Insert and Get All (list)

### Lending / Borrowing System
- Get all lenders (API key required) / get lender by ID (no API key required)
- **Core lending operation** (`lendersControllers.addbook`):
  1. Checks if user exists by email; creates account if not
  2. Validates book availability
  3. Decrements available copies
  4. Updates user cost (Price * Copies)
  5. Inserts into `borrower` table with "Not Returned" status
  6. Generates password reset token for new users
  7. Sends detailed email notification (text + HTML) with login link
  8. New users get password change link for security
  9. **No API key required** for this operation

### Password Reset Flow
- **OTP-based** (via `/api/mail/*`):
  1. Generate 6-digit OTP → store in `OPPS` table
  2. Send email with HTML template + OTP
  3. Auto-delete OTP after 3 minutes
  4. Verify OTP
  5. Reset password (bcrypt hash)
  6. Resend OTP (replaces old one)

- **Token-based** (via `/api/token/*`):
  1. Generate UUID reset token → store in `PasswordResetTokens`
  2. 15-minute expiry (PKT timezone)
  3. Reset link: `https://xlms-admin.netlify.app/reset-password?token=...`
  4. Verify token → return user info
  5. Update password → delete used token

### Email Service
- Gmail API via OAuth2 (CLIENT_ID, CLIENT_SECRET, REDIRECT_URI, REFRESH_TOKEN)
- Handles token refresh on `invalid_grant` errors
- Sends raw MIME messages (base64 encoded)
- Both text and HTML email templates

### Notifications
- Add notification (PKT timezone, auto-deletes old read notifications)
- Get unread notifications for current user (from JWT)
- Mark single notification as read
- Mark all notifications as read

### Dashboard Statistics
- Returns: TotalBooks, TotalUsers, TotalBorrowers, AvailableBooks, OverdueBooks
- Queries `books`, `users`, `borrower` tables

### Resources
- Add external resource (Name, Email, Website) to `Resource` table

---

## Frontend Features (UI Only — All Dummy Data)

### Splash Screen
- 2.5s animated splash with progress bar sweep
- Auto-navigates based on session (Admin/Client/Login)

### Login
- **Uses hardcoded credentials** (NOT backend API):
  - Admin: `admin@xlms.com` / `admin123`
  - User: `user@xlms.com` / `user123`
- Password visibility toggle
- "Remember Me" switch
- Saves session to SharedPreferences

### Admin Dashboard
- ViewPager2 with 5 tabs
- BottomNavigationView for tab switching
- Slide-out bottom sheet (navigation drawer) with 7 links
- Notification PopupWindow (anchored to bell icon)
- Fragment back stack for detail screens
- Custom slide animations between fragments
- Logout clears session → back to login

### Dashboard Home Tab
- Hardcoded stats: 29 books, 74 lended, 29 available, 12 users, 0 overdue
- Programmatically generated bar chart
- Activity feed (dummy)

### Manage Books Tab
- RecyclerView with 7 hardcoded dummy books
- Search filtering
- Category filter dialog
- Status filter dialog
- Tap book → opens BookInfoFragment
- Add book button → AddBookFragment

### Members Tab
- RecyclerView with 6 hardcoded dummy members
- Search filtering
- Filter by Active / Deactivated / All
- Tap member → opens UserInfoFragment
- Add user button → AddUserFragment

### Notifications Tab
- RecyclerView with 3 hardcoded notifications (Warning, Info, Success)
- Type badges with color coding
- Clear all button
- Refresh button

### Profile Tab
- Hardcoded user "Dr. Julian Vane"
- Password change form with validation
- Forgot password → ForgotPasswordFragment

### Detail Screens
- **BookInfoFragment**: Full book details, current date, "Lend This Book" button
- **LendBookFragment**: Lending form (name, email, due date, copies, fine), spinners for book selection
- **UserInfoFragment**: View/edit mode toggle, password read-only, delete user confirmation, activate/deactivate toggle
- **LendedBooksFragment**: Lent books list, filter by Returned/Not Returned/All, search
- **AddBookFragment**: Form with spinners (Category, Language, Status), UUID book ID generation
- **AddUserFragment**: Form with name, email, password, role radio buttons, membership spinners
- **ForgotPasswordFragment**: Email input for password reset
- **ResourcesFragment**: Stub with "coming soon" toast

### Sign Up Flow (3-Step)
1. **SignUpFragment**: Name, email, password, terms checkbox
2. **EmailVerificationFragment**: 6-digit OTP, auto-focus, 10-min countdown timer, resend
3. **SignUpSuccessFragment**: Success confirmation → goes to login

### Client Dashboard (UI Built — Dummy Data)
- DrawerLayout + NavigationView (slide-out sidebar from left)
- AppBarLayout with hamburger menu, "XLMS LIBRARY" title, notification bell (red dot), circular profile avatar
- BottomNavigationView with 6 tabs: Dashboard, Catalog, Account, Search (FAB style), Help, Exit
- Welcome section: "Hello User" greeting + Library Hours card
- 3 metric cards (horizontal): Lended (blue), Overdue (red), Reserved (gold)
- Borrowed Books panel: circular progress indicator + Good Standing % + Overdue/Returned stats row
- Lending Activity panel: 12-month bar chart (Jan-Dec) with dynamic heights, peak month highlighted
- Sidebar drawer: profile header (avatar + name + "Lead Researcher"), 5 nav items, logout button
- All data hardcoded; ready for backend integration

---

## Not Yet Implemented (Frontend → Backend Integration)
- ❌ HTTP client (no Retrofit, Volley, OkHttp)
- ❌ API calls for login
- ❌ API calls for any CRUD operations
- ❌ Real-time notifications
- ❌ Actual password reset flow
- ❌ Email verification integration
- ❌ Client dashboard API integration (Catalog, Account, Search, Borrow History screens)
- ❌ Client bottom nav tabs beyond Dashboard (Catalog, Account, Search, Help all show "coming soon" toast)
- ❌ Client drawer nav items (Catalog, Account, Help, Notifications all show "coming soon" toast)
