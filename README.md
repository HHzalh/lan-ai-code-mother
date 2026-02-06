# Lan-AI-Code-Mother 微服务版

基于 Dubbo + Nacos +Higress 的微服务架构 AI 代码生成与部署平台，支持跨服务调用和分布式部署。

## 项目简介

Lan-AI-Code-Mother 是一个基于 AI 的代码生成与部署平台。用户通过自然语言对话生成前端应用代码，并支持一键部署到静态资源服务器。微服务版本将原有单体应用拆分为多个独立服务，实现了服务解耦、水平扩展和灵活部署。

### 核心特性

- 🤖 **AI 驱动代码生成**：基于 LangChain4j + DeepSeek/AliDashScope API
- 🏗️ **Dubbo 微服务架构**：服务解耦，独立部署，水平扩展
- 🔍 **Nacos 服务治理**：服务注册发现、配置管理
- 💰 **积分经济系统**：签到、邀请、消费等多维度积分管理
- 👥 **用户权限管理**：基于 RBAC 的权限控制
- 📸 **异步截图服务**：基于 RabbitMQ + Selenium 的应用预览截图
- 🚀 **一键部署**：生成代码自动部署到静态资源服务器

## 微服务架构

### 服务模块划分

```
lan-ai-code-mother-microservices/
├── lan-ai-code-common/      # 公共模块（工具类、异常处理、AOP、限流等）
├── lan-ai-code-model/       # 数据模型模块（实体类、DTO、VO、枚举）
├── lan-ai-code-client/      # Dubbo 服务接口定义（内部服务接口）
├── lan-ai-code-user/        # 用户服务（用户管理、认证、邮件）
├── lan-ai-code-point/       # 积分服务（积分账户、流水、签到、规则）
├── lan-ai-code-ai/          # AI 服务（代码生成、LangChain4j、Redis ChatMemory）
├── lan-ai-code-screenshot/  # 截图服务（Selenium 截图、RabbitMQ 消费）
└── lan-ai-code-app/         # 网关应用服务（对外 API、业务编排、Session 共享）
```

