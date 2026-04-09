const { Temporal } = require('@js-temporal/polyfill');
const { User, Token } = require("../models");

module.exports = {
    confirm: async (req, res) => {
        const user = await User
            .findOne({ where: { email: req.body.email } });

        if (user === null) {
            return res.status(404).json({
                message: 'User not found.',
                messageCode: 'api__email__confirm__user_not_found',
            });
        }

        const token = await Token
            .findOne({
                where: {
                    userId: user.id,
                    token: req.body.token,
                }
            });

        if (token === null) {
            return res.status(404).json({
                message: 'Token not found or expired.',
                messageCode: 'api__email__confirm__invalid_token',
            });
        }

        if (Temporal.Instant.from(token.expires_at).since(Temporal.Now.instant()).total('seconds') < 0) {
            return res.status(400).json({
                message: 'Token not found or expired.',
                messageCode: 'api__email__confirm__invalid_token',
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
                    return res.status(500).json({
                        message: 'Something went wrong on our end. Please try again in a moment.',
                        messageCode: 'general__server_error',
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
                    return res.status(500).json({
                        message: 'Something went wrong on our end. Please try again in a moment.',
                        messageCode: 'general__server_error',
                    });
                }

                break;
            case "delete-user":
                try {
                    await user.destroy();
                } catch (error) {
                    console.error(error);
                    return res.status(500).json({
                        message: 'Something went wrong on our end. Please try again in a moment.',
                        messageCode: 'general__server_error',
                    });
                }
                break;
            default:
                return res.status(400).json({
                    message: 'Unknown action.',
                    messageCode: 'api__email__confirm__unknown_action',
                });
        }

        return res.status(200).send({
            message: 'Email confirmed successfully.',
            messageCode: 'api__email__confirm__success',
        });
    },
};