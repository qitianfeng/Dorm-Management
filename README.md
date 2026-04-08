# 宿舍管理系统

一个功能完善的宿舍管理系统，包含后端API、PC端管理后台和H5移动端。

## 📸 系统截图

### PC端管理后台

| 登录页面 | 首页仪表盘 |
|:---:|:---:|
| ![登录页面](docs/screenshots/admin/01-login.png) | ![首页仪表盘](docs/screenshots/admin/02-dashboard.png) |

| 楼栋管理 | 房间管理 |
|:---:|:---:|
| ![楼栋管理](docs/screenshots/admin/03-building.png) | ![房间管理](docs/screenshots/admin/04-room.png) |

| 住宿管理 | 选宿舍配置 |
|:---:|:---:|
| ![住宿管理](docs/screenshots/admin/05-accommodation.png) | ![选宿舍配置](docs/screenshots/admin/06-selection.png) |

| 报修管理 | 卫生检查 |
|:---:|:---:|
| ![报修管理](docs/screenshots/admin/07-repair.png) | ![卫生检查](docs/screenshots/admin/08-hygiene.png) |

| 访客登记 | 缴费管理 |
|:---:|:---:|
| ![访客登记](docs/screenshots/admin/09-visitor.png) | ![缴费管理](docs/screenshots/admin/10-fee.png) |

| 违纪记录 | 公告管理 |
|:---:|:---:|
| ![违纪记录](docs/screenshots/admin/11-disciplinary.png) | ![公告管理](docs/screenshots/admin/12-announcement.png) |

| 统计报表 | 系统管理 |
|:---:|:---:|
| ![统计报表](docs/screenshots/admin/13-statistics.png) | ![用户管理](docs/screenshots/admin/14-user.png) |

| 角色管理 | 权限管理 |
|:---:|:---:|
| ![角色管理](docs/screenshots/admin/15-role.png) | ![权限管理](docs/screenshots/admin/16-permission.png) |

### H5移动端

| 首页 | 报修 |
|:---:|:---:|
| ![首页](docs/screenshots/h5/01-home.png) | ![报修](docs/screenshots/h5/02-repair.png) |

| 公告 | 我的 |
|:---:|:---:|
| ![公告](docs/screenshots/h5/03-notice.png) | ![我的](docs/screenshots/h5/04-mine.png) |

## 📋 项目概述

### 技术栈

#### 后端
- **Spring Boot 3.2.0** - 后端框架
- **Spring Security** - 安全框架
- **JWT** - 身份认证
- **Spring Data JPA** - 数据持久化
- **MySQL 8.0** - 数据库
- **Druid** - 数据库连接池
- **Lombok** - 代码简化
- **MapStruct** - 对象映射
- **SpringDoc OpenAPI** - API文档

#### 前端管理后台
- **Vue 3.4** - 前端框架
- **Vue Router 4** - 路由管理
- **Pinia** - 状态管理
- **Element Plus** - UI组件库
- **Axios** - HTTP请求
- **ECharts** - 图表库
- **Vite** - 构建工具

#### H5移动端
- **Vue 3.4** - 前端框架
- **Vant 4** - 移动端UI组件库
- **Vue Router 4** - 路由管理
- **Pinia** - 状态管理
- **Vite** - 构建工具

## 🎯 功能模块

### 1. 用户管理
- 用户登录/注册
- JWT Token认证
- 角色权限管理（管理员、宿管、学生）
- 个人信息管理

### 2. 宿舍楼管理
- 宿舍楼信息维护（CRUD）
- 楼栋类型管理（男生/女生宿舍）
- 楼层、房间数、床位数统计
- 宿管信息管理

### 3. 房间管理
- 房间信息维护（CRUD）
- 房间类型（四人间、六人间、八人间）
- 床位管理
- 房间状态（空闲、部分占用、已满、维修中）
- 设施信息管理

### 4. 学生住宿管理
- **入住管理** - 学生入住登记
- **调宿管理** - 学生宿舍调整
- **退宿管理** - 学生退宿办理
- 床位分配
- 押金管理
- 住宿记录查询

### 5. 报修管理
- 报修申请提交
- 报修进度跟踪
- 报修类型分类（水电、门窗、家具、空调等）
- 优先级设置
- 维修费用记录

### 6. 卫生检查
- 卫生检查记录
- 评分系统（0-100分）
- 评级（优秀、良好、合格、不合格）
- 检查项目记录
- 问题反馈

### 7. 访客登记
- 访客信息登记
- 来访事由记录
- 访问时间管理
- 访客状态跟踪

### 8. 门禁记录
- 进出记录管理
- 设备位置记录
- 进出结果统计

### 9. 缴费管理
- 费用类型管理（住宿费、水电费等）
- 缴费记录
- 缴费状态跟踪
- 支付方式记录

### 10. 违纪记录
- 违纪记录管理
- 处罚措施记录
- 罚款管理

### 11. 公告通知
- 公告发布
- 公告类型（通知、新闻、政策、活动）
- 优先级设置
- 目标受众设置
- 浏览统计
- 置顶功能

### 12. 统计报表
- 宿舍总体统计
- 入住率分析
- 报修类型分布
- 卫生评分趋势
- 楼栋入住情况
- 数据可视化展示

