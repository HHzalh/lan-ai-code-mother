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
      <!-- 左侧步骤指示 -->
      <div class="intro-section">
        <div class="brand-mark">
          <div class="mark-icon">
            <svg fill="none" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
              <path
                d="M12 22C17.5 22 22 17.5 22 12C22 6.5 17.5 2 12 2C6.5 2 2 6.5 2 12C2 17.5 6.5 22 12 22Z"
                stroke="currentColor"
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
              />
              <path
                d="M12 16V12"
                stroke="currentColor"
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
              />
              <path
                d="M12 8H12.01"
                stroke="currentColor"
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
              />
            </svg>
          </div>
          <h1 class="brand-title">密码找回</h1>
        </div>

        <div class="step-indicator">
          <div :class="{ active: step === 1, completed: step > 1 }" class="step-item">
            <div class="step-number">1</div>
            <div class="step-content">
              <h3 class="step-title">验证身份</h3>
              <p class="step-desc">确认账号和邮箱</p>
            </div>
          </div>

          <div :class="{ active: step > 1 }" class="step-line"></div>

          <div :class="{ active: step === 2, completed: step > 2 }" class="step-item">
            <div class="step-number">2</div>
            <div class="step-content">
              <h3 class="step-title">重置密码</h3>
              <p class="step-desc">设置新密码</p>
            </div>
          </div>

          <div :class="{ active: step > 2 }" class="step-line"></div>

          <div :class="{ completed: step > 2 }" class="step-item">
            <div class="step-number">3</div>
            <div class="step-content">
              <h3 class="step-title">完成</h3>
              <p class="step-desc">使用新密码登录</p>
            </div>
          </div>
        </div>

        <div class="intro-footer">
          <p class="intro-text">我们会通过邮箱帮你安全找回密码</p>
        </div>
      </div>

      <!-- 右侧表单区域 -->
      <div class="form-section">
        <div class="glass-card">
          <!-- 步骤 1: 验证身份 -->
          <div v-if="step === 1" class="step-content">
            <div class="card-header">
              <h2 class="card-title">忘记密码</h2>
              <p class="card-subtitle">别担心，我们来帮你找回</p>
            </div>

            <a-form
              ref="step1FormRef"
              :model="findPasswordForm"
              autocomplete="off"
              class="reset-form"
              @finish="handleSendCode"
            >
              <a-form-item
                :rules="[
                  { required: true, message: '请输入账号' },
                  { min: 4, message: '账号长度不能少于 4 位' },
                ]"
                name="userAccount"
              >
                <label class="form-label">账号</label>
                <a-input
                  v-model:value="findPasswordForm.userAccount"
                  class="glass-input"
                  placeholder="请输入你的账号"
                  size="large"
                />
              </a-form-item>

              <a-form-item
                :rules="[
                  { required: true, message: '请输入邮箱地址' },
                  { type: 'email', message: '请输入正确的邮箱格式' },
                ]"
                name="email"
              >
                <label class="form-label">邮箱地址</label>
                <a-input
                  v-model:value="findPasswordForm.email"
                  class="glass-input"
                  placeholder="请输入注册时使用的邮箱"
                  size="large"
                />
              </a-form-item>

              <a-button
                :loading="sendCodeLoading"
                block
                class="submit-button"
                html-type="submit"
                size="large"
                type="primary"
              >
                {{ sendCodeLoading ? '发送中...' : '发送验证码' }}
              </a-button>
            </a-form>

            <div class="form-footer">
              <span class="footer-text">想起密码了？</span>
              <RouterLink class="footer-link" to="/user/login">返回登录</RouterLink>
            </div>
          </div>

          <!-- 步骤 2: 重置密码 -->
          <div v-if="step === 2" class="step-content">
            <div class="success-alert">
              <div class="alert-icon">✓</div>
              <div class="alert-content">
                <p class="alert-title">验证码已发送</p>
                <p class="alert-desc">请查收你的邮箱，验证码有效期为 5 分钟</p>
              </div>
            </div>

            <div class="card-header" style="margin-top: 32px">
              <h2 class="card-title">重置密码</h2>
              <p class="card-subtitle">请输入验证码和新密码</p>
            </div>

            <a-form
              ref="step2FormRef"
              :model="resetPasswordForm"
              autocomplete="off"
              class="reset-form"
              @finish="handleResetPassword"
            >
              <a-form-item
                :rules="[
                  { required: true, message: '请输入验证码' },
                  { min: 4, message: '验证码不能少于 4 位' },
                ]"
                name="code"
              >
                <label class="form-label">验证码</label>
                <a-input
                  v-model:value="resetPasswordForm.code"
                  class="glass-input"
                  placeholder="请输入邮箱收到的验证码"
                  size="large"
                />
              </a-form-item>

              <a-form-item
                :rules="[
                  { required: true, message: '请输入新密码' },
                  { min: 8, message: '密码不能小于 8 位' },
                  { max: 20, message: '密码不能超过 20 位' },
                ]"
                name="newPassword"
              >
                <label class="form-label">新密码</label>
                <a-input-password
                  v-model:value="resetPasswordForm.newPassword"
                  class="glass-input"
                  placeholder="请输入新密码（至少 8 位）"
                  size="large"
                />
              </a-form-item>

              <a-form-item
                :rules="[
                  { required: true, message: '请确认新密码' },
                  { validator: validateCheckPassword },
                ]"
                name="checkPassword"
              >
                <label class="form-label">确认新密码</label>
                <a-input-password
                  v-model:value="resetPasswordForm.checkPassword"
                  class="glass-input"
                  placeholder="请再次输入新密码"
                  size="large"
                />
              </a-form-item>

              <a-button
                :loading="resetLoading"
                block
                class="submit-button primary"
                html-type="submit"
                size="large"
                type="primary"
              >
                {{ resetLoading ? '重置中...' : '重置密码' }}
              </a-button>

              <a-button block class="submit-button secondary" size="large" @click="handleBack">
                返回上一步
              </a-button>
            </a-form>

            <div class="form-footer">
              <span class="footer-text">想起密码了？</span>
              <RouterLink class="footer-link" to="/user/login">返回登录</RouterLink>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { useRouter } from 'vue-router'
