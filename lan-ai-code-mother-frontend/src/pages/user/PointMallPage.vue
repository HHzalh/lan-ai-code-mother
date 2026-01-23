<template>
  <div class="point-mall-wrapper">
    <!-- Hero 区域 -->
    <section class="mall-hero">
      <div class="hero-overlay"></div>
      <div class="hero-content">
        <div class="hero-badge">
          <ShoppingCartOutlined />
          <span>积分商城</span>
        </div>
        <h1 class="hero-title">
          <span class="title-number"></span>
          积分规则中心
        </h1>
        <p class="hero-subtitle">
          <FileTextOutlined />
          了解积分规则，合理使用积分，享受更好的服务
        </p>

        <!-- 当前积分卡片 -->
        <div class="current-points-card purple-card">
          <div class="points-icon">
            <WalletOutlined />
          </div>
          <div class="points-info">
            <div class="points-label">当前积分</div>
            <div class="points-value">{{ accountInfo?.availablePoints ?? 0 }}</div>
          </div>
          <div class="points-divider"></div>
          <div class="points-action">
            <a-button size="large" type="primary" @click="goBack">
              <template #icon>
                <ArrowLeftOutlined />
              </template>
              返回个人中心
            </a-button>
          </div>
        </div>
      </div>
    </section>

    <!-- 主内容区 -->
    <section class="mall-content">
      <!-- 积分规则说明 -->
      <div class="rule-card intro-card success-card">
        <div class="card-header">
          <InfoCircleOutlined class="header-icon" />
          <h3>积分规则说明</h3>
        </div>
        <p class="rule-desc">
          积分是平台的虚拟货币，可用于体验各种AI服务。通过以下方式可以获得或消耗积分：
        </p>
      </div>

      <!-- 获得积分 -->
      <div class="rule-card info-card">
        <div class="card-header">
          <ThunderboltOutlined class="header-icon income-icon" />
          <h3>获得积分</h3>
          <a-tag color="green">{{ incomeRules.length }} 项可用</a-tag>
        </div>
        <div class="rule-grid">
          <div v-for="rule in incomeRules" :key="rule.id" class="rule-item income-item">
            <div class="rule-item-icon">
              <component :is="getIncomeIcon(rule.ruleKey)" />
            </div>
            <div class="rule-item-content">
              <div class="rule-item-title">{{ getRuleTitle(rule.ruleKey) }}</div>
              <div class="rule-item-desc">
                {{ rule.ruleDesc || getRuleDefaultDesc(rule.ruleKey) }}
              </div>
            </div>
            <div class="rule-item-points income">+{{ rule.ruleValue }}</div>
          </div>
        </div>
      </div>

      <!-- 消耗积分 -->
      <div class="rule-card info-card">
        <div class="card-header">
          <ShoppingCartOutlined class="header-icon expense-icon" />
          <h3>消耗积分</h3>
          <a-tag color="red">{{ expenseRules.length }} 项规则</a-tag>
        </div>
        <div class="rule-grid">
          <div v-for="rule in expenseRules" :key="rule.id" class="rule-item expense-item">
            <div :class="{ disabled: rule.status === 0 }" class="rule-item-icon">
              <component :is="getExpenseIcon(rule.ruleKey)" />
            </div>
            <div class="rule-item-content">
              <div class="rule-item-title">{{ getRuleTitle(rule.ruleKey) }}</div>
              <div class="rule-item-desc">{{ rule.ruleDesc }}</div>
            </div>
            <div v-if="rule.status !== 0" class="rule-item-points expense">
              -{{ rule.ruleValue }}
            </div>
            <div v-else class="rule-item-points disabled">已禁用</div>
          </div>
        </div>
      </div>

      <!-- 积分使用建议 -->
      <div class="suggestion-card cyan-card">
        <div class="card-header">
          <BulbOutlined class="header-icon" />
          <h3>积分使用建议</h3>
        </div>
        <div class="suggestions-grid">
          <div class="suggestion-item">
            <div class="suggestion-icon">
              <AimOutlined />
            </div>
            <div class="suggestion-content">
              <div class="suggestion-title">合理规划</div>
              <div class="suggestion-text">根据您的使用频率合理分配积分，优先体验核心功能</div>
            </div>
          </div>
          <div class="suggestion-item">
            <div class="suggestion-icon">
              <TeamOutlined />
            </div>
            <div class="suggestion-content">
              <div class="suggestion-title">邀请好友</div>
              <div class="suggestion-text">通过邀请好友注册是快速获得积分的有效方式</div>
            </div>
          </div>
          <div class="suggestion-item">
            <div class="suggestion-icon">
              <SyncOutlined />
            </div>
            <div class="suggestion-content">
              <div class="suggestion-title">循序渐进</div>
              <div class="suggestion-text">从简单的AI对话开始，逐步尝试创建更复杂的应用</div>
            </div>
          </div>
          <div class="suggestion-item">
            <div class="suggestion-icon">
              <BarChartOutlined />
            </div>
            <div class="suggestion-content">
              <div class="suggestion-title">关注余额</div>
              <div class="suggestion-text">定期查看积分余额，避免在关键时刻积分不足</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 常见问题 -->
      <div class="faq-card warning-card">
        <div class="card-header">
          <QuestionCircleOutlined class="header-icon" />
          <h3>常见问题</h3>
          <a-tag color="orange">6 个问题</a-tag>
        </div>
        <div class="faq-grid">
          <div v-for="(faq, index) in faqs" :key="index" class="faq-item">
            <div class="faq-number">{{ String(index + 1).padStart(2, '0') }}</div>
            <div class="faq-content">
              <div class="faq-question">{{ faq.question }}</div>
              <div class="faq-answer">{{ faq.answer }}</div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  AimOutlined,
  ArrowLeftOutlined,
  BarChartOutlined,
  BulbOutlined,
  CalendarOutlined,
  CodeOutlined,
  DownloadOutlined,
  FileTextOutlined,
  GiftOutlined,
  InfoCircleOutlined,
  LinkOutlined,
  MessageOutlined,
  QuestionCircleOutlined,
  RocketOutlined,
  ShoppingCartOutlined,
  SyncOutlined,
  TeamOutlined,
  ThunderboltOutlined,
  UserAddOutlined,
  WalletOutlined,
} from '@ant-design/icons-vue'
import { getAllRules, getMyAccount } from '@/api/pointController'

