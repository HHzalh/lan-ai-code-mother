<template>
  <div class="help-docs-page">
    <div class="docs-container">
      <!-- 侧边栏导航 -->
      <div class="docs-sidebar">
        <div class="sidebar-content">
          <h2 class="sidebar-title">文档导航</h2>
          <a-anchor
            :affix="false"
            :get-container="() => document.querySelector('.docs-main') || window"
            :target-offset="80"
          >
            <a-anchor-link href="#introduction" title="简介" />
            <a-anchor-link href="#quickstart" title="快速开始" />
            <a-anchor-link href="#create" title="创建应用" />
            <a-anchor-link href="#edit" title="编辑应用" />
            <a-anchor-link href="#optimize" title="优化应用" />
            <a-anchor-link href="#faq" title="常见问题" />
            <a-anchor-link href="#terms" title="服务条款" />
          </a-anchor>
        </div>
      </div>

      <!-- 主内容区域 -->
      <div class="docs-main">
        <!-- 简介 -->
        <section id="introduction" class="docs-section">
          <h1 class="section-title">简介</h1>
          <div class="section-content">
            <p class="intro-text">
              欢迎使用 <strong>蓝海智造</strong>！这是一个强大的 AI
              驱动的应用生成平台，让您能够通过简单的自然语言描述，快速创建完整的 Web 应用程序。
            </p>

            <h2>什么是蓝海智造？</h2>
            <p>
              蓝海智造是一个创新的应用开发平台，它利用先进的人工智能技术，将您的想法转化为可用的 Web
              应用。无论您是开发者、设计师还是创业者，都可以通过我们的平台快速实现您的创意。
            </p>

            <h2>核心特性</h2>
            <div class="features-grid">
              <div class="feature-card">
                <div class="feature-icon">🚀</div>
                <h3>快速生成</h3>
                <p>只需描述您的需求，AI 即可在几分钟内生成完整的应用代码</p>
              </div>
              <div class="feature-card">
                <div class="feature-icon">💬</div>
                <h3>智能对话</h3>
                <p>通过与 AI 对话，持续优化和完善您的应用功能</p>
              </div>
              <div class="feature-card">
                <div class="feature-icon">🎨</div>
                <h3>现代化设计</h3>
                <p>自动生成美观、响应式的现代化用户界面</p>
              </div>
              <div class="feature-card">
                <div class="feature-icon">⚡</div>
                <h3>一键部署</h3>
                <p>生成的应用可直接部署，无需额外配置</p>
              </div>
            </div>

            <h2>适用场景</h2>
            <ul class="scenario-list">
              <li>个人博客和作品展示网站</li>
              <li>企业官网和产品展示</li>
              <li>在线商城和电商平台</li>
              <li>管理后台和数据看板</li>
              <li>原型设计和概念验证</li>
            </ul>
          </div>
        </section>

        <!-- 快速开始 -->
        <section id="quickstart" class="docs-section">
          <h1 class="section-title">快速开始</h1>
          <div class="section-content">
            <p>
              让我们开始您的第一个应用创建之旅！只需几个简单步骤，您就能拥有一个完整的 Web 应用。
            </p>

            <h2>第一步：注册账号</h2>
            <p>如果您还没有账号，请先注册：</p>
            <ol class="step-list">
              <li>点击页面右上角的 <strong>"登录"</strong> 按钮</li>
              <li>选择 <strong>"注册"</strong> 选项</li>
              <li>填写用户名、密码等信息</li>
              <li>完成注册后自动登录</li>
            </ol>

            <h2>第二步：创建应用</h2>
            <p>登录后，在首页输入您的应用描述：</p>
            <div class="code-example">
              <div class="code-header">示例描述</div>
              <pre><code>创建一个现代化的个人博客网站，包含文章列表、详情页、分类标签、搜索功能、评论系统和个人简介页面。采用简洁的设计风格，支持响应式布局，文章支持Markdown格式，首页展示最新文章和热门推荐。</code></pre>
            </div>

            <h2>第三步：与 AI 对话</h2>
            <p>应用创建后，您将进入对话页面。在这里您可以：</p>
            <ul>
              <li>要求 AI 修改应用样式和布局</li>
              <li>添加新的功能和页面</li>
              <li>优化代码和性能</li>
              <li>获取代码解释和技术支持</li>
            </ul>

            <h2>第四步：查看和部署</h2>
            <p>
              完善应用后，点击
              <strong>"查看作品"</strong> 按钮即可访问您的应用。部署后，您可以直接分享给他人使用。
            </p>

            <div class="tip-box">
              <strong>💡 提示：</strong>
              描述越详细，生成的应用越符合您的需求。建议包含功能需求、设计风格、页面结构等信息。
            </div>
          </div>
        </section>

        <!-- 创建应用 -->
        <section id="create" class="docs-section">
          <h1 class="section-title">创建应用</h1>
          <div class="section-content">
            <p>创建应用是使用蓝海智造的第一步。本节将详细介绍如何创建您的第一个应用。</p>

            <h2>应用描述指南</h2>
            <p>一个好的应用描述应该包含以下要素：</p>

            <h3>1. 应用类型</h3>
            <p>明确说明您要创建什么类型的应用，例如：</p>
            <ul>
              <li>个人博客网站</li>
              <li>企业官网</li>
              <li>在线商城</li>
              <li>作品展示网站</li>
              <li>管理后台系统</li>
            </ul>

            <h3>2. 核心功能</h3>
            <p>列出应用需要的主要功能，例如：</p>
            <div class="code-example">
              <div class="code-header">功能描述示例</div>
              <pre><code>• 用户注册和登录系统
