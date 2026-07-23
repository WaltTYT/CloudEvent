# CloudEvent - 云事件管理系统

基于 Spring Boot + Spring Cloud 微服务架构的文章管理系统，提供用户管理、文章发布、分类管理、文件上传等功能。

## 技术栈

### 后端

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 4.0.3 | 后端框架 |
| Spring Cloud | 2025.1.0 | 微服务框架 |
| Spring Cloud Alibaba | 2025.1.0.0 | Nacos 服务发现与配置 |
| MyBatis | 4.0.0 | ORM 框架 |
| PageHelper | 1.4.6 | 分页插件 |
| MySQL | 8.0+ | 关系型数据库 |
| Redis | 3.2.100 | 缓存 / Token 存储 |
| RabbitMQ | 4.2.4 | 消息队列 |
| JWT (java-jwt) | 4.4.0 | 身份认证 |
| Spring Validation | 内置 | 参数校验 |

### 前端

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | 3.5.32 | 前端框架 |
| Element Plus | 2.14.0 | UI 组件库 |
| Pinia | 3.0.4 | 状态管理 |
| Vue Router | 5.0.4 | 路由 |
| Vite | 8.0.8 | 构建工具 |
| Axios | 1.16.1 | HTTP 客户端 |
| ECharts | 6.1.0 | 图表 |

### 基础设施

| 技术 | 版本 | 用途 |
|------|------|------|
| Nginx | 1.20.2 | 反向代理 / 静态资源 |
| Nacos | 3.1.1 | 服务注册与配置中心 |
| Redis | 3.2.100 | 缓存 / Token 存储 |
| RabbitMQ | 4.2.4 | 消息队列 |

## 项目结构

```
CloudEvent/
├── cloud-event-common/          # 公共模块（实体类、工具类、配置、拦截器）
├── cloud-event-gateway/         # 网关服务（路由转发、鉴权过滤）
├── cloud-event-user/            # 用户服务（注册、登录、信息管理）
├── cloud-event-category/        # 分类服务（分类 CRUD）
├── cloud-event-article/         # 文章服务（文章 CRUD、消息消费）
├── cloud-event-file/            # 文件服务（图片上传）
├── frontend/CLoudEventFron/     # Vue 前端项目
└── Repo/                        # 上传文件存储目录
```

### 服务端口

| 服务 | 端口 | 说明 |
|------|------|------|
| cloud-event-gateway | 8090 | 网关，统一入口 |
| cloud-event-user | 8081 | 用户服务 |
| cloud-event-category | 8082 | 分类服务 |
| cloud-event-article | 8083 | 文章服务 |
| cloud-event-file | 8084 | 文件服务 |
| Nginx | 80 | 反向代理 |

## 快速开始

### 环境要求

- JDK 21+
- Maven 3.9+
- Node.js 18+
- MySQL 8.0+
- Redis 3.2.100
- RabbitMQ 4.2.4
- Nacos 3.1.1

### 数据库初始化

创建以下数据库：
- `cloud_event_user`
- `cloud_event_category`
- `cloud_event_article`

### 启动中间件

```bash
# 启动 Nacos
startup.cmd -m standalone

# 启动 Redis
redis-server

# 启动 RabbitMQ
rabbitmq-server
```

### 启动后端

```bash
# 编译整个项目
mvn clean compile

# 分别启动各服务（建议按以下顺序）
# 1. 网关
cd cloud-event-gateway && mvn spring-boot:run
# 2. 用户服务
cd cloud-event-user && mvn spring-boot:run
# 3. 分类服务
cd cloud-event-category && mvn spring-boot:run
# 4. 文章服务
cd cloud-event-article && mvn spring-boot:run
# 5. 文件服务
cd cloud-event-file && mvn spring-boot:run
```

### 启动前端

```bash
cd frontend/CLoudEventFron
npm install
npm run dev
```

### 访问地址

- 前端页面：`http://localhost`（Nginx）
- 网关入口：`http://localhost:8090/api/...`

## 功能模块

### 用户管理
- 用户注册 / 登录（JWT Token 认证）
- 个人信息查看与修改
- 头像上传
- 密码修改

### 分类管理
- 分类的增删改查
- 按用户隔离分类数据

### 文章管理
- 文章发布 / 编辑 / 删除
- 文章状态管理（草稿 / 已发布）
- 分页查询（支持分类和状态筛选）
- 已发布文章列表（前台展示）
- 文章详情查询（远程填充分类名称和作者名称）

### 文件上传
- 图片上传，返回 Nginx 可访问的 URL

### 消息队列
- 文章发布、操作日志通过 RabbitMQ 异步通知
- 用户注册、登录日志通过 RabbitMQ 异步通知
- 文件上传通过 RabbitMQ 异步通知

## 安全设计

- JWT Token 无状态认证，有效期 1 小时
- Token 存储在 Redis 中，支持主动失效
- 密码使用 MD5 加密存储
- Spring Validation 参数校验
- 网关层全局鉴权过滤器
- 登录拦截器验证受保护路由


