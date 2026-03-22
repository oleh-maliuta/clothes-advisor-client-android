FROM node:24-alpine

WORKDIR /app

COPY package-lock.json package.json ./

RUN npm install

COPY . .

EXPOSE 7000

CMD ["npm", "run", "start"]
