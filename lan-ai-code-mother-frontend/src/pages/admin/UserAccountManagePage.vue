<template>
  <div class="user-account-container">
    <!-- Hero 背景 -->
    <div class="hero-background">
      <div class="hero-overlay"></div>
    </div>

    <!-- 背景装饰元素 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>

    <!-- 主内容区 -->
    <div class="content-wrapper">
      <!-- 页面头部 -->
      <div class="page-header">
        <div class="header-left">
          <div class="icon-wrapper">
            <WalletOutlined class="header-icon" />
          </div>
          <div class="header-text">
            <h1 class="page-title">积分账户管理</h1>
            <p class="page-subtitle">
              <DatabaseOutlined />
              管理用户积分账户与发放奖励
            </p>
          </div>
        </div>
      </div>

      <!-- 主卡片 -->
      <div class="main-card info-card">
        <!-- 搜索区域 -->
        <div class="search-section">
          <div class="search-header">
            <FilterOutlined class="search-icon" />
            <span class="search-title">筛选条件</span>
          </div>
          <a-form :model="searchParams" class="search-form" layout="vertical" @finish="doSearch">
            <div class="search-grid">
              <a-form-item label="用户ID">
                <a-input
                  v-model:value="searchParams.userId"
                  placeholder="请输入用户ID"
                  size="default"
                >
                  <template #prefix>
                    <UserOutlined class="input-icon" />
                  </template>
                </a-input>
              </a-form-item>
              <a-form-item label="邀请码">
                <a-input
                  v-model:value="searchParams.invitationCode"
                  placeholder="请输入邀请码"
                  size="default"
                >
                  <template #prefix>
                    <KeyOutlined class="input-icon" />
                  </template>
                </a-input>
              </a-form-item>
              <a-form-item label="可用积分范围">
                <div class="range-inputs">
                  <a-input-number
                    v-model:value="searchParams.minAvailablePoints"
                    :min="0"
                    placeholder="最小"
                    size="default"
                    style="width: 100%"
                  />
                  <span class="range-separator">-</span>
                  <a-input-number
                    v-model:value="searchParams.maxAvailablePoints"
                    :min="0"
                    placeholder="最大"
                    size="default"
                    style="width: 100%"
                  />
                </div>
              </a-form-item>
              <a-form-item label="累计积分范围">
                <div class="range-inputs">
                  <a-input-number
                    v-model:value="searchParams.minTotalPoints"
                    :min="0"
                    placeholder="最小"
                    size="default"
                    style="width: 100%"
                  />
                  <span class="range-separator">-</span>
                  <a-input-number
                    v-model:value="searchParams.maxTotalPoints"
                    :min="0"
                    placeholder="最大"
                    size="default"
                    style="width: 100%"
                  />
                </div>
              </a-form-item>
            </div>
            <div class="search-actions">
              <a-button html-type="submit" size="default" type="primary" @click="doSearch">
                <template #icon>
                  <SearchOutlined />
                </template>
                搜索
              </a-button>
              <a-button size="default" type="primary" @click="resetSearch">
                <template #icon>
                  <ReloadOutlined />
                </template>
                重置
              </a-button>
              <a-button danger size="default" type="primary" @click="showGrantAllModal">
                <template #icon>
                  <GiftOutlined />
                </template>
                批量奖励
              </a-button>
            </div>
          </a-form>
        </div>

        <a-divider class="section-divider" />

        <!-- 统计卡片 -->
        <div class="stats-section">
          <div class="section-title">
            <BarChartOutlined class="title-icon" />
            <span>数据统计</span>
          </div>
          <a-row :gutter="16" class="stats-row">
            <a-col :lg="6" :md="12" :sm="24" :xs="24">
              <div class="stat-card stat-users">
                <div class="stat-icon-wrapper">
                  <UserOutlined class="stat-icon" />
                </div>
                <div class="stat-content">
                  <div class="stat-value">{{ totalUsers }}</div>
                  <div class="stat-label">总用户数</div>
                </div>
              </div>
            </a-col>
            <a-col :lg="6" :md="12" :sm="24" :xs="24">
              <div class="stat-card stat-points">
                <div class="stat-icon-wrapper">
                  <GiftOutlined class="stat-icon" />
                </div>
                <div class="stat-content">
                  <div class="stat-value">{{ totalPoints }}</div>
                  <div class="stat-label">累计获得积分</div>
                </div>
              </div>
            </a-col>
            <a-col :lg="6" :md="12" :sm="24" :xs="24">
              <div class="stat-card stat-consume">
                <div class="stat-icon-wrapper">
                  <ShoppingCartOutlined class="stat-icon" />
                </div>
                <div class="stat-content">
                  <div class="stat-value">{{ totalConsume }}</div>
                  <div class="stat-label">累计消耗积分</div>
                </div>
              </div>
            </a-col>
            <a-col :lg="6" :md="12" :sm="24" :xs="24">
              <div class="stat-card stat-invites">
                <div class="stat-icon-wrapper">
                  <TeamOutlined class="stat-icon" />
                </div>
                <div class="stat-content">
                  <div class="stat-value">{{ totalInvites }}</div>
                  <div class="stat-label">总邀请人数</div>
                </div>
              </div>
            </a-col>
          </a-row>
        </div>

        <a-divider class="section-divider" />

        <!-- 数据表格 -->
        <div class="table-section">
          <div class="table-header">
            <div class="table-header-left">
              <TableOutlined class="table-icon" />
              <span class="table-title">账户列表</span>
            </div>
            <a-button :icon="h(ReloadOutlined)" class="refresh-btn" size="large" @click="fetchData">
              刷新
            </a-button>
          </div>

          <a-table
            :columns="columns"
            :data-source="data"
            :pagination="pagination"
            :scroll="{ x: 1800 }"
            class="account-table"
            @change="doTableChange"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.dataIndex === 'userId'">
                <a-tag class="id-tag">
                  <IdcardOutlined />
                  {{ record.userId }}
                </a-tag>
              </template>
              <template v-if="column.dataIndex === 'invitationCode'">
                <a-tag class="code-tag">
                  <KeyOutlined />
                  {{ record.invitationCode }}
                </a-tag>
              </template>
              <template v-if="column.dataIndex === 'availablePoints'">
                <a-tag class="points-tag available">
                  <DollarOutlined />
                  {{ record.availablePoints }}
                </a-tag>
              </template>
              <template v-if="column.dataIndex === 'totalPoints'">
                <a-tag class="points-tag total">
                  <RiseOutlined />
                  {{ record.totalPoints }}
                </a-tag>
              </template>
              <template v-if="column.dataIndex === 'freezePoints'">
                <a-tag class="points-tag freeze">
                  <LockOutlined />
                  {{ record.freezePoints || 0 }}
                </a-tag>
              </template>
              <template v-if="column.dataIndex === 'totalConsume'">
                <a-tag class="points-tag consume">
                  <FallOutlined />
                  {{ record.totalConsume }}
                </a-tag>
              </template>
              <template v-if="column.dataIndex === 'continuousDays'">
                <a-tag
                  :class="{
                    'days-gold': record.continuousDays >= 7,
                    'days-cyan': record.continuousDays >= 3 && record.continuousDays < 7,
                    'days-default': record.continuousDays < 3,
                  }"
                  class="days-tag"
                >
                  <CalendarOutlined />
                  {{ record.continuousDays }} 天
                </a-tag>
              </template>
              <template v-if="column.dataIndex === 'invitationCount'">
                <a-tag class="invite-tag">
                  <TeamOutlined />
                  {{ record.invitationCount }} 人
                </a-tag>
              </template>
              <template v-if="column.dataIndex === 'totalInvitePoints'">
                <a-tag class="invite-points-tag">
                  <TrophyOutlined />
                  {{ record.totalInvitePoints }}
                </a-tag>
              </template>
              <template v-if="column.dataIndex === 'lastSignDate'">
                <div class="date-cell">
                  <CalendarOutlined class="date-icon" />
                  <span>{{ record.lastSignDate || '未签到' }}</span>
                </div>
              </template>
              <template v-if="column.key === 'action'">
                <a-button size="large" type="primary" @click="showGrantModal(record)">
                  <template #icon>
                    <GiftOutlined />
                  </template>
                  发放积分
                </a-button>
              </template>
            </template>
          </a-table>
        </div>
      </div>
    </div>

    <!-- 发放积分弹窗 -->
    <a-modal
      v-model:open="grantModalVisible"
      :centered="true"
      :footer="null"
      class="grant-modal"
      title="积分奖励"
      width="560px"
      @cancel="resetGrantForm"
    >
      <div class="grant-modal-content">
        <div class="modal-header">
          <GiftOutlined class="modal-icon" />
          <span class="modal-title">为用户发放积分</span>
        </div>
        <a-form :label-col="{ span: 5 }" :model="grantForm" :wrapper-col="{ span: 19 }">
          <a-form-item label="用户ID">
            <a-input v-model:value="grantForm.userId" class="form-input" disabled size="large" />
          </a-form-item>
          <a-form-item
            :rules="[
              { required: true, message: '请输入积分数' },
              { pattern: /^[1-9]\d*$/, message: '积分数必须为大于0的整数' },
            ]"
            label="积分数"
            name="points"
          >
            <a-input
              v-model:value="grantForm.points"
              class="form-input"
              placeholder="请输入要发放的积分数"
              size="large"
            >
              <template #prefix>
                <DollarOutlined class="input-icon" />
              </template>
            </a-input>
          </a-form-item>
          <a-form-item
            :rules="[{ required: true, message: '请输入备注' }]"
            label="备注"
            name="remark"
          >
            <a-textarea
              v-model:value="grantForm.remark"
              :rows="4"
              class="form-input"
              placeholder="请输入发放积分的备注说明"
            />
          </a-form-item>
        </a-form>
        <div class="modal-footer">
          <a-button class="cancel-btn" size="large" @click="resetGrantForm"> 取消</a-button>
          <a-button
            :loading="granting"
            class="confirm-btn"
            size="large"
            type="primary"
            @click="handleGrant"
          >
            <template #icon>
              <GiftOutlined />
            </template>
            确认发放
          </a-button>
        </div>
      </div>
    </a-modal>

    <!-- 批量发放积分弹窗 -->
    <a-modal
      v-model:open="grantAllModalVisible"
      :centered="true"
      :footer="null"
      class="grant-modal"
      title="批量积分奖励"
      width="560px"
      @cancel="resetGrantAllForm"
    >
      <div class="grant-modal-content">
        <div class="modal-header">
          <GiftOutlined class="modal-icon" />
          <span class="modal-title">批量发放积分</span>
        </div>
        <a-alert
          class="warning-alert"
          message="此操作将给所有用户发放积分，请谨慎操作！"
          show-icon
          type="warning"
        >
          <template #icon>
            <ExclamationCircleOutlined />
          </template>
        </a-alert>
        <a-form :label-col="{ span: 5 }" :model="grantAllForm" :wrapper-col="{ span: 19 }">
          <a-form-item
            :rules="[
              { required: true, message: '请输入积分数' },
              { pattern: /^[1-9]\d*$/, message: '积分数必须为大于0的整数' },
            ]"
            label="积分数"
            name="points"
          >
            <a-input
              v-model:value="grantAllForm.points"
              class="form-input"
              placeholder="请输入要发放的积分数"
              size="large"
            >
              <template #prefix>
                <DollarOutlined class="input-icon" />
              </template>
            </a-input>
          </a-form-item>
          <a-form-item
            :rules="[{ required: true, message: '请输入备注' }]"
            label="备注"
            name="remark"
          >
            <a-textarea
              v-model:value="grantAllForm.remark"
              :rows="4"
              class="form-input"
              placeholder="请输入发放积分的备注说明"
            />
          </a-form-item>
        </a-form>
        <div class="modal-footer">
          <a-button class="cancel-btn" size="large" @click="resetGrantAllForm"> 取消</a-button>
          <a-button
            :loading="granting"
            class="confirm-btn"
            size="large"
            type="primary"
            @click="handleGrantAll"
          >
            <template #icon>
              <GiftOutlined />
            </template>
            确认发放
          </a-button>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
