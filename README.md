# 🚀 XLMS

<div align="center">
  <img src="assets/images/logo.png" alt="XLMS Logo" width="140" style="border-radius: 28px; box-shadow: 0 12px 24px rgba(0, 70, 67, 0.15); margin-bottom: 20px;" />

<h3><b>XLMS Library Management System</b></h3>
  <p>
    A full-stack library management system for administrators, borrowers, notifications, email verification, and inventory tracking.
  </p>

<p>
    <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=FFFFFF" />
    <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=FFFFFF" />
    <img src="https://img.shields.io/badge/Node.js-339933?style=for-the-badge&logo=node.js&logoColor=FFFFFF" />
    <img src="https://img.shields.io/badge/Express.js-000000?style=for-the-badge&logo=express&logoColor=FFFFFF" />
    <img src="https://img.shields.io/badge/MS_SQL_Server-CC2927?style=for-the-badge&logo=microsoftsqlserver&logoColor=FFFFFF" />
  </p>
</div>

---

## 📌 Overview

**XLMS Library Management System** is a full-stack solution designed to simplify library operations across web services and Android devices.

It combines:

- ⚙️ A **Node.js + Express** backend with Microsoft SQL Server
- 📱 A **native Android application** using modern AndroidX libraries
- 🔐 Secure authentication, notifications, and automation workflows

---

## 💡 Why this project is useful

XLMS helps streamline library operations with:

- Centralized book and user management
- Secure authentication with JWT
- OTP email verification and automated notifications
- Real-time Android synchronization with backend APIs
- Inventory and borrowing lifecycle tracking

---

## ✨ Key Features

- 📚 Admin dashboard for books, users, reservations, and inventory
- 🔐 JWT authentication with refresh token support
- 📩 Email OTP verification via Gmail API
- 🔄 Book lending lifecycle tracking system
- 👥 Role-based access control (Admin / User)
- 📱 Android app with Retrofit + OkHttp integration
- 🎨 Smooth UI with Shimmer loading effects
- 📊 Modular backend architecture (MVC pattern)

---

## ⚡ Tech Stack

| Tech | Details |
|------|--------|
| ![Backend](https://img.shields.io/badge/Backend-339933?style=for-the-badge&logo=node.js&logoColor=FFFFFF) | Node.js • Express.js • mssql • JWT • Bcrypt • Gmail API |
| ![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=000000) | Android Studio • AndroidX • Retrofit • OkHttp • Gson • Shimmer |
| ![Database](https://img.shields.io/badge/Database-CC2927?style=for-the-badge&logo=microsoftsqlserver&logoColor=FFFFFF) | Microsoft SQL Server |
| ![CI/CD](https://img.shields.io/badge/CI%2FCD-2088FF?style=for-the-badge&logo=githubactions&logoColor=FFFFFF) | GitHub Actions (`android_release.yml`) |                      |                   |

---

## 📁 Repository Structure

```
XLMS/
├── BackEnd/                    # Node.js REST API
│   ├── controller/             # Business logic (auth, books, users, lenders, mail, etc.)
│   ├── middleware/             # JWT auth + auto-refresh middleware (app.js)
│   ├── models/                 # SQL Server connection pool (db.js)
│   ├── routes/                 # Express route definitions
│   └── server.js               # Entry point, CORS, route mounts
├── FrontEnd/                   # Android application (Java)
│   ├── app/src/main/java/com/xlms/librarymanagement/
│   │   ├── SplashActivity.java
│   │   ├── api/                # Retrofit client, request/response models
│   │   ├── model/              # POJOs (Book, Member, Notification, etc.)
│   │   ├── adapter/            # RecyclerView adapters
│   │   ├── utils/              # SessionManager, repositories
│   │   └── ui/                 # Screen fragments
│   │       ├── admin/          # Admin dashboard tabs & detail screens
│   │       ├── client/         # Client dashboard tabs & detail screens
│   │       ├── auth/
│   │       ├── components/     # Custom charts (PieChartView, StackedAreaChartView)
│   │       ├── login/
│   │       ├── signup/
│   │       └── error/
│   └── build.gradle.kts
├── UI Design/                  # UI mockups and HTML prototypes
│   └── screens/                # 46 screen design files
├── .github/workflows/          # CI/CD pipeline
│   └── android_release.yml     # Automated APK build & GitHub Release
├── overview.md                 # System overview
├── features.md                 # Feature breakdown (backend + frontend)
├── api.md                      # Full API reference with endpoints
├── architecture.md             # Backend & frontend architecture details
├── app-flows.md                # Application flow diagrams (Mermaid)
├── ui.md                       # UI screen hierarchy & design language
├── PRD.md                      # Product Requirements Document
├── TRD.md                      # Technical Requirements Document
└── README.md
```

---

## 🛠️ Getting Started

### Prerequisites

- Node.js 18+
- npm
- Android Studio
- Microsoft SQL Server
- Gmail API credentials (optional)

---

### Backend Setup

```bash
cd BackEnd
npm install
```

Create `.env` file:

```env
user=YOUR_DB_USER
DB_PASS=YOUR_DB_PASSWORD
server=YOUR_DB_SERVER
database=YOUR_DB_NAME
URL=http://localhost:3000
JWT=your_jwt_secret
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
GOOGLE_REDIRECT_URI=https://developers.google.com/oauthplayground
GOOGLE_REFRESH_TOKEN=your_google_refresh_token
GOOGLE_USER_EMAIL=your-email@example.com
```

Run server:

```bash
npm start
# or
npm run dev
```

---

### Android Setup

```bash
cd FrontEnd
```

* Open in Android Studio
* Sync Gradle

Run:

```bash
./gradlew assembleDebug -PBASE_URL="http://localhost:3000/"
```

> **Note:** Omit the `-PBASE_URL` flag to use the default hosted API endpoint.

---

## 🚀 Example Usage

* API Base: `http://localhost:3000/api`
* Android login & registration with OTP email verification
* Admin dashboard with book/user/lender management
* Client dashboard with catalog browsing, checkout & reservations
* Real-time borrowing & return workflow with reservation queue

---

## 📚 Documentation

| Document | Description |
|----------|-------------|
| `overview.md` | System overview & integration status |
| `features.md` | Full feature breakdown (backend + frontend) |
| `api.md` | Complete API reference with request/response examples |
| `architecture.md` | Backend & frontend architecture details |
| `app-flows.md` | Sequence diagrams for all major workflows |
| `ui.md` | Screen hierarchy, design language & layout inventory |
| `PRD.md` | Product Requirements Document |
| `TRD.md` | Technical Requirements Document |
| `UI Design/` | HTML mockups for all 46 app screens |
| `BackEnd/server.js` | Backend entry point |

---

## 🤝 Contributing

* Fork repository
* Create feature branch
* Submit pull request to `main`
* Follow clean code structure

---

## 📄 License

This project is open-source for learning and academic use.
