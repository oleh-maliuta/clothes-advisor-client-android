'use strict';

const { Model } = require('sequelize');
const { v4: uuidv4 } = require('uuid');

module.exports = (sequelize, DataTypes) => {
    class ClothingItem extends Model {
        static associate({ User, Outfit }) {
            this.belongsTo(User, { foreignKey: 'user_id', as: 'user', onDelete: 'CASCADE' });
            this.belongsToMany(Outfit, {
                through: 'clothing_outfit_items',
                foreignKey: 'item_id',
                otherKey: 'outfit_id',
                as: 'outfits'
            });
        }
    }

    ClothingItem.init({
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
        filename: {
            type: DataTypes.STRING(255),
            allowNull: false,
            unique: true,
        },
        name: {
            type: DataTypes.STRING(100),
            allowNull: false,
        },
        category: {
            type: DataTypes.ENUM(
                'tshirt', 'pants', 'jacket', 'dress', 'skirt', 'shorts', 'hoodie', 'sweater',
                'coat', 'blouse', 'shoes', 'accessories', 'boots', 'sneakers', 'sandals',
                'hat', 'scarf', 'gloves', 'socks', 'underwear', 'swimwear', 'belt', 'bag',
                'watch', 'jeans', 'leggings', 'tank_top', 'overalls', 'beanie'
            ),
            allowNull: false,
        },
        season: {
            type: DataTypes.ENUM('winter', 'spring', 'summer', 'autumn'),
            allowNull: false,
        },
        red: {
            type: DataTypes.TINYINT.UNSIGNED,
            allowNull: true,
        },
        green: {
            type: DataTypes.TINYINT.UNSIGNED,
            allowNull: true,
        },
        blue: {
            type: DataTypes.TINYINT.UNSIGNED,
            allowNull: true,
        },
        material: {
            type: DataTypes.STRING(50),
            allowNull: false,
        },
        brand: {
            type: DataTypes.STRING(100),
            allowNull: true,
        },
        purchase_date: {
            type: DataTypes.DATEONLY,
            allowNull: true,
        },
        price: {
            type: DataTypes.FLOAT.UNSIGNED,
            allowNull: true,
        },
        is_favorite: {
            type: DataTypes.BOOLEAN,
            allowNull: false,
            defaultValue: false,
        },
    }, {
        sequelize,
        modelName: 'ClothingItem',
        tableName: 'clothing_items',
        timestamps: false,
    });

    return ClothingItem;
};
