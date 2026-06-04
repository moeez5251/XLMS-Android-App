# API Reference

## Base URL
`http://localhost:5000/api` (development) | Host is configurable via environment configuration

## Authentication & Headers
- Secured endpoints require a valid JWT token in either HTTP-only cookie (`token`) or as a Bearer token in the `Authorization` header.
- **Auto-Refresh**: If the Access Token is near its 1-hour expiry (under 5 minutes) or expired, the backend verifies the client's `refreshToken`. If valid, it returns a new token via the `X-New-Token` response header, which the frontend interceptor captures to update local storage.

---

## Auth Endpoints

### POST `/api/auth/login`
- **Authentication**: None
- **Body**:
  ```json
  {
    "email": "user@example.com",
    "password": "securepassword"
  }
  ```
- **Response (200)**:
  ```json
  {
    "message": "Login successful",
    "token": "JWT_ACCESS_TOKEN",
    "userid": "U_UUID_STRING",
    "role": "ADMIN" 
  }
  ```

### POST `/api/auth/logout`
- **Authentication**: None
- **Response (200)**:
  ```json
  {
    "message": "Logout successful"
  }
  ```

---

## User Endpoints

### POST `/api/users/signup`
- **Authentication**: None
- **Body**:
  ```json
  {
    "User_Name": "John Doe",
    "Email": "john@example.com",
    "Password": "securepassword"
  }
  ```
- **Response (200)**:
  ```json
  {
    "message": "Signup successful"
  }
  ```

### POST `/api/users/exist`
- **Authentication**: None
- **Body**:
  ```json
  {
    "Email": "john@example.com"
  }
  ```
- **Response (200)**:
  ```json
  {
    "exists": true
  }
  ```

### POST `/api/users/auth-users`
- **Authentication**: None
- **Body**:
  ```json
  {
    "email": "john@example.com"
  }
  ```
- **Response (200)**: Returns user verification details.

### POST `/api/users/register` (Admin Flow)
- **Authentication**: Required (JWT)
- **Body**:
  ```json
  {
    "User_Name": "Jane Doe",
    "Email": "jane@example.com",
    "Role": "User",
    "Membership_Type": "English",
    "Password": "tempPassword123"
  }
  ```
- **Response (201)**:
  ```json
  {
    "message": "User created successfully"
  }
  ```

### POST `/api/users/all` (Admin Flow)
- **Authentication**: Required (JWT)
- **Response (200)**:
  ```json
  [
    {
      "User_id": "U8a9b1c2d",
      "User_Name": "Jane Doe",
      "Email": "jane@example.com",
      "Role": "User",
      "Membership_Type": "English",
      "Status": "Active",
      "Cost": 0
    }
  ]
  ```

### GET `/api/users/getbyid`
- **Authentication**: Required (JWT - checks token identity)
- **Response (200)**:
  ```json
  {
    "User_id": "U8a9b1c2d",
    "User_Name": "Jane Doe",
    "Email": "jane@example.com",
    "Role": "User",
    "Membership_Type": "English",
    "Status": "Active"
  }
  ```

### POST `/api/users/update`
- **Authentication**: Required (JWT)
- **Body**:
  ```json
  {
    "ID": "U8a9b1c2d",
    "User_Name": "Jane Updated",
    "Role": "Admin",
    "Membership_Type": "Urdu"
  }
  ```
- **Response (200)**:
  ```json
  {
    "message": "User updated successfully"
  }
  ```

### POST `/api/users/activate`
- **Authentication**: Required (JWT)
- **Body**:
  ```json
  {
    "ID": "U8a9b1c2d"
  }
  ```
- **Response (200)**:
  ```json
  {
    "message": "User activated successfully"
  }
  ```

### POST `/api/users/deactivate`
- **Authentication**: Required (JWT)
- **Body**: `["U8a9b1c2d", "U1a2b3c4d"]` (Array of IDs)
- **Response (200)**:
  ```json
  {
    "message": "Users deactivated successfully"
  }
  ```

### DELETE `/api/users/delete`
- **Authentication**: Required (JWT)
- **Body**: `["U8a9b1c2d"]` (Array of IDs to delete)
- **Response (200)**:
  ```json
  {
    "message": "Users deleted successfully"
  }
  ```

### PUT `/api/users/changepassword`
- **Authentication**: Required (JWT)
- **Body**:
  ```json
  {
    "OldPassword": "currentpassword",
    "NewPassword": "newsecurepassword"
  }
  ```
