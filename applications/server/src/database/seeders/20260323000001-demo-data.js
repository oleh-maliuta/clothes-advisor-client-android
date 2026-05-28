'use strict';

const bcrypt = require('bcrypt');

/** @type {import('sequelize-cli').Migration} */
module.exports = {
  async up(queryInterface) {
    return queryInterface.bulkInsert('users', [
      {
        email: '1@gmail.com',
        password_hash: bcrypt.hashSync('Password1', 10),
        is_email_verified: true,
      },
      {
        email: '2@gmail.com',
        password_hash: bcrypt.hashSync('Password1', 10),
        is_email_verified: true,
      },
      {
        email: '3@gmail.com',
        password_hash: bcrypt.hashSync('Password1', 10),
        is_email_verified: true,
      },
    ]);
  },

  async down(queryInterface) {
    return queryInterface.bulkDelete('users', null, {});
  }
};
