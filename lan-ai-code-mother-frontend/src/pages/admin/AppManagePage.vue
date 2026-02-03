<template>
  <div class="app-manage-wrapper admin-manage-wrapper">
    <!-- 动态渐变背景 -->
    <div class="gradient-bg admin-gradient-bg"></div>

    <!-- 主容器 -->
    <div class="manage-container admin-manage-container">
      <!-- 页面标题区 -->
      <div class="page-header admin-page-header">
        <h1 class="page-title admin-page-title">应用管理</h1>
        <p class="page-subtitle admin-page-subtitle">管理平台所有应用</p>
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

        <!-- 应用列表卡片 -->
        <div class="filter-grid admin-filter-grid">
          <div class="filter-item admin-filter-item">
            <label class="filter-label admin-filter-label">应用名称</label>
            <input
              v-model="searchParams.appName"
              class="filter-input admin-filter-input"
              placeholder="请输入应用名称"
            />
          </div>

          <div class="filter-item admin-filter-item">
            <label class="filter-label admin-filter-label">创建者ID</label>
            <input
              v-model="searchParams.userId"
              class="filter-input admin-filter-input"
              placeholder="请输入用户ID"
            />
          </div>

          <div class="filter-item admin-filter-item">
            <label class="filter-label admin-filter-label">生成类型</label>
            <select v-model="searchParams.codeGenType" class="filter-select admin-filter-select">
              <option value="">全部</option>
              <option
                v-for="option in CODE_GEN_TYPE_OPTIONS"
                :key="option.value"
                :value="option.value"
              >
                {{ option.label }}
              </option>
            </select>
          </div>
        </div>
      </div>
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
              <rect height="18" rx="2" ry="2" width="18" x="3" y="3"></rect>
              <line x1="3" x2="21" y1="9" y2="9"></line>
              <line x1="9" x2="9" y1="21" y2="9"></line>
            </svg>
            <span>应用列表</span>
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
          class="app-table admin-data-table"
          @change="doTableChange"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.dataIndex === 'id'">
              <span class="id-badge admin-id-badge">{{ record.id }}</span>
            </template>

            <template v-else-if="column.dataIndex === 'appName'">
              <span class="app-name">{{ truncateText(record.appName, 15) }}</span>
            </template>

            <template v-else-if="column.dataIndex === 'cover'">
              <a-image
                v-if="record.cover"
                :height="40"
                :src="record.cover"
                :width="60"
                class="app-cover"
              />
              <div v-else class="no-cover">无封面</div>
            </template>

            <template v-else-if="column.dataIndex === 'initPrompt'">
              <span class="prompt-text admin-message-text">{{
                truncateText(record.initPrompt, 20)
              }}</span>
            </template>

            <template v-else-if="column.dataIndex === 'codeGenType'">
              <span :class="getTypeClass(record.codeGenType)" class="type-badge admin-type-badge">
                {{ formatCodeGenType(record.codeGenType) }}
              </span>
            </template>

            <template v-else-if="column.dataIndex === 'priority'">
              <span v-if="record.priority === 99" class="featured-badge admin-featured-badge"
                >精选</span
              >
              <span
                v-else
                class="priority-badge admin-type-badge"
                style="background: rgba(255, 107, 107, 0.1); color: var(--admin-color-primary)"
                >{{ record.priority || 0 }}</span
              >
            </template>

            <template v-else-if="column.dataIndex === 'deployedTime'">
              <span v-if="record.deployedTime" class="time-badge admin-time-badge">
                {{ formatTime(record.deployedTime) }}
              </span>
              <span v-else class="no-deploy-badge admin-no-deploy-badge">未部署</span>
            </template>

            <template v-else-if="column.dataIndex === 'user'">
              <span v-if="record.user" class="user-name">
                {{ truncateText(record.user.userName || record.user.userAccount || '未知', 12) }}
              </span>
              <span v-else>-</span>
            </template>

            <template v-else-if="column.dataIndex === 'createTime'">
              <span class="time-text admin-time-text">{{ formatTime(record.createTime) }}</span>
            </template>

            <template v-else-if="column.key === 'action'">
              <div class="action-buttons admin-action-buttons">
                <button
                  class="action-btn-sm primary admin-action-btn-sm primary"
                  @click="editApp(record)"
                >
                  <svg
                    fill="none"
                    height="14"
                    stroke="currentColor"
                    stroke-width="2"
                    viewBox="0 0 24 24"
                    width="14"
                  >
                    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                  </svg>
                  编辑
                </button>
                <button
                  :class="{ 'featured-btn': record.priority === 99 }"
                  class="action-btn-sm secondary admin-action-btn-sm secondary"
                  @click="toggleFeatured(record)"
                >
                  <svg
                    fill="none"
                    height="14"
                    stroke="currentColor"
                    stroke-width="2"
                    viewBox="0 0 24 24"
                    width="14"
                  >
                    <polygon
                      points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"
                    ></polygon>
                  </svg>
                  {{ record.priority === 99 ? '取消' : '精选' }}
                </button>
                <button
                  class="action-btn-sm danger admin-action-btn-sm danger"
                  @click="deleteApp(record.id)"
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
import { deleteAppByAdmin, listAppVoByPageByAdmin, updateAppByAdmin } from '@/api/appController'
import { CODE_GEN_TYPE_OPTIONS, CodeGenTypeEnum, formatCodeGenType } from '@/utils/codeGenTypes'
import { formatTime } from '@/utils/time'

