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
      message.success('邀请信息已复制到剪贴板，快去邀请小伙伴吧!')
    } else {
      const textArea = document.createElement('textarea')
      textArea.value = invitationText
      textArea.style.position = 'fixed'
      textArea.style.left = '-9999px'
      document.body.appendChild(textArea)
      textArea.select()
      try {
        document.execCommand('copy')
        message.success('邀请信息已复制到剪贴板，快去邀请小伙伴吧!')
      } catch {
        message.error('复制失败，请手动复制')
      }
      document.body.removeChild(textArea)
    }
  } catch {
    message.error('复制失败，请重试')
  }
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
  } catch {
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
    <!-- 用户信息卡片 - 现代化左右布局 -->
    <div class="profile-card-modern">
      <!-- 左侧：个人信息 -->
      <div class="profile-left">
        <div class="avatar-wrapper">
          <a-avatar :size="88" :src="displayAvatar" class="user-avatar-modern">
            <template #icon>
              <UserOutlined />
            </template>
          </a-avatar>
        </div>
        <div class="user-info-modern">
          <h2 class="user-name-modern">
            {{ truncateText(formState.userName || '未设置昵称', 12) }}
          </h2>
          <p class="user-account-modern">@{{ formState.userAccount }}</p>
          <p class="user-bio-modern">{{ formState.userProfile || '这个人很懒，什么都没留下~' }}</p>
          <div class="quick-actions">
            <a-button class="quick-action-btn" size="small" @click="goToEditProfile">
              <EditOutlined />
              编辑资料
            </a-button>
            <a-button class="quick-action-btn" size="small" @click="goToPointLogs">
              <HistoryOutlined />
              积分明细
            </a-button>
          </div>
        </div>
      </div>

      <!-- 右侧：数据统计 -->
      <div class="profile-right">
        <div class="stats-grid-modern">
          <!-- 积分卡片 -->
          <div class="stat-card-modern stat-card-points">
            <div class="stat-header">
              <WalletOutlined class="stat-icon-modern" />
              <span class="stat-label-modern">当前积分</span>
            </div>
            <div class="stat-value-modern">{{ accountInfo?.availablePoints ?? 0 }}</div>
            <div class="stat-actions">
              <a-tooltip title="每日签到">
                <a-button
                  :loading="signing"
                  class="stat-action-btn"
                  shape="circle"
                  size="small"
                  type="primary"
                  @click="handleSignIn"
                >
                  <GiftOutlined />
                </a-button>
              </a-tooltip>
              <a-tooltip title="积分商城">
                <a-button
                  class="stat-action-btn"
                  shape="circle"
                  size="small"
                  type="primary"
                  @click="goToPointMall"
                >
                  <ShoppingOutlined />
                </a-button>
              </a-tooltip>
            </div>
          </div>

          <!-- 加入天数 -->
          <div class="stat-card-modern stat-card-days">
            <div class="stat-header">
              <CalendarOutlined class="stat-icon-modern" />
              <span class="stat-label-modern">已加入</span>
            </div>
            <div class="stat-value-modern">{{ joinedDays }} <span class="stat-unit">天</span></div>
          </div>

          <!-- 邮箱 -->
          <div class="stat-card-modern stat-card-email">
            <div class="stat-header">
              <MailOutlined class="stat-icon-modern" />
              <span class="stat-label-modern">邮箱</span>
            </div>
            <div class="stat-value-small">{{ loginUserStore.loginUser.userEmail || '未绑定' }}</div>
          </div>

          <!-- 邀请码 -->
          <div class="stat-card-modern stat-card-invite">
            <div class="stat-header">
              <ShareAltOutlined class="stat-icon-modern" />
              <span class="stat-label-modern">邀请码</span>
            </div>
            <div class="invite-code-modern">{{ invitationCode || '加载中' }}</div>
            <a-button class="copy-invite-btn" size="small" type="link" @click="copyInvitation">
              <CopyOutlined />
              复制
            </a-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 我的应用 -->
    <a-card :bordered="false" class="apps-card">
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

<style>
/* ========== 全局变量（非scoped） ========== */
:root {
  --color-primary: #3b82f6;
  --color-primary-light: #60a5fa;
  --color-primary-dark: #2563eb;
  --color-text: #1e293b;
  --color-text-secondary: #64748b;
  --color-text-light: #94a3b8;
  --color-bg: #f8fafc;
  --color-glass: rgba(255, 255, 255, 0.8);
  --color-glass-border: rgba(255, 255, 255, 0.95);
  --shadow-sm: 0 1px 2px rgba(0, 0, 0, 0.05);
  --shadow-soft: 0 4px 16px rgba(0, 0, 0, 0.06);
  --shadow-hover: 0 8px 30px rgba(0, 0, 0, 0.1);
  --radius-md: 12px;
  --radius-lg: 20px;
  --radius-xl: 24px;
  --font-main:
    -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB',
    'Microsoft YaHei', sans-serif;
}
</style>

<style scoped>
/* ========== 字体引入 ========== */
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');

/* ========== 主容器 ========== */
.user-profile-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: 28px;
  display: flex;
  flex-direction: column;
  gap: 28px;
  font-family: var(--font-main);
}

