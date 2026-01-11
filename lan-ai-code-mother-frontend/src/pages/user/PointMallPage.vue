<template>
  <div class="point-mall-wrapper">
    <section class="mall-hero">
      <div class="hero-content">
        <h2>积分商城</h2>
        <p class="subtitle">了解积分规则,合理使用积分,享受更好的服务</p>
      </div>
      <div class="current-points-card">
        <div class="points-label">当前积分</div>
        <div class="points-value">{{ accountInfo?.availablePoints ?? 0 }}</div>
      </div>
    </section>

    <section class="mall-card">
      <!-- 积分规则说明 -->
      <div class="rule-section">
        <div class="section-header">
          <FileTextOutlined class="section-icon" />
          <h3>积分规则说明</h3>
        </div>
        <p class="rule-desc">
          积分是平台的虚拟货币,可用于体验各种AI服务。通过以下方式可以获得或消耗积分:
        </p>
      </div>

      <!-- 获得积分 -->
      <div class="rule-section">
        <div class="section-header">
          <ThunderboltOutlined class="section-icon" />
          <h3>获得积分</h3>
        </div>
        <div class="rule-list">
          <div class="rule-item">
            <div class="rule-item-icon">
              <UserAddOutlined />
            </div>
            <div class="rule-item-content">
              <div class="rule-item-title">用户注册</div>
              <div class="rule-item-desc">新用户注册成功即可获得积分奖励</div>
            </div>
            <div class="rule-item-points income">+30</div>
          </div>
          <div class="rule-item">
            <div class="rule-item-icon">
              <LinkOutlined />
            </div>
            <div class="rule-item-content">
              <div class="rule-item-title">邀请码注册</div>
              <div class="rule-item-desc">使用他人邀请码注册可获得额外积分</div>
            </div>
            <div class="rule-item-points income">+50</div>
          </div>
          <div class="rule-item">
            <div class="rule-item-icon">
              <TeamOutlined />
            </div>
            <div class="rule-item-content">
              <div class="rule-item-title">邀请他人</div>
              <div class="rule-item-desc">成功邀请他人注册并使用您的邀请码</div>
            </div>
            <div class="rule-item-points income">+50</div>
          </div>
          <div class="rule-item">
            <div class="rule-item-icon">
              <CalendarOutlined />
            </div>
            <div class="rule-item-content">
              <div class="rule-item-title">每日签到</div>
              <div class="rule-item-desc">每日签到可获得积分</div>
            </div>
            <div class="rule-item-points income">+10</div>
          </div>
        </div>
      </div>

      <!-- 消耗积分 -->
      <div class="rule-section">
        <div class="section-header">
          <ShoppingCartOutlined class="section-icon" />
          <h3>消耗积分</h3>
        </div>
        <div class="rule-list">
          <div class="rule-item">
            <div class="rule-item-icon">
              <RocketOutlined />
            </div>
            <div class="rule-item-content">
              <div class="rule-item-title">创建应用</div>
              <div class="rule-item-desc">使用AI生成新的应用项目</div>
            </div>
            <div class="rule-item-points expense">-5</div>
          </div>
          <div class="rule-item">
            <div class="rule-item-icon">
              <MessageOutlined />
            </div>
            <div class="rule-item-content">
              <div class="rule-item-title">AI对话</div>
              <div class="rule-item-desc">与AI助手进行对话交流</div>
            </div>
            <div class="rule-item-points expense">-2</div>
          </div>
        </div>
      </div>
    </section>

    <!-- 积分使用建议 -->
    <section class="mall-card">
      <div class="section-header">
        <BulbOutlined class="section-icon" />
        <h3>积分使用建议</h3>
      </div>
      <div class="suggestions-list">
        <div class="suggestion-item">
          <div class="suggestion-icon">
            <AimOutlined />
          </div>
          <div class="suggestion-text">合理规划:根据您的使用频率合理分配积分,优先体验核心功能</div>
        </div>
        <div class="suggestion-item">
          <div class="suggestion-icon">
            <TeamOutlined />
          </div>
          <div class="suggestion-text">邀请好友:通过邀请好友注册是快速获得积分的有效方式</div>
        </div>
        <div class="suggestion-item">
          <div class="suggestion-icon">
            <SyncOutlined />
          </div>
          <div class="suggestion-text">循序渐进:从简单的AI对话开始,逐步尝试创建更复杂的应用</div>
        </div>
        <div class="suggestion-item">
          <div class="suggestion-icon">
            <BarChartOutlined />
          </div>
          <div class="suggestion-text">关注余额:定期查看积分余额,避免在关键时刻积分不足</div>
        </div>
      </div>
    </section>

    <!-- 常见问题 -->
    <section class="mall-card">
      <div class="section-header">
        <QuestionCircleOutlined class="section-icon" />
        <h3>常见问题</h3>
      </div>
      <div class="faq-list">
        <div class="faq-item">
          <RightOutlined class="faq-icon" />
          <div class="faq-text">积分有有效期吗?</div>
        </div>
        <div class="faq-item">
          <RightOutlined class="faq-icon" />
          <div class="faq-text">积分不足时怎么办?</div>
        </div>
        <div class="faq-item">
          <RightOutlined class="faq-icon" />
          <div class="faq-text">邀请码在哪里查看?</div>
        </div>
        <div class="faq-item">
          <RightOutlined class="faq-icon" />
          <div class="faq-text">积分可以转让吗?</div>
        </div>
      </div>
    </section>

    <div class="back-button-container">
      <a-button class="back-button" size="large" type="primary" @click="goBack">
        <ArrowLeftOutlined />
        返回个人中心
      </a-button>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  AimOutlined,
  ArrowLeftOutlined,
  BarChartOutlined,
  BulbOutlined,
  CalendarOutlined,
  FileTextOutlined,
  LinkOutlined,
  MessageOutlined,
  QuestionCircleOutlined,
  RightOutlined,
  RocketOutlined,
  ShoppingCartOutlined,
  SyncOutlined,
  TeamOutlined,
  ThunderboltOutlined,
  UserAddOutlined,
} from '@ant-design/icons-vue'
import { getMyAccount } from '@/api/pointController'

