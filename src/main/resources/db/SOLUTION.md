# 数据库问题解决方案

## 问题描述

项目无法启动，出现以下错误：
```
Failed to execute goal org.springframework.boot:spring-boot-maven-plugin:2.7.12:run (default-cli) on project trading: Application finished with exit code: 1
```

具体原因是数据库连接失败，错误信息为：
```
java.sql.SQLException: Access denied for user 'root'@'localhost' (using password: YES)
```

## 解决方案

1. 创建数据库初始化脚本 `init.sql`
   - 包含创建数据库、表结构和初始数据的SQL语句
   - 避免使用MySQL保留字（如`condition`）作为字段名

2. 修改实体类中的字段名
   - 将 `condition` 改为 `itemCondition`
   - 同时更新相应的getter、setter和builder方法

3. 添加数据库设置说明
   - 创建 `README.md` 文件，详细说明如何设置数据库
   - 提供多种设置方式，适应不同组员的环境

4. 创建示例配置文件
   - 提供 `application-example.yml` 作为配置模板
   - 组员可以根据自己的环境进行修改

## 使用方法

1. 执行 `init.sql` 脚本创建数据库和表结构
2. 修改 `application.yml` 中的数据库连接信息
3. 启动应用程序

## 注意事项

- 如果不需要Redis、RabbitMQ或ElasticSearch，可以按照 `README.md` 中的说明禁用这些服务
- 首次运行时，可以将 `spring.jpa.hibernate.ddl-auto` 设置为 `create`，之后改为 `update`
- 如果遇到外键约束问题，可以临时禁用外键检查

## 其他问题

应用程序启动时还出现了缺少 `UserService` Bean 的错误，这需要在后续修复：
```
No qualifying bean of type 'com.campus.trading.service.UserService' available
``` 