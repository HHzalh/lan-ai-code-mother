<template>
  <div class="login-container">
    <!-- Hero 图片背景 -->
    <div class="hero-background">
      <div class="hero-overlay"></div>
    </div>

    <!-- 背景装饰元素 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>

    <div class="login-card cyan-card">
      <!-- Logo 和标题 -->
      <div class="card-header">
        <div class="logo-wrapper">
          <div class="logo-icon">
            <CodeOutlined />
          </div>
        </div>
        <h1 class="login-title">欢迎回来</h1>
        <p class="welcome-text">登录账号，继续您的 AI 创作之旅</p>
      </div>

      <!-- 登录表单 -->
      <a-form
        ref="formRef"
        :model="formState"
        autocomplete="off"
        class="login-form"
        layout="vertical"
        name="login"
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
          <a-input
            v-model:value="formState.userAccount"
            class="login-input"
            placeholder="请输入账号"
            size="large"
          >
            <template #prefix>
              <UserOutlined class="input-icon" />
            </template>
          </a-input>
        </a-form-item>

        <!-- 密码输入 -->
        <a-form-item
          :rules="[
            { required: true, message: '请输入密码' },
            { min: 8, message: '密码不能小于 8 位' },
          ]"
          name="userPassword"
        >
          <a-input-password
            v-model:value="formState.userPassword"
            class="login-input"
            placeholder="请输入密码"
            size="large"
          >
            <template #prefix>
              <LockOutlined class="input-icon" />
            </template>
          </a-input-password>
        </a-form-item>

        <!-- 登录按钮 -->
        <a-form-item>
          <a-button
            :loading="loading"
            block
            class="login-button"
            html-type="submit"
            size="large"
            type="primary"
          >
            <template #icon>
              <LoginOutlined />
            </template>
            立即登录
          </a-button>
        </a-form-item>
      </a-form>

      <!-- 注册链接 -->
      <div class="register-link">
        还没有账号？
        <RouterLink class="link-text" to="/user/register">立即注册</RouterLink>
        <span class="link-divider">|</span>
        <RouterLink class="link-text" to="/user/find-password">找回密码</RouterLink>
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
import { CodeOutlined, LockOutlined, LoginOutlined, UserOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)
const loginUserStore = useLoginUserStore()

const formState = reactive<API.UserLoginRequest>({
  userAccount: '',
  userPassword: '',
})

/**
 * 提交登录表单
 */
const handleSubmit = async (values: API.UserLoginRequest) => {
  loading.value = true
  try {
    const res = await userLogin(values)
    // 登录成功，把登录态保存到全局状态中
    if (res.data.code === 0 && res.data.data) {
      await loginUserStore.fetchLoginUser()
      message.success('🎉 登录成功！欢迎回来')
      setTimeout(() => {
        router.push({
          path: '/',
          replace: true,
        })
      }, 1000)
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

<style lang="less" scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  position: relative;
  overflow: hidden;
}

/* Hero 背景 */
.hero-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: url('https://images.unsplash.com/photo-1517604931442-7e0c8ed2963c?w=1920&q=80');
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
  z-index: 0;
}

.hero-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    135deg,
    rgba(249, 115, 22, 0.92) 0%,
    rgba(234, 88, 12, 0.88) 50%,
    rgba(251, 191, 36, 0.85) 100%
  );
  backdrop-filter: blur(2px);
}

/* 背景装饰 */
.bg-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  overflow: hidden;
  z-index: 1;
  pointer-events: none;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 20s infinite ease-in-out;
}

.circle-1 {
  width: 300px;
  height: 300px;
  top: -100px;
  left: -100px;
  animation-delay: 0s;
}

.circle-2 {
  width: 200px;
  height: 200px;
  top: 20%;
  right: 10%;
  animation-delay: 5s;
}

.circle-3 {
  width: 150px;
  height: 150px;
  bottom: 10%;
  left: 20%;
  animation-delay: 10s;
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0) scale(1);
  }
  50% {
    transform: translateY(-30px) scale(1.1);
  }
}

