const { jest } = require('@jest/globals');
const { v4: uuidv4, v5: uuidv5 } = jest.requireActual('uuid');

module.exports = {
  v4: uuidv4,
  v5: uuidv5,
};