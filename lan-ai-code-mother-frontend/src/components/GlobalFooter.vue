<template>
  <footer class="global-footer">
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
          </ul>
        </div>
      </div>

      <!-- 底部版权信息 -->
      <div class="footer-bottom">
        <div class="footer-divider"></div>
        <div class="copyright-info">
          <p class="copyright">
            © 2026
            <RouterLink to="/" class="author-link">
              <p>蓝海智造</p>
            </RouterLink>
            . All rights reserved.
          </p>
          <p class="slogan">让想法，瞬间成为可用的应用</p>
        </div>
      </div>
    </div>
  </footer>
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
.global-footer {
  background: var(--color-glass);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-top: 1px solid var(--color-glass-border);
  margin-top: 60px;
  padding: 0;
  position: relative;
  overflow: hidden;
  transform: translateZ(0);
  backface-visibility: hidden;
  will-change: transform;
}

/* 顶部装饰线 */
.global-footer::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(
    90deg,
    transparent,
    var(--color-primary),
    var(--color-secondary),
    var(--color-primary),
    transparent
  );
  opacity: 0.3;
}

.footer-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 60px 24px 30px;
  position: relative;
  z-index: 1;
  transform: translateZ(0);
  backface-visibility: hidden;
}

/* ========== 主要内容区域 ========== */
.footer-content {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 40px;
  margin-bottom: 50px;
}

.footer-section {
  display: flex;
  flex-direction: column;
  transform: translateZ(0);
  backface-visibility: hidden;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0 0 20px;
  padding-bottom: 12px;
  position: relative;
  letter-spacing: 0.5px;
}

/* 标题下划线装饰 */
.section-title::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 40px;
  height: 2px;
  background: linear-gradient(90deg, var(--color-primary), var(--color-secondary));
  border-radius: 1px;
  transition: width 0.3s ease;
}

.footer-section:hover .section-title::after {
  width: 60px;
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
  color: var(--color-text-secondary);
  text-decoration: none;
  font-size: 14px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: inline-block;
  position: relative;
  padding: 4px 0;
}

/* 链接左侧装饰条 */
.footer-links a::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%) translateZ(0);
  width: 0;
  height: 2px;
  background: linear-gradient(90deg, var(--color-primary), var(--color-secondary));
  transition: width 0.3s ease;
  border-radius: 1px;
}

.footer-links a:hover {
  color: var(--color-primary);
  padding-left: 12px;
  transform: translate3d(4px, 0, 0);
}

.footer-links a:hover::before {
  width: 6px;
}

/* ========== 底部版权信息 ========== */
.footer-bottom {
  margin-top: 40px;
}

.footer-divider {
  height: 1px;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(255, 107, 107, 0.2),
    rgba(255, 168, 168, 0.2),
    rgba(255, 107, 107, 0.2),
    transparent
  );
  margin-bottom: 30px;
  transform: translateZ(0);
}

.copyright-info {
  text-align: center;
  padding: 20px 0;
}

.copyright {
  margin: 0 0 8px;
  color: var(--color-text-secondary);
  font-size: 14px;
  line-height: 1.6;
  transform: translateZ(0);
}

.author-link {
  color: var(--color-primary);
  text-decoration: none;
  font-weight: 500;
  transition: all 0.3s ease;
  position: relative;
  display: inline-block;
}

.author-link:hover {
  color: var(--color-primary-dark);
  transform: translate3d(0, -2px, 0);
}

.slogan {
  margin: 0;
  color: var(--color-text-light);
  font-size: 13px;
  letter-spacing: 0.5px;
  font-weight: 400;
  transform: translateZ(0);
}

/* ========== 响应式设计 ========== */

/* 平板设备（1024px 及以下） */
@media (max-width: 1024px) {
  .footer-content {
    grid-template-columns: repeat(3, 1fr);
    gap: 32px;
  }

  .footer-container {
    padding: 50px 24px 24px;
  }

  .section-title {
    font-size: 15px;
  }

  .footer-links a {
    font-size: 13px;
  }
}

/* 小平板设备（768px 及以下） */
@media (max-width: 768px) {
  .footer-content {
    grid-template-columns: repeat(2, 1fr);
    gap: 28px;
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

/* 手机设备（640px 及以下） */
@media (max-width: 640px) {
  .footer-content {
    grid-template-columns: 1fr;
    gap: 32px;
  }

  .footer-container {
    padding: 36px 20px 20px;
  }

  .section-title {
    font-size: 15px;
    margin-bottom: 16px;
  }

  .section-title::after {
    width: 30px;
  }

  .footer-section:hover .section-title::after {
    width: 45px;
  }

  .footer-links a {
    font-size: 14px;
    padding: 6px 0;
  }

  .footer-links a:hover {
    padding-left: 10px;
  }

  .copyright-info {
    padding: 16px 0;
  }

  .copyright {
    font-size: 12px;
  }

  .slogan {
    font-size: 12px;
  }
}

/* 小屏手机（480px 及以下） */
@media (max-width: 480px) {
  .footer-container {
    padding: 32px 16px 16px;
  }

  .footer-content {
    gap: 28px;
    margin-bottom: 40px;
  }

  .section-title {
    font-size: 14px;
  }

  .footer-links {
    gap: 8px;
  }

  .footer-links a {
    font-size: 13px;
  }

  .copyright {
    font-size: 12px;
  }

  .slogan {
    font-size: 11px;
  }
}

/* 超小屏设备（375px 及以下） */
@media (max-width: 375px) {
  .footer-container {
    padding: 28px 12px 12px;
  }

  .footer-content {
    gap: 24px;
  }

  .section-title {
    font-size: 14px;
    margin-bottom: 14px;
  }

  .footer-links a {
    font-size: 12px;
  }

  .copyright {
    font-size: 11px;
  }

  .slogan {
    font-size: 11px;
  }
}
</style>
