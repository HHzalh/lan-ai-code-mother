<template>
  <div class="point-manage-wrapper admin-manage-wrapper">
    <!-- 动态渐变背景 -->
    <div class="gradient-bg admin-gradient-bg"></div>

    <!-- 主容器 -->
    <div class="manage-container admin-manage-container">
      <!-- 页面标题区 -->
      <div class="page-header admin-page-header">
        <h1 class="page-title admin-page-title">积分管理</h1>
        <p class="page-subtitle admin-page-subtitle">管理用户积分账户、流水和规则</p>
      </div>

      <!-- 标签页卡片 -->
      <div class="tabs-card admin-glass-card admin-tabs-card">
        <a-tabs v-model:activeKey="activeKey" class="point-tabs admin-point-tabs">
          <a-tab-pane key="account">
            <template #tab>
              <span class="tab-item admin-tab-item">
                <svg
                  fill="none"
                  height="16"
                  stroke="currentColor"
                  stroke-width="2"
                  viewBox="0 0 24 24"
                  width="16"
                >
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                  <circle cx="12" cy="7" r="4"></circle>
                </svg>
                <span>用户积分账户</span>
              </span>
            </template>
            <UserAccountManagePage />
          </a-tab-pane>

          <a-tab-pane key="logs">
            <template #tab>
              <span class="tab-item admin-tab-item">
                <svg
                  fill="none"
                  height="16"
                  stroke="currentColor"
                  stroke-width="2"
                  viewBox="0 0 24 24"
                  width="16"
                >
                  <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                  <polyline points="14 2 14 8 20 8"></polyline>
                  <line x1="16" x2="8" y1="13" y2="13"></line>
                  <line x1="16" x2="8" y1="17" y2="17"></line>
                  <polyline points="10 9 9 9 8 9"></polyline>
                </svg>
                <span>积分流水管理</span>
              </span>
            </template>
            <PointLogManagePage />
          </a-tab-pane>

          <a-tab-pane key="rules">
            <template #tab>
              <span class="tab-item admin-tab-item">
                <svg
                  fill="none"
                  height="16"
                  stroke="currentColor"
                  stroke-width="2"
                  viewBox="0 0 24 24"
                  width="16"
                >
                  <circle cx="12" cy="12" r="3"></circle>
                  <path d="M12 1v6m0 6v6m9-3h-6m-6 0H3"></path>
                </svg>
                <span>积分规则管理</span>
              </span>
            </template>
            <PointRuleManagePage />
          </a-tab-pane>
        </a-tabs>
      </div>
    </div>
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

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@300;400;500;600;700&display=swap');
@import '@/styles/admin-glassmorphism.css';

/* ========== 标签页卡片 ========== */
.admin-tabs-card {
  padding: 0;
  overflow: hidden;
}

/* ========== 标签页导航样式 ========== */
.admin-point-tabs :deep(.ant-tabs-nav) {
  margin-bottom: 0;
  background: rgba(255, 255, 255, 0.5);
  border-bottom: 1px solid rgba(255, 107, 107, 0.15);
  padding: 16px 24px 0;
}

.admin-point-tabs :deep(.ant-tabs-nav::before) {
  display: none;
}

.admin-point-tabs :deep(.ant-tabs-tab) {
  margin: 0 8px -1px 0;
  padding: 12px 20px;
  font-size: 14px;
  font-weight: 500;
  border-radius: 12px 12px 0 0;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  background: transparent;
  border: 1px solid transparent;
  color: var(--admin-text-secondary);
}

.admin-point-tabs :deep(.ant-tabs-tab:hover) {
  color: var(--admin-color-primary);
  background: rgba(255, 107, 107, 0.08);
}

.admin-point-tabs :deep(.ant-tabs-tab-active) {
  background: rgba(255, 255, 255, 0.9);
  color: var(--admin-color-primary);
  font-weight: 600;
  border: 1px solid rgba(255, 107, 107, 0.2);
  border-bottom-color: rgba(255, 255, 255, 0.9);
  box-shadow: 0 -2px 8px rgba(255, 107, 107, 0.1);
}

/* ========== 标签页内容区域 ========== */
.admin-point-tabs :deep(.ant-tabs-content) {
  padding: 0;
}

.admin-point-tabs :deep(.ant-tabs-tabpane) {
  background: transparent;
}

/* ========== 活动指示条 ========== */
.admin-point-tabs :deep(.ant-tabs-ink-bar) {
  background: linear-gradient(
    90deg,
    var(--admin-color-primary) 0%,
    var(--admin-color-primary-dark) 100%
  );
  height: 3px;
  border-radius: 2px;
}

/* ========== 标签项图标和文字 ========== */
.admin-tab-item {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

/* ========== 子页面容器样式调整 ========== */
.admin-point-tabs :deep(.admin-manage-wrapper) {
  background: transparent;
}

.admin-point-tabs :deep(.admin-gradient-bg) {
  display: none;
}

.admin-point-tabs :deep(.admin-manage-container) {
  max-width: 100%;
  padding: 24px;
}

.admin-point-tabs :deep(.admin-page-header) {
  display: none;
}

.admin-point-tabs :deep(.admin-filter-card),
.admin-point-tabs :deep(.admin-table-card) {
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(16px);
  border: 1px solid rgba(255, 107, 107, 0.12);
  box-shadow: 0 4px 16px rgba(255, 107, 107, 0.08);
}
</style>
