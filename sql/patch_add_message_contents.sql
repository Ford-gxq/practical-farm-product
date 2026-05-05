-- =========================================================
-- 家有良田 - 留言管理模块增量补丁
-- 适用场景：你已经执行过旧版 sql/init.sql，不想重建数据库，只想新增留言功能。
-- 执行数据库：practical_farm_product
-- =========================================================

USE practical_farm_product;

CREATE TABLE IF NOT EXISTS message_contents (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '留言ID',
    name VARCHAR(50) NOT NULL COMMENT '留言人姓名',
    phone VARCHAR(30) NOT NULL COMMENT '联系电话',
    content TEXT NOT NULL COMMENT '留言内容',
    source VARCHAR(50) NOT NULL DEFAULT 'wechat_mini_program' COMMENT '来源：wechat_mini_program微信小程序，website网站',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0未处理，1已处理',
    admin_remark VARCHAR(500) DEFAULT NULL COMMENT '管理员处理备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_message_status (status),
    KEY idx_message_created_at (created_at)
) COMMENT='用户留言表';

INSERT INTO message_contents(name, phone, content, source, status, admin_remark)
SELECT '张三', '13800138000', '你好，我想采购一批土鸡蛋，请问怎么联系农户？', 'wechat_mini_program', 0, NULL
WHERE NOT EXISTS (SELECT 1 FROM message_contents WHERE phone = '13800138000' AND content LIKE '%土鸡蛋%');
