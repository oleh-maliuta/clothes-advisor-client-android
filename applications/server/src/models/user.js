'use strict';

const { Model } = require('sequelize');
const { v4: uuidv4 } = require('uuid');

module.exports = (sequelize, DataTypes) => {
    class User extends Model {
        static associate({ Token, ClothingItem, Outfit }) {
            this.hasMany(Token, { foreignKey: 'user_id', as: 'tokens', onDelete: 'CASCADE' });
            this.hasMany(ClothingItem, { foreignKey: 'user_id', as: 'clothing_items', onDelete: 'CASCADE' });
            this.hasMany(Outfit, { foreignKey: 'user_id', as: 'outfits', onDelete: 'CASCADE' });
        }
    }

    User.init({
        id: {
            type: DataTypes.UUID,
            defaultValue: () => uuidv4(),
            allowNull: false,
            primaryKey: true,
        },
        email: {
            type: DataTypes.STRING(255),
            allowNull: false,
            unique: true,
        },
        password_hash: {
            type: DataTypes.TEXT,
            allowNull: false,
        },
        is_email_verified: {
            type: DataTypes.BOOLEAN,
            allowNull: false,
        },
        synchronized_at: {
            type: DataTypes.DATE(6),
            allowNull: false,
        },
        created_at: {
            type: DataTypes.DATE,
            allowNull: false,
            defaultValue: DataTypes.NOW,
        }
    }, {
        sequelize,
        modelName: 'User',
        tableName: 'users',
    });

    return User;
};
