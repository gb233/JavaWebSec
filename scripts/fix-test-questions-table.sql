-- =====================================================
-- 修复 test_questions 表结构不匹配问题
-- 问题：数据库表中有 question_text 字段，但代码使用 question_title 和 question_content
-- =====================================================

USE security_teaching_system;

-- 1. 检查表结构
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_DEFAULT
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'security_teaching_system'
  AND TABLE_NAME = 'test_questions'
ORDER BY ORDINAL_POSITION;

-- 2. 如果存在 question_text 字段，需要迁移数据
-- 检查是否存在 question_text 字段
SET @has_question_text = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = 'security_teaching_system'
      AND TABLE_NAME = 'test_questions'
      AND COLUMN_NAME = 'question_text'
);

-- 3. 如果存在 question_text，迁移数据
SET @sql = IF(@has_question_text > 0, 
    '-- 需要迁移数据',
    '-- 不需要迁移，表结构已正确'
);

SELECT @sql AS migration_status;

-- 4. 如果存在 question_text 字段，执行以下迁移步骤：

-- 步骤1：添加新字段（如果不存在）
ALTER TABLE test_questions 
  ADD COLUMN IF NOT EXISTS question_title VARCHAR(500) NULL AFTER question_type,
  ADD COLUMN IF NOT EXISTS question_content TEXT NULL AFTER question_title;

-- 步骤2：迁移数据（如果 question_text 有数据且新字段为空）
UPDATE test_questions 
SET 
  question_title = COALESCE(question_title, question_text, ''),
  question_content = COALESCE(question_content, question_text, '')
WHERE (question_title IS NULL OR question_title = '')
   OR (question_content IS NULL OR question_content = '');

-- 步骤3：设置字段为 NOT NULL（如果数据已迁移）
-- 先检查是否有空值
SET @has_null_title = (SELECT COUNT(*) FROM test_questions WHERE question_title IS NULL OR question_title = '');
SET @has_null_content = (SELECT COUNT(*) FROM test_questions WHERE question_content IS NULL OR question_content = '');

-- 如果没有空值，设置为 NOT NULL
SET @sql_title = IF(@has_null_title = 0,
    'ALTER TABLE test_questions MODIFY COLUMN question_title VARCHAR(500) NOT NULL;',
    '-- 存在空值，无法设置为 NOT NULL，请先处理数据'
);

SET @sql_content = IF(@has_null_content = 0,
    'ALTER TABLE test_questions MODIFY COLUMN question_content TEXT NOT NULL;',
    '-- 存在空值，无法设置为 NOT NULL，请先处理数据'
);

-- 步骤4：删除旧的 question_text 字段（如果存在）
ALTER TABLE test_questions DROP COLUMN IF EXISTS question_text;

-- 5. 验证表结构
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_DEFAULT
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'security_teaching_system'
  AND TABLE_NAME = 'test_questions'
ORDER BY ORDINAL_POSITION;

-- 6. 验证数据完整性
SELECT 
    COUNT(*) AS total_rows,
    COUNT(question_title) AS has_title,
    COUNT(question_content) AS has_content,
    SUM(CASE WHEN question_title IS NULL OR question_title = '' THEN 1 ELSE 0 END) AS empty_title,
    SUM(CASE WHEN question_content IS NULL OR question_content = '' THEN 1 ELSE 0 END) AS empty_content
FROM test_questions;







