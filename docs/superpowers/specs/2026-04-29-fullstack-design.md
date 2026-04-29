# 在线课程教育实验平台 - 全栈设计文档

## 1. 项目概述

毕业设计项目：在线课程教育实验平台。前端已完成（Vue 3 + Element Plus），需要补全 Spring Boot 后端、前端API对接、Docker部署配置。

**约束条件：**
- 时间紧迫：1-2周内完成
- 答辩要求所有功能可用
- 前端可以清理无用模块、改mock为API调用，不能改变UI和功能
- 部署目标：同学虚拟机（Linux，无Docker），需一键部署

---

## 2. 系统架构

```
┌─────────────────────────────────────────────────┐
│                  Nginx (:80)                     │
│  /           → 用户前端静态资源                   │
│  /admin/     → 管理后台静态资源                   │
│  /api/       → 反向代理到 Spring Boot (:8080)    │
└─────────────────────────────────────────────────┘
        │                        │
        ▼                        ▼
┌──────────────┐        ┌──────────────────┐
│ 用户前端      │        │ 管理后台前端       │
│ Vue3+Vite    │        │ Vue3+Vite+TS     │
│ :3000(开发)   │        │ :8848(开发)       │
└──────────────┘        └──────────────────┘
                                │
                                ▼
                    ┌──────────────────────┐
                    │  Spring Boot (:8080)  │
                    │  + JWT认证            │
                    └──────────┬───────────┘
                               │
                    ┌──────────▼───────────┐
                    │    MySQL 8.0 (:3306)  │
                    └──────────────────────┘
```

### Docker Compose 服务

| 服务 | 镜像/构建 | 端口 | 说明 |
|------|----------|------|------|
| mysql | mysql:8.0 | 3306 | 数据库 |
| backend | Dockerfile (Java 17) | 8080 | Spring Boot API |
| user-frontend | Dockerfile (nginx) | - | 用户前端 |
| admin-frontend | Dockerfile (nginx) | - | 管理后台 |
| nginx | nginx:alpine | 80 | 反向代理 |

---

## 3. 数据库设计

### 3.1 现有表（保留，需补Entity）

#### user_authorize（已有Entity）
用户认证表，支持多种登录方式。
- 已有字段：authorize_id, user_id, user_status, identity_type, identifier, credential, login_time, login_ip, access_token, refresh_token 等
- 现有后端已实现登录/注册/登出/刷新Token

#### user_info（需补Entity）
用户基本信息表。
- 核心字段：user_id, user_type, user_name, user_nickname, user_photo, user_motto, sex, birthday, tel, email, school
- user_type: "student" | "teacher" | "admin"
- school: 学生注册时选择的学校标识

#### vanx_platf_roles（需补Entity）
角色表。
- role_key, role_name, role_status
- 预置角色：admin(管理员), teacher(教师), student(学生)

#### vanx_platf_role_menu（需补Entity）
角色-菜单权限关联表。

#### vanx_platf_user_roles（需补Entity）
用户-角色关联表。

### 3.2 新增业务表

#### course（课程表）
```sql
CREATE TABLE course (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  course_code VARCHAR(50) UNIQUE COMMENT '课程编号',
  name VARCHAR(200) NOT NULL COMMENT '课程名称',
  english_name VARCHAR(200) COMMENT '英文名',
  cover_url VARCHAR(500) COMMENT '封面图URL',
  description TEXT COMMENT '课程描述（HTML）',
  category VARCHAR(50) COMMENT '课程分类：专业必修课/专业选修课/公共必修课/公共选修课',
  belong_unit VARCHAR(100) COMMENT '所属单位',
  department VARCHAR(100) COMMENT '院系',
  teacher_name VARCHAR(100) COMMENT '主讲教师姓名',
  total_hours INT COMMENT '总学时',
  status VARCHAR(20) DEFAULT 'draft' COMMENT '状态：draft/published/closed',
  creator_id BIGINT COMMENT '创建人ID',
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted INT DEFAULT 0,
  INDEX idx_creator(creator_id),
  INDEX idx_status(status)
);
```

