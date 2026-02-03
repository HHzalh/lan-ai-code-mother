<template>
  <div class="point-mall-wrapper">
    <!-- 动态渐变背景 -->
    <div class="gradient-bg"></div>

    <!-- 主容器 -->
    <div class="mall-container">
      <!-- 顶部积分卡片 - 不对称布局 -->
      <div class="points-hero-section">
        <div class="points-display-card glass-card">
          <div class="card-glow"></div>
          <div class="points-content">
            <div class="points-meta">
              <span class="points-label">可用积分</span>
              <span class="points-count">{{ accountInfo?.availablePoints ?? 0 }}</span>
            </div>
            <div class="points-actions">
              <button class="action-btn secondary" @click="goBack">返回</button>
            </div>
          </div>
          <div class="floating-shapes">
            <div class="shape shape-1"></div>
            <div class="shape shape-2"></div>
            <div class="shape shape-3"></div>
          </div>
        </div>
      </div>

      <!-- Bento Grid 不对称布局 -->
      <div class="bento-layout">
        <!-- 大卡片 - 如何获得积分 -->
        <div class="bento-item bento-large glass-card spotlight-card">
          <div class="card-glow pink-glow"></div>
          <div class="bento-header">
            <div class="header-icon">
              <svg
                fill="none"
                height="24"
                stroke="currentColor"
                stroke-width="2"
                viewBox="0 0 24 24"
                width="24"
              >
                <path d="M12 2L2 7l10 5 10-5-10-5z"></path>
                <path d="M2 17l10 5 10-5"></path>
                <path d="M2 12l10 5 10-5"></path>
              </svg>
            </div>
            <h3 class="bento-title">获得积分</h3>
            <span class="bento-badge">{{ incomeRules.length }} 种方式</span>
          </div>
          <div class="bento-content">
            <div v-for="rule in incomeRules" :key="rule.id" class="rule-row">
              <div class="rule-info">
                <div class="rule-name">{{ getRuleTitle(rule.ruleKey) }}</div>
                <div class="rule-detail">
                  {{ rule.ruleDesc || getRuleDefaultDesc(rule.ruleKey) }}
                </div>
              </div>
              <div class="rule-points gain">+{{ rule.ruleValue }}</div>
            </div>
          </div>
        </div>

        <!-- 中卡片 - 积分消耗 -->
        <div class="bento-item bento-medium glass-card">
          <div class="card-glow coral-glow"></div>
          <div class="bento-header">
            <div class="header-icon">
              <svg
                fill="none"
                height="24"
                stroke="currentColor"
                stroke-width="2"
                viewBox="0 0 24 24"
                width="24"
              >
                <circle cx="9" cy="21" r="1"></circle>
                <circle cx="20" cy="21" r="1"></circle>
                <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"></path>
              </svg>
            </div>
            <h3 class="bento-title">消费</h3>
          </div>
          <div class="bento-content compact">
            <div
              v-for="rule in expenseRules"
              :key="rule.id"
              :class="{ disabled: rule.status === 0 }"
              class="expense-item"
            >
              <span class="expense-name">{{ getRuleTitle(rule.ruleKey) }}</span>
              <span :class="{ disabled: rule.status === 0 }" class="expense-cost">
                {{ rule.status !== 0 ? `-${rule.ruleValue}` : '已禁用' }}
              </span>
            </div>
          </div>
        </div>

        <!-- 小卡片 - 快速提示 -->
        <div class="bento-item bento-small glass-card accent-card">
          <div class="card-glow yellow-glow"></div>
          <div class="bento-header">
            <div class="header-icon">
              <svg
                fill="none"
                height="20"
                stroke="currentColor"
                stroke-width="2"
                viewBox="0 0 24 24"
                width="20"
              >
                <circle cx="12" cy="12" r="10"></circle>
                <line x1="12" x2="12" y1="16" y2="12"></line>
                <line x1="12" x2="12.01" y1="8" y2="8"></line>
              </svg>
            </div>
            <h3 class="bento-title">提示</h3>
          </div>
          <div class="bento-content">
            <p class="tip-text">积分永久有效 · 支持多种获取方式</p>
          </div>
        </div>

        <!-- 宽卡片 - 常见问题 -->
        <div class="bento-item bento-wide glass-card">
          <div class="bento-header">
            <div class="header-icon">
              <svg
                fill="none"
                height="24"
                stroke="currentColor"
                stroke-width="2"
                viewBox="0 0 24 24"
                width="24"
              >
                <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
              </svg>
            </div>
            <h3 class="bento-title">常见问题</h3>
          </div>
          <div class="bento-content">
            <div class="faq-grid-compact">
              <div v-for="(faq, index) in faqs.slice(0, 4)" :key="index" class="faq-item-compact">
                <span class="faq-q">{{ faq.question }}</span>
                <span class="faq-a">{{ faq.answer }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getAllRules, getMyAccount } from '@/api/pointController'

