const fs = require('fs');
const path = require('path');
const https = require("https");
const cors = require('cors');
const express = require("express");
const fileUpload = require('express-fileupload');
const morgan = require("morgan");
const cookieParser = require("cookie-parser");
const { CORS_ORIGIN, COOKIE_SECRET } = require('./configs/env.config');

const app = express();

app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');

app.use(cors({
    origin: [CORS_ORIGIN],
    methods: 'GET,HEAD,PUT,PATCH,POST,DELETE',
    credentials: true
}));
app.use(morgan('short'));
app.use(cookieParser(COOKIE_SECRET));
app.use(fileUpload({ createParentPath: true }));
app.use(express.json({ limit: '10gb' }));
app.use(express.urlencoded({ extended: true, limit: '10gb' }));

app.use('/api/ping', require('./routes/ping.routes'));
app.use('/api/auth', require('./routes/auth.routes'));
app.use('/api/email', require('./routes/email.routes'));

const server = https.createServer(
    {
        key: fs.readFileSync("./ssl/key.pem"),
        cert: fs.readFileSync("./ssl/cert.pem"),
    }, app
);

module.exports = server;
