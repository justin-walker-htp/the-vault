# frontend/Dockerfile

# 1. Use Node.js (The tool that runs JavaScript)
FROM node:22-alpine

# 2. Set the working directory inside the container
WORKDIR /app

# 3. Copy package.json and install dependencies
COPY package*.json ./
RUN npm install

# 4. Copy the rest of the code
COPY . .

# 5. Expose the port Vite runs on
EXPOSE 5173

# 6. Run the app using the "host" flag so Docker can see it
CMD ["npm", "run", "dev", "--", "--host"]