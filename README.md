# Bug管理系统

一个功能完善的 Bug 提交和管理系统，基于 Spring Boot 3 + Vue 3 + Element Plus 构建，支持多角色权限管理、文件上传、状态跟踪等功能。

## 📋 项目简介

本系统是一个企业级的 Bug 管理平台，提供完整的缺陷跟踪和管理功能。支持三种用户角色（普通用户、管理员、超级管理员），具有严格的权限控制和数据安全保障。

## 🏗️ 项目结构

```
bug/
├── springbook1/              # 后端项目 (Spring Boot 3)
│   ├── src/main/
│   │   ├── java/com/wunai/springbook1/
│   │   │   ├── config/       # 配置类（CORS、Security、Web）
│   │   │   ├── constant/     # 常量定义（角色常量）
│   │   │   ├── controller/   # 控制器层
│   │   │   │   ├── UserController.java      # 用户管理
│   │   │   │   ├── BugController.java       # Bug管理
│   │   │   │   ├── AdminController.java     # 管理员功能
│   │   │   │   ├── AnnouncementController.java  # 公告管理
│   │   │   │   ├── CategoryController.java  # 分类管理
│   │   │   │   └── FileController.java      # 文件管理
│   │   │   ├── interceptors/ # 拦截器（登录、权限）
│   │   │   ├── mapper/       # MyBatis Mapper
│   │   │   ├── pojo/         # 实体类
│   │   │   ├── service/      # 服务层
│   │   │   └── utils/        # 工具类（JWT、MD5、文件）
│   │   └── resources/
│   │       ├── application.yml  # 应用配置
│   │       └── mapper/          # MyBatis XML
│   └── pom.xml               # Maven 依赖
│
├── demo/                     # 前端项目 (Vue 3)
│   ├── src/
│   │   ├── assets/           # 静态资源
│   │   ├── components/       # Vue 组件
│   │   │   ├── management/   # 管理后台组件
│   │   │   │   ├── home.vue       # 数据统计首页
│   │   │   │   ├── bugList.vue    # Bug管理列表
│   │   │   │   ├── userList.vue   # 用户管理列表
│   │   │   │   └── notice.vue     # 公告管理
│   │   │   ├── bug_detail.vue     # Bug详情
│   │   │   ├── bug_list.vue       # Bug列表
│   │   │   ├── SubmitBug.vue      # 提交Bug
│   │   │   ├── userHome.vue       # 用户首页
│   │   │   └── userInfo.vue       # 个人信息
│   │   ├── router/           # 路由配置
│   │   ├── utils/            # 工具类（Axios）
│   │   ├── views/            # 页面视图
│   │   │   ├── login.vue          # 登录页
│   │   │   ├── Register.vue       # 注册页
│   │   │   ├── Home.vue           # 用户主页
│   │   │   ├── Manager.vue        # 管理后台
│   │   │   └── 404.vue            # 404页面
│   │   ├── App.vue           # 根组件
│   │   └── main.js           # 入口文件
│   ├── package.json          # npm 依赖
│   └── vite.config.js        # Vite 配置
│
├── files/                    # 文件上传目录
│   └── document_files/       # Bug附件存储
│
├── big_event.sql             # 数据库初始化脚本
└── README.md                 # 项目文档
```

## 🚀 技术栈

### 后端技术
- **Spring Boot 3.5.6** - 核心框架
- **Spring Security** - 安全框架
- **MyBatis 3.0.5** - ORM 框架
- **MySQL 8.0** - 数据库
- **JWT (java-jwt 4.4.0)** - Token 认证
- **Hutool 5.8.16** - Java 工具库
- **PageHelper 1.4.6** - 分页插件
- **Lombok** - 简化代码
- **Maven** - 项目管理

### 前端技术
- **Vue 3.5.22** - 渐进式框架
- **Element Plus 2.11.5** - UI 组件库
- **Vue Router 4.6.3** - 路由管理
- **Axios 1.13.0** - HTTP 客户端
- **ECharts 6.0.0** - 数据可视化
- **Day.js 1.11.19** - 时间处理
- **jwt-decode 4.0.0** - JWT 解析
- **Vite 7.1.11** - 构建工具

