<template>
  <div class="quick-start-page">
    <!-- Hero Section with Image Background -->
    <div class="hero-section">
      <div class="hero-overlay"></div>
      <div class="container">
        <div class="hero-content">
          <div class="hero-badge">
            <ClockCircleOutlined />
            <span>5分钟上手</span>
          </div>
          <h1 class="hero-title">快速开始您的<br />AI应用开发之旅</h1>
          <p class="hero-subtitle">
            无需编程基础，通过自然语言对话，快速创建、优化并部署您的专属应用
          </p>
          <div class="hero-stats">
            <div class="stat-item">
              <div class="stat-number">4</div>
              <div class="stat-label">简单步骤</div>
            </div>
            <div class="stat-divider"></div>
            <div class="stat-item">
              <div class="stat-number">0</div>
              <div class="stat-label">代码要求</div>
            </div>
            <div class="stat-divider"></div>
            <div class="stat-item">
              <div class="stat-number">∞</div>
              <div class="stat-label">创作可能</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Main Content -->
    <div class="main-content">
      <div class="container">
        <!-- Steps Timeline -->
        <div class="timeline-section">
          <div class="section-header">
            <span class="section-number">01</span>
            <div class="section-title-block">
              <h2 class="section-title">开发流程</h2>
              <p class="section-subtitle">四步完成从想法到部署</p>
            </div>
          </div>

          <div class="timeline-grid">
            <!-- Step 1 -->
            <div class="timeline-card step-1" @click="currentStep = 0">
              <div class="card-header">
                <div class="step-number">01</div>
                <UserOutlined class="step-icon" />
              </div>
              <div class="card-content">
                <h3>注册/登录账号</h3>
                <p>创建您的专属账号或直接登录已有账号，开启创作之旅</p>
              </div>
              <div class="card-actions">
                <a-button size="large" type="primary" @click.stop="goToRegister">
                  <template #icon>
                    <UserAddOutlined />
                  </template>
                  立即注册
                </a-button>
                <a-button size="large" @click.stop="goToLogin">
                  <template #icon>
                    <LoginOutlined />
                  </template>
                  已有账号登录
                </a-button>
              </div>
            </div>

            <!-- Step 2 -->
            <div class="timeline-card step-2" @click="currentStep = 1">
              <div class="card-header">
                <div class="step-number">02</div>
                <MessageOutlined class="step-icon" />
              </div>
              <div class="card-content">
                <h3>描述您的应用</h3>
                <p>用自然语言详细描述您想要的应用功能，AI将为您生成初始代码</p>
              </div>
              <div class="card-example">
                <div class="example-label">
                  <FileTextOutlined />
                  示例描述
                </div>
                <div class="example-text">
                  "创建一个现代化的个人博客网站，包含文章列表、详情页、分类标签、搜索功能、评论系统和个人简介页面。采用简洁的设计风格，支持响应式布局。"
                </div>
              </div>
              <div class="card-actions">
                <a-button size="large" type="primary" @click.stop="goToHome">
                  <template #icon>
                    <HomeOutlined />
                  </template>
                  前往首页创建
                </a-button>
              </div>
            </div>

            <!-- Step 3 -->
            <div class="timeline-card step-3" @click="currentStep = 2">
              <div class="card-header">
                <div class="step-number">03</div>
                <CommentOutlined class="step-icon" />
              </div>
              <div class="card-content">
                <h3>与AI对话完善</h3>
                <p>通过多轮对话持续优化应用，调整样式、添加功能、优化性能</p>
              </div>
              <div class="tips-list">
                <div class="tip-item">
                  <BgColorsOutlined />
                  <span>修改应用样式和布局</span>
                </div>
                <div class="tip-item">
                  <PlusCircleOutlined />
                  <span>添加新功能和页面</span>
                </div>
                <div class="tip-item">
                  <ThunderboltOutlined />
                  <span>优化代码和性能</span>
                </div>
                <div class="tip-item">
                  <BulbOutlined />
                  <span>解释代码逻辑</span>
                </div>
              </div>
            </div>

            <!-- Step 4 -->
            <div class="timeline-card step-4" @click="currentStep = 3">
              <div class="card-header">
                <div class="step-number">04</div>
                <RocketOutlined class="step-icon" />
              </div>
              <div class="card-content">
                <h3>部署和分享</h3>
                <p>一键部署您的应用，获得专属链接，随时随地分享您的作品</p>
              </div>
              <div class="features-preview">
                <div class="feature-tag">
                  <MobileOutlined />
                  响应式
                </div>
                <div class="feature-tag">
                  <PictureOutlined />
                  现代化UI
                </div>
                <div class="feature-tag">
                  <SafetyCertificateOutlined />
                  安全可靠
                </div>
              </div>
              <div class="card-actions">
                <a-button size="large" type="primary" @click.stop="goToHome">
                  <template #icon>
                    <RocketOutlined />
                  </template>
                  开始创建应用
                </a-button>
              </div>
            </div>
          </div>
        </div>

        <!-- Quick Actions -->
        <div class="actions-section">
          <div class="section-header">
            <span class="section-number">02</span>
            <div class="section-title-block">
              <h2 class="section-title">快捷操作</h2>
              <p class="section-subtitle">立即开始您的创作</p>
            </div>
          </div>

          <div class="actions-grid">
            <div class="action-card action-primary" @click="goToHome">
              <div class="action-background">
                <HomeOutlined />
              </div>
              <div class="action-content">
                <h3>返回首页</h3>
                <p>开始创建您的第一个应用</p>
                <div class="action-arrow">
                  <ArrowRightOutlined />
                </div>
              </div>
            </div>

            <div class="action-card action-secondary" @click="goToLogin">
              <div class="action-background">
                <LoginOutlined />
              </div>
              <div class="action-content">
                <h3>用户登录</h3>
                <p>登录您的账号</p>
                <div class="action-arrow">
                  <ArrowRightOutlined />
                </div>
              </div>
            </div>

            <div class="action-card action-tertiary" @click="goToRegister">
              <div class="action-background">
                <UserAddOutlined />
              </div>
              <div class="action-content">
                <h3>用户注册</h3>
                <p>创建新账号</p>
                <div class="action-arrow">
                  <ArrowRightOutlined />
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- FAQ Section -->
        <div class="faq-section">
          <div class="section-header">
            <span class="section-number">03</span>
            <div class="section-title-block">
              <h2 class="section-title">常见问题</h2>
              <p class="section-subtitle">快速解答您的疑问</p>
            </div>
          </div>

          <a-collapse v-model:activeKey="activeFaq" class="faq-collapse">
            <a-collapse-panel key="1">
              <template #header>
                <div class="faq-header">
                  <QuestionCircleOutlined />
                  <span>需要编程基础吗？</span>
                </div>
              </template>
              <div class="faq-content">
                完全不需要！您只需要用自然语言描述您的需求，AI会自动生成代码。如果您想进一步定制，也可以通过与AI对话来调整应用。
              </div>
            </a-collapse-panel>

            <a-collapse-panel key="2">
              <template #header>
                <div class="faq-header">
                  <QuestionCircleOutlined />
                  <span>生成的代码可以下载吗？</span>
                </div>
              </template>
              <div class="faq-content">
                当然可以！您可以在应用编辑页面点击"下载代码"按钮，获取完整的项目代码。下载需要消耗积分，但首次下载免费。
              </div>
            </a-collapse-panel>

            <a-collapse-panel key="3">
              <template #header>
                <div class="faq-header">
                  <QuestionCircleOutlined />
                  <span>如何获取更多积分？</span>
                </div>
              </template>
              <div class="faq-content">
                系统提供多种获取积分的方式：每日签到、邀请好友注册、完善个人资料等。您也可以在积分商城查看更多获取方式。
              </div>
            </a-collapse-panel>

            <a-collapse-panel key="4">
              <template #header>
                <div class="faq-header">
                  <QuestionCircleOutlined />
                  <span>应用部署后可以修改吗？</span>
                </div>
              </template>
              <div class="faq-content">
                可以！您随时可以进入应用编辑页面，继续与AI对话来优化应用。每次修改后，系统会自动更新部署版本。
              </div>
            </a-collapse-panel>
          </a-collapse>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  ArrowRightOutlined,
  BgColorsOutlined,
  BulbOutlined,
  ClockCircleOutlined,
  CommentOutlined,
  FileTextOutlined,
  HomeOutlined,
  LoginOutlined,
  MessageOutlined,
  MobileOutlined,
  PictureOutlined,
  PlusCircleOutlined,
  QuestionCircleOutlined,
  RocketOutlined,
  SafetyCertificateOutlined,
  ThunderboltOutlined,
  UserAddOutlined,
  UserOutlined,
} from '@ant-design/icons-vue'

