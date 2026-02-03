<template>
  <div class="auth-container">
    <!-- 渐变背景 -->
    <div class="gradient-bg">
      <div class="gradient-orb orb-1"></div>
      <div class="gradient-orb orb-2"></div>
      <div class="gradient-orb orb-3"></div>
    </div>

    <!-- 内容容器 -->
    <div class="content-wrapper">
      <!-- 左侧介绍区域 -->
      <div class="intro-section">
        <div class="brand-mark">
          <div class="mark-icon">
            <svg fill="none" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
              <path
                d="M12 2L2 7L12 12L22 7L12 2Z"
                stroke="currentColor"
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
              />
              <path
                d="M2 17L12 22L22 17"
                stroke="currentColor"
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
              />
              <path
                d="M2 12L12 17L22 12"
                stroke="currentColor"
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
              />
            </svg>
          </div>
          <h1 class="brand-title">蓝海智造</h1>
        </div>

        <div class="feature-list">
          <div
            v-for="(feature, index) in features"
            :key="index"
            :style="{ '--delay': `${index * 0.1}s` }"
            class="feature-item"
          >
            <div class="feature-dot"></div>
            <span class="feature-text">{{ feature }}</span>
          </div>
        </div>

        <div class="intro-footer">
          <p class="intro-text">让 AI 为你创造无限可能</p>
          <p class="intro-text">让想法，瞬间成为可用的应用</p>
        </div>
      </div>

      <!-- 右侧登录表单 -->
      <div class="form-section">
        <div class="glass-card">
          <div class="card-header">
            <h2 class="card-title">欢迎回来</h2>
            <p class="card-subtitle">登录以继续你的创作之旅</p>
          </div>

          <a-form
            ref="formRef"
            :model="formState"
            autocomplete="off"
            class="login-form"
            @finish="handleSubmit"
          >
            <!-- 账号输入 -->
            <a-form-item
              :rules="[
                { required: true, message: '请输入账号' },
                { min: 4, message: '账号长度不能少于 4 位' },
              ]"
              name="userAccount"
            >
              <label class="form-label">账号</label>
              <a-input
                v-model:value="formState.userAccount"
                class="glass-input"
                placeholder="请输入你的账号"
                size="large"
              />
            </a-form-item>

            <!-- 密码输入 -->
            <a-form-item
              :rules="[
                { required: true, message: '请输入密码' },
                { min: 8, message: '密码不能小于 8 位' },
              ]"
              name="userPassword"
            >
              <label class="form-label">密码</label>
              <a-input-password
                v-model:value="formState.userPassword"
                class="glass-input"
                placeholder="请输入你的密码"
                size="large"
              />
            </a-form-item>

            <!-- 登录按钮 -->
            <a-button
              :loading="loading"
              block
              class="submit-button"
              html-type="submit"
              size="large"
              type="primary"
            >
              {{ loading ? '登录中...' : '登录' }}
            </a-button>
          </a-form>

          <!-- 底部链接 -->
          <div class="form-footer">
            <span class="footer-text">还没有账号？</span>
            <RouterLink class="footer-link" to="/user/register">立即注册</RouterLink>
            <span class="footer-divider">|</span>
            <RouterLink class="footer-link" to="/user/find-password">忘记密码</RouterLink>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { useRouter } from 'vue-router'
import type { FormInstance } from 'ant-design-vue'
import { message } from 'ant-design-vue'
import { reactive, ref } from 'vue'
import { userLogin } from '@/api/userController.ts'
import { useLoginUserStore } from '@/stores/loginUser.ts'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)
const loginUserStore = useLoginUserStore()

const formState = reactive<API.UserLoginRequest>({
  userAccount: '',
  userPassword: '',
})

const features = ['智能代码生成', '一键部署上线', '积分奖励系统']

const handleSubmit = async (values: API.UserLoginRequest) => {
  loading.value = true
  try {
    const res = await userLogin(values)
    if (res.data.code === 0 && res.data.data) {
      await loginUserStore.fetchLoginUser()
      message.success('登录成功！欢迎回来')
      setTimeout(() => {
        router.push({
          path: '/',
          replace: true,
        })
      }, 800)
    } else {
      message.error(res.data.message || '登录失败，请重试')
    }
  } catch (error: any) {
    console.error('登录失败:', error)
    const errorMsg = error?.response?.data?.message || error?.message || '登录失败，请检查网络连接'
    message.error(errorMsg)
  } finally {
    loading.value = false
  }
}
</script>

