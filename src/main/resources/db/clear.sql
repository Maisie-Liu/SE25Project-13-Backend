-- 删除所有表以重新建表 --
-- 关闭外键约束检查，避免删除时外键约束报错
SET FOREIGN_KEY_CHECKS = 0;

-- 删除表（按依赖关系反向顺序删除）
DROP TABLE IF EXISTS t_escrow;
DROP TABLE IF EXISTS t_order;
DROP TABLE IF EXISTS t_favorite;
DROP TABLE IF EXISTS t_item_images;
DROP TABLE IF EXISTS t_item;
DROP TABLE IF EXISTS t_category;
DROP TABLE IF EXISTS t_user_roles;
DROP TABLE IF EXISTS t_user;
DROP TABLE IF EXISTS t_comment;

-- 重新开启外键约束检查
SET FOREIGN_KEY_CHECKS = 1;