| 端口 | 服务名称               | 路由前缀                        | 主要功能                                   | 依赖服务                         |
| ---- | ---------------------- | ------------------------------- | ------------------------------------------ | -------------------------------- |
|      | **通用模块**           |                                 |                                            |                                  |
|      | lan-ai-code-common     | -                               | 注解、异常处理、工具类、常量、公共响应类   | -                                |
|      | lan-ai-code-model      | -                               | 实体类、DTO、VO、枚举类、AI 模型类         | common                           |
|      | lan-ai-code-client     | -                               | 服务接口定义、内部调用契约                 | common、model                    |
|      | **业务服务**           |                                 |                                            |                                  |
| 8124 | lan-ai-code-user       | /api/user/**                    | 用户管理、权限认证、用户信息维护           | Redis、MySQL                     |
| 8125 | lan-ai-code-app        | /api/app/** /api/chatHistory/** | 应用管理、聊天历史、项目下载、代码解析保存 | Redis、MySQL、用户服务、截图服务 |
|      | lan-ai-code-ai         | -                               | AI代码生成、模型管理                       | AI 大模型                        |
| 8126 | lan-ai-code-point      | /api/point/**                   | 积分管理、积分规则、积分流水、签到、邀请   | Redis、MySQL、用户服务、应用服务 |
| 8127 | lan-ai-code-screenshot | /api/screenshot/**              | 网页截图、图片处理、对象存储               | 腾讯云 COS、RabbitMQ             |


### 服务依赖关系

```
┌─────────────────────────────────────────────────────────────────┐
│                         Nacos 注册中心                           │
│                    (服务注册与发现、配置管理)                      │
└─────────────────────────────────────────────────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
        ▼                     ▼                     ▼
┌───────────────┐    ┌───────────────┐    ┌───────────────┐
│  App 服务     │    │  User 服务     │    │ Point 服务    │
│  (8125)       │    │  (8124)       │    │  (8126)       │
│               │    │               │    │               │
│ - Dubbo 消费者│    │ - Dubbo 提供者 │    │ - Dubbo 提供者│
│ - Session管理 │    │ - 用户管理     │    │ - 积分账户     │
│ - 业务编排    │    │ - 邮件验证     │    │ - 积分流水     │
└───────────────┘    └───────────────┘    └───────────────┘
        │                     │                     │
        └─────────────────────┼─────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        ▼                     ▼                     ▼
┌───────────────┐    ┌───────────────┐    ┌───────────────┐
│  AI 服务      │    │Screenshot服务 │    │   Redis       │
│  (内部调用)    │    │  (8127)       │    │               │
│               │    │               │    │ - Session存储 │
│ - 代码生成     │    │ - Selenium截图│    │ - Chat Memory │
│ - LangChain4j │    │ - RabbitMQ消费│    │ - 积分缓存    │
└───────────────┘    └───────────────┘    └───────────────┘
                              │
                              ▼
                    ┌───────────────┐
                    │   MySQL       │
                    │               │
                    │ - 业务数据     │
                    └───────────────┘
```

### Dubbo 服务接口

**内部服务接口定义** (`lan-ai-code-client` 模块):

- **InnerUserService**: 用户查询接口
  - `getById(id)`: 根据 ID 查询用户
  - `listByIds(ids)`: 批量查询用户
  - `getUserVO(user)`: 用户实体转 VO

- **InnerPointService**: 积分操作接口
  - `deductPoints()`: 扣减积分
  - `addPoints()`: 增加积分
  - `getUserAccount()`: 获取积分账户
  - `checkPointsSufficient()`: 检查积分是否充足

- **InnerScreenshotService**: 截图服务接口
  - `asyncCaptureScreenshot()`: 异步截图任务

### 技术栈

**核心框架**:
- **Java 21**: 采用最新的长期支持版本
- **Spring Boot 3.5.3**: 现代化的 Spring 框架
- **Spring Cloud 2023.0.1**: 微服务基础设施
- **Spring Cloud Alibaba 2023.0.1.0**: 阿里巴巴微服务生态

**微服务治理**:
- **Apache Dubbo 3.3.0**: 高性能 RPC 框架（使用 Triple 协议）
- **Nacos 2.x**: 服务注册发现、配置中心
- **Spring Session + Redis**: 分布式 Session 共享

**数据访问**:
- **MyBatis-Flex 1.11.0**: 轻量级 ORM 框架
- **MySQL 8.0+**: 关系型数据库
- **HikariCP**: 高性能数据库连接池
- **Redis + Redisson 3.50.0**: 分布式缓存、锁、限流

**AI 引擎**:
- **LangChain4j 1.1.0-beta7**: Java AI 应用开发框架
- **DeepSeek API**: 深度求索 AI 模型（deepseek-chat、deepseek-reasoner）
- **阿里云 DashScope**: 通义千问模型（智能路由）

**异步处理**:
- **RabbitMQ 3.12+**: 消息队列（异步截图任务）
- **Spring Boot Starter AMQP**: RabbitMQ 集成

**工具库**:
- **Hutool 5.8.38**: Java 工具类库
- **Lombok 1.18.38**: 简化 Java 代码
- **Knife4j 4.4.0**: 接口文档增强（基于 OpenAPI 3.0）
- **Caffeine**: 本地缓存

**其他**:
- **Selenium 4.33.0**: 网页自动化截图
- **WebDriverManager 6.1.0**: 浏览器驱动管理
- **腾讯云 COS 5.6.227**: 对象存储

## 快速开始

### 前置要求

在开始之前，请确保已安装以下环境：

- **JDK 21+**: [下载地址](https://adoptium.net/)
- **Maven 3.8+**: [下载地址](https://maven.apache.org/download.cgi)
- **MySQL 8.0+**: [下载地址](https://dev.mysql.com/downloads/mysql/)
- **Redis 6.0+**: [下载地址](https://redis.io/download)
- **Nacos 2.x**: [下载地址](https://github.com/alibaba/nacos/releases)
- **RabbitMQ 3.12+**: [下载地址](https://www.rabbitmq.com/download.html) (可选，用于异步截图)

推荐使用 Docker 快速启动基础设施服务：

```bash
# MySQL
docker run --name mysql -e MYSQL_ROOT_PASSWORD=123456 -e MYSQL_DATABASE=lan_ai_code_mother -p 3306:3306 -d mysql:8.0

# Redis
docker run --name redis -p 6379:6379 -d redis:7

# Nacos
docker run --name nacos-quick -e MODE=standalone -p 8848:8848 -d nacos/nacos-server:v2.3.0

# RabbitMQ (可选)
docker run --name rabbitmq -p 5672:5672 -p 15672:15672 -d rabbitmq:3.12-management
```

### 1. 克隆项目

```bash
git clone https://gitee.com/hhzalh/lan-ai-code-mother.git
cd lan-ai-code-mother/lan-ai-code-mother-microservices
```

### 2. 安装依赖

编译并安装所有模块到本地 Maven 仓库：

```bash
# 清理并编译所有模块
mvn clean install -DskipTests
```

### 3. 数据库初始化

#### 创建数据库

```bash
mysql -u root -p123456 -e "CREATE DATABASE IF NOT EXISTS lan_ai_code_mother CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

#### 初始化表结构

从父项目目录导入 SQL 脚本：

```bash
# 从父项目导入表结构
mysql -u root -p123456 lan_ai_code_mother < ../sql/create_table.sql

# 导入积分系统表
mysql -u root -p123456 lan_ai_code_mother < ../sql/point_system_table.sql
```

### 4. 配置服务

各服务的配置文件位于 `src/main/resources/application.yml` 和 `application-local.yml`。

#### 4.1 公共配置

所有服务都需要配置以下基础项：

**数据库配置** (所有服务的 `application.yml`):

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/lan_ai_code_mother
    username: root
    password: 123456  # 修改为你的密码
  data:
    redis:
      host: localhost
      port: 6379
      password:  # 如果 Redis 设置了密码，请填写
```

**Nacos 配置** (所有服务的 `application.yml`):

```yaml
dubbo:
  registry:
    address: nacos://127.0.0.1:8848?username=nacos&password=nacos
    register-mode: instance
  protocol:
    name: tri  # Triple 协议
```

#### 4.2 AI 服务配置

**AI 服务** (`lan-ai-code-ai` 模块):

AI 服务作为内部服务，不直接对外暴露，由 App 服务通过 Dubbo 调用。

```yaml
# 在 lan-ai-code-app/src/main/resources/application.yml 中配置
langchain4j:
  open-ai:
    chat-model:
      base-url: https://api.deepseek.com
      api-key: <Your DeepSeek API Key>  # 替换为你的 API Key
      model-name: deepseek-chat
      max-tokens: 8192
    streaming-chat-model:
      base-url: https://api.deepseek.com
      api-key: <Your DeepSeek API Key>
      model-name: deepseek-chat
      max-tokens: 8192
    reasoning-streaming-chat-model:
      base-url: https://api.deepseek.com
      api-key: <Your DeepSeek API Key>
      model-name: deepseek-reasoner
      max-tokens: 32768
    routing-chat-model:
      base-url: https://dashscope.aliyuncs.com/compatible-mode/v1
      api-key: <Your AliDashScope API Key>
      model-name: qwen-turbo
```

#### 4.3 用户服务配置

**用户服务** (`lan-ai-code-user` 模块):

```yaml
server:
  port: 8124
  servlet:
    context-path: /api

# 邮件服务配置 (可选)
mail:
  smtp-host: smtp.example.com
  smtp-port: 587
  from: noreply@example.com
  password: your-mail-password
  ssl-enable: true

# 腾讯云 COS 配置 (用户头像上传)
cos:
  client:
    host: your-custom-domain.com
    secret-id: your-secret-id
    secret-key: your-secret-key
    region: ap-guangzhou
    bucket: your-bucket-name
```

#### 4.4 积分服务配置

**积分服务** (`lan-ai-code-point` 模块):

```yaml
server:
  port: 8126
  servlet:
    context-path: /api

point:
  cachewarm-enabled: true  # 启用积分规则缓存预热
```

#### 4.5 截图服务配置

**截图服务** (`lan-ai-code-screenshot` 模块):

```yaml
server:
  port: 8127
  servlet:
    context-path: /api

# RabbitMQ 配置
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
    virtual-host: /

# 腾讯云 COS 配置 (截图上传)
cos:
  client:
    host: your-custom-domain.com
    secret-id: your-secret-id
    secret-key: your-secret-key
    region: ap-guangzhou
    bucket: your-bucket-name
```

#### 4.6 应用服务配置

**应用服务** (`lan-ai-code-app` 模块):

```yaml
server:
  port: 8125
  servlet:
    context-path: /api
    session:
      cookie:
        max-age: 604800  # 7 天

spring:
  session:
    store-type: redis
    timeout: 604800
```

### 5. 启动服务

#### 5.1 启动 Nacos

确保 Nacos 已启动：

```bash
# Docker 方式启动
docker start nacos-quick

# 或本地安装方式
cd nacos/bin
./startup.sh -m standalone
```

访问 Nacos 控制台：http://localhost:8848/nacos (默认用户名/密码: `nacos/nacos`)

#### 5.2 启动基础设施

确保 MySQL 和 Redis 已启动：

```bash
# Docker 方式
docker start mysql redis rabbitmq

# 或本地服务
# MySQL、Redis、RabbitMQ 按照各自方式启动
```

#### 5.3 启动微服务

按照以下顺序启动服务（因为服务之间存在依赖）：

**方式一：使用 Maven 命令启动**

```bash
# 1. 启动用户服务 (端口 8124)
cd lan-ai-code-user
mvn spring-boot:run

# 2. 启动积分服务 (端口 8126)
cd ../lan-ai-code-point
mvn spring-boot:run

# 3. 启动截图服务 (端口 8127)
cd ../lan-ai-code-screenshot
mvn spring-boot:run

# 4. 启动 AI 服务 (内部服务，无 HTTP 端口)
cd ../lan-ai-code-ai
mvn spring-boot:run

# 5. 启动应用服务 (端口 8125)
cd ../lan-ai-code-app
mvn spring-boot:run
```

**方式二：使用 IDE 启动**

在 IDEA 中依次启动以下 Application 类：

1. `lan-ai-code-user` → `LanAiCodeUserApplication`
2. `lan-ai-code-point` → `LanAiCodePointApplication`
3. `lan-ai-code-screenshot` → `LanAiCodeScreenshotApplication`
5. `lan-ai-code-app` → `LanAiCodeAppApplication`

#### 5.4 验证服务

在 Nacos 控制台的"服务管理 → 服务列表"中，应看到以下服务已注册：

- `lan-ai-code-user` (提供者)
- `lan-ai-code-point` (提供者)
- `lan-ai-code-screenshot` (提供者)
- `lan-ai-code-ai` (提供者)
- `lan-ai-code-app` (消费者)

访问各服务的 API 文档：

- **用户服务**: http://localhost:8124/api/doc.html
- **积分服务**: http://localhost:8126/api/doc.html
- **截图服务**: http://localhost:8127/api/doc.html
- **应用服务**: http://localhost:8125/api/doc.html (主要对外接口)

### 6. 测试接口

使用 Knife4j 接口文档测试，或使用 curl：

```bash
# 用户注册
curl -X POST http://localhost:8124/api/user/register \
  -H "Content-Type: application/json" \
  -d '{"userAccount":"test","userPassword":"12345678","checkPassword":"12345678"}'

# 用户登录
curl -X POST http://localhost:8124/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"userAccount":"test","userPassword":"12345678"}' \
  -c cookies.txt

# 获取当前用户信息
curl -X GET http://localhost:8124/api/user/get/login \
  -b cookies.txt
```

## 服务详细说明

### 1. lan-ai-code-common (公共模块)

**职责**: 提供公共工具类、异常处理、AOP 切面、限流器等。

**核心组件**:

- **异常处理**: `BusinessException`、`ErrorCode`
- **AOP 切面**:
  - `AuthInterceptor`: 权限验证切面 (`@AuthCheck`)
- **工具类**: Hutool 工具集、JWT 工具、加密工具
- **对象存储**: 腾讯云 COS 客户端封装
- **邮件服务**: 邮件发送工具
- **数据库**: MyBatis-Flex 代码生成器

**依赖**:
```xml
<dependency>
    <groupId>com.lanhai</groupId>
    <artifactId>lan-ai-code-common</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### 2. lan-ai-code-model (数据模型模块)

**职责**: 定义所有实体类、DTO、VO、枚举类型。

**主要内容**:

- **实体类** (`entity/`):
  - `User`: 用户信息
  - `UserAccount`: 用户积分账户
  - `PointLog`: 积分流水
  - `App`: 应用信息
  - `ChatHistory`: 对话历史

- **数据传输对象** (`dto/`):
  - 请求 DTO (如 `UserLoginRequest`)
  - 响应 DTO (如 `UserLoginResponse`)

- **视图对象** (`vo/`):
  - 返回前端的 VO (如 `UserVO`、`AppVO`)

- **枚举类型** (`enums/`):
  - `UserRole`: 用户角色
  - `PointBusinessTypeEnum`: 积分业务类型
  - `AppTypeEnum`: 应用类型

**依赖**:
```xml
<dependency>
    <groupId>com.lanhai</groupId>
    <artifactId>lan-ai-code-model</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### 3. lan-ai-code-client (Dubbo 服务接口定义)

**职责**: 定义 Dubbo 内部服务接口，作为服务提供者和消费者的契约。

**核心接口**:

#### InnerUserService (用户服务接口)

```java
public interface InnerUserService {
    List<User> listByIds(Collection<? extends Serializable> ids);
    User getById(Serializable id);
    UserVO getUserVO(User user);
}
```

**提供者**: `lan-ai-code-user`
**消费者**: `lan-ai-code-app`、`lan-ai-code-point`

#### InnerPointService (积分服务接口)

```java
public interface InnerPointService {
    boolean deductPoints(Long userId, Long points, String businessType, String businessId, String remark);
    boolean addPoints(Long userId, Long points, String businessType, String businessId, String remark);
    UserAccount getUserAccount(Long userId);
    boolean checkPointsSufficient(Long userId, Long points);
}
```

**提供者**: `lan-ai-code-point`
**消费者**: `lan-ai-code-app`

#### InnerScreenshotService (截图服务接口)

```java
public interface InnerScreenshotService {
    String asyncCaptureScreenshot(String deployUrl, Long appId);
}
```

**提供者**: `lan-ai-code-screenshot`
**消费者**: `lan-ai-code-app`

### 4. lan-ai-code-user (用户服务)

**端口**: 8124
**职责**: 用户管理、认证授权、邮件验证、头像上传。

**主要功能**:

- **用户管理**: 注册、登录、信息查询、更新
- **权限控制**: 基于角色的访问控制 (RBAC)
- **邮件验证**: 邮箱验证码发送与校验
- **头像上传**: 上传用户头像到腾讯云 COS

**核心 API**:

| 接口路径 | 方法 | 说明 | 是否需登录 |
|---------|------|------|-----------|
| `/api/user/register` | POST | 用户注册 | ❌ |
| `/api/user/login` | POST | 用户登录 | ❌ |
| `/api/user/get/login` | GET | 获取当前登录用户 | ✅ |
| `/api/user/update` | POST | 更新用户信息 | ✅ |
| `/api/user/send-email` | POST | 发送验证邮件 | ✅ |

**Dubbo 服务暴露**:

```java
@DubboService
public class InnerUserServiceImpl implements InnerUserService {
    // 实现用户查询接口
}
```

**数据库表**: `user`、`user_account`

### 5. lan-ai-code-point (积分服务)

**端口**: 8126
**职责**: 积分账户管理、积分流水、签到奖励、积分规则。

**主要功能**:

- **积分账户**: 创建、查询、更新积分账户
- **积分扣减**: 支持事务性扣减、余额检查
- **积分增加**: 签到奖励、邀请奖励、注册奖励
- **流水记录**: 完整的积分变更历史
- **签到系统**: 每日签到、连续签到奖励
- **积分规则**: 动态规则配置、缓存预热

**核心 API**:

| 接口路径 | 方法 | 说明 | 是否需登录 |
|---------|------|------|-----------|
| `/api/point/account` | GET | 获取我的积分账户 | ✅ |
| `/api/point/log` | GET | 获取积分流水 | ✅ |
| `/api/point/sign-in` | POST | 每日签到 | ✅ |
| `/api/point/rules` | GET | 获取积分规则 | ❌ (需 admin) |
| `/api/point/rules` | PUT | 更新积分规则 | ❌ (需 admin) |

**Dubbo 服务暴露**:

```java
@DubboService
public class InnerPointServiceImpl implements InnerPointService {
    // 实现积分操作接口
}
```

**数据库表**: `user_account`、`point_log`、`point_sign_in_record`、`point_rule`

**积分规则示例**:

| 业务类型 | 积分变化 | 说明 |
|---------|---------|------|
| `SIGN_IN` | +10 | 每日签到基础积分 |
| `CONTINUOUS_3_DAYS` | +10 | 连续 3 天额外奖励 |
| `CONTINUOUS_7_DAYS` | +50 | 连续 7 天额外奖励 |
| `REGISTER` | +100 | 新用户注册奖励 |
| `INVITE` | +30 | 邀请新用户奖励 |
| `AI_MESSAGE` | -20 | AI 生成代码 |
| `APP_DEPLOY` | -30 | 部署应用 |

### 6. lan-ai-code-ai (AI 服务)

**端口**: 无 (内部服务，仅通过 Dubbo 调用)
**职责**: AI 代码生成、多轮对话管理、智能路由。

**主要功能**:

- **流式代码生成**: 基于 Server-Sent Events (SSE) 的实时代码生成
- **多模型支持**:
  - `deepseek-chat`: 通用代码生成模型
  - `deepseek-reasoner`: 复杂推理模型 (32K 上下文)
  - `qwen-turbo`: 智能路由模型
- **对话记忆管理**: 基于 Redis 的 Chat Memory（以 `appId` 为记忆 ID）
- **工作流编排** (基于 LangGraph4j):
  1. `ImageCollectorNode`: 收集用户输入中的图片 URL
  2. `PromptEnhancerNode`: 根据图片优化提示词
  3. `RouterNode`: 根据代码类型选择生成策略
  4. `CodeGeneratorNode`: 调用 AI 生成代码
  5. `CodeQualityCheckNode`: 代码质量检查
  6. `ProjectBuilderNode`: 构建项目文件结构

**Dubbo 服务暴露**:

AI 服务目前主要被 App 服务内部调用，暂未暴露独立的 Dubbo 服务接口。

**AI 模型配置**:

在 `lan-ai-code-app` 的 `application.yml` 中配置多个 AI 模型：

```yaml
langchain4j:
  open-ai:
    chat-model:  # 基础模型
      model-name: deepseek-chat
    reasoning-streaming-chat-model:  # 推理模型
      model-name: deepseek-reasoner
    routing-chat-model:  # 路由模型
      model-name: qwen-turbo
```

**依赖服务**:
- Redis: Chat Memory 存储
- DeepSeek API: 代码生成
- 阿里云 DashScope: 智能路由

### 7. lan-ai-code-screenshot (截图服务)

**端口**: 8127
**职责**: 应用预览截图生成、异步处理、上传到对象存储。

**主要功能**:

- **同步截图**: 立即生成应用预览图
- **异步截图**: 基于 RabbitMQ 的异步截图任务
- **浏览器管理**: WebDriverManager 自动管理 Chrome 驱动
- **上传 COS**: 截图自动上传到腾讯云 COS
- **重试机制**: 失败自动重试（最多 3 次）

**核心 API**:

| 接口路径 | 方法 | 说明 | 是否需登录 |
|---------|------|------|-----------|
| `/api/screenshot/capture` | POST | 同步截图 | ✅ (内部) |
| `/api/screenshot/async-capture` | POST | 异步截图 | ✅ (内部) |

**RabbitMQ 消费者**:

```java
@RabbitListener(queues = "screenshot.queue")
public void handleScreenshotTask(ScreenshotTask task) {
    // 消费截图任务
}
```

**Dubbo 服务暴露**:

```java
@DubboService
public class InnerScreenshotServiceImpl implements InnerScreenshotService {
    public String asyncCaptureScreenshot(String deployUrl, Long appId) {
        // 发送消息到 RabbitMQ
        rabbitTemplate.convertAndSend("screenshot.exchange", "screenshot.key", task);
        return taskId;
    }
}
```

**依赖服务**:
- RabbitMQ: 异步任务队列
- 腾讯云 COS: 截图存储
- Selenium + Chrome: 网页截图

### 8. lan-ai-code-app (网关应用服务)

**端口**: 8125
**职责**: 对外 API 网关、业务编排、Session 管理、Dubbo 消费者。

**主要功能**:

- **统一 API 入口**: 所有外部请求的统一网关
- **业务编排**: 协调调用用户、积分、AI、截图等服务
- **Session 管理**: 基于 Redis Session 的登录状态管理
- **积分扣减 AOP**: 使用 `@ConsumePoints` 注解自动扣减积分
- **限流保护**: 使用 `@RateLimit` 注解实现接口限流
- **权限验证**: 使用 `@AuthCheck` 注解进行权限校验

**核心 API**:

| 接口路径 | 方法 | 说明 | 积分消耗 | 是否需登录 |
|---------|------|------|---------|-----------|
| `/api/app/chat` | POST | AI 对话生成代码 | 20 | ✅ |
| `/api/app/create` | POST | 创建新应用 | 0 | ✅ |
| `/api/app/deploy` | POST | 部署应用 | 30 | ✅ |
| `/api/app/get/{id}` | GET | 获取应用详情 | 0 | ✅ |
| `/api/app/list` | GET | 我的应用列表 | 0 | ✅ |

**Dubbo 服务引用**:

```java
@DubboReference
private InnerUserService innerUserService;

@DubboReference
private InnerPointService innerPointService;

@DubboReference
private InnerScreenshotService innerScreenshotService;
```

**AOP 示例**:

```java
@ConsumePoints(
    businessType = PointBusinessTypeEnum.AI_MESSAGE,
    ruleKey = PointRuleKeyEnum.AI_MESSAGE_COST,
    once = false  // 每次调用都扣费
)
@RateLimit(key = "ai_chat", permitsPerSecond = 3, timeout = 1)
@AuthCheck(mustRole = "user")
public Flux<String> chatToGenCode(Long appId, String message) {
    // 1. 调用 AI 服务生成代码
    // 2. 保存对话历史
    // 3. 返回流式响应
}
```

**数据库表**: `app`、`chat_history`

## 开发指南

### 添加新的 Dubbo 服务接口

#### 1. 定义接口

在 `lan-ai-code-client` 模块中创建接口：

```java
package com.lanhai.lanaicodemother.innerservice;

public interface InnerNewService {
    String doSomething(Long userId, String param);
}
```

#### 2. 实现接口

在对应的服务模块（如 `lan-ai-code-new`）中实现：

```java
package com.lanhai.lanaicodemother.impl;

import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class InnerNewServiceImpl implements InnerNewService {
    @Override
    public String doSomething(Long userId, String param) {
        // 业务逻辑
        return "result";
    }
}
```

#### 3. 消费服务

在 `lan-ai-code-app` 或其他消费者中引用：

```java
import org.apache.dubbo.config.annotation.DubboReference;

@Resource
@DubboReference
private InnerNewService innerNewService;

public void someMethod() {
    String result = innerNewService.doSomething(1L, "test");
}
```

### 跨服务 Session 共享

所有服务都使用 Spring Session + Redis 实现 Session 共享：

**配置**:

```yaml
spring:
  session:
    store-type: redis
    timeout: 604800  # 7 天
  data:
    redis:
      host: localhost
      port: 6379
```

**使用**:

```java
// 任何服务都可以获取登录用户
User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
```

### 积分扣减 AOP 使用

在 App 服务中，使用 `@ConsumePoints` 注解自动扣减积分：

```java
@ConsumePoints(
    businessType = PointBusinessTypeEnum.NEW_BUSINESS,
    ruleKey = PointRuleKeyEnum.NEW_BUSINESS_COST,
    once = false  // false=每次扣费, true=仅首次扣费
)
public Result doSomething(Long userId, Long businessId) {
    // 业务逻辑，AOP 会自动扣减积分
}
```

### 分布式限流使用

使用 `@RateLimit` 注解实现接口限流：

```java
@RateLimit(
    key = "user_operation",  // 限流键
    permitsPerSecond = 10,   // 每秒允许 10 次请求
    timeout = 1              // 获取许可超时时间（秒）
)
public Result doOperation() {
    // 业务逻辑
}
```

## 部署指南

### Docker Compose 部署

创建 `docker-compose.yml`:

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: mysql
    environment:
      MYSQL_ROOT_PASSWORD: 123456
      MYSQL_DATABASE: lan_ai_code_mother
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql

  redis:
    image: redis:7
    container_name: redis
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data

  nacos:
    image: nacos/nacos-server:v2.3.0
    container_name: nacos
    environment:
      MODE: standalone
    ports:
      - "8848:8848"
      - "9848:9848"

  rabbitmq:
    image: rabbitmq:3.12-management
    container_name: rabbitmq
    ports:
      - "5672:5672"
      - "15672:15672"
    environment:
      RABBITMQ_DEFAULT_USER: guest
      RABBITMQ_DEFAULT_PASS: guest

volumes:
  mysql-data:
  redis-data:
```

启动：

```bash
docker-compose up -d
```

### 打包部署

#### 1. 打包所有模块

```bash
mvn clean package -DskipTests
```

生成的 JAR 文件位于各模块的 `target/` 目录：

- `lan-ai-code-user/target/lan-ai-code-user-0.0.1-SNAPSHOT.jar`
- `lan-ai-code-point/target/lan-ai-code-point-0.0.1-SNAPSHOT.jar`
- `lan-ai-code-screenshot/target/lan-ai-code-screenshot-0.0.1-SNAPSHOT.jar`
- `lan-ai-code-ai/target/lan-ai-code-ai-0.0.1-SNAPSHOT.jar`
- `lan-ai-code-app/target/lan-ai-code-app-0.0.1-SNAPSHOT.jar`

#### 2. 启动服务

在服务器上按顺序启动：

```bash
# 用户服务
java -jar lan-ai-code-user-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod \
  --server.port=8124

# 积分服务
java -jar lan-ai-code-point-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod \
  --server.port=8126

# 截图服务
java -jar lan-ai-code-screenshot-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod \
  --server.port=8127

# AI 服务
java -jar lan-ai-code-ai-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod

# 应用服务
java -jar lan-ai-code-app-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod \
  --server.port=8125
```

#### 3. 生产环境配置

创建 `application-prod.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://your-prod-host:3306/lan_ai_code_mother
    username: prod_user
    password: strong_password
  data:
    redis:
      host: your-prod-redis-host
      port: 6379
      password: redis_password

dubbo:
  registry:
    address: nacos://your-prod-nacos-host:8848?username=nacos&password=nacos

langchain4j:
  open-ai:
    chat-model:
      api-key: ${DEEPSEEK_API_KEY}
```

启动时指定 profile：

```bash
java -jar app.jar --spring.profiles.active=prod
```

### Nginx 反向代理

配置 Nginx 作为统一入口：

```nginx
upstream app_backend {
    server localhost:8125;
}

upstream user_backend {
    server localhost:8124;
}

upstream point_backend {
    server localhost:8126;
}

server {
    listen 80;
    server_name your-domain.com;

    # 应用服务（主要 API）
    location /api/app/ {
        proxy_pass http://app_backend/api/app/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    # 用户服务
    location /api/user/ {
        proxy_pass http://user_backend/api/user/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    # 积分服务
    location /api/point/ {
        proxy_pass http://point_backend/api/point/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    # API 文档
    location /doc.html {
        proxy_pass http://app_backend/doc.html;
    }
}
```

### 运行Nacos+Higress

- Nacos
  - 访问注册中心 服务列表 看看各个服务是否注册成功
- Higress
  - 在服务来源看是否出现 Nacos.x.x ,创建其他模块的服务来源，填写对应的ip、host
  - 在路由配置绑定对应的服务来源，并填写对应的路由条件
- 前端
  - 根据Higress的地址填写对应的转发地址

## 测试

### 运行测试

```bash
# 运行所有测试
mvn test

# 运行特定模块测试
cd lan-ai-code-user
mvn test

# 运行特定测试类
mvn test -Dtest=UserServiceTest
```

### API 测试

使用 Knife4j 接口文档（推荐）：

- 用户服务: http://localhost:8124/api/doc.html
- 积分服务: http://localhost:8126/api/doc.html
- 截图服务: http://localhost:8127/api/doc.html
- 应用服务: http://localhost:8125/api/doc.html

或使用 curl:

```bash
# 用户登录
curl -X POST http://localhost:8124/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"userAccount":"test","userPassword":"12345678"}' \
  -c cookies.txt

# AI 生成代码（需要先登录）
curl -X POST http://localhost:8125/api/app/chat \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{"appId":1,"message":"创建一个待办事项列表应用"}'

# 每日签到
curl -X POST http://localhost:8126/api/point/sign-in \
  -b cookies.txt
```

## 常见问题

### 1. Dubbo 服务调用失败

**错误**: `No provider available`

**解决方案**:

1. 检查 Nacos 中服务是否已注册：http://localhost:8848/nacos
2. 检查服务提供者是否正常启动
3. 检查 `dubbo.registry.address` 配置是否正确
4. 查看服务提供者日志，确认是否成功暴露服务

```bash
# 查看 Nacos 服务列表
curl -X GET "http://localhost:8848/nacos/v1/ns/instance/list?serviceName=lan-ai-code-user"
```

### 2. Session 共享失败

**错误**: 登录后其他服务获取不到用户信息

**解决方案**:

1. 确保所有服务都引入了 `spring-session-data-redis` 依赖
2. 检查 Redis 连接配置是否一致
3. 确认所有服务的 `spring.session.store-type=redis`

```yaml
spring:
  session:
    store-type: redis
    redis:
      namespace: lan-ai-code  # 确保所有服务使用相同的 namespace
```

### 3. AI 服务调用超时

**错误**: `Timeout waiting for AI response`

**解决方案**:

1. 增加 Dubbo 消费者超时时间：

```yaml
dubbo:
  consumer:
    timeout: 120000  # 120 秒
```

2. 调整 AI 模型的 `max-tokens` 参数
3. 检查网络连接到 DeepSeek API 是否畅通

### 4. 积分扣减失败

**错误**: `Insufficient points` 或积分扣减未生效

**解决方案**:

1. 检查积分规则是否已配置：访问 `/api/point/rules`
2. 确认用户积分账户已创建：访问 `/api/point/account`
3. 查看积分流水：访问 `/api/point/log`
4. 检查 `@ConsumePoints` 注解配置是否正确

### 5. 截图服务失败

**错误**: `Screenshot capture failed`

**解决方案**:

1. 确认 Chrome 浏览器已安装（Selenium 需要）
2. 检查 RabbitMQ 是否正常运行：

```bash
# 查看 RabbitMQ 队列
curl -u guest:guest http://localhost:15672/api/queues
```

3. 检查腾讯云 COS 配置是否正确
4. 查看截图服务日志

### 6. 端口冲突

**错误**: `Port already in use`

**解决方案**:

修改各服务的 `server.port` 配置：

| 服务 | 默认端口 | 可修改为 |
|-----|---------|---------|
| 用户服务 | 8124 | 9124 |
| 应用服务 | 8125 | 9125 |
| 积分服务 | 8126 | 9126 |
| 截图服务 | 8127 | 9127 |

或终止占用端口的进程：

```bash
# Windows
netstat -ano | findstr :8124
taskkill /PID <PID> /F

# Linux/Mac
lsof -i :8124
kill -9 <PID>
```

## 项目结构

```
lan-ai-code-mother-microservices/
├── lan-ai-code-common/           # 公共模块
│   ├── src/main/java/com/lanhai/lanaicodemother/
│   │   ├── aop/                  # AOP 切面
│   │   ├── exception/            # 异常定义
│   │   ├── ratelimiter/          # 限流器
│   │   ├── util/                 # 工具类
│   │   └── cos/                  # COS 客户端
│   └── pom.xml
│
├── lan-ai-code-model/            # 数据模型
│   ├── src/main/java/com/lanhai/lanaicodemother/
│   │   ├── entity/               # 实体类
│   │   ├── dto/                  # 数据传输对象
│   │   ├── vo/                   # 视图对象
│   │   └── enums/                # 枚举类型
│   └── pom.xml
│
├── lan-ai-code-client/           # Dubbo 服务接口定义
│   ├── src/main/java/com/lanhai/lanaicodemother/
│   │   └── innerservice/         # 内部服务接口
│   │       ├── InnerUserService.java
│   │       ├── InnerPointService.java
│   │       └── InnerScreenshotService.java
│   └── pom.xml
│
├── lan-ai-code-user/             # 用户服务
│   ├── src/main/java/com/lanhai/lanaicodemother/
│   │   ├── controller/           # REST 控制器
│   │   ├── service/              # 业务逻辑
│   │   ├── mapper/               # 数据访问
│   │   └── impl/                 # Dubbo 服务实现
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── application-local.yml
│   └── pom.xml
│
├── lan-ai-code-point/            # 积分服务
│   ├── src/main/java/com/lanhai/lanaicodemother/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── mapper/
│   │   └── impl/
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── application-local.yml
│   └── pom.xml
│
├── lan-ai-code-ai/               # AI 服务
│   ├── src/main/java/com/lanhai/lanaicodemother/
│   │   ├── service/              # AI 服务
│   │   │   └── AiCodeGeneratorService.java
│   │   └── langgraph4j/          # 工作流节点
│   ├── src/main/resources/
│   │   └── application.yml
│   └── pom.xml
│
├── lan-ai-code-screenshot/       # 截图服务
│   ├── src/main/java/com/lanhai/lanaicodemother/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── rabbitmq/             # RabbitMQ 监听器
│   │   └── impl/                 # Dubbo 服务实现
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── application-local.yml
│   └── pom.xml
│
├── lan-ai-code-app/              # 网关应用服务
│   ├── src/main/java/com/lanhai/lanaicodemother/
│   │   ├── controller/           # 对外 REST API
│   │   ├── service/              # 业务编排
│   │   └── aop/                  # AOP 切面（特定于 App 服务）
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── application-local.yml
│   └── pom.xml
│
├── pom.xml                       # 父 POM
└── README.md
```

## 环境变量说明

### 必需配置

| 变量 | 说明 | 示例 | 获取方式 |
|-----|------|------|---------|
| `spring.datasource.url` | MySQL 连接地址 | `jdbc:mysql://localhost:3306/lan_ai_code_mother` | 本地安装或云数据库 |
| `spring.datasource.username` | MySQL 用户名 | `root` | - |
| `spring.datasource.password` | MySQL 密码 | `123456` | - |
| `spring.data.redis.host` | Redis 主机 | `localhost` | - |
| `spring.data.redis.port` | Redis 端口 | `6379` | - |
| `dubbo.registry.address` | Nacos 地址 | `nacos://127.0.0.1:8848` | 安装 Nacos |

### AI 服务配置

| 变量 | 说明 | 示例 | 获取方式 |
|-----|------|------|---------|
| `langchain4j.open-ai.chat-model.api-key` | DeepSeek API Key | `sk-xxx` | [DeepSeek 平台](https://platform.deepseek.com/) |
| `langchain4j.open-ai.routing-chat-model.api-key` | 阿里云 API Key | `sk-xxx` | [阿里云 DashScope](https://dashscope.aliyun.com/) |

### 可选配置

| 变量 | 说明 | 示例 | 默认值 |
|-----|------|------|-------|
| `mail.smtp-host` | 邮件服务器 | `smtp.qq.com` | - |
| `cos.client.secret-id` | 腾讯云 SecretId | `xxx` | - |
| `spring.rabbitmq.host` | RabbitMQ 主机 | `localhost` | - |

## 性能优化建议

### 1. 数据库优化

- 为常用查询字段添加索引
- 使用 MyBatis-Flex 的 `@Table` 注解配置分表策略
- 配置 HikariCP 连接池参数：

```yaml
spring:
  datasource:
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      idle-timeout: 30000
      connection-timeout: 30000
```

### 2. Redis 缯存优化

- 启用 Redis 持久化（RDB + AOF）
- 配置合理的过期时间
- 使用 Redis Cluster 提高可用性

### 3. Dubbo 调用优化

- 调整超时时间：`dubbo.consumer.timeout`
- 配置负载均衡策略：`dubbo.consumer.loadbalance=roundrobin`
- 启用异步调用：

```java
@DubboReference(async = true)
private InnerUserService innerUserService;

CompletableFuture<User> future = innerUserService.getById(1L);
```

### 4. JVM 参数优化

生产环境启动时添加 JVM 参数：

```bash
java -Xms2g -Xmx2g \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -XX:+HeapDumpOnOutOfMemoryError \
     -XX:HeapDumpPath=/logs/heapdump.hprof \
     -jar app.jar
```

## 监控与运维

### 健康检查

所有服务默认提供 Spring Boot Actuator 健康检查：

```bash
curl http://localhost:8124/actuator/health
curl http://localhost:8126/actuator/health
curl http://localhost:8125/actuator/health
```

### 日志管理

日志输出目录：`logs/`

```bash
# 查看用户服务日志
tail -f logs/lan-ai-code-user.log

# 查看积分服务日志
tail -f logs/lan-ai-code-point.log
```

### Nacos 监控

访问 Nacos 控制台查看服务状态：

- 服务列表：http://localhost:8848/nacos
- 配置管理：http://localhost:8848/nacos/config.html
- 节点状态：http://localhost:8848/nacos/console.html

## 贡献指南

欢迎贡献代码！请遵循以下规范：

### 代码规范

- 遵循阿里巴巴 Java 开发手册
- 使用 Lombok 简化代码
- 添加必要的注释和文档

### 提交规范

```
<type>(<scope>): <subject>

<body>

<footer>
```

**类型 (type)**:
- `feat`: 新功能
- `fix`: 修复 bug
- `refactor`: 重构
- `docs`: 文档更新
- `test`: 测试相关
- `chore`: 构建/工具链相关

**示例**:
```
feat(dubbo): 添加内部积分服务接口

- 定义 InnerPointService 接口
- 实现 deductPoints 和 addPoints 方法
- 添加积分不足检查逻辑

Closes #123
```

## 相关资源

- [Dubbo 官方文档](https://dubbo.apache.org/zh/docs/)
- [Nacos 官方文档](https://nacos.io/zh-cn/docs/what-is-nacos.html)
- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
- [MyBatis-Flex 文档](https://mybatis-flex.com/)
- [LangChain4j 文档](https://docs.langchain4j.dev/)
- [Redis 文档](https://redis.io/docs/)
- [RabbitMQ 文档](https://www.rabbitmq.com/docs)

## 联系方式

- **项目负责人**: 致爱蓝海 (hhzalh)
- **仓库地址**: [Gitee](https://gitee.com/hhzalh/lan-ai-code-mother)

## 许可证

本项目采用 MIT 许可证。

---

**最后更新**: 2026-02-06
**版本**: 0.0.1-SNAPSHOT
