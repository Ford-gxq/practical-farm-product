const { BASE_URL, IMAGE_BASE_URL } = require('./config')

function request(path, data = {}, method = 'GET') {
  return new Promise((resolve, reject) => {
    wx.request({
      url: BASE_URL + path,
      method,
      data,
      header: { 'content-type': 'application/json' },
      success(res) {
        if (res.statusCode < 200 || res.statusCode >= 300) {
          wx.showToast({ title: '服务异常', icon: 'none' })
          reject(res)
          return
        }
        const body = res.data || {}
        // 后端统一格式：{ code: 200, message: 'success', data: ... }
        if (body.code && body.code !== 200) {
          wx.showToast({ title: body.message || '请求失败', icon: 'none' })
          reject(body)
          return
        }
        resolve(body.data !== undefined ? body.data : body)
      },
      fail(err) {
        wx.showToast({ title: '请检查后端是否启动', icon: 'none' })
        reject(err)
      }
    })
  })
}

function normalizeImage(url) {
  if (!url) return ''
  if (/^https?:\/\//.test(url)) return url
  if (url.startsWith('/')) return IMAGE_BASE_URL + url
  return IMAGE_BASE_URL + '/' + url
}

function articleList(params = {}) {
  return request('/site/articles', params)
}
function articleDetail(id) {
  return request('/site/articles/' + id)
}
function categories() {
  return request('/site/categories')
}
function submitMessage(data) {
  return request('/site/messages', data, 'POST')
}

module.exports = {
  request,
  normalizeImage,
  articleList,
  articleDetail,
  categories,
  submitMessage
}
