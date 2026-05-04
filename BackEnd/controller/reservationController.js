const { poolPromise } = require('../models/db');
const { sendEmail } = require('./mailer');
const { addNotificationHelper } = require('./notificationscontroller');

// Reserve a book
exports.reservebook = async (req, res) => {
    try {
        const { book_id, reservation_date } = req.body;
        const userId = req.user?.id;

        if (!book_id || !userId) {
            return res.status(400).json({ error: 'Book ID is required' });
        }

        const pool = await poolPromise;

        // Insert reservation
        await pool.request()
            .input('user_id', userId)
            .input('book_id', book_id)
            .input('reserved_date', reservation_date || new Date().toISOString().split('T')[0])
            .query('INSERT INTO reserved (User_ID, Book_ID, Reserved_Date) VALUES (@user_id, @book_id, @reserved_date)');

        // Update book status to Reserved
        await pool.request()
            .input('book_id', book_id)
            .query("UPDATE books SET Status = 'Reserved' WHERE Book_ID = @book_id");

        // Get user and book details for email
        const userResult = await pool.request()
            .input('user_id', userId)
            .query('SELECT User_Name, Email FROM users WHERE User_id = @user_id');

        const bookResult = await pool.request()
            .input('book_id', book_id)
            .query('SELECT Book_Title FROM books WHERE Book_ID = @book_id');

        if (userResult.recordset.length > 0 && bookResult.recordset.length > 0) {
            const user = userResult.recordset[0];
            const book = bookResult.recordset[0];

            // Send confirmation email
            const htmlBody = `<!doctype html>
<html>
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width,initial-scale=1">
  <title>Reservation Confirmed</title>
</head>
<body style="font-family: Arial, sans-serif; margin:0; padding:0; background:#f4f4f6;">
  <table width="100%" cellpadding="0" cellspacing="0" role="presentation">
    <tr>
      <td align="center" style="padding:20px 10px;">
        <table width="600" cellpadding="0" cellspacing="0" role="presentation" style="background:#ffffff; border-radius:8px; overflow:hidden; box-shadow:0 2px 6px rgba(0,0,0,0.08);">
          <!-- Header -->
          <tr>
            <td style="padding:20px; text-align:left; background:#2b6cb0; color:#ffffff;">
              <h1 style="margin:0; font-size:20px;">Reservation Confirmed</h1>
            </td>
          </tr>

          <!-- Body -->
          <tr>
            <td style="padding:24px;">
              <p style="margin:0 0 16px 0; font-size:15px; color:#333333;">
                Hi <strong>${user.User_Name}</strong>,
              </p>

              <p style="margin:0 0 18px 0; font-size:15px; color:#333333;">
                Thanks — your reservation for <strong>"${book.Book_Title}"</strong> is confirmed.
              </p>

              <table cellpadding="0" cellspacing="0" role="presentation" style="width:100%; margin:12px 0;">
                <tr>
                  <td style="padding:10px; background:#f7fafc; border-radius:6px; font-size:14px; color:#333;">
                    <strong>Time Period:</strong> 1 Day<br>
                  </td>
                </tr>
              </table>

              <p style="margin:0 0 18px 0; font-size:14px; color:#333;">
                When a copy becomes available, we'll issue it to you.
              </p>

              <p style="margin:0 0 8px 0; font-size:14px; color:#333;">
                If you want to cancel the reservation or have questions, reply to this email or contact the library staff.
              </p>

              <p style="margin:24px 0 0 0; font-size:13px; color:#777;">
               XLMS
              </p>
            </td>
          </tr>

          <!-- Footer -->
          <tr>
            <td style="padding:14px 20px; background:#f1f5f9; font-size:12px; color:#666;">
              This is an automated message. Please do not reply to this email address.
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</body>
</html>`;

            const textBody = `Hi ${user.User_Name},\n\nYour reservation for '${book.Book_Title}' is confirmed.\n\nWhen a copy becomes available, we'll issue it to you.\n\nIf you want to cancel the reservation or have questions, reply to this email or contact the library staff.\n\nXLMS`;

            await sendEmail(user.Email, 'Your Book Reservation is Confirmed', textBody, htmlBody);
        }

        // Add notification
        addNotificationHelper(userId, 'Your Book Reservation is Confirmed');

        res.json({ message: 'Book reserved successfully' });
    } catch (err) {
        console.error('Error reserving book:', err);
        res.status(500).json({ error: 'Internal Server Error' });
    }
};

// Get reservations for a user
exports.getreservations = async (req, res) => {
    try {
        const userId = req.user?.id;

        if (!userId) {
            return res.status(401).json({ error: 'User ID is required' });
        }

        const pool = await poolPromise;

        const result = await pool.request()
            .input('user_id', userId)
            .query('SELECT * FROM reserved WHERE User_ID = @user_id');

        res.json(result.recordset);
    } catch (err) {
        console.error('Error fetching reservations:', err);
        res.status(500).json({ error: 'Internal Server Error' });
    }
};
