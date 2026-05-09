const { poolPromise } = require('../models/db');
const bycrypt = require('bcrypt');
const { sendEmail } = require('./mailer');
exports.OTP = async (req, res) => {
    const { Name, Email } = req.body;
    try {
        const pool = await poolPromise;
        const OTP = Math.floor(100000 + Math.random() * 900000).toString();
        const EmailTemplate =
            `<!DOCTYPE html>
        <html lang="en">
        <head>
        <meta charset="UTF-8" />
        <title></title>
        <style>
            body {
            margin: 0;
            padding: 0;
            font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
            color: #333;
            background-color: #fff;
            }

            .container {
            margin: 0 auto;
            width: 100%;
            max-width: 600px;
            padding: 0 0px;
            padding-bottom: 10px;
            border-radius: 5px;
            line-height: 1.8;
            }

            .header {
            border-bottom: 1px solid #eee;
            }

            .header a {
            font-size: 1.4em;
            color: #000;
            text-decoration: none;
            font-weight: 600;
            }

            .content {
            min-width: 700px;
            overflow: auto;
            line-height: 2;
            }

            .otp {
            background: linear-gradient(to right, #00bc69 0, #00bc88 50%, #00bca8 100%);
            margin: 0 auto;
            width: max-content;
            padding: 0 10px;
            color: #fff;
            border-radius: 4px;
            font-size: 2em;
            }

            .footer {
            color: #aaa;
            font-size: 0.8em;
            line-height: 1;
            font-weight: 300;
            }

            .email-info {
            color: #666666;
            font-weight: 400;
            font-size: 13px;
            line-height: 18px;
            padding-bottom: 6px;
            }

            .email-info a {
            text-decoration: none;
            color: #00bc69;
            }
        </style>
        </head>

        <body>
       
        <div class="container">
            <div class="header">
            <a>XLMS Password Reset</a>
            </div>
            <br />
            <strong>Dear ${Name},</strong>
            <p>
            We have received a Password Reset request for your XLMS account. For
            security purposes, please verify your identity by providing the
            following One-Time Password (OTP).
            <br />
            <b>Your One-Time Password (OTP) verification code is:</b>
            </p>
            <h2 class="otp">${OTP}</h2>
            <p style="font-size: 0.9em">
            <strong>One-Time Password (OTP) is valid for 3 minutes.</strong>
            <br />
            <br />
            If you did not initiate this  request, please disregard this
            message. Please ensure the confidentiality of your OTP and do not share
            it with anyone.<br />
            <strong>Do not forward or give this code to anyone.</strong>
            <br />
            <br />
            <strong>Thank you for using XLMS.</strong>
            <br />
            <br />
            Best regards,
            <br />
            <strong>XLMS</strong>
            </p>

            <hr style="border: none; border-top: 0.5px solid #131111" />
            <div class="footer">
            <p>This email can't receive replies.</p>
            
            </div>
        </div>
        <div style="text-align: center">
            <div class="email-info">
            <span>
                This email was sent to
                <a href="mailto:${Email}">${Email}</a>
            </span>
            </div>
        
            <div class="email-info">
            &copy; 2025 XLMS. All rights
            reserved.
            </div>
        </div>
        </body>
        </html>`
        const text = `XLMS Password Reset

        Dear ${Name},

        We have received a Password Reset request for your XLMS account. For security purposes, please verify your identity by providing the following One-Time Password (OTP).

        Your One-Time Password (OTP) verification code is:
        ${OTP}

        One-Time Password (OTP) is valid for 3 minutes.

        If you did not initiate this request, please disregard this message. Please ensure the confidentiality of your OTP and do not share it with anyone.
        Do not forward or give this code to anyone.

        Thank you for using XLMS.

        Best regards,  
        XLMS

        ----------------------------------------

        This email can't receive replies.

        This email was sent to: ${Email}

        © 2025 XLMS. All rights reserved.`;

        const subject = 'XLMS Password Reset OTP';
        const request = await pool
            .request()
            .input('Email', Email)
            .input('OTP', OTP)
            .query('INSERT INTO OTPS (Email, OTPCode) VALUES (@Email, @OTP)');
        await sendEmail(Email, subject, text, EmailTemplate);
        setTimeout(async () => {
            await pool.request()
                .input('Email', Email)
                .query('DELETE FROM OTPS WHERE Email = @Email');
        }, 180000);
        res.status(200).json({ message: 'OTP sent successfully' });
    }
    catch (err) {
        return res.status(500).json({ error: 'Internal Server Error' });
    }
}
exports.resendotp = async (req, res) => {
    const { Name, Email } = req.body;
    try {
        const pool = await poolPromise;
        const OTP = Math.floor(100000 + Math.random() * 900000).toString();
        const EmailTemplate =
            `<!DOCTYPE html>
        <html lang="en">
        <head>
        <meta charset="UTF-8" />
        <title></title>
        <style>
            body {
            margin: 0;
            padding: 0;
            font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
            color: #333;
            background-color: #fff;
            }

            .container {
            margin: 0 auto;
            width: 100%;
            max-width: 600px;
            padding: 0 0px;
            padding-bottom: 10px;
            border-radius: 5px;
            line-height: 1.8;
            }

            .header {
            border-bottom: 1px solid #eee;
            }

            .header a {
            font-size: 1.4em;
            color: #000;
            text-decoration: none;
            font-weight: 600;
            }

            .content {
            min-width: 700px;
            overflow: auto;
            line-height: 2;
            }

            .otp {
            background: linear-gradient(to right, #00bc69 0, #00bc88 50%, #00bca8 100%);
            margin: 0 auto;
            width: max-content;
            padding: 0 10px;
            color: #fff;
            border-radius: 4px;
            font-size: 2em;
            }

            .footer {
            color: #aaa;
            font-size: 0.8em;
            line-height: 1;
            font-weight: 300;
            }

            .email-info {
            color: #666666;
            font-weight: 400;
            font-size: 13px;
            line-height: 18px;
            padding-bottom: 6px;
            }

            .email-info a {
            text-decoration: none;
            color: #00bc69;
            }
        </style>
        </head>

        <body>
       
        <div class="container">
            <div class="header">
            <a>XLMS Password Reset</a>
            </div>
            <br />
            <strong>Dear ${Name},</strong>
            <p>
            We have received a Password Reset request for your XLMS account. For
            security purposes, please verify your identity by providing the
            following One-Time Password (OTP).
            <br />
            <b>Your One-Time Password (OTP) verification code is:</b>
            </p>
            <h2 class="otp">${OTP}</h2>
            <p style="font-size: 0.9em">
            <strong>One-Time Password (OTP) is valid for 3 minutes.</strong>
            <br />
            <br />
            If you did not initiate this  request, please disregard this
            message. Please ensure the confidentiality of your OTP and do not share
            it with anyone.<br />
            <strong>Do not forward or give this code to anyone.</strong>
            <br />
            <br />
            <strong>Thank you for using XLMS.</strong>
            <br />
            <br />
            Best regards,
            <br />
            <strong>XLMS</strong>
            </p>

            <hr style="border: none; border-top: 0.5px solid #131111" />
            <div class="footer">
            <p>This email can't receive replies.</p>
            
            </div>
        </div>
        <div style="text-align: center">
            <div class="email-info">
            <span>
                This email was sent to
                <a href="mailto:${Email}">${Email}</a>
            </span>
            </div>
        
            <div class="email-info">
            &copy; 2025 XLMS. All rights
            reserved.
            </div>
        </div>
        </body>
        </html>`
        const text = `XLMS Password Reset

        Dear ${Name},

        We have received a Password Reset request for your XLMS account. For security purposes, please verify your identity by providing the following One-Time Password (OTP).

        Your One-Time Password (OTP) verification code is:
        ${OTP}

        One-Time Password (OTP) is valid for 3 minutes.

        If you did not initiate this request, please disregard this message. Please ensure the confidentiality of your OTP and do not share it with anyone.
        Do not forward or give this code to anyone.

        Thank you for using XLMS.

        Best regards,  
        XLMS

        ----------------------------------------

        This email can't receive replies.

        This email was sent to: ${Email}

        © 2025 XLMS. All rights reserved.`;

        const subject = 'XLMS Password Reset OTP';
        await pool.request()
            .input('Email', Email)
            .query('DELETE FROM OTPS WHERE Email = @Email');
        const request = await pool
            .request()
            .input('Email', Email)
            .input('OTP', OTP)
            .query('INSERT INTO OTPS (Email, OTPCode) VALUES (@Email, @OTP)');
        await sendEmail(Email, subject, text, EmailTemplate);
        setTimeout(async () => {
            await pool.request()
                .input('Email', Email)
                .query('DELETE FROM OTPS WHERE Email = @Email');
        }, 180000);
        res.status(200).json({ message: 'OTP sent successfully' });
    }
    catch {
        return res.status(500).json({ error: 'Internal Server Error' });
    }
}
exports.verifyotp = async (req, res) => {
    const { Email, OTP } = req.body;
    try {
        const pool = await poolPromise;
        const request = await pool
            .request()
            .input('Email', Email)
            .input('OTP', OTP)
            .query('SELECT * FROM OTPS WHERE Email = @Email AND OTPCode = @OTP');
        if (request.recordset.length > 0) {
            await pool.request()
                .input('Email', Email)
                .query('DELETE FROM OTPS WHERE Email = @Email');
            return res.status(200).json({ message: 'OTP verified successfully' });
        } else {
            return res.status(400).json({ error: 'Invalid OTP' });
        }
    } catch (err) {
        return res.status(500).json({ error: 'Internal Server Error' });
    }
}

