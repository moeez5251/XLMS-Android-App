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
| Auth | JWT + HTTP-only cookies + Bearer Token |
| Email | Gmail API (OAuth2) |
| Android UI | XML layouts + Material Design + ViewPager2 |
| Android Language | Java |
| Networking | Retrofit 2 + OkHttp 3 |
| Session (Android) | SharedPreferences (via `SessionManager`) |

## Project Structure

```
Admin/
├── BackEnd/               # Node.js REST API
│   ├── server.js          # Entry point, CORS, middleware registration
│   ├── controller/        # Business logic
│   ├── routes/            # Express routers
│   ├── middleware/        # JWT auth middleware
│   └── models/            # SQL Server connection pool
├── FrontEnd/              # Android App
│   ├── app/src/main/
│   │   ├── java/com/xlms/librarymanagement/
│   │   │   ├── api/       # Retrofit interfaces and API client
│   │   │   ├── utils/     # SessionManager, constants
│   │   │   ├── model/     # POJOs (Book, Member, Notification, etc.)
│   │   │   ├── adapter/   # RecyclerView adapters
│   │   │   └── ui/        # Activities + Fragments
│   │   └── res/           # layouts, drawables, menus, colors, animations, fonts
│   └── build.gradle.kts
├── context/               # Project documentation (v2.0)
└── README.md
```

## Integration Status
The Android app is currently in a **Partial Integration** phase.
- **Operational**: Authentication (Login/Signup), Dashboard Statistics, and In-App Notifications are fully connected to the backend API.
- **In-Progress**: CRUD operations for Books and Members are still using dummy data in the UI and require migration to the existing backend endpoints.
- **UI-Only**: The Client Dashboard and Resource sections are fully designed but not yet wired to the API.
