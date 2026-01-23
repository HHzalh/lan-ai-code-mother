<script lang="ts" setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/loginUser'
import { addApp, listGoodAppVoByPage, listMyAppVoByPage } from '@/api/appController'
import { getDeployUrl } from '@/config/env'
import AppCard from '@/components/AppCard.vue'
import {
  BuildOutlined,
  FileTextOutlined,
  PictureOutlined,
  ShoppingOutlined,
} from '@ant-design/icons-vue'

const router = useRouter()
const loginUserStore = useLoginUserStore()

// 用户提示词
const userPrompt = ref('')
const creating = ref(false)

// 我的应用数据
const myApps = ref<API.AppVO[]>([])
const myAppsPage = reactive({
  current: 1,
  pageSize: 6,
  total: 0,
})

// 精选应用数据
const featuredApps = ref<API.AppVO[]>([])
const featuredAppsPage = reactive({
  current: 1,
  pageSize: 6,
  total: 0,
})

// 设置提示词
const setPrompt = (prompt: string) => {
  userPrompt.value = prompt
}

// 优化提示词功能已移除

// 创建应用
const createApp = async () => {
  if (!userPrompt.value.trim()) {
    message.warning('请输入应用描述')
    return
  }

  if (!loginUserStore.loginUser.id) {
    message.warning('请先登录')
    await router.push('/user/login')
    return
  }

  creating.value = true
  try {
    const res = await addApp({
      initPrompt: userPrompt.value.trim(),
    })

    if (res.data.code === 0 && res.data.data) {
      message.success('应用创建成功')
      // 跳转到对话页面，确保ID是字符串类型
      const appId = String(res.data.data)
      await router.push(`/app/chat/${appId}`)
    } else {
      message.error('创建失败：' + res.data.message)
    }
  } catch (error) {
    console.error('创建应用失败：', error)
    message.error('创建失败，请重试')
  } finally {
    creating.value = false
  }
}

// 加载我的应用
const loadMyApps = async () => {
  if (!loginUserStore.loginUser.id) {
    return
  }

  try {
    const res = await listMyAppVoByPage({
      pageNum: myAppsPage.current,
      pageSize: myAppsPage.pageSize,
      sortField: 'createTime',
      sortOrder: 'desc',
    })

    if (res.data.code === 0 && res.data.data) {
      myApps.value = res.data.data.records || []
      myAppsPage.total = res.data.data.totalRow || 0
    }
  } catch (error) {
    console.error('加载我的应用失败：', error)
  }
}

// 加载精选应用
const loadFeaturedApps = async () => {
  try {
    const res = await listGoodAppVoByPage({
      pageNum: featuredAppsPage.current,
      pageSize: featuredAppsPage.pageSize,
      sortField: 'createTime',
      sortOrder: 'desc',
    })

    if (res.data.code === 0 && res.data.data) {
      featuredApps.value = res.data.data.records || []
      featuredAppsPage.total = res.data.data.totalRow || 0
    }
  } catch (error) {
    console.error('加载精选应用失败：', error)
  }
}

// 查看对话
const viewChat = (appId: string | number | undefined) => {
  if (appId) {
    router.push(`/app/chat/${appId}?view=1`)
  }
}

// 查看作品
const viewWork = (app: API.AppVO) => {
  if (app.deployKey) {
    const url = getDeployUrl(app.deployKey)
    window.open(url, '_blank')
  }
}

// 格式化时间函数已移除，不再需要显示创建时间

// 页面加载时获取数据
onMounted(() => {
  loadMyApps()
  loadFeaturedApps()

  // 鼠标跟随光效
  const handleMouseMove = (e: MouseEvent) => {
    const { clientX, clientY } = e
    const { innerWidth, innerHeight } = window
    const x = (clientX / innerWidth) * 100
    const y = (clientY / innerHeight) * 100

    document.documentElement.style.setProperty('--mouse-x', `${x}%`)
    document.documentElement.style.setProperty('--mouse-y', `${y}%`)
  }

  document.addEventListener('mousemove', handleMouseMove)

  // 清理事件监听器
  return () => {
    document.removeEventListener('mousemove', handleMouseMove)
  }
})
</script>

