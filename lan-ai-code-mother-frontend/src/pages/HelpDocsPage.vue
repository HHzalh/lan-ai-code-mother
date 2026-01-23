<template>
  <div class="help-docs-page">
    <!-- Hero 区域 - 图片背景 -->
    <div class="docs-hero">
      <div class="hero-overlay">
        <div class="hero-content">
          <span class="hero-badge">帮助文档</span>
          <h1 class="hero-title">探索<br /><span class="highlight">蓝海智造</span></h1>
          <p class="hero-subtitle">AI 驱动的应用生成平台完整指南</p>
        </div>
      </div>
    </div>

    <div class="docs-container">
      <!-- 侧边栏导航 - 磨玻璃效果 -->
      <div class="docs-sidebar">
        <div class="sidebar-content">
          <div class="sidebar-header">
            <h2 class="sidebar-title">目录导航</h2>
            <div class="sidebar-divider"></div>
          </div>
          <a-anchor
            :affix="false"
            :get-container="() => document.querySelector('.docs-main') || window"
            :target-offset="80"
          >
            <a-anchor-link href="#introduction" title="平台简介" />
            <a-anchor-link href="#quickstart" title="快速开始" />
            <a-anchor-link href="#create" title="创建应用" />
            <a-anchor-link href="#edit" title="编辑优化" />
            <a-anchor-link href="#faq" title="常见问题" />
            <a-anchor-link href="#terms" title="服务条款" />
          </a-anchor>
        </div>
      </div>

      <!-- 主内容区域 - 不对称布局 -->
      <div class="docs-main">
        <!-- 简介 - 使用大型不对称布局 -->
        <section id="introduction" class="docs-section">
          <div class="section-header">
            <div class="section-number">01</div>
            <h1 class="section-title">平台简介</h1>
          </div>

          <div class="section-content">
            <div class="intro-block">
              <p class="intro-text">
                欢迎使用 <strong>蓝海智造</strong>！这是一个强大的 AI
                驱动的应用生成平台，让您能够通过简单的自然语言描述，快速创建完整的 Web 应用程序。
              </p>
            </div>

            <h2>什么是蓝海智造？</h2>
            <p class="description-text">
              蓝海智造是一个创新的应用开发平台，它利用先进的人工智能技术，将您的想法转化为可用的 Web
              应用。无论您是开发者、设计师还是创业者，都可以通过我们的平台快速实现您的创意。
            </p>

            <!-- 特性网格 - 不对称布局 -->
            <div class="features-grid">
              <div class="feature-card feature-large">
                <div class="feature-icon-wrapper">
                  <RocketOutlined class="feature-icon" />
                </div>
                <h3>快速生成</h3>
                <p>只需描述您的需求，AI 即可在几分钟内生成完整的应用代码</p>
              </div>

              <div class="feature-card">
                <div class="feature-icon-wrapper">
                  <MessageOutlined class="feature-icon" />
                </div>
                <h3>智能对话</h3>
                <p>通过与 AI 对话，持续优化和完善您的应用功能</p>
              </div>

              <div class="feature-card">
                <div class="feature-icon-wrapper">
                  <BgColorsOutlined class="feature-icon" />
                </div>
                <h3>现代化设计</h3>
                <p>自动生成美观、响应式的现代化用户界面</p>
              </div>

              <div class="feature-card feature-wide">
                <div class="feature-icon-wrapper">
                  <ThunderboltOutlined class="feature-icon" />
                </div>
                <h3>一键部署</h3>
                <p>生成的应用可直接部署，无需额外配置，让您的想法快速落地</p>
              </div>
            </div>

            <!-- 适用场景 - 列表样式 -->
            <div class="scenario-section">
              <h2>适用场景</h2>
              <div class="scenario-list">
                <div class="scenario-item">
                  <span class="scenario-dot"></span>
                  <span>个人博客和作品展示网站</span>
                </div>
                <div class="scenario-item">
                  <span class="scenario-dot"></span>
                  <span>企业官网和产品展示</span>
                </div>
                <div class="scenario-item">
                  <span class="scenario-dot"></span>
                  <span>在线商城和电商平台</span>
                </div>
                <div class="scenario-item">
                  <span class="scenario-dot"></span>
                  <span>管理后台和数据看板</span>
                </div>
                <div class="scenario-item">
                  <span class="scenario-dot"></span>
                  <span>原型设计和概念验证</span>
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- 快速开始 -->
        <section id="quickstart" class="docs-section">
          <div class="section-header">
            <div class="section-number">02</div>
            <h1 class="section-title">快速开始</h1>
          </div>

          <div class="section-content">
            <div class="intro-block">
              <p>
                让我们开始您的第一个应用创建之旅！只需几个简单步骤，您就能拥有一个完整的 Web 应用。
              </p>
            </div>

            <div class="steps-container">
              <div class="step-card">
                <div class="step-header">
                  <div class="step-icon">
                    <UserAddOutlined />
                  </div>
                  <div>
                    <h3>注册账号</h3>
                    <span class="step-number">Step 01</span>
                  </div>
                </div>
                <div class="step-content">
                  <p>如果您还没有账号，请先注册：</p>
                  <ol>
                    <li>点击页面右上角的 <strong>"登录"</strong> 按钮</li>
                    <li>选择 <strong>"注册"</strong> 选项</li>
                    <li>填写用户名、密码等信息</li>
                    <li>完成注册后自动登录</li>
                  </ol>
                </div>
              </div>

              <div class="step-card step-highlight">
                <div class="step-header">
                  <div class="step-icon">
                    <PlusCircleOutlined />
                  </div>
                  <div>
                    <h3>创建应用</h3>
                    <span class="step-number">Step 02</span>
                  </div>
                </div>
                <div class="step-content">
                  <p>登录后，在首页输入您的应用描述：</p>
                  <div class="code-example">
                    <div class="code-header">
                      <CodeOutlined />
                      <span>示例描述</span>
                    </div>
                    <pre><code>创建一个现代化的个人博客网站，包含文章列表、详情页、分类标签、搜索功能、评论系统和个人简介页面。采用简洁的设计风格，支持响应式布局，文章支持Markdown格式，首页展示最新文章和热门推荐。</code></pre>
                  </div>
                </div>
              </div>

              <div class="step-card">
                <div class="step-header">
                  <div class="step-icon">
                    <CommentOutlined />
                  </div>
                  <div>
                    <h3>AI 对话</h3>
                    <span class="step-number">Step 03</span>
                  </div>
                </div>
                <div class="step-content">
                  <p>应用创建后，您将进入对话页面。在这里您可以：</p>
                  <ul>
                    <li>要求 AI 修改应用样式和布局</li>
                    <li>添加新的功能和页面</li>
                    <li>优化代码和性能</li>
                    <li>获取代码解释和技术支持</li>
                  </ul>
                </div>
              </div>

              <div class="step-card step-highlight">
                <div class="step-header">
                  <div class="step-icon">
                    <RocketOutlined />
                  </div>
                  <div>
                    <h3>查看部署</h3>
                    <span class="step-number">Step 04</span>
                  </div>
                </div>
                <div class="step-content">
                  <p>
                    完善应用后，点击
                    <strong>"查看作品"</strong>
                    按钮即可访问您的应用。部署后，您可以直接分享给他人使用。
                  </p>
                </div>
              </div>
            </div>

            <div class="tip-box">
              <div class="tip-icon">
                <BulbOutlined />
              </div>
              <div class="tip-content">
                <strong>专业提示：</strong>
                <p>
                  描述越详细，生成的应用越符合您的需求。建议包含功能需求、设计风格、页面结构等信息。
                </p>
              </div>
            </div>
          </div>
        </section>

        <!-- 创建应用 -->
        <section id="create" class="docs-section">
          <div class="section-header">
            <div class="section-number">03</div>
            <h1 class="section-title">创建应用</h1>
          </div>

          <div class="section-content">
            <p class="description-text">
              创建应用是使用蓝海智造的第一步。本节将详细介绍如何创建您的第一个应用。
            </p>

            <div class="guide-grid">
              <div class="guide-card">
                <div class="guide-header">
                  <span class="guide-number">1</span>
                  <h3>应用类型</h3>
                </div>
                <p>明确说明您要创建什么类型的应用</p>
                <div class="guide-examples">
                  <span class="example-tag">个人博客</span>
                  <span class="example-tag">企业官网</span>
                  <span class="example-tag">在线商城</span>
                  <span class="example-tag">作品展示</span>
                </div>
              </div>

              <div class="guide-card guide-featured">
                <div class="guide-header">
                  <span class="guide-number">2</span>
                  <h3>核心功能</h3>
                </div>
                <p>列出应用需要的主要功能</p>
                <div class="guide-examples">
                  <span class="example-tag">用户系统</span>
                  <span class="example-tag">商品展示</span>
                  <span class="example-tag">购物车</span>
                  <span class="example-tag">支付功能</span>
                </div>
              </div>

              <div class="guide-card">
                <div class="guide-header">
                  <span class="guide-number">3</span>
                  <h3>设计风格</h3>
                </div>
                <p>描述您期望的设计风格</p>
                <div class="guide-examples">
                  <span class="example-tag">简洁现代</span>
                  <span class="example-tag">商务专业</span>
                  <span class="example-tag">创意艺术</span>
                  <span class="example-tag">科技感</span>
                </div>
              </div>

              <div class="guide-card guide-featured">
                <div class="guide-header">
                  <span class="guide-number">4</span>
                  <h3>页面结构</h3>
                </div>
                <p>说明应用需要哪些页面</p>
                <div class="guide-examples">
                  <span class="example-tag">首页轮播</span>
                  <span class="example-tag">产品列表</span>
                  <span class="example-tag">详情页面</span>
                  <span class="example-tag">用户中心</span>
                </div>
              </div>
            </div>

            <div class="shortcut-section">
              <h2>快捷模板</h2>
              <p>首页提供了多个快捷模板，您可以直接点击使用：</p>
              <div class="shortcuts-grid">
                <div class="shortcut-item">
                  <FileTextOutlined />
                  <span>个人博客网站</span>
                </div>
                <div class="shortcut-item">
                  <BuildOutlined />
                  <span>企业官网</span>
                </div>
                <div class="shortcut-item">
                  <ShoppingOutlined />
                  <span>在线商城</span>
                </div>
                <div class="shortcut-item">
                  <PictureOutlined />
                  <span>作品展示网站</span>
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- 编辑应用 -->
        <section id="edit" class="docs-section">
          <div class="section-header">
            <div class="section-number">04</div>
            <h1 class="section-title">编辑与优化</h1>
          </div>

          <div class="section-content">
            <p class="description-text">创建应用后，您可以通过与 AI 对话来编辑和优化您的应用。</p>

            <div class="edit-scenarios">
              <div class="scenario-block">
                <div class="scenario-header">
                  <BgColorsOutlined />
                  <h3>样式调整</h3>
                </div>
                <p>修改颜色、字体、间距、边框等视觉元素</p>
                <div class="example-chips">
                  <span>"把首页背景改成橙色"</span>
                  <span>"调整按钮圆角大小"</span>
                </div>
              </div>

              <div class="scenario-block scenario-primary">
                <div class="scenario-header">
                  <MobileOutlined />
                  <h3>响应式优化</h3>
                </div>
                <p>优化移动端显示效果，确保在不同设备上正常显示</p>
                <div class="example-chips">
                  <span>"优化手机端布局"</span>
                  <span>"调整平板显示"</span>
                </div>
              </div>

              <div class="scenario-block">
                <div class="scenario-header">
                  <ThunderboltOutlined />
                  <h3>性能优化</h3>
                </div>
                <p>优化代码性能，提升页面加载速度</p>
                <div class="example-chips">
                  <span>"使用懒加载"</span>
                  <span>"优化渲染性能"</span>
                </div>
              </div>

              <div class="scenario-block scenario-primary">
                <div class="scenario-header">
                  <ToolOutlined />
                  <h3>功能增强</h3>
                </div>
                <p>添加新功能或改进现有功能</p>
                <div class="example-chips">
                  <span>"添加搜索功能"</span>
                  <span>"实现评论系统"</span>
                </div>
              </div>
            </div>

            <div class="tips-section">
              <h2>编辑技巧</h2>
              <div class="tips-grid">
                <div class="tip-item">
                  <div class="tip-number">01</div>
                  <h4>明确表达需求</h4>
                  <p>尽量清晰地描述您想要的效果，例如："把按钮改成橙色" 比 "改一下按钮" 更明确</p>
                </div>
                <div class="tip-item">
                  <div class="tip-number">02</div>
                  <h4>分步骤修改</h4>
                  <p>如果有多处需要修改，建议分步骤进行，每次修改一个方面</p>
                </div>
                <div class="tip-item">
                  <div class="tip-number">03</div>
                  <h4>查看代码</h4>
                  <p>AI 会显示修改后的代码，您可以查看并理解修改内容</p>
                </div>
                <div class="tip-item">
                  <div class="tip-number">04</div>
                  <h4>及时反馈</h4>
                  <p>如果修改结果不符合预期，可以继续与 AI 对话说明问题</p>
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- 常见问题 - FAQ -->
        <section id="faq" class="docs-section">
          <div class="section-header">
            <div class="section-number">05</div>
            <h1 class="section-title">常见问题</h1>
          </div>

          <div class="section-content">
            <div class="faq-grid">
              <div v-for="(faq, index) in faqs" :key="index" class="faq-item">
                <div class="faq-question">
                  <QuestionCircleOutlined />
                  <h3>{{ faq.q }}</h3>
                </div>
                <p class="faq-answer">{{ faq.a }}</p>
              </div>
            </div>

            <div class="contact-box">
              <div class="contact-header">
                <CustomerServiceOutlined />
                <h3>还有问题？</h3>
              </div>
              <p>如果您的问题在这里找不到答案，欢迎通过以下方式联系我们：</p>
              <a class="contact-link" href="https://www.gitee.com/hhzalh" target="_blank">
                <GithubOutlined />
                <span>访问 Gitee 仓库提交 Issue</span>
                <ArrowRightOutlined />
              </a>
            </div>
          </div>
        </section>

        <!-- 服务条款 -->
        <section id="terms" class="docs-section">
          <div class="section-header">
            <div class="section-number">06</div>
            <h1 class="section-title">服务条款</h1>
          </div>

          <div class="section-content">
            <div class="terms-intro">
              <p>
                欢迎使用
                <strong>蓝海智造</strong>
                平台！在使用本平台服务之前，请您仔细阅读并充分理解本服务条款的全部内容。
              </p>
            </div>

            <div class="terms-sections">
              <div class="terms-block">
                <h3>服务说明</h3>
                <p>
                  蓝海智造是一个基于人工智能技术的应用生成平台，为用户提供通过自然语言描述快速生成
                  Web 应用程序的服务。
                </p>
              </div>

              <div class="terms-block">
                <h3>用户账户</h3>
                <p>
                  您需要注册账户才能使用本平台的核心功能。注册时，您需要提供真实、准确、完整的个人信息，并妥善保管账户密码。
                </p>
              </div>

              <div class="terms-block">
                <h3>服务使用规范</h3>
                <p>
                  您在使用本平台服务时，必须遵守中华人民共和国相关法律法规，不得利用本平台从事任何违法违规活动。
                </p>
              </div>

              <div class="terms-block">
                <h3>知识产权</h3>
                <p>
                  通过本平台生成的应用代码及相关内容，其知识产权归您所有。但您需确保生成的内容不侵犯第三方的合法权益。
                </p>
              </div>

              <div class="terms-block">
                <h3>免责声明</h3>
                <p>
                  本平台通过 AI
                  技术生成的应用代码仅供参考使用。我们不对生成代码的准确性、完整性、适用性作任何保证。
                </p>
              </div>
            </div>

            <div class="terms-footer">
              <p class="update-date">
                <CalendarOutlined />
                最后更新时间：2026年1月
              </p>
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
  ArrowRightOutlined,
  BgColorsOutlined,
  BuildOutlined,
  BulbOutlined,
  CalendarOutlined,
  CodeOutlined,
  CommentOutlined,
  CustomerServiceOutlined,
  FileTextOutlined,
  GithubOutlined,
  MessageOutlined,
  MobileOutlined,
  PictureOutlined,
  PlusCircleOutlined,
  QuestionCircleOutlined,
  RocketOutlined,
  ShoppingOutlined,
  ThunderboltOutlined,
  ToolOutlined,
  UserAddOutlined,
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

