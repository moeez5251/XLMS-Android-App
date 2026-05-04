const { poolPromise } = require('../models/db');
exports.getbookdata = async (req, res) => {
    const pool = await poolPromise;
    try {
        const result = await pool.request()
            .query('SELECT * FROM books');
        const totalusers = await pool.request()
            .query('SELECT COUNT(*) AS Total_Users FROM users');
        const Totalusers = totalusers.recordset[0].Total_Users;
        const totalborrower = await pool.request()
            .query('SELECT COUNT(*) AS Total_Borrowers FROM borrower');
        const Totalborrowers = totalborrower.recordset[0].Total_Borrowers;
        const Totalbooks = result.recordset.length;
        const availablebooks = result.recordset.filter(book => book.Status == 'Available').length;
        const overduebooks = result.recordset.filter(book => book.Status == 'Overdue').length;
        res.json({
            Totalbooks: Totalbooks,
            Totalusers: Totalusers,
            Totalborrowers: Totalborrowers,
            availablebooks: availablebooks,
            overduebooks: overduebooks,
        })
    }
    catch {
        res.status(500).send('Error retrieving book data');
    }
}

// ==================== CLIENT APIs ====================

// Get user chart details (returned and overdue books)
exports.chartdetails = async (req, res) => {
    try {
        const userId = req.user?.id;

        if (!userId) {
            return res.status(401).json({ error: 'User ID is required' });
        }

        const pool = await poolPromise;

        // Get returned books count
        const returnedResult = await pool.request()
            .input('user_id', userId)
            .query("SELECT COUNT(*) AS count FROM borrower WHERE user_id = @user_id AND Status = 'Returned'");

        // Get overdue books count (due date is before today)
        const overdueResult = await pool.request()
            .input('user_id', userId)
            .query("SELECT COUNT(*) AS count FROM borrower WHERE DueDate < CAST(GETDATE() AS DATE) AND Status = 'not returned' AND user_id = @user_id");

        res.json({
            returned: returnedResult.recordset[0].count,
            overdue: overdueResult.recordset[0].count
        });
    } catch (err) {
        console.error('Error fetching chart details:', err);
        res.status(500).json({ error: 'Internal Server Error' });
    }
};

// Get lending activity by month
exports.lendingactivity = async (req, res) => {
    try {
        const userId = req.user?.id;

        if (!userId) {
            return res.status(401).json({ error: 'User ID is required' });
        }

        const pool = await poolPromise;

        const result = await pool.request()
            .input('user_id', userId)
            .query(`
                SELECT DATENAME(MONTH, TRY_CONVERT(datetime, IssuedDate, 101)) AS MonthName, COUNT(*) AS Count
                FROM borrower
                WHERE user_id = @user_id
                GROUP BY DATENAME(MONTH, TRY_CONVERT(datetime, IssuedDate, 101))
            `);

        // Initialize months object
        const months = {
            "January": 0, "February": 0, "March": 0, "April": 0,
            "May": 0, "June": 0, "July": 0, "August": 0,
            "September": 0, "October": 0, "November": 0, "December": 0
        };

        // Fill in the counts
        for (const row of result.recordset) {
            if (row.MonthName && months.hasOwnProperty(row.MonthName)) {
                months[row.MonthName] = row.Count;
            }
        }

        res.json(months);
    } catch (err) {
        console.error('Error fetching lending activity:', err);
        res.status(500).json({ error: 'Internal Server Error' });
    }
};

// Get user lending statistics
exports.otherget = async (req, res) => {
    try {
        const userId = req.user?.id;

        if (!userId) {
            return res.status(401).json({ error: 'User ID is required' });
        }

        const pool = await poolPromise;

        // Get current lended books count (not returned)
        const lendedResult = await pool.request()
            .input('user_id', userId)
            .query("SELECT COUNT(*) AS count FROM borrower WHERE user_id = @user_id AND Status = 'not returned'");

        // Get overdue books count
        const overdueResult = await pool.request()
            .input('user_id', userId)
            .query("SELECT COUNT(*) AS count FROM borrower WHERE DueDate < CAST(GETDATE() AS DATE) AND Status = 'not returned' AND user_id = @user_id");

        // Get reserved books count
        const reservedResult = await pool.request()
            .input('user_id', userId)
            .query('SELECT COUNT(*) AS count FROM reserved WHERE User_ID = @user_id');

        res.json({
            lended: lendedResult.recordset[0].count,
            overdue: overdueResult.recordset[0].count,
            reserved: reservedResult.recordset[0].count
        });
    } catch (err) {
        console.error('Error fetching other stats:', err);
        res.status(500).json({ error: 'Internal Server Error' });
    }
}