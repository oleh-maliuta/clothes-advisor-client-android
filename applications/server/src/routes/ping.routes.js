const express = require("express");
const controller = require("../controllers/ping.controller");

const router = express.Router();

router.get('/', controller.ping);

module.exports = router;
