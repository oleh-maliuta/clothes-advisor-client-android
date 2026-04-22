const { body } = require('express-validator');

module.exports = {
    register: () => {
        return [
            body('email')
                .notEmpty().withMessage('auth__register__request__email__empty')
                .isEmail().withMessage('auth__register__request__email__invalid'),
            body('password')
                .notEmpty().withMessage('auth__register__request__password__empty')
                .isLength({ min: 6 }).withMessage('auth__register__request__password__short')
                .matches(/^(?=.*[\p{Lu}])(?=.*[\p{Ll}])(?=.*[\p{N}]).*$/u).withMessage('auth__register__request__password__weak'),
        ];
    },

    login: () => {
        return [
            body('email')
                .notEmpty().withMessage('auth__login__request__email__empty')
                .isEmail().withMessage('auth__login__request__email__invalid'),
            body('password')
                .notEmpty().withMessage('auth__login__request__password__empty'),
        ];
    },
};