- **Response (200)**:
  ```json
  {
    "message": "Password changed successfully"
  }
  ```

### POST `/api/users/forgotpassword`
- **Authentication**: None
- **Body**:
  ```json
  {
    "email": "user@example.com"
  }
  ```
- **Response (200)**:
  ```json
  {
    "message": "Reset instructions sent!"
  }
  ```

### POST `/api/users/resetpassword`
- **Authentication**: None
- **Body**:
  ```json
  {
    "Email": "user@example.com",
    "Password": "newsecurepassword"
  }
  ```
- **Response (200)**:
  ```json
  {
    "message": "Password updated successfully"
  }
  ```

---

## Book Endpoints

### POST `/api/books/insert`
- **Authentication**: Required (JWT)
- **Body**:
  ```json
  {
    "Book_Title": "The Great Gatsby",
    "Author": "F. Scott Fitzgerald",
    "Category": "Fiction",
    "Language": "English",
    "Total_Copies": 10,
    "Status": "Available",
    "Pages": 180,
    "Price": 500
  }
  ```
- **Response (201)**:
  ```json
  {
    "message": "Book Added successfully"
  }
  ```

### POST `/api/books/get`
- **Authentication**: Required (JWT)
- **Response (200)**: Returns full array of Book objects.

### POST `/api/books/getbyID`
- **Authentication**: Required (JWT)
- **Body**:
  ```json
  {
    "ID": "BOOK_UUID"
  }
  ```
- **Response (200)**: Returns single-element array containing the matching Book.

### PUT `/api/books/update`
- **Authentication**: Required (JWT)
- **Body**:
  ```json
  {
    "Book_ID": "BOOK_UUID",
    "Book_Title": "The Great Gatsby (Edited)",
    "Author": "F. Scott Fitzgerald",
    "Category": "Fiction",
    "Language": "English",
    "Total_Copies": 12,
    "Status": "Available",
    "Pages": 180,
    "Price": 550
  }
  ```
- **Response (200)**:
  ```json
  {
    "message": "Book updated successfully"
  }
  ```

### DELETE `/api/books/delete`
- **Authentication**: Required (JWT)
- **Body**: `["BOOK_UUID_1", "BOOK_UUID_2"]`
- **Response (200)**:
  ```json
  {
    "message": "Books deleted successfully"
  }
  ```

### POST `/api/books/col`
- **Authentication**: Required (JWT)
- **Body**:
  ```json
  {
    "column": ["Category"]
  }
  ```
- **Response (200)**:
  ```json
  [
    { "Category": "Fiction" },
    { "Category": "Sci-Fi" }
  ]
  ```

### POST `/api/books/lend` (Client Checkout)
- **Authentication**: Required (JWT)
- **Body**:
  ```json
  {
    "book_id": "BOOK_UUID",
    "IssuedDate": "2026-06-04",
    "DueDate": "2026-06-18",
    "CopiesLent": 1,
    "FinePerDay": 50
  }
  ```
- **Response (200)**:
  ```json
  {
    "message": "Book lent successfully"
  }
  ```

---

## Lending & Return (Lenders) Endpoints

### GET `/api/lenders/all` (Admin Flow)
- **Authentication**: Required (JWT)
- **Response (200)**: Returns array of active borrower records.

### POST `/api/lenders/getlenderbyid` (Admin Flow)
- **Authentication**: Required (JWT)
- **Body**:
  ```json
  {
    "ID": "BORROWER_ID_INTEGER"
  }
  ```
- **Response (200)**: Returns detailed borrower profile, with user email combined.

### POST `/api/lenders/insert` (Admin lending creation)
- **Authentication**: Required (JWT)
- **Body**:
  ```json
  {
    "Email": "borrower@example.com",
    "Lendername": "Borrower Name",
    "PhoneNumber": "12345678",
    "BookTitle": "The Great Gatsby",
    "Author": "F. Scott Fitzgerald",
    "Category": "Fiction",
    "IssuedDate": "2026-06-04",
    "DueDate": "2026-06-18",
    "CopiesLent": 1,
    "Fine": 50,
    "Role": "User"
  }
  ```
- **Response (200)**:
  ```json
  {
    "message": "Book issued successfully"
  }
  ```

