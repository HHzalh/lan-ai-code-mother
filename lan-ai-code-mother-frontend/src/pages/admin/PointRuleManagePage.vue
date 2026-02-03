<template>
  <div class="point-rule-wrapper admin-manage-wrapper">
    <!-- 动态渐变背景 -->
    <div class="gradient-bg admin-gradient-bg"></div>

    <!-- 主容器 -->
    <div class="manage-container admin-manage-container">
      <!-- 页面标题区 -->
      <div class="page-header admin-page-header">
        <h1 class="page-title admin-page-title">积分规则配置</h1>
        <p class="page-subtitle admin-page-subtitle">管理积分获取与消耗规则</p>
      </div>

      <!-- 规则列表卡片 -->
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
              <circle cx="12" cy="12" r="3"></circle>
              <path d="M12 1v6m0 6v6m9-3h-6m-6 0H3"></path>
            </svg>
            <span>规则列表</span>
          </div>
        </div>

        <a-table
          :columns="columns"
          :data-source="data"
          :loading="loading"
          :pagination="false"
          :scroll="{ x: 1100 }"
          class="rules-table admin-data-table"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.dataIndex === 'ruleKey'">
              <span
                class="rule-key-badge admin-type-badge"
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
                  <path
                    d="M21 2l-2 2m-7.61 7.61a5.5 5.5 0 1 1-7.778 7.778 5.5 5.5 0 0 1 7.777-7.777zm0 0L15.5 7.5m0 0l3 3L22 7l-3-3m-3.5 3.5L19 4"
                  ></path>
                </svg>
                {{ record.ruleKey }}
              </span>
            </template>

            <template v-else-if="column.dataIndex === 'ruleDesc'">
              <input
                v-model="record.ruleDesc"
                class="rule-input admin-filter-input"
                placeholder="规则描述"
                @blur="handleDescChange(record)"
              />
            </template>

            <template v-else-if="column.dataIndex === 'ruleValue'">
              <input
                v-model.number="record.ruleValue"
                class="rule-input-number admin-filter-input"
                min="0"
                type="number"
                @blur="handleValueChange(record)"
              />
            </template>

            <template v-else-if="column.dataIndex === 'status'">
              <div class="rule-status-wrapper">
                <label class="rule-switch">
                  <input
                    :checked="record.status === 1"
                    type="checkbox"
                    @change="
                      (e: any) => {
                        record.status = e.target.checked ? 1 : 0
                        handleStatusChange(record)
                      }
                    "
                  />
                  <span class="slider"></span>
                </label>
              </div>
            </template>
          </template>
        </a-table>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, ref } from 'vue'
import { getAllRules, updateRule } from '@/api/pointController'
import { message } from 'ant-design-vue'

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
    width: 150,
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
      message.success('规则值已更新')
    } else {
      message.error(res.data.message ?? '更新失败')
      await fetchData()
    }
  } catch {
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
      message.success('规则描述已更新')
    } else {
      message.error(res.data.message ?? '更新失败')
      await fetchData()
    }
  } catch {
    message.error('更新失败，请重试')
    await fetchData()
  }
}

// 更新规则状态
const handleStatusChange = async (record: API.PointRuleVO) => {
  if (!record.id) return
  const statusValue = record.status ?? 1

  try {
    const res = await updateRule({
      id: record.id,
      status: statusValue,
    })
    if (res.data.code === 0) {
      message.success('规则状态已更新')
    } else {
      message.error(res.data.message ?? '更新失败')
      record.status = statusValue === 1 ? 0 : 1
      await fetchData()
    }
  } catch {
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
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@300;400;500;600;700&display=swap');
@import '@/styles/admin-glassmorphism.css';

/* ========== 页面特定样式 ========== */
.rule-key-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 600;
}

/* 规则输入框 */
.rule-input,
.rule-input-number {
  width: 100%;
  padding: 8px 12px;
  border-radius: 10px;
  border: 1.5px solid rgba(255, 107, 107, 0.15);
  background: rgba(255, 255, 255, 0.8);
  font-size: 14px;
  font-weight: 500;
  color: var(--admin-text-primary);
  transition: all 0.3s ease;
  outline: none;
}

.rule-input:hover,
.rule-input-number:hover {
  border-color: var(--admin-color-primary);
}

.rule-input:focus,
.rule-input-number:focus {
  border-color: var(--admin-color-primary);
  background: rgba(255, 255, 255, 1);
}

/* 状态显示容器 */
.rule-status-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 状态文字标签 */
.rule-status-text {
  font-size: 13px;
  font-weight: 600;
  color: var(--admin-text-secondary);
  transition: all 0.3s ease;
}

.rule-status-text.active {
  color: var(--admin-color-primary);
}

/* 规则开关 */
.rule-switch {
  position: relative;
  display: inline-block;
  width: 52px;
  height: 28px;
}

.rule-switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  color: black;
  background-color: rgba(255, 107, 107, 0.2);
  transition: 0.4s;
  border-radius: 34px;
}

.slider:before {
  position: absolute;
  content: '';
  height: 20px;
  width: 20px;
  left: 4px;
  bottom: 4px;
  background-color: #4f93ed;
  transition: 0.4s;
  border-radius: 50%;
}

input:checked + .slider {
  background: linear-gradient(
    135deg,
    var(--admin-color-primary) 0%,
    var(--admin-color-primary-dark) 100%
  );
}

input:checked + .slider:before {
  transform: translateX(24px);
}

input:focus + .slider {
  box-shadow: 0 0 1px var(--admin-color-primary);
}
</style>