• 商品展示和搜索功能
• 购物车和订单管理
• 支付结算功能
• 用户评价系统</code></pre>
            </div>

            <h3>3. 设计风格</h3>
            <p>描述您期望的设计风格，例如：</p>
            <ul>
              <li>简洁现代</li>
              <li>商务专业</li>
              <li>创意艺术</li>
              <li>科技感</li>
              <li>简约风格</li>
            </ul>

            <h3>4. 页面结构</h3>
            <p>说明应用需要哪些页面，例如：</p>
            <ul>
              <li>首页（轮播图、产品展示）</li>
              <li>产品列表页</li>
              <li>产品详情页</li>
              <li>购物车页面</li>
              <li>用户中心</li>
            </ul>

            <h2>创建流程</h2>
            <div class="process-flow">
              <div class="process-step">
                <div class="step-number">1</div>
                <div class="step-content">
                  <h4>输入描述</h4>
                  <p>在首页输入框中详细描述您的应用需求</p>
                </div>
              </div>
              <div class="process-step">
                <div class="step-number">2</div>
                <div class="step-content">
                  <h4>点击创建</h4>
                  <p>点击"创建应用"按钮，AI 开始生成代码</p>
                </div>
              </div>
              <div class="process-step">
                <div class="step-number">3</div>
                <div class="step-content">
                  <h4>等待生成</h4>
                  <p>系统会在几秒到几分钟内完成应用生成</p>
                </div>
              </div>
              <div class="process-step">
                <div class="step-number">4</div>
                <div class="step-content">
                  <h4>进入对话</h4>
                  <p>生成完成后自动跳转到对话页面</p>
                </div>
              </div>
            </div>

            <h2>快捷模板</h2>
            <p>首页提供了多个快捷模板，您可以直接点击使用：</p>
            <ul>
              <li><strong>个人博客网站</strong> - 适合个人内容创作</li>
              <li><strong>企业官网</strong> - 适合公司展示</li>
              <li><strong>在线商城</strong> - 适合电商业务</li>
              <li><strong>作品展示网站</strong> - 适合设计师和艺术家</li>
            </ul>
          </div>
        </section>

        <!-- 编辑应用 -->
        <section id="edit" class="docs-section">
          <h1 class="section-title">编辑应用</h1>
          <div class="section-content">
            <p>
              创建应用后，您可以通过与 AI 对话来编辑和优化您的应用。本节介绍如何有效地编辑应用。
            </p>

            <h2>对话编辑</h2>
            <p>在应用对话页面，您可以向 AI 提出各种编辑需求：</p>

            <h3>修改样式</h3>
            <div class="code-example">
              <div class="code-header">示例对话</div>
              <pre><code>用户：把首页的背景色改成蓝色