const router = useRouter()

const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    width: 80,
    fixed: 'left' as const,
  },
  {
    title: '应用名称',
    dataIndex: 'appName',
    width: 150,
  },
  {
    title: '封面',
    dataIndex: 'cover',
    width: 80,
  },
  {
    title: '初始提示词',
    dataIndex: 'initPrompt',
    width: 100,
  },
  {
    title: '生成类型',
    dataIndex: 'codeGenType',
    width: 140,
  },
  {
    title: '优先级',
    dataIndex: 'priority',
    width: 80,
  },
  {
    title: '部署时间',
    dataIndex: 'deployedTime',
    width: 140,
  },
  {
    title: '创建者',
    dataIndex: 'user',
    width: 120,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 100,
  },
  {
    title: '操作',
    key: 'action',
    width: 270,
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

// 获取类型对应的 class
const getTypeClass = (type: string | undefined) => {
  if (!type) return ''
  const typeMap = {
    [CodeGenTypeEnum.HTML]: 'html',
    [CodeGenTypeEnum.MULTI_FILE]: 'multi-file',
    [CodeGenTypeEnum.VUE_PROJECT]: 'vue-project',
  }
  return typeMap[type as CodeGenTypeEnum] || ''
}

// 截取文本
const truncateText = (text: string, maxLength: number) => {
  if (!text) return ''
  if (text.length <= maxLength) return text
  return text.substring(0, maxLength) + '...'
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
      message.success(newPriority === 99 ? '已设为精选' : '已取消精选')
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

  Modal.confirm({
    title: '确认删除',
    content: '确定要删除这个应用吗？',
    okText: '确定',
    cancelText: '取消',
    okButtonProps: { danger: true },
    onOk: async () => {
      try {
        const res = await deleteAppByAdmin({ id })
        if (res.data.code === 0) {
          message.success('删除成功')
          fetchData()
        } else {
          message.error('删除失败：' + res.data.message)
        }
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

/* ========== 页面特定样式 ========== */
.app-name {
  font-weight: 600;
  color: var(--admin-text-primary);
}

.app-cover {
  border-radius: 8px;
  border: 1.5px solid rgba(255, 107, 107, 0.2);
}

.no-cover {
  padding: 8px 12px;
  background: rgba(255, 107, 107, 0.05);
  color: var(--admin-text-secondary);
  border-radius: 8px;
  font-size: 12px;
  text-align: center;
}

.prompt-text {
  color: var(--admin-text-secondary);
  font-size: 13px;
}

.user-name {
  font-weight: 500;
  color: var(--admin-text-primary);
}
</style>
