<template>
  <div class="help-docs-container">
    <!-- 渐变背景 -->
    <div class="gradient-bg">
      <div class="gradient-orb orb-1"></div>
      <div class="gradient-orb orb-2"></div>
      <div class="gradient-orb orb-3"></div>
    </div>

    <!-- Hero 区域 -->
    <div class="hero-section">
      <div class="hero-content">
        <div class="hero-mark">
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 6.253V13.5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M12 2C6.477 2 2 6.477 2 12C2 17.523 6.477 22 12 22C17.523 22 22 17.523 22 12C22 6.477 17.523 2 12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M12 17V17.01" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <h1 class="hero-title">帮助文档</h1>
        <p class="hero-subtitle">探索蓝海智造的无限可能</p>
      </div>
    </div>

    <!-- 文档内容 -->
    <div class="docs-wrapper">
      <!-- 侧边导航 -->
      <div class="docs-sidebar">
        <div class="sidebar-card">
          <h3 class="sidebar-title">目录导航</h3>
          <nav class="sidebar-nav">
            <a href="#introduction" class="nav-item">平台简介</a>
            <a href="#quickstart" class="nav-item">快速开始</a>
            <a href="#create" class="nav-item">创建应用</a>
            <a href="#edit" class="nav-item">编辑优化</a>
            <a href="#faq" class="nav-item">常见问题</a>
          </nav>
        </div>
      </div>

      <!-- 主内容区 -->
      <div class="docs-main">
        <!-- 平台简介 -->
        <section id="introduction" class="docs-section">
          <div class="section-header">
            <span class="section-number">01</span>
            <h2 class="section-title">平台简介</h2>
          </div>
          <div class="section-content">
            <div class="intro-card">
              <p>
                欢迎使用<strong>蓝海智造</strong>！这是一个应用生成平台，让您能够通过简单的自然语言描述，快速创建完整的 Web 应用程序。
              </p>
            </div>

            <div class="features-grid">
              <div class="feature-item feature-large">
                <div class="feature-icon">
                  <RocketOutlined />
                </div>
                <h3>快速生成</h3>
                <p>只需描述您的需求，系统即可在几分钟内生成完整的应用代码</p>
              </div>
              <div class="feature-item">
                <div class="feature-icon">
                  <MessageOutlined />
                </div>
                <h3>对话优化</h3>
                <p>通过自然语言对话，持续优化和完善您的应用功能</p>
              </div>
              <div class="feature-item">
                <div class="feature-icon">
                  <PictureOutlined />
                </div>
                <h3>精美界面</h3>
                <p>自动生成美观、响应式的现代化用户界面</p>
              </div>
              <div class="feature-item feature-wide">
                <div class="feature-icon">
                  <ThunderboltOutlined />
                </div>
                <h3>即时部署</h3>
                <p>生成的应用可直接部署，无需额外配置，快速上线</p>
              </div>
            </div>
          </div>
        </section>

        <!-- 快速开始 -->
        <section id="quickstart" class="docs-section">
          <div class="section-header">
            <span class="section-number">02</span>
            <h2 class="section-title">快速开始</h2>
          </div>
          <div class="section-content">
            <div class="steps-list">
              <div class="step-item">
                <div class="step-badge">1</div>
                <div class="step-info">
                  <h3>注册账号</h3>
                  <p>点击右上角登录按钮，选择注册选项，填写信息完成注册。首次注册可获得积分奖励！</p>
                </div>
              </div>
              <div class="step-item">
                <div class="step-badge">2</div>
                <div class="step-info">
                  <h3>创建应用</h3>
                  <p>在首页输入框中详细描述您的应用需求，例如："创建一个现代化的个人博客网站，包含文章列表、详情页、分类标签和搜索功能。"</p>
                </div>
              </div>
              <div class="step-item">
                <div class="step-badge">3</div>
                <div class="step-info">
                  <h3>AI 对话优化</h3>
                  <p>应用创建后，进入对话页面，通过自然语言与 AI 交流，要求修改样式、添加功能或优化代码。</p>
                </div>
              </div>
              <div class="step-item">
                <div class="step-badge">4</div>
                <div class="step-info">
                  <h3>部署分享</h3>
                  <p>完善应用后，点击"查看作品"按钮访问您的应用，并可以分享链接给他人使用。</p>
                </div>
              </div>
              <div class="step-item">
                <div class="step-badge">5</div>
                <div class="step-info">
                  <h3>下载代码</h3>
                  <p>可以自由下载生成的代码，并自行部署到服务器等等。</p>
                </div>
              </div>
            </div>

            <div class="tip-card">
              <div class="tip-icon">
                <BulbOutlined />
              </div>
              <div class="tip-content">
                <h4>使用建议</h4>
                <p>描述越详细，生成的应用越符合您的需求。建议包含功能需求、设计风格、页面结构等信息。</p>
              </div>
            </div>
          </div>
        </section>

        <!-- 创建应用 -->
        <section id="create" class="docs-section">
          <div class="section-header">
            <span class="section-number">03</span>
            <h2 class="section-title">创建应用</h2>
          </div>
          <div class="section-content">
            <p class="description-text">创建应用是使用蓝海智造的第一步。以下是创建优质应用的关键要素：</p>

            <div class="guide-grid">
              <div class="guide-card">
                <div class="guide-number">1</div>
                <h3>应用类型</h3>
                <p>明确说明您要创建什么类型的应用</p>
                <div class="guide-tags">
                  <span class="tag">个人博客</span>
                  <span class="tag">企业官网</span>
                  <span class="tag">在线商城</span>
                </div>
              </div>
              <div class="guide-card">
                <div class="guide-number">2</div>
                <h3>核心功能</h3>
                <p>列出应用需要的主要功能</p>
                <div class="guide-tags">
                  <span class="tag">用户系统</span>
                  <span class="tag">商品展示</span>
                  <span class="tag">购物车</span>
                </div>
              </div>
              <div class="guide-card">
                <div class="guide-number">3</div>
                <h3>设计风格</h3>
                <p>描述您期望的设计风格</p>
                <div class="guide-tags">
                  <span class="tag">简洁现代</span>
                  <span class="tag">商务专业</span>
                  <span class="tag">创意艺术</span>
                </div>
              </div>
              <div class="guide-card">
                <div class="guide-number">4</div>
                <h3>页面结构</h3>
                <p>说明应用需要哪些页面</p>
                <div class="guide-tags">
                  <span class="tag">首页轮播</span>
                  <span class="tag">产品列表</span>
                  <span class="tag">详情页面</span>
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- 编辑优化 -->
        <section id="edit" class="docs-section">
          <div class="section-header">
            <span class="section-number">04</span>
            <h2 class="section-title">编辑与优化</h2>
          </div>
          <div class="section-content">
            <p class="description-text">创建应用后，您可以通过与 AI 对话来编辑和优化您的应用。</p>

            <div class="scenarios-grid">
              <div class="scenario-card">
                <h4><BgColorsOutlined /> 样式调整</h4>
                <p>修改颜色、字体、间距、边框等视觉元素</p>
                <div class="scenario-examples">
                  <span>"把首页背景改成橙色"</span>
                  <span>"调整按钮圆角大小"</span>
                </div>
              </div>
              <div class="scenario-card">
                <h4><MobileOutlined /> 响应式优化</h4>
                <p>优化移动端显示效果，确保在不同设备上正常显示</p>
                <div class="scenario-examples">
                  <span>"优化手机端布局"</span>
                  <span>"调整平板显示"</span>
                </div>
              </div>
              <div class="scenario-card">
                <h4><ThunderboltOutlined /> 性能优化</h4>
                <p>优化代码性能，提升页面加载速度</p>
                <div class="scenario-examples">
                  <span>"使用懒加载"</span>
                  <span>"优化渲染性能"</span>
                </div>
              </div>
              <div class="scenario-card">
                <h4><ToolOutlined /> 功能增强</h4>
                <p>添加新功能或改进现有功能</p>
                <div class="scenario-examples">
                  <span>"添加搜索功能"</span>
                  <span>"实现评论系统"</span>
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- 常见问题 -->
        <section id="faq" class="docs-section">
          <div class="section-header">
            <span class="section-number">05</span>
            <h2 class="section-title">常见问题</h2>
          </div>
          <div class="section-content">
            <div class="faq-list">
              <div v-for="(item, index) in faqs" :key="index" class="faq-item">
                <h4 class="faq-question">{{ item.q }}</h4>
                <p class="faq-answer">{{ item.a }}</p>
              </div>
            </div>

            <div class="contact-card">
              <h4>还有问题？</h4>
              <p>如果您的问题在这里找不到答案，欢迎访问我们的 Gitee 仓库提交 Issue。</p>
              <a href="https://www.gitee.com/hhzalh" target="_blank" class="contact-link">
                访问 Gitee 仓库 →
              </a>
            </div>
          </div>
        </section>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import {
  RocketOutlined,
  MessageOutlined,
  PictureOutlined,
  ThunderboltOutlined,
  BulbOutlined,
  BgColorsOutlined,
  MobileOutlined,
  ToolOutlined,
} from '@ant-design/icons-vue'

