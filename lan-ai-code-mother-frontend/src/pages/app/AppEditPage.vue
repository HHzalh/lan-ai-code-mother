<template>
  <div id="appEditPage">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-left">
          <a-button type="text" @click="goToChat">
            <template #icon>
              <ArrowLeftOutlined />
            </template>
          </a-button>
          <div class="header-info">
            <h1>编辑应用</h1>
            <p v-if="appInfo">修改应用配置和信息</p>
          </div>
        </div>
        <div class="header-actions">
          <a-button @click="goToChat">
            <template #icon>
              <MessageOutlined />
            </template>
            进入对话
          </a-button>
        </div>
      </div>
    </div>

    <!-- 主要内容 -->
    <div class="edit-container">
      <a-row :gutter="[24, 24]">
        <!-- 左侧：编辑表单 -->
        <a-col :lg="14" :xs="24">
          <a-spin :spinning="loading">
            <div class="form-card info-card">
              <div class="card-header">
                <EditOutlined class="header-icon" />
                <h2>基本信息</h2>
              </div>

              <a-form
                ref="formRef"
                :model="formData"
                :rules="rules"
                layout="vertical"
                @finish="handleSubmit"
              >
                <a-form-item label="应用名称" name="appName">
                  <a-input
                    v-model:value="formData.appName"
                    :maxlength="50"
                    placeholder="请输入应用名称"
                    show-count
                    size="large"
                  />
                </a-form-item>

                <a-form-item
                  v-if="isAdmin"
                  extra="支持图片链接，建议尺寸：400x300"
                  label="应用封面"
                  name="cover"
                >
                  <a-input
                    v-model:value="formData.cover"
                    placeholder="请输入封面图片链接"
                    size="large"
                  />
                  <div v-if="formData.cover" class="cover-preview">
                    <img :alt="formData.appName" :src="formData.cover" />
                  </div>
                </a-form-item>

                <a-form-item
                  v-if="isAdmin"
                  extra="设置为99表示精选应用"
                  label="优先级"
                  name="priority"
                >
                  <a-input-number
                    v-model:value="formData.priority"
                    :max="99"
                    :min="0"
                    size="large"
                    style="width: 100%"
                  />
                </a-form-item>

                <a-divider>只读信息</a-divider>

                <a-form-item label="初始提示词" name="initPrompt">
                  <a-textarea
                    v-model:value="formData.initPrompt"
                    :maxlength="1000"
                    :rows="4"
                    disabled
                    placeholder="请输入初始提示词"
                    show-count
                  />
                  <div class="form-tip">
                    <LockOutlined />
                    初始提示词不可修改
                  </div>
                </a-form-item>

                <a-form-item label="生成类型" name="codeGenType">
                  <a-input
                    :value="formatCodeGenType(formData.codeGenType)"
                    disabled
                    placeholder="生成类型"
                    size="large"
                  >
                    <template #prefix>
                      <CodeOutlined />
                    </template>
                  </a-input>
                  <div class="form-tip">
                    <LockOutlined />
                    生成类型不可修改
                  </div>
                </a-form-item>

                <a-form-item v-if="formData.deployKey" label="部署密钥" name="deployKey">
                  <a-input
                    v-model:value="formData.deployKey"
                    disabled
                    placeholder="部署密钥"
                    size="large"
                  >
                    <template #prefix>
                      <KeyOutlined />
                    </template>
                  </a-input>
                  <div class="form-tip">
                    <LockOutlined />
                    部署密钥不可修改
                  </div>
                </a-form-item>

                <a-form-item>
                  <a-space :size="12">
                    <a-button :loading="submitting" html-type="submit" size="large" type="primary">
                      <template #icon>
                        <SaveOutlined />
                      </template>
                      保存修改
                    </a-button>
                    <a-button size="large" @click="resetForm">
                      <template #icon>
                        <ReloadOutlined />
                      </template>
                      重置
                    </a-button>
                  </a-space>
                </a-form-item>
              </a-form>
            </div>
          </a-spin>
        </a-col>

        <!-- 右侧：应用信息 -->
        <a-col :lg="10" :xs="24">
          <div class="info-card">
            <div class="card-header">
              <InfoCircleOutlined class="header-icon" />
              <h2>应用信息</h2>
            </div>

            <div class="info-list">
              <div class="info-item">
                <div class="item-label">
                  <IdcardOutlined />
                  应用ID
                </div>
                <div class="item-value">{{ appInfo?.id }}</div>
              </div>

              <div class="info-item">
                <div class="item-label">
                  <UserOutlined />
                  创建者
                </div>
                <div class="item-value">
                  <UserInfo :user="appInfo?.user" size="small" />
                </div>
              </div>

              <div class="info-item">
                <div class="item-label">
                  <ClockCircleOutlined />
                  创建时间
                </div>
                <div class="item-value">{{ formatTime(appInfo?.createTime) }}</div>
              </div>

              <div class="info-item">
                <div class="item-label">
                  <SyncOutlined />
                  更新时间
                </div>
                <div class="item-value">{{ formatTime(appInfo?.updateTime) }}</div>
              </div>

              <div class="info-item">
                <div class="item-label">
                  <RocketOutlined />
                  部署时间
                </div>
                <div class="item-value">
                  {{ appInfo?.deployedTime ? formatTime(appInfo.deployedTime) : '未部署' }}
                </div>
              </div>

              <div class="info-item">
                <div class="item-label">
                  <LinkOutlined />
                  访问链接
                </div>
                <div class="item-value">
                  <a-button v-if="appInfo?.deployKey" size="small" type="link" @click="openPreview">
                    <template #icon>
                      <ExportOutlined />
                    </template>
                    查看预览
                  </a-button>
                  <span v-else class="text-secondary">未部署</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 快捷操作卡片 -->
          <div class="action-card">
            <div class="card-header">
              <ThunderboltOutlined class="header-icon" />
              <h2>快捷操作</h2>
            </div>

            <div class="action-list">
              <a-button block size="large" @click="goToChat">
                <template #icon>
                  <MessageOutlined />
                </template>
                进入对话
              </a-button>
              <a-button
                v-if="appInfo?.deployKey"
                block
                size="large"
                type="primary"
                @click="openPreview"
              >
                <template #icon>
                  <ExportOutlined />
                </template>
                预览应用
              </a-button>
            </div>
          </div>
        </a-col>
      </a-row>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type { FormInstance } from 'ant-design-vue'
