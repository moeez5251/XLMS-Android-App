const bcrypt = require('bcrypt');
const { poolPromise } = require('../models/db');
const jwt = require('jsonwebtoken');
const { v4: uuidv4 } = require('uuid');

function generateToken(user) {
  return jwt.sign(
    { id: user.User_id, email: user.Email },
    process.env.JWT,
    { expiresIn: '1h' }
  );
}

function generateRefreshToken(user) {
  return jwt.sign(
    { id: user.User_id, email: user.Email, type: 'refresh' },
    process.env.JWT_REFRESH || process.env.JWT + '_refresh',
    { expiresIn: '7d' }
  );
}

exports.login = async (req, res) => {
  const email = req.body.email?.trim().toLowerCase();
  const password = req.body.password?.trim();

  try {
    const pool = await poolPromise;
    const result = await pool.request()
      .input('email', email)
      .query('SELECT User_id, password, Role, Status FROM Users WHERE LOWER(Email) = LOWER(@email)');

    if (!result.recordset.length) {
      return res.status(401).json({ message: 'Invalid email or password' });
    }

    const user = result.recordset[0];

    const isMatch = await bcrypt.compare(password, user.password);
    if (!isMatch) {
      return res.status(401).json({ message: 'Invalid email or password' });
    }

    if (user.Status === "Deactivated") {
      return res.status(401).json({ message: 'Your account is Deactivated' });
    }

    const token = generateToken(user);
    const refreshToken = generateRefreshToken(user);

    res.cookie("token", token, {
      httpOnly: true,
      sameSite: "None",
      secure: true,
      maxAge: 3600000 // 1 hour
    });

    res.cookie("refreshToken", refreshToken, {
      httpOnly: true,
      sameSite: "None",
      secure: true,
      maxAge: 604800000 // 7 days
    });

    res.json({
      message: 'Login successful',
      token,
      refreshToken,
      userid: user.User_id,
      role: user.Role
    });

  } catch (error) {
    console.error('Login error:', error.message, error.stack);
    res.status(500).json({ message: 'Server error', error: error.message });
  }
};

exports.logout = async (req, res) => {
  try {
    res.clearCookie("token", {
      httpOnly: true,
      sameSite: "None",
      secure: true,
      path: "/",
    });

    res.clearCookie("refreshToken", {
      httpOnly: true,
      sameSite: "None",
      secure: true,
      path: "/",
    });


    res.status(200).json({ message: 'Logout successful' });
  } catch (error) {
    console.error('Logout error:', error.message, error.stack);
    res.status(500).json({ message: 'Server error', error: error.message });
  }
};