const router = useRouter()
const currentStep = ref(0)
const activeFaq = ref<string[]>(['1'])

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
@import url('https://fonts.googleapis.com/css2?family=Noto+Serif+SC:wght@400;600;700&family=Noto+Sans+SC:wght@300;400;500;600&display=swap');

:root {
  --color-primary: #f97316;
  --color-primary-dark: #ea580c;
  --color-primary-light: #fbbf24;
  --color-text: #1e293b;
  --color-text-secondary: #64748b;
  --color-bg: #ffffff;
  --font-serif: 'Noto Serif SC', serif;
  --font-sans: 'Noto Sans SC', sans-serif;
}

.quick-start-page {
  min-height: 100vh;
  background: #f8fafc;
}

/* Hero Section */
.hero-section {
  position: relative;
  background-image: url('https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=1920&q=80');
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
  padding: 100px 0 120px;
  overflow: hidden;
}

.hero-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    135deg,
    rgba(249, 115, 22, 0.92) 0%,
    rgba(234, 88, 12, 0.88) 50%,
    rgba(251, 191, 36, 0.85) 100%
  );
}

.hero-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  backdrop-filter: blur(2px);
  -webkit-backdrop-filter: blur(2px);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 16px;
  position: relative;
  z-index: 1;
}

.hero-content {
  text-align: center;
  color: white;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  padding: 8px 20px;
  border-radius: 30px;
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 32px;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.hero-title {
  font-family: var(--font-serif);
  font-size: 56px;
  font-weight: 700;
  line-height: 1.3;
  margin: 0 0 24px;
  text-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
}

.hero-subtitle {
  font-family: var(--font-sans);
  font-size: 18px;
  font-weight: 400;
  line-height: 1.8;
  max-width: 700px;
  margin: 0 auto 48px;
  opacity: 0.95;
}

.hero-stats {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 32px;
  flex-wrap: wrap;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-family: var(--font-serif);
  font-size: 48px;
  font-weight: 700;
  line-height: 1;
  margin-bottom: 8px;
}

.stat-label {
  font-family: var(--font-sans);
  font-size: 14px;
  font-weight: 500;
  opacity: 0.9;
}

.stat-divider {
  width: 1px;
  height: 32px;
  background: rgba(255, 255, 255, 0.3);
}

/* Main Content */
.main-content {
  padding: 80px 0;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-bottom: 48px;
}

.section-number {
  font-family: var(--font-serif);
  font-size: 72px;
  font-weight: 700;
  color: var(--color-primary);
  opacity: 0.2;
  line-height: 1;
  -webkit-text-stroke: 2px var(--color-primary);
}

.section-title-block {
  flex: 1;
}

.section-title {
  font-family: var(--font-serif);
  font-size: 36px;
  font-weight: 700;
  color: var(--color-text);
  margin: 0 0 8px;
}

.section-subtitle {
  font-family: var(--font-sans);
  font-size: 16px;
  color: var(--color-text-secondary);
  margin: 0;
}

/* Timeline Grid */
.timeline-section {
  margin-bottom: 100px;
}

.timeline-grid {
  display: grid;
  grid-template-columns: repeat(12, 1fr);
  gap: 24px;
}

.timeline-card {
  background: white;
  border-radius: 20px;
  padding: 32px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(249, 115, 22, 0.1);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.timeline-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, var(--color-primary), var(--color-primary-light));
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.timeline-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(249, 115, 22, 0.15);
  border-color: rgba(249, 115, 22, 0.3);
}

