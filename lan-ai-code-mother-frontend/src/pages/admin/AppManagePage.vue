<template>
  <div class="app-manage-container">
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
            <AppstoreOutlined class="header-icon" />
          </div>
          <div class="header-text">
            <h1 class="page-title">应用管理</h1>
            <p class="page-subtitle">
              <CodeOutlined />
              管理平台应用与精选配置
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
              <a-form-item label="应用名称">
                <a-input
                  v-model:value="searchParams.appName"
                  placeholder="请输入应用名称"
                  size="default"
                >
                  <template #prefix>
                    <AppstoreOutlined class="input-icon" />
                  </template>
                </a-input>
              </a-form-item>
              <a-form-item label="创建者ID">
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
              <a-form-item label="生成类型">
                <a-select
                  v-model:value="searchParams.codeGenType"
                  placeholder="选择生成类型"
                  size="default"
                >
                  <a-select-option value="">全部</a-select-option>
                  <a-select-option
                    v-for="option in CODE_GEN_TYPE_OPTIONS"
                    :key="option.value"
                    :value="option.value"
                  >
                    {{ option.label }}
                  </a-select-option>
                </a-select>
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

        <!-- 数据表格 -->
        <div class="table-section">
          <div class="table-header">
            <TableOutlined class="table-icon" />
            <span class="table-title">应用列表</span>
          </div>

          <a-table
            :columns="columns"
            :data-source="data"
            :pagination="pagination"
            :scroll="{ x: 1600 }"
            class="app-table"
            @change="doTableChange"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.dataIndex === 'id'">
                <a-tag class="id-tag">
                  <IdcardOutlined />
                  {{ record.id }}
                </a-tag>
              </template>
              <template v-else-if="column.dataIndex === 'appName'">
                <a-tooltip :title="record.appName">
                  <a-tag class="app-name-tag">
                    <AppstoreOutlined />
                    {{ truncateText(record.appName, 12) }}
                  </a-tag>
                </a-tooltip>
              </template>
              <template v-else-if="column.dataIndex === 'cover'">
                <a-image
                  v-if="record.cover"
                  :height="50"
                  :src="record.cover"
                  :width="70"
                  class="app-cover"
                  fallback="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAMAAAADACAMAAABlApw1AAAABlBMVEUAAAD///+l2Z/dAAAAAXRSTlMAQObYZgAAAAxJREFUeNrtwTEBAAAAwqD1T20JT6AAAH4MAAAAA="
                />
                <div v-else class="no-cover">
                  <PictureOutlined />
                  无封面
                </div>
              </template>
              <template v-else-if="column.dataIndex === 'initPrompt'">
                <a-tooltip :title="record.initPrompt">
                  <a-tag class="prompt-tag">
                    <MessageOutlined />
                    {{ truncateText(record.initPrompt, 12) }}
                  </a-tag>
                </a-tooltip>
              </template>
              <template v-else-if="column.dataIndex === 'codeGenType'">
                <a-tag
                  :style="{ background: getCodeGenTypeConfig(record.codeGenType)?.backgroundColor }"
                  :class="['type-tag', getCodeGenTypeConfig(record.codeGenType)?.className]"
                >
                  <component :is="getCodeGenTypeConfig(record.codeGenType)?.icon" />
                  {{ formatCodeGenType(record.codeGenType) }}
                </a-tag>
              </template>
              <template v-else-if="column.dataIndex === 'priority'">
                <a-tag v-if="record.priority === 99" class="featured-tag">
                  <StarOutlined />
                  精选
                </a-tag>
                <a-tag v-else class="priority-tag">
                  <NumberOutlined />
                  {{ record.priority || 0 }}
                </a-tag>
              </template>
              <template v-else-if="column.dataIndex === 'deployedTime'">
                <a-tag v-if="record.deployedTime" class="deploy-time-tag">
                  <RocketOutlined />
                  {{ formatTime(record.deployedTime) }}
                </a-tag>
                <a-tag v-else class="no-deploy-tag">
                  <ClockCircleOutlined />
                  未部署
                </a-tag>
              </template>
              <template v-else-if="column.dataIndex === 'createTime'">
                <div class="time-cell">
                  <CalendarOutlined class="time-icon" />
                  <span>{{ formatTime(record.createTime) }}</span>
                </div>
              </template>
              <template v-else-if="column.dataIndex === 'user'">
                <a-tooltip
                  v-if="record.user"
                  :title="record.user.userName || record.user.userAccount || '未知用户'"
                >
                  <div class="user-cell">
                    <a-avatar :size="24" :src="record.user.userAvatar">
                      {{
                        record.user.userName?.charAt(0) || record.user.userAccount?.charAt(0) || 'U'
                      }}
                    </a-avatar>
                    <span class="user-name-text">{{
                      truncateText(
                        record.user.userName || record.user.userAccount || '未知用户',
                        12,
                      )
                    }}</span>
                  </div>
                </a-tooltip>
                <span v-else class="empty-text">-</span>
              </template>
              <template v-else-if="column.key === 'action'">
                <a-space>
                  <a-button size="large" type="primary" @click="editApp(record)">
                    <template #icon>
                      <EditOutlined />
                    </template>
                    编辑
                  </a-button>
                  <a-button
                    :class="{ 'featured-btn': record.priority === 99 }"
                    size="large"
                    @click="toggleFeatured(record)"
                  >
                    <template #icon>
                      <StarOutlined />
                    </template>
                    {{ record.priority === 99 ? '取消精选' : '精选' }}
                  </a-button>
                  <a-popconfirm title="确定要删除这个应用吗？" @confirm="deleteApp(record.id)">
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
import { deleteAppByAdmin, listAppVoByPageByAdmin, updateAppByAdmin } from '@/api/appController'
import { CodeGenTypeEnum, CODE_GEN_TYPE_CONFIG, CODE_GEN_TYPE_OPTIONS, formatCodeGenType } from '@/utils/codeGenTypes'
import { formatTime } from '@/utils/time'
import {
  AppstoreOutlined,
  CalendarOutlined,
  ClockCircleOutlined,
  CodeOutlined,
  DeleteOutlined,
  EditOutlined,
  FilterOutlined,
  IdcardOutlined,
  MessageOutlined,
  NumberOutlined,
  PictureOutlined,
  ReloadOutlined,
  RocketOutlined,
  SearchOutlined,
  StarOutlined,
  TableOutlined,
  UserOutlined,
} from '@ant-design/icons-vue'

