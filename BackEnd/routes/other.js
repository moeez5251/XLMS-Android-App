const express = require('express');
const router = express.Router();
const otherController = require('../controller/other'); 

// Admin Routes
router.get('/getbookdata', otherController.getbookdata);

// ==================== CLIENT APIs ====================
router.get('/chartdetails', otherController.chartdetails);
router.get('/lendingactivity', otherController.lendingactivity);
router.get('/mystats', otherController.otherget);

module.exports = router;