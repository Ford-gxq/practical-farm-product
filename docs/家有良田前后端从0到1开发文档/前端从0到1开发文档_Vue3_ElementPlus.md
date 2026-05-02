# 家有良田农产品网站前端从 0 到 1 开发文档

> 对应项目目录：`frontend/`  
> 技术栈：Vue 3、Vite、Vue Router、Pinia、Axios、Element Plus、md-editor-v3、markdown-it  
> 目标：你可以照着本文档，从空目录一步步开发出和当前项目同结构、同功能的前端。

---

## 1. 前端项目最终效果

本项目的前端分为两套界面：

1. **前台展示网站**
   - 首页 `/`
   - 商品列表展示
   - 源头好物分类筛选
   - 时令推荐筛选
   - 乡村故事展示
   - 商品/故事详情页 `/article/:id`

2. **后台管理系统**
   - 登录页 `/admin/login`
   - 后台布局 `/admin`
   - 仪表盘 `/admin/dashboard`
   - 内容管理 `/admin/articles`
   - 分类管理 `/admin/categories`
   - 封面图片管理 `/admin/images`

前端的核心工作是：

```text
Vue 页面组件
  ↓ 调用
api/*.js 接口封装
  ↓ 使用
Axios 请求 /api/xxx
  ↓ Vite 代理
Spring Boot 后端 http://localhost:8088
```

---

## 2. 前端技术栈知识点总览

### 2.1 Vue 3

当前项目使用 Vue 3 的组合式 API：

```js
import { ref, reactive, computed, onMounted } from 'vue'
```

常用知识点：

| 知识点 | 作用 | 项目中的例子 |
|---|---|---|
| `ref()` | 定义基本响应式变量 | `const articles = ref([])` |
| `reactive()` | 定义对象型响应式数据 | `const query = reactive({ pageNo: 1 })` |
| `computed()` | 定义计算属性 | `displayProducts` 根据推荐状态过滤商品 |
| `onMounted()` | 页面加载完成后执行 | 进入首页后加载分类和商品 |
| `v-if` | 条件渲染 | 根据 `activeView` 显示不同区域 |
| `v-for` | 循环渲染 | 循环商品卡片、分类下拉菜单 |
| `v-model` | 表单双向绑定 | 登录表单、商品编辑表单 |
| `@click` | 事件绑定 | 点击分类、提交表单、删除商品 |
| `:class` | 动态 class | 导航菜单高亮 |
| `:src` | 动态图片路径 | 商品封面图展示 |

### 2.2 Vite

Vite 是前端构建工具，负责本地开发服务器、热更新、打包。

项目中的 `package.json`：

```json
{
  "scripts": {
    "dev": "vite --host 0.0.0.0",
    "build": "vite build",
    "preview": "vite preview"
  }
}
```

常用命令：

```bash
npm install
npm run dev
npm run build
npm run preview
```

### 2.3 Vue Router

Vue Router 负责前端页面路由。

当前项目路由：

```text
/                     前台首页
/article/:id          商品或故事详情
/admin/login          后台登录
/admin/dashboard      仪表盘
/admin/articles       内容管理
/admin/categories     分类管理
/admin/images         图片管理
```

核心知识点：

```js
import { createRouter, createWebHistory } from 'vue-router'
```

后台还使用了路由守卫：

```js
router.beforeEach((to, from, next) => {
  if (to.path.startsWith('/admin') && to.path !== '/admin/login') {
    const token = localStorage.getItem('blog_token')
    if (!token) {
      return next('/admin/login')
    }
  }
  next()
})
```

作用：访问后台页面前，先判断本地是否有 `blog_token`。没有 token 就跳转登录页。

### 2.4 Pinia

Pinia 是 Vue 3 推荐的状态管理库。当前项目用它保存登录状态。

项目中的 store：`src/store/user.js`

核心数据：

```js
token
nickname
role
```

核心动作：

```js
setLoginInfo(token, nickname, role)
logout()
```

### 2.5 Axios

Axios 用来请求后端接口。当前项目封装在 `src/api/request.js`。

封装重点：

1. 统一 baseURL：`/api`
2. 请求时自动携带 `X-Token`
3. 响应时统一判断 `code === 200`
4. 登录失效时自动跳转登录页

