<template>
  <div class="point-logs-wrapper">
    <section class="logs-hero">
      <div class="hero-content">
        <p class="eyebrow">POINT LOGS</p>
        <h2>积分流水</h2>
        <p class="subtitle">查看您的积分变动记录</p>
      </div>
    </section>

    <section class="logs-card">
      <!-- 搜索表单 -->
      <a-form :model="searchParams" class="search-form" layout="inline" @finish="doSearch">
        <a-form-item label="业务类型">
          <a-select
            v-model:value="searchParams.businessType"
            allow-clear
            placeholder="选择业务类型"
            style="width: 150px"
          >
            <a-select-option value="">全部</a-select-option>
            <a-select-option value="SIGN_IN">签到</a-select-option>
            <a-select-option value="REGISTER_REWARD">注册奖励</a-select-option>
            <a-select-option value="INVITEE_BONUS">被邀请人奖励</a-select-option>
            <a-select-option value="INVITER_BONUS">邀请人奖励</a-select-option>
            <a-select-option value="GENERATE">生成消耗</a-select-option>
            <a-select-option value="DEPLOY">部署消耗</a-select-option>
            <a-select-option value="SYSTEM_GRANT">系统发放</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="积分类型">
          <a-select
            v-model:value="searchParams.pointType"
            allow-clear
            placeholder="选择积分类型"
            style="width: 150px"
          >
            <a-select-option value="">全部</a-select-option>
            <a-select-option value="INCOME">收入</a-select-option>
            <a-select-option value="EXPENSE">支出</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="开始时间">
          <a-date-picker
            v-model:value="startDate"
            format="YYYY-MM-DD"
            placeholder="选择开始时间"
            style="width: 150px"
          />
        </a-form-item>
        <a-form-item label="结束时间">
          <a-date-picker
            v-model:value="endDate"
            format="YYYY-MM-DD"
            placeholder="选择结束时间"
            style="width: 150px"
          />
        </a-form-item>
        <a-form-item>
          <a-button html-type="submit" type="primary">搜索</a-button>
          <a-button style="margin-left: 8px" @click="resetSearch">重置</a-button>
        </a-form-item>
      </a-form>

      <a-divider />

      <!-- 表格 -->
      <a-table
        :columns="columns"
        :data-source="data"
        :pagination="pagination"
        :scroll="{ x: 1000 }"
        @change="doTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'businessTypeText'">
            <a-tag :color="getBusinessTypeColor(record.businessType)">
              {{ record.businessTypeText }}
            </a-tag>
          </template>
          <template v-else-if="column.dataIndex === 'pointTypeText'">
            <a-tag :color="record.pointType === 'INCOME' ? 'green' : 'red'">
              {{ record.pointTypeText }}
            </a-tag>
          </template>
          <template v-else-if="column.dataIndex === 'pointChange'">
            <span :class="record.pointType === 'INCOME' ? 'point-income' : 'point-expense'">
              {{ record.pointType === 'INCOME' ? '+' : '-' }}{{ record.pointChange }}
            </span>
          </template>
          <template v-else-if="column.dataIndex === 'createTime'">
            {{ formatTime(record.createTime) }}
          </template>
        </template>
      </a-table>
    </section>
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { getMyLogs } from '@/api/pointController'
import { message } from 'ant-design-vue'
import { type Dayjs } from 'dayjs'
import { formatTime } from '@/utils/time'

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
    width: 120,
  },
  {
    title: '变动前',
    dataIndex: 'beforePoints',
    width: 100,
  },
  {
    title: '变动后',
    dataIndex: 'afterPoints',
    width: 100,
  },
  {
    title: '备注',
    dataIndex: 'remark',
    ellipsis: true,
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

// 搜索条件
const searchParams = reactive<API.PointLogQueryRequest>({
  pageNum: 1,
  pageSize: 10,
})

// 获取数据
const fetchData = async () => {
  try {
    const params: API.PointLogQueryRequest = {
      ...searchParams,
    }
    if (startDate.value) {
      params.startTime = startDate.value.format('YYYY-MM-DD')
    }
    if (endDate.value) {
      params.endTime = endDate.value.format('YYYY-MM-DD')
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
    SIGN_IN: 'blue',
    REGISTER_REWARD: 'green',
    INVITEE_BONUS: 'cyan',
    INVITER_BONUS: 'purple',
    GENERATE: 'orange',
    DEPLOY: 'red',
    SYSTEM_GRANT: 'magenta',
  }
  return colorMap[type ?? ''] || 'default'
}

// 页面加载时请求一次
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.point-logs-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px 0 64px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.logs-hero {
  background: linear-gradient(120deg, #e0f2ff, #f5f7ff);
  border-radius: 18px;
  padding: 28px 32px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(24, 144, 255, 0.15);
}

.eyebrow {
  letter-spacing: 0.4em;
  font-size: 12px;
  color: #3c92ff;
  margin-bottom: 8px;
}

.logs-hero h2 {
  margin: 0;
  font-size: 28px;
  color: #1f2d3d;
}

.subtitle {
  margin-top: 8px;
  color: #5f6b7c;
}

.logs-card {
  background: #fff;
  border-radius: 18px;
  padding: 32px 40px 40px;
  box-shadow: 0 12px 35px rgba(15, 39, 80, 0.07);
  border: 1px solid #f0f2f5;
}

.search-form {
  margin-bottom: 24px;
}

/* 搜索表单样式优化 */
:deep(.ant-form-item) {
  margin-bottom: 16px;
  margin-right: 16px;
}

:deep(.ant-divider) {
  margin: 24px 0;
}

/* 表格样式优化 */
:deep(.ant-table) {
  font-size: 14px;
}

:deep(.ant-table-thead > tr > th) {
  background: #fafafa;
  font-weight: 600;
  padding: 16px;
  border-bottom: 2px solid #f0f0f0;
}

:deep(.ant-table-tbody > tr > td) {
  padding: 16px;
  border-bottom: 1px solid #f5f5f5;
}

:deep(.ant-table-tbody > tr:hover > td) {
  background: #fafafa;
}

:deep(.ant-table-tbody > tr:last-child > td) {
  border-bottom: none;
}

/* 分页样式优化 */
:deep(.ant-pagination) {
  margin-top: 24px;
  text-align: right;
}

.point-income {
  color: #52c41a;
  font-weight: 600;
  font-size: 15px;
}

.point-expense {
  color: #ff4d4f;
  font-weight: 600;
  font-size: 15px;
}

/* 按钮样式 */
:deep(.ant-btn) {
  height: 36px;
  padding: 0 16px;
  border-radius: 6px;
  font-weight: 500;
}

@media (max-width: 768px) {
  .logs-card {
    padding: 24px;
  }

  .search-form {
    flex-direction: column;
  }

  :deep(.ant-form-item) {
    margin-right: 0;
    margin-bottom: 12px;
  }

  :deep(.ant-table-thead > tr > th),
  :deep(.ant-table-tbody > tr > td) {
    padding: 12px 8px;
    font-size: 13px;
  }
}
</style>