#### course_teacher（课程教师团队）
```sql
CREATE TABLE course_teacher (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  course_id BIGINT NOT NULL,
  name VARCHAR(100) NOT NULL,
  role VARCHAR(50) COMMENT '角色：主讲教师/助教/辅导员',
  work_no VARCHAR(50) COMMENT '学号/工号',
  department VARCHAR(100) COMMENT '院系',
  join_time DATETIME,
  user_id BIGINT COMMENT '关联user_authorize的user_id',
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  is_deleted INT DEFAULT 0,
  INDEX idx_course(course_id)
);
```

#### course_class（课程班级）
```sql
CREATE TABLE course_class (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  course_id BIGINT NOT NULL,
  name VARCHAR(200) NOT NULL COMMENT '班级名称',
  student_count INT DEFAULT 0,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  is_deleted INT DEFAULT 0,
  INDEX idx_course(course_id)
);
```

#### course_student（班级学生）
```sql
CREATE TABLE course_student (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  class_id BIGINT NOT NULL,
  course_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL COMMENT '关联user_authorize的user_id',
  student_id VARCHAR(50) COMMENT '学号',
  name VARCHAR(100),
  department VARCHAR(100),
  major VARCHAR(100),
  class_name VARCHAR(200),
  join_time DATETIME,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  is_deleted INT DEFAULT 0,
  UNIQUE KEY uk_class_user(class_id, user_id),
  INDEX idx_course(course_id)
);
```

#### course_term（课程学期）
```sql
CREATE TABLE course_term (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  course_id BIGINT NOT NULL,
  name VARCHAR(200) COMMENT '学期名称',
  start_time DATETIME,
  end_time DATETIME,
  status VARCHAR(20) COMMENT '状态：upcoming/ongoing/finished',
  info VARCHAR(500) COMMENT '学期信息',
  content TEXT COMMENT '教学内容',
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  is_deleted INT DEFAULT 0,
  INDEX idx_course(course_id)
);
```

#### homework（作业）
```sql
CREATE TABLE homework (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  course_id BIGINT NOT NULL,
  class_id BIGINT COMMENT '关联班级（空=全部班级）',
  name VARCHAR(200) NOT NULL,
  cover VARCHAR(500) COMMENT '封面图',
  type VARCHAR(20) DEFAULT 'question' COMMENT 'question/answer',
  score_type VARCHAR(20) DEFAULT 'average' COMMENT 'average/custom',
  start_time DATETIME,
  end_time DATETIME,
  status VARCHAR(20) DEFAULT 'draft' COMMENT 'draft/published/closed',
  pending_count INT DEFAULT 0,
  submitted_count INT DEFAULT 0,
  unsubmitted_count INT DEFAULT 0,
  creator_id BIGINT,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted INT DEFAULT 0,
  INDEX idx_course(course_id)
);
```

#### homework_question（作业题目）
```sql
CREATE TABLE homework_question (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  homework_id BIGINT NOT NULL,
  sort_order INT DEFAULT 0,
  type VARCHAR(20) NOT NULL COMMENT 'single/multiple/judge/fill',
  content TEXT NOT NULL COMMENT '题目内容',
  options JSON COMMENT '选项 [{content:"A选项"},{content:"B选项"}]',
  answer VARCHAR(500) COMMENT '正确答案',
  analysis TEXT COMMENT '解析',
  difficulty DECIMAL(3,2) DEFAULT 0.60 COMMENT '难度系数 0.8/0.6/0.4',
  knowledge_points JSON COMMENT '知识点标签',
  tags JSON COMMENT '标签',
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  is_deleted INT DEFAULT 0,
  INDEX idx_homework(homework_id)
);
```

#### homework_answer（学生作业提交）
```sql
CREATE TABLE homework_answer (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  homework_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  answers JSON NOT NULL COMMENT '{"s1":"A","m1":["A","B"],"j1":"true","f1":"答案"}',
  score DECIMAL(5,2) COMMENT '得分',
  submit_time DATETIME,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_homework_user(homework_id, user_id)
);
```