### 2.6 Element Plus

Element Plus 是 Vue 3 UI 组件库。后台页面大量使用它。

项目中用到的组件：

| 组件 | 用途 |
|---|---|
| `el-button` | 按钮 |
| `el-form` / `el-form-item` | 表单 |
| `el-input` | 输入框 |
| `el-input-number` | 数字输入框 |
| `el-select` / `el-option` | 下拉选择 |
| `el-table` / `el-table-column` | 表格 |
| `el-dialog` | 弹窗 |
| `el-pagination` | 分页 |
| `el-upload` | 图片上传 |
| `el-card` | 卡片 |
| `el-row` / `el-col` | 栅格布局 |
| `el-tag` | 标签 |
| `ElMessage` | 消息提示 |
| `ElMessageBox` | 确认弹框 |

### 2.7 Markdown 编辑与渲染

后台内容编辑使用：

```text
md-editor-v3
```

前台详情页渲染 Markdown 使用：

```text
markdown-it
```

详情页中的核心代码：

```js
import MarkdownIt from 'markdown-it'

const md = new MarkdownIt({ html: true, linkify: true, breaks: true })
const renderedContent = computed(() => md.render(article.value?.content || ''))
```

---

## 3. 从 0 创建前端项目

### 3.1 准备环境

建议版本：

```text
Node.js 18+ 或 20+
npm 9+
```

查看版本：

```bash
node -v
npm -v
```

### 3.2 创建 Vite + Vue 项目

```bash
npm create vite@latest frontend -- --template vue
cd frontend
npm install
```

### 3.3 安装项目依赖

```bash
npm install vue-router pinia axios element-plus markdown-it md-editor-v3
```

如果需要保持和当前项目一致，也可以安装 Vite 插件：

```bash
npm install @vitejs/plugin-vue vite
```

最终 `package.json` 的依赖类似：

```json
{
  "dependencies": {
    "@vitejs/plugin-vue": "latest",
    "axios": "latest",
    "element-plus": "latest",
    "markdown-it": "^14.1.1",
    "md-editor-v3": "^6.5.0",
    "pinia": "latest",
    "vite": "latest",
    "vue": "latest",
    "vue-router": "latest"
  }
}
```

---

## 4. 前端目录结构设计

照着当前项目创建目录：

```text
frontend
├─ index.html
├─ package.json
├─ vite.config.js
├─ public
│  └─ asset
│     └─ images
│        ├─ covers       # 商品封面、故事封面
│        └─ content      # Markdown 正文插图
└─ src
   ├─ main.js            # 项目入口
   ├─ App.vue            # 根组件，只放 router-view
   ├─ style.css          # 全局样式
   ├─ api
   │  ├─ request.js      # axios 统一封装
   │  ├─ site.js         # 前台接口
   │  └─ admin.js        # 后台接口
   ├─ router
   │  └─ index.js        # 路由配置
   ├─ store
   │  └─ user.js         # Pinia 登录状态
   ├─ components
   │  └─ ParticleBackground.vue
   └─ views
      ├─ site
      │  ├─ SiteHome.vue
      │  └─ ArticleDetail.vue
      └─ admin
         ├─ AdminLogin.vue
         ├─ AdminLayout.vue
         ├─ AdminDashboard.vue
         ├─ AdminArticles.vue
         ├─ AdminCategories.vue
         └─ AdminImages.vue
```

---

## 5. 第一步：编写入口文件

### 5.1 `src/main.js`

```js
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './style.css'
import App from './App.vue'
import router from './router'

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.use(ElementPlus)
app.mount('#app')
```

解释：

1. `createApp(App)` 创建 Vue 应用。
2. `app.use(createPinia())` 注册 Pinia。
3. `app.use(router)` 注册路由。
4. `app.use(ElementPlus)` 注册 Element Plus。
5. `app.mount('#app')` 挂载到 `index.html` 的 `#app`。

### 5.2 `src/App.vue`

```vue
<template>
  <router-view />
</template>
```

根组件只负责显示当前路由匹配到的页面。

---

## 6. 第二步：配置 Vite 代理

### 6.1 `vite.config.js`

```js
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8088',
        changeOrigin: true
      }
    }
  }
})
```

为什么要配置代理？

