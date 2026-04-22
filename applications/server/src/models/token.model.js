'use strict';

const { Model } = require('sequelize');
const { v4: uuidv4 } = require('uuid');
const { convertToISOTimeString } = require('../utils/time.utils');

module.exports = (sequelize, DataTypes) => {
    class Token extends Model {
        static associate({ User }) {
            this.belongsTo(User, { foreignKey: 'user_id', as: 'user', onDelete: 'CASCADE' });
        }

        toJSON() {
            const attributes = { ...this.get() };

            attributes.expires_at = convertToISOTimeString(attributes.expires_at);
            
            return attributes;
        }
    }

    Token.init({
        id: {
            type: DataTypes.UUID,
            defaultValue: () => uuidv4(),
            allowNull: false,
            primaryKey: true,
        },
        user_id: {
            type: DataTypes.UUID,
            allowNull: false,
            references: {
                model: 'users',
                key: 'id',
            },
        },
        action: {
            type: DataTypes.STRING(100),
            allowNull: false
        },
        expires_at: {
            type: DataTypes.DATE,
            allowNull: false
        }
    }, {
        sequelize,
        modelName: 'Token',
        tableName: 'tokens',
        timestamps: false,
    });

    return Token;
};