<style scoped>
/* 字体定义 */
@import url('https://fonts.googleapis.com/css2?family=Noto+Serif+SC:wght@400;600;700&family=Noto+Sans+SC:wght@300;400;500;600&display=swap');

/* CSS 变量 */
:root {
  --color-primary: #f97316;
  --color-primary-dark: #ea580c;
  --color-primary-light: #fbbf24;
  --color-text: #1e293b;
  --color-text-secondary: #64748b;
  --color-bg: #fff7ed;
  --color-card-bg: rgba(255, 255, 255, 0.98);
  --color-border: rgba(249, 115, 22, 0.1);
  --font-serif: 'Noto Serif SC', serif;
  --font-sans: 'Noto Sans SC', sans-serif;
}

/* 主容器 */
.help-docs-page {
  min-height: calc(100vh - 64px);
  background: #fafafa;
}

/* Hero 区域 - 图片背景 */
.docs-hero {
  position: relative;
  height: 400px;
  background-image: url('https://images.unsplash.com/photo-1519681393784-d120267933ba?w=1920&q=80');
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
  overflow: hidden;
}

.hero-overlay {
  position: relative;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    135deg,
    rgba(249, 115, 22, 0.85) 0%,
    rgba(234, 88, 12, 0.75) 50%,
    rgba(251, 191, 36, 0.7) 100%
  );
  backdrop-filter: blur(2px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 24px;
}

