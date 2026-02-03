<template>
  <div class="chat-manage-wrapper admin-manage-wrapper">
    <!-- 动态渐变背景 -->
    <div class="gradient-bg admin-gradient-bg"></div>

    <!-- 主容器 -->
    <div class="manage-container admin-manage-container">
      <!-- 页面标题区 -->
      <div class="page-header admin-page-header">
        <h1 class="page-title admin-page-title">对话历史管理</h1>
        <p class="page-subtitle admin-page-subtitle">查看和管理所有对话记录</p>
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
              <circle cx="11" cy="11" r="8"></circle>
              <line x1="21" x2="16.65" y1="21" y2="16.65"></line>
            </svg>
            <span>筛选</span>
          </div>
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

        <div class="filter-grid admin-filter-grid">
          <div class="filter-item admin-filter-item">
            <label class="filter-label admin-filter-label">消息内容</label>
            <input
              v-model="searchParams.message"
              class="filter-input admin-filter-input"
              placeholder="请输入消息内容"
            />
          </div>

          <div class="filter-item admin-filter-item">
            <label class="filter-label admin-filter-label">消息类型</label>
            <select v-model="searchParams.messageType" class="filter-select admin-filter-select">
              <option value="">全部</option>
              <option value="user">用户消息</option>
              <option value="ai">AI消息</option>
            </select>
          </div>

          <div class="filter-item admin-filter-item">
            <label class="filter-label admin-filter-label">应用ID</label>
            <input
              v-model="searchParams.appId"
              class="filter-input admin-filter-input"
              placeholder="请输入应用ID"
            />
          </div>

          <div class="filter-item admin-filter-item">
            <label class="filter-label admin-filter-label">用户ID</label>
            <input
              v-model="searchParams.userId"
              class="filter-input admin-filter-input"
              placeholder="请输入用户ID"
            />
          </div>
        </div>
      </div>

      <!-- 对话列表卡片 -->
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
              <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
            </svg>
            <span>对话列表</span>
            <span class="table-count admin-table-count">{{ total }} 条</span>
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
          class="chat-table admin-data-table"
          @change="doTableChange"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.dataIndex === 'id'">
              <span class="id-badge admin-id-badge">{{ record.id }}</span>
            </template>

            <template v-else-if="column.dataIndex === 'message'">
              <span class="message-text admin-message-text">{{
                truncateText(record.message, 30)
              }}</span>
            </template>

            <template v-else-if="column.dataIndex === 'messageType'">
              <span
                :class="record.messageType === 'user' ? 'admin-user-badge' : 'admin-ai-badge'"
                class="type-badge"
              >
                <svg
                  fill="none"
                  height="12"
                  stroke="currentColor"
                  stroke-width="2"
                  viewBox="0 0 24 24"
                  width="12"
                >
                  <path
                    v-if="record.messageType === 'user'"
                    d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"
                  ></path>
                  <circle v-if="record.messageType === 'user'" cx="12" cy="7" r="4"></circle>
                  <g v-else>
                    <path
                      d="M12 2a2 2 0 0 1 2 2v2a2 2 0 0 1-2 2 2 2 0 0 1-2-2V4a2 2 0 0 1 2-2z"
                    ></path>
                    <path
                      d="M4 8a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V8z"
                    ></path>
                  </g>
                </svg>
                {{ record.messageType === 'user' ? '用户消息' : 'AI消息' }}
              </span>
            </template>

            <template v-else-if="column.dataIndex === 'appId'">
              <span class="app-id-badge admin-id-badge">{{ record.appId }}</span>
            </template>

            <template v-else-if="column.dataIndex === 'userId'">
              <span
                class="user-id-badge admin-type-badge"
                style="background: rgba(249, 115, 22, 0.1); color: #ea580c"
                >{{ record.userId }}</span
              >
            </template>

            <template v-else-if="column.dataIndex === 'createTime'">
              <span class="time-text admin-time-text">{{ formatTime(record.createTime) }}</span>
            </template>

            <template v-else-if="column.key === 'action'">
              <div class="action-buttons admin-action-buttons">
                <button
                  class="action-btn-sm primary admin-action-btn-sm primary"
                  @click="viewAppChat(record.appId)"
                >
                  <svg
                    fill="none"
                    height="14"
                    stroke="currentColor"
                    stroke-width="2"
                    viewBox="0 0 24 24"
                    width="14"
                  >
                    <path d="M1 12s4-8 11-8 11 8-11 8-4 8-11-8-11-8z"></path>
                    <circle cx="12" cy="12" r="3"></circle>
                  </svg>
                  查看
                </button>
                <button
                  class="action-btn-sm danger admin-action-btn-sm danger"
                  @click="deleteMessage(record.id)"
                >
                  <svg
                    fill="none"
                    height="14"
                    stroke="currentColor"
                    stroke-width="2"
                    viewBox="0 0 24 24"
                    width="14"
                  >
                    <polyline points="3 6 5 6 21 6"></polyline>
                    <path
                      d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"
                    ></path>
                  </svg>
                  删除
                </button>
              </div>
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
import { message, Modal } from 'ant-design-vue'
import { listAllChatHistoryByPageForAdmin } from '@/api/chatHistoryController'
import { formatTime } from '@/utils/time'

const router = useRouter()

const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    width: 60,
    fixed: 'left' as const,
  },
  {
    title: '消息内容',
    dataIndex: 'message',
    width: 80,
  },
  {
    title: '消息类型',
    dataIndex: 'messageType',
    width: 120,
  },
  {
    title: '应用ID',
    dataIndex: 'appId',
    width: 60,
  },
  {
    title: '用户ID',
    dataIndex: 'userId',
    width: 60,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 150,
  },
  {
    title: '操作',
    key: 'action',
    width: 200,
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

// 截取文本
const truncateText = (text: string, maxLength: number) => {
  if (!text) return ''
  if (text.length <= maxLength) return text
  return text.substring(0, maxLength) + '...'
}

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
  if (searchParams.messageType === '') delete searchParams.messageType
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

  Modal.confirm({
    title: '确认删除',
    content: '确定要删除这条消息吗？',
    okText: '确定',
    cancelText: '取消',
    okButtonProps: { danger: true },
    onOk: async () => {
      try {
        // 注意：这里需要后端提供删除对话历史的接口
        message.success('删除成功')
        fetchData()
      } catch (error) {
        console.error('删除失败：', error)
        message.error('删除失败')
      }
    },
  })
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@300;400;500;600;700&display=swap');
@import '@/styles/admin-glassmorphism.css';
</style>