const router = useRouter()

const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    width: 120,
    fixed: 'left' as const,
  },
  {
    title: '应用名称',
    dataIndex: 'appName',
    width: 130,
  },
  {
    title: '封面',
    dataIndex: 'cover',
    width: 80,
  },
  {
    title: '初始提示词',
    dataIndex: 'initPrompt',
    width: 130,
  },
  {
    title: '生成类型',
    dataIndex: 'codeGenType',
    width: 90,
  },
  {
    title: '优先级',
    dataIndex: 'priority',
    width: 60,
  },
  {
    title: '部署时间',
    dataIndex: 'deployedTime',
    width: 100,
  },
  {
    title: '创建者',
    dataIndex: 'user',
    width: 100,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 100,
  },
  {
    title: '操作',
    key: 'action',
    width: 180,
    fixed: 'right' as const,
  },
]

// 数据
const data = ref<API.AppVO[]>([])
const total = ref(0)

// 搜索条件
const searchParams = reactive<API.AppQueryRequest>({
  pageNum: 1,
  pageSize: 10,
})

// 获取 codeGenType 配置
const getCodeGenTypeConfig = (codeGenType?: string) => {
  if (!codeGenType) return null
  return CODE_GEN_TYPE_CONFIG[codeGenType as CodeGenTypeEnum]
}

