<template>
  <div class="quick-start-page">
    <div class="container">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1 class="page-title">快速开始</h1>
        <p class="page-subtitle">只需几步，即可开始使用蓝海智造创建您的应用</p>
      </div>

      <!-- 步骤指南 -->
      <div class="steps-section">
        <a-steps :current="currentStep" :items="stepItems" direction="horizontal" />
      </div>

      <!-- 详细内容 -->
      <div class="content-section">
        <!-- 步骤 1: 注册登录 -->
        <div v-show="currentStep === 0" class="step-content">
          <div class="step-card">
            <div class="step-icon">👤</div>
            <h2 class="step-title">第一步：注册/登录账号</h2>
            <p class="step-description">
              如果您还没有账号，请先注册一个新账号。如果已有账号，直接登录即可。
            </p>
            <div class="step-actions">
              <a-button size="large" type="primary" @click="goToRegister">
                <template #icon>
                  <UserAddOutlined />
                </template>
                立即注册
              </a-button>
              <a-button size="large" @click="goToLogin">
                <template #icon>
                  <LoginOutlined />
                </template>
                已有账号？登录
              </a-button>
            </div>
          </div>
        </div>

        <!-- 步骤 2: 创建应用 -->
        <div v-show="currentStep === 1" class="step-content">
          <div class="step-card">
            <div class="step-icon">✨</div>
            <h2 class="step-title">第二步：描述您的应用</h2>
            <p class="step-description">
              在首页的输入框中，详细描述您想要创建的应用。描述越详细，生成的应用越符合您的需求。
            </p>
            <div class="example-box">
              <h3>示例描述：</h3>
              <div class="example-text">
                "创建一个现代化的个人博客网站，包含文章列表、详情页、分类标签、搜索功能、评论系统和个人简介页面。采用简洁的设计风格，支持响应式布局，文章支持Markdown格式，首页展示最新文章和热门推荐。"
              </div>
            </div>
            <div class="step-actions">
              <a-button size="large" type="primary" @click="goToHome">
                <template #icon>
                  <HomeOutlined />
                </template>
                前往首页创建
              </a-button>
            </div>
          </div>
        </div>

        <!-- 步骤 3: 完善应用 -->
        <div v-show="currentStep === 2" class="step-content">
          <div class="step-card">
            <div class="step-icon">💬</div>
            <h2 class="step-title">第三步：与AI对话完善应用</h2>
            <p class="step-description">
              创建应用后，您将进入对话页面。在这里，您可以与AI对话，进一步完善和优化您的应用。
            </p>
            <div class="tips-box">
              <h3>💡 使用技巧：</h3>
              <ul>
                <li>可以要求AI修改应用的样式和布局</li>
                <li>可以添加新的功能和页面</li>
                <li>可以优化代码和性能</li>
                <li>可以要求AI解释代码逻辑</li>
              </ul>
            </div>
          </div>
        </div>

        <!-- 步骤 4: 部署应用 -->
        <div v-show="currentStep === 3" class="step-content">
          <div class="step-card">
            <div class="step-icon">🚀</div>
            <h2 class="step-title">第四步：部署和分享</h2>
            <p class="step-description">
              应用完善后，系统会自动生成部署链接。您可以点击"查看作品"按钮，访问您的应用。
            </p>
            <div class="features-box">
              <h3>主要功能：</h3>
              <div class="features-grid">
                <div class="feature-item">
                  <span class="feature-icon">📱</span>
                  <span>响应式设计</span>
                </div>
                <div class="feature-item">
                  <span class="feature-icon">🎨</span>
                  <span>现代化UI</span>
                </div>
                <div class="feature-item">
                  <span class="feature-icon">⚡</span>
                  <span>高性能</span>
                </div>
                <div class="feature-item">
                  <span class="feature-icon">🔒</span>
                  <span>安全可靠</span>
                </div>
              </div>
            </div>
            <div class="step-actions">
              <a-button size="large" type="primary" @click="goToHome">
                <template #icon>
                  <RocketOutlined />
                </template>
                开始创建应用
              </a-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 快捷操作 -->
      <div class="quick-actions-section">
        <h2 class="section-title">快捷操作</h2>
        <div class="quick-actions-grid">
          <div class="quick-action-card" @click="goToHome">
            <div class="action-icon">🏠</div>
            <h3>返回首页</h3>
            <p>开始创建您的第一个应用</p>
          </div>
          <div class="quick-action-card" @click="goToLogin">
            <div class="action-icon">🔐</div>
            <h3>用户登录</h3>
            <p>登录您的账号</p>
          </div>
          <div class="quick-action-card" @click="goToRegister">
            <div class="action-icon">📝</div>
            <h3>用户注册</h3>
            <p>创建新账号</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { HomeOutlined, LoginOutlined, RocketOutlined, UserAddOutlined } from '@ant-design/icons-vue'
