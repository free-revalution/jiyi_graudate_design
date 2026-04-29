# 登录服务 (zx_smalab_login_service)

基于 Spring Boot 3.2 的用户登录服务，支持学生/教师账号登录、密码验证、JWT Token 生成等功能。

## 技术栈

- Java 21
- Spring Boot 3.2.0
- Spring Security 6.2
- Spring Data JPA
- MySQL 8.0+
- JJWT 0.12.3
- SpringDoc OpenAPI 2.3.0

## 功能特性

- 用户账号密码登录
- 支持学生/教师身份类型
- JWT Token 认证
- Token 刷新机制
- 用户状态管理
- 登录日志记录

## 快速开始

### 环境要求

- JDK 21+
- Maven 3.8+
- MySQL 8.0+

### 数据库配置

确保 MySQL 数据库已创建：

```sql
CREATE DATABASE IF NOT EXISTS vanx_user_permiss CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 运行项目

```bash
cd zx_smalab_login_service
mvn spring-boot:run
```

### 访问接口文档

启动后访问 Swagger UI：http://localhost:8080/swagger-ui.html

## API 接口

### 登录接口

**POST** `/api/login`

请求体：
```json
{
  "school": "henu",
  "userType": "student",
  "username": "20240001",
  "password": "123456"
}
```

响应：
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "abc123def456...",
    "expiresIn": 7200,
    "userId": 1001,
    "userStatus": "1",
    "identityType": "student_id"
  }
}
```

### 刷新令牌

**POST** `/api/login/refresh`

请求体：
```json
{
  "refreshToken": "abc123def456..."
}
```

### 退出登录

**POST** `/api/login/logout`

请求头：`Authorization: Bearer <access_token>`

## 目录结构

```
src/main/java/com/example/login/
├── LoginApplication.java      # 启动类
├── config/                    # 配置类
│   ├── JwtConfig.java         # JWT配置
│   ├── SecurityConfig.java    # 安全配置
│   └── OpenApiConfig.java     # Swagger配置
├── controller/                # 控制层
│   └── LoginController.java   # 登录控制器
├── service/                   # 服务层
│   ├── LoginService.java      # 登录服务接口
│   └── impl/
│       └── LoginServiceImpl.java
├── repository/                # 数据访问层
│   └── UserAuthorizeRepository.java
├── entity/                    # 实体类
│   └── UserAuthorize.java     # 用户授权实体
├── dto/                       # 数据传输对象
│   ├── request/
│   │   ├── LoginRequest.java
│   │   └── RefreshTokenRequest.java
│   └── response/
│       ├── LoginResponse.java
│       └── ApiResponse.java
└── exception/                 # 异常处理
    ├── BusinessException.java
    └── GlobalExceptionHandler.java
```

## 测试账号

| 用户类型 | 账号 | 密码 | 用户ID |
|---------|------|------|--------|
| 学生 | 20240001 | 123456 | 1001 |
| 学生 | 20240002 | 123456 | 1002 |
| 教师 | T001 | 123456 | 2001 |
| 教师 | T002 | 123456 | 2002 |
| 手机号 | 15720801803 | 123456 | 3001 |

## 配置说明

| 配置项 | 说明 | 默认值 |
|-------|------|--------|
| server.port | 服务端口 | 8080 |
| spring.datasource.url | 数据库连接地址 | jdbc:mysql://localhost:3306/vanx_user_permiss |
| jwt.secret | JWT密钥 | zx_smalab_login_service_jwt_secret_key_2024 |
| jwt.expiration | Token过期时间(秒) | 7200 (2小时) |