-- 初始化测试用户数据
-- BCrypt加密后的密码: 123456

INSERT INTO user_authorize (authorize_id, user_id, user_status, identity_type, identifier, credential, created_time)
VALUES 
(1, 1001, '1', 'student_id', '20240001', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', NOW()),
(2, 1002, '1', 'student_id', '20240002', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', NOW()),
(3, 2001, '1', 'teacher_id', 'T001', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', NOW()),
(4, 2002, '1', 'teacher_id', 'T002', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', NOW()),
(5, 3001, '1', 'phone', '15720801803', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', NOW())
ON DUPLICATE KEY UPDATE user_status = VALUES(user_status);