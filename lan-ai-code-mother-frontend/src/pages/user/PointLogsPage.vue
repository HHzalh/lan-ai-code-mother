<template>
  <div class="point-logs-wrapper">
    <!-- 动态渐变背景 -->
    <div class="gradient-bg"></div>

    <!-- 主容器 -->
    <div class="logs-container">
      <!-- 返回按钮 -->
      <button class="back-btn glass-card" @click="goBack">
        <svg
          fill="none"
          height="20"
          stroke="currentColor"
          stroke-linecap="round"
          stroke-linejoin="round"
          stroke-width="2.5"
          viewBox="0 0 24 24"
          width="20"
        >
          <line x1="19" x2="5" y1="12" y2="12"></line>
          <polyline points="12 19 5 12 12 5"></polyline>
        </svg>
        <span>返回个人中心</span>
      </button>

      <!-- 页面标题区 -->
      <div class="page-header">
        <h1 class="page-title">积分流水</h1>
        <p class="page-subtitle">查看你的积分变动记录</p>
      </div>

      <!-- 筛选卡片 -->
      <div class="filter-card glass-card">
        <div class="filter-header">
          <div class="filter-title">
            <svg
              fill="none"
              height="20"
              stroke="currentColor"
              stroke-width="2"
              viewBox="0 0 24 24"
              width="20"
            >
              <circle cx="11" cy="11" r="8"></circle>
              <line x1="21" x2="16.65" y1="21" y2="16.65"></line>
            </svg>
            <span>筛选</span>
          </div>
          <button class="toggle-btn" @click="toggleFilter">
            {{ filterExpanded ? '收起' : '展开' }}
            <svg
              v-if="!filterExpanded"
              fill="none"
              height="16"
              stroke="currentColor"
              stroke-width="2"
              viewBox="0 0 24 24"
              width="16"
            >
              <polyline points="6 9 12 15 18 9"></polyline>
            </svg>
            <svg
              v-else
              fill="none"
              height="16"
              stroke="currentColor"
              stroke-width="2"
              viewBox="0 0 24 24"
              width="16"
            >
              <polyline points="18 15 12 9 6 15"></polyline>
            </svg>
          </button>
        </div>

        <div v-show="filterExpanded" class="filter-content">
          <div class="filter-grid">
            <div class="filter-item">
              <label class="filter-label">类型</label>
              <select v-model="searchParams.businessType" class="filter-select">
                <option value="">全部</option>
                <option value="SIGN_IN">签到</option>
                <option value="REGISTER_REWARD">注册</option>
                <option value="INVITEE_BONUS">被邀请</option>
                <option value="INVITER_BONUS">邀请</option>
                <option value="GENERATE">创建</option>
                <option value="MESSAGE">对话</option>
                <option value="DEPLOY">部署</option>
                <option value="DOWNLOAD">下载</option>
              </select>
            </div>

            <div class="filter-item">
              <label class="filter-label">收支</label>
              <select v-model="searchParams.pointType" class="filter-select">
                <option value="">全部</option>
                <option value="INCOME">收入</option>
                <option value="EXPENSE">支出</option>
              </select>
            </div>

            <div class="filter-item">
              <label class="filter-label">开始</label>
              <input v-model="startDateStr" class="filter-input" type="date" />
            </div>

            <div class="filter-item">
              <label class="filter-label">结束</label>
              <input v-model="endDateStr" class="filter-input" type="date" />
            </div>
          </div>

          <div class="filter-actions">
            <button class="action-btn primary" @click="doSearch">
              <svg
                fill="none"
                height="16"
                stroke="currentColor"
                stroke-width="2"
                viewBox="0 0 24 24"
                width="16"
              >
                <circle cx="11" cy="11" r="8"></circle>
                <line x1="21" x2="16.65" y1="21" y2="16.65"></line>
              </svg>
              搜索
            </button>
            <button class="action-btn secondary" @click="resetSearch">重置</button>
          </div>
        </div>
      </div>

      <!-- 流水记录卡片 -->
      <div class="records-card glass-card">
        <div class="records-header">
          <div class="records-title">
            <svg
              fill="none"
              height="20"
              stroke="currentColor"
              stroke-width="2"
              viewBox="0 0 24 24"
              width="20"
            >
              <line x1="8" x2="21" y1="6" y2="6"></line>
              <line x1="8" x2="21" y1="12" y2="12"></line>
              <line x1="8" x2="21" y1="18" y2="18"></line>
              <line x1="3" x2="3.01" y1="6" y2="6"></line>
              <line x1="3" x2="3.01" y1="12" y2="12"></line>
              <line x1="3" x2="3.01" y1="18" y2="18"></line>
            </svg>
            <span>流水记录</span>
            <span class="records-count">{{ total }} 条</span>
          </div>
        </div>

        <a-table
          :columns="columns"
          :data-source="data"
          :pagination="pagination"
          :scroll="{ x: 800 }"
          class="logs-table"
          @change="doTableChange"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'pointType'">
              <span
                :class="record.pointType === 'INCOME' ? 'income' : 'expense'"
                class="type-badge"
              >
                <svg
                  v-if="record.pointType === 'INCOME'"
                  fill="none"
                  height="16"
                  stroke="currentColor"
                  stroke-width="2"
                  viewBox="0 0 24 24"
                  width="16"
                >
                  <polyline points="23 6 13.5 15.5 8.5 10.5 1 18"></polyline>
                  <polyline points="17 6 23 6 23 12"></polyline>
                </svg>
                <svg
                  v-else
                  fill="none"
                  height="16"
                  stroke="currentColor"
                  stroke-width="2"
                  viewBox="0 0 24 24"
                  width="16"
                >
                  <polyline points="23 18 13.5 8.5 8.5 13.5 1 6"></polyline>
                  <polyline points="17 18 23 18 23 12"></polyline>
                </svg>
                {{ record.pointType === 'INCOME' ? '收入' : '支出' }}
              </span>
            </template>

            <template v-else-if="column.key === 'pointChange'">
              <span
                :class="record.pointType === 'INCOME' ? 'income' : 'expense'"
                class="amount-value"
              >
                {{ record.pointType === 'INCOME' ? '+' : '-' }}{{ record.pointChange }}
              </span>
            </template>
          </template>
        </a-table>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getMyLogs } from '@/api/pointController'
