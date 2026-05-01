# Architecture

## Backend Architecture

### Entry Point (`server.js`)
- Express app on port 5000.
- CORS with dynamic origins.
- Global JWT auth middleware.
- **Unprotected routes**: `/api/auth/login`, `/api/auth/logout`, `/api/token/verify`, `/api/token/update`, `/`.

### Database Layer (`models/db.js`)
- SQL Server connection pool via `mssql`.
- Hosted on Somee.com cloud.

### Key Controllers
- `authController`: Login, logout, role checks.
- `bookscontroller`: Book CRUD & status management.
- `userController`: User lifecycle & password hashing.
- `notificationscontroller`: In-app notification management.
- `other`: Aggregated dashboard statistics.

## Frontend Architecture

### Networking Layer (`com.xlms.librarymanagement.api`)
- **`ApiClient`**: Configures OkHttpClient with:
    - `HttpLoggingInterceptor` for debugging.
    - Custom `Interceptor` that injects `Authorization: Bearer <token>` and handles session cookies.
    - **Auto-Refresh**: Interceptor extracts `X-New-Token` from server responses to update local session seamlessly.
- **`ApiService`**: Retrofit interface defining the API endpoints.

### Navigation Flow
```
SplashActivity → SessionManager check → 
  ├── isLoggedIn + ADMIN → AdminDashboardActivity
  ├── isLoggedIn + CLIENT → ClientDashboardActivity
  └── not logged in → LoginActivity
```

### Component Structure
- **Activities**: Top-level containers (ViewPager2 for Admin, Drawer for Client).
- **Fragments**: Modular UI sections. Details are pushed onto the back stack.
- **Custom Views**:
    - `PieChartView`: Native canvas-drawn book availability chart.
    - `StackedAreaChartView`: Smooth area chart for 12-month visitor data.
- **Adapters**: Connect dynamic lists to RecyclerViews.

### Data Model
- **POJOs**: `Book`, `Member`, `Notification` are fully mapped to backend columns using GSON `@SerializedName`.

## API Integration Patterns
1. **Automated Session**: Token injection and refresh are handled at the network layer.
2. **Asynchronous UI**: All requests are non-blocking.
3. **Synchronization**: Parent fragments (e.g., ManageBooks) listen for changes in detail screens to refresh data immediately upon Save or Delete.
