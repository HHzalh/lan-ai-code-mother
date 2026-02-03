<template>
  <div class="user-account-wrapper admin-manage-wrapper">
    <!-- 动态渐变背景 -->
    <div class="gradient-bg admin-gradient-bg"></div>

    <!-- 主容器 -->
    <div class="manage-container admin-manage-container">
      <!-- 页面标题区 -->
      <div class="page-header admin-page-header">
        <h1 class="page-title admin-page-title">积分账户管理</h1>
        <p class="page-subtitle admin-page-subtitle">管理用户积分账户与发放奖励</p>
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
              <polygon points="22 3 2 3 10 12.46 10 19 14 17 14 12.46 22 3"></polygon>
            </svg>
            <span>筛选</span>
          </div>
          <div class="filter-actions">
            <button class="action-btn secondary admin-action-btn secondary" @click="resetSearch">
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
                <path
                  d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"
                ></path>
              </svg>
              重置
            </button>
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
            <button
              class="action-btn-sm danger admin-action-btn-sm danger"
              @click="showGrantAllModal"
            >
              <svg
                fill="none"
                height="14"
                stroke="currentColor"
                stroke-width="2"
                viewBox="0 0 24 24"
                width="14"
              >
                <path d="M20 12v6a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2v-6"></path>
                <rect height="4" width="16" x="4" y="4"></rect>
              </svg>
              批量奖励
            </button>
          </div>
        </div>

        <div class="filter-grid admin-filter-grid">
          <div class="filter-item admin-filter-item">
            <label class="filter-label admin-filter-label">用户ID</label>
            <input
              v-model="searchParams.userId"
              class="filter-input admin-filter-input"
              placeholder="请输入用户ID"
            />
          </div>

          <div class="filter-item admin-filter-item">
            <label class="filter-label admin-filter-label">邀请码</label>
            <input
              v-model="searchParams.invitationCode"
              class="filter-input admin-filter-input"
              placeholder="请输入邀请码"
            />
          </div>

          <div class="filter-item admin-filter-item">
            <label class="filter-label admin-filter-label">可用积分范围</label>
            <div class="range-inputs">
              <input
                v-model="searchParams.minAvailablePoints"
                class="filter-input admin-filter-input"
                placeholder="最小"
                type="number"
              />
              <span class="range-separator">-</span>
              <input
                v-model="searchParams.maxAvailablePoints"
                class="filter-input admin-filter-input"
                placeholder="最大"
                type="number"
              />
            </div>
          </div>

          <div class="filter-item admin-filter-item">
            <label class="filter-label admin-filter-label">累计积分范围</label>
            <div class="range-inputs">
              <input
                v-model="searchParams.minTotalPoints"
                class="filter-input admin-filter-input"
                placeholder="最小"
                type="number"
              />
              <span class="range-separator">-</span>
              <input
                v-model="searchParams.maxTotalPoints"
                class="filter-input admin-filter-input"
                placeholder="最大"
                type="number"
              />
            </div>
          </div>
        </div>
      </div>

      <!-- 统计卡片 -->
      <div class="stats-cards-container">
        <div class="stats-card admin-glass-card">
          <div
            class="stat-icon-wrapper"
            style="background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%)"
          >
            <svg
              fill="none"
              height="24"
              stroke="currentColor"
              stroke-width="2"
              viewBox="0 0 24 24"
              width="24"
            >
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
              <circle cx="12" cy="7" r="4"></circle>
            </svg>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ totalUsers }}</div>
            <div class="stat-label">总用户数</div>
          </div>
        </div>

        <div class="stats-card admin-glass-card">
          <div
            class="stat-icon-wrapper"
            style="background: linear-gradient(135deg, #fbbf24 0%, #f97316 100%)"
          >
            <svg
              fill="none"
              height="24"
              stroke="currentColor"
              stroke-width="2"
              viewBox="0 0 24 24"
              width="24"
            >
              <path d="M20 12v6a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2v-6"></path>
              <rect height="4" width="16" x="4" y="4"></rect>
            </svg>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ totalPoints }}</div>
            <div class="stat-label">累计获得积分</div>
          </div>
        </div>

        <div class="stats-card admin-glass-card">
          <div
            class="stat-icon-wrapper"
            style="background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%)"
          >
            <svg
              fill="none"
              height="24"
              stroke="currentColor"
              stroke-width="2"
              viewBox="0 0 24 24"
              width="24"
            >
              <circle cx="9" cy="21" r="1"></circle>
              <circle cx="20" cy="21" r="1"></circle>
              <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"></path>
            </svg>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ totalConsume }}</div>
            <div class="stat-label">累计消耗积分</div>
          </div>
        </div>

        <div class="stats-card admin-glass-card">
          <div
            class="stat-icon-wrapper"
            style="background: linear-gradient(135deg, #a78bfa 0%, #8b5cf6 100%)"
          >
            <svg
              fill="none"
              height="24"
              stroke="currentColor"
              stroke-width="2"
              viewBox="0 0 24 24"
              width="24"
            >
              <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
              <circle cx="9" cy="7" r="4"></circle>
              <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
              <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
            </svg>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ totalInvites }}</div>
            <div class="stat-label">总邀请人数</div>
          </div>
        </div>
      </div>

      <!-- 账户列表卡片 -->
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
            <span>账户列表</span>
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
          class="account-table admin-data-table"
          @change="doTableChange"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.dataIndex === 'userId'">
              <span class="id-badge admin-id-badge">{{ record.userId }}</span>
            </template>

            <template v-else-if="column.dataIndex === 'invitationCode'">
              <span
                class="code-badge admin-type-badge"
                style="
                  background: linear-gradient(135deg, #f3e8ff 0%, #e9d5ff 100%);
                  color: #9333ea;
                "
                >{{ record.invitationCode }}</span
              >
            </template>

            <template v-else-if="column.dataIndex === 'availablePoints'">
              <span
                class="points-badge admin-type-badge"
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
                  <line x1="12" x2="12" y1="1" y2="23"></line>
                  <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"></path>
                </svg>
                {{ record.availablePoints }}
              </span>
            </template>

            <template v-else-if="column.dataIndex === 'totalPoints'">
              <span
                class="points-badge admin-type-badge"
                style="
                  background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
                  color: #2563eb;
                "
                >{{ record.totalPoints }}</span
              >
            </template>

            <template v-else-if="column.dataIndex === 'freezePoints'">
              <span
                class="freeze-badge admin-type-badge"
                style="
                  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
                  color: #d97706;
                "
                >{{ record.freezePoints || 0 }}</span
              >
            </template>

            <template v-else-if="column.dataIndex === 'totalConsume'">
              <span
                class="consume-badge admin-type-badge"
                style="
                  background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
                  color: #dc2626;
                "
                >{{ record.totalConsume }}</span
              >
            </template>

            <template v-else-if="column.dataIndex === 'continuousDays'">
              <span
                :class="{
                  'days-gold': record.continuousDays >= 7,
                  'days-cyan': record.continuousDays >= 3 && record.continuousDays < 7,
                  'days-default': record.continuousDays < 3,
                }"
                class="days-badge admin-type-badge"
              >
                <svg
                  fill="none"
                  height="12"
                  stroke="currentColor"
                  stroke-width="2"
                  viewBox="0 0 24 24"
                  width="12"
                >
                  <rect height="18" rx="2" ry="2" width="18" x="3" y="4"></rect>
                  <line x1="16" x2="16" y1="2" y2="6"></line>
                  <line x1="8" x2="8" y1="2" y2="6"></line>
                  <line x1="3" x2="21" y1="10" y2="10"></line>
                </svg>
                {{ record.continuousDays }} 天
              </span>
            </template>

            <template v-else-if="column.dataIndex === 'invitationCount'">
              <span
                class="invite-badge admin-type-badge"
                style="
                  background: linear-gradient(135deg, #f3e8ff 0%, #e9d5ff 100%);
                  color: #9333ea;
                "
                >{{ record.invitationCount }} 人</span
              >
            </template>

            <template v-else-if="column.dataIndex === 'totalInvitePoints'">
              <span
                class="invite-points-badge admin-type-badge"
                style="
                  background: linear-gradient(135deg, #ffedd5 0%, #fed7aa 100%);
                  color: #ea580c;
                "
                >{{ record.totalInvitePoints }}</span
              >
            </template>

            <template v-else-if="column.dataIndex === 'lastSignDate'">
              <span class="time-text admin-time-text">{{ record.lastSignDate || '未签到' }}</span>
            </template>

            <template v-else-if="column.key === 'action'">
              <div class="action-buttons admin-action-buttons">
                <button
                  class="action-btn-sm primary admin-action-btn-sm primary"
                  @click="showGrantModal(record)"
                >
                  <svg
                    fill="none"
                    height="14"
                    stroke="currentColor"
                    stroke-width="2"
                    viewBox="0 0 24 24"
                    width="14"
                  >
                    <path d="M20 12v6a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2v-6"></path>
                    <rect height="4" width="16" x="4" y="4"></rect>
                  </svg>
                  发放积分
                </button>
              </div>
            </template>
          </template>
        </a-table>
      </div>
    </div>

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
          <svg
            fill="none"
            height="28"
            stroke="var(--admin-color-primary)"
            stroke-width="2"
            viewBox="0 0 24 24"
            width="28"
          >
            <path d="M20 12v6a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2v-6"></path>
            <rect height="4" width="16" x="4" y="4"></rect>
          </svg>
          <span class="modal-title">为用户发放积分</span>
        </div>
        <a-form :label-col="{ span: 5 }" :model="grantForm" :wrapper-col="{ span: 19 }">
          <a-form-item label="用户ID">
            <a-input
              v-model:value="grantForm.userId"
              class="form-input admin-filter-input"
              disabled
              size="large"
            />
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
              class="form-input admin-filter-input"
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
              class="form-input admin-filter-input"
              placeholder="请输入发放积分的备注说明"
            />
          </a-form-item>
        </a-form>
        <div class="modal-footer">
          <button class="action-btn secondary admin-action-btn secondary" @click="resetGrantForm">
            取消
          </button>
          <button
            :disabled="granting"
            class="action-btn primary admin-action-btn primary"
            @click="handleGrant"
          >
            确认发放
          </button>
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
          <svg
            fill="none"
            height="28"
            stroke="var(--admin-color-primary)"
            stroke-width="2"
            viewBox="0 0 24 24"
            width="28"
          >
            <path d="M20 12v6a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2v-6"></path>
            <rect height="4" width="16" x="4" y="4"></rect>
          </svg>
          <span class="modal-title">批量发放积分</span>
        </div>
        <a-alert
          class="warning-alert"
          message="此操作将给所有用户发放积分，请谨慎操作！"
          show-icon
          type="warning"
        />
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
              class="form-input admin-filter-input"
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
              class="form-input admin-filter-input"
              placeholder="请输入发放积分的备注说明"
            />
          </a-form-item>
        </a-form>
        <div class="modal-footer">
          <button
            class="action-btn secondary admin-action-btn secondary"
            @click="resetGrantAllForm"
          >
            取消
          </button>
          <button
            :disabled="granting"
            class="action-btn primary admin-action-btn primary"
            @click="handleGrantAll"
          >
            确认发放
          </button>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, ref } from 'vue'
import { message } from 'ant-design-vue'
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
  { title: '用户ID', dataIndex: 'userId', width: 80, fixed: 'left' as const },
  { title: '邀请码', dataIndex: 'invitationCode', width: 120 },
  { title: '可用积分', dataIndex: 'availablePoints', width: 100 },
  { title: '累计积分', dataIndex: 'totalPoints', width: 100 },
  { title: '冻结积分', dataIndex: 'freezePoints', width: 100 },
  { title: '累计消耗', dataIndex: 'totalConsume', width: 100 },
  { title: '连续签到天数', dataIndex: 'continuousDays', width: 110 },
  { title: '邀请人数', dataIndex: 'invitationCount', width: 100 },
  { title: '邀请奖励积分', dataIndex: 'totalInvitePoints', width: 100 },
  { title: '最后签到日期', dataIndex: 'lastSignDate', width: 100 },
  { title: '操作', key: 'action', width: 140 },
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
  } catch {
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
    points: '',
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
      fetchData()
    } else {
      message.error(res.data.message || '发放失败')
    }
  } catch {
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
    points: '',
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
      fetchData()
    } else {
      message.error(res.data.message || '发放失败')
    }
  } catch {
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

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@300;400;500;600;700&display=swap');
@import '@/styles/admin-glassmorphism.css';

/* ========== 页面特定样式 ========== */
.filter-actions {
  display: flex;
  gap: 12px;
}

/* 覆盖全局的筛选网格布局，改为更合理的 2 列布局 */
.user-account-wrapper .admin-filter-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

@media (max-width: 1024px) {
  .user-account-wrapper .admin-filter-grid {
    grid-template-columns: 1fr;
  }
}

.range-inputs {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
}

.range-inputs input {
  flex: 1;
  min-width: 0;
}

.range-separator {
  color: var(--admin-text-secondary);
  font-weight: 600;
  flex-shrink: 0;
}

/* 统计卡片 */
.stats-cards-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.stats-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.stats-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--admin-shadow-hover);
}

