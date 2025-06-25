# 数据库初始化指南

本目录包含了初始化项目数据库所需的SQL脚本。

## 数据库设置步骤

### 方法一：直接执行SQL脚本（推荐）

1. 确保已安装MySQL数据库（推荐版本：8.0+）
2. 使用MySQL命令行工具或图形化界面工具（如MySQL Workbench、Navicat等）
3. 以管理员权限登录MySQL
4. 执行`init.sql`脚本文件

```bash
# 命令行方式执行
mysql -u root -p < init.sql

# 或者登录后执行
mysql> source /path/to/init.sql
```

### 方法二：手动创建数据库

如果你希望手动创建数据库，请执行以下步骤：

1. 创建数据库：
```sql
CREATE DATABASE campus_trading CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 使用数据库：
```sql
USE campus_trading;
```

3. 然后执行`init.sql`中的表创建语句和数据插入语句

## 默认账户信息

脚本会自动创建以下测试账户：

1. 管理员账户
   - 用户名：admin
   - 密码：admin123
   - 角色：ROLE_ADMIN, ROLE_USER

2. 测试用户账户
   - 用户名：user1
   - 密码：password
   - 角色：ROLE_USER

## 数据库配置

确保在`application.yml`中配置正确的数据库连接信息：

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/campus_trading?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: 你的密码
```

## 禁用非必要服务

如果你的开发环境中没有安装Redis、RabbitMQ或ElasticSearch，可以通过以下配置禁用这些服务：

### 方法一：使用配置排除

在`application.yml`中添加以下配置：

```yaml
spring:
  autoconfigure:
    exclude:
      - org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
      - org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration
      - org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration
      - org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration
      - org.springframework.boot.autoconfigure.data.elasticsearch.ReactiveElasticsearchRepositoriesAutoConfiguration
      - org.springframework.boot.autoconfigure.data.elasticsearch.ReactiveElasticsearchRestClientAutoConfiguration
```

### 方法二：创建开发配置文件

1. 复制`application-example.yml`为`application-dev.yml`
2. 在`application-dev.yml`中注释掉Redis、RabbitMQ和ElasticSearch相关配置
3. 运行时使用开发配置：
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## 注意事项

- 请确保MySQL服务已启动
- 请确保使用的数据库用户具有创建数据库和表的权限
- 如果遇到外键约束问题，可能需要临时禁用外键检查：
  ```sql
  SET FOREIGN_KEY_CHECKS=0;
  -- 执行脚本
  SET FOREIGN_KEY_CHECKS=1;
  ``` 