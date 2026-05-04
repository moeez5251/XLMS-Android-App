const express = require('express');
const router = express.Router();
const lenders = require('../controller/lendersControllers');

// Admin Routes
router.get("/all", lenders.getalllenders);
router.post("/getlenderbyid", lenders.getlenderbyid);
router.post("/insert",lenders.addbook)

// ==================== CLIENT APIs ====================
router.get("/mylendings", lenders.getlendings);
router.post("/return", lenders.returnbook);

module.exports = router;