<style>
/* ========== 全局变量（非scoped） ========== */
:root {
  --color-primary: #ff6b6b;
  --color-primary-light: #ff8787;
  --color-primary-dark: #fa5252;
  --color-secondary: #ffa8a8;
  --color-accent: #ffec99;
  --color-text: #2d3436;
  --color-text-secondary: #636e72;
  --color-text-light: #b2bec3;
  --color-bg: #fff5f5;
  --color-glass: rgba(255, 255, 255, 0.7);
  --color-glass-border: rgba(255, 255, 255, 0.9);
  --shadow-soft: 0 8px 32px rgba(255, 107, 107, 0.1);
  --shadow-hover: 0 12px 48px rgba(255, 107, 107, 0.15);
  --radius-sm: 8px;
  --radius-md: 16px;
  --radius-lg: 24px;
  --font-main: 'Noto Sans SC', -apple-system, BlinkMacSystemFont, sans-serif;
}
</style>

<style scoped>
/* ========== 字体引入 ========== */
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@300;400;500;600;700&display=swap');

/* ========== 主容器 ========== */
.auth-container {
  min-height: 100vh;
  position: relative;
  overflow: hidden;
  font-family: var(--font-main);
  background: var(--color-bg);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}

/* ========== 渐变背景 ========== */
.gradient-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  overflow: hidden;
  will-change: transform;
  transform: translateZ(0);
  backface-visibility: hidden;
  perspective: 1000px;
}

.gradient-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.6;
  animation: float 20s ease-in-out infinite;
  will-change: transform;
  transform: translateZ(0);
  backface-visibility: hidden;
}

.orb-1 {
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, var(--color-primary) 0%, transparent 70%);
  top: -200px;
  right: -100px;
  animation-delay: 0s;
}

.orb-2 {
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, var(--color-secondary) 0%, transparent 70%);
  bottom: -150px;
  left: -100px;
  animation-delay: 7s;
}

.orb-3 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, var(--color-accent) 0%, transparent 70%);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation-delay: 14s;
}

@keyframes float {
  0%,
  100% {
    transform: translate3d(0, 0, 0) scale(1);
  }
  33% {
    transform: translate3d(30px, -30px, 0) scale(1.1);
  }
  66% {
    transform: translate3d(-20px, 20px, 0) scale(0.9);
  }
}

/* ========== 内容容器 ========== */
.content-wrapper {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 80px;
  max-width: 1200px;
  width: 100%;
  z-index: 1;
  align-items: center;
  transform: translateZ(0);
  backface-visibility: hidden;
  perspective: 1000px;
}

/* ========== 左侧介绍区域 ========== */
.intro-section {
  padding: 40px;
}

.brand-mark {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 60px;
  opacity: 0;
  animation: fadeInUp 0.8s ease forwards;
}

.mark-icon {
  width: 56px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--color-primary), var(--color-secondary));
  border-radius: var(--radius-md);
  color: white;
  box-shadow: var(--shadow-soft);
}

.mark-icon svg {
  width: 28px;
  height: 28px;
}

.brand-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--color-text);
  margin: 0;
  letter-spacing: 0.5px;
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
  margin-bottom: 60px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 16px;
  opacity: 0;
  animation: fadeInUp 0.8s ease forwards;
}

.feature-dot {
  width: 8px;
  height: 8px;
  background: var(--color-primary);
  border-radius: 50%;
  box-shadow: 0 0 12px var(--color-primary);
}

.feature-text {
  font-size: 16px;
  color: var(--color-text-secondary);
  font-weight: 400;
}

.intro-footer {
  opacity: 0;
  animation: fadeInUp 0.8s ease 0.4s forwards;
}

.intro-text {
  font-size: 18px;
  color: var(--color-text);
  font-weight: 500;
  margin: 0;
  line-height: 1.6;
}

/* ========== 右侧表单区域 ========== */
.form-section {
  display: flex;
  justify-content: center;
}

.glass-card {
  background: var(--color-glass);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid var(--color-glass-border);
  border-radius: var(--radius-lg);
  padding: 48px;
  width: 100%;
  max-width: 440px;
  box-shadow: var(--shadow-soft);
  animation: fadeInUp 1s ease 0.2s both;
  transform: translateZ(0);
  backface-visibility: hidden;
  will-change: transform, opacity;
}

.card-header {
  text-align: center;
  margin-bottom: 40px;
}

.card-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--color-text);
  margin: 0 0 12px 0;
}

