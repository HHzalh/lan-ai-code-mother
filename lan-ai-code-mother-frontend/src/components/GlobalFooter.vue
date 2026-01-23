<template>
  <a-layout-footer class="footer">
    <div class="footer-container">
      <!-- 主要内容区域 -->
      <div class="footer-content">
        <!-- 快速开始 -->
        <div class="footer-section">
          <h3 class="section-title">快速开始</h3>
          <ul class="footer-links">
            <li>
              <router-link to="/" @click="scrollToCreate">创建应用</router-link>
            </li>
            <li>
              <router-link to="/" @click="scrollToFeatured">精选案例</router-link>
            </li>
          </ul>
        </div>

        <!-- 帮助支持 -->
        <div class="footer-section">
          <h3 class="section-title">帮助支持</h3>
          <ul class="footer-links">
            <li>
              <router-link to="/help/docs#introduction">使用帮助</router-link>
            </li>
            <li>
              <router-link to="/help/docs#faq">常见问题</router-link>
            </li>
          </ul>
        </div>

        <!-- 关于我们 -->
        <div class="footer-section">
          <h3 class="section-title">关于我们</h3>
          <ul class="footer-links">
            <li>
              <router-link to="/help/docs#introduction">项目介绍</router-link>
            </li>
            <li>
              <a href="https://www.gitee.com/hhzalh" rel="noopener noreferrer" target="_blank">
                联系作者
              </a>
            </li>
            <li>
              <a href="https://www.gitee.com/hhzalh" rel="noopener noreferrer" target="_blank">
                Gitee 仓库
              </a>
            </li>
            <li>
              <router-link to="/help/docs#terms">服务条款</router-link>
            </li>
          </ul>
        </div>
      </div>

      <!-- 底部版权信息 -->
      <div class="footer-bottom">
        <div class="footer-divider"></div>
        <div class="copyright-info">
          <p class="copyright">
            © 2026
            <a
              class="author-link"
              href="https://www.gitee.com/hhzalh"
              rel="noopener noreferrer"
              target="_blank"
            >
              蓝海智造
            </a>
            . All rights reserved.
          </p>
          <p class="slogan">让想法，瞬间成为可用的应用</p>
        </div>
      </div>
    </div>
  </a-layout-footer>
</template>

<script lang="ts" setup>
import { useRouter } from 'vue-router'

const router = useRouter()

// 滚动到创建应用区域
const scrollToCreate = () => {
  router.push('/').then(() => {
    setTimeout(() => {
      const element = document.querySelector('.main-input-card')
      if (element) {
        element.scrollIntoView({ behavior: 'smooth', block: 'start' })
      }
    }, 100)
  })
}

// 滚动到精选案例区域
const scrollToFeatured = () => {
  router.push('/').then(() => {
    setTimeout(() => {
      const element = document.querySelector('.featured-grid')
      if (element) {
        element.scrollIntoView({ behavior: 'smooth', block: 'start' })
      }
    }, 100)
  })
}
</script>

<style scoped>
.footer {
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.95) 0%, rgba(248, 250, 252, 0.98) 100%);
  backdrop-filter: blur(20px);
  border-top: 1px solid rgba(59, 130, 246, 0.1);
  margin-top: 60px;
  padding: 0;
  position: relative;
  overflow: hidden;
}

.footer::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(59, 130, 246, 0.3),
    rgba(139, 92, 246, 0.3),
    rgba(59, 130, 246, 0.3),
    transparent
  );
}

.footer-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 60px 24px 30px;
  position: relative;
  z-index: 1;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 40px;
  margin-bottom: 50px;
}

.footer-section {
  display: flex;
  flex-direction: column;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 20px;
  padding-bottom: 12px;
  position: relative;
  letter-spacing: 0.5px;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 40px;
  height: 2px;
  background: linear-gradient(90deg, #3b82f6, #8b5cf6);
  border-radius: 1px;
}

.footer-links {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.footer-links li {
  margin: 0;
}

.footer-links a {
  color: #64748b;
  text-decoration: none;
  font-size: 14px;
  transition: all 0.3s ease;
  display: inline-block;
  position: relative;
  padding-left: 0;
}

.footer-links a::before {
  content: '';
  position: absolute;
  left: -12px;
  top: 50%;
  transform: translateY(-50%);
  width: 0;
  height: 2px;
  background: linear-gradient(90deg, #3b82f6, #8b5cf6);
  transition: width 0.3s ease;
  border-radius: 1px;
}

.footer-links a:hover {
  color: #3b82f6;
  padding-left: 12px;
  transform: translateX(4px);
}

.footer-links a:hover::before {
  width: 6px;
}

.footer-bottom {
  margin-top: 40px;
}

.footer-divider {
  height: 1px;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(59, 130, 246, 0.2),
    rgba(139, 92, 246, 0.2),
    rgba(59, 130, 246, 0.2),
    transparent
  );
  margin-bottom: 30px;
}

.copyright-info {
  text-align: center;
  padding: 20px 0;
}

.copyright {
  margin: 0 0 8px;
  color: #64748b;
  font-size: 14px;
  line-height: 1.6;
}

.author-link {
  color: #3b82f6;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.3s ease;
  position: relative;
}

.author-link:hover {
  color: #8b5cf6;
  text-decoration: underline;
}

.slogan {
  margin: 0;
  color: #94a3b8;
  font-size: 13px;
  font-style: italic;
  letter-spacing: 0.5px;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .footer-content {
    grid-template-columns: repeat(2, 1fr);
    gap: 32px;
  }

  .footer-container {
    padding: 50px 24px 24px;
  }
}

@media (max-width: 768px) {
  .footer-content {
    grid-template-columns: repeat(2, 1fr);
    gap: 24px;
  }

  .footer-container {
    padding: 40px 20px 20px;
  }

  .section-title {
    font-size: 15px;
    margin-bottom: 16px;
  }

  .footer-links {
    gap: 10px;
  }

  .footer-links a {
    font-size: 13px;
  }

  .copyright {
    font-size: 13px;
  }

  .slogan {
    font-size: 12px;
  }
}

@media (max-width: 480px) {
  .footer-content {
    grid-template-columns: 1fr;
    gap: 32px;
  }

  .footer-container {
    padding: 32px 16px 16px;
  }

  .section-title {
    font-size: 14px;
  }

  .footer-links a {
    font-size: 12px;
  }
}
</style>
