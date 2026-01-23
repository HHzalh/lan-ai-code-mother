<template>
  <div class="chat-manage-container">
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
            <MessageOutlined class="header-icon" />
          </div>
          <div class="header-text">
            <h1 class="page-title">对话历史管理</h1>
            <p class="page-subtitle">
              <CommentOutlined />
              管理AI对话记录与消息
            </p>
          </div>
        </div>
        <div class="header-actions">
          <a-button :icon="h(ReloadOutlined)" class="action-btn" size="large" @click="fetchData">
            刷新
          </a-button>
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
              <a-form-item label="消息内容">
                <a-input
                  v-model:value="searchParams.message"
                  placeholder="请输入消息内容"
                  size="large"
                >
                  <template #prefix>
                    <SearchOutlined class="input-icon" />
                  </template>
                </a-input>
              </a-form-item>
              <a-form-item label="消息类型">
                <a-select
                  v-model:value="searchParams.messageType"
                  placeholder="选择消息类型"
                  size="large"
                >
                  <a-select-option value="">全部</a-select-option>
                  <a-select-option value="user">用户消息</a-select-option>
                  <a-select-option value="assistant">AI消息</a-select-option>
                </a-select>
              </a-form-item>
              <a-form-item label="应用ID">
                <a-input v-model:value="searchParams.appId" placeholder="请输入应用ID" size="large">
                  <template #prefix>
                    <AppstoreOutlined class="input-icon" />
                  </template>
                </a-input>
              </a-form-item>
              <a-form-item label="用户ID">
                <a-input
                  v-model:value="searchParams.userId"
                  placeholder="请输入用户ID"
                  size="large"
                >
                  <template #prefix>
                    <UserOutlined class="input-icon" />
                  </template>
                </a-input>
              </a-form-item>
              <a-form-item class="search-button-item">
                <a-button block html-type="submit" size="large" type="primary" @click="doSearch">
                  <template #icon>
                    <SearchOutlined />
                  </template>
                  搜索
                </a-button>
              </a-form-item>
            </div>
          </a-form>
        </div>

        <a-divider class="section-divider" />

        <!-- 数据表格 -->
        <div class="table-section">
          <div class="table-header">
            <TableOutlined class="table-icon" />
            <span class="table-title">对话列表</span>
          </div>

          <a-table
            :columns="columns"
            :data-source="data"
            :pagination="pagination"
            :scroll="{ x: 1500 }"
            class="chat-table"
            @change="doTableChange"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.dataIndex === 'id'">
                <a-tag class="id-tag">
                  <NumberOutlined />
                  {{ record.id }}
                </a-tag>
              </template>
              <template v-else-if="column.dataIndex === 'message'">
                <a-tooltip :title="record.message">
                  <div class="message-text">
                    <CommentOutlined class="message-icon" />
                    {{ record.message }}
                  </div>
                </a-tooltip>
              </template>
              <template v-else-if="column.dataIndex === 'messageType'">
                <a-tag
                  :class="{
                    'message-user': record.messageType === 'user',
                    'message-ai': record.messageType === 'assistant',
                  }"
                  class="message-type-tag"
                >
                  <UserOutlined v-if="record.messageType === 'user'" />
                  <RobotOutlined v-else />
                  {{ record.messageType === 'user' ? '用户消息' : 'AI消息' }}
                </a-tag>
              </template>
              <template v-else-if="column.dataIndex === 'appId'">
                <a-tag class="app-tag">
                  <AppstoreOutlined />
                  {{ record.appId }}
                </a-tag>
              </template>
              <template v-else-if="column.dataIndex === 'userId'">
                <a-tag class="user-tag">
                  <UserOutlined />
                  {{ record.userId }}
                </a-tag>
              </template>
              <template v-else-if="column.dataIndex === 'createTime'">
                <div class="time-cell">
                  <CalendarOutlined class="time-icon" />
                  <span>{{ formatTime(record.createTime) }}</span>
                </div>
              </template>
              <template v-else-if="column.key === 'action'">
                <a-space>
                  <a-button size="large" type="primary" @click="viewAppChat(record.appId)">
                    <template #icon>
                      <EyeOutlined />
                    </template>
                    查看对话
                  </a-button>
                  <a-popconfirm title="确定要删除这条消息吗？" @confirm="deleteMessage(record.id)">
                    <a-button danger size="large" type="primary">
                      <template #icon>
                        <DeleteOutlined />
                      </template>
                      删除
                    </a-button>
                  </a-popconfirm>
                </a-space>
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
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { listAllChatHistoryByPageForAdmin } from '@/api/chatHistoryController'
import { formatTime } from '@/utils/time'
import {
  AppstoreOutlined,
  CalendarOutlined,
  CommentOutlined,
  DeleteOutlined,
  EyeOutlined,
  FilterOutlined,
  MessageOutlined,
  NumberOutlined,
  ReloadOutlined,
  RobotOutlined,
  SearchOutlined,
  TableOutlined,
  UserOutlined,
} from '@ant-design/icons-vue'

const router = useRouter()

const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    width: 100,
    fixed: 'left' as const,
  },
  {
    title: '消息内容',
    dataIndex: 'message',
    width: 350,
  },
  {
    title: '消息类型',
    dataIndex: 'messageType',
    width: 140,
  },
  {
    title: '应用ID',
    dataIndex: 'appId',
    width: 120,
  },
  {
    title: '用户ID',
    dataIndex: 'userId',
    width: 120,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 170,
  },
  {
    title: '操作',
    key: 'action',
    width: 220,
    fixed: 'right' as const,
  },
]

// 数据
const data = ref<API.ChatHistory[]>([])
const total = ref(0)

// 搜索条件
const searchParams = reactive<API.ChatHistoryQueryRequest>({
  pageNum: 1,
  pageSize: 10,
})