const router = useRouter()
const accountInfo = ref<API.UserAccountVO | null>(null)
const rules = ref<API.PointRuleVO[]>([])

// 常见问题数据
const faqs = [
  {
    question: '积分有有效期吗？',
    answer: '积分永久有效，不会过期，您可以随时使用。',
  },
  {
    question: '积分不足时怎么办？',
    answer:
      '您可以通过每日签到、邀请好友注册等方式获得积分。建议提前规划积分使用，避免在需要时积分不足。',
  },
  {
    question: '邀请码在哪里查看？',
    answer: '您可以在个人中心页面查看您的专属邀请码，分享给好友使用可获得奖励积分。',
  },
  {
    question: '积分可以转让吗？',
    answer: '积分不支持转让，但您可以通过邀请好友注册的方式帮助好友获得积分奖励。',
  },
  {
    question: '签到有额外奖励吗？',
    answer: '连续签到会有额外奖励，连续3天可获得额外积分，连续7天可获得更多奖励。',
  },
  {
    question: '如何快速获得积分？',
    answer:
      '最快速的方式是邀请好友注册，每成功邀请一位好友可获得奖励积分。同时每日签到也是稳定的积分来源。',
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

// 计算获得的积分规则（只显示启用的）
const incomeRules = computed(() => {
  return rules.value
    .filter((rule) => INCOME_RULE_KEYS.includes(rule.ruleKey || ''))
    .filter((rule) => rule.status !== 0)
})

// 计算消耗的积分规则（显示所有，包括禁用的）
const expenseRules = computed(() => {
  return rules.value.filter((rule) => EXPENSE_RULE_KEYS.includes(rule.ruleKey || ''))
})

// 获取规则标题
const getRuleTitle = (ruleKey?: string) => {
  const titleMap: Record<string, string> = {
    REGISTER_REWARD: '用户注册',
    INVITE_NEW: '邀请码注册',
    INVITE_REWARD: '邀请他人',
    SIGN_IN_BASE: '每日签到',
    SIGN_IN_CONTINUOUS_3: '连续签到3天',
    SIGN_IN_CONTINUOUS_7: '连续签到7天',
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
    REGISTER_REWARD: '新用户注册即可获得积分奖励',
    INVITE_NEW: '被邀请人使用邀请码注册可获得奖励',
    INVITE_REWARD: '成功邀请好友注册可获得积分',
    SIGN_IN_BASE: '每日登录签到即可获得积分',
    SIGN_IN_CONTINUOUS_3: '连续签到3天可获得额外积分奖励',
    SIGN_IN_CONTINUOUS_7: '连续签到7天可获得更多积分奖励',
  }
  return descMap[ruleKey || ''] || ''
}

// 获取获得积分图标
const getIncomeIcon = (ruleKey?: string) => {
  const iconMap: Record<string, any> = {
    REGISTER_REWARD: UserAddOutlined,
    INVITE_NEW: LinkOutlined,
    INVITE_REWARD: TeamOutlined,
    SIGN_IN_BASE: CalendarOutlined,
    SIGN_IN_CONTINUOUS_3: GiftOutlined,
    SIGN_IN_CONTINUOUS_7: GiftOutlined,
  }
  return iconMap[ruleKey || ''] || GiftOutlined
}

// 获取消耗积分图标
const getExpenseIcon = (ruleKey?: string) => {
  const iconMap: Record<string, any> = {
    GENERATE_COST: CodeOutlined,
    AI_MESSAGE_COST: MessageOutlined,
    DEPLOY_COST: RocketOutlined,
    DOWNLOAD_COST: DownloadOutlined,
  }
  return iconMap[ruleKey || ''] || MessageOutlined
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
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Serif+SC:wght@400;600;700&family=Noto+Sans+SC:wght@300;400;500;600;700&display=swap');

:root {
  --color-primary: #f97316;
  --color-primary-dark: #ea580c;
  --color-primary-light: #fbbf24;
  --color-text: #1e293b;
  --color-text-secondary: #64748b;
  --color-border: #e2e8f0;
  --color-bg-hover: #f8fafc;
  --font-serif: 'Noto Serif SC', serif;
  --font-sans: 'Noto Sans SC', sans-serif;
}

.point-mall-wrapper {
  min-height: 100vh;
  background: #f8fafc;
  padding-bottom: 40px;
}

/* Hero 区域 */
.mall-hero {
  position: relative;
  background-image: url('https://images.unsplash.com/photo-1556742049-0cfed4f6a45d?w=1920&q=80');
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
  border-radius: 0;
  padding: 60px 24px;
  margin-bottom: 32px;
  overflow: hidden;
}

.hero-overlay {
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
  backdrop-filter: blur(2px);
}

.hero-content {
  position: relative;
  max-width: 1200px;
  margin: 0 auto;
  z-index: 1;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(10px);
  padding: 8px 20px;
  border-radius: 24px;
  color: white;
  font-size: 13px;
  font-weight: 500;
  letter-spacing: 0.05em;
  margin-bottom: 20px;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.hero-title {
  font-family: var(--font-serif);
  font-size: 48px;
  font-weight: 700;
  color: white;
  text-shadow:
    0 2px 4px rgba(0, 0, 0, 0.25),
    0 4px 12px rgba(0, 0, 0, 0.2);
  margin: 0 0 16px;
  line-height: 1.2;
  letter-spacing: -0.02em;
}

.title-number {
  display: inline-block;
  font-size: 72px;
  font-weight: 700;
  color: rgba(255, 255, 255, 0.3);
  margin-right: 16px;
  line-height: 1;
}

.hero-subtitle {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  color: rgba(255, 255, 255, 0.95);
  text-shadow:
    0 2px 4px rgba(0, 0, 0, 0.25),
    0 1px 8px rgba(0, 0, 0, 0.2);
  margin: 0 0 40px;
  font-weight: 400;
}

/* 当前积分卡片 */
.current-points-card {
  display: flex;
  align-items: center;
  gap: 24px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  padding: 32px;
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.current-points-card:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
}

.points-icon {
  width: 64px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 16px;
  font-size: 32px;
  color: white;
  flex-shrink: 0;
}

.points-info {
  flex: 1;
}

.points-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.85);
  margin-bottom: 8px;
  font-weight: 500;
}

.points-value {
  font-size: 42px;
  font-weight: 700;
  color: white;
  line-height: 1;
  font-family: var(--font-serif);
}

.points-divider {
  width: 1px;
  height: 60px;
  background: rgba(255, 255, 255, 0.3);
  flex-shrink: 0;
}

/* 主内容区 */
.mall-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 16px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 卡片通用样式 */
.rule-card,
.suggestion-card,
.faq-card,
.intro-card {
  background: white;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid var(--color-border);
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
  font-size: 24px;
  color: var(--color-primary);
}

.income-icon {
  color: #52c41a;
}

.expense-icon {
  color: #ff4d4f;
}

.card-header h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: var(--color-text);
  flex: 1;
  font-family: var(--font-serif);
}

/* 介绍卡片 */
.rule-desc {
  font-size: 16px;
  color: var(--color-text-secondary);
  line-height: 1.8;
  margin: 0;
}

/* 规则网格 */
.rule-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.rule-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 24px;
  background: var(--color-bg-hover);
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid transparent;
}

