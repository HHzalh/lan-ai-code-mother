# Lan-AI-Code-Mother

> 基于 AI 的代码生成与部署平台

Lan-AI-Code-Mother 是一个创新的 AI 驱动代码生成平台，用户通过自然语言对话即可生成前端应用代码，并支持一键部署到静态服务器。平台内置完整的积分经济系统和用户权限管理，为开发者提供从创意到部署的全流程解决方案。

## ✨ 核心特性

- 🤖 **AI 驱动代码生成** - 基于 LangChain4j + LangGraph4j 构建的多节点工作流，支持智能提示词增强和代码质量检查
- 🚀 **一键部署** - 将生成的应用快速部署到静态资源服务器（支持本地和腾讯云 COS）
- 💰 **积分经济系统** - 完整的积分获取（签到、邀请）、消费（生成、部署）和管理体系
- 👥 **用户权限管理** - 基于 RBAC 的用户角色管理和权限控制
- 📊 **应用市场** - 精选应用展示与分享平台
- ⚡ **实时流式响应** - 使用 SSE（Server-Sent Events）实现代码生成的实时推送
- 📸 **自动截图预览** - 基于 Selenium 的应用自动截图功能

## 🛠 技术栈

### 后端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| **Java** | 21 | 编程语言 |
| **Spring Boot** | 3.5.4 | 应用框架 |
| **MySQL** | 8.0+ | 关系型数据库 |
| **MyBatis-Flex** | 1.11.0 | ORM 框架 |
| **Redis** | - | 缓存 + Session 存储 |
| **Redisson** | 3.50.0 | 分布式限流 |
| **Caffeine** | - | 本地缓存 |
| **LangChain4j** | 1.1.0 | AI 应用框架 |
| **LangGraph4j** | 1.6.0-rc2 | AI 工作流编排 |
| **Knife4j** | 4.4.0 | API 文档 |
| **Selenium** | 4.33.0 | 网页自动化 |
| **腾讯云 COS** | 5.6.227 | 对象存储 |
| **RabbitMQ** | 3.12 | 消息队列 |
| **Hutool** | 5.8.38 | Java 工具库 |

### 前端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| **Vue** | 3.5.17 | 前端框架 |
| **TypeScript** | 5.8.0 | 类型系统 |
| **Vite** | 7.0.0 | 构建工具 |
| **Ant Design Vue** | 4.2.6 | UI 组件库 |
| **Pinia** | 3.0.3 | 状态管理 |
| **Vue Router** | 4.5.1 | 路由管理 |
| **Axios** | 1.13.2 | HTTP 客户端 |
| **markdown-it** | 14.1.0 | Markdown 渲染 |
| **highlight.js** | 11.11.1 | 代码高亮 |
| **openapi2ts** | - | TypeScript 类型生成 |

## 📋 目录