<template>
  <div id="homePage">
    <div class="container">
      <!-- 网站标题和描述 -->
      <div class="hero-section">
        <div class="hero-content">
          <h1 class="hero-title">蓝海智造</h1>
          <p class="hero-description">让想法，瞬间成为可用的应用</p>
        </div>
      </div>

      <!-- 主输入区域卡片 -->
      <div class="main-input-card">
        <!-- 用户提示词输入框 -->
        <div class="input-section">
          <a-textarea
            v-model:value="userPrompt"
            :auto-size="{ minRows: 5, maxRows: 8 }"
            :maxlength="1000"
            :rows="5"
            class="prompt-input"
            placeholder="描述你想要创建的应用，例如：帮我创建一个现代化的个人博客网站..."
          />
          <div class="input-actions">
            <a-button
              :loading="creating"
              class="create-btn"
              size="large"
              type="primary"
              @click="createApp"
            >
              <template #icon>
                <span class="send-icon">↑</span>
              </template>
              创建应用
            </a-button>
          </div>
        </div>

        <!-- 快捷按钮 -->
        <div class="quick-actions">
          <div class="quick-actions-label">快速开始：</div>
          <div class="quick-buttons">
            <a-button
              class="quick-btn"
              type="default"
              @click="
                setPrompt(
                  '创建一个现代化的个人博客网站，包含文章列表、详情页、分类标签、搜索功能、评论系统和个人简介页面。采用简洁的设计风格，支持响应式布局，文章支持Markdown格式，首页展示最新文章和热门推荐。',
                )
              "
            >
              <template #icon>
                <FileTextOutlined />
              </template>
              个人博客网站
            </a-button>
            <a-button
              class="quick-btn"
              type="default"
              @click="
                setPrompt(
                  '设计一个专业的企业官网，包含公司介绍、产品服务展示、新闻资讯、联系我们等页面。采用商务风格的设计，包含轮播图、产品展示卡片、团队介绍、客户案例展示，支持多语言切换和在线客服功能。',
                )
              "
            >
              <template #icon>
                <BuildOutlined />
              </template>
              企业官网
            </a-button>
            <a-button
              class="quick-btn"
              type="default"
              @click="
                setPrompt(
                  '构建一个功能完整的在线商城，包含商品展示、购物车、用户注册登录、订单管理、支付结算等功能。设计现代化的商品卡片布局，支持商品搜索筛选、用户评价、优惠券系统和会员积分功能。',
                )
              "
            >
              <template #icon>
                <ShoppingOutlined />
              </template>
              在线商城
            </a-button>
            <a-button
              class="quick-btn"
              type="default"
              @click="
                setPrompt(
                  '制作一个精美的作品展示网站，适合设计师、摄影师、艺术家等创作者。包含作品画廊、项目详情页、个人简历、联系方式等模块。采用瀑布流或网格布局展示作品，支持图片放大预览和作品分类筛选。',
                )
              "
            >
              <template #icon>
                <PictureOutlined />
              </template>
              作品展示网站
            </a-button>
          </div>
        </div>
      </div>

      <!-- 我的作品 -->
      <div class="section">
        <h2 class="section-title">我的作品</h2>
        <div class="app-grid">
          <AppCard
            v-for="app in myApps"
            :key="app.id"
            :app="app"
            @view-chat="viewChat"
            @view-work="viewWork"
          />
        </div>
        <div class="pagination-wrapper">
          <a-pagination
            v-model:current="myAppsPage.current"
            v-model:page-size="myAppsPage.pageSize"
            :show-size-changer="false"
            :show-total="(total: number) => `共 ${total} 个应用`"
            :total="myAppsPage.total"
            @change="loadMyApps"
          />
        </div>
      </div>

      <!-- 精选案例 -->
      <div class="section">
        <h2 class="section-title">精选案例</h2>
        <div class="featured-grid">
          <AppCard
            v-for="app in featuredApps"
            :key="app.id"
            :app="app"
            :featured="true"
            @view-chat="viewChat"
            @view-work="viewWork"
          />
        </div>
        <div class="pagination-wrapper">
          <a-pagination
            v-model:current="featuredAppsPage.current"
            v-model:page-size="featuredAppsPage.pageSize"
            :show-size-changer="false"
            :show-total="(total: number) => `共 ${total} 个案例`"
            :total="featuredAppsPage.total"
            @change="loadFeaturedApps"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
