# CloudEvent Agent 配置文件

## 1. 项目概述

### 1.1 项目名称
CloudEvent - 云事件管理系统（文章管理系统）

### 1.2 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 4.0.3 |
| 微服务 | Spring Cloud | 2025.1.0 |
| 服务发现/配置 | Nacos | 3.1.1 |
| 网关 | Spring Cloud Gateway | 2025.1.0 |
| 远程调用 | Spring Cloud OpenFeign | 5.0.0 |
| 负载均衡 | Spring Cloud LoadBalancer | 5.0.0 |
| 消息队列 | RabbitMQ | 4.2.4 |
| 对象关系映射 | MyBatis | 4.0.0 |
| 分页插件 | PageHelper | 1.4.6 |
| 数据库 | MySQL | 8.0+ |
| 缓存 | Redis | 3.2.100 |
| 身份认证 | JWT (java-jwt) | 4.4.0 |
| 参数校验 | Spring Validation | (内置) |
| 前端框架 | Vue | 3.5.32 |
| UI组件库 | Element Plus | 2.14.0 |
| 状态管理 | Pinia | 3.0.4 |
| 路由 | Vue Router | 5.0.4 |
| 构建工具 | Vite | 8.0.8 |
| HTTP客户端 | Axios | 1.16.1 |
| 图表 | ECharts | 6.1.0 |
| 反向代理 | Nginx | 1.20.2 |

### 1.3 架构
- **模式**: 微服务架构，基于 Spring Cloud Gateway 统一网关入口
- **服务发现**: 使用 Nacos 进行服务注册与发现
- **配置管理**: 使用 Nacos 配置中心
- **通信**: 服务间通过 OpenFeign 远程调用，异步消息通过 RabbitMQ
- **网关**: Spring Cloud Gateway (端口 8090)，统一路由转发和 JWT 鉴权
- **数据库**: MySQL，按服务拆分库（`cloud_event_user`、`cloud_event_category`、`cloud_event_article`），Redis 用于缓存 JWT Token

---

## 2. Agent 规则

### 2.1 基本原则
- 每一次执行任务时，都必须先检查用户配置和项目记忆中的偏好设置
- 按要求执行，不多不少
- 除非绝对必要，否则绝不创建文件
- 优先编辑现有文件，而非创建新文件
- 除非明确要求，否则绝不主动创建文档文件（*.md 或 README）
- 提交或推送代码变更前需要明确获得用户许可
- 没有用户明确允许，绝不推送项目到 GitHub
- 没有用户明确允许，绝不删除项目中的任何文件

### 2.2 任务开始前
1. 检查可用技能 - 检查 `<available_skills>` 中是否有相关技能
2. 查看记忆上下文 - 查看用户配置和项目记忆中的偏好设置
3. 先规划后执行 - 使用 `TodoWrite` 处理复杂的多步骤任务
4. 提出澄清问题 - 如果需求不明确，使用 `AskUserQuestion` 工具

### 2.3 任务执行规则

#### 2.3.1 探索阶段
- 使用 `LS`、`Glob`、`Grep`、`SearchCodebase` 工具了解代码库
- 阅读关键文件以理解代码模式和约定
- 识别现有的安全原语（验证器、认证中间件、加密包装器）

#### 2.3.2 规划阶段
- 对于需要 3 个以上工具调用的任务，创建详细的 `TodoWrite` 计划
- 所有任务初始状态为 `pending`，然后转换为 `in_progress`，最后转换为 `completed`
- 同一时间只能有一个任务处于 `in_progress` 状态

#### 2.3.3 实现阶段
- 遵循现有的代码约定和模式
- 模仿代码风格、命名约定和类型定义
- 尽可能使用现有的库和工具
- 绝不引入暴露或记录机密信息的代码

#### 2.3.4 验证阶段
- 运行 `GetDiagnostics` 检查语法和类型错误
- 如果可用，运行项目特定的测试命令
- 验证更改是否按预期工作

### 2.4 代码风格规则
- **除非用户明确要求，否则不添加任何注释**
- 遵循现有的缩进模式（制表符/空格）
- 使用一致的命名约定（camelCase、PascalCase、snake_case）
- 保持代码整洁和可维护

### 2.5 安全指南
- 绝不将机密或密钥提交到仓库
- 遵循 OWASP 安全最佳实践
- 验证和清理所有用户输入
- 使用参数化查询防止 SQL 注入（MyBatis 已默认支持）
- 密码存储前使用 MD5 哈希处理
- JWT Token 存储在 Redis 中，设置 1 小时过期时间
- 网关层 `AuthFilter` 进行全局 JWT 鉴权
- 各服务 `LoginInterceptor` 拦截器验证受保护路由的 Token

