@echo off
REM =====================================================
REM 数据库完整初始化脚本（Windows）
REM =====================================================
REM 用途：一次性导入所有数据库内容（表结构 + 1000题 + 挑战场景）
REM 使用方法：scripts\init-all-questions.bat
REM =====================================================

setlocal enabledelayedexpansion

REM 配置（可根据实际情况修改）
if "%DB_HOST%"=="" set DB_HOST=localhost
if "%DB_PORT%"=="" set DB_PORT=3306
if "%DB_USER%"=="" set DB_USER=root
if "%DB_PASSWORD%"=="" set DB_PASSWORD=root
if "%DB_NAME%"=="" set DB_NAME=security_teaching_system

echo ========================================
echo 数据库完整初始化脚本
echo ========================================
echo.

REM 检查MySQL命令是否存在
where mysql >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未找到mysql命令，请先安装MySQL客户端
    echo 请确保MySQL的bin目录已添加到PATH环境变量
    pause
    exit /b 1
)

REM 提示输入数据库密码（如果未设置）
if "%DB_PASSWORD%"=="root" (
    set /p PASSWORD_INPUT="请输入MySQL root密码（直接回车使用默认值：root）："
    if not "!PASSWORD_INPUT!"=="" set DB_PASSWORD=!PASSWORD_INPUT!
)

echo 数据库配置：
echo   主机: %DB_HOST%
echo   端口: %DB_PORT%
echo   用户: %DB_USER%
echo   数据库: %DB_NAME%
echo.

REM 测试数据库连接
echo [1/14] 测试数据库连接...
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% -e "SELECT 1;" >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 无法连接到MySQL数据库，请检查配置
    pause
    exit /b 1
)
echo [成功] 数据库连接成功
echo.

REM 创建数据库（如果不存在）
echo [2/14] 创建数据库（如果不存在）...
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% -e "CREATE DATABASE IF NOT EXISTS `%DB_NAME%` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>nul
echo [成功] 数据库创建完成
echo.

REM 导入表结构
echo [3/14] 导入表结构...
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% %DB_NAME% < scripts\init-db.sql
if %errorlevel% neq 0 (
    echo [错误] 表结构导入失败
    pause
    exit /b 1
)
echo [成功] 表结构导入完成
echo.

REM 导入A01-A10题目数据
echo [4/14] 导入A01题目数据（100题）...
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% %DB_NAME% < scripts\a01_complete_questions.sql
echo [成功] A01题目导入完成

echo [5/14] 导入A02题目数据（100题）...
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% %DB_NAME% < scripts\a02_complete_questions.sql
echo [成功] A02题目导入完成

echo [6/14] 导入A03题目数据（100题）...
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% %DB_NAME% < scripts\a03_complete_questions.sql
echo [成功] A03题目导入完成

echo [7/14] 导入A04题目数据（100题）...
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% %DB_NAME% < scripts\a04_complete_questions.sql
echo [成功] A04题目导入完成

echo [8/14] 导入A05题目数据（100题）...
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% %DB_NAME% < scripts\a05_complete_questions.sql
echo [成功] A05题目导入完成

echo [9/14] 导入A06题目数据（100题）...
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% %DB_NAME% < scripts\a06_complete_questions.sql
echo [成功] A06题目导入完成

echo [10/14] 导入A07题目数据（100题）...
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% %DB_NAME% < scripts\a07_complete_questions.sql
echo [成功] A07题目导入完成

echo [11/14] 导入A08题目数据（100题）...
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% %DB_NAME% < scripts\a08_complete_questions.sql
echo [成功] A08题目导入完成

echo [12/14] 导入A09题目数据（100题）...
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% %DB_NAME% < scripts\a09_complete_questions.sql
echo [成功] A09题目导入完成

echo [13/14] 导入A10题目数据（100题）...
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% %DB_NAME% < scripts\a10_complete_questions.sql
echo [成功] A10题目导入完成
echo.

REM 导入挑战场景数据
echo [14/14] 导入挑战场景数据...
if exist scripts\challenge-scenarios-init.sql (
    mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% %DB_NAME% < scripts\challenge-scenarios-init.sql
    echo [成功] 挑战场景数据导入完成
) else (
    echo [警告] 挑战场景脚本不存在，跳过
)
echo.

REM 验证数据导入
echo 验证数据导入...
for /f "tokens=1" %%a in ('mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% %DB_NAME% -N -e "SELECT COUNT(*) FROM vulnerability_questions;" 2^>nul') do set QUESTION_COUNT=%%a
echo 题目总数: %QUESTION_COUNT%

if %QUESTION_COUNT% equ 1000 (
    echo [成功] 数据导入成功！共导入1000题
) else (
    echo [警告] 题目数量不正确，期望1000题，实际%QUESTION_COUNT%题
    echo [警告] 请检查导入日志
)

echo.
echo ========================================
echo 数据库初始化完成！
echo ========================================
pause

