import { createRouter, createWebHistory } from 'vue-router'

// 前台页面
import SiteHome from '../views/site/SiteHome.vue'
import ArticleDetail from '../views/site/ArticleDetail.vue'

// 后台页面
import AdminLogin from '../views/admin/AdminLogin.vue'
import AdminLayout from '../views/admin/AdminLayout.vue'
import AdminDashboard from '../views/admin/AdminDashboard.vue'
import AdminArticles from '../views/admin/AdminArticles.vue'
import AdminCategories from '../views/admin/AdminCategories.vue'
import AdminImages from '../views/admin/AdminImages.vue'

/**
 * Vue Router 路由配置。
 *
 * 前台：
 *   /              家有良田首页
 *   /article/:id   商品详情或乡村故事详情
 *
 * 后台：
 *   /admin/login       后台登录
 *   /admin/dashboard   仪表盘
 *   /admin/articles    内容管理
 *   /admin/categories  商品分类
 *   /admin/images      封面图片管理
 */
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

/**
 * 路由守卫。
 *
 * 只要访问 /admin 开头的页面，就要求本地存在 blog_token。
 * token 的有效性还会由后端 AdminAuthInterceptor 再校验一次。
 */
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
