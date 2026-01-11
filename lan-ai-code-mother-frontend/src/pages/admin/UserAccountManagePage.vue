<template>
  <div class="user-account-content">
    <!-- 搜索表单 -->
    <a-form :model="searchParams" class="search-form" layout="inline" @finish="doSearch">
      <a-form-item label="用户ID">
        <a-input
          v-model:value="searchParams.userId"
          placeholder="输入用户ID"
          style="width: 200px"
        />
      </a-form-item>
      <a-form-item label="邀请码">
        <a-input
          v-model:value="searchParams.invitationCode"
          placeholder="输入邀请码"
          style="width: 150px"
        />
      </a-form-item>
      <a-form-item label="可用积分范围">
        <a-input-number
          v-model:value="searchParams.minAvailablePoints"
          :min="0"
          placeholder="最小"
          style="width: 100px"
        />
        <span style="margin: 0 8px">-</span>
        <a-input-number
          v-model:value="searchParams.maxAvailablePoints"
          :min="0"
          placeholder="最大"
          style="width: 100px"
        />
      </a-form-item>
      <a-form-item label="累计积分范围">
        <a-input-number
          v-model:value="searchParams.minTotalPoints"
          :min="0"
          placeholder="最小"
          style="width: 100px"
        />
        <span style="margin: 0 8px">-</span>
        <a-input-number
          v-model:value="searchParams.maxTotalPoints"
          :min="0"
          placeholder="最大"
          style="width: 100px"
        />
      </a-form-item>
      <a-form-item>
        <a-button html-type="submit" type="primary">
          <SearchOutlined />
          搜索
        </a-button>
        <a-button style="margin-left: 10px" @click="resetSearch">
          <ReloadOutlined />
          重置
        </a-button>
        <a-button danger style="margin-left: 10px" type="primary" @click="showGrantAllModal">
          <GiftOutlined />
          批量奖励
        </a-button>
      </a-form-item>
    </a-form>

    <!-- 统计卡片 -->
    <a-row :gutter="16" class="stats-cards">
      <a-col :span="6">
        <a-card>
          <a-statistic :value="totalUsers" :value-style="{ color: '#3f8600' }" title="总用户数">
            <template #prefix>
              <UserOutlined />
            </template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card>
          <a-statistic
            :value="totalPoints"
            :value-style="{ color: '#1890ff' }"
            title="累计获得积分"
          >
            <template #prefix>
              <GiftOutlined />
            </template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card>
          <a-statistic
            :value="totalConsume"
            :value-style="{ color: '#cf1322' }"
            title="累计消耗积分"
          >
            <template #prefix>
              <ShoppingCartOutlined />
            </template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card>
          <a-statistic :value="totalInvites" :value-style="{ color: '#722ed1' }" title="总邀请人数">
            <template #prefix>
              <TeamOutlined />
            </template>
          </a-statistic>
        </a-card>
      </a-col>
    </a-row>

    <!-- 数据表格 -->
    <a-card class="table-card">
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
          <template v-if="column.dataIndex === 'invitationCode'">
            <a-tag color="purple">{{ record.invitationCode }}</a-tag>
          </template>
          <template v-if="column.dataIndex === 'availablePoints'">
            <a-tag color="green">{{ record.availablePoints }}</a-tag>
          </template>
          <template v-if="column.dataIndex === 'totalPoints'">
            <a-tag color="blue">{{ record.totalPoints }}</a-tag>
          </template>
          <template v-if="column.dataIndex === 'freezePoints'">
            <a-tag color="orange">{{ record.freezePoints || 0 }}</a-tag>
          </template>
          <template v-if="column.dataIndex === 'totalConsume'">
            <a-tag color="red">{{ record.totalConsume }}</a-tag>
          </template>
          <template v-if="column.dataIndex === 'continuousDays'">
            <a-tag
              :color="
                record.continuousDays >= 7
                  ? 'gold'
                  : record.continuousDays >= 3
                    ? 'cyan'
                    : 'default'
              "
            >
              {{ record.continuousDays }} 天
            </a-tag>
          </template>
          <template v-if="column.dataIndex === 'invitationCount'">
            <a-tag color="purple">{{ record.invitationCount }} 人</a-tag>
          </template>
          <template v-if="column.dataIndex === 'totalInvitePoints'">
            <a-tag color="magenta">{{ record.totalInvitePoints }}</a-tag>
          </template>
          <template v-if="column.dataIndex === 'lastSignDate'">
            <span>{{ record.lastSignDate || '未签到' }}</span>
          </template>
          <template v-if="column.key === 'action'">
            <a-button size="small" type="primary" @click="showGrantModal(record)">
              <GiftOutlined />
              发放积分
            </a-button>
          </template>
        </template>
      </a-table>
    </a-card>

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
          <a-form-item label="用户ID">
            <a-input v-model:value="grantForm.userId" class="form-input" disabled size="large" />
          </a-form-item>
          <a-form-item
            :rules="[
              { required: true, message: '请输入积分数' },
              { pattern: /^[1-9]\d*$/, message: '积分数必须为大于0的整数' },
            ]"
            label="积分数"
            name="points"
          >
            <a-input
              v-model:value="grantForm.points"
              class="form-input"
              placeholder="请输入要发放的积分数"
              size="large"
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

    <!-- 批量发放积分弹窗 -->
    <a-modal
      v-model:open="grantAllModalVisible"
      :centered="true"
      :footer="null"
      class="grant-modal"
      title="批量积分奖励"
      width="560px"
      @cancel="resetGrantAllForm"
    >
      <div class="grant-modal-content">
        <div class="modal-header">
          <GiftOutlined class="modal-icon" />
          <span class="modal-title">批量发放积分</span>
        </div>
        <a-alert
          class="warning-alert"
          message="此操作将给所有用户发放积分，请谨慎操作！"
          show-icon
          type="warning"
        >
          <template #icon>
            <ExclamationCircleOutlined />
          </template>
        </a-alert>
        <a-form :label-col="{ span: 5 }" :model="grantAllForm" :wrapper-col="{ span: 19 }">
          <a-form-item
            :rules="[
              { required: true, message: '请输入积分数' },
              { pattern: /^[1-9]\d*$/, message: '积分数必须为大于0的整数' },
            ]"
            label="积分数"
            name="points"
          >
            <a-input
              v-model:value="grantAllForm.points"
              class="form-input"
              placeholder="请输入要发放的积分数"
              size="large"
            />
          </a-form-item>
          <a-form-item
            :rules="[{ required: true, message: '请输入备注' }]"
            label="备注"
            name="remark"
          >
            <a-textarea
              v-model:value="grantAllForm.remark"
              :rows="4"
              class="form-input"
              placeholder="请输入发放积分的备注说明"
            />
          </a-form-item>
        </a-form>
        <div class="modal-footer">
          <a-button class="cancel-btn" size="large" @click="resetGrantAllForm"> 取消</a-button>
          <a-button
            :loading="granting"
            class="confirm-btn"
            size="large"
            type="primary"
            @click="handleGrantAll"
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
import { computed, onMounted, ref } from 'vue'
import { message } from 'ant-design-vue'
import {
  ExclamationCircleOutlined,
  GiftOutlined,
  ReloadOutlined,
  SearchOutlined,
  ShoppingCartOutlined,
  TeamOutlined,
  UserOutlined,
} from '@ant-design/icons-vue'
import { grantPoints, grantPointsToAll, listAccounts } from '@/api/pointController'