import { formatTime } from '@/utils/time'

const router = useRouter()

// 数据
const data = ref<API.PointLogVO[]>([])
const total = ref(0)
const filterExpanded = ref(false)
const startDateStr = ref('')
const endDateStr = ref('')

// 搜索条件
const searchParams = reactive<API.PointLogQueryRequest>({
  pageNum: 1,
  pageSize: 10,
})

// 表格列配置
const columns = [
  {
    title: '类型',
    dataIndex: 'businessTypeText',
    key: 'businessTypeText',
    width: 120,
  },
  {
    title: '收支',
    key: 'pointType',
    width: 100,
  },
  {
    title: '积分变动',
    key: 'pointChange',
    width: 120,
    align: 'right' as const,
  },
  {
    title: '变动后余额',
    dataIndex: 'afterPoints',
    key: 'afterPoints',
    width: 140,
    align: 'right' as const,
  },
  {
    title: '时间',
    dataIndex: 'createTime',
    key: 'createTime',
    width: 180,
    customRender: ({ record }: { record: API.PointLogVO }) => formatTime(record.createTime),
  },
  {
    title: '备注',
    dataIndex: 'remark',
    key: 'remark',
    ellipsis: true,
  },
]

// 分页配置
const pagination = computed(() => {
  return {
    current: searchParams.pageNum ?? 1,
    pageSize: searchParams.pageSize ?? 10,
    total: total.value,
    showSizeChanger: true,
    showQuickJumper: true,
    showTotal: (total: number) => `共 ${total} 条`,
    pageSizeOptions: ['10', '20', '50', '100'],
  }
})

// 表格变化处理
const doTableChange = (pagination: any, filters: any, sorter: any) => {
  searchParams.pageNum = pagination.current
  searchParams.pageSize = pagination.pageSize
  fetchData()
}

// 切换筛选展开状态
const toggleFilter = () => {
  filterExpanded.value = !filterExpanded.value
}

// 获取数据
const fetchData = async () => {
  try {
    const params: API.PointLogQueryRequest = {
      ...searchParams,
    }
    if (startDateStr.value) {
      params.startTime = startDateStr.value + 'T00:00:00'
    }
    if (endDateStr.value) {
      params.endTime = endDateStr.value + 'T23:59:59'
    }
    const res = await getMyLogs(params)
    if (res.data.data) {
      data.value = res.data.data.records ?? []
      total.value = res.data.data.totalRow ?? 0
    }
  } catch (error) {
    console.error('获取数据失败：', error)
  }
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
  startDateStr.value = ''
  endDateStr.value = ''
  doSearch()
}

// 返回个人中心
const goBack = () => {
  router.push('/user/profile')
}

// 页面加载时请求一次
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
/* ========== 字体引入 ========== */
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@300;400;500;600;700&display=swap');

