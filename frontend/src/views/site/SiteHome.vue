<template>
  <div class="farm-site">
    <header class="farm-header">
      <div class="header-inner">
        <div class="logo-area" @click="goHome">
          <div class="logo-mark">农</div>
          <div>
            <div class="site-title">家有良田</div>
            <div class="site-subtitle">农户直连 · 新鲜到家</div>
          </div>
        </div>

        <nav class="main-nav">
          <a href="javascript:;" :class="{ active: activeView === 'home' }" @click="showHome">首页</a>

          <div class="nav-item-with-dropdown">
            <a href="javascript:;" :class="{ active: activeView === 'goods' }" @click="showGoods">源头好物</a>
            <div class="dropdown-menu">
              <a
                  v-for="item in categoryMenu"
                  :key="item.name"
                  href="javascript:;"
                  @click="selectCategory(item.id)"
              >
                <strong>{{ item.name }}</strong>
                <small>{{ item.desc }}</small>
              </a>
            </div>
          </div>

          <a href="javascript:;" :class="{ active: activeView === 'season' }" @click="showRecommended">时令推荐</a>
          <a href="javascript:;" :class="{ active: activeView === 'story' }" @click="showStory">乡村故事</a>
          <a href="javascript:;" :class="{ active: activeView === 'contact' }" @click="showContact">联系站长</a>
        </nav>

        <router-link class="admin-link" to="/admin/login">后台管理</router-link>
      </div>
    </header>

    <section v-if="activeView === 'home'" class="hero-section">
      <div class="hero-content">
        <div class="hero-badge">源头直发 / 产地优选 / 农户好物</div>
        <h1>把县城乡村里的好农货，展示给更多人</h1>
        <p>
          汇集本地新鲜果蔬、禽蛋肉奶、粮油杂粮和特色农产品，
          帮助农户展示商品，也方便客户联系采购。
        </p>
        <div class="hero-actions">
          <button @click="showGoods">查看全部商品</button>
          <button class="secondary" @click="showContact">联系站长</button>
        </div>
      </div>
    </section>

    <main class="farm-container">
      <section v-if="showProductArea" class="category-section">
        <div class="section-title">
          <h2>{{ productSectionTitle }}</h2>
          <p>{{ productSectionDesc }}</p>
        </div>

        <div class="category-tabs" v-if="activeView !== 'season'">
          <button
              :class="{ active: query.categoryId === null }"
              @click="selectCategory(null)"
          >
            全部商品
          </button>

          <button
              v-for="c in categories"
              :key="c.id"
              :class="{ active: query.categoryId === c.id }"
              @click="selectCategory(c.id)"
          >
            {{ c.name }}
          </button>
        </div>
      </section>

      <section v-if="showProductArea" class="product-grid-section">
        <div class="product-grid">
          <router-link
              v-for="item in displayProducts"
              :key="item.id"
              class="product-card"
              :to="`/article/${item.id}`"
          >
            <div class="product-image-wrap">
              <img class="product-image" :src="item.coverImage || defaultImage" :alt="item.title"/>
              <span class="trade-badge">在线咨询</span>
            </div>

            <div class="product-body">
              <div class="price-row">
                <span class="price">{{ formatPrice(item.price) }}</span>
                <span class="unit">{{ item.price ? item.unit || '元/斤' : '' }}</span>
                <span class="sales">{{ item.salesText || '欢迎咨询' }}</span>
              </div>

              <div class="product-title">{{ item.title }}</div>

              <div class="product-tags">
                <span v-for="tag in splitTags(item.productTags)" :key="tag">{{ tag }}</span>
              </div>

              <div class="product-footer">
                <span>{{ item.originPlace || item.categoryName }}</span>
                <span>浏览 {{ item.viewCount }}</span>
              </div>
            </div>
          </router-link>
        </div>

        <el-empty v-if="displayProducts.length === 0" description="暂无商品，请到后台新增商品"/>
      </section>

      <section v-if="activeView === 'home' || activeView === 'story'" id="story" class="story-section only-section">
        <div class="section-title">
          <h2>乡村故事</h2>
          <p>只展示农户故事、种植过程、采摘记录和乡村生活文章，不展示商品列表。</p>
        </div>

        <div class="story-grid">
          <router-link
              v-for="story in storyArticles"
              :key="story.id"
              class="story-card"
              :to="`/article/${story.id}`"
          >
            <img :src="story.coverImage || '/asset/images/covers/story-field.svg'" :alt="story.title"/>
            <div class="story-info">
              <h3>{{ story.title }}</h3>
              <p>{{ story.summary }}</p>
              <span>阅读故事 →</span>
            </div>
          </router-link>
        </div>

        <el-empty v-if="storyArticles.length === 0" description="暂无乡村故事，请到后台发布故事文章"/>
      </section>

      <section v-if="activeView === 'home' || activeView === 'contact'" id="contact"
               class="contact-section only-section">
        <div class="section-title">
          <h2>联系站长</h2>
          <p>支持咨询价格、批量采购、产地直发合作</p>
        </div>

        <div class="contact-card">
          <div><strong>联系人：</strong>农产品服务站</div>
          <div><strong>联系电话1：</strong>138-0000-0000</div>
          <div><strong>联系电话2：</strong>138-0000-0001</div>
          <div><strong>服务范围：</strong>新鲜果蔬、粮油杂粮、特产农产品、农户手作</div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import {computed, onMounted, reactive, ref} from 'vue'
