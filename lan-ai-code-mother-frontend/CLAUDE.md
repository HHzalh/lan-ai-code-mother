# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

**Lan-AI-Code-Mother Frontend** 是一个基于 Vue 3 + TypeScript 的 AI 代码生成平台前端应用，为用户提供 AI 对话、代码生成、应用部署和积分管理等功能。

**核心技术栈**：
- Vue 3.5.17 (Composition API)
- TypeScript 5.8.0
- Vite 7.0.0 (构建工具)
- Ant Design Vue 4.2.6 (UI 组件库)
- Pinia 3.0.3 (状态管理)
- Vue Router 4.5.1 (路由)

---

## 常用命令

### 开发相关

```bash
# 安装依赖
npm install

# 启动开发服务器（Vite 代理已配置 /api 到 http://localhost:8123）
npm run dev

# 预览生产构建
npm run preview
```

### 构建相关

```bash
# 标准构建（包含类型检查）
npm run build

# 纯构建（不含类型检查，更快）
npm run pure-build
```

### 代码质量

```bash
# ESLint 检查并自动修复
npm run lint

# Prettier 格式化代码
npm run format

# TypeScript 类型检查
npm run type-check
```

### API 类型生成

```bash
# 从后端 OpenAPI 文档生成 TypeScript 类型定义
# 需要后端服务运行在 http://localhost:8123
npm run openapi2ts
```

**注意**：类型定义会自动生成到 `src/api/typings.d.ts` 和各个 Controller 文件中。

---

## 架构概览

### 目录结构

```
src/
├── api/                    # API 接口封装（通过 openapi2ts 自动生成）
│   ├── index.ts            # Axios 实例配置
│   ├── typings.d.ts        # TypeScript 类型定义
│   └── *Controller.ts      # 各模块接口（app, user, point 等）
├── assets/                 # 静态资源
├── components/             # 全局公共组件
├── config/                 # 配置文件（env.ts）
├── layouts/                # 布局组件（BasicLayout）
├── pages/                  # 页面组件
│   ├── admin/              # 管理员页面（用户、应用、积分管理）
│   ├── app/                # 应用相关页面（AI 对话、应用编辑）
│   └── user/               # 用户页面（登录、注册、个人中心）
├── router/                 # Vue Router 配置
├── stores/                 # Pinia 状态管理
├── utils/                  # 工具函数
├── access.ts               # 全局路由守卫（权限控制）
├── App.vue                 # 根组件
├── main.ts                 # 应用入口
└── request.ts              # Axios 配置（拦截器、大数字处理）
```

### 核心架构模式

#### 1. 分层架构

```
Pages（页面组件）
    ↓
Components（公共组件）
    ↓
API（接口封装）
    ↓
Request（Axios 实例）
    ↓
后端服务
```

#### 2. 状态管理模式

- 使用 **Pinia** 进行全局状态管理
- **唯一 Store**：`loginUser`（登录用户信息）
- 其他状态优先使用组件本地状态（`ref`/`reactive`）

#### 3. 路由权限控制

**文件位置**：`src/access.ts`

路由守卫在页面跳转前执行：
1. 首次访问时自动获取登录用户信息
2. `/admin/*` 路由需要 `admin` 角色
3. 未登录用户会被重定向到登录页

**关键实现**：
```typescript
// 确保首次加载时等后端返回用户信息
if (firstFetchLoginUser) {
  await loginUserStore.fetchLoginUser()
  firstFetchLoginUser = false
}

// 管理员权限检查
if (toUrl.startsWith('/admin')) {
  if (!loginUser || loginUser.userRole !== 'admin') {
    message.error('没有权限')
    next(`/user/login?redirect=${to.fullPath}`)
    return
  }
}
```

#### 4. API 调用模式

**文件位置**：`src/request.ts`

**核心特性**：
- **大数字精度处理**：自动将超过 `Number.MAX_SAFE_INTEGER` 的数字转为字符串
- **自动登录重定向**：401 状态码自动跳转到登录页
- **统一错误处理**：通过拦截器处理全局错误
- **Cookie 认证**：`withCredentials: true` 携带 Session Cookie

**使用示例**：
```typescript
import { addApp, deployApp } from '@/api/appController'

// 调用 API
const appId = await addApp({ appName: 'Test', codeGenType: 'HTML_CODE' })
const deployUrl = await deployApp({ appId })
```

#### 5. AI 流式对话实现

**文件位置**：`src/pages/app/AppChatPage.vue`

使用 **EventSource**（SSE）实现实时流式响应：

```typescript
// 建立 SSE 连接
const eventSource = new EventSource(url)

eventSource.onmessage = (event) => {
  const data = JSON.parse(event.data)
  // 追加生成的代码
  generatedCode.value += data.d
}

eventSource.addEventListener('done', () => {
  // 生成完成，关闭连接
  eventSource.close()
})
```

---

## 开发指南

### 添加新页面

1. 在 `src/pages/` 对应目录创建 Vue 组件
2. 在 `src/router/index.ts` 添加路由配置
3. 如需权限控制，添加 `meta: { role: 'admin' }`