/* 登录卡片 */
.login-card {
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 48px 42px;
  width: 100%;
  max-width: 480px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  position: relative;
  z-index: 2;
  animation: slideUp 0.6s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 卡片头部 */
.card-header {
  text-align: center;
  margin-bottom: 36px;
}

.logo-wrapper {
  margin-bottom: 20px;
}

.logo-icon {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%);
  border-radius: 16px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 32px;
  box-shadow: 0 8px 24px rgba(249, 115, 22, 0.4);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%,
  100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

.login-title {
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-align: center;
  margin: 0 0 12px 0;
  letter-spacing: -0.5px;
}

.welcome-text {
  text-align: center;
  color: #6b7280;
  font-size: 13px;
  margin: 0;
  line-height: 1.5;
  font-weight: 400;
}

/* 表单样式 */
.login-form {
  margin-top: 0;
}

.login-form :deep(.ant-form-item) {
  margin-bottom: 20px;
}

.login-form :deep(.ant-form-item:last-child) {
  margin-bottom: 0;
}

.login-input {
  border-radius: 12px;
  transition: all 0.3s ease;
}

.login-input :deep(.ant-input),
.login-input :deep(.ant-input-password) {
  border-radius: 12px;
  height: 50px;
  font-size: 13px;
  padding-left: 44px;
  border: 2px solid #e5e7eb;
  background: #f9fafb;
  transition: all 0.3s ease;
}

.login-input :deep(.ant-input:hover),
.login-input :deep(.ant-input-password:hover) {
  border-color: #f97316;
  background: #ffffff;
}

.login-input :deep(.ant-input:focus),
.login-input :deep(.ant-input-focused),
.login-input :deep(.ant-input-password:focus) {
  border-color: #f97316;
  background: #ffffff;
  box-shadow: 0 0 0 4px rgba(249, 115, 22, 0.1);
}

.input-icon {
  color: #9ca3af;
  font-size: 16px;
  transition: color 0.3s ease;
}

.login-input:focus-within .input-icon {
  color: #f97316;
}

.login-input :deep(.ant-input-password-icon) {
  color: #9ca3af;
  font-size: 16px;
  transition: color 0.3s ease;
}

.login-input :deep(.ant-input-password-icon:hover) {
  color: #f97316;
}

/* 登录按钮 */
.login-button {
  height: 52px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%);
  border: none;
  margin-top: 8px;
  box-shadow: 0 4px 12px rgba(249, 115, 22, 0.3);
  transition: all 0.3s ease;
}

