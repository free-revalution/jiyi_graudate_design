#!/bin/bash
set -e

echo "=========================================="
echo "  在线课程教育实验平台 - 一键部署脚本"
echo "=========================================="

# Check Docker
if ! command -v docker &> /dev/null; then
    echo "[1/4] 安装 Docker..."
    curl -fsSL https://get.docker.com | sh
    sudo systemctl start docker
    sudo systemctl enable docker
    echo "Docker 安装完成"
else
    echo "[1/4] Docker 已安装，跳过安装"
fi

# Check Docker Compose
if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null 2>&1; then
    echo "[2/4] 安装 Docker Compose..."
    sudo apt-get update -qq
    sudo apt-get install -y docker-compose-plugin
    echo "Docker Compose 安装完成"
else
    echo "[2/4] Docker Compose 已安装，跳过安装"
fi

# Ensure buildx is available (needed for cross-context Dockerfile references)
echo "[3/4] 检查 Docker Buildx..."
if ! docker buildx version &> /dev/null 2>&1; then
    mkdir -p ~/.docker/cli-plugins
    curl -sSL https://github.com/docker/buildx/releases/download/v0.12.0/buildx-v0.12.0.linux-amd64 -o ~/.docker/cli-plugins/docker-buildx
    chmod +x ~/.docker/cli-plugins/docker-buildx
fi
docker buildx install 2>/dev/null || true
echo "Docker Buildx 就绪"

# Build and start
echo "[4/4] 构建并启动服务..."
cd "$(dirname "$0")"
docker compose down 2>/dev/null || true
docker compose up -d --build

echo ""
echo "=========================================="
echo "  部署完成!"
echo "=========================================="
echo ""
echo "  用户前端:  http://localhost"
echo "  管理后台:  http://localhost/admin/"
echo "  API文档:  http://localhost/swagger-ui.html"
echo ""
echo "  测试账号:"
echo "    管理员: admin / admin123"
echo "    教  师: T001 / 123456 (或 20240001/123456)"
echo "    学  生: 20240001 / 123456"
echo ""