## ✨ 功能特性

### 用户功能
- ✅ **用户注册/登录** - 支持用户名密码登录，JWT Token 认证
- ✅ **个人信息管理** - 修改昵称、邮箱、头像、密码
- ✅ **Bug 提交** - 支持标题、严重程度、优先级、模块、复现步骤、附件上传（图片/视频，最多5个）
- ✅ **Bug 查询** - 查看自己提交的 Bug，支持标题搜索、严重程度筛选
- ✅ **Bug 详情** - 查看 Bug 详细信息、附件预览、状态跟踪
- ✅ **Bug 编辑** - 编辑自己提交的 Bug，支持附件增删改

### 管理员功能
- ✅ **Bug 管理** - 查看所有 Bug，按优先级排序显示
- ✅ **状态管理** - 更新 Bug 状态（待处理、处理中、已解决、已关闭、已拒绝）
- ✅ **用户管理** - 查看、编辑、删除普通用户
- ✅ **创建用户** - 创建普通用户账号（不能创建管理员）
- ✅ **角色管理** - 修改普通用户角色（不能修改管理员和超级管理员）
- ✅ **公告管理** - 发布、编辑、删除系统公告
- ✅ **数据统计** - 查看 Bug 统计图表、用户统计

### 超级管理员功能
- ✅ **完整权限** - 拥有所有管理员权限
- ✅ **管理员管理** - 创建、编辑、删除管理员账号
- ✅ **角色提升** - 将普通用户提升为管理员
- ✅ **高级删除** - 删除管理员账号（不能删除超级管理员）

### 权限控制
- ✅ **角色权限** - 三级权限体系（USER、ADMIN、SUPER_ADMIN）
- ✅ **操作限制** - 严格的权限检查，防止越权操作
- ✅ **自我保护** - 禁止修改自己的角色，防止误操作
- ✅ **超管保护** - 任何人都不能删除超级管理员账号
- ✅ **创建限制** - 禁止创建超级管理员，管理员只能创建普通用户
- ✅ **外键保护** - 删除用户前检查关联数据，防止数据丢失

### 文件管理
- ✅ **文件上传** - 支持图片、视频上传
- ✅ **文件预览** - 图片在线预览，视频播放
- ✅ **文件下载** - 支持附件下载
- ✅ **多文件支持** - 单个 Bug 最多上传 5 个附件
- ✅ **附件编辑** - 编辑 Bug 时保留原有附件，支持增删改

### 数据展示
- ✅ **分页查询** - 所有列表支持分页
- ✅ **搜索过滤** - 支持标题搜索、角色筛选、严重程度筛选
- ✅ **排序显示** - Bug 按优先级排序（高优先级在前）
- ✅ **时间格式化** - 统一的时间显示格式
- ✅ **状态标签** - 彩色标签显示状态、严重程度、优先级
- ✅ **数据统计** - ECharts 图表展示统计数据

## 🔐 权限规则

### 用户角色权限对比

| 功能 | 普通用户 | 管理员 | 超级管理员 |
|------|---------|--------|-----------|
| 提交 Bug | ✅ | ✅ | ✅ |
| 查看自己的 Bug | ✅ | ✅ | ✅ |
| 编辑自己的 Bug | ✅ | ✅ | ✅ |
| 查看所有 Bug | ❌ | ✅ | ✅ |
| 更新 Bug 状态 | ❌ | ✅ | ✅ |
| 删除 Bug | ❌ | ✅ | ✅ |
| 查看用户列表 | ❌ | ✅ | ✅ |
| 创建普通用户 | ❌ | ✅ | ✅ |
| 创建管理员 | ❌ | ❌ | ✅ |
| 编辑普通用户 | ❌ | ✅ | ✅ |
| 编辑管理员 | ❌ | ❌ | ✅ |
| 删除普通用户 | ❌ | ✅ | ✅ |
| 删除管理员 | ❌ | ❌ | ✅ |
| 删除超级管理员 | ❌ | ❌ | ❌ |
| 修改自己角色 | ❌ | ❌ | ❌ |
| 管理公告 | ❌ | ✅ | ✅ |

