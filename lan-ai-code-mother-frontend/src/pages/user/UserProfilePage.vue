<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
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
import {
  getMyAccount,
  getMyInvitationCode,
  getSignInCalendar,
  getSignStatus,
  signIn,
} from '@/api/pointController'
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
const signCalendar = ref<API.PointSignInRecordVO[]>([])
const currentMonth = ref(dayjs())
const invitationCode = ref<string>('')

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
  loadSignCalendar()
  loadInvitationCode()
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

// 加载签到日历
const loadSignCalendar = async () => {
  try {
    const startDate = currentMonth.value.startOf('month').format('YYYY-MM-DD')
    const endDate = currentMonth.value.endOf('month').format('YYYY-MM-DD')
    const res = await getSignInCalendar({
      startDate,
      endDate,
    })
    if (res.data.code === 0 && res.data.data) {
      signCalendar.value = res.data.data
    }
  } catch (error) {
    console.error('加载签到日历失败：', error)
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
      await loadSignCalendar()
    } else {
      message.error(res.data.message ?? '签到失败')
    }
  } catch (error: any) {
    message.error(error?.response?.data?.message ?? '签到失败，请重试')
  } finally {
    signing.value = false
  }
}

// 判断某天是否已签到
const isDateSigned = (date: string) => {
  return signCalendar.value.some((record) => record.signDate === date)
}

// 获取某天的签到信息
const getSignInfo = (date: string) => {
  return signCalendar.value.find((record) => record.signDate === date)
}

// 切换月份
const changeMonth = (direction: 'prev' | 'next') => {
  if (direction === 'prev') {
    currentMonth.value = currentMonth.value.subtract(1, 'month')
  } else {
    currentMonth.value = currentMonth.value.add(1, 'month')
  }
  loadSignCalendar()
}

// 生成当月日历
const calendarDays = computed(() => {
  const start = currentMonth.value.startOf('month')
  const end = currentMonth.value.endOf('month')
  const days: Array<{
    date: string
    day: number
    signed: boolean
    info?: API.PointSignInRecordVO
  }> = []

  // 填充月初空白
  const startDay = start.day()
  for (let i = 0; i < startDay; i++) {
    days.push({ date: '', day: 0, signed: false })
  }

  // 填充日期
  let current = start
  while (current.isBefore(end) || current.isSame(end, 'day')) {
    const dateStr = current.format('YYYY-MM-DD')
    days.push({
      date: dateStr,
      day: current.date(),
      signed: isDateSigned(dateStr),
      info: getSignInfo(dateStr),
    })
    current = current.add(1, 'day')
  }

  return days
})

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

    <!-- 签到日历 -->
    <section class="profile-card calendar-card">
      <div class="card-header">
        <h3>签到日历</h3>
      </div>
      <div class="sign-calendar">
        <div class="calendar-header">
          <a-button class="month-nav-btn" @click="changeMonth('prev')">
            <template #icon>
              <span>←</span>
            </template>
          </a-button>
          <h4>{{ currentMonth.format('YYYY年MM月') }}</h4>
          <a-button class="month-nav-btn" @click="changeMonth('next')">
            <template #icon>
              <span>→</span>
            </template>
          </a-button>
        </div>
        <div class="calendar-grid">
          <div class="calendar-weekday">日</div>
          <div class="calendar-weekday">一</div>
          <div class="calendar-weekday">二</div>
          <div class="calendar-weekday">三</div>
          <div class="calendar-weekday">四</div>
          <div class="calendar-weekday">五</div>
          <div class="calendar-weekday">六</div>
          <div
            v-for="(day, index) in calendarDays"
            :key="index"
            :class="[
              'calendar-day',
              {
                'calendar-day-signed': day.signed,
                'calendar-day-today': day.date === dayjs().format('YYYY-MM-DD'),
                'calendar-day-empty': !day.date,
              },
            ]"
          >
            <span v-if="day.date" class="day-number">{{ day.day }}</span>
            <span v-if="day.signed" class="day-check">✓</span>
          </div>
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

.calendar-card {
  margin-top: 0;
}

.card-header {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 2px solid #f0f2f5;
}

.card-header h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #1f2d3d;
}

.sign-calendar {
  margin-top: 0;
}

.calendar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.calendar-header h4 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1f2d3d;
}

.month-nav-btn {
  border: none;
  background: transparent;
  color: #1890ff;
  cursor: pointer;
  font-size: 18px;
  padding: 4px 12px;
  border-radius: 6px;
  transition: all 0.3s;
}

.month-nav-btn:hover {
  background: #f0f2f5;
}

.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 12px;
}

.calendar-weekday {
  text-align: center;
  padding: 12px;
  font-weight: 600;
  color: #5f6b7c;
  font-size: 14px;
  background: #f8f9fa;
  border-radius: 8px;
}

.calendar-day {
  aspect-ratio: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  background: #f8f9fa;
  position: relative;
  cursor: pointer;
  transition: all 0.3s;
  min-height: 48px;
}

.calendar-day:hover {
  background: #e0f2ff;
  transform: translateY(-2px);
}

.calendar-day-empty {
  background: transparent;
  cursor: default;
}

.calendar-day-today {
  background: linear-gradient(135deg, #1890ff 0%, #6bc1ff 100%);
  color: white;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
}

.calendar-day-signed {
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(82, 196, 26, 0.3);
}

.calendar-day-signed.calendar-day-today {
  background: linear-gradient(135deg, #1890ff 0%, #6bc1ff 100%);
}

.day-number {
  font-size: 15px;
  font-weight: 500;
}

.day-check {
  position: absolute;
  top: 4px;
  right: 6px;
  font-size: 12px;
  font-weight: bold;
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

  .calendar-grid {
    gap: 8px;
  }

  .calendar-day {
    min-height: 40px;
    font-size: 13px;
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