const router = useRouter()
const accountInfo = ref<API.UserAccountVO | null>(null)

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

const goBack = () => {
  router.push('/user/profile')
}

onMounted(() => {
  loadAccountInfo()
})
</script>

<style scoped>
.point-mall-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px 0 64px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.mall-hero {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 18px;
  padding: 32px 40px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.3);
  color: white;
}

.hero-content h2 {
  margin: 0 0 8px 0;
  font-size: 32px;
  font-weight: 600;
  color: white;
}

.hero-content .subtitle {
  margin: 0;
  font-size: 16px;
  opacity: 0.9;
  color: white;
}

.current-points-card {
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 20px 32px;
  text-align: center;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.points-label {
  font-size: 14px;
  opacity: 0.9;
  margin-bottom: 8px;
}

.points-value {
  font-size: 32px;
  font-weight: 600;
  color: white;
}

.mall-card {
  background: #fff;
  border-radius: 18px;
  padding: 32px 40px;
  box-shadow: 0 12px 35px rgba(15, 39, 80, 0.07);
  border: 1px solid #f0f2f5;
}

.rule-section {
  margin-bottom: 40px;
}

.rule-section:last-child {
  margin-bottom: 0;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.section-icon {
  font-size: 24px;
  color: #1890ff;
}

.section-header h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #1f2d3d;
}

.rule-desc {
  margin: 0 0 24px 0;
  font-size: 15px;
  color: #5f6b7c;
  line-height: 1.6;
}

.rule-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.rule-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
  transition: all 0.3s;
}

.rule-item:hover {
  background: #f0f2f5;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.rule-item-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  flex-shrink: 0;
}

.rule-item-content {
  flex: 1;
}

.rule-item-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2d3d;
  margin-bottom: 4px;
}

.rule-item-desc {
  font-size: 14px;
  color: #5f6b7c;
  line-height: 1.5;
}

.rule-item-points {
  font-size: 24px;
  font-weight: 600;
  min-width: 60px;
  text-align: right;
}

.rule-item-points.income {
  color: #52c41a;
}

.rule-item-points.expense {
  color: #ff4d4f;
}

.suggestions-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.suggestion-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
}

.suggestion-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #1890ff;
  flex-shrink: 0;
}

.suggestion-text {
  font-size: 15px;
  color: #5f6b7c;
  line-height: 1.6;
}

.faq-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.faq-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s;
}

.faq-item:hover {
  background: #f0f2f5;
  transform: translateX(4px);
}

.faq-icon {
  color: #8c8c8c;
  font-size: 14px;
}

.faq-text {
  font-size: 15px;
  color: #1f2d3d;
}

.back-button-container {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

.back-button {
  height: 48px;
  padding: 0 32px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.back-button:hover {
  background: linear-gradient(135deg, #5568d3 0%, #6a3d91 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

@media (max-width: 768px) {
  .mall-hero {
    flex-direction: column;
    gap: 24px;
    text-align: center;
  }

  .mall-card {
    padding: 24px;
  }

  .rule-item {
    flex-direction: column;
    align-items: flex-start;
  }

  .rule-item-points {
    align-self: flex-end;
  }
}
</style>
