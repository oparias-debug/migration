import { defineConfig } from 'vitest/config';
import react from '@vitejs/plugin-react';

// El backend (api-gateway) no expone CORS: en dev, Vite actúa como
// reverse-proxy same-origin para /auth y /back, igual que hace Nginx en producción.
const proxyTarget = process.env.VITE_API_PROXY_TARGET ?? 'http://localhost:8080';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/auth': { target: proxyTarget, changeOrigin: true },
      '/back': { target: proxyTarget, changeOrigin: true },
    },
  },
  test: {
    environment: 'jsdom',
    setupFiles: ['./src/test/setup.ts'],
    coverage: {
      provider: 'v8',
      reporter: ['text', 'html'],
      include: ['src/**/*.{ts,tsx}'],
      exclude: ['src/main.tsx', 'src/vite-env.d.ts', 'src/**/*.test.{ts,tsx}'],
    },
  },
});