## 🚀 快速开始

### 环境要求

- **JDK 17+**
- **Maven 3.6+**
- **MySQL 8.0+**
- **Node.js 18+**
- **npm 或 yarn**

### 后端启动

1. **创建数据库**
```bash
# 登录MySQL
mysql -u root -p

# 执行数据库初始化脚本
source database/init.sql
```

2. **修改配置文件**
```bash
# 编辑 backend/src/main/resources/application.yml
# 修改数据库连接信息
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/dormitory_db?...
    username: root
    password: your_password
```

3. **启动后端服务**
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

后端服务启动成功后：
- API地址：http://localhost:8080/api
- Swagger文档：http://localhost:8080/api/swagger-ui.html
- Druid监控：http://localhost:8080/api/druid/index.html

### 前端管理后台启动

```bash
cd frontend/admin
npm install
npm run dev
```

访问地址：http://localhost:3000

**测试账号：**
| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | password |
| 学生 | stu001 | password |

### H5移动端启动

```bash
cd frontend/h5
npm install
npm run dev
```

访问地址：http://localhost:3001

## 📁 项目结构

```
宿舍管理系统/
├── backend/                    # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/dormitory/
│   │   │   │   ├── entity/    # 实体类
│   │   │   │   ├── repository/# 数据访问层
│   │   │   │   ├── service/   # 业务逻辑层
│   │   │   │   ├── controller/# 控制器层
│   │   │   │   ├── dto/       # 数据传输对象
│   │   │   │   ├── config/    # 配置类
│   │   │   │   ├── security/  # 安全配置
│   │   │   │   ├── exception/ # 异常处理
│   │   │   │   └── common/    # 公共类
│   │   │   └── resources/
│   │   │       └── application.yml # 配置文件
│   │   └── test/              # 测试代码
│   └── pom.xml                # Maven配置
│
├── frontend/                   # 前端项目
│   ├── admin/                  # PC端管理后台
│   │   ├── src/
│   │   │   ├── api/           # API接口
│   │   │   ├── assets/        # 静态资源
│   │   │   ├── components/    # 公共组件
│   │   │   ├── layouts/       # 布局组件
│   │   │   ├── router/        # 路由配置
│   │   │   ├── stores/        # 状态管理
│   │   │   ├── utils/         # 工具函数
│   │   │   └── views/         # 页面组件
│   │   ├── index.html
│   │   ├── vite.config.js
│   │   └── package.json
│   │
│   └── h5/                     # H5移动端
│       ├── src/
│       │   ├── api/           # API接口
│       │   ├── assets/        # 静态资源
│       │   ├── components/    # 公共组件
│       │   ├── router/        # 路由配置
│       │   ├── stores/        # 状态管理
│       │   ├── utils/         # 工具函数
│       │   └── views/         # 页面组件
│       ├── index.html
│       ├── vite.config.js
│       └── package.json
│
└── database/                   # 数据库脚本
    └── init.sql               # 初始化脚本
```

## 🔧 开发指南

### API接口规范

所有API接口返回统一格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1234567890
}
```

### 代码规范

- 后端遵循阿里巴巴Java开发规范
- 前端遵循Vue 3官方风格指南
- 使用ESLint和Prettier进行代码格式化

### Git提交规范

- feat: 新功能
- fix: 修复bug
- docs: 文档更新
- style: 代码格式调整
- refactor: 重构
- test: 测试相关
- chore: 构建/工具链相关

## 📊 数据库设计

系统包含13个核心数据表：

1. **sys_user** - 系统用户表
2. **sys_role** - 角色表
3. **sys_user_role** - 用户角色关联表
4. **dorm_building** - 宿舍楼表
5. **dorm_room** - 房间表
6. **dorm_accommodation** - 学生住宿记录表
7. **dorm_repair** - 报修申请表
8. **dorm_hygiene** - 卫生检查记录表
9. **dorm_visitor** - 访客登记表
10. **dorm_access_record** - 门禁记录表
11. **dorm_fee** - 缴费记录表
12. **dorm_disciplinary** - 违纪记录表
13. **dorm_announcement** - 公告通知表

## 🚢 部署说明

### Docker部署（推荐）

```bash
# 构建镜像
docker-compose build

# 启动服务
docker-compose up -d
```

### 传统部署

1. 打包后端项目
```bash
cd backend
mvn clean package
```

2. 打包前端项目
```bash
cd frontend/admin
npm run build

cd ../h5
npm run build
```

3. 部署到服务器
- 后端：使用`java -jar`命令启动
- 前端：使用Nginx托管静态文件

## 📝 更新日志

### v1.0.0 (2024-01-01)
- ✨ 初始版本发布
- ✨ 完成核心功能模块开发
- ✨ 实现用户认证授权
- ✨ 完成PC端管理后台
- ✨ 完成H5移动端

## 👥 贡献者

感谢所有为这个项目做出贡献的开发者。

## 📄 许可证

本项目采用 MIT 许可证。详见 [LICENSE](LICENSE) 文件。

## 📞 联系方式

如有问题或建议，请通过以下方式联系：

- 提交 Issue
- 发送邮件

---

**⭐ 如果这个项目对你有帮助，请给一个Star支持一下！**
