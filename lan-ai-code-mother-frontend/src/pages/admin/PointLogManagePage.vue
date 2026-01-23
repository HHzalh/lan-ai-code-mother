<template>
  <div class="point-log-manage-page">
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
            <TransactionOutlined class="header-icon" />
          </div>
          <div class="header-text">
            <h1 class="page-title">积分流水管理</h1>
            <p class="page-subtitle">
              <FileTextOutlined />
              查看与管理所有用户积分变动记录
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
              <a-form-item label="业务类型">
                <a-select
                  v-model:value="searchParams.businessType"
                  allow-clear
                  placeholder="选择业务类型"
                  size="default"
                >
                  <a-select-option value="">全部</a-select-option>
                  <a-select-option value="SIGN_IN">
                    <CheckCircleOutlined class="option-icon" />
                    签到
                  </a-select-option>
                  <a-select-option value="REGISTER_REWARD">
                    <GiftOutlined class="option-icon" />
                    注册奖励
                  </a-select-option>
                  <a-select-option value="INVITEE_BONUS">
                    <UserAddOutlined class="option-icon" />
                    被邀请人奖励
                  </a-select-option>
                  <a-select-option value="INVITER_BONUS">
                    <TeamOutlined class="option-icon" />
                    邀请人奖励
                  </a-select-option>
                  <a-select-option value="GENERATE">
                    <CodeOutlined class="option-icon" />
                    创建应用
                  </a-select-option>
                  <a-select-option value="MESSAGE">
                    <MessageOutlined class="option-icon" />
                    AI对话
                  </a-select-option>
                  <a-select-option value="DEPLOY">
                    <RocketOutlined class="option-icon" />
                    部署
                  </a-select-option>
                  <a-select-option value="DOWNLOAD">
                    <DownloadOutlined class="option-icon" />
                    下载代码
                  </a-select-option>
                  <a-select-option value="SYSTEM_GRANT">
                    <BankOutlined class="option-icon" />
                    系统发放
                  </a-select-option>
                </a-select>
              </a-form-item>
              <a-form-item label="积分类型">
                <a-select
                  v-model:value="searchParams.pointType"
                  allow-clear
                  placeholder="选择积分类型"
                  size="default"
                >
                  <a-select-option value="">全部</a-select-option>
                  <a-select-option value="INCOME">
                    <ArrowUpOutlined class="option-icon income-icon" />
                    收入
                  </a-select-option>
                  <a-select-option value="EXPENSE">
                    <ArrowDownOutlined class="option-icon expense-icon" />
                    支出
                  </a-select-option>
                </a-select>
              </a-form-item>
            </div>
            <div class="search-actions">
              <a-button html-type="submit" size="default" type="primary" @click="doSearch">
                <template #icon>
                  <SearchOutlined />
                </template>
                搜索
              </a-button>
              <a-button size="default" @click="resetSearch">
                <template #icon>
                  <ReloadOutlined />
                </template>
                重置
              </a-button>
            </div>
          </a-form>
        </div>

        <a-divider class="section-divider" />

        <!-- 数据表格 -->
        <div class="table-section">
          <div class="table-header">
            <TableOutlined class="table-icon" />
            <span class="table-title">积分流水列表</span>
            <a-button :icon="h(ReloadOutlined)" class="refresh-btn" size="large" @click="fetchData">
              刷新
            </a-button>
          </div>

          <a-table
            :columns="columns"
            :data-source="data"
            :pagination="pagination"
            :scroll="{ x: 1600 }"
            class="data-table"
            @change="doTableChange"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.dataIndex === 'userId'">
                <a-tag class="id-tag">
                  <IdcardOutlined />
                  {{ record.userId }}
                </a-tag>
              </template>
              <template v-else-if="column.dataIndex === 'businessTypeText'">
                <a-tag :class="['business-type-tag', record.businessType?.toLowerCase()]">
                  <component :is="getBusinessTypeIcon(record.businessType)" class="tag-icon" />
                  {{ record.businessTypeText }}
                </a-tag>
              </template>
              <template v-else-if="column.dataIndex === 'pointTypeText'">
                <a-tag
                  :class="{
                    'point-type-tag': true,
                    'income-tag': record.pointType === 'INCOME',
                    'expense-tag': record.pointType === 'EXPENSE',
                  }"
                >
                  <ArrowUpOutlined v-if="record.pointType === 'INCOME'" class="tag-icon" />
                  <ArrowDownOutlined v-else class="tag-icon" />
                  {{ record.pointTypeText }}
                </a-tag>
              </template>
              <template v-else-if="column.dataIndex === 'pointChange'">
                <span
                  :class="{
                    'point-change-value': true,
                    income: record.pointType === 'INCOME',
                    expense: record.pointType === 'EXPENSE',
                  }"
                >
                  {{ record.pointType === 'INCOME' ? '+' : '-' }}{{ record.pointChange }}
                </span>
              </template>
              <template v-else-if="column.dataIndex === 'beforePoints'">
                <a-tag class="points-tag before">
                  <DollarOutlined />
                  {{ record.beforePoints }}
                </a-tag>
              </template>
              <template v-else-if="column.dataIndex === 'afterPoints'">
                <a-tag class="points-tag after">
                  <DollarOutlined />
                  {{ record.afterPoints }}
                </a-tag>
              </template>
              <template v-else-if="column.dataIndex === 'remark'">
                <a-tooltip v-if="record.remark" :title="record.remark">
                  <a-tag class="remark-tag">
                    <FileTextOutlined />
                    {{ truncateText(record.remark, 15) }}
                  </a-tag>
                </a-tooltip>
                <span v-else class="empty-text">-</span>
              </template>
              <template v-else-if="column.dataIndex === 'createTime'">
                <div class="time-cell">
                  <CalendarOutlined class="time-icon" />
                  <span>{{ formatTime(record.createTime) }}</span>
                </div>
              </template>
            </template>
          </a-table>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed, h, onMounted, reactive, ref } from 'vue'
