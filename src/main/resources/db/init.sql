-- -- 创建数据库
-- CREATE DATABASE IF NOT EXISTS campus_trading CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- -- 使用数据库
-- USE campus_trading;

-- 用户表
CREATE TABLE IF NOT EXISTS t_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(50),
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    avatar VARCHAR(255),
    status INT NOT NULL DEFAULT 1,
    last_login_time DATETIME,
    create_time DATETIME,
    update_time DATETIME
);

-- 用户角色表
CREATE TABLE IF NOT EXISTS t_user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES t_user(id) ON DELETE CASCADE
);

-- 分类表
CREATE TABLE IF NOT EXISTS t_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200),
    icon VARCHAR(255),
    sort_order INT,
    parent_id BIGINT,
    create_time DATETIME,
    update_time DATETIME
);

-- 物品表
CREATE TABLE IF NOT EXISTS t_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category_id BIGINT,
    price DECIMAL(10, 2) NOT NULL,
    description VARCHAR(2000),
    item_condition INT NOT NULL,
    status INT NOT NULL DEFAULT 1,
    popularity INT NOT NULL DEFAULT 0,
    user_id BIGINT NOT NULL,
    stock INT NOT NULL DEFAULT 1,
    create_time DATETIME,
    update_time DATETIME,
    FOREIGN KEY (category_id) REFERENCES t_category(id),
    FOREIGN KEY (user_id) REFERENCES t_user(id)
);

-- 物品图片表
CREATE TABLE IF NOT EXISTS t_item_images (
    item_id BIGINT NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    PRIMARY KEY (item_id, image_url),
    FOREIGN KEY (item_id) REFERENCES t_item(id) ON DELETE CASCADE
);

-- 订单表
CREATE TABLE IF NOT EXISTS t_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(32) NOT NULL UNIQUE,
    buyer_id BIGINT NOT NULL,
    seller_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    status INT NOT NULL DEFAULT 0,
    trade_type INT NOT NULL DEFAULT 0,
    trade_location VARCHAR(200),
    trade_time DATETIME,
    buyer_message VARCHAR(500),
    seller_remark VARCHAR(500),
    buyer_comment VARCHAR(1000),
    seller_comment VARCHAR(1000),
    create_time DATETIME,
    update_time DATETIME,
    finish_time DATETIME,
    buyer_rating INT DEFAULT NULL,
    seller_rating INT DEFAULT NULL,
    FOREIGN KEY (buyer_id) REFERENCES t_user(id),
    FOREIGN KEY (seller_id) REFERENCES t_user(id),
    FOREIGN KEY (item_id) REFERENCES t_item(id)
);

-- 定金托管表
CREATE TABLE IF NOT EXISTS t_escrow (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    order_no VARCHAR(64) NOT NULL,
    buyer_id BIGINT NOT NULL,
    seller_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    item_name VARCHAR(128) NOT NULL,
    escrow_amount DECIMAL(10, 2) NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    status INT NOT NULL DEFAULT 1,
    contract_address VARCHAR(64),
    transaction_hash VARCHAR(128),
    payment_method INT,
    payment_time DATETIME,
    expire_time DATETIME NOT NULL,
    update_time DATETIME,
    create_time DATETIME NOT NULL,
    remark VARCHAR(512),
    FOREIGN KEY (order_id) REFERENCES t_order(id),
    FOREIGN KEY (buyer_id) REFERENCES t_user(id),
    FOREIGN KEY (seller_id) REFERENCES t_user(id),
    FOREIGN KEY (item_id) REFERENCES t_item(id)
);

-- 收藏表
CREATE TABLE IF NOT EXISTS t_favorite (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    create_time DATETIME,
    UNIQUE KEY uk_user_item (user_id, item_id),
    FOREIGN KEY (user_id) REFERENCES t_user(id),
    FOREIGN KEY (item_id) REFERENCES t_item(id)
);