import { findPassword, resetPassword } from '@/api/userController'
import type { FormInstance } from 'ant-design-vue'
import { message } from 'ant-design-vue'
import { reactive, ref } from 'vue'

const router = useRouter()
const step1FormRef = ref<FormInstance>()
const step2FormRef = ref<FormInstance>()
const step = ref(1) // 1: 发送验证码, 2: 重置密码
const sendCodeLoading = ref(false)
const resetLoading = ref(false)

const findPasswordForm = reactive<API.FindPasswordRequest>({
  userAccount: '',
  email: '',
})

const resetPasswordForm = reactive<API.ResetPasswordRequest>({
  userAccount: '',
  email: '',
  code: '',
  newPassword: '',
  checkPassword: '',
})

const validateCheckPassword = (
  _rule: unknown,
  value: string,
  callback: (error?: Error) => void,
) => {
  if (value && value !== resetPasswordForm.newPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const handleSendCode = async (values: API.FindPasswordRequest) => {
  sendCodeLoading.value = true
  try {
    const res = await findPassword(values)
    if (res.data.code === 0) {
      message.success('验证码已发送到你的邮箱，请查收')
      resetPasswordForm.userAccount = values.userAccount || ''
      resetPasswordForm.email = values.email || ''
      step.value = 2
    } else {
      message.error(res.data.message || '发送失败，请重试')
    }
  } catch (error: any) {
    console.error('发送验证码失败:', error)
    const errorMsg = error?.response?.data?.message || error?.message || '发送失败，请检查网络连接'
    message.error(errorMsg)
  } finally {
    sendCodeLoading.value = false
  }
}

const handleResetPassword = async (values: API.ResetPasswordRequest) => {
  resetLoading.value = true
  try {
    const res = await resetPassword({
      userAccount: resetPasswordForm.userAccount,
      email: resetPasswordForm.email,
      code: values.code,
      newPassword: values.newPassword,
      checkPassword: values.checkPassword,
    })
    if (res.data.code === 0) {
      message.success('密码重置成功！请使用新密码登录')
      setTimeout(() => {
        router.push({
          path: '/user/login',
          replace: true,
        })
      }, 1500)
    } else {
      message.error(res.data.message || '重置失败，请重试')
    }
  } catch (error: any) {
    console.error('重置密码失败:', error)
    const errorMsg = error?.response?.data?.message || error?.message || '重置失败，请检查网络连接'
    message.error(errorMsg)
  } finally {
    resetLoading.value = false
  }
}

const handleBack = () => {
  step.value = 1
  resetPasswordForm.code = ''
  resetPasswordForm.newPassword = ''
  resetPasswordForm.checkPassword = ''
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
  margin-bottom: 50px;
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

/* ========== 步骤指示器 ========== */
.step-indicator {
  display: flex;
  flex-direction: column;
  gap: 0;
  margin-bottom: 40px;
}

.step-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  opacity: 0;
  animation: fadeInUp 0.8s ease forwards;
  position: relative;
}

.step-item:nth-child(1) {
  animation-delay: 0.2s;
}

.step-item:nth-child(3) {
  animation-delay: 0.4s;
}

.step-item:nth-child(5) {
  animation-delay: 0.6s;
}

.step-number {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 107, 107, 0.1);
  border: 2px solid rgba(255, 107, 107, 0.2);
  border-radius: 50%;
  color: var(--color-primary);
  font-weight: 600;
  font-size: 14px;
  flex-shrink: 0;
  transition: all 0.3s ease;
}

.step-item.active .step-number {
  background: var(--color-primary);
  border-color: var(--color-primary);
  color: white;
  box-shadow: 0 0 12px var(--color-primary);
}

.step-item.completed .step-number {
  background: var(--color-primary);
  border-color: var(--color-primary);
  color: white;
}

.step-content {
  flex: 1;
  padding-top: 4px;
}

.step-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0 0 4px 0;
}

