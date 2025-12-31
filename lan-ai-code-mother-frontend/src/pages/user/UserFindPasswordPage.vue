<template>
  <div class="find-password-container">
    <div class="find-password-card">
      <h1 class="find-password-title">找回密码</h1>
      <p class="welcome-text">
        忘记密码？<br />
        别担心，我们会通过邮箱为您找回密码。
        <br />请按照指引，重设您的专属密码。
      </p>

      <!-- 第一步：发送验证码 -->
      <div v-if="step === 1" class="step-content">
        <a-form
          :model="findPasswordForm"
          autocomplete="off"
          class="find-password-form"
          name="findPassword"
          @finish="handleSendCode"
        >
          <a-form-item :rules="[{ required: true, message: '请输入账号' }]" name="userAccount">
            <a-input
              v-model:value="findPasswordForm.userAccount"
              class="find-password-input"
              placeholder="请输入账号"
              size="large"
            >
              <template #prefix>
                <UserOutlined />
              </template>
            </a-input>
          </a-form-item>

          <a-form-item
            :rules="[
              { required: true, message: '请输入邮箱地址' },
              { type: 'email', message: '请输入正确的邮箱格式' },
            ]"
            name="email"
          >
            <a-input
              v-model:value="findPasswordForm.email"
              class="find-password-input"
              placeholder="请输入邮箱地址"
              size="large"
            >
              <template #prefix>
                <MailOutlined />
              </template>
            </a-input>
          </a-form-item>

          <a-form-item>
            <a-button
              :loading="sendCodeLoading"
              block
              class="find-password-button"
              html-type="submit"
              size="large"
              type="primary"
            >
              发送验证码
            </a-button>
          </a-form-item>
        </a-form>
      </div>

      <!-- 第二步：重置密码 -->
      <div v-if="step === 2" class="step-content">
        <a-form
          :model="resetPasswordForm"
          autocomplete="off"
          class="find-password-form"
          name="resetPassword"
          @finish="handleResetPassword"
        >
          <a-form-item>
            <a-alert
              :closable="false"
              description="验证码已发送到您的邮箱，请查收（有效期5分钟）"
              message="验证码已发送"
              show-icon
              type="success"
            />
          </a-form-item>

          <a-form-item :rules="[{ required: true, message: '请输入验证码' }]" name="code">
            <a-input
              v-model:value="resetPasswordForm.code"
              class="find-password-input"
              placeholder="请输入验证码"
              size="large"
            >
              <template #prefix>
                <SafetyOutlined />
              </template>
            </a-input>
          </a-form-item>

          <a-form-item
            :rules="[
              { required: true, message: '请输入新密码' },
              { min: 8, message: '密码不能小于 8 位' },
            ]"
            name="newPassword"
          >
            <a-input-password
              v-model:value="resetPasswordForm.newPassword"
              class="find-password-input"
              placeholder="请输入新密码"
              size="large"
            >
              <template #prefix>
                <LockOutlined />
              </template>
            </a-input-password>
          </a-form-item>

          <a-form-item
            :rules="[
              { required: true, message: '请确认新密码' },
              { min: 8, message: '密码不能小于 8 位' },
              { validator: validateCheckPassword },
            ]"
            name="checkPassword"
          >
            <a-input-password
              v-model:value="resetPasswordForm.checkPassword"
              class="find-password-input"
              placeholder="请确认新密码"
              size="large"
            >
              <template #prefix>
                <LockOutlined />
              </template>
            </a-input-password>
          </a-form-item>

          <a-form-item>
            <a-button
              :loading="resetLoading"
              block
              class="find-password-button"
              html-type="submit"
              size="large"
              type="primary"
            >
              重置密码
            </a-button>
          </a-form-item>

          <a-form-item>
            <a-button block class="back-button" size="large" @click="handleBack">
              返回上一步
            </a-button>
          </a-form-item>
        </a-form>
      </div>

      <div class="login-link">
        <RouterLink to="/user/login">返回登录</RouterLink>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { findPassword, resetPassword } from '@/api/userController'
