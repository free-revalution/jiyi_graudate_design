#!/bin/bash
# ============================================================
# 在线课程教育平台 - 一键启动脚本
# 使用方法: chmod +x start.sh && ./start.sh
# ============================================================

export NVM_DIR="$HOME/.nvm"
[ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh"

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BOLD='\033[1m'
NC='\033[0m'

cleanup() {
    echo ""
    echo -e "${YELLOW}正在停止所有服务...${NC}"
    for port in 8080 3000 8848; do
        lsof -ti:$port 2>/dev/null | xargs kill -9 2>/dev/null
    done
    sleep 1
    echo -e "${GREEN}所有服务已停止${NC}"
    exit 0
}
trap cleanup INT TERM

echo "============================================"
echo -e "${BOLD}  在线课程教育平台 - 启动${NC}"
echo "============================================"
echo ""

# 释放端口
for port in 8080 3000 8848; do
    lsof -ti:$port 2>/dev/null | xargs kill -9 2>/dev/null
done
sleep 1

# 1. 后端（后台启动，不阻塞）
echo -e "${YELLOW}[1/3] 启动后端...${NC}"
cd "$SCRIPT_DIR/zx_smalab_login_service"
mvn spring-boot:run -q > /tmp/backend.log 2>&1 &
echo -e "  后端启动中，日志: /tmp/backend.log"

# 2. 用户端前端
echo -e "${YELLOW}[2/3] 启动用户端 (3000)...${NC}"
cd "$SCRIPT_DIR/zx_smalab_user_pc"
[ ! -d "node_modules" ] && npm install --registry=https://registry.npmmirror.com -s
rm -rf node_modules/.vite
npx vite --host 0.0.0.0 > /tmp/user_pc.log 2>&1 &
sleep 5
if curl -sf http://127.0.0.1:3000/ >/dev/null 2>&1; then
    echo -e "  ${GREEN}用户端已启动: http://127.0.0.1:3000${NC}"
else
    echo -e "  ${RED}用户端启动失败，查看日志: cat /tmp/user_pc.log${NC}"
fi

# 3. 管理端前端
echo -e "${YELLOW}[3/3] 启动管理端 (8848)...${NC}"
cd "$SCRIPT_DIR/zx_smalab_tea_mana_pc"
[ ! -d "node_modules" ] && npm install --registry=https://registry.npmmirror.com -s
npx vite --host 0.0.0.0 > /tmp/admin_pc.log 2>&1 &
sleep 5
if curl -sf http://127.0.0.1:8848/ >/dev/null 2>&1; then
    echo -e "  ${GREEN}管理端已启动: http://127.0.0.1:8848${NC}"
else
    echo -e "  ${RED}管理端启动失败，查看日志: cat /tmp/admin_pc.log${NC}"
fi

# 检查后端状态
echo ""
sleep 3
if curl -sf http://127.0.0.1:8080/swagger-ui.html >/dev/null 2>&1; then
    echo -e "  ${GREEN}后端已启动: http://127.0.0.1:8080${NC}"
else
    echo -e "  ${YELLOW}后端仍在启动中，请稍后刷新页面...${NC}"
fi

echo ""
echo "============================================"
echo -e "  ${BOLD}用户端:${NC}    http://127.0.0.1:3000"
echo -e "  ${BOLD}管理端:${NC}    http://127.0.0.1:8848"
echo -e "  ${BOLD}后端API:${NC}   http://127.0.0.1:8080"
echo ""
echo "  测试账号 (密码均为 123456):"
echo "    教师:  手机号 13800000001"
echo "    学生:  手机号 13900000001"
echo ""
echo -e "${YELLOW}  按 Ctrl+C 停止所有服务${NC}"
echo "============================================"
echo ""

wait