const route = useRoute()

const faqs = ref([
  {
    q: '如何创建应用？',
    a: '在首页输入框中详细描述您想要创建的应用，然后点击"创建应用"按钮即可。系统会自动生成应用代码。',
  },
  {
    q: '应用生成需要多长时间？',
    a: '应用生成时间取决于应用的复杂度，通常在几秒到几分钟之间。简单应用可能只需几秒，复杂应用可能需要几分钟。',
  },
  {
    q: '如何修改已创建的应用？',
    a: '在应用对话页面，您可以直接与 AI 对话，描述您想要修改的内容。AI 会理解您的需求并修改代码。',
  },
  {
    q: '可以部署到自己的服务器吗？',
    a: '可以。系统会生成完整的应用代码，您可以下载代码并部署到任何支持的前端服务器上。',
  },
  {
    q: '支持哪些类型的应用？',
    a: '目前主要支持前端 Web 应用，包括博客、官网、商城、展示网站等。未来会支持更多类型。',
  },
  {
    q: '生成的应用代码质量如何？',
    a: 'AI 生成的应用代码遵循现代前端开发最佳实践，代码结构清晰，易于维护和扩展。',
  },
])

onMounted(() => {
  // 如果 URL 中有锚点，滚动到对应位置
  if (route.hash) {
    setTimeout(() => {
      const element = document.querySelector(route.hash)
      if (element) {
        element.scrollIntoView({ behavior: 'smooth', block: 'start' })
      }
    }, 100)
  }
})
</script>

