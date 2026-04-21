# Architecture

## Backend Architecture

### Entry Point (`server.js`)
- Express app on configurable port (default 5000)
- CORS with dynamic origins from `URL` env variable
- Middleware: `cookie-parser`, `express.json()`, global JWT auth
- **Unprotected routes**: `/api/auth/login`, `/api/auth/logout`, `/api/token/verify`, `/api/token/update`, `/`
- All other routes automatically protected by JWT middleware

### Authentication Flow
1. **Login** → POST `/api/auth/login` with `{email, password, API}`
2. Validates API key against `XLMS_API` env
3. Queries `Users` table by email
4. Verifies bcrypt password hash
5. Checks role = "Admin" and status ≠ "Deactivated"
6. Generates JWT (1h expiry, payload: `{id, email}`)
7. Sets HTTP-only cookie (`sameSite: "None"`, `secure: true`)
8. **Logout** clears the cookie

### Database Layer (`models/db.js`)
- SQL Server connection pool via `mssql`
- Pool config: max 10, min 0, idle timeout 30s
- Encryption enabled with `trustServerCertificate: true`
- Hosted on Somee.com cloud
- Exports `sql` object and `poolPromise`

### JWT Middleware (`middleware/app.js`)
- Extracts `token` from HTTP-only cookie
- Verifies against `JWT` env secret
- Decodes `{id, email}` into `req.user`
- Returns 401 if no token, 403 if invalid/expired

### Key Architectural Pattern
- **Backend**: Classic MVC — routes → controllers → models (SQL pool)
- **Frontend**: Activity → ViewPager2 (Admin) / DrawerLayout (Client) → Fragments
- **Global auth middleware**: All routes protected by default, except login/logout/token routes
- **API key gate**: Inconsistent enforcement. Some endpoints (login, book/user/lender list, book insert, user register) require `XLMS_API` key in request body, while many other write/read operations do not.

| Controller | Purpose | Key Operations |
|------------|---------|---------------|
| `authController` | Login/logout | JWT generation, role check, bcrypt verification |
| `bookscontroller` | Book CRUD | Insert, get all, get by ID, update (adjusts available copies), batch delete, column query |
| `lendersControllers` | Lending mgmt | Get lenders, get by ID, **lend book** (checks availability, creates user if needed, sends email) |
| `userController` | User mgmt | Create, get all, get by ID, update, activate/deactivate, batch delete, change password |
| `notificationscontroller` | In-app notifications | Add (auto-deletes old read), get unread, mark read (single or all) |
| `other` | Dashboard stats | Returns TotalBooks, TotalUsers, TotalBorrowers, AvailableBooks, OverdueBooks |
| `mails` | Password reset OTP | Generate 6-digit OTP, store in DB, send email, verify, reset password |
| `mailer` | Email service | Gmail API OAuth2, sends raw MIME messages |
| `tokengenerator` | Reset token | UUID token, 15-min expiry, returns reset link |
| `tokencontroller` | Token verify/update | Verify token exists, update password, delete used token |
| `resourcecontroller` | External resources | Add Name/Email/Website to Resource table |
| `sendEmail` | Generic email sender | Wraps mailer for general use |

### API Routes

| Route Prefix | Path | Methods | Auth Required |
|-------------|------|---------|--------------|
| `/api/auth` | `/login` | POST | No |
| `/api/auth` | `/logout` | POST | No |
| `/api/users` | `/register` | POST | Yes |
| `/api/users` | `/all` | POST | Yes |
| `/api/users` | `/getbyid` | GET | Yes |
| `/api/users` | `/getmemberbyid` | POST | Yes |
| `/api/users` | `/update` | POST | Yes |
| `/api/users` | `/deactivate` | POST | Yes |
| `/api/users` | `/activate` | POST | Yes |
| `/api/users` | `/delete` | DELETE | Yes |
| `/api/users` | `/changepassword` | PUT | Yes |
| `/api/books` | `/insert` | POST | Yes |
| `/api/books` | `/get` | POST | Yes |
| `/api/books` | `/getbyID` | POST | Yes |
| `/api/books` | `/update` | PUT | Yes |
| `/api/books` | `/delete` | DELETE | Yes |
| `/api/books` | `/col` | POST | Yes |
| `/api/lenders` | `/all` | POST | Yes |
| `/api/lenders` | `/getlenderbyid` | POST | Yes |
| `/api/lenders` | `/insert` | POST | Yes |
| `/api/token` | `/verify` | POST | No |
| `/api/token` | `/update` | PUT | No |
| `/api/mail` | `/otp` | POST | Yes |
| `/api/mail` | `/verify` | POST | Yes |
| `/api/mail` | `/resend` | POST | Yes |
| `/api/mail` | `/reset` | POST | Yes |
| `/api/notifications` | `/add` | POST | Yes |
| `/api/notifications` | `/get` | GET | Yes |
| `/api/notifications` | `/markasread` | POST | Yes |
| `/api/other` | `/getbookdata` | GET | Yes |
| `/api/resource` | `/add` | POST | Yes |
| `/send-email` | `/send-email` | POST | Yes |

## Frontend Architecture

### Navigation Flow
```
SplashActivity (2.5s) → SessionManager check → 
  ├── isLoggedIn + ADMIN → AdminDashboardActivity
  ├── isLoggedIn + CLIENT → ClientDashboardActivity
  └── not logged in → LoginActivity
```

