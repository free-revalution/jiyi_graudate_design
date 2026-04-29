# 在线课程教育实验平台 - 实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 补全在线课程教育实验平台的后端API、前端API对接、Docker一键部署，使所有功能可运行。

**Architecture:** Spring Boot 3.2 + JPA + MySQL 8.0 后端，Vue 3 + Element Plus 前端。后端沿用现有JPA模式（Entity + Repository + Service + Controller）。前端将mock数据替换为API调用。Docker Compose编排所有服务。

**Tech Stack:** Java 17, Spring Boot 3.2, Spring Data JPA, Spring Security, JWT (jjwt 0.12.3), MySQL 8.0, Lombok, Vue 3, Vite, Element Plus, Pinia, Nginx, Docker

**Design spec:** `docs/superpowers/specs/2026-04-29-fullstack-design.md`

---

## Phase 1: 后端基础设施（Task 1-4）

### Task 1: 数据库初始化SQL

**Files:**
- Create: `zx_smalab_login_service/src/main/resources/schema.sql`

- [ ] **Step 1: 编写完整建表SQL**

创建 `schema.sql`，包含所有业务表的 CREATE TABLE 语句 + 种子数据（INSERT）。

关键表：course, course_teacher, course_class, course_student, course_term, homework, homework_question, homework_answer, class_exercise, exercise_question, exercise_answer, training, training_node, training_student_task, training_stage_score, check_in, check_in_record, question_bank, question_bank_item, score_weight, course_notice, course_material, user_info

种子数据包括：
- 管理员 (user_id=1001, identity_type=admin_id, identifier=admin, credential=BCrypt(admin123))
- 教师 (user_id=2001, T001/123456)
- 学生 (user_id=3001, 20240001/123456)
- 角色 (admin/teacher/student)
- 示例课程、班级、学生关联、作业、练习、题库

- [ ] **Step 2: 更新 application.yml**

修改 `zx_smalab_login_service/src/main/resources/application.yml`：
- `jpa.hibernate.ddl-auto` 从 `update` 改为 `none`（使用schema.sql管理表结构）
- 添加 `spring.sql.init.mode=always` 和 `spring.sql.init.data-locations=classpath:schema.sql`
- 添加文件上传路径配置

- [ ] **Step 3: Commit**

```bash
git add zx_smalab_login_service/src/main/resources/schema.sql zx_smalab_login_service/src/main/resources/application.yml
git commit -m "feat: add database schema and seed data SQL"
```

---

### Task 2: 后端Entity层

**Files:**
- Create: `entity/Course.java`, `entity/CourseTeacher.java`, `entity/CourseClass.java`, `entity/CourseStudent.java`, `entity/CourseTerm.java`, `entity/Homework.java`, `entity/HomeworkQuestion.java`, `entity/HomeworkAnswer.java`, `entity/ClassExercise.java`, `entity/ExerciseQuestion.java`, `entity/ExerciseAnswer.java`, `entity/Training.java`, `entity/TrainingNode.java`, `entity/TrainingStudentTask.java`, `entity/TrainingStageScore.java`, `entity/CheckIn.java`, `entity/CheckInRecord.java`, `entity/QuestionBank.java`, `entity/QuestionBankItem.java`, `entity/ScoreWeight.java`, `entity/CourseNotice.java`, `entity/CourseMaterial.java`, `entity/UserInfo.java`, `entity/Role.java`, `entity/RoleMenu.java`, `entity/UserRole.java`

所有Entity遵循现有 `UserAuthorize.java` 的模式：`@Entity @Table @Data @Builder @NoArgsConstructor @AllArgsConstructor`，使用 `jakarta.persistence.*`。

每个Entity的JSON字段（options, knowledge_points, tags, weights, answers）使用 `@Column(columnDefinition = "TEXT")` + String类型存储，在Service层用Jackson做序列化/反序列化。

- [ ] **Step 1: 创建所有Entity文件**

每个Entity对应schema.sql中的一张表，字段名与数据库列名完全一致。使用 `@Column(name = "xxx")` 映射。ID字段使用 `@Id @GeneratedValue(strategy = GenerationType.IDENTITY)`。

