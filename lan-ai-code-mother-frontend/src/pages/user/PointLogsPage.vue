<template>
  <div class="point-logs-wrapper">
    <!-- Hero 区域 -->
    <section class="logs-hero">
      <div class="hero-overlay"></div>
      <div class="hero-content">
        <div class="hero-badge">
          <HistoryOutlined />
          <span>积分流水记录</span>
        </div>
        <h1 class="hero-title">
          <span class="title-number"></span>
          积分变动明细
        </h1>
        <p class="hero-subtitle">
          <FileTextOutlined />
          查看您的积分收入与支出记录
        </p>
      </div>
    </section>

    <!-- 主内容区 -->
    <section class="logs-content">
      <!-- 搜索卡片 -->
      <div class="search-card warning-card">
        <div class="card-header">
          <SearchOutlined class="header-icon" />
          <h3>筛选条件</h3>
          <a-button size="small" type="text" @click="toggleSearch">
            {{ searchExpanded ? '收起' : '展开' }}
            <UpOutlined v-if="searchExpanded" />
            <DownOutlined v-else />
          </a-button>
        </div>

        <a-collapse v-model:activeKey="searchExpanded" :bordered="false">
          <a-collapse-panel key="1" :show-arrow="false">
            <a-form :model="searchParams" class="search-form" layout="vertical" @finish="doSearch">
              <a-row :gutter="[16, 16]">
                <a-col :lg="6" :md="12" :sm="24" :xs="24">
                  <a-form-item label="业务类型">
                    <a-select
                      v-model:value="searchParams.businessType"
                      allow-clear
                      placeholder="选择业务类型"
                      size="large"
                    >
                      <a-select-option value="">
                        <FileTextOutlined />
                        全部类型
                      </a-select-option>
                      <a-select-option value="SIGN_IN">
                        <CheckCircleOutlined />
                        签到
                      </a-select-option>
                      <a-select-option value="REGISTER_REWARD">
                        <GiftOutlined />
                        注册奖励
                      </a-select-option>
                      <a-select-option value="INVITEE_BONUS">
                        <UserAddOutlined />
                        被邀请人奖励
                      </a-select-option>
                      <a-select-option value="INVITER_BONUS">
                        <TeamOutlined />
                        邀请人奖励
                      </a-select-option>
                      <a-select-option value="GENERATE">
                        <CodeOutlined />
                        创建应用
                      </a-select-option>
                      <a-select-option value="MESSAGE">
                        <MessageOutlined />
                        AI对话
                      </a-select-option>
                      <a-select-option value="DEPLOY">
                        <RocketOutlined />
                        部署
                      </a-select-option>
                      <a-select-option value="DOWNLOAD">
                        <DownloadOutlined />
                        下载代码
                      </a-select-option>
                      <a-select-option value="SYSTEM_GRANT">
                        <BankOutlined />
                        系统发放
                      </a-select-option>
                    </a-select>
                  </a-form-item>
                </a-col>

                <a-col :lg="6" :md="12" :sm="24" :xs="24">
                  <a-form-item label="积分类型">
                    <a-select
                      v-model:value="searchParams.pointType"
                      allow-clear
                      placeholder="选择积分类型"
                      size="large"
                    >
                      <a-select-option value="">
                        <UnorderedListOutlined />
                        全部
                      </a-select-option>
                      <a-select-option value="INCOME">
                        <ArrowUpOutlined />
                        收入
                      </a-select-option>
                      <a-select-option value="EXPENSE">
                        <ArrowDownOutlined />
                        支出
                      </a-select-option>
                    </a-select>
                  </a-form-item>
                </a-col>

                <a-col :lg="6" :md="12" :sm="24" :xs="24">
                  <a-form-item label="开始时间">
                    <a-date-picker
                      v-model:value="startDate"
                      format="YYYY-MM-DD"
                      placeholder="选择开始时间"
                      size="large"
                      style="width: 100%"
                    />
                  </a-form-item>
                </a-col>

                <a-col :lg="6" :md="12" :sm="24" :xs="24">
                  <a-form-item label="结束时间">
                    <a-date-picker
                      v-model:value="endDate"
                      format="YYYY-MM-DD"
                      placeholder="选择结束时间"
                      size="large"
                      style="width: 100%"
                    />
                  </a-form-item>
                </a-col>
              </a-row>

              <a-form-item>
                <a-space :size="12">
                  <a-button html-type="submit" size="large" type="primary">
                    <template #icon>
                      <SearchOutlined />
                    </template>
                    搜索
                  </a-button>
                  <a-button size="large" @click="resetSearch">
                    <template #icon>
                      <ReloadOutlined />
                    </template>
                    重置
                  </a-button>
                </a-space>
              </a-form-item>
            </a-form>
          </a-collapse-panel>
        </a-collapse>
      </div>

      <!-- 数据表格卡片 -->
      <div class="table-card cyan-card">
        <div class="card-header">
          <UnorderedListOutlined class="header-icon" />
          <h3>流水记录</h3>
          <div class="header-actions">
            <a-tag color="orange">共 {{ total }} 条记录</a-tag>
          </div>
        </div>

        <a-table
          :columns="columns"
          :data-source="data"
          :pagination="pagination"
          :scroll="{ x: 1200 }"
          @change="doTableChange"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.dataIndex === 'businessTypeText'">
              <a-tag :color="getBusinessTypeColor(record.businessType)">
                <component :is="getBusinessTypeIcon(record.businessType)" />
                {{ record.businessTypeText }}
              </a-tag>
            </template>
            <template v-else-if="column.dataIndex === 'pointTypeText'">
              <a-tag :color="record.pointType === 'INCOME' ? 'green' : 'red'">
                <ArrowUpOutlined v-if="record.pointType === 'INCOME'" />
                <ArrowDownOutlined v-else />
                {{ record.pointTypeText }}
              </a-tag>
            </template>
            <template v-else-if="column.dataIndex === 'pointChange'">
              <span :class="record.pointType === 'INCOME' ? 'point-income' : 'point-expense'">
                <ArrowUpOutlined v-if="record.pointType === 'INCOME'" />
                <ArrowDownOutlined v-else />
                {{ record.pointChange }}
              </span>
            </template>
            <template v-else-if="column.dataIndex === 'createTime'">
              <div class="time-cell">
                <CalendarOutlined />
                {{ formatTime(record.createTime) }}
              </div>
            </template>
          </template>
        </a-table>
      </div>
    </section>
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { getMyLogs } from '@/api/pointController'
import { message } from 'ant-design-vue'
import { type Dayjs } from 'dayjs'
import { formatTime } from '@/utils/time'
import {
  ArrowDownOutlined,
  ArrowUpOutlined,
  BankOutlined,
  CalendarOutlined,
  CheckCircleOutlined,
  CodeOutlined,
  DownloadOutlined,
  DownOutlined,
  FileTextOutlined,
  GiftOutlined,
  HistoryOutlined,
  MessageOutlined,
  ReloadOutlined,
  RocketOutlined,
  SearchOutlined,
  TeamOutlined,
  UnorderedListOutlined,
  UpOutlined,
  UserAddOutlined,
} from '@ant-design/icons-vue'

