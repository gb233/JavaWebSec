#!/bin/bash

# =====================================================
# 合并所有题目SQL脚本
# =====================================================
# 用途：将所有A01-A10的题目SQL文件合并为一个文件
# 输出：scripts/all-questions.sql

OUTPUT_FILE="scripts/all-questions.sql"
TEMP_DIR=$(mktemp -d)

echo "-- =====================================================" > "$OUTPUT_FILE"
echo "-- Java Web安全教学系统 - 所有题目数据" >> "$OUTPUT_FILE"
echo "-- =====================================================" >> "$OUTPUT_FILE"
echo "-- 生成时间: $(date '+%Y-%m-%d %H:%M:%S')" >> "$OUTPUT_FILE"
echo "-- 说明: 包含A01-A10所有分类的题目数据（共1000题）" >> "$OUTPUT_FILE"
echo "-- =====================================================" >> "$OUTPUT_FILE"
echo "" >> "$OUTPUT_FILE"

echo "SET FOREIGN_KEY_CHECKS = 0;" >> "$OUTPUT_FILE"
echo "SET NAMES utf8mb4;" >> "$OUTPUT_FILE"
echo "" >> "$OUTPUT_FILE"

# 按顺序合并A01-A10的题目文件
for i in {01..10}; do
    FILE="scripts/a${i}_complete_questions.sql"
    if [ -f "$FILE" ]; then
        echo "-- =====================================================" >> "$OUTPUT_FILE"
        echo "-- A${i} 题目数据" >> "$OUTPUT_FILE"
        echo "-- =====================================================" >> "$OUTPUT_FILE"
        echo "" >> "$OUTPUT_FILE"
        cat "$FILE" >> "$OUTPUT_FILE"
        echo "" >> "$OUTPUT_FILE"
        echo "-- A${i} 题目数据导入完成" >> "$OUTPUT_FILE"
        echo "" >> "$OUTPUT_FILE"
    else
        echo "警告: 文件 $FILE 不存在" >&2
    fi
done

echo "SET FOREIGN_KEY_CHECKS = 1;" >> "$OUTPUT_FILE"

echo "✅ 所有题目SQL已合并到: $OUTPUT_FILE"
echo "📊 文件大小: $(du -h "$OUTPUT_FILE" | cut -f1)"