.hero-content {
  text-align: center;
  color: white;
  position: relative;
  z-index: 2;
}

.hero-badge {
  display: inline-block;
  padding: 8px 20px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border-radius: 50px;
  font-size: 14px;
  font-weight: 500;
  letter-spacing: 2px;
  text-transform: uppercase;
  margin-bottom: 24px;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.hero-title {
  font-family: var(--font-serif);
  font-size: 64px;
  font-weight: 700;
  line-height: 1.2;
  margin: 0 0 20px;
  letter-spacing: -2px;
}

.hero-title .highlight {
  color: var(--color-primary-light);
  text-shadow: 0 2px 20px rgba(0, 0, 0, 0.2);
}

.hero-subtitle {
  font-family: var(--font-sans);
  font-size: 20px;
  font-weight: 300;
  opacity: 0.95;
  margin: 0;
  letter-spacing: 1px;
}

/* 主容器 */
.docs-container {
  max-width: 1400px;
  margin: -60px auto 0;
  position: relative;
  z-index: 10;
  display: flex;
  gap: 40px;
  padding: 0 16px 60px;
}

/* 侧边栏 */
.docs-sidebar {
  width: 260px;
  flex-shrink: 0;
  position: sticky;
  top: 80px;
  height: fit-content;
  max-height: calc(100vh - 100px);
  overflow-y: auto;
}

.sidebar-content {
  background: var(--color-card-bg);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 32px 28px;
  box-shadow: 0 10px 40px rgba(249, 115, 22, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.8);
}

.sidebar-header {
  margin-bottom: 24px;
}

.sidebar-title {
  font-family: var(--font-serif);
  font-size: 20px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0 0 16px;
}

.sidebar-divider {
  width: 40px;
  height: 3px;
  background: linear-gradient(90deg, var(--color-primary), var(--color-primary-light));
  border-radius: 2px;
}

/* 主内容区 */
.docs-main {
  flex: 1;
  background: var(--color-card-bg);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 60px;
  box-shadow: 0 10px 40px rgba(249, 115, 22, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.8);
}

/* 文档区块 */
.docs-section {
  margin-bottom: 100px;
  scroll-margin-top: 80px;
  position: relative;
}

.docs-section:last-child {
  margin-bottom: 0;
}

.section-header {
  display: flex;
  align-items: flex-end;
  gap: 20px;
  margin-bottom: 40px;
  position: relative;
}

.section-number {
  font-family: var(--font-serif);
  font-size: 72px;
  font-weight: 700;
  color: rgba(249, 115, 22, 0.15);
  line-height: 1;
  position: relative;
  top: 8px;
}

.section-title {
  font-family: var(--font-serif);
  font-size: 48px;
  font-weight: 700;
  color: var(--color-text);
  margin: 0;
  letter-spacing: -1px;
}

.section-content {
  font-family: var(--font-sans);
  line-height: 1.8;
  color: var(--color-text-secondary);
}

.section-content h2 {
  font-family: var(--font-serif);
  font-size: 28px;
  font-weight: 600;
  color: var(--color-text);
  margin: 48px 0 24px;
  position: relative;
  padding-left: 20px;
}

.section-content h2::before {
  content: '';
  position: absolute;
  left: 0;
  top: 4px;
  bottom: 4px;
  width: 4px;
  background: linear-gradient(180deg, var(--color-primary), var(--color-primary-light));
  border-radius: 2px;
}

.section-content h3 {
  font-family: var(--font-serif);
  font-size: 20px;
  font-weight: 600;
  color: var(--color-text);
  margin: 32px 0 16px;
}

.section-content p {
  margin: 16px 0;
  font-size: 16px;
  font-weight: 400;
}

.section-content ul,
.section-content ol {
  margin: 20px 0;
  padding-left: 28px;
}

.section-content li {
  margin: 12px 0;
  font-size: 16px;
}

/* 简介区块 */
.intro-block {
  margin: 32px 0;
  padding: 32px;
  background: linear-gradient(135deg, var(--color-bg) 0%, rgba(255, 255, 255, 0.9) 100%);
  border-radius: 16px;
  border-left: 4px solid var(--color-primary);
}

.intro-text {
  font-size: 18px;
  color: var(--color-text);
  margin: 0;
  line-height: 1.8;
}

.description-text {
  font-size: 17px;
  color: var(--color-text-secondary);
  margin: 24px 0;
}

/* 特性网格 - 不对称布局 */
.features-grid {
  display: grid;
  grid-template-columns: repeat(12, 1fr);
  gap: 24px;
  margin: 48px 0;
}

.feature-card {
  grid-column: span 4;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 16px;
  padding: 32px;
  text-align: center;
  border: 1px solid var(--color-border);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.feature-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, var(--color-primary), var(--color-primary-light));
  opacity: 0;
  transition: opacity 0.3s ease;
}

