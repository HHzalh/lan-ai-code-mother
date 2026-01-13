<template>
  <div class="find-password-container">
    <!-- 背景装饰元素 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>

    <div class="find-password-card">
      <!-- Logo 和标题 -->
      <div class="card-header">
        <div class="logo-wrapper">
          <div class="logo-icon">
            <KeyOutlined />
          </div>
        </div>
        <h1 class="find-password-title">找回密码</h1>
        <p class="welcome-text">
          忘记密码？别担心，<br />
          我们会通过邮箱帮您安全找回密码
        </p>
      </div>

      <!-- 第一步：发送验证码 -->
      <div v-if="step === 1" class="step-content">
        <a-form
          ref="step1FormRef"
          :model="findPasswordForm"
          autocomplete="off"
          class="find-password-form"
          layout="vertical"
          name="findPassword"
          @finish="handleSendCode"
        >
          <!-- 账号输入 -->
          <a-form-item
            :rules="[
              { required: true, message: '请输入账号' },
              { min: 4, message: '账号长度不能少于 4 位' },
            ]"
            label="账号"
            name="userAccount"
          >
            <a-input
              v-model:value="findPasswordForm.userAccount"
              class="find-password-input"
              placeholder="请输入账号"
              size="large"
            >
              <template #prefix>
                <UserOutlined class="input-icon" />
              </template>
            </a-input>
          </a-form-item>

          <!-- 邮箱输入 -->
          <a-form-item
            :rules="[
              { required: true, message: '请输入邮箱地址' },
              { type: 'email', message: '请输入正确的邮箱格式' },
            ]"
            label="邮箱地址"
            name="email"
          >
            <a-input
              v-model:value="findPasswordForm.email"
              class="find-password-input"
              placeholder="请输入注册时使用的邮箱"
              size="large"
            >
              <template #prefix>
                <MailOutlined class="input-icon" />
              </template>
            </a-input>
          </a-form-item>

          <!-- 发送验证码按钮 -->
          <a-form-item>
            <a-button
              :loading="sendCodeLoading"
              block
              class="find-password-button"
              html-type="submit"
              size="large"
              type="primary"
            >
              <template #icon>
                <SendOutlined />
              </template>
              发送验证码
            </a-button>
          </a-form-item>
        </a-form>
      </div>

      <!-- 第二步：重置密码 -->
      <div v-if="step === 2" class="step-content">
        <a-form
          ref="step2FormRef"
          :model="resetPasswordForm"
          autocomplete="off"
          class="find-password-form"
          layout="vertical"
          name="resetPassword"
          @finish="handleResetPassword"
        >
          <!-- 成功提示 -->
          <a-alert
            :closable="false"
            description="验证码已发送到您的邮箱，请查收（有效期5分钟）"
            message="验证码已发送"
            show-icon
            style="margin-bottom: 20px"
            type="success"
          />

          <!-- 验证码输入 -->
          <a-form-item
            :rules="[
              { required: true, message: '请输入验证码' },
              { min: 4, message: '验证码不能少于 4 位' },
            ]"
            label="验证码"
            name="code"
          >
            <a-input
              v-model:value="resetPasswordForm.code"
              class="find-password-input"
              placeholder="请输入邮箱收到的验证码"
              size="large"
            >
              <template #prefix>
                <SafetyOutlined class="input-icon" />
              </template>
            </a-input>
          </a-form-item>

          <!-- 新密码输入 -->
          <a-form-item
            :rules="[
              { required: true, message: '请输入新密码' },
              { min: 8, message: '密码不能小于 8 位' },
              { max: 20, message: '密码不能超过 20 位' },
            ]"
            label="新密码"
            name="newPassword"
          >
            <a-input-password
              v-model:value="resetPasswordForm.newPassword"
              class="find-password-input"
              placeholder="请输入新密码（至少8位）"
              size="large"
            >
              <template #prefix>
                <LockOutlined class="input-icon" />
              </template>
            </a-input-password>
          </a-form-item>

          <!-- 确认密码输入 -->
          <a-form-item
            :rules="[
              { required: true, message: '请确认新密码' },
              { min: 8, message: '密码不能小于 8 位' },
              { validator: validateCheckPassword },
            ]"
            label="确认新密码"
            name="checkPassword"
          >
            <a-input-password
              v-model:value="resetPasswordForm.checkPassword"
              class="find-password-input"
              placeholder="请再次输入新密码"
              size="large"
            >
              <template #prefix>
                <LockOutlined class="input-icon" />
              </template>
            </a-input-password>
          </a-form-item>

          <!-- 重置密码按钮 -->
          <a-form-item>
            <a-button
              :loading="resetLoading"
              block
              class="find-password-button"
              html-type="submit"
              size="large"
              type="primary"
            >
              <template #icon>
                <KeyOutlined />
              </template>
              重置密码
            </a-button>
          </a-form-item>

          <!-- 返回按钮 -->
          <a-form-item>
            <a-button block class="back-button" size="large" @click="handleBack">
              <template #icon>
                <ArrowLeftOutlined />
              </template>
              返回上一步
            </a-button>
          </a-form-item>
        </a-form>
      </div>

      <!-- 返回登录链接 -->
      <div class="login-link">
        想起密码了？
        <RouterLink class="link-text" to="/user/login">返回登录</RouterLink>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { useRouter } from 'vue-router'
