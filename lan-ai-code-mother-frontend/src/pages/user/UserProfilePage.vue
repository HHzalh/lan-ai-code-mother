<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message, type UploadProps } from 'ant-design-vue'
import { LockOutlined, SafetyOutlined, UploadOutlined } from '@ant-design/icons-vue'
import { useLoginUserStore } from '@/stores/loginUser'
import { changePassword, updateUser, uploadUserAvatar } from '@/api/userController'

const router = useRouter()
const loginUserStore = useLoginUserStore()
const submitting = ref(false)
const avatarUploading = ref(false)
const passwordSubmitting = ref(false)
const showPasswordModal = ref(false)

const formState = reactive<Partial<API.UserUpdateRequest>>({
  id: undefined,
  userAccount: '',
  userName: '',
  userProfile: '',
  userAvatar: '',
})

const passwordForm = reactive<API.UserChangePasswordRequest>({
  oldPassword: '',
  newPassword: '',
  checkPassword: '',
})

const displayAvatar = computed(() => {
  return formState.userAvatar
})

const initForm = async () => {
  if (!loginUserStore.loginUser.id) {
    await loginUserStore.fetchLoginUser()
  }
  const user = loginUserStore.loginUser
  if (!user?.id) {
    message.warning('请先登录')
    router.push('/user/login')
    return
  }
  Object.assign(formState, {
    id: user.id,
    userAccount: user.userAccount,
    userName: user.userName,
    userProfile: user.userProfile,
    userAvatar: user.userAvatar,
  })
}

onMounted(() => {
  initForm()
})

const toBase64 = (file: File) =>
  new Promise<string>((resolve, reject) => {
    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = () => resolve(reader.result as string)
    reader.onerror = (error) => reject(error)
  })

const handleAvatarUpload: UploadProps['beforeUpload'] = async (file) => {
  avatarUploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', file)
    const res = await uploadUserAvatar(
      { file: '' },
      {
        data: formData,
        params: undefined,
      },
    )
    if (res.data.code === 0 && res.data.data) {
      formState.userAvatar = res.data.data
      message.success('头像已上传')
      await loginUserStore.fetchLoginUser()
    } else {
      message.error(res.data.message ?? '上传失败，请重试')
    }
  } catch (error) {
    message.error('上传失败，请重试')
  } finally {
    avatarUploading.value = false
  }
  return false
}

const handleSubmit = async () => {
  if (!formState.id) {
    message.error('缺少用户 ID，无法更新')
    return
  }
  submitting.value = true
  try {
    const res = await updateUser(formState as API.UserUpdateRequest)
    if (res.data.code === 0) {
      message.success('资料已更新')
      await loginUserStore.fetchLoginUser()
    } else {
      message.error(res.data.message ?? '更新失败')
    }
  } finally {
    submitting.value = false
  }
}

/**
 * 验证确认密码
 */
