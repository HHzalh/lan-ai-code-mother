<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  ArrowLeftOutlined,
  EditOutlined,
  GiftOutlined,
  LockOutlined,
  MailOutlined,
  SafetyOutlined,
  UploadOutlined,
  UserOutlined,
} from '@ant-design/icons-vue'
import type { UploadProps } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/loginUser'
import { changePassword, updateUserInfo, uploadUserAvatar } from '@/api/userController'
import { getMyAccount } from '@/api/pointController'
import dayjs from 'dayjs'

const router = useRouter()
const loginUserStore = useLoginUserStore()
const submitting = ref(false)
const avatarUploading = ref(false)
const passwordSubmitting = ref(false)
const showPasswordModal = ref(false)

// 积分相关
const accountInfo = ref<API.UserAccountVO | null>(null)

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

// 计算已加入天数
const joinedDays = computed(() => {
  if (!loginUserStore.loginUser.createTime) return 0
  return dayjs().diff(dayjs(loginUserStore.loginUser.createTime), 'day')
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
  loadAccountInfo()
})

// 加载积分账户信息
const loadAccountInfo = async () => {
  try {
    const res = await getMyAccount()
    if (res.data.code === 0 && res.data.data) {
      accountInfo.value = res.data.data
    }
  } catch (error) {
    console.error('加载积分账户失败：', error)
  }
}

const handleAvatarUpload: UploadProps['beforeUpload'] = async (file) => {
  avatarUploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', file)
    const res = await uploadUserAvatar(
      {} as API.uploadUserAvatarParams,
      {
        data: formData,
      },
    )
    if (res.data.code === 0 && res.data.data) {
      formState.userAvatar = res.data.data
      message.success('头像已上传')
      await loginUserStore.fetchLoginUser()
    } else {
      message.error(res.data.message ?? '上传失败，请重试')
    }
  } catch (error: any) {
    message.error(error?.response?.data?.message ?? '上传失败，请重试')
  } finally {
    avatarUploading.value = false
  }
  return false
}

const handleSubmit = async () => {
  submitting.value = true
  try {
    // 只发送 userName 和 userProfile，后端接口只允许更新这两个字段
    const updateData: API.UserUpdateRequest = {
      userName: formState.userName,
      userProfile: formState.userProfile,
    }
    const res = await updateUserInfo(updateData)
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

const goBack = () => {
  router.push('/user/profile')
}
</script>

<template>
  <div class="edit-profile-wrapper">
    <!-- Hero 区域 -->
    <section class="edit-hero">
      <div class="hero-content">
        <p class="eyebrow">EDIT PROFILE</p>
        <h2>编辑资料</h2>
        <p class="subtitle">修改您的个人信息和账户设置</p>
      </div>
    </section>

    <!-- 用户资料卡片 -->
    <section class="profile-card">
      <div class="profile-info">
        <div class="user-avatar-section">
          <a-avatar :size="80" :src="displayAvatar" class="user-avatar" />
          <div class="avatar-upload">
            <a-upload
              :before-upload="handleAvatarUpload"
              :show-upload-list="false"
              accept="image/*"
            >
              <a-button :loading="avatarUploading" size="large" type="primary">
                <UploadOutlined />
                更换头像
              </a-button>
            </a-upload>
          </div>
        </div>
        <div class="user-details">
          <h3 class="user-name">{{ formState.userName || '未设置' }}</h3>
          <p class="user-account">@{{ formState.userAccount }}</p>
          <div class="user-stats">
            <div class="stat-item">
              <GiftOutlined />
              <span>积分 {{ accountInfo?.availablePoints ?? 0 }}</span>
            </div>
            <div class="stat-item">
              <UserOutlined />
              <span>已加入 {{ joinedDays }} 天</span>
            </div>
            <div class="stat-item">
              <MailOutlined />
              <a-tag color="orange" size="small">未绑定邮箱</a-tag>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 基本信息卡片 -->
    <section class="info-card">
      <div class="card-header">
        <EditOutlined class="card-icon" />
        <h3>基本信息</h3>
      </div>
      <a-form
        :model="formState"
        autocomplete="off"
        class="edit-form"
        name="editProfile"
        @finish="handleSubmit"
      >
        <a-form-item
          :rules="[{ required: true, message: '请输入用户名' }]"
          label="用户名"
          name="userName"
        >
          <a-input v-model:value="formState.userName" placeholder="请输入用户名" size="large" />
        </a-form-item>
        <a-form-item label="个人简介" name="userProfile">
          <a-textarea
            v-model:value="formState.userProfile"
            :rows="4"
            placeholder="介绍一下自己，内容会显示在个人名片中"
          />
        </a-form-item>
        <a-form-item class="form-actions">
          <a-button :loading="submitting" html-type="submit" size="large" type="primary">
            保存修改
          </a-button>
          <a-button size="large" @click="goBack">取消</a-button>
        </a-form-item>
      </a-form>
    </section>

    <!-- 邮箱管理卡片 -->
    <section class="info-card">
      <div class="card-header">
        <MailOutlined class="card-icon" />
        <h3>邮箱管理</h3>
      </div>
      <div class="email-management">
        <div class="email-status">
          <MailOutlined />
          <span>暂未绑定邮箱</span>
          <a-tag color="orange" size="small">未绑定</a-tag>
        </div>
        <a-button class="bind-email-btn" size="large" type="primary">
          <MailOutlined />
          绑定邮箱
        </a-button>
      </div>
    </section>

    <!-- 安全设置卡片 -->
    <section class="info-card">
      <div class="card-header">
        <SafetyOutlined class="card-icon" />
        <h3>安全设置</h3>
      </div>
      <div class="security-settings">
        <div class="security-item">
          <div class="security-info">
            <h5>登录密码</h5>
            <p>用于登录账户的密码，建议定期修改</p>
          </div>
          <a-button class="change-password-btn" size="large" @click="openPasswordModal">
            <LockOutlined />
            修改密码
          </a-button>
        </div>
      </div>
    </section>

    <!-- 返回按钮 -->
    <div class="back-button-container">
      <a-button class="back-button" size="large" type="primary" @click="goBack">
        <ArrowLeftOutlined />
        返回个人中心
      </a-button>
    </div>

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
            <a-button class="cancel-button" size="large" @click="closePasswordModal">取消</a-button>
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
.edit-profile-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px 0 64px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* Hero 区域 */
.edit-hero {
  background: linear-gradient(120deg, #e0f2ff, #f5f7ff);
  border-radius: 18px;
  padding: 28px 32px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(24, 144, 255, 0.15);
}

.eyebrow {
  letter-spacing: 0.4em;
  font-size: 12px;
  color: #3c92ff;
  margin-bottom: 8px;
}

.edit-hero h2 {
  margin: 0;
  font-size: 28px;
  color: #1f2d3d;
}

.subtitle {
  margin-top: 8px;
  color: #5f6b7c;
}

/* 用户资料卡片 */
.profile-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 18px;
  padding: 32px 40px;
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.3);
  color: white;
}

.profile-info {
  display: flex;
  align-items: center;
  gap: 32px;
}

.user-avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.user-avatar {
  flex-shrink: 0;
  border: 3px solid rgba(255, 255, 255, 0.3);
}

.avatar-upload :deep(.ant-btn) {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.3);
  color: white;
  backdrop-filter: blur(10px);
}

