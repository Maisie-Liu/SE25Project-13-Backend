# 交物通后端服务 - 校园二手交易平台

这是“交物通”校园二手交易平台的后端服务部分，基于 Spring Boot 构建，提供完整的 RESTful API 支持。

## 技术栈

- **Spring Boot 2.7.x**
- **Spring Security + JWT 认证**
- **Spring Data JPA**
- **MySQL 数据库**
- **Redis 缓存**
- **ElasticSearch 搜索引擎**
- **MongoDB + GridFS 图片与大文件存储**
- **AI 服务集成（物品描述生成）**

## 核心功能模块

- 用户注册、登录与权限控制
- 物品发布、编辑、状态管理
- 搜索与筛选（关键词、分类、价格、新旧程度等）
- 订单创建与状态跟踪
- 用户互评与信誉系统
- 消息通知与站内私信支持
- 智能推荐（基于用户行为）

## 项目结构
```
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── campus/
│                   └── trading/
│                       ├── config/       # 配置类（如安全配置、数据库配置等）
│                       ├── controller/   # 控制器层，处理 HTTP 请求
│                       ├── dao/          # 数据访问对象层，负责与数据库交互
│                       ├── dto/          # 数据传输对象，用于在不同层之间传递数据
│                       ├── entity/       # 实体类，映射数据库表结构
│                       ├── repository/   # 数据访问层接口，定义数据访问方法
│                       ├── service/      # 业务逻辑层，实现具体的业务逻辑
│                       └── TradingApplication.java # 应用启动类
├── resources/
│   └── db/
│       ├── clear.sql         # 清空数据库脚本
│       ├── init.sql          # 初始化数据库脚本
│       ├── README.md         # 数据库相关说明文档
│       └── update_user_table.sql # 更新用户表脚本
│   └── application-example.yml # 应用配置文件示例
├── static/         # 静态资源（如默认图片）
├── .gitignore      # Git 忽略文件配置
├── lombok.config   # Lombok 配置文件
├── pom.xml         # Maven 项目配置文件
└── README.md       # 项目说明文档
```

## 安装与运行

### 1. 数据库初始化

```bash
# MySQL 初始化
mysql -u root -p < src/main/resources/db/init.sql
```

> 详见: [数据库初始化指南](./src/main/resources/db/README.md)


### 2. 启动后端服务

```bash
# 安装依赖
mvn clean install

# 启动服务
mvn spring-boot:run
```

默认端口：`8080`

## 配置说明

- 数据库连接、Redis、MongoDB、ElasticSearch 等配置，请在相同目录下复制 `src\main\resources\application-example.yml` ，并重命名为 `application.yml` ，在其中进行修改。
- 支持多环境配置（dev / prod）。

