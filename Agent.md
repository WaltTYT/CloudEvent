# CloudEvent Agent 配置文件

## 1. 项目概述

### 1.1 项目名称
CloudEvent - 云事件管理系统（文章管理系统）

### 1.2 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 4.0.3 |
| 微服务 | Spring Cloud | 2025.1.0 |
| 服务发现/配置 | Spring Cloud Alibaba (Nacos) | 2025.1.0.0 |
| 对象关系映射 | MyBatis | 4.0.0 |
| 分页插件 | PageHelper | 1.4.6 |
| 数据库 | MySQL | 8.0+ |
| 缓存 | Redis | 7.0+ |
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
- **模式**: 单体 Spring Boot 应用（已引入 Spring Cloud 依赖，具备微服务化基础）
- **服务发现**: 使用 Nacos 进行服务注册与发现
- **配置管理**: 使用 Nacos 配置中心
- **通信**: 前端与后端通过 RESTful API 通信，Nginx 作为反向代理和静态资源服务器
- **数据库**: MySQL（库名 `big_event`），Redis 用于缓存 JWT Token

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
- 登录拦截器（`LoginInterceptor`）验证受保护路由的 Token

---

## 3. 项目结构

### 3.1 后端 (`src/main/java/cn/edu/scau/`)
```
├── CloudEventApplication.java    # 启动类
├── controller/                   # REST API 控制器
│   ├── UserController.java       # 用户注册、登录、信息管理
│   ├── ArticleController.java    # 文章 CRUD、分页查询
│   ├── CategoryController.java   # 分类管理
│   └── FileUploadController.java # 图片上传
├── service/                      # 业务逻辑层
│   ├── UserService.java
│   ├── ArticleService.java
│   ├── CategoryService.java
│   └── impl/                     # 服务实现类
├── mapper/                       # MyBatis 映射器接口
│   ├── UserMapper.java
│   ├── ArticleMapper.java
│   └── CategoryMapper.java
├── pojo/                         # 实体类
│   ├── User.java
│   ├── Article.java
│   ├── Category.java
│   ├── PageBean.java
│   └── Result.java               # 统一响应封装
├── config/                       # 配置类
│   └── WebConfig.java            # Web 配置（拦截器、CORS）
├── interceptors/                 # 请求拦截器
│   └── LoginInterceptor.java     # JWT Token 校验
├── exception/                    # 全局异常处理
│   └── GlobalExceptionHandler.java
├── utils/                        # 工具类
│   ├── JwtUtil.java              # JWT 生成与解析
│   ├── Md5Util.java              # MD5 加密
│   └── ThreadLocalUtil.java      # 线程本地存储
├── anno/                         # 自定义注解
│   └── State.java
└── Validation/                   # 自定义验证器
    └── StateValidation.java
```

### 3.2 前端 (`frontend/CLoudEventFron/src/`)
```
├── api/                          # API 请求模块
│   ├── user.js
│   ├── article.js
│   └── category.js
├── assets/                       # 静态资源
├── router/                       # 路由配置
│   └── index.js
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

### 3.3 其他目录
```
├── Repo/                         # 上传文件存储目录
├── src/main/resources/
│   ├── application.yml           # Spring Boot 配置
│   └── cn/edu/scau/mapper/       # MyBatis XML 映射文件
│       └── ArticleMapper.xml
└── src/test/java/cn/edu/scau/    # 测试代码
    ├── JwtTest.java
    └── TheadLocalTest.java