.timeline-card:hover::before {
  transform: scaleX(1);
}

.step-1 {
  grid-column: span 12;
}

.step-2 {
  grid-column: span 12;
}

.step-3 {
  grid-column: span 12;
}

.step-4 {
  grid-column: span 12;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.step-number {
  font-family: var(--font-serif);
  font-size: 48px;
  font-weight: 700;
  color: var(--color-primary);
  opacity: 0.15;
  line-height: 1;
}

.step-icon {
  font-size: 32px;
  color: var(--color-primary);
}

.card-content h3 {
  font-family: var(--font-serif);
  font-size: 24px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0 0 12px;
}

.card-content p {
  font-family: var(--font-sans);
  font-size: 13px;
  line-height: 1.8;
  color: var(--color-text-secondary);
  margin: 0;
}

.card-example {
  background: rgba(249, 115, 22, 0.05);
  border-radius: 12px;
  padding: 20px;
  margin: 24px 0;
  border: 1px solid rgba(249, 115, 22, 0.1);
}

.example-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: var(--color-primary);
  margin-bottom: 12px;
  font-size: 14px;
}

.example-text {
  font-family: var(--font-sans);
  font-size: 14px;
  line-height: 1.8;
  color: var(--color-text);
  background: white;
  padding: 16px;
  border-radius: 8px;
  border-left: 3px solid var(--color-primary);
}

.tips-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin: 24px 0;
}

.tip-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: rgba(249, 115, 22, 0.05);
  border-radius: 8px;
  font-size: 14px;
  color: var(--color-text);
  font-weight: 500;
  transition: all 0.2s ease;
}

.tip-item:hover {
  background: rgba(249, 115, 22, 0.1);
  transform: translateX(4px);
}

.tip-item :deep(.anticon) {
  font-size: 18px;
  color: var(--color-primary);
}

.features-preview {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin: 24px 0;
}

