<template>
  <div>
    <h1>仪表盘</h1>
    <el-row :gutter="16">
      <el-col :span="6"><el-card><h2>{{ data.articleCount }}</h2><p>商品总数</p></el-card></el-col>
      <el-col :span="6"><el-card><h2>{{ data.publishedCount }}</h2><p>已上架商品</p></el-card></el-col>
      <el-col :span="6"><el-card><h2>{{ data.categoryCount }}</h2><p>商品分类数</p></el-card></el-col>
      <el-col :span="6"><el-card><h2>{{ data.unhandledMessageCount }}</h2><p>未处理留言</p></el-card></el-col>
    </el-row>
  </div>
</template>

<script setup>
import { onMounted, reactive } from 'vue'
import { getDashboard } from '../../api/admin'

const data = reactive({ articleCount: 0, publishedCount: 0, categoryCount: 0, messageCount: 0, unhandledMessageCount: 0 })
onMounted(async () => Object.assign(data, await getDashboard()))
</script>
