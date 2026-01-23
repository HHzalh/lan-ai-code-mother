<template>
  <div class="register-container">
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

    <div class="register-card success-card">
      <!-- Logo 和标题 -->
      <div class="card-header">
        <div class="logo-wrapper">
          <div class="logo-icon">
            <CodeOutlined />
          </div>
        </div>
        <h1 class="register-title">创建账号</h1>
        <p class="welcome-text">开启您的 AI 代码生成之旅</p>
      </div>

      <!-- 注册表单 -->
      <a-form
        ref="formRef"
        :model="formState"
        autocomplete="off"
        class="register-form"
        layout="vertical"
        name="register"
        @finish="handleSubmit"
      >
        <!-- 账号输入 -->
        <a-form-item
          :rules="[
            { required: true, message: '请输入账号' },
            { min: 4, message: '账号长度不能少于 4 位' },
            { max: 20, message: '账号长度不能超过 20 位' },
            { pattern: /^[a-zA-Z0-9_]+$/, message: '账号只能包含字母、数字和下划线' },
          ]"
          name="userAccount"
        >
          <a-input
            v-model:value="formState.userAccount"
            class="register-input"
            placeholder="请输入账号（4-20位字母数字）"
            size="large"
            @change="handleAccountChange"
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
            { max: 20, message: '密码不能超过 20 位' },
          ]"
          name="userPassword"
        >
          <a-input-password
            v-model:value="formState.userPassword"
            class="register-input"
            placeholder="请输入密码（至少8位）"
            size="large"
            @change="handlePasswordChange"
          >
            <template #prefix>
              <LockOutlined class="input-icon" />
            </template>
          </a-input-password>
        </a-form-item>

        <!-- 密码强度指示器 -->
        <div v-if="formState.userPassword" class="password-strength">
          <div class="strength-bar">
            <div
              :class="['strength-segment', { active: passwordStrength >= 1 }]"
              class="strength-segment weak"
            ></div>
            <div
              :class="['strength-segment', { active: passwordStrength >= 2 }]"
              class="strength-segment medium"
            ></div>
            <div
              :class="['strength-segment', { active: passwordStrength >= 3 }]"
              class="strength-segment strong"
            ></div>
          </div>
          <span :class="['strength-text', passwordStrengthClass]">
            {{ passwordStrengthText }}
          </span>
        </div>

        <!-- 确认密码 -->
        <a-form-item
          :rules="[
            { required: true, message: '请确认密码' },
            { min: 8, message: '密码不能小于 8 位' },
            { validator: validateCheckPassword },
          ]"
          name="checkPassword"
        >
          <a-input-password
            v-model:value="formState.checkPassword"
            class="register-input"
            placeholder="请再次输入密码"
            size="large"
          >
            <template #prefix>
              <SafetyOutlined class="input-icon" />
            </template>
          </a-input-password>
        </a-form-item>

        <!-- 邀请码（可选） -->
        <a-form-item name="invitationCode">
          <a-input
            v-model:value="formState.invitationCode"
            class="register-input"
            placeholder="邀请码（可选，填写可获得积分奖励）"
            size="large"
          >
            <template #prefix>
              <GiftOutlined class="input-icon" />
            </template>
            <template #suffix>
              <a-tooltip title="填写邀请码，注册成功后您和邀请人都能获得积分奖励">
                <QuestionCircleOutlined class="help-icon" />
              </a-tooltip>
            </template>
          </a-input>
        </a-form-item>

        <!-- 图形验证码 -->
        <CaptchaInput ref="captchaRef" v-model="formState.captcha" />

        <!-- 注册按钮 -->
        <a-form-item>
          <a-button
            :loading="loading"
            block
            class="register-button"
            html-type="submit"
            size="large"
            type="primary"
          >
            <template #icon>
              <UserAddOutlined />
            </template>
            立即注册
          </a-button>
        </a-form-item>

        <!-- 协议同意提示 -->
        <div class="agreement-tip">
          注册即表示您同意我们的
          <a class="link" href="/help/docs#terms">服务条款</a>
        </div>
      </a-form>

      <!-- 登录链接 -->
      <div class="login-link">
        已有账号？
        <RouterLink class="link-text" to="/user/login">立即登录</RouterLink>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { useRoute, useRouter } from 'vue-router'
import { userRegister } from '@/api/userController.ts'
import type { FormInstance } from 'ant-design-vue'
import { message } from 'ant-design-vue'
import { computed, onMounted, reactive, ref } from 'vue'
import CaptchaInput from '@/components/CaptchaInput.vue'
import {
  CodeOutlined,
  GiftOutlined,
  LockOutlined,
  QuestionCircleOutlined,
  SafetyOutlined,
  UserAddOutlined,
  UserOutlined,
} from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()
const formRef = ref<FormInstance>()
const loading = ref(false)
const captchaRef = ref<InstanceType<typeof CaptchaInput>>()

const formState = reactive<API.UserRegisterRequest & { captcha: string }>({
  userAccount: '',
  userPassword: '',
  checkPassword: '',
  invitationCode: '',
  captcha: '',
})

// 密码强度计算（0-3）
const passwordStrength = computed(() => {
  const password = formState.userPassword || ''
  let strength = 0

  if (password.length >= 8) strength++
  if (password.length >= 12) strength++
  if (/[A-Z]/.test(password) && /[a-z]/.test(password)) strength++
  if (/[0-9]/.test(password)) strength++
  if (/[^A-Za-z0-9]/.test(password)) strength++

  return Math.min(strength, 3)
})

const passwordStrengthText = computed(() => {
  const strength = passwordStrength.value
  if (strength === 0) return '太弱'
  if (strength === 1) return '弱'
  if (strength === 2) return '中等'
  return '强'
})

