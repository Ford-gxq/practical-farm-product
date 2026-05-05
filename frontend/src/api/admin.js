/**
 * 后台管理相关接口统一封装文件
 *
 * 这个文件的作用：
 * 1. 统一管理后台所有接口地址，避免在 Vue 页面中到处写 URL。
 * 2. 页面只需要调用这里的方法，不需要关心 axios 的具体写法。
 * 3. 后续如果后端接口路径变化，只需要集中修改这个文件。
 *
 * 注意：
 * request 是项目封装好的 axios 实例。
 * 它通常会统一处理：
 * - baseURL，例如 /api
 * - 请求头 token
 * - 响应 code 判断
 * - 错误提示
 */
import request from './request'

/**
 * 后台登录接口
 *
 * 请求地址：
 * POST /api/admin/auth/login
 *
 * 前端这里不用写 /api，因为 request.js 里一般已经配置了 baseURL: '/api'
 *
 * @param {Object} data 登录表单数据
 * @param {string} data.username 用户名
 * @param {string} data.password 密码
 * @returns {Promise} 返回登录结果，通常包含 token、nickname 等信息
 *
 * 示例：
 * login({
 *   username: 'admin',
 *   password: 'admin123'
 * })
 */
export const login = data => request.post('/admin/auth/login', data)

/**
 * 获取后台仪表盘统计数据
 *
 * 请求地址：
 * GET /api/admin/dashboard
 *
 * 一般用于后台首页展示：
 * - 商品数量
 * - 分类数量
 * - 浏览量
 * - 乡村故事数量
 */
export const getDashboard = () => request.get('/admin/dashboard')

/**
 * 获取后台商品分类列表
 *
 * 请求地址：
 * GET /api/admin/categories
 *
 * 用途：
 * 1. 商品分类管理页面展示分类列表。
 * 2. 内容管理页面新增/编辑商品时，用于下拉选择分类。
 */
export const getAdminCategories = () => request.get('/admin/categories')

/**
 * 新增或修改商品分类
 *
 * 请求地址：
 * POST /api/admin/categories
 *
 * @param {Object} data 分类数据
 * @param {number|null} data.id 分类 ID
 * - 有 id：表示修改分类
 * - 没有 id：表示新增分类
 * @param {string} data.name 分类名称
 * @param {number} data.sortOrder 排序值
 */
export const saveCategory = data => request.post('/admin/categories', data)

/**
 * 删除商品分类
 *
 * 请求地址：
 * DELETE /api/admin/categories/{id}
 *
 * @param {number} id 分类 ID
 *
 * 注意：
 * 如果该分类下面已经有关联商品，后端可能会限制删除。
 */
export const deleteCategory = id => request.delete(`/admin/categories/${id}`)

/**
 * 分页查询后台内容列表
 *
 * 请求地址：
 * GET /api/admin/articles
 *
 * 虽然后端接口名还叫 articles，
 * 但当前业务中它已经表示“内容管理”，包括：
 * - 农产品商品
 * - 乡村故事文章
 *
 * @param {Object} params 查询参数
 * @param {number} params.pageNo 当前页码
 * @param {number} params.pageSize 每页条数
 * @param {string} params.keyword 搜索关键词
 * @param {string} params.articleType 内容类型
 * - product：农产品商品
 * - story：乡村故事文章
 *
 * 示例：
 * getAdminArticles({
 *   pageNo: 1,
 *   pageSize: 10,
 *   keyword: '蜂蜜',
 *   articleType: 'product'
 * })
 */
export const getAdminArticles = params => request.get('/admin/articles', { params })

/**
 * 获取后台内容详情
 *
 * 请求地址：
 * GET /api/admin/articles/{id}
 *
 * @param {number} id 内容 ID
 *
 * 用途：
 * 编辑商品/文章时，根据 ID 查询完整内容。
 */
export const getAdminArticleDetail = id => request.get(`/admin/articles/${id}`)

/**
 * 新增或修改内容
 *
 * 请求地址：
 * POST /api/admin/articles
 *
 * @param {Object} data 内容表单数据
 *
 * 常见字段：
 * @param {number|null} data.id 内容 ID
 * - 有 id：修改
 * - 无 id：新增
 *
 * @param {string} data.articleType 内容类型
 * - product：农产品商品
 * - story：乡村故事文章
 *
 * @param {string} data.title 标题/商品名称
 * @param {number} data.categoryId 分类 ID
 * @param {string} data.summary 简介
 * @param {string} data.coverImage 封面图片路径，例如 /asset/images/covers/product-honey.jpg
 * @param {string} data.content Markdown 正文内容
 * @param {number} data.status 状态：0 草稿，1 发布
 *
 * 商品专用字段：
 * @param {number} data.price 商品价格
 * @param {string} data.unit 单位，例如 元/斤、元/瓶
 * @param {string} data.salesText 成交文案，例如 成交7.2万元
 * @param {string} data.productTags 商品标签，多个标签用英文逗号分隔
 * @param {string} data.farmerName 农户/作者
 * @param {string} data.farmerPhone 联系电话
 * @param {string} data.originPlace 产地/地点
 * @param {number} data.recommended 是否推荐：0 否，1 是
 */
