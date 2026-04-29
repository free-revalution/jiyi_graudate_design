-- ============================================================
-- Database Schema for zx_smalab_login_service
-- Online Education Platform - Business Tables & Seed Data
-- ============================================================

-- ============================================================
-- 1. user_info (Extended user information)
-- ============================================================
CREATE TABLE IF NOT EXISTS user_info (
  user_id BIGINT PRIMARY KEY,
  user_type VARCHAR(20) COMMENT 'student/teacher/admin',
  user_name VARCHAR(100),
  user_nickname VARCHAR(100),
  user_photo VARCHAR(500),
  user_motto VARCHAR(500),
  sex VARCHAR(10),
  birthday DATE,
  age INT,
  tel VARCHAR(50),
  email VARCHAR(100),
  school VARCHAR(100),
  role_type VARCHAR(50),
  add_source VARCHAR(20) DEFAULT 'custom',
  editor_id BIGINT,
  creator_id BIGINT,
  modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  other_info_one VARCHAR(50),
  other_info_two VARCHAR(50),
  remark VARCHAR(50),
  is_deleted INT DEFAULT 0
);

-- ============================================================
-- 2. course (Course master table)
-- ============================================================
CREATE TABLE IF NOT EXISTS course (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  course_code VARCHAR(50) UNIQUE,
  name VARCHAR(200) NOT NULL,
  english_name VARCHAR(200),
  cover_url VARCHAR(500),
  description TEXT,
  category VARCHAR(50),
  belong_unit VARCHAR(100),
  department VARCHAR(100),
  teacher_name VARCHAR(100),
  total_hours INT,
  status VARCHAR(20) DEFAULT 'draft',
  creator_id BIGINT,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted INT DEFAULT 0,
  INDEX idx_creator(creator_id),
  INDEX idx_status(status)
);

-- ============================================================
-- 3. course_teacher (Course teacher association)
-- ============================================================
CREATE TABLE IF NOT EXISTS course_teacher (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  course_id BIGINT NOT NULL,
  name VARCHAR(100) NOT NULL,
  role VARCHAR(50),
  work_no VARCHAR(50),
  department VARCHAR(100),
  join_time DATETIME,
  user_id BIGINT,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  is_deleted INT DEFAULT 0,
  INDEX idx_course(course_id)
);

-- ============================================================
-- 4. course_class (Course class/section)
-- ============================================================
CREATE TABLE IF NOT EXISTS course_class (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  course_id BIGINT NOT NULL,
  name VARCHAR(200) NOT NULL,
  student_count INT DEFAULT 0,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  is_deleted INT DEFAULT 0,
  INDEX idx_course(course_id)
);

