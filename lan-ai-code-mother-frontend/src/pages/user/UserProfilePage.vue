<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  AppstoreOutlined,
  CheckCircleOutlined,
  CopyOutlined,
  EditOutlined,
  GiftOutlined,
  HistoryOutlined,
  LockOutlined,
  ShoppingOutlined,
  ShareAltOutlined,
} from '@ant-design/icons-vue'
import { useLoginUserStore } from '@/stores/loginUser'
import { changePassword } from '@/api/userController'
import { getMyAccount, getMyInvitationCode, getSignStatus, signIn } from '@/api/pointController'
import { listMyAppVoByPage } from '@/api/appController'
import AppCard from '@/components/AppCard.vue'
import { getDeployUrl } from '@/config/env'
import dayjs from 'dayjs'

const router = useRouter()
const loginUserStore = useLoginUserStore()
const passwordSubmitting = ref(false)
const showPasswordModal = ref(false)
const showInvitationCard = ref(false)

const accountInfo = ref<API.UserAccountVO | null>(null)
const todaySigned = ref(false)
const signing = ref(false)
const invitationCode = ref<string>('')

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

const joinedDays = computed(() => {
  if (!loginUserStore.loginUser.createTime) return 0
  return dayjs().diff(dayjs(loginUserStore.loginUser.createTime), 'day')
})

const invitationLink = computed(() => {
  return `${window.location.origin}/user/register?invitationCode=${invitationCode.value}`
})

const generateInvitationText = () => {
  return `欢迎加入 蓝海智造 智能AI应用生成平台! 🎉

我的邀请码：${invitationCode.value}

👉 使用此邀请码注册，即可获得专属福利！

👉 体验智能应用生成，快速构建AI应用！

👉 加入我们的开发者社区，共同探索AI创新！

访问链接：${invitationLink.value}`
}

const copyInvitation = async () => {
  if (!invitationCode.value) {
    message.warning('邀请码加载中...')
    return
  }
  const invitationText = generateInvitationText()
  try {
    if (navigator.clipboard && navigator.clipboard.writeText) {
      await navigator.clipboard.writeText(invitationText)
      message.success('✨ 邀请信息已复制到剪贴板，快去邀请小伙伴吧!')
    } else {
      const textArea = document.createElement('textarea')
      textArea.value = invitationText
      textArea.style.position = 'fixed'
      textArea.style.left = '-9999px'
      document.body.appendChild(textArea)
      textArea.select()
      try {
        document.execCommand('copy')
        message.success('✨ 邀请信息已复制到剪贴板，快去邀请小伙伴吧!')
      } catch (err) {
        message.error('复制失败，请手动复制')
      }
      document.body.removeChild(textArea)
    }
  } catch (error) {
    message.error('复制失败，请重试')
  }
}

const openInvitationCard = () => {
  if (!invitationCode.value) {
    message.warning('邀请码加载中...')
    return
  }
  showInvitationCard.value = true
}

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
      message.success(`🎁 签到成功！获得 ${data.points} 积分，连续签到 ${data.continuousDays} 天${data.isBonus ? '，获得额外奖励！' : ''}`)
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

const viewAppChat = (appId: string | number | undefined) => {
  if (appId) {
    router.push(`/app/chat/${appId}`)
  }
}

const viewAppWork = (app: API.AppVO) => {
  if (app.deployKey) {
    const url = getDeployUrl(app.deployKey)
    window.open(url, '_blank')
  }
}

const handleAppsPageChange = (page: number, pageSize: number) => {
  appsPage.current = page
  appsPage.pageSize = pageSize
  loadMyApps()
}

const goToPointMall = () => {
  router.push('/user/point-mall')
}

const goToPointLogs = () => {
  router.push('/user/point-logs')
}

const goToEditProfile = () => {
  router.push('/user/edit-profile')
}