const validateCheckPassword = (rule: unknown, value: string, callback: (error?: Error) => void) => {
  if (value && value !== passwordForm.newPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

/**
 * 打开修改密码弹窗
 */
const openPasswordModal = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.checkPassword = ''
  showPasswordModal.value = true
}

/**
 * 关闭修改密码弹窗
 */
const closePasswordModal = () => {
  showPasswordModal.value = false
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.checkPassword = ''
}

/**
 * 提交修改密码
 */
const handlePasswordSubmit = async () => {
  passwordSubmitting.value = true
  try {
    const res = await changePassword(passwordForm)
    if (res.data.code === 0) {
      message.success('密码修改成功，请使用新密码登录')
      closePasswordModal()
      // 可以选择退出登录，让用户重新登录
      setTimeout(() => {
        router.push('/user/login')
      }, 1500)
    } else {
      message.error(res.data.message ?? '密码修改失败')
    }
  } catch (error) {
    message.error('密码修改失败，请检查网络连接')
  } finally {
    passwordSubmitting.value = false
  }
}
</script>

<template>
  <div class="profile-wrapper">
    <section class="profile-hero">
      <div class="hero-content">
        <p class="eyebrow">PROFILE</p>
        <h2>个人资料中心</h2>
        <p class="subtitle">让世界看到更棒的你</p>
      </div>
      <div class="hero-divider"></div>
      <div class="hero-avatar">
        <a-avatar :size="96" :src="displayAvatar" />
        <p class="avatar-tip">{{ formState.userName || '无名' }}</p>
      </div>
    </section>

    <section class="profile-card">
      <div class="card-header">
        <span class="line"></span>
        <h3>基础信息</h3>
      </div>
      <a-form
        :label-col="{ span: 5 }"
        :model="formState"
        :wrapper-col="{ span: 19 }"
        label-align="left"
        @finish="handleSubmit"
      >
        <a-form-item label="账号">
          <a-input v-model:value="formState.userAccount" disabled />
        </a-form-item>
        <a-form-item
          :rules="[{ required: true, message: '请输入昵称' }]"
          label="昵称"
          name="userName"
        >
          <a-input v-model:value="formState.userName" placeholder="请输入昵称" />
        </a-form-item>
        <a-form-item label="头像">
          <div class="avatar-uploader">
            <a-avatar :size="80" :src="displayAvatar" />
            <a-upload
              :before-upload="handleAvatarUpload"
              :show-upload-list="false"
              accept="image/*"
            >
              <a-button :loading="avatarUploading" type="default">
                <UploadOutlined />
                重新上传
              </a-button>
            </a-upload>
          </div>
        </a-form-item>
        <a-form-item label="个人简介" name="userProfile">
          <a-textarea
            v-model:value="formState.userProfile"
            :rows="4"
            placeholder="介绍一下自己，内容会显示在个人名片中"
          />
        </a-form-item>
        <a-form-item :wrapper-col="{ span: 19, offset: 5 }" class="form-actions">
          <a-button :loading="submitting" html-type="submit" type="primary">保存修改</a-button>
        </a-form-item>
      </a-form>
    </section>

    <section class="profile-card password-card">
      <div class="card-header">
        <span class="line"></span>
        <h3>安全设置</h3>
      </div>
      <div class="password-content">
        <div class="password-info">
          <div class="password-icon">
            <LockOutlined />
          </div>
          <div class="password-text">
            <h4>登录密码</h4>
            <p>定期修改密码可以让账号更安全</p>
          </div>
        </div>
        <a-button type="primary" @click="openPasswordModal">
          <SafetyOutlined />
          修改密码
        </a-button>
      </div>
    </section>

    <!-- 修改密码弹窗 -->
    <a-modal
      v-model:open="showPasswordModal"
      :footer="null"
      :title="null"
      class="password-modal"
      width="520px"
      @cancel="closePasswordModal"
    >
      <div class="modal-header">
        <h3>修改密码</h3>
        <p class="modal-subtitle">为了您的账号安全，请定期修改密码</p>
      </div>
      <a-form
        :model="passwordForm"
        autocomplete="off"
        class="password-form"
        name="changePassword"
        @finish="handlePasswordSubmit"
      >
        <a-form-item :rules="[{ required: true, message: '请输入旧密码' }]" name="oldPassword">
          <a-input-password
            v-model:value="passwordForm.oldPassword"
            class="password-input"
            placeholder="请输入旧密码"
            size="large"
          >
            <template #prefix>
              <LockOutlined />
            </template>
          </a-input-password>
        </a-form-item>

        <a-form-item
          :rules="[
            { required: true, message: '请输入新密码' },
            { min: 8, message: '密码不能小于 8 位' },
          ]"
          name="newPassword"
        >
          <a-input-password
            v-model:value="passwordForm.newPassword"
            class="password-input"
            placeholder="请输入新密码（至少8位）"
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
            v-model:value="passwordForm.checkPassword"
            class="password-input"
            placeholder="请确认新密码"
            size="large"
          >
            <template #prefix>
              <LockOutlined />
            </template>
          </a-input-password>
        </a-form-item>

        <a-form-item class="password-form-actions">
          <div class="button-group">
            <a-button class="cancel-button" size="large" @click="closePasswordModal">
              取消
            </a-button>
            <a-button
              :loading="passwordSubmitting"
              class="submit-button"
              html-type="submit"
              size="large"
              type="primary"
            >
              确认修改
            </a-button>
          </div>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<style scoped>
