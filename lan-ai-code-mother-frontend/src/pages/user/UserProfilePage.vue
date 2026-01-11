<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  AppstoreOutlined,
  CheckCircleOutlined,
  EditOutlined,
  GiftOutlined,
  HistoryOutlined,
  LockOutlined,
  MailOutlined,
  SafetyOutlined,
  ShoppingOutlined,
  UploadOutlined,
  UserOutlined,
} from '@ant-design/icons-vue'
import type { UploadProps } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/loginUser'
import { changePassword, updateUserInfo, uploadUserAvatar } from '@/api/userController'
import { getMyAccount, getMyInvitationCode, getSignStatus, signIn } from '@/api/pointController'
import { listMyAppVoByPage } from '@/api/appController'
import AppCard from '@/components/AppCard.vue'
import { getDeployUrl } from '@/config/env'
import dayjs from 'dayjs'

const router = useRouter()
const loginUserStore = useLoginUserStore()
const submitting = ref(false)
const avatarUploading = ref(false)
const passwordSubmitting = ref(false)
const showEditModal = ref(false)
const showPasswordModal = ref(false)

// 积分相关
const accountInfo = ref<API.UserAccountVO | null>(null)
const todaySigned = ref(false)
const signing = ref(false)
const invitationCode = ref<string>('')

// 我的应用相关
const myApps = ref<API.AppVO[]>([])
const appsPage = reactive({
  current: 1,
  pageSize: 6,
  total: 0,
})
const appsLoading = ref(false)

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
  loadSignStatus()
  loadInvitationCode()
  loadMyApps()
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

// 加载邀请码
const loadInvitationCode = async () => {
  try {
    const res = await getMyInvitationCode()
    if (res.data.code === 0 && res.data.data) {
      invitationCode.value = res.data.data
    }
  } catch (error) {
    console.error('加载邀请码失败：', error)
  }
}

// 加载今日签到状态
const loadSignStatus = async () => {
  try {
    const res = await getSignStatus()
    if (res.data.code === 0) {
      todaySigned.value = res.data.data ?? false
    }
  } catch (error) {
    console.error('加载签到状态失败：', error)
  }
}

// 执行签到
const handleSignIn = async () => {
  if (todaySigned.value) {
    message.warning('今日已签到')
    return
  }
  signing.value = true
  try {
    const res = await signIn()
    if (res.data.code === 0 && res.data.data) {
      const data = res.data.data
      message.success(
        `签到成功！获得 ${data.points} 积分，连续签到 ${data.continuousDays} 天${data.isBonus ? '，获得额外奖励！' : ''}`,
      )
      todaySigned.value = true
      await loadAccountInfo()
    } else {
      message.error(res.data.message ?? '签到失败')
    }
  } catch (error: any) {
    message.error(error?.response?.data?.message ?? '签到失败，请重试')
  } finally {
    signing.value = false
  }
}

// 加载我的应用
const loadMyApps = async () => {
  if (!loginUserStore.loginUser.id) {
    return
  }
  appsLoading.value = true
  try {
    const res = await listMyAppVoByPage({
      pageNum: appsPage.current,
      pageSize: appsPage.pageSize,
      sortField: 'createTime',
      sortOrder: 'desc',
    })
    if (res.data.code === 0 && res.data.data) {
      myApps.value = res.data.data.records || []
      appsPage.total = res.data.data.totalRow || 0
    }
  } catch (error) {
    console.error('加载我的应用失败：', error)
  } finally {
    appsLoading.value = false
  }
}

// 查看应用对话
const viewAppChat = (appId: string | number | undefined) => {
  if (appId) {
    router.push(`/app/chat/${appId}`)
  }
}

// 查看应用作品
const viewAppWork = (app: API.AppVO) => {
  if (app.deployKey) {
    const url = getDeployUrl(app.deployKey)
    window.open(url, '_blank')
  }
}

// 切换应用列表页码
const handleAppsPageChange = (page: number, pageSize: number) => {
  appsPage.current = page
  appsPage.pageSize = pageSize
  loadMyApps()
}

// 跳转到积分商城
const goToPointMall = () => {
  router.push('/user/point-mall')
}

// 跳转到积分流水页面
const goToPointLogs = () => {
  router.push('/user/point-logs')
}

// 打开编辑资料弹窗
const openEditModal = () => {
  showEditModal.value = true
  // 重置表单数据
  const user = loginUserStore.loginUser
  Object.assign(formState, {
    id: user.id,
    userAccount: user.userAccount,
    userName: user.userName,
    userProfile: user.userProfile,
    userAvatar: user.userAvatar,
  })
}

