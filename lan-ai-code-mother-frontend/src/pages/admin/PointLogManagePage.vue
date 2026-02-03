<template>
  <div class="point-log-manage-wrapper admin-manage-wrapper">
    <!-- 动态渐变背景 -->
    <div class="gradient-bg admin-gradient-bg"></div>

    <!-- 主容器 -->
    <div class="manage-container admin-manage-container">
      <!-- 页面标题区 -->
      <div class="page-header admin-page-header">
        <h1 class="page-title admin-page-title">积分流水管理</h1>
        <p class="page-subtitle admin-page-subtitle">查看与管理所有用户积分变动记录</p>
      </div>

      <!-- 筛选卡片 -->
      <div class="filter-card admin-glass-card admin-filter-card">
        <div class="filter-header admin-filter-header">
          <div class="filter-title admin-filter-title">
            <svg
              fill="none"
              height="20"
              stroke="currentColor"
              stroke-width="2"
              viewBox="0 0 24 24"
              width="20"
            >
              <polygon points="22 3 2 3 10 12.46 10 19 14 17 14 12.46 22 3"></polygon>
            </svg>
            <span>筛选</span>
          </div>
          <div class="filter-actions">
            <button class="action-btn secondary admin-action-btn secondary" @click="resetSearch">
              <svg
                fill="none"
                height="16"
                stroke="currentColor"
                stroke-width="2"
                viewBox="0 0 24 24"
                width="16"
              >
                <path d="M23 4v6h-6"></path>
                <path d="M1 20v-6h6"></path>
                <path
                  d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"
                ></path>
              </svg>
              重置
            </button>
            <button class="action-btn primary admin-action-btn primary" @click="doSearch">
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
          </div>
        </div>

        <div class="filter-grid admin-filter-grid">
          <div class="filter-item admin-filter-item">
            <label class="filter-label admin-filter-label">用户ID</label>
            <input
              v-model="searchParams.userId"
              class="filter-input admin-filter-input"
              placeholder="请输入用户ID"
            />
          </div>

          <div class="filter-item admin-filter-item">
            <label class="filter-label admin-filter-label">业务类型</label>
            <select v-model="searchParams.businessType" class="filter-select admin-filter-select">
              <option value="">全部</option>
              <option value="SIGN_IN">签到</option>
              <option value="REGISTER_REWARD">注册奖励</option>
              <option value="INVITEE_BONUS">被邀请人奖励</option>
              <option value="INVITER_BONUS">邀请人奖励</option>
              <option value="GENERATE">创建应用</option>
              <option value="MESSAGE">AI对话</option>
              <option value="DEPLOY">部署</option>
              <option value="DOWNLOAD">下载代码</option>
              <option value="SYSTEM_GRANT">系统发放</option>
            </select>
          </div>

          <div class="filter-item admin-filter-item">
            <label class="filter-label admin-filter-label">积分类型</label>
            <select v-model="searchParams.pointType" class="filter-select admin-filter-select">
              <option value="">全部</option>
              <option value="INCOME">收入</option>
              <option value="EXPENSE">支出</option>
            </select>
          </div>
        </div>
      </div>

      <!-- 流水列表卡片 -->
      <div class="table-card admin-glass-card admin-table-card">
        <div class="table-header admin-table-header">
          <div class="table-title admin-table-title">
            <svg
              fill="none"
              height="20"
              stroke="currentColor"
              stroke-width="2"
              viewBox="0 0 24 24"
              width="20"
            >
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
              <polyline points="14 2 14 8 20 8"></polyline>
              <line x1="16" x2="8" y1="13" y2="13"></line>
              <line x1="16" x2="8" y1="17" y2="17"></line>
              <polyline points="10 9 9 9 8 9"></polyline>
            </svg>
            <span>积分流水列表</span>
          </div>
          <button class="action-btn secondary admin-action-btn secondary" @click="fetchData">
            <svg
              fill="none"
              height="16"
              stroke="currentColor"
              stroke-width="2"
              viewBox="0 0 24 24"
              width="16"
            >
              <path d="M23 4v6h-6"></path>
              <path d="M1 20v-6h6"></path>
              <path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"></path>
            </svg>
            刷新
          </button>
        </div>

        <a-table
          :columns="columns"
          :data-source="data"
          :pagination="pagination"
          :scroll="{ x: 'max-content' }"
          class="data-table admin-data-table"
          @change="doTableChange"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.dataIndex === 'userId'">
              <span class="id-badge admin-id-badge">{{ record.userId }}</span>
            </template>

            <template v-else-if="column.dataIndex === 'businessTypeText'">
              <span
                :class="'business-type-badge ' + getBusinessTypeClass(record.businessType)"
                class="admin-type-badge"
              >
                {{ record.businessTypeText }}
              </span>
            </template>

            <template v-else-if="column.dataIndex === 'pointTypeText'">
              <span
                v-if="record.pointType === 'INCOME'"
                class="income-badge admin-type-badge"
                style="
                  background: linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%);
                  color: #059669;
                "
              >
                <svg
                  fill="none"
                  height="12"
                  stroke="currentColor"
                  stroke-width="2"
                  viewBox="0 0 24 24"
                  width="12"
                >
                  <line x1="12" x2="12" y1="19" y2="5"></line>
                  <polyline points="5 12 12 5 19 12"></polyline>
                </svg>
                {{ record.pointTypeText }}
              </span>
              <span
                v-else
                class="expense-badge admin-type-badge"
                style="
                  background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
                  color: #dc2626;
                "
              >
                <svg
                  fill="none"
                  height="12"
                  stroke="currentColor"
                  stroke-width="2"
                  viewBox="0 0 24 24"
                  width="12"
                >
                  <line x1="12" x2="12" y1="5" y2="19"></line>
                  <polyline points="19 12 12 19 5 12"></polyline>
                </svg>
                {{ record.pointTypeText }}
              </span>
            </template>

            <template v-else-if="column.dataIndex === 'pointChange'">
              <span
                :class="{
                  income: record.pointType === 'INCOME',
                  expense: record.pointType === 'EXPENSE',
                }"
                class="point-change-value"
                style="font-size: 15px; font-weight: 700"
              >
                {{ record.pointType === 'INCOME' ? '+' : '-' }}{{ record.pointChange }}
              </span>
            </template>

            <template v-else-if="column.dataIndex === 'beforePoints'">
              <span
                class="before-points-badge admin-type-badge"
                style="
                  background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);
                  color: #64748b;
                "
                >{{ record.beforePoints }}</span
              >
            </template>

            <template v-else-if="column.dataIndex === 'afterPoints'">
              <span
                class="after-points-badge admin-type-badge"
                style="
                  background: linear-gradient(135deg, #dcfce7 0%, #bbf7d0 100%);
                  color: #16a34a;
                "
                >{{ record.afterPoints }}</span
              >
            </template>

            <template v-else-if="column.dataIndex === 'remark'">
              <span
                v-if="record.remark"
                class="remark-badge admin-type-badge"
                style="
                  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
                  color: #16a34a;
                "
                >{{ truncateText(record.remark, 15) }}</span
              >
              <span v-else class="empty-text">-</span>
            </template>

            <template v-else-if="column.dataIndex === 'createTime'">
              <span class="time-text admin-time-text">{{ formatTime(record.createTime) }}</span>
            </template>
          </template>
        </a-table>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { getLogs } from '@/api/pointController'
import { message } from 'ant-design-vue'
import { formatTime } from '@/utils/time'

const columns = [
  {
    title: '用户ID',
    dataIndex: 'userId',
    width: 160,
  },
  {
    title: '业务类型',
    dataIndex: 'businessTypeText',
    width: 140,
  },
  {
    title: '积分类型',
    dataIndex: 'pointTypeText',
    width: 140,
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
    width: 150,
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

// 获取业务类型样式类
const getBusinessTypeClass = (type?: string) => {
  const typeMap: Record<string, string> = {
    SIGN_IN: 'sign-in',
    REGISTER_REWARD: 'register-reward',
    INVITEE_BONUS: 'invitee-bonus',
    INVITER_BONUS: 'inviter-bonus',
    GENERATE: 'generate',
    MESSAGE: 'message',
    DEPLOY: 'deploy',
    DOWNLOAD: 'download',
    SYSTEM_GRANT: 'system-grant',
  }
  return typeMap[type ?? ''] || ''
}

// 获取业务类型图标ID
const getBusinessTypeIcon = (type?: string) => {
  const iconMap: Record<string, string> = {
    SIGN_IN: 'icon-check',
    REGISTER_REWARD: 'icon-gift',
    INVITEE_BONUS: 'icon-user-add',
    INVITER_BONUS: 'icon-users',
    GENERATE: 'icon-code',
    MESSAGE: 'icon-message',
    DEPLOY: 'icon-rocket',
    DOWNLOAD: 'icon-download',
    SYSTEM_GRANT: 'icon-bank',
  }
  return iconMap[type ?? ''] || 'icon-file'
}

// 页面加载时请求一次
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@300;400;500;600;700&display=swap');
@import '@/styles/admin-glassmorphism.css';

/* ========== 页面特定样式 ========== */
.filter-actions {
  display: flex;
  gap: 12px;
}

/* 确保下拉框选项文字可见 */
.admin-filter-select {
  color: #2d3436 !important; /* 强制使用深色文字 */
}

.admin-filter-select option {
  color: #2d3436 !important; /* 强制使用深色文字 */
  background: white !important;
}

.admin-filter-select option:hover {
  background: #ff6b6b !important;
  color: white !important;
}

.admin-filter-select option:selected {
  background: #ff6b6b !important;
  color: white !important;
}

.empty-text {
  color: var(--admin-text-secondary);
  font-size: 13px;
}

/* 业务类型标签 */
.business-type-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 600;
}

.business-type-badge.sign-in {
  background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
  color: #1e40af;
}

.business-type-badge.register-reward {
  background: linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%);
  color: #065f46;
}

.business-type-badge.invitee-bonus {
  background: linear-gradient(135deg, #cffafe 0%, #a5f3fc 100%);
  color: #0e7490;
}

.business-type-badge.inviter-bonus {
  background: linear-gradient(135deg, #f3e8ff 0%, #d8b4fe 100%);
  color: #6b21a8;
}

.business-type-badge.generate {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  color: #92400e;
}

.business-type-badge.message {
  background: linear-gradient(135deg, #fae8ff 0%, #f5d0fe 100%);
  color: #86198f;
}

.business-type-badge.deploy {
  background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
  color: #991b1b;
}

.business-type-badge.download {
  background: linear-gradient(135deg, #e0e7ff 0%, #c7d2fe 100%);
  color: #3730a3;
}

.business-type-badge.system-grant {
  background: linear-gradient(135deg, #fce7f3 0%, #fbcfe8 100%);
  color: #9d174d;
}

/* 积分类型标签 */
.income-badge,
.expense-badge,
.before-points-badge,
.after-points-badge,
.remark-badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
}

/* 积分变动值 */
.point-change-value.income {
  color: #10b981;
}

.point-change-value.expense {
  color: #ef4444;
}

/* SVG 图标定义（隐藏） */
.svg-icons {
  display: none;
}
</style>