import { getLogs } from '@/api/pointController'
import { message } from 'ant-design-vue'
import { formatTime } from '@/utils/time'
import {
  ArrowDownOutlined,
  ArrowUpOutlined,
  BankOutlined,
  CalendarOutlined,
  CheckCircleOutlined,
  CodeOutlined,
  DollarOutlined,
  DownloadOutlined,
  FileTextOutlined,
  FilterOutlined,
  GiftOutlined,
  IdcardOutlined,
  MessageOutlined,
  ReloadOutlined,
  RocketOutlined,
  SearchOutlined,
  TableOutlined,
  TeamOutlined,
  TransactionOutlined,
  UserAddOutlined,
  UserOutlined,
} from '@ant-design/icons-vue'

const columns = [
  {
    title: '用户ID',
    dataIndex: 'userId',
    width: 160,
  },
  {
    title: '业务类型',
    dataIndex: 'businessTypeText',
    width: 180,
  },
  {
    title: '积分类型',
    dataIndex: 'pointTypeText',
    width: 120,
  },
  {
    title: '积分变动',
    dataIndex: 'pointChange',
    width: 140,
  },
  {
    title: '变动前积分',
    dataIndex: 'beforePoints',
    width: 140,
  },
  {
    title: '变动后积分',
    dataIndex: 'afterPoints',
    width: 140,
  },
  {
    title: '备注',
    dataIndex: 'remark',
    width: 200,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 180,
  },
]

// 搜索参数
const searchParams = reactive<API.PointLogQueryRequest>({
  pageNum: 1,
  pageSize: 10,
})

// 数据
const data = ref<API.PointLogVO[]>([])
const total = ref(0)

// 获取数据
const fetchData = async () => {
  try {
    const res = await getLogs(searchParams)
    if (res.data.data) {
      data.value = res.data.data.records ?? []
      total.value = res.data.data.totalRow ?? 0
    } else {
      message.error('获取数据失败，' + res.data.message)
    }
  } catch (error) {
    console.error('获取数据失败：', error)
    message.error('获取数据失败')
  }
}