-- 评论表
CREATE TABLE IF NOT EXISTS t_comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content VARCHAR(500) NOT NULL,
    user_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    parent_id BIGINT,
    reply_user_id BIGINT,
    status INT NOT NULL DEFAULT 1,
    create_time DATETIME,
    update_time DATETIME,
    FOREIGN KEY (user_id) REFERENCES t_user(id),
    FOREIGN KEY (item_id) REFERENCES t_item(id)
);

-- 聊天表
CREATE TABLE IF NOT EXISTS chats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user1_id BIGINT NOT NULL,
    user2_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    last_message VARCHAR(255),
    FOREIGN KEY (user1_id) REFERENCES t_user(id),
    FOREIGN KEY (user2_id) REFERENCES t_user(id),
    FOREIGN KEY (item_id) REFERENCES t_item(id)
);

-- 消息表（使用单表继承）
CREATE TABLE IF NOT EXISTS messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipient_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    message_type VARCHAR(20) NOT NULL,
    
    -- ChatMessage 特有字段
    content VARCHAR(1000),
    item_id BIGINT,
    chat_id BIGINT,
    
    -- CommentMessage 特有字段
    comment_id BIGINT,
    
    -- FavoriteMessage 特有字段
    favorite_id BIGINT,
    
    -- OrderMessage 特有字段
    order_id BIGINT,
    status VARCHAR(50),
    status_text VARCHAR(255),
    
    FOREIGN KEY (recipient_id) REFERENCES t_user(id),
    FOREIGN KEY (sender_id) REFERENCES t_user(id),
    FOREIGN KEY (item_id) REFERENCES t_item(id),
    FOREIGN KEY (chat_id) REFERENCES chats(id) ON DELETE CASCADE,
    FOREIGN KEY (comment_id) REFERENCES t_comment(id) ON DELETE CASCADE,
    FOREIGN KEY (favorite_id) REFERENCES t_favorite(id) ON DELETE CASCADE,
    FOREIGN KEY (order_id) REFERENCES t_order(id) ON DELETE CASCADE
);

-- 插入初始管理员用户（密码：123456）
INSERT INTO t_user (username, password, nickname, email, status, create_time, update_time)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 'admin@example.com', 1, NOW(), NOW());

-- 插入管理员角色
INSERT INTO t_user_roles (user_id, role) VALUES (1, 'ROLE_ADMIN');
INSERT INTO t_user_roles (user_id, role) VALUES (1, 'ROLE_USER');