const router = useRouter()
const accountInfo = ref<API.UserAccountVO | null>(null)
const rules = ref<API.PointRuleVO[]>([])

// 常见问题数据 - 简化表达
const faqs = [
  {
    question: '积分会过期吗？',
    answer: '永久有效，随时可用',
  },
  {
    question: '积分不够怎么办',
    answer: '签到、邀请好友都能获得积分',
  },
  {
    question: '邀请码在哪找',
    answer: '个人中心里有你的专属邀请码',
  },
  {
    question: '可以送人吗',
    answer: '不能转让，但可以邀请好友获得奖励',
  },
  {
    question: '连续签到有奖励吗',
    answer: '有，3天和7天都有额外积分',
  },
  {
    question: '最快获取方式',
    answer: '邀请好友注册是最快的方式',
  },
]

// 获得积分的规则键
const INCOME_RULE_KEYS = [
  'REGISTER_REWARD',
  'INVITE_NEW',
  'INVITE_REWARD',
  'SIGN_IN_BASE',
  'SIGN_IN_CONTINUOUS_3',
  'SIGN_IN_CONTINUOUS_7',
]

// 消耗积分的规则键
const EXPENSE_RULE_KEYS = ['GENERATE_COST', 'AI_MESSAGE_COST', 'DEPLOY_COST', 'DOWNLOAD_COST']

// 计算获得的积分规则(只显示启用的)
const incomeRules = computed(() => {
  return rules.value
    .filter((rule) => INCOME_RULE_KEYS.includes(rule.ruleKey || ''))
    .filter((rule) => rule.status !== 0)
})

// 计算消耗的积分规则(显示所有,包括禁用的)
const expenseRules = computed(() => {
  return rules.value.filter((rule) => EXPENSE_RULE_KEYS.includes(rule.ruleKey || ''))
})

// 获取规则标题
const getRuleTitle = (ruleKey?: string) => {
  const titleMap: Record<string, string> = {
    REGISTER_REWARD: '注册奖励',
    INVITE_NEW: '被邀请',
    INVITE_REWARD: '邀请好友',
    SIGN_IN_BASE: '每日签到',
    SIGN_IN_CONTINUOUS_3: '连续3天',
    SIGN_IN_CONTINUOUS_7: '连续7天',
    GENERATE_COST: '生成应用',
    AI_MESSAGE_COST: 'AI对话',
    DEPLOY_COST: '部署应用',
    DOWNLOAD_COST: '下载代码',
  }
  return titleMap[ruleKey || ''] || ruleKey || ''
}

// 获取规则默认描述
const getRuleDefaultDesc = (ruleKey?: string) => {
  const descMap: Record<string, string> = {
    REGISTER_REWARD: '新用户注册获得',
    INVITE_NEW: '使用邀请码注册',
    INVITE_REWARD: '成功邀请好友',
    SIGN_IN_BASE: '每日登录签到',
    SIGN_IN_CONTINUOUS_3: '连续签到3天额外',
    SIGN_IN_CONTINUOUS_7: '连续签到7天更多',
  }
  return descMap[ruleKey || ''] || ''
}

const loadAccountInfo = async () => {
  try {
    const res = await getMyAccount()
    if (res.data.code === 0 && res.data.data) {
      accountInfo.value = res.data.data
    }
  } catch (error) {
    console.error('加载积分账户失败：', error)
  }
}

const loadRules = async () => {
  try {
    const res = await getAllRules()
    if (res.data.code === 0 && res.data.data) {
      rules.value = res.data.data || []
    }
  } catch (error) {
    console.error('加载积分规则失败：', error)
  }
}

const goBack = () => {
  router.push('/user/profile')
}

onMounted(() => {
  loadAccountInfo()
  loadRules()

  // 鼠标跟随效果 - 用于聚光灯
  const handleMouseMove = (e: MouseEvent) => {
    const cards = document.querySelectorAll('.spotlight-card')
    cards.forEach((card) => {
      const rect = (card as HTMLElement).getBoundingClientRect()
      const x = e.clientX - rect.left
      const y = e.clientY - rect.top
      ;(card as HTMLElement).style.setProperty('--mouse-x', `${x}px`)
      ;(card as HTMLElement).style.setProperty('--mouse-y', `${y}px`)
    })
  }

  document.addEventListener('mousemove', handleMouseMove)

  return () => {
    document.removeEventListener('mousemove', handleMouseMove)
  }
})
</script>

