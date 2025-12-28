// frontend/src/api/axiosConfig.js
import axios from 'axios';

// 1. Determine the URL
// If we are in Production (Vercel), use the Environment Variable.
// If we are in Development (Laptop), use localhost:8080.
const API_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

// 2. Create the instance
const api = axios.create({
    baseURL: API_URL
});

// 3. Add the Interceptor (The Automatic Stapler)
api.interceptors.request.use(config => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

export default api;