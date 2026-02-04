<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import type { UploadProps } from 'ant-design-vue'
import { message } from 'ant-design-vue'
import {
  ArrowLeftOutlined,
  EditOutlined,
  GiftOutlined,
  LockOutlined,
  SafetyOutlined,
  UploadOutlined,
  UserOutlined,
} from '@ant-design/icons-vue'
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

// 用户角色标签
const userRoleLabel = computed(() => {
  const role = loginUserStore.loginUser.userRole
  if (role === 'admin') return '管理员'
  if (role === 'user') return '普通用户'
  return '未知角色'
})

// 创建时间标签
const createTimeLabel = computed(() => {
  if (!loginUserStore.loginUser.createTime) return '创建时间：未知'
  return `创建时间：${dayjs(loginUserStore.loginUser.createTime).format('YYYY-MM-DD')}`
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
    const res = await uploadUserAvatar({} as API.uploadUserAvatarParams, {
      data: formData,
    })
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
  } catch {
    message.error('密码修改失败，请检查网络连接')
  } finally {
    passwordSubmitting.value = false
  }
}

const goBack = () => {
  router.push('/user/profile')
}

// 截取文本
const truncateText = (text: string, maxLength: number) => {
  if (!text) return ''
  if (text.length <= maxLength) return text
  return text.substring(0, maxLength) + '...'
}
</script>

<template>
  <div class="edit-profile-wrapper">
    <!-- 不对称网格布局 -->
    <div class="asym-grid">
      <!-- 左侧栏（窄） -->
      <div class="left-sidebar">
        <!-- 返回按钮 -->
        <div class="back-section">
          <a-button class="back-button" size="large" @click="goBack">
            <ArrowLeftOutlined />
            返回个人中心
          </a-button>
        </div>

        <!-- 用户资料卡片（樱花粉玻璃拟态） -->
        <section class="profile-card-mini sakura-glass">
          <div class="avatar-section">
            <a-avatar :size="64" :src="displayAvatar" class="user-avatar" />
            <div class="avatar-upload">
              <a-upload
                :before-upload="handleAvatarUpload"
                :show-upload-list="false"
                accept="image/*"
              >
                <a-button :loading="avatarUploading" size="small" type="text">
                  <UploadOutlined />
                  更换
                </a-button>
              </a-upload>
            </div>
          </div>
          <div class="user-info-mini">
            <h3 class="user-name">{{ truncateText(formState.userName || '未设置', 10) }}</h3>
            <p class="user-account">@{{ formState.userAccount }}</p>
            <p class="user-email">{{ loginUserStore.loginUser.userEmail || '未绑定邮箱' }}</p>
            <div class="user-role-badge">
              <span class="role-tag">{{ userRoleLabel }}</span>
            </div>
            <div class="user-stats-mini">
              <div class="stat-mini">
                <GiftOutlined />
                <span>{{ accountInfo?.availablePoints ?? 0 }}</span>
              </div>
              <div class="stat-mini">
                <UserOutlined />
                <span>{{ joinedDays }}天</span>
              </div>
            </div>
            <div class="create-time-mini">
              <p>{{ createTimeLabel }}</p>
            </div>
          </div>
        </section>

        <!-- 安全设置卡片 -->
        <section class="security-card">
          <div class="card-header">
            <SafetyOutlined class="card-icon" />
            <h3>安全设置</h3>
          </div>
          <div class="security-list">
            <div class="security-item-compact">
              <div class="security-info">
                <LockOutlined class="security-icon" />
                <div>
                  <h5>登录密码</h5>
                  <p>定期修改密码保护账户安全</p>
                </div>
              </div>
              <a-button class="change-password-btn" size="small" @click="openPasswordModal">
                修改
              </a-button>
            </div>

            <!--            <div class="security-item-compact security-item-spaced">-->
            <!--              <div class="security-info">-->
            <!--                <MailOutlined class="security-icon" />-->
            <!--                <div>-->
            <!--                  <h5>绑定邮箱</h5>-->
            <!--                  <p>绑定邮箱以接收重要通知</p>-->
            <!--                </div>-->
            <!--              </div>-->
            <!--              <a-button class="change-password-btn" size="small"> 绑定 </a-button>-->
            <!--            </div>-->
          </div>
        </section>
      </div>

      <!-- 右侧栏（宽） -->
      <div class="right-main">
        <!-- Hero 区域 -->
        <section class="edit-hero">
          <div class="hero-content">
            <p class="eyebrow">EDIT PROFILE</p>
            <h2>编辑资料</h2>
            <p class="subtitle">修改您的个人信息和账户设置</p>
          </div>
        </section>

        <!-- 基本信息卡片（非对称两栏布局） -->
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
            <!-- 账户信息区域（两栏非对称） -->
            <div class="account-info-section">
              <a-form-item
                :rules="[{ required: true, message: '请输入用户名' }]"
                class="form-item-name"
                label="用户名"
                name="userName"
              >
                <a-input
                  v-model:value="formState.userName"
                  placeholder="请输入用户名"
                  size="large"
                />
              </a-form-item>

              <a-form-item class="form-item-account" label="账号">
                <a-input
                  :value="formState.userAccount"
                  disabled
                  placeholder="系统账号"
                  size="large"
                />
              </a-form-item>
            </div>

            <!-- 个人简介（全宽） -->
            <a-form-item class="form-item-bio" label="个人简介" name="userProfile">
              <a-textarea
                v-model:value="formState.userProfile"
                :maxlength="200"
                :rows="4"
                placeholder="介绍一下自己，内容会显示在个人名片中"
                show-count
              />
            </a-form-item>

            <a-form-item class="form-actions">
              <a-button :loading="submitting" html-type="submit" size="large" type="primary">
                保存修改
              </a-button>
            </a-form-item>
          </a-form>
        </section>
      </div>
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
            >
              确认修改
            </a-button>
          </div>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

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
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@300;400;500;600;700;800&display=swap');

