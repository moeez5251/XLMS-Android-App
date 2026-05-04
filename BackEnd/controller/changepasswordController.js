const { poolPromise } = require('../models/db');
const bcrypt = require('bcrypt');
const { addNotificationHelper } = require('./notificationscontroller');

// Change password with OTP
exports.changebyotp = async (req, res) => {
    try {
        const { password } = req.body;
        const userId = req.user?.id;

        if (!password || !userId) {
            return res.status(400).json({ error: 'Password is required' });
        }

        const pool = await poolPromise;

        // Get current password from database
        const userResult = await pool.request()
            .input('user_id', userId)
            .query('SELECT Password FROM users WHERE User_id = @user_id');

        if (userResult.recordset.length === 0) {
            return res.status(404).json({ error: 'User not found' });
        }

        const currentPassword = userResult.recordset[0].Password;

        // Check if new password is different from old
        const isSamePassword = await bcrypt.compare(password, currentPassword);
        if (isSamePassword) {
            return res.status(400).json({ error: 'New password must be different from old password' });
        }

        // Hash new password
        const hashedPassword = await bcrypt.hash(password, 10);

        // Update password
        await pool.request()
            .input('user_id', userId)
            .input('password', hashedPassword)
            .query('UPDATE users SET Password = @password WHERE User_id = @user_id');

        // Add notification
        addNotificationHelper(userId, 'Your password has been changed');

        res.json({ message: 'Password changed successfully' });
    } catch (err) {
        console.error('Error changing password:', err);
        res.status(500).json({ error: 'Internal Server Error' });
    }
};

// Change password with old password verification
exports.changebyoldpassword = async (req, res) => {
    try {
        const { old_password, new_password } = req.body;
        const userId = req.user?.id;

        if (!old_password || !new_password || !userId) {
            return res.status(400).json({ error: 'Old password and new password are required' });
        }

        const pool = await poolPromise;

        // Get current password from database
        const userResult = await pool.request()
            .input('user_id', userId)
            .query('SELECT Password FROM users WHERE User_id = @user_id');

        if (userResult.recordset.length === 0) {
            return res.status(404).json({ error: 'User not found' });
        }

        const currentPassword = userResult.recordset[0].Password;

        // Verify old password
        const isPasswordValid = await bcrypt.compare(old_password, currentPassword);
        if (!isPasswordValid) {
            return res.status(401).json({ error: 'Invalid old password' });
        }

        // Check if new password is different from old
        const isSamePassword = await bcrypt.compare(new_password, currentPassword);
        if (isSamePassword) {
            return res.status(400).json({ error: 'New password must be different from old password' });
        }

        // Hash new password
        const hashedPassword = await bcrypt.hash(new_password, 10);

        // Update password
        await pool.request()
            .input('user_id', userId)
            .input('password', hashedPassword)
            .query('UPDATE users SET Password = @password WHERE User_id = @user_id');

        // Add notification
        addNotificationHelper(userId, 'Your password has been changed');

        res.json({ message: 'Password changed successfully' });
    } catch (err) {
        console.error('Error changing password:', err);
        res.status(500).json({ error: 'Internal Server Error' });
    }
};
