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
  background: #f0f2f5;
  min-height: 100vh;

  .point-tabs {
    :deep(.ant-tabs) {
      .ant-tabs-nav {
        margin-bottom: 16px;
        background: #fff;
        border-radius: 8px 8px 0 0;
        padding: 8px 16px 0;
      }

      .ant-tabs-tab {
        margin: 0 4px -1px 0;
        padding: 12px 24px;
        font-size: 15px;
        font-weight: 500;
        border-radius: 8px 8px 0 0;
        transition: all 0.3s;

        &:hover {
          color: #1890ff;
        }

        &.ant-tabs-tab-active {
          background: #fff;
          color: #1890ff;
          font-weight: 600;
        }
      }

      .ant-tabs-ink-bar {
        display: none;
      }
    }

    :deep(.ant-tabs-content-holder) {
      background: #fff;
      border-radius: 0 0 8px 8px;
      padding: 24px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }
  }
}
</style>
