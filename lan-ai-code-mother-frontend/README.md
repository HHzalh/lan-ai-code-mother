# Lan-AI-Code-Mother Frontend

> 基于 Vue 3 + TypeScript + Vite 的 AI 代码生成平台前端应用

Lan-AI-Code-Mother Frontend 是现代化、高性能的单页应用（SPA），为用户提供 AI 驱动的代码生成、应用部署和积分管理等功能。

## ✨ 核心特性

- 🎨 **现代化 UI** - 基于 Ant Design Vue 4.2.6
- ⚡ **极速开发** - Vite 7.0 驱动，毫秒级热更新
- 🔒 **类型安全** - 全面使用 TypeScript 5.8，自动生成 API 类型定义
- 🚀 **实时流式响应** - 使用 SSE 接收 AI 生成的代码
- 🛡️ **权限控制** - 基于 RBAC 的路由权限管理

## 🛠 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| **Vue** | 3.5.17 | 渐进式 JavaScript 框架 |
| **TypeScript** | 5.8.0 | 静态类型检查 |
| **Vite** | 7.0.0 | 下一代前端构建工具 |
| **Ant Design Vue** | 4.2.6 | 企业级 UI 组件库 |
| **Pinia** | 3.0.3 | 状态管理库 |

## 🚀 快速开始

### 安装依赖

\`\`\`bash
npm install
\`\`\`

### 生成 API 类型定义

\`\`\`bash
npm run openapi2ts
\`\`\`

### 启动开发服务器

\`\`\`bash
npm run dev
\`\`\`

访问：http://localhost:5173

## 📁 项目结构

\`\`\`
src/
├── api/              # API 接口封装（自动生成）
├── components/       # 公共组件
├── layouts/          # 布局组件
├── pages/            # 页面组件
├── router/           # 路由配置
├── stores/           # Pinia 状态管理
├── access.ts         # 路由守卫
├── App.vue           # 根组件
└── main.ts           # 应用入口
\`\`\`

## 💻 可用脚本

| 命令 | 说明 |
|------|------|
| `npm run dev` | 启动开发服务器 |
| `npm run build` | 标准构建（含类型检查） |
| `npm run pure-build` | 纯构建（不含类型检查） |
| `npm run openapi2ts` | 生成 API 类型定义 |
| `npm run lint` | ESLint 检查并修复 |
| `npm run format` | Prettier 格式化代码 |

## 📖 开发指南

### 组件开发规范

\`\`\`vue
<script setup lang="ts">
import { ref, computed } from 'vue'

interface Props {
  title: string
}
const props = defineProps<Props>()

const emit = defineEmits<{
  submit: [data: any]
}>()
</script>
\`\`\`

### API 调用

\`\`\`typescript
import { getAppVoById } from '@/api/appController'

const app = await getAppVoById({ id: appId })
\`\`\`

## 🏗 构建与部署

\`\`\`bash
npm run build
\`\`\`

---

Made with ❤️ by Lan-AI-Code-Mother Team