#### class_exercise（课堂练习）
```sql
CREATE TABLE class_exercise (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  course_id BIGINT NOT NULL,
  name VARCHAR(200) NOT NULL,
  type VARCHAR(20) DEFAULT 'question',
  score_type VARCHAR(20) DEFAULT 'average',
  start_time DATETIME,
  end_time DATETIME,
  status VARCHAR(20) DEFAULT 'draft' COMMENT 'draft/unstarted/ongoing/closed',
  completed_count INT DEFAULT 0,
  uncompleted_count INT DEFAULT 0,
  question_count INT DEFAULT 0,
  creator_id BIGINT,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted INT DEFAULT 0,
  INDEX idx_course(course_id)
);
```

#### exercise_question（练习题目）
```sql
CREATE TABLE exercise_question (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  exercise_id BIGINT NOT NULL,
  sort_order INT DEFAULT 0,
  type VARCHAR(20) NOT NULL,
  content TEXT NOT NULL,
  options JSON,
  answer VARCHAR(500),
  analysis TEXT,
  difficulty DECIMAL(3,2) DEFAULT 0.60,
  knowledge_points JSON,
  tags JSON,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  is_deleted INT DEFAULT 0,
  INDEX idx_exercise(exercise_id)
);
```

#### exercise_answer（学生练习提交）
```sql
CREATE TABLE exercise_answer (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  exercise_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  answers JSON NOT NULL,
  score DECIMAL(5,2),
  submit_time DATETIME,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_exercise_user(exercise_id, user_id)
);
```

#### training（实训）
```sql
CREATE TABLE training (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  course_id BIGINT NOT NULL,
  name VARCHAR(200) NOT NULL,
  cover VARCHAR(500),
  description TEXT,
  start_time DATETIME,
  end_time DATETIME,
  status VARCHAR(20) DEFAULT 'draft' COMMENT 'draft/unstarted/ongoing/closed',
  pending_count INT DEFAULT 0,
  submitted_count INT DEFAULT 0,
  unsubmitted_count INT DEFAULT 0,
  creator_id BIGINT,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted INT DEFAULT 0,
  INDEX idx_course(course_id)
);
```

#### training_node（实训目录节点，树形结构）
```sql
CREATE TABLE training_node (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  training_id BIGINT NOT NULL,
  parent_id BIGINT DEFAULT 0 COMMENT '父节点ID，0=根节点',
  node_index VARCHAR(20) COMMENT '序号：01/1.1/1.1.1',
  label VARCHAR(200) NOT NULL COMMENT '节点名称',
  content TEXT COMMENT '内容（HTML富文本）',
  sort_order INT DEFAULT 0,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  is_deleted INT DEFAULT 0,
  INDEX idx_training(training_id),
  INDEX idx_parent(parent_id)
);
```

#### training_student_task（学生实训任务）
```sql
CREATE TABLE training_student_task (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  training_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  name VARCHAR(100),
  student_id VARCHAR(50),
  class_name VARCHAR(200),
  training_status VARCHAR(20) DEFAULT 'notStarted' COMMENT 'notStarted/ongoing/completed',
  total_time INT DEFAULT 0 COMMENT '总耗时（秒）',
  pass_rate VARCHAR(20),
  eval_count INT DEFAULT 0,
  final_score DECIMAL(5,2),
  reject_count INT DEFAULT 0,
  review_status VARCHAR(20) DEFAULT 'pending' COMMENT 'pending/reviewed/rejected',
  comment TEXT,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_training_user(training_id, user_id),
  INDEX idx_training(training_id)
);
```

#### training_stage_score（关卡成绩）
```sql
CREATE TABLE training_stage_score (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  task_id BIGINT NOT NULL COMMENT '关联training_student_task的id',
  stage INT COMMENT '关卡序号',
  task_name VARCHAR(200),
  start_time DATETIME,
  code_changes INT DEFAULT 0,
  eval_count INT DEFAULT 0,
  finish_time DATETIME,
  training_time INT DEFAULT 0,
  view_answer INT DEFAULT 0,
  exp INT DEFAULT 0,
  stage_score DECIMAL(5,2),
  deduction DECIMAL(5,2) DEFAULT 0,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_task(task_id)
);
```

