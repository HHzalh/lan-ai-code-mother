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
          <h1 class="brand-title">加入创作者社区</h1>
        </div>

        <div class="benefit-list">
          <div
            v-for="(benefit, index) in benefits"
            :key="index"
            :style="{ '--delay': `${index * 0.1}s` }"
            class="benefit-card"
          >
            <div class="benefit-icon">{{ benefit.icon }}</div>
            <div class="benefit-content">
              <h3 class="benefit-title">{{ benefit.title }}</h3>
              <p class="benefit-desc">{{ benefit.desc }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧注册表单 -->
      <div class="form-section">
        <div class="glass-card">
          <div class="card-header">
            <h2 class="card-title">创建账号</h2>
            <p class="card-subtitle">开启你的 AI 创作之旅</p>
          </div>

          <a-form
            ref="formRef"
            :model="formState"
            autocomplete="off"
            class="register-form"
            @finish="handleSubmit"
          >
            <!-- 账号输入 -->
            <a-form-item
              :rules="[
                { required: true, message: '请输入账号' },
                { min: 4, message: '账号长度不能少于 4 位' },
                { max: 20, message: '账号长度不能超过 20 位' },
              ]"
              name="userAccount"
            >
              <label class="form-label">账号</label>
              <a-input
                v-model:value="formState.userAccount"
                class="glass-input"
                placeholder="请输入账号（4-20 位）"
                size="large"
              />
            </a-form-item>

            <!-- 密码输入 -->
            <a-form-item
              :rules="[
                { required: true, message: '请输入密码' },
                { min: 8, message: '密码不能小于 8 位' },
                { max: 20, message: '密码不能超过 20 位' },
              ]"
              name="userPassword"
            >
              <label class="form-label">密码</label>
              <a-input-password
                v-model:value="formState.userPassword"
                class="glass-input"
                placeholder="请输入密码（至少 8 位）"
                size="large"
                @change="handlePasswordChange"
              />

              <!-- 密码强度指示器 -->
              <div v-if="formState.userPassword" class="password-strength">
                <div class="strength-bar">
                  <div
                    v-for="i in 4"
                    :key="i"
                    :class="['strength-segment', { active: i <= passwordStrength }]"
                  ></div>
                </div>
                <span class="strength-text">{{ passwordStrengthText }}</span>
              </div>
            </a-form-item>

            <!-- 确认密码 -->
            <a-form-item
              :rules="[
                { required: true, message: '请确认密码' },
                { validator: validateCheckPassword },
              ]"
              name="checkPassword"
            >
              <label class="form-label">确认密码</label>
              <a-input-password
                v-model:value="formState.checkPassword"
                class="glass-input"
                placeholder="请再次输入密码"
                size="large"
              />
            </a-form-item>

            <!-- 邀请码 -->
            <a-form-item name="invitationCode">
              <label class="form-label">
                邀请码
                <span class="optional-tag">（可选）</span>
              </label>
              <a-input
                v-model:value="formState.invitationCode"
                class="glass-input"
                placeholder="填写邀请码可获得积分奖励"
                size="large"
              />
            </a-form-item>

            <!-- 注册邮箱 -->
            <a-form-item
              :rules="[
                { required: true, message: '请输入邮箱' },
                { type: 'email', message: '请输入有效的邮箱地址' },
              ]"
              name="userEmail"
            >
              <label class="form-label">注册邮箱</label>
              <a-input
                v-model:value="formState.userEmail"
                class="glass-input"
                placeholder="请输入您的邮箱地址"
                size="large"
              >
                <template #prefix>
                  <MailOutlined class="input-icon" />
                </template>
              </a-input>
            </a-form-item>

            <!-- 邮箱验证码 -->
            <a-form-item
              :rules="[
                { required: true, message: '请输入邮箱验证码' },
                { len: 6, message: '验证码为6位数字' },
              ]"
              name="code"
            >
              <label class="form-label">
                邮箱验证码
                <span class="validity-hint">验证码5分钟内有效</span>
              </label>
              <div class="email-code-row">
                <a-input
                  v-model:value="formState.code"
                  class="glass-input code-input"
                  maxlength="6"
                  placeholder="请输入6位验证码"
                  size="large"
                >
                  <template #prefix>
                    <SafetyOutlined class="input-icon" />
                  </template>
                </a-input>
                <a-button
                  :disabled="isSending || countdown > 0"
                  :loading="isSending"
                  class="send-code-button"
                  size="large"
                  @click="handleSendCode"
                >
                  {{ countdown > 0 ? `${countdown}秒后重试` : '发送验证码' }}
                </a-button>
              </div>
              <div v-if="codeSentTime" class="code-sent-tip">
                <ClockCircleOutlined class="tip-icon" />
                验证码已发送，请查收邮箱。{{ countdown > 0 ? `(${countdown}秒后可重新发送)` : '' }}
              </div>
            </a-form-item>

            <!-- 注册按钮 -->
            <a-button
              :loading="loading"
              block
              class="submit-button"
              html-type="submit"
              size="large"
              type="primary"
            >
              {{ loading ? '注册中...' : '立即注册' }}
            </a-button>

            <!-- 协议提示 -->
            <div class="agreement-notice">
              注册即表示同意我们的
              <a class="agreement-link" href="/help/docs#terms">服务条款</a>
            </div>
          </a-form>

          <!-- 底部链接 -->
          <div class="form-footer">
            <span class="footer-text">已有账号？</span>
            <RouterLink class="footer-link" to="/user/login">立即登录</RouterLink>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { useRoute, useRouter } from 'vue-router'
