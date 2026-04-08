# Docker 部署说明

## 快速启动（推荐）

### 方式一：仅启动数据库服务

```bash
# 启动MySQL数据库
docker-compose -f docker-compose.simple.yml up -d

# 查看服务状态
docker-compose -f docker-compose.simple.yml ps

# 查看日志
docker-compose -f docker-compose.simple.yml logs -f mysql
```

### 方式二：完整Docker部署（需要Docker Hub权限）

```bash
# 登录Docker Hub（需要账号）
docker login

# 构建并启动所有服务
docker-compose up -d --build

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

## 服务访问地址

启动成功后，可以访问以下地址：

- **PC管理后台**: http://localhost:3000
- **H5移动端**: http://localhost:3001
- **后端API**: http://localhost:8080/api
- **Swagger文档**: http://localhost:8080/api/swagger-ui.html
- **MySQL**: localhost:3306
- **Redis** (可选): localhost:6379

## 默认账号

### 管理员账号
- 用户名: `admin`
- 密码: `password`

### 学生账号
- 用户名: `stu001`
- 密码: `password`

### 宿管账号
- 用户名: `manager001`
- 密码: `password`

## 常用命令

```bash
# 停止所有服务
docker-compose down

# 停止并删除数据卷
docker-compose down -v

# 重启某个服务
docker-compose restart backend

# 查看某个服务日志
docker-compose logs -f backend

# 进入MySQL容器
docker exec -it dormitory-mysql mysql -uroot -p123456 dormitory_db

# 备份MySQL数据
docker exec dormitory-mysql mysqldump -uroot -p123456 dormitory_db > backup.sql

# 恢复MySQL数据
docker exec -i dormitory-mysql mysql -uroot -p123456 dormitory_db < backup.sql
```

## 数据持久化

MySQL数据存储在Docker卷中：
- 卷名: `宿舍管理系统_mysql_data`
- 位置: `/var/lib/docker/volumes/宿舍管理系统_mysql_data/_data`

查看卷信息：
```bash
docker volume inspect 宿舍管理系统_mysql_data
```

## 网络配置

所有服务使用桥接网络：
- 网络名: `宿舍管理系统_dormitory-network`
- 服务间可以通过容器名互相访问

## 环境变量配置

复制 `.env.example` 为 `.env` 并修改配置：

```bash
cp .env.example .env
# 编辑 .env 文件
```

## 故障排查

### 1. MySQL连接失败

```bash
# 检查MySQL是否启动
docker-compose ps mysql

# 查看MySQL日志
docker-compose logs mysql

# 测试连接
docker exec -it dormitory-mysql mysql -uroot -p123456
```

### 2. 后端启动失败

```bash
# 查看后端日志
docker-compose logs backend

# 检查后端健康状态
curl http://localhost:8080/api/auth/login -X POST -H "Content-Type: application/json" -d '{"username":"admin","password":"password"}'
```

### 3. 前端无法访问后端

```bash
# 检查网络连接
docker network ls
docker network inspect 宿舍管理系统_dormitory-network

# 检查nginx配置
docker exec dormitory-admin cat /etc/nginx/conf.d/default.conf
```

## 生产环境建议

1. **修改默认密码**
   - 修改 `.env` 中的数据库密码
   - 修改后端JWT密钥

2. **启用HTTPS**
   - 配置SSL证书
   - 使用nginx反向代理

3. **启用Redis缓存**
   ```bash
   docker-compose --profile redis up -d
   ```

4. **数据备份**
   ```bash
   # 定期备份
   docker exec dormitory-mysql mysqldump -uroot -p123456 dormitory_db > backup_$(date +%Y%m%d).sql
   ```

5. **日志管理**
   - 配置日志驱动
   - 集中日志收集

## 开发环境

### 本地开发 + Docker数据库

```bash
# 只启动MySQL
docker-compose -f docker-compose.simple.yml up -d

# 本地启动后端
cd backend
mvn spring-boot:run

# 本地启动前端
cd frontend/admin
npm run dev

cd frontend/h5
npm run dev
```

这种方式适合开发调试，代码修改即时生效。
