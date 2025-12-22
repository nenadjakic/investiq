# Builder: Angular production build
FROM node:22 AS build
WORKDIR /workspace
COPY frontend/package*.json ./
RUN npm install
COPY frontend .
ENV NG_CLI_ANALYTICS=false
RUN npm run build -- --configuration production

# Runtime: serve static build via nginx
FROM nginx:1.29.4-alpine
COPY infrastructure/frontend/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /workspace/dist/investiq/browser /usr/share/nginx/html
EXPOSE 80
ENTRYPOINT ["nginx","-g","daemon off;"]