const columns = [
  {
    title: '业务类型',
    dataIndex: 'businessTypeText',
    width: 120,
  },
  {
    title: '积分类型',
    dataIndex: 'pointTypeText',
    width: 100,
  },
  {
    title: '变动积分',
    dataIndex: 'pointChange',
    width: 80,
  },
  {
    title: '变动前',
    dataIndex: 'beforePoints',
    width: 80,
  },
  {
    title: '变动后',
    dataIndex: 'afterPoints',
    width: 80,
  },
  {
    title: '备注',
    dataIndex: 'remark',
    ellipsis: true,
    width: 130,
  },
  {
    title: '时间',
    dataIndex: 'createTime',
    width: 180,
  },
]

// 数据
const data = ref<API.PointLogVO[]>([])
const total = ref(0)
const startDate = ref<Dayjs | null>(null)
const endDate = ref<Dayjs | null>(null)
const searchExpanded = ref(['1'])

// 搜索条件
const searchParams = reactive<API.PointLogQueryRequest>({
  pageNum: 1,
  pageSize: 10,
})

// 统计数据
const totalIncome = computed(() => {
  return data.value
    .filter((item) => item.pointType === 'INCOME')
    .reduce((sum, item) => sum + Math.abs(item.pointChange || 0), 0)
})

