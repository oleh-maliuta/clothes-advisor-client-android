'use strict';

/** @type {import('sequelize-cli').Migration} */
module.exports = {
  async up(queryInterface, Sequelize) {
    await queryInterface.createTable('clothing_items', {
      id: {
        type: Sequelize.UUID,
        defaultValue: Sequelize.literal('(UUID())'),
        allowNull: false,
        primaryKey: true,
      },
      user_id: {
        type: Sequelize.UUID,
        allowNull: false,
        references: {
          model: 'users',
          key: 'id',
        },
        onDelete: 'CASCADE',
        onUpdate: 'CASCADE',
      },
      filename: {
        type: Sequelize.STRING(255),
        allowNull: false,
        unique: true,
      },
      name: {
        type: Sequelize.STRING(100),
        allowNull: false,
      },
      category: {
        type: Sequelize.ENUM(
          'tshirt', 'pants', 'jacket', 'dress', 'skirt', 'shorts', 'hoodie', 'sweater',
          'coat', 'blouse', 'shoes', 'accessories', 'boots', 'sneakers', 'sandals',
          'hat', 'scarf', 'gloves', 'socks', 'underwear', 'swimwear', 'belt', 'bag',
          'watch', 'jeans', 'leggings', 'tank_top', 'overalls', 'beanie'
        ),
        allowNull: false,
      },
      season: {
        type: Sequelize.ENUM('winter', 'spring', 'summer', 'autumn'),
        allowNull: false,
      },
      red: {
        type: Sequelize.TINYINT.UNSIGNED,
        allowNull: true,
      },
      green: {
        type: Sequelize.TINYINT.UNSIGNED,
        allowNull: true,
      },
      blue: {
        type: Sequelize.TINYINT.UNSIGNED,
        allowNull: true,
      },
      material: {
        type: Sequelize.STRING(50),
        allowNull: false,
      },
      brand: {
        type: Sequelize.STRING(100),
        allowNull: true,
      },
      purchase_date: {
        type: Sequelize.DATEONLY,
        allowNull: true,
      },
      price: {
        type: Sequelize.FLOAT.UNSIGNED,
        allowNull: true,
      },
      is_favorite: {
        type: Sequelize.BOOLEAN,
        allowNull: false,
        defaultValue: false,
      },
    });
  },

  async down(queryInterface) {
    await queryInterface.dropTable('clothing_items');
  }
};
