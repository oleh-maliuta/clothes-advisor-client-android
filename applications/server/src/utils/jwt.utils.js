const { Temporal } = require('@js-temporal/polyfill');
const jwt = require('jsonwebtoken');
const { JWT_SECRET } = process.env;

module.exports = {
    /**
    * Creates a JWT for the given user.
    * @param {any} user - The user object for which to create the JWT.
    * @returns {string} The generated JWT token.
    */
    createAuthJWT: (user) => {
        return jwt.sign(
            {
                id: user.id,
                login: user.email,
                tokenCreatedAt: Temporal.Now.instant(),
            },
            JWT_SECRET || 'jwt_secret_key',
            { expiresIn: '14d' },
        );
    },
};
