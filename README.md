# 🛒 悠然商城 (ease-mall)

基于 Spring Boot 3 + Vue 3 的全栈电商平台，涵盖用户认证、商品管理、订单交易、支付模拟、后台管理等核心业务流程。

> 温暖自然的设计风格 · ~100 文件 · ~5000 行代码

## 技术架构

| 层级 | 技术栈 |
|------|--------|
| 后端框架 | Java 21 + Spring Boot 3.2 |
| 持久层 | MyBatis-Plus 3.5 + Druid 连接池 |
| 数据库 | MySQL 8.0（Docker） / H2（开发模式） |
| 缓存 | Redis 7，支持优雅降级 |
| 认证 | JWT + Redis 黑名单双重验证 |
| 文档 | Knife4j (OpenAPI 3.0) |
| 前端 | Vue 3 + Vite + Element Plus + Pinia + Axios |
| 构建 | Maven Wrapper（自包含，无需预装 Maven） |

## 项目结构

```
ease-mall/
├── pom.xml
├── docker-compose.yml
├── docs/sql/init.sql
├── src/main/java/com/mall/
│   ├── MallApplication.java
│   ├── common/          # 公共基础设施
│   │   ├── result/      # 统一响应体 R<T>、状态码
│   │   ├── exception/   # 全局异常处理、业务异常
│   │   ├── utils/       # JWT 工具、雪花 ID 生成器
│   │   ├── interceptor/ # Token 拦截器（统一鉴权）
│   │   └── config/      # WebMvc、JWT、密码加密
│   ├── auth/            # 认证 —— 注册、登录、退出
│   ├── user/            # 用户 —— 个人信息、收货地址
│   ├── product/         # 商品 —— 分类树、搜索、库存
│   ├── order/           # 订单 —— 购物车、下单、状态机
│   ├── payment/         # 支付 —— 模拟支付、支付记录
│   └── admin/           # 管理 —— 数据看板、用户管理
└── web/                 # Vue 3 前端
    └── src/views/
        ├── user/        # 登录 / 注册 / 个人中心
        ├── product/     # 首页 / 商品详情
        ├── order/       # 购物车 / 订单列表
        └── admin/       # 管理登录 / 数据看板
```

## 快速开始

### 基础设施（Docker）

```bash
docker compose up -d    # MySQL:3307 + Redis:6379
```

### 后端

```bash
mvnw clean package -DskipTests
java -jar target/ease-mall-1.0.0-SNAPSHOT.jar

# API 文档: http://localhost:8080/doc.html
```

### 前端

```bash
cd web && npm install && npm run dev

# 首页: http://localhost:5173
# 管理后台: http://localhost:5173/admin/login  (admin / admin123)
```

## API 概览

| 模块 | Method | Path | Auth |
|------|--------|------|------|
| Auth | POST | `/auth/register` `/auth/login` `/auth/logout` | - / - / JWT |
| User | GET/PUT | `/user/profile` | JWT |
| Address | GET/POST/PUT/DEL | `/user/address` `/{id}` | JWT |
| Category | GET | `/product/category` | - |
| Product | GET/POST/PUT | `/product` `/{id}` | GET 公开 |
| Cart | GET/POST/PUT/DEL | `/cart` `/{productId}` | JWT |
| Order | GET/POST | `/order` `/{id}` | JWT |
| Order | PUT | `/order/{id}/cancel` `/pay` `/ship` `/complete` | JWT / Admin |
| Payment | POST | `/payment/pay` | JWT |
| Admin | POST | `/admin/login` | - |
| Admin | GET | `/admin/dashboard` `/admin/users` | - |

## 功能模块

| 模块 | 核心功能 |
|------|----------|
| 认证 | BCrypt 密码加密、JWT 签发与解析、Redis 黑名单强制失效、Token 拦截器统一鉴权 |
| 用户 | 个人信息编辑、收货地址 CRUD、默认地址管理 |
| 商品 | 分类树（递归构建）、关键词搜索 + 分类筛选 + 分页、库存原子扣减与回滚 |
| 订单 | ConcurrnetHashMap 购物车、雪花 ID 生成订单号、订单状态机流转、取消失败回滚库存 |
| 支付 | 模拟支付网关、交易流水号生成、支付记录持久化 |
| 管理 | 多维度数据看板（用户/订单/GMV/待发货）、用户状态管控 |

## 设计亮点

- **Token 拦截器** — 所有认证逻辑收敛至 `TokenInterceptor`，Controller 零侵入
- **Redis 降级** — Redis 不可用时自动跳过黑名单校验，系统正常运行
- **雪花 ID 安全序列化** — `@JsonFormat(Shape.STRING)` 防止 JavaScript 大整数精度丢失
- **温暖自然 UI** — Nunito Sans 字体、#F9F5F0 暖米背景、大圆角卡片、手绘 SVG 装饰、Element Plus 专业图标
- **H2 开发模式** — 无需任何外部依赖即可启动，适合快速原型验证
