import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

// 创建 Axios 实例，统一配置 baseURL 和超时时间
const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器：后台接口自动带上 token
request.interceptors.request.use(config => {
  const token = localStorage.getItem('blog_token')
  if (token) {
    config.headers['X-Token'] = token
  }
  return config
})

// 响应拦截器：统一处理后端 ApiResponse 格式
request.interceptors.response.use(response => {
  const res = response.data
  if (res.code !== 200) {
    ElMessage.error(res.message || '请求失败')
    if (res.code === 401) {
      localStorage.removeItem('blog_token')
      router.push('/admin/login')
    }
    return Promise.reject(res)
  }
  return res.data
}, error => {
  ElMessage.error(error.message || '网络异常')
  return Promise.reject(error)
})

export default request
