import { defineStore } from 'pinia'

// Pinia 用户状态：保存后台登录信息
export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('blog_token') || '',
    nickname: localStorage.getItem('blog_nickname') || '',
    role: localStorage.getItem('blog_role') || ''
  }),
  actions: {
    setLoginInfo(token, nickname, role = '') {
      this.token = token
      this.nickname = nickname
      this.role = role
      localStorage.setItem('blog_token', token)
      localStorage.setItem('blog_nickname', nickname)
      localStorage.setItem('blog_role', role)
    },
    logout() {
      this.token = ''
      this.nickname = ''
      this.role = ''
      localStorage.removeItem('blog_token')
      localStorage.removeItem('blog_nickname')
      localStorage.removeItem('blog_role')
    }
  }
})
