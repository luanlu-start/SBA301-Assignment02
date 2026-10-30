import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
// The React plugin enables JSX transformation and development Fast Refresh.
// Both dev and preview forward /api requests to Spring Boot; the browser keeps the frontend origin.
// The production host needs its own API proxy/base URL because Vite is not the production web server.
export default defineConfig({
  plugins: [react()],
  server: { proxy: { '/api': { target: 'http://localhost:8082', changeOrigin: true } } },
  preview: { proxy: { '/api': { target: 'http://localhost:8082', changeOrigin: true } } },
});