/* ========== 主容器 ========== */
.edit-profile-wrapper {
  max-width: 1400px;
  margin: 0 auto;
  padding: 32px 24px 64px;
  font-family: var(--font-main);
  text-align: left;
  transform: translateZ(0);
  backface-visibility: hidden;
}

/* ========== 不对称网格布局 ========== */
.asym-grid {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 32px;
  align-items: start;
  transform: translateZ(0);
  backface-visibility: hidden;
}

/* ========== 左侧栏（窄） ========== */
.left-sidebar {
  display: flex;
  flex-direction: column;
  gap: 24px;
  position: sticky;
  top: 100px;
  transform: translateZ(0);
  backface-visibility: hidden;
}

.back-section {
  margin-bottom: 8px;
}

.back-button {
  width: 100%;
  height: 44px;
  font-size: 15px;
  font-weight: 500;
  border-radius: var(--radius-sm);
  background: var(--color-glass);
  backdrop-filter: blur(20px);
  border: 1px solid var(--color-glass-border);
  color: var(--color-text);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  transform: translateZ(0);
  text-align: left;
}

.back-button:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
  transform: translate3d(2px, 0, 0);
  box-shadow: var(--shadow-soft);
}

/* ========== 樱花粉玻璃拟态 ========== */
.sakura-glass {
  background: linear-gradient(
    135deg,
    rgba(255, 228, 225, 0.85) 0%,
    rgba(255, 183, 197, 0.75) 100%
  ) !important;
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 183, 197, 0.3) !important;
  box-shadow: 0 8px 32px rgba(255, 183, 197, 0.2) !important;
}

/* 用户资料卡片（迷你） */
.profile-card-mini {
  background: linear-gradient(135deg, rgba(255, 228, 225, 0.85) 0%, rgba(255, 183, 197, 0.75) 100%);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: var(--radius-lg);
  padding: 24px;
  box-shadow: 0 8px 32px rgba(255, 183, 197, 0.2);
  border: 1px solid rgba(255, 183, 197, 0.3);
  color: var(--color-text);
  transform: translateZ(0);
  backface-visibility: hidden;
  position: relative;
  overflow: hidden;
}