.avatar-upload :deep(.ant-btn:hover) {
  background: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.4);
  color: white;
}

.user-details {
  flex: 1;
}

.user-name {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 600;
  color: white;
}

.user-account {
  margin: 0 0 20px 0;
  font-size: 16px;
  opacity: 0.9;
  color: white;
}

.user-stats {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  opacity: 0.9;
}

/* 信息卡片 */
.info-card {
  background: #fff;
  border-radius: 18px;
  padding: 32px 40px;
  box-shadow: 0 12px 35px rgba(15, 39, 80, 0.07);
  border: 1px solid #f0f2f5;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f2f5;
}

.card-icon {
  font-size: 24px;
  color: #1890ff;
}

.card-header h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #1f2d3d;
}

.edit-form {
  padding: 0;
}

.form-actions {
  margin-top: 32px;
  margin-bottom: 0;
  display: flex;
  gap: 12px;
}

.email-management {
  padding: 20px;
  background: #fafafa;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.email-status {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 15px;
  color: #5f6b7c;
}

.bind-email-btn {
  border-radius: 8px;
}

.security-settings {
  padding: 20px;
  background: #fafafa;
  border-radius: 12px;
}

.security-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.security-info h5 {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 600;
  color: #1f2d3d;
}

.security-info p {
  margin: 0;
  font-size: 14px;
  color: #5f6b7c;
}

.change-password-btn {
  border-radius: 8px;
}

/* 返回按钮 */
.back-button-container {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

.back-button {
  height: 48px;
  padding: 0 32px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.back-button:hover {
  background: linear-gradient(135deg, #5568d3 0%, #6a3d91 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

/* 修改密码弹窗 */
.password-modal :deep(.ant-modal-content) {
  border-radius: 16px;
  overflow: hidden;
}

.password-modal :deep(.ant-modal-body) {
  padding: 0;
}

.modal-header {
  padding: 32px 32px 24px;
  background: linear-gradient(120deg, #e0f2ff, #f5f7ff);
  border-bottom: 1px solid #f0f2f5;
}

.modal-header h3 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #1f2d3d;
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
  .info-card,
  .profile-card {
    padding: 24px;
  }

  .profile-info {
    flex-direction: column;
    text-align: center;
  }

  .user-avatar-section {
    width: 100%;
  }

  .user-stats {
    justify-content: center;
  }

  .card-header {
    margin-bottom: 16px;
  }

  .email-management {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .security-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .button-group {
    flex-direction: column;
  }

  .form-actions {
    flex-direction: column;
  }
}
</style>
