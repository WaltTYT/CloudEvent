# CloudEvent 微服务架构改造方案

## 一、现状分析

### 1.1 当前架构
```
┌─────────────────────────────────────────────────────────────┐
│                    CloudEvent (8081)                        │
│  ┌─────────┐  ┌──────────┐  ┌──────────┐  ┌──────────────┐ │
│  │  User   │  │ Article  │  │ Category │  │ FileUpload   │ │
│  └────┬────┘  └────┬─────┘  └────┬─────  └──────┬─────── │
│       │            │             │               │         │
│       └────────────┴──────┬──────┴───────────────┘         │
│                           ▼                                │
│                    MySQL (big_event)                       │
│                    Redis                                   │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 已有基础
- ✅ Spring Boot 4.0.3
- ✅ Spring Cloud 2025.1.0
- ✅ Spring Cloud Alibaba 2025.1.0.0
- ✅ Nacos 服务发现与配置中心
- ✅ Redis 缓存
- ✅ JWT 认证
- ✅ Nginx 反向代理

### 1.3 业务模块划分
| 模块 | Controller | 功能 |
|------|-----------|------|
| 用户服务 | UserController | 注册、登录、个人信息管理 |
| 文章服务 | ArticleController | 文章 CRUD、分页查询 |
| 分类服务 | CategoryController | 分类管理 |
| 文件服务 | FileUploadController | 图片上传 |

---

## 二、目标架构

### 2.1 微服务架构图
```
┌─────────────────────────────────────────────────────────────────────┐
│                         Nacos (8848)                               │
│                 服务注册中心 + 配置中心                              │
└─────────────────────────────────────────────────────────────────────┘
                                    ▲
                                    │
┌─────────────────────────────────────────────────────────────────────┐
│                         Gateway (8080)                             │
│                    统一入口 + 路由转发 + Token校验                   │
└─────────────────────────────────────────────────────────────────────┘
          │              │              │              │
          ▼              ▼              ▼              ▼
┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│  UserService │ │ArticleService│ │CategoryService│ │ FileService  │
│    (8081)    │ │    (8082)    │ │    (8083)    │ │    (8084)    │
───────┬──────┘ └───────┬──────┘ └───────┬──────┘ └───────┬──────┘
        │                │                │                │
        ▼                │                ▼                ▼
┌──────────────┐         │         ┌──────────────┐  ┌──────────────┐
│ MySQL(user)  │         │         │ MySQL(category)│  │ 文件存储    │
──────────────┘         ▼         └──────────────┘  └──────────────
                    ┌──────────────┐
                    │ MySQL(article)│
                    └──────────────┘
                         │
                         ▼
                    ┌──────────────┐
                    │    Redis     │
                    └──────────────
```

### 2.2 项目结构
```
CloudEvent/                          # 父工程
├── cloud-event-common/              # 公共模块
│   ├── src/main/java/cn/edu/scau/common/
│   │   ├── pojo/                    # 公共实体类
│   │   ├── result/                  # 统一响应
│   │   ├── utils/                   # 工具类
│   │   ├── exception/               # 异常处理
│   │   └── interceptor/             # 拦截器
│   └── pom.xml
│
├── cloud-event-gateway/             # 网关服务
│   ├── src/main/java/cn/edu/scau/gateway/
│   │   ├── config/                  # 网关配置
│   │   └── filter/                  # 自定义过滤器
│   └── pom.xml
│
├── cloud-event-user/                # 用户微服务
│   ├── src/main/java/cn/edu/scau/user/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── mapper/
│   │   └── config/
│   └── pom.xml
│
├── cloud-event-article/             # 文章微服务
│   ├── src/main/java/cn/edu/scau/article/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── mapper/
│   │   ├── feign/                   # 远程调用
│   │   └── config/
│   └── pom.xml
│
├── cloud-event-category/            # 分类微服务
│   ├── src/main/java/cn/edu/scau/category/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── mapper/
│   │   └── config/
│   └── pom.xml
│
├── cloud-event-file/                # 文件微服务
│   ├── src/main/java/cn/edu/scau/file/
│   │   ├── controller/
│   │   ├── service/
│   │   ── config/
│   └── pom.xml
│
├── frontend/                        # 前端项目
│   └── CLoudEventFron/
│
└── pom.xml                          # 父 POM
```

---

## 三、改造步骤

### 阶段一：基础架构搭建（第 1-2 天）

#### 步骤 1.1：创建父工程 POM
```xml
<!-- 父 pom.xml -->
<groupId>cn.edu.scau</groupId>
<artifactId>cloud-event-parent</artifactId>
<version>1.0-SNAPSHOT</version>
<packaging>pom</packaging>

