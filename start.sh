#!/bin/bash
# ============================================================
# 在线课程教育平台 - 一键启动脚本
# 使用方法: chmod +x start.sh && ./start.sh
# ============================================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo "============================================"
echo "  在线课程教育平台 - 环境检查与启动"
echo "============================================"
echo ""

# 1. 检查环境依赖
echo -e "${YELLOW}[1/5] 检查环境依赖...${NC}"

command -v java >/dev/null 2>&1 || { echo -e "${RED}错误: 未安装 Java，请安装 JDK 17${NC}"; exit 1; }
command -v mvn >/dev/null 2>&1 || { echo -e "${RED}错误: 未安装 Maven${NC}"; exit 1; }
command -v node >/dev/null 2>&1 || { echo -e "${RED}错误: 未安装 Node.js${NC}"; exit 1; }
command -v npm >/dev/null 2>&1 || { echo -e "${RED}错误: 未安装 npm${NC}"; exit 1; }
command -v mysql >/dev/null 2>&1 || { echo -e "${RED}错误: 未安装 MySQL 客户端${NC}"; exit 1; }

JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | cut -d'.' -f1)
NODE_VERSION=$(node -v | cut -d'v' -f2 | cut -d'.' -f1)

echo -e "  Java: $(java -version 2>&1 | head -1 | tr -d '"')"
echo -e "  Maven: $(mvn -v 2>&1 | head -1 | awk '{print $3}')"
echo -e "  Node.js: $(node -v)"
echo -e "  npm: $(npm -v)"

if [ "$JAVA_VERSION" -lt 17 ] 2>/dev/null; then
    echo -e "${RED}错误: Java 版本需要 >= 17，当前: $JAVA_VERSION${NC}"
    exit 1
fi

echo -e "${GREEN}  环境检查通过!${NC}"
echo ""

# 2. 初始化 MySQL 数据库
echo -e "${YELLOW}[2/5] 初始化 MySQL 数据库...${NC}"

# 获取当前脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
SCHEMA_FILE="$SCRIPT_DIR/zx_smalab_login_service/src/main/resources/schema.sql"

if [ ! -f "$SCHEMA_FILE" ]; then
    echo -e "${RED}错误: 找不到 schema.sql: $SCHEMA_FILE${NC}"
    exit 1
fi

echo "  请输入 MySQL root 密码 (默认 123456，直接回车使用默认): "
read -s MYSQL_PWD
MYSQL_PWD=${MYSQL_PWD:-123456}

# 测试 MySQL 连接
if ! mysql -u root -p"$MYSQL_PWD" -e "SELECT 1" >/dev/null 2>&1; then
    echo -e "${RED}错误: 无法连接 MySQL，请检查密码和 MySQL 服务是否启动${NC}"
    echo -e "${YELLOW}提示: Ubuntu 上启动 MySQL: sudo systemctl start mysql${NC}"
    exit 1
fi

# 执行 schema.sql (包含 CREATE DATABASE)
echo "  正在导入数据库..."
mysql -u root -p"$MYSQL_PWD" < "$SCHEMA_FILE" 2>&1
echo -e "${GREEN}  数据库初始化完成!${NC}"
echo ""

# 3. 更新后端配置 (如果密码不是默认的 123456)
if [ "$MYSQL_PWD" != "123456" ]; then
    echo -e "${YELLOW}[提示] MySQL 密码不是默认的 123456，需要修改后端配置${NC}"
    echo "  请手动修改: zx_smalab_login_service/src/main/resources/application.yml"
    echo "  将 spring.datasource.password 改为: $MYSQL_PWD"
    echo ""
fi

# 4. 启动后端
echo -e "${YELLOW}[3/5] 启动后端服务 (Spring Boot)...${NC}"

cd "$SCRIPT_DIR/zx_smalab_login_service"

