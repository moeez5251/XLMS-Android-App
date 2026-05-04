const { poolPromise } = require('../models/db');
const { v4: uuidv4 } = require('uuid');
require('dotenv').config();

exports.inserting = async (req, res) => {
    const { Book_Title, Author, Category, Language, Total_Copies, Status, Pages, Price } = req.body;

    if (!Book_Title || !Author || !Category || !Language || !Total_Copies || !Status || !Pages || !Price) {
        return res.status(400).json({ error: 'All fields are required' });
    }
    try {
        const pool = await poolPromise;
        const result = await pool
            .request()
            .input('Book_ID', uuidv4())
            .input('Book_Title', Book_Title)
            .input('Author', Author)
            .input('Category', Category)
            .input('Language', Language)
            .input('Total_Copies', Total_Copies)
            .input('Status', Status)
            .input('Pages', Pages)
            .input('Price', Price)
            .input('Available', Total_Copies)
            .query('INSERT INTO Books (Book_ID, Book_Title, Author, Category, Language, Total_Copies, Status,Pages,Price,Available) VALUES (@Book_ID, @Book_Title, @Author, @Category, @Language, @Total_Copies, @Status,@Pages,@Price,@Available)');

        res.status(201).json({ message: 'Book Added successfully' });
    } catch (err) {
        console.error('Error inserting book:', err);
        res.status(500).json({ error: 'Internal Server Error' });
    }
}

exports.getting = async (req, res) => {
    try {
        const pool = await poolPromise;
        const result = await pool.request().query('SELECT * FROM Books');
        res.json(result.recordset);
    } catch (err) {
        console.error('Error fetching books:', err);
        res.status(500).json({ error: 'Internal Server Error' });
    }
}

exports.getbyID = async (req, res) => {
    try {
        const { ID } = req.body;
        if (!ID) {
            return res.status(400).json({ error: 'All fields are required' });
        }
        const pool = await poolPromise;
        const result = await pool
            .request()
            .input('ID', ID)
            .query('SELECT * FROM Books WHERE Book_ID = @ID');
        res.json(result.recordset);
    }
    catch (err) {
        console.error('Error fetching Data:', err);
        res.status(500).json({ error: 'Internal Server Error' });
    }
}

exports.updatebook = async (req, res) => {
    const { Book_ID, Book_Title, Author, Category, Language, Total_Copies, Status, Pages, Price } = req.body;
    if (!Book_ID || !Book_Title || !Author || !Category || !Language || !Status || !Pages || !Price) {
        return res.status(400).json({ error: 'All fields are required' });
    }
    try {
        const pool = await poolPromise;
        const old = await pool
            .request()
            .input('Book_ID', Book_ID)
            .query('SELECT Available,Total_Copies FROM Books WHERE Book_ID = @Book_ID');
        const oldavailable = old.recordset[0].Available
        const oldtotal = old.recordset[0].Total_Copies
        const result = await pool
            .request()
            .input('Book_ID', Book_ID)
            .input('Book_Title', Book_Title)
            .input('Author', Author)
            .input('Category', Category)
            .input('Language', Language)
            .input('Total_Copies', Total_Copies)
            .input('Status', Status)
            .input('Pages', Pages)
            .input('Price', Price)
            .input('Available', (Number(oldavailable) + Number(Total_Copies) - Number(oldtotal)).toString())
            .query('UPDATE Books SET Book_Title = @Book_Title, Author = @Author, Category = @Category, Language = @Language, Total_Copies = @Total_Copies, Status = @Status, Pages = @Pages, Price = @Price , Available = @Available WHERE Book_ID = @Book_ID');
        res.json({ message: 'Book updated successfully' });
    } catch (err) {
        console.error('Error updating book:', err);
        res.status(500).json({ error: 'Internal Server Error' });
    }
}

exports.deletebook = async (req, res) => {
    const ID_arr = req.body;

    if (!Array.isArray(ID_arr) || ID_arr.length === 0) {
        return res.status(400).json({ error: 'ID array is required and cannot be empty' });
    }

    try {
        const pool = await poolPromise;
        const request = pool.request();

        const idParams = ID_arr.map((id, index) => {
            const paramName = `id${index}`;
            request.input(paramName, id);
            return `@${paramName}`;
        });

        const query = `DELETE FROM Books WHERE Book_ID IN (${idParams.join(',')})`;

        await request.query(query);

        res.json({ message: 'Books deleted successfully' });
    } catch (err) {
        console.error('Error deleting books:', err);
        res.status(500).json({ error: 'Internal Server Error' });
    }
};