---

## 3. 项目结构

### 3.1 模块总览
```
CloudEvent/
├── cloud-event-common/          # 公共模块（实体类、工具类、配置、拦截器、异常处理）
├── cloud-event-gateway/         # 网关服务（路由转发、JWT 鉴权过滤）
├── cloud-event-user/            # 用户服务（注册、登录、信息管理）
├── cloud-event-category/        # 分类服务（分类 CRUD）
├── cloud-event-article/         # 文章服务（文章 CRUD、消息消费、Feign 远程调用）
├── cloud-event-file/            # 文件服务（图片上传）
├── frontend/CLoudEventFron/     # Vue 前端项目
└── Repo/                        # 上传文件存储目录
```

### 3.2 cloud-event-common 公共模块
```
src/main/java/cn/edu/scau/
├── config/                       # 配置类
│   ├── WebConfig.java            # Web 配置（拦截器、静态资源）
│   └── RabbitMqConfig.java       # RabbitMQ 交换机、队列、绑定配置
├── pojo/                         # 实体类
│   ├── User.java                 # 用户实体
│   ├── Article.java              # 文章实体
│   ├── Category.java             # 分类实体
│   ├── PageBean.java             # 分页结果封装
│   └── Result.java               # 统一响应封装
├── utils/                        # 工具类
│   ├── JwtUtil.java              # JWT 生成与解析
│   ├── Md5Util.java              # MD5 加密
│   └── ThreadLocalUtil.java      # 线程本地存储
├── interceptors/                 # 请求拦截器
│   └── LoginInterceptor.java     # JWT Token 校验
├── exception/                    # 全局异常处理
│   └── GlobalExceptionHandler.java
├── anno/                         # 自定义注解
│   └── State.java                # 文章状态校验注解
└── Validation/                   # 自定义验证器
    └── StateValidation.java      # 状态值校验（已发布/草稿）
```

### 3.3 cloud-event-gateway 网关服务
```
src/main/java/cn/edu/scau/gateway/
├── GatewayApplication.java       # 启动类
└── filter/
    └── AuthFilter.java           # 全局 JWT 鉴权过滤器
```

### 3.4 cloud-event-user 用户服务
```
src/main/java/cn/edu/scau/user/
├── UserApplication.java          # 启动类
├── controller/
│   └── UserController.java       # 用户注册、登录、信息管理
├── service/
│   ├── UserService.java          # 用户业务接口
│   ├── UserMessageService.java   # 用户消息服务（RabbitMQ）
│   └── impl/
│       └── UserServiceImpl.java  # 用户业务实现
└── mapper/
    └── UserMapper.java           # 用户数据访问层
```

### 3.5 cloud-event-category 分类服务
```
src/main/java/cn/edu/scau/category/
├── CategoryApplication.java      # 启动类
├── controller/
│   └── CategoryController.java   # 分类 CRUD
├── service/
│   ├── CategoryService.java      # 分类业务接口
│   └── impl/
│       └── CategoryServiceImpl.java  # 分类业务实现
└── mapper/
    └── CategoryMapper.java       # 分类数据访问层
```

### 3.6 cloud-event-article 文章服务
```
src/main/java/cn/edu/scau/article/
├── ArticleApplication.java       # 启动类
├── controller/
│   └── ArticleController.java    # 文章 CRUD、分页查询
├── service/
│   ├── ArticleService.java       # 文章业务接口
│   ├── ArticleMessageService.java # 文章消息服务（RabbitMQ）
│   └── impl/
│       └── ArticleServiceImpl.java  # 文章业务实现
├── mapper/
│   └── ArticleMapper.java        # 文章数据访问层
├── consumer/
│   └── ArticleLogConsumer.java   # 文章日志消息消费者
└── feign/
    ├── CategoryFeignClient.java  # 分类服务远程调用
    └── UserFeignClient.java      # 用户服务远程调用
```

### 3.7 cloud-event-file 文件服务
```
src/main/java/cn/edu/scau/file/
├── FileApplication.java          # 启动类
├── controller/
│   └── FileUploadController.java # 图片上传
└── service/
    └── FileMessageService.java   # 文件上传消息服务（RabbitMQ）
```

### 3.8 前端 (`frontend/CLoudEventFron/src/`)
```
├── api/                          # API 请求模块
│   ├── user.js
│   ├── article.js
│   └── category.js
── assets/                       # 静态资源
├── router/                       # 路由配置
│   ── index.js
├── stores/                       # Pinia 状态管理
│   └── user.js
├── utils/                        # 工具函数
│   ── request.js                # Axios 封装
├── views/                        # 页面组件
│   ├── Login.vue                 # 登录页
│   ├── Layout.vue                # 布局容器
│   ├── Dashboard.vue             # 仪表盘
│   ├── ArticleList.vue           # 文章列表
│   ├── ArticleEdit.vue           # 文章编辑
│   ├── Category.vue              # 分类管理
│   ├── UserProfile.vue           # 用户信息
│   └── Chart.vue                 # 图表
├── App.vue                       # 根组件
└── main.js                       # 应用入口
```

