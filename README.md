# 🛒 悠然商城 (ease-mall)

一个基于 Spring Boot 3 + Vue 3 的全栈电商项目，适合 Java 应届生简历项目。

> 温暖自然的设计风格 · 完整业务流程 · 92 个文件 · ~4500 行代码

## 技术栈

| 类别 | 技术 |
|------|------|
| 后端框架 | Java 21 + Spring Boot 3.2 |
| ORM | MyBatis-Plus 3.5 |
| 数据库 | MySQL 8.0 / H2（开发环境零安装） |
| 缓存 | Redis 7（可降级，无 Redis 也能跑） |
| 消息队列 | RabbitMQ 3.x（预留） |
| 认证 | JWT + Redis 黑名单双重验证 |
| API 文档 | Knife4j (Swagger 增强) |
| 连接池 | Druid |
| 前端 | Vue 3 + Vite + Element Plus + Pinia + Axios |
| 构建 | Maven Wrapper（无需安装 Maven） |

## 项目结构

```
ease-mall/
├── pom.xml                          # 唯一的 POM，单体应用
├── mvnw / mvnw.cmd                  # Maven Wrapper
├── docker-compose.yml               # MySQL + Redis + RabbitMQ（可选）
├── docs/sql/init.sql                # 生产环境建表脚本
│
├── src/main/java/com/mall/
│   ├── MallApplication.java         # 唯一启动类
│   ├── common/                      # 公共模块
│   │   ├── result/                  # R<T> 统一返回 + 状态码
│   │   ├── exception/               # 全局异常处理
│   │   ├── utils/                   # JWT + 雪花ID
│   │   ├── interceptor/             # Token 统一拦截器
│   │   ├── service/                 # Token 黑名单服务
│   │   └── config/                  # WebMvc + JWT 配置
│   ├── auth/                        # 认证模块（注册/登录/退出）
│   ├── user/                        # 用户模块（收货地址CRUD）
│   ├── product/                     # 商品模块（分类树+搜索+库存）
│   ├── order/                       # 订单模块（购物车+下单+状态机）
│   ├── payment/                     # 支付模块（模拟支付+记录）
│   └── admin/                       # 管理模块（看板+用户管理）
│
├── src/main/resources/
│   ├── application.yml              # 应用配置
│   ├── schema.sql                   # H2 自动建表
│   └── data.sql                     # H2 测试数据
│
└── web/                             # Vue 3 前端
    └── src/views/
        ├── user/                    # 登录 / 注册
        ├── product/                 # 商品首页 / 详情
        ├── order/                   # 购物车 / 我的订单
        └── admin/                   # 管理登录 / 数据看板
```

## 快速开始

### 后端

```bash
# 1. 编译 + 启动（H2 内存数据库，零安装依赖）
cd ease-mall
mvnw clean package -DskipTests
java -jar target/ease-mall-1.0.0-SNAPSHOT.jar

# 接口文档: http://localhost:8080/doc.html
```

### 前端

```bash
# 2. 新终端，启动前端
cd ease-mall/web
npm install
npm run dev

# 商城首页: http://localhost:5173
```

### Docker 基础设施（可选，生产模式需要）

```bash
docker-compose up -d    # 启动 MySQL + Redis + RabbitMQ
# 然后修改 application.yml 的数据源为 MySQL
```

## API 接口一览

| 模块 | 方法 | 路径 | 认证 |
|------|------|------|------|
| 认证 | POST | `/auth/register` | 无 |
| 认证 | POST | `/auth/login` | 无 |
| 认证 | POST | `/auth/logout` | JWT |
| 地址 | GET/POST/PUT/DELETE | `/user/address` `/{id}` | JWT |
| 分类 | GET | `/product/category` | 无 |
| 商品 | GET/POST/PUT | `/product` `/{id}` | GET 无 / 其他 JWT |
| 购物车 | GET/POST/PUT/DELETE | `/cart` `/{productId}` | JWT |
| 订单 | GET/POST | `/order` `/{id}` | JWT |
| 订单 | PUT | `/order/{id}/cancel` | JWT |
| 订单 | PUT | `/order/{id}/pay/ship/complete` | 无（管理端） |
| 支付 | POST | `/payment/pay` | JWT |
| 管理 | POST | `/admin/login` | 无 |
| 管理 | GET | `/admin/dashboard` | 无 |
| 管理 | GET | `/admin/users` | 无 |

## 功能清单

| 模块 | 功能 | 状态 |
|------|------|------|
| 认证 | 注册（BCrypt）、登录（JWT）、退出（Redis 黑名单） | ✅ |
| 用户 | 收货地址 CRUD、默认地址管理 | ✅ |
| 商品 | 分类树、商品搜索、分页、库存扣减/回滚 | ✅ |
| 订单 | 购物车、下单、订单状态机、取消自动回库存 | ✅ |
| 支付 | 模拟支付、支付记录 | ✅ |
| 管理 | 数据看板、用户启用/禁用 | ✅ |
| 前端 | 8 个页面、暖色 UI、手绘风装饰 | ✅ |

## 特色亮点

- **Redis 双重验证**：JWT + 黑名单，无 Redis 时自动降级
- **Token 统一拦截器**：所有 Controller 不再重复写解析代码
- **手绘风 UI**：Nunito Sans 字体、暖米色背景、圆角卡片、SVG 装饰
- **H2 开发模式**：无需安装数据库即可快速启动测试
- **Docker 一键部署**：`docker-compose up -d` 启动全部基础设施

## Git 提交历史

| 提交 | 说明 |
|------|------|
| 20afbf2 | 🚀 项目初始化 |
| 2e38255 | ✨ 商品管理模块 |
| 066af43 | ✨ 订单 + 购物车 |
| 8a08ef8 | 🐛 H2 保留字修复 |
| 3630689 | ✨ 后台管理模块 |
| 105dc04 | ✨ Vue3 前端 |
| d18ded7 | ✨ 模拟支付 |
| c5cb33c | 🎨 前端 UI 美化 |
| 2a8c699 | 🔐 Redis 双重验证 |
| dfdfa0b | 🎨 暖色自然风换肤 |
| 904140b | 🎨 中文 Logo「悠然商城」 |
