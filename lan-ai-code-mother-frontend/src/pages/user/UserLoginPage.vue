<template>
  <div class="login-container">
    <div class="login-card">
      <h1 class="login-title">用户登录</h1>
      <p class="welcome-text">让想法，瞬间成为可用的应用！</p>

      <a-form
        :model="formState"
        autocomplete="off"
        class="login-form"
        name="login"
        @finish="handleSubmit"
      >
        <a-form-item :rules="[{ required: true, message: '请输入账号' }]" name="userAccount">
          <a-input
            v-model:value="formState.userAccount"
            class="login-input"
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
              <LockOutlined />
            </template>
          </a-input-password>
        </a-form-item>

        <a-form-item>
          <a-button
            :loading="loading"
            block
            class="login-button"
            html-type="submit"
            size="large"
            type="primary"
          >
            登录
          </a-button>
        </a-form-item>
      </a-form>

      <div class="register-link">
        没有账号?
        <RouterLink to="/user/register">立即注册</RouterLink>
        <span class="link-divider">|</span>
        <RouterLink to="/user/find-password">找回密码</RouterLink>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { userLogin } from '@/api/userController.ts'
import { useLoginUserStore } from '@/stores/loginUser.ts'
import { useRouter } from 'vue-router'
import { LockOutlined, UserOutlined } from '@ant-design/icons-vue'

const loading = ref(false)
const formState = reactive<API.UserLoginRequest>({
  userAccount: '',
  userPassword: '',
})

const router = useRouter()
const loginUserStore = useLoginUserStore()

/**
 * 提交表单
 * @param values
 */
const handleSubmit = async (values: any) => {
  loading.value = true
  try {
    const res = await userLogin(values)
    // 登录成功，把登录态保存到全局状态中
    if (res.data.code === 0 && res.data.data) {
      await loginUserStore.fetchLoginUser()
      message.success('登录成功')
      router.push({
        path: '/',
        replace: true,
      })
    } else {
      message.error('登录失败，' + res.data.message)
    }
  } catch (error) {
    message.error('登录失败，请检查网络连接')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: calc(100vh - 64px);
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(to bottom right, #a8b5ff 0%, #6b46c1 100%);
  padding: 40px 20px;
  position: relative;
  overflow: hidden;
}

.login-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 48px 40px;
  width: 100%;
  max-width: 440px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.25);
  position: relative;
  z-index: 1;
}

.login-title {
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

.login-form {
  margin-top: 0;
}

.login-input :deep(.ant-input),
.login-input :deep(.ant-input-password) {
  border-radius: 8px;
  height: 48px;
  font-size: 15px;
  padding-left: 40px;
  border-color: #d9d9d9;
  background: #ffffff;
  transition: all 0.3s;
}

.login-input :deep(.ant-input:hover),
.login-input :deep(.ant-input-password:hover) {
  border-color: #667eea;
  background: #ffffff !important;
}

.login-input :deep(.ant-input:focus),
.login-input :deep(.ant-input-focused),
.login-input :deep(.ant-input-password:focus) {
  border-color: #667eea;
  background: #ffffff !important;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
}

.login-input :deep(.ant-input-prefix) {
  left: 14px;
  color: #d4af37;
  font-size: 16px;
}

.login-input :deep(.ant-input-password-icon) {
  color: #999;
  font-size: 16px;
  cursor: pointer;
  transition: color 0.3s;
}

.login-input :deep(.ant-input-password-icon:hover) {
  color: #667eea;
}

.login-button {
  height: 48px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  margin-top: 8px;
  transition: all 0.3s ease;
}

.login-button:hover {
  background: linear-gradient(135deg, #5568d3 0%, #6a3d91 100%);
  transform: translateY(-1px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.login-button:active {
  transform: translateY(0);
}

.login-button :deep(.ant-btn-loading-icon) {
  margin-right: 8px;
}

.register-link {
  text-align: center;
  color: #666;
  font-size: 14px;
  margin-top: 24px;
}

.register-link a {
  color: #1890ff;
  text-decoration: none;
  font-weight: 500;
  margin-left: 4px;
  transition: color 0.3s ease;
}

.register-link a:hover {
  color: #40a9ff;
  text-decoration: underline;
}

.link-divider {
  margin: 0 8px;
  color: #d9d9d9;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-card {
    padding: 32px 24px;
    border-radius: 12px;
  }

  .login-title {
    font-size: 24px;
  }

  .welcome-text {
    font-size: 13px;
  }
}
</style>
