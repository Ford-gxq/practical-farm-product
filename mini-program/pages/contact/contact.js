const api = require('../../utils/api')

Page({
  data: {
    submitting: false,
    form: {
      name: '',
      phone: '',
      content: ''
    }
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field
    this.setData({ ['form.' + field]: e.detail.value })
  },

  submit() {
    const f = this.data.form
    if (!f.name.trim() || !f.phone.trim() || !f.content.trim()) {
      wx.showToast({ title: '请填写完整信息', icon: 'none' })
      return
    }
    if (!/^1\d{10}$/.test(f.phone.trim())) {
      wx.showToast({ title: '请输入11位手机号', icon: 'none' })
      return
    }

    this.setData({ submitting: true })
    api.submitMessage({
      name: f.name.trim(),
      phone: f.phone.trim(),
      content: f.content.trim(),
      source: 'wechat_mini_program'
    }).then(() => {
      wx.showToast({ title: '留言已提交', icon: 'success' })
      this.setData({ form: { name: '', phone: '', content: '' } })
    }).catch(() => {
      wx.showToast({ title: '提交失败，请检查后端', icon: 'none' })
    }).finally(() => {
      this.setData({ submitting: false })
    })
  },

  goHome() { wx.redirectTo({ url: '/pages/home/home' }) },
  goProducts() { wx.redirectTo({ url: '/pages/products/products' }) },
  goStories() { wx.redirectTo({ url: '/pages/stories/stories' }) }
})