#### check_in（签到）
```sql
CREATE TABLE check_in (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  course_id BIGINT NOT NULL,
  class_id BIGINT,
  name VARCHAR(200) NOT NULL,
  date DATE,
  type VARCHAR(20) DEFAULT 'qrcode' COMMENT 'qrcode/location',
  enable_location TINYINT DEFAULT 1,
  location_range INT DEFAULT 500,
  auto_refresh_qrcode TINYINT DEFAULT 1,
  qrcode_refresh_rate INT DEFAULT 10,
  duration_minutes INT DEFAULT 30,
  manual_end TINYINT DEFAULT 0,
  late_minutes INT DEFAULT 10,
  enable_sign_out TINYINT DEFAULT 0,
  total_count INT DEFAULT 0,
  absent_count INT DEFAULT 0,
  status VARCHAR(20) DEFAULT 'notStarted' COMMENT 'notStarted/ongoing/finished',
  creator_id BIGINT,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  is_deleted INT DEFAULT 0,
  INDEX idx_course(course_id)
);
```

#### check_in_record（签到记录）
```sql
CREATE TABLE check_in_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  check_in_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  student_id VARCHAR(50),
  name VARCHAR(100),
  avatar VARCHAR(500),
  sign_time DATETIME,
  sign_out_time DATETIME,
  is_late TINYINT DEFAULT 0,
  is_absent TINYINT DEFAULT 0,
  personal_leave TINYINT DEFAULT 0,
  sick_leave TINYINT DEFAULT 0,
  early_leave TINYINT DEFAULT 0,
  official_leave TINYINT DEFAULT 0,
  attendance_rate DECIMAL(5,2),
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_checkin_user(check_in_id, user_id),
  INDEX idx_checkin(check_in_id)
);
```

#### question_bank（题库）
```sql
CREATE TABLE question_bank (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(200) NOT NULL,
  category VARCHAR(50) COMMENT 'programming/dataStructure/algorithm/database/network/other',
  creator VARCHAR(100),
  department VARCHAR(150),
  question_count INT DEFAULT 0,
  status VARCHAR(20) DEFAULT 'enabled' COMMENT 'enabled/disabled',
  creator_id BIGINT,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted INT DEFAULT 0
);
```

#### question_bank_item（题库题目）
```sql
CREATE TABLE question_bank_item (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  bank_id BIGINT NOT NULL,
  type VARCHAR(20) NOT NULL COMMENT 'single/multiple/judge/fill',
  content TEXT NOT NULL,
  options JSON,
  answer VARCHAR(500),
  analysis TEXT,
  difficulty DECIMAL(3,2) DEFAULT 0.60,
  knowledge_points JSON,
  tags JSON,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  is_deleted INT DEFAULT 0,
  INDEX idx_bank(bank_id)
);
```