import {useRouter} from 'vue-router'
import {getSiteArticles, getSiteCategories} from '../../api/site'

const router = useRouter()
const categories = ref([])
const articles = ref([])
const storyArticles = ref([])
const activeView = ref('home')
const onlyRecommended = ref(false)
const defaultImage = '/asset/images/covers/product-goji.svg'

const categoryDescriptions = {
  新鲜果蔬: '当季蔬菜、水果，现摘现发',
  禽蛋肉奶: '土鸡、土鸡蛋、冷鲜肉、羊奶等',
  粮油杂粮: '生态大米、菜籽油、绿豆、小米等',
  特产农产品: '手工辣酱、红薯粉条、腌菜、山核桃等',
  其他商品: '散养产品或农户手作'
}

const query = reactive({
  pageNo: 1,
  pageSize: 50,
  categoryId: null,
  articleType: 'product'
})

const categoryMenu = computed(() => {
  return categories.value.map(c => ({
    ...c,
    desc: categoryDescriptions[c.name] || '本地农户好物'
  }))
})

const showProductArea = computed(() => {
  return activeView.value === 'home' || activeView.value === 'goods' || activeView.value === 'season'
})

const productSectionTitle = computed(() => {
  if (activeView.value === 'season') return '时令推荐'
  return '源头好物'
})

const productSectionDesc = computed(() => {
  if (activeView.value === 'season') return '按季节优先推荐的本地农产品'
  return '按品类快速查找本地农产品'
})

const displayProducts = computed(() => {
  if (!onlyRecommended.value) return articles.value
  return articles.value.filter(item => item.recommended === 1)
})

function goHome() {
  showHome()
  router.push('/')
}

async function loadCategories() {
  categories.value = await getSiteCategories()
}

async function loadArticles() {
  const page = await getSiteArticles(query)
  articles.value = page.records || []
}

async function loadStories() {
  const page = await getSiteArticles({pageNo: 1, pageSize: 20, articleType: 'story'})
  storyArticles.value = page.records || []
}

function showHome() {
  activeView.value = 'home'
  onlyRecommended.value = false
  query.categoryId = null
  loadArticles()
}

function showGoods() {
  activeView.value = 'goods'
  onlyRecommended.value = false
  query.categoryId = null
  loadArticles()
}

function selectCategory(categoryId) {
  activeView.value = 'goods'
  onlyRecommended.value = false
  query.categoryId = categoryId
  query.pageNo = 1
  loadArticles()
}

function showRecommended() {
  activeView.value = 'season'
  query.categoryId = null
  onlyRecommended.value = true
  loadArticles()
}

function showStory() {
  activeView.value = 'story'
  loadStories()
  window.scrollTo({top: 0, behavior: 'smooth'})
}

function showContact() {
  activeView.value = 'contact'
  window.scrollTo({top: 0, behavior: 'smooth'})
}

function formatPrice(price) {
  if (price === null || price === undefined || price === '') return '面议'
  return Number(price).toFixed(2)
}

function splitTags(tags) {
  if (!tags) return ['源头直发']
  return tags.split(',').map(item => item.trim()).filter(Boolean)
}

onMounted(async () => {
  await loadCategories()
  await loadArticles()
  await loadStories()
})
</script>

<style scoped>
.farm-site {
  min-height: 100vh;
  background: #f5f5f5;
  color: #1f2937;
}

.farm-header {
  background: #fff;
  border-bottom: 1px solid #eee;
  position: sticky;
  top: 0;
  z-index: 50;
}