// 搜索参数
const searchParams = ref<API.UserAccountQueryRequest>({
  pageNum: 1,
  pageSize: 10,
})

// 数据列表
const data = ref<API.UserAccountVO[]>([])

// 表格列配置
const columns = [
  { title: '用户ID', dataIndex: 'userId', width: 160, fixed: 'left' },
  { title: '邀请码', dataIndex: 'invitationCode', width: 120 },
  { title: '可用积分', dataIndex: 'availablePoints', width: 120 },
  { title: '累计积分', dataIndex: 'totalPoints', width: 120 },
  { title: '冻结积分', dataIndex: 'freezePoints', width: 120 },
  { title: '累计消耗', dataIndex: 'totalConsume', width: 120 },
  { title: '连续签到天数', dataIndex: 'continuousDays', width: 140 },
  { title: '邀请人数', dataIndex: 'invitationCount', width: 120 },
  { title: '邀请奖励积分', dataIndex: 'totalInvitePoints', width: 140 },
  { title: '最后签到日期', dataIndex: 'lastSignDate', width: 150 },
  { title: '操作', key: 'action', width: 120, fixed: 'right' },
]

// 分页配置
const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条`,
})

// 统计数据
const totalUsers = computed(() => pagination.value.total)
const totalPoints = computed(() =>
  data.value.reduce((sum, item) => sum + Number(item.totalPoints || 0), 0),
)
const totalConsume = computed(() =>
  data.value.reduce((sum, item) => sum + Number(item.totalConsume || 0), 0),
)
const totalInvites = computed(() =>
  data.value.reduce((sum, item) => sum + Number(item.invitationCount || 0), 0),
)

// 获取数据
const fetchData = async () => {
  try {
    const res = await listAccounts(searchParams.value)
    if (res.data.code === 0 && res.data.data) {
      data.value = res.data.data.records || []
      pagination.value.current = res.data.data.pageNumber || 1
      pagination.value.pageSize = res.data.data.pageSize || 10
      pagination.value.total = res.data.data.totalRow || 0
    }
  } catch (error) {
    message.error('获取数据失败')
  }
}

// 搜索
const doSearch = () => {
  searchParams.value.pageNum = 1
  pagination.value.current = 1
  fetchData()
}

// 重置搜索
const resetSearch = () => {
  searchParams.value = {
    pageNum: 1,
    pageSize: 10,
  }
  fetchData()
}

// 表格变化
const doTableChange = (pag: any) => {
  searchParams.value.pageNum = pag.current
  searchParams.value.pageSize = pag.pageSize
  pagination.value.current = pag.current
  pagination.value.pageSize = pag.pageSize
  fetchData()
}

// 发放积分弹窗相关
const grantModalVisible = ref(false)
const granting = ref(false)
const grantForm = ref({
  userId: '',
  points: '',
  remark: '',
})

// 显示发放积分弹窗
const showGrantModal = (record: API.UserAccountVO) => {
  grantForm.value = {
    userId: String(record.userId),
    points: '',
    remark: '',
  }
  grantModalVisible.value = true
}

// 重置发放积分表单
const resetGrantForm = () => {
  grantForm.value = {
    userId: '',
    points: undefined,
    remark: '',
  }
  grantModalVisible.value = false
}

// 处理发放积分
const handleGrant = async () => {
  if (!grantForm.value.userId || !grantForm.value.points || !grantForm.value.remark) {
    message.error('请填写完整信息')
    return
  }

  granting.value = true
  try {
    const res = await grantPoints({
      userId: grantForm.value.userId as unknown as number,
      points: grantForm.value.points,
      remark: grantForm.value.remark,
    })
    if (res.data.code === 0) {
      message.success('积分发放成功')
      resetGrantForm()
      fetchData() // 刷新数据
    } else {
      message.error(res.data.message || '发放失败')
    }
  } catch (error) {
    message.error('发放失败，请重试')
  } finally {
    granting.value = false
  }
}

// 批量发放积分弹窗相关
const grantAllModalVisible = ref(false)
const grantAllForm = ref({
  points: '',
  remark: '',
})

// 显示批量发放积分弹窗
const showGrantAllModal = () => {
  grantAllForm.value = {
    points: '',
    remark: '',
  }
  grantAllModalVisible.value = true
}

// 重置批量发放积分表单
const resetGrantAllForm = () => {
  grantAllForm.value = {
    points: undefined,
    remark: '',
  }
  grantAllModalVisible.value = false
}

// 处理批量发放积分
const handleGrantAll = async () => {
  if (!grantAllForm.value.points || !grantAllForm.value.remark) {
    message.error('请填写完整信息')
    return
  }

  granting.value = true
  try {
    const res = await grantPointsToAll({
      points: grantAllForm.value.points,
      remark: grantAllForm.value.remark,
    })
    if (res.data.code === 0) {
      message.success(`成功给 ${res.data.data} 个用户发放积分`)
      resetGrantAllForm()
      fetchData() // 刷新数据
    } else {
      message.error(res.data.message || '发放失败')
    }
  } catch (error) {
    message.error('发放失败，请重试')
  } finally {
    granting.value = false
  }
}

// 页面加载
onMounted(() => {
  fetchData()
})
</script>

<style lang="less" scoped>
.user-account-content {
  .search-form {
    margin-bottom: 24px;
    padding: 20px;
    background: #f9f9f9;
    border-radius: 8px;

    :deep(.ant-form-item) {
      margin-bottom: 16px;
      margin-right: 24px;
    }

    :deep(.ant-btn) {
      margin-right: 8px;
    }
  }

  .stats-cards {
    margin-bottom: 24px;

    :deep(.ant-card) {
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      transition: all 0.3s;

      &:hover {
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        transform: translateY(-2px);
      }
    }

    :deep(.ant-statistic) {
      text-align: center;

      .ant-statistic-title {
        font-size: 16px;
        color: #8c8c8c;
        margin-bottom: 8px;
      }

      .ant-statistic-content {
        font-size: 28px;
        font-weight: 600;
      }
    }
  }

  .table-card {
    border-radius: 8px;

    :deep(.ant-table) {
      .ant-table-thead > tr > th {
        background: #fafafa;
        font-weight: 600;
        font-size: 14px;
        padding: 16px;
      }

      .ant-table-tbody > tr > td {
        padding: 16px;
        font-size: 14px;
      }

      .ant-table-tbody > tr:hover > td {
        background: #f0f7ff;
      }
    }

    :deep(.ant-tag) {
      font-size: 13px;
      padding: 4px 8px;
      border-radius: 4px;
    }
  }
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

  .warning-alert {
    margin-bottom: 24px;
    padding: 12px 16px;
    background: #fffbe6;
    border: 1px solid #ffe58f;
    border-radius: 8px;

    :deep(.ant-alert-icon) {
      color: #fa8c16;
    }

    :deep(.ant-alert-message) {
      color: #d48806;
      font-size: 14px;
      font-weight: 500;
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