.profile-wrapper {
  max-width: 960px;
  margin: 0 auto;
  padding: 24px 0 64px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.profile-hero {
  background: linear-gradient(120deg, #e0f2ff, #f5f7ff);
  border-radius: 18px;
  padding: 28px 32px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(24, 144, 255, 0.15);
}

.hero-content {
  flex: 1;
}

.eyebrow {
  letter-spacing: 0.4em;
  font-size: 12px;
  color: #3c92ff;
  margin-bottom: 8px;
}

.profile-hero h2 {
  margin: 0;
  font-size: 28px;
  color: #1f2d3d;
}

.subtitle {
  margin-top: 8px;
  color: #5f6b7c;
}

.hero-divider {
  width: 1px;
  height: 80px;
  background: rgba(255, 255, 255, 0.7);
  margin: 0 32px;
}

.hero-avatar {
  text-align: center;
}

.avatar-tip {
  margin-top: 8px;
  color: #4f5969;
}

.profile-card {
  background: #fff;
  border-radius: 18px;
  padding: 32px 40px 40px;
  box-shadow: 0 12px 35px rgba(15, 39, 80, 0.07);
  border: 1px solid #f0f2f5;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 32px;
}

.card-header h3 {
  margin: 0;
  position: relative;
  padding-bottom: 8px;
}

.card-header h3::after {
  content: '';
  position: absolute;
  left: 0;
  bottom: 0;
  width: 100%;
  height: 3px;
  background: linear-gradient(90deg, #1890ff, #6bc1ff);
  border-radius: 2px;
}

.card-header .line {
  display: none;
}

.avatar-uploader {
  display: flex;
  align-items: center;
  gap: 16px;
}

.form-actions {
  margin-top: 24px;
}

.password-card {
  margin-top: 24px;
}

.password-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px 0;
}

.password-info {
  display: flex;
  align-items: center;
  gap: 16px;
  flex: 1;
}

.password-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 20px;
}

.password-text h4 {
  margin: 0 0 4px 0;
  font-size: 16px;
  color: #1f2d3d;
  font-weight: 500;
}

.password-text p {
  margin: 0;
  font-size: 14px;
  color: #5f6b7c;
}

/* 修改密码弹窗样式 */
.password-modal :deep(.ant-modal-content) {
  border-radius: 16px;
  overflow: hidden;
}

.password-modal :deep(.ant-modal-body) {
  padding: 0;
}

.modal-header {
  padding: 32px 32px 24px;
  text-align: center;
  background: linear-gradient(120deg, #e0f2ff, #f5f7ff);
  border-bottom: 1px solid #f0f2f5;
}

.modal-header h3 {
  margin: 0 0 8px 0;
  font-size: 24px;
  color: #1f2d3d;
  font-weight: 600;
}

.modal-subtitle {
  margin: 0;
  font-size: 14px;
  color: #5f6b7c;
}

.password-form {
  padding: 32px;
}

.password-input :deep(.ant-input),
.password-input :deep(.ant-input-password) {
  border-radius: 8px;
  height: 48px;
  font-size: 15px;
  padding-left: 40px;
  border-color: #d9d9d9;
  transition: all 0.3s;
}

.password-input :deep(.ant-input:hover),
.password-input :deep(.ant-input-password:hover) {
  border-color: #667eea;
}

.password-input :deep(.ant-input:focus),
.password-input :deep(.ant-input-focused),
.password-input :deep(.ant-input-password:focus) {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
}

.password-input :deep(.ant-input-prefix) {
  left: 14px;
  color: #667eea;
  font-size: 16px;
}

.password-input :deep(.ant-input-password-icon) {
  color: #999;
  font-size: 16px;
  cursor: pointer;
  transition: color 0.3s;
}

.password-input :deep(.ant-input-password-icon:hover) {
  color: #667eea;
}

.password-form-actions {
  margin-top: 32px;
  margin-bottom: 0;
}

.button-group {
  display: flex;
  gap: 12px;
  width: 100%;
}

.cancel-button {
  flex: 1;
  height: 48px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  background: #ffffff;
  border: 1px solid #d9d9d9;
  color: #666;
  transition: all 0.3s ease;
}

.cancel-button:hover {
  border-color: #667eea;
  color: #667eea;
  background: #f5f5f5;
}

.submit-button {
  flex: 1;
  height: 48px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s ease;
}

.submit-button:hover {
  background: linear-gradient(135deg, #5568d3 0%, #6a3d91 100%);
  transform: translateY(-1px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.submit-button:active {
  transform: translateY(0);
}

@media (max-width: 768px) {
  .profile-hero {
    flex-direction: column;
    gap: 16px;
  }

  .hero-divider {
    width: 60%;
    height: 1px;
    margin: 12px 0;
  }

  .profile-card {
    padding: 24px;
  }

  .password-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .password-form {
    padding: 24px;
  }

  .button-group {
    flex-direction: column;
  }

  .cancel-button,
  .submit-button {
    width: 100%;
  }
}
</style>