// 分页参数
const pagination = computed(() => {
  return {
    current: searchParams.pageNum ?? 1,
    pageSize: searchParams.pageSize ?? 10,
    total: total.value,
    showSizeChanger: true,
    showQuickJumper: true,
    showTotal: (total: number) => `共 ${total} 条`,
  }
})

// 表格变化处理
const doTableChange = (page: any) => {
  searchParams.pageNum = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}

// 搜索
const doSearch = () => {
  searchParams.pageNum = 1
  fetchData()
}

// 重置搜索
const resetSearch = () => {
  searchParams.userId = undefined
  searchParams.businessType = undefined
  searchParams.pointType = undefined
  doSearch()
}

// 截取文本
const truncateText = (text: string, maxLength: number) => {
  if (!text) return ''
  if (text.length <= maxLength) return text
  return text.substring(0, maxLength) + '...'
}

// 获取业务类型图标
const getBusinessTypeIcon = (type?: string) => {
  const iconMap: Record<string, any> = {
    SIGN_IN: CheckCircleOutlined,
    REGISTER_REWARD: GiftOutlined,
    INVITEE_BONUS: UserAddOutlined,
    INVITER_BONUS: TeamOutlined,
    GENERATE: CodeOutlined,
    MESSAGE: MessageOutlined,
    DEPLOY: RocketOutlined,
    DOWNLOAD: DownloadOutlined,
    SYSTEM_GRANT: BankOutlined,
  }
  return iconMap[type ?? ''] || FileTextOutlined
}

// 页面加载时请求一次
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

.point-log-manage-page {
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
  font-size: 20px;
  color: var(--color-primary);
}

.search-title {
  font-family: var(--font-serif);
  font-size: 18px;
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
  font-size: 14px;
}

