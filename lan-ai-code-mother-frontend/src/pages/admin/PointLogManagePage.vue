<template>
  <div class="point-log-content">
    <!-- 搜索表单 -->
    <a-form :model="searchParams" layout="inline" @finish="doSearch">
      <a-form-item label="用户ID">
        <a-input
          v-model:value="searchParams.userId"
          placeholder="输入用户ID"
          style="width: 200px"
        />
      </a-form-item>
      <a-form-item label="业务类型">
        <a-select
          v-model:value="searchParams.businessType"
          allow-clear
          placeholder="选择业务类型"
          style="width: 200px"
        >
          <a-select-option value="">全部</a-select-option>
          <a-select-option value="SIGN_IN">签到</a-select-option>
          <a-select-option value="REGISTER_REWARD">注册奖励</a-select-option>
          <a-select-option value="INVITEE_BONUS">被邀请人奖励</a-select-option>
          <a-select-option value="INVITER_BONUS">邀请人奖励</a-select-option>
          <a-select-option value="GENERATE">生成消耗</a-select-option>
          <a-select-option value="DOWNLOAD">下载代码</a-select-option>
          <a-select-option value="DEPLOY">部署消耗</a-select-option>
          <a-select-option value="SYSTEM_GRANT">系统发放</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="积分类型">
        <a-select
          v-model:value="searchParams.pointType"
          allow-clear
          placeholder="选择积分类型"
          style="width: 180px"
        >
          <a-select-option value="">全部</a-select-option>
          <a-select-option value="INCOME">收入</a-select-option>
          <a-select-option value="EXPENSE">支出</a-select-option>
        </a-select>
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
      :scroll="{ x: 1600 }"
      @change="doTableChange"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'userId'">
          <a-tag color="blue">{{ record.userId }}</a-tag>
        </template>
        <template v-else-if="column.dataIndex === 'businessTypeText'">
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
    width: 180,
  },
  {
    title: '积分类型',
    dataIndex: 'pointTypeText',
    width: 120,
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
    width: 200,
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

// 获取业务类型颜色
const getBusinessTypeColor = (type?: string) => {
  const colorMap: Record<string, string> = {
    SIGN_IN: 'blue',
    REGISTER_REWARD: 'green',
    INVITEE_BONUS: 'cyan',
    INVITER_BONUS: 'purple',
    GENERATE: 'orange',
    DOWNLOAD: 'geekblue',
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
/* 搜索表单样式优化 */
:deep(.ant-form) {
  margin-bottom: 28px;
}

:deep(.ant-form-item) {
  margin-bottom: 20px;
  margin-right: 24px;
}

:deep(.ant-form-item-label > label) {
  font-size: 14px;
  font-weight: 500;
  color: #1f2d3d;
}

:deep(.ant-divider) {
  margin: 28px 0;
  border-color: #f0f0f0;
}

/* 表格样式优化 */
:deep(.ant-table) {
  font-size: 15px;
}

:deep(.ant-table-thead > tr > th) {
  background: #fafafa;
  font-weight: 600;
  padding: 20px;
  border-bottom: 2px solid #e8e8e8;
  font-size: 15px;
}

:deep(.ant-table-tbody > tr > td) {
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
  font-size: 15px;
}

:deep(.ant-table-tbody > tr:hover > td) {
  background: #f5f7fa;
  transition: background-color 0.3s ease;
}

:deep(.ant-table-tbody > tr:last-child > td) {
  border-bottom: none;
}

/* 标签样式 */
:deep(.ant-tag) {
  font-size: 13px;
  padding: 4px 12px;
  border-radius: 4px;
  margin: 0;
}

/* 分页样式优化 */
:deep(.ant-pagination) {
  margin-top: 32px;
  text-align: right;
}

:deep(.ant-pagination-item) {
  height: 36px;
  line-height: 34px;
  border-radius: 6px;
}

:deep(.ant-pagination-item-active) {
  border-color: #1890ff;
}

:deep(.ant-pagination-options-quick-jumper input) {
  border-radius: 6px;
}

.point-income {
  color: #52c41a;
  font-weight: 600;
  font-size: 16px;
}

.point-expense {
  color: #ff4d4f;
  font-weight: 600;
  font-size: 16px;
}

/* 按钮组优化 */
:deep(.ant-form-item:last-child) {
  margin-right: 0;
}

:deep(.ant-btn) {
  height: 40px;
  padding: 0 20px;
  border-radius: 8px;
  font-weight: 500;
  font-size: 14px;
  transition: all 0.3s ease;
}

:deep(.ant-btn:hover) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.15);
}

/* 输入框和选择器样式 */
:deep(.ant-input),
:deep(.ant-select-selector) {
  border-radius: 8px;
  padding: 8px 12px;
  font-size: 14px;
}

:deep(.ant-select-selector) {
  height: 40px;
  display: flex;
  align-items: center;
}

:deep(.ant-input) {
  height: 40px;
}
</style>
