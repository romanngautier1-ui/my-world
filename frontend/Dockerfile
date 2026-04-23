FROM node:20-alpine AS build
WORKDIR /app

# Install dependencies
COPY package*.json ./
RUN npm ci --silent || npm install --silent

# Build app
COPY . .
RUN npm run build -- --configuration development
# RUN npm run build -- --configuration production

FROM nginx:stable-alpine
# Remove default nginx page and copy built app files
RUN rm -rf /usr/share/nginx/html/*
COPY --from=build /app/dist/frontend/browser/. /usr/share/nginx/html

# Ensure nginx can read static files (fonts, maps, etc.)
RUN chmod -R a+r /usr/share/nginx/html && find /usr/share/nginx/html -type d -exec chmod a+rx {} \; || true
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