.search-form :deep(.ant-input),
.search-form :deep(.ant-select-selector) {
  border-radius: 8px;
  height: 32px;
  border: 2px solid var(--color-border);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.search-form :deep(.ant-input:hover),
.search-form :deep(.ant-select:hover .ant-select-selector) {
  border-color: var(--color-primary);
}

.search-form :deep(.ant-input:focus),
.search-form :deep(.ant-input-focused),
.search-form :deep(.ant-select-focused .ant-select-selector) {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 4px rgba(249, 115, 22, 0.1);
}

.input-icon {
  color: var(--color-text-secondary);
  font-size: 16px;
}

.option-icon {
  margin-right: 8px;
  color: var(--color-primary);
}

.option-icon.income-icon {
  color: #10b981;
}

.option-icon.expense-icon {
  color: #ef4444;
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

/* 分隔线 */
.section-divider {
  margin: 28px 0;
  border-color: var(--color-border);
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

.table-icon {
  font-size: 20px;
  color: var(--color-primary);
}

.table-title {
  font-family: var(--font-serif);
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text);
  margin-left: 12px;
  flex: 1;
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
.data-table :deep(.ant-table) {
  background: white;
  border-radius: 12px;
  overflow: hidden;
}

.data-table :deep(.ant-table-thead > tr > th) {
  background: #3b82f6;
  color: white;
  font-weight: 700;
  font-size: 14px;
  padding: 16px;
  border: none;
  letter-spacing: 0.5px;
}

.data-table :deep(.ant-table-tbody > tr > td) {
  padding: 16px;
  font-size: 14px;
  border-bottom: 1px solid var(--color-border);
  background: white;
}

.data-table :deep(.ant-table-tbody > tr:hover > td) {
  background: var(--color-bg-hover);
}

/* 标签样式 */
.id-tag {
  font-size: 11px;
  padding: 4px 10px;
  border-radius: 6px;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  border: none;
  background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
  color: #2563eb;
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

.points-tag.before {
  background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);
  color: #64748b;
}

.points-tag.after {
  background: linear-gradient(135deg, #dcfce7 0%, #bbf7d0 100%);
  color: #16a34a;
}

.remark-tag {
  font-size: 11px;
  padding: 4px 10px;
  border-radius: 6px;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  border: none;
  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
  color: #16a34a;
  cursor: pointer;
}

.time-tag {
  font-size: 11px;
  padding: 4px 10px;
  border-radius: 6px;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  border: none;
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  color: #d97706;
}

.empty-text {
  color: var(--color-text-secondary);
  font-size: 11px;
}

.business-type-tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 600;
  border: none;
}

.business-type-tag .tag-icon {
  font-size: 14px;
}

/* 业务类型标签颜色 */
.business-type-tag.sign_in {
  background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
  color: #1e40af;
}

.business-type-tag.register_reward {
  background: linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%);
  color: #065f46;
}

.business-type-tag.invitee_bonus {
  background: linear-gradient(135deg, #cffafe 0%, #a5f3fc 100%);
  color: #0e7490;
}

.business-type-tag.inviter_bonus {
  background: linear-gradient(135deg, #f3e8ff 0%, #d8b4fe 100%);
  color: #6b21a8;
}

.business-type-tag.generate {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  color: #92400e;
}

.business-type-tag.message {
  background: linear-gradient(135deg, #fae8ff 0%, #f5d0fe 100%);
  color: #86198f;
}

.business-type-tag.deploy {
  background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
  color: #991b1b;
}

.business-type-tag.download {
  background: linear-gradient(135deg, #e0e7ff 0%, #c7d2fe 100%);
  color: #3730a3;
}

.business-type-tag.system_grant {
  background: linear-gradient(135deg, #fce7f3 0%, #fbcfe8 100%);
  color: #9d174d;
}

/* 积分类型标签 */
.point-type-tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 600;
  border: none;
}

.point-type-tag.income-tag {
  background: linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%);
  color: #059669;
}

.point-type-tag.expense-tag {
  background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
  color: #dc2626;
}

.point-type-tag .tag-icon {
  font-size: 14px;
}

/* 积分变动值 */
.point-change-value {
  font-family: var(--font-sans);
  font-size: 16px;
  font-weight: 700;
}

.point-change-value.income {
  color: #10b981;
}

.point-change-value.expense {
  color: #ef4444;
}

/* 积分数值 */
/* 时间单元格（已废弃，使用 time-tag） */
.time-cell {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 11px;
  color: var(--color-text-secondary);
}

.time-icon {
  color: var(--color-primary);
  font-size: 12px;
}

/* 分页样式 */
.data-table :deep(.ant-pagination) {
  margin-top: 24px;
}

.data-table :deep(.ant-pagination-item-active) {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  border-color: transparent;
}

.data-table :deep(.ant-pagination-item-active a) {
  color: white;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .search-grid {
    grid-template-columns: repeat(2, 1fr);
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
  font-weight: 700 !important;
  font-size: 13px !important;
  background: white !important;
  border-radius: 12px !important;
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
  font-weight: 700 !important;
  font-size: 14px !important;
}

:deep(.ant-input::placeholder) {
  color: #475569 !important;
  font-weight: 500 !important;
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
  /* ========== 输入框和按钮高度统一 ========== */

  :deep(.ant-input) {
    height: 32px !important;
    border-radius: 8px !important;
  }

  :deep(.ant-input-number) {
    height: 32px !important;
    border-radius: 8px !important;
  }

  :deep(.ant-select-selector) {
    height: 32px !important;
    border-radius: 8px !important;
  }

  :deep(.ant-btn-primary) {
    height: 32px !important;
    border-radius: 8px !important;
  }

  :deep(.ant-btn-default) {
    height: 32px !important;
    border-radius: 8px !important;
  }

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

/* ========== 补充统一样式 ========== */ /* 输入框和选择器 */ .search-input :deep(.ant-input),
.search-select :deep(.ant-select-selector) { height: 32px !important; border-radius: 8px !important;
} .search-input :deep(.ant-input-number), .search-select :deep(.ant-input-number) { height: 32px
!important; border-radius: 8px !important; } /* 搜索按钮 */ .search-actions :deep(.ant-btn) {
height: 32px !important; border-radius: 8px !important; } /* 表格按钮 */ :deep(.ant-table)
.ant-btn-primary { height: 32px !important; border-radius: 12px !important; }
