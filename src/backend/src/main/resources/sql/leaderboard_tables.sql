-- 排行榜功能暂时注释掉 - 2025-01-15
-- 挑战排行榜表
-- CREATE TABLE IF NOT EXISTS challenge_leaderboard (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    category_code VARCHAR(10) DEFAULT NULL COMMENT '分类代码（A01, A02等，NULL表示总体）',
    total_score INT UNSIGNED DEFAULT 0 COMMENT '总积分',
    total_challenges INT UNSIGNED DEFAULT 0 COMMENT '总挑战次数',
    success_count INT UNSIGNED DEFAULT 0 COMMENT '成功次数',
    success_rate DECIMAL(5,2) DEFAULT 0.00 COMMENT '成功率（百分比）',
    current_rank INT UNSIGNED DEFAULT NULL COMMENT '当前排名',
    last_challenge_at DATETIME DEFAULT NULL COMMENT '最后挑战时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引
    INDEX idx_user_id (user_id),
    INDEX idx_category_code (category_code),
    INDEX idx_total_score (total_score DESC),
    INDEX idx_success_rate (success_rate DESC),
    INDEX idx_updated_at (updated_at),
    
    -- 唯一约束
    UNIQUE KEY uk_user_category (user_id, category_code),
    
    -- 外键约束
    CONSTRAINT fk_leaderboard_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='挑战排行榜';

-- 挑战排行榜历史记录表（用于记录排名变化）
-- CREATE TABLE IF NOT EXISTS challenge_leaderboard_history (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    category_code VARCHAR(10) DEFAULT NULL COMMENT '分类代码',
    rank_change INT DEFAULT 0 COMMENT '排名变化（正数为上升，负数为下降）',
    score_change INT DEFAULT 0 COMMENT '积分变化',
    change_reason VARCHAR(100) DEFAULT NULL COMMENT '变化原因',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    -- 索引
    INDEX idx_user_id (user_id),
    INDEX idx_category_code (category_code),
    INDEX idx_created_at (created_at),
    
    -- 外键约束
    CONSTRAINT fk_leaderboard_history_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='挑战排行榜历史记录';
*/