前端访问：

```text
http://localhost:5173/api/site/articles
```

Vite 会转发到：

```text
http://localhost:8088/api/site/articles
```

这样前端代码只需要写 `/api`，不用写完整后端地址。

---

## 7. 第三步：封装 Axios

### 7.1 `src/api/request.js`

```js
import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.request.use(config => {
  const token = localStorage.getItem('blog_token')
  if (token) {
    config.headers['X-Token'] = token
  }
  return config
})

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
```

### 7.2 为什么要封装 request？

不封装时，每个页面都要写：

```js
axios.get('/api/site/articles')
```

还要手动处理 token、错误提示、code 判断。封装后页面只关心业务数据：

```js
const page = await getSiteArticles(query)
articles.value = page.records
```

---

## 8. 第四步：封装接口文件

### 8.1 前台接口 `src/api/site.js`

```js
import request from './request'

export const getSiteCategories = () => request.get('/site/categories')
export const getSiteArticles = params => request.get('/site/articles', { params })
export const getSiteArticleDetail = id => request.get(`/site/articles/${id}`)
```

对应后端接口：

| 前端方法 | 请求方式 | 后端地址 |
|---|---|---|
| `getSiteCategories` | GET | `/api/site/categories` |
| `getSiteArticles` | GET | `/api/site/articles` |
| `getSiteArticleDetail` | GET | `/api/site/articles/{id}` |

### 8.2 后台接口 `src/api/admin.js`

```js
import request from './request'

export const login = data => request.post('/admin/auth/login', data)
export const getDashboard = () => request.get('/admin/dashboard')
export const getAdminCategories = () => request.get('/admin/categories')
export const saveCategory = data => request.post('/admin/categories', data)
export const deleteCategory = id => request.delete(`/admin/categories/${id}`)
export const getAdminArticles = params => request.get('/admin/articles', { params })
export const getAdminArticleDetail = id => request.get(`/admin/articles/${id}`)
export const saveArticle = data => request.post('/admin/articles', data)
export const deleteArticle = id => request.delete(`/admin/articles/${id}`)

export const getImageList = (type = 'cover') => request.get('/admin/images', {
  params: { type }
})

export const uploadImage = (file, type = 'cover') => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/admin/images/upload', formData, {
    params: { type },
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export const deleteImage = (name, type = 'cover') => request.delete('/admin/images', {
  params: { name, type }
})
```

---

## 9. 第五步：配置路由

### 9.1 `src/router/index.js`

```js
import { createRouter, createWebHistory } from 'vue-router'

import SiteHome from '../views/site/SiteHome.vue'
import ArticleDetail from '../views/site/ArticleDetail.vue'
import AdminLogin from '../views/admin/AdminLogin.vue'
import AdminLayout from '../views/admin/AdminLayout.vue'
import AdminDashboard from '../views/admin/AdminDashboard.vue'
import AdminArticles from '../views/admin/AdminArticles.vue'
import AdminCategories from '../views/admin/AdminCategories.vue'
import AdminImages from '../views/admin/AdminImages.vue'

const routes = [
  { path: '/', component: SiteHome },
  { path: '/article/:id', component: ArticleDetail },
  { path: '/admin/login', component: AdminLogin },
  {
    path: '/admin',
    component: AdminLayout,
    children: [
      { path: '', redirect: '/admin/dashboard' },
      { path: 'dashboard', component: AdminDashboard },
      { path: 'articles', component: AdminArticles },
      { path: 'categories', component: AdminCategories },
      { path: 'images', component: AdminImages }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  if (to.path.startsWith('/admin') && to.path !== '/admin/login') {
    const token = localStorage.getItem('blog_token')
    if (!token) {
      return next('/admin/login')
    }
  }
  next()
})

export default router
```

### 9.2 子路由解释

`/admin` 使用 `AdminLayout.vue` 作为后台公共布局，里面放侧边栏和顶部栏。

子页面通过：

```vue
<router-view />
```

显示在布局内容区域。

---

## 10. 第六步：编写 Pinia 登录状态

### 10.1 `src/store/user.js`