---

## 4. 关键配置文件

| 文件 | 用途 |
|------|------|
| `pom.xml` | 父 POM，Maven 依赖和构建配置，包含 Spring Cloud + Alibaba 依赖管理 |
| `cloud-event-common/pom.xml` | 公共模块依赖（Web、Validation、Redis、RabbitMQ、JWT） |
| `cloud-event-gateway/src/main/resources/application.yml` | 网关配置（端口 8090、路由规则、CORS） |
| `cloud-event-user/src/main/resources/application.yml` | 用户服务配置（端口 8081、数据源 cloud_event_user） |
| `cloud-event-category/src/main/resources/application.yml` | 分类服务配置（端口 8082、数据源 cloud_event_category） |
| `cloud-event-article/src/main/resources/application.yml` | 文章服务配置（端口 8083、数据源 cloud_event_article） |
| `cloud-event-file/src/main/resources/application.yml` | 文件服务配置（端口 8084） |
| `cloud-event-common/src/main/java/cn/edu/scau/config/RabbitMqConfig.java` | RabbitMQ 交换机、队列、绑定、消息转换器配置 |
| `frontend/CLoudEventFron/package.json` | 前端依赖配置 |
| `frontend/CLoudEventFron/vite.config.js` | Vite 构建配置，含开发代理 |
| `frontend/CLoudEventFron/src/utils/request.js` | Axios 请求封装 |
| `D:\Development\Java\Development\nginx-1.20.2\conf\nginx.conf` | Nginx 反向代理配置（端口 80） |

---

## 5. 安全基线

### 5.1 身份认证
- 使用 JWT Token 实现无状态认证
- Token 存储在 Redis 中，设置 1 小时过期时间
- 网关层 `AuthFilter` 进行全局 JWT 鉴权，放行登录/注册/静态资源请求
- 各服务 `LoginInterceptor` 拦截器二次验证受保护路由的 Token
- 修改密码时删除 Redis 中的旧 Token

### 5.2 输入验证
- 使用 Spring Validation 进行请求参数验证
- 自定义 `@State` 注解 + `StateValidation` 验证器处理文章状态业务规则

### 5.3 数据保护
- 密码存储前使用 MD5 哈希处理
- API 响应中排除敏感字段（密码等，使用 `@JsonIgnore`）

### 5.4 CORS 配置
- 在 Gateway 的 `application.yml` 中配置全局 CORS，允许所有来源和方法

---

## 6. 消息队列

### 6.1 RabbitMQ 配置
- **地址**: `localhost:5672`，用户名 `guest`，密码 `guest`
- **消息转换器**: `SimpleMessageConverter`，配置反序列化白名单（`cn.edu.scau.**`、`java.lang.**`、`java.time.**`、`java.util.**`）

### 6.2 交换机与队列

| 交换机 | 队列 | 路由键 | 用途 |
|--------|------|--------|------|
| `article-exchange` | `article-queue` | `article.publish` | 文章发布通知 |
| `article-exchange` | `article-log-queue` | `article.log` | 文章操作日志 |
| `file-exchange` | `file-upload-queue` | `file.upload` | 文件上传通知 |
| `user-exchange` | `user-log-queue` | `user.log` | 用户操作日志 |

### 6.3 消费者
- `ArticleLogConsumer`: 监听 `article-log-queue`，打印文章操作日志

---

## 7. 测试指南

### 7.1 后端测试
- 使用 Spring Boot Test 框架
- 运行测试: `mvn test`

### 7.2 前端测试
- 尚未配置测试框架
- 手动测试

---

## 8. 部署

### 8.1 所需服务
- MySQL (localhost:3306)
- Redis (localhost:6379)
- RabbitMQ (localhost:5672)
- Nacos (localhost:8848)
- Nginx (localhost:80)

### 8.2 后端启动
```bash
# 编译整个项目
mvn clean compile

# 分别启动各服务（建议按以下顺序）
cd cloud-event-gateway && mvn spring-boot:run    # 端口 8090
cd cloud-event-user && mvn spring-boot:run        # 端口 8081
cd cloud-event-category && mvn spring-boot:run    # 端口 8082
cd cloud-event-article && mvn spring-boot:run     # 端口 8083
cd cloud-event-file && mvn spring-boot:run        # 端口 8084
```

