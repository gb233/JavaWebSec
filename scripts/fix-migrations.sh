#!/bin/bash
# 修复所有迁移脚本的 PREPARE/EXECUTE 语法错误

MIGRATIONS_DIR="scripts/migrations"

echo "开始修复迁移脚本..."

for file in $(find "$MIGRATIONS_DIR" -name "*.sql" -type f | sort); do
    echo "处理: $file"
    
    # 检查文件是否包含 PREPARE stmt FROM
    if grep -q "PREPARE stmt FROM" "$file"; then
        echo "  发现 PREPARE/EXECUTE 语法，需要修复"
        # 这里可以添加自动修复逻辑，但为了安全，我们手动修复
    fi
done

echo "修复完成！请手动检查并修复包含 PREPARE/EXECUTE 的迁移脚本"

