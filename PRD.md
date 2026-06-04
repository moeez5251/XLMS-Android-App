# Product Requirements Document (PRD) - XLMS Library Management System

## 1. Product Overview
**XLMS** is a full-stack, dual-role (Admin and Client) Library Management System designed to streamline library operations, automate borrowing and return workflows, and provide real-time status tracking via a native Android client application and a robust Express.js REST API.

### Vision
To provide a secure, modern library solution that eliminates manual pen-and-paper tracking, automates notifications and security updates (e.g. OTP validation), and provides library members with self-service tools for book borrowing, reservations, support ticket submissions, and digital accounts tracking.

---

## 2. Target Audience & Roles

### 2.1. Library Administrator (Admin)
- **Objective**: Manage books inventory, member directories, manual loan issuances, external resource links, and monitor system metrics.
- **Access Level**: Full write/delete access across all databases.

### 2.2. Library Member (Client)
- **Objective**: Search catalog, borrow books, reserve out-of-stock items, track active loans/fines, manage profiles, and submit support tickets.
- **Access Level**: Access restricted to personal records and self-service catalog actions.

---

## 3. Core Product Features

### 3.1. User & Authentication Management
- **Role-Based Access Control**: Separate landing screens (Admin Tabbed Dashboard vs. Client Bottom Navigation/Drawer Dashboard) matching user roles.
- **Multi-Step Self-Registration (Client)**: Email availability check, OTP validation via Gmail API, and profile setup.
- **Profile Management**: Profile updating, secure passwords changing, and automated forgot-password reset processes (email-driven).
- **Admin Control Panel**: Ability to create new members manually, update profiles, toggle status (Activate/Deactivate), and delete accounts.

### 3.2. Book Catalog & Inventory Management
- **Book CRUD (Admin)**: Add books (Pages, Price, Title, Author, Language, Category, Copies), update specifications, and delete books in bulk.
- **Interactive Catalog (Client)**: Multi-field filtering (spinners populated dynamically with database Categories and Languages) combined with real-time keyword search.
- **Inventory Tracking**: Automated copy calculations decrementing `Available` stock when borrowed and incrementing on returns.

### 3.3. Lending Lifecycle & Returns
- **Admin Lending Creation**: Manual lending registration which validates stock, adds custom fine settings, establishes due dates, and alerts the user with default credentials if they are new.
- **Client Self-Checkout**: Immediate borrowing directly from the client app.
- **Client Return Action**: Users can return books directly from their accounts tab. This recalculates account costs, restores item stock, and triggers reservation checks.

### 3.4. Reservation System
- **Hold System**: Clients can reserve out-of-stock or borrowed books. This flags the book status as `Reserved`.
- **First-In-First-Out (FIFO) Auto-Lending**: When a borrowed book with pending reservations is returned, the system bypasses restocking and automatically issues the book to the oldest reserver, updating their loan log and notifying them.

### 3.5. Real-Time Dashboards & Analytics
- **Admin Analytics**: Counters for total books, active borrowers, overdue materials, and interactive canvas charts (visitor activity, book availability distributions).
- **Client Dashboard**: Custom metrics (Lended, Overdue, Reserved), a circular percentage chart representing return efficiency, and programmatically constructed vertical bar charts detailing 12-month borrowing frequency.

### 3.6. Support Ticket Desk
- **Contact Forms**: Client-facing feedback page to submit support tickets, automatically routing confirmation emails to users and detailed warnings to administrator inbox.

### 3.7. In-App Notifications
- Persistent notification logs tracking active alerts (borrow events, returns, reservation confirmations) with quick-access popup drawer buttons and bulk "Mark as Read" actions.

---

## 4. Key User Journeys & Use Cases

### Use Case 1: Member Self-Registration & OTP Verification
1. User enters their email in the Signup flow.
2. System triggers a backend search checking if email exists.
3. If unique, an email containing a 6-digit numeric OTP code is dispatched via Gmail API (expires in 3 minutes).
4. User submits the correct OTP inside the app, validating their email.
5. User enters account details (Username, Password) and is registered as a Client.

### Use Case 2: Client Book Checkout & Return Flow
1. Client searches catalog, selects "The Great Gatsby", and clicks "Checkout".
2. Client selects copy count and return date, then submits.
3. Backend checks if copy count is available. If yes, it logs a borrower record (`not returned`), decrements the book's availability pool, and increases the client's account balance (Price × copies × days).
4. When finished, client opens the Accounts tab, clicks "Return" next to the book, and confirms.
5. Backend updates borrower record status to `Returned`, decreases the user's cost balance, increments book availability, and dispatches a confirmation notification.

### Use Case 3: Automated Reservation Hand-off
1. Client A reserves "The Hobbit" (currently out of stock). Book status updates to `Reserved`.
2. Client B returns their copy of "The Hobbit".
3. Return endpoint identifies Client A's pending reservation.
4. Instead of incrementing stock, the system automatically creates a new borrowing record for Client A, deletes the reservation log, and dispatches an alert: *"Your reserved book is now issued to you."*

---

## 5. Usability & UI Design Requirements
- **Skeleton Shimmers**: Lists must implement custom shimmer layouts during network data retrievals instead of plain circular loading bars.
- **Clean Layouts**: UUID fields must be truncated to the first 4 characters (`8a9b...`) to maintain clear tabular spacing.
- **Non-blocking Loading**: Critical actions (Signups, Saves) must lock inputs and display progress shimmers until the backend responds.
- **Gradient Actions**: Custom primary action buttons must utilize subtle blue-to-dark-blue gradient states, and dangerous operations (Delete) must show red alerts.