.stat-icon-wrapper {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stat-icon-wrapper svg {
  width: 24px;
  height: 24px;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--admin-text-primary);
  line-height: 1;
  margin-bottom: 6px;
}

.stat-label {
  font-size: 13px;
  color: var(--admin-text-secondary);
  font-weight: 500;
}

/* 徽章样式 */
.code-badge,
.points-badge,
.freeze-badge,
.consume-badge,
.days-badge,
.invite-badge,
.invite-points-badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
}

.days-gold {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  color: #d97706;
}

.days-cyan {
  background: linear-gradient(135deg, #cffafe 0%, #a5f3fc 100%);
  color: #0891b2;
}

.days-default {
  background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);
  color: #64748b;
}

/* 弹窗样式 */
.grant-modal-content {
  padding: 8px 0;
}

.modal-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(255, 107, 107, 0.1);
}

.modal-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--admin-text-primary);
}

.warning-alert {
  margin-bottom: 24px;
  padding: 12px 16px;
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  border: none;
  border-radius: 10px;
}

.warning-alert :deep(.ant-alert-icon) {
  color: #d97706;
}

.warning-alert :deep(.ant-alert-message) {
  color: #d97706;
  font-size: 14px;
  font-weight: 500;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid rgba(255, 107, 107, 0.1);
}

.form-input :deep(.ant-input),
.form-input :deep(textarea) {
  border-radius: 10px;
  border: 1.5px solid rgba(255, 107, 107, 0.15);
  transition: all 0.3s ease;
}

.form-input :deep(.ant-input:hover),
.form-input :deep(textarea:hover) {
  border-color: var(--admin-color-primary);
}

.form-input :deep(.ant-input:focus),
.form-input :deep(textarea:focus) {
  border-color: var(--admin-color-primary);
  background: rgba(255, 255, 255, 1);
}
</style>
