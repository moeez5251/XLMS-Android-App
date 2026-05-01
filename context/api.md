# API Reference

## Base URL
`http://localhost:5000/api` (dev) | Configurable via environment

## Authentication
- Endpoints require a valid JWT token either in an HTTP-only cookie (`token`) or an `Authorization: Bearer <token>` header.
- **Auto-Refresh**: Backend provides an `X-New-Token` header if the current token is refreshed; frontend automatically updates its session.
- Unprotected endpoints: `/api/auth/login`, `/api/auth/logout`, `/api/token/verify`, `/api/token/update`, `/`

---

## Auth Endpoints

### POST `/api/auth/login`
**No auth required**

Body: `{ email: string, password: string }`

Response (200): `{ message: "Login successful", token: string, userid: string, role: string }`

---

## User Endpoints

### POST `/api/users/register`
Body: `{ User_Name, Email, Role, Membership_Type, Password }`

Auto-generates: `User_id`, `Status` ("Active"), bcrypt password

Response (201): `{ message: "User created successfully" }`

### POST `/api/users/all`
Response (200): `Array<Member>`

### GET `/api/users/getbyid`
Uses `req.user.id` from JWT

Response (200): `{ User_id, User_Name, Email, Role, Membership_Type, Status }`

### POST `/api/users/update`
Body: `{ ID, User_Name, Role, Membership_Type }` (Email is immutable)

Response (200): `{ message: "User updated successfully" }`

### POST `/api/users/activate`
Body: `{ ID }`

Response (200): `{ message: "User activated successfully" }`

### POST `/api/users/deactivate`
Body: `[id1, id2, ...]` (array of IDs)

Response (200): `{ message: "Users deactivated successfully" }`

### DELETE `/api/users/delete`
Body: `[id1, id2, ...]` (array of IDs)

Response (200): `{ message: "Users deleted successfully" }`

### PUT `/api/users/changepassword`
Uses `req.user.id` from JWT

Body: `{ OldPassword, NewPassword }`

Response (200): `{ message: "Password changed successfully" }`

### POST `/api/users/forgotpassword`
Body: `{ email }`

Response (200): `{ message: "Reset instructions sent!" }`

---

## Book Endpoints

### POST `/api/books/insert`
Body: `{ Book_Title, Author, Category, Language, Total_Copies, Status, Pages, Price }`

Response (201): `{ message: "Book Added successfully" }`

### POST `/api/books/get`
Response (200): `Array<Book>` — all books

### POST `/api/books/getbyID`
Body: `{ ID }`

Response (200): `Array<Book>` (single element)

### PUT `/api/books/update`
Body: `{ Book_ID, Book_Title, Author, Category, Language, Total_Copies, Status, Pages, Price }`

Response (200): `{ message: "Book updated successfully" }`

### DELETE `/api/books/delete`
Body: `[id1, id2, ...]`

Response (200): `{ message: "Books deleted successfully" }`

### POST `/api/books/col`
Body: `{ column: string[] }` (e.g., `["Category"]`, `["Language"]`)

Returns distinct values for dynamic filtering.

---

## Notification Endpoints

### GET `/api/notifications/get`
Uses `req.user.id` from JWT

Response (200): `Array<Notification>`

### POST `/api/notifications/markasread`
Uses `req.user.id` from JWT

Response (200): `{ message: "Notification(s) marked as read" }`

---

## Other Endpoints

### GET `/api/other/getbookdata`
Aggregated stats for the dashboard overview.

Response (200): `{ Totalbooks, Totalusers, Totalborrowers, availablebooks, overduebooks }`

### GET `/`
Response (200): `✅ App is alive!`