<style>
/* ========== 全局变量（非scoped） ========== */
:root {
  --color-primary: #ff6b6b;
  --color-primary-light: #ff8787;
  --color-primary-dark: #fa5252;
  --color-secondary: #ffa8a8;
  --color-accent: #ffec99;
  --color-text: #2d3436;
  --color-text-secondary: #636e72;
  --color-text-light: #b2bec3;
  --color-bg: #fff5f5;
  --color-glass: rgba(255, 255, 255, 0.7);
  --color-glass-border: rgba(255, 255, 255, 0.9);
  --shadow-soft: 0 8px 32px rgba(255, 107, 107, 0.1);
  --shadow-hover: 0 12px 48px rgba(255, 107, 107, 0.15);
  --radius-sm: 8px;
  --radius-md: 16px;
  --radius-lg: 24px;
  --font-main: 'Noto Sans SC', -apple-system, BlinkMacSystemFont, sans-serif;
}
</style>

<style scoped>
/* ========== 字体引入 ========== */
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@300;400;500;600;700&display=swap');

/* ========== 主容器 ========== */
.help-docs-container {
  min-height: 100vh;
  position: relative;
  font-family: var(--font-main);
  background: var(--color-bg);
}

/* ========== 渐变背景 ========== */
.gradient-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  overflow: hidden;
  will-change: transform;
  transform: translateZ(0);
  backface-visibility: hidden;
  perspective: 1000px;
}

