# Features

## Backend Features

### Authentication
- JWT access tokens (1-hour expiry) stored in HTTP-only cookies or Bearer Authorization headers.
- **Auto-Refresh**: Middleware interceptor automatically validates the refresh token and signs a new access token when the request is within a 5-minute expiry threshold, sending it back to the client via `X-New-Token` header.
- Role-based route enforcement (Admin/User).
- Password management (bcrypt hashing and validations).

### User Management
- User account creation (`register`/`signup`), updates, and deletions.
- Account status controls (Activate/Deactivate).
- Email checking APIs (`/users/exist` and `/users/auth-users`).

### Book Management
- Book CRUD (Create, Read, Update, Delete) for catalog inventory.
- Distinct column fetch API (`col`) for language and category filtering values.
- Availability tracking (automatic increment/decrement on lending and return operations).

### Lending & Return Management
- Admin lending registration (`/lenders/insert`) which registers a loan, checks book availability, updates user costs, generates a password token for new users, and logs borrower files.
- Client lending checkout (`/books/lend`) which allows the client to borrow directly.
- Client returning (`/lenders/return`) which handles status updates, adjusts user costs, checks for waiting reservations, and auto-assigns the book to the next reserver.
- Lending history lookups (All list for Admin, user-specific listings for Client via `/lenders/mylendings`).

### Reservation Management
- Book reservations (`/reservations/reserve`) which adds reservation logs and toggles status to 'Reserved' if copies are checked out.
- Client reservation listings (`/reservations/myreservations`).

### Email & Support Notifications
- OTP verification codes generated for signup validation and password resets.
- Automated library alerts (such as lending confirmations, reservation completions, return statements).
- Client support tickets (`/mail/issue-mail`) sending confirmation emails to users and detailed notifications to administrators.
- In-app notification management with user-specific persistent logs and "Mark as Read" operations.

---

## Frontend Features

### Dual-Role Client & Admin Portals

### Authentication (✅ Fully Integrated)
- OTP Verification Flow: 3-step registration and reset flows.
- Dynamic session management: Silent network-level token refresh.

### Admin Dashboard (✅ Fully Integrated)
- **Live Stats Counters**: Total books, users, active borrowers, available items, and overdue listings.
- **Custom Area Charts**: Stacked Area Chart showing a 12-month visitor progression.
- **Custom Pie Charts**: PieChartView visualizing real-time book availability proportions.
- **Recent Activities**: A popup checklist of the latest notifications.

### Client Dashboard (✅ Fully Integrated)
- **Welcome greeting**: Displays user's name.
- **Metrics Dashboard**: Cards for Lended, Overdue, and Reserved counts.
- **Circular Chart**: Displays the returned-to-overdue book ratio.
- **Activity Bar Chart**: Programmatically rendered vertical bar chart of 12-month lending frequency.

### Manage Books (Admin Tab — ✅ Fully Integrated)
- **Skeleton Shimmers**: Professional 5-item shimmer loading placeholders.
- **Spinner Filtering**: Dynamic dropdown filtering based on database categories and languages.
- **CRUD Operations**: Details view, form editing with validation spinners, and confirmation deletes.
- **Lend Book Integration**: Shortcut to lend specific books to members directly.

### Book Catalog (Client Tab — ✅ Fully Integrated)
- **Search & Filters**: Real-time keyword search with categories/languages spinners.
- **Lend Book (Checkout)**: Form to choose copy counts, dates, and submit checkout requests.
- **Reserve Book**: Reserve out-of-stock items, receiving email confirmations.

### Members Tab (Admin Tab — ✅ Fully Integrated)
- **Grid view**: Displays member name initials, UUIDs, roles, and status levels.
- **Profiles**: Profile editing, role selection, and user deletion.
- **Activity Control**: Real-time activation/deactivation triggers.

### Account Tab (Client Tab — ✅ Fully Integrated)
- **Active Loans**: List of currently borrowed books, showing issue/due dates, copies, and fine-per-day.
- **Return Book**: Direct returning trigger that processes returns, updates costs, and updates status.
- **User Profile & Security**: Live profile detail viewing and password changes.

### Resources Tab (Admin Section — ✅ Fully Integrated)
- Admin form to add third-party resource listings (Name, Email, Website) to the database.

### Support Help Desk (Client Tab — ✅ Fully Integrated)
- Form to submit tickets (Subject, Description) which triggers support emails.

### Notifications Tab (✅ Fully Integrated)
- Quick-access drawer popup with notification badges.
- Full listing tab with relative time layouts.
- Bulk "Mark as Read All" actions.
