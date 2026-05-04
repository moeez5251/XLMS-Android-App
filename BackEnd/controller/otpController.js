const { poolPromise } = require('../models/db');

// Verify OTP
exports.verifyotp = async (req, res) => {
    try {
        const { email, otp } = req.body;

        if (!email || !otp) {
            return res.status(400).json({ error: 'Email and OTP are required' });
        }

        const pool = await poolPromise;

        const result = await pool.request()
            .input('email', email)
            .input('otp', otp)
            .query('SELECT * FROM OTPS WHERE Email = @email AND OTPCode = @otp');

        if (result.recordset.length === 0) {
            return res.status(401).json({ error: 'Invalid OTP' });
        }

        // OTP verified successfully - remove it from database
        await pool.request()
            .input('email', email)
            .query('DELETE FROM OTPS WHERE Email = @email');

        res.json({ message: 'OTP verified successfully' });
    } catch (err) {
        console.error('Error verifying OTP:', err);
        res.status(500).json({ error: 'Internal Server Error' });
    }
};
