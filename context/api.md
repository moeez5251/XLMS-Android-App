# API Reference

## Base URL
`http://localhost:5000/api` (dev) | Configurable via environment

## Authentication
- Most endpoints require a valid JWT token in an HTTP-only cookie named `token`
- Most write endpoints require `XLMS_API` key in request body
- Unprotected endpoints: `/api/auth/login`, `/api/auth/logout`, `/api/token/verify`, `/api/token/update`, `/`

---

## Auth Endpoints

### POST `/api/auth/login`
**No auth required**

Body: `{ email: string, password: string, API: string }`

Response (200): `{ message: "Login successful", token: string, userid: string }`

Errors:
- 400: Invalid API key
- 401: Invalid email/password, account deactivated
- 403: Not admin role
- 500: Server error

### POST `/api/auth/logout`
**No auth required**

Response (200): `{ message: "Logout successful" }`

---

## User Endpoints

### POST `/api/users/register`
Body: `{ API, User_Name, Email, Role, Membership_Type, Password }`

Auto-generates: `User_id` (first letter + 8 random chars), `Cost` (0), `Status` ("Active"), bcrypt password

Response (201): `{ message: "User created successfully" }`

Errors:
- 400: Invalid API, missing fields, email already exists
- 500: Server error

### POST `/api/users/all`
Body: `{ API }`

Response (200): `Array<User>` — `{ User_id, User_Name, Email, Role, Membership_Type, Status, Cost }`

### GET `/api/users/getbyid`
Uses `req.user.id` from JWT (no body needed)

Response (200): `{ User_id, User_Name, Email, Role, Membership_Type, Status }`

### POST `/api/users/getmemberbyid`
Body: `{ ID }`

Response (200): `{ User_id, User_Name, Email, Role, Membership_Type, Status }`

### POST `/api/users/update`
Body: `{ ID, User_Name, Email, Role, Membership_Type }`

Response (200): `{ message: "User updated successfully" }`

### POST `/api/users/deactivate`
Body: `[id1, id2, ...]` (array of User_ids)

Checks if any user already deactivated → returns 405

Response (200): `{ message: "Users deactivated successfully" }`

### POST `/api/users/activate`
Body: `{ ID }`

Response (200): `{ message: "User activated successfully" }`

### DELETE `/api/users/delete`
Body: `[id1, id2, ...]` (array of User_ids)

Response (200): `{ message: "Users deleted successfully" }`

### PUT `/api/users/changepassword`
Uses `req.user.id` from JWT

Body: `{ OldPassword, NewPassword }`

Verifies old password via bcrypt before updating

Response (200): `{ message: "Password changed successfully" }`

---

## Book Endpoints

### POST `/api/books/insert`
Body: `{ API, Book_Title, Author, Category, Language, Total_Copies, Status, Pages, Price }`

Auto-generates: `Book_ID` (UUID), `Available` (same as Total_Copies)

Response (201): `{ message: "Book Added successfully" }`

### POST `/api/books/get`
Body: `{ API }`

Response (200): `Array<Book>` — all books

### POST `/api/books/getbyID`
Body: `{ ID }`

Response (200): `Array<Book>` (single element)

### PUT `/api/books/update`
Body: `{ Book_ID, Book_Title, Author, Category, Language, Total_Copies, Status, Pages, Price }`

Auto-adjusts: `Available = oldAvailable + newTotalCopies - oldTotalCopies`

Response (200): `{ message: "Book updated successfully" }`

### DELETE `/api/books/delete`
Body: `[id1, id2, ...]` (array of Book_IDs)

Response (200): `{ message: "Books deleted successfully" }`

### POST `/api/books/col`
Body: `{ column: string[] }`

Returns distinct values for specified columns

Response (200): `Array<object>`

---

## Lender / Borrowing Endpoints

### POST `/api/lenders/all`
Body: `{ API }`

Response (200): `Array<Borrower>` — all records from `borrower` table

### POST `/api/lenders/getlenderbyid`
Body: `{ ID }`

Joins email from Users table

Response (200): `{ Borrower record with Email field }`

Errors:
- 404: Lender not found, user was deleted
- 500: Server error

### POST `/api/lenders/insert`
**Core lending operation**

Body: `{ Lendername, Email, BookTitle, Author, Category, IssuedDate, DueDate, CopiesLent, Fine, Role, PhoneNumber }`

Logic:
1. Checks if user exists by email; creates account with random password if not
2. Looks up book by title+author+category to get Price, Book_ID, Available
3. Validates available copies >= requested copies
4. Decrements available copies; sets status to "Out of stock" if 0
5. If new user: inserts into Users, generates password reset token
6. If existing user: updates Cost (adds Price * Copies)
7. Inserts into `borrower` table with "Not Returned" status
8. Sends email (text + HTML) with book details + password change link (if new user)

Response (200): `{ message: "Book issued successfully" }`

Errors:
- 400: Not enough copies, user deactivated

---

## Password Reset (OTP Flow)

### POST `/api/mail/otp`
Body: `{ Name, Email }`

Generates 6-digit OTP → stores in `OPPS` table → sends email → auto-deletes after 3 min

Response (200): `{ message: "OTP sent successfully" }`

### POST `/api/mail/verify`
Body: `{ Email, OTP }`

Deletes OTP if valid

Response (200): `{ message: "OTP verified successfully" }` | 400: Invalid OTP

### POST `/api/mail/resend`
Body: `{ Name, Email }`

Replaces existing OTP with new one

Response (200): `{ message: "OTP sent successfully" }`

### POST `/api/mail/reset`
Body: `{ ID, Email, NewPassword }`

bcrypt hashes new password

Response (200): `{ message: "Password updated successfully" }` | 400: Failed to update

---

## Password Reset (Token Flow)

### POST `/api/token/verify`
**No auth required**

(Internal — token generated by `tokengenerator.js` when lending to new user)

Body: `{ token }` (inferred)

Returns user info if token valid and not expired

### PUT `/api/token/update`
**No auth required**

Body: `{ token, newPassword }` (inferred)

Hashes password, updates user, deletes used token

---

## Notification Endpoints

### POST `/api/notifications/add`
Uses `req.user.id` from JWT

Body: `{ Message }`

Auto-sets: `CreatedAt` (PKT timezone), `IsRead` (false)
Auto-deletes old read notifications

Response (200): `{ message: "Notification added successfully" }`

### GET `/api/notifications/get`
Uses `req.user.id` from JWT

Response (200): `Array<Notification>` — unread only, ordered by CreatedAt DESC

### POST `/api/notifications/markasread`
Uses `req.user.id` from JWT

Body: `{ NotificationId }` (optional — if absent, marks all as read)

Response (200): `{ message: "Notification(s) marked as read" }`

---

## Other Endpoints

### GET `/api/other/getbookdata`
No body required

Response (200):
```json
{
  "Totalbooks": number,
  "Totalusers": number,
  "Totalborrowers": number,
  "availablebooks": number,
  "overduebooks": number
}
```

### POST `/api/resource/add`
Body: `{ Name, Email, Website }`

Response (201): Resource created

### POST `/api/mail/send-email`
Body: `{ to, subject, text, html }` (inferred from mailer)

Generic email sender using Gmail API

### GET `/`
Response (200): `✅ App is alive!`
