# 在线课程教育实验平台 — 虚拟机部署教程

## 目录

1. [项目说明](#1-项目说明)
2. [环境要求](#2-环境要求)
3. [部署前准备](#3-部署前准备)
4. [一键部署](#4-一键部署)
5. [手动部署（备选）](#5-手动部署备选)
6. [验证部署](#6-验证部署)
7. [常用运维命令](#7-常用运维命令)
8. [常见问题排查](#8-常见问题排查)

---

## 1. 项目说明

本平台包含以下服务：

| 服务 | 说明 | 端口 |
|------|------|------|
| MySQL 8.0 | 数据库 | 3306 |
| Spring Boot 后端 | API 服务 | 8080 |
| 用户前端 (Vue 3) | 学生端 | 80 (nginx) |
| 管理后台 (Vue 3) | 教师/管理员端 | 80 (nginx `/admin/`) |
| Nginx | 反向代理 | 80 |

所有服务通过 Docker Compose 编排，一条命令即可启动。

## 2. 环境要求

- **操作系统**：Ubuntu 20.04/22.04 或 CentOS 7/8（推荐 Ubuntu 22.04）
- **内存**：至少 4GB（推荐 8GB）
- **磁盘**：至少 20GB 可用空间
- **网络**：能访问外网（需要下载 Docker 镜像和 npm 包）
- **无需预装**：Docker、Node.js、Java、MySQL 都不需要提前安装

## 3. 部署前准备

### 3.1 将项目上传到虚拟机

**方法一：使用 git（推荐，需要虚拟机能访问 git 仓库）**

```bash
# 在虚拟机上执行
git clone <你的仓库地址> /opt/education-platform
cd /opt/education-platform
```

**方法二：使用 scp 直接上传**

在你的开发机上执行：

```bash
# 将整个项目打包上传
cd /path/to/zhyouqianduan
tar czf education-platform.tar.gz \
  deploy/ \
  zx_smalab_login_service/ \
  zx_smalab_user_pc/ \
  zx_smalab_tea_mana_pc/

scp education-platform.tar.gz user@虚拟机IP:/opt/
```

在虚拟机上解压：

```bash
cd /opt
mkdir -p education-platform
cd education-platform
tar xzf ../education-platform.tar.gz
```

**方法三：使用 U盘/共享文件夹**

直接将项目文件夹复制到虚拟机的 `/opt/education-platform/`。

### 3.2 确认目录结构

上传完成后，确认目录结构如下：

```
/opt/education-platform/
├── deploy/                          # 部署配置
│   ├── docker-compose.yml
│   ├── .env
│   ├── setup.sh
│   ├── backend/Dockerfile
│   ├── user-frontend/Dockerfile
│   ├── admin-frontend/Dockerfile
│   ├── nginx/nginx.conf
│   └── mysql/init.sql
├── zx_smalab_login_service/         # Spring Boot 后端
│   ├── pom.xml
│   └── src/
├── zx_smalab_user_pc/               # 用户前端
│   ├── package.json
│   └── src/
└── zx_smalab_tea_mana_pc/           # 管理后台前端
    ├── package.json
    └── src/
```

验证命令：

```bash
ls /opt/education-platform/deploy/docker-compose.yml
ls /opt/education-platform/zx_smalab_login_service/pom.xml
ls /opt/education-platform/zx_smalab_user_pc/package.json
ls /opt/education-platform/zx_smalab_tea_mana_pc/package.json
```

以上四个文件都必须存在，缺一不可。

## 4. 一键部署

### 4.1 执行部署脚本

```bash
cd /opt/education-platform/deploy
chmod +x setup.sh
sudo ./setup.sh
```

脚本会自动完成：
1. 安装 Docker（如果未安装）
2. 安装 Docker Compose（如果未安装）
3. 构建 5 个服务的镜像
4. 启动所有服务

**首次构建大约需要 10-20 分钟**（取决于网络速度，需要下载基础镜像和 npm 依赖）。

### 4.2 修改数据库密码（可选）

默认数据库密码是 `123456`。如需修改：

```bash
cd /opt/education-platform/deploy
nano .env
```

修改内容：

```
MYSQL_PASSWORD=你的密码
```

> **注意**：如果修改了密码，需要重建所有服务：
> ```bash
> sudo docker compose down -v
> sudo ./setup.sh
> ```

## 5. 手动部署（备选）

如果一键脚本失败，可以按以下步骤手动操作：

### 5.1 安装 Docker

```bash
# Ubuntu
curl -fsSL https://get.docker.com | sudo sh
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker $USER
# 重新登录使 docker 组生效
```

```bash
# CentOS
sudo yum install -y yum-utils
sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
sudo yum install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin
sudo systemctl start docker
sudo systemctl enable docker
```

### 5.2 安装 Docker Compose

Docker 20.10+ 自带 `docker compose`（注意没有横杠）。检查：

```bash
docker compose version
```

如果没有：

```bash
# Ubuntu
sudo apt-get update && sudo apt-get install -y docker-compose-plugin

# CentOS
sudo yum install -y docker-compose-plugin
```

### 5.3 构建和启动

```bash
cd /opt/education-platform/deploy
sudo docker compose up -d --build
```

## 6. 验证部署

### 6.1 检查服务状态

```bash
cd /opt/education-platform/deploy
sudo docker compose ps
```

所有 5 个服务都应该是 `Up` 状态。

### 6.2 检查后端日志

```bash
sudo docker compose logs backend --tail 50
```

看到 `Started LoginApplication` 表示后端启动成功。

### 6.3 检查 MySQL 初始化

```bash
sudo docker compose logs mysql --tail 30
```

看到 `ready for connections` 表示数据库就绪。

### 6.4 访问系统

在浏览器中访问（将 `虚拟机IP` 替换为实际 IP）：

| 入口 | 地址 |
|------|------|
| 用户前端 | http://虚拟机IP |
| 管理后台 | http://虚拟机IP/admin/ |
| API 文档 | http://虚拟机IP/swagger-ui.html |

### 6.5 测试登录

**管理后台登录：**

1. 打开 http://虚拟机IP/admin/
2. 使用以下账号登录：
   - 管理员：`admin` / `admin123`
   - 教师：`20240001` / `123456`

**用户前端登录：**

1. 打开 http://虚拟机IP/login
2. 使用以下账号登录：
   - 学生：`20240001` / `123456`

### 6.6 功能验证清单

答辩前请逐一验证以下功能：

**管理后台（教师端）：**
- [ ] 登录/退出
- [ ] 首页仪表盘（统计数据）
- [ ] 课程管理 → 教学课程列表
- [ ] 课程管理 → 课程设置（基本信息、教师团队、权重设置）
- [ ] 课程管理 → 班级活动（班级管理、学生列表）
- [ ] 课程管理 → 作业管理（列表、详情、题目）
- [ ] 课程管理 → 课堂练习（列表、详情）
- [ ] 课程管理 → 实训管理（列表、详情、节点树）
- [ ] 课程管理 → 考勤签到（列表、详情）
- [ ] 课程管理 → 题库管理
- [ ] 课程管理 → 通知公告
- [ ] 系统管理 → 用户/角色管理

**用户前端（学生端）：**
- [ ] 首页（热门课程、统计数据）
- [ ] 课程列表（搜索、浏览）
- [ ] 课程详情
- [ ] 登录/注册
- [ ] 个人中心（我的课程）
- [ ] 作业作答
- [ ] 课堂练习
- [ ] 实训等级

## 7. 常用运维命令

以下命令都在 `/opt/education-platform/deploy` 目录下执行。

### 启动/停止/重启

```bash
# 启动所有服务
sudo docker compose up -d

# 停止所有服务
sudo docker compose down

# 重启单个服务
sudo docker compose restart backend

# 查看所有服务状态
sudo docker compose ps
```

### 查看日志

```bash
# 后端日志
sudo docker compose logs -f backend

# 前端日志（nginx）
sudo docker compose logs -f nginx

# MySQL日志
sudo docker compose logs -f mysql

# 所有服务日志
sudo docker compose logs -f
```

### 重新构建

当修改了代码后，需要重新构建对应的服务：

```bash
# 重新构建并启动所有服务
sudo docker compose up -d --build

# 只重新构建后端
sudo docker compose up -d --build backend

# 只重新构建用户前端
sudo docker compose up -d --build user-frontend
```

### 数据库操作

```bash
# 进入 MySQL 命令行
sudo docker compose exec mysql mysql -uroot -p123456 vanx_user_permiss

# 常用 SQL
mysql> SELECT COUNT(*) FROM course;
mysql> SELECT * FROM user_info;
mysql> SELECT * FROM user_authorize WHERE identifier = '20240001';
```

### 备份数据库

```bash
# 导出数据库
sudo docker compose exec mysql mysqldump -uroot -p123456 vanx_user_permiss > backup.sql

# 恢复数据库
sudo docker compose exec -T mysql mysql -uroot -p123456 vanx_user_permiss < backup.sql
```

### 重置所有数据

```bash
# 停止并删除所有容器和数据卷（数据库数据会丢失！）
sudo docker compose down -v

# 重新构建并启动（会重新初始化数据库）
sudo docker compose up -d --build
```

## 8. 常见问题排查

### 问题 1：后端启动失败，报数据库连接错误

**现象**：`backend` 容器反复重启，日志显示 `Communications link failure`

**排查**：
```bash
# 检查 MySQL 是否就绪
sudo docker compose ps mysql

# 检查 MySQL 日志
sudo docker compose logs mysql --tail 20
```

**解决**：等待 MySQL 完全启动（健康检查通过后才会启动后端）。如果 MySQL 启动失败，检查磁盘空间：

```bash
df -h
```

### 问题 2：前端页面空白

**现象**：访问前端地址显示空白页

**排查**：
```bash
# 检查前端构建日志
sudo docker compose logs user-frontend --tail 30
sudo docker compose logs admin-frontend --tail 30
```

**解决**：常见原因是 npm install 或 build 失败。重新构建：

```bash
sudo docker compose up -d --build user-frontend admin-frontend
```

### 问题 3：API 请求 404

**现象**：页面能打开但数据加载失败

**排查**：
```bash
# 检查 nginx 配置
sudo docker compose exec nginx nginx -t

# 检查后端日志
sudo docker compose logs backend --tail 20
```

**解决**：确认后端已成功启动（看到 `Started LoginApplication`）。

### 问题 4：登录失败

**现象**：输入账号密码提示"用户名或密码错误"

**排查**：
```bash
# 检查数据库是否有数据
sudo docker compose exec mysql mysql -uroot -p123456 vanx_user_permiss \
  -e "SELECT user_id, identifier, identity_type, user_status FROM user_authorize;"
```

**解决**：如果没有数据，说明数据库初始化未执行。检查 schema.sql 是否正确挂载：

```bash
sudo docker compose exec mysql ls /docker-entrypoint-initdb.d/
```

应该能看到 `init.sql` 文件。如果没有，重新部署：

```bash
sudo docker compose down -v
sudo docker compose up -d --build
```

### 问题 5：端口被占用

**现象**：`bind: address already in use`

**解决**：修改 `deploy/docker-compose.yml` 中的端口映射，例如把 `80:80` 改为 `8088:80`：

```yaml
nginx:
  ports:
    - "8088:80"   # 将 80 改为 8088
```

然后重启：`sudo docker compose up -d`

### 问题 6：虚拟机内存不足

**现象**：后端容器被 OOM Kill，或构建时卡住

**解决**：

```bash
# 检查内存
free -h

# 如果内存小于 4GB，可以限制容器内存
# 在 docker-compose.yml 的 backend 服务中添加：
deploy:
  resources:
    limits:
      memory: 512M
```

### 问题 7：构建很慢

**原因**：首次构建需要下载 Docker 基础镜像和 npm 依赖

**加速方法**（如果在国内虚拟机上）：

```bash
# 配置 Docker 镜像加速
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<'EOF'
{
  "registry-mirrors": [
    "https://mirror.ccs.tencentyun.com",
    "https://registry.docker-cn.com"
  ]
}
EOF
sudo systemctl daemon-reload
sudo systemctl restart docker
```

对于 npm，在后端 Dockerfile 和前端 Dockerfile 的 `RUN npm install` 前添加：

```dockerfile
RUN npm config set registry https://registry.npmmirror.com
```

### 问题 8：防火墙导致无法访问

**现象**：本机能访问但外部电脑无法访问

**解决**：

```bash
# Ubuntu
sudo ufw allow 80
sudo ufw allow 8080

# CentOS
sudo firewall-cmd --permanent --add-port=80/tcp
sudo firewall-cmd --permanent --add-port=8080/tcp
sudo firewall-cmd --reload
```

---

## 快速参考卡

```bash
# 完整部署流程（从零开始）
cd /opt/education-platform/deploy
chmod +x setup.sh
sudo ./setup.sh

# 日常运维
cd /opt/education-platform/deploy
sudo docker compose ps          # 查看状态
sudo docker compose logs -f     # 查看日志
sudo docker compose restart     # 重启
sudo docker compose down        # 停止

# 代码更新后
sudo docker compose up -d --build
```