### 8.3 前端开发模式
```bash
cd frontend/CLoudEventFron
npm install
npm run dev
```

### 8.4 前端生产构建
```bash
cd frontend/CLoudEventFron
npm run build
```
构建产物输出到 `frontend/CLoudEventFron/dist/`

### 8.5 Nginx 配置
- **配置文件**: `D:\Development\Java\Development\nginx-1.20.2\conf\nginx.conf`
- **静态文件目录**: `D:/study/JavaStudy/SpringBootStudy/CloudEvent/frontend/CLoudEventFron/dist`
- **图片存储目录**: `D:/study/JavaStudy/SpringBootStudy/CloudEvent/Repo`
- **API 代理**: `/api/user/`, `/api/category/`, `/api/article/`, `/api/upload/` → `http://localhost:8090`（网关）
- **图片资源**: `/repo/` → 静态文件服务

### 8.6 Nginx 命令
```bash
cd D:\Development\Java\Development\nginx-1.20.2

# 启动
.\nginx.exe

# 停止
.\nginx.exe -s stop

# 重新加载配置
.\nginx.exe -s reload
```

### 8.7 访问地址
- **前端页面**: http://localhost
- **网关入口**: http://localhost:8090/api/...
- **各服务直连**: http://localhost:8081/..., http://localhost:8082/..., http://localhost:8083/..., http://localhost:8084/...

---

## 9. 版本控制

- **远程仓库**: https://github.com/WaltTYT/CloudEvent
- **分支**: main
- **推送策略**: 需要明确获得用户许可才能推送

### 9.1 提交格式规范

| 类型 | 格式 | 说明 | 示例 |
|------|------|------|------|
| 初始化项目 | `init(模块 : 初始化描述)` | 初始化项目结构、配置文件 | `init(all : 初始化默认模板文件)` |
| 增加功能 | `add(模块 : 功能描述)` | 添加新功能、新依赖、新文件 | `add(后端 : 添加nacos)` |
| 修复bug | `fix(模块 : 修复描述)` | 修复问题、修正错误 | `fix(前端 : 修复图片尺寸限制)` |
| 删除内容 | `delete(模块 : 删除描述)` | 删除文件、移除无用代码 | `delete(前端 : 删除默认模板文件)` |
| 重构 | `refactor(模块 : 重构描述)` | 代码重构，不改变功能 | `refactor(后端 : 优化异常处理)` |
| 文档 | `docs(模块 : 文档描述)` | 更新文档 | `docs(Agent.md : 更新部署说明)` |

### 9.2 提交格式规则

1. **模块标识**: 使用 `后端`、`前端`、`all` 或具体模块名（如 `gateway`、`user`、`article`）
2. **分隔符**: 使用 `:` 分隔模块和描述，两侧各有一个空格
3. **多提交**: 使用 `&&` 连接多个提交类型，前后各有一个空格
4. **示例**:
   - 单个提交: `add(后端 : 添加redis)`
   - 多个提交: `add(后端 : 添加nacos) && fix(前端 : 图片尺寸大小修改)`

---

## 10. 已知约束

1. **SSL 验证**: 由于证书问题，GitHub 操作已禁用 SSL 验证（`git -c http.sslVerify=false`）
2. **数据库**: 使用 MySQL 数据库，按服务拆分库：`cloud_event_user`、`cloud_event_category`、`cloud_event_article`
3. **端口**: 网关 8090，用户服务 8081，分类服务 8082，文章服务 8083，文件服务 8084，Nginx 80
4. **文件路径**: 图片存储路径为 `D:\study\JavaStudy\SpringBootStudy\CloudEvent\Repo`
5. **Nginx**: 作为反向代理和静态资源服务器，配置文件位于 `D:\Development\Java\Development\nginx-1.20.2\conf\nginx.conf`
6. **Nacos**: 服务发现与配置中心，地址 `localhost:8848`，namespace `public`
7. **RabbitMQ**: 消息队列，地址 `localhost:5672`，消息转换器使用 `SimpleMessageConverter` + 反序列化白名单
8. **Jackson 版本**: Spring Boot 4.0.3 使用 Jackson 3.x（包名 `tools.jackson`），`jackson-datatype-jsr310` 3.0.4 在 Maven 中央仓库不可用，`LocalDateTime` 序列化需自定义处理

---

## 11. Agent 自检清单

完成任务前，请验证：
- [ ] 代码遵循项目约定
- [ ] 未暴露任何机密或凭证
- [ ] 所有输入均已验证
- [ ] 测试通过（如适用）
- [ ] 变更最小化且聚焦
- [ ] 已获得用户提交/推送权限
