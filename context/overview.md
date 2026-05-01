# XLMS Admin - Project Overview

## Project Summary
**XLMS** is a Library Management System with both Admin and Client dashboards. It consists of:
- **Backend**: Node.js/Express REST API with SQL Server database.
- **Frontend**: Android app (Java, XML layouts) with dual-role dashboards (Admin tabbed, Client drawer-based).

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend Framework | Express.js v5.1.0 |
| Database | Microsoft SQL Server (via `mssql`) |
| Auth | JWT + Custom Cookie/Bearer Token Interceptor |
| Email | Gmail API (OAuth2) |
| Android UI | XML layouts + Material Design + ViewPager2 |
| Android Language | Java |
| Networking | Retrofit 2 + OkHttp 3 + Custom Shimmers |
| Session (Android) | SharedPreferences + Automated Token Refresh |

## Project Structure

```
Admin/
├── BackEnd/               # Node.js REST API
│   ├── server.js          # Entry point, CORS, middleware registration
│   ├── controller/        # Business logic
│   ├── routes/            # Express routers
│   ├── middleware/        # JWT auth + Auto-Refresh logic
│   └── models/            # SQL Server connection pool
├── FrontEnd/              # Android App
│   ├── app/src/main/
│   │   ├── java/com/xlms/librarymanagement/
│   │   │   ├── api/       # Retrofit interfaces, Interceptors, Authenticators
│   │   │   ├── utils/     # SessionManager, constants
│   │   │   ├── model/     # POJOs (Book, Member, Notification, etc.)
│   │   │   ├── adapter/   # RecyclerView adapters
│   │   │   └── ui/        # Activities + Fragments + Custom Views
│   │   └── res/           # layouts, drawables, menus, colors, animations, fonts
│   └── build.gradle.kts
├── context/               # Project documentation (v3.0)
└── README.md
```

## Integration Status
The Android app has moved into the **Full Integration** phase for core modules.
- **Operational**: Authentication (Login/Signup), Dashboard Statistics, and In-App Notifications are fully connected.
- **Books & Members**: Full CRUD (Create, Read, Update, Delete) integrated with real-time UI synchronization and skeleton loading.
- **Session Management**: Automated token refresh and persistent cookie management are active.
- **UI-Only**: The Client Dashboard and Resource sections are fully designed but not yet wired to the API.
