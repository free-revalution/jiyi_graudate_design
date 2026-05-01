#!/bin/bash
# 停止所有服务

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

for svc in backend user-frontend admin-frontend; do
    PID_FILE="$SCRIPT_DIR/${svc}.pid"
    if [ -f "$PID_FILE" ]; then
        PID=$(cat "$PID_FILE")
        if kill -0 "$PID" 2>/dev/null; then
            kill "$PID"
            echo "已停止 $svc (PID: $PID)"
        else
            echo "$svc (PID: $PID) 已不在运行"
        fi
        rm "$PID_FILE"
    fi
done

# 同时杀掉残留的 node 和 java 进程 (基于端口)
for PORT in 8080 3000 8848; do
    PID=$(lsof -ti :$PORT 2>/dev/null)
    if [ -n "$PID" ]; then
        kill $PID 2>/dev/null && echo "已释放端口 $PORT"
    fi
done

echo "所有服务已停止"