// 关闭编辑资料弹窗
const closeEditModal = () => {
  showEditModal.value = false
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
      closeEditModal()
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
    <!-- 用户资料卡片 -->
    <section class="profile-card">
      <div class="profile-info">
        <!-- 左侧用户信息 -->
        <div class="user-info-left">
          <a-avatar :size="80" :src="displayAvatar" class="user-avatar" />
          <div class="user-details">
            <h3 class="user-name">{{ formState.userName || '未设置' }}</h3>
            <p class="user-account">@{{ formState.userAccount }}</p>
            <div class="user-stats">
              <div class="stat-item">
                <span class="stat-label">积分</span>
                <span class="stat-value">{{ accountInfo?.availablePoints ?? 0 }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">已加入</span>
                <span class="stat-value">{{ joinedDays }}天</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">邮箱</span>
                <a-tag class="stat-tag" color="orange">未绑定</a-tag>
              </div>
              <div class="stat-item">
                <span class="stat-label">邀请码</span>
                <span class="stat-value code">{{ invitationCode || '加载中...' }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧操作按钮 -->
        <div class="user-actions">
          <a-button class="action-btn" type="primary" @click="goToPointMall">
            <ShoppingOutlined />
            积分商城
            <span class="points-badge">{{ accountInfo?.availablePoints ?? 0 }}</span>
          </a-button>
          <a-button class="action-btn" @click="goToPointLogs">
            <HistoryOutlined />
            积分详情
          </a-button>
          <a-button
            :disabled="todaySigned"
            :loading="signing"
            class="action-btn"
            type="primary"
            @click="handleSignIn"
          >
            <CheckCircleOutlined v-if="todaySigned" />
            <GiftOutlined v-else />
            {{ todaySigned ? '今日已签到' : '立即签到' }}
          </a-button>
          <a-button class="action-btn" type="primary" @click="openEditModal">
            <EditOutlined />
            编辑资料
          </a-button>
        </div>
      </div>
    </section>

    <!-- 我的应用 -->
    <section class="profile-card my-apps-card">
      <div class="my-apps-header">
        <div class="apps-title-section">
          <AppstoreOutlined class="apps-title-icon" />
          <h3>我的应用</h3>
        </div>
      </div>
      <div class="my-apps-content">
        <a-spin :spinning="appsLoading">
          <div v-if="myApps.length > 0" class="apps-grid">
            <div v-for="app in myApps" :key="app.id" class="app-item">
              <AppCard :app="app" @view-chat="viewAppChat" @view-work="viewAppWork" />
            </div>
          </div>
          <div v-else class="apps-empty">
            <div class="empty-icon">📱</div>
            <p class="empty-text">暂无应用</p>
            <p class="empty-hint">请创建一个应用开始使用</p>
          </div>
        </a-spin>
        <div v-if="appsPage.total > 0" class="apps-pagination">
          <a-pagination
            v-model:current="appsPage.current"
            v-model:page-size="appsPage.pageSize"
            :total="appsPage.total"
            :show-size-changer="false"
            :show-total="(total: number) => `共 ${total} 个应用`"
            size="small"
            @change="handleAppsPageChange"
          />
        </div>
      </div>
    </section>

    <!-- 编辑资料弹窗 -->
    <a-modal
      v-model:open="showEditModal"
      :footer="null"
      :title="null"
      class="edit-modal"
      width="800px"
      @cancel="closeEditModal"
    >
      <div class="modal-header">
        <h3>编辑个人信息</h3>
        <p class="modal-subtitle">修改您的个人信息和账户设置</p>
      </div>

      <div class="modal-content">
        <!-- 用户资料卡片 -->
        <div class="modal-profile-card">
          <a-avatar :size="64" :src="displayAvatar" />
          <div class="modal-profile-info">
            <h4>{{ formState.userName || '未设置' }}</h4>
            <p>@{{ formState.userAccount }}</p>
            <div class="modal-profile-stats">
              <div class="modal-stat-item">
                <GiftOutlined />
                <span>积分 {{ accountInfo?.availablePoints ?? 0 }}</span>
              </div>
              <div class="modal-stat-item">
                <UserOutlined />
                <span>已加入 {{ joinedDays }}天</span>
              </div>
              <div class="modal-stat-item">
                <MailOutlined />
                <a-tag color="orange" size="small">未绑定</a-tag>
              </div>
            </div>
          </div>
        </div>

        <!-- 基本信息 -->
        <div class="modal-section">
          <div class="section-header">
            <EditOutlined class="section-icon" />
            <h4>基本信息</h4>
          </div>
          <a-form
            :model="formState"
            autocomplete="off"
            class="edit-form"
            name="editProfile"
            @finish="handleSubmit"
          >
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
            <a-form-item
              :rules="[{ required: true, message: '请输入用户名' }]"
              label="* 用户名"
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
                <template #icon>
                  <SafetyOutlined />
                </template>
                保存修改
              </a-button>
              <a-button size="large" @click="closeEditModal">重置</a-button>
            </a-form-item>
          </a-form>
        </div>

        <!-- 邮箱管理 -->
        <div class="modal-section">
          <div class="section-header">
            <MailOutlined class="section-icon" />
            <h4>邮箱管理</h4>
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
        </div>

        <!-- 安全设置 -->
        <div class="modal-section">
          <div class="section-header">
            <SafetyOutlined class="section-icon" />
            <h4>安全设置</h4>
          </div>
          <div class="security-settings">
            <div class="security-item">
              <div class="security-info">
                <h5>登录密码</h5>
                <p>用于登录账户的密码</p>
              </div>
              <a-button class="change-password-btn" size="large" @click="openPasswordModal">
                <LockOutlined />
                修改密码
              </a-button>
            </div>
          </div>
        </div>
      </div>
    </a-modal>

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
.profile-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px 0 64px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.profile-card {
  background: #fff;
  border-radius: 18px;
  padding: 32px 40px;
  box-shadow: 0 12px 35px rgba(15, 39, 80, 0.07);
  border: 1px solid #f0f2f5;
}

.profile-info {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 32px;
}

.user-info-left {
  display: flex;
  align-items: flex-start;
  gap: 24px;
  flex: 1;
}

.user-avatar {
  flex-shrink: 0;
  border: 3px solid #f0f2f5;
}

.user-details {
  flex: 1;
}

.user-name {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #1f2d3d;
}

.user-account {
  margin: 0 0 16px 0;
  font-size: 16px;
  color: #8c8c8c;
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
}

.stat-label {
  font-size: 14px;
  color: #8c8c8c;
}

.stat-value {
  font-size: 16px;
  font-weight: 600;
  color: #1f2d3d;
}

.stat-value.code {
  font-family: 'Courier New', monospace;
  color: #1890ff;
  font-weight: 600;
}

.stat-tag {
  margin: 0;
}

.user-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-width: 150px;
}

.action-btn {
  height: 42px;
  font-size: 15px;
  font-weight: 500;
  border-radius: 8px;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.points-badge {
  margin-left: 4px;
  font-weight: 600;
}

.my-apps-card {
  margin-top: 0;
}

.my-apps-header {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f2f5;
}

.apps-title-section {
  display: flex;
  align-items: center;
  gap: 8px;
}

.apps-title-icon {
  font-size: 18px;
  color: #1890ff;
}

.my-apps-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1f2d3d;
}

.my-apps-content {
  min-height: 200px;
}

.apps-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.app-item {
  width: 100%;
}

.apps-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
  opacity: 0.5;
}

.empty-text {
  font-size: 16px;
  font-weight: 500;
  color: #5f6b7c;
  margin: 0 0 8px 0;
}

.empty-hint {
  font-size: 14px;
  color: #8c8c8c;
  margin: 0;
}

.apps-pagination {
  display: flex;
  justify-content: center;
  padding-top: 16px;
  border-top: 1px solid #f0f2f5;
}

/* 编辑资料弹窗样式 */
.edit-modal :deep(.ant-modal-content) {
  border-radius: 16px;
  overflow: hidden;
}

.edit-modal :deep(.ant-modal-body) {
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

.modal-content {
  padding: 32px;
  max-height: 70vh;
  overflow-y: auto;
}

.modal-profile-card {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 24px;
  background: linear-gradient(120deg, #f5f7ff, #fff);
  border-radius: 12px;
  margin-bottom: 32px;
  border: 1px solid #f0f2f5;
}

.modal-profile-info h4 {
  margin: 0 0 4px 0;
  font-size: 20px;
  font-weight: 600;
  color: #1f2d3d;
}

.modal-profile-info p {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #8c8c8c;
}

.modal-profile-stats {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.modal-stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #5f6b7c;
}

.modal-section {
  margin-bottom: 32px;
}

.modal-section:last-child {
  margin-bottom: 0;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.section-icon {
  font-size: 20px;
  color: #1890ff;
}

.section-header h4 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1f2d3d;
}

.edit-form {
  padding: 20px;
  background: #fafafa;
  border-radius: 12px;
}

.avatar-uploader {
  display: flex;
  align-items: center;
  gap: 16px;
}

.form-actions {
  margin-top: 24px;
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

/* 修改密码弹窗样式 */
.password-modal :deep(.ant-modal-content) {
  border-radius: 16px;
  overflow: hidden;
}

.password-modal :deep(.ant-modal-body) {
  padding: 0;
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
  .profile-card {
    padding: 24px;
  }

  .profile-info {
    flex-direction: column;
    gap: 24px;
  }

  .user-actions {
    width: 100%;
    flex-direction: row;
    flex-wrap: wrap;
  }

  .action-btn {
    flex: 1;
    min-width: 120px;
  }

  .user-stats {
    gap: 16px;
  }

  .apps-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
  }

  .apps-empty {
    padding: 40px 20px;
  }

  .empty-icon {
    font-size: 48px;
  }

  .modal-content {
    padding: 24px;
  }

  .modal-profile-card {
    flex-direction: column;
    text-align: center;
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
}
</style>
