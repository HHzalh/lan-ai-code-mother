<template>
  <div class="point-rule-container">
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
            <SettingOutlined class="header-icon" />
          </div>
          <div class="header-text">
            <h1 class="page-title">积分规则配置</h1>
            <p class="page-subtitle">
              <KeyOutlined />
              管理积分获取与消耗规则
            </p>
          </div>
        </div>
      </div>

      <!-- 主卡片 -->
      <div class="main-card info-card">
        <!-- 数据表格 -->
        <div class="table-section">
          <div class="table-header">
            <div class="table-header-left">
              <TableOutlined class="table-icon" />
              <span class="table-title">规则列表</span>
            </div>
          </div>

          <a-table
            :columns="columns"
            :data-source="data"
            :loading="loading"
            :pagination="false"
            :scroll="{ x: 1100 }"
            class="rules-table"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.dataIndex === 'ruleKey'">
                <a-tag class="rule-key-tag">
                  <KeyOutlined />
                  {{ record.ruleKey }}
                </a-tag>
              </template>
              <template v-else-if="column.dataIndex === 'ruleDesc'">
                <a-input
                  v-model:value="record.ruleDesc"
                  class="rule-input"
                  placeholder="规则描述"
                  @blur="handleDescChange(record)"
                >
                  <template #prefix>
                    <FileTextOutlined />
                  </template>
                </a-input>
              </template>
              <template v-else-if="column.dataIndex === 'ruleValue'">
                <a-input-number
                  v-model:value="record.ruleValue"
                  :min="0"
                  :precision="0"
                  class="rule-input-number"
                  @blur="handleValueChange(record)"
                />
              </template>
              <template v-else-if="column.dataIndex === 'status'">
                <a-switch
                  :checked="record.status === 1"
                  class="rule-switch"
                  @change="
                    (checked: boolean) => {
                      record.status = checked ? 1 : 0
                      handleStatusChange(record)
                    }
                  "
                />
              </template>
            </template>
          </a-table>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, ref } from 'vue'
import { getAllRules, updateRule } from '@/api/pointController'
import { message } from 'ant-design-vue'
import {
  FileTextOutlined,
  KeyOutlined,
  SettingOutlined,
  TableOutlined,
} from '@ant-design/icons-vue'

const columns = [
  {
    title: '规则键',
    dataIndex: 'ruleKey',
    width: 200,
    fixed: 'left' as const,
  },
  {
    title: '规则描述',
    dataIndex: 'ruleDesc',
    width: 350,
  },
  {
    title: '规则值（积分）',
    dataIndex: 'ruleValue',
    width: 180,
  },
  {
    title: '状态',
    dataIndex: 'status',
    width: 120,
  },
]

