FROM docker.1ms.run/node:16-alpine AS build
WORKDIR /app
COPY package*.json ./
COPY package-lock.json ./
RUN ls -lah
RUN test -f package-lock.json && echo "LOCKFILE OK" || (echo "LOCKFILE MISSING" && exit 1)
RUN npm install
COPY . .
RUN npm run build

FROM docker.1ms.run/nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80