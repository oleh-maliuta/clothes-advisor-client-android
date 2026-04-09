const bcrypt = require('bcrypt');
const { Temporal } = require('@js-temporal/polyfill');
const { User, Token, sequelize } = require('../models');
const { serviceInstance: emailService } = require('../services/email.service');
const { SERVER_BASE_URL } = require('../configs/env.config');
const { createAuthJWT } = require('../utils/jwt.utils');

module.exports = {
    register: async (req, res) => {
        let user = await User
            .findOne({ where: { email: req.body.email, }, });

        let token = null;
        let createNewUser = false;

        if (user) {
            if (user.is_email_verified) {
                return res.status(409).json({
                    message: 'User with this email already exists.',
                    messageCode: 'auth__user_exists',
                });
            } else {
                token = await Token.findOne({
                    where: {
                        user_id: user.id,
                        action: 'registration',
                    },
                });

                if (token) {
                    if (Temporal.Instant.from(token.expires_at).since(Temporal.Now.instant()).total('seconds') > 0) {
                        token.expires_at = Temporal.Now.instant().add({ hours: 12 }).toString();
                        try { await token.save(); } catch (error) {
                            console.error(error);
                            return res.status(500).json({
                                message: 'Something went wrong on our end. Please try again in a moment.',
                                messageCode: 'general__server_error',
                            });
                        }
                    } else {
                        try { await token.destroy(); } catch (error) {
                            console.error(error);
                            return res.status(500).json({
                                message: 'Something went wrong on our end. Please try again in a moment.',
                                messageCode: 'general__server_error',
                            });
                        }
                        token = null;
                    }
                }
            }
        } else {
            createNewUser = true;
        }

        const transaction = await sequelize.transaction();
        const hash = await bcrypt.hash(req.body.password, 10);

        try {
            user = user ?? await User.create({
                email: req.body.email,
                password_hash: hash,
                is_email_verified: false,
            }, { transaction: transaction });

            token = token ?? await Token.create({
                user_id: user.id,
                token: token,
                action: 'registration',
                expires_at: Temporal.Now.instant().add({ hours: 12 }).toString(),
            }, { transaction: transaction });

            const emailFormLink = new URL(
                `/api/email/confirm`,
                SERVER_BASE_URL,
            );
            emailFormLink.searchParams.append('email', user.email);
            emailFormLink.searchParams.append('token', token.id);

            await emailService.sendTemplateAsync(
                'registration_confirm',
                'Confirm registration',
                user.email,
                [
                    { key: 'link', value: emailFormLink.toString() },
                    { key: 'email', value: user.email },
                    { key: 'token', value: token.id },
                ]
            );

            await transaction.commit();
        } catch (error) {
            console.error(error);
            await transaction.rollback();
            return res.status(500).json({
                message: 'Something went wrong on our end. Please try again in a moment.',
                messageCode: 'general__server_error',
            });
        }

        if (createNewUser) {
            return res.status(200).json({
                message: 'Email has been sent to verify your email address.',
                messageCode: 'api__auth__register__email_has_been_sent',
            });
        }

        return res.status(200).json({
            message: 'You have already created an account. Email has been resent.',
            messageCode: 'api__auth__register__same_token_and_email_has_been_sent',
        });
    },

    login: async (req, res) => {
        const user = await User
            .findOne({ where: { login: req.body.login } });

        if (user === null) {
            return res.status(404).json({
                message: "Invalid email or password.",
                messageCode: 'api__auth__login__invalid_credentials',
            });
        }

        if (!await bcrypt.compare(req.body.password, user.password_hash)) {
            return res.status(409).json({
                message: "Invalid email or password.",
                messageCode: 'api__auth__login__invalid_credentials',
            });
        }

        if (!user.is_email_verified) {
            return res.status(400).json({
                message: "User is not verified.",
                messageCode: 'api__auth__login__not_verified',
            });
        }

        const token = createAuthJWT(user);

        return res.status(200).json({
            data: token,
            message: 'Login successful.',
            messageCode: 'api__auth__login__success',
        });
    },
};
