'use strict';

const { Model } = require('sequelize');
const { v4: uuidv4 } = require('uuid');

module.exports = (sequelize, DataTypes) => {
    class Outfit extends Model {
        static associate({ User, ClothingItem }) {
            this.belongsTo(User, { foreignKey: 'user_id', as: 'user', onDelete: 'CASCADE' });
            this.belongsToMany(ClothingItem, {
                through: 'clothing_outfit_items',
                foreignKey: 'outfit_id',
                otherKey: 'item_id',
                as: 'items'
            });
        }
    }

    Outfit.init({
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
        name: {
            type: DataTypes.STRING(100),
            allowNull: false,
        },
    }, {
        sequelize,
        modelName: 'Outfit',
        tableName: 'outfits',
        timestamps: false,
    });

    return Outfit;
};