### GET `/api/lenders/mylendings` (Client active lendings)
- **Authentication**: Required (JWT)
- **Response (200)**: Returns client's active borrow logs.

### POST `/api/lenders/return` (Client Return trigger)
- **Authentication**: Required (JWT)
- **Body**:
  ```json
  {
    "book_id": "BOOK_UUID",
    "borrower_id": "BORROWER_ID_INTEGER"
  }
  ```
- **Response (200)**:
  ```json
  {
    "message": "Book returned successfully"
  }
  ```

---

## Mail & Support Endpoints

### POST `/api/mail/otp`
- **Authentication**: None
- **Body**:
  ```json
  {
    "Name": "User Name",
    "Email": "user@example.com"
  }
  ```
- **Response (200)**:
  ```json
  {
    "message": "OTP sent successfully"
  }
  ```

### POST `/api/mail/verify`
- **Authentication**: None
- **Body**:
  ```json
  {
    "Email": "user@example.com",
    "OTP": "123456"
  }
  ```
- **Response (200)**:
  ```json
  {
    "message": "OTP verified successfully"
  }
  ```

### POST `/api/mail/resend`
- **Authentication**: None
- **Body**:
  ```json
  {
    "Name": "User Name",
    "Email": "user@example.com"
  }
  ```
- **Response (200)**:
  ```json
  {
    "message": "OTP sent successfully"
  }
  ```

### POST `/api/mail/issue-mail` (Support ticket email)
- **Authentication**: Required (JWT)
- **Body**:
  ```json
  {
    "name": "User Name",
    "sender": "user@example.com",
    "subject": "App Issue",
    "issue": "Detailed description of the issue."
  }
  ```
- **Response (200)**:
  ```json
  {
    "message": "Email sent successfully"
  }
  ```

---

## Book Reservation Endpoints

### POST `/api/reservations/reserve`
- **Authentication**: Required (JWT)
- **Body**:
  ```json
  {
    "book_id": "BOOK_UUID",
    "reservation_date": "2026-06-04"
  }
  ```
- **Response (200)**:
  ```json
  {
    "message": "Book reserved successfully"
  }
  ```

### GET `/api/reservations/myreservations`
- **Authentication**: Required (JWT)
- **Response (200)**: Returns user's reservations array.

---

## Resource Endpoints

### POST `/api/resource/add` (Admin resource addition)
- **Authentication**: Required (JWT)
- **Body**:
  ```json
  {
    "Name": "Resource Library",
    "Email": "library@example.com",
    "Website": "https://resource-library.org"
  }
  ```
- **Response (200)**:
  ```json
  {
    "message": "Resource added successfully"
  }
  ```

---

## Notification Endpoints

### GET `/api/notifications/get`
- **Authentication**: Required (JWT)
- **Response (200)**: Returns user's Notifications list.

### POST `/api/notifications/markasread`
- **Authentication**: Required (JWT)
- **Body**: `{}` (Empty JSON object marks all as read)
- **Response (200)**:
  ```json
  {
    "message": "Notification(s) marked as read"
  }
  ```

---

## Analytics & Statistics Endpoints

### GET `/api/other/getbookdata` (Admin counters)
- **Authentication**: Required (JWT)
- **Response (200)**:
  ```json
  {
    "Totalbooks": 50,
    "Totalusers": 20,
    "Totalborrowers": 15,
    "availablebooks": 35,
    "overduebooks": 2
  }
  ```

### GET `/api/other/mystats` (Client metrics)
- **Authentication**: Required (JWT)
- **Response (200)**:
  ```json
  {
    "lended": 2,
    "overdue": 0,
    "reserved": 1
  }
  ```

### GET `/api/other/chartdetails` (Client circular chart)
- **Authentication**: Required (JWT)
- **Response (200)**:
  ```json
  {
    "returned": 5,
    "overdue": 1
  }
  ```

### GET `/api/other/lendingactivity` (Client bar chart monthly maps)
- **Authentication**: Required (JWT)
- **Response (200)**:
  ```json
  {
    "January": 3,
    "February": 1,
    "March": 5,
    "April": 0,
    "May": 2,
    "June": 4,
    "July": 1,
    "August": 0,
    "September": 3,
    "October": 2,
    "November": 4,
    "December": 1
  }
  ```

---

## General Heartbeat

### GET `/`
- **Authentication**: None
- **Response (200)**: `✅ App is alive!`
