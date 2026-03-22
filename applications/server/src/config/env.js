require('dotenv').config();

module.exports = {
    APP_MODE: process.env.APP_MODE || 'development',
    PORT: process.env.PORT || 7000,
    CORS_ORIGIN: process.env.CORS_ORIGIN,
    JWT_SECRET: process.env.JWT_SECRET,
    MYSQL_DATABASE_NAME: process.env.MYSQL_DATABASE_NAME || 'clothes_advisor',
    MYSQL_USERNAME: process.env.MYSQL_USERNAME || 'root',
    MYSQL_PASSWORD: process.env.MYSQL_PASSWORD || 'password',
    MYSQL_HOST: process.env.MYSQL_HOST || 'localhost',
};