# 检查是否已安装依赖
if [ ! -d "target" ] || [ ! -f "target/*.jar" ]; then
    echo "  首次运行，正在编译后端项目 (可能需要几分钟)..."
    mvn package -DskipTests -q
fi

# 后台启动后端
JAR_FILE=$(ls target/*.jar 2>/dev/null | head -1)
if [ -z "$JAR_FILE" ]; then
    echo -e "${RED}错误: 后端编译失败，找不到 jar 文件${NC}"
    exit 1
fi

# 导出 Spring 配置
export SPRING_DATASOURCE_PASSWORD="$MYSQL_PWD"

nohup java -jar "$JAR_FILE" > "$SCRIPT_DIR/backend.log" 2>&1 &
BACKEND_PID=$!
echo "$BACKEND_PID" > "$SCRIPT_DIR/backend.pid"
echo "  后端 PID: $BACKEND_PID"
echo "  日志文件: $SCRIPT_DIR/backend.log"

# 等待后端启动
echo "  等待后端启动..."
for i in $(seq 1 30); do
    if curl -s http://127.0.0.1:8080/user_permiss/auth/password/login >/dev/null 2>&1; then
        break
    fi
    if ! kill -0 $BACKEND_PID 2>/dev/null; then
        echo -e "${RED}  后端启动失败! 请查看 backend.log${NC}"
        exit 1
    fi
    sleep 2
done

if curl -s http://127.0.0.1:8080/user_permiss/auth/password/login >/dev/null 2>&1; then
    echo -e "${GREEN}  后端启动成功! http://127.0.0.1:8080${NC}"
else
    echo -e "${YELLOW}  后端可能还在启动中，请稍后查看 backend.log${NC}"
fi
echo ""

# 5. 启动用户端前端
echo -e "${YELLOW}[4/5] 启动用户端前端...${NC}"

cd "$SCRIPT_DIR/zx_smalab_user_pc"

if [ ! -d "node_modules" ]; then
    echo "  首次运行，正在安装依赖..."
    npm install --registry=https://registry.npmmirror.com
fi

nohup npm run dev > "$SCRIPT_DIR/user-frontend.log" 2>&1 &
USER_PID=$!
echo "$USER_PID" > "$SCRIPT_DIR/user-frontend.pid"
echo "  用户端 PID: $USER_PID"
echo -e "${GREEN}  用户端启动中... 请稍等片刻访问 http://127.0.0.1:3000${NC}"
echo ""

# 6. 启动管理端前端
echo -e "${YELLOW}[5/5] 启动管理端前端...${NC}"

cd "$SCRIPT_DIR/zx_smalab_tea_mana_pc"

if [ ! -d "node_modules" ]; then
    echo "  首次运行，正在安装依赖..."
    npm install --registry=https://registry.npmmirror.com
fi

nohup npm run dev > "$SCRIPT_DIR/admin-frontend.log" 2>&1 &
ADMIN_PID=$!
echo "$ADMIN_PID" > "$SCRIPT_DIR/admin-frontend.pid"
echo "  管理端 PID: $ADMIN_PID"
echo -e "${GREEN}  管理端启动中... 请稍等片刻访问 http://127.0.0.1:8848${NC}"
echo ""

# 完成
echo "============================================"
echo -e "${GREEN}  启动完成!${NC}"
echo "============================================"
echo ""
echo "  用户端:  http://127.0.0.1:3000"
echo "  管理端:  http://127.0.0.1:8848"
echo "  后端API: http://127.0.0.1:8080"
echo ""
echo "  测试账号 (密码均为 123456):"
echo "    管理员: admin (手机号/学号登录)"
echo "    老师:   T001"
echo "    学生:   20240001"
echo ""
echo "  日志文件:"
echo "    后端:   backend.log"
echo "    用户端: user-frontend.log"
echo "    管理端: admin-frontend.log"
echo ""
echo "  停止服务: ./stop.sh"
echo "============================================"
