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

# MongoDB图片存储配置说明

本项目将所有商品图片和用户头像存储于MongoDB数据库，采用GridFS进行大文件管理。

## 1. MongoDB安装

- 推荐使用MongoDB 4.x及以上版本。
- 可参考官方文档：https://docs.mongodb.com/manual/installation/

## 2. 配置application.yml

在`spring.data.mongodb`下配置MongoDB连接信息，例如：

```yaml
spring:
  data:
    mongodb:
      host: localhost
      port: 27017
      database: campus_trading
      username: <你的用户名，可选>
      password: <你的密码，可选>
      authentication-database: admin
```

## 3. 图片存储说明

- 图片文件通过Spring Data MongoDB的GridFS存储，默认bucket为`images`。
- 图片上传后返回图片ID，业务实体（如商品、用户）仅保存图片ID。
- 访问图片时通过接口`/api/image/{id}`获取。

## 4. 依赖与驱动

- Spring Boot Starter Data MongoDB已集成。
- 需确保`spring-boot-starter-data-mongodb`依赖在`pom.xml`中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

## 5. 图片上传与下载接口

- 图片上传接口需鉴权。
- 图片下载接口需鉴权或防盗链。

## 6. 其他

- 推荐使用MongoDB Compass等可视化工具管理图片文件。
- 若需更改bucket名，可在`application.yml`中修改`image.bucket`配置。

# MongoDB数据库与图片初始化

## 1. 使用MongoDB Compass创建数据库

1. 打开MongoDB Compass，点击左侧"Create Database"。
2. Database Name 填写：`campus_trading`
3. Collection Name 填写：`fs.files`
4. 点击"Create Database"即可。
   - GridFS会自动在第一次上传图片时创建`fs.chunks`集合。

## 2. 使用命令行创建数据库

```bash
# 进入Mongo Shell
mongosh

# 创建数据库
use campus_trading
# 创建GridFS文件集合（可选，首次上传图片时会自动创建）
db.createCollection('fs.files')
```

## 3. 上传图片到MongoDB（GridFS）

### 方法一：使用mongofiles命令行工具 (下载路径 https://www.mongodb.com/try/download/database-tools)

```bash
# 上传图片到GridFS，--db指定数据库，put后跟图片路径
mongofiles --host localhost --port 27017 --db campus_trading put ./avatar1.jpg --type image/jpeg
# 上传后会返回ObjectId，记下该ID用于MySQL表的avatar_image_id或image_id字段
```

### 方法二：使用Spring Boot脚本上传

可编写简单的Spring Boot初始化脚本，示例：

```java
@Autowired
private GridFsTemplate gridFsTemplate;

public void uploadInitImages() throws FileNotFoundException {
    File file = new File("path/to/avatar1.jpg");
    ObjectId id = gridFsTemplate.store(new FileInputStream(file), "avatar1.jpg", "image/jpeg");
    System.out.println("avatar1 ObjectId: " + id.toHexString());
}
```

### 方法三：使用MongoDB Compass上传

1. 进入`campus_trading`数据库，选择`fs.files`集合。
2. 点击"Add Data" → "Insert Document"，可手动插入图片元数据（不推荐，建议用命令行或脚本上传二进制图片）。

## 4. 在MySQL中引用图片ID

- 上传图片后，记录返回的ObjectId（如`65e1234567890abcdef12345`）。
- 在`init.sql`中，用户表的`avatar_image_id`、物品图片表的`image_id`字段填入该ID。

## 5. 访问图片

- 前端或后端通过`/api/image/{id}`接口访问图片。 