<modules>
    <module>cloud-event-common</module>
    <module>cloud-event-gateway</module>
    <module>cloud-event-user</module>
    <module>cloud-event-article</module>
    <module>cloud-event-category</module>
    <module>cloud-event-file</module>
</modules>
```

#### 步骤 1.2：创建公共模块 cloud-event-common
**迁移内容：**
- `pojo/` - User.java, Article.java, Category.java, PageBean.java, Result.java
- `utils/` - JwtUtil.java, Md5Util.java, ThreadLocalUtil.java
- `exception/` - GlobalExceptionHandler.java
- `interceptor/` - LoginInterceptor.java
- `anno/` - @State 注解
- `Validation/` - StateValidation.java

**依赖：**
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
        <groupId>com.auth0</groupId>
        <artifactId>java-jwt</artifactId>
        <version>4.4.0</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
</dependencies>
```

### 阶段二：网关服务搭建（第 3 天）

#### 步骤 2.1：创建 cloud-event-gateway
**依赖：**
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-gateway</artifactId>
    </dependency>
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
    </dependency>
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
    </dependency>
    <dependency>
        <groupId>cn.edu.scau</groupId>
        <artifactId>cloud-event-common</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

#### 步骤 2.2：配置网关路由
```yaml
# application.yml
server:
  port: 8080

spring:
  application:
    name: cloud-event-gateway
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://cloud-event-user
          predicates:
            - Path=/api/user/**
        - id: article-service
          uri: lb://cloud-event-article
          predicates:
            - Path=/api/article/**
        - id: category-service
          uri: lb://cloud-event-category
          predicates:
            - Path=/api/category/**
        - id: file-service
          uri: lb://cloud-event-file
          predicates:
            - Path=/api/upload/**
      globalcors:
        cors-configurations:
          '[/**]':
            allowedOriginPatterns: "*"
            allowedMethods: "*"
            allowedHeaders: "*"
            allowCredentials: true
```

#### 步骤 2.3：实现 Token 校验过滤器
```java
@Component
public class AuthFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        // 验证 token...
        return chain.filter(exchange);
    }
    
    @Override
    public int getOrder() {
        return -100;
    }
}
```

### 阶段三：用户微服务（第 4-5 天）

#### 步骤 3.1：创建 cloud-event-user
**迁移内容：**
- `UserController.java`
- `UserService.java` / `UserServiceImpl.java`
- `UserMapper.java` / `UserMapper.xml`

**配置：**
```yaml
server:
  port: 8081

spring:
  application:
    name: cloud-event-user
  datasource:
    url: jdbc:mysql://localhost:3306/cloud_event_user
    username: root
    password: 123456
  data:
    redis:
      host: localhost
      port: 6379
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
```

#### 步骤 3.2：创建独立数据库
```sql
CREATE DATABASE cloud_event_user DEFAULT CHARACTER SET utf8mb4;
USE cloud_event_user;

-- 迁移 users 表
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(20) NOT NULL,
    password VARCHAR(32) NOT NULL,
    nickname VARCHAR(10),
    email VARCHAR(128),
    user_pic VARCHAR(128),
    create_time DATETIME,
    update_time DATETIME
);
```

### 阶段四：分类微服务（第 6-7 天）

#### 步骤 4.1：创建 cloud-event-category
**迁移内容：**
- `CategoryController.java`
- `CategoryService.java` / `CategoryServiceImpl.java`
- `CategoryMapper.java` / `CategoryMapper.xml`

**配置：**
```yaml
server:
  port: 8082

spring:
  application:
    name: cloud-event-category
  datasource:
    url: jdbc:mysql://localhost:3306/cloud_event_category
```

#### 步骤 4.2：创建独立数据库
```sql
CREATE DATABASE cloud_event_category DEFAULT CHARACTER SET utf8mb4;
USE cloud_event_category;

CREATE TABLE category (
    id INT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(100) NOT NULL,
    category_alias VARCHAR(100) NOT NULL,
    create_user INT NOT NULL,
    create_time DATETIME,
    update_time DATETIME
);
```

### 阶段五：文章微服务（第 8-9 天）

#### 步骤 5.1：创建 cloud-event-article
**迁移内容：**
- `ArticleController.java`
- `ArticleService.java` / `ArticleServiceImpl.java`
- `ArticleMapper.java` / `ArticleMapper.xml`

**新增：Feign 远程调用**
```java
@FeignClient("cloud-event-category")
public interface CategoryFeignClient {
    @GetMapping("/api/category/{id}")
    Result<Category> getById(@PathVariable Integer id);
}
```