.login-button:hover {
  background: linear-gradient(135deg, #ea580c 0%, #3b82f6 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(249, 115, 22, 0.4);
}

.login-button:active {
  transform: translateY(0);
}

/* 注册链接 */
.register-link {
  text-align: center;
  color: #6b7280;
  font-size: 14px;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #e5e7eb;
}

.link-text {
  color: #f97316;
  text-decoration: none;
  font-weight: 600;
  margin: 0 4px;
  transition: all 0.3s ease;
}

.link-text:hover {
  color: #ea580c;
  text-decoration: underline;
}

.link-divider {
  margin: 0 8px;
  color: #d9d9d9;
}

/* 响应式设计 */
@media (max-width: 640px) {
  .login-card {
    padding: 36px 28px;
    border-radius: 20px;
  }

  .login-title {
    font-size: 24px;
  }

  .welcome-text {
    font-size: 14px;
  }

  .circle {
    display: none;
  }
}

/* ========== 超强全局字体优化 ========== */

/* 强制所有文字清晰可读 */
* {
  -webkit-font-smoothing: antialiased !important;
  -moz-osx-font-smoothing: grayscale !important;
}

/* Ant Design 按钮优化 */
:deep(.ant-btn-primary) {
  background: #3b82f6 !important;
  border-color: #3b82f6 !important;
  color: white !important;
  font-weight: 700 !important;
  font-size: 13px !important;
  letter-spacing: 0.3px;
  height: 32px !important;
  padding: 0 16px !important;
}

:deep(.ant-btn-primary:hover) {
  background: #2563eb !important;
  border-color: #2563eb !important;
}

:deep(.ant-btn-default) {
  color: #0f172a !important;
  border-color: #e2e8f0 !important;
  font-weight: 600 !important;
  font-size: 13px !important;
  background: white !important;
}

:deep(.ant-btn-default:hover) {
  color: #3b82f6 !important;
  border-color: #3b82f6 !important;
}

/* 表单标签优化 */
:deep(.ant-form-item-label > label) {
  color: #0f172a !important;
  font-weight: 700 !important;
  font-size: 14px !important;
}

/* 输入框文字优化 */
:deep(.ant-input) {
  color: #0f172a !important;
  font-weight: 600 !important;
  font-size: 14px !important;
}

:deep(.ant-input::placeholder) {
  color: #64748b !important;
  font-weight: 400 !important;
}

:deep(.ant-select-selection-item) {
  color: #0f172a !important;
  font-weight: 600 !important;
}

/* Textarea 文字 */
:deep(.ant-input-textarea) {
  color: #0f172a !important;
  font-weight: 600 !important;
}

/* 表格内容文字优化 */
:deep(.ant-table-tbody) {
  color: #0f172a !important;
}

:deep(.ant-table-thead > tr > th) {
  color: white !important;
  font-weight: 700 !important;
}

/* Modal 标题优化 */
:deep(.ant-modal-title) {
  color: #0f172a !important;
  font-weight: 700 !important;
  font-size: 18px !important;
}

:deep(.ant-modal-body) {
  color: #0f172a !important;
}

:deep(.ant-modal-content) {
  color: #0f172a !important;
}

/* Tag 标签文字优化 */
:deep(.ant-tag) {
  font-weight: 700 !important;
  color: #0f172a !important;
}

/* Card 标题 */
:deep(.ant-card-head-title) {
  color: #0f172a !important;
  font-weight: 700 !important;
  font-size: 18px !important;
}

/* Card 内容 */
:deep(.ant-card-body) {
  color: #0f172a !important;
}

/* 所有文本元素 */
:deep(.ant-typography),
:deep(.ant-text),
:deep(label),
:deep(span),
:deep(p),
:deep(div) {
  color: #0f172a !important;
}

/* 链接文字 */
:deep(a) {
  color: #3b82f6 !important;
  font-weight: 600 !important;
}

:deep(a:hover) {
  color: #2563eb !important;
}

/* 下拉菜单 */
:deep(.ant-dropdown-menu-item) {
  color: #0f172a !important;
  font-weight: 600 !important;
}

/* 分页 */
:deep(.ant-pagination-item) {
  color: #0f172a !important;
  font-weight: 600 !important;
}

/* 描述列表 */
:deep(.ant-descriptions-item-label) {
  color: #0f172a !important;
  font-weight: 700 !important;
}

:deep(.ant-descriptions-item-content) {
  color: #0f172a !important;
  font-weight: 600 !important;
}

/* ========== 超强力表单元素优化 ========== */

/* 表单标签 - 纯黑色 + 超粗体 */
.login-container :deep(.ant-form-item-label > label) {
  color: #000000 !important;
  font-size: 15px !important;
  font-weight: 700 !important;
  letter-spacing: 0.5px;
}

/* 输入框 - 纯黑色文字 + 白色背景 */
.login-container :deep(.ant-input),
.login-container :deep(.ant-input-password) {
  background: #ffffff !important;
  border-color: #cbd5e1 !important;
  color: #000000 !important;
  font-weight: 700 !important;
  font-size: 14px !important;
}

.login-container :deep(.ant-input::placeholder),
.login-container :deep(.ant-input-password::placeholder) {
  color: #475569 !important;
  font-weight: 500 !important;
}

.login-container :deep(.ant-input:hover),
.login-container :deep(.ant-input-password:hover) {
  border-color: #3b82f6 !important;
}

.login-container :deep(.ant-input:focus),
.login-container :deep(.ant-input-password:focus),
.login-container :deep(.ant-input-focused) {
  border-color: #3b82f6 !important;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1) !important;
  background: #ffffff !important;
  color: #000000 !important;
}

/* Modal 增强 */
.login-container :deep(.ant-modal-title) {
  color: #000000 !important;
  font-weight: 700 !important;
}

.login-container :deep(.ant-modal-body) {
  color: #000000 !important;
}

/* Tag 标签增强 */
.login-container :deep(.ant-tag) {
  font-weight: 700 !important;
  color: #000000 !important;
}
</style>