```js
import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('blog_token') || '',
    nickname: localStorage.getItem('blog_nickname') || '',
    role: localStorage.getItem('blog_role') || ''
  }),
  actions: {
    setLoginInfo(token, nickname, role = '') {
      this.token = token
      this.nickname = nickname
      this.role = role
      localStorage.setItem('blog_token', token)
      localStorage.setItem('blog_nickname', nickname)
      localStorage.setItem('blog_role', role)
    },
    logout() {
      this.token = ''
      this.nickname = ''
      this.role = ''
      localStorage.removeItem('blog_token')
      localStorage.removeItem('blog_nickname')
      localStorage.removeItem('blog_role')
    }
  }
})
```

### 10.2 为什么 localStorage 和 Pinia 都要用？

- Pinia：页面运行期间方便响应式读取。
- localStorage：刷新页面后还能保留登录状态。

---

## 11. 第七步：开发后台登录页

### 11.1 页面功能

登录页需要完成：

1. 输入用户名和密码。
2. 调用 `/api/admin/auth/login`。
3. 登录成功后保存 token。
4. 跳转 `/admin/dashboard`。

### 11.2 核心代码结构

```vue
<template>
  <div class="login-page">
    <el-card class="login-card">
      <h2>家有良田后台登录</h2>

      <el-form :model="form" @keyup.enter="submit">
        <el-form-item label="用户名">
          <el-input v-model="form.username" />
        </el-form-item>

        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>

        <el-button type="primary" @click="submit">登录</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '../../api/admin'
import { useUserStore } from '../../store/user'

const router = useRouter()
const userStore = useUserStore()

const form = reactive({
  username: 'admin',
  password: 'admin123'
})

async function submit() {
  const data = await login(form)
  userStore.setLoginInfo(data.token, data.nickname, data.role)
  ElMessage.success('登录成功')
  router.push('/admin/dashboard')
}
</script>
```

---

## 12. 第八步：开发后台布局页

后台布局页负责：

1. 左侧菜单。
2. 顶部用户信息。
3. 退出登录。
4. 子页面显示出口。

核心结构：

```vue
<template>
  <el-container class="admin-layout">
    <el-aside width="220px">
      <div class="admin-logo">家有良田后台</div>
      <el-menu router default-active="/admin/dashboard">
        <el-menu-item index="/admin/dashboard">仪表盘</el-menu-item>
        <el-menu-item index="/admin/articles">内容管理</el-menu-item>
        <el-menu-item index="/admin/categories">商品分类</el-menu-item>
        <el-menu-item index="/admin/images">封面图片</el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header>
        <span>后台管理系统</span>
        <el-button @click="logout">退出登录</el-button>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>
```

退出登录逻辑：

```js
function logout() {
  userStore.logout()
  router.push('/admin/login')
}
```

---

## 13. 第九步：开发仪表盘页面

### 13.1 页面目标

显示三个统计值：

- 商品总数
- 已上架商品
- 商品分类数

### 13.2 核心代码

```vue
<template>
  <div>
    <h1>仪表盘</h1>
    <el-row :gutter="16">
      <el-col :span="8">
        <el-card><h2>{{ data.articleCount }}</h2><p>商品总数</p></el-card>
      </el-col>
      <el-col :span="8">
        <el-card><h2>{{ data.publishedCount }}</h2><p>已上架商品</p></el-card>
      </el-col>
      <el-col :span="8">
        <el-card><h2>{{ data.categoryCount }}</h2><p>商品分类数</p></el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { onMounted, reactive } from 'vue'
import { getDashboard } from '../../api/admin'

const data = reactive({
  articleCount: 0,
  publishedCount: 0,
  categoryCount: 0
})

onMounted(async () => {
  Object.assign(data, await getDashboard())
})
</script>
```

---

## 14. 第十步：开发分类管理页面

### 14.1 页面功能

分类管理页包含：

1. 查询分类列表。
2. 新增分类。
3. 编辑分类。
4. 删除分类。

### 14.2 核心知识点

| 功能 | Element Plus 组件 |
|---|---|
| 表格 | `el-table` |
| 弹窗 | `el-dialog` |
| 表单 | `el-form` |
| 输入框 | `el-input` |
| 数字排序 | `el-input-number` |
| 删除确认 | `ElMessageBox.confirm` |

### 14.3 核心代码

