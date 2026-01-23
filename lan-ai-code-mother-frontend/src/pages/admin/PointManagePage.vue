<template>
  <div id="pointManagePage">
    <a-tabs v-model:activeKey="activeKey" class="point-tabs" type="card">
      <a-tab-pane key="account" tab="用户积分账户">
        <UserAccountManagePage />
      </a-tab-pane>
      <a-tab-pane key="logs" tab="积分流水管理">
        <PointLogManagePage />
      </a-tab-pane>
      <a-tab-pane key="rules" tab="积分规则管理">
        <PointRuleManagePage />
      </a-tab-pane>
    </a-tabs>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import UserAccountManagePage from './UserAccountManagePage.vue'
import PointLogManagePage from './PointLogManagePage.vue'
import PointRuleManagePage from './PointRuleManagePage.vue'

// 当前激活的标签页
const activeKey = ref('account')
</script>

<style lang="less" scoped>
#pointManagePage {
  padding: 24px;
  background: linear-gradient(135deg, #f0f2f5 0%, #e5e7eb 100%);
  min-height: 100vh;

  .point-tabs {
    :deep(.ant-tabs) {
      .ant-tabs-nav {
        margin-bottom: 16px;
        background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
        border: 1px solid #e2e8f0;
        border-radius: 8px 8px 0 0;
        padding: 8px 16px 0;
      }

      .ant-tabs-tab {
        margin: 0 4px -1px 0;
        padding: 12px 24px;
        font-size: 13px;
        font-weight: 500;
        border-radius: 8px 8px 0 0;
        transition: all 0.3s;

        &:hover {
          color: #1890ff;
          background: rgba(59, 130, 246, 0.1);
        }

        &.ant-tabs-tab-active {
          background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
          color: #1890ff;
          font-weight: 600;
          border: 1px solid #bae6fd;
        }
      }

      .ant-tabs-ink-bar {
        display: none;
      }
    }

    :deep(.ant-tabs-content-holder) {
      background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
      border: 1px solid #e2e8f0;
      border-radius: 0 0 8px 8px;
      padding: 24px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }
  }
}
</style>