exports.issue_mail = async (req, res) => {
    const { name, sender, subject, issue } = req.body;
    try {
        const admin_html = `
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
    <title>New Support Ticket</title>
  </head>
  <body style="font-family: Arial, sans-serif; background-color: #f6f7fb; padding: 30px; color: #333;">
    <table style="max-width: 600px; margin: 0 auto; background: #ffffff; border-radius: 10px; box-shadow: 0 2px 6px rgba(0,0,0,0.1);" cellpadding="0" cellspacing="0">
      <tr>
        <td style="padding: 25px;">
          <h2 style="color: #6941c5;">📩 New Support Ticket Received</h2>
          <p style="font-size: 15px;">A new support ticket has been submitted with the following details:</p>

          <table style="width: 100%; margin-top: 15px; border-collapse: collapse;">
            <tr><td style="padding: 8px 0; font-weight: bold;">Name:</td><td style="padding: 8px 0;">${name}</td></tr>
            <tr><td style="padding: 8px 0; font-weight: bold;">Email:</td><td style="padding: 8px 0;">${sender}</td></tr>
            <tr><td style="padding: 8px 0; font-weight: bold;">Subject:</td><td style="padding: 8px 0;">${subject}</td></tr>
          </table>

          <div style="margin-top: 15px; background-color: #f4f4f4; padding: 15px; border-radius: 6px;">
            <p style="margin: 0; font-size: 15px; white-space: pre-line;">${issue}</p>
          </div>

          <p style="margin-top: 20px; font-size: 14px; color: #777;">
            Please respond to this ticket at your earliest convenience.
          </p>
        </td>
      </tr>
    </table>
  </body>
</html>
`;
        const user_html = `
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
    <title>Ticket Confirmation</title>
  </head>
  <body style="font-family: Arial, sans-serif; background-color: #f6f7fb; padding: 30px; color: #333;">
    <table style="max-width: 600px; margin: 0 auto; background: #ffffff; border-radius: 10px; box-shadow: 0 2px 6px rgba(0,0,0,0.1);" cellpadding="0" cellspacing="0">
      <tr>
        <td style="padding: 25px;">
          <h2 style="color: #6941c5;">✅ Ticket Submitted Successfully</h2>
          <p style="font-size: 15px;">Hi <strong>${name}</strong>,</p>
          <p style="font-size: 15px;">Your support ticket has been received. Our team will review your issue and get back to you soon.</p>

          <h4 style="margin-top: 20px; color: #6941c5;">Ticket Details:</h4>
          <ul style="line-height: 1.7; font-size: 15px; padding-left: 20px;">
            <li><strong>Subject:</strong> ${subject}</li>
            <li><strong>Issue:</strong> ${issue}</li>
          </ul>

          <p style="margin-top: 25px; font-size: 14px; color: #777;">
            Thank you for contacting support.<br/>
            — The Support Team
          </p>
        </td>
      </tr>
    </table>
  </body>
</html>
`;
        const user_text = `
✅ Ticket Submitted Successfully

Hi ${name},

Your support ticket has been received. Our team will review your issue and get back to you soon.

Ticket Details:
- Subject: ${subject}
- Issue: ${issue}

Thank you for contacting support.
— The Support Team
`;
        const admin_text = `
📩 New Support Ticket Submitted

A new support ticket has been submitted.

Ticket Details:
- Name: ${name}
- Sender Email: ${sender}
- Subject: ${subject}
- Issue: ${issue}

Please review and respond to this ticket in the admin dashboard.
— Automated Notification System
`;

        await sendEmail(sender, subject, user_text, user_html);
        await sendEmail("moeez66656@gmail.com", subject, admin_text, admin_html);

        // Add notification for the user if they exist in the database
        const pool = await poolPromise;
        const userResult = await pool.request()
            .input('email', sender)
            .query('SELECT User_id FROM users WHERE Email = @email');
        
        if (userResult.recordset.length > 0) {
            await addNotificationHelper(userResult.recordset[0].User_id, `Your support ticket "${subject}" has been submitted successfully.`);
        }

        return res.status(200).json({ message: "Email sent successfully" });
    } catch (e) {
        return res.status(500).json({ error: `Failed to send email: ${e.message}` });
    }
};

// ==================== HELPER FUNCTIONS ====================

// Helper function to remove OTP from database
exports.otpremove = async (email) => {
    try {
        const pool = await poolPromise;
        await pool.request()
            .input('email', email)
            .query('DELETE FROM OTPS WHERE Email = @email');
    } catch (err) {
        console.error('Error removing OTP:', err);
    }
};