const passwordStrengthClass = computed(() => {
  const strength = passwordStrength.value
  if (strength === 0) return 'weak'
  if (strength === 1) return 'weak'
  if (strength === 2) return 'medium'
  return 'strong'
})

/**
 * 账号输入变化处理
 */
const handleAccountChange = () => {
  // 可以添加实时验证逻辑
}

/**
 * 密码输入变化处理
 */
const handlePasswordChange = () => {
  // 密码变化时清空确认密码
  if (formState.checkPassword && formState.userPassword !== formState.checkPassword) {
    formState.checkPassword = ''
  }
}

/**
 * 验证确认密码
 */
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

/**
 * 提交注册表单
 */
const handleSubmit = async (values: API.UserRegisterRequest & { captcha: string }) => {
  loading.value = true
  try {
    // 验证验证码
    if (!captchaRef.value?.validate()) {
      message.error('验证码错误，请重新输入')
      captchaRef.value?.refresh()
      loading.value = false
      return
    }

    // 移除captcha字段，因为后端不需要
    const { captcha, ...registerData } = values
    const res = await userRegister(registerData)
    if (res.data.code === 0) {
      message.success('🎉 注册成功！即将跳转到登录页面...')
      setTimeout(() => {
        router.push({
          path: '/user/login',
          replace: true,
        })
      }, 1500)
    } else {
      message.error(res.data.message || '注册失败，请重试')
      captchaRef.value?.refresh()
    }
  } catch (error: any) {
    console.error('注册失败:', error)
    const errorMsg = error?.response?.data?.message || error?.message || '注册失败，请检查网络连接'
    message.error(errorMsg)
    captchaRef.value?.refresh()
  } finally {
    loading.value = false
  }
}

// 解析 URL 参数，自动填入邀请码
onMounted(() => {
  const invitationCodeParam = route.query.invitationCode as string
  if (invitationCodeParam) {
    formState.invitationCode = invitationCodeParam
  }
})
</script>

<style lang="less" scoped>
.register-container {
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

/* 注册卡片 */
.register-card {
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

.register-title {
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
.register-form {
  margin-top: 0;
}

.register-form :deep(.ant-form-item) {
  margin-bottom: 20px;
}

.register-form :deep(.ant-form-item:last-child) {
  margin-bottom: 0;
}

.register-input {
  border-radius: 12px;
  transition: all 0.3s ease;
}

.register-input :deep(.ant-input),
.register-input :deep(.ant-input-password) {
  border-radius: 12px;
  height: 50px;
  font-size: 13px;
  padding-left: 44px;
  border: 2px solid #e5e7eb;
  background: #f9fafb;
  transition: all 0.3s ease;
}

.register-input :deep(.ant-input:hover),
.register-input :deep(.ant-input-password:hover) {
  border-color: #f97316;
  background: #ffffff;
}

.register-input :deep(.ant-input:focus),
.register-input :deep(.ant-input-focused),
.register-input :deep(.ant-input-password:focus) {
  border-color: #f97316;
  background: #ffffff;
  box-shadow: 0 0 0 4px rgba(249, 115, 22, 0.1);
}

.input-icon {
  color: #9ca3af;
  font-size: 16px;
  transition: color 0.3s ease;
}

.register-input:focus-within .input-icon {
  color: #f97316;
}

.register-input :deep(.ant-input-password-icon) {
  color: #9ca3af;
  font-size: 16px;
  transition: color 0.3s ease;
}

.register-input :deep(.ant-input-password-icon:hover) {
  color: #f97316;
}

.help-icon {
  color: #9ca3af;
  font-size: 14px;
  cursor: help;
  transition: color 0.3s ease;
}

.help-icon:hover {
  color: #f97316;
}

/* 密码强度指示器 */
.password-strength {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: -12px;
  margin-bottom: 12px;
  padding: 0 4px;
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
  background: #e5e7eb;
  transition: all 0.3s ease;
}

.strength-segment.active.weak {
  background: #ef4444;
}

.strength-segment.active.medium {
  background: #f59e0b;
}

.strength-segment.active.strong {
  background: #10b981;
}

.strength-text {
  font-size: 12px;
  font-weight: 500;
  min-width: 30px;
  text-align: right;
}

.strength-text.weak {
  color: #ef4444;
}

.strength-text.medium {
  color: #f59e0b;
}

.strength-text.strong {
  color: #10b981;
}

/* 注册按钮 */
.register-button {
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

.register-button:hover {
  background: linear-gradient(135deg, #ea580c 0%, #3b82f6 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(249, 115, 22, 0.4);
}

.register-button:active {
  transform: translateY(0);
}

/* 协议提示 */
.agreement-tip {
  text-align: center;
  color: #6b7280;
  font-size: 13px;
  margin-top: 16px;
  line-height: 1.5;
}

.agreement-tip .link {
  color: #f97316;
  text-decoration: none;
  font-weight: 500;
  margin: 0 2px;
  transition: color 0.3s ease;
}

.agreement-tip .link:hover {
  color: #ea580c;
  text-decoration: underline;
}

/* 登录链接 */
.login-link {
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
  margin-left: 4px;
  transition: all 0.3s ease;
}

.link-text:hover {
  color: #ea580c;
  text-decoration: underline;
}

/* 页脚 */
.page-footer {
  position: absolute;
  bottom: 20px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 13px;
  text-align: center;
  z-index: 1;
}

/* 响应式设计 */
@media (max-width: 640px) {
  .register-card {
    padding: 36px 28px;
    border-radius: 20px;
  }

  .register-title {
    font-size: 24px;
  }

  .welcome-text {
    font-size: 14px;
  }

  .circle {
    display: none;
  }

  .page-footer {
    position: relative;
    margin-top: 32px;
    bottom: auto;
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
</style>
