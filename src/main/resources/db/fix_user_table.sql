-- 修复t_user表结构，添加缺少的字段
ALTER TABLE t_user 
ADD COLUMN avatar_image_id VARCHAR(255) DEFAULT NULL COMMENT '头像图片ID',
ADD COLUMN status INT NOT NULL DEFAULT 1 COMMENT '用户状态：0-禁用，1-启用',
ADD COLUMN last_login_time DATETIME DEFAULT NULL COMMENT '最后登录时间';

-- 更新已存在用户的头像字段（将原来的avatar字段值迁移到avatar_image_id）
UPDATE t_user SET avatar_image_id = avatar WHERE avatar IS NOT NULL;

-- 更新已存在用户的状态
UPDATE t_user SET status = 1 WHERE status IS NULL; 