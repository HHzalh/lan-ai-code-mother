<template>
  <header class="global-header">
    <div class="header-container">
      <!-- 左侧：Logo和标题 -->
      <RouterLink to="/" class="header-left">
        <img alt="Logo" class="logo" src="@/assets/logo.png" />
        <h1 class="site-title">蓝海智造</h1>
      </RouterLink>

      <!-- 中间：导航菜单 -->
      <nav class="header-nav">
        <div
          v-for="item in menuItems"
          :key="item.key"
          class="nav-item"
          :class="{ active: selectedKeys.includes(item.key) }"
          @click="handleMenuClick({ key: item.key })"
        >
          <component :is="item.icon()" class="nav-icon" />
          <span class="nav-label">{{ item.label }}</span>
        </div>
      </nav>

      <!-- 右侧：用户操作区域 -->
      <div class="header-right">
        <!-- 已登录 -->
        <div v-if="loginUserStore.loginUser.id" class="user-info">
          <a-dropdown>
            <div class="user-avatar-wrapper">
              <a-avatar :src="loginUserStore.loginUser.userAvatar" />
              <a-tooltip
                v-if="loginUserStore.loginUser.userName"
                :title="loginUserStore.loginUser.userName"
              >
                <span class="user-name">{{ truncateText(loginUserStore.loginUser.userName, 10) }}</span>
              </a-tooltip>
              <span v-else class="user-name">无名</span>
            </div>
            <template #overlay>
              <a-menu>
                <a-menu-item @click="router.push('/user/profile')">
                  <UserOutlined />
                  个人中心
                </a-menu-item>
                <a-menu-item @click="doLogout">
                  <LogoutOutlined />
                  退出登录
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>

        <!-- 未登录 -->
        <a-button v-else class="login-btn" type="primary" href="/user/login">
          <template #icon>
            <LoginOutlined />
          </template>
          登录
        </a-button>
      </div>
    </div>
  </header>
</template>

<script lang="ts" setup>
import { computed, h, ref } from 'vue'
import { useRouter } from 'vue-router'
import { type MenuProps, message } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/loginUser.ts'
import { userLogout } from '@/api/userController.ts'
import {
  AppstoreOutlined,
  BookOutlined,
  GiftOutlined,
  HomeOutlined,
  LoginOutlined,
  LogoutOutlined,
  TeamOutlined,
  MessageOutlined,
  UserOutlined,
} from '@ant-design/icons-vue'
import ChatManagePage from "@/pages/admin/ChatManagePage.vue";

const loginUserStore = useLoginUserStore()
const router = useRouter()

// 截取文本
const truncateText = (text: string, maxLength: number) => {
  if (!text) return ''
  if (text.length <= maxLength) return text
  return text.substring(0, maxLength) + '...'
}
// 当前选中菜单
const selectedKeys = ref<string[]>(['/'])
// 监听路由变化，更新当前选中菜单
router.afterEach((to) => {
  selectedKeys.value = [to.path]
})

// 菜单配置项
const originItems = [
  {
    key: '/',
    icon: () => h(HomeOutlined),
    label: '主页',
    title: '主页',
  },
  {
    key: '/help/docs',
    icon: () => h(BookOutlined),
    label: '快速上手',
    title: '快速上手',
  },
  {
    key: '/admin/userManage',
    icon: () => h(TeamOutlined),
    label: '用户管理',
    title: '用户管理',
  },
  {
    key: '/admin/appManage',
    icon: () => h(AppstoreOutlined),
    label: '应用管理',
    title: '应用管理',
  },
  {
    key: '/admin/ChatManage',
    icon: () => h(MessageOutlined),
    label: '对话管理',
    title: '对话管理',
  },
  {
    key: '/admin/pointManage',
    icon: () => h(GiftOutlined),
    label: '积分管理',
    title: '积分管理',
  },
]

// 过滤菜单项
const filterMenus = (menus = [] as MenuProps['items']) => {
  return menus?.filter((menu) => {
    const menuKey = menu?.key as string
    if (menuKey?.startsWith('/admin')) {
      const loginUser = loginUserStore.loginUser
      if (!loginUser || loginUser.userRole !== 'admin') {
        return false
      }
    }
    return true
  })
}

// 展示在菜单的路由数组
const menuItems = computed<MenuProps['items']>(() => filterMenus(originItems))

// 处理菜单点击
const handleMenuClick: MenuProps['onClick'] = (e) => {
  const key = e.key as string
  selectedKeys.value = [key]
  // 跳转到对应页面
  if (key.startsWith('/')) {
    router.push(key)
  }
}

// 退出登录
const doLogout = async () => {
  const res = await userLogout()
  if (res.data.code === 0) {
    loginUserStore.setLoginUser({
      userName: '未登录',
    })
    message.success('退出登录成功')
    await router.push('/user/login')
  } else {
    message.error('退出登录失败，' + res.data.message)
  }
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
.global-header {
  background: var(--color-glass);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom: 1px solid var(--color-glass-border);
  box-shadow: 0 2px 16px rgba(255, 107, 107, 0.08);
  position: sticky;
  top: 0;
  z-index: 1000;
  transform: translateZ(0);
  backface-visibility: hidden;
  will-change: transform;
}

.header-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 32px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 40px;
  transform: translateZ(0);
  backface-visibility: hidden;
}

/* ========== 左侧：Logo和标题 ========== */
.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
  text-decoration: none;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  flex-shrink: 0;
  transform: translateZ(0);
  backface-visibility: hidden;
}

.header-left:hover {
  transform: translate3d(0, -2px, 0);
}

.header-left:hover .logo {
  transform: rotate(5deg) scale(1.05);
}

