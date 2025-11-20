-- =====================================================
-- 版本: V1.7.0
-- 描述: 添加业务功能相关表（订单、产品、备份表）
-- 依赖: V1.6.0
-- =====================================================

SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;
SET collation_connection = utf8mb4_unicode_ci;
SET FOREIGN_KEY_CHECKS = 0;

SET @version = 'V1.7.0';
SET @script_name = 'V1.7.0__business_tables.sql';
SET @description = '添加业务功能相关表（订单、产品、备份表）';

-- 检查版本是否已执行（修复排序规则冲突）
SELECT COUNT(*) INTO @version_exists 
FROM schema_version 
WHERE version = @version COLLATE utf8mb4_unicode_ci AND execution_status = 'SUCCESS' COLLATE utf8mb4_unicode_ci;

-- 如果版本不存在，执行迁移
SET @should_execute = IF(@version_exists = 0, 1, 0);

-- 直接执行 SQL，不使用 PREPARE/EXECUTE
-- 产品表
CREATE TABLE IF NOT EXISTS products (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '产品ID',
    name VARCHAR(255) NOT NULL COMMENT '产品名称',
    description TEXT COMMENT '产品描述',
    price DECIMAL(10,2) NOT NULL COMMENT '产品价格',
    category VARCHAR(255) COMMENT '产品分类',
    stock_quantity INT NOT NULL COMMENT '库存数量',
    is_active BIT(1) NOT NULL DEFAULT b'1' COMMENT '是否启用',
    created_at DATETIME(6) NOT NULL COMMENT '创建时间',
    updated_at DATETIME(6) COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX idx_category (category),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品表';

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    order_number VARCHAR(255) NOT NULL COMMENT '订单号',
    product_name VARCHAR(255) NOT NULL COMMENT '产品名称',
    quantity INT NOT NULL COMMENT '数量',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单价',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    amount DOUBLE NOT NULL COMMENT '金额',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '总金额',
    status VARCHAR(255) NOT NULL COMMENT '订单状态',
    receiver_name VARCHAR(255) COMMENT '收货人姓名',
    receiver_phone VARCHAR(255) COMMENT '收货人电话',
    receiver_address VARCHAR(255) COMMENT '收货地址',
    remarks VARCHAR(500) COMMENT '备注',
    created_at DATETIME(6) NOT NULL COMMENT '创建时间',
    updated_at DATETIME(6) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY UK_nthkiu7pgmnqnu86i2jyoe2v7 (order_number),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 漏洞题目备份表
CREATE TABLE IF NOT EXISTS vulnerability_questions_backup (
    id BIGINT NOT NULL DEFAULT 0 COMMENT '题目ID',
    category_code VARCHAR(10) NOT NULL COMMENT '分类代码',
    question_type VARCHAR(255) NOT NULL COMMENT '题目类型',
    difficulty VARCHAR(255) NOT NULL COMMENT '难度等级',
    knowledge_source VARCHAR(255) NOT NULL COMMENT '知识点来源',
    question_text TEXT NOT NULL COMMENT '题目内容',
    question_image VARCHAR(500) COMMENT '题目图片',
    options JSON COMMENT '选项内容',
    correct_answer TEXT NOT NULL COMMENT '正确答案',
    explanation TEXT NOT NULL COMMENT '题目解析',
    score INT NOT NULL COMMENT '题目分值',
    tags JSON COMMENT '标签',
    author_id BIGINT COMMENT '作者ID',
    status VARCHAR(255) NOT NULL COMMENT '题目状态',
    created_at DATETIME(6) NOT NULL COMMENT '创建时间',
    updated_at DATETIME(6) COMMENT '更新时间',
    INDEX idx_category_code (category_code),
    INDEX idx_question_type (question_type),
    INDEX idx_difficulty (difficulty),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='漏洞题目备份表';

SET FOREIGN_KEY_CHECKS = 1;

-- 记录迁移版本（修复字段名问题）
INSERT INTO schema_version (version, script_name, description, execution_status)
SELECT @version, @script_name, @description, 'SUCCESS'
WHERE NOT EXISTS (
    SELECT 1 FROM schema_version WHERE version = @version COLLATE utf8mb4_unicode_ci AND execution_status = 'SUCCESS' COLLATE utf8mb4_unicode_ci
);

SET FOREIGN_KEY_CHECKS = 1;
