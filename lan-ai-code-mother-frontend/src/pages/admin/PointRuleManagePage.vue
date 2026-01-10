<template>
  <div class="point-rule-content">
    <a-table
      :columns="columns"
      :data-source="data"
      :loading="loading"
      :pagination="false"
      :scroll="{ x: 1000 }"
      bordered
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'status'">
          <a-switch
            :checked="record.status === 1"
            @change="
              (checked: boolean) => {
                record.status = checked ? 1 : 0
                handleStatusChange(record)
              }
            "
          />
        </template>
        <template v-else-if="column.dataIndex === 'ruleValue'">
          <a-input-number
            v-model:value="record.ruleValue"
            :min="0"
            :precision="0"
            style="width: 150px"
            @blur="handleValueChange(record)"
          />
        </template>
        <template v-else-if="column.dataIndex === 'ruleDesc'">
          <a-input
            v-model:value="record.ruleDesc"
            placeholder="规则描述"
            @blur="handleDescChange(record)"
          />
        </template>
        <template v-else-if="column.dataIndex === 'ruleKey'">
          <a-tag color="blue">{{ record.ruleKey }}</a-tag>
        </template>
      </template>
    </a-table>
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
    fixed: 'left',
  },
  {
    title: '规则描述',
    dataIndex: 'ruleDesc',
    width: 300,
  },
  {
    title: '规则值（积分）',
    dataIndex: 'ruleValue',
    width: 200,
  },
  {
    title: '状态',
    dataIndex: 'status',
    width: 100,
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
      // 重新加载数据以恢复原值
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
      message.success('规则描述已更新')
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
  // 确保 status 是数字类型（0 或 1），而不是布尔值
  const statusValue =
    typeof record.status === 'boolean' ? (record.status ? 1 : 0) : (record.status ?? 1)

  try {
    const res = await updateRule({
      id: record.id,
      status: statusValue as number,
    })
    if (res.data.code === 0) {
      message.success('规则状态已更新')
      // 确保 record.status 是数字类型
      record.status = statusValue
    } else {
      message.error(res.data.message ?? '更新失败')
      // 恢复原状态
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
.point-rule-content {
  /* 内容由父组件提供容器 */
}
</style>
