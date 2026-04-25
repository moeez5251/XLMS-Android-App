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
- `lendersControllers`: The core lending logic and user auto-creation.
- `other`: Aggregated dashboard statistics.

## Frontend Architecture

### Networking Layer (`com.xlms.librarymanagement.api`)
- **`ApiClient`**: Configures OkHttpClient with:
    - `HttpLoggingInterceptor` for debugging.
    - Custom `Interceptor` that injects the `Authorization: Bearer <token>` header from `SessionManager`.
    - Cookie handling for session persistence.
- **`ApiService`**: Retrofit interface defining the API endpoints.

### Navigation Flow
```
SplashActivity → SessionManager check → 
  ├── isLoggedIn + ADMIN → AdminDashboardActivity
  ├── isLoggedIn + CLIENT → ClientDashboardActivity
  └── not logged in → LoginActivity
```

### Component Structure
- **Activities**: Act as containers for top-level navigation (Tabs or Drawer).
- **Fragments**: Handle specific UI sections (e.g., `ManageBooksFragment`). Detail screens are pushed onto the back stack with slide animations.
- **Adapters**: Connect model lists (e.g., `List<Book>`) to `RecyclerView` components.
- **SessionManager**: Encapsulates `SharedPreferences` for secure storage of JWT tokens and user metadata.

### Data Model
- Standard POJOs (`Book`, `Member`, `Notification`, etc.) used for both dummy data and Retrofit deserialization.

## API Integration Patterns
1. **Request**: Activity/Fragment calls `ApiClient.getApiService(context).someMethod(request)`.
2. **Execution**: `enqueue()` for asynchronous processing.
3. **Session**: Interceptor automatically adds the JWT token to the headers.
4. **Response**: 
   - On Success: Update local models and notify adapters.
   - On Failure: Parse error body to show specific backend messages (e.g., "Account deactivated").