import { useRouter } from 'vue-router'
import { LockOutlined, MailOutlined, SafetyOutlined, UserOutlined } from '@ant-design/icons-vue'

const router = useRouter()
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
const validateCheckPassword = (rule: unknown, value: string, callback: (error?: Error) => void) => {
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
      message.success('验证码已发送到您的邮箱，请查收')
      // 保存账号和邮箱到重置密码表单
      resetPasswordForm.userAccount = values.userAccount || ''
      resetPasswordForm.email = values.email || ''
      // 进入下一步
      step.value = 2
    } else {
      message.error('发送失败，' + res.data.message)
    }
  } catch (error) {
    message.error('发送失败，请检查网络连接')
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
      message.success('密码重置成功，请使用新密码登录')
      // 跳转到登录页面
      router.push({
        path: '/user/login',
        replace: true,
      })
    } else {
      message.error('重置失败，' + res.data.message)
    }
  } catch (error) {
    message.error('重置失败，请检查网络连接')
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

<style scoped>
.find-password-container {
  min-height: calc(100vh - 64px);
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(to bottom right, #a8b5ff 0%, #6b46c1 100%);
  padding: 40px 20px;
  position: relative;
  overflow: hidden;
}

.find-password-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 48px 40px;
  width: 100%;
  max-width: 440px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.25);
  position: relative;
  z-index: 1;
}

.find-password-title {
  font-size: 28px;
  font-weight: 600;
  color: #5b21b6;
  text-align: center;
  margin: 0 0 16px 0;
  letter-spacing: 0.5px;
}

.welcome-text {
  text-align: center;
  color: #5caef6;
  font-size: 14px;
  margin: 0 0 32px 0;
  line-height: 1.6;
}

.step-content {
  margin-top: 0;
}

.find-password-form {
  margin-top: 0;
}

.find-password-input :deep(.ant-input),
.find-password-input :deep(.ant-input-password) {
  border-radius: 8px;
  height: 48px;
  font-size: 15px;
  padding-left: 40px;
  border-color: #d9d9d9;
  background: #ffffff;
  transition: all 0.3s;
}

.find-password-input :deep(.ant-input:hover),
.find-password-input :deep(.ant-input-password:hover) {
  border-color: #667eea;
  background: #ffffff !important;
}

.find-password-input :deep(.ant-input:focus),
.find-password-input :deep(.ant-input-focused),
.find-password-input :deep(.ant-input-password:focus) {
  border-color: #667eea;
  background: #ffffff !important;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
}

.find-password-input :deep(.ant-input-prefix) {
  left: 14px;
  color: #d4af37;
  font-size: 16px;
}

.find-password-input :deep(.ant-input-password-icon) {
  color: #999;
  font-size: 16px;
  cursor: pointer;
  transition: color 0.3s;
}

.find-password-input :deep(.ant-input-password-icon:hover) {
  color: #667eea;
}

.find-password-button {
  height: 48px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  margin-top: 8px;
  transition: all 0.3s ease;
}

.find-password-button:hover {
  background: linear-gradient(135deg, #5568d3 0%, #6a3d91 100%);
  transform: translateY(-1px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.find-password-button:active {
  transform: translateY(0);
}

.find-password-button :deep(.ant-btn-loading-icon) {
  margin-right: 8px;
}

.back-button {
  height: 48px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  background: #ffffff;
  border: 1px solid #d9d9d9;
  color: #666;
  margin-top: 8px;
  transition: all 0.3s ease;
}

.back-button:hover {
  border-color: #667eea;
  color: #667eea;
  background: #f5f5f5;
}

.login-link {
  text-align: center;
  color: #666;
  font-size: 14px;
  margin-top: 24px;
}

.login-link a {
  color: #1890ff;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s ease;
}

.login-link a:hover {
  color: #40a9ff;
  text-decoration: underline;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .find-password-card {
    padding: 32px 24px;
    border-radius: 12px;
  }

  .find-password-title {
    font-size: 24px;
  }

  .welcome-text {
    font-size: 13px;
  }
}
</style>