<style scoped>
/* ========== 字体引入 ========== */
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@300;400;500;600;700&display=swap');

/* ========== 全局变量 ========== */
:root {
  --color-primary: #ff6b6b;
  --color-primary-light: #ff8787;
  --color-primary-dark: #fa5252;
  --color-secondary: #ffa8a8;
  --color-accent: #ffec99;
  --color-coral: #ff8a80;
  --color-yellow: #ffd93d;
  --text-primary: #2d3436;
  --text-secondary: #636e72;
  --bg-pink: #fff5f5;
  --glass-bg: rgba(255, 255, 255, 0.65);
  --glass-border: rgba(255, 255, 255, 0.85);
  --shadow-soft: 0 8px 32px rgba(255, 107, 107, 0.12);
  --shadow-hover: 0 12px 48px rgba(255, 107, 107, 0.18);
}

/* ========== 主容器 ========== */
.point-mall-wrapper {
  min-height: 100vh;
  position: relative;
  overflow: hidden;
  font-family:
    'Noto Sans SC',
    -apple-system,
    BlinkMacSystemFont,
    sans-serif;
}

/* ========== 动态渐变背景 ========== */
.gradient-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background:
    radial-gradient(circle at 25% 25%, rgba(255, 107, 107, 0.15) 0%, transparent 50%),
    radial-gradient(circle at 75% 75%, rgba(255, 168, 168, 0.12) 0%, transparent 50%),
    radial-gradient(circle at 50% 50%, rgba(255, 236, 153, 0.08) 0%, transparent 50%);
  animation: gradientMove 20s ease-in-out infinite alternate;
  z-index: 0;
  pointer-events: none;
}

@keyframes gradientMove {
  0% {
    transform: scale(1) rotate(0deg);
  }
  100% {
    transform: scale(1.1) rotate(5deg);
  }
}

/* ========== 内容容器 ========== */
.mall-container {
  position: relative;
  z-index: 2;
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 24px 80px;
}

/* ========== 玻璃拟态卡片基类 ========== */
.glass-card {
  background: var(--glass-bg);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  border: 1px solid var(--glass-border);
  border-radius: 24px;
  box-shadow: var(--shadow-soft);
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
  position: relative;
  overflow: hidden;
}

.glass-card:hover {
  box-shadow: var(--shadow-hover);
  transform: translateY(-4px) scale(1.01);
}

/* ========== 卡片发光效果 ========== */
.card-glow {
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, var(--color-primary) 0%, transparent 70%);
  opacity: 0;
  transition: opacity 0.6s cubic-bezier(0.34, 1.56, 0.64, 1);
  pointer-events: none;
}

.pink-glow {
  background: radial-gradient(circle, rgba(255, 107, 107, 0.2) 0%, transparent 70%);
}

.coral-glow {
  background: radial-gradient(circle, rgba(255, 138, 128, 0.2) 0%, transparent 70%);
}

.yellow-glow {
  background: radial-gradient(circle, rgba(255, 217, 61, 0.2) 0%, transparent 70%);
}

.glass-card:hover .card-glow {
  opacity: 1;
}

/* ========== 聚光灯效果 ========== */
.spotlight-card::before {
  content: '';
  position: absolute;
  top: var(--mouse-y, 50%);
  left: var(--mouse-x, 50%);
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.15) 0%, transparent 70%);
  transform: translate(-50%, -50%);
  pointer-events: none;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.spotlight-card:hover::before {
  opacity: 1;
}

/* ========== 积分展示区域 ========== */
.points-hero-section {
  margin-bottom: 40px;
}

.points-display-card {
  padding: 48px;
  position: relative;
}

.points-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  z-index: 2;
}

.points-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.points-label {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.points-count {
  font-size: 64px;
  font-weight: 700;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-family: 'Noto Sans SC', sans-serif;
  line-height: 1;
}

/* ========== 浮动装饰形状 ========== */
.floating-shapes {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  pointer-events: none;
}

.shape {
  position: absolute;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-secondary) 100%);
  opacity: 0.1;
  filter: blur(40px);
}

.shape-1 {
  width: 200px;
  height: 200px;
  top: -50px;
  right: -50px;
  animation: float1 15s ease-in-out infinite;
}

.shape-2 {
  width: 150px;
  height: 150px;
  bottom: -30px;
  left: 20%;
  animation: float2 18s ease-in-out infinite reverse;
}

