const api = require('../../utils/api')
const fmt = require('../../utils/format')

Page({
  data: {
    categories: [], products: [], pageNo: 1, pageSize: 10, total: 0,
    keyword: '', categoryId: null, recommended: false, loading: false, hasMore: true,
    pageTitle: '全部农产品'
  },
  onLoad(options) {
    const data = {}
    if (options.categoryId) data.categoryId = Number(options.categoryId)
    if (options.recommended) data.recommended = true
    this.setData(data)
    this.init()
  },
  onShow() {
    const pending = wx.getStorageSync('pendingProductQuery')
    if (pending) {
      wx.removeStorageSync('pendingProductQuery')
      this.setData({
        categoryId: pending.categoryId ? Number(pending.categoryId) : null,
        recommended: !!pending.recommended
      })
      this.updateTitle()
      this.reload()
    }
  },
  onPullDownRefresh() {
    this.reload().finally(() => wx.stopPullDownRefresh())
  },
  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) this.loadProducts(false)
  },
  async init() {
    try {
      const categories = await api.categories()
      this.setData({ categories: categories || [] })
      this.updateTitle()
      await this.reload()
    } catch (e) { console.error(e) }
  },
  async reload() {
    this.setData({ pageNo: 1, products: [], hasMore: true })
    await this.loadProducts(true)
  },
  async loadProducts(reset) {
    this.setData({ loading: true })
    try {
      const params = { pageNo: this.data.pageNo, pageSize: this.data.pageSize, articleType: 'product' }
      if (this.data.keyword) params.keyword = this.data.keyword
      if (this.data.categoryId) params.categoryId = this.data.categoryId
      const res = await api.articleList(params)
      let records = res.records || []
      if (this.data.recommended) records = records.filter(item => item.recommended === 1)
      const list = fmt.normalizeArticles(records, api)
      const products = reset ? list : this.data.products.concat(list)
      const loaded = products.length
      const total = this.data.recommended ? products.length : (res.total || 0)
      this.setData({
        products, total,
        pageNo: this.data.pageNo + 1,
        hasMore: !this.data.recommended && loaded < total,
        loading: false
      })
    } catch (e) {
      console.error(e)
      this.setData({ loading: false })
    }
  },
  updateTitle() {
    let title = '全部农产品'
    if (this.data.recommended) title = '时令推荐'
    if (this.data.categoryId) {
      const c = this.data.categories.find(item => item.id === this.data.categoryId)
      if (c) title = c.name
    }
    this.setData({ pageTitle: title })
  },
  onKeywordInput(e) { this.setData({ keyword: e.detail.value }) },
  search() { this.reload() },
  clearKeyword() { this.setData({ keyword: '' }); this.reload() },
  chooseAll() { this.setData({ categoryId: null, recommended: false }); this.updateTitle(); this.reload() },
  chooseRecommended() { this.setData({ categoryId: null, recommended: true }); this.updateTitle(); this.reload() },
  chooseCategory(e) { this.setData({ categoryId: e.currentTarget.dataset.id, recommended: false }); this.updateTitle(); this.reload() },
  goHome() { wx.redirectTo({ url: '/pages/home/home' }) },
  goStories() { wx.redirectTo({ url: '/pages/stories/stories' }) },
  goContact() { wx.redirectTo({ url: '/pages/contact/contact' }) },
  goDetail(e) { wx.navigateTo({ url: '/pages/detail/detail?id=' + e.currentTarget.dataset.id }) }
})