import type { StepProps } from 'ant-design-vue'

const router = useRouter()
const currentStep = ref(0)

const stepItems: StepProps[] = [
  {
    title: '注册登录',
    description: '创建或登录账号',
  },
  {
    title: '描述应用',
    description: '输入应用需求',
  },
  {
    title: '完善应用',
    description: '与AI对话优化',
  },
  {
    title: '部署分享',
    description: '发布您的应用',
  },
]

const goToHome = () => {
  router.push('/')
}

const goToLogin = () => {
  router.push('/user/login')
}

const goToRegister = () => {
  router.push('/user/register')
}
</script>

<style scoped>
.quick-start-page {
  min-height: calc(100vh - 64px);
  background: linear-gradient(180deg, #f8fafc 0%, #f1f5f9 8%, #e2e8f0 20%, #cbd5e1 100%);
  padding: 40px 0 80px;
  position: relative;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

/* 页面标题 */
.page-header {
  text-align: center;
  margin-bottom: 60px;
  padding-top: 40px;
}

.page-title {
  font-size: 48px;
  font-weight: 700;
  margin: 0 0 16px;
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 50%, #10b981 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.page-subtitle {
  font-size: 18px;
  color: #64748b;
  margin: 0;
}

/* 步骤指示器 */
.steps-section {
  margin-bottom: 60px;
  padding: 0 20px;
}

/* 内容区域 */
.content-section {
  margin-bottom: 80px;
}

.step-content {
  animation: fadeIn 0.5s ease-in;
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

.step-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 48px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.8);
  text-align: center;
}

.step-icon {
  font-size: 64px;
  margin-bottom: 24px;
  display: inline-block;
  animation: bounce 2s ease-in-out infinite;
}

@keyframes bounce {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.step-title {
  font-size: 32px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 16px;
}

.step-description {
  font-size: 16px;
  color: #64748b;
  line-height: 1.8;
  margin: 0 0 32px;
  max-width: 700px;
  margin-left: auto;
  margin-right: auto;
}

.step-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
  margin-top: 32px;
}

/* 示例框 */
.example-box,
.tips-box,
.features-box {
  background: rgba(59, 130, 246, 0.05);
  border-radius: 16px;
  padding: 24px;
  margin: 32px 0;
  text-align: left;
  border: 1px solid rgba(59, 130, 246, 0.1);
}

.example-box h3,
.tips-box h3,
.features-box h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 16px;
}

.example-text {
  font-size: 14px;
  color: #475569;
  line-height: 1.8;
  background: rgba(255, 255, 255, 0.8);
  padding: 16px;
  border-radius: 8px;
  border-left: 3px solid #3b82f6;
}

.tips-box ul {
  margin: 0;
  padding-left: 24px;
  color: #475569;
  line-height: 2;
}

.tips-box li {
  margin-bottom: 8px;
}

/* 功能网格 */
.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 16px;
  margin-top: 16px;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  font-size: 14px;
  color: #475569;
  font-weight: 500;
}

.feature-icon {
  font-size: 32px;
}

/* 快捷操作区域 */
.quick-actions-section {
  margin-top: 80px;
}

.section-title {
  font-size: 32px;
  font-weight: 600;
  color: #1e293b;
  text-align: center;
  margin-bottom: 40px;
}

.quick-actions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
}

.quick-action-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 32px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.quick-action-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 40px rgba(59, 130, 246, 0.2);
  border-color: rgba(59, 130, 246, 0.3);
}

.action-icon {
  font-size: 48px;
  margin-bottom: 16px;
  display: inline-block;
}

.quick-action-card h3 {
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 8px;
}

.quick-action-card p {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-title {
    font-size: 36px;
  }

  .page-subtitle {
    font-size: 16px;
  }

  .step-card {
    padding: 32px 24px;
  }

  .step-title {
    font-size: 24px;
  }

  .step-description {
    font-size: 15px;
  }

  .step-actions {
    flex-direction: column;
  }

  .step-actions .ant-btn {
    width: 100%;
  }

  .quick-actions-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .page-title {
    font-size: 28px;
  }

  .step-card {
    padding: 24px 20px;
  }

  .step-icon {
    font-size: 48px;
  }

  .step-title {
    font-size: 20px;
  }
}
</style>