/* ========== 全局变量 ========== */
:root {
  --color-primary: #ff6b6b;
  --color-primary-light: #ff8787;
  --color-primary-dark: #fa5252;
  --color-secondary: #ffa8a8;
  --color-accent: #ffec99;
  --text-primary: #2d3436;
  --text-secondary: #636e72;
  --glass-bg: rgba(255, 255, 255, 0.65);
  --glass-border: rgba(255, 255, 255, 0.85);
  --shadow-soft: 0 8px 32px rgba(255, 107, 107, 0.12);
  --shadow-hover: 0 12px 48px rgba(255, 107, 107, 0.18);
}

/* ========== 主容器 ========== */
.point-logs-wrapper {
  min-height: 100vh;
  position: relative;
  overflow: hidden;
  font-family:
    'Noto Sans SC',
    -apple-system,
    BlinkMacSystemFont,
    sans-serif;
}

/* ========== 动态渐变背景 ========== */
.gradient-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background:
    radial-gradient(circle at 25% 25%, rgba(255, 107, 107, 0.15) 0%, transparent 50%),
    radial-gradient(circle at 75% 75%, rgba(255, 168, 168, 0.12) 0%, transparent 50%),
    radial-gradient(circle at 50% 50%, rgba(255, 236, 153, 0.08) 0%, transparent 50%);
  animation: gradientMove 20s ease-in-out infinite alternate;
  z-index: 0;
  pointer-events: none;
}

@keyframes gradientMove {
  0% {
    transform: scale(1) rotate(0deg);
  }
  100% {
    transform: scale(1.1) rotate(5deg);
  }
}

/* ========== 内容容器 ========== */
.logs-container {
  position: relative;
  z-index: 2;
  max-width: 900px;
  margin: 0 auto;
  padding: 40px 24px 80px;
}

/* ========== 返回按钮 ========== */
.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  margin-bottom: 24px;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border: 1.5px solid rgba(255, 107, 107, 0.2);
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  outline: none;
}

.back-btn:hover {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  color: white;
  border-color: transparent;
  transform: translateX(-4px);
  box-shadow: 0 6px 20px rgba(255, 107, 107, 0.3);
}

.back-btn:active {
  transform: translateX(-2px);
}

.back-btn svg {
  flex-shrink: 0;
}

/* ========== 页面标题 ========== */
.page-header {
  text-align: center;
  margin-bottom: 40px;
}

.page-title {
  font-size: 48px;
  font-weight: 700;
  margin: 0 0 12px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-family: 'Noto Sans SC', sans-serif;
}

.page-subtitle {
  font-size: 16px;
  color: var(--text-secondary);
  margin: 0;
}

/* ========== 玻璃拟态卡片基类 ========== */
.glass-card {
  background: var(--glass-bg);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  border: 1px solid var(--glass-border);
  border-radius: 24px;
  box-shadow: var(--shadow-soft);
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
  position: relative;
  overflow: hidden;
}

.glass-card:hover {
  box-shadow: var(--shadow-hover);
  transform: translateY(-4px);
}

/* ========== 筛选卡片 ========== */
.filter-card {
  padding: 24px;
  margin-bottom: 24px;
}

.filter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(255, 107, 107, 0.1);
}

.filter-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.toggle-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  background: rgba(255, 107, 107, 0.08);
  border: 1px solid rgba(255, 107, 107, 0.2);
  border-radius: 8px;
  font-size: 13px;
  font-weight: 500;
  color: var(--color-primary);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.toggle-btn:hover {
  background: var(--color-primary);
  color: white;
}

.filter-content {
  margin-top: 16px;
}

.filter-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.filter-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.filter-label {
  font-size: 13px;
  font-weight: 500;
  color: var(--text-secondary);
}

.filter-select,
.filter-input {
  padding: 10px 14px;
  background: rgba(255, 255, 255, 0.8);
  border: 1.5px solid rgba(255, 107, 107, 0.15);
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
  transition: all 0.3s ease;
  outline: none;
}

.filter-select:focus,
.filter-input:focus {
  border-color: var(--color-primary);
  background: rgba(255, 255, 255, 1);
}

.filter-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 24px;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  border: none;
  outline: none;
}

.action-btn.primary {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);
}

.action-btn.primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(255, 107, 107, 0.4);
}

.action-btn.secondary {
  background: rgba(255, 107, 107, 0.1);
  color: var(--color-primary);
  border: 2px solid rgba(255, 107, 107, 0.2);
}

.action-btn.secondary:hover {
  background: var(--color-primary);
  color: white;
}

/* ========== 记录卡片 ========== */
.records-card {
  padding: 24px;
}

.records-header {
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(255, 107, 107, 0.1);
}

