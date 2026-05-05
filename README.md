# 在线课程教育平台

基于 Spring Boot + Vue 3 的在线课程教育平台，包含教师管理端和学生用户端。

## 技术栈

| 模块 | 技术 |
|------|------|
| 后端 | Java 17, Spring Boot 3.2, Spring Data JPA, MySQL |
| 管理端前端 | Vue 3 + TypeScript, Vite 5, Element Plus, WangEditor |
| 用户端前端 | Vue 3 + JavaScript, Vite 4, Element Plus |

## 项目结构

```
.
├── zx_smalab_login_service/   # 后端 Spring Boot 服务
├── zx_smalab_tea_mana_pc/     # 教师管理端前端
├── zx_smalab_user_pc/         # 学生用户端前端
├── deploy/mysql/init.sql      # 数据库建表+种子数据
└── start.sh                   # 一键启动脚本
```

## 环境要求

- **Ubuntu 22.04+**（其他 Linux 发行版也行）
- **Java 17**（OpenJDK 17 即可）
- **Maven 3.8+**
- **Node.js 16+**（推荐 18 LTS）
- **MySQL 8.0+**

## 安装步骤

### 1. 安装基础环境

```bash
# 更新系统
sudo apt update && sudo apt upgrade -y

# 安装 Java 17
sudo apt install -y openjdk-17-jdk
java -version    # 确认输出 17.x.x

# 安装 Maven
sudo apt install -y maven
mvn -version     # 确认输出 3.x.x

# 安装 Node.js（推荐用 nvm）
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.7/install.sh | bash
source ~/.bashrc
nvm install 18
node -v         # 确认输出 v18.x.x

# 安装 MySQL 8.0
sudo apt install -y mysql-server
sudo systemctl start mysql
sudo systemctl enable mysql
```

### 2. 配置 MySQL 数据库

```bash
# 登录 MySQL（首次安装可能没有密码，直接回车）
sudo mysql
```

在 MySQL 命令行中执行：

```sql
-- 设置 root 密码（如果不是新安装可跳过）
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '12345678';
FLUSH PRIVILEGES;
EXIT;

-- 用新密码重新登录
mysql -u root -p
```

然后执行以下 SQL 初始化数据库：

```sql
-- 1. 创建数据库（必须手动创建，项目不再自动创建）
CREATE DATABASE IF NOT EXISTS vanx_user_permiss
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;

-- 2. 导入建表和种子数据
USE vanx_user_permiss;
SOURCE /path/to/project/deploy/mysql/init.sql;
```

> **注意**：把 `/path/to/project` 替换为你实际的项目路径。也可以用命令行导入：
> ```bash
> mysql -u root -p vanx_user_permiss < deploy/mysql/init.sql
> ```

### 3. 修改后端数据库配置（如果你的密码不是 12345678）

编辑文件 `zx_smalab_login_service/src/main/resources/application.yml`，找到数据库连接部分：

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/vanx_user_permiss?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useSSL=false&connectionCollation=utf8_general_ci
    username: root
    password: 12345678    # 改成你的 MySQL 密码
```

### 4. 修改前端 API 地址（如果前后端不在同一台机器）

如果前端和后端部署在不同机器，需要修改前端请求地址：

- 管理端：编辑 `zx_smalab_tea_mana_pc/.env.development`，将 `VITE_API_URL` 改为后端地址
- 用户端：编辑 `zx_smalab_user_pc/src/api/index.js`，找到 `baseURL` 修改为后端地址

> **同一台机器运行则不需要修改**，默认都指向 `127.0.0.1:8080`。

## 启动项目

### 方式一：一键启动（推荐）

```bash
chmod +x start.sh
./start.sh
```

脚本会自动：
1. 启动后端（端口 8080）
2. 安装前端依赖（首次运行）
3. 启动用户端（端口 3000）
4. 启动管理端（端口 8848）

按 `Ctrl+C` 停止所有服务。

### 方式二：分别启动

```bash
# 终端 1：启动后端
cd zx_smalab_login_service
mvn spring-boot:run

# 终端 2：启动用户端
cd zx_smalab_user_pc
npm install    # 首次运行需要
npm run dev

# 终端 3：启动管理端
cd zx_smalab_tea_mana_pc
npm install    # 首次运行需要
npm run dev
```

## 访问地址

| 服务 | 地址 |
|------|------|
| 用户端 | http://localhost:3000 |
| 管理端 | http://localhost:8848 |
| 后端 API | http://localhost:8080 |
| API 文档 | http://localhost:8080/swagger-ui.html |

> 如果在虚拟机中运行，用虚拟机的 IP 地址替换 `localhost`，例如 `http://192.168.x.x:3000`

## 测试账号

所有账号密码统一为 **123456**

| 角色 | 登录方式 | 账号 |
|------|----------|------|
| 管理员 | 手机号 | 13800000001（未配置前端入口） |
| 教师 | 手机号 | 13800000001 |
| 教师 | 工号 | T001 |
| 学生 | 手机号 | 13900000001 |
| 学生 | 学号 | 20240001 |

> 教师端和管理端使用教师账号登录，用户端使用学生账号登录。也可以通过注册页面自行注册新账号。

## 数据库说明

数据库使用的是 **JPA 的 `ddl-auto: none` 模式**，不会自动建表或修改表结构。所有表通过 `deploy/mysql/init.sql` 初始化，包含：

- **26 张业务表**：用户、课程、作业、课堂练习、实训、签到、题库等
- **种子数据**：预设了 2 门课程、5 个用户（管理员+2教师+2学生）、示例作业题目、课堂练习、题库等

> 如果需要重置数据库，先删除再重建：
> ```bash
> mysql -u root -p -e "DROP DATABASE vanx_user_permiss; CREATE DATABASE vanx_user_permiss DEFAULT CHARACTER SET utf8mb4;"
> mysql -u root -p vanx_user_permiss < deploy/mysql/init.sql
> ```

## 常见问题

### 后端启动失败：连接数据库失败
- 确认 MySQL 服务已启动：`sudo systemctl status mysql`
- 确认数据库名为 `vanx_user_permiss`
- 确认 `application.yml` 中的用户名密码正确
- 确认已执行 `init.sql` 建表

### 前端启动失败：端口被占用
```bash
# 查看端口占用
lsof -i:3000
lsof -i:8848
# 杀掉占用进程
kill -9 <PID>
```

### Maven 下载依赖慢
编辑 `~/.m2/settings.xml`（如果没有就创建），添加阿里云镜像：
```xml
<settings>
  <mirrors>
    <mirror>
      <id>aliyunmaven</id>
      <mirrorOf>*</mirrorOf>
      <name>阿里云公共仓库</name>
      <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
  </mirrors>
</settings>
```

### npm install 下载慢
```bash
npm config set registry https://registry.npmmirror.com
```