import { message } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/loginUser'
import { getAppVoById, updateApp, updateAppByAdmin } from '@/api/appController'
import { formatCodeGenType } from '@/utils/codeGenTypes'
import { formatTime } from '@/utils/time'
import UserInfo from '@/components/UserInfo.vue'
import { getStaticPreviewUrl } from '@/config/env'
import {
  ArrowLeftOutlined,
  ClockCircleOutlined,
  CodeOutlined,
  EditOutlined,
  ExportOutlined,
  IdcardOutlined,
  InfoCircleOutlined,
  KeyOutlined,
  LinkOutlined,
  LockOutlined,
  MessageOutlined,
  ReloadOutlined,
  RocketOutlined,
  SaveOutlined,
  SyncOutlined,
  ThunderboltOutlined,
  UserOutlined,
} from '@ant-design/icons-vue'

const route = useRoute()
const router = useRouter()
const loginUserStore = useLoginUserStore()

// 应用信息
const appInfo = ref<API.AppVO>()
const loading = ref(false)
const submitting = ref(false)
const formRef = ref<FormInstance>()

// 表单数据
const formData = reactive({
  appName: '',
  cover: '',
  priority: 0,
  initPrompt: '',
  codeGenType: '',
  deployKey: '',
})

// 是否为管理员
const isAdmin = computed(() => {
  return loginUserStore.loginUser.userRole === 'admin'
})

// 表单验证规则
const rules = {
  appName: [
    { required: true, message: '请输入应用名称', trigger: 'blur' },
    { min: 1, max: 50, message: '应用名称长度在1-50个字符', trigger: 'blur' },
  ],
  cover: [{ type: 'url', message: '请输入有效的URL', trigger: 'blur' }],
  priority: [{ type: 'number', min: 0, max: 99, message: '优先级范围0-99', trigger: 'blur' }],
}

// 获取应用信息
const fetchAppInfo = async () => {
  const id = route.params.id as string
  if (!id) {
    message.error('应用ID不存在')
    router.push('/')
    return
  }

  loading.value = true
  try {
    const res = await getAppVoById({ id: id as unknown as number })
    if (res.data.code === 0 && res.data.data) {
      appInfo.value = res.data.data

      // 检查权限
      if (!isAdmin.value && appInfo.value.userId !== loginUserStore.loginUser.id) {
        message.error('您没有权限编辑此应用')
        router.push('/')
        return
      }

      // 填充表单数据
      formData.appName = appInfo.value.appName || ''
      formData.cover = appInfo.value.cover || ''
      formData.priority = appInfo.value.priority || 0
      formData.initPrompt = appInfo.value.initPrompt || ''
      formData.codeGenType = appInfo.value.codeGenType || ''
      formData.deployKey = appInfo.value.deployKey || ''
    } else {
      message.error('获取应用信息失败')
      router.push('/')
    }
  } catch (error) {
    console.error('获取应用信息失败：', error)
    message.error('获取应用信息失败')
    router.push('/')
  } finally {
    loading.value = false
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!appInfo.value?.id) return

  submitting.value = true
  try {
    let res
    if (isAdmin.value) {
      // 管理员可以修改更多字段
      res = await updateAppByAdmin({
        id: appInfo.value.id,
        appName: formData.appName,
        cover: formData.cover,
        priority: formData.priority,
      })
    } else {
      // 普通用户只能修改应用名称
      res = await updateApp({
        id: appInfo.value.id,
        appName: formData.appName,
      })
    }

    if (res.data.code === 0) {
      message.success('修改成功')
      // 重新获取应用信息
      await fetchAppInfo()
    } else {
      message.error('修改失败：' + res.data.message)
    }
  } catch (error) {
    console.error('修改失败：', error)
    message.error('修改失败')
  } finally {
    submitting.value = false
  }
}

