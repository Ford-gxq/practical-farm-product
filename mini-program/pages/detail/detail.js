const api = require('../../utils/api')
const fmt = require('../../utils/format')

Page({
  data: {
    id: null,
    article: null,
    contentLines: []
  },

  onLoad(options) {
    this.setData({
      id: options.id
    })
    this.loadDetail()
  },

  async loadDetail() {
    try {
      const detail = await api.articleDetail(this.data.id)
      const article = fmt.normalizeArticle(detail, api)

      this.setData({
        article,
        contentLines: fmt.contentToLines(article.content)
      })

      wx.setNavigationBarTitle({
        title: article.articleType === 'story' ? '乡村故事' : '商品详情'
      })
    } catch (e) {
      console.error(e)
      wx.showToast({
        title: '详情加载失败',
        icon: 'none'
      })
    }
  },

  /**
   * 复制联系电话
   */
  copyPhone() {
    const article = this.data.article || {}

    // 这里优先使用 farmerPhone
    // 如果后端字段名不同，也可以兼容 phone / contactPhone / mobile / phoneText
    const phone =
      article.farmerPhone ||
      article.phone ||
      article.contactPhone ||
      article.mobile ||
      article.phoneText

    if (!phone || phone === '暂无电话') {
      wx.showToast({
        title: '暂无电话',
        icon: 'none'
      })
      return
    }

    wx.setClipboardData({
      data: String(phone),
      success: () => {
        wx.showToast({
          title: '电话已复制',
          icon: 'success'
        })
      },
      fail: () => {
        wx.showToast({
          title: '复制失败',
          icon: 'none'
        })
      }
    })
  },

  /**
   * 拨打联系电话
   */
  callPhone() {
    const article = this.data.article || {}

    // 注意：你当前商品详情页显示的是 article.phoneText
    // 真正后端字段通常是 farmerPhone
    const phone =
      article.farmerPhone ||
      article.phone ||
      article.contactPhone ||
      article.mobile ||
      article.phoneText

    if (!phone || phone === '暂无电话') {
      wx.showToast({
        title: '暂无联系电话',
        icon: 'none'
      })
      return
    }

    wx.makePhoneCall({
      phoneNumber: String(phone),
      fail: () => {
        wx.showToast({
          title: '取消或拨号失败',
          icon: 'none'
        })
      }
    })
  }
})