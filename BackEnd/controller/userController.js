const { poolPromise } = require('../models/db');
const bcrypt = require("bcrypt");
const { v4: uuidv4 } = require('uuid');
const { generatetoken } = require('./tokengenerator');
const { sendEmail } = require('./mailer');

exports.createUser = async (req, res) => {
  try {
    const { User_Name, Email, Role, Membership_Type, Password } = req.body;

    if (!User_Name || !Email || !Role || !Membership_Type || !Password)
      return res.status(400).json({ error: 'All fields are required' });
    const pool = await poolPromise;
    const existingUserResult = await pool.request()
      .input('email', Email)
      .query('SELECT COUNT(*) AS count FROM users WHERE email = @email');

    if (existingUserResult.recordset[0].count > 0) {
      return res.status(400).json({ error: 'User with this email already exists' });
    }

    const hashedPassword = await bcrypt.hash(Password, 10);

    const userId = `${User_Name[0].toUpperCase()}${uuidv4().replace(/-/g, "").slice(0, 8)}`;
    const result = await pool.request()
      .input('User_id', userId)
      .input('User_Name', User_Name)
      .input('Email', Email)
      .input('Role', Role)
      .input('Membership_Type', Membership_Type)
      .input('Password', hashedPassword)
      .input('Cost', 0)
      .input('Status', 'Active')
      .query('INSERT INTO users (User_id, User_Name, Email, Role, Membership_Type, Password, Cost, Status) VALUES (@User_id, @User_Name, @Email, @Role, @Membership_Type, @Password, @Cost, @Status)');
    res.status(201).json({ message: 'User created successfully' });
  } catch (err) {
    console.error('Error creating user:', err);
    res.status(500).json({ error: 'Internal Server Error' });
  }
};

