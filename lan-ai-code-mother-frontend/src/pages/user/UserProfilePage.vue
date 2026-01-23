<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  AppstoreOutlined,
  CalendarOutlined,
  CheckCircleOutlined,
  CopyOutlined,
  CrownOutlined,
  EditOutlined,
  GiftOutlined,
  HistoryOutlined,
  LinkOutlined,
  MailOutlined,
  NumberOutlined,
  ShareAltOutlined,
  ShoppingOutlined,
  ThunderboltOutlined,
  TrophyOutlined,
  UserOutlined,
  WalletOutlined,
} from '@ant-design/icons-vue'
import { useLoginUserStore } from '@/stores/loginUser'
import { changePassword } from '@/api/userController'
import {
  getAllRules,
  getMyAccount,
  getMyInvitationCode,
  getSignStatus,
  signIn,
} from '@/api/pointController'
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
const inviteeReward = ref<number>(50) // 被邀请人奖励
const inviterReward = ref<number>(30) // 邀请人奖励

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
  return `欢迎加入 蓝海智造 智能AI应用生成平台!

我的邀请码：${invitationCode.value}

使用此邀请码注册，即可获得专属福利！

体验智能应用生成，快速构建AI应用！

加入我们的开发者社区，共同探索AI创新！

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
  loadInvitationRules()
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

const loadInvitationRules = async () => {
  try {
    const res = await getAllRules()
    if (res.data.code === 0 && res.data.data) {
      const rules = res.data.data
      // 查找被邀请人奖励和邀请人奖励
      const inviteeRule = rules.find((r: API.PointRuleVO) => r.ruleKey === 'INVITE_NEW')
      const inviterRule = rules.find((r: API.PointRuleVO) => r.ruleKey === 'INVITE_REWARD')
      if (inviteeRule) {
        inviteeReward.value = inviteeRule.ruleValue
      }
      if (inviterRule) {
        inviterReward.value = inviterRule.ruleValue
      }
    }
  } catch (error) {
    console.error('加载邀请规则失败：', error)
    // 使用默认值
    inviteeReward.value = 50
    inviterReward.value = 30
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
      message.success(
        `🎁 签到成功！获得 ${data.points} 积分，连续签到 ${data.continuousDays} 天${data.isBonus ? '，获得额外奖励！' : ''}`,
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

// 截取文本
const truncateText = (text: string, maxLength: number) => {
  if (!text) return ''
  if (text.length <= maxLength) return text
  return text.substring(0, maxLength) + '...'
}
</script>

<template>
  <div class="user-profile-page">
    <!-- 用户信息卡片 -->
    <a-card :bordered="false" class="profile-card cyan-card">
      <div class="profile-header">
        <div class="avatar-section">
          <a-avatar :size="120" :src="displayAvatar" class="user-avatar">
            <template #icon>
              <UserOutlined />
            </template>
          </a-avatar>
          <div class="user-info">
            <a-tooltip v-if="formState.userName" :title="formState.userName">
              <h2 class="user-name">{{ truncateText(formState.userName, 12) }}</h2>
            </a-tooltip>
            <h2 v-else class="user-name">未设置昵称</h2>
            <p class="user-account">
              <UserOutlined />
              {{ formState.userAccount }}
            </p>
            <a-tooltip v-if="formState.userProfile" :title="formState.userProfile">
              <p class="user-profile">{{ truncateText(formState.userProfile, 20) }}</p>
            </a-tooltip>
            <p v-else class="user-profile">这个人很懒，什么都没留下~</p>
          </div>
        </div>
      </div>

      <!-- 用户统计 -->
      <div class="user-stats">
        <div class="stat-item">
          <div class="stat-icon points-icon">
            <WalletOutlined />
          </div>
          <div class="stat-content">
            <span class="stat-label">当前积分</span>
            <span class="stat-value">{{ accountInfo?.availablePoints ?? 0 }}</span>
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-icon days-icon">
            <CalendarOutlined />
          </div>
          <div class="stat-content">
            <span class="stat-label">已加入</span>
            <span class="stat-value">{{ joinedDays }} 天</span>
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-icon email-icon">
            <MailOutlined />
          </div>
          <div class="stat-content">
            <span class="stat-label">邮箱</span>
            <span class="stat-value stat-email">{{
              loginUserStore.loginUser.userEmail || '未绑定'
            }}</span>
          </div>
        </div>
        <div class="stat-item stat-item-invitation">
          <div class="stat-icon invite-icon">
            <ShareAltOutlined />
          </div>
          <div class="stat-content">
            <span class="stat-label">我的邀请码</span>
            <a-button
              class="invitation-code-btn ant-btn-purple"
              size="small"
              type="primary"
              @click="openInvitationCard"
            >
              {{ invitationCode || '加载中...' }}
              <ShareAltOutlined />
            </a-button>
          </div>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <a-button
          :loading="signing"
          class="action-btn action-btn-gift ant-btn-success"
          size="large"
          @click="handleSignIn"
        >
          <template #icon>
            <GiftOutlined />
          </template>
          {{ todaySigned ? '今日已签到' : '每日签到' }}
        </a-button>
        <a-button
          class="action-btn action-btn-mall ant-btn-warning"
          size="large"
          @click="goToPointMall"
        >
          <template #icon>
            <ShoppingOutlined />
          </template>
          积分商城
        </a-button>
        <a-button
          class="action-btn action-btn-edit ant-btn-primary"
          size="large"
          @click="goToEditProfile"
        >
          <template #icon>
            <EditOutlined />
          </template>
          编辑资料
        </a-button>
        <a-button
          class="action-btn action-btn-logs ant-btn-info"
          size="large"
          @click="goToPointLogs"
        >
          <template #icon>
            <HistoryOutlined />
          </template>
          积分明细
        </a-button>
      </div>
    </a-card>

    <!-- 我的应用 -->
    <a-card :bordered="false" class="apps-card info-card">
      <template #title>
        <div class="card-title">
          <AppstoreOutlined />
          我的应用
        </div>
      </template>
      <template #extra>
        <a-button type="link" @click="() => router.push('/')">
          <ThunderboltOutlined />
          创建新应用
        </a-button>
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
          :show-size-changer="true"
          :show-total="(total) => `共 ${total} 个应用`"
          :total="appsPage.total"
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
      width="520px"
      @cancel="showInvitationCard = false"
    >
      <div class="invitation-card">
        <div class="invitation-header">
          <div class="invitation-icon">
            <TrophyOutlined />
          </div>
          <h3 class="invitation-title">邀请好友加入蓝海智造</h3>
          <p class="invitation-subtitle">分享邀请码，双方均可获得积分奖励</p>
        </div>
        <div class="invitation-content">
          <div class="invitation-code-section">
            <div class="section-title">
              <NumberOutlined />
              我的邀请码
            </div>
            <div class="invitation-code-display">
              <span class="invitation-code-text">{{ invitationCode }}</span>
              <a-button class="copy-btn" size="large" type="primary" @click="copyInvitation">
                <template #icon>
                  <CopyOutlined />
                </template>
                复制邀请信息
              </a-button>
            </div>
          </div>
          <div class="invitation-rewards">
            <div class="reward-item">
              <div class="reward-icon">
                <CrownOutlined />
              </div>
              <div class="reward-content">
                <div class="reward-title">被邀请人奖励</div>
                <div class="reward-value">+{{ inviteeReward }} 积分</div>
              </div>
            </div>
            <div class="reward-item">
              <div class="reward-icon">
                <GiftOutlined />
              </div>
              <div class="reward-content">
                <div class="reward-title">邀请人奖励</div>
                <div class="reward-value">+{{ inviterReward }} 积分</div>
              </div>
            </div>
          </div>
          <div class="invitation-steps">
            <div class="section-title">
              <CheckCircleOutlined />
              邀请步骤
            </div>
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
            <div class="section-title">
              <LinkOutlined />
              邀请链接
            </div>
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
      :confirm-loading="passwordSubmitting"
      title="修改密码"
      @cancel="closePasswordModal"
      @ok="handlePasswordSubmit"
    >
      <a-form :label-col="{ span: 6 }" :model="passwordForm" :wrapper-col="{ span: 16 }">
        <a-form-item
          :rules="[{ required: true, message: '请输入原密码' }]"
          label="原密码"
          name="oldPassword"
        >
          <a-input-password v-model:value="passwordForm.oldPassword" placeholder="请输入原密码" />
        </a-form-item>
        <a-form-item
          :rules="[{ required: true, message: '请输入新密码' }]"
          label="新密码"
          name="newPassword"
        >
          <a-input-password
            v-model:value="passwordForm.newPassword"
            placeholder="请输入新密码（6-16位）"
          />
        </a-form-item>
        <a-form-item
          :rules="[
            { required: true, message: '请再次输入新密码' },
            { validator: validateCheckPassword },
          ]"
          label="确认密码"
          name="checkPassword"
        >
          <a-input-password
            v-model:value="passwordForm.checkPassword"
            placeholder="请再次输入新密码"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Serif+SC:wght@400;600;700&family=Noto+Sans+SC:wght@300;400;500;600;700&display=swap');

:root {
  --color-primary: #f97316;
  --color-primary-dark: #ea580c;
  --color-primary-light: #fbbf24;
  --color-text: #1e293b;
  --color-text-secondary: #64748b;
  --color-border: #4d82c8;
  --color-bg-hover: #f8fafc;
  --font-serif: 'Noto Serif SC', serif;
  --font-sans: 'Noto Sans SC', sans-serif;
}

.user-profile-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 用户信息卡片 */
.profile-card {
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border: 1px solid var(--color-border);
  background: linear-gradient(135deg, #fff7ed 0%, #ffffff 100%);
}

.profile-header {
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid var(--color-border);
}

.avatar-section {
  display: flex;
  gap: 28px;
  align-items: flex-start;
}

.user-avatar {
  border: 4px solid white;
  box-shadow: 0 4px 12px rgba(249, 115, 22, 0.2);
}

.user-info {
  flex: 1;
  padding-top: 12px;
}

.user-name {
  font-family: var(--font-serif);
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 12px 0;
  color: var(--color-text);
}

.user-account {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--color-text-secondary);
  margin: 0 0 8px 0;
  font-weight: 500;
}

.user-profile {
  font-size: 13px;
  color: var(--color-text-secondary);
  margin: 0;
  line-height: 1.6;
}

/* 用户统计 */
.user-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  padding: 24px;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 12px;
  margin-bottom: 32px;
  border: 1px solid var(--color-border);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: white;
  border-radius: 12px;
  border: 1px solid var(--color-border);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.stat-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(249, 115, 22, 0.15);
  border-color: var(--color-primary);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  flex-shrink: 0;
}

.points-icon {
  background: linear-gradient(135deg, var(--color-primary-light) 0%, var(--color-primary) 100%);
  color: white;
}

.days-icon {
  background: linear-gradient(135deg, #60a5fa 0%, #3b82f6 100%);
  color: white;
}

.email-icon {
  background: linear-gradient(135deg, #34d399 0%, #10b981 100%);
  color: white;
}

.invite-icon {
  background: linear-gradient(135deg, #a78bfa 0%, #8b5cf6 100%);
  color: white;
}

.stat-content {
  flex: 1;
}

.stat-label {
  font-size: 13px;
  color: var(--color-text-secondary);
  margin-bottom: 6px;
  font-weight: 500;
  display: block;
}

.stat-value {
  font-size: 22px;
  font-weight: 700;
  color: var(--color-text);
  font-family: var(--font-serif);
}

.stat-email {
  font-size: 14px;
  font-weight: 500;
  word-break: break-all;
}

.stat-item-invitation {
  grid-column: span 1;
}

.invitation-code-btn {
  font-family: 'Courier New', monospace;
  font-weight: 600;
  height: 36px;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  border: none;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.invitation-code-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(249, 115, 22, 0.4);
}

/* 操作按钮 */
.action-buttons {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

/* 强制所有操作按钮使用统一大小 */
.action-buttons :deep(.ant-btn) {
  height: 48px !important;
  font-size: 14px !important;
  font-weight: 700 !important;
  border-radius: 12px !important;
  padding: 0 20px !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  gap: 8px !important;
}

.action-buttons :deep(.ant-btn:hover) {
  transform: translateY(-2px) !important;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.12) !important;
}

.action-buttons :deep(.ant-btn > .anticon) {
  font-size: 16px !important;
}

.action-btn {
  height: 48px;
  font-size: 14px;
  font-weight: 700;
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border: 2px solid transparent;
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.12);
}

.action-btn-gift {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  border: none;
  color: white;
}

.action-btn-gift:hover {
  background: linear-gradient(135deg, #059669 0%, #047857 100%);
}

.action-btn-mall {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  border: none;
  color: white;
}

.action-btn-mall:hover {
  background: linear-gradient(135deg, #d97706 0%, #b45309 100%);
}

.action-btn-edit {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  border: none;
  color: white;
}

.action-btn-edit:hover {
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
}

.action-btn-logs {
  background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%);
  border: none;
  color: white;
}

.action-btn-logs:hover {
  background: linear-gradient(135deg, #0891b2 0%, #0e7490 100%);
}

/* 我的应用卡片 */
.apps-card {
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border: 1px solid var(--color-border);
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
  font-family: var(--font-serif);
  color: var(--color-text);
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
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
}

.invitation-header {
  padding: 40px 32px 32px;
  text-align: center;
  color: white;
}

.invitation-icon {
  width: 72px;
  height: 72px;
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(10px);
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
  margin-bottom: 20px;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.invitation-title {
  font-family: var(--font-serif);
  font-size: 26px;
  font-weight: 700;
  margin: 0 0 12px 0;
  color: #5798f1;
}

.invitation-subtitle {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.9);
  margin: 0;
}

.invitation-content {
  padding: 32px;
  background: white;
  border-radius: 20px 20px 0 0;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text);
  margin-bottom: 16px;
}

.invitation-code-section {
  margin-bottom: 28px;
}

.invitation-code-display {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 24px;
  background: linear-gradient(135deg, #fff7ed 0%, #ffedd5 100%);
  border-radius: 12px;
  border: 2px solid var(--color-primary-light);
}

.invitation-code-text {
  font-family: 'Courier New', monospace;
  font-size: 32px;
  font-weight: 700;
  color: var(--color-primary);
  text-align: center;
  letter-spacing: 4px;
  padding: 12px;
}

.copy-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  border: none;
  border-radius: 10px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.copy-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(249, 115, 22, 0.4);
}

.invitation-rewards {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-bottom: 28px;
}

.reward-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid transparent;
}

.reward-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(34, 197, 94, 0.2);
  border-color: #86efac;
}

.reward-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  flex-shrink: 0;
}

.reward-content {
  flex: 1;
}

.reward-title {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin-bottom: 6px;
  font-weight: 500;
}

.reward-value {
  font-size: 22px;
  font-weight: 700;
  color: #16a34a;
  font-family: var(--font-serif);
}

.invitation-steps {
  margin-bottom: 24px;
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
  padding: 14px 18px;
  background: var(--color-bg-hover);
  border-radius: 10px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid transparent;
}

.step-item:hover {
  background: #fff7ed;
  border-color: var(--color-primary-light);
}

.step-number {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  color: white;
  border-radius: 50%;
  font-size: 13px;
  font-weight: 700;
  flex-shrink: 0;
  font-family: var(--font-serif);
}

.step-text {
  font-size: 14px;
  color: var(--color-text);
  font-weight: 500;
}

.invitation-link-section {
  margin-bottom: 16px;
}

.link-display {
  padding: 14px;
  background: var(--color-bg-hover);
  border-radius: 10px;
  word-break: break-all;
  border: 1px solid var(--color-border);
}

.link-text {
  font-size: 13px;
  color: var(--color-text-secondary);
  font-family: 'Courier New', monospace;
  line-height: 1.6;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .user-stats {
    grid-template-columns: repeat(2, 1fr);
  }

  .action-buttons {
    grid-template-columns: repeat(2, 1fr);
  }
}

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
    grid-template-columns: 1fr;
  }

  .stat-item-invitation {
    grid-column: span 1;
  }

  .action-buttons {
    grid-template-columns: 1fr;
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

/* ========== 全局字体和按钮优化 ========== */

/* Ant Design 按钮优化 */
:deep(.ant-btn-primary) {
  background: #3b82f6 !important;
  border-color: #3b82f6 !important;
  color: white !important;
  font-weight: 600 !important;
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
  color: #1e293b !important;
  border-color: #e2e8f0 !important;
  font-weight: 500 !important;
  font-size: 13px !important;
}

:deep(.ant-btn-default:hover) {
  color: #3b82f6 !important;
  border-color: #3b82f6 !important;
}

/* 表单标签优化 */
:deep(.ant-form-item-label > label) {
  color: #1e293b !important;
  font-weight: 600 !important;
}

/* 输入框文字优化 */
:deep(.ant-input),
:deep(.ant-select-selection-item) {
  color: #1e293b !important;
  font-weight: 500 !important;
}

/* 表格内容文字优化 */
:deep(.ant-table-tbody) {
  color: #1e293b !important;
}

/* Modal 标题优化 */
:deep(.ant-modal-title) {
  color: #1e293b !important;
  font-weight: 700 !important;
}

/* Tag 标签文字优化 */
:deep(.ant-tag) {
  font-weight: 600 !important;
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

/* ========== 超强力表单元素优化 ========== */

/* 表单标签 - 纯黑色 + 超粗体 */
.user-profile-page :deep(.ant-form-item-label > label) {
  color: #000000 !important;
  font-size: 15px !important;
  font-weight: 700 !important;
  letter-spacing: 0.5px;
}

/* 输入框 - 纯黑色文字 + 白色背景 */
.user-profile-page :deep(.ant-input),
.user-profile-page :deep(.ant-input-number) {
  background: #ffffff !important;
  border-color: #cbd5e1 !important;
  color: #000000 !important;
  font-weight: 700 !important;
  font-size: 14px !important;
}

.user-profile-page :deep(.ant-input::placeholder),
.user-profile-page :deep(.ant-input-number::placeholder) {
  color: #475569 !important;
  font-weight: 500 !important;
}

.user-profile-page :deep(.ant-input:hover),
.user-profile-page :deep(.ant-input-number:hover) {
  border-color: #3b82f6 !important;
}

.user-profile-page :deep(.ant-input:focus),
.user-profile-page :deep(.ant-input-number:focus),
.user-profile-page :deep(.ant-input-focused) {
  border-color: #3b82f6 !important;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1) !important;
  background: #ffffff !important;
  color: #000000 !important;
}

/* Select 选择器 */
.user-profile-page :deep(.ant-select-selector) {
  background: #ffffff !important;
  border-color: #cbd5e1 !important;
  color: #000000 !important;
  font-weight: 700 !important;
  font-size: 14px !important;
}

.user-profile-page :deep(.ant-select-selection-item) {
  color: #000000 !important;
  font-weight: 700 !important;
}

.user-profile-page :deep(.ant-select-selection-placeholder) {
  color: #475569 !important;
  font-weight: 500 !important;
}

.user-profile-page :deep(.ant-select:hover .ant-select-selector) {
  border-color: #3b82f6 !important;
}

.user-profile-page :deep(.ant-select-focused .ant-select-selector) {
  border-color: #3b82f6 !important;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1) !important;
  color: #000000 !important;
}

/* 表格内容增强 */
.user-profile-page :deep(.ant-table-tbody > tr > td) {
  color: #000000 !important;
  font-weight: 600 !important;
}

/* Modal 增强 */
.user-profile-page :deep(.ant-modal-title) {
  color: #000000 !important;
  font-weight: 700 !important;
}

.user-profile-page :deep(.ant-modal-body) {
  color: #000000 !important;
}

/* Tag 标签增强 */
.user-profile-page :deep(.ant-tag) {
  font-weight: 700 !important;
  color: #000000 !important;
}
</style>
