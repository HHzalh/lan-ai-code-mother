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
        <a-button style="margin-left: 8px" type="primary" @click="showGrantModal">
          <GiftOutlined />
          积分奖励
        </a-button>
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

    <!-- 发放积分弹窗 -->
    <a-modal
      v-model:open="grantModalVisible"
      :centered="true"
      :footer="null"
      class="grant-modal"
      title="积分奖励"
      width="560px"
      @cancel="resetGrantForm"
    >
      <div class="grant-modal-content">
        <div class="modal-header">
          <GiftOutlined class="modal-icon" />
          <span class="modal-title">为用户发放积分</span>
        </div>
        <a-form :label-col="{ span: 5 }" :model="grantForm" :wrapper-col="{ span: 19 }">
          <a-form-item
            :rules="[{ required: true, message: '请输入用户ID' }]"
            label="用户ID"
            name="userId"
          >
            <a-input
              v-model:value="grantForm.userId"
              class="form-input"
              placeholder="请输入用户ID"
              size="large"
              style="width: 100%"
            />
          </a-form-item>
          <a-form-item
            :rules="[
              { required: true, message: '请输入积分数' },
              { type: 'number', min: 1, message: '积分数必须大于0' },
            ]"
            label="积分数"
            name="points"
          >
            <a-input-number
              v-model:value="grantForm.points"
              :min="1"
              :precision="0"
              class="form-input"
              placeholder="请输入要发放的积分数"
              size="large"
              style="width: 100%"
            />
          </a-form-item>
          <a-form-item
            :rules="[{ required: true, message: '请输入备注' }]"
            label="备注"
            name="remark"
          >
            <a-textarea
              v-model:value="grantForm.remark"
              :rows="4"
              class="form-input"
              placeholder="请输入发放积分的备注说明"
            />
          </a-form-item>
        </a-form>
        <div class="modal-footer">
          <a-button class="cancel-btn" size="large" @click="resetGrantForm"> 取消</a-button>
          <a-button
            :loading="granting"
            class="confirm-btn"
            size="large"
            type="primary"
            @click="handleGrant"
          >
            <template #icon>
              <GiftOutlined />
            </template>
            确认发放
          </a-button>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { getLogs, grantPoints } from '@/api/pointController'
import { message } from 'ant-design-vue'
import { GiftOutlined } from '@ant-design/icons-vue'
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

// 发放积分相关
const grantModalVisible = ref(false)
const granting = ref(false)
const grantForm = reactive({
  userId: undefined as string | undefined,
  points: undefined as number | undefined,
  remark: '',
})

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

// 显示发放积分弹窗
const showGrantModal = () => {
  grantModalVisible.value = true
}

// 重置发放表单
const resetGrantForm = () => {
  grantForm.userId = undefined
  grantForm.points = undefined
  grantForm.remark = ''
  grantModalVisible.value = false
}

// 处理发放积分
const handleGrant = async () => {
  if (!grantForm.userId || !grantForm.points || !grantForm.remark) {
    message.warning('请填写完整信息')
    return
  }
  granting.value = true
  try {
    const res = await grantPoints({
      userId: grantForm.userId,
      points: grantForm.points,
      remark: grantForm.remark,
    })
    if (res.data.code === 0) {
      message.success('积分发放成功')
      resetGrantForm()
      fetchData()
    } else {
      message.error(res.data.message ?? '发放失败')
    }
  } catch (error) {
    message.error('发放失败，请重试')
  } finally {
    granting.value = false
  }
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

/* 弹窗样式 */
:deep(.grant-modal) {
  .ant-modal-header {
    border-bottom: none;
    padding-bottom: 0;
  }

  .ant-modal-close {
    top: 16px;
    right: 16px;
  }
}

.grant-modal-content {
  padding: 8px 0;

  .modal-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 24px;
    padding-bottom: 16px;
    border-bottom: 1px solid #f0f0f0;

    .modal-icon {
      font-size: 28px;
      color: #1890ff;
    }

    .modal-title {
      font-size: 20px;
      font-weight: 600;
      color: #1f2d3d;
    }
  }

  :deep(.ant-form) {
    .ant-form-item-label > label {
      font-size: 15px;
      font-weight: 500;
      color: #1f2d3d;
    }

    .ant-form-item {
      margin-bottom: 20px;
    }
  }

  .form-input {
    :deep(.ant-input),
    :deep(.ant-input-number),
    :deep(.ant-input-number-input),
    :deep(.ant-input-number-handler-wrap),
    :deep(textarea) {
      border-radius: 8px;
      border: 1px solid #d9d9d9;
      transition: all 0.3s;

      &:hover {
        border-color: #40a9ff;
      }

      &:focus,
      &.ant-input-focused {
        border-color: #1890ff;
        box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1);
      }
    }

    :deep(.ant-input-number) {
      width: 100%;

      .ant-input-number-handler-wrap {
        border-left: 1px solid #d9d9d9;

        .ant-input-number-handler {
          &:hover {
            color: #1890ff;
          }
        }
      }
    }
  }

  .modal-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    margin-top: 32px;
    padding-top: 24px;
    border-top: 1px solid #f0f0f0;

    .cancel-btn {
      border-radius: 8px;
      height: 42px;
      padding: 0 28px;
      font-size: 15px;
      font-weight: 500;
      color: #595959;
      border: 1px solid #d9d9d9;

      &:hover {
        color: #1890ff;
        border-color: #1890ff;
      }
    }

    .confirm-btn {
      border-radius: 8px;
      height: 42px;
      padding: 0 32px;
      font-size: 15px;
      font-weight: 600;
      background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
      border: none;
      box-shadow: 0 4px 12px rgba(24, 144, 255, 0.2);

      &:hover:not(:disabled) {
        background: linear-gradient(135deg, #40a9ff 0%, #1890ff 100%);
        transform: translateY(-1px);
        box-shadow: 0 6px 16px rgba(24, 144, 255, 0.3);
      }

      &:disabled {
        opacity: 0.6;
      }
    }
  }
}
</style>