```vue
<template>
  <div>
    <div class="toolbar">
      <h1>商品分类管理</h1>
      <el-button type="primary" @click="openDialog()">新增商品分类</el-button>
    </div>

    <el-table :data="list" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="商品分类名称" />
      <el-table-column prop="sortOrder" label="排序" width="100" />
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button size="small" @click="openDialog(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="visible" title="商品分类表单" width="420px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteCategory, getAdminCategories, saveCategory } from '../../api/admin'

const list = ref([])
const visible = ref(false)
const form = reactive({ id: null, name: '', sortOrder: 0 })

async function load() {
  list.value = await getAdminCategories()
}

function openDialog(row) {
  Object.assign(form, row || { id: null, name: '', sortOrder: 0 })
  visible.value = true
}

async function submit() {
  await saveCategory(form)
  ElMessage.success('保存成功')
  visible.value = false
  load()
}

async function remove(id) {
  await ElMessageBox.confirm('确认删除该分类？')
  await deleteCategory(id)
  ElMessage.success('删除成功')
  load()
}

onMounted(load)
</script>
```

---

## 15. 第十一步：开发内容管理页面

内容管理页是后台最复杂的页面。它管理两类内容：

```text
product = 农产品商品
story   = 乡村故事文章
```

### 15.1 页面功能拆分

1. 顶部搜索：关键词、内容类型。
2. 表格展示：标题、分类、状态、浏览量、操作。
3. 分页查询。
4. 新增内容。
5. 编辑内容。
6. 删除内容。
7. 商品字段：价格、单位、成交文案、标签、农户、电话、产地、推荐。
8. Markdown 正文编辑。
9. 封面图片下拉选择。

### 15.2 数据结构

表单建议这样定义：

```js
const form = reactive({
  id: null,
  articleType: 'product',
  title: '',
  categoryId: null,
  coverImage: '',
  summary: '',
  content: '',
  status: 1,
  price: 0,
  unit: '元/斤',
  salesText: '',
  productTags: '',
  farmerName: '',
  farmerPhone: '',
  originPlace: '',
  recommended: 0
})
```

查询参数：

```js
const query = reactive({
  pageNo: 1,
  pageSize: 10,
  keyword: '',
  articleType: ''
})
```

### 15.3 分页加载

```js
async function load() {
  const page = await getAdminArticles(query)
  list.value = page.records || []
  total.value = page.total || 0
}
```

### 15.4 新增和编辑共用一个保存接口

```js
async function submit() {
  await saveArticle(form)
  ElMessage.success('保存成功')
  visible.value = false
  load()
}
```

后端通过 `id` 判断：

```text
id 为空：新增
id 不为空：修改
```

### 15.5 Markdown 编辑器

安装：

```bash
npm install md-editor-v3
```

页面中引入：

```js
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
```

模板中使用：

```vue
<MdEditor v-model="form.content" style="height: 420px" />
```

### 15.6 封面图片选择

后台图片接口返回：

```json
[
  {
    "name": "product-honey.jpg",
    "path": "/asset/images/covers/product-honey.jpg",
    "size": 123456
  }
]
```

页面加载：

```js
const imageOptions = ref([])

async function loadImages() {
  imageOptions.value = await getImageList('cover')
}
```

下拉选择：

```vue
<el-select v-model="form.coverImage" placeholder="请选择封面图片" filterable clearable>
  <el-option
    v-for="img in imageOptions"
    :key="img.path"
    :label="img.name"
    :value="img.path"
  />
</el-select>

<img v-if="form.coverImage" :src="form.coverImage" class="cover-mini-preview" />
```

---

## 16. 第十二步：开发图片管理页面

### 16.1 页面功能

图片管理页负责：

1. 查询封面图片。
2. 上传封面图片。
3. 删除封面图片。
4. 复制图片路径。

### 16.2 上传前端校验

当前项目限制图片尺寸不能超过：

```text
1000 × 1000 px
```

前端校验代码：

