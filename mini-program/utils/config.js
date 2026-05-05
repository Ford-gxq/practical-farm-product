// 本地调试默认地址：后端 Spring Boot application.yml 中 server.port = 8088
// 电脑本地微信开发者工具调试：一般可以使用 http://127.0.0.1:8088/api
// 真机调试：把 127.0.0.1 改成你电脑的局域网 IP，例如 http://192.168.1.8:8088/api
// const BASE_URL = 'http://127.0.0.1:8088/api'
// const IMAGE_BASE_URL = 'http://127.0.0.1:8088'
const BASE_URL = 'http://192.168.254.126:8088/api'
const IMAGE_BASE_URL = 'http://192.168.254.126:8088'
module.exports = {
  BASE_URL,
  IMAGE_BASE_URL
}