### 角色修改规则

| 操作者 \ 目标用户 | 普通用户 | 管理员 | 超级管理员 | 自己 |
|-------------------|---------|--------|-----------|------|
| **普通用户** | ❌ | ❌ | ❌ | ❌ |
| **管理员** | ✅ | ❌ | ❌ | ❌ |
| **超级管理员** | ✅ | ✅ | ❌ | ❌ |

**说明：**
- ✅ 可以操作
- ❌ 不能操作
- 任何人都不能修改自己的角色（包括超级管理员）
- 任何人都不能创建或设置为超级管理员

## 📦 快速开始

### 环境要求

- **JDK 17+** - Java 开发环境
- **Maven 3.6+** - 项目构建工具
- **Node.js 20.19.0+ 或 22.12.0+** - 前端运行环境
- **MySQL 8.0+** - 数据库
- **Git** - 版本控制

### 数据库初始化

1. 创建数据库：
```sql
CREATE DATABASE big_event CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
```

2. 导入数据库脚本：
```bash
mysql -u root -p big_event < big_event.sql
```

3. 数据库包含以下表：
   - `user` - 用户表
   - `bug` - Bug 表
   - `announcement` - 公告表
   - `category` - 分类表
   - `article` - 文章表

### 后端启动

1. **配置数据库连接**

编辑 `springbook1/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/big_event?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
```

2. **配置文件上传路径**

```yaml
file:
  upload:
    base-path: E:\bug\files  # 修改为你的文件存储路径
    access-path: /files/**
```

3. **启动后端服务**

方法一：使用 IDE
- 打开 `springbook1` 项目
- 运行 `Springbook1Application.java` 主类

方法二：使用 Maven
```bash
cd springbook1
mvn clean install
mvn spring-boot:run
```

4. **验证启动**
- 后端服务运行在 `http://localhost:8080`
- 访问 `http://localhost:8080/user/userInfo` 测试接口

### 前端启动

1. **安装依赖**

```bash
cd demo
npm install
```

2. **启动开发服务器**

```bash
npm run dev
```

3. **验证启动**
- 前端服务运行在 `http://localhost:5173`
- 浏览器自动打开登录页面

### 默认账号

系统提供以下测试账号：

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| pppppp | 123456 | SUPER_ADMIN | 超级管理员 |
| test123 | test123 | ADMIN | 管理员 |
| ling0 | test123 | USER | 普通用户 |

## 🔧 配置说明

### 后端配置

**application.yml 主要配置项：**

```yaml
server:
  port: 8080  # 服务端口

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/big_event
    username: root
    password: your_password
  
  servlet:
    multipart:
      max-file-size: 10MB      # 单个文件最大大小
      max-request-size: 50MB   # 请求最大大小

mybatis:
  configuration:
    map-underscore-to-camel-case: true  # 下划线转驼峰

file:
  upload:
    base-path: E:\bug\files    # 文件存储路径
    access-path: /files/**     # 文件访问路径
```

### 前端配置

**vite.config.js 代理配置：**

```javascript
export default defineConfig({
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
})
```

**axios 基础配置：**

```javascript
// demo/src/utils/axios.js
const instance = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000
})
```

## 📊 数据库设计

### 核心表结构

**user 表（用户表）**
```sql
- id: 用户ID（主键）
- username: 用户名（唯一）
- password: 密码（MD5加密）
- nickname: 昵称
- email: 邮箱
- user_pic: 头像URL
- role: 角色（USER/ADMIN/SUPER_ADMIN）
- create_time: 创建时间
- update_time: 更新时间
```