exports.getAllUsers = async (req, res) => {
  try {
    const pool = await poolPromise;
    const result = await pool.request().query('SELECT User_id, User_Name, Email, 	Role, Membership_Type,Status, Cost FROM users');
    res.json(result.recordset);
  } catch (err) {
    console.error('Error fetching users:', err);
    res.status(500).json({ error: 'Internal Server Error' });
  }
};
exports.getuserbyid = async (req, res) => {
  try {

    const pool = await poolPromise;
    const result = await pool
      .request()
      .input('ID', req.user.id)
      .query('SELECT  User_Name, Email, Role, Membership_Type,Status FROM users WHERE User_id = @ID');
    res.json({
      ...result.recordset[0],
      User_id: req.user.id
    });
  } catch (err) {
    console.error('Error fetching user by ID:', err);
    res.status(500).json({ error: 'Internal Server Error' });
  }
}
exports.getmemberbyid = async (req, res) => {
  try {
    const { ID } = req.body;
    const pool = await poolPromise;
    const result = await pool
      .request()
      .input('ID', ID)
      .query('SELECT  User_Name, Email, Role, Membership_Type,Status FROM users WHERE User_id = @ID');
    res.json({
      ...result.recordset[0],
      User_id: ID
    });
  } catch (err) {
    console.error('Error fetching user by ID:', err);
    res.status(500).json({ error: 'Internal Server Error' });
  }
}
exports.updateuser = async (req, res) => {
  const { ID, User_Name, Email, Role, Membership_Type } = req.body;
  try {
    if (!ID) {
      return res.status(400).json({ error: 'User ID is required' });
    }
    const pool = await poolPromise;
    const request = pool.request();
    request.input('ID', ID);

    let updates = [];
    if (User_Name) { request.input('User_Name', User_Name); updates.push('User_Name = @User_Name'); }
    if (Email) { request.input('Email', Email); updates.push('Email = @Email'); }
    if (Role) { request.input('Role', Role); updates.push('Role = @Role'); }
    if (Membership_Type) { request.input('Membership_Type', Membership_Type); updates.push('Membership_Type = @Membership_Type'); }

    if (updates.length === 0) {
      return res.status(400).json({ error: 'No fields provided for update' });
    }

    const query = `UPDATE users SET ${updates.join(', ')} WHERE User_id = @ID`;
    await request.query(query);
    res.json({ message: 'User updated successfully' });
  } catch (err) {
    console.error('Error updating user:', err);
    res.status(500).json({ error: 'Internal Server Error' });
  }
}
exports.deactivateUser = async (req, res) => {
  const id_arr = req.body;
  if (!Array.isArray(id_arr) || id_arr.length === 0) {
    return res.status(400).json({ error: 'ID array is required and cannot be empty' });
  }

  try {
    const pool = await poolPromise;
    const request = pool.request();

    const idParams = id_arr.map((id, index) => {
      const paramName = `id${index}`;
      request.input(paramName, id);
      return `@${paramName}`;
    });
    const quer = `select Status from users where User_id IN (${idParams.join(',')})`
    const result = await request.query(quer);

    for (const item of result.recordset) {
      if (item.Status === 'Deactivated') {
        return res.status(405).json({ error: 'User is already deactivated' });
      }
    }
    const query = `UPDATE users SET Status = 'Deactivated' WHERE User_id IN (${idParams.join(',')})`;

    await request.query(query);

    res.json({ message: 'Users deactivated successfully' });
  } catch (err) {
    res.status(500).json({ error: 'Internal Server Error' });
  }
}
exports.activateUser = async (req, res) => {
  const { ID } = req.body
  if (!ID) {
    return res.status(400).json({ error: 'ID is required' });
  }

  try {
    const pool = await poolPromise;
    const result = await pool
      .request()
      .input('ID', ID)
      .query("UPDATE users SET Status = 'Active' WHERE User_id = @ID");
    res.json({ message: 'User activated successfully' });
  } catch (err) {
    console.error('Error activating user:', err);
    res.status(500).json({ error: 'Internal Server Error' });
  }
}
exports.deleteaccount = async (req, res) => {
  const id_arr = req.body;
  if (!Array.isArray(id_arr) || id_arr.length === 0) {
    return res.status(400).json({ error: 'ID array is required and cannot be empty' });
  }

  try {
    const pool = await poolPromise;
    const request = pool.request();
    const idParams = id_arr.map((id, index) => {
      const paramName = `id${index}`;
      request.input(paramName, id);
      return `@${paramName}`;
    });
    const query = `DELETE FROM users WHERE User_id IN (${idParams.join(',')})`;
    await request.query(query);
    res.json({ message: 'Users deleted successfully' });
  } catch (err) {
    console.error('Error deleting users:', err);
    res.status(500).json({ error: 'Internal Server Error' });
  }
}
exports.changepassword = async (req, res) => {
  const { OldPassword, NewPassword } = req.body;
  if (!OldPassword || !NewPassword) {
    return res.status(400).json({ error: 'All fields are required' });
  }
  try {
    const pool = await poolPromise;
    const userResult = await pool.request()
      .input('ID', req.user.id)
      .query('SELECT Password FROM users WHERE User_id = @ID');

    if (userResult.recordset.length === 0) {
      return res.status(404).json({ error: 'User not found' });
    }

    const user = userResult.recordset[0];
    const isMatch = await bcrypt.compare(OldPassword, user.Password);
    if (!isMatch) {
      return res.status(400).json({ error: 'Invalid Password' });
    }

    const hashedNewPassword = await bcrypt.hash(NewPassword, 10);
    await pool.request()
      .input('ID', req.user.id)
      .input('NewPassword', hashedNewPassword)
      .query('UPDATE users SET Password = @NewPassword WHERE User_id = @ID');

    res.json({ message: 'Password changed successfully' });
  } catch (err) {
    console.error('Error changing password:', err);
    res.status(500).json({ error: 'Internal Server Error' });
  }
}
exports.forgotpassword = async (req, res) => {
  const { ID } = req.body;
  if (!ID) {
    return res.status(400).json({ error: 'User ID is required' });
  }
  const Email = await poolPromise.then(pool => pool.request()
    .input('ID', ID)
    .query('SELECT Email FROM users WHERE User_id = @ID')).then(result => {
      if (result.recordset.length === 0) {
        return res.status(404).json({ error: 'User not found' });
      }
      return result.recordset[0].Email;
    });
  const link = await generatetoken(ID);
  sendEmail(Email, 'Password Reset Request', `Hello user, Click the link to reset your password: ${link}`, `<table width="600" cellpadding="0" cellspacing="0" border="0" style="background:#ffffff; border-radius:10px; overflow:hidden; box-shadow:0 4px 12px rgba(0,0,0,0.08);"> <tr> <td style="background:#2563eb; padding:25px; text-align:center; color:#ffffff; font-size:26px; font-weight:bold; font-family:Arial, Helvetica, sans-serif;"> Password Reset Request </td> </tr> <tr> <td style="padding:35px; color:#333333; font-size:16px; line-height:1.7; font-family:Arial, Helvetica, sans-serif;"> <p style="margin-top:0;">Hello User,</p> <p> We received a request to reset your password. Click the button below to create a new password. </p> <p style="text-align:center; margin:35px 0;"> <a href="${link}" style="background:#2563eb; color:#ffffff; text-decoration:none; padding:14px 28px; border-radius:6px; display:inline-block; font-weight:bold; font-family:Arial, Helvetica, sans-serif;"> Reset Password </a> </p> <p> If the button doesn't work, copy and paste this link into your browser: </p> <p style="word-break:break-all; color:#2563eb; font-weight:bold;"> ${link} </p> <p> If you did not request a password reset, you can ignore this email. </p> <p style="margin-bottom:0;"> Regards,<br /> Support Team </p> </td> </tr> <tr> <td style="background:#f9fafb; padding:18px; text-align:center; font-size:13px; color:#777777; font-family:Arial, Helvetica, sans-serif;"> © 2026 XLMS. All rights reserved. </td> </tr> </table> </td>`);
  res.status(200).json({ message: 'Password reset link sent to email' });
}