.card-subtitle {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin: 0;
}

/* ========== 表单样式 ========== */
.login-form {
  margin-bottom: 24px;
}

.login-form :deep(.ant-form-item) {
  margin-bottom: 24px;
}

.login-form :deep(.ant-form-item:last-child) {
  margin-bottom: 0;
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text);
  margin-bottom: 8px;
}

.glass-input :deep(.ant-input),
.glass-input :deep(.ant-input-password) {
  background: rgba(255, 255, 255, 0.5);
  border: 1px solid rgba(255, 107, 107, 0.2);
  border-radius: var(--radius-sm);
  font-size: 15px;
  height: 48px;
  color: var(--color-text);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.glass-input :deep(.ant-input::placeholder),
.glass-input :deep(.ant-input-password::placeholder) {
  color: var(--color-text-light);
}

.glass-input :deep(.ant-input:hover),
.glass-input :deep(.ant-input-password:hover) {
  border-color: var(--color-primary-light);
  background: rgba(255, 255, 255, 0.7);
}

.glass-input :deep(.ant-input:focus),
.glass-input :deep(.ant-input-focused),
.glass-input :deep(.ant-input-password:focus) {
  border-color: var(--color-primary);
  background: rgba(255, 255, 255, 0.8);
  box-shadow: 0 0 0 3px rgba(255, 107, 107, 0.1);
  outline: none;
}

.glass-input :deep(.ant-input-password-icon) {
  color: var(--color-text-light);
  transition: color 0.3s ease;
}

.glass-input :deep(.ant-input-password-icon:hover) {
  color: var(--color-primary);
}

/* ========== 提交按钮 ========== */
.submit-button {
  height: 50px;
  border: none;
  background: linear-gradient(135deg, var(--color-primary), var(--color-primary-dark));
  color: white;
  font-size: 16px;
  font-weight: 600;
  border-radius: var(--radius-sm);
  box-shadow: var(--shadow-soft);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  margin-top: 8px;
}

.submit-button:hover {
  background: linear-gradient(135deg, var(--color-primary-light), var(--color-primary));
  box-shadow: var(--shadow-hover);
  transform: translate3d(0, -2px, 0);
}

.submit-button:active {
  transform: translateY(0);
}

/* ========== 表单底部 ========== */
.form-footer {
  text-align: center;
  padding-top: 24px;
  border-top: 1px solid rgba(255, 107, 107, 0.1);
}

.footer-text {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin-right: 4px;
}

.footer-link {
  font-size: 14px;
  color: var(--color-primary);
  text-decoration: none;
  font-weight: 500;
  margin: 0 4px;
  transition: all 0.3s ease;
}

.footer-link:hover {
  color: var(--color-primary-dark);
  text-decoration: underline;
}

.footer-divider {
  color: rgba(255, 107, 107, 0.3);
  margin: 0 4px;
}

/* ========== 动画 ========== */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translate3d(0, 30px, 0);
  }
  to {
    opacity: 1;
    transform: translate3d(0, 0, 0);
  }
}

/* ========== 响应式设计 ========== */
@media (max-width: 1024px) {
  .content-wrapper {
    grid-template-columns: 1fr;
    gap: 40px;
  }

  .intro-section {
    order: 2;
    text-align: center;
    padding: 20px;
  }

  .brand-mark {
    justify-content: center;
    margin-bottom: 30px;
  }

  .feature-list {
    align-items: center;
  }

  .gradient-orb {
    filter: blur(60px);
  }

  .orb-1,
  .orb-2,
  .orb-3 {
    width: 300px;
    height: 300px;
  }
}

@media (max-width: 640px) {
  .auth-container {
    padding: 20px 16px;
  }

  .glass-card {
    padding: 32px 24px;
  }

  .card-title {
    font-size: 24px;
  }

  .brand-title {
    font-size: 22px;
  }

  .feature-text {
    font-size: 14px;
  }

  .form-footer {
    flex-direction: column;
    gap: 12px;
  }

  .footer-divider {
    display: none;
  }
}

/* ========== Ant Design 覆盖样式 ========== */
:deep(.ant-form-item-explain-error) {
  color: var(--color-primary-dark) !important;
  font-size: 13px;
  margin-top: 6px;
}

:deep(.ant-btn-primary) {
  background: var(--color-primary) !important;
  border-color: var(--color-primary) !important;
  color: white !important;
}

:deep(.ant-btn-loading) {
  opacity: 0.8;
}
</style>