/* 樱花瓣装饰效果 */
.profile-card-mini.sakura-glass::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -30%;
  width: 200px;
  height: 200px;
  background: radial-gradient(circle, rgba(255, 192, 203, 0.3) 0%, transparent 70%);
  border-radius: 50%;
  pointer-events: none;
  z-index: 0;
}

.profile-card-mini.sakura-glass > * {
  position: relative;
  z-index: 1;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.user-avatar {
  border: 3px solid rgba(255, 183, 197, 0.4);
  background: rgba(255, 255, 255, 0.5);
}

.avatar-upload :deep(.ant-btn) {
  background: rgba(255, 107, 107, 0.1);
  border-color: rgba(255, 107, 107, 0.2);
  color: var(--color-primary);
  border-radius: var(--radius-sm);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  transform: translateZ(0);
}

.avatar-upload :deep(.ant-btn:hover) {
  background: rgba(255, 107, 107, 0.15);
  border-color: rgba(255, 107, 107, 0.3);
  transform: translate3d(0, -1px, 0);
}

.user-info-mini {
  text-align: center;
}

.user-info-mini .user-name {
  margin: 0 0 8px;
  font-size: 18px;
  font-weight: 700;
  color: var(--color-text);
  line-height: 1.3;
}

.user-info-mini .user-account {
  margin: 0 0 8px;
  font-size: 13px;
  color: var(--color-text-secondary);
  font-weight: 500;
}

.user-info-mini .user-email {
  margin: 0 0 12px;
  font-size: 12px;
  color: var(--color-text-secondary);
  font-weight: 400;
  word-break: break-all;
  opacity: 0.85;
}

/* 用户角色徽章 */
.user-role-badge {
  margin-bottom: 16px;
  display: flex;
  justify-content: center;
}

.role-tag {
  display: inline-flex;
  align-items: center;
  padding: 4px 12px;
  background: linear-gradient(135deg, rgba(255, 107, 107, 0.15) 0%, rgba(255, 168, 168, 0.15) 100%);
  border: 1px solid rgba(255, 107, 107, 0.25);
  border-radius: var(--radius-sm);
  font-size: 12px;
  font-weight: 600;
  color: var(--color-primary);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  transform: translateZ(0);
}

.role-tag:hover {
  background: linear-gradient(135deg, rgba(255, 107, 107, 0.2) 0%, rgba(255, 168, 168, 0.2) 100%);
  transform: translate3d(0, -1px, 0);
}

/* 创建时间 */
.create-time-mini {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid rgba(255, 183, 197, 0.25);
}

.create-time-mini p {
  margin: 0;
  font-size: 11px;
  color: var(--color-text-secondary);
  font-weight: 400;
  letter-spacing: 0.3px;
}

.user-stats-mini {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.stat-mini {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 500;
  color: var(--color-text);
  opacity: 0.9;
}

/* 安全设置卡片 */
.security-card {
  background: var(--color-glass);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: var(--radius-lg);
  padding: 24px;
  box-shadow: var(--shadow-soft);
  border: 1px solid var(--color-glass-border);
  transform: translateZ(0);
  backface-visibility: hidden;
}

.security-card .card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(255, 107, 107, 0.15);
}

.security-card .card-icon {
  font-size: 20px;
  color: var(--color-primary);
}

.security-card .card-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text);
}

