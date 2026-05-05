const api = require('../../utils/api')
const fmt = require('../../utils/format')

Page({
  data: { stories: [], pageNo: 1, pageSize: 8, total: 0, loading: false, hasMore: true },
  onLoad() { this.reload() },
  onPullDownRefresh() { this.reload().finally(() => wx.stopPullDownRefresh()) },
  onReachBottom() { if (this.data.hasMore && !this.data.loading) this.loadStories(false) },
  async reload() { this.setData({ pageNo: 1, stories: [], hasMore: true }); await this.loadStories(true) },
  async loadStories(reset) {
    this.setData({ loading: true })
    try {
      const res = await api.articleList({ pageNo: this.data.pageNo, pageSize: this.data.pageSize, articleType: 'story' })
      const list = fmt.normalizeArticles(res.records || [], api)
      const stories = reset ? list : this.data.stories.concat(list)
      this.setData({
        stories, total: res.total || 0, pageNo: this.data.pageNo + 1,
        hasMore: stories.length < (res.total || 0), loading: false
      })
    } catch (e) { console.error(e); this.setData({ loading: false }) }
  },
  goHome() { wx.redirectTo({ url: '/pages/home/home' }) },
  goProducts() { wx.redirectTo({ url: '/pages/products/products' }) },
  goContact() { wx.redirectTo({ url: '/pages/contact/contact' }) },
  goDetail(e) { wx.navigateTo({ url: '/pages/detail/detail?id=' + e.currentTarget.dataset.id }) }
})
