-- 为用户表添加个人简介和所在地字段
ALTER TABLE t_user
ADD COLUMN bio VARCHAR(500) DEFAULT NULL COMMENT '个人简介',
ADD COLUMN location VARCHAR(100) DEFAULT NULL COMMENT '所在地';

-- 创建评分表
CREATE TABLE IF NOT EXISTS ratings (
  id BIGINT NOT NULL AUTO_INCREMENT,
  order_id BIGINT NOT NULL COMMENT '订单ID',
  item_id BIGINT NOT NULL COMMENT '物品ID',
  user_id BIGINT NOT NULL COMMENT '用户ID（被评价者）',
  rater_id BIGINT NOT NULL COMMENT '评价者ID',
  role VARCHAR(10) NOT NULL COMMENT '角色（BUYER/SELLER）',
  rating INT NOT NULL COMMENT '评分（1-5）',
  comment VARCHAR(500) DEFAULT NULL COMMENT '评价内容',
  create_time DATETIME DEFAULT NULL COMMENT '创建时间',
  update_time DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_user_id (user_id),
  KEY idx_item_id (item_id),
  KEY idx_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

-- 为评分表添加初始数据（示例数据）
INSERT INTO ratings (order_id, item_id, user_id, rater_id, role, rating, comment, create_time, update_time)
VALUES
  (1, 1, 2, 1, 'SELLER', 5, '卖家服务态度很好，物品和描述一致，非常满意！', NOW(), NOW()),
  (1, 1, 1, 2, 'BUYER', 5, '买家很好沟通，交易顺利，期待下次合作！', NOW(), NOW()),
  (2, 2, 3, 1, 'SELLER', 4, '物品不错，但发货有点慢。', NOW(), NOW()),
  (2, 2, 1, 3, 'BUYER', 5, '买家付款及时，很愉快的交易。', NOW(), NOW()),
  (3, 3, 1, 4, 'SELLER', 5, '卖家发货速度快，服务态度很好。', NOW(), NOW()),
  (3, 3, 4, 1, 'BUYER', 4, '买家人很好，就是有点挑剔。', NOW(), NOW()); 