import { computed, h, onMounted, ref } from 'vue'
import { message } from 'ant-design-vue'
import {
  BarChartOutlined,
  CalendarOutlined,
  DatabaseOutlined,
  DollarOutlined,
  ExclamationCircleOutlined,
  FallOutlined,
  FilterOutlined,
  GiftOutlined,
  IdcardOutlined,
  KeyOutlined,
  LockOutlined,
  ReloadOutlined,
  RiseOutlined,
  SearchOutlined,
  ShoppingCartOutlined,
  TableOutlined,
  TeamOutlined,
  TrophyOutlined,
  UserOutlined,
  WalletOutlined,
} from '@ant-design/icons-vue'
import { grantPoints, grantPointsToAll, listAccounts } from '@/api/pointController'

// 搜索参数
const searchParams = ref<API.UserAccountQueryRequest>({
  pageNum: 1,
  pageSize: 10,
})

// 数据列表
const data = ref<API.UserAccountVO[]>([])

// 表格列配置
const columns = [
  { title: '用户ID', dataIndex: 'userId', width: 80, fixed: 'left' as const },
  { title: '邀请码', dataIndex: 'invitationCode', width: 50 },
  { title: '可用积分', dataIndex: 'availablePoints', width: 50 },
  { title: '累计积分', dataIndex: 'totalPoints', width: 50 },
  { title: '冻结积分', dataIndex: 'freezePoints', width: 50 },
  { title: '累计消耗', dataIndex: 'totalConsume', width: 50 },
  { title: '连续签到天数', dataIndex: 'continuousDays', width: 50 },
  { title: '邀请人数', dataIndex: 'invitationCount', width: 50 },
  { title: '邀请奖励积分', dataIndex: 'totalInvitePoints', width: 50 },
  { title: '最后签到日期', dataIndex: 'lastSignDate', width: 50 },
  { title: '操作', key: 'action', width: 100 },
]