const validateCheckPassword = (rule: unknown, value: string, callback: (error?: Error) => void) => {
  if (value && value !== passwordForm.newPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const closePasswordModal = () => {
  showPasswordModal.value = false
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.checkPassword = ''
}

const handlePasswordSubmit = async () => {
  passwordSubmitting.value = true
  try {
    const res = await changePassword(passwordForm)
    if (res.data.code === 0) {
      message.success('密码修改成功，请使用新密码登录')
      closePasswordModal()
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
  <div class="user-profile-page">
    <!-- 用户信息卡片 -->
    <a-card class="profile-card" :bordered="false">
      <div class="profile-header">
        <div class="avatar-section">
          <a-avatar :size="100" :src="displayAvatar">
            <template #icon>
              <UserOutlined />
            </template>
          </a-avatar>
          <div class="user-info">
            <h2 class="user-name">{{ formState.userName || '未设置昵称' }}</h2>
            <p class="user-account">{{ formState.userAccount }}</p>
            <p class="user-profile">{{ formState.userProfile || '这个人很懒，什么都没留下~' }}</p>
          </div>
        </div>
      </div>

      <!-- 用户统计 -->
      <div class="user-stats">
        <div class="stat-item">
          <span class="stat-label">💎 积分</span>
          <span class="stat-value">{{ accountInfo?.availablePoints ?? 0 }}</span>
        </div>
        <div class="stat-item">
          <span class="stat-label">📅 已加入</span>
          <span class="stat-value">{{ joinedDays }} 天</span>
        </div>
        <div class="stat-item">
          <span class="stat-label">📧 邮箱</span>
          <span class="stat-value stat-email">{{ loginUserStore.loginUser.userEmail || '未绑定' }}</span>
        </div>
        <div class="stat-item stat-item-invitation">
          <span class="stat-label">🎁 邀请码</span>
          <a-button class="invitation-code-btn" size="small" type="primary" @click="openInvitationCard">
            {{ invitationCode || '加载中...' }}
            <ShareAltOutlined />
          </a-button>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <a-button class="action-btn action-btn-gift" size="large" @click="handleSignIn" :loading="signing">
          <GiftOutlined />
          {{ todaySigned ? '今日已签到' : '每日签到' }}
        </a-button>
        <a-button class="action-btn action-btn-primary" size="large" @click="goToPointMall">
          <ShoppingOutlined />
          积分商城
        </a-button>
        <a-button class="action-btn action-btn-edit" size="large" @click="goToEditProfile">
          <EditOutlined />
          编辑资料
        </a-button>
        <a-button class="action-btn" size="large" @click="goToPointLogs">
          <HistoryOutlined />
          积分明细
        </a-button>
      </div>
    </a-card>

    <!-- 我的应用 -->
    <a-card class="apps-card" :bordered="false">
      <template #title>
        <div class="card-title">
          <AppstoreOutlined />
          我的应用
        </div>
      </template>
      <template #extra>
        <a-button type="link" @click="() => router.push('/')">创建新应用</a-button>
      </template>

      <a-spin :spinning="appsLoading">
        <div v-if="myApps.length > 0" class="apps-grid">
          <AppCard
            v-for="app in myApps"
            :key="app.id"
            :app="app"
            @view-chat="viewAppChat"
            @view-work="viewAppWork"
          />
        </div>
        <a-empty v-else description="暂无应用，快去创建一个吧~" />
      </a-spin>

      <div v-if="appsPage.total > 0" class="pagination-wrapper">
        <a-pagination
          v-model:current="appsPage.current"
          v-model:page-size="appsPage.pageSize"
          :total="appsPage.total"
          :show-size-changer="true"
          :show-total="(total) => `共 ${total} 个应用`"
          @change="handleAppsPageChange"
        />
      </div>
    </a-card>

    <!-- 邀请卡片弹窗 -->
    <a-modal
      v-model:open="showInvitationCard"
      :footer="null"
      :title="null"
      class="invitation-modal"
      width="500px"
      @cancel="showInvitationCard = false"
    >
      <div class="invitation-card">
        <div class="invitation-header">
          <div class="invitation-icon">🎉</div>
          <h3 class="invitation-title">邀请好友加入蓝海智造</h3>
          <p class="invitation-subtitle">分享邀请码，双方均可获得积分奖励</p>
        </div>
        <div class="invitation-content">
          <div class="invitation-code-section">
            <div class="invitation-code-label">🎯 我的邀请码</div>
            <div class="invitation-code-display">
              <span class="invitation-code-text">{{ invitationCode }}</span>
              <a-button class="copy-btn" size="large" type="primary" @click="copyInvitation">
                <CopyOutlined />
                复制邀请信息
              </a-button>
            </div>
          </div>
          <div class="invitation-rewards">
            <div class="reward-item">
              <div class="reward-icon">🎁</div>
              <div class="reward-content">
                <div class="reward-title">被邀请人奖励</div>
                <div class="reward-value">+50 积分</div>
              </div>
            </div>
            <div class="reward-item">
              <div class="reward-icon">💰</div>
              <div class="reward-content">
                <div class="reward-title">邀请人奖励</div>
                <div class="reward-value">+30 积分</div>
              </div>
            </div>
          </div>
          <div class="invitation-steps">
            <div class="step-title">📋 邀请步骤</div>
            <div class="step-list">
              <div class="step-item">
                <span class="step-number">1</span>
                <span class="step-text">点击复制邀请信息</span>
              </div>
              <div class="step-item">
                <span class="step-number">2</span>
                <span class="step-text">分享给好友或朋友圈</span>
              </div>
              <div class="step-item">
                <span class="step-number">3</span>
                <span class="step-text">好友注册成功即可获得奖励</span>
              </div>
            </div>
          </div>
          <div class="invitation-link-section">
            <div class="link-label">🔗 邀请链接</div>
            <div class="link-display">
              <span class="link-text">{{ invitationLink }}</span>
            </div>
          </div>
        </div>
      </div>
    </a-modal>

    <!-- 修改密码弹窗 -->
    <a-modal
      v-model:open="showPasswordModal"
      title="修改密码"
      :confirm-loading="passwordSubmitting"
      @ok="handlePasswordSubmit"
      @cancel="closePasswordModal"
    >
      <a-form
        :model="passwordForm"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 16 }"
      >
        <a-form-item label="原密码" name="oldPassword" :rules="[{ required: true, message: '请输入原密码' }]">
          <a-input-password v-model:value="passwordForm.oldPassword" placeholder="请输入原密码" />
        </a-form-item>
        <a-form-item label="新密码" name="newPassword" :rules="[{ required: true, message: '请输入新密码' }]">
          <a-input-password v-model:value="passwordForm.newPassword" placeholder="请输入新密码（6-16位）" />
        </a-form-item>
        <a-form-item
          label="确认密码"
          name="checkPassword"
          :rules="[
            { required: true, message: '请再次输入新密码' },
            { validator: validateCheckPassword }
          ]"
        >
          <a-input-password v-model:value="passwordForm.checkPassword" placeholder="请再次输入新密码" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<style scoped>
.user-profile-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.profile-card {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.profile-header {
  margin-bottom: 24px;
}

.avatar-section {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}

.user-info {
  flex: 1;
  padding-top: 8px;
}

.user-name {
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 8px 0;
  color: #1a1a1a;
}

.user-account {
  font-size: 14px;
  color: #8c8c8c;
  margin: 0 0 8px 0;
}

.user-profile {
  font-size: 14px;
  color: #595959;
  margin: 0;
  line-height: 1.6;
}

.user-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  padding: 20px;
  background: #fafafa;
  border-radius: 8px;
  margin-bottom: 24px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
  transition: all 0.3s ease;
}

.stat-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.stat-label {
  font-size: 13px;
  color: #8c8c8c;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 20px;
  font-weight: 600;
  color: #1a1a1a;
}

.stat-email {
  font-size: 14px;
  font-weight: 400;
  word-break: break-all;
  text-align: center;
}

.stat-item-invitation {
  grid-column: span 1;
}

.invitation-code-btn {
  font-family: 'Courier New', monospace;
  font-weight: 600;
  height: 32px;
  display: flex;
  align-items: center;
  gap: 4px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s ease;
}

.invitation-code-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.action-buttons {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 12px;
}

.action-btn {
  height: 48px;
  font-size: 15px;
  font-weight: 500;
  border-radius: 8px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}

.action-btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: #fff;
}

.action-btn-primary:hover {
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
}

.action-btn-gift {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  border: none;
  color: #fff;
}

.action-btn-gift:hover {
  background: linear-gradient(135deg, #f5576c 0%, #f093fb 100%);
}

.action-btn-edit {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  border: none;
  color: #fff;
}

.action-btn-edit:hover {
  background: linear-gradient(135deg, #00f2fe 0%, #4facfe 100%);
}

.apps-card {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
}

.apps-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

/* 邀请卡片弹窗样式 */
.invitation-modal :deep(.ant-modal-content) {
  border-radius: 20px;
  overflow: hidden;
}

.invitation-modal :deep(.ant-modal-body) {
  padding: 0;
}

.invitation-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.invitation-header {
  padding: 40px 32px 32px;
  text-align: center;
  color: #fff;
}

.invitation-icon {
  font-size: 64px;
  margin-bottom: 16px;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.invitation-title {
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 8px 0;
  color: #fff;
}

.invitation-subtitle {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.9);
  margin: 0;
}

.invitation-content {
  padding: 32px;
  background: #fff;
  border-radius: 20px 20px 0 0;
}

.invitation-code-section {
  margin-bottom: 24px;
}

.invitation-code-label {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 12px;
}

.invitation-code-display {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 20px;
  background: #f5f5f5;
  border-radius: 12px;
}

.invitation-code-text {
  font-family: 'Courier New', monospace;
  font-size: 28px;
  font-weight: 700;
  color: #667eea;
  text-align: center;
  letter-spacing: 2px;
}

.copy-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.copy-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.invitation-rewards {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.reward-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  border-radius: 12px;
  transition: all 0.3s ease;
}

.reward-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.reward-icon {
  font-size: 32px;
}

.reward-content {
  flex: 1;
}

.reward-title {
  font-size: 14px;
  color: #595959;
  margin-bottom: 4px;
}

.reward-value {
  font-size: 20px;
  font-weight: 700;
  color: #667eea;
}

.invitation-steps {
  margin-bottom: 24px;
}

.step-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 16px;
}

.step-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.step-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: #fafafa;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.step-item:hover {
  background: #f0f0f0;
}

.step-number {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border-radius: 50%;
  font-size: 14px;
  font-weight: 600;
  flex-shrink: 0;
}

.step-text {
  font-size: 14px;
  color: #595959;
}

.invitation-link-section {
  margin-bottom: 16px;
}

.link-label {
  font-size: 14px;
  font-weight: 600;
  color: #595959;
  margin-bottom: 8px;
}

.link-display {
  padding: 12px;
  background: #f5f5f5;
  border-radius: 8px;
  word-break: break-all;
}

.link-text {
  font-size: 12px;
  color: #8c8c8c;
  font-family: 'Courier New', monospace;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-profile-page {
    padding: 16px;
  }

  .avatar-section {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  .user-stats {
    grid-template-columns: repeat(2, 1fr);
  }

  .stat-item-invitation {
    grid-column: span 2;
  }

  .action-buttons {
    grid-template-columns: repeat(2, 1fr);
  }

  .apps-grid {
    grid-template-columns: 1fr;
  }

  .invitation-header {
    padding: 32px 24px 24px;
  }

  .invitation-content {
    padding: 24px;
  }

  .invitation-rewards {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .user-stats {
    grid-template-columns: 1fr;
  }

  .stat-item-invitation {
    grid-column: span 1;
  }

  .action-buttons {
    grid-template-columns: 1fr;
  }
}
</style>
