# XLMS Admin - Project Overview

## Project Summary
**XLMS** is a Library Management System with an Admin dashboard. It consists of:
- **Backend**: Node.js/Express REST API with SQL Server database
- **Frontend**: Android app (Java, XML layouts) with tabbed admin dashboard

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend Framework | Express.js v5.1.0 |
| Database | Microsoft SQL Server (via `mssql`) |
| Auth | JWT + HTTP-only cookies + bcrypt |
| Email | Gmail API (OAuth2) |
| Android UI | XML layouts + Material Design + ViewPager2 |
| Android Language | Java (no Kotlin source) |
| Session (Android) | SharedPreferences |

## Project Structure

```
Admin/
├── BackEnd/               # Node.js REST API
│   ├── server.js          # Entry point, CORS, middleware registration
│   ├── controller/        # Business logic (12 files)
│   ├── routes/            # Express routers (10 files)
│   ├── middleware/        # JWT auth middleware
│   └── models/            # SQL Server connection pool
├── FrontEnd/              # Android App
│   ├── app/src/main/
│   │   ├── java/com/xlms/librarymanagement/
│   │   │   ├── SplashActivity.java
│   │   │   ├── utils/SessionManager.java
│   │   │   ├── model/     # 6 POJOs (Book, Member, Notification, etc.)
│   │   │   ├── adapter/   # 4 RecyclerView adapters
│   │   │   └── ui/        # Activities + Fragments
│   │   │       ├── admin/     # 16 admin fragments
│   │   │       ├── auth/      # ForgotPasswordActivity
│   │   │       ├── client/    # ClientDashboardActivity + ClientDashboardContentFragment
│   │   │       ├── login/     # LoginActivity
│   │   │       └── signup/    # 3-step signup flow
│   │   └── res/           # layouts, drawables, menus, colors, animations, fonts
│   └── build.gradle.kts
├── README.md
└── DESIGN.md              # Full design system spec
```

## Key Architectural Pattern
- **Backend**: Classic MVC — routes → controllers → models (SQL pool)
- **Frontend**: Activity → ViewPager2 → Fragments → RecyclerView adapters
- **Global auth middleware**: All routes protected by default, except login/logout/token routes
- **API key gate**: Most endpoints require `XLMS_API` key in request body

## Critical Gap
**The Android app has ZERO integration with the backend.** All data is hardcoded/dummy. Login uses hardcoded credentials. No HTTP client library is included. The backend is fully functional but completely unused by the frontend.