**依赖：**
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-loadbalancer</artifactId>
</dependency>
```

#### 步骤 5.2：创建独立数据库
```sql
CREATE DATABASE cloud_event_article DEFAULT CHARACTER SET utf8mb4;
USE cloud_event_article;

CREATE TABLE article (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    content VARCHAR(10000) NOT NULL,
    cover_img VARCHAR(128),
    state VARCHAR(5) NOT NULL,
    category_id INT NOT NULL,
    create_user INT NOT NULL,
    create_time DATETIME,
    update_time DATETIME
);
```

### 阶段六：文件微服务（第 10 天）

#### 步骤 6.1：创建 cloud-event-file
**迁移内容：**
- `FileUploadController.java`

**配置：**
```yaml
server:
  port: 8083

file:
  upload-path: D:/study/JavaStudy/SpringBootStudy/CloudEvent/Repo
  access-url: http://localhost/api/upload/
```

### 阶段七：前端改造（第 11-12 天）

#### 步骤 7.1：修改 API 请求路径
```javascript
// 修改前
const baseURL = ''  // 开发环境使用 Vite 代理

// 修改后
const baseURL = 'http://localhost:8080/api'  // 统一通过网关访问
```

#### 步骤 7.2：更新 Nginx 配置
```nginx
server {
    listen 80;
    server_name localhost;

    # 前端静态文件
    location / {
        root D:/study/JavaStudy/SpringBootStudy/CloudEvent/frontend/CLoudEventFron/dist;
        index index.html index.htm;
        try_files $uri $uri/ /index.html;
    }

    # API 请求转发到网关
    location /api/ {
        proxy_pass http://localhost:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    # 图片资源
    location /repo/ {
        alias D:/study/JavaStudy/SpringBootStudy/CloudEvent/Repo/;
    }
}
```

---

## 四、关键技术点

### 4.1 服务间通信
| 方式 | 适用场景 | 技术 |
|------|---------|------|
| 同步调用 | 需要立即返回结果 | OpenFeign |
| 异步消息 | 解耦、削峰 | RabbitMQ/Kafka |
| 事件驱动 | 状态变更通知 | Spring Cloud Stream |

### 4.2 分布式事务
```xml
<!-- 可选：引入 Seata -->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-seata</artifactId>
</dependency>
```

### 4.3 服务熔断降级
```xml
<!-- 可选：引入 Sentinel -->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
</dependency>
```

### 4.4 链路追踪
```xml
<!-- 可选：引入 Sleuth + Zipkin -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-sleuth</artifactId>
</dependency>
```

---

## 五、数据库拆分策略

### 5.1 拆分原则
- **按业务边界拆分**：每个微服务拥有独立数据库
- **避免跨库 JOIN**：通过服务间调用获取关联数据
- **数据冗余**：适当冗余减少跨服务调用

### 5.2 数据库清单
| 数据库 | 表 | 所属服务 |
|--------|-----|---------|
| cloud_event_user | users | 用户服务 |
| cloud_event_article | articles | 文章服务 |
| cloud_event_category | categories | 分类服务 |

### 5.3 数据迁移脚本
```sql
-- 1. 创建新数据库
CREATE DATABASE cloud_event_user DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE cloud_event_article DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE cloud_event_category DEFAULT CHARACTER SET utf8mb4;

-- 2. 迁移数据
INSERT INTO cloud_event_user.users SELECT * FROM big_event.users;
INSERT INTO cloud_event_article.article SELECT * FROM big_event.article;
INSERT INTO cloud_event_category.category SELECT * FROM big_event.category;

-- 3. 验证数据一致性
SELECT COUNT(*) FROM big_event.users;
SELECT COUNT(*) FROM cloud_event_user.users;
```

---

## 六、配置中心管理

### 6.1 Nacos 配置清单
| Data ID | Group | 说明 |
|---------|-------|------|
| cloud-event-gateway.yml | DEFAULT_GROUP | 网关配置 |
| cloud-event-user.yml | DEFAULT_GROUP | 用户服务配置 |
| cloud-event-article.yml | DEFAULT_GROUP | 文章服务配置 |
| cloud-event-category.yml | DEFAULT_GROUP | 分类服务配置 |
| cloud-event-file.yml | DEFAULT_GROUP | 文件服务配置 |

### 6.2 共享配置
```yaml
# 在 Nacos 中创建 shared-config.yml
spring:
  data:
    redis:
      host: localhost
      port: 6379
```

---

## 七、测试计划

### 7.1 单元测试
- 每个服务的 Controller、Service 层单元测试
- 使用 MockMvc 测试 API 接口

### 7.2 集成测试
- 服务间调用测试（Feign）
- 网关路由测试
- 数据库事务测试

### 7.3 性能测试
- 单服务并发测试
- 全链路压力测试
- 数据库连接池监控

---

## 八、部署方案

### 8.1 本地开发环境
```bash
# 1. 启动基础设施
# - MySQL (3306)
# - Redis (6379)
# - Nacos (8848)

# 2. 启动微服务（按顺序）
mvn clean install
java -jar cloud-event-gateway/target/*.jar
java -jar cloud-event-user/target/*.jar
java -jar cloud-event-article/target/*.jar
java -jar cloud-event-category/target/*.jar
java -jar cloud-event-file/target/*.jar

# 3. 启动前端
cd frontend/CLoudEventFron
npm run dev

# 4. 启动 Nginx
cd D:\Development\Java\Development\nginx-1.20.2
.\nginx.exe
```

### 8.2 Docker 部署（可选）
```yaml
# docker-compose.yml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    ports:
      - "3306:3306"
  redis:
    image: redis:7.0
    ports:
      - "6379:6379"
  nacos:
    image: nacos/nacos-server:v2.3.0
    ports:
      - "8848:8848"
  gateway:
    build: ./cloud-event-gateway
    ports:
      - "8080:8080"
    depends_on:
      - nacos
  user:
    build: ./cloud-event-user
    ports:
      - "8081:8081"
    depends_on:
      - nacos
  article:
    build: ./cloud-event-article
    ports:
      - "8082:8082"
    depends_on:
      - nacos
  category:
    build: ./cloud-event-category
    ports:
      - "8083:8083"
    depends_on:
      - nacos
  file:
    build: ./cloud-event-file
    ports:
      - "8084:8084"
    depends_on:
      - nacos
```

---

## 九、风险与应对

### 9.1 技术风险
| 风险 | 影响 | 应对措施 |
|------|------|---------|
| 分布式事务 | 数据不一致 | 引入 Seata 或最终一致性方案 |
| 服务雪崩 | 系统不可用 | 引入 Sentinel 熔断降级 |
| 网络延迟 | 响应变慢 | 合理设置超时时间、异步调用 |
| 数据库拆分 | 跨库查询复杂 | 数据冗余、CQRS 模式 |

### 9.2 迁移风险
| 风险 | 应对措施 |
|------|---------|
| 数据丢失 | 迁移前备份、迁移后校验 |
| 服务中断 | 灰度发布、回滚方案 |
| 兼容性问题 | API 版本管理、向后兼容 |

---

## 十、验收标准

### 10.1 功能验收
- [ ] 用户注册、登录功能正常
- [ ] 文章 CRUD 功能正常
- [ ] 分类管理功能正常
- [ ] 图片上传功能正常
- [ ] 服务间调用（文章查询分类）正常

### 10.2 性能验收
- [ ] 单服务响应时间 < 200ms
- [ ] 网关转发延迟 < 50ms
- [ ] 支持 100 并发用户

### 10.3 可用性验收
- [ ] 单服务故障不影响其他服务
- [ ] 服务自动注册与发现
- [ ] 配置动态刷新

---

## 十一、时间规划

| 阶段 | 任务 | 预计时间 |
|------|------|---------|
| 阶段一 | 基础架构搭建 | 2 天 |
| 阶段二 | 网关服务搭建 | 1 天 |
| 阶段三 | 用户微服务 | 2 天 |
| 阶段四 | 分类微服务 | 2 天 |
| 阶段五 | 文章微服务 | 2 天 |
| 阶段六 | 文件微服务 | 1 天 |
| 阶段七 | 前端改造 | 2 天 |
| 测试优化 | 集成测试与优化 | 2 天 |
| **总计** | | **14 天** |

---

## 十二、回滚方案

如果改造过程中出现问题，可以快速回滚：

1. **保留原项目备份**：改造前完整备份 CloudEvent 项目
2. **分支管理**：在 Git 中创建 `microservice` 分支进行改造
3. **回滚命令**：
   ```bash
   git checkout main
   git branch -D microservice
   ```

---

## 附录

### A. 常用命令
```bash
# 查看 Nacos 服务列表
curl http://localhost:8848/nacos/v1/ns/instance/list?serviceName=cloud-event-user

# 查看服务健康状态
curl http://localhost:8080/actuator/health

# 强制刷新配置
curl -X POST http://localhost:8081/actuator/refresh
```

### B. 参考资料
- Spring Cloud Gateway 官方文档
- Spring Cloud Alibaba 官方文档
- Nacos 官方文档
- OpenFeign 使用指南