```

---

## 4. 关键配置文件

| 文件 | 用途 |
|------|------|
| `pom.xml` | Maven 依赖和构建配置，包含 Spring Cloud + Alibaba 依赖管理 |
| `src/main/resources/application.yml` | Spring Boot 配置（数据源、Redis、Nacos、端口 8081） |
| `frontend/CLoudEventFron/package.json` | 前端依赖配置 |
| `frontend/CLoudEventFron/vite.config.js` | Vite 构建配置，含开发代理（proxy 到 8081） |
| `frontend/CLoudEventFron/src/utils/request.js` | Axios 请求封装，baseURL 为空（依赖 Vite 代理或 Nginx） |
| `D:\Development\Java\Development\nginx-1.20.2\conf\nginx.conf` | Nginx 反向代理配置（端口 80） |

---

## 5. 安全基线

### 5.1 身份认证
- 使用 JWT Token 实现无状态认证
- Token 存储在 Redis 中，设置 1 小时过期时间
- `LoginInterceptor` 拦截器验证受保护路由的 Token
- 修改密码时删除 Redis 中的旧 Token

### 5.2 输入验证
- 使用 Spring Validation 进行请求参数验证
- 自定义 `@State` 注解 + `StateValidation` 验证器处理业务规则

### 5.3 数据保护
- 密码存储前使用 MD5 哈希处理
- API 响应中排除敏感字段（密码等）

### 5.4 CORS 配置
- 在 `WebConfig` 中配置跨域请求，允许前端开发服务器访问

---

## 6. 测试指南

### 6.1 后端测试
- 使用 Spring Boot Test 框架
- 测试目录: `src/test/java/cn/edu/scau/`
- 运行测试: `mvn test`

### 6.2 前端测试
- 尚未配置测试框架
- 手动测试

---

## 7. 部署

### 7.1 所需服务
- MySQL (localhost:3306)
- Redis (localhost:6379)
- Nacos (localhost:8848)
- Nginx (localhost:80)

### 7.2 后端启动
```bash
mvn spring-boot:run
```

### 7.3 前端开发模式
```bash
cd frontend/CLoudEventFron
npm install
npm run dev
```

### 7.4 前端生产构建
```bash
cd frontend/CLoudEventFron
npm run build
```
构建产物输出到 `frontend/CLoudEventFron/dist/`

### 7.5 Nginx 配置
- **配置文件**: `D:\Development\Java\Development\nginx-1.20.2\conf\nginx.conf`
- **静态文件目录**: `D:/study/JavaStudy/SpringBootStudy/CloudEvent/frontend/CLoudEventFron/dist`
- **图片存储目录**: `D:/study/JavaStudy/SpringBootStudy/CloudEvent/Repo`
- **API 代理**: `/user/`, `/category/`, `/article/`, `/upload/` → `http://localhost:8081`
- **图片资源**: `/repo/` → 静态文件服务

### 7.6 Nginx 命令
```bash
cd D:\Development\Java\Development\nginx-1.20.2

# 启动
.\nginx.exe

# 停止
.\nginx.exe -s stop

# 重新加载配置
.\nginx.exe -s reload
```

### 7.7 访问地址
- **前端页面**: http://localhost
- **后端 API**: http://localhost/user/***, http://localhost/article/*** 等

---

## 8. 版本控制

- **远程仓库**: https://github.com/WaltTYT/CloudEvent
- **分支**: main
- **推送策略**: 需要明确获得用户许可才能推送

### 8.1 提交格式规范

| 类型 | 格式 | 说明 | 示例 |
|------|------|------|------|
| 初始化项目 | `init(模块 : 初始化描述)` | 初始化项目结构、配置文件 | `init(all : 初始化默认模板文件)` |
| 增加功能 | `add(模块 : 功能描述)` | 添加新功能、新依赖、新文件 | `add(后端 : 添加nacos)` |
| 修复bug | `fix(模块 : 修复描述)` | 修复问题、修正错误 | `fix(前端 : 修复图片尺寸限制)` |
| 删除内容 | `delete(模块 : 删除描述)` | 删除文件、移除无用代码 | `delete(前端 : 删除默认模板文件)` |
| 重构 | `refactor(模块 : 重构描述)` | 代码重构，不改变功能 | `refactor(后端 : 优化异常处理)` |
| 文档 | `docs(模块 : 文档描述)` | 更新文档 | `docs(Agent.md : 更新部署说明)` |

### 8.2 提交格式规则

1. **模块标识**: 使用 `后端`、`前端`、`all` 或具体模块名
2. **分隔符**: 使用 `:` 分隔模块和描述，两侧各有一个空格
3. **多提交**: 使用 `&&` 连接多个提交类型，前后各有一个空格
4. **示例**:
   - 单个提交: `add(后端 : 添加redis)`
   - 多个提交: `add(后端 : 添加nacos) && fix(前端 : 图片尺寸大小修改)`

---

## 9. 已知约束

1. **SSL 验证**: 由于证书问题，GitHub 操作已禁用 SSL 验证
2. **数据库**: 使用 MySQL 数据库，库名 `big_event`
3. **端口**: 后端运行在端口 8081，Nginx 运行在端口 80
4. **文件路径**: 图片存储路径为 `D:\study\JavaStudy\SpringBootStudy\CloudEvent\Repo`
5. **Nginx**: 作为反向代理和静态资源服务器，配置文件位于 `D:\Development\Java\Development\nginx-1.20.2\conf\nginx.conf`
6. **Nacos**: 服务发现与配置中心，地址 `localhost:8848`，namespace `public`
7. **微服务改造**: 已有 `plan.md` 记录了微服务架构改造方案

---

## 10. Agent 自检清单

完成任务前，请验证：
- [ ] 代码遵循项目约定
- [ ] 未暴露任何机密或凭证
- [ ] 所有输入均已验证
- [ ] 测试通过（如适用）
- [ ] 变更最小化且聚焦
- [ ] 已获得用户提交/推送权限
