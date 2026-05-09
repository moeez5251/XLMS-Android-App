const express = require('express');
const router = express.Router();
const notificationsController = require('../controller/notificationscontroller');
router.get('/get', notificationsController.getnotifications);
router.post('/markasread', notificationsController.markasread);
module.exports = router;