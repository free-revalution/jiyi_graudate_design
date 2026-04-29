/*
 Navicat Premium Dump SQL

 Source Server         : mysql
 Source Server Type    : MySQL
 Source Server Version : 80019 (8.0.19)
 Source Host           : localhost:3306
 Source Schema         : vanx_user_permiss

 Target Server Type    : MySQL
 Target Server Version : 80019 (8.0.19)
 File Encoding         : 65001

 Date: 29/04/2026 11:09:32
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user_authorize
-- ----------------------------
DROP TABLE IF EXISTS `user_authorize`;
CREATE TABLE `user_authorize`  (
  `authorize_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id号',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户id',
  `user_status` varchar(5) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '1' COMMENT '用户状态 1.正常 2.禁用;3.其他',
  `identity_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '登录类型(手机号/密码/验证码/微信/QQ/支付宝等)',
  `identifier` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标识(手机号/邮箱/第三方平台的唯一ID)',
  `credential` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '凭证(密码/token/unionId等)',
  `login_time` datetime(6) NULL DEFAULT NULL,
  `login_ip` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户登录的设备ip',
  `login_note` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '登录情况备注(是否异常，异常情况)',
  `login_status` varchar(5) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '登录情况状态 1 正常 2 异常 3 其他',
  `access_token` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '访问令牌',
  `refresh_token` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '刷新令牌',
  `expires_in` bigint NULL DEFAULT NULL COMMENT '过期时间',
  `oauth_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '第三方返回的原始用户信息(JSON格式)',
  `editor_id` bigint NULL DEFAULT NULL COMMENT '最后修改者用户id',
  `creator_id` bigint NULL DEFAULT NULL COMMENT '创建者用户id',
  `modified_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `created_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `other_info_one` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '其他信息1',
  `other_info_two` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '其他信息2',
  `remark` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `is_deleted` int NULL DEFAULT NULL,
  PRIMARY KEY (`authorize_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户授权表(即各种登录方式，支持第三方登录)' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_authorize
-- ----------------------------
INSERT INTO `user_authorize` VALUES (1, 1001, '1', 'student_id', '20240001', '$2a$10$vLqHIH97k5NzQcGtSLN6WO1suXePq.CmwCpmRQ07v9VISySNC1mFC', '2026-04-29 11:06:49.372000', '127.0.0.1', NULL, '2', NULL, NULL, NULL, NULL, NULL, NULL, '2026-04-29 11:06:52', '2026-04-29 09:22:08', NULL, NULL, NULL, 0);
INSERT INTO `user_authorize` VALUES (2, 1002, '1', 'student_id', '20240002', '$2a$10$vLqHIH97k5NzQcGtSLN6WO1suXePq.CmwCpmRQ07v9VISySNC1mFC', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-04-29 09:22:08', NULL, NULL, NULL, 0);
INSERT INTO `user_authorize` VALUES (3, 2001, '1', 'teacher_id', 'T001', '$2a$10$vLqHIH97k5NzQcGtSLN6WO1suXePq.CmwCpmRQ07v9VISySNC1mFC', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-04-29 09:22:08', NULL, NULL, NULL, 0);
INSERT INTO `user_authorize` VALUES (4, 2002, '1', 'teacher_id', 'T002', '$2a$10$vLqHIH97k5NzQcGtSLN6WO1suXePq.CmwCpmRQ07v9VISySNC1mFC', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-04-29 09:22:08', NULL, NULL, NULL, 0);
INSERT INTO `user_authorize` VALUES (5, 3001, '1', 'phone', '15720801803', '$2a$10$vLqHIH97k5NzQcGtSLN6WO1suXePq.CmwCpmRQ07v9VISySNC1mFC', '2026-04-29 11:06:13.095000', '127.0.0.1', NULL, '1', 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIzMDAxIiwiaWRlbnRpZmllciI6IjE1NzIwODAxODAzIiwiaWF0IjoxNzc3NDMxOTczLCJleHAiOjE3Nzc0MzkxNzN9.PVcxOT41Tq34-e8j9Ssj0qIT8jvhrhkh8YFbOmtLbiI', 'e501c061fb53413795c7acf36394e423', 7200, NULL, NULL, NULL, '2026-04-29 11:06:13', '2026-04-29 09:22:08', NULL, NULL, NULL, 0);
INSERT INTO `user_authorize` VALUES (6, 3002, '1', 'student_id', '20240088', '$2a$10$H5te4dJJNhHr57rvGhTeouakKCK3FPh/Ctu89BUv8EBl9Co5sF98q', '2026-04-29 10:51:21.842000', '127.0.0.1', NULL, '2', NULL, NULL, NULL, NULL, NULL, NULL, '2026-04-29 10:51:26', '2026-04-29 10:47:34', 'henu', '18888888888', NULL, NULL);
INSERT INTO `user_authorize` VALUES (7, 3003, '1', 'student_id', '20240099', '$2a$10$yO3kId4bM/sE3/WvEpMoJ.KzQxeg.rsV3XZGwJo5NjkJvcVQGjKJ2', '2026-04-29 11:07:43.343000', '127.0.0.1', NULL, '2', NULL, NULL, NULL, NULL, NULL, NULL, '2026-04-29 11:07:47', '2026-04-29 11:07:27', 'henu', '13888888888', NULL, NULL);

-- ----------------------------
-- Table structure for user_device_login_record
-- ----------------------------
DROP TABLE IF EXISTS `user_device_login_record`;
CREATE TABLE `user_device_login_record`  (
  `device_login_record_id` bigint NOT NULL COMMENT '用户设备记录id',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户id',
  `user_photo` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户默认头像',
  `social_circle_id` bigint NULL DEFAULT NULL COMMENT '圈子id',
  `device_uuid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '500',
  `login_time` datetime NULL DEFAULT NULL COMMENT '登录时间',
  `identity_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '登录类型(手机号)',
  `identifier` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标识(手机号)',
  `created_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `modified_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `creator_id` bigint NULL DEFAULT NULL COMMENT '创建者用户id',
  `editor_id` bigint NULL DEFAULT NULL COMMENT '最后修改者用户id',
  `other_info_one` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '其他信息1',
  `other_info_two` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '其他信息2',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `is_deleted` int NULL DEFAULT 0 COMMENT '是否删除（1-未删除，2-删除默认1）',
  PRIMARY KEY (`device_login_record_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_device_login_record
-- ----------------------------

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info`  (
  `user_id` bigint NOT NULL COMMENT '主键id',
  `created_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `modified_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `creator_id` bigint NULL DEFAULT NULL COMMENT '创建者用户id',
  `editor_id` bigint NULL DEFAULT NULL COMMENT '最后修改者用户id',
  `user_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户类型',
  `user_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户真实姓名(加密)',
  `user_nickname` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `user_photo` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户头像',
  `user_motto` varchar(80) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户的座右铭或个人签名',
  `user_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户(代理人)编号',
  `sex` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '性别',
  `birthday` date NULL DEFAULT NULL COMMENT '生日',
  `credential_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户的证件类型(身份证,护照)',
  `credential_number` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户证件号码(加密)',
  `user_internal_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '系统生成的账号（唯一），即：Vanx号',
  `tel` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户手机号(加密)',
  `email` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户邮箱(加密)',
  `role_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户的角色类型:1.代理人:agent;4.销售主管:sales_manager',
  `add_source` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户来源：自定义添加、平台添加',
  `other_info_one` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '其他信息1',
  `other_info_two` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '其他信息2',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `is_deleted` int NULL DEFAULT 0 COMMENT '是否删除（1-未删除，2-删除默认1）',
  `age` int NULL DEFAULT NULL COMMENT '用户年龄',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户基本信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_info
-- ----------------------------

-- ----------------------------
-- Table structure for user_info_fast
-- ----------------------------
DROP TABLE IF EXISTS `user_info_fast`;
CREATE TABLE `user_info_fast`  (
  `user_fast_id` bigint NOT NULL COMMENT 'id',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户id',
  `longitude` double(14, 2) NULL DEFAULT 0.00 COMMENT '用户所在位置的经度',
  `latitude` double(14, 2) NULL DEFAULT 0.00 COMMENT '用户所在位置纬度',
  `media_current_page` bigint NULL DEFAULT 1 COMMENT '用户浏览推荐视频当前页码',
  `growth_value` double(14, 2) NULL DEFAULT 0.00 COMMENT '用户的成长值',
  `user_amount_balance` decimal(14, 2) NULL DEFAULT NULL COMMENT '用户账户余额',
  `user_virtual_balance` decimal(14, 2) NULL DEFAULT NULL COMMENT '用户系统中虚拟币的余额',
  `is_paid_memb_user` tinyint(1) NULL DEFAULT 0 COMMENT '是否是会员用户',
  `is_real_name_authenti` tinyint(1) NULL DEFAULT 0 COMMENT '是否实名认证',
  `need_pop_up_advert` tinyint(1) NULL DEFAULT 0 COMMENT '该用户是否被需要弹出广告',
  `user_behavior_points` double(14, 2) NULL DEFAULT 0.00 COMMENT '用户的行为积分，即该字段只能增加，不能进行兑换，仅为计算用户成长值',
  `user_shopping_points` double(14, 2) NULL DEFAULT 0.00 COMMENT '用户的购物积分，即该字段不能进行兑换，仅为计算用户成长值',
  `user_game_points` double(14, 2) NULL DEFAULT 0.00 COMMENT '用户的游戏积分，即该字段不能进行兑换，仅为计算用户成长值',
  `dynamic_behavior_points` double(14, 2) NULL DEFAULT 0.00 COMMENT '用户的动态行为积分，即该积分可以增加或减扣',
  `dynamic_shopping_points` double(14, 2) NULL DEFAULT 0.00 COMMENT '用户的动态购物积分，即该积分可以增加或减扣',
  `dynamic_game_points` double(14, 2) NULL DEFAULT 0.00 COMMENT '用户的动态游戏积分，即该积分可以增加或减扣',
  `used_total_disk_size` double(14, 2) NULL DEFAULT 0.00 COMMENT '用户已经使用的全部云盘大小',
  `user_public_disk_size` double(14, 2) NULL DEFAULT 0.00 COMMENT '用户公开、粉丝、好友圈使用的云盘大小',
  `user_priv_disk_size` double(14, 2) NULL DEFAULT 0.00 COMMENT '用户使用的私有空间大小',
  `recommend_value` double(14, 2) NULL DEFAULT NULL COMMENT '该用户的人气；或者推荐指数',
  `money_of_received_item` decimal(14, 2) NULL DEFAULT NULL COMMENT '该用户收到别人送的礼物的金钱数量(金额)',
  `money_for_recommend` decimal(14, 2) NULL DEFAULT NULL COMMENT '为提高该用户的人气；或者推荐指数而消费的金钱数量(金额)',
  `num_of_followers` tinyint NULL DEFAULT NULL COMMENT '该用户的粉丝数量',
  `user_nickname` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `role_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户角色类型',
  `creator_id` bigint NULL DEFAULT NULL COMMENT '创建者用户id',
  `editor_id` bigint NULL DEFAULT NULL COMMENT '修改者用户id',
  `created_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `modified_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  `other_info_one` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '其他信息1',
  `other_info_two` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '其他信息2',
  `remark` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_fast_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_info_fast
-- ----------------------------

-- ----------------------------
-- Table structure for vanx_platf_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `vanx_platf_role_menu`;
CREATE TABLE `vanx_platf_role_menu`  (
  `permission_id` bigint NOT NULL COMMENT '权限id号',
  `role_id` bigint NULL DEFAULT NULL COMMENT '角色id号',
  `menu_id` bigint NULL DEFAULT NULL COMMENT '菜单id号',
  `created_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `modified_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `creator_id` bigint NULL DEFAULT NULL COMMENT '创建者用户id',
  `editor_id` bigint NULL DEFAULT NULL COMMENT '最后修改者用户id',
  `other_info_one` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '其他信息1',
  `other_info_two` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '其他信息2',
  `remark` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除 1 未删除 2 删除 默认 1',
  PRIMARY KEY (`permission_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '菜单权限功能表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of vanx_platf_role_menu
-- ----------------------------

-- ----------------------------
-- Table structure for vanx_platf_roles
-- ----------------------------
DROP TABLE IF EXISTS `vanx_platf_roles`;
CREATE TABLE `vanx_platf_roles`  (
  `role_id` bigint NOT NULL COMMENT '角色id号',
  `created_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `modified_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `creator_id` bigint NULL DEFAULT NULL COMMENT '创建者用户id',
  `editor_id` bigint NULL DEFAULT NULL COMMENT '最后修改者用户id',
  `role_key` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色权限码',
  `role_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色名称；1 普通代理人用户 2 用户所在系统管理员用户；3 用户所在系统超级管理用户；4平台的管理员用户；5 用户所在平台的超级管理员用户',
  `frontend_show_name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色前端显示名称',
  `role_categ_name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色所属大类名称',
  `role_sort` int NULL DEFAULT NULL COMMENT '角色的显示顺序',
  `role_status` varchar(5) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色状态 1 正常 2 禁用 3 其他',
  `add_source` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色状态 1 用户自定义 2 平台增加',
  `other_info_one` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '其他信息1',
  `other_info_two` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '其他信息2',
  `remark` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除 0未删除 1 删除 默认 0',
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of vanx_platf_roles
-- ----------------------------

-- ----------------------------
-- Table structure for vanx_platf_user_roles
-- ----------------------------
DROP TABLE IF EXISTS `vanx_platf_user_roles`;
CREATE TABLE `vanx_platf_user_roles`  (
  `user_role_id` bigint NOT NULL COMMENT '用户角色id',
  `created_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `modified_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `creator_id` bigint NULL DEFAULT NULL COMMENT '创建者用户id，及创建者用户的id号码',
  `editor_id` bigint NULL DEFAULT NULL COMMENT '最后修改者用户id',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户的id号',
  `role_id` bigint NULL DEFAULT NULL COMMENT '角色id号',
  `is_active` tinyint(1) NULL DEFAULT 1 COMMENT '用户是否活跃，及用户的角色状态',
  `user_role_status` tinyint NULL DEFAULT 1 COMMENT '用户角色审核状态，0驳回 1 待审核 2 审核通过',
  `reject_reason` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '审核失败原因',
  `platform_id` bigint NULL DEFAULT NULL COMMENT '审核人id号',
  `platform_user_id` bigint NULL DEFAULT NULL COMMENT '批注人id号',
  `pass_time` datetime NULL DEFAULT NULL COMMENT '审核通过时间',
  `other_info_one` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '其他信息1',
  `other_info_two` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '其他信息2',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除，0 未删除 1 删除 默认 0',
  PRIMARY KEY (`user_role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户角色关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of vanx_platf_user_roles
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