- [ ] **Step 2: 编译验证**

Run: `cd zx_smalab_login_service && mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 3: Commit**

```bash
git add zx_smalab_login_service/src/main/java/com/example/login/entity/
git commit -m "feat: add all JPA entity classes for business tables"
```

---

### Task 3: JWT认证过滤器 + SecurityConfig扩展

**Files:**
- Create: `config/JwtAuthenticationFilter.java`
- Create: `config/JwtTokenProvider.java`
- Modify: `config/SecurityConfig.java`

当前问题：SecurityConfig只放行了 `/user_permiss/auth/**`，但所有 `/api/**` 路径需要JWT验证。现有的 `/user_permiss/auth/**` 登录接口已经可以正常工作，不需要修改。

- [ ] **Step 1: 创建 JwtTokenProvider**

从现有 `LoginServiceImpl` 中提取JWT生成和验证逻辑：

```java
@Component
public class JwtTokenProvider {
    private final JwtConfig jwtConfig;
    // generateToken(userId, identifier) - 从LoginServiceImpl迁移
    // getUserIdFromToken(token) - 解析JWT获取userId
    // validateToken(token) - 验证JWT有效性
}
```

- [ ] **Step 2: 创建 JwtAuthenticationFilter**

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // 从请求头 "token" 或 "Authorization: Bearer xxx" 提取token
    // 验证token有效性
    // 设置 SecurityContextHolder.getContext().setAuthentication()
    // 路径 /user_permiss/auth/** 放行
}
```

- [ ] **Step 3: 修改 SecurityConfig**

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/user_permiss/auth/**", "/swagger-ui/**", "/v3/api-docs/**", "/api/home/**", "/api/courses/**").permitAll()
    .anyRequest().authenticated()
)
// 添加 JwtAuthenticationFilter
.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
```

注意：`/api/home/**` 和 `/api/courses/**` 是用户端公开接口（课程浏览不需要登录）。

- [ ] **Step 4: 修改 LoginServiceImpl 使用 JwtTokenProvider**

将 `generateAccessToken` 和 `generateRefreshToken` 方法委托给 `JwtTokenProvider`，消除代码重复。

- [ ] **Step 5: 编译验证**

Run: `cd zx_smalab_login_service && mvn compile -q`

- [ ] **Step 6: Commit**

```bash
git add zx_smalab_login_service/src/main/java/com/example/login/config/ zx_smalab_login_service/src/main/java/com/example/login/service/
git commit -m "feat: add JWT authentication filter for API endpoints"
```

---

### Task 4: Repository层

**Files:**
- Create: 所有业务表的Repository接口

每个Repository继承 `JpaRepository<Entity, Long>`，添加必要的自定义查询方法。

关键Repository的自定义方法：
- `CourseRepository`: `findByCreatorIdAndIsDeleted(Long creatorId, Integer isDeleted)`, `findByStatusAndIsDeleted(String status, Integer isDeleted)`
- `HomeworkRepository`: `findByCourseIdAndIsDeleted(Long courseId, Integer isDeleted)`
- `ClassExerciseRepository`: `findByCourseIdAndIsDeleted(Long courseId, Integer isDeleted)`
- `TrainingRepository`: `findByCourseIdAndIsDeleted(Long courseId, Integer isDeleted)`
- `CheckInRepository`: `findByCourseIdAndIsDeleted(Long courseId, Integer isDeleted)`
- `CourseStudentRepository`: `findByCourseIdAndIsDeleted(Long courseId, Integer isDeleted)`, `findByClassIdAndIsDeleted(Long classId, Integer isDeleted)`
- `UserAuthorizeRepository`: 已有，不修改

- [ ] **Step 1: 创建所有Repository文件**

- [ ] **Step 2: 编译验证**

- [ ] **Step 3: Commit**

```bash
git add zx_smalab_login_service/src/main/java/com/example/login/repository/
git commit -m "feat: add JPA repositories for all business tables"
```

---

## Phase 2: 核心业务API（Task 5-9）

### Task 5: 通用工具 + 课程CRUD API

**Files:**
- Create: `dto/response/PageResult.java`
- Create: `service/CourseService.java`, `service/impl/CourseServiceImpl.java`
- Create: `controller/CourseController.java`
- Create: `dto/request/CourseRequest.java`

**PageResult** 统一分页响应：
```java
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PageResult<T> {
    private List<T> list;
    private long total;
    private int page;
    private int limit;
}
```

**CourseController** 端点：
- `GET /api/course/list?page=1&limit=10&name=&status=` → 分页列表
- `POST /api/course` → 创建课程
- `GET /api/course/{id}` → 课程详情
- `PUT /api/course/{id}` → 更新课程
- `DELETE /api/course/{id}` → 删除（软删除）
- `POST /api/course/{id}/publish` → 发布
- `POST /api/course/{id}/close` → 结束
- `GET /api/course/{courseId}/terms` → 学期列表
- `POST /api/course/{courseId}/terms` → 创建学期
- `PUT /api/course/{courseId}/terms/{id}` → 更新学期
- `DELETE /api/course/{courseId}/terms/{id}` → 删除学期

Controller中获取当前用户ID的工具方法：
```java
// 从 SecurityContext 获取当前认证用户的userId
protected Long getCurrentUserId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return Long.parseLong(auth.getName());
}
```

- [ ] **Step 1: 创建 PageResult DTO**
- [ ] **Step 2: 创建 CourseRequest DTO**
- [ ] **Step 3: 创建 CourseService 接口**
- [ ] **Step 4: 创建 CourseServiceImpl 实现**
- [ ] **Step 5: 创建 CourseController**
- [ ] **Step 6: 编译验证**
- [ ] **Step 7: Commit**

```bash
git commit -m "feat: add course CRUD API with term management"
```

---

### Task 6: 教师团队 + 班级学生 API

**Files:**
- Modify: `service/CourseService.java`, `service/impl/CourseServiceImpl.java`
- Modify: `controller/CourseController.java`
- Create: `dto/request/TeacherRequest.java`, `dto/request/StudentRequest.java`, `dto/request/ClassRequest.java`

**教师团队端点：**
- `GET /api/course/{courseId}/teachers`
- `POST /api/course/{courseId}/teachers`
- `PUT /api/course/{courseId}/teachers/{id}`
- `DELETE /api/course/{courseId}/teachers/{id}`

**班级端点：**
- `GET /api/course/{courseId}/classes`
- `POST /api/course/{courseId}/classes`

**学生端点：**
- `GET /api/course/{courseId}/class/{classId}/students`
- `POST /api/course/{courseId}/class/{classId}/students`
- `DELETE /api/course/{courseId}/class/{classId}/students/{id}`

- [ ] **Step 1: 创建DTO文件**
- [ ] **Step 2: 扩展 CourseService 和实现**
- [ ] **Step 3: 扩展 CourseController**
- [ ] **Step 4: 编译验证**
- [ ] **Step 5: Commit**

```bash
git commit -m "feat: add teacher team and class/student management APIs"
```

---

### Task 7: 作业管理 API

**Files:**
- Create: `service/HomeworkService.java`, `service/impl/HomeworkServiceImpl.java`
- Create: `controller/HomeworkController.java`
- Create: `dto/request/HomeworkRequest.java`, `dto/request/HomeworkSubmitRequest.java`

**管理端端点：**
- `GET /api/course/{courseId}/homework/list?page=1&limit=10&name=&status=`
- `POST /api/course/{courseId}/homework` — 创建作业，body包含作业信息+题目列表
- `GET /api/course/{courseId}/homework/{id}` — 作业详情含题目
- `PUT /api/course/{courseId}/homework/{id}` — 更新作业
- `DELETE /api/course/{courseId}/homework/{id}` — 删除
- `POST /api/course/{courseId}/homework/{id}/publish` — 发布

**用户端端点：**
- `GET /api/course/{courseId}/homework/my` — 我的作业列表（含提交状态）
- `GET /api/course/{courseId}/homework/{id}/questions` — 获取作业题目（用户答题用）
- `POST /api/course/{courseId}/homework/{id}/submit` — 提交答案

创建作业时的请求体结构：
```json
{
  "name": "实验一课后习题",
  "type": "question",
  "scoreType": "average",
  "startTime": "2025-03-01 08:00",
  "endTime": "2025-03-07 23:59",
  "questions": [
    {
      "type": "single",
      "content": "题干内容",
      "options": [{"content":"A选项"},{"content":"B选项"},{"content":"C选项"},{"content":"D选项"}],
      "answer": "A",
      "analysis": "解析",
      "difficulty": 0.6,
      "sortOrder": 1
    }
  ]
}
```

提交答案的请求体：
```json
{
  "answers": {
    "s1": "A",
    "m1": ["A", "B"],
    "j1": "true",
    "f1": "填空答案"
  }
}
```

注意：questions返回给用户端时，不包含answer和analysis字段。

- [ ] **Step 1: 创建DTO文件**
- [ ] **Step 2: 创建 HomeworkService 接口**
- [ ] **Step 3: 创建 HomeworkServiceImpl 实现**
- [ ] **Step 4: 创建 HomeworkController**
- [ ] **Step 5: 编译验证**
- [ ] **Step 6: Commit**

```bash
git commit -m "feat: add homework management API (admin + user submit)"
```

---

### Task 8: 课堂练习 API

**Files:**
- Create: `service/ExerciseService.java`, `service/impl/ExerciseServiceImpl.java`
- Create: `controller/ExerciseController.java`
- Create: `dto/request/ExerciseRequest.java`, `dto/request/ExerciseSubmitRequest.java`

结构与作业几乎相同（homework → exercise），可复用 HomeworkService 的大部分逻辑模式。

**管理端端点：**
- `GET /api/course/{courseId}/exercise/list`
- `POST /api/course/{courseId}/exercise`
- `GET /api/course/{courseId}/exercise/{id}`
- `PUT /api/course/{courseId}/exercise/{id}`
- `DELETE /api/course/{courseId}/exercise/{id}`
- `POST /api/course/{courseId}/exercise/{id}/publish`

**用户端端点：**
- `GET /api/course/{courseId}/exercise/my`
- `GET /api/course/{courseId}/exercise/{id}/questions`
- `POST /api/course/{courseId}/exercise/{id}/submit`

- [ ] **Step 1: 创建DTO文件**
- [ ] **Step 2: 创建 ExerciseService 接口**
- [ ] **Step 3: 创建 ExerciseServiceImpl 实现**
- [ ] **Step 4: 创建 ExerciseController**
- [ ] **Step 5: 编译验证**
- [ ] **Step 6: Commit**

```bash
git commit -m "feat: add classroom exercise API (admin + user submit)"
```

---

### Task 9: 用户端公开API + 个人中心

**Files:**
- Create: `controller/HomeController.java`
- Create: `controller/UserController.java`
- Create: `controller/MyController.java`

**HomeController（无需认证）：**
- `GET /api/home/hot-courses` → 热门课程列表（status=published, 按创建时间排序, limit=4）
- `GET /api/home/stats` → 平台统计（课程总数、学生总数、教师总数）
- `GET /api/courses?page=1&limit=10&keyword=&category=` → 课程中心列表
- `GET /api/courses/{id}` → 课程公开详情

**UserController（需要认证）：**
- `GET /api/user/info` → 获取当前用户信息（从user_info和user_authorize关联查询）
- `PUT /api/user/info` → 更新用户信息（昵称、头像、签名等）

**MyController（需要认证）：**
- `GET /api/my/courses` → 我选的课程列表（通过course_student关联查询）
- `GET /api/my/course/{courseId}` → 我的课程详情
- `GET /api/my/course/{courseId}/practice-levels` → 我的实训关卡列表

- [ ] **Step 1: 创建 HomeController**
- [ ] **Step 2: 创建 UserController**
- [ ] **Step 3: 创建 MyController**
- [ ] **Step 4: 编译验证**
- [ ] **Step 5: Commit**

```bash
git commit -m "feat: add public course browsing and user profile APIs"
```

---

## Phase 3: 扩展业务API（Task 10-13）

### Task 10: 实训管理 API

**Files:**
- Create: `service/TrainingService.java`, `service/impl/TrainingServiceImpl.java`
- Create: `controller/TrainingController.java`
- Create: `dto/request/TrainingRequest.java`, `dto/request/TrainingNodeRequest.java`

**管理端端点：**
- `GET /api/course/{courseId}/training/list`
- `POST /api/course/{courseId}/training` — 创建实训
- `GET /api/course/{courseId}/training/{id}` — 实训详情
- `PUT /api/course/{courseId}/training/{id}` — 更新
- `DELETE /api/course/{courseId}/training/{id}` — 删除
- `GET /api/course/{courseId}/training/{id}/nodes` — 获取目录树（parent_id=0的根节点+递归子节点）
- `POST /api/course/{courseId}/training/{id}/nodes` — 保存目录树（全量替换）
- `GET /api/course/{courseId}/training/{id}/students` — 学生任务列表
- `GET /api/course/{courseId}/training/{id}/student/{userId}` — 单个学生实训详情（含关卡成绩）

**用户端端点：**
- `GET /api/course/{courseId}/training/my` — 我的实训列表
- `GET /api/course/{courseId}/training/{id}/detail` — 实训详情（含目录树，不暴露其他学生数据）

目录树保存逻辑：
1. 接收前端发送的树形节点数组
2. 删除该training的所有现有节点
3. 递归遍历树形结构，设置parentId，批量插入

- [ ] **Step 1: 创建DTO和Service**
- [ ] **Step 2: 创建Controller**
- [ ] **Step 3: 编译验证**
- [ ] **Step 4: Commit**

```bash
git commit -m "feat: add training management API with tree structure"
```

---

### Task 11: 签到管理 API

**Files:**
- Create: `service/CheckInService.java`, `service/impl/CheckInServiceImpl.java`
- Create: `controller/CheckInController.java`
- Create: `dto/request/CheckInRequest.java`

**端点：**
- `GET /api/course/{courseId}/checkin/list`
- `POST /api/course/{courseId}/checkin` — 创建签到
- `GET /api/course/{courseId}/checkin/{id}` — 签到详情
- `POST /api/course/{courseId}/checkin/{id}/start` — 开始签到（status→ongoing）
- `POST /api/course/{courseId}/checkin/{id}/end` — 结束签到（status→finished, 自动标记未签到为缺勤）
- `GET /api/course/{courseId}/checkin/{id}/records` — 签到记录列表（含已签到/未签到成员）
- `GET /api/course/{courseId}/checkin/{id}/statistics` — 签到统计（出勤率、各类请假统计）

- [ ] **Step 1: 创建Service**
- [ ] **Step 2: 创建Controller**
- [ ] **Step 3: 编译验证**
- [ ] **Step 4: Commit**

```bash
git commit -m "feat: add check-in management API"
```

---

### Task 12: 题库 + 成绩权重 + 通知 + 资料 API

**Files:**
- Create: `service/QuestionBankService.java`, `service/impl/QuestionBankServiceImpl.java`
- Create: `controller/QuestionBankController.java`
- Create: `controller/ScoreWeightController.java`
- Create: `controller/NoticeController.java`
- Create: `controller/MaterialController.java`
- Create: `controller/FileController.java`
- Create: `dto/request/QuestionBankRequest.java`, `dto/request/ScoreWeightRequest.java`, `dto/request/NoticeRequest.java`

**题库端点：**
- `GET /api/question-bank/list?page=1&limit=10&name=&category=`
- `POST /api/question-bank` — 创建题库（含题目列表）
- `PUT /api/question-bank/{id}` — 更新题库
- `DELETE /api/question-bank/{id}` — 删除
- `GET /api/question-bank/{id}/items` — 获取题库题目

**成绩权重端点：**
- `GET /api/course/{courseId}/score-weight`
- `PUT /api/course/{courseId}/score-weight` — 保存权重JSON

**通知端点：**
- `GET /api/course/{courseId}/notice/list`
- `POST /api/course/{courseId}/notice`
- `PUT /api/course/{courseId}/notice/{id}`
- `DELETE /api/course/{courseId}/notice/{id}`

**资料端点：**
- `GET /api/course/{courseId}/material/list`
- `POST /api/course/{courseId}/material`
- `DELETE /api/course/{courseId}/material/{id}`

**文件上传端点：**
- `POST /user_permiss/minio/upload-system-user-file` — 文件上传（保存到本地 `uploads/` 目录，返回访问URL）

文件上传实现：
```java
@PostMapping("/user_permiss/minio/upload-system-user-file")
public ApiResponse<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
    // 保存到本地 uploads/ 目录
    // 返回 { fileUrl: "/uploads/xxx.jpg" }
    // 配置 ResourceHandlerMapping 映射 /uploads/** 到文件目录
}
```

需要在WebConfig中添加静态资源映射：
```java
registry.addResourceHandler("/uploads/**").addResourceLocations("file:" + uploadPath + "/");
```

- [ ] **Step 1: 创建所有Service**
- [ ] **Step 2: 创建所有Controller**
- [ ] **Step 3: 创建FileController + WebConfig静态资源映射**
- [ ] **Step 4: 编译验证**
- [ ] **Step 5: Commit**

```bash
git commit -m "feat: add question bank, score weight, notice, material, and file upload APIs"
```

---

### Task 13: 仪表盘 API

**Files:**
- Create: `controller/DashboardController.java`

**端点：**
- `GET /api/dashboard/overview` → `{ courseCount, studentCount, teacherCount, homeworkCount, exerciseCount, trainingCount }`
- `GET /api/dashboard/trend?days=7` → `{ date, courseCount, studentCount, homeworkCount }[]` 按天统计趋势

直接用JPA的 `COUNT` 聚合查询，不需要复杂的统计逻辑。

- [ ] **Step 1: 创建 DashboardController**
- [ ] **Step 2: 编译验证**
- [ ] **Step 3: Commit**

```bash
git commit -m "feat: add dashboard statistics API"
```

---

## Phase 4: 前端改造（Task 14-17）

### Task 14: 管理后台清理 + API模块

**Files:**
- Modify: `zx_smalab_tea_mana_pc/src/assets/json/authMenuList.json`
- Delete: `src/views/community/`, `src/views/communityBasicConfig/`, `src/views/orderRevenue/`, `src/views/dashboard/`, `src/views/dataScreen/`, `src/views/proTable/`, `src/views/form/`, `src/views/echarts/`, `src/views/directives/`, `src/views/menu/`, `src/views/link/`, `src/views/assembly/`
- Delete: `src/api/modules/community.ts`, `src/api/modules/communityBasicConfig.ts`
- Create: 管理端API模块文件（course.ts, homework.ts, exercise.ts, training.ts, checkin.ts, questionBank.ts, notice.ts, material.ts, scoreWeight.ts）
- Modify: `src/api/modules/dashboard.ts`（替换为教育仪表盘API）

- [ ] **Step 1: 删除无用视图目录**
- [ ] **Step 2: 删除无用API文件**
- [ ] **Step 3: 清理 authMenuList.json** — 移除 community、communityBasicConfig、orderRevenue、dataScreen、proTable、form、echarts、directives、menu、link、assembly 相关的菜单项
- [ ] **Step 4: 创建新的API模块文件**
- [ ] **Step 5: 替换 dashboard.ts 为教育仪表盘API**
- [ ] **Step 6: Commit**

```bash
git commit -m "refactor: clean up unused modules and add API modules for admin panel"
```

---

### Task 15: 管理后台页面API对接

**Files:**
- Modify: `src/views/home/index.vue` — 仪表盘对接新API
- Modify: `src/views/courseCenter/teachingCourse/index.vue` — 课程列表对接API
- Modify: `src/views/courseManage/courseSetting/baseInfo/index.vue` — 课程基本信息对接
- Modify: `src/views/courseManage/courseSetting/teacherTeamManage/index.vue` — 教师团队对接
- Modify: `src/views/courseManage/courseSetting/scoreWeight/index.vue` — 成绩权重对接
- Modify: `src/views/courseManage/classActivity/index.vue` — 班级列表对接
- Modify: `src/views/courseManage/classActivity/classManage.vue` — 学生列表对接
- Modify: `src/views/courseManage/homework/index.vue` — 作业列表对接
- Modify: `src/views/courseManage/classExercise/index.vue` — 练习列表对接
- Modify: `src/views/courseManage/training/index.vue` — 实训列表对接
- Modify: `src/views/courseManage/courseCheckIn/index.vue` — 签到列表对接
- Modify: `src/views/courseManage/questionBank/index.vue` — 题库列表对接

每个页面的改造模式一致：
1. 删除硬编码的mock数据
2. 在 `onMounted` 中调用API获取数据
3. 绑定API返回数据到响应式变量
4. 操作按钮调用API（创建/编辑/删除）

注意保持前端现有数据字段名不变。后端API返回的字段名通过Service层转换来匹配前端期望的格式。

- [ ] **Step 1: 仪表盘对接**
- [ ] **Step 2: 课程中心对接**
- [ ] **Step 3: 课程管理各子页面对接**
- [ ] **Step 4: 作业/练习页面对接**
- [ ] **Step 5: 实训/签到/题库页面对接**
- [ ] **Step 6: Commit**

```bash
git commit -m "feat: connect admin panel pages to backend APIs"
```

---

### Task 16: 用户前端API对接

**Files:**
- Create: `zx_smalab_user_pc/src/api/index.js` — axios封装
- Create: `zx_smalab_user_pc/src/api/course.js`, `homework.js`, `exercise.js`, `training.js`, `user.js`
- Modify: `src/views/Home.vue` — 热门课程+统计
- Modify: `src/views/course/index.vue` — 课程中心列表
- Modify: `src/views/course/courseDetail/index.vue` — 课程详情
- Modify: `src/views/profile/index.vue` — 个人中心
- Modify: `src/views/profile/courseDetail/index.vue` — 课程学习详情
- Modify: `src/views/profile/homework/index.vue` — 作业列表
- Modify: `src/views/profile/homework/answer.vue` — 作业答题
- Modify: `src/views/profile/classroomExercise/index.vue` — 练习列表
- Modify: `src/views/profile/classroomExercise/exercise.vue` — 练习答题
- Modify: `src/views/profile/practicelevel/index.vue` — 实训关卡
- Modify: `src/router/modules/home.js` — 添加路由守卫

**axios封装要点：**
```js
// src/api/index.js
import axios from 'axios';
const request = axios.create({ baseURL: 'http://127.0.0.1:8080', timeout: 30000 });
request.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) config.headers.token = token;
  return config;
});
// 响应拦截：code !== 200 时 ElMessage.error(msg)
export default request;
```

**课程中心页 (`course/index.vue`) 改造：**
当前是空页面，需要补全课程列表展示功能。使用现有的 `CourseCard.vue` 组件 + Element Plus 的搜索筛选。

**路由守卫：**
在 `/profile/*` 路由添加 `meta: { requiresAuth: true }`，`beforeEach` 中检查 `localStorage.getItem('token')`。

- [ ] **Step 1: 创建API封装和模块文件**
- [ ] **Step 2: 首页对接**
- [ ] **Step 3: 课程中心页补全+对接**
- [ ] **Step 4: 个人中心对接**
- [ ] **Step 5: 作业/练习/实训页面对接**
- [ ] **Step 6: 添加路由守卫**
- [ ] **Step 7: Commit**

```bash
git commit -m "feat: connect user frontend pages to backend APIs"
```

---

## Phase 5: Docker部署（Task 17-18）

### Task 17: Docker配置文件

**Files:**
- Create: `deploy/docker-compose.yml`
- Create: `deploy/.env`
- Create: `deploy/nginx/nginx.conf`
- Create: `deploy/backend/Dockerfile`
- Create: `deploy/user-frontend/Dockerfile`
- Create: `deploy/user-frontend/nginx.conf`
- Create: `deploy/admin-frontend/Dockerfile`
- Create: `deploy/admin-frontend/nginx.conf`
- Create: `deploy/mysql/init.sql`（从schema.sql复制）
- Create: `deploy/setup.sh` — 一键安装Docker+启动脚本

**docker-compose.yml：**
```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_PASSWORD:-123456}
      MYSQL_DATABASE: vanx_user_permiss
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./mysql/init.sql:/docker-entrypoint-initdb.d/init.sql
    command: --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci

  backend:
    build: ./backend
    ports:
      - "8080:8080"
    depends_on:
      - mysql
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/vanx_user_permiss?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useSSL=false
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: ${MYSQL_PASSWORD:-123456}

  user-frontend:
    build: ./user-frontend
    depends_on:
      - backend

  admin-frontend:
    build: ./admin-frontend
    depends_on:
      - backend

  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf
    depends_on:
      - user-frontend
      - admin-frontend
      - backend

volumes:
  mysql_data:
```

**nginx.conf** 反向代理配置：
```nginx
server {
    listen 80;
    # 用户前端
    location / {
        proxy_pass http://user-frontend:80;
        try_files $uri $uri/ /index.html;
    }
    # 管理后台
    location /admin/ {
        proxy_pass http://admin-frontend:80/;
        try_files $uri $uri/ /index.html;
    }
    # API反向代理
    location /api/ {
        proxy_pass http://backend:8080/api/;
    }
    location /user_permiss/ {
        proxy_pass http://backend:8080/user_permiss/;
    }
    # 文件上传访问
    location /uploads/ {
        proxy_pass http://backend:8080/uploads/;
    }
}
```

**后端 Dockerfile：**
```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**前端 Dockerfile（用户端/管理端相同模式）：**
```dockerfile
FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
```

**setup.sh：**
```bash
#!/bin/bash
set -e
echo "=== 安装Docker ==="
if ! command -v docker &> /dev/null; then
    curl -fsSL https://get.docker.com | sh
    systemctl start docker
    systemctl enable docker
fi
echo "=== 启动服务 ==="
docker-compose up -d --build
echo "=== 部署完成 ==="
echo "用户前端: http://$(hostname -I | awk '{print $1}')"
echo "管理后台: http://$(hostname -I | awk '{print $1}')/admin/"
echo "API文档: http://$(hostname -I | awk '{print $1}')/swagger-ui.html"
```

- [ ] **Step 1: 创建所有Docker配置文件**
- [ ] **Step 2: 创建 setup.sh 脚本**
- [ ] **Step 3: Commit**

```bash
git commit -m "feat: add Docker deployment configuration"
```

---

### Task 18: 端到端测试 + 修复

- [ ] **Step 1: 本地启动MySQL，执行schema.sql验证建表**
- [ ] **Step 2: 启动后端，访问Swagger验证所有API**
- [ ] **Step 3: 启动用户前端，测试登录→浏览课程→个人中心→作业→练习流程**
- [ ] **Step 4: 启动管理后台，测试登录→创建课程→添加作业→发布→用户端提交流程**
- [ ] **Step 5: 修复发现的问题**
- [ ] **Step 6: 最终Commit**

```bash
git commit -m "fix: end-to-end testing and bug fixes"
```

---

## 关键实现注意事项

1. **前端字段名匹配**：后端返回的JSON字段名必须与前端现有代码中使用的字段名完全一致。例如前端用 `pendingCount` 而不是 `pending_count`。使用 `@JsonProperty` 或 Jackson 的 `naming-strategy` 配置。

2. **JSON字段处理**：数据库中 options、answers、weights 等JSON字段在Entity中存为String，Controller返回时需反序列化为JSON对象/数组。在Service层使用 `ObjectMapper` 进行转换。

3. **认证兼容**：前端管理后台通过 `token` header 传递JWT（不是 `Authorization: Bearer`），JWT过滤器需要同时支持这两种方式。

4. **分页兼容**：管理后台使用 ProTable 组件，期望分页响应格式为 `{ list: [], total: N }`。用户端自行处理分页。后端统一使用 `PageResult` DTO。

5. **软删除**：所有业务表都有 `is_deleted` 字段，查询时需添加 `WHERE is_deleted = 0` 条件。

6. **时间格式**：前端使用的日期格式为 `YYYY-MM-DD HH:mm:ss`，后端使用 `@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")` 确保格式一致。