.feature-tag {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: rgba(249, 115, 22, 0.1);
  border-radius: 20px;
  font-size: 13px;
  color: var(--color-primary);
  font-weight: 600;
}

.card-actions {
  display: flex;
  gap: 12px;
  margin-top: 24px;
  flex-wrap: wrap;
}

/* Actions Section */
.actions-section {
  margin-bottom: 100px;
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(12, 1fr);
  gap: 24px;
}

.action-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(249, 115, 22, 0.1);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  position: relative;
}

.action-primary {
  grid-column: span 12;
  display: flex;
  align-items: center;
}

.action-secondary {
  grid-column: span 6;
}

.action-tertiary {
  grid-column: span 6;
}

.action-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 16px 48px rgba(249, 115, 22, 0.2);
}

.action-background {
  padding: 40px;
  background: linear-gradient(135deg, rgba(249, 115, 22, 0.1) 0%, rgba(234, 88, 12, 0.05) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-background :deep(.anticon) {
  font-size: 64px;
  color: var(--color-primary);
  opacity: 0.8;
}

.action-primary .action-background {
  flex: 0 0 200px;
}

.action-secondary .action-background,
.action-tertiary .action-background {
  padding: 32px;
}

.action-secondary .action-background :deep(.anticon),
.action-tertiary .action-background :deep(.anticon) {
  font-size: 48px;
}

.action-content {
  padding: 40px;
  flex: 1;
  position: relative;
}

.action-primary .action-content {
  padding: 40px 60px 40px 40px;
}

.action-content h3 {
  font-family: var(--font-serif);
  font-size: 24px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0 0 8px;
}

.action-content p {
  font-family: var(--font-sans);
  font-size: 14px;
  color: var(--color-text-secondary);
  margin: 0;
}

.action-arrow {
  position: absolute;
  top: 50%;
  right: 24px;
  transform: translateY(-50%);
  font-size: 24px;
  color: var(--color-primary);
  transition: transform 0.3s ease;
}

.action-card:hover .action-arrow {
  transform: translateY(-50%) translateX(8px);
}

/* FAQ Section */
.faq-section {
  margin-bottom: 60px;
}

.faq-collapse {
  background: transparent;
  border: none;
}

.faq-collapse :deep(.ant-collapse-item) {
  background: white;
  border-radius: 16px !important;
  margin-bottom: 16px;
  border: 1px solid rgba(249, 115, 22, 0.1);
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
}

.faq-collapse :deep(.ant-collapse-item:hover) {
  border-color: rgba(249, 115, 22, 0.2);
  box-shadow: 0 4px 20px rgba(249, 115, 22, 0.1);
}

.faq-collapse :deep(.ant-collapse-header) {
  padding: 20px 24px !important;
  background: transparent !important;
  border: none !important;
}

.faq-header {
  display: flex;
  align-items: center;
  gap: 12px;
  font-family: var(--font-serif);
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text);
}

.faq-header :deep(.anticon) {
  font-size: 20px;
  color: var(--color-primary);
}

.faq-collapse :deep(.ant-collapse-content) {
  border-top: 1px solid rgba(249, 115, 22, 0.1) !important;
}

.faq-collapse :deep(.ant-collapse-content-box) {
  padding: 20px 24px !important;
}

.faq-content {
  font-family: var(--font-sans);
  font-size: 13px;
  line-height: 1.8;
  color: var(--color-text-secondary);
}

/* Responsive Design */
@media (min-width: 1024px) {
  .step-1 {
    grid-column: span 6;
  }

  .step-2 {
    grid-column: span 6;
  }

  .step-3 {
    grid-column: span 6;
  }

  .step-4 {
    grid-column: span 6;
  }
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 40px;
  }

  .hero-subtitle {
    font-size: 16px;
  }

  .hero-stats {
    gap: 24px;
  }

  .stat-number {
    font-size: 36px;
  }

  .section-number {
    font-size: 48px;
  }

  .section-title {
    font-size: 28px;
  }

  .action-primary {
    grid-column: span 12;
  }

  .action-secondary {
    grid-column: span 12;
  }

  .action-tertiary {
    grid-column: span 12;
  }

  .action-primary {
    flex-direction: column;
  }

  .action-primary .action-background {
    flex: none;
    width: 100%;
    padding: 32px;
  }

  .action-primary .action-content {
    padding: 32px;
  }

  .action-arrow {
    display: none;
  }
}

@media (max-width: 480px) {
  .hero-title {
    font-size: 32px;
  }

  .hero-subtitle {
    font-size: 13px;
  }

  .section-title {
    font-size: 24px;
  }

  .timeline-card {
    padding: 24px;
  }

  .card-content h3 {
    font-size: 20px;
  }

  .step-number {
    font-size: 36px;
  }

  .step-icon {
    font-size: 24px;
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
