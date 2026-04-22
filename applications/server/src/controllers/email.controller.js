const path = require('path');
const { Temporal } = require('@js-temporal/polyfill');
const { User, Token } = require("../models");
const { DEFAULT_LOCALE, LOCALES } = require('../utils/constants.utils');
const { convertToISOTimeString } = require('../utils/time.utils');

module.exports = {
    confirm: async (req, res) => {
        req.body.locale = req.body.locale.toLowerCase();
        req.body.locale = !LOCALES.includes(req.body.locale) ?
            DEFAULT_LOCALE : req.body.locale.toLowerCase();

        const failurePath = path.join(req.body.locale, 'failure_result');

        const user = await User
            .findOne({ where: { email: req.body.email } });

        if (!user) {
            return res.render(failurePath, {
                message: 'User not found.',
            });
        }

        const token = await Token
            .findOne({
                where: {
                    id: req.body.token,
                    user_id: user.id,
                }
            });

        if (!token) {
            return res.render(failurePath, {
                message: 'Token not found or expired.',
            });
        }

        if (
            Temporal.Instant.from(convertToISOTimeString(token.expires_at))
                .since(Temporal.Now.instant()).total('seconds') < 0
        ) {
            return res.render(failurePath, {
                message: 'Token not found or expired.',
            });
        }

        const acAction = token.action.split(',');

        switch (acAction[0]) {
            case "registration":
                user.is_email_verified = true;
                try {
                    await user.save();
                    await token.destroy();
                } catch (error) {
                    console.error(error);
                    return res.render(failurePath, {
                        message: 'Something went wrong on our end. Please try again in a moment.',
                    });
                }
                break;
            case "change-email":
                user.email = acAction[1];
                try {
                    await user.save();
                    await token.destroy();
                } catch (error) {
                    console.error(error);
                    return res.render(failurePath, {
                        message: 'Something went wrong on our end. Please try again in a moment.',
                    });
                }

                break;
            case "delete-user":
                try {
                    await user.destroy();
                } catch (error) {
                    console.error(error);
                    return res.render(failurePath, {
                        message: 'Something went wrong on our end. Please try again in a moment.',
                    });
                }
                break;
            default:
                return res.render(failurePath, {
                    message: 'Unknown action.',
                });
        }

        return res.render(path.join(req.body.locale, 'email_verified'));
    },
};