.feature-card:hover::before {
  opacity: 1;
}

.feature-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(249, 115, 22, 0.15);
  border-color: rgba(249, 115, 22, 0.3);
}

.feature-large {
  grid-column: span 8;
}

.feature-wide {
  grid-column: span 8;
}

.feature-icon-wrapper {
  width: 72px;
  height: 72px;
  margin: 0 auto 20px;
  background: linear-gradient(135deg, var(--color-bg) 0%, rgba(255, 255, 255, 1) 100%);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.feature-icon {
  font-size: 32px;
  color: var(--color-primary);
}

.feature-card h3 {
  font-family: var(--font-serif);
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 12px;
  color: var(--color-text);
}

.feature-card p {
  font-size: 13px;
  color: var(--color-text-secondary);
  margin: 0;
  line-height: 1.7;
}

/* 场景列表 */
.scenario-section {
  margin: 48px 0;
}

.scenario-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.scenario-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px 24px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  transition: all 0.3s ease;
}

.scenario-item:hover {
  background: rgba(255, 255, 255, 1);
  transform: translateX(8px);
  box-shadow: 0 4px 20px rgba(249, 115, 22, 0.1);
}

.scenario-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: var(--color-primary);
  flex-shrink: 0;
}

.scenario-item span:last-child {
  font-size: 16px;
  color: var(--color-text);
  font-weight: 500;
}