.rule-item:hover {
  background: #fff7ed;
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(249, 115, 22, 0.1);
  border-color: var(--color-primary);
}

.income-item {
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
}

.income-item:hover {
  background: linear-gradient(135deg, #dcfce7 0%, #d1fae5 100%);
}

.expense-item {
  background: linear-gradient(135deg, #fef2f2 0%, #fee2e2 100%);
}

.expense-item:hover {
  background: linear-gradient(135deg, #fef2f2 0%, #fecaca 100%);
}

.rule-item-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  flex-shrink: 0;
}

.rule-item-icon.disabled {
  background: #d1d5db;
  color: #9ca3af;
}

.rule-item-content {
  flex: 1;
}

.rule-item-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text);
  margin-bottom: 6px;
}

.rule-item-desc {
  font-size: 14px;
  color: var(--color-text-secondary);
  line-height: 1.5;
}

.rule-item-points {
  font-size: 24px;
  font-weight: 700;
  min-width: 80px;
  text-align: right;
  font-family: var(--font-serif);
}

.rule-item-points.income {
  color: #52c41a;
}

.rule-item-points.expense {
  color: #ff4d4f;
}

.rule-item-points.disabled {
  color: #9ca3af;
  font-size: 14px;
  font-weight: 500;
}

/* 建议网格 */
.suggestions-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.suggestion-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 24px;
  background: linear-gradient(135deg, #fffbeb 0%, #fef3c7 100%);
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid transparent;
}