const totalExpense = computed(() => {
  return data.value
    .filter((item) => item.pointType === 'EXPENSE')
    .reduce((sum, item) => sum + Math.abs(item.pointChange || 0), 0)
})

const currentBalance = computed(() => {
  // 当前余额 = 总收入 - 总支出
  return totalIncome.value - totalExpense.value
})

// 切换搜索展开状态
const toggleSearch = () => {
  if (searchExpanded.value.length > 0) {
    searchExpanded.value = []
  } else {
    searchExpanded.value = ['1']
  }
}

// 获取数据
const fetchData = async () => {
  try {
    const params: API.PointLogQueryRequest = {
      ...searchParams,
    }
    if (startDate.value) {
      params.startTime = startDate.value.format('YYYY-MM-DD') + 'T00:00:00'
    }
    if (endDate.value) {
      params.endTime = endDate.value.format('YYYY-MM-DD') + 'T23:59:59'
    }
    const res = await getMyLogs(params)
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
  searchParams.businessType = undefined
  searchParams.pointType = undefined
  startDate.value = null
  endDate.value = null
  doSearch()
}

// 获取业务类型颜色
const getBusinessTypeColor = (type?: string) => {
  const colorMap: Record<string, string> = {
    SIGN_IN: 'orange',
    REGISTER_REWARD: 'gold',
    INVITEE_BONUS: 'cyan',
    INVITER_BONUS: 'blue',
    GENERATE: 'purple',
    DOWNLOAD: 'geekblue',
    DEPLOY: 'red',
    MESSAGE: 'magenta',
    SYSTEM_GRANT: 'green',
  }
  return colorMap[type ?? ''] || 'default'
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

.point-logs-wrapper {
  min-height: 100vh;
  background: #f8fafc;
  padding-bottom: 40px;
}

/* Hero 区域 */
.logs-hero {
  position: relative;
  background-image: url('https://images.unsplash.com/photo-1554224155-8d04cb21cd6c?w=1920&q=80');
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
  border-radius: 0;
  padding: 60px 24px;
  margin-bottom: 32px;
  overflow: hidden;
}

.hero-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    135deg,
    rgba(249, 115, 22, 0.92) 0%,
    rgba(234, 88, 12, 0.88) 50%,
    rgba(251, 191, 36, 0.85) 100%
  );
  backdrop-filter: blur(2px);
}

.hero-content {
  position: relative;
  max-width: 1200px;
  margin: 0 auto;
  z-index: 1;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(10px);
  padding: 8px 20px;
  border-radius: 24px;
  color: white;
  font-size: 13px;
  font-weight: 500;
  letter-spacing: 0.05em;
  margin-bottom: 20px;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.hero-title {
  font-family: var(--font-serif);
  font-size: 48px;
  font-weight: 700;
  color: white;
  text-shadow:
    0 2px 4px rgba(0, 0, 0, 0.25),
    0 4px 12px rgba(0, 0, 0, 0.2);
  margin: 0 0 16px;
  line-height: 1.2;
  letter-spacing: -0.02em;
}

.title-number {
  display: inline-block;
  font-size: 72px;
  font-weight: 700;
  color: rgba(255, 255, 255, 0.3);
  margin-right: 16px;
  line-height: 1;
}

.hero-subtitle {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  color: rgba(255, 255, 255, 0.95);
  text-shadow:
    0 2px 4px rgba(0, 0, 0, 0.25),
    0 1px 8px rgba(0, 0, 0, 0.2);
  margin: 0;
  font-weight: 400;
}

/* 快速统计 */
.quick-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-top: 40px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 20px;
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(10px);
  padding: 28px 24px;
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.4);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  position: relative;
  overflow: hidden;
}

.stat-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.1) 0%, rgba(255, 255, 255, 0) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.stat-item:hover::before {
  opacity: 1;
}

.stat-item:hover {
  background: rgba(255, 255, 255, 0.35);
  transform: translateY(-4px);
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.2);
  border-color: rgba(255, 255, 255, 0.5);
}