.security-list {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.security-item-compact {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 12px;
  background: rgba(255, 107, 107, 0.05);
  border-radius: var(--radius-sm);
  border: 1px solid rgba(255, 107, 107, 0.1);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.security-item-compact:hover {
  background: rgba(255, 107, 107, 0.08);
  border-color: rgba(255, 107, 107, 0.2);
}

/* 安全项间距 */
.security-item-spaced {
  margin-top: 16px;
}

.security-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.security-icon {
  font-size: 18px;
  color: var(--color-primary);
  flex-shrink: 0;
}

.security-info h5 {
  margin: 0 0 2px;
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text);
}

.security-info p {
  margin: 0;
  font-size: 12px;
  color: var(--color-text-secondary);
}

.change-password-btn {
  flex-shrink: 0;
  border-radius: var(--radius-sm);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  transform: translateZ(0);
}

.change-password-btn:hover {
  transform: translate3d(0, -1px, 0);
}

/* ========== 右侧栏（宽） ========== */
.right-main {
  display: flex;
  flex-direction: column;
  gap: 24px;
  transform: translateZ(0);
  backface-visibility: hidden;
}

/* Hero 区域 */
.edit-hero {
  background: var(--color-glass);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: var(--radius-lg);
  padding: 32px 40px;
  box-shadow: var(--shadow-soft);
  border: 1px solid var(--color-glass-border);
  text-align: left;
  transform: translateZ(0);
  backface-visibility: hidden;
}

.eyebrow {
  letter-spacing: 0.4em;
  font-size: 12px;
  color: var(--color-primary);
  margin-bottom: 8px;
  font-weight: 600;
  text-transform: uppercase;
}

.edit-hero h2 {
  margin: 0 0 12px;
  font-size: 32px;
  color: var(--color-text);
  font-weight: 700;
}

.subtitle {
  margin: 0;
  color: var(--color-text-secondary);
  font-size: 15px;
}

/* 信息卡片 */
.info-card {
  background: var(--color-glass);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: var(--radius-lg);
  padding: 32px 40px;
  box-shadow: var(--shadow-soft);
  border: 1px solid var(--color-glass-border);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  text-align: left;
  transform: translateZ(0);
  backface-visibility: hidden;
}

.info-card:hover {
  box-shadow: var(--shadow-hover);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 28px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(255, 107, 107, 0.15);
}

.card-icon {
  font-size: 24px;
  color: var(--color-primary);
}

.card-header h3 {
  margin: 0;
  font-size: 22px;
  font-weight: 600;
  color: var(--color-text);
}

.edit-form {
  padding: 0;
}

/* ========== 非对称表单布局 ========== */
.account-info-section {
  display: grid;
  grid-template-columns: 1.2fr 0.8fr;
  gap: 24px;
  margin-bottom: 24px;
}

.form-item-name {
  margin-bottom: 0;
}

.form-item-account {
  margin-bottom: 0;
}

.form-item-bio {
  margin-bottom: 32px;
}

/* 账号输入框禁用样式 */
.form-item-account :deep(.ant-input:disabled) {
  background: rgba(255, 107, 107, 0.05);
  border-color: rgba(255, 107, 107, 0.15);
  color: var(--color-text-secondary);
  cursor: not-allowed;
}

.form-item-account :deep(.ant-input:disabled:hover) {
  border-color: rgba(255, 107, 107, 0.15);
}

.edit-form :deep(.ant-input),
.edit-form :deep(.ant-input-textarea) {
  border-radius: var(--radius-sm);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  transform: translateZ(0);
}

.edit-form :deep(.ant-input:focus),
.edit-form :deep(.ant-input-textarea:focus) {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 2px rgba(255, 107, 107, 0.1);
}

.edit-form :deep(.ant-form-item) {
  margin-bottom: 24px;
}

.edit-form :deep(.ant-form-item-label) {
  text-align: left;
}

.form-actions {
  margin-top: 32px;
  margin-bottom: 0;
  display: flex;
  gap: 12px;
  justify-content: flex-start;
}

.form-actions :deep(.ant-btn) {
  border-radius: var(--radius-sm);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  transform: translateZ(0);
}

.form-actions :deep(.ant-btn-primary) {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  border: none;
}

.form-actions :deep(.ant-btn-primary:hover) {
  transform: translate3d(0, -2px, 0);
  box-shadow: 0 6px 20px rgba(255, 107, 107, 0.3);
}

/* ========== 修改密码弹窗 ========== */
.password-modal :deep(.ant-modal-content) {
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-hover);
}