#homePage {
  width: 100%;
  margin: 0;
  padding: 0;
  min-height: 100vh;
  background:
    linear-gradient(180deg, #fff7ed 0%, #ffedd5 8%, #fed7aa 20%, #fdba74 100%),
    radial-gradient(circle at 20% 80%, rgba(249, 115, 22, 0.15) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(234, 88, 12, 0.12) 0%, transparent 50%),
    radial-gradient(circle at 40% 40%, rgba(251, 191, 36, 0.08) 0%, transparent 50%);
  position: relative;
  overflow: hidden;
}

/* 科技感网格背景 */
#homePage::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image:
    linear-gradient(rgba(249, 115, 22, 0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(249, 115, 22, 0.05) 1px, transparent 1px),
    linear-gradient(rgba(234, 88, 12, 0.04) 1px, transparent 1px),
    linear-gradient(90deg, rgba(234, 88, 12, 0.04) 1px, transparent 1px);
  background-size:
    100px 100px,
    100px 100px,
    20px 20px,
    20px 20px;
  pointer-events: none;
  animation: gridFloat 20s ease-in-out infinite;
}

/* 动态光效 */
#homePage::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
    radial-gradient(
      600px circle at var(--mouse-x, 50%) var(--mouse-y, 50%),
      rgba(249, 115, 22, 0.08) 0%,
      rgba(234, 88, 12, 0.06) 40%,
      transparent 80%
    ),
    linear-gradient(45deg, transparent 30%, rgba(249, 115, 22, 0.04) 50%, transparent 70%),
    linear-gradient(-45deg, transparent 30%, rgba(251, 191, 36, 0.04) 50%, transparent 70%);
  pointer-events: none;
  animation: lightPulse 8s ease-in-out infinite alternate;
}

@keyframes gridFloat {
  0%,
  100% {
    transform: translate(0, 0);
  }
  50% {
    transform: translate(5px, 5px);
  }
}

@keyframes lightPulse {
  0% {
    opacity: 0.3;
  }
  100% {
    opacity: 0.7;
  }
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 40px 24px;
  position: relative;
  z-index: 2;
  width: 100%;
  box-sizing: border-box;
}

/* 英雄区域 - 铺满容器 */
.hero-section {
  text-align: center;
  padding: 60px 0;
  margin: 0 -24px 40px -24px;
  color: #1e293b;
  position: relative;
  overflow: hidden;
  background-image: url('https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=1920&q=80');
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
  width: calc(100% + 48px);
}

.hero-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(2px);
}

.hero-content {
  position: relative;
  z-index: 2;
}

.hero-title {
  font-size: 64px;
  font-weight: 800;
  margin: 0 0 24px;
  line-height: 1.1;
  background: linear-gradient(135deg, #f97316 0%, #ea580c 50%, #fbbf24 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  background-size: 200% 200%;
  letter-spacing: -2px;
  position: relative;
  z-index: 2;
  animation: titleShimmer 4s ease-in-out infinite;
}

@keyframes titleShimmer {
  0%,
  100% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
}

.hero-description {
  font-size: 22px;
  margin: 0;
  opacity: 0.85;
  color: #475569;
  position: relative;
  z-index: 2;
  font-weight: 400;
  letter-spacing: 0.3px;
}

/* 主输入卡片 */
.main-input-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 40px;
  margin-bottom: 60px;
  box-shadow:
    0 20px 60px rgba(249, 115, 22, 0.08),
    0 0 0 1px rgba(255, 255, 255, 0.5) inset;
  border: 1px solid rgba(255, 255, 255, 0.8);
  transition: all 0.3s ease;
}

.main-input-card:hover {
  box-shadow:
    0 25px 70px rgba(249, 115, 22, 0.12),
    0 0 0 1px rgba(255, 255, 255, 0.6) inset;
  transform: translateY(-2px);
}

/* 输入区域 */
.input-section {
  position: relative;
  margin-bottom: 32px;
}

.prompt-input {
  border-radius: 16px;
  border: 2px solid rgba(249, 115, 22, 0.1);
  font-size: 16px;
  padding: 24px 140px 24px 24px;
  background: rgba(255, 255, 255, 1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  line-height: 1.6;
}

.prompt-input:focus,
.prompt-input:hover {
  border-color: rgba(249, 115, 22, 0.3);
  box-shadow: 0 8px 24px rgba(249, 115, 22, 0.15);
  background: rgba(255, 255, 255, 1);
}

.prompt-input::placeholder {
  color: #94a3b8;
}

.input-actions {
  position: absolute;
  bottom: 16px;
  right: 16px;
  display: flex;
  gap: 8px;
  align-items: center;
}

.create-btn {
  height: 48px;
  padding: 0 32px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(249, 115, 22, 0.3);
  transition: all 0.3s ease;
}

.create-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(249, 115, 22, 0.4);
}