AI：已为您修改首页背景色为蓝色

用户：让按钮的圆角更大一些
AI：已调整按钮圆角半径</code></pre>
            </div>

            <h3>添加功能</h3>
            <p>您可以要求 AI 添加新功能：</p>
            <ul>
              <li>"添加一个搜索功能"</li>
              <li>"在首页添加轮播图"</li>
              <li>"添加用户评论功能"</li>
              <li>"实现分页功能"</li>
            </ul>

            <h3>修改布局</h3>
            <p>调整页面布局和结构：</p>
            <ul>
              <li>"把导航栏改成固定在顶部"</li>
              <li>"将侧边栏移到右侧"</li>
              <li>"使用卡片式布局展示内容"</li>
            </ul>

            <h2>编辑技巧</h2>
            <div class="tips-list">
              <div class="tip-item">
                <strong>1. 明确表达需求</strong>
                <p>尽量清晰地描述您想要的效果，例如："把按钮改成红色" 比 "改一下按钮" 更明确。</p>
              </div>
              <div class="tip-item">
                <strong>2. 分步骤修改</strong>
                <p>
                  如果有多处需要修改，建议分步骤进行，每次修改一个方面，这样更容易达到预期效果。
                </p>
              </div>
              <div class="tip-item">
                <strong>3. 查看代码</strong>
                <p>AI 会显示修改后的代码，您可以查看并理解修改内容，学习最佳实践。</p>
              </div>
              <div class="tip-item">
                <strong>4. 及时反馈</strong>
                <p>如果修改结果不符合预期，可以继续与 AI 对话，说明问题并请求进一步调整。</p>
              </div>
            </div>

            <h2>常见编辑场景</h2>
            <div class="scenario-cards">
              <div class="scenario-card">
                <h4>🎨 样式调整</h4>
                <p>修改颜色、字体、间距、边框等视觉元素</p>
              </div>
              <div class="scenario-card">
                <h4>📱 响应式优化</h4>
                <p>优化移动端显示效果，确保在不同设备上正常显示</p>
              </div>
              <div class="scenario-card">
                <h4>⚡ 性能优化</h4>
                <p>优化代码性能，提升页面加载速度</p>
              </div>
              <div class="scenario-card">
                <h4>🔧 功能增强</h4>
                <p>添加新功能或改进现有功能</p>
              </div>
            </div>
          </div>
        </section>

        <!-- 优化应用 -->
        <section id="optimize" class="docs-section">
          <h1 class="section-title">优化应用</h1>
          <div class="section-content">
            <p>应用创建后，您可以通过多种方式优化应用，提升用户体验和性能。</p>

            <h2>性能优化</h2>
            <h3>1. 代码优化</h3>
            <p>您可以要求 AI 优化代码：</p>
            <ul>
              <li>"优化代码性能，减少不必要的渲染"</li>
              <li>"使用懒加载优化图片加载"</li>
              <li>"压缩和合并 CSS/JS 文件"</li>
            </ul>

            <h3>2. 资源优化</h3>
            <p>优化静态资源：</p>
            <ul>
              <li>压缩图片大小</li>
            </ul>

            <h2>用户体验优化</h2>
            <h3>1. 交互优化</h3>
            <ul>
              <li>添加加载动画</li>
              <li>优化表单验证提示</li>
              <li>改进错误处理机制</li>
              <li>添加操作反馈</li>
            </ul>

            <h3>2. 响应式优化</h3>
            <p>确保应用在不同设备上都有良好的显示效果：</p>
            <ul>
              <li>移动端适配</li>
              <li>平板端优化</li>
              <li>桌面端增强</li>
            </ul>

            <h2>SEO 优化</h2>
            <p>提升搜索引擎优化：</p>
            <ul>
              <li>优化页面标题和描述</li>
              <li>添加语义化 HTML 标签</li>
              <li>优化 URL 结构</li>
              <li>添加 sitemap</li>
            </ul>

            <h2>安全性优化</h2>
            <ul>
              <li>输入验证和过滤</li>
              <li>XSS 防护</li>
              <li>CSRF 保护</li>
              <li>数据加密</li>
            </ul>

            <div class="optimization-checklist">
              <h3>优化检查清单</h3>
              <ul>
                <li>✅ 页面加载速度是否足够快？</li>
                <li>✅ 移动端显示是否正常？</li>
                <li>✅ 交互是否流畅？</li>
                <li>✅ 错误处理是否完善？</li>
                <li>✅ 代码是否规范？</li>
                <li>✅ 安全性是否考虑？</li>
              </ul>
            </div>
          </div>
        </section>

        <!-- 常见问题 -->
        <section id="faq" class="docs-section">
          <h1 class="section-title">常见问题</h1>
          <div class="section-content">
            <div class="faq-list">
              <div class="faq-item">
                <h3>Q: 如何创建应用？</h3>
                <p>
                  <strong>A:</strong>
                  在首页输入框中详细描述您想要创建的应用，然后点击"创建应用"按钮即可。系统会自动生成应用代码。
                </p>
              </div>

              <div class="faq-item">
                <h3>Q: 应用生成需要多长时间？</h3>
                <p>
                  <strong>A:</strong>
                  应用生成时间取决于应用的复杂度，通常在几秒到几分钟之间。简单应用可能只需几秒，复杂应用可能需要几分钟。
                </p>
              </div>

              <div class="faq-item">
                <h3>Q: 如何修改已创建的应用？</h3>
                <p>
                  <strong>A:</strong> 在应用对话页面，您可以直接与 AI 对话，描述您想要修改的内容。AI
                  会理解您的需求并修改代码。
                </p>
              </div>

              <div class="faq-item">
                <h3>Q: 可以部署到自己的服务器吗？</h3>
                <p>
                  <strong>A:</strong>
                  可以。系统会生成完整的应用代码，您可以下载代码并部署到任何支持的前端服务器上。
                </p>
              </div>

              <div class="faq-item">
                <h3>Q: 支持哪些类型的应用？</h3>
                <p>
                  <strong>A:</strong> 目前主要支持前端 Web
                  应用，包括博客、官网、商城、展示网站等。未来会支持更多类型。
                </p>
              </div>

              <div class="faq-item">
                <h3>Q: 生成的应用代码质量如何？</h3>
                <p>
                  <strong>A:</strong> AI
                  生成的应用代码遵循现代前端开发最佳实践，代码结构清晰，易于维护和扩展。
                </p>
              </div>

              <div class="faq-item">
                <h3>Q: 如何获取技术支持？</h3>
                <p><strong>A:</strong> 您可以通过以下方式获取支持：</p>
                <ul>
                  <li>查看本文档</li>
                  <li>在应用对话页面询问 AI</li>
                  <li>联系作者（Gitee）</li>
                </ul>
              </div>

              <div class="faq-item">
                <h3>Q: 应用有使用限制吗？</h3>
                <p>
                  <strong>A:</strong>
                  目前平台对应用数量和使用频率有一定的限制。具体限制请查看服务条款或联系管理员。
                </p>
              </div>

              <div class="faq-item">
                <h3>Q: 可以导出应用代码吗？</h3>
                <p>
                  <strong>A:</strong>
                  可以。在应用对话页面，您可以查看完整的应用代码，并复制或下载使用。
                </p>
              </div>

              <div class="faq-item">
                <h3>Q: 如何删除应用？</h3>
                <p>
                  <strong>A:</strong>
                  在"我的作品"页面，您可以找到已创建的应用。如需删除，请联系管理员或使用应用管理功能。
                </p>
              </div>
            </div>

            <div class="contact-box">
              <h3>还有问题？</h3>
              <p>如果您的问题在这里找不到答案，欢迎通过以下方式联系我们：</p>
              <ul>
                <li>
                  访问 <a href="https://www.gitee.com/hhzalh" target="_blank">Gitee 仓库</a> 提交
                  Issue
                </li>
              </ul>
            </div>
          </div>
        </section>

        <!-- 服务条款 -->
        <section id="terms" class="docs-section">
          <h1 class="section-title">服务条款</h1>
          <div class="section-content">
            <p class="intro-text">
              欢迎使用 <strong>蓝海智造</strong>
              平台！在使用本平台服务之前，请您仔细阅读并充分理解本服务条款的全部内容。当您注册、登录、使用本平台服务时，即表示您已阅读、理解并同意接受本服务条款的全部内容。
            </p>

            <h2>一、服务说明</h2>
            <p>
              蓝海智造是一个基于人工智能技术的应用生成平台，为用户提供通过自然语言描述快速生成 Web
              应用程序的服务。本平台利用 AI 技术，将用户的需求描述转化为可用的应用代码。
            </p>

            <h2>二、用户账户</h2>
            <h3>2.1 账户注册</h3>
            <ul>
              <li>您需要注册账户才能使用本平台的核心功能</li>
              <li>注册时，您需要提供真实、准确、完整的个人信息</li>
              <li>您有责任维护账户信息的安全性和准确性</li>
              <li>您不得将账户转让、出售或与他人共享</li>
            </ul>

            <h3>2.2 账户安全</h3>
            <ul>
              <li>您有责任妥善保管账户密码，不得将密码泄露给第三方</li>
              <li>如发现账户被盗用或存在安全风险，请立即通知我们</li>
              <li>因您的原因导致账户信息泄露造成的损失，由您自行承担</li>
            </ul>

            <h2>三、服务使用规范</h2>
            <h3>3.1 合法使用</h3>
            <p>您在使用本平台服务时，必须遵守以下规定：</p>
            <ul>
              <li>遵守中华人民共和国相关法律法规</li>
              <li>不得利用本平台从事任何违法违规活动</li>
              <li>不得生成、传播违法、有害、侵权的内容</li>
              <li>不得利用本平台进行任何商业欺诈或诈骗行为</li>
              <li>不得干扰、破坏本平台的正常运行</li>
            </ul>

            <h3>3.2 禁止行为</h3>
            <p>您不得从事以下行为：</p>
            <ul>
              <li>使用自动化工具批量创建应用或进行恶意操作</li>
              <li>尝试破解、逆向工程或干扰平台的技术架构</li>
              <li>利用平台漏洞进行任何形式的攻击或破坏</li>
              <li>发布、传播含有病毒、木马等恶意代码的内容</li>
              <li>侵犯他人知识产权、隐私权或其他合法权益</li>
              <li>进行任何可能损害平台或其他用户利益的行为</li>
            </ul>

            <h2>四、知识产权</h2>
            <h3>4.1 平台知识产权</h3>
            <p>
              本平台的所有知识产权，包括但不限于商标、专利、著作权、商业秘密等，均归蓝海智造所有。未经授权，您不得使用、复制、传播或修改本平台的任何内容。
            </p>

            <h3>4.2 生成内容的知识产权</h3>
            <p>
              通过本平台生成的应用代码及相关内容，其知识产权归您所有。但您需确保生成的内容不侵犯第三方的合法权益。如因生成内容引发的知识产权纠纷，由您自行承担责任。
            </p>

            <h3>4.3 用户内容授权</h3>
            <p>
              您在使用本平台时上传、输入的内容（包括但不限于应用描述、对话内容等），您授予我们使用、存储、处理这些内容的权利，以便为您提供服务。
            </p>

            <h2>五、服务限制与变更</h2>
            <h3>5.1 服务可用性</h3>
            <ul>
              <li>我们努力保证服务的稳定性和可用性，但不保证服务不会中断或出错</li>
              <li>因系统维护、升级、故障等原因导致的服务中断，我们不承担责任</li>
              <li>我们有权根据实际情况调整服务内容、功能或限制使用频率</li>
            </ul>

            <h3>5.2 使用限制</h3>
            <ul>
              <li>我们可能对免费用户的应用创建数量、使用频率等进行合理限制</li>
              <li>我们有权根据用户行为、系统负载等情况调整服务限制</li>
              <li>如发现用户存在滥用行为，我们有权暂停或终止其服务</li>
            </ul>

            <h2>六、隐私保护</h2>
            <p>
              我们重视您的隐私保护。在使用本平台服务时，我们会收集、使用、存储您的个人信息。我们承诺：
            </p>
            <ul>
              <li>严格按照相关法律法规和隐私政策处理您的个人信息</li>
              <li>不会未经授权向第三方披露您的个人信息</li>
              <li>采取合理的安全措施保护您的个人信息安全</li>
              <li>您有权查询、更正、删除您的个人信息</li>
            </ul>
            <p>详细的隐私保护政策请参考我们的隐私政策文档。</p>

            <h2>七、免责声明</h2>
            <h3>7.1 服务免责</h3>
            <p>在以下情况下，我们不承担责任：</p>
            <ul>
              <li>因不可抗力（如自然灾害、战争、政府行为等）导致的服务中断</li>
              <li>因您违反本服务条款导致的服务中断或损失</li>
              <li>因第三方原因（如网络服务商、云服务商等）导致的问题</li>
              <li>因您使用生成的应用代码导致的任何损失或纠纷</li>
            </ul>

            <h3>7.2 生成内容免责</h3>
            <p>
              本平台通过 AI
              技术生成的应用代码仅供参考使用。我们不对生成代码的准确性、完整性、适用性作任何保证。您在使用生成的应用代码时，应当：
            </p>
            <ul>
              <li>自行审查和测试生成代码的适用性</li>
              <li>对生成代码进行必要的修改和优化</li>
              <li>承担使用生成代码可能产生的风险和责任</li>
            </ul>

            <h2>八、服务变更与终止</h2>
            <h3>8.1 服务变更</h3>
            <p>
              我们有权根据业务发展需要，随时修改、暂停或终止部分或全部服务。我们会尽可能提前通知用户，但紧急情况下可能无法提前通知。
            </p>

            <h3>8.2 账户终止</h3>
            <p>在以下情况下，我们有权终止您的账户和服务：</p>
            <ul>
              <li>您违反本服务条款或相关法律法规</li>
              <li>您长期未使用账户（超过 12 个月）</li>
              <li>您主动申请注销账户</li>
              <li>其他我们认为需要终止服务的情况</li>
            </ul>

            <h2>九、争议解决</h2>
            <p>
              如因本服务条款或使用本平台服务产生争议，双方应友好协商解决。协商不成的，任何一方均可向本平台运营方所在地有管辖权的人民法院提起诉讼。
            </p>

            <h2>十、其他条款</h2>
            <h3>10.1 条款修改</h3>
            <p>
              我们有权根据法律法规变化、业务发展需要等因素，随时修改本服务条款。修改后的条款将在平台上公布，自公布之日起生效。如您不同意修改后的条款，请停止使用本平台服务。
            </p>

            <h3>10.2 可分割性</h3>
            <p>如本服务条款的某一条款被认定为无效或不可执行，不影响其他条款的效力。</p>

            <h3>10.3 联系方式</h3>
            <p>如您对本服务条款有任何疑问或建议，欢迎通过以下方式联系我们：</p>
            <ul>
              <li>
                访问 <a href="https://www.gitee.com/hhzalh" target="_blank">Gitee 仓库</a> 提交
                Issue
              </li>
              <li>通过平台内的反馈功能联系我们</li>
            </ul>

            <div class="tip-box">
              <strong>📝 重要提示：</strong>
              本服务条款自您注册账户或首次使用本平台服务时生效。请您在使用服务前仔细阅读并充分理解本条款的全部内容。如您不同意本条款的任何内容，请立即停止使用本平台服务。
            </div>

            <p class="update-date"><strong>最后更新时间：</strong> 2025年1月</p>
          </div>
        </section>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