.gradient-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.6;
  animation: float 20s ease-in-out infinite;
  will-change: transform;
  transform: translateZ(0);
  backface-visibility: hidden;
}

.orb-1 {
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, var(--color-primary) 0%, transparent 70%);
  top: -200px;
  right: -100px;
  animation-delay: 0s;
}

.orb-2 {
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, var(--color-secondary) 0%, transparent 70%);
  bottom: -150px;
  left: -100px;
  animation-delay: 7s;
}

.orb-3 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, var(--color-accent) 0%, transparent 70%);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation-delay: 14s;
}

@keyframes float {
  0%,
  100% {
    transform: translate3d(0, 0, 0) scale(1);
  }
  33% {
    transform: translate3d(30px, -30px, 0) scale(1.1);
  }
  66% {
    transform: translate3d(-20px, 20px, 0) scale(0.9);
  }
}

/* ========== Hero 区域 ========== */
.hero-section {
  position: relative;
  z-index: 1;
  padding: 80px 20px 60px;
  text-align: center;
}

.hero-content {
  max-width: 800px;
  margin: 0 auto;
}

.hero-mark {
  width: 80px;
  height: 80px;
  margin: 0 auto 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--color-primary), var(--color-secondary));
  border-radius: var(--radius-lg);
  color: white;
  box-shadow: var(--shadow-soft);
  transform: translateZ(0);
  backface-visibility: hidden;
}

.hero-mark svg {
  width: 40px;
  height: 40px;
}

.hero-title {
  font-size: 48px;
  font-weight: 700;
  color: var(--color-text);
  margin: 0 0 16px 0;
  letter-spacing: -1px;
}

.hero-subtitle {
  font-size: 18px;
  color: var(--color-text-secondary);
  margin: 0;
}

/* ========== 文档包装器 ========== */
.docs-wrapper {
  position: relative;
  z-index: 1;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px 80px;
  display: grid;
  grid-template-columns: 260px 1fr;
  gap: 40px;
  transform: translateZ(0);
  backface-visibility: hidden;
}

/* ========== 侧边栏 ========== */
.docs-sidebar {
  position: sticky;
  top: 80px;
  height: fit-content;
}

.sidebar-card {
  background: var(--color-glass);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid var(--color-glass-border);
  border-radius: var(--radius-lg);
  padding: 28px 24px;
  box-shadow: var(--shadow-soft);
  transform: translateZ(0);
  backface-visibility: hidden;
}

.sidebar-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0 0 20px 0;
}

