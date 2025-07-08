# 用户公开资料功能说明

本文档描述了用户公开资料功能的实现和部署步骤。

## 功能概述

本次更新添加了用户公开资料功能，使其他用户可以查看特定用户的基本信息、发布的物品和评价记录。主要实现了以下功能：

1. 获取用户基本公开信息（用户名、昵称、头像、简介等）
2. 获取用户发布的所有物品
3. 获取用户收到的所有评价

## 数据库更新

需要执行以下数据库更新脚本：

```sql
backend/src/main/resources/db/update_user_table.sql
```

该脚本将：
1. 为用户表添加个人简介（bio）和所在地（location）字段
2. 创建评价表（ratings）
3. 添加一些示例评价数据

## API 接口

新增了以下API接口：

### 获取用户公开资料

```
GET /api/users/{userId}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "user": {
      "id": 1,
      "username": "user1",
      "nickname": "小明",
      "avatarUrl": "http://example.com/images/avatar1.jpg",
      "bio": "这是我的个人简介",
      "location": "北京",
      "createTime": "2023-05-01T10:00:00"
    },
    "items": [
      {
        "id": 1,
        "name": "二手笔记本电脑",
        "price": 2000.00,
        "description": "使用不到一年的笔记本",
        "imageUrls": ["http://example.com/images/laptop1.jpg"],
        "condition": 8,
        "status": 1,
        "createTime": "2023-05-10T14:30:00"
      }
    ],
    "ratings": [
      {
        "id": 1,
        "orderId": 1,
        "itemId": 1,
        "rating": 5,
        "comment": "卖家服务态度很好，物品和描述一致，非常满意！",
        "role": "SELLER",
        "createTime": "2023-05-15T16:20:00",
        "rater": {
          "id": 2,
          "username": "user2",
          "nickname": "小红",
          "avatarUrl": "http://example.com/images/avatar2.jpg"
        },
        "item": {
          "id": 1,
          "name": "二手笔记本电脑",
          "price": 2000.00,
          "images": ["http://example.com/images/laptop1.jpg"]
        }
      }
    ]
  }
}
```

## 部署步骤

1. 将新增的Java类添加到项目中
2. 更新现有的Java类
3. 执行数据库迁移脚本
4. 重启应用服务器

## 前端集成

前端需要完成以下工作：

1. 确保在CommentList.js和ItemDetail.js中的用户头像点击事件正确跳转到用户公开资料页面
2. 验证UserPublicProfile.js组件正确显示用户资料、物品列表和评价信息

## 注意事项

1. 确保数据库中已经存在基本的用户、物品和订单数据
2. 评价数据依赖于订单数据，所以请确保订单相关功能已经实现
3. 本功能实现了用户资料的读取功能，更新功能通过原有的用户个人中心实现 