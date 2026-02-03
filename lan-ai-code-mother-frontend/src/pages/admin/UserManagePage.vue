<template>
  <div class="user-manage-wrapper admin-manage-wrapper">
    <!-- 动态渐变背景 -->
    <div class="gradient-bg admin-gradient-bg"></div>

    <!-- 主容器 -->
    <div class="manage-container admin-manage-container">
      <!-- 页面标题区 -->
      <div class="page-header admin-page-header">
        <h1 class="page-title admin-page-title">用户管理</h1>
        <p class="page-subtitle admin-page-subtitle">管理系统用户信息与权限</p>
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
            <label class="filter-label admin-filter-label">账号</label>
            <input
              v-model="searchParams.userAccount"
              class="filter-input admin-filter-input"
              placeholder="请输入账号"
            />
          </div>

          <div class="filter-item admin-filter-item">
            <label class="filter-label admin-filter-label">用户名</label>
            <input
              v-model="searchParams.userName"
              class="filter-input admin-filter-input"
              placeholder="请输入用户名"
            />
          </div>
        </div>
      </div>

      <!-- 用户列表卡片 -->
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
              <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
              <circle cx="9" cy="7" r="4"></circle>
              <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
              <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
            </svg>
            <span>用户列表</span>
            <span class="table-count admin-table-count">{{ total }} 个</span>
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
          class="user-table admin-data-table"
          @change="doTableChange"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.dataIndex === 'id'">
              <span class="id-badge admin-id-badge">{{ record.id }}</span>
            </template>

            <template v-else-if="column.dataIndex === 'userAccount'">
              <span
                class="account-badge admin-type-badge"
                style="
                  background: linear-gradient(135deg, #f3e8ff 0%, #e9d5ff 100%);
                  color: #9333ea;
                "
                >{{ record.userAccount }}</span
              >
            </template>

            <template v-else-if="column.dataIndex === 'userName'">
              <span
                v-if="record.userName"
                class="name-badge admin-type-badge"
                style="
                  background: linear-gradient(135deg, #e0f2fe 0%, #bae6fd 100%);
                  color: #0284c7;
                "
                >{{ truncateText(record.userName, 12) }}</span
              >
              <span v-else class="empty-text">未命名</span>
            </template>

            <template v-else-if="column.dataIndex === 'userAvatar'">
              <a-image
                :src="record.userAvatar"
                :width="60"
                class="user-avatar"
                fallback="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAMAAAADACAMAAABlApw1AAAABlBMVEUAAAD///+l2Z/dAAAAAXRSTlMAQObYZgAAAAxJREFUeNrtwTEBAAAAwqD1T20JT6AAAH4MAAAAA="
              />
            </template>

            <template v-else-if="column.dataIndex === 'userProfile'">
              <span
                v-if="record.userProfile"
                class="profile-badge admin-type-badge"
                style="
                  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
                  color: #16a34a;
                "
                >{{ truncateText(record.userProfile, 12) }}</span
              >
              <span v-else class="empty-text">-</span>
            </template>

            <template v-else-if="column.dataIndex === 'userRole'">
              <span
                v-if="record.userRole === 'admin'"
                class="role-badge-admin admin-type-badge"
                style="
                  background: linear-gradient(135deg, #dcfce7 0%, #bbf7d0 100%);
                  color: #16a34a;
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
                  <path
                    d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"
                  ></path>
                </svg>
                管理员
              </span>
              <span
                v-else
                class="role-badge-user admin-type-badge"
                style="
                  background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
                  color: #2563eb;
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
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                  <circle cx="12" cy="7" r="4"></circle>
                </svg>
                普通用户
              </span>
            </template>

            <template v-else-if="column.dataIndex === 'createTime'">
              <span class="time-text admin-time-text">{{ formatTime(record.createTime) }}</span>
            </template>

            <template v-else-if="column.key === 'action'">
              <div class="action-buttons admin-action-buttons">
                <button
                  class="action-btn-sm danger admin-action-btn-sm danger"
                  @click="doDelete(record.id)"
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
import { deleteUser, listUserVoByPage } from '@/api/userController'
import { message } from 'ant-design-vue'
import { formatTime } from '@/utils/time'

const columns = [
  {
    title: '用户ID',
    dataIndex: 'id',
    width: 150,
    fixed: 'left' as const,
  },
  {
    title: '账号',
    dataIndex: 'userAccount',
    width: 120,
  },
  {
    title: '用户名',
    dataIndex: 'userName',
    width: 120,
  },
  {
    title: '头像',
    dataIndex: 'userAvatar',
    width: 100,
  },
  {
    title: '简介',
    dataIndex: 'userProfile',
    width: 100,
  },
  {
    title: '用户角色',
    dataIndex: 'userRole',
    width: 120,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 150,
  },
  {
    title: '操作',
    key: 'action',
    width: 120,
  },
]

// 数据
const data = ref<API.UserVO[]>([])
const total = ref(0)

// 搜索条件
const searchParams = reactive<API.UserQueryRequest>({
  pageNum: 1,
  pageSize: 10,
})

// 获取数据
const fetchData = async () => {
  try {
    const res = await listUserVoByPage({
      ...searchParams,
    })
    if (res.data.data) {
      data.value = res.data.data.records ?? []
      total.value = res.data.data.totalRow ?? 0
    } else {
      message.error('获取数据失败，' + res.data.message)
    }
  } catch {
    message.error('获取数据失败，请检查网络连接')
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
    pageSizeOptions: ['10', '20', '50', '100'],
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

// 删除数据
const doDelete = async (id: string) => {
  if (!id) {
    return
  }

  try {
    const res = await deleteUser({ id })
    if (res.data.code === 0) {
      message.success('删除成功')
      fetchData()
    } else {
      message.error(res.data.message || '删除失败')
    }
  } catch {
    message.error('删除失败，请检查网络连接')
  }
}

// 截取文本
const truncateText = (text: string, maxLength: number) => {
  if (!text) return ''
  if (text.length <= maxLength) return text
  return text.substring(0, maxLength) + '...'
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
.user-avatar {
  border-radius: 12px;
  border: 2px solid rgba(255, 107, 107, 0.2);
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.user-avatar:hover {
  transform: scale(1.1);
  border-color: var(--admin-color-primary);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);
}

.empty-text {
  color: var(--admin-text-secondary);
  font-size: 13px;
}
</style>