// 页面加载时滚动到锚点
import { onMounted } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

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
.help-docs-page {
  min-height: calc(100vh - 64px);
  background: linear-gradient(180deg, #f8fafc 0%, #f1f5f9 100%);
  padding: 40px 0;
}

.docs-container {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  gap: 40px;
  padding: 0 24px;
}

/* 侧边栏 */
.docs-sidebar {
  width: 240px;
  flex-shrink: 0;
  position: sticky;
  top: 80px;
  height: fit-content;
  max-height: calc(100vh - 100px);
  overflow-y: auto;
}

.sidebar-content {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.8);
}

.sidebar-title {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid rgba(59, 130, 246, 0.1);
}

/* 主内容区域 */
.docs-main {
  flex: 1;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 16px;
  padding: 48px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.8);
}

.docs-section {
  margin-bottom: 80px;
  scroll-margin-top: 80px;
}

.docs-section:last-child {
  margin-bottom: 0;
}

.section-title {
  font-size: 36px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 32px;
  padding-bottom: 16px;
  border-bottom: 3px solid rgba(59, 130, 246, 0.2);
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.section-content {
  line-height: 1.8;
  color: #475569;
}

.section-content h2 {
  font-size: 24px;
  font-weight: 600;
  color: #1e293b;
  margin: 32px 0 16px;
}

.section-content h3 {
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
  margin: 24px 0 12px;
}

.section-content p {
  margin: 12px 0;
  font-size: 16px;
}

.section-content ul,
.section-content ol {
  margin: 16px 0;
  padding-left: 24px;
}

.section-content li {
  margin: 8px 0;
  font-size: 16px;
}

/* 简介部分 */
.intro-text {
  font-size: 18px;
  color: #64748b;
  margin-bottom: 32px;
  padding: 20px;
  background: rgba(59, 130, 246, 0.05);
  border-radius: 12px;
  border-left: 4px solid #3b82f6;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 24px;
  margin: 32px 0;
}

.feature-card {
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  padding: 24px;
  text-align: center;
  border: 1px solid rgba(59, 130, 246, 0.1);
  transition: all 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.15);
  border-color: rgba(59, 130, 246, 0.3);
}

