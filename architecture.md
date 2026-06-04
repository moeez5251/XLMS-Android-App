# Architecture

## Backend Architecture

### Entry Point (`server.js`)
- Express app on port 5000.
- CORS with dynamic origins (verifies allowed origins via environment variables).
- Global JWT authentication middleware applied to all routes except the defined unprotected paths.
- **Unprotected routes**:
  - `/api/auth/login` (Login authentication)
  - `/api/auth/logout` (Logout session)
  - `/` (App heartbeat)
  - `/api/users/signup` (Client user self-registration)
  - `/api/users/exist` (Check if email address exists)
  - `/api/users/auth-users` (Verify existences and authentication check)
  - `/api/users/resetpassword` (Password reset payload operation)
  - `/api/mail/otp` (Send OTP verification via email)
  - `/api/mail/verify` (Verify email OTP code correctness)
  - `/api/mail/resend` (Resend verification OTP)

### Database Layer (`models/db.js`)
- SQL Server connection pool via `mssql`.
- Hosted on Somee.com cloud database service.

### Key Controllers
- `authController`: Login, logout, role checking.
- `bookscontroller`: Book CRUD, column listings, and client borrowing (`lendbook`).
- `userController`: User lifecycle, activation/deactivation, password hashing, and profiles.
- `notificationscontroller`: In-app notification creation, retrieval, and read confirmations.
- `lendersControllers`: Admin lendings CRUD (`addbook`), status filtering, detail lookup, and client returning (`returnbook`).
- `mails`: OTP verification mailing and ticket issue submission (`issue_mail`).
- `reservationController`: Client reservation creations (`reservebook`) and reservation tracking.
- `resourcecontroller`: Admin third-party resource listings.
- `other`: Dashboard stats aggregators (Admin counters and Client monthly analytics maps).

## Frontend Architecture

### Networking Layer (`com.xlms.librarymanagement.api`)
- **`ApiClient`**: Configures `OkHttpClient` with:
  - `HttpLoggingInterceptor` for debugging Retrofit payloads.
  - Custom Interceptor that injects `Authorization: Bearer <token>` and cookie tracking.
  - **Auto-Refresh**: When the Access Token is near expiry (within 5 minutes) or expired, the backend middleware validates the `refreshToken` and returns a new Access Token in the `X-New-Token` HTTP response header. The OkHttp interceptor reads this header and automatically updates the session's authentication token dynamically.
- **`ApiService`**: Retrofit 2 interface defining the routes.

### Navigation Flow
```
SplashActivity → SessionManager check → 
  ├── isLoggedIn + ADMIN → AdminDashboardActivity
  ├── isLoggedIn + CLIENT → ClientDashboardActivity
  └── not logged in → LoginActivity
```

### Component Structure
- **Activities**: Top-level ViewPager2 containers for tab navigation.
- **Fragments**: Tab fragments and pushed sub-detail fragments (using transactions over `mainContentFrame` frame layout).
  - **Admin Tabs**: `DashboardContentFragment`, `ManageBooksFragment`, `MembersFragment`, `NotificationsFragment`, `ProfileFragment`.
  - **Admin Details**: `BookInfoFragment`, `UserInfoFragment`, `AddBookFragment`, `AddUserFragment`, `LendedBooksFragment`, `LendedBookInfoFragment`, `LendBookFragment`, `ResourcesFragment`.
  - **Client Tabs**: `ClientDashboardContentFragment`, `ClientCatalogFragment`, `ClientAccountFragment`, `ClientHelpFragment`, `ClientNotificationsFragment`.
  - **Client Details**: `CheckoutFragment`, `ClientBookInfoFragment`, `ReservationFragment`, `ForgotPasswordFragment`.
- **Custom Components & UI Widgets**:
  - **Admin Views**: `PieChartView` (native canvas book availability pie chart), `StackedAreaChartView` (visitor trends).
  - **Client Views**: Material `CircularProgressIndicator` (borrowed/returned ratio), dynamic programmatically-built `LinearLayout` (vertical bar graph for 12-month activity).
- **Adapters**: Recycler list mappers (e.g. `NotificationAdapter`, `BookCatalogAdapter`, `LendingHistoryAdapter`, `ReservationAdapter`).

### Data Model
- **POJOs**: `Book`, `BookInfo`, `BookLending`, `LendedBook`, `Member`, `Notification`, `Reservation` mapped via GSON `@SerializedName`.
