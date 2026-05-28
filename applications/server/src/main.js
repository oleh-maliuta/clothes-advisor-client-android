const process = require('process');
const { server } = require('./server');
const { sequelize } = require("./models");
const { PORT } = require('./configs/env.config');

const listener = server.listen(PORT, async () => {
    try {
        await sequelize.authenticate();
        console.log('Database connected.');
        console.log(`Server running on port ${PORT}.`);
    } catch (err) {
        console.error('Unable to connect to the database:', err);
        process.exit(1);
    }
});

const shutdown = async () => {
    console.log('\nShutting down...');

    try {
        await sequelize.close();
        console.log('Database connection closed.');
    } catch (err) {
        console.error('Error closing database connection:', err);
    }

    listener.close(() => {
        console.log('Server stopped.');
        process.exit(0);
    });
};

process.on('SIGINT', shutdown);
process.on('SIGTERM', shutdown);
