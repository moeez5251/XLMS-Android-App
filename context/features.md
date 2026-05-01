# Features

## Backend Features (Fully Implemented)

### Authentication
- JWT token generation (1h expiry) via HTTP-only cookie + Bearer Token.
- **Auto-Refresh**: Middleware refreshes tokens within a 5-minute expiry window.
- Role enforcement (Admin/User).
- Password change (bcrypt verification).
- Forgot password (email-based reset).

### User Management
- Full user lifecycle (Register, Update, Delete).
- Status control (Activate/Deactivate).
- Filterable user listing.

### Book Management
- Full CRUD for books.
- Dynamic filtering by Category, Language, and Status.
- Availability tracking (auto-updates on lending).

### Email & Notifications
- OTP verification for new users.
- Automated lending notification emails.
- Persistent in-app notifications with "Mark as Read" capability.

---

## Frontend Features (Fully Integrated)

### Authentication (✅ Fully Integrated)
- **Login/Sign Up**: Complete 3-step registration and secure login.
- **Auto-Session**: Silent token refreshing via `ApiClient` interceptor.

### Dashboard Overview (✅ Fully Integrated)
- **Live Stats**: Real-time counters for library metrics.
- **Visitors Chart**: Dynamic 12-month **Stacked Area Chart** (Orange/Green theme).
- **Availability Chart**: Custom **Pie Chart** reflecting real-time inventory.
- **Recent Activity**: Displays the 5 latest unread notifications.

### Manage Books (✅ Fully Integrated)
- **Skeleton Shimmers**: Professional 5-item loading placeholders.
- **Dynamic Filters**: Server-side fetched categories, languages, and statuses.
- **Full CRUD**: View details, Edit (with spinners), and Delete with confirmation.
- **ID Shortening**: Clean table display (e.g., `8f2a...`).

### Members Tab (✅ Fully Integrated)
- **Skeleton Loading**: Multi-item shimmer effect during data fetch.
- **User Control**: Edit profile, Toggle status (Activate/Deactivate), and Delete.
- **Role Management**: Switch between User and Admin roles.

### Profile & Security (✅ Fully Integrated)
- **Live Profile**: Fetches current user data from `/getbyid`.
- **Password Security**: Change password with backend error validation.
- **Forgot Password**: Integrated email reset flow with pre-filled, protected fields.

### Notifications (✅ Fully Integrated)
- **Clean UI**: Professional item layout with relative time (e.g., "1 min ago").
- **Bulk Actions**: "Read All" functionality to clear unread counts.
- **Global Access**: Fast-access popup from the navigation bar.
