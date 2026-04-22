const express = require("express");
const controller = require("../controllers/email.controller");
const requestRules = require("../request_rules/email.rules");
const { validateRequest } = require("../middleware/request.middleware");

const router = express.Router();

router.post('/confirm', requestRules.confirm(), validateRequest, controller.confirm);

module.exports = router;