.password-modal :deep(.ant-modal-body) {
  padding: 0;
}

.modal-header {
  padding: 32px 32px 24px;
  background: var(--color-glass);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 107, 107, 0.15);
  text-align: left;
}

.modal-header h3 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: var(--color-text);
}

.modal-subtitle {
  margin: 0;
  font-size: 14px;
  color: var(--color-text-secondary);
}

.password-form {
  padding: 32px;
  text-align: left;
}

.password-input :deep(.ant-input),
.password-input :deep(.ant-input-password) {
  border-radius: var(--radius-sm);
  height: 48px;
  font-size: 14px;
  padding-left: 40px;
  border-color: rgba(255, 107, 107, 0.2);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  transform: translateZ(0);
}

.password-input :deep(.ant-input:hover),
.password-input :deep(.ant-input-password:hover) {
  border-color: var(--color-primary);
}

.password-input :deep(.ant-input:focus),
.password-input :deep(.ant-input-focused),
.password-input :deep(.ant-input-password:focus) {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 2px rgba(255, 107, 107, 0.1);
}

.password-input :deep(.ant-input-prefix) {
  left: 14px;
  color: var(--color-primary);
  font-size: 16px;
}

.password-input :deep(.ant-input-password-icon) {
  color: var(--color-text-light);
  font-size: 16px;
  cursor: pointer;
  transition: color 0.3s;
}

.password-input :deep(.ant-input-password-icon:hover) {
  color: var(--color-primary);
}

.password-form-actions {
  margin-top: 32px;
  margin-bottom: 0;
}

.button-group {
  display: flex;
  gap: 12px;
  width: 100%;
  justify-content: flex-end;
}

.cancel-button {
  flex: 0;
  height: 48px;
  padding: 0 24px;
  border-radius: var(--radius-sm);
  font-size: 16px;
  font-weight: 500;
  background: white;
  border: 1px solid rgba(255, 107, 107, 0.2);
  color: var(--color-text-secondary);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.cancel-button:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
  background: rgba(255, 107, 107, 0.05);
}

.submit-button {
  flex: 0;
  height: 48px;
  padding: 0 32px;
  border-radius: var(--radius-sm);
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  border: none;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.submit-button:hover {
  transform: translate3d(0, -1px, 0);
  box-shadow: 0 8px 20px rgba(255, 107, 107, 0.4);
}

.submit-button:active {
  transform: translate3d(0, 0, 0);
}

/* ========== 响应式设计 ========== */
@media (max-width: 1024px) {
  .edit-profile-wrapper {
    padding: 24px 16px 48px;
  }

  .asym-grid {
    grid-template-columns: 280px 1fr;
    gap: 24px;
  }

  .left-sidebar {
    position: static;
  }

  .edit-hero {
    padding: 28px 32px;
  }

  .edit-hero h2 {
    font-size: 28px;
  }

  .info-card {
    padding: 28px 32px;
  }

  .account-info-section {
    grid-template-columns: 1fr 1fr;
    gap: 20px;
  }
}

@media (max-width: 768px) {
  .asym-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .left-sidebar {
    position: static;
  }

  .back-button {
    width: 100%;
  }

  .profile-card-mini {
    padding: 20px;
  }

  .edit-hero {
    padding: 24px;
  }

  .edit-hero h2 {
    font-size: 24px;
  }

  .info-card {
    padding: 24px;
  }

  .card-header {
    margin-bottom: 20px;
    padding-bottom: 14px;
  }

  .security-item-compact {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .change-password-btn {
    width: 100%;
  }

  /* 表单响应式 */
  .account-info-section {
    grid-template-columns: 1fr;
    gap: 0;
  }

  .form-item-name,
  .form-item-account {
    margin-bottom: 24px;
  }

  .form-actions {
    flex-direction: column;
  }

  .form-actions :deep(.ant-btn) {
    width: 100%;
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
