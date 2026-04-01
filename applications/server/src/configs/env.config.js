require('dotenv').config();

module.exports = {
    APP_NAME: process.env.APP_NAME || 'Clothes Advisor',
    APP_MODE: process.env.APP_MODE || 'development',
    PORT: process.env.PORT || 7000,
    CORS_ORIGIN: process.env.CORS_ORIGIN || 'http://localhost:5173',
    JWT_SECRET: process.env.JWT_SECRET || 'your_jwt_secret_key',
    MYSQL_DATABASE_NAME: process.env.MYSQL_DATABASE_NAME || 'clothes_advisor',
    MYSQL_USERNAME: process.env.MYSQL_USERNAME || 'root',
    MYSQL_PASSWORD: process.env.MYSQL_PASSWORD || 'password',
    MYSQL_HOST: process.env.MYSQL_HOST || 'localhost',
    EMAIL_HOST: process.env.EMAIL_HOST || 'smtp.gmail.com',
    EMAIL_PORT: process.env.EMAIL_PORT || 587,
    EMAIL_SENDER_ADDRESS: process.env.EMAIL_SENDER_ADDRESS || 'test@gmail.com',
    EMAIL_SENDER_PASSWORD: process.env.EMAIL_SENDER_PASSWORD || '1234 5678 9010 1112',
};
