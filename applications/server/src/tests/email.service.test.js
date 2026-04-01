const { readFile } = require('fs/promises');
const nodemailer = require('nodemailer');
const path = require('path');
const { EmailService } = require('../services/email.service');
const { APP_NAME, EMAIL_HOST, EMAIL_PORT, EMAIL_SENDER_ADDRESS, EMAIL_SENDER_PASSWORD } = require('../configs/env.config');

jest.mock('fs/promises');
jest.mock('nodemailer');

describe('EmailService', () => {
  let mockSendMail;
  let service;
  const mockTemplatesPath = '/mock/templates/path';

  beforeEach(() => {
    mockSendMail = jest.fn().mockResolvedValue({ messageId: 'abc123' });

    service = new EmailService({
      transporter: { sendMail: mockSendMail },
      templatesPath: mockTemplatesPath,
    });
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('Constructor & Initialization', () => {
    test('initializes with default transporter when no arguments are provided', () => {
      const createTransportSpy = jest.spyOn(nodemailer, 'createTransport');
      
      new EmailService();

      expect(createTransportSpy).toHaveBeenCalledTimes(1);
      expect(createTransportSpy).toHaveBeenCalledWith({
        host: EMAIL_HOST,
        port: EMAIL_PORT,
        secure: false,
        auth: {
          user: EMAIL_SENDER_ADDRESS,
          pass: EMAIL_SENDER_PASSWORD,
        },
      });
    });
  });

  describe('sendHTMLTemplateEmail', () => {
    test('sends HTML template email with subject from meta and replacements', async () => {
      const mockHtmlContent = '<meta name="subject" content="Discover the Outfit" />\n<p>Hello {{ name }},</p>';
      readFile.mockResolvedValue(mockHtmlContent);

      const result = await service.sendHTMLTemplateEmail(
        'welcome', 'en', 'client@example.com', [{ key: 'name', value: 'World' }]
      );

      expect(readFile).toHaveBeenCalledWith(
        path.resolve(mockTemplatesPath, 'en', 'welcome.html'),
        'utf-8'
      );
      
      expect(result).toEqual({ messageId: 'abc123' });
      expect(mockSendMail).toHaveBeenCalledTimes(1);
      expect(mockSendMail).toHaveBeenCalledWith({
        from: APP_NAME,
        to: 'client@example.com',
        subject: 'Discover the Outfit',
        html: '<meta name="subject" content="Discover the Outfit" />\n<p>Hello World,</p>',
      });
    });

    test('falls back to APP_NAME when subject meta tag is missing', async () => {
      const mockHtmlContent = '<p>Hi {{name}}</p>';
      readFile.mockResolvedValue(mockHtmlContent);

      await service.sendHTMLTemplateEmail(
        'reset-password', 'en', 'another@example.com', [{ key: 'name', value: 'Bob' }]
      );

      expect(mockSendMail).toHaveBeenCalledWith(expect.objectContaining({
        from: APP_NAME,
        to: 'another@example.com',
        subject: APP_NAME,
        html: '<p>Hi Bob</p>',
      }));
    });

    test('propagates file read errors when template does not exist', async () => {
      const fileErrorText = 'ENOENT: no such file or directory';
      const fileError = new Error(fileErrorText);
      readFile.mockRejectedValue(fileError);

      await expect(
        service.sendHTMLTemplateEmail('missing', 'en', 'noone@example.com', [])
      ).rejects.toThrow(fileErrorText);
      
      expect(mockSendMail).not.toHaveBeenCalled();
    });

    test('bubbles up errors when the transporter fails to send the email', async () => {
      const mockHtmlContent = '<p>Test email</p>';
      readFile.mockResolvedValue(mockHtmlContent);
      
      const smtpErrorText = 'SMTP connection timed out';
      const smtpError = new Error(smtpErrorText);
      mockSendMail.mockRejectedValue(smtpError);

      await expect(
        service.sendHTMLTemplateEmail('welcome', 'en', 'client@example.com', [])
      ).rejects.toThrow(smtpErrorText);
    });
  });
});
