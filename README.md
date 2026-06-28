# ease-mall — 电商平台

一个基于 Spring Boot 3 的电商项目，适合 Java 应届生简历项目。

## 技术栈

| 类别 | 技术 |
|------|------|
| 核心框架 | Spring Boot 3.2 + Spring Cloud 2023 |
| 微服务 | Spring Cloud Alibaba（Nacos 注册/配置中心） |
| API 网关 | Spring Cloud Gateway |
| 服务调用 | OpenFeign + LoadBalancer |
| ORM | MyBatis-Plus 3.5 |
| 数据库 | MySQL 8.0 |
| 缓存 | Redis 7 |
| 消息队列 | RabbitMQ 3.x |
| 安全认证 | Spring Security + JWT |
| API 文档 | Knife4j |
| 连接池 | Druid |
| 工具库 | Lombok + Hutool |

## 项目结构

```
mall-micro/
├── mall-common      # 公共模块（R类、异常、工具类）
├── mall-gateway     # API 网关（8080）
├── mall-auth        # 认证服务（8081）
├── mall-user        # 用户服务（8082）
├── mall-product     # 商品服务（8083）
├── mall-order       # 订单服务（8084）
├── mall-payment     # 支付服务（8085）
├── mall-admin       # 管理服务（8086）
└── docs/sql/        # SQL 脚本
```

## 快速开始

### 1. 启动基础设施

```bash
docker-compose up -d
```

### 2. 编译项目

```bash
./mvnw clean compile -DskipTests   # Git Bash / Linux / Mac
mvnw.cmd clean compile -DskipTests # Windows CMD
```

### 3. 启动服务（按顺序）

```bash
# 先启动网关
./mvnw -pl mall-gateway spring-boot:run

# 再启动业务服务
./mvnw -pl mall-auth spring-boot:run
./mvnw -pl mall-user spring-boot:run
./mvnw -pl mall-product spring-boot:run
./mvnw -pl mall-order spring-boot:run
./mvnw -pl mall-payment spring-boot:run
./mvnw -pl mall-admin spring-boot:run
```

### 4. 访问

| 地址 | 说明 |
|------|------|
| http://localhost:8848/nacos | Nacos 控制台（用户名/密码：nacos/nacos） |
| http://localhost:15672 | RabbitMQ 管理界面（guest/guest） |
| http://localhost:8081/doc.html | 认证服务 API 文档 |
| http://localhost:8082/doc.html | 用户服务 API 文档 |
| http://localhost:8083/doc.html | 商品服务 API 文档 |

## 开发进度

- [x] 项目骨架搭建
- [ ] 认证服务（注册/登录/JWT）
- [ ] 用户服务（信息/地址）
- [ ] 商品服务（分类/商品/库存）
- [ ] 订单服务（购物车/下单）
- [ ] 支付服务（模拟支付）
- [ ] 后台管理服务
- [ ] Vue3 前端