/* 步骤卡片 */
.steps-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
  margin: 40px 0;
}

.step-card {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 16px;
  padding: 32px;
  border: 1px solid var(--color-border);
  transition: all 0.3s ease;
}

.step-card:hover {
  box-shadow: 0 10px 30px rgba(249, 115, 22, 0.12);
  border-color: rgba(249, 115, 22, 0.2);
}

.step-highlight {
  background: linear-gradient(135deg, var(--color-bg) 0%, rgba(255, 255, 255, 1) 100%);
  border-left: 4px solid var(--color-primary);
}

.step-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
}

.step-icon {
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  flex-shrink: 0;
}

.step-header > div {
  flex: 1;
}

.step-header h3 {
  font-family: var(--font-serif);
  font-size: 22px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0 0 8px;
}

.step-number {
  font-size: 13px;
  color: var(--color-primary);
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.step-content {
  padding-left: 76px;
}

.step-content p {
  margin-bottom: 16px;
}

.step-content ol,
.step-content ul {
  margin: 16px 0;
  padding-left: 24px;
}

/* 代码示例 */
.code-example {
  margin: 24px 0;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid var(--color-border);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.code-header {
  display: flex;
  align-items: center;
  gap: 12px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  color: white;
  padding: 14px 20px;
  font-size: 14px;
  font-weight: 500;
}

.code-example pre {
  margin: 0;
  padding: 24px;
  background: #1e293b;
  color: #e2e8f0;
  overflow-x: auto;
  font-size: 14px;
  line-height: 1.7;
  font-family: 'Consolas', 'Monaco', monospace;
}

.code-example code {
  white-space: pre-wrap;
  word-wrap: break-word;
}

/* 提示框 */
.tip-box {
  display: flex;
  gap: 20px;
  background: linear-gradient(135deg, rgba(251, 191, 36, 0.1) 0%, rgba(255, 255, 255, 0.95) 100%);
  border-left: 4px solid var(--color-primary-light);
  border-radius: 16px;
  padding: 28px;
  margin: 40px 0;
}

.tip-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, var(--color-primary-light) 0%, #f59e0b 100%);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  flex-shrink: 0;
}

.tip-content strong {
  display: block;
  font-family: var(--font-serif);
  font-size: 18px;
  color: var(--color-text);
  margin-bottom: 8px;
}

.tip-content p {
  margin: 0;
  color: var(--color-text-secondary);
}

/* 指南网格 */
.guide-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
  margin: 40px 0;
}

