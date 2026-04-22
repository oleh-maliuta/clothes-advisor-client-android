const { body } = require('express-validator');

module.exports = {
    confirm: () => {
        return [
            body('email')
                .notEmpty().withMessage('email__confirm__email__empty')
                .isEmail().withMessage('email__confirm__email__invalid'),
            body('token')
                .notEmpty().withMessage('email__confirm__token__empty'),
            body('locale')
                .notEmpty().withMessage('email__confirm__locale__empty')
        ];
    },
};