.header-inner {
  max-width: 1280px;
  margin: 0 auto;
  height: 76px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo-area {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}

.logo-mark {
  width: 46px;
  height: 46px;
  border-radius: 12px;
  background: #16a34a;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
  font-weight: 700;
}

.site-title {
  font-size: 22px;
  font-weight: 700;
}

.site-subtitle {
  font-size: 12px;
  color: #6b7280;
  margin-top: 2px;
}

.main-nav {
  display: flex;
  align-items: center;
  gap: 34px;
  font-size: 16px;
  font-weight: 600;
}

.main-nav a, .main-nav span {
  color: #222;
  text-decoration: none;
  cursor: pointer;
}

.main-nav a:hover, .main-nav a.active {
  color: #16a34a;
}

.nav-item-with-dropdown {
  position: relative;
  height: 76px;
  display: flex;
  align-items: center;
}

.dropdown-menu {
  display: none;
  position: absolute;
  top: 68px;
  left: -30px;
  width: 300px;
  background: #fff;
  box-shadow: 0 14px 36px rgba(0, 0, 0, .16);
  border-radius: 10px;
  padding: 10px;
  z-index: 999;
}

.nav-item-with-dropdown:hover .dropdown-menu {
  display: block;
}

.dropdown-menu a {
  display: block;
  padding: 12px 14px;
  border-radius: 8px;
  font-size: 14px;
}

.dropdown-menu a:hover {
  background: #f0fdf4;
}

.dropdown-menu strong {
  display: block;
  color: #111827;
}

.dropdown-menu small {
  display: block;
  color: #6b7280;
  margin-top: 4px;
  font-weight: 400;
}

.admin-link {
  background: #16a34a;
  color: #fff;
  text-decoration: none;
  padding: 10px 18px;
  border-radius: 6px;
  font-weight: 600;
}

.hero-section {
  background:
    linear-gradient(
      90deg,
      rgba(5, 46, 22, 0.76) 0%,
      rgba(22, 101, 52, 0.58) 45%,
      rgba(21, 128, 61, 0.35) 100%
    ),
    url('/asset/images/hero-farm-banner.png') center/cover no-repeat;

  min-height: 360px;
  display: flex;
  align-items: center;
}
.hero-content {
  width: 1280px;
  margin: 0 auto;
  color: #fff;
}

.hero-badge {
  display: inline-block;
  padding: 8px 14px;
  background: rgba(255, 255, 255, .18);
  border-radius: 999px;
  margin-bottom: 18px;
}

.hero-content h1 {
  font-size: 42px;
  margin: 0 0 18px;
}

.hero-content p {
  max-width: 720px;
  line-height: 1.9;
  font-size: 18px;
}

.hero-actions {
  display: flex;
  gap: 14px;
  margin-top: 26px;
}

.hero-actions button {
  border: none;
  background: #f97316;
  color: #fff;
  padding: 12px 24px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 16px;
}

.hero-actions .secondary {
  background: rgba(255, 255, 255, .2);
}

.farm-container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 32px 0 60px;
}

.section-title {
  margin-bottom: 18px;
}

.section-title h2 {
  margin: 0;
  font-size: 28px;
}

.section-title p {
  color: #6b7280;
  margin-top: 8px;
}

.category-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 26px;
}

.category-tabs button {
  border: 1px solid #d1d5db;
  background: #fff;
  padding: 10px 18px;
  border-radius: 999px;
  cursor: pointer;
}

.category-tabs button.active, .category-tabs button:hover {
  border-color: #16a34a;
  background: #dcfce7;
  color: #15803d;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 22px;
}

.product-card {
  background: #fff;
  text-decoration: none;
  color: #1f2937;
  display: block;
  padding: 18px;
  border: 1px solid transparent;
  transition: all .2s;
}

.product-card:hover {
  border-color: #ef4444;
  transform: translateY(-3px);
}

.product-image-wrap {
  height: 220px;
  position: relative;
  overflow: hidden;
  background: #f3f4f6;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.trade-badge {
  position: absolute;
  right: 10px;
  top: 10px;
  background: #fb7185;
  color: #fff;
  padding: 5px 10px;
  border-radius: 999px;
  font-size: 14px;
}

.product-body {
  padding-top: 14px;
}

.price-row {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.price {
  color: #ff3b20;
  font-size: 28px;
}

.unit {
  color: #ff3b20;
  font-size: 14px;
}

.sales {
  margin-left: auto;
  color: #6b7280;
  font-size: 14px;
}

.product-title {
  font-size: 17px;
  line-height: 1.5;
  margin: 10px 0;
  height: 52px;
  overflow: hidden;
}

.product-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
  min-height: 28px;
}

.product-tags span {
  border: 1px solid #93c5fd;
  color: #2563eb;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 13px;
}

.product-footer {
  margin-top: 12px;
  display: flex;
  justify-content: space-between;
  color: #6b7280;
  font-size: 13px;
}

.only-section {
  padding-top: 10px;
}

.story-section, .contact-section {
  margin-top: 48px;
}

.story-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 22px;
}

.story-card {
  background: #fff;
  border-radius: 10px;
  overflow: hidden;
  text-decoration: none;
  color: #1f2937;
  box-shadow: 0 8px 24px rgba(0, 0, 0, .06);
}

.story-card img {
  width: 100%;
  height: 220px;
  object-fit: cover;
  display: block;
  background: #f3f4f6;
}

.story-info {
  padding: 22px;
}

.story-info h3 {
  margin: 0 0 12px;
  font-size: 22px;
}

.story-info p {
  color: #4b5563;
  line-height: 1.8;
  min-height: 56px;
}

.story-info span {
  color: #16a34a;
  font-weight: 700;
}

.contact-card {
  background: #fff;
  padding: 28px;
  border-radius: 8px;
  line-height: 1.9;
}

.contact-card div {
  margin-bottom: 10px;
}

@media (max-width: 1200px) {
  .header-inner, .hero-content, .farm-container {
    width: auto;
    margin-left: 20px;
    margin-right: 20px;
  }

  .product-grid {
    grid-template-columns: repeat(3, 1fr);
  }

  .story-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .main-nav {
    display: none;
  }

  .product-grid, .story-grid {
    grid-template-columns: repeat(1, 1fr);
  }

  .hero-content h1 {
    font-size: 30px;
  }
}
</style>