- [前置要求](#前置要求)
- [快速开始](#快速开始)
- [项目结构](#项目结构)
- [核心架构](#核心架构)
- [环境配置](#环境配置)
- [可用脚本](#可用脚本)
- [测试](#测试)
- [部署](#部署)
- [前端开发指南](#前端开发指南)
- [故障排查](#故障排查)
- [开发指南](#开发指南)
- [常见问题](#常见问题)
- [贡献指南](#贡献指南)
- [许可证](#许可证)

## 📦 前置要求

在开始之前，请确保您的开发环境已安装以下工具：

- **Java**：JDK 21 或更高版本
- **Maven**：3.8+（或使用项目自带的 `mvnw`）
- **MySQL**：8.0+ 或 Docker
- **Redis**：5.0+ 或 Docker
- **RabbitMQ**：3.12+ 或 Docker（可选，用于异步任务）
- **Node.js**：20+ 或更高版本
- **npm** 或 **pnpm**：包管理器
- **Git**：版本控制

### 可选依赖

- **Chrome/Chromium 浏览器**：用于 Selenium 截图功能
- **腾讯云 COS 账号**：用于对象存储（生产环境推荐）

## 🚀 快速开始

### 1. 克隆仓库

```bash
git clone https://gitee.com/hhzalh/lan-ai-code-mother.git
cd lan-ai-code-mother
```

### 2. 数据库初始化

**使用 Docker 启动 MySQL**（推荐）：

```bash
docker run --name mysql-lanai \
  -e MYSQL_ROOT_PASSWORD=123456 \
  -e MYSQL_DATABASE=lan_ai_code_mother \
  -p 3306:3306 \
  -d mysql:8.0
```

**导入数据库脚本**：

```bash
# 创建数据库表结构
mysql -u root -p < sql/create_table.sql

# 初始化积分系统表
mysql -u root -p < sql/point_system_table.sql
```

或直接在 MySQL 中执行：

```sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS lan_ai_code_mother;
USE lan_ai_code_mother;

-- 执行 sql/create_table.sql 中的建表语句
-- 执行 sql/point_system_table.sql 中的积分系统表
```

### 3. 启动 Redis

**使用 Docker**（推荐）：

```bash
docker run --name redis-lanai \
  -p 6379:6379 \
  -d redis:7-alpine
```

或使用本地 Redis：

```bash
# Windows
redis-server

# Linux/macOS
redis-server /path/to/redis.conf
```

### 4. 启动 RabbitMQ（可选）

**使用 Docker**：

```bash
docker run --name rabbitmq-lanai \
  -p 5672:5672 \
  -p 15672:15672 \
  -e RABBITMQ_DEFAULT_USER=guest \
  -e RABBITMQ_DEFAULT_PASS=guest \
  -d rabbitmq:3.12-management
```

管理界面：http://localhost:15672（guest/guest）

### 5. 后端环境配置

复制并编辑环境配置文件：

```bash
# 复制配置文件（如果不存在）
cp src/main/resources/application-local.yml.example src/main/resources/application-local.yml
```

编辑 `src/main/resources/application-local.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/lan_ai_code_mother
    username: root
    password: 123456  # 修改为你的 MySQL 密码

  data:
    redis:
      host: localhost
      port: 6379
      password:  # 如果有密码则填写

  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest

langchain4j:
  open-ai:
    chat-model:
      base-url: https://open.bigmodel.cn/api/coding/paas/v4
      api-key: YOUR_API_KEY  # 替换为你的智谱 AI API Key
      model-name: glm-4.7

cos:
  client:
    secret-id: YOUR_SECRET_ID  # 腾讯云 COS Secret ID
    secret-key: YOUR_SECRET_KEY  # 腾讯云 COS Secret Key
```

**获取 AI API Key**：
- [智谱 AI 开放平台](https://open.bigmodel.cn/)
- [阿里云 DashScope](https://dashscope.aliyuncs.com/)（用于图片生成）

**获取腾讯云 COS 密钥**：
- 登录 [腾讯云控制台](https://console.cloud.tencent.com/cos)
- 创建存储桶并获取 API 密钥

### 6. 启动后端服务

```bash
# 使用 Maven Wrapper（推荐）
./mvnw spring-boot:run

# 或使用 Maven
mvn spring-boot:run

# Windows 用户
mvnw.cmd spring-boot:run
```

服务启动后：
- **后端 API**：http://localhost:8123/api
- **API 文档**：http://localhost:8123/api/doc.html

### 7. 前端开发

```bash
# 进入前端目录
cd lan-ai-code-mother-frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端开发服务器：http://localhost:5173

### 8. 生成前端类型定义

确保后端服务已启动，然后运行：

```bash
cd lan-ai-code-mother-frontend
npm run openapi2ts
```

这将从后端 OpenAPI 文档自动生成 TypeScript 类型定义到 `src/api/` 目录。

### 9. 访问应用

打开浏览器访问：http://localhost:5173

**默认管理员账号**（通过配置文件中的 `app.admin.register-password` 注册）：
- 账号：admin
- 密码：admin13299626612（可在 `application-local.yml` 中修改）

## 📁 项目结构

```
lan-ai-code-mother/
├── src/main/java/com/lanhai/lanaicodemother/
│   ├── annotation/          # 自定义注解（@ConsumePoints、@RateLimit、@AuthCheck）
│   ├── aspect/              # AOP 切面（积分扣减、权限验证）
│   ├── aop/                 # AOP 相关类
│   ├── ai/                  # AI 服务集成
│   ├── common/              # 公共类（结果封装、异常处理）
│   ├── config/              # 配置类（Redis、COS、RabbitMQ）
│   ├── constant/            # 常量定义（枚举、业务常量）
│   ├── controller/          # 控制器层（REST API）
│   ├── core/                # 核心业务逻辑
│   ├── exception/           # 全局异常处理
│   ├── generator/           # 代码生成器
│   ├── langgraph4j/         # LangGraph4j 工作流
│   │   ├── node/            # 工作流节点
│   │   └── CodeGenWorkflow.java  # 代码生成工作流
│   ├── manager/             # 业务管理层
│   ├── mapper/              # MyBatis-Flex 数据访问层
│   ├── model/               # 数据模型（Entity、VO、DTO）
│   ├── rabbitmq/            # RabbitMQ 相关配置
│   ├── ratelimter/          # 限流器实现
│   ├── service/             # 业务逻辑层
│   ├── task/                # 定时任务
│   └── utils/               # 工具类
├── src/main/resources/
│   ├── application.yml              # 主配置文件
│   ├── application-local.yml        # 本地开发环境配置
│   ├── application-prod.yml         # 生产环境配置
│   └── prompt/                      # AI 系统 Prompt 模板
├── lan-ai-code-mother-frontend/     # 前端项目
│   ├── src/
│   │   ├── api/                    # API 接口封装（自动生成）
│   │   ├── assets/                 # 静态资源
│   │   ├── components/             # 公共组件
│   │   ├── config/                 # 配置文件
│   │   ├── layouts/                # 布局组件
│   │   ├── pages/                  # 页面组件
│   │   │   ├── admin/              # 管理员页面
│   │   │   ├── app/                # 应用相关页面
│   │   │   └── user/               # 用户页面
│   │   ├── router/                 # 路由配置
│   │   ├── stores/                 # Pinia 状态管理
│   │   ├── utils/                  # 工具函数
│   │   ├── access.ts               # 全局路由守卫
│   │   ├── App.vue                 # 根组件
│   │   └── main.ts                 # 应用入口
│   ├── vite.config.ts              # Vite 配置
│   ├── openapi2ts.config.ts        # OpenAPI 类型生成配置
│   └── package.json
├── lan-ai-code-mother-microservices/  # 微服务子模块（开发中）
│   ├── lan-ai-code-app/
│   ├── lan-ai-code-point/
│   └── lan-ai-code-user/
├── sql/                        # 数据库脚本
│   ├── create_table.sql        # 建表脚本
│   └── point_system_table.sql  # 积分系统表
├── docs/                       # 项目文档
├── static/                     # 静态资源（部署的应用）
├── tmp/                        # 临时文件（生成的代码）
├── logs/                       # 日志文件
├── pom.xml                     # Maven 配置
├── CLAUDE.md                   # AI 开发助手指南
└── README.md                   # 项目说明文档
```

## 🏗 核心架构

### 后端分层架构

```
┌─────────────────────────────────────────────────────────────┐
│                    Controller 层                             │
│              （REST API 入口 + 注解式 AOP）                   │
│  @RateLimit → @ConsumePoints → @AuthCheck → 业务方法         │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                     Service 层                               │
│           （业务逻辑 + 事务管理 + AI 服务调用）                │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                     Mapper 层                                │
│              （MyBatis-Flex 数据访问）                        │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│              MySQL（持久化） + Redis（缓存）                  │
└─────────────────────────────────────────────────────────────┘
```

### 前端架构模式

```
Pages（页面组件）
    ↓
Components（公共组件）
    ↓
API（接口封装，通过 openapi2ts 自动生成）
    ↓
Request（Axios 实例，大数字精度处理）
    ↓
后端服务
```

**关键特性**：
- **大数字处理**：自动将超过 `Number.MAX_SAFE_INTEGER` 的数字转为字符串（`src/request.ts`）
- **路由守卫**：首次加载自动获取用户信息，管理员路由需要 admin 角色（`src/access.ts`）
- **状态管理**：仅登录用户使用 Pinia 全局状态（`stores/loginUser.ts`），其他使用组件本地状态

### 积分系统 AOP 机制

**核心实现**：`src/main/java/com/lanhai/lanaicodemother/aspect/ConsumePointsAspect.java`

**使用示例**：

```java
@ConsumePoints(
    businessType = PointBusinessTypeEnum.MESSAGE,
    ruleKey = PointRuleKeyEnum.AI_MESSAGE_COST,
    once = false,  // false=每次扣费, true=仅首次扣费
    businessIdParam = "appId"
)
public Flux<String> chatToGenCode(Long appId, String message) {
    // AOP 自动扣减积分，这里只需实现业务逻辑
}
```

**事务一致性**：积分扣减与流水记录在同一事务中，失败自动回滚。

## ⚙️ 环境配置

### 后端环境变量

**application-local.yml**（本地开发）：

| 配置项 | 说明 | 示例值 |
|--------|------|--------|
| `spring.datasource.url` | MySQL 连接地址 | `jdbc:mysql://localhost:3306/lan_ai_code_mother` |
| `spring.datasource.username` | MySQL 用户名 | `root` |
| `spring.datasource.password` | MySQL 密码 | `123456` |
| `spring.data.redis.host` | Redis 主机 | `localhost` |
| `spring.data.redis.port` | Redis 端口 | `6379` |
| `spring.data.redis.password` | Redis 密码 | （留空表示无密码） |
| `spring.rabbitmq.host` | RabbitMQ 主机 | `localhost` |
| `spring.rabbitmq.port` | RabbitMQ 端口 | `5672` |
| `langchain4j.open-ai.chat-model.api-key` | AI API Key | `your_api_key_here` |
| `cos.client.secret-id` | 腾讯云 Secret ID | `your_secret_id` |
| `cos.client.secret-key` | 腾讯云 Secret Key | `your_secret_key` |
| `app.admin.register-password` | 管理员注册密码 | `admin13299626612` |

**application-prod.yml**（生产环境）：
- 修改数据库连接为生产数据库
- 修改 Redis 连接为生产 Redis
- 配置真实的域名和 HTTPS
- 更新 AI API Key 和腾讯云密钥

### 前端环境变量

**开发环境**（`vite.config.ts`）：
```typescript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8123',  // 后端服务地址
      changeOrigin: true
    }
  }
}
```

**生产环境**（`src/config/env.ts`）：
```typescript
export const API_BASE_URL = 'https://your-domain.com/api'
```

### 积分规则配置

系统当前积分规则（可动态调整）：

| 业务类型 | 积分变化 | 说明 |
|---------|---------|------|
| 每日签到 | +10 | 基础签到积分 |
| 连续 3 天 | +10 | 额外奖励 |
| 连续 7 天 | +50 | 额外奖励 |
| 注册奖励 | +100 | 新用户注册 |
| 被邀请注册 | +50 | 被邀请人获得 |
| 邀请新用户 | +30 | 邀请人获得 |
| 生成应用 | -20 | 每次生成 |
| 部署应用 | -30 | 每次部署 |
| 下载代码 | -30 | 仅首次 |

**管理后台**：管理员可通过接口 `/point/rules` 动态调整规则。

## 💻 可用脚本

### 后端脚本

```bash
# 启动后端服务（默认使用 local profile）
mvn spring-boot:run

# 或使用 Maven Wrapper（推荐）
./mvnw spring-boot:run

# 使用指定 profile 启动
mvn spring-boot:run -Dspring-boot.run.profiles=prod

# 打包项目
mvn clean package

# 跳过测试打包
mvn clean package -DskipTests

# 运行测试
mvn test

# 运行特定测试类
mvn test -Dtest=AiCodeGeneratorServiceTest

# 清理项目
mvn clean
```

### 前端脚本

```bash
cd lan-ai-code-mother-frontend

# 安装依赖
npm install

# 启动开发服务器（代理到 http://localhost:8123）
npm run dev

# 标准构建（包含类型检查）
npm run build

# 纯构建（不含类型检查，更快）
npm run pure-build

# 预览生产构建
npm run preview

# 从后端 OpenAPI 生成 TypeScript 类型定义
# 需要后端服务运行在 http://localhost:8123
npm run openapi2ts

# ESLint 检查并自动修复
npm run lint

# Prettier 格式化代码
npm run format

# TypeScript 类型检查
npm run type-check
```

### 数据库脚本

```bash
# 创建数据库表结构
mysql -u root -p < sql/create_table.sql

# 初始化积分系统表
mysql -u root -p < sql/point_system_table.sql
```

## 🧪 测试

### 后端测试

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=AiCodeGeneratorServiceTest

# 运行性能测试
mvn test -Dtest=PointModulePerformanceTest

# 运行并发工作流测试
mvn test -Dtest=CodeGenConcurrentWorkflowTest
```

**关键测试类**：
- `AiCodeGeneratorServiceTest.java` - AI 代码生成测试
- `CodeGenWorkflowTest.java` - 代码生成工作流测试
- `PointModulePerformanceTest.java` - 积分模块性能测试
- `CodeGenConcurrentWorkflowTest.java` - 并发工作流测试

### 前端测试

- **测试框架**：暂无（计划集成 Vitest）
- **手动测试**：通过浏览器 DevTools 和 Vue DevTools

## 🚀 部署

### Docker 部署（推荐）

#### 后端部署

创建 `Dockerfile`：

```dockerfile
FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/lan-ai-code-mother-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8123

ENTRYPOINT ["java", "-jar", "app.jar"]
```

构建并运行：

```bash
# 构建镜像
docker build -t lan-ai-code-mother-backend .

# 运行容器
docker run -d \
  --name lanai-backend \
  -p 8123:8123 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/lan_ai_code_mother \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  -e SPRING_DATA_REDIS_HOST=redis \
  lan-ai-code-mother-backend
```

#### 前端部署

构建生产版本：

```bash
cd lan-ai-code-mother-frontend
npm run build
```

使用 Nginx 部署：

```nginx
server {
    listen 80;
    server_name your-domain.com;

    root /usr/share/nginx/html;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://backend:8123;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### Docker Compose 部署

创建 `docker-compose.yml`：

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: lanai-mysql
    environment:
      MYSQL_ROOT_PASSWORD: your_password
      MYSQL_DATABASE: lan_ai_code_mother
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql

  redis:
    image: redis:7-alpine
    container_name: lanai-redis
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data

  rabbitmq:
    image: rabbitmq:3.12-management
    container_name: lanai-rabbitmq
    ports:
      - "5672:5672"
      - "15672:15672"
    environment:
      RABBITMQ_DEFAULT_USER: guest
      RABBITMQ_DEFAULT_PASS: guest

  backend:
    build: .
    container_name: lanai-backend
    ports:
      - "8123:8123"
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/lan_ai_code_mother
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: your_password
      SPRING_DATA_REDIS_HOST: redis
    depends_on:
      - mysql
      - redis
      - rabbitmq

  frontend:
    image: nginx:alpine
    container_name: lanai-frontend
    ports:
      - "80:80"
    volumes:
      - ./lan-ai-code-mother-frontend/dist:/usr/share/nginx/html
      - ./nginx.conf:/etc/nginx/conf.d/default.conf
    depends_on:
      - backend

volumes:
  mysql-data:
  redis-data:
```

启动：

```bash
docker-compose up -d
```

### 传统部署

#### 后端部署

```bash
# 打包
mvn clean package -DskipTests

# 上传到服务器
scp target/lan-ai-code-mother-0.0.1-SNAPSHOT.jar user@server:/path/to/app/

# 在服务器上启动
nohup java -jar lan-ai-code-mother-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod > app.log 2>&1 &

# 或使用 systemd 管理
sudo systemctl start lan-ai-code-mother
```

#### 前端部署

```bash
# 构建
cd lan-ai-code-mother-frontend
npm run build

# 上传到服务器
scp -r dist/* user@server:/path/to/nginx/html/
```

### 生产环境注意事项

1. **安全配置**：
   - 修改所有默认密码
   - 使用环境变量存储敏感信息
   - 配置 HTTPS
   - 启用防火墙

2. **性能优化**：
   - 配置 Redis 持久化
   - 启用 MySQL 慢查询日志
   - 配置 CDN 加速静态资源

3. **监控告警**：
   - 配置日志收集（ELK/Loki）
   - 设置应用性能监控（APM）
   - 配置异常告警

## 🎨 前端开发指南

### 前端项目概述

前端项目位于 `lan-ai-code-mother-frontend/` 目录，基于 Vue 3 + TypeScript + Vite 构建，采用 Composition API 和 Ant Design Vue 组件库。

### 前端目录结构

```
lan-ai-code-mother-frontend/
├── src/
│   ├── api/                    # API 接口封装（通过 openapi2ts 自动生成）
│   │   ├── index.ts            # Axios 实例配置
│   │   ├── typings.d.ts        # TypeScript 类型定义
│   │   ├── appController.ts    # 应用相关接口
│   │   ├── userController.ts   # 用户相关接口
│   │   └── pointController.ts  # 积分相关接口
│   ├── assets/                 # 静态资源（图片、样式等）
│   ├── components/             # 全局公共组件
│   ├── config/                 # 配置文件
│   │   └── env.ts              # 环境配置（API_BASE_URL 等）
│   ├── layouts/                # 布局组件
│   │   └── BasicLayout.vue     # 基础布局
│   ├── pages/                  # 页面组件
│   │   ├── admin/              # 管理员页面
│   │   │   ├── UserManagePage.vue      # 用户管理
│   │   │   ├── AppManagePage.vue       # 应用管理
│   │   │   └── PointManagePage.vue     # 积分管理
│   │   ├── app/                # 应用相关页面
│   │   │   ├── AppListPage.vue         # 应用列表
│   │   │   ├── AppChatPage.vue         # AI 对话页面
│   │   │   └── AppEditPage.vue         # 应用编辑
│   │   └── user/               # 用户页面
│   │       ├── UserLoginPage.vue       # 登录
│   │       ├── UserRegisterPage.vue    # 注册
│   │       └── UserCenterPage.vue      # 个人中心
│   ├── router/                 # Vue Router 配置
│   │   └── index.ts            # 路由定义
│   ├── stores/                 # Pinia 状态管理
│   │   └── loginUser.ts        # 登录用户状态
│   ├── utils/                  # 工具函数
│   ├── styles/                 # 全局样式
│   ├── access.ts               # 全局路由守卫（权限控制）
│   ├── App.vue                 # 根组件
│   ├── main.ts                 # 应用入口
│   └── request.ts              # Axios 配置（拦截器、大数字处理）
├── public/                     # 公共静态资源
├── .env.development            # 开发环境变量
├── .env.production             # 生产环境变量
├── vite.config.ts              # Vite 配置
├── openapi2ts.config.ts        # OpenAPI 类型生成配置
├── package.json                # 项目依赖
└── README.md                   # 前端说明文档
```

### 前端核心架构

#### 1. 分层架构

```
Pages（页面组件）
    ↓
Components（公共组件）
    ↓
API（接口封装，通过 openapi2ts 自动生成）
    ↓
Request（Axios 实例，大数字精度处理）
    ↓
后端服务
```

#### 2. 状态管理模式

- 使用 **Pinia** 进行全局状态管理
- **唯一 Store**：`loginUser`（登录用户信息）
- 其他状态优先使用组件本地状态（`ref`/`reactive`）

**核心原则**：
- ✅ **全局状态**：仅用于跨组件共享的数据（如登录用户）
- ✅ **组件状态**：优先使用本地 `ref`/`reactive`
- ❌ **避免过度使用 Store**：不要将所有状态都放到 Pinia

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

### 前端开发环境配置

#### 1. 开发环境变量

**文件**：`.env.development`
```bash
# Vite 会自动注入这些变量
VITE_API_BASE_URL=http://localhost:8123
```

**文件**：`.env.production`
```bash
VITE_API_BASE_URL=https://your-domain.com
```

#### 2. Vite 配置

**文件**：`vite.config.ts`
```typescript
export default defineConfig({
  plugins: [vue(), vueDevTools()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8123',  // 后端服务地址
        changeOrigin: true,
        secure: false,
      },
    },
  },
})
```

**说明**：
- **路径别名**：`@/` 指向 `src/` 目录
- **开发代理**：`/api` 请求代理到 `http://localhost:8123`
- **Vue DevTools**：开发环境自动启用

#### 3. OpenAPI 类型生成配置

**文件**：`openapi2ts.config.ts`
```typescript
export default {
  requestLibPath: "import request from '@/request'",
  schemaPath: 'http://localhost:8123/api/v3/api-docs',  // 后端 API 文档地址
  serversPath: './src',
}
```

### 前端开发流程

#### 1. 启动开发服务器

```bash
cd lan-ai-code-mother-frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

访问：http://localhost:5173

#### 2. 生成 API 类型定义

**确保后端服务已启动**，然后运行：

```bash
npm run openapi2ts
```

类型定义将自动生成到：
- `src/api/typings.d.ts` - 全局类型定义
- `src/api/*Controller.ts` - 各模块接口和类型

**⚠️ 注意**：不要手动编辑这些文件，它们会在下次运行时被覆盖。

#### 3. 开发新页面

**步骤 1**：创建页面组件

```vue
<!-- src/pages/user/UserSettingsPage.vue -->
<script setup lang="ts">
import { ref } from 'vue'
import { message } from 'ant-design-vue'

const loading = ref(false)
const formData = ref({
  username: '',
  email: '',
})

const handleSubmit = async () => {
  loading.value = true
  try {
    // 调用 API
    message.success('保存成功')
  } catch (error) {
    // 全局拦截器已处理错误提示
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="settings-page">
    <a-card title="设置">
      <a-form :model="formData" @submit="handleSubmit">
        <!-- 表单内容 -->
      </a-form>
    </a-card>
  </div>
</template>

<style scoped>
.settings-page {
  padding: 24px;
}
</style>
```

**步骤 2**：添加路由配置

```typescript
// src/router/index.ts
{
  path: 'settings',
  name: 'UserSettings',
  component: () => import('@/pages/user/UserSettingsPage.vue'),
  meta: {
    title: '设置',
    requiresAuth: true  // 需要登录
  }
}
```

**步骤 3**：（可选）添加权限控制

```typescript
{
  path: 'admin/users',
  name: 'UserManage',
  component: () => import('@/pages/admin/UserManagePage.vue'),
  meta: {
    title: '用户管理',
    role: 'admin'  // 仅管理员可访问
  }
}
```

### 前端组件开发规范

#### 1. 组件编写规范

**使用 `<script setup lang="ts">` 语法**：

```vue
<script setup lang="ts">
// 1. 导入
import { ref, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'

// 2. 定义 Props（使用 TypeScript 接口）
interface Props {
  title: string
  visible?: boolean
  data?: Record<string, any>
}
const props = withDefaults(defineProps<Props>(), {
  visible: false,
  data: () => ({})
})

// 3. 定义 Emits
const emit = defineEmits<{
  close: []
  submit: [data: Record<string, any>]
}>()

// 4. 响应式状态
const loading = ref(false)

// 5. 计算属性
const titleText = computed(() => props.title || '默认标题')

// 6. 方法
const handleSubmit = async () => {
  loading.value = true
  try {
    // 业务逻辑
    emit('submit', { /* data */ })
  } finally {
    loading.value = false
  }
}

// 7. 生命周期
onMounted(() => {
  // 初始化逻辑
})
</script>

<template>
  <div class="my-component">
    <h1>{{ titleText }}</h1>
    <!-- 模板内容 -->
  </div>
</template>

<style scoped>
.my-component {
  /* 组件样式 */
}
</style>
```

#### 2. 组件命名规范

- **文件名**：大驼峰（PascalCase）+ `.vue`
  - ✅ `UserListPage.vue`
  - ❌ `userList.vue` 或 `user-list.vue`

- **组件引用**：使用大驼峰
  ```vue
  <script setup>
  import UserList from '@/components/UserList.vue'
  </script>

  <template>
    <UserList />
  </template>
  ```

#### 3. Props 和 Emits 定义

**使用 TypeScript 接口定义类型**：

```typescript
// Props
interface Props {
  userId: number
  userName: string
  isAdmin?: boolean  // 可选属性
}
const props = withDefaults(defineProps<Props>(), {
  isAdmin: false  // 默认值
})

// Emits
const emit = defineEmits<{
  update: [value: string]
  delete: [id: number]
  change: [event: Event]
}>()
```

#### 4. 组件通信

**父传子（Props）**：
```vue
<!-- 父组件 -->
<UserCard :user-id="userId" :user-name="userName" />

<!-- 子组件 -->
<script setup lang="ts">
interface Props {
  userId: number
  userName: string
}
const props = defineProps<Props>()
</script>
```

**子传父（Emits）**：
```vue
<!-- 子组件 -->
<script setup lang="ts">
const emit = defineEmits<{
  submit: [data: FormData]
}>()

const handleSubmit = () => {
  emit('submit', formData)
}
</script>

<!-- 父组件 -->
<UserForm @submit="handleFormSubmit" />
```

**跨组件通信（Pinia Store）**：
```typescript
// stores/loginUser.ts
export const useLoginUserStore = defineStore('loginUser', () => {
  const user = ref<User | null>(null)

  const fetchUser = async () => {
    // 获取用户逻辑
  }

  return { user, fetchUser }
})

// 组件中使用
<script setup>
import { useLoginUserStore } from '@/stores/loginUser'

const loginUserStore = useLoginUserStore()
const user = computed(() => loginUserStore.user)
</script>
```

### 前端 API 调用最佳实践

#### 1. 基本调用

```typescript
import { getAppVoById, updateApp, deleteApp } from '@/api/appController'

// 查询
const app = await getAppVoById({ id: appId })

// 更新
await updateApp({
  id: appId,
  appName: 'New Name',
  codeGenType: 'HTML_CODE'
})

// 删除
await deleteApp({ id: appId })
```

#### 2. 错误处理

```typescript
try {
  await deployApp({ appId })
  message.success('部署成功')
} catch (error) {
  // 全局拦截器已处理错误提示
  // 这里可以添加特定的错误处理逻辑
  console.error('部署失败:', error)
}
```

#### 3. 并发请求

```typescript
import { getAppVoById, getUserVoById } from '@/api'

const [app, user] = await Promise.all([
  getAppVoById({ id: appId }),
  getUserVoById({ id: userId })
])
```

#### 4. 取消请求

```typescript
import axios from 'axios'

const controller = new AbortController()

try {
  await someApi({ signal: controller.signal })
} catch (error) {
  if (axios.isCancel(error)) {
    console.log('请求已取消')
  }
}

// 取消请求
controller.abort()
```

### 前端调试技巧

#### 1. Vue DevTools

项目已集成 `vite-plugin-vue-devtools`，开发时自动启用：

- **查看组件树**：检查组件层级和 Props/Emits
- **Pinia 状态**：查看和修改 Store 状态
- **性能分析**：组件渲染性能分析
- **路由信息**：查看当前路由和参数

**快捷键**：
- **Windows/Linux**：`Ctrl + Shift + P`
- **macOS**：`Cmd + Shift + P`

#### 2. 网络请求调试

**浏览器 DevTools → Network**：

1. 筛选 XHR/EventSource 请求
2. 查看请求头、响应、错误信息
3. 检查请求参数和响应数据

**常见检查点**：
- ✅ 请求 URL 是否正确
- ✅ 请求方法（GET/POST/PUT/DELETE）
- ✅ 请求头（Content-Type、Cookie）
- ✅ 响应状态码（200/401/500）
- ✅ 响应数据格式

#### 3. AI 对话流式响应调试

**Network → EventSource → 查看 `/app/chat/gen/code`**

**消息格式**：
```json
{
  "d": "代码片段"
}
```

**完成标识**：监听 `done` 事件

**调试技巧**：
```typescript
const eventSource = new EventSource(url)

eventSource.onmessage = (event) => {
  console.log('收到消息:', event.data)
  const data = JSON.parse(event.data)
  generatedCode.value += data.d
}

eventSource.addEventListener('done', () => {
  console.log('生成完成')
  eventSource.close()
})

eventSource.onerror = (error) => {
  console.error('连接错误:', error)
}
```

#### 4. Console 调试

```typescript
// 打印变量
console.log('用户信息:', user)

// 打印表格
console.table(appList)

// 分组打印
console.group('API 调用')
console.log('请求参数:', params)
console.log('响应数据:', response)
console.groupEnd()

// 性能计时
console.time('数据处理')
// ... 数据处理逻辑
console.timeEnd('数据处理')
```

#### 5. TypeScript 类型检查

```bash
# 运行类型检查
npm run type-check
```

**VS Code 配置**：
```json
// .vscode/settings.json
{
  "typescript.tsdk": "node_modules/typescript/lib",
  "typescript.enablePromptUseWorkspaceTsdk": true
}
```

### 前端常见问题

#### Q: 如何处理后端返回的大数字精度丢失问题？

A: 前端已在 `src/request.ts` 中自动处理。超过 `Number.MAX_SAFE_INTEGER` 的数字会自动转为字符串。

**示例**：
```typescript
// 后端返回：{ "id": 9007199254740991 }
// 前端自动转为：{ "id": "9007199254740991" }
```

#### Q: 如何处理积分不足等业务异常？

A: 后端会返回特定错误码（如 50001），前端通过 Axios 拦截器统一处理：

```typescript
// src/request.ts
if (error.response?.data?.code === 50001) {
  message.error('积分不足，请充值后重试')
}
```

#### Q: 类型定义从哪里来？

A: 运行 `npm run openapi2ts` 从后端 OpenAPI 文档自动生成。不要手动编辑 `src/api/typings.d.ts`。

**重新生成步骤**：
1. 确保后端服务已启动
2. 运行 `npm run openapi2ts`
3. 重启前端开发服务器

#### Q: 如何切换到生产环境 API？

A: 修改 `src/config/env.ts` 中的 `API_BASE_URL`：

```typescript
export const API_BASE_URL = 'https://your-domain.com/api'
```

或在构建时设置环境变量：
```bash
npm run build
```

#### Q: 如何优化前端构建体积？

A: 1. 使用路由懒加载：
```typescript
component: () => import('@/pages/user/UserCenterPage.vue')
```

2. 按需导入 Ant Design Vue 组件：
```typescript
import { Button, Form, Input } from 'ant-design-vue'
```

3. 分析构建产物：
```bash
npm run build
# 查看 dist/report.html
```

#### Q: 如何实现主题定制？

A: 在 `src/styles/global.css` 中覆盖 CSS 变量：

```css
:root {
  --primary-color: #1890ff;
  --border-radius: 4px;
}
```

或使用 Ant Design Vue 主题配置：
```typescript
// main.ts
import { ConfigProvider } from 'ant-design-vue'

app.use(ConfigProvider, {
  theme: {
    primaryColor: '#1890ff',
  }
})
```

### 前端性能优化建议

#### 1. 组件懒加载

```typescript
// 路由懒加载
{
  path: 'user/center',
  component: () => import('@/pages/user/UserCenterPage.vue')
}

// 组件懒加载
<script setup>
const HeavyComponent = defineAsyncComponent(
  () => import('@/components/HeavyComponent.vue')
)
</script>
```

#### 2. 虚拟滚动

对于长列表使用虚拟滚动：
```vue
<template>
  <a-virtual-list
    :data-source="longList"
    :height="600"
    item-key="id"
  >
    <template #item="{ item }">
      <div>{{ item.name }}</div>
    </template>
  </a-virtual-list>
</template>
```

#### 3. 防抖和节流

```typescript
import { debounce } from 'lodash-es'

const handleSearch = debounce((keyword: string) => {
  // 搜索逻辑
}, 300)
```

#### 4. 图片懒加载

```vue
<template>
  <img v-lazy="imageUrl" alt="description" />
</template>
```

#### 5. 代码分割

```typescript
// vite.config.ts
export default defineConfig({
  build: {
    rollupOptions: {
      output: {
        manualChunks: {
          'ant-design-vue': ['ant-design-vue'],
          'vue-vendor': ['vue', 'vue-router', 'pinia']
        }
      }
    }
  }
})
```

### 前端推荐 IDE 设置

#### VS Code

**推荐扩展**：
- [Vue - Official](https://marketplace.visualstudio.com/items?itemName=Vue.volar) - Vue 语言支持
- [TypeScript Vue Plugin](https://marketplace.visualstudio.com/items?itemName=Vue.volar) - TypeScript Vue 插件
- [ESLint](https://marketplace.visualstudio.com/items?itemName=dbaeumer.vscode-eslint) - 代码检查
- [Prettier](https://marketplace.visualstudio.com/items?itemName=esbenp.prettier-vscode) - 代码格式化

**工作区配置** (`.vscode/settings.json`)：
```json
{
  "editor.formatOnSave": true,
  "editor.defaultFormatter": "esbenp.prettier-vscode",
  "editor.codeActionsOnSave": {
    "source.fixAll.eslint": true
  },
  "typescript.tsdk": "node_modules/typescript/lib",
  "volar.autoCompleteRefs": true
}
```

---

## 🔧 故障排查

### 数据库连接问题

**错误**：`Could not create connection to database server`

**解决方案**：

1. 检查 MySQL 是否运行：
   ```bash
   docker ps | grep mysql
   # 或
   pg_isready
   ```

2. 检查 `application-local.yml` 中的数据库连接字符串格式：
   ```
   jdbc:mysql://localhost:3306/lan_ai_code_mother
   ```

3. 确保数据库已创建：
   ```sql
   CREATE DATABASE IF NOT EXISTS lan_ai_code_mother;
   ```

### Redis 连接问题

**错误**：`Unable to connect to Redis`

**解决方案**：

1. 检查 Redis 是否运行：
   ```bash
   docker ps | grep redis
   # 或
   redis-cli ping
   ```

2. 检查 Redis 配置：
   ```yaml
   spring:
     data:
       redis:
         host: localhost
         port: 6379
         password:  # 如果有密码则填写
   ```

### AI API 调用失败

**错误**：`401 Unauthorized` 或 `API key invalid`

**解决方案**：

1. 检查 API Key 是否正确：
   ```yaml
   langchain4j:
     open-ai:
       chat-model:
         api-key: YOUR_API_KEY  # 替换为真实 API Key
   ```

2. 确认 API Key 有效且未过期：
   - 访问 [智谱 AI 控制台](https://open.bigmodel.cn/) 检查

3. 检查 API 配额是否用完

### 积分扣减失败

**错误**：`50001 积分不足`

**解决方案**：

1. 检查用户积分余额：
   ```sql
   SELECT * FROM user_account WHERE user_id = ?;
   ```

2. 充值积分：
   ```sql
   UPDATE user_account SET available_points = available_points + 100 WHERE user_id = ?;
   ```

3. 查看积分流水记录：
   ```sql
   SELECT * FROM point_log WHERE user_id = ? ORDER BY create_time DESC LIMIT 10;
   ```

### 前端 API 调用失败

**错误**：`Network Error` 或 `404 Not Found`

**解决方案**：

1. 确保后端服务已启动：
   ```bash
   curl http://localhost:8123/api/doc.html
   ```

2. 检查 Vite 代理配置：
   ```typescript
   server: {
     proxy: {
       '/api': {
         target: 'http://localhost:8123',
         changeOrigin: true
       }
     }
   }
   ```

3. 查看浏览器 DevTools Network 面板，检查请求详情

### AI 生成代码超时

**错误**：`Read timed out`

**解决方案**：

1. 增加超时时间：
   ```yaml
   langchain4j:
     open-ai:
       chat-model:
         timeout: 600s  # 增加到 10 分钟
   ```

2. 检查网络连接到 AI API 是否稳定

3. 考虑使用更快的 AI 模型

### 类型定义不存在

**错误**：`Cannot find module '@/api/appController'`

**解决方案**：

1. 确保后端服务已启动

2. 重新生成类型定义：
   ```bash
   cd lan-ai-code-mother-frontend
   npm run openapi2ts
   ```

3. 检查 `openapi2ts.config.ts` 配置：
   ```typescript
   {
     schemaPath: 'http://localhost:8123/api/v3/api-docs',
     serversPath: './src'
   }
   ```

### Maven 依赖下载失败

**错误**：`Could not resolve dependencies`

**解决方案**：

1. 配置国内镜像源（`~/.m2/settings.xml`）：
   ```xml
   <mirrors>
     <mirror>
       <id>aliyun</id>
       <mirrorOf>central</mirrorOf>
       <url>https://maven.aliyun.com/repository/public</url>
     </mirror>
   </mirrors>
   ```

2. 清理并重新下载：
   ```bash
   mvn clean install -U
   ```

### 前端构建失败

**错误**：`TypeScript error` 或 `Build failed`

**解决方案**：

1. 使用纯构建（跳过类型检查）：
   ```bash
   npm run pure-build
   ```

2. 检查 TypeScript 错误：
   ```bash
   npm run type-check
   ```

3. 清理并重新安装依赖：
   ```bash
   rm -rf node_modules package-lock.json
   npm install
   ```

## 📚 开发指南

### 添加新的业务类型积分扣减

步骤：

1. **在枚举中添加业务类型**：
   ```java
   // PointBusinessTypeEnum.java
   NEW_FEATURE("new_feature", "新功能");
   ```

2. **在数据库中添加规则**：
   ```sql
   INSERT INTO point_rule (rule_key, rule_value, rule_name, business_type, status)
   VALUES ('new_feature_cost', -50, '新功能消费', 'new_feature', 1);
   ```

3. **在 Service 方法上添加注解**：
   ```java
   @ConsumePoints(
       businessType = PointBusinessTypeEnum.NEW_FEATURE,
       ruleKey = PointRuleKeyEnum.NEW_FEATURE_COST,
       once = false  // 每次都扣费
   )
   public void newFeatureMethod(Long userId, Long appId) {
       // 业务逻辑
   }
   ```

详细参考：`docs/POINT_AOP_USAGE.md`

### 添加新页面（前端）

1. **创建页面组件**：
   ```vue
   <!-- src/pages/user/UserSettingsPage.vue -->
   <script setup lang="ts">
   import { ref } from 'vue'

   const settings = ref({
       // ...
   })
   </script>

   <template>
     <div class="settings-page">
       <h1>设置</h1>
       <!-- 页面内容 -->
     </div>
   </template>
   ```

2. **添加路由配置**：
   ```typescript
   // src/router/index.ts
   {
     path: 'settings',
     component: () => import('@/pages/user/UserSettingsPage.vue'),
     meta: { title: '设置' }
   }
   ```

3. **（可选）添加权限控制**：
   ```typescript
   {
     path: 'admin/users',
     component: () => import('@/pages/admin/UserManagePage.vue'),
     meta: { title: '用户管理', role: 'admin' }
   }
   ```

### 调用后端 API（前端）

1. **确保 API 已在后端定义**：
   ```java
   @RestController
   @RequestMapping("/app")
   public class AppController {

       @GetMapping("/get")
       public BaseResponse<App> getAppById(Long id) {
           // ...
       }
   }
   ```

2. **运行类型生成**：
   ```bash
   npm run openapi2ts
   ```

3. **在组件中调用**：
   ```typescript
   import { getAppVoById } from '@/api/appController'

   const appInfo = await getAppVoById({ id: appId })
   ```

### 添加新的 AI 工作流节点

1. **创建节点类**：
   ```java
   @Component
   public class CustomNode implements Node<State> {

       @Override
       public State execute(State state) {
           // 节点逻辑
           return state;
       }
   }
   ```

2. **在工作流中注册**：
   ```java
   // CodeGenWorkflow.java
   @Override
   public Graph<State> buildGraph() {
       return StateGraph.builder()
           .addNode("customNode", customNode)
           // 添加边和条件
           .build();
   }
   ```

### 编码规范

#### 后端规范

- **命名规范**：
  - 类名：大驼峰（PascalCase）
  - 方法名/变量名：小驼峰（camelCase）
  - 常量：全大写下划线分隔（UPPER_SNAKE_CASE）

- **注解使用**：
  - `@Resource` 用于依赖注入
  - `@AuthCheck` 用于权限验证
  - `@ConsumePoints` 用于积分扣减
  - `@RateLimit` 用于接口限流

- **异常处理**：
  - 统一使用 `BusinessException` 抛出业务异常
  - 由全局异常处理器统一返回 `BaseResponse`

#### 前端规范

- **命名规范**：
  - 组件文件：大驼峰（PascalCase）+ `.vue`
  - 工具函数/常量：小驼峰（camelCase）
  - 类型定义：大驼峰（PascalCase）+ `.ts`

- **代码风格**：
  - 使用 ESLint + Prettier 自动格式化
  - 组件采用 `<script setup lang="ts">` 语法
  - 使用组合式 API（Composition API）

- **API 调用**：
  - 统一使用 `src/api/` 下的封装方法
  - 使用 TypeScript 接口定义请求/响应类型

## ❓ 常见问题

### Q: 如何修改积分扣减规则？

A:
1. 通过管理员接口 `/point/rules` 查询所有规则
2. 使用 `/point/rules` (PUT) 接口更新规则值
3. 或直接修改数据库 `point_rule` 表

### Q: AI 生成的代码保存在哪里？

A:
- 生成代码：`tmp/code_output/{type}_{appId}/`
- 部署代码：`static/app/{deployKey}/`
- 支持的代码类型：`HTML_CODE`、`MULTI_FILE_CODE`

### Q: 如何调试 AI 代码生成工作流？

A:
1. 参考 `src/test/java/com/lanhai/lanaicodemother/langgraph4j/CodeGenWorkflowTest.java`
2. 查看 `CodeGenWorkflow.java` 了解工作流配置
3. 查看各个 Node 的实现（`langgraph4j/node/`）

### Q: 前端如何调用后端接口？

A:
1. 查看已有的 API 封装：`src/api/*.ts`
2. 使用 `axios` 实例：`src/request.ts`
3. 响应数据统一格式：`BaseResponse<T>`

### Q: 如何添加新的业务类型积分扣减？

A:
1. 在 `PointBusinessTypeEnum` 中添加枚举
2. 在 `point_rule` 表中添加规则配置
3. 在 Service 方法上添加 `@ConsumePoints` 注解
4. 参考：`docs/POINT_AOP_USAGE.md`

### Q: 积分扣减如何保证事务一致性？

A:
- AOP 切面使用 `@Transactional` 保证事务
- 积分不足会自动回滚
- 流水日志与扣减操作在同一事务中
- 参考：`src/main/java/com/lanhai/lanaicodemother/aspect/ConsumePointsAspect.java`

### Q: 如何切换 AI 模型？

A:
修改 `application-local.yml` 中的配置：
```yaml
langchain4j:
  open-ai:
    chat-model:
      base-url: https://api.openai.com/v1  # 或其他兼容 OpenAI API 的服务
      api-key: your_api_key
      model-name: gpt-4
```

### Q: 如何启用 HTTPS？

A:
1. 在生产环境配置 HTTPS 证书（推荐使用 Let's Encrypt）
2. 配置 Nginx 反向代理
3. 修改 `src/config/env.ts` 中的 `API_BASE_URL` 为 `https://`

## 🤝 贡献指南

我们欢迎所有形式的贡献！

### 代码贡献流程

1. **Fork 项目**
2. **创建特性分支**：
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. **提交更改**：
   ```bash
   git commit -m 'feat: add some amazing feature'
   ```
4. **推送到分支**：
   ```bash
   git push origin feature/amazing-feature
   ```
5. **提交 Pull Request**

### 代码提交规范

采用 [Conventional Commits](https://www.conventionalcommits.org/) 规范：

```
<type>(<scope>): <subject>

<body>

<footer>
```

**类型（type）**：
- `feat`: 新功能
- `fix`: 修复 bug
- `refactor`: 重构
- `docs`: 文档更新
- `test`: 测试相关
- `chore`: 构建/工具链相关

**示例**：
```
feat(point): 添加连续签到奖励功能

- 实现 3 天/7 天连续签到奖励逻辑
- 更新积分流水记录
- 添加签到记录查询接口

Closes #123
```

### 开发环境准备

1. 阅读 `CLAUDE.md` 了解项目架构
2. 按照 [快速开始](#快速开始) 配置开发环境
3. 运行测试确保一切正常

## 📄 许可证

本项目采用 MIT 许可证。详情请参阅 [LICENSE](LICENSE) 文件。

## 📞 联系方式

- **项目负责人**：致爱蓝海 (hhzalh)
- **仓库地址**：[Gitee](https://gitee.com/hhzalh/lan-ai-code-mother)
- **问题反馈**：[Issues](https://gitee.com/hhzalh/lan-ai-code-mother/issues)

---

<div align="center">

**如果这个项目对您有帮助，请给一个 ⭐️ Star！**

Made with ❤️ by Lan-AI-Code-Mother Team

</div>