.sidebar-nav {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.nav-item {
  display: block;
  padding: 10px 16px;
  color: var(--color-text-secondary);
  text-decoration: none;
  border-radius: var(--radius-sm);
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.nav-item:hover {
  background: rgba(255, 107, 107, 0.1);
  color: var(--color-primary);
  transform: translate3d(4px, 0, 0);
}

/* ========== 主内容区 ========== */
.docs-main {
  display: flex;
  flex-direction: column;
  gap: 60px;
}

.docs-section {
  background: var(--color-glass);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid var(--color-glass-border);
  border-radius: var(--radius-lg);
  padding: 40px;
  box-shadow: var(--shadow-soft);
  scroll-margin-top: 80px;
  transform: translateZ(0);
  backface-visibility: hidden;
  will-change: transform, opacity;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 32px;
}

.section-number {
  font-size: 48px;
  font-weight: 700;
  color: rgba(255, 107, 107, 0.2);
  line-height: 1;
}

.section-title {
  font-size: 32px;
  font-weight: 700;
  color: var(--color-text);
  margin: 0;
}

.section-content {
  color: var(--color-text-secondary);
  line-height: 1.8;
}

/* ========== 简介卡片 ========== */
.intro-card {
  background: rgba(255, 107, 107, 0.05);
  border-left: 4px solid var(--color-primary);
  border-radius: var(--radius-md);
  padding: 24px;
  margin-bottom: 32px;
}

.intro-card p {
  font-size: 16px;
  margin: 0;
  color: var(--color-text);
}

.description-text {
  font-size: 16px;
  margin: 0 0 24px 0;
}

/* ========== 特性网格（不对称布局） ========== */
.features-grid {
  display: grid;
  grid-template-columns: repeat(12, 1fr);
  gap: 24px;
}

.feature-item {
  background: rgba(255, 255, 255, 0.5);
  border: 1px solid rgba(255, 107, 107, 0.2);
  border-radius: var(--radius-md);
  padding: 28px;
  text-align: center;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

/* 大型卡片 - 跨8列 */
.feature-large {
  grid-column: span 8;
}

/* 宽卡片 - 跨8列 */
.feature-wide {
  grid-column: span 8;
}

/* 小卡片 - 跨4列 */
.feature-item:not(.feature-large):not(.feature-wide) {
  grid-column: span 4;
}

/* 顶部装饰线 */
.feature-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, var(--color-primary), var(--color-primary-light));
  opacity: 0;
  transition: opacity 0.3s ease;
}

.feature-item:hover::before {
  opacity: 1;
}

.feature-item:hover {
  transform: translate3d(0, -4px, 0);
  box-shadow: var(--shadow-hover);
  border-color: rgba(255, 107, 107, 0.3);
}

.feature-icon {
  width: 64px;
  height: 64px;
  margin: 0 auto 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(255, 107, 107, 0.1) 0%, rgba(255, 255, 255, 0.5) 100%);
  border-radius: 50%;
  color: var(--color-primary);
  font-size: 28px;
}

.feature-item h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0 0 12px 0;
}

.feature-item p {
  font-size: 14px;
  margin: 0;
}

/* ========== 步骤列表 ========== */
.steps-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
  margin-bottom: 32px;
}

.step-item {
  display: flex;
  gap: 20px;
  background: rgba(255, 255, 255, 0.5);
  border: 1px solid rgba(255, 107, 107, 0.2);
  border-radius: var(--radius-md);
  padding: 24px;
  transition: all 0.3s ease;
}

.step-item:hover {
  border-color: rgba(255, 107, 107, 0.3);
  box-shadow: var(--shadow-soft);
}

.step-badge {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--color-primary), var(--color-primary-dark));
  color: white;
  font-size: 20px;
  font-weight: 700;
  border-radius: 50%;
  flex-shrink: 0;
}

.step-info {
  flex: 1;
}

.step-info h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0 0 12px 0;
}

.step-info p {
  font-size: 14px;
  margin: 0;
  line-height: 1.7;
}

/* ========== 提示卡片 ========== */
.tip-card {
  display: flex;
  gap: 16px;
  background: rgba(255, 236, 153, 0.15);
  border: 1px solid rgba(255, 236, 153, 0.3);
  border-radius: var(--radius-md);
  padding: 24px;
}

.tip-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--color-accent) 0%, #f59e0b 100%);
  border-radius: 50%;
  color: white;
  font-size: 20px;
  flex-shrink: 0;
}

.tip-content h4 {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0 0 8px 0;
}

.tip-content p {
  font-size: 14px;
  margin: 0;
}

/* ========== 指南网格 ========== */
.guide-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.guide-card {
  background: rgba(255, 255, 255, 0.5);
  border: 1px solid rgba(255, 107, 107, 0.2);
  border-radius: var(--radius-md);
  padding: 24px;
  transition: all 0.3s ease;
}