.records-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.records-count {
  margin-left: auto;
  padding: 4px 12px;
  background: rgba(255, 107, 107, 0.1);
  color: var(--color-primary);
  border-radius: 12px;
  font-size: 13px;
  font-weight: 600;
}

/* ========== Ant Design 表格样式 ========== */
.logs-table {
  background: transparent;
}

.logs-table :deep(.ant-table) {
  background: transparent;
}

.logs-table :deep(.ant-table-container) {
  background: transparent;
}

.logs-table :deep(.ant-table-content) {
  overflow-x: auto;
}

.logs-table :deep(.ant-table-thead > tr > th) {
  background: rgba(255, 107, 107, 0.05);
  border-bottom: 1px solid rgba(255, 107, 107, 0.15);
  color: var(--text-primary);
  font-weight: 600;
  padding: 14px 16px;
}

.logs-table :deep(.ant-table-tbody > tr > td) {
  background: rgba(255, 255, 255, 0.4);
  border-bottom: 1px solid rgba(255, 107, 107, 0.08);
  color: var(--text-primary);
  padding: 14px 16px;
  transition: all 0.3s ease;
}

.logs-table :deep(.ant-table-tbody > tr:hover > td) {
  background: rgba(255, 255, 255, 0.7);
}

.logs-table :deep(.ant-table-tbody > tr > td:first-child),
.logs-table :deep(.ant-table-thead > tr > th:first-child) {
  border-radius: 8px 0 0 8px;
}

.logs-table :deep(.ant-table-tbody > tr > td:last-child),
.logs-table :deep(.ant-table-thead > tr > th:last-child) {
  border-radius: 0 8px 8px 0;
}

/* 收支类型徽章 */
.type-badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 12px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 600;
}

.type-badge.income {
  background: rgba(82, 196, 26, 0.1);
  color: #52c41a;
}

.type-badge.expense {
  background: rgba(255, 77, 79, 0.1);
  color: #ff4d4f;
}

/* 积分变动金额 */
.amount-value {
  font-size: 16px;
  font-weight: 700;
  font-family: 'Noto Sans SC', sans-serif;
}

.amount-value.income {
  color: #52c41a;
}

.amount-value.expense {
  color: #ff4d4f;
}

/* 分页样式 */
.logs-table :deep(.ant-pagination) {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid rgba(255, 107, 107, 0.1);
}

.logs-table :deep(.ant-pagination-item) {
  border-radius: 8px;
  border-color: rgba(255, 107, 107, 0.2);
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.logs-table :deep(.ant-pagination-item:hover) {
  border-color: var(--color-primary);
}

.logs-table :deep(.ant-pagination-item-active) {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  border-color: transparent;
}

.logs-table :deep(.ant-pagination-item-active a) {
  color: white;
}

.logs-table :deep(.ant-pagination-prev),
.logs-table :deep(.ant-pagination-next) {
  border-radius: 8px;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.logs-table :deep(.ant-pagination-prev:hover),
.logs-table :deep(.ant-pagination-next:hover) {
  border-color: var(--color-primary);
}

.logs-table :deep(.ant-pagination-disabled) {
  opacity: 0.5;
}

.logs-table :deep(.ant-pagination-options) {
  display: flex;
  align-items: center;
  gap: 8px;
}

.logs-table :deep(.ant-pagination-total-text) {
  color: var(--text-secondary);
  font-size: 14px;
}

.logs-table :deep(.ant-select-selector) {
  border-radius: 8px !important;
  border-color: rgba(255, 107, 107, 0.2) !important;
}

.logs-table :deep(.ant-pagination-options-quick-jumper input) {
  border-radius: 8px;
  border-color: rgba(255, 107, 107, 0.2);
}

.logs-table :deep(.ant-pagination-options-quick-jumper input:focus) {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 2px rgba(255, 107, 107, 0.1);
}

/* ========== 响应式设计 ========== */
@media (max-width: 768px) {
  .logs-container {
    padding: 24px 16px 60px;
  }

  .page-title {
    font-size: 36px;
  }

  .page-subtitle {
    font-size: 15px;
  }

  .filter-grid {
    grid-template-columns: 1fr;
  }

  .filter-actions {
    flex-direction: column;
  }

  .action-btn {
    width: 100%;
    justify-content: center;
  }

  .logs-table :deep(.ant-table) {
    font-size: 13px;
  }

  .logs-table :deep(.ant-table-thead > tr > th),
  .logs-table :deep(.ant-table-tbody > tr > td) {
    padding: 10px 12px;
  }

  .back-btn {
    font-size: 13px;
    padding: 8px 16px;
  }
}
</style>