#### score_weight（成绩权重）
```sql
CREATE TABLE score_weight (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  course_id BIGINT NOT NULL UNIQUE,
  weights JSON NOT NULL COMMENT '[{"dimension":"课堂表现","dimensionScore":20,"subDimension":"出勤率","subScore":10,"standard":"..."}]',
  updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### course_notice（课程通知）
```sql
CREATE TABLE course_notice (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  course_id BIGINT NOT NULL,
  title VARCHAR(200) NOT NULL,
  content TEXT,
  is_top TINYINT DEFAULT 0,
  creator_id BIGINT,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  is_deleted INT DEFAULT 0,
  INDEX idx_course(course_id)
);
```

#### course_material（课程资料）
```sql
CREATE TABLE course_material (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  course_id BIGINT NOT NULL,
  name VARCHAR(200) NOT NULL,
  file_url VARCHAR(500),
  file_type VARCHAR(50) COMMENT 'pdf/doc/video/other',
  file_size BIGINT,
  creator_id BIGINT,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  is_deleted INT DEFAULT 0,
  INDEX idx_course(course_id)
);
```

---

## 4. API接口设计

### 4.1 认证模块（已有，需微调）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /user_permiss/auth/password/login | 登录 |
| POST | /user_permiss/auth/register | 注册 |
| POST | /user_permiss/auth/logout | 登出 |
| POST | /user_permiss/auth/refresh | 刷新Token |

### 4.2 用户模块

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/user/info | 获取当前用户信息 |
| PUT | /api/user/info | 更新用户信息 |

### 4.3 课程模块（管理端）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/course/list | 课程列表（分页、搜索、状态筛选） |
| POST | /api/course | 创建课程 |
| GET | /api/course/{id} | 课程详情 |
| PUT | /api/course/{id} | 更新课程 |
| DELETE | /api/course/{id} | 删除课程 |
| POST | /api/course/{id}/publish | 发布课程 |
| POST | /api/course/{id}/close | 结束课程 |

### 4.4 课程教师团队

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/course/{courseId}/teachers | 教师列表 |
| POST | /api/course/{courseId}/teachers | 添加教师 |
| PUT | /api/course/{courseId}/teachers/{id} | 编辑教师 |
| DELETE | /api/course/{courseId}/teachers/{id} | 删除教师 |

### 4.5 课程班级与学生

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/course/{courseId}/classes | 班级列表 |
| POST | /api/course/{courseId}/classes | 创建班级 |
| GET | /api/course/{courseId}/class/{classId}/students | 班级学生列表 |
| POST | /api/course/{courseId}/class/{classId}/students | 添加学生 |
| DELETE | /api/course/{courseId}/class/{classId}/students/{id} | 移除学生 |

### 4.6 课程学期

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/course/{courseId}/terms | 学期列表 |
| POST | /api/course/{courseId}/terms | 创建学期 |
| PUT | /api/course/{courseId}/terms/{id} | 更新学期 |
| DELETE | /api/course/{courseId}/terms/{id} | 删除学期 |

### 4.7 作业管理（管理端）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/course/{courseId}/homework/list | 作业列表 |
| POST | /api/course/{courseId}/homework | 创建作业（含题目） |
| GET | /api/course/{courseId}/homework/{id} | 作业详情（含题目） |
| PUT | /api/course/{courseId}/homework/{id} | 更新作业 |
| DELETE | /api/course/{courseId}/homework/{id} | 删除作业 |
| POST | /api/course/{courseId}/homework/{id}/publish | 发布作业 |

### 4.8 作业（用户端）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/course/{courseId}/homework/my | 我的作业列表 |
| GET | /api/course/{courseId}/homework/{id}/questions | 获取作业题目 |
| POST | /api/course/{courseId}/homework/{id}/submit | 提交作业 |

### 4.9 课堂练习（管理端）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/course/{courseId}/exercise/list | 练习列表 |
| POST | /api/course/{courseId}/exercise | 创建练习（含题目） |
| GET | /api/course/{courseId}/exercise/{id} | 练习详情 |
| PUT | /api/course/{courseId}/exercise/{id} | 更新练习 |
| DELETE | /api/course/{courseId}/exercise/{id} | 删除练习 |
| POST | /api/course/{courseId}/exercise/{id}/publish | 发布练习 |

### 4.10 课堂练习（用户端）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/course/{courseId}/exercise/my | 我的练习列表 |
| GET | /api/course/{courseId}/exercise/{id}/questions | 获取练习题目 |
| POST | /api/course/{courseId}/exercise/{id}/submit | 提交练习 |

### 4.11 实训管理（管理端）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/course/{courseId}/training/list | 实训列表 |
| POST | /api/course/{courseId}/training | 创建实训 |
| GET | /api/course/{courseId}/training/{id} | 实训详情 |
| PUT | /api/course/{courseId}/training/{id} | 更新实训 |
| DELETE | /api/course/{courseId}/training/{id} | 删除实训 |
| GET | /api/course/{courseId}/training/{id}/nodes | 获取实训目录树 |
| POST | /api/course/{courseId}/training/{id}/nodes | 保存实训目录树 |
| GET | /api/course/{courseId}/training/{id}/students | 学生任务列表 |
| GET | /api/course/{courseId}/training/{id}/student/{userId} | 单个学生实训详情 |

### 4.12 实训（用户端）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/course/{courseId}/training/my | 我的实训列表 |
| GET | /api/course/{courseId}/training/{id}/detail | 实训详情（含目录树） |

### 4.13 签到管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/course/{courseId}/checkin/list | 签到列表 |
| POST | /api/course/{courseId}/checkin | 创建签到 |
| GET | /api/course/{courseId}/checkin/{id} | 签到详情 |
| POST | /api/course/{courseId}/checkin/{id}/start | 开始签到 |
| POST | /api/course/{courseId}/checkin/{id}/end | 结束签到 |
| GET | /api/course/{courseId}/checkin/{id}/records | 签到记录列表 |
| GET | /api/course/{courseId}/checkin/{id}/statistics | 签到统计 |

### 4.14 题库管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/question-bank/list | 题库列表 |
| POST | /api/question-bank | 创建题库 |
| PUT | /api/question-bank/{id} | 更新题库 |
| DELETE | /api/question-bank/{id} | 删除题库 |
| GET | /api/question-bank/{id}/items | 题库题目列表 |

### 4.15 成绩权重

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/course/{courseId}/score-weight | 获取权重配置 |
| PUT | /api/course/{courseId}/score-weight | 更新权重配置 |

### 4.16 课程通知

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/course/{courseId}/notice/list | 通知列表 |
| POST | /api/course/{courseId}/notice | 创建通知 |
| PUT | /api/course/{courseId}/notice/{id} | 更新通知 |
| DELETE | /api/course/{courseId}/notice/{id} | 删除通知 |

### 4.17 课程资料

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/course/{courseId}/material/list | 资料列表 |
| POST | /api/course/{courseId}/material | 上传资料 |
| DELETE | /api/course/{courseId}/material/{id} | 删除资料 |

### 4.18 用户端-首页/课程浏览

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/home/hot-courses | 热门课程列表 |
| GET | /api/home/stats | 平台统计 |
| GET | /api/courses | 课程中心列表（分页、搜索、分类筛选） |
| GET | /api/courses/{id} | 课程公开详情 |

### 4.19 用户端-个人中心

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/my/courses | 我选的课程列表 |
| GET | /api/my/course/{courseId} | 我的课程详情（含统计数据） |
| GET | /api/my/course/{courseId}/practice-levels | 我的实训关卡列表 |

### 4.20 仪表盘（管理端首页）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/dashboard/overview | 总览数据（课程数/学生数/教师数/作业数等） |
| GET | /api/dashboard/trend | 趋势数据（近N天） |

### 4.21 文件上传

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /user_permiss/minio/upload-system-user-file | 文件上传（已有前端调用） |

---

## 5. 前端改造计划

### 5.1 管理后台改造

**清理无用模块：**
- 删除 `src/views/community/` 整个目录
- 删除 `src/views/communityBasicConfig/` 整个目录
- 删除 `src/views/orderRevenue/` 整个目录
- 删除 `src/views/dashboard/` 目录（用新的教育仪表盘替代）
- 删除 `src/views/dataScreen/` 目录（无用）
- 删除 `src/views/proTable/` 目录（示例代码）
- 删除 `src/views/form/` 目录（示例代码）
- 删除 `src/views/echarts/` 目录（示例代码）
- 删除 `src/views/directives/` 目录（示例代码）
- 删除 `src/views/menu/` 目录（示例代码）
- 删除 `src/views/link/` 目录（外部链接）
- 删除 `src/views/assembly/` 目录（组件示例）
- 删除 `src/api/modules/community.ts`
- 删除 `src/api/modules/communityBasicConfig.ts`
- 删除 `src/api/modules/dashboard.ts`（替换为新内容）
- 清理 `authMenuList.json` 中对应的无用菜单项

**API对接（管理端）：**
- 首页仪表盘：改为教育数据统计
- 课程中心-教学课程：对接课程CRUD
- 课程管理内所有页面：对接对应API
- 题库管理：对接题库CRUD
- 系统管理stub页面：保持stub状态即可

### 5.2 用户前端改造

**API对接（用户端）：**
- 首页：热门课程、平台统计
- 课程中心页：补充课程列表（目前是空页面）
- 课程详情页：课程详情API
- 个人中心：用户信息、已选课程
- 课程学习详情：课程统计数据
- 作业列表/答题：作业API
- 课堂练习列表/答题：练习API
- 实训关卡：实训API
- 添加路由守卫（登录检查）
- 利用已定义但未使用的 Pinia store

### 5.3 新增前端API模块

**管理端新增 API 文件：**
- `src/api/modules/course.ts` — 课程CRUD
- `src/api/modules/homework.ts` — 作业管理
- `src/api/modules/exercise.ts` — 课堂练习
- `src/api/modules/training.ts` — 实训管理
- `src/api/modules/checkin.ts` — 签到管理
- `src/api/modules/questionBank.ts` — 题库管理
- `src/api/modules/notice.ts` — 通知管理
- `src/api/modules/material.ts` — 资料管理
- `src/api/modules/scoreWeight.ts` — 成绩权重
- `src/api/modules/dashboard.ts` — 教育仪表盘（覆盖原文件）

**用户端新增 API 目录：**
- `src/api/index.js` — axios封装
- `src/api/course.js` — 课程相关
- `src/api/homework.js` — 作业相关
- `src/api/exercise.js` — 练习相关
- `src/api/training.js` — 实训相关

---

## 6. 后端代码结构

```
zx_smalab_login_service/src/main/java/com/example/login/
├── LoginApplication.java
├── config/
│   ├── JwtConfig.java              （已有）
│   ├── OpenApiConfig.java          （已有）
│   ├── SecurityConfig.java         （已有，需扩展）
│   ├── WebConfig.java              （已有，需扩展）
│   └── MyBatisPlusConfig.java      （新增，如果用MyBatis-Plus）
├── controller/
│   ├── LoginController.java        （已有）
│   ├── UserController.java         （新增）
│   ├── CourseController.java       （新增）
│   ├── HomeworkController.java     （新增）
│   ├── ExerciseController.java     （新增）
│   ├── TrainingController.java     （新增）
│   ├── CheckInController.java      （新增）
│   ├── QuestionBankController.java （新增）
│   ├── NoticeController.java       （新增）
│   ├── MaterialController.java     （新增）
│   ├── DashboardController.java    （新增）
│   └── FileController.java         （新增，文件上传）
├── dto/
│   ├── request/                    （已有3个，新增多个）
│   └── response/                   （已有2个，新增多个）
├── entity/                         （已有1个，新增~15个）
├── exception/                      （已有2个，保持）
├── repository/                     （已有1个，新增~15个）
├── service/                        （已有接口+实现，新增多个）
│   └── impl/
└── common/                         （新增）
    ├── Result.java                 （统一响应体，已有ApiResponse可用）
    └── PageResult.java             （分页响应体）