import { sendRegisterEmailCode, userRegister } from '@/api/userController.ts'
import type { FormInstance } from 'ant-design-vue'
import { message } from 'ant-design-vue'
import { computed, onMounted, onUnmounted, reactive, ref } from 'vue'
import { ClockCircleOutlined, MailOutlined, SafetyOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()
const formRef = ref<FormInstance>()
const loading = ref(false)
const isSending = ref(false)
const countdown = ref(0)
const codeSentTime = ref<string>('')
const countdownTimer = ref<number | null>(null)

const formState = reactive<API.UserRegisterRequest>({
  userAccount: '',
  userPassword: '',
  checkPassword: '',
  invitationCode: '',
  userEmail: '',
  code: '',
})

const benefits = [
  {
    icon: '🎨',
    title: '智能创作',
    desc: 'AI 助你快速生成应用代码',
  },
  {
    icon: '🚀',
    title: '一键部署',
    desc: '轻松发布到云端服务器',
  },
  {
    icon: '🎁',
    title: '积分奖励',
    desc: '签到邀请获取丰厚奖励',
  },
]

// 密码强度计算（0-4）
const passwordStrength = computed(() => {
  const password = formState.userPassword || ''
  let strength = 0

  if (password.length >= 8) strength++
  if (password.length >= 12) strength++
  if (/[A-Z]/.test(password) && /[a-z]/.test(password)) strength++
  if (/[0-9]/.test(password)) strength++
  if (/[^A-Za-z0-9]/.test(password)) strength++

  return Math.min(strength, 4)
})

const passwordStrengthText = computed(() => {
  const strength = passwordStrength.value
  if (strength <= 1) return '弱'
  if (strength === 2) return '中等'
  if (strength === 3) return '强'
  return '非常强'
})

const handlePasswordChange = () => {
  if (formState.checkPassword && formState.userPassword !== formState.checkPassword) {
    formState.checkPassword = ''
  }
}

const validateCheckPassword = (
  _rule: unknown,
  value: string,
  callback: (error?: Error) => void,
) => {
  if (value && value !== formState.userPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 发送邮箱验证码
const handleSendCode = async () => {
  if (!formState.userEmail) {
    message.error('请先输入邮箱地址')
    formRef.value?.validateFields(['userEmail'])
    return
  }

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(formState.userEmail)) {
    message.error('请输入有效的邮箱地址')
    return
  }

  isSending.value = true
  try {
    const res = await sendRegisterEmailCode({ email: formState.userEmail })
    if (res.data.code === 0) {
      message.success('验证码已发送到您的邮箱，请注意查收')
      codeSentTime.value = new Date().toLocaleTimeString()
      countdown.value = 60
      countdownTimer.value = window.setInterval(() => {
        countdown.value--
        if (countdown.value <= 0) {
          if (countdownTimer.value) {
            clearInterval(countdownTimer.value)
            countdownTimer.value = null
          }
        }
      }, 1000)
    } else {
      message.error(res.data.message || '发送失败，请重试')
    }
  } catch (error: any) {
    console.error('发送验证码失败:', error)
    const errorMsg = error?.response?.data?.message || error?.message || '发送失败，请检查网络连接'
    message.error(errorMsg)
  } finally {
    isSending.value = false
  }
}

const handleSubmit = async (values: API.UserRegisterRequest) => {
  loading.value = true
  try {
    const res = await userRegister(values)
    if (res.data.code === 0) {
      message.success('注册成功！即将跳转到登录页面...')
      setTimeout(() => {
        router.push({
          path: '/user/login',
          replace: true,
        })
      }, 1500)
    } else {
      message.error(res.data.message || '注册失败，请重试')
    }
  } catch (error: any) {
    console.error('注册失败:', error)
    const errorMsg = error?.response?.data?.message || error?.message || '注册失败，请检查网络连接'
    message.error(errorMsg)
  } finally {
    loading.value = false
  }
}

// 清理定时器
onUnmounted(() => {
  if (countdownTimer.value) {
    clearInterval(countdownTimer.value)
  }
})

onMounted(() => {
  const invitationCodeParam = route.query.invitationCode as string
  if (invitationCodeParam) {
    formState.invitationCode = invitationCodeParam
  }
})
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

.benefit-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.benefit-card {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(10px);
  border-radius: var(--radius-md);
  border: 1px solid rgba(255, 107, 107, 0.1);
  opacity: 0;
  animation: fadeInUp 0.8s ease forwards;
  transition: all 0.3s ease;
}

.benefit-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-soft);
}