// 重置表单
const resetForm = () => {
  if (appInfo.value) {
    formData.appName = appInfo.value.appName || ''
    formData.cover = appInfo.value.cover || ''
    formData.priority = appInfo.value.priority || 0
  }
  formRef.value?.clearValidate()
  message.info('表单已重置')
}

// 进入对话页面
const goToChat = () => {
  if (appInfo.value?.id) {
    router.push(`/app/chat/${appInfo.value.id}?view`)
  }
}

// 打开预览
const openPreview = () => {
  if (appInfo.value?.codeGenType && appInfo.value?.id) {
    const url = getStaticPreviewUrl(appInfo.value.codeGenType, String(appInfo.value.id))
    window.open(url, '_blank')
  }
}

// 页面加载时获取应用信息
onMounted(() => {
  fetchAppInfo()
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@400;500;600;700&display=swap');

:root {
  --color-primary: #f97316;
  --color-primary-dark: #ea580c;
  --color-primary-light: #fbbf24;
  --color-text: #1e293b;
  --color-text-secondary: #64748b;
  --color-border: #e2e8f0;
  --color-bg-hover: #f8fafc;
  --font-sans: 'Noto Sans SC', sans-serif;
}

#appEditPage {
  min-height: 100vh;
  background: #f8fafc;
  padding-bottom: 40px;
}

/* 页面头部 */
.page-header {
  background: white;
  border-bottom: 1px solid var(--color-border);
  padding: 20px 0;
  margin-bottom: 24px;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-info h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text);
}

.header-info p {
  margin: 4px 0 0;
  font-size: 14px;
  color: var(--color-text-secondary);
}

/* 编辑容器 */
.edit-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 16px;
}

/* 卡片通用样式 */
.form-card,
.info-card,
.action-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  margin-bottom: 24px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--color-border);
}

.header-icon {
  font-size: 20px;
  color: var(--color-primary);
}

.card-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text);
}

/* 表单样式 */
:deep(.ant-form-item-label > label) {
  font-weight: 500;
  color: var(--color-text);
}

:deep(.ant-input),
:deep(.ant-input-number) {
  border-radius: 8px;
}

:deep(.ant-input:focus),
:deep(.ant-input-number:focus) {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 2px rgba(249, 115, 22, 0.1);
}

.cover-preview {
  margin-top: 12px;
  padding: 12px;
  border: 1px solid var(--color-border);
  border-radius: 8px;
  background: var(--color-bg-hover);
  text-align: center;
}

.cover-preview img {
  max-width: 100%;
  height: auto;
  border-radius: 4px;
}

.form-tip {
  font-size: 12px;
  color: var(--color-text-secondary);
  margin-top: 6px;
  display: flex;
  align-items: center;
  gap: 4px;
}

:deep(.ant-divider-horizontal.ant-divider-with-text) {
  font-size: 13px;
  color: var(--color-text-secondary);
  margin: 32px 0 24px;
}

/* 信息列表样式 */
.info-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: var(--color-bg-hover);
  border-radius: 8px;
}

.item-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text-secondary);
}

.item-label :deep(.anticon) {
  color: var(--color-primary);
}

.item-value {
  font-size: 14px;
  color: var(--color-text);
  font-weight: 500;
}

.text-secondary {
  color: var(--color-text-secondary);
}

/* 操作卡片 */
.action-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.action-list :deep(.ant-btn) {
  height: 48px;
  border-radius: 8px;
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 16px;
  }

  .header-left {
    width: 100%;
  }

  .header-actions {
    width: 100%;
  }

  .header-actions :deep(.ant-btn) {
    width: 100%;
  }

  .edit-container {
    padding: 0 16px;
  }

  .form-card,
  .info-card,
  .action-card {
    padding: 16px;
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
  font-weight: 600 !important;
  font-size: 13px !important;
  background: white !important;
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
  font-weight: 600 !important;
  font-size: 14px !important;
}

:deep(.ant-input::placeholder) {
  color: #64748b !important;
  font-weight: 400 !important;
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
