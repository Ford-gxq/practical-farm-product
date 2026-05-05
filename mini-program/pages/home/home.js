const api = require('../../utils/api')
const fmt = require('../../utils/format')

Page({
  data: {
    categories: [],
    recommended: [],
    stories: [],
    activeCategoryId: null
  },
  onLoad() {
    this.loadData()
  },
  onPullDownRefresh() {
    this.loadData().finally(() => wx.stopPullDownRefresh())
  },
  async loadData() {
    try {
      const [categories, recommendedPage, storyPage] = await Promise.all([
        api.categories(),
        api.articleList({ pageNo: 1, pageSize: 6, articleType: 'product', recommended: 1 }),
        api.articleList({ pageNo: 1, pageSize: 3, articleType: 'story' })
      ])
      let recommended = recommendedPage.records || []
      // 后端当前没有 dedicated recommended 查询参数时，接口会忽略该参数；这里前端兜底筛选并保留美观展示。
      recommended = recommended.filter(item => item.recommended === 1)
      if (recommended.length === 0) recommended = (recommendedPage.records || []).slice(0, 6)
      this.setData({
        categories: categories || [],
        recommended: fmt.normalizeArticles(recommended, api),
        stories: fmt.normalizeArticles((storyPage.records || []).slice(0, 3), api)
      })
    } catch (e) {
      console.error(e)
    }
  },
  chooseCategory(e) {
    const id = e.currentTarget.dataset.id
    wx.setStorageSync('pendingProductQuery', { categoryId: id, recommended: false })
    wx.redirectTo({ url: '/pages/products/products' })
  },
  goProducts() { wx.redirectTo({ url: '/pages/products/products' }) },
  goRecommended() {
    wx.setStorageSync('pendingProductQuery', { categoryId: null, recommended: true })
    wx.redirectTo({ url: '/pages/products/products' })
  },
  goStories() { wx.redirectTo({ url: '/pages/stories/stories' }) },
  goContact() { wx.redirectTo({ url: '/pages/contact/contact' }) },
  goDetail(e) { wx.navigateTo({ url: '/pages/detail/detail?id=' + e.currentTarget.dataset.id }) }
})