/* ========== 现代化用户信息卡片 ========== */
.profile-card-modern {
  display: grid;
  grid-template-columns: 340px 1fr;
  gap: 32px;
  padding: 36px;
  background: var(--color-glass);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-soft);
  border: 1px solid var(--color-glass-border);
  position: relative;
  overflow: hidden;
}

/* 装饰性渐变背景 */
.profile-card-modern::before {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle at top right, rgba(59, 130, 246, 0.04) 0%, transparent 70%);
  border-radius: 50%;
  pointer-events: none;
}

/* 左侧：个人信息区 */
.profile-left {
  display: flex;
  flex-direction: column;
  gap: 20px;
  position: relative;
  z-index: 1;
  border-right: 1px solid rgba(0, 0, 0, 0.06);
  padding-right: 32px;
}

.avatar-wrapper {
  position: relative;
  display: inline-block;
}

.user-avatar-modern {
  border: 3px solid white;
  box-shadow: var(--shadow-soft);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.user-avatar-modern:hover {
  transform: scale(1.03);
  box-shadow: var(--shadow-hover);
}

.user-info-modern {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.user-name-modern {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text);
  margin: 0;
  letter-spacing: -0.3px;
}

.user-account-modern {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin: 0;
  font-weight: 500;
}

.user-bio-modern {
  font-size: 13px;
  color: var(--color-text-secondary);
  margin: 0;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.quick-actions {
  display: flex;
  gap: 10px;
  margin-top: 8px;
}

.quick-action-btn {
  height: 32px;
  padding: 0 14px;
  font-size: 13px;
  font-weight: 500;
  border-radius: var(--radius-md);
  border: 1px solid rgba(0, 0, 0, 0.1);
  background: white;
  color: var(--color-text);
  transition: all 0.2s ease;
}

.quick-action-btn:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
  background: rgba(59, 130, 246, 0.04);
}

/* 右侧：数据统计区 */
.profile-right {
  position: relative;
  z-index: 1;
}

.stats-grid-modern {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.stat-card-modern {
  padding: 20px;
  background: white;
  border-radius: var(--radius-lg);
  border: 1px solid rgba(0, 0, 0, 0.06);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.stat-card-modern::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, var(--color-primary), var(--color-primary-light));
  opacity: 0;
  transition: opacity 0.3s ease;
}

.stat-card-modern:hover {
  transform: translate3d(0, -4px, 0);
  box-shadow: var(--shadow-hover);
  border-color: rgba(59, 130, 246, 0.2);
}

.stat-card-modern:hover::before {
  opacity: 1;
}

.stat-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.stat-icon-modern {
  font-size: 18px;
  color: var(--color-primary);
}

.stat-label-modern {
  font-size: 13px;
  color: var(--color-text-secondary);
  font-weight: 500;
}

.stat-value-modern {
  font-size: 32px;
  font-weight: 700;
  color: var(--color-text);
  letter-spacing: -0.8px;
  line-height: 1.2;
  margin-bottom: 4px;
}

.stat-unit {
  font-size: 16px;
  font-weight: 500;
  color: var(--color-text-secondary);
  margin-left: 4px;
}

.stat-value-small {
  font-size: 14px;
  color: var(--color-text);
  font-weight: 500;
}

.stat-actions {
  display: flex;
  gap: 8px;
  margin-top: 12px;
}

.stat-action-btn {
  width: 36px;
  height: 36px;
  border-radius: var(--radius-md);
  background: var(--color-primary);
  border: none;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.stat-action-btn:hover {
  background: var(--color-primary-dark);
  transform: scale(1.05);
}

.invite-code-modern {
  font-family: 'SF Mono', 'Monaco', 'Inconsolata', 'Courier New', monospace;
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text);
  letter-spacing: 2px;
  margin: 4px 0;
}

.copy-invite-btn {
  padding: 0;
  height: auto;
  font-size: 13px;
  color: var(--color-primary);
  background: none;
  border: none;
}

.copy-invite-btn:hover {
  color: var(--color-primary-dark);
  background: none;
}

/* ========== 我的应用卡片 ========== */
.apps-card {
  background: var(--color-glass);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-soft);
  border: 1px solid var(--color-glass-border);
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text);
}

.apps-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

/* 分页器 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

/* ========== 响应式设计 ========== */

/* 大屏设备（1200px 及以下） */
@media (max-width: 1200px) {
  .profile-card-modern {
    grid-template-columns: 280px 1fr;
    gap: 24px;
  }

  .stats-grid-modern {
    grid-template-columns: repeat(2, 1fr);
  }
}

