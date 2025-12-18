# Bug提交系统

一个基于 Spring Boot + Vue 3 的 Bug 提交和管理系统。

## 项目结构

```
IdeaProjects/
├── springbook1/          # 后端项目 (Spring Boot)
│   ├── src/
│   │   └── main/
│   │       ├── java/     # Java 源代码
│   │       └── resources/ # 配置文件
│   └── pom.xml           # Maven 依赖配置
│
└── demo/                 # 前端项目 (Vue 3)
    ├── src/              # Vue 源代码
    ├── public/           # 静态资源
    └── package.json      # npm 依赖配置
```

## 技术栈

### 后端
- Spring Boot
- MyBatis
- MySQL
- JWT 认证
- 文件上传

### 前端
- Vue 3
- Element Plus
- Axios
- Vue Router
- ECharts

## 功能特性

- ✅ 用户注册/登录
- ✅ JWT Token 认证
- ✅ Bug 提交（支持文件上传）
- ✅ Bug 列表查询
- ✅ Bug 状态管理
- ✅ 用户权限管理（普通用户/管理员/超级管理员）
- ✅ 个人信息管理
- ✅ 数据统计图表

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- Node.js 16+
- MySQL 5.7+

### 后端启动

1. 配置数据库连接（`springbook1/src/main/resources/application.yml`）
2. 运行 `Springbook1Application.java`
3. 后端服务运行在 `http://localhost:8080`

### 前端启动

1. 进入 `demo` 目录
2. 安装依赖：`npm install`
3. 启动开发服务器：`npm run dev`
4. 前端服务运行在 `http://localhost:5173`

## 配置说明

### 数据库配置

修改 `springbook1/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/your_database
    username: your_username
    password: your_password
```

### 文件上传配置

修改 `springbook1/src/main/resources/application.yml`：

```yaml
file:
  upload:
    base-path: E:\IdeaProjects\files  # 文件存储路径
    access-path: /files/**            # 文件访问路径
```

## 用户角色

- **USER**: 普通用户，可以提交和查看自己的 Bug
- **ADMIN**: 管理员，可以管理所有 Bug
- **SUPER_ADMIN**: 超级管理员，拥有所有权限

## 许可证

MIT License