.step-item.active .step-title {
  color: var(--color-primary);
}

.step-desc {
  font-size: 13px;
  color: var(--color-text-secondary);
  margin: 0;
}

.step-line {
  height: 40px;
  margin-left: 17px;
  width: 2px;
  background: rgba(255, 107, 107, 0.2);
  transition: all 0.3s ease;
}

.step-line.active {
  background: var(--color-primary);
}

.intro-footer {
  opacity: 0;
  animation: fadeInUp 0.8s ease 0.6s forwards;
}

.intro-text {
  font-size: 16px;
  color: var(--color-text-secondary);
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
  max-width: 480px;
  box-shadow: var(--shadow-soft);
  animation: fadeInUp 1s ease 0.2s both;
  transform: translateZ(0);
  backface-visibility: hidden;
  will-change: transform, opacity;
}

.step-content {
  animation: fadeIn 0.4s ease;
}

.card-header {
  text-align: center;
  margin-bottom: 36px;
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

/* ========== 成功提示 ========== */
.success-alert {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: rgba(255, 107, 107, 0.08);
  border: 1px solid rgba(255, 107, 107, 0.2);
  border-radius: var(--radius-md);
  margin-bottom: 32px;
}

.alert-icon {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-primary);
  color: white;
  border-radius: 50%;
  font-weight: 700;
  font-size: 18px;
  flex-shrink: 0;
}

.alert-content {
  flex: 1;
}

.alert-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--color-primary);
  margin: 0 0 4px 0;
}

.alert-desc {
  font-size: 13px;
  color: var(--color-text-secondary);
  margin: 0;
  line-height: 1.5;
}

/* ========== 表单样式 ========== */
.reset-form {
  margin-bottom: 24px;
}

.reset-form :deep(.ant-form-item) {
  margin-bottom: 24px;
}

.reset-form :deep(.ant-form-item:last-child) {
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
  margin-bottom: 12px;
}

.submit-button:hover {
  background: linear-gradient(135deg, var(--color-primary-light), var(--color-primary));
  box-shadow: var(--shadow-hover);
  transform: translate3d(0, -2px, 0);
}

.submit-button:active {
  transform: translateY(0);
}

.submit-button.secondary {
  background: rgba(255, 255, 255, 0.5);
  border: 1px solid rgba(255, 107, 107, 0.3);
  color: var(--color-text);
  box-shadow: none;
}

.submit-button.secondary:hover {
  background: rgba(255, 255, 255, 0.7);
  border-color: var(--color-primary);
  color: var(--color-primary);
  box-shadow: var(--shadow-soft);
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
  transition: all 0.3s ease;
}

.footer-link:hover {
  color: var(--color-primary-dark);
  text-decoration: underline;
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

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translate3d(0, 10px, 0);
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
    padding: 20px;
  }

  .brand-mark {
    justify-content: center;
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

  .step-title {
    font-size: 15px;
  }

  .step-desc {
    font-size: 12px;
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
