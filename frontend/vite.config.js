import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    host: '0.0.0.0',
    port: 5173,
    proxy: {
      // 前端请求 /api 时，代理到后端 Spring Boot
      '/api': {
        target: 'http://localhost:8088',
        changeOrigin: true
      },
      // 局域网通过 5173 访问时，图片也代理到后端静态资源映射，避免裂图
      '/asset/images': {
        target: 'http://localhost:8088',
        changeOrigin: true
      }
    }
  }
})