-- ============================================================
-- 5. course_student (Course student enrollment)
-- ============================================================
CREATE TABLE IF NOT EXISTS course_student (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  class_id BIGINT NOT NULL,
  course_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  student_id VARCHAR(50),
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

-- ============================================================
-- 6. course_term (Course term/semester)
-- ============================================================
CREATE TABLE IF NOT EXISTS course_term (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  course_id BIGINT NOT NULL,
  name VARCHAR(200),
  start_time DATETIME,
  end_time DATETIME,
  status VARCHAR(20),
  info VARCHAR(500),
  content TEXT,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  is_deleted INT DEFAULT 0,
  INDEX idx_course(course_id)
);

-- ============================================================
-- 7. homework (Homework assignments)
-- ============================================================
CREATE TABLE IF NOT EXISTS homework (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  course_id BIGINT NOT NULL,
  class_id BIGINT,
  name VARCHAR(200) NOT NULL,
  cover VARCHAR(500),
  type VARCHAR(20) DEFAULT 'question',
  score_type VARCHAR(20) DEFAULT 'average',
  start_time DATETIME,
  end_time DATETIME,
  status VARCHAR(20) DEFAULT 'draft',
  pending_count INT DEFAULT 0,
  submitted_count INT DEFAULT 0,
  unsubmitted_count INT DEFAULT 0,
  creator_id BIGINT,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted INT DEFAULT 0,
  INDEX idx_course(course_id)
);

-- ============================================================
-- 8. homework_question (Homework question items)
-- ============================================================
CREATE TABLE IF NOT EXISTS homework_question (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  homework_id BIGINT NOT NULL,
  sort_order INT DEFAULT 0,
  type VARCHAR(20) NOT NULL,
  content TEXT NOT NULL,
  options TEXT,
  answer VARCHAR(500),
  analysis TEXT,
  difficulty DECIMAL(3,2) DEFAULT 0.60,
  knowledge_points TEXT,
  tags TEXT,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  is_deleted INT DEFAULT 0,
  INDEX idx_homework(homework_id)
);

-- ============================================================
-- 9. homework_answer (Student homework submissions)
-- ============================================================
CREATE TABLE IF NOT EXISTS homework_answer (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  homework_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  answers TEXT NOT NULL,
  score DECIMAL(5,2),
  submit_time DATETIME,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_homework_user(homework_id, user_id)
);

-- ============================================================
-- 10. class_exercise (In-class exercises)
-- ============================================================
CREATE TABLE IF NOT EXISTS class_exercise (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  course_id BIGINT NOT NULL,
  name VARCHAR(200) NOT NULL,
  type VARCHAR(20) DEFAULT 'question',
  score_type VARCHAR(20) DEFAULT 'average',
  start_time DATETIME,
  end_time DATETIME,
  status VARCHAR(20) DEFAULT 'draft',
  completed_count INT DEFAULT 0,
  uncompleted_count INT DEFAULT 0,
  question_count INT DEFAULT 0,
  creator_id BIGINT,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted INT DEFAULT 0,
  INDEX idx_course(course_id)
);

-- ============================================================
-- 11. exercise_question (Exercise question items)
-- ============================================================
CREATE TABLE IF NOT EXISTS exercise_question (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  exercise_id BIGINT NOT NULL,
  sort_order INT DEFAULT 0,
  type VARCHAR(20) NOT NULL,
  content TEXT NOT NULL,
  options TEXT,
  answer VARCHAR(500),
  analysis TEXT,
  difficulty DECIMAL(3,2) DEFAULT 0.60,
  knowledge_points TEXT,
  tags TEXT,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  is_deleted INT DEFAULT 0,
  INDEX idx_exercise(exercise_id)
);

-- ============================================================
-- 12. exercise_answer (Student exercise submissions)
-- ============================================================
CREATE TABLE IF NOT EXISTS exercise_answer (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  exercise_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  answers TEXT NOT NULL,
  score DECIMAL(5,2),
  submit_time DATETIME,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_exercise_user(exercise_id, user_id)
);

-- ============================================================
-- 13. training (Practice training assignments)
-- ============================================================
CREATE TABLE IF NOT EXISTS training (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  course_id BIGINT NOT NULL,
  name VARCHAR(200) NOT NULL,
  cover VARCHAR(500),
  description TEXT,
  start_time DATETIME,
  end_time DATETIME,
  status VARCHAR(20) DEFAULT 'draft',
  pending_count INT DEFAULT 0,
  submitted_count INT DEFAULT 0,
  unsubmitted_count INT DEFAULT 0,
  creator_id BIGINT,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted INT DEFAULT 0,
  INDEX idx_course(course_id)
);

-- ============================================================
-- 14. training_node (Training tree nodes)
-- ============================================================
CREATE TABLE IF NOT EXISTS training_node (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  training_id BIGINT NOT NULL,
  parent_id BIGINT DEFAULT 0,
  node_index VARCHAR(20),
  label VARCHAR(200) NOT NULL,
  content TEXT,
  sort_order INT DEFAULT 0,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  is_deleted INT DEFAULT 0,
  INDEX idx_training(training_id),
  INDEX idx_parent(parent_id)
);

-- ============================================================
-- 15. training_student_task (Student training task progress)
-- ============================================================
CREATE TABLE IF NOT EXISTS training_student_task (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  training_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  name VARCHAR(100),
  student_id VARCHAR(50),
  class_name VARCHAR(200),
  training_status VARCHAR(20) DEFAULT 'notStarted',
  total_time INT DEFAULT 0,
  pass_rate VARCHAR(20),
  eval_count INT DEFAULT 0,
  final_score DECIMAL(5,2),
  reject_count INT DEFAULT 0,
  review_status VARCHAR(20) DEFAULT 'pending',
  comment TEXT,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_training_user(training_id, user_id),
  INDEX idx_training(training_id)
);

-- ============================================================
-- 16. training_stage_score (Per-stage score in training)
-- ============================================================
CREATE TABLE IF NOT EXISTS training_stage_score (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  task_id BIGINT NOT NULL,
  stage INT,
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

-- ============================================================
-- 17. check_in (Check-in/attendance sessions)
-- ============================================================
CREATE TABLE IF NOT EXISTS check_in (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  course_id BIGINT NOT NULL,
  class_id BIGINT,
  name VARCHAR(200) NOT NULL,
  date DATE,
  type VARCHAR(20) DEFAULT 'qrcode',
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
  status VARCHAR(20) DEFAULT 'notStarted',
  creator_id BIGINT,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  is_deleted INT DEFAULT 0,
  INDEX idx_course(course_id)
);

-- ============================================================
-- 18. check_in_record (Individual check-in records)
-- ============================================================
CREATE TABLE IF NOT EXISTS check_in_record (
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

-- ============================================================
-- 19. question_bank (Question bank collections)
-- ============================================================
CREATE TABLE IF NOT EXISTS question_bank (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(200) NOT NULL,
  category VARCHAR(50),
  creator VARCHAR(100),
  department VARCHAR(150),
  question_count INT DEFAULT 0,
  status VARCHAR(20) DEFAULT 'enabled',
  creator_id BIGINT,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted INT DEFAULT 0
);

-- ============================================================
-- 20. question_bank_item (Individual questions in a bank)
-- ============================================================
CREATE TABLE IF NOT EXISTS question_bank_item (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  bank_id BIGINT NOT NULL,
  type VARCHAR(20) NOT NULL,
  content TEXT NOT NULL,
  options TEXT,
  answer VARCHAR(500),
  analysis TEXT,
  difficulty DECIMAL(3,2) DEFAULT 0.60,
  knowledge_points TEXT,
  tags TEXT,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  is_deleted INT DEFAULT 0,
  INDEX idx_bank(bank_id)
);

-- ============================================================
-- 21. score_weight (Course score weight configuration)
-- ============================================================
CREATE TABLE IF NOT EXISTS score_weight (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  course_id BIGINT NOT NULL UNIQUE,
  weights TEXT NOT NULL,
  updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ============================================================
-- 22. course_notice (Course announcements)
-- ============================================================
CREATE TABLE IF NOT EXISTS course_notice (
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

-- ============================================================
-- 23. course_material (Course materials/resources)
-- ============================================================
CREATE TABLE IF NOT EXISTS course_material (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  course_id BIGINT NOT NULL,
  name VARCHAR(200) NOT NULL,
  file_url VARCHAR(500),
  file_type VARCHAR(50),
  file_size BIGINT,
  creator_id BIGINT,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  is_deleted INT DEFAULT 0,
  INDEX idx_course(course_id)
);

-- ============================================================
-- 24. vanx_platf_roles (Platform roles)
-- ============================================================
CREATE TABLE IF NOT EXISTS vanx_platf_roles (
  role_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  role_key VARCHAR(100),
  role_name VARCHAR(100),
  frontend_show_name VARCHAR(100),
  role_categ_name VARCHAR(100),
  role_sort INT DEFAULT 0,
  role_status VARCHAR(10) DEFAULT '1',
  add_source VARCHAR(20) DEFAULT '1',
  editor_id BIGINT,
  creator_id BIGINT,
  modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  other_info_one VARCHAR(50),
  other_info_two VARCHAR(50),
  remark VARCHAR(50),
  is_deleted INT DEFAULT 0
);

-- ============================================================
-- 25. vanx_platf_role_menu (Role-menu permission mapping)
-- ============================================================
CREATE TABLE IF NOT EXISTS vanx_platf_role_menu (
  permission_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  role_id BIGINT,
  menu_id VARCHAR(100),
  editor_id BIGINT,
  creator_id BIGINT,
  modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  other_info_one VARCHAR(50),
  other_info_two VARCHAR(50),
  remark VARCHAR(50),
  is_deleted INT DEFAULT 0
);

-- ============================================================
-- 26. vanx_platf_user_roles (User-role binding)
-- ============================================================
CREATE TABLE IF NOT EXISTS vanx_platf_user_roles (
  user_role_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT,
  role_id BIGINT,
  is_active TINYINT DEFAULT 1,
  user_role_status VARCHAR(10) DEFAULT '2',
  reject_reason VARCHAR(500),
  platform_id BIGINT,
  platform_user_id BIGINT,
  pass_time DATETIME,
  editor_id BIGINT,
  creator_id BIGINT,
  modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  other_info_one VARCHAR(50),
  other_info_two VARCHAR(50),
  remark VARCHAR(50),
  is_deleted INT DEFAULT 0
);


-- ============================================================
-- SEED DATA
-- ============================================================

-- ============================================================
-- Seed: Platform Roles
-- ============================================================
INSERT IGNORE INTO vanx_platf_roles (role_id, role_key, role_name, frontend_show_name, role_categ_name, role_sort, role_status) VALUES
(1, 'admin', '管理员', '管理员', '系统管理', 1, '1'),
(2, 'teacher', '教师', '教师', '教学管理', 2, '1'),
(3, 'student', '学生', '学生', '学习管理', 3, '1');

-- ============================================================
-- Seed: User-Role Bindings
-- ============================================================
INSERT IGNORE INTO vanx_platf_user_roles (user_id, role_id, is_active, user_role_status) VALUES
(1001, 1, 1, '2'),
(2001, 2, 1, '2'),
(2002, 2, 1, '2'),
(3001, 3, 1, '2'),
(3002, 3, 1, '2');

-- ============================================================
-- Seed: User Info
-- ============================================================
INSERT IGNORE INTO user_info (user_id, user_type, user_name, school, sex, created_time) VALUES
(1001, 'admin', '系统管理员', 'henu', '男', NOW()),
(2001, 'teacher', '张三', 'henu', '男', NOW()),
(2002, 'teacher', '王教授', 'henu', '男', NOW()),
(3001, 'student', '李同学', 'henu', '女', NOW()),
(3002, 'student', '赵同学', 'henu', '男', NOW());

-- ============================================================
-- Seed: Courses
-- ============================================================
INSERT IGNORE INTO course (id, course_code, name, category, belong_unit, department, teacher_name, status, creator_id) VALUES
(1, 'AI20250298', '人工智能', '专业选修课', '计算机学院', '人工智能系', '李萧红教授', 'published', 2001),
(2, 'DM20250101', '离散数学', '专业必修课', '计算机学院', '计算机科学系', '王教授', 'published', 2002);

-- ============================================================
-- Seed: Course Teachers (for course 1)
-- ============================================================
INSERT IGNORE INTO course_teacher (course_id, name, role, work_no, department, user_id) VALUES
(1, '张三', '主讲教师', 'T20250001', '人工智能系', 2001),
(1, '李四', '助教', 'TA20250001', '人工智能系', NULL),
(1, '王五', '助教', 'TA20250002', '人工智能系', NULL),
(1, '赵六', '辅导员', 'C20250001', '学生工作处', NULL);

-- ============================================================
-- Seed: Course Classes (for course 1)
-- ============================================================
INSERT IGNORE INTO course_class (course_id, name, student_count) VALUES
(1, '2024级软件工程1班', 45),
(1, '2024级计算机科学2班', 42),
(1, '2024级软件工程2班', 48),
(1, '2024级人工智能1班', 35);

-- ============================================================
-- Seed: Course Students (class_id=1, course_id=1)
-- ============================================================
INSERT IGNORE INTO course_student (class_id, course_id, user_id, student_id, name, department, major, class_name) VALUES
(1, 1, 3001, '20240001', '李同学', '计算机学院', '软件工程', '2024级软件工程1班'),
(1, 1, 3002, '20240002', '赵同学', '计算机学院', '软件工程', '2024级软件工程1班'),
(1, 1, 3003, '20240003', '孙同学', '计算机学院', '软件工程', '2024级软件工程1班'),
(1, 1, 3004, '20240004', '周同学', '计算机学院', '软件工程', '2024级软件工程1班'),
(1, 1, 3005, '20240005', '吴同学', '计算机学院', '软件工程', '2024级软件工程1班');

-- ============================================================
-- Seed: Course Terms (for course 1)
-- ============================================================
INSERT IGNORE INTO course_term (course_id, name, start_time, end_time, status, info) VALUES
(1, '2025-2026第一学期', '2025-09-01 00:00:00', '2026-01-15 23:59:59', 'ongoing', '人工智能课程第一学期教学安排');

-- ============================================================
-- Seed: Homework (for course 1)
-- ============================================================
INSERT IGNORE INTO homework (id, course_id, class_id, name, type, status, creator_id) VALUES
(1, 1, 1, '实验一课后习题', 'question', 'draft', 2001),
(2, 1, 1, '实验二编程练习', 'question', 'published', 2001);

-- ============================================================
-- Seed: Homework Questions (for homework 2)
-- ============================================================
INSERT IGNORE INTO homework_question (homework_id, sort_order, type, content, options, answer, analysis, difficulty, knowledge_points) VALUES
(2, 1, 'single', '以下哪个是人工智能的子领域？', '["A.机器学习", "B.数据库管理", "C.网络工程", "D.操作系统"]', 'A', '机器学习是人工智能的核心子领域之一，涉及让计算机通过数据学习规律。', 0.60, '人工智能基础'),
(2, 2, 'multiple', '以下哪些属于深度学习框架？（多选）', '["A.PyTorch", "B.TensorFlow", "C.Spring Boot", "D.Keras"]', 'A,B,D', 'PyTorch、TensorFlow和Keras都是主流深度学习框架，Spring Boot是Java Web框架。', 0.70, '深度学习'),
(2, 3, 'fill', '反向传播算法的核心思想是利用___法则计算损失函数对各层权重的梯度。', NULL, '链式', '反向传播通过链式法则逐层计算梯度，是训练神经网络的基础算法。', 0.80, '神经网络');

-- ============================================================
-- Seed: Class Exercises (for course 1)
-- ============================================================
INSERT IGNORE INTO class_exercise (id, course_id, name, type, status, question_count, creator_id) VALUES
(1, 1, '课堂练习0226', 'question', 'published', 2, 2001);

-- ============================================================
-- Seed: Exercise Questions (for exercise 1)
-- ============================================================
INSERT IGNORE INTO exercise_question (exercise_id, sort_order, type, content, options, answer, analysis, difficulty, knowledge_points) VALUES
(1, 1, 'single', 'Python中用于定义函数的关键字是？', '["A.def", "B.func", "C.function", "D.define"]', 'A', 'Python使用def关键字来定义函数，语法为def function_name(parameters):', 0.40, 'Python基础'),
(1, 2, 'single', '下列哪种数据结构遵循先进先出（FIFO）原则？', '["A.栈", "B.队列", "C.二叉树", "D.哈希表"]', 'B', '队列遵循先进先出原则，栈遵循后进先出（LIFO）原则。', 0.50, '数据结构');

-- ============================================================
-- Seed: Training (for course 1)
-- ============================================================
INSERT IGNORE INTO training (id, course_id, name, cover, description, status, creator_id) VALUES
(1, 1, 'Python基础实训', NULL, 'Python编程语言基础实训，涵盖变量、数据类型、控制流等知识点。', 'published', 2001),
(2, 1, '机器学习入门实训', NULL, '机器学习基本概念与常用算法实训，包括线性回归、决策树等。', 'published', 2001);

-- ============================================================
-- Seed: Question Banks
-- ============================================================
INSERT IGNORE INTO question_bank (id, name, category, creator, department, question_count, creator_id) VALUES
(1, 'Python基础', '编程基础', '张三', '人工智能系', 50, 2001),
(2, '数据结构', '计算机基础', '张三', '人工智能系', 80, 2001),
(3, '算法设计', '计算机基础', '李四', '人工智能系', 60, NULL),
(4, 'MySQL数据库', '数据库', '王五', '人工智能系', 45, NULL);

-- ============================================================
-- Seed: Check-in (for course 1)
-- ============================================================
INSERT IGNORE INTO check_in (id, course_id, class_id, name, date, type, duration_minutes, status, creator_id) VALUES
(1, 1, 1, '第1周课堂签到', '2025-09-05', 'qrcode', 30, 'ended', 2001),
(2, 1, 1, '第2周课堂签到', '2025-09-12', 'qrcode', 30, 'ended', 2001);

-- ============================================================
-- Seed: Score Weight (for course 1)
-- ============================================================
INSERT IGNORE INTO score_weight (course_id, weights) VALUES
(1, '{"homework":30,"exercise":10,"training":30,"checkIn":10,"finalExam":20}');

-- ============================================================
-- Seed: Course Notices (for course 1)
-- ============================================================
INSERT IGNORE INTO course_notice (course_id, title, content, is_top, creator_id) VALUES
(1, '开学通知', '欢迎同学们选修人工智能课程！本学期课程将于9月1日正式开始，请同学们提前做好准备。课程教学大纲和参考资料已上传至课程资料区。', 1, 2001),
(1, '实验环境配置说明', '请同学们在课前完成Python开发环境的配置。推荐使用Anaconda + Jupyter Notebook，详细安装步骤请参考课程资料区的配置文档。', 0, 2001);