-- 插入测试用户（密码：123456）
INSERT INTO t_user (username, password, nickname, email, status, create_time, update_time)
VALUES ('user1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '测试用户1', 'user1@example.com', 1, NOW(), NOW());

-- 插入普通用户角色
INSERT INTO t_user_roles (user_id, role) VALUES (2, 'ROLE_USER');

-- 插入测试用户test1（密码：123456）
INSERT INTO t_user (username, password, nickname, email, phone, avatar, status, create_time, update_time)
VALUES ('test1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '测试用户', 'test1@example.com', '13800138000', 'https://via.placeholder.com/100', 1, NOW(), NOW());

-- 插入普通用户角色
INSERT INTO t_user_roles (user_id, role) VALUES (3, 'ROLE_USER');

-- 插入基础分类
INSERT INTO t_category (name, description, sort_order, create_time, update_time)
VALUES ('电子产品', '手机、电脑、平板等电子设备', 1, NOW(), NOW());

INSERT INTO t_category (name, description, sort_order, create_time, update_time)
VALUES ('图书教材', '各类书籍、教材、资料', 2, NOW(), NOW());

INSERT INTO t_category (name, description, sort_order, create_time, update_time)
VALUES ('生活用品', '日常生活所需物品', 3, NOW(), NOW());

INSERT INTO t_category (name, description, sort_order, create_time, update_time)
VALUES ('服装鞋帽', '各类衣物、鞋子、帽子等', 4, NOW(), NOW());

INSERT INTO t_category (name, description, sort_order, create_time, update_time)
VALUES ('体育用品', '运动器材、健身装备等', 5, NOW(), NOW());

-- 插入子分类
INSERT INTO t_category (name, description, parent_id, sort_order, create_time, update_time)
VALUES ('手机', '各类手机及配件', 1, 1, NOW(), NOW());

INSERT INTO t_category (name, description, parent_id, sort_order, create_time, update_time)
VALUES ('电脑', '笔记本、台式机等', 1, 2, NOW(), NOW());

INSERT INTO t_category (name, description, parent_id, sort_order, create_time, update_time)
VALUES ('教材', '各专业教材', 2, 1, NOW(), NOW());

INSERT INTO t_category (name, description, parent_id, sort_order, create_time, update_time)
VALUES ('小说', '各类小说作品', 2, 2, NOW(), NOW());

-- 插入测试物品数据
-- 电子产品类物品
INSERT INTO t_item (name, category_id, price, description, item_condition, status, popularity, user_id, create_time, update_time)
VALUES ('MacBook Pro 2021款', 7, 8999.00, 'M1芯片，16GB内存，512GB SSD，几乎全新，用了不到3个月，无划痕。', 2, 1, 58, 3, NOW(), NOW());

INSERT INTO t_item (name, category_id, price, description, item_condition, status, popularity, user_id, create_time, update_time)
VALUES ('全新AirPods Pro', 6, 1299.00, '全新未拆封，入手渠道正规，带发票。', 1, 1, 42, 2, NOW(), NOW());

INSERT INTO t_item (name, category_id, price, description, item_condition, status, popularity, user_id, create_time, update_time)
VALUES ('iPhone 13 128G 午夜色', 6, 4599.00, '使用8个月，无划痕，电池健康度95%，配件齐全，带原厂充电器。', 3, 1, 76, 3, NOW(), NOW());

-- 图书教材类物品
INSERT INTO t_item (name, category_id, price, description, item_condition, status, popularity, user_id, create_time, update_time)
VALUES ('高等数学教材，配习题集', 8, 45.00, '高等数学第七版，上下册都有，配套习题集，有少量笔记，保存完好。', 4, 1, 25, 2, NOW(), NOW());

INSERT INTO t_item (name, category_id, price, description, item_condition, status, popularity, user_id, create_time, update_time)
VALUES ('Java核心技术卷I（原书第11版）', 8, 69.00, '全新，包装还在，买来没看几页，适合Java入门学习。', 1, 1, 31, 3, NOW(), NOW());

-- 生活用品类物品
INSERT INTO t_item (name, category_id, price, description, item_condition, status, popularity, user_id, create_time, update_time)
VALUES ('宿舍神器小台灯', 3, 39.00, '充电式LED台灯，有三档亮度调节，续航长达24小时，9成新。', 2, 1, 17, 2, NOW(), NOW());

-- 服装鞋帽类物品
INSERT INTO t_item (name, category_id, price, description, item_condition, status, popularity, user_id, create_time, update_time)
VALUES ('Nike运动鞋，43码', 4, 299.00, 'Nike Air Zoom系列，黑色，43码，穿过5次左右，基本全新。', 3, 1, 45, 3, NOW(), NOW());

-- 运动用品类物品
INSERT INTO t_item (name, category_id, price, description, item_condition, status, popularity, user_id, create_time, update_time)
VALUES ('二手自行车，九成新', 5, 399.00, '捷安特牌山地自行车，前后减震，变速系统完好，骑行不到500公里。', 2, 1, 37, 2, NOW(), NOW());

-- 物品图片
INSERT INTO t_item_images (item_id, image_url) VALUES (1, 'https://via.placeholder.com/400x300?text=MacBook+Pro');
INSERT INTO t_item_images (item_id, image_url) VALUES (1, 'https://via.placeholder.com/400x300?text=MacBook+Side');
INSERT INTO t_item_images (item_id, image_url) VALUES (2, 'https://via.placeholder.com/400x300?text=AirPods+Pro');
INSERT INTO t_item_images (item_id, image_url) VALUES (3, 'https://via.placeholder.com/400x300?text=iPhone+13');
INSERT INTO t_item_images (item_id, image_url) VALUES (4, 'https://via.placeholder.com/400x300?text=Math+Book');
INSERT INTO t_item_images (item_id, image_url) VALUES (5, 'https://via.placeholder.com/400x300?text=Java+Book');
INSERT INTO t_item_images (item_id, image_url) VALUES (6, 'https://via.placeholder.com/400x300?text=Desk+Lamp');
INSERT INTO t_item_images (item_id, image_url) VALUES (7, 'https://via.placeholder.com/400x300?text=Nike+Shoes');
INSERT INTO t_item_images (item_id, image_url) VALUES (8, 'https://via.placeholder.com/400x300?text=Bicycle');

-- 收藏数据
INSERT INTO t_favorite (user_id, item_id, create_time) VALUES (3, 2, NOW());
INSERT INTO t_favorite (user_id, item_id, create_time) VALUES (2, 1, NOW());
INSERT INTO t_favorite (user_id, item_id, create_time) VALUES (2, 3, NOW());
INSERT INTO t_favorite (user_id, item_id, create_time) VALUES (3, 4, NOW());

-- 生成一些订单测试数据
INSERT INTO t_order (order_no, buyer_id, seller_id, item_id, amount, status, trade_type, trade_location, trade_time, buyer_message, create_time, update_time)
VALUES (CONCAT('ORD', DATE_FORMAT(NOW(), '%Y%m%d'), '0001'), 3, 2, 4, 45.00, 2, 1, '校图书馆门口', DATE_ADD(NOW(), INTERVAL 2 DAY), '周末下午方便交易', NOW(), NOW());

INSERT INTO t_order (order_no, buyer_id, seller_id, item_id, amount, status, trade_type, trade_location, trade_time, buyer_message, create_time, update_time)
VALUES (CONCAT('ORD', DATE_FORMAT(NOW(), '%Y%m%d'), '0002'), 2, 3, 7, 299.00, 1, 0, NULL, NULL, '请问鞋子穿着舒适吗？', NOW(), NOW());

-- 插入聊天测试数据
INSERT INTO chats (user1_id, user2_id, item_id, created_at, updated_at, last_message)
VALUES (2, 3, 1, NOW(), NOW(), '您好，这个MacBook还可以便宜点吗？');

INSERT INTO chats (user1_id, user2_id, item_id, created_at, updated_at, last_message)
VALUES (3, 2, 4, NOW(), NOW(), '这本书是最新版本吗？');

-- 插入聊天消息测试数据
INSERT INTO messages (recipient_id, sender_id, created_at, is_read, message_type, content, item_id, chat_id)
VALUES (3, 2, NOW(), false, 'CHAT', '您好，请问这台MacBook Pro还在出售吗？', 1, 1);

INSERT INTO messages (recipient_id, sender_id, created_at, is_read, message_type, content, item_id, chat_id)
VALUES (2, 3, DATE_ADD(NOW(), INTERVAL 5 MINUTE), false, 'CHAT', '是的，还在售，有什么问题吗？', 1, 1);

INSERT INTO messages (recipient_id, sender_id, created_at, is_read, message_type, content, item_id, chat_id)
VALUES (3, 2, DATE_ADD(NOW(), INTERVAL 10 MINUTE), false, 'CHAT', '您好，这个MacBook还可以便宜点吗？', 1, 1);

INSERT INTO messages (recipient_id, sender_id, created_at, is_read, message_type, content, item_id, chat_id)
VALUES (2, 3, DATE_ADD(NOW(), INTERVAL 2 MINUTE), false, 'CHAT', '这本书是最新版本吗？', 4, 2);

INSERT INTO messages (recipient_id, sender_id, created_at, is_read, message_type, content, item_id, chat_id)
VALUES (3, 2, DATE_ADD(NOW(), INTERVAL 7 MINUTE), false, 'CHAT', '是的，是最新版，而且几乎没怎么用过。', 4, 2);

-- 插入评论消息测试数据
INSERT INTO t_comment (content, user_id, item_id, status, create_time, update_time)
VALUES ('这个价格合适吗？能再便宜一点吗？', 2, 1, 1, NOW(), NOW());

INSERT INTO t_comment (content, user_id, item_id, parent_id, reply_user_id, status, create_time, update_time)
VALUES ('价格已经很优惠了，这是市场最低价。', 3, 1, 1, 2, 1, DATE_ADD(NOW(), INTERVAL 30 MINUTE), DATE_ADD(NOW(), INTERVAL 30 MINUTE));

INSERT INTO messages (recipient_id, sender_id, created_at, is_read, message_type, item_id, comment_id)
VALUES (3, 2, NOW(), false, 'COMMENT', 1, 1);

INSERT INTO messages (recipient_id, sender_id, created_at, is_read, message_type, item_id, comment_id)
VALUES (2, 3, DATE_ADD(NOW(), INTERVAL 30 MINUTE), false, 'COMMENT', 1, 2);

-- 插入收藏消息测试数据（基于现有收藏数据）
-- 用户3收藏了用户2的物品2 (AirPods Pro)，给用户2发送消息
INSERT INTO messages (recipient_id, sender_id, created_at, is_read, message_type, item_id, favorite_id)
VALUES (2, 3, NOW(), false, 'FAVORITE', 2, 1);

-- 用户2收藏了用户3的物品1 (MacBook Pro)，给用户3发送消息
INSERT INTO messages (recipient_id, sender_id, created_at, is_read, message_type, item_id, favorite_id)
VALUES (3, 2, DATE_ADD(NOW(), INTERVAL 1 HOUR), false, 'FAVORITE', 1, 2);

-- 用户2收藏了用户3的物品3 (iPhone 13)，给用户3发送消息
INSERT INTO messages (recipient_id, sender_id, created_at, is_read, message_type, item_id, favorite_id)
VALUES (3, 2, DATE_ADD(NOW(), INTERVAL 2 HOUR), false, 'FAVORITE', 3, 3);

-- 用户3收藏了用户2的物品4 (高等数学教材)，给用户2发送消息
INSERT INTO messages (recipient_id, sender_id, created_at, is_read, message_type, item_id, favorite_id)
VALUES (2, 3, DATE_ADD(NOW(), INTERVAL 3 HOUR), false, 'FAVORITE', 4, 4);

-- 插入订单消息测试数据（基于现有订单数据）
-- 订单1：用户3购买用户2的物品4 (高等数学教材)，状态2
INSERT INTO messages (recipient_id, sender_id, created_at, is_read, message_type, order_id, status, status_text)
VALUES (2, 3, NOW(), false, 'ORDER', 1, 'CREATED', '买家已创建订单');

INSERT INTO messages (recipient_id, sender_id, created_at, is_read, message_type, order_id, status, status_text)
VALUES (3, 2, DATE_ADD(NOW(), INTERVAL 1 HOUR), false, 'ORDER', 1, 'PAID', '买家已支付');

INSERT INTO messages (recipient_id, sender_id, created_at, is_read, message_type, order_id, status, status_text)
VALUES (2, 3, DATE_ADD(NOW(), INTERVAL 2 HOUR), false, 'ORDER', 1, 'SHIPPING', '卖家已发货');

-- 订单2：用户2购买用户3的物品7 (Nike运动鞋)，状态1
INSERT INTO messages (recipient_id, sender_id, created_at, is_read, message_type, order_id, status, status_text)
VALUES (3, 2, DATE_ADD(NOW(), INTERVAL 1 DAY), false, 'ORDER', 2, 'CREATED', '买家已创建订单');

INSERT INTO messages (recipient_id, sender_id, created_at, is_read, message_type, order_id, status, status_text)
VALUES (2, 3, DATE_ADD(NOW(), INTERVAL 1 DAY), false, 'ORDER', 2, 'PAID', '买家已支付'); 