### Admin Dashboard Structure
```
AdminDashboardActivity
├── ViewPager2 (5 tabs via DashboardViewPagerAdapter)
│   ├── DashboardContentFragment    → Stats + chart + activity feed
│   ├── ManageBooksFragment         → Book list + search/filter/add
│   ├── MembersFragment             → Member list + search/filter/add
│   ├── NotificationsFragment       → Notification list
│   └── ProfileFragment             → Profile view + password change
├── BottomNavigationView            → Tab switching
├── Slide-out Bottom Sheet          → Sidebar navigation
├── Notification PopupWindow        → Quick notification preview
└── Detail fragments (pushed on back stack)
    ├── BookInfoFragment
    ├── LendBookFragment
    ├── UserInfoFragment
    ├── LendedBooksFragment
    ├── AddBookFragment
    ├── AddUserFragment
    ├── ForgotPasswordFragment
    └── ResourcesFragment
```

### Client Dashboard Structure
```
ClientDashboardActivity
├── DrawerLayout (root)
│   ├── Main Content
│   │   ├── AppBarLayout
│   │   │   ├── hamburger menu (open drawer)
│   │   │   ├── "XLMS LIBRARY" title
│   │   │   ├── notification bell (red dot indicator)
│   │   │   └── profile avatar (circle background)
│   │   ├── FrameLayout (mainContentFrame)
│   │   │   └── ClientDashboardContentFragment
│   │   │       ├── Welcome section (greeting + library hours card)
│   │   │       ├── 3 metric cards horizontal (Lended, Overdue, Reserved)
│   │   │       ├── Borrowed Books panel (CircularProgressIndicator + stats row)
│   │   │       └── Lending Activity panel (12-month bar chart, programmatic)
│   │   └── BottomNavigationView (6 items: Dashboard, Catalog, Account, Search, Help, Exit)
│   └── NavigationView (drawer, start gravity)
│       ├── nav_header_client (profile avatar + name + role)
│       └── client_drawer_menu (5 nav items + logout separator)
```

### Session Management
- **`SessionManager.java`**: SharedPreferences wrapper
- Stores: `isLoggedIn` (boolean), `email` (string), `role` (ADMIN/CLIENT)
- Methods: `saveSession()`, `clearSession()`, `isLoggedIn()`, `getUserEmail()`, `getUserRole()`

### Data Model POJOs
- `Book` — title, author, category, language, copies, price, pages, status, ID
- `BookInfo` — extended book details
- `BookLending` — lending transaction details
- `LendedBook` — lent book with return status
- `Member` — user info with membership details
- `Notification` — type, title, message, timestamp

### RecyclerView Adapters
- `BookAdapter` — book cards with status badges
- `LendedBookAdapter` — lent book records
- `MemberAdapter` — member list with status chips
- `NotificationAdapter` — notification cards with type icons

### Client-Specific Resources

**Layouts:**
- `activity_client_dashboard.xml` — DrawerLayout root, AppBarLayout, FrameLayout, BottomNavigationView, NavigationView
- `fragment_client_dashboard_content.xml` — ScrollView with welcome, metric cards, charts
- `nav_header_client.xml` — Drawer header (avatar + name + role)
- `metric_card_item.xml` — (unused; cards are inline in fragment layout)

**Menus:**
- `client_drawer_menu.xml` — 5 nav items + logout
- `client_bottom_navigation_menu.xml` — 6 bottom tabs

**Drawables:**
- `metric_card_background.xml` — rounded rect with subtle stroke
- `metric_icon_background.xml` — rounded rect for icon badges
- `chart_panel_background.xml` — rounded card for chart panels
- `bar_chart_item_background.xml` — top-rounded rect for bars
- `hours_card_background.xml` — rounded card for library hours
- `library_pulse_badge_background.xml` — pill badge
- `circle_image_background.xml` — oval shape for profile avatars
- `notification_dot_background.xml` — red circle for notification badge
- `ic_schedule.xml`, `ic_event_busy.xml`, `ic_bookmark_add.xml` — new Material icons

**Colors (added):**
- `surface_tint` — `#455F88`

**Color Selectors:**
- `client_bottom_nav_color.xml` — active=primary, inactive=on_surface_variant
- `client_nav_icon_color.xml`, `client_nav_text_color.xml`, `client_nav_item_ripple.xml`

**Styles (themes.xml):**
- `ClientNavItemText` — 14sp, inter_medium
- `ClientNavItemShape` — 10dp corner radius

## Database Schema (inferred from queries)

### `Users` Table
- `User_id` (PK, string like "A12345678")
- `User_Name`, `Email`, `Role`, `Membership_Type`, `Password` (bcrypt), `Cost`, `Status` ("Active"/"Deactivated")

### `Books` Table
- `Book_ID` (PK, UUID), `Book_Title`, `Author`, `Category`, `Language`, `Total_Copies`, `Available`, `Status`, `Pages`, `Price`

### `borrower` Table
- `Borrower_ID` (PK), `user_id`, `Name`, `BookTitle`, `Author`, `Category`, `IssuedDate`, `DueDate`, `CopiesLent`, `FinePerDay`, `Price`, `Book_ID`, `Status` ("Not Returned"/"Returned")

### `Notifications` Table
- `Id` (PK), `Userid`, `Message`, `CreatedAt`, `IsRead`

### `OPPS` Table
- `Email`, `OTPCode` (6-digit, auto-deleted after 3 min)

### `PasswordResetTokens` Table
- Token UUID, `User_id`, expiry (15 min)

### `Resource` Table
- `Name`, `Email`, `Website`