.benefit-icon {
  font-size: 28px;
  line-height: 1;
}

.benefit-content {
  flex: 1;
}

.benefit-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0 0 6px 0;
}

.benefit-desc {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin: 0;
  line-height: 1.5;
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

/* ========== 表单样式 ========== */
.register-form {
  margin-bottom: 24px;
}

.register-form :deep(.ant-form-item) {
  margin-bottom: 20px;
}

.register-form :deep(.ant-form-item:last-child) {
  margin-bottom: 0;
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text);
  margin-bottom: 8px;
}

.optional-tag {
  font-size: 13px;
  color: var(--color-text-light);
  font-weight: 400;
  margin-left: 4px;
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

.glass-input:focus-within .input-icon {
  color: var(--color-primary);
}

/* ========== 邮箱验证码行 ========== */
.email-code-row {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.code-input {
  flex: 1;
}

.send-code-button {
  flex-shrink: 0;
  width: auto;
  min-width: 120px;
  height: 48px;
  background: linear-gradient(135deg, var(--color-primary-light), var(--color-primary));
  border: none;
  color: white;
  font-size: 14px;
  font-weight: 500;
  border-radius: var(--radius-sm);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.2);
}

.send-code-button:hover:not(:disabled) {
  background: linear-gradient(135deg, var(--color-primary), var(--color-primary-dark));
  box-shadow: 0 6px 16px rgba(255, 107, 107, 0.3);
  transform: translate3d(0, -2px, 0);
}

.send-code-button:active:not(:disabled) {
  transform: translateY(0);
}

.send-code-button:disabled {
  background: rgba(184, 184, 184, 0.3);
  color: var(--color-text-light);
  box-shadow: none;
  cursor: not-allowed;
}

.code-sent-tip {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 8px;
  font-size: 13px;
  color: var(--color-text-secondary);
  animation: fadeInUp 0.3s ease;
}

.tip-icon {
  color: var(--color-primary);
  font-size: 14px;
}

/* ========== 密码强度指示器 ========== */
.password-strength {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 10px;
}

.strength-bar {
  flex: 1;
  display: flex;
  gap: 6px;
  height: 4px;
}

.strength-segment {
  flex: 1;
  border-radius: 2px;
  background: rgba(255, 107, 107, 0.2);
  transition: all 0.3s ease;
}

.strength-segment.active:nth-child(1) {
  background: #ff6b6b;
}

.strength-segment.active:nth-child(2) {
  background: #ff8787;
}

.strength-segment.active:nth-child(3) {
  background: #ffa8a8;
}

.strength-segment.active:nth-child(4) {
  background: #74b9ff;
}

.strength-text {
  font-size: 13px;
  font-weight: 500;
  min-width: 60px;
  text-align: right;
  color: var(--color-text-secondary);
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
  margin-top: 12px;
}

.submit-button:hover {
  background: linear-gradient(135deg, var(--color-primary-light), var(--color-primary));
  box-shadow: var(--shadow-hover);
  transform: translate3d(0, -2px, 0);
}

.submit-button:active {
  transform: translateY(0);
}

/* ========== 协议提示 ========== */
.agreement-notice {
  text-align: center;
  font-size: 13px;
  color: var(--color-text-secondary);
  margin-top: 16px;
  line-height: 1.5;
}

.agreement-link {
  color: var(--color-primary);
  text-decoration: none;
  font-weight: 500;
  transition: all 0.3s ease;
}

.agreement-link:hover {
  color: var(--color-primary-dark);
  text-decoration: underline;
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

  .benefit-card {
    padding: 16px;
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

  .benefit-icon {
    font-size: 24px;
  }

  .benefit-title {
    font-size: 15px;
  }

  .benefit-desc {
    font-size: 13px;
  }

  .email-code-row {
    flex-direction: column;
    gap: 8px;
  }

  .send-code-button {
    width: 100%;
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
