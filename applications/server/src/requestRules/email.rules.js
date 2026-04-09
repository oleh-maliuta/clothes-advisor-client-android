const { body } = require('express-validator');

module.exports = {
    confirm: () => {
        return [
            body('email')
                .notEmpty().withMessage('email__email__empty')
                .isEmail().withMessage('email__email__invalid'),
            body('token')
                .notEmpty().withMessage('email__token__empty'),
            body('locale')
                .notEmpty().withMessage('email__locale__empty')
        ];
    },
};
