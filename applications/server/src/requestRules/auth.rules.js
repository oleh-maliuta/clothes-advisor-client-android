const { body } = require('express-validator');

module.exports = {
    register: () => {
        return [
            body('email')
                .notEmpty().withMessage('register__email__empty')
                .isEmail().withMessage('register__email__invalid'),
            body('password')
                .notEmpty().withMessage('register__password__empty')
                .isLength({ min: 6 }).withMessage('register__password__short')
                .matches(/^(?=.*\p{Nd})(?=.*\p{Lu})(?=.*\p{Ll}).+$/).withMessage('register__password__format'),
        ];
    },

    login: () => {
        return [
            body('email')
                .notEmpty().withMessage('login__email__empty')
                .isEmail().withMessage('login__email__invalid'),
            body('password')
                .notEmpty().withMessage('login__password__empty'),
        ];
    },
};