// 数据
const data = ref<API.PointRuleVO[]>([])
const loading = ref(false)

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    const res = await getAllRules()
    if (res.data.code === 0 && res.data.data) {
      data.value = res.data.data.map((rule) => ({
        ...rule,
        status: rule.status ?? 1,
      }))
    } else {
      message.error('获取数据失败，' + res.data.message)
    }
  } catch (error) {
    console.error('获取数据失败：', error)
    message.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

// 更新规则值
const handleValueChange = async (record: API.PointRuleVO) => {
  if (!record.id) return
  try {
    const res = await updateRule({
      id: record.id,
      ruleValue: record.ruleValue,
    })
    if (res.data.code === 0) {
      message.success('✅ 规则值已更新')
    } else {
      message.error(res.data.message ?? '更新失败')
      await fetchData()
    }
  } catch (error) {
    message.error('更新失败，请重试')
    await fetchData()
  }
}

// 更新规则描述
const handleDescChange = async (record: API.PointRuleVO) => {
  if (!record.id) return
  try {
    const res = await updateRule({
      id: record.id,
      ruleDesc: record.ruleDesc,
    })
    if (res.data.code === 0) {
      message.success('✅ 规则描述已更新')
    } else {
      message.error(res.data.message ?? '更新失败')
      await fetchData()
    }
  } catch (error) {
    message.error('更新失败，请重试')
    await fetchData()
  }
}

// 更新规则状态
const handleStatusChange = async (record: API.PointRuleVO) => {
  if (!record.id) return
  const statusValue =
    typeof record.status === 'boolean' ? (record.status ? 1 : 0) : (record.status ?? 1)

  try {
    const res = await updateRule({
      id: record.id,
      status: statusValue as number,
    })
    if (res.data.code === 0) {
      message.success('✅ 规则状态已更新')
      record.status = statusValue
    } else {
      message.error(res.data.message ?? '更新失败')
      record.status = statusValue === 1 ? 0 : 1
      await fetchData()
    }
  } catch (error) {
    message.error('更新失败，请重试')
    record.status = statusValue === 1 ? 0 : 1
    await fetchData()
  }
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

.point-rule-container {
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
  background-image: url('https://images.unsplash.com/photo-1554224155-8d04cb21cd6c?w=1920&q=80');
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
  margin-bottom: 28px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
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
  opacity: 0.95;
  font-weight: 400;
  color: white;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.5);
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

.table-header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.table-icon {
  font-size: 18px;
  color: var(--color-primary);
}

.table-title {
  font-family: var(--font-serif);
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text);
}

/* 表格样式 */
.rules-table :deep(.ant-table) {
  background: white;
  border-radius: 12px;
  overflow: hidden;
}

.rules-table :deep(.ant-table-thead > tr > th) {
  background: #3b82f6;
  color: white;
  font-weight: 700;
  font-size: 14px;
  padding: 16px;
  border: none;
  letter-spacing: 0.5px;
}

.rules-table :deep(.ant-table-tbody > tr > td) {
  padding: 16px;
  font-size: 14px;
  border-bottom: 1px solid var(--color-border);
  background: white;
}

.rules-table :deep(.ant-table-tbody > tr:hover > td) {
  background: var(--color-bg-hover);
}

.rules-table :deep(.ant-table-tbody > tr > td:first-child) {
  border-radius: 0 8px 8px 0;
}

.rules-table :deep(.ant-table-tbody > tr > td:last-child) {
  border-radius: 8px 0 0 8px;
}

/* 规则键标签 */
.rule-key-tag {
  font-size: 13px;
  padding: 6px 12px;
  border-radius: 8px;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  border: none;
  background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
  color: #2563eb;
}

/* 输入框样式 */
.rule-input {
  border-radius: 10px;
  height: 32px;
  border: 2px solid var(--color-border);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: white;
}

.rule-input :deep(.ant-input) {
  background: white !important;
  border: none !important;
  padding: 0 !important;
  height: 100% !important;
  color: var(--color-text) !important;
  font-weight: 600 !important;
}

.rule-input :deep(.ant-input:focus) {
  background: white !important;
  box-shadow: none !important;
}

.rule-input:hover {
  border-color: var(--color-primary);
}

.rule-input:focus,
.rule-input.ant-input-focused {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 4px rgba(249, 115, 22, 0.1);
  background: white;
}

.rule-input :deep(.ant-input-prefix) {
  color: var(--color-primary);
  font-size: 16px;
  margin-right: 8px;
}

.rules-table :deep(.ant-table-tbody > tr:hover > td .rule-input) {
  background: var(--color-bg-hover);
}

.rules-table :deep(.ant-table-tbody > tr:hover > td .rule-input .ant-input) {
  background: var(--color-bg-hover) !important;
}

/* 数字输入框 */
.rule-input-number {
  width: 100%;
  border-radius: 10px;
  height: 32px;
  border: 2px solid var(--color-border);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: white;
}

.rule-input-number :deep(.ant-input-number-input) {
  background: white !important;
  border: none !important;
  color: var(--color-text) !important;
  font-weight: 600 !important;
}

.rule-input-number :deep(.ant-input-number-input:focus) {
  background: white !important;
  box-shadow: none !important;
}

.rule-input-number:hover {
  border-color: var(--color-primary);
}

.rule-input-number:focus,
.rule-input-number.ant-input-number-focused {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 4px rgba(249, 115, 22, 0.1);
  background: white;
}

.rules-table :deep(.ant-table-tbody > tr:hover > td .rule-input-number) {
  background: var(--color-bg-hover);
}

.rules-table :deep(.ant-table-tbody > tr:hover > td .rule-input-number .ant-input-number-input) {
  background: var(--color-bg-hover) !important;
}

/* 开关样式 */
.rule-switch {
  min-width: 52px;
}

.rule-switch :deep(.ant-switch-checked) {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
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

/* ========== 补充统一样式 ========== */ /* 输入框和选择器 */ :deep(.ant-input),
:deep(.ant-input-number), :deep(.ant-select-selector) { height: 48px !important; border-radius: 12px
!important; } /* 搜索按钮 */ :deep(.ant-btn) { height: 48px !important; border-radius: 12px
!important; } /* 表格按钮 */ :deep(.ant-table) .ant-btn-primary { height: 32px !important;
border-radius: 12px !important; }