.suggestion-item:hover {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(251, 191, 36, 0.15);
  border-color: var(--color-primary-light);
}

.suggestion-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: linear-gradient(135deg, var(--color-primary-light) 0%, var(--color-primary) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  flex-shrink: 0;
}

.suggestion-content {
  flex: 1;
}

.suggestion-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text);
  margin-bottom: 6px;
}

.suggestion-text {
  font-size: 14px;
  color: var(--color-text-secondary);
  line-height: 1.6;
}

/* FAQ 网格 */
.faq-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.faq-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 24px;
  background: var(--color-bg-hover);
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid transparent;
}

.faq-item:hover {
  background: #fff7ed;
  transform: translateX(4px);
  border-color: var(--color-primary);
}

.faq-number {
  width: 40px;
  height: 32px;
  border-radius: 10px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  color: white;
  font-size: 18px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-family: var(--font-serif);
}

.faq-content {
  flex: 1;
}

.faq-question {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text);
  margin-bottom: 10px;
}

.faq-answer {
  font-size: 14px;
  color: var(--color-text-secondary);
  line-height: 1.7;
}

/* 按钮样式 */
:deep(.ant-btn) {
  border-radius: 8px;
  font-weight: 500;
  height: 44px;
}

:deep(.ant-btn-primary) {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  border: none;
}

:deep(.ant-btn-primary:hover) {
  background: linear-gradient(135deg, var(--color-primary-dark) 0%, #dc2626 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(249, 115, 22, 0.3);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .rule-grid,
  .suggestions-grid,
  .faq-grid {
    grid-template-columns: 1fr;
  }

  .current-points-card {
    flex-direction: column;
    text-align: center;
  }

  .points-divider {
    width: 100%;
    height: 1px;
  }
}

@media (max-width: 768px) {
  .mall-hero {
    padding: 40px 16px;
  }

  .hero-title {
    font-size: 32px;
  }

  .title-number {
    font-size: 48px;
  }

  .hero-subtitle {
    font-size: 16px;
    flex-direction: column;
    align-items: flex-start;
  }

  .points-value {
    font-size: 36px;
  }

  .mall-content {
    padding: 0 16px;
  }

  .rule-card,
  .suggestion-card,
  .faq-card,
  .intro-card {
    padding: 24px;
  }

  .rule-item,
  .suggestion-item,
  .faq-item {
    padding: 20px;
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
