'use strict';

const { Model } = require('sequelize');
const { v4: uuidv4 } = require('uuid');

module.exports = (sequelize, DataTypes) => {
    class Token extends Model {
        static associate({ User }) {
            this.belongsTo(User, { foreignKey: 'user_id', as: 'user', onDelete: 'CASCADE' });
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
        value: {
            type: DataTypes.STRING(256),
            allowNull: false
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
    });

    return Token;
};