```js
function beforeUpload(file) {
  if (!file.type.startsWith('image/')) {
    ElMessage.error('只能上传图片文件')
    return false
  }

  return new Promise((resolve, reject) => {
    const img = new Image()
    const url = URL.createObjectURL(file)

    img.onload = () => {
      URL.revokeObjectURL(url)
      if (img.width > 1000 || img.height > 1000) {
        ElMessage.error(`上传失败：图片尺寸不能超过 1000 × 1000 px，当前图片为 ${img.width} × ${img.height} px`)
        reject(false)
        return
      }
      resolve(true)
    }

    img.onerror = () => {
      URL.revokeObjectURL(url)
      ElMessage.error('图片读取失败')
      reject(false)
    }

    img.src = url
  })
}
```

### 16.3 自定义上传

```vue
<el-upload
  :show-file-list="false"
  :before-upload="beforeUpload"
  :http-request="handleUpload"
  accept="image/*"
>
  <el-button type="primary">上传封面图片</el-button>
</el-upload>
```

```js
async function handleUpload(option) {
  await uploadImage(option.file, 'cover')
  ElMessage.success('上传成功')
  await loadImages()
}
```

---

## 17. 第十三步：开发前台首页

### 17.1 首页数据来源

首页需要加载三类数据：

```js
onMounted(() => {
  loadCategories()
  loadArticles()
  loadStories()
})
```

对应接口：

```text
GET /api/site/categories
GET /api/site/articles?articleType=product&pageNo=1&pageSize=50
GET /api/site/articles?articleType=story&pageNo=1&pageSize=20
```

### 17.2 首页状态设计

```js
const categories = ref([])
const articles = ref([])
const storyArticles = ref([])
const activeView = ref('home')
const onlyRecommended = ref(false)

const query = reactive({
  pageNo: 1,
  pageSize: 50,
  categoryId: null,
  articleType: 'product'
})
```

`activeView` 的含义：

| 值 | 页面显示 |
|---|---|
| `home` | 首页全部区域 |
| `goods` | 源头好物 |
| `season` | 时令推荐 |
| `story` | 乡村故事 |
| `contact` | 联系站长 |

### 17.3 分类菜单

```js
const categoryMenu = computed(() => {
  return categories.value.map(c => ({
    ...c,
    desc: categoryDescriptions[c.name] || '本地农户好物'
  }))
})
```

### 17.4 商品列表过滤

```js
const displayProducts = computed(() => {
  if (!onlyRecommended.value) return articles.value
  return articles.value.filter(item => item.recommended === 1)
})
```

### 17.5 点击分类加载商品

```js
function selectCategory(categoryId) {
  activeView.value = 'goods'
  onlyRecommended.value = false
  query.categoryId = categoryId
  query.pageNo = 1
  loadArticles()
}
```

### 17.6 商品卡片跳转详情

```vue
<router-link
  v-for="item in displayProducts"
  :key="item.id"
  class="product-card"
  :to="`/article/${item.id}`"
>
  <img :src="item.coverImage || defaultImage" :alt="item.title" />
  <div class="product-title">{{ item.title }}</div>
</router-link>
```

---

## 18. 第十四步：开发详情页

### 18.1 获取路由参数

```js
import { useRoute } from 'vue-router'

const route = useRoute()
```

详情接口：

```js
onMounted(async () => {
  article.value = await getSiteArticleDetail(route.params.id)
})
```

### 18.2 渲染 Markdown

```js
const md = new MarkdownIt({ html: true, linkify: true, breaks: true })
const renderedContent = computed(() => md.render(article.value?.content || ''))
```

模板：

```vue
<div class="article-content markdown-body" v-html="renderedContent"></div>
```

注意：

`v-html` 会直接渲染 HTML。真实生产项目要注意 XSS 安全问题。本项目是后台管理员自己编辑内容，学习项目中可以这样处理。

---

## 19. 第十五步：编写全局样式

建议在 `src/style.css` 放公共样式：

```css
* {
  box-sizing: border-box;
}

body {
  margin: 0;
  font-family: Arial, "Microsoft YaHei", sans-serif;
  background: #f6f7fb;
}

a {
  color: inherit;
  text-decoration: none;
}

.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.08);
}
```

然后分别在各个 `.vue` 文件中使用：

```vue
<style scoped>
/* 当前组件自己的样式 */
</style>
```

---

## 20. 前端和后端的接口约定

后端统一返回：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

所以 Axios 响应拦截器中直接返回：

```js
return res.data
```

页面拿到的就是 `data`，不是完整响应。

分页接口返回：

