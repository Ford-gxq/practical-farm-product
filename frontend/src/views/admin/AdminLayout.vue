<template>
  <div class="admin-layout">
    <aside class="admin-sidebar">
      <h2>农品后台</h2>
      <router-link to="/admin/dashboard">仪表盘</router-link>
      <router-link to="/admin/articles">内容管理</router-link>
      <router-link to="/admin/images">封面图片</router-link>
      <router-link to="/admin/categories">商品分类</router-link>
      <router-link to="/admin/messages">留言管理</router-link>
      <router-link to="/">返回网站</router-link>
      <a class="logout-link" href="javascript:void(0)" @click="logout">退出登录</a>
    </aside>
    <main class="admin-main"><router-view /></main>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '../../store/user'

const router = useRouter()
const userStore = useUserStore()
function logout() {
  userStore.logout()
  router.push('/admin/login')
}
</script>
