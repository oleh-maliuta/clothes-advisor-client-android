const request = require('supertest');
const app = require('./test-app');

describe('Ping API Integration Tests', () => {
  describe('GET /api/ping', () => {
    test('should return pong message', async () => {
      const response = await request(app)
        .get('/api/ping')
        .expect(200);

      expect(response.body.message).toBe('pong');
    });
  });
});