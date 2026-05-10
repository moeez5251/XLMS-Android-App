# 📚 XLMS Library Management System

Welcome to the **XLMS Library Management System**, a robust, full-stack solution designed for modern library operations. This project features a powerful Node.js backend and a feature-rich Android application, providing a seamless experience for both administrators and clients.

---

## 🎨 UI Design Preview

Experience the design vision of XLMS directly in your browser:
👉 **[Launch Interactive UI Design](./UI%20Design/index.html)**

*Note: The design folder contains high-fidelity HTML/CSS mockups representing the core application screens.*

---

## 🚀 Key Features

### 🛠️ Admin Dashboard
- **Live Statistics**: Real-time counters for library metrics (total books, members, active lendings).
- **Interactive Charts**:
    - **Stacked Area Chart**: 12-month visitor activity tracking with a vibrant Orange/Green theme.
    - **Custom Pie Chart**: Real-time visualization of book availability.
- **Book Management**: Full CRUD operations with server-side filtering by category, language, and status.
- **Member Control**: Manage user lifecycles, roles (Admin/User), and account statuses (Active/Deactivated).
- **Notification Center**: In-app notifications with "Mark as Read" and bulk clear functionality.

### 👤 Client Features
- **Book Discovery**: Browse and search the library catalog (UI Integrated).
- **Personal Account**: Manage profile settings and view lending history.
- **Resources**: Dedicated section for digital library resources.

### 🔐 Security & Integration
- **Advanced Auth**: JWT-based authentication with automated token refreshing.
- **Email Services**: OTP verification and automated lending notifications via Gmail API.
- **Session Management**: Persistent sessions with secure cookie handling.

---

## 🛠️ Tech Stack

### **Backend**
- **Runtime**: Node.js (Express.js v5.1.0)
- **Database**: Microsoft SQL Server
- **Authentication**: JWT (JSON Web Tokens) + Bcrypt
- **Email**: Gmail API (OAuth2)

### **Frontend (Android)**
- **Language**: Kotlin / Java
- **Architecture**: XML Layouts + Material Design
- **Networking**: Retrofit 2 + OkHttp 3
- **Animations**: Facebook Shimmer (Skeleton loading effects)
- **Visuals**: Custom Canvas-drawn charts

---

## 📁 Project Structure

```bash
Admin/
├── BackEnd/               # Node.js REST API
│   ├── controller/        # Business logic & API handlers
│   ├── middleware/        # JWT & Session logic
│   ├── models/            # Database connection (SQL Server)
│   └── routes/            # Express API endpoints
├── FrontEnd/              # Android Application
│   ├── app/src/main/java/ # Application logic (Activities, Fragments, Models)
│   └── app/src/main/res/  # UI Resources (Layouts, Drawables, Animations)
├── UI Design/             # Web-based UI Prototypes & Mockups
└── context/               # Project documentation and specifications
```

---

## ⚙️ Getting Started

### **Backend Setup**
1. Navigate to the `BackEnd` directory.
2. Install dependencies:
   ```bash
   npm install
   ```
3. Configure your `.env` file with database credentials and Gmail API keys.
4. Start the server:
   ```bash
   npm start
   ```

### **Android App Setup**
1. Open the `FrontEnd` folder in **Android Studio**.
2. Sync the project with Gradle files.
3. Ensure you have a running instance of the backend or update the `BASE_URL` in the `ApiClient` configuration.
4. Build and run on an emulator or physical device.

---

## 📄 License

This project is developed as part of the XLMS Library Management System. All rights reserved.

---

*Developed with ❤️ for efficient library management.*