```

---

## 7. Docker部署方案

### 7.1 目录结构

```
deploy/
├── docker-compose.yml
├── .env                            # 环境变量配置
├── mysql/
│   ├── Dockerfile
│   └── init.sql                    # 建表+种子数据
├── backend/
│   └── Dockerfile
├── user-frontend/
│   ├── Dockerfile
│   └── nginx.conf
├── admin-frontend/
│   ├── Dockerfile
│   └── nginx.conf
└── nginx/
    ├── Dockerfile
    └── nginx.conf                  # 反向代理配置
```

### 7.2 一键部署流程

同学只需：
```bash
# 1. 安装Docker（提供安装脚本）
curl -fsSL https://get.docker.com | sh

# 2. 上传项目代码

# 3. 一键启动
cd deploy
docker-compose up -d

# 4. 访问
# 用户前端: http://<IP>
# 管理后台: http://<IP>/admin/
# API文档: http://<IP>/swagger-ui.html
```

---

## 8. 种子数据

初始化SQL将包含：
- 管理员账号：admin / admin123
- 教师账号：T001 / 123456, T002 / 123456
- 学生账号：20240001 / 123456, 20240002 / 123456
- 示例课程：人工智能、离散数学
- 示例班级、学生
- 示例作业、练习、实训
- 示例题库

---

## 9. 实施优先级

### P0 - 核心流程（必须先完成）
1. 数据库建表 + 种子数据
2. 认证模块完善（确保登录/注册正常）
3. 课程CRUD（管理端创建课程 + 用户端浏览课程）
4. 作业CRUD + 用户提交
5. 课堂练习CRUD + 用户提交

### P1 - 重要功能
6. 实训管理 + 目录树
7. 签到管理
8. 题库管理
9. 教师团队管理
10. 班级学生管理

### P2 - 辅助功能
11. 仪表盘统计
12. 成绩权重配置
13. 课程通知/资料
14. 个人中心完善

### P3 - 部署
15. Docker配置
16. 前端清理 + API对接
17. 端到端测试
