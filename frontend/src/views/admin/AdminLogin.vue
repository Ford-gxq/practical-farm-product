<template>
  <div class="login-page">
    <!-- 粒子背景组件 -->
    <ParticleBackground />

    <!-- 登录卡片 -->
    <div class="login-card">
      <h2>后台登录</h2>

      <el-form :model="form" label-width="70px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" />
        </el-form-item>

        <el-form-item label="密码">
          <el-input
            v-model="form.password"
            type="password"
            show-password
          />
        </el-form-item>

        <el-button type="primary" class="login-btn" @click="handleLogin">
          登录
        </el-button>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import ParticleBackground from '../../components/ParticleBackground.vue'
import { login } from '../../api/admin'
import { useUserStore } from '../../store/user'


const router = useRouter()
const userStore = useUserStore()

const form = reactive({
  username: 'admin',
  password: ''
})

async function handleLogin() {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }

  try {
    const res = await login({
      username: form.username,
      password: form.password
    })

    userStore.setLoginInfo(res.token, res.nickname || form.username, res.role || "")

    ElMessage.success('登录成功')

    router.push('/admin/dashboard')
  } catch (error) {
    console.error('登录失败：', error)
    ElMessage.error('登录失败，请检查用户名或密码')
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: radial-gradient(circle at top, #1f3b5c 0%, #101827 45%, #0f172a 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.login-card {
  width: 420px;
  padding: 42px 36px;
  background: rgba(255, 255, 255, 0.96);
  border-radius: 8px;
  box-shadow: 0 18px 60px rgba(0, 0, 0, 0.35);
  position: relative;
  z-index: 2;
}

.login-card h2 {
  text-align: center;
  margin-bottom: 28px;
  font-size: 28px;
  color: #1f2937;
}

.login-btn {
  width: 100%;
}
</style>