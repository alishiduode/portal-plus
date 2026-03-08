#!/bin/bash
# =============================================
# 门户网站部署脚本
# 使用方法：chmod +x deploy.sh && ./deploy.sh
# =============================================

set -e

echo "=============================="
echo " DevPortal 部署脚本"
echo "=============================="

# 配置变量
APP_NAME="portal"
BACKEND_PORT=8080
FRONTEND_DIR="/var/www/portal/dist"
BACKEND_JAR="portal-backend/target/portal-backend-1.0.0.jar"

# ===== 1. 构建后端 =====
echo ""
echo ">>> [1/4] 构建 Spring Boot 后端..."
cd portal-backend
mvn clean package -DskipTests -q
cd ..
echo "✓ 后端构建完成"

# ===== 2. 构建前端 =====
echo ""
echo ">>> [2/4] 构建 Vue 前端..."
cd portal-frontend
npm install --silent
npm run build
cd ..
echo "✓ 前端构建完成"

# ===== 3. 部署前端静态文件 =====
echo ""
echo ">>> [3/4] 部署前端静态文件到 $FRONTEND_DIR..."
mkdir -p $FRONTEND_DIR
cp -r portal-frontend/dist/* $FRONTEND_DIR/
echo "✓ 前端文件部署完成"

# ===== 4. 重启后端服务 =====
echo ""
echo ">>> [4/4] 重启后端服务..."

# 停止旧进程
OLD_PID=$(lsof -ti:$BACKEND_PORT 2>/dev/null || echo "")
if [ -n "$OLD_PID" ]; then
    kill -9 $OLD_PID
    echo "  停止旧进程 PID: $OLD_PID"
    sleep 2
fi

# 启动新进程
nohup java -jar $BACKEND_JAR \
    --server.port=$BACKEND_PORT \
    --spring.profiles.active=prod \
    > /var/log/portal/backend.log 2>&1 &

NEW_PID=$!
echo "  启动后端进程 PID: $NEW_PID"
sleep 3

# 健康检查
if curl -s http://localhost:$BACKEND_PORT/api/projects > /dev/null; then
    echo "✓ 后端服务启动成功"
else
    echo "✗ 后端服务启动失败，请查看日志"
    exit 1
fi

# ===== 5. 重载 Nginx =====
echo ""
echo ">>> 重载 Nginx 配置..."
nginx -t && nginx -s reload
echo "✓ Nginx 重载成功"

echo ""
echo "=============================="
echo " 部署完成！"
echo " 门户地址：http://portal.example.com"
echo " API地址：http://portal.example.com/api"
echo "=============================="