export const saveArticle = data => request.post('/admin/articles', data)

/**
 * 删除内容
 *
 * 请求地址：
 * DELETE /api/admin/articles/{id}
 *
 * @param {number} id 内容 ID
 *
 * 当前项目一般是逻辑删除，不一定真的物理删除数据库记录。
 */
export const deleteArticle = id => request.delete(`/admin/articles/${id}`)

/**
 * 查询图片列表
 *
 * 请求地址：
 * GET /api/admin/images
 *
 * 当前新版图片资源已经分成两类：
 * 1. cover：封面图片
 *    保存目录：/asset/images/covers/
 *
 * 2. content：正文插图
 *    保存目录：/asset/images/content/
 *
 * @param {string} type 图片类型
 * - cover：查询封面图片列表
 * - content：查询正文插图列表
 *
 * @returns {Promise} 返回图片列表
 *
 * 返回示例：
 * [
 *   {
 *     name: 'product-honey.jpg',
 *     path: '/asset/images/covers/product-honey.jpg',
 *     size: 123456
 *   }
 * ]
 *
 * 默认值：
 * 如果不传 type，默认查 cover。
 */
export const getImageList = (type = 'cover') => request.get('/admin/images', {
  params: { type }
})

/**
 * 上传图片
 *
 * 请求地址：
 * POST /api/admin/images/upload
 *
 * 当前支持两种上传类型：
 *
 * 1. type = cover
 *    用于上传商品/文章封面图
 *    保存到：/asset/images/covers/
 *
 * 2. type = content
 *    用于上传 Markdown 正文插图
 *    保存到：/asset/images/content/
 *
 * @param {File} file 浏览器选择的图片文件
 * @param {string} type 图片类型
 * - cover：封面图片
 * - content：正文插图
 *
 * @returns {Promise} 返回上传后的图片信息
 *
 * 返回示例：
 * {
 *   name: 'cover-20260502-abcd1234.jpg',
 *   path: '/asset/images/covers/cover-20260502-abcd1234.jpg',
 *   width: 800,
 *   height: 800,
 *   size: 123456
 * }
 *
 * 注意：
 * 这里必须使用 FormData，因为图片上传是 multipart/form-data 格式。
 */
export const uploadImage = (file, type = 'cover') => {
  // 创建 FormData 对象，用来模拟表单文件上传。
  const formData = new FormData()

  // 后端接口中使用 @RequestParam("file") 接收，所以这里的 key 必须叫 file。
  formData.append('file', file)

  return request.post('/admin/images/upload', formData, {
    // type 作为 URL 查询参数传给后端。
    // 例如：/admin/images/upload?type=cover
    params: { type },

    // 告诉后端当前请求是文件上传。
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 删除图片
 *
 * 请求地址：
 * DELETE /api/admin/images
 *
 * @param {string} name 图片文件名，例如 product-honey.jpg
 * @param {string} type 图片类型
 * - cover：删除封面图片
 * - content：删除正文插图
 *
 * 注意：
 * 这里只传文件名，不传完整路径。
 * 后端会根据 type 判断是删除 covers 目录还是 content 目录。
 *
 * 示例：
 * deleteImage('product-honey.jpg', 'cover')
 */
export const deleteImage = (name, type = 'cover') => request.delete('/admin/images', {
  params: {
    name,
    type
  }
})
/**
 * 分页查询用户留言列表
 *
 * 请求地址：GET /api/admin/messages
 *
 * @param {Object} params 查询参数
 * @param {number} params.pageNo 当前页码
 * @param {number} params.pageSize 每页条数
 * @param {number|null} params.status 处理状态：0未处理，1已处理
 * @param {string} params.keyword 搜索关键词：姓名、电话、留言内容
 */
export const getAdminMessages = params => request.get('/admin/messages', { params })

/**
 * 修改留言处理状态和管理员备注
 *
 * 请求地址：PUT /api/admin/messages/{id}/status
 *
 * @param {number} id 留言ID
 * @param {Object} data 处理信息
 * @param {number} data.status 状态：0未处理，1已处理
 * @param {string} data.adminRemark 管理员备注
 */
export const updateMessageStatus = (id, data) => request.put(`/admin/messages/${id}/status`, data)

/**
 * 删除留言
 *
 * 请求地址：DELETE /api/admin/messages/{id}
 */
export const deleteMessage = id => request.delete(`/admin/messages/${id}`)