exports.getbycolumnname = async (req, res) => {
    const { column } = req.body;
    if (column.length === 0) {
        return res.status(400).json({ error: 'All fields are required' });
    }
    try {
        const pool = await poolPromise;
        const request = pool.request();
        const query = `Select DISTINCT ${column.join(',')} from Books`;
        const result = await request.query(query);
        res.json(result.recordset);
    }
    catch {
        res.status(500).json({ error: 'Internal Server Error' });
    }
}

// ==================== CLIENT APIs ====================

// Get all books for client
exports.getbooks = async (req, res) => {
    try {
        const pool = await poolPromise;
        const result = await pool.request()
            .query('SELECT Book_ID AS id, Book_Title AS name, Author, Category, Language, Status, Pages, Price AS price, Available AS Available_Copies FROM books');
        res.json(result.recordset);
    } catch (err) {
        console.error('Error fetching books:', err);
        res.status(500).json({ error: 'Internal Server Error' });
    }
};

// Lend book to user
exports.lendbook = async (req, res) => {
    try {
        const { book_id, IssuedDate, DueDate, CopiesLent, FinePerDay } = req.body;
        const userId = req.user?.id;

        if (!book_id || !IssuedDate || !DueDate || !CopiesLent || !FinePerDay || !userId) {
            return res.status(400).json({ error: 'All fields are required' });
        }

        const pool = await poolPromise;

        // Get user info
        const userResult = await pool.request()
            .input('user_id', userId)
            .query('SELECT User_Name, Cost FROM users WHERE User_id = @user_id');

        if (userResult.recordset.length === 0) {
            return res.status(404).json({ error: 'User not found' });
        }

        const user = userResult.recordset[0];

        // Get book info
        const bookResult = await pool.request()
            .input('book_id', book_id)
            .query('SELECT Category, Price, Book_Title, Author, Available FROM books WHERE Book_ID = @book_id');

        if (bookResult.recordset.length === 0) {
            return res.status(404).json({ error: 'Book not found' });
        }

        const book = bookResult.recordset[0];

        // Parse dates
        const issued = new Date(IssuedDate);
        const due = new Date(DueDate);
        const daysLent = Math.floor((due - issued) / (1000 * 60 * 60 * 24));

        // Insert borrower record
        await pool.request()
            .input('book_id', book_id)
            .input('user_id', userId)
            .input('name', user.User_Name)
            .input('booktitle', book.Book_Title)
            .input('author', book.Author)
            .input('issueddate', issued.toISOString().split('T')[0])
            .input('duedate', due.toISOString().split('T')[0])
            .input('copieslent', CopiesLent)
            .input('fineperday', FinePerDay)
            .input('price', book.Price)
            .input('category', book.Category)
            .query(`INSERT INTO borrower (
                Book_ID, user_id, Name, BookTitle, Author, IssuedDate, DueDate,
                CopiesLent, FinePerDay, Price, Category
            ) VALUES (@book_id, @user_id, @name, @booktitle, @author, @issueddate,
                @duedate, @copieslent, @fineperday, @price, @category)`);

        // Update book availability
        if (parseInt(book.Available) === parseInt(CopiesLent)) {
            await pool.request()
                .input('book_id', book_id)
                .query("UPDATE Books SET Available = 0, Status = 'Borrowed' WHERE Book_ID = @book_id");
        } else {
            const newAvailable = parseInt(book.Available) - parseInt(CopiesLent);
            await pool.request()
                .input('book_id', book_id)
                .input('available', newAvailable)
                .query('UPDATE Books SET Available = @available WHERE Book_ID = @book_id');
        }

        // Update user cost
        const totalCost = parseInt(user.Cost) + (parseInt(CopiesLent) * parseInt(book.Price) * daysLent);
        await pool.request()
            .input('user_id', userId)
            .input('cost', totalCost)
            .query('UPDATE users SET Cost = @cost WHERE User_id = @user_id');

        // Add notification
        const { addNotificationHelper } = require('./notificationscontroller');
        const issuedDateFormatted = issued.toLocaleDateString('en-PK');
        const dueDateFormatted = due.toLocaleDateString('en-PK');
        addNotificationHelper(userId, `You have borrowed ${book.Book_Title} from ${issuedDateFormatted} to ${dueDateFormatted}`);

        res.json({ message: 'Book lent successfully' });
    } catch (err) {
        console.error('Error lending book:', err);
        res.status(500).json({ error: 'Internal Server Error' });
    }
};
