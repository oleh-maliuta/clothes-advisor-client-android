const fs = require('fs');
const https = require("https");
const cors = require('cors');
const express = require("express");
const fileUpload = require('express-fileupload');
const morgan = require("morgan");
const cookieParser = require("cookie-parser");
const process = require('process');
const { sequelize } = require("./models");
const pingRouter = require('./routes/ping');
const { PORT, CORS_ORIGIN } = require('./config/env');

const app = express();

app.use(cors({
    origin: [CORS_ORIGIN],
    methods: 'GET,HEAD,PUT,PATCH,POST,DELETE',
    credentials: true
}));
app.use(cookieParser());
app.use(fileUpload({ createParentPath: true }));
app.use(morgan('short'));
app.use(express.json({ limit: '10gb' }));
app.use(express.urlencoded({ extended: true, limit: '10gb' }));

app.use("/api/ping", pingRouter);

// Start server
const server = https.createServer(
    {
        key: fs.readFileSync("./ssl/key.pem"),
        cert: fs.readFileSync("./ssl/cert.pem"),
    }, app
).listen(PORT, async () => {
    try {
        await sequelize.authenticate();
        console.log('Database connected.');
        console.log(`Server running on port ${PORT}.`);
    } catch (err) {
        console.error('Unable to connect to the database:', err);
        process.exit(1);
    }
});

// Graceful shutdown handler
const shutdown = async () => {
    console.log('\nShutting down...');
    try {
        await sequelize.close();
        console.log('Database connection closed.');
    } catch (err) {
        console.error('Error closing database connection:', err);
    }
    server.close(() => {
        console.log('Server stopped.');
        process.exit(0);
    });
};

// Handle termination signals
process.on('SIGINT', shutdown);
process.on('SIGTERM', shutdown);