// 获取数据
const fetchData = async () => {
  try {
    const res = await listAllChatHistoryByPageForAdmin({
      ...searchParams,
    })
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

// 页面加载时请求一次
onMounted(() => {
  fetchData()
})

// 分页参数
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
const doTableChange = (page: { current: number; pageSize: number }) => {
  searchParams.pageNum = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}

// 搜索
const doSearch = () => {
  searchParams.pageNum = 1
  fetchData()
}

// 查看应用对话
const viewAppChat = (appId: number | undefined) => {
  if (appId) {
    router.push(`/app/chat/${appId}`)
  }
}

// 删除消息
const deleteMessage = async (id: number | undefined) => {
  if (!id) return

  try {
    // 注意：这里需要后端提供删除对话历史的接口
    message.success('✅ 删除成功')
    fetchData()
  } catch (error) {
    console.error('删除失败：', error)
    message.error('删除失败')
  }
}
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

.chat-manage-container {
  min-height: 100vh;
  padding: 0;
  position: relative;
  overflow: hidden;
}

/* Hero 背景 */
.hero-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: url('https://images.unsplash.com/photo-1535905557558-afc4877a26fc?w=1920&q=80');
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28px;
  gap: 24px;
  flex-wrap: wrap;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
  flex: 1;
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
  text-shadow:
    0 2px 4px rgba(0, 0, 0, 0.25),
    0 4px 12px rgba(0, 0, 0, 0.2);
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

.header-actions {
  display: flex;
  gap: 12px;
}

.action-btn {
  height: 48px;
  border-radius: 12px;
  font-weight: 600;
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(20px);
  border: 2px solid rgba(255, 255, 255, 0.3);
  color: white;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.action-btn:hover {
  background: rgba(255, 255, 255, 0.35);
  transform: translateY(-2px);
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
  align-items: end;
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
  border-radius: 12px;
  height: 48px;
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

.search-button-item {
  grid-column: span 1;
}

.search-button-item :deep(.ant-btn) {
  height: 48px;
  border-radius: 12px;
  font-weight: 600;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(249, 115, 22, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.search-button-item :deep(.ant-btn:hover) {
  background: linear-gradient(135deg, var(--color-primary-dark) 0%, #dc2626 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(249, 115, 22, 0.4);
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
}

/* 表格样式 */
.chat-table :deep(.ant-table) {
  background: white;
  border-radius: 12px;
  overflow: hidden;
}

.chat-table :deep(.ant-table-thead > tr > th) {
  background: #3b82f6;
  color: white;
  font-weight: 700;
  font-size: 14px;
  padding: 16px;
  border: none;
  letter-spacing: 0.5px;
}

.chat-table :deep(.ant-table-tbody > tr > td) {
  padding: 16px;
  font-size: 14px;
  border-bottom: 1px solid var(--color-border);
}

.chat-table :deep(.ant-table-tbody > tr:hover > td) {
  background: var(--color-bg-hover);
}

/* ID标签 */
.id-tag {
  font-size: 13px;
  padding: 6px 12px;
  border-radius: 8px;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  border: none;
  background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
  color: #2563eb;
}

/* 消息文本 */
.message-text {
  display: flex;
  align-items: center;
  gap: 8px;
  max-width: 350px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 13px;
  color: var(--color-text-secondary);
}

.message-icon {
  color: var(--color-primary);
  font-size: 14px;
  flex-shrink: 0;
}

/* 消息类型标签 */
.message-type-tag {
  font-size: 13px;
  padding: 6px 12px;
  border-radius: 8px;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  border: none;
}

.message-user {
  background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
  color: #2563eb;
}

.message-ai {
  background: linear-gradient(135deg, #dcfce7 0%, #bbf7d0 100%);
  color: #16a34a;
}

/* 应用标签 */
.app-tag {
  font-size: 13px;
  padding: 6px 12px;
  border-radius: 8px;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  border: none;
  background: linear-gradient(135deg, #f3e8ff 0%, #e9d5ff 100%);
  color: #9333ea;
}

/* 用户标签 */
.user-tag {
  font-size: 13px;
  padding: 6px 12px;
  border-radius: 8px;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  border: none;
  background: linear-gradient(135deg, #ffedd5 0%, #fed7aa 100%);
  color: #ea580c;
}

/* 时间单元格 */
.time-cell {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--color-text-secondary);
}

.time-icon {
  color: var(--color-primary);
  font-size: 14px;
}

/* 操作按钮 */
.chat-table :deep(.ant-space) {
  display: flex;
  gap: 8px;
}

.chat-table :deep(.ant-btn-primary) {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  border: none;
  border-radius: 10px;
  font-weight: 600;
  height: 32px;
  padding: 0 20px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.chat-table :deep(.ant-btn-primary:hover) {
  background: linear-gradient(135deg, var(--color-primary-dark) 0%, #dc2626 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(249, 115, 22, 0.4);
}

.chat-table :deep(.ant-btn-dangerous) {
  background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
  border: none;
  color: #dc2626;
  border-radius: 10px;
  font-weight: 600;
  height: 32px;
  padding: 0 20px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.chat-table :deep(.ant-btn-dangerous:hover) {
  background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(220, 38, 38, 0.4);
}

/* 分页样式 */
.chat-table :deep(.ant-pagination) {
  margin-top: 24px;
}

.chat-table :deep(.ant-pagination-item-active) {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  border-color: transparent;
}

.chat-table :deep(.ant-pagination-item-active a) {
  color: white;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .page-header {
    flex-direction: column;
    align-items: stretch;
  }

  .header-left {
    flex-direction: column;
    align-items: flex-start;
  }

  .header-actions {
    justify-content: flex-start;
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

  .search-button-item {
    grid-column: span 1;
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