.guide-card {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 16px;
  padding: 28px;
  border: 1px solid var(--color-border);
  transition: all 0.3s ease;
}

.guide-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 30px rgba(249, 115, 22, 0.12);
}

.guide-featured {
  background: linear-gradient(135deg, var(--color-bg) 0%, rgba(255, 255, 255, 1) 100%);
  border-left: 4px solid var(--color-primary);
}

.guide-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.guide-number {
  width: 40px;
  height: 32px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 700;
  font-size: 18px;
  flex-shrink: 0;
}

.guide-header h3 {
  font-family: var(--font-serif);
  font-size: 20px;
  font-weight: 600;
  margin: 0;
  color: var(--color-text);
}

.guide-card > p {
  margin: 0 0 16px;
  color: var(--color-text-secondary);
  font-size: 13px;
}

.guide-examples {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.example-tag {
  padding: 6px 14px;
  background: rgba(249, 115, 22, 0.08);
  border: 1px solid rgba(249, 115, 22, 0.2);
  border-radius: 8px;
  font-size: 13px;
  color: var(--color-primary-dark);
  font-weight: 500;
}

/* 快捷模板 */
.shortcut-section {
  margin: 48px 0;
}

.shortcuts-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-top: 24px;
}

.shortcut-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 24px 16px;
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid var(--color-border);
  border-radius: 12px;
  text-align: center;
  transition: all 0.3s ease;
}

