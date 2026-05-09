const express = require('express');
const router = express.Router();
const mails = require('../controller/mails');
router.post("/otp", mails.OTP);
router.post("/verify", mails.verifyotp);
router.post("/resend", mails.resendotp);
router.post("/issue-mail", mails.issue_mail);
module.exports = router;