// 获取数据
const fetchData = async () => {
  try {
    const res = await listAppVoByPageByAdmin({
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

// 编辑应用
const editApp = (app: API.AppVO) => {
  router.push(`/app/edit/${app.id}`)
}

// 切换精选状态
const toggleFeatured = async (app: API.AppVO) => {
  if (!app.id) return

  const newPriority = app.priority === 99 ? 0 : 99
  const oldPriority = app.priority

  // 立即更新本地数据，让按钮状态立即变化
  const appIndex = data.value.findIndex((item) => item.id === app.id)
  if (appIndex !== -1) {
    data.value[appIndex].priority = newPriority
  }

  try {
    const res = await updateAppByAdmin({
      id: app.id,
      priority: newPriority,
    })

    if (res.data.code === 0) {
      message.success(newPriority === 99 ? ' 已设为精选' : ' 已取消精选')
      // 刷新数据以确保数据同步
      fetchData()
    } else {
      // 如果失败，回滚本地数据
      if (appIndex !== -1) {
        data.value[appIndex].priority = oldPriority
      }
      message.error('操作失败：' + res.data.message)
    }
  } catch (error) {
    // 如果失败，回滚本地数据
    if (appIndex !== -1) {
      data.value[appIndex].priority = oldPriority
    }
    console.error('操作失败：', error)
    message.error('操作失败')
  }
}

// 删除应用
const deleteApp = async (id: number | undefined) => {
  if (!id) return

  try {
    const res = await deleteAppByAdmin({ id })
    if (res.data.code === 0) {
      message.success('✅ 删除成功')
      fetchData()
    } else {
      message.error('删除失败：' + res.data.message)
    }
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

.app-manage-container {
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
  background-image: url('https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=1920&q=80');
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
  font-weight: 700;
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
  font-weight: 700;
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
  font-weight: 700;
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

.search-button-item {
  grid-column: span 1;
}

.search-button-item :deep(.ant-btn) {
  height: 32px;
  padding: 0 16px;
  font-size: 13px;
  border-radius: 8px;
  font-weight: 700;
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
  justify-content: flex-start;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 2px solid var(--color-border);
  gap: 12px;
}

.table-icon {
  font-size: 20px;
  color: var(--color-primary);
}

.table-title {
  font-family: var(--font-serif);
  font-size: 18px;
  font-weight: 700;
  color: var(--color-text);
}

/* 表格样式 */
.app-table :deep(.ant-table) {
  background: white;
  border-radius: 12px;
  overflow: hidden;
}

.app-table :deep(.ant-table-thead > tr > th) {
  background: #3b82f6;
  color: white;
  font-weight: 700;
  font-size: 12px;
  padding: 12px;
  border: none;
  letter-spacing: 0.5px;
}

.app-table :deep(.ant-table-tbody > tr > td) {
  padding: 12px;
  font-size: 12px;
  border-bottom: 1px solid var(--color-border);
  background: white;
}

.app-table :deep(.ant-table-tbody > tr:hover > td) {
  background: var(--color-bg-hover);
}

/* ID标签 */
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

/* 应用名称标签 */
.app-name-tag {
  font-size: 11px;
  padding: 4px 10px;
  border-radius: 6px;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  border: none;
  background: linear-gradient(135deg, #e0f2fe 0%, #bae6fd 100%);
  color: #0284c7;
  cursor: pointer;
}

/* 封面图片 */
.app-cover {
  border-radius: 8px;
  border: 2px solid var(--color-border);
  transition: all 0.3s ease;
}

.app-cover:hover {
  transform: scale(1.05);
  border-color: var(--color-primary);
  box-shadow: 0 4px 12px rgba(249, 115, 22, 0.3);
}

.no-cover {
  width: 70px;
  height: 50px;
  background: var(--color-bg-hover);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  color: var(--color-text-secondary);
  font-size: 10px;
  border-radius: 8px;
  border: 2px solid var(--color-border);
}

.no-cover .anticon {
  font-size: 20px;
}

/* 提示词标签 */
.prompt-tag {
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

/* 类型标签 */
.type-tag {
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

/* 精选标签 */
.featured-tag {
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

/* 优先级标签 */
.priority-tag {
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

/* 部署时间标签 */
.deploy-time-tag {
  font-size: 11px;
  padding: 4px 10px;
  border-radius: 6px;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  border: none;
  background: linear-gradient(135deg, #dcfce7 0%, #bbf7d0 100%);
  color: #16a34a;
}

/* 创建者单元格 */
.user-cell {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.user-name-text {
  font-size: 11px;
  color: var(--color-text);
  font-weight: 600;
}

.empty-text {
  color: var(--color-text-secondary);
  font-size: 11px;
}

/* 时间单元格 */
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

.no-deploy-tag {
  font-size: 11px;
  padding: 4px 10px;
  border-radius: 6px;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  border: none;
  background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);
  color: #64748b;
}

/* 操作按钮 */
.app-table :deep(.ant-space) {
  display: flex;
  gap: 8px;
}

.app-table :deep(.ant-btn-primary) {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  border: none;
  border-radius: 10px;
  font-weight: 600;
  height: 28px;
  padding: 0 16px;
  font-size: 11px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.app-table :deep(.ant-btn-primary:hover) {
  background: linear-gradient(135deg, var(--color-primary-dark) 0%, #dc2626 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(249, 115, 22, 0.4);
}

.app-table :deep(.ant-btn-default) {
  border: 2px solid var(--color-border);
  border-radius: 10px;
  font-weight: 600;
  height: 28px;
  padding: 0 16px;
  font-size: 11px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: white;
  color: var(--color-text);
}

.app-table :deep(.ant-btn-default:hover) {
  border-color: var(--color-primary);
  color: var(--color-primary);
  transform: translateY(-2px);
  background: white;
}

.app-table :deep(.ant-btn-default.featured-btn) {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%) !important;
  border-color: #fde68a !important;
  color: #d97706 !important;
}

.app-table :deep(.ant-btn-default.featured-btn:hover) {
  background: linear-gradient(135deg, #fde68a 0%, #fcd34d 100%) !important;
  border-color: #fcd34d !important;
  color: #b45309 !important;
  transform: translateY(-2px);
}

.app-table :deep(.ant-btn-dangerous) {
  background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
  border: none;
  color: #dc2626;
  border-radius: 10px;
  font-weight: 600;
  height: 28px;
  padding: 0 16px;
  font-size: 11px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.app-table :deep(.ant-btn-dangerous:hover) {
  background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(220, 38, 38, 0.4);
}

/* 分页样式 */
.app-table :deep(.ant-pagination) {
  margin-top: 24px;
}

.app-table :deep(.ant-pagination-item-active) {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  border-color: transparent;
}

.app-table :deep(.ant-pagination-item-active a) {
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
</style>
