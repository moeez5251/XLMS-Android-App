const express = require('express');
const router = express.Router();
const reservationController = require('../controller/reservationController');

// ==================== CLIENT APIs ====================
router.post('/reserve', reservationController.reservebook);
router.get('/myreservations', reservationController.getreservations);

module.exports = router;
