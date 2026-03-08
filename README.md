# DevPortal - 个人开发者门户网站

一个精美的个人程序员作品集门户，展示所有开发的应用，支持跳转到各子系统。

## 🚀 技术栈

| 层级 | 技术 |
|------|------|
| **后端** | JDK 1.8 + Spring Boot 2.7.x + Spring Security |
| **ORM** | Spring Data JPA + Hibernate |
| **数据库** | H2（开发）/ MySQL 8.0（生产）|
| **前端** | Vue 3 + Vite + Pinia + Vue Router |
| **UI组件** | Element Plus 2.x |
| **部署** | Nginx 反向代理 + Docker Compose（可选）|

---

## 📁 项目结构

```
webapp/
├── portal-backend/          # Spring Boot 后端
│   ├── src/
│   │   ├── main/java/com/portal/
│   │   │   ├── controller/  # API 控制器
│   │   │   ├── service/     # 业务逻辑
│   │   │   ├── repository/  # 数据访问层
│   │   │   ├── entity/      # 实体类
│   │   │   ├── dto/         # 数据传输对象
│   │   │   └── config/      # 配置类
│   │   └── resources/
│   │       ├── application.properties
│   │       └── data.sql     # 初始化数据
│   └── Dockerfile
│
├── portal-frontend/         # Vue 3 前端
│   ├── src/
│   │   ├── views/           # 页面组件
│   │   ├── components/      # 公共组件
│   │   ├── api/             # API封装
│   │   ├── router/          # 路由配置
│   │   └── store/           # Pinia状态管理
│   └── vite.config.js
│
├── nginx/
│   └── nginx.conf           # Nginx配置
├── deploy.sh                # 一键部署脚本
└── docker-compose.yml       # Docker Compose
```

---

## ✨ 功能清单

### 门户前端
- [x] **首页 Hero 区域**：炫酷渐变背景、统计数据展示
- [x] **分类筛选**：快速按分类过滤项目
- [x] **精选项目**：突出展示重点项目
- [x] **项目卡片**：Hover 交互、状态标识、技术栈标签
- [x] **全部项目页**：搜索 + 筛选 + 状态过滤
- [x] **项目详情页**：完整信息、访问统计
- [x] **管理后台**：项目/分类的增删改查
- [x] **响应式设计**：PC 和移动端适配
- [x] **暗色主题**：现代科技风格 UI

### 后端 API
- [x] 项目查询（全部/分类/搜索/精选）
- [x] 项目跳转（自动记录访问量）
- [x] 统计接口
- [x] 管理员 CRUD（Basic Auth 保护）
- [x] 全局异常处理
- [x] 跨域配置

---

## 🔌 API 接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/projects` | 获取项目列表（支持筛选） | 公开 |
| GET | `/api/projects?keyword=xxx` | 搜索项目 | 公开 |
| GET | `/api/projects?categoryId=1` | 按分类筛选 | 公开 |
| GET | `/api/projects?featured=true` | 获取精选项目 | 公开 |
| GET | `/api/projects/{id}` | 获取项目详情 | 公开 |
| POST | `/api/projects/{id}/visit` | 记录访问/获取跳转URL | 公开 |
| GET | `/api/projects/stats` | 统计数据 | 公开 |
| POST | `/api/projects` | 创建项目 | 管理员 |
| PUT | `/api/projects/{id}` | 更新项目 | 管理员 |
| DELETE | `/api/projects/{id}` | 删除项目 | 管理员 |
| GET | `/api/categories` | 获取分类列表 | 公开 |
| POST | `/api/categories` | 创建分类 | 管理员 |

---

## 🚀 快速启动

### 后端启动（JDK 1.8 + Maven）

```bash
cd portal-backend
mvn spring-boot:run
# 访问: http://localhost:8080/api/projects
# H2控制台: http://localhost:8080/api/h2-console
```

### 前端启动（Node.js 16+）

```bash
cd portal-frontend
npm install
npm run dev
# 访问: http://localhost:5173
```

### 一键构建部署

```bash
chmod +x deploy.sh
./deploy.sh
```

### Docker Compose 部署

```bash
# 构建前端
cd portal-frontend && npm run build && cd ..

# 启动所有服务
docker-compose up -d

# 查看日志
docker-compose logs -f
```

---

## ⚙️ Nginx 部署配置

```
portal.example.com     →  门户主站（Vue前端 + /api/* 代理到8080）
task.example.com       →  子系统A：任务管理（端口9001）
chart.example.com      →  子系统B：数据可视化（端口9002）
api-manager.example.com → 子系统C：接口管理（端口9003）
```

**配置文件**：`nginx/nginx.conf`

---

## 🔑 管理员账户

| 字段 | 默认值 |
|------|--------|
| 用户名 | `admin` |
| 密码 | `admin123` |
| 修改位置 | `application.properties` |

> ⚠️ 生产环境请修改默认密码！

---

## 🏗️ 新增子系统步骤

1. 部署子系统到独立端口（如 9007）
2. 在 `nginx/nginx.conf` 中添加对应 server 块
3. 在管理后台添加项目，填写对应 URL
4. 门户首页自动展示，点击即可跳转

---

## 📊 数据模型

### Project（项目）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键 |
| name | String | 项目名称 |
| description | String | 项目描述 |
| url | String | 访问地址 |
| icon | String | 图标URL |
| categoryId | Long | 所属分类 |
| techStack | String | 技术栈（逗号分隔）|
| status | Enum | ACTIVE/MAINTENANCE/OFFLINE |
| isFeatured | Boolean | 是否精选 |
| visitCount | Long | 访问次数 |
| githubUrl | String | GitHub地址 |
| tags | String | 标签（逗号分隔）|

### ProjectCategory（分类）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键 |
| name | String | 分类名称 |
| icon | String | 图标 |
| sortOrder | Integer | 排序 |

---

## 📞 生产环境切换 MySQL

修改 `portal-backend/src/main/resources/application.properties`：

```properties
# 注释 H2 相关配置，取消注释 MySQL 配置
spring.datasource.url=jdbc:mysql://localhost:3306/portal_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
```
