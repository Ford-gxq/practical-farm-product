<template>
  <div>
    <header class="site-header">
      <div class="site-header-inner">
        <router-link to="/">← 返回首页</router-link>
      </div>
    </header>

    <main class="page-container product-detail-container">
      <article class="card product-detail-card" v-if="article">
        <div class="detail-layout">
          <div class="cover-box">
            <img v-if="article.coverImage" :src="article.coverImage" :alt="article.title" />
          </div>

          <div class="info-box">
            <el-tag :type="article.articleType === 'story' ? 'warning' : 'success'" style="margin-bottom: 12px">
              {{ article.articleType === 'story' ? '乡村故事' : '农产品商品' }}
            </el-tag>
            <h1>{{ article.title }}</h1>

            <template v-if="article.articleType !== 'story'">
              <div class="detail-price">
                {{ article.price ? Number(article.price).toFixed(2) : '面议' }}
                <span>{{ article.price ? article.unit || '' : '' }}</span>
              </div>
            </template>

            <div class="detail-meta">
              分类：{{ article.categoryName }} ｜ 产地/地点：{{ article.originPlace || '本地农户' }} ｜ 浏览：{{ article.viewCount }}
            </div>

            <div class="tag-list">
              <span v-for="tag in splitTags(article.productTags)" :key="tag">{{ tag }}</span>
            </div>

            <div class="detail-contact">
              <div><strong>{{ article.articleType === 'story' ? '讲述人：' : '农户：' }}</strong>{{ article.farmerName || '本地农户' }}</div>
              <div><strong>联系电话：</strong>{{ article.farmerPhone || '请联系服务站' }}</div>
              <div v-if="article.articleType !== 'story'"><strong>采购说明：</strong>{{ article.salesText || '支持咨询价格、批量采购、产地直发合作' }}</div>
            </div>
          </div>
        </div>

        <div class="content-title">{{ article.articleType === 'story' ? '故事正文' : '商品详情' }}</div>
        <div class="article-content markdown-body" v-html="renderedContent"></div>
      </article>
    </main>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { getSiteArticleDetail } from '../../api/site'
import MarkdownIt from 'markdown-it'

const route = useRoute()
const article = ref(null)

const md = new MarkdownIt({ html: true, linkify: true, breaks: true })
const renderedContent = computed(() => md.render(article.value?.content || ''))

function splitTags(tags) {
  if (!tags) return ['源头直发']
  return tags.split(',').map(item => item.trim()).filter(Boolean)
}

onMounted(async () => {
  article.value = await getSiteArticleDetail(route.params.id)
})
</script>

<style scoped>
.product-detail-container { max-width: 1200px; }
.product-detail-card { padding: 34px; }
.detail-layout { display: grid; grid-template-columns: 420px 1fr; gap: 34px; margin-bottom: 34px; }
.cover-box { background: #f3f4f6; border-radius: 10px; overflow: hidden; min-height: 320px; }
.cover-box img { width: 100%; height: 100%; object-fit: cover; display: block; }
.info-box h1 { font-size: 34px; margin: 0 0 18px; color: #111827; }
.detail-price { color: #ff3b20; font-size: 38px; font-weight: 700; margin-bottom: 12px; }
.detail-price span { font-size: 16px; margin-left: 4px; }
.detail-meta { color: #6b7280; line-height: 1.8; }
.tag-list { display: flex; flex-wrap: wrap; gap: 8px; margin: 16px 0; }
.tag-list span { border: 1px solid #93c5fd; color: #2563eb; padding: 3px 8px; border-radius: 4px; font-size: 14px; }
.detail-contact { margin-top: 18px; padding: 18px; background: #f9fafb; border-radius: 8px; line-height: 2; }
.content-title { font-size: 24px; font-weight: 700; margin: 24px 0 12px; padding-top: 24px; border-top: 1px solid #e5e7eb; }
.markdown-body { font-size: 16px; line-height: 2; color: #1f2937; }
.markdown-body :deep(h1) { font-size: 32px; margin: 32px 0 20px; font-weight: 700; }
.markdown-body :deep(h2) { font-size: 26px; margin: 28px 0 16px; font-weight: 700; }
.markdown-body :deep(h3) { font-size: 22px; margin: 24px 0 14px; font-weight: 700; }
.markdown-body :deep(p) { margin: 16px 0; }
.markdown-body :deep(ul), .markdown-body :deep(ol) { padding-left: 28px; margin: 16px 0; }
.markdown-body :deep(li) { margin: 8px 0; }
.markdown-body :deep(blockquote) { margin: 20px 0; padding: 12px 18px; background: #f3f4f6; border-left: 4px solid #16a34a; color: #4b5563; }
.markdown-body :deep(code) { padding: 2px 6px; background: #f3f4f6; border-radius: 4px; font-family: Consolas, Monaco, monospace; }
.markdown-body :deep(pre) { padding: 16px; background: #111827; color: #f9fafb; border-radius: 8px; overflow-x: auto; }
.markdown-body :deep(pre code) { padding: 0; background: transparent; color: inherit; }
.markdown-body :deep(img) { max-width: 100%; border-radius: 8px; margin: 16px 0; }
.markdown-body :deep(table) { width: 100%; border-collapse: collapse; margin: 20px 0; }
.markdown-body :deep(th), .markdown-body :deep(td) { border: 1px solid #e5e7eb; padding: 10px 12px; }
.markdown-body :deep(th) { background: #f9fafb; }
@media (max-width: 900px) { .detail-layout { grid-template-columns: 1fr; } }
</style>