.feature-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.feature-card h3 {
  font-size: 18px;
  margin: 0 0 12px;
  color: #1e293b;
}

.feature-card p {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

.scenario-list {
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  padding: 24px;
  border-left: 4px solid #10b981;
}

/* 代码示例 */
.code-example {
  margin: 24px 0;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid rgba(59, 130, 246, 0.2);
}

.code-header {
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
  color: white;
  padding: 12px 20px;
  font-size: 14px;
  font-weight: 500;
}

.code-example pre {
  margin: 0;
  padding: 20px;
  background: #1e293b;
  color: #e2e8f0;
  overflow-x: auto;
  font-size: 14px;
  line-height: 1.6;
}

.code-example code {
  font-family: 'Courier New', monospace;
  white-space: pre-wrap;
  word-wrap: break-word;
}

/* 步骤列表 */
.step-list {
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  padding: 24px 24px 24px 40px;
  border-left: 4px solid #3b82f6;
}

/* 提示框 */
.tip-box {
  background: rgba(16, 185, 129, 0.1);
  border-left: 4px solid #10b981;
  border-radius: 8px;
  padding: 16px 20px;
  margin: 24px 0;
}

/* 流程步骤 */
.process-flow {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin: 32px 0;
}

.process-step {
  display: flex;
  gap: 20px;
  align-items: flex-start;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  padding: 24px;
  border-left: 4px solid #8b5cf6;
}

.step-number {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  flex-shrink: 0;
}

.step-content h4 {
  margin: 0 0 8px;
  color: #1e293b;
  font-size: 18px;
}

.step-content p {
  margin: 0;
  color: #64748b;
}

/* 技巧列表 */
.tips-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin: 32px 0;
}