.logo {
  height: 40px;
  width: 40px;
  border-radius: var(--radius-sm);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  transform: translateZ(0);
  backface-visibility: hidden;
}

.site-title {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -0.5px;
  white-space: nowrap;
}

/* ========== 中间：导航菜单 ========== */
.header-nav {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transform: translateZ(0);
  backface-visibility: hidden;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  color: var(--color-text-secondary);
  font-size: 14px;
  font-weight: 500;
  position: relative;
  transform: translateZ(0);
  backface-visibility: hidden;
  white-space: nowrap;
}

/* 底部装饰条 */
.nav-item::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%) translateZ(0);
  width: 0;
  height: 2px;
  background: linear-gradient(90deg, var(--color-primary), var(--color-secondary));
  border-radius: 1px;
  transition: width 0.3s ease;
}

.nav-item:hover {
  color: var(--color-primary);
  background: rgba(255, 107, 107, 0.08);
  transform: translate3d(0, -2px, 0);
}

.nav-item:hover::after {
  width: 60%;
}

.nav-item.active {
  color: var(--color-primary);
  background: rgba(255, 107, 107, 0.12);
}

.nav-item.active::after {
  width: 60%;
}

.nav-icon {
  font-size: 16px;
  transition: transform 0.3s ease;
}

.nav-item:hover .nav-icon {
  transform: translate3d(0, -1px, 0) scale(1.1);
}

.nav-label {
  white-space: nowrap;
}

/* ========== 右侧：用户操作区域 ========== */
.header-right {
  display: flex;
  align-items: center;
  flex-shrink: 0;
  transform: translateZ(0);
  backface-visibility: hidden;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-avatar-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 6px 12px;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: rgba(255, 255, 255, 0.5);
  border: 1px solid rgba(255, 107, 107, 0.15);
  transform: translateZ(0);
  backface-visibility: hidden;
}

.user-avatar-wrapper:hover {
  background: rgba(255, 107, 107, 0.08);
  border-color: rgba(255, 107, 107, 0.25);
  transform: translate3d(0, -2px, 0);
  box-shadow: var(--shadow-soft);
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text);
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.login-btn {
  font-size: 14px;
  font-weight: 600;
  border-radius: var(--radius-sm);
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  border: none;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  transform: translateZ(0);
  backface-visibility: hidden;
}

.login-btn:hover {
  transform: translate3d(0, -2px, 0);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);
}

.login-btn:active {
  transform: translate3d(0, 0, 0);
}

/* ========== 响应式设计 ========== */

/* 大屏设备（1280px 及以下） */
@media (max-width: 1280px) {
  .header-container {
    padding: 0 24px;
    gap: 32px;
  }

  .nav-item {
    padding: 8px 14px;
    font-size: 14px;
  }
}

/* 平板设备（1024px 及以下） */
@media (max-width: 1024px) {
  .header-container {
    padding: 0 20px;
    gap: 24px;
  }

  .site-title {
    font-size: 18px;
  }

  .logo {
    height: 36px;
    width: 36px;
  }

  .nav-item {
    padding: 8px 12px;
    font-size: 13px;
    gap: 6px;
  }

  .nav-icon {
    font-size: 15px;
  }

  .user-name {
    max-width: 100px;
    font-size: 13px;
  }

  .login-btn {
    height: 34px;
    padding: 0 18px;
    font-size: 13px;
  }
}

/* 小平板设备（768px 及以下） */
@media (max-width: 768px) {
  .header-container {
    padding: 0 16px;
    gap: 16px;
    height: 56px;
  }

  .site-title {
    font-size: 17px;
  }

  .logo {
    height: 32px;
    width: 32px;
  }

  .header-nav {
    gap: 4px;
  }

  .nav-item {
    padding: 6px 10px;
    font-size: 13px;
    gap: 6px;
  }

  .nav-icon {
    font-size: 14px;
  }

  .nav-label {
    display: none;
  }

  .user-name {
    max-width: 80px;
    font-size: 12px;
  }

  .login-btn {
    height: 32px;
    padding: 0 16px;
    font-size: 13px;
  }

  .login-btn span {
    display: inline;
  }
}

/* 手机设备（640px 及以下） */
@media (max-width: 640px) {
  .global-header {
    position: sticky;
  }

  .header-container {
    padding: 0 12px;
    gap: 12px;
    height: 52px;
  }

  .header-left {
    gap: 8px;
  }

  .site-title {
    font-size: 16px;
  }

  .logo {
    height: 30px;
    width: 30px;
  }

  .header-nav {
    gap: 2px;
  }

  .nav-item {
    padding: 6px 8px;
    border-radius: var(--radius-sm);
  }

  .nav-icon {
    font-size: 16px;
  }

  .nav-label {
    display: none;
  }

  .user-avatar-wrapper {
    padding: 4px 8px;
    gap: 8px;
  }

  .user-name {
    max-width: 60px;
    font-size: 12px;
  }

  .login-btn {
    height: 30px;
    padding: 0 14px;
    font-size: 12px;
  }

  .login-btn .anticon {
    margin-right: 0;
  }

  .login-btn span {
    display: none;
  }
}

/* 小屏手机（480px 及以下） */
@media (max-width: 480px) {
  .header-container {
    padding: 0 10px;
    gap: 8px;
    height: 48px;
  }

  .site-title {
    font-size: 15px;
  }

  .logo {
    height: 28px;
    width: 28px;
  }

  .nav-item {
    padding: 4px 6px;
  }

  .nav-icon {
    font-size: 15px;
  }

  .user-avatar-wrapper {
    padding: 4px 6px;
    gap: 6px;
  }

  .user-name {
    display: none;
  }

  .login-btn {
    height: 28px;
    padding: 0 12px;
  }

  .login-btn .anticon {
    font-size: 13px;
  }
}
</style>