.shape-3 {
  width: 100px;
  height: 100px;
  top: 40%;
  right: 30%;
  animation: float3 12s ease-in-out infinite;
}

@keyframes float1 {
  0%,
  100% {
    transform: translate(0, 0) rotate(0deg);
  }
  33% {
    transform: translate(20px, -20px) rotate(120deg);
  }
  66% {
    transform: translate(-10px, 10px) rotate(240deg);
  }
}

@keyframes float2 {
  0%,
  100% {
    transform: translate(0, 0) scale(1);
  }
  50% {
    transform: translate(-30px, 30px) scale(1.1);
  }
}

@keyframes float3 {
  0%,
  100% {
    transform: translate(0, 0) rotate(0deg);
  }
  50% {
    transform: translate(15px, -15px) rotate(180deg);
  }
}

/* ========== 操作按钮 ========== */
.action-btn {
  padding: 12px 32px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  border: none;
  outline: none;
}

.action-btn.secondary {
  background: rgba(255, 107, 107, 0.1);
  color: var(--color-primary);
  border: 2px solid rgba(255, 107, 107, 0.2);
}

.action-btn.secondary:hover {
  background: var(--color-primary);
  color: white;
  transform: scale(1.05);
}

/* ========== Bento Grid 布局 ========== */
.bento-layout {
  display: grid;
  grid-template-columns: repeat(12, 1fr);
  grid-auto-rows: minmax(140px, auto);
  gap: 24px;
}

.bento-item {
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.bento-large {
  grid-column: span 12;
  grid-row: span 2;
}

@media (min-width: 768px) {
  .bento-large {
    grid-column: span 8;
  }

  .bento-medium {
    grid-column: span 4;
    grid-row: span 2;
  }

  .bento-small {
    grid-column: span 4;
    grid-row: span 1;
  }

  .bento-wide {
    grid-column: span 12;
    grid-row: span 1;
  }
}

/* ========== Bento 卡片内容 ========== */
.bento-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(255, 107, 107, 0.1);
}

.header-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  border-radius: 12px;
  color: white;
  flex-shrink: 0;
}

.bento-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
  flex: 1;
}

.bento-badge {
  padding: 6px 14px;
  background: rgba(255, 107, 107, 0.1);
  color: var(--color-primary);
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.bento-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* ========== 规则行 ========== */
.rule-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: rgba(255, 107, 107, 0.04);
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.rule-row:hover {
  background: rgba(255, 107, 107, 0.08);
  transform: translateX(4px);
}

.rule-info {
  flex: 1;
}

.rule-name {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.rule-detail {
  font-size: 13px;
  color: var(--text-secondary);
  line-height: 1.4;
}

.rule-points {
  font-size: 20px;
  font-weight: 700;
  font-family: 'Noto Sans SC', sans-serif;
}

.rule-points.gain {
  color: #52c41a;
}

/* ========== 消费列表 ========== */
.bento-content.compact {
  gap: 12px;
}

.expense-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: rgba(255, 138, 128, 0.05);
  border-radius: 10px;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.expense-item:hover:not(.disabled) {
  background: rgba(255, 138, 128, 0.1);
  transform: translateX(4px);
}

.expense-item.disabled {
  opacity: 0.5;
}

.expense-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
}

.expense-cost {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-primary);
}

.expense-cost.disabled {
  color: var(--text-secondary);
  font-size: 12px;
}

/* ========== 提示卡片 ========== */
.tip-text {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.6;
  text-align: center;
  padding: 20px 0;
}

/* ========== FAQ 紧凑网格 ========== */
.faq-grid-compact {
  display: grid;
  gap: 16px;
}

.faq-item-compact {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 16px;
  background: rgba(255, 107, 107, 0.04);
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.faq-item-compact:hover {
  background: rgba(255, 107, 107, 0.08);
  transform: translateY(-2px);
}

.faq-q {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

.faq-a {
  font-size: 13px;
  color: var(--text-secondary);
  line-height: 1.5;
}

/* ========== 响应式设计 ========== */
@media (max-width: 768px) {
  .mall-container {
    padding: 24px 16px 60px;
  }

  .points-display-card {
    padding: 32px 24px;
  }

  .points-content {
    flex-direction: column;
    gap: 24px;
    text-align: center;
  }

  .points-count {
    font-size: 48px;
  }

  .bento-layout {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .bento-item {
    grid-column: span 1 !important;
    grid-row: auto !important;
  }

  .bento-header {
    margin-bottom: 16px;
    padding-bottom: 12px;
  }

  .bento-title {
    font-size: 16px;
  }
}
</style>
