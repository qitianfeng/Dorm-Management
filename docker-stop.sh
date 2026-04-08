#!/bin/bash

echo "=========================================="
echo "   停止宿舍管理系统 Docker Compose"
echo "=========================================="
echo ""

echo "停止所有服务..."
docker-compose down

echo ""
echo "是否删除MySQL数据卷？(y/N)"
read -t 5 answer || answer="n"

if [ "$answer" = "y" ] || [ "$answer" = "Y" ]; then
    echo "删除MySQL数据卷..."
    docker volume rm dormitory_mysql_data 2>/dev/null || true
    echo "数据卷已删除"
else
    echo "保留MySQL数据卷"
fi

echo ""
echo "=========================================="
echo "   服务已停止"
echo "=========================================="