```json
{
  "records": [],
  "total": 100,
  "pageNo": 1,
  "pageSize": 10
}
```

前端用法：

```js
const page = await getAdminArticles(query)
list.value = page.records || []
total.value = page.total || 0
```

---

## 21. 图片路径规则

图片实际位置：

```text
frontend/public/asset/images/covers
frontend/public/asset/images/content
```

浏览器访问路径：

```text
/asset/images/covers/xxx.jpg
/asset/images/content/xxx.jpg
```

数据库只保存相对访问路径：

```text
/asset/images/covers/product-honey.jpg
```

不要保存 Windows 绝对路径：

```text
E:\IDEA_Project\xxx\product-honey.jpg
```

否则部署后浏览器无法访问。

---

## 22. 本地运行前端

先启动后端：

```bash
cd backend
mvn spring-boot:run
```

再启动前端：

```bash
cd frontend
npm install
npm run dev
```

访问：

```text
前台：http://localhost:5173/
后台：http://localhost:5173/admin/login
```

默认账号：

```text
admin / admin123
```

---

## 23. 常见问题排查

### 23.1 页面请求 404

检查：

1. 后端是否启动在 `8088`。
2. `vite.config.js` 是否配置 `/api` 代理。
3. 前端接口是否写成 `/site/articles`，不要重复写 `/api/api/site/articles`。

### 23.2 后台跳回登录页

原因：

1. localStorage 没有 `blog_token`。
2. 后端重启后，内存 token 丢失。
3. token 请求头没有带上。

检查 `request.js`：

```js
config.headers['X-Token'] = token
```

### 23.3 图片显示不出来

检查图片路径是否以 `/asset/images/` 开头：

```text
正确：/asset/images/covers/product-honey.jpg
错误：hero-farm-banner.png
错误：E:/xxx/hero-farm-banner.png
```

如果路径是 `/asset/images/hero-farm-banner.png`，需要确认文件是否真的存在：

```text
frontend/public/asset/images/hero-farm-banner.png
```

如果路径是 `/asset/images/covers/hero-farm-banner.png`，需要确认文件是否真的存在：

```text
frontend/public/asset/images/covers/hero-farm-banner.png
```

### 23.4 Element Plus 样式没有生效

检查 `main.js` 是否引入：

```js
import 'element-plus/dist/index.css'
```

### 23.5 Markdown 不显示格式

检查是否安装并引入：

```bash
npm install markdown-it
```

详情页是否使用：

```js
const md = new MarkdownIt({ html: true, linkify: true, breaks: true })
```

---

## 24. 推荐学习顺序

1. 先运行项目，看整体效果。
2. 看 `main.js`，理解插件注册。
3. 看 `router/index.js`，理解页面路由。
4. 看 `api/request.js`，理解 Axios 封装。
5. 看 `api/site.js`，理解前台接口。
6. 看 `SiteHome.vue`，理解首页数据加载和商品展示。
7. 看 `ArticleDetail.vue`，理解详情页和 Markdown 渲染。
8. 看 `store/user.js`，理解登录状态保存。
9. 看 `AdminLogin.vue`，理解登录流程。
10. 看 `AdminLayout.vue`，理解后台布局和子路由。
11. 看 `AdminCategories.vue`，理解简单 CRUD。
12. 看 `AdminArticles.vue`，理解复杂表单、分页、Markdown 编辑。
13. 看 `AdminImages.vue`，理解文件上传。

---

## 25. 你照着开发时的最小版本路线

不要一开始就写完整项目，建议按下面顺序做：

```text
第 1 步：创建 Vue + Vite 项目
第 2 步：安装 Element Plus、Router、Pinia、Axios
第 3 步：配置 main.js、App.vue、router
第 4 步：写 request.js 和接口文件
第 5 步：先写前台首页，只显示商品列表
第 6 步：写详情页，能根据 id 查看商品
第 7 步：写后台登录页，能保存 token
第 8 步：写后台布局页
第 9 步：写分类管理 CRUD
第 10 步：写商品管理 CRUD
第 11 步：接入 Markdown 编辑器
第 12 步：接入图片上传和封面选择
第 13 步：补充样式和交互细节
```

这样开发最稳，不容易被复杂页面卡住。