**bug 表（Bug表）**
```sql
- id: Bug ID（主键）
- title: Bug标题
- severity: 严重程度（1-一般，2-严重，3-致命）
- priority: 优先级（1-低，2-中，3-高）
- module: 所属模块（1-前端，2-后端，3-数据库，4-其他）
- user_id: 提交人ID（外键）
- handler_id: 处理人ID（外键）
- status: 状态（0-待处理，1-处理中，2-已解决，3-已关闭，4-已拒绝）
- remark: 复现步骤
- document: 附件URL（多个用逗号分隔）
- feedback: 处理反馈
- create_time: 创建时间
- update_time: 更新时间
```

## 🎯 API 接口

### 用户接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | /user/register | 用户注册 | 公开 |
| POST | /user/login | 用户登录 | 公开 |
| GET | /user/userInfo | 获取用户信息 | 登录 |
| PUT | /user/update | 更新个人信息 | 登录 |
| POST | /user/admin/update | 管理员更新用户 | 管理员 |
| POST | /user/admin/add | 管理员创建用户 | 管理员 |
| DELETE | /user/delete/{id} | 删除用户 | 管理员 |
| DELETE | /user/delete/batch | 批量删除用户 | 管理员 |
| POST | /user/all | 查询用户列表 | 管理员 |

### Bug 接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | /bugs/submit | 提交Bug | 登录 |
| GET | /bugs/bugAll | 查询所有Bug | 登录 |
| GET | /bugs/{id} | 查询Bug详情 | 登录 |
| PUT | /bugs/edit | 编辑Bug | 登录 |
| DELETE | /bugs/deleteId/{id} | 删除Bug | 管理员 |
| DELETE | /bugs/deleteIds | 批量删除Bug | 管理员 |
| PATCH | /bugs/{id}/status | 更新Bug状态 | 管理员 |

### 文件接口

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | /files/upload | 上传文件 | 登录 |
| GET | /files/download/{filename} | 下载文件 | 公开 |
| GET | /files/download/document_files/{filename} | 下载Bug附件 | 公开 |

## 🐛 已修复的问题

1. ✅ **用户信息更新CORS错误** - 统一CORS配置，解决跨域问题
2. ✅ **Bug附件丢失问题** - 编辑时保留原有附件
3. ✅ **管理员编辑用户问题** - 优化参数验证，支持部分字段更新
4. ✅ **角色修改权限问题** - 严格的角色修改权限控制
5. ✅ **删除用户权限漏洞** - 防止管理员删除其他管理员
6. ✅ **删除用户外键约束问题** - 友好的错误提示
7. ✅ **管理员创建用户验证规则** - 统一注册和创建的验证规则
8. ✅ **管理员创建用户权限限制** - 管理员只能创建普通用户
9. ✅ **禁止修改自己角色** - 防止误操作导致权限丢失
10. ✅ **Bug优先级排序** - 高优先级Bug优先显示

## 📝 开发规范

### 代码规范
- 后端遵循阿里巴巴 Java 开发手册
- 前端遵循 Vue 3 官方风格指南
- 使用 Lombok 简化 Java 代码
- 使用 ESLint 检查前端代码

### 命名规范
- 数据库：下划线命名（user_name）
- Java：驼峰命名（userName）
- 前端：驼峰命名（userName）
- 常量：大写下划线（USER_ROLE）

### Git 提交规范
- feat: 新功能
- fix: 修复bug
- docs: 文档更新
- style: 代码格式调整
- refactor: 重构
- test: 测试
- chore: 构建/工具变动

## 🚧 待开发功能

- [ ] Bug 评论功能
- [ ] Bug 关联功能
- [ ] 邮件通知
- [ ] 数据导出（Excel）
- [ ] 操作日志
- [ ] 数据备份
- [ ] 移动端适配
- [ ] 暗黑模式

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

## 📄 许可证

MIT License

Copyright (c) 2025

## 📧 联系方式

如有问题或建议，欢迎联系：
- 项目地址：[GitHub Repository]

---

**注意事项：**
1. 首次启动前请确保数据库已正确配置
2. 文件上传路径需要有写入权限
3. 生产环境请修改 JWT 密钥和数据库密码
4. 建议定期备份数据库
5. 重启后端服务后所有修改才能生效