.guide-card:hover {
  border-color: rgba(255, 107, 107, 0.3);
  box-shadow: var(--shadow-soft);
}

.guide-number {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--color-primary), var(--color-primary-dark));
  color: white;
  font-size: 18px;
  font-weight: 700;
  border-radius: 50%;
  margin-bottom: 16px;
}

.guide-card h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0 0 12px 0;
}

.guide-card > p {
  font-size: 14px;
  margin: 0 0 16px 0;
}

.guide-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag {
  padding: 6px 12px;
  background: rgba(255, 107, 107, 0.1);
  border: 1px solid rgba(255, 107, 107, 0.2);
  border-radius: 6px;
  font-size: 13px;
  color: var(--color-primary-dark);
  font-weight: 500;
}

/* ========== 场景网格 ========== */
.scenarios-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.scenario-card {
  background: rgba(255, 255, 255, 0.5);
  border: 1px solid rgba(255, 107, 107, 0.2);
  border-radius: var(--radius-md);
  padding: 24px;
}

.scenario-card h4 {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0 0 12px 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.scenario-card h4 .anticon {
  font-size: 18px;
  color: var(--color-primary);
}

.scenario-card > p {
  font-size: 14px;
  margin: 0 0 16px 0;
}

.scenario-examples {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.scenario-examples span {
  padding: 8px 12px;
  background: rgba(255, 107, 107, 0.05);
  border-radius: 6px;
  font-size: 13px;
  color: var(--color-text);
  font-family: 'Consolas', monospace;
}

/* ========== FAQ ========== */
.faq-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 32px;
}

.faq-item {
  background: rgba(255, 255, 255, 0.5);
  border: 1px solid rgba(255, 107, 107, 0.2);
  border-radius: var(--radius-md);
  padding: 20px;
  transition: all 0.3s ease;
}

.faq-item:hover {
  border-color: rgba(255, 107, 107, 0.3);
  box-shadow: var(--shadow-soft);
  transform: translate3d(4px, 0, 0);
}

.faq-question {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0 0 12px 0;
}

.faq-answer {
  font-size: 14px;
  margin: 0;
  line-height: 1.7;
}

/* ========== 联系卡片 ========== */
.contact-card {
  background: rgba(255, 107, 107, 0.05);
  border: 1px solid rgba(255, 107, 107, 0.2);
  border-radius: var(--radius-md);
  padding: 24px;
  text-align: center;
}

.contact-card h4 {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0 0 12px 0;
}

.contact-card > p {
  font-size: 14px;
  margin: 0 0 20px 0;
}

.contact-link {
  display: inline-block;
  padding: 12px 24px;
  background: linear-gradient(135deg, var(--color-primary), var(--color-primary-dark));
  color: white;
  text-decoration: none;
  border-radius: var(--radius-sm);
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.contact-link:hover {
  box-shadow: var(--shadow-hover);
  transform: translate3d(0, -2px, 0);
}

/* ========== 响应式设计 ========== */
@media (max-width: 1024px) {
  .docs-wrapper {
    grid-template-columns: 1fr;
  }

  .docs-sidebar {
    position: static;
  }

  .features-grid,
  .guide-grid,
  .scenarios-grid {
    grid-template-columns: 1fr;
  }

  .features-grid .feature-item {
    grid-column: span 12 !important;
  }
}

@media (max-width: 640px) {
  .hero-title {
    font-size: 36px;
  }

  .hero-subtitle {
    font-size: 16px;
  }

  .docs-section {
    padding: 28px 20px;
  }

  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .section-number {
    font-size: 36px;
  }

  .section-title {
    font-size: 24px;
  }

  .step-item {
    flex-direction: column;
    text-align: center;
  }

  .step-badge {
    align-self: center;
  }

  .tip-card {
    flex-direction: column;
    text-align: center;
  }

  .tip-icon {
    align-self: center;
  }
}
</style>