.shortcut-item:hover {
  background: rgba(255, 255, 255, 1);
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(249, 115, 22, 0.12);
}

.shortcut-item .anticon {
  font-size: 32px;
  color: var(--color-primary);
}

.shortcut-item span {
  font-size: 14px;
  color: var(--color-text);
  font-weight: 500;
}

/* 编辑场景 */
.edit-scenarios {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin: 40px 0;
}

.scenario-block {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 16px;
  padding: 28px;
  border: 1px solid var(--color-border);
  transition: all 0.3s ease;
}

.scenario-block:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 30px rgba(249, 115, 22, 0.12);
}

.scenario-primary {
  background: linear-gradient(135deg, var(--color-bg) 0%, rgba(255, 255, 255, 1) 100%);
  border-left: 4px solid var(--color-primary);
}

.scenario-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.scenario-header .anticon {
  font-size: 28px;
  color: var(--color-primary);
}

.scenario-header h3 {
  font-family: var(--font-serif);
  font-size: 20px;
  font-weight: 600;
  margin: 0;
  color: var(--color-text);
}

.scenario-block > p {
  margin: 0 0 16px;
  color: var(--color-text-secondary);
}

.example-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.example-chips span {
  padding: 6px 12px;
  background: rgba(249, 115, 22, 0.06);
  border-radius: 6px;
  font-size: 13px;
  color: var(--color-text);
  font-family: 'Consolas', monospace;
}

/* 技巧部分 */
.tips-section {
  margin: 48px 0;
}

.tips-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-top: 24px;
}

.tip-item {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 16px;
  padding: 28px;
  border: 1px solid var(--color-border);
  position: relative;
  overflow: hidden;
}

.tip-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 4px;
  background: linear-gradient(90deg, var(--color-primary), var(--color-primary-light));
}

.tip-number {
  font-family: var(--font-serif);
  font-size: 32px;
  font-weight: 700;
  color: rgba(249, 115, 22, 0.2);
  margin-bottom: 12px;
}

.tip-item h4 {
  font-family: var(--font-serif);
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 12px;
  color: var(--color-text);
}

.tip-item p {
  margin: 0;
  color: var(--color-text-secondary);
  font-size: 13px;
  line-height: 1.7;
}

