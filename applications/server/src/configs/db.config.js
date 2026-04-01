const env = require('../configs/env.config');

module.exports = {
  "development": {
    "username": env.MYSQL_USERNAME || 'root',
    "password": env.MYSQL_PASSWORD || 'password',
    "database": env.MYSQL_DATABASE_NAME || 'clothes_advisor',
    "host": env.MYSQL_HOST || 'localhost',
    "dialect": "mysql",
    "define": {
        "timestamps": false
    }
  },
  "test": {
    "username": env.MYSQL_USERNAME || 'root',
    "password": env.MYSQL_PASSWORD || 'password',
    "database": env.MYSQL_DATABASE_NAME || 'clothes_advisor',
    "host": env.MYSQL_HOST || 'localhost',
    "dialect": "mysql",
    "define": {
        "timestamps": false
    }
  },
  "production": {
    "username": env.MYSQL_USERNAME || 'root',
    "password": env.MYSQL_PASSWORD || 'password',
    "database": env.MYSQL_DATABASE_NAME || 'clothes_advisor',
    "host": env.MYSQL_HOST || 'localhost',
    "dialect": "mysql",
    "define": {
        "timestamps": false
    }
  }
}
