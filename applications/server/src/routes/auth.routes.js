const express = require("express");
const controller = require("../controllers/auth.controller");
const requestRules = require("../request_rules/auth.rules");
const { validateRequest } = require("../middleware/request.middleware");

const router = express.Router();

router.post('/register', requestRules.register(), validateRequest, controller.register);
router.post('/login', requestRules.login(), validateRequest, controller.login);

module.exports = router;