.tip-item {
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  padding: 20px;
  border-left: 4px solid #f59e0b;
}

.tip-item strong {
  display: block;
  color: #1e293b;
  margin-bottom: 8px;
  font-size: 16px;
}

.tip-item p {
  margin: 0;
  color: #64748b;
}

/* 场景卡片 */
.scenario-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin: 32px 0;
}

.scenario-card {
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  padding: 20px;
  border: 1px solid rgba(59, 130, 246, 0.1);
  transition: all 0.3s ease;
}

.scenario-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.15);
}

.scenario-card h4 {
  margin: 0 0 8px;
  color: #1e293b;
  font-size: 16px;
}

.scenario-card p {
  margin: 0;
  font-size: 14px;
  color: #64748b;
}

/* 优化检查清单 */
.optimization-checklist {
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  padding: 24px;
  margin: 32px 0;
  border-left: 4px solid #10b981;
}

.optimization-checklist h3 {
  margin-top: 0;
}

.optimization-checklist ul {
  list-style: none;
  padding-left: 0;
}

.optimization-checklist li {
  padding: 8px 0;
  font-size: 16px;
}

/* FAQ */
.faq-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
  margin: 32px 0;
}

.faq-item {
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  padding: 24px;
  border-left: 4px solid #3b82f6;
}