.send-icon {
  font-size: 18px;
  margin-right: 8px;
  display: inline-block;
  transition: transform 0.3s ease;
}

.create-btn:hover .send-icon {
  transform: translateY(-2px);
}

/* 快捷按钮区域 */
.quick-actions {
  padding-top: 32px;
  border-top: 1px solid rgba(249, 115, 22, 0.1);
}

.quick-actions-label {
  font-size: 14px;
  color: #64748b;
  margin-bottom: 16px;
  font-weight: 500;
  text-align: center;
}

.quick-buttons {
  display: flex;
  gap: 12px;
  justify-content: center;
  flex-wrap: wrap;
}

.quick-btn {
  border-radius: 12px;
  padding: 10px 20px;
  height: auto;
  background: rgba(255, 255, 255, 0.9);
  border: 1.5px solid rgba(249, 115, 22, 0.15);
  color: #475569;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  font-weight: 500;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.quick-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(249, 115, 22, 0.08), transparent);
  transition: left 0.5s ease;
}

.quick-btn:hover::before {
  left: 100%;
}

.quick-btn:hover {
  background: rgba(255, 255, 255, 1);
  border-color: rgba(249, 115, 22, 0.4);
  color: #f97316;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(249, 115, 22, 0.2);
}

.btn-icon {
  font-size: 18px;
  display: inline-block;
  transition: transform 0.3s ease;
}

.quick-btn:hover .btn-icon {
  transform: scale(1.1) rotate(5deg);
}

/* 区域标题 */
.section {
  margin-bottom: 80px;
  position: relative;
}

.section-title {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 40px;
  color: #1e293b;
  position: relative;
  padding-bottom: 16px;
  display: inline-block;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 60px;
  height: 4px;
  background: linear-gradient(90deg, #f97316, #ea580c);
  border-radius: 2px;
}

/* 我的作品网格 */
.app-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 28px;
  margin-bottom: 40px;
}

/* 精选案例网格 */
.featured-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 28px;
  margin-bottom: 40px;
}

/* 分页 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 40px;
  padding-top: 32px;
  border-top: 1px solid rgba(249, 115, 22, 0.1);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .container {
    padding: 32px 20px;
  }

  .hero-section {
    padding: 50px 0;
  }

  .hero-title {
    font-size: 48px;
  }

  .main-input-card {
    padding: 32px 24px;
  }

  .app-grid,
  .featured-grid {
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 20px;
  }
}

@media (max-width: 768px) {
  .container {
    padding: 24px 16px;
  }

  .hero-section {
    padding: 40px 0;
    margin: 0 -20px 32px -20px;
  }

  .hero-title {
    font-size: 36px;
    letter-spacing: -1px;
  }

  .hero-description {
    font-size: 18px;
  }

  .main-input-card {
    padding: 24px 20px;
    border-radius: 20px;
  }

  .prompt-input {
    padding: 20px 120px 20px 20px;
    font-size: 15px;
  }

  .input-actions {
    bottom: 12px;
    right: 12px;
  }

  .create-btn {
    height: 40px;
    padding: 0 24px;
    font-size: 14px;
  }

  .quick-buttons {
    gap: 8px;
  }

  .quick-btn {
    padding: 8px 16px;
    font-size: 14px;
  }

  .section {
    margin-bottom: 60px;
  }

  .section-title {
    font-size: 28px;
    margin-bottom: 32px;
  }

  .app-grid,
  .featured-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .pagination-wrapper {
    margin-top: 32px;
    padding-top: 24px;
  }
}

@media (max-width: 480px) {
  .hero-title {
    font-size: 32px;
  }

  .hero-description {
    font-size: 16px;
  }

  .main-input-card {
    padding: 20px 16px;
  }

  .prompt-input {
    padding: 16px 100px 16px 16px;
    font-size: 14px;
  }

  .create-btn {
    height: 36px;
    padding: 0 20px;
    font-size: 13px;
  }

  .quick-btn {
    padding: 6px 12px;
    font-size: 13px;
  }

  .section-title {
    font-size: 24px;
  }
}
</style>