.stat-icon {
  width: 64px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 16px;
  font-size: 28px;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.income-icon {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
}

.expense-icon {
  background: linear-gradient(135deg, #f43f5e 0%, #e11d48 100%);
  color: white;
}

.balance-icon {
  background: linear-gradient(135deg, #fbbf24 0%, #f97316 100%);
  color: white;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: 800;
  line-height: 1.2;
  margin-bottom: 6px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.income-value {
  color: #ffffff;
}

.expense-value {
  color: #ffffff;
}

.balance-value {
  color: #ffffff;
}

.stat-label {
  font-size: 15px;
  color: rgba(255, 255, 255, 0.9);
  font-weight: 600;
  letter-spacing: 0.5px;
}

.stat-divider {
  display: none;
}

/* 主内容区 */
.logs-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 16px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 卡片通用样式 */
.search-card,
.table-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid var(--color-border);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--color-border);
}

.header-icon {
  font-size: 20px;
  color: var(--color-primary);
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text);
  flex: 1;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 搜索表单 */
.search-form {
  margin-top: 16px;
}

:deep(.ant-form-item-label > label) {
  font-weight: 500;
  color: var(--color-text);
}

:deep(.ant-select),
:deep(.ant-picker) {
  border-radius: 8px;
}

:deep(.ant-select:focus),
:deep(.ant-select-focused),
:deep(.ant-picker-focused) {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 2px rgba(249, 115, 22, 0.1);
}

:deep(.ant-collapse) {
  background: transparent;
}

:deep(.ant-collapse-content-box) {
  padding: 16px 0 0;
}

:deep(.ant-collapse-header) {
  display: none;
}

/* 表格样式 */
:deep(.ant-table) {
  font-size: 14px;
}

:deep(.ant-table-thead > tr > th) {
  background: #3b82f6;
  font-weight: 700;
  padding: 16px;
  border-bottom: 2px solid var(--color-primary);
  color: var(--color-text);
}

:deep(.ant-table-tbody > tr > td) {
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  transition: all 0.2s ease;
}

:deep(.ant-table-tbody > tr:hover > td) {
  background: #fff7ed;
}

:deep(.ant-table-tbody > tr:last-child > td) {
  border-bottom: none;
}

/* 业务类型标签 */
:deep(.ant-tag) {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 12px;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
}

/* 时间单元格 */
.time-cell {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--color-text-secondary);
}

/* 积分样式 */
.point-income {
  color: #52c41a;
  font-weight: 600;
  font-size: 16px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.point-expense {
  color: #ff4d4f;
  font-weight: 600;
  font-size: 16px;
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 分页样式 */
:deep(.ant-pagination) {
  margin-top: 24px;
}

:deep(.ant-pagination-item-active) {
  background: var(--color-primary);
  border-color: var(--color-primary);
}

:deep(.ant-pagination-item-active a) {
  color: white;
}

:deep(.ant-pagination-item:hover) {
  border-color: var(--color-primary);
}

:deep(.ant-pagination-item:hover a) {
  color: var(--color-primary);
}

/* 按钮样式 */
:deep(.ant-btn) {
  border-radius: 8px;
  font-weight: 500;
  height: 32px;
}

:deep(.ant-btn-primary) {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  border: none;
}

:deep(.ant-btn-primary:hover) {
  background: linear-gradient(135deg, var(--color-primary-dark) 0%, #dc2626 100%);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .quick-stats {
    grid-template-columns: 1fr;
  }

  .stat-divider {
    display: none;
  }
}

@media (max-width: 768px) {
  .logs-hero {
    padding: 40px 16px;
  }

  .hero-title {
    font-size: 32px;
  }

  .title-number {
    font-size: 48px;
  }

  .hero-subtitle {
    font-size: 16px;
  }

  .quick-stats {
    gap: 16px;
    margin-top: 24px;
  }

  .stat-item {
    padding: 20px;
  }

  .stat-value {
    font-size: 24px;
  }

  .logs-content {
    padding: 0 16px;
  }

  .search-card,
  .table-card {
    padding: 16px;
  }

  :deep(.ant-table-thead > tr > th),
  :deep(.ant-table-tbody > tr > td) {
    padding: 12px 8px;
    font-size: 13px;
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