/* FAQ */
.faq-grid {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin: 40px 0;
}

.faq-item {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 16px;
  padding: 28px;
  border: 1px solid var(--color-border);
  transition: all 0.3s ease;
}

.faq-item:hover {
  transform: translateX(8px);
  box-shadow: 0 8px 24px rgba(249, 115, 22, 0.12);
  border-color: rgba(249, 115, 22, 0.2);
}

.faq-question {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 16px;
}

.faq-question .anticon {
  font-size: 24px;
  color: var(--color-primary);
  flex-shrink: 0;
  margin-top: 2px;
}

.faq-question h3 {
  font-family: var(--font-serif);
  font-size: 18px;
  font-weight: 600;
  margin: 0;
  color: var(--color-text);
}

.faq-answer {
  margin: 0;
  padding-left: 40px;
  color: var(--color-text-secondary);
  line-height: 1.7;
}

/* 联系框 */
.contact-box {
  background: linear-gradient(135deg, rgba(249, 115, 22, 0.05) 0%, rgba(255, 255, 255, 0.95) 100%);
  border-radius: 16px;
  padding: 32px;
  margin: 40px 0;
  border: 1px solid var(--color-border);
}

.contact-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.contact-header .anticon {
  font-size: 32px;
  color: var(--color-primary);
}

.contact-header h3 {
  font-family: var(--font-serif);
  font-size: 24px;
  font-weight: 600;
  margin: 0;
  color: var(--color-text);
}

.contact-box > p {
  margin: 0 0 20px;
  color: var(--color-text-secondary);
}

.contact-link {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  padding: 16px 24px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  color: white;
  text-decoration: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.contact-link:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(249, 115, 22, 0.3);
}

/* 服务条款 */
.terms-intro {
  margin: 32px 0;
  padding: 28px;
  background: linear-gradient(135deg, var(--color-bg) 0%, rgba(255, 255, 255, 0.9) 100%);
  border-radius: 16px;
  border-left: 4px solid var(--color-primary);
}

.terms-intro p {
  margin: 0;
  font-size: 17px;
  color: var(--color-text);
  line-height: 1.8;
}

.terms-sections {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin: 40px 0;
}

.terms-block {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  padding: 24px;
  border: 1px solid var(--color-border);
}

.terms-block h3 {
  font-family: var(--font-serif);
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 12px;
  color: var(--color-text);
}

.terms-block p {
  margin: 0;
  color: var(--color-text-secondary);
  font-size: 13px;
  line-height: 1.7;
}

.terms-footer {
  margin-top: 40px;
  padding-top: 24px;
  border-top: 1px solid var(--color-border);
}

.update-date {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
  color: var(--color-text-secondary);
  font-size: 14px;
}

.update-date .anticon {
  color: var(--color-primary);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .docs-hero {
    height: 300px;
  }

  .hero-title {
    font-size: 48px;
  }

  .docs-container {
    flex-direction: column;
    margin-top: -60px;
  }

  .docs-sidebar {
    width: 100%;
    position: static;
    max-height: none;
  }

  .docs-main {
    padding: 40px 32px;
  }

  .features-grid {
    grid-template-columns: repeat(6, 1fr);
  }

  .feature-card {
    grid-column: span 6 !important;
  }

  .guide-grid {
    grid-template-columns: 1fr;
  }

  .edit-scenarios {
    grid-template-columns: 1fr;
  }

  .tips-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .docs-hero {
    height: 240px;
  }

  .hero-title {
    font-size: 36px;
  }

  .hero-subtitle {
    font-size: 16px;
  }

  .docs-main {
    padding: 32px 24px;
  }

  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .section-number {
    font-size: 48px;
  }

  .section-title {
    font-size: 32px;
  }

  .section-content h2 {
    font-size: 22px;
  }

  .shortcuts-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .steps-container {
    gap: 20px;
  }

  .step-content {
    padding-left: 0;
    padding-top: 16px;
  }
}

@media (max-width: 480px) {
  .docs-container {
    padding: 0 16px 40px;
  }

  .docs-main {
    padding: 24px 20px;
  }

  .hero-title {
    font-size: 28px;
  }

  .section-number {
    font-size: 36px;
  }

  .section-title {
    font-size: 24px;
  }

  .shortcuts-grid {
    grid-template-columns: 1fr;
  }

  .tip-box {
    flex-direction: column;
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