// ==================== CLIENT APIs ====================

// Login user by email and password
exports.loginUser = async (req, res) => {
  try {
    const { email, password } = req.body;

    if (!email || !password)
      return res.status(400).json({ error: 'Email and password are required' });

    const pool = await poolPromise;
    
    const userResult = await pool.request()
      .input('email', email)
      .query("SELECT * FROM users WHERE Email = @email AND Role = 'Standard-User'");

    if (userResult.recordset.length === 0) {
      return res.status(401).json({ error: 'Invalid credentials' });
    }

    const user = userResult.recordset[0];
    const isMatch = await bcrypt.compare(password, user.Password);

    if (!isMatch) {
      return res.status(401).json({ error: 'Invalid credentials' });
    }

    const token = require('jsonwebtoken').sign(
      { user_id: user.User_id, email: user.Email },
      process.env.JWT,
      { expiresIn: '1d' }
    );

    res.cookie('token', token, {
      httpOnly: true,
      secure: true,
      sameSite: 'lax',
      maxAge: 60 * 60 * 24 * 1000 // 1 day
    });

    res.json({ user_id: user.User_id, message: 'Login successful' });
  } catch (err) {
    console.error('Error logging in user:', err);
    res.status(500).json({ error: 'Internal Server Error' });
  }
};

// Signup user (client registration)
exports.signupUser = async (req, res) => {
  try {
    const { name, email, password } = req.body;

    if (!name || !email || !password)
      return res.status(400).json({ error: 'All fields are required' });

    const pool = await poolPromise;

    const existingUser = await pool.request()
      .input('email', email)
      .query('SELECT COUNT(*) AS count FROM users WHERE Email = @email');

    if (existingUser.recordset[0].count > 0) {
      return res.status(400).json({ error: 'User with this email already exists' });
    }

    const hashedPassword = await bcrypt.hash(password, 10);
    const uid = uuidv4();
    const userId = `${name[0].toUpperCase()}${uid.slice(0, 7)}`;

    await pool.request()
      .input('User_id', userId)
      .input('User_Name', name)
      .input('Email', email)
      .input('Password', hashedPassword)
      .input('Role', 'Standard-User')
      .input('Membership_Type', 'English')
      .input('Cost', 0)
      .input('Status', 'Active')
      .query('INSERT INTO users (User_id, User_Name, Email, Password, Role, Membership_Type, Cost, Status) VALUES (@User_id, @User_Name, @Email, @Password, @Role, @Membership_Type, @Cost, @Status)');

    const token = require('jsonwebtoken').sign(
      { user_id: userId, email: email },
      process.env.JWT,
      { expiresIn: '1d' }
    );

    res.cookie('token', token, {
      httpOnly: true,
      secure: true,
      sameSite: 'lax',
      maxAge: 60 * 60 * 24 * 1000 // 1 day
    });

    res.status(201).json({ 
      message: 'User created successfully',
      user_id: userId
    });
  } catch (err) {
    console.error('Error creating user:', err);
    res.status(500).json({ error: 'Internal Server Error' });
  }
};

// Check if email exists
exports.checkEmailExists = async (req, res) => {
  try {
    const { email } = req.body;

    if (!email)
      return res.status(400).json({ error: 'Email is required' });

    const pool = await poolPromise;
    
    const result = await pool.request()
      .input('email', email)
      .query('SELECT COUNT(*) AS count FROM users WHERE Email = @email');

    res.json({ exist: result.recordset[0].count > 0 });
  } catch (err) {
    console.error('Error checking email:', err);
    res.status(500).json({ error: 'Internal Server Error' });
  }
}