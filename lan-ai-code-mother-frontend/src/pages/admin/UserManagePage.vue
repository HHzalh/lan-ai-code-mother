<template>
  <div class="user-manage-container">
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
            <TeamOutlined class="header-icon" />
          </div>
          <div class="header-text">
            <h1 class="page-title">用户管理</h1>
            <p class="page-subtitle">
              <SafetyOutlined />
              管理系统用户信息与权限
            </p>
          </div>
        </div>
        <div class="header-stats">
          <div class="stat-card stat-total">
            <UserOutlined class="stat-icon" />
            <div class="stat-info">
              <div class="stat-value">{{ total }}</div>
              <div class="stat-label">总用户数</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 内容卡片 -->
      <div class="main-card info-card">
        <!-- 搜索区域 -->
        <div class="search-section">
          <div class="search-header">
            <SearchOutlined class="search-icon" />
            <span class="search-title">筛选条件</span>
          </div>
          <a-form :model="searchParams" class="search-form" layout="vertical" @finish="doSearch">
            <div class="search-grid">
              <a-form-item label="账号">
                <a-input
                  v-model:value="searchParams.userAccount"
                  placeholder="请输入账号"
                  size="default"
                >
                  <template #prefix>
                    <UserOutlined class="input-icon" />
                  </template>
                </a-input>
              </a-form-item>
              <a-form-item label="用户名">
                <a-input
                  v-model:value="searchParams.userName"
                  placeholder="请输入用户名"
                  size="default"
                >
                  <template #prefix>
                    <IdcardOutlined class="input-icon" />
                  </template>
                </a-input>
              </a-form-item>
              <a-form-item class="search-button-item">
                <a-button html-type="submit" size="default" type="primary" @click="doSearch">
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

        <!-- 数据表格区域 -->
        <div class="table-section">
          <div class="table-header">
            <div class="table-header-left">
              <TableOutlined class="table-icon" />
              <span class="table-title">用户列表</span>
            </div>
            <a-button :icon="h(ReloadOutlined)" class="refresh-btn" size="large" @click="fetchData">
              刷新
            </a-button>
          </div>

          <a-table
            :columns="columns"
            :data-source="data"
            :pagination="pagination"
            :scroll="{ x: 1400 }"
            class="user-table"
            @change="doTableChange"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.dataIndex === 'id'">
                <a-tag class="id-tag">
                  <IdcardOutlined />
                  {{ record.id }}
                </a-tag>
              </template>
              <template v-else-if="column.dataIndex === 'userAccount'">
                <a-tag class="account-tag">
                  <UserOutlined />
                  {{ record.userAccount }}
                </a-tag>
              </template>
              <template v-else-if="column.dataIndex === 'userName'">
                <a-tooltip v-if="record.userName" :title="record.userName">
                  <a-tag class="name-tag">
                    <IdcardOutlined />
                    {{ truncateText(record.userName, 12) }}
                  </a-tag>
                </a-tooltip>
                <a-tag v-else class="name-tag">
                  <IdcardOutlined />
                  未命名
                </a-tag>
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
                <a-tooltip v-if="record.userProfile" :title="record.userProfile">
                  <a-tag class="profile-tag">
                    <FileTextOutlined />
                    {{ truncateText(record.userProfile, 12) }}
                  </a-tag>
                </a-tooltip>
                <span v-else class="empty-text">-</span>
              </template>
              <template v-else-if="column.dataIndex === 'userRole'">
                <a-tag v-if="record.userRole === 'admin'" class="role-tag role-admin">
                  <CrownOutlined />
                  管理员
                </a-tag>
                <a-tag v-else class="role-tag role-user">
                  <UserOutlined />
                  普通用户
                </a-tag>
              </template>
              <template v-else-if="column.dataIndex === 'createTime'">
                <div class="time-cell">
                  <CalendarOutlined class="time-icon" />
                  <span>{{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}</span>
                </div>
              </template>
              <template v-else-if="column.key === 'action'">
                <a-button danger size="large" type="primary" @click="doDelete(record.id)">
                  <template #icon>
                    <DeleteOutlined />
                  </template>
                  删除
                </a-button>
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
import { deleteUser, listUserVoByPage } from '@/api/userController'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import {
  CalendarOutlined,
  CrownOutlined,
  DeleteOutlined,
  FileTextOutlined,
  IdcardOutlined,
  ReloadOutlined,
  SafetyOutlined,
  SearchOutlined,
  TableOutlined,
  TeamOutlined,
  UserOutlined,
} from '@ant-design/icons-vue'

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
    width: 150,
  },
  {
    title: '用户名',
    dataIndex: 'userName',
    width: 150,
  },
  {
    title: '头像',
    dataIndex: 'userAvatar',
    width: 100,
  },
  {
    title: '简介',
    dataIndex: 'userProfile',
    width: 200,
    ellipsis: true,
  },
  {
    title: '用户角色',
    dataIndex: 'userRole',
    width: 120,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 180,
  },
  {
    title: '操作',
    key: 'action',
    width: 120,
    fixed: 'right' as const,
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
  } catch (error) {
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
      message.success('✅ 删除成功')
      fetchData()
    } else {
      message.error(res.data.message || '删除失败')
    }
  } catch (error) {
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

.user-manage-container {
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
  background-image: url('https://images.unsplash.com/photo-1556761175-5973dc0f32e7?w=1920&q=80');
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
    rgba(234, 88, 12, 0.97) 50%,
    rgba(251, 191, 36, 0.95) 100%
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
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.5);
}

.page-subtitle {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  margin: 0;
  color: white;
  opacity: 0.95;
  font-weight: 400;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.5);
}

.header-stats {
  display: flex;
  gap: 16px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(20px);
  border-radius: 16px;
  padding: 16px 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  border: 2px solid rgba(255, 255, 255, 0.3);
  min-width: 160px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.stat-info {
  color: white;
}

.stat-value {
  font-family: var(--font-serif);
  font-size: 28px;
  font-weight: 700;
  line-height: 1;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 13px;
  opacity: 0.9;
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
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
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

.search-form :deep(.ant-input) {
  border-radius: 8px;
  height: 32px;
  border: 2px solid var(--color-border);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.search-form :deep(.ant-input:hover) {
  border-color: var(--color-primary);
}

.search-form :deep(.ant-input:focus) {
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
  height: 32px;
  padding: 0 16px;
  font-size: 13px;
  border-radius: 8px;
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

.table-header-left {
  display: flex;
  align-items: center;
  gap: 12px;
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
.user-table :deep(.ant-table) {
  background: white;
  border-radius: 12px;
  overflow: hidden;
}

.user-table :deep(.ant-table-thead > tr > th) {
  background: #3b82f6;
  color: white;
  font-weight: 700;
  font-size: 14px;
  padding: 16px;
  border: none;
  letter-spacing: 0.5px;
}

.user-table :deep(.ant-table-tbody > tr > td) {
  padding: 16px;
  font-size: 14px;
  border-bottom: 1px solid var(--color-border);
  background: white;
}

.user-table :deep(.ant-table-tbody > tr:hover > td) {
  background: var(--color-bg-hover);
}

.user-table :deep(.ant-table-tbody > tr > td:first-child) {
  border-radius: 0 8px 8px 0;
}

.user-table :deep(.ant-table-tbody > tr > td:last-child) {
  border-radius: 8px 0 0 8px;
}

/* 用户头像 */
.user-avatar {
  border-radius: 12px;
  border: 2px solid var(--color-border);
  transition: all 0.3s ease;
}

.user-avatar:hover {
  transform: scale(1.1);
  border-color: var(--color-primary);
  box-shadow: 0 4px 12px rgba(249, 115, 22, 0.3);
}

/* 角色标签 */
.role-tag {
  font-size: 13px;
  padding: 6px 14px;
  border-radius: 8px;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  border: none;
}

.role-admin {
  background: linear-gradient(135deg, #dcfce7 0%, #bbf7d0 100%);
  color: #16a34a;
}

.role-user {
  background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
  color: #2563eb;
}

/* 标签样式 */
.id-tag,
.account-tag,
.name-tag,
.profile-tag {
  font-size: 13px;
  padding: 6px 12px;
  border-radius: 8px;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  border: none;
}

.id-tag {
  background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
  color: #2563eb;
}

.account-tag {
  background: linear-gradient(135deg, #f3e8ff 0%, #e9d5ff 100%);
  color: #9333ea;
}

.name-tag {
  background: linear-gradient(135deg, #e0f2fe 0%, #bae6fd 100%);
  color: #0284c7;
}

.profile-tag {
  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
  color: #16a34a;
  cursor: pointer;
}

.empty-text {
  color: var(--color-text-secondary);
  font-size: 13px;
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
.user-table :deep(.ant-btn-danger) {
  background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
  border: none;
  color: #dc2626;
  border-radius: 10px;
  font-weight: 600;
  height: 32px;
  padding: 0 20px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.user-table :deep(.ant-btn-danger:hover) {
  background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(220, 38, 38, 0.4);
}

/* 分页样式 */
.user-table :deep(.ant-pagination) {
  margin-top: 24px;
}

.user-table :deep(.ant-pagination-item-active) {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  border-color: transparent;
}

.user-table :deep(.ant-pagination-item-active a) {
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

  .header-stats {
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

/* ========== 超强力表单元素优化 ========== */

/* 表单标签 - 纯黑色 + 超粗体 */
.user-manage-container :deep(.ant-form-item-label > label) {
  color: #000000 !important;
  font-size: 15px !important;
  font-weight: 700 !important;
  letter-spacing: 0.5px;
}

/* 输入框 - 纯黑色文字 + 白色背景 */
.user-manage-container :deep(.ant-input),
.user-manage-container :deep(.ant-input-number) {
  background: #ffffff !important;
  border-color: #cbd5e1 !important;
  color: #000000 !important;
  font-weight: 700 !important;
  font-size: 14px !important;
}

.user-manage-container :deep(.ant-input::placeholder),
.user-manage-container :deep(.ant-input-number::placeholder) {
  color: #475569 !important;
  font-weight: 500 !important;
}

.user-manage-container :deep(.ant-input:hover),
.user-manage-container :deep(.ant-input-number:hover) {
  border-color: #3b82f6 !important;
}

.user-manage-container :deep(.ant-input:focus),
.user-manage-container :deep(.ant-input-number:focus),
.user-manage-container :deep(.ant-input-focused) {
  border-color: #3b82f6 !important;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1) !important;
  background: #ffffff !important;
  color: #000000 !important;
}

/* Select 选择器 */
.user-manage-container :deep(.ant-select-selector) {
  background: #ffffff !important;
  border-color: #cbd5e1 !important;
  color: #000000 !important;
  font-weight: 700 !important;
  font-size: 14px !important;
}

.user-manage-container :deep(.ant-select-selection-item) {
  color: #000000 !important;
  font-weight: 700 !important;
}

.user-manage-container :deep(.ant-select-selection-placeholder) {
  color: #475569 !important;
  font-weight: 500 !important;
}

.user-manage-container :deep(.ant-select:hover .ant-select-selector) {
  border-color: #3b82f6 !important;
}

.user-manage-container :deep(.ant-select-focused .ant-select-selector) {
  border-color: #3b82f6 !important;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1) !important;
  color: #000000 !important;
}

/* 表格内容增强 */
.user-manage-container :deep(.ant-table-tbody > tr > td) {
  color: #000000 !important;
  font-weight: 600 !important;
}

/* Modal 增强 */
.user-manage-container :deep(.ant-modal-title) {
  color: #000000 !important;
  font-weight: 700 !important;
}

.user-manage-container :deep(.ant-modal-body) {
  color: #000000 !important;
}

/* Tag 标签增强 */
.user-manage-container :deep(.ant-tag) {
  font-weight: 700 !important;
  color: #000000 !important;
}
</style>
