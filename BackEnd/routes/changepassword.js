const express = require('express');
const router = express.Router();
const changepasswordController = require('../controller/changepasswordController');

// ==================== CLIENT APIs ====================
router.post('/byotp', changepasswordController.changebyotp);
router.post('/byoldpassword', changepasswordController.changebyoldpassword);

module.exports = router;