**示例**：
```typescript
// src/router/index.ts
{
  path: 'user/settings',
  component: () => import('@/pages/user/UserSettingsPage.vue'),
  meta: { title: '设置' }
}
```

### 调用后端 API

1. 确保 API 已在后端定义并通过 OpenAPI 导出
2. 运行 `npm run openapi2ts` 生成类型定义
3. 在组件中导入并调用

**示例**：
```typescript
import { getAppVoById, updateApp } from '@/api/appController'

const appInfo = await getAppVoById({ id: appId })
await updateApp({ id: appId, appName: 'New Name' })
```

### 添加新的公共组件

1. 在 `src/components/` 创建组件
2. 使用 `<script setup lang="ts">` 语法
3. 通过 Props 和 Emits 定义接口

**示例**：
```vue
<script setup lang="ts">
interface Props {
  title: string
  visible?: boolean
}
const props = withDefaults(defineProps<Props>(), {
  visible: false
})

const emit = defineEmits<{
  close: []
}>()
</script>
```

### 状态管理最佳实践

- **全局状态**：仅用于跨组件共享的数据（如登录用户）
- **组件状态**：优先使用本地 `ref`/`reactive`
- **避免过度使用 Store**：不要将所有状态都放到 Pinia

**示例**：
```typescript
// 好的做法：组件本地状态
const messages = ref<Message[]>([])
const loading = ref(false)

// 必要时：全局状态（stores/loginUser.ts）
const loginUserStore = useLoginUserStore()
const user = loginUserStore.loginUser
```

---

## 重要配置文件

### Vite 配置（vite.config.ts）

```typescript
{
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8123',  // 后端服务地址
        changeOrigin: true
      }
    }
  },
  resolve: {
    alias: {
      '@': './src'  // 路径别名
    }
  }
}
```

### OpenAPI 配置（openapi2ts.config.ts）

```typescript
{
  requestLibPath: "import request from '@/request'",
  schemaPath: 'http://localhost:8123/api/v3/api-docs',  // 后端 API 文档地址
  serversPath: './src'
}
```

---

## 常见问题

### Q: 如何处理后端返回的大数字精度丢失问题？

A: 前端已在 `src/request.ts` 中自动处理。超过 `Number.MAX_SAFE_INTEGER` 的数字会自动转为字符串。

### Q: 如何处理积分不足等业务异常？

A: 后端会返回特定错误码（如 50001），前端通过 Axios 拦截器统一处理：

```typescript
if (error.response?.data?.code === 50001) {
  message.error('积分不足，请充值后重试')
}
```

### Q: 如何调试 AI 流式对话？

A: 打开浏览器 DevTools → Network → 事件流，查看 `/app/chat/gen/code` 的实时响应。

### Q: 类型定义从哪里来？

A: 运行 `npm run openapi2ts` 从后端 OpenAPI 文档自动生成。不要手动编辑 `src/api/typings.d.ts`。

---

## 技术细节

### 组件编写规范

- 使用 **Composition API** (`<script setup>`)
- 使用 **TypeScript** 定义 Props 和 Emits
- 遵循 **单一职责原则**，保持组件简洁

### API 错误处理

所有 API 调用应使用 `try-catch`：

```typescript
try {
  await deployApp({ appId })
  message.success('部署成功')
} catch (error) {
  // 全局拦截器已处理错误提示
  // 这里可以添加特定的错误处理逻辑
}
```

### 路径别名

- `@/` 指向 `src/` 目录
- 示例：`import request from '@/request'`

---

## 关键文件说明

| 文件 | 作用 | 修改频率 |
|-----|------|---------|
| `src/request.ts` | Axios 配置、拦截器、大数字处理 | 低（核心配置） |
| `src/access.ts` | 全局路由守卫、权限控制 | 低（核心逻辑） |
| `src/stores/loginUser.ts` | 登录用户全局状态 | 低（核心状态） |
| `src/router/index.ts` | 路由配置 | 中（新增页面时修改） |
| `src/config/env.ts` | 环境配置（API 地址等） | 低（部署配置） |
| `vite.config.ts` | Vite 构建配置、代理配置 | 低（构建配置） |
| `openapi2ts.config.ts` | OpenAPI 类型生成配置 | 低（API 配置） |

---

## 环境变量

### 开发环境

后端服务地址默认为 `http://localhost:8123`，在以下位置配置：

1. **Vite 代理**：`vite.config.ts`（开发时使用）
2. **API_BASE_URL**：`src/config/env.ts`（生产环境使用）

### 切换到生产环境

修改 `src/config/env.ts` 中的 `API_BASE_URL` 为实际生产地址。

---

## 调试技巧

### Vue DevTools

项目已集成 `vite-plugin-vue-devtools`，开发时自动启用：
- 查看 Pinia 状态
- 检查组件树
- 性能分析

### 网络请求调试

1. 打开浏览器 DevTools → Network
2. 筛选 XHR/EventSource 请求
3. 查看请求头、响应、错误信息

### AI 对话调试

- **流式响应**：Network → EventSource → 查看 `/app/chat/gen/code`
- **消息格式**：每次事件包含 `{ d: "代码片段" }`
- **完成标识**：监听 `done` 事件