import type { FormInstance } from 'ant-design-vue'
import { message } from 'ant-design-vue'
import { reactive, ref } from 'vue'
import { findPassword, resetPassword } from '@/api/userController'
import {
  ArrowLeftOutlined,
  KeyOutlined,
  LockOutlined,
  MailOutlined,
  SafetyOutlined,
  SendOutlined,
  UserOutlined,
} from '@ant-design/icons-vue'

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

/**
 * 验证确认密码
 */
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

/**
 * 发送验证码
 */
const handleSendCode = async (values: API.FindPasswordRequest) => {
  sendCodeLoading.value = true
  try {
    const res = await findPassword(values)
    if (res.data.code === 0) {
      message.success('✅ 验证码已发送到您的邮箱，请查收')
      // 保存账号和邮箱到重置密码表单
      resetPasswordForm.userAccount = values.userAccount || ''
      resetPasswordForm.email = values.email || ''
      // 进入下一步
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

/**
 * 重置密码
 */
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
      message.success('🎉 密码重置成功！请使用新密码登录')
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

/**
 * 返回上一步
 */
const handleBack = () => {
  step.value = 1
  resetPasswordForm.code = ''
  resetPasswordForm.newPassword = ''
  resetPasswordForm.checkPassword = ''
}
</script>

<style lang="less" scoped>
.find-password-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
  position: relative;
  overflow: hidden;
}

/* 背景装饰 */
.bg-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  overflow: hidden;
  z-index: 0;
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

/* 找回密码卡片 */
.find-password-card {
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 48px 42px;
  width: 100%;
  max-width: 480px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  position: relative;
  z-index: 1;
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 32px;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.4);
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

.find-password-title {
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
  font-size: 15px;
  margin: 0;
  line-height: 1.5;
  font-weight: 400;
}

/* 表单样式 */
.step-content {
  margin-top: 0;
}

.find-password-form {
  margin-top: 0;
}

.find-password-form :deep(.ant-form-item) {
  margin-bottom: 20px;
}

.find-password-form :deep(.ant-form-item:last-child) {
  margin-bottom: 0;
}

.find-password-form :deep(.ant-form-item-label > label) {
  font-weight: 500;
  color: #374151;
}

.find-password-input {
  border-radius: 12px;
  transition: all 0.3s ease;
}

.find-password-input :deep(.ant-input),
.find-password-input :deep(.ant-input-password) {
  border-radius: 12px;
  height: 50px;
  font-size: 15px;
  padding-left: 44px;
  border: 2px solid #e5e7eb;
  background: #f9fafb;
  transition: all 0.3s ease;
}

.find-password-input :deep(.ant-input:hover),
.find-password-input :deep(.ant-input-password:hover) {
  border-color: #667eea;
  background: #ffffff;
}

.find-password-input :deep(.ant-input:focus),
.find-password-input :deep(.ant-input-focused),
.find-password-input :deep(.ant-input-password:focus) {
  border-color: #667eea;
  background: #ffffff;
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
}

.input-icon {
  color: #9ca3af;
  font-size: 16px;
  transition: color 0.3s ease;
}

.find-password-input:focus-within .input-icon {
  color: #667eea;
}

.find-password-input :deep(.ant-input-password-icon) {
  color: #9ca3af;
  font-size: 16px;
  transition: color 0.3s ease;
}

.find-password-input :deep(.ant-input-password-icon:hover) {
  color: #667eea;
}

/* 找回密码按钮 */
.find-password-button {
  height: 52px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  margin-top: 8px;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.find-password-button:hover {
  background: linear-gradient(135deg, #5568d3 0%, #6a3d91 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.4);
}

.find-password-button:active {
  transform: translateY(0);
}

/* 返回按钮 */
.back-button {
  height: 52px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  background: #ffffff;
  border: 2px solid #e5e7eb;
  color: #6b7280;
  margin-top: 8px;
  transition: all 0.3s ease;
}

.back-button:hover {
  border-color: #667eea;
  color: #667eea;
  background: #f9fafb;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
}

.back-button:active {
  transform: translateY(0);
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
  color: #667eea;
  text-decoration: none;
  font-weight: 600;
  margin-left: 4px;
  transition: all 0.3s ease;
}

.link-text:hover {
  color: #764ba2;
  text-decoration: underline;
}

/* 响应式设计 */
@media (max-width: 640px) {
  .find-password-card {
    padding: 36px 28px;
    border-radius: 20px;
  }

  .find-password-title {
    font-size: 24px;
  }

  .welcome-text {
    font-size: 14px;
  }

  .circle {
    display: none;
  }
}
</style>
