#!/bin/bash

echo "=========================================="
echo "   宿舍管理系统 Docker Compose 启动"
echo "=========================================="
echo ""

# 停止并删除旧容器
echo "1. 清理旧容器..."
docker-compose down

# 构建镜像
echo ""
echo "2. 构建Docker镜像..."
docker-compose build

# 启动服务
echo ""
echo "3. 启动服务..."
docker-compose up -d

# 等待服务启动
echo ""
echo "4. 等待服务启动（30秒）..."
sleep 30

# 检查服务状态
echo ""
echo "5. 服务状态:"
docker-compose ps

echo ""
echo "=========================================="
echo "   启动完成！"
echo "=========================================="
echo ""
echo "访问地址:"
echo "  PC管理后台: http://localhost:3000"
echo "  H5移动端:  http://localhost:3001"
echo "  后端API:    http://localhost:8080/api"
echo "  MySQL:      localhost:3306"
echo ""
echo "默认账号:"
echo "  管理员: admin / password"
echo "  学生:   stu001 / password"
echo ""
echo "查看日志: docker-compose logs -f"
echo "停止服务: docker-compose down"
echo "=========================================="
