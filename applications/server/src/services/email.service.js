const nodemailer = require('nodemailer');
const { resolve } = require('path');
const { readFile } = require('fs/promises');
const { EMAIL_HOST, EMAIL_PORT, EMAIL_SENDER_ADDRESS, EMAIL_SENDER_PASSWORD, APP_NAME } = require('../configs/env.config');

class EmailService {
  constructor({ transporter, templatesPath } = {}) {
    this.transporter = transporter || nodemailer.createTransport({
      host: EMAIL_HOST,
      port: EMAIL_PORT,
      secure: false,
      auth: {
        user: EMAIL_SENDER_ADDRESS,
        pass: EMAIL_SENDER_PASSWORD,
      },
    });

    this.templatesPath = templatesPath || resolve(__dirname, '..', 'web');
  }

  async sendHTMLTemplateEmail(template, locale, to, params = []) {
    const templatePath = resolve(this.templatesPath, locale, `${template}.html`);
    let htmlTemplate = await readFile(templatePath, 'utf-8');

    const subjectMatches = htmlTemplate.match(
      /<meta\s+name=["']subject["']\s+content=["'](.*?)["']/i
    );
    const subject = subjectMatches && subjectMatches[1]
      ? subjectMatches[1] : APP_NAME;

    for (const el of params) {
      const regex = new RegExp(`\\{\\{\\s*${el.key}\\s*\\}\\}`, 'g');
      htmlTemplate = htmlTemplate.replace(regex, el.value);
    }

    return this.transporter.sendMail({
      from: APP_NAME,
      to,
      subject,
      html: htmlTemplate,
    });
  }
}

const defaultEmailService = new EmailService();

module.exports = {
  defaultEmailService,
  EmailService
};