// 分页配置
const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条`,
})

// 统计数据
const totalUsers = computed(() => pagination.value.total)
const totalPoints = computed(() =>
  data.value.reduce((sum, item) => sum + Number(item.totalPoints || 0), 0),
)
const totalConsume = computed(() =>
  data.value.reduce((sum, item) => sum + Number(item.totalConsume || 0), 0),
)
const totalInvites = computed(() =>
  data.value.reduce((sum, item) => sum + Number(item.invitationCount || 0), 0),
)

// 获取数据
const fetchData = async () => {
  try {
    const res = await listAccounts(searchParams.value)
    if (res.data.code === 0 && res.data.data) {
      data.value = res.data.data.records || []
      pagination.value.current = res.data.data.pageNumber || 1
      pagination.value.pageSize = res.data.data.pageSize || 10
      pagination.value.total = res.data.data.totalRow || 0
    }
  } catch (error) {
    message.error('获取数据失败')
  }
}

// 搜索
const doSearch = () => {
  searchParams.value.pageNum = 1
  pagination.value.current = 1
  fetchData()
}

// 重置搜索
const resetSearch = () => {
  searchParams.value = {
    pageNum: 1,
    pageSize: 10,
  }
  fetchData()
}

// 表格变化
const doTableChange = (pag: any) => {
  searchParams.value.pageNum = pag.current
  searchParams.value.pageSize = pag.pageSize
  pagination.value.current = pag.current
  pagination.value.pageSize = pag.pageSize
  fetchData()
}

// 发放积分弹窗相关
const grantModalVisible = ref(false)
const granting = ref(false)
const grantForm = ref({
  userId: '',
  points: '',
  remark: '',
})

// 显示发放积分弹窗
const showGrantModal = (record: API.UserAccountVO) => {
  grantForm.value = {
    userId: String(record.userId),
    points: '',
    remark: '',
  }
  grantModalVisible.value = true
}

// 重置发放积分表单
const resetGrantForm = () => {
  grantForm.value = {
    userId: '',
    points: undefined,
    remark: '',
  }
  grantModalVisible.value = false
}

// 处理发放积分
const handleGrant = async () => {
  if (!grantForm.value.userId || !grantForm.value.points || !grantForm.value.remark) {
    message.error('请填写完整信息')
    return
  }

  granting.value = true
  try {
    const res = await grantPoints({
      userId: grantForm.value.userId as unknown as number,
      points: grantForm.value.points,
      remark: grantForm.value.remark,
    })
    if (res.data.code === 0) {
      message.success('✅ 积分发放成功')
      resetGrantForm()
      fetchData()
    } else {
      message.error(res.data.message || '发放失败')
    }
  } catch (error) {
    message.error('发放失败，请重试')
  } finally {
    granting.value = false
  }
}

// 批量发放积分弹窗相关
const grantAllModalVisible = ref(false)
const grantAllForm = ref({
  points: '',
  remark: '',
})

// 显示批量发放积分弹窗
const showGrantAllModal = () => {
  grantAllForm.value = {
    points: '',
    remark: '',
  }
  grantAllModalVisible.value = true
}

// 重置批量发放积分表单
const resetGrantAllForm = () => {
  grantAllForm.value = {
    points: undefined,
    remark: '',
  }
  grantAllModalVisible.value = false
}

// 处理批量发放积分
const handleGrantAll = async () => {
  if (!grantAllForm.value.points || !grantAllForm.value.remark) {
    message.error('请填写完整信息')
    return
  }

  granting.value = true
  try {
    const res = await grantPointsToAll({
      points: grantAllForm.value.points,
      remark: grantAllForm.value.remark,
    })
    if (res.data.code === 0) {
      message.success(`✅ 成功给 ${res.data.data} 个用户发放积分`)
      resetGrantAllForm()
      fetchData()
    } else {
      message.error(res.data.message || '发放失败')
    }
  } catch (error) {
    message.error('发放失败，请重试')
  } finally {
    granting.value = false
  }
}

// 页面加载
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Serif+SC:wght@400;600;700&family=Noto+Sans+SC:wght@300;400;500;600;700&display=swap');

:root {
  --color-primary: #f97316;
  --color-primary-dark: #ea580c;
  --color-primary-light: #fbbf24;
  --color-text: #1e293b;
  --color-text-secondary: #64748b;
  --color-border: #e2e8f0;
  --color-bg-hover: #f8fafc;
  --font-serif: 'Noto Serif SC', serif;
  --font-sans: 'Noto Sans SC', sans-serif;
}

.user-account-container {
  min-height: 100vh;
  padding: 0;
  position: relative;
  overflow: hidden;
}

/* Hero 背景 */
.hero-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: url('https://images.unsplash.com/photo-1554224155-8d04cb21cd6c?w=1920&q=80');
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
  z-index: 0;
}

.hero-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    135deg,
    rgba(249, 115, 22, 0.98) 0%,
    rgba(234, 88, 12, 0.85) 50%,
    rgba(251, 191, 36, 0.82) 100%
  );
  backdrop-filter: blur(2px);
}

/* 背景装饰 */
.bg-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  overflow: hidden;
  z-index: 1;
  pointer-events: none;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.08);
  animation: float 20s infinite ease-in-out;
}

.circle-1 {
  width: 400px;
  height: 400px;
  top: -150px;
  right: -100px;
  animation-delay: 0s;
}

.circle-2 {
  width: 250px;
  height: 250px;
  bottom: 20%;
  left: -80px;
  animation-delay: 7s;
}

.circle-3 {
  width: 180px;
  height: 180px;
  bottom: 15%;
  right: 15%;
  animation-delay: 12s;
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0) scale(1);
  }
  50% {
    transform: translateY(-40px) scale(1.15);
  }
}

/* 主内容区 */
.content-wrapper {
  position: relative;
  z-index: 2;
  padding: 32px;
  animation: fadeIn 0.6s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 页面头部 */
.page-header {
  margin-bottom: 28px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.icon-wrapper {
  width: 72px;
  height: 72px;
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 36px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  border: 2px solid rgba(255, 255, 255, 0.3);
}

.header-icon {
  color: white;
}

.header-text {
  color: white;
}

.page-title {
  font-family: var(--font-serif);
  font-size: 36px;
  font-weight: 700;
  margin: 0 0 8px 0;
  color: white;
  letter-spacing: -0.5px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.5);
}

.page-subtitle {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  margin: 0;
  opacity: 0.95;
  font-weight: 400;
}

/* 主卡片 */
.main-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  padding: 32px;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

/* 搜索区域 */
.search-section {
  margin-bottom: 0;
}

.search-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 2px solid var(--color-border);
}

.search-icon {
  font-size: 18px;
  color: var(--color-primary);
}

.search-title {
  font-family: var(--font-serif);
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text);
}

.search-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.search-form :deep(.ant-form-item) {
  margin-bottom: 0;
}

.search-form :deep(.ant-form-item-label > label) {
  font-weight: 600;
  color: var(--color-text);
  font-size: 12px;
}

.search-form :deep(.ant-input),
.search-form :deep(.ant-input-number) {
  border-radius: 8px;
  height: 32px;
  border: 2px solid var(--color-border);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.search-form :deep(.ant-input:hover),
.search-form :deep(.ant-input-number:hover) {
  border-color: var(--color-primary);
}

.search-form :deep(.ant-input:focus),
.search-form :deep(.ant-input-focused),
.search-form :deep(.ant-input-number:focus),
.search-form :deep(.ant-input-number-focused) {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 4px rgba(249, 115, 22, 0.1);
}

.range-inputs {
  display: flex;
  align-items: center;
  gap: 12px;
}

.range-separator {
  color: var(--color-text-secondary);
  font-weight: 600;
}

.input-icon {
  color: var(--color-text-secondary);
  font-size: 16px;
}

.search-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.search-actions :deep(.ant-btn) {
  height: 32px;
  border-radius: 8px;
  font-weight: 600;
  padding: 0 16px;
  font-size: 13px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.search-actions :deep(.ant-btn-primary) {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(249, 115, 22, 0.3);
}

.search-actions :deep(.ant-btn-primary:hover) {
  background: linear-gradient(135deg, var(--color-primary-dark) 0%, #dc2626 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(249, 115, 22, 0.4);
}

.search-actions :deep(.ant-btn-default) {
  border: 2px solid var(--color-border);
  color: var(--color-text-secondary);
}

.search-actions :deep(.ant-btn-default:hover) {
  border-color: var(--color-primary);
  color: var(--color-primary);
}

.search-actions :deep(.ant-btn-dangerous) {
  background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(220, 38, 38, 0.3);
}

.search-actions :deep(.ant-btn-dangerous:hover) {
  background: linear-gradient(135deg, #b91c1c 0%, #991b1b 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(220, 38, 38, 0.4);
}

/* 分隔线 */
.section-divider {
  margin: 28px 0;
  border-color: var(--color-border);
}

/* 统计卡片 */
.stats-section {
  margin: 0;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 2px solid var(--color-border);
}

.title-icon {
  font-size: 20px;
  color: var(--color-primary);
}

.section-title span {
  font-family: var(--font-serif);
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text);
}

.stats-row {
  margin: 0 -8px !important;
}

.stat-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border: 2px solid var(--color-border);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  margin-bottom: 16px;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.12);
}

.stat-icon-wrapper {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: white;
}

.stat-users .stat-icon-wrapper {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
}

.stat-points .stat-icon-wrapper {
  background: linear-gradient(135deg, var(--color-primary-light) 0%, var(--color-primary) 100%);
}

.stat-consume .stat-icon-wrapper {
  background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
}

.stat-invites .stat-icon-wrapper {
  background: linear-gradient(135deg, #a78bfa 0%, #8b5cf6 100%);
}

.stat-icon {
  font-size: 28px;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-family: var(--font-serif);
  font-size: 32px;
  font-weight: 700;
  line-height: 1;
  margin-bottom: 8px;
  color: var(--color-text);
}

.stat-label {
  font-size: 14px;
  color: var(--color-text-secondary);
  font-weight: 500;
}

/* 表格区域 */
.table-section {
  margin-top: 0;
}

.table-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 2px solid var(--color-border);
}

.table-header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.table-icon {
  font-size: 18px;
  color: var(--color-primary);
}

.table-title {
  font-family: var(--font-serif);
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text);
}

.refresh-btn {
  height: 32px;
  border-radius: 10px;
  font-weight: 600;
  border: 2px solid var(--color-border);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.refresh-btn:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
  transform: translateY(-2px);
}

/* 表格样式 */
.account-table :deep(.ant-table) {
  background: white;
  border-radius: 12px;
  overflow: hidden;
}

.account-table :deep(.ant-table-thead > tr > th) {
  background: #3b82f6;
  color: white;
  font-weight: 700;
  font-size: 12px;
  padding: 12px;
  border: none;
  letter-spacing: 0.5px;
}

.account-table :deep(.ant-table-tbody > tr > td) {
  padding: 12px;
  font-size: 12px;
  border-bottom: 1px solid var(--color-border);
}

.account-table :deep(.ant-table-tbody > tr:hover > td) {
  background: var(--color-bg-hover);
}

/* 各种标签 */
.id-tag,
.code-tag {
  font-size: 11px;
  padding: 4px 10px;
  border-radius: 6px;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  border: none;
}

.id-tag {
  background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
  color: #2563eb;
}

.code-tag {
  background: linear-gradient(135deg, #f3e8ff 0%, #e9d5ff 100%);
  color: #9333ea;
}

.points-tag {
  font-size: 11px;
  padding: 4px 10px;
  border-radius: 6px;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  border: none;
}

.points-tag.available {
  background: linear-gradient(135deg, #dcfce7 0%, #bbf7d0 100%);
  color: #16a34a;
}

.points-tag.total {
  background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
  color: #2563eb;
}

.points-tag.freeze {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  color: #d97706;
}

.points-tag.consume {
  background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
  color: #dc2626;
}

.days-tag {
  font-size: 11px;
  padding: 4px 10px;
  border-radius: 6px;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  border: none;
}

.days-gold {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  color: #d97706;
}

.days-cyan {
  background: linear-gradient(135deg, #cffafe 0%, #a5f3fc 100%);
  color: #0891b2;
}

.days-default {
  background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);
  color: #64748b;
}

.invite-tag {
  font-size: 11px;
  padding: 4px 10px;
  border-radius: 6px;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  border: none;
  background: linear-gradient(135deg, #f3e8ff 0%, #e9d5ff 100%);
  color: #9333ea;
}

.invite-points-tag {
  font-size: 11px;
  padding: 4px 10px;
  border-radius: 6px;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  border: none;
  background: linear-gradient(135deg, #ffedd5 0%, #fed7aa 100%);
  color: #ea580c;
}

.date-cell {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 11px;
  color: var(--color-text-secondary);
}

.date-icon {
  color: var(--color-primary);
  font-size: 14px;
}

/* 操作按钮 */
.account-table :deep(.ant-btn-primary) {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  border: none;
  border-radius: 10px;
  font-weight: 600;
  height: 32px;
  padding: 0 20px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.account-table :deep(.ant-btn-primary:hover) {
  background: linear-gradient(135deg, var(--color-primary-dark) 0%, #dc2626 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(249, 115, 22, 0.4);
}

/* 分页样式 */
.account-table :deep(.ant-pagination) {
  margin-top: 24px;
}

.account-table :deep(.ant-pagination-item-active) {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  border-color: transparent;
}

.account-table :deep(.ant-pagination-item-active a) {
  color: white;
}

/* 弹窗样式 */
:deep(.grant-modal) .ant-modal-content {
  border-radius: 20px;
  overflow: hidden;
}

:deep(.grant-modal) .ant-modal-header {
  border-bottom: none;
  padding-bottom: 0;
}

.grant-modal-content {
  padding: 8px 0;
}

.modal-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 2px solid var(--color-border);
}

.modal-icon {
  font-size: 28px;
  color: var(--color-primary);
}

.modal-title {
  font-family: var(--font-serif);
  font-size: 20px;
  font-weight: 600;
  color: var(--color-text);
}

.warning-alert {
  margin-bottom: 24px;
  padding: 12px 16px;
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  border: none;
  border-radius: 12px;
}

.warning-alert :deep(.ant-alert-icon) {
  color: #d97706;
}

.warning-alert :deep(.ant-alert-message) {
  color: #d97706;
  font-size: 14px;
  font-weight: 500;
}

:deep(.ant-form) .ant-form-item-label > label {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-text);
}

:deep(.ant-form) .ant-form-item {
  margin-bottom: 20px;
}

.form-input :deep(.ant-input),
.form-input :deep(.ant-input-number),
.form-input :deep(textarea) {
  border-radius: 12px;
  border: 2px solid var(--color-border);
  transition: all 0.3s;
}

.form-input :deep(.ant-input:hover),
.form-input :deep(.ant-input-number:hover) {
  border-color: var(--color-primary);
}

.form-input :deep(.ant-input:focus),
.form-input :deep(.ant-input-focused),
.form-input :deep(textarea:focus) {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 4px rgba(249, 115, 22, 0.1);
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 2px solid var(--color-border);
}

.cancel-btn {
  border-radius: 12px;
  height: 48px;
  padding: 0 28px;
  font-size: 13px;
  font-weight: 600;
  color: var(--color-text-secondary);
  border: 2px solid var(--color-border);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.cancel-btn:hover {
  color: var(--color-primary);
  border-color: var(--color-primary);
}

.confirm-btn {
  border-radius: 12px;
  height: 48px;
  padding: 0 32px;
  font-size: 13px;
  font-weight: 600;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(249, 115, 22, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.confirm-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, var(--color-primary-dark) 0%, #dc2626 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(249, 115, 22, 0.4);
}

.confirm-btn:disabled {
  opacity: 0.6;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .search-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .range-inputs {
    flex-direction: column;
  }
}

@media (max-width: 768px) {
  .content-wrapper {
    padding: 20px;
  }

  .main-card {
    padding: 24px;
  }

  .page-title {
    font-size: 28px;
  }

  .search-grid {
    grid-template-columns: 1fr;
  }

  .search-actions {
    flex-direction: column;
  }

  .search-actions :deep(.ant-btn) {
    width: 100%;
  }

  .circle {
    display: none;
  }
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
</style>
