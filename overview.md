# XLMS Admin - Project Overview

## Project Summary
**XLMS** is a Library Management System with both Admin and Client dashboards. It consists of:
- **Backend**: Node.js/Express REST API with SQL Server database.
- **Frontend**: Android app (Java, XML layouts) with dual-role dashboards (Admin tabbed, Client tabbed view with bottom navigation and drawer sheet options).

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend Framework | Express.js v5.1.0 |
| Database | Microsoft SQL Server (via `mssql`) |
| Auth | JWT + Cookie / Authorization Header Interceptor |
| Email | Gmail API (OAuth2) |
| Android UI | XML layouts + Material Design Components + ViewPager2 |
| Android Language | Java |
| Networking | Retrofit 2 + OkHttp 3 |
| Session (Android) | SharedPreferences + Automated Token Refresh Interceptor |

## Project Structure

```
Admin/
├── BackEnd/               # Node.js REST API
│   ├── server.js          # Entry point, CORS, unprotected routes, route mounts
│   ├── controller/        # Business logic controllers
│   │   ├── authController.js
│   │   ├── bookscontroller.js
│   │   ├── lendersControllers.js
│   │   ├── mailer.js
│   │   ├── mails.js
│   │   ├── notificationscontroller.js
│   │   ├── other.js
│   │   ├── otpController.js
│   │   ├── reservationController.js
│   │   ├── resourcecontroller.js
│   │   └── tokengenerator.js
│   ├── routes/            # Express routers
│   ├── middleware/        # JWT auth + Auto-Refresh middleware (app.js)
│   └── models/            # SQL Server connection pool (db.js)
├── FrontEnd/              # Android App
│   ├── app/src/main/
│   │   ├── java/com/xlms/librarymanagement/
│   │   │   ├── SplashActivity.java
│   │   │   ├── api/       # Retrofit interfaces, Response/Request models, Authenticators
│   │   │   ├── utils/     # SessionManager, Constants
│   │   │   ├── model/     # POJOs (Book, Member, Notification, Reservation, etc.)
│   │   │   ├── adapter/   # RecyclerView adapters
│   │   │   └── ui/        # Dual-Role Screens
│   │   │       ├── admin/ # Admin dashboard tabs & detail screens
│   │   │       ├── client/# Client dashboard tabs & detail screens
│   │   │       ├── auth/
│   │   │       ├── components/ # Custom charts (PieChartView, StackedAreaChartView)
│   │   │       └── login/ / signup/
│   │   └── res/           # layouts, drawables, menus, colors, animations, fonts
│   └── build.gradle.kts
├── context/               # Project documentation (v4.0)
└── README.md
```

## Integration Status
The Android app is in the **Fully Integrated** phase for all core modules.
- **Operational**: Authentication (Login/Signup with OTP verification), Dashboard Statistics (Admin & Client), and In-App Notifications are fully connected.
- **Books & Members**: Full CRUD (Create, Read, Update, Delete) integrated with real-time UI synchronization and skeleton loading shimmers.
- **Session Management**: Automated token refresh and persistent cookie management are active.
- **Client & Resources**: The Client Dashboard (with circular stats and monthly bar charts), Support Ticketing (sending email directly through Mailer API), Book catalog (reserve/lend/return), and Resource additions are fully connected to the API and operational.