.faq-item h3 {
  margin: 0 0 12px;
  color: #1e293b;
  font-size: 18px;
}

.faq-item p {
  margin: 0;
  color: #475569;
}

.faq-item ul {
  margin: 12px 0 0 24px;
}

/* 联系框 */
.contact-box {
  background: rgba(59, 130, 246, 0.05);
  border-radius: 12px;
  padding: 24px;
  margin-top: 40px;
  border: 1px solid rgba(59, 130, 246, 0.2);
}

.contact-box h3 {
  margin-top: 0;
  color: #1e293b;
}

.contact-box a {
  color: #3b82f6;
  text-decoration: none;
}

.contact-box a:hover {
  text-decoration: underline;
}

/* 服务条款特殊样式 */
.update-date {
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid rgba(59, 130, 246, 0.1);
  color: #94a3b8;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .docs-container {
    flex-direction: column;
  }

  .docs-sidebar {
    width: 100%;
    position: static;
    max-height: none;
  }

  .docs-main {
    padding: 32px;
  }
}

@media (max-width: 768px) {
  .docs-main {
    padding: 24px;
  }

  .section-title {
    font-size: 28px;
  }

  .features-grid {
    grid-template-columns: 1fr;
  }

  .scenario-cards {
    grid-template-columns: 1fr;
  }

  .process-step {
    flex-direction: column;
    text-align: center;
  }
}

@media (max-width: 480px) {
  .docs-container {
    padding: 0 16px;
  }

  .docs-main {
    padding: 20px;
  }

  .section-title {
    font-size: 24px;
  }

  .section-content h2 {
    font-size: 20px;
  }

  .section-content h3 {
    font-size: 18px;
  }
}
</style>