/* 小平板设备（968px 及以下） */
@media (max-width: 968px) {
  .profile-card-modern {
    grid-template-columns: 1fr;
    gap: 28px;
    padding: 28px;
  }

  .profile-left {
    border-right: none;
    border-bottom: 1px solid rgba(0, 0, 0, 0.06);
    padding-right: 0;
    padding-bottom: 24px;
    flex-direction: row;
    align-items: center;
    flex-wrap: wrap;
  }

  .avatar-wrapper {
    flex-shrink: 0;
  }

  .user-info-modern {
    flex: 1;
  }

  .quick-actions {
    flex-wrap: wrap;
  }

  .stats-grid-modern {
    grid-template-columns: repeat(2, 1fr);
  }
}

/* 手机设备（768px 及以下） */
@media (max-width: 768px) {
  .user-profile-page {
    padding: 16px;
    gap: 20px;
  }

  .profile-card-modern {
    padding: 20px;
  }

  .profile-left {
    flex-direction: column;
    text-align: center;
  }

  .user-info-modern {
    align-items: center;
  }

  .quick-actions {
    justify-content: center;
  }

  .stats-grid-modern {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .stat-value-modern {
    font-size: 28px;
  }

  .apps-grid {
    grid-template-columns: 1fr;
  }
}

/* 小屏手机（480px 及以下） */
@media (max-width: 480px) {
  .profile-card-modern {
    padding: 16px;
  }

  .user-avatar-modern {
    width: 72px !important;
    height: 72px !important;
  }

  .user-name-modern {
    font-size: 20px;
  }

  .stat-card-modern {
    padding: 16px;
  }

  .stat-value-modern {
    font-size: 24px;
  }
}

/* ========== 邀请卡片弹窗 ========== */
.invitation-modal :deep(.ant-modal-content) {
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-hover);
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
  font-size: 26px;
  font-weight: 700;
  margin: 0 0 12px 0;
  color: white;
}

.invitation-subtitle {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.9);
  margin: 0;
}

.invitation-content {
  padding: 32px;
  background: white;
  border-radius: var(--radius-lg) var(--radius-lg) 0 0;
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
  background: linear-gradient(135deg, #fff5f5 0%, #ffe5e5 100%);
  border-radius: var(--radius-md);
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
  border-radius: var(--radius-sm);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  transform: translateZ(0);
}

.copy-btn:hover {
  transform: translate3d(0, -2px, 0);
  box-shadow: var(--shadow-hover);
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
  background: linear-gradient(135deg, #f0fff4 0%, #dcfce7 100%);
  border-radius: var(--radius-md);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid transparent;
  transform: translateZ(0);
}

.reward-item:hover {
  transform: translate3d(0, -2px, 0);
  box-shadow: 0 4px 12px rgba(34, 197, 94, 0.2);
  border-color: #86efac;
}

.reward-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
  border-radius: var(--radius-md);
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
  background: var(--color-bg);
  border-radius: var(--radius-sm);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid transparent;
  transform: translateZ(0);
}

.step-item:hover {
  background: #fff5f5;
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
  background: var(--color-bg);
  border-radius: var(--radius-sm);
  word-break: break-all;
  border: 1px solid rgba(255, 107, 107, 0.2);
}

.link-text {
  font-size: 13px;
  color: var(--color-text-secondary);
  font-family: 'Courier New', monospace;
  line-height: 1.6;
}

/* ========== 响应式设计 ========== */

/* 大屏设备（1280px 及以下） */
@media (max-width: 1280px) {
  .user-stats {
    grid-template-columns: 1fr 1fr;
  }

  .action-buttons {
    grid-template-columns: 1fr 1fr;
  }
}

/* 小平板设备（768px 及以下） */
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

/* ========== 全局样式覆盖 ========== */
:deep(.ant-btn-primary) {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  border-color: var(--color-primary);
  color: white;
  font-weight: 600;
  font-size: 14px;
  height: 36px;
  padding: 0 20px;
  border-radius: var(--radius-sm);
}

:deep(.ant-btn-primary:hover) {
  background: linear-gradient(135deg, var(--color-primary-dark) 0%, #e63939 100%);
  border-color: var(--color-primary-dark);
  transform: translate3d(0, -1px, 0);
}

:deep(.ant-form-item-label > label) {
  color: var(--color-text);
  font-weight: 600;
  font-size: 14px;
}

:deep(.ant-input) {
  color: var(--color-text);
  font-weight: 500;
  font-size: 14px;
}

:deep(.ant-modal-title) {
  color: var(--color-text);
  font-weight: 700;
  font-size: 18px;
}

:deep(.ant-tag) {
  font-weight: 600;
}

:deep(.ant-table-tbody) {
  color: var(--color-text);
}
</style>
