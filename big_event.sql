/*
 Navicat Premium Data Transfer

 Source Server         : root
 Source Server Type    : MySQL
 Source Server Version : 80039
 Source Host           : localhost:3306
 Source Schema         : big_event

 Target Server Type    : MySQL
 Target Server Version : 80039
 File Encoding         : 65001

 Date: 18/12/2025 15:05:20
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for announcement
-- ----------------------------
DROP TABLE IF EXISTS `announcement`;
CREATE TABLE `announcement`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告内容',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of announcement
-- ----------------------------
INSERT INTO `announcement` VALUES (4, ' 发发哇', '是f色鬼SegaeG', '2025-12-16 18:55:58', '2025-12-16 18:55:58');
INSERT INTO `announcement` VALUES (5, '453453453', '5434', '2025-12-16 19:12:24', '2025-12-16 19:12:24');
INSERT INTO `announcement` VALUES (6, '4534345345', '4534534', '2025-12-16 19:12:29', '2025-12-16 19:12:29');
INSERT INTO `announcement` VALUES (7, '534534534534', '434534‘i；i', '2025-12-16 19:12:38', '2025-12-16 19:13:53');

-- ----------------------------
-- Table structure for article
-- ----------------------------
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `title` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章标题',
  `content` varchar(10000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章内容',
  `cover_img` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章封面',
  `state` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '草稿' COMMENT '文章状态: 只能是[已发布] 或者 [草稿]',
  `category_id` int UNSIGNED NULL DEFAULT NULL COMMENT '文章分类ID',
  `create_user` int UNSIGNED NOT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_article_category`(`category_id` ASC) USING BTREE,
  INDEX `fk_article_user`(`create_user` ASC) USING BTREE,
  CONSTRAINT `fk_article_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_article_user` FOREIGN KEY (`create_user`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of article
-- ----------------------------

-- ----------------------------
-- Table structure for bug
-- ----------------------------
DROP TABLE IF EXISTS `bug`;
CREATE TABLE `bug`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID，唯一标识一个bug，自增生成',
  `title` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'bug标题，简洁描述问题核心',
  `severity` tinyint NOT NULL DEFAULT 2 COMMENT 'bug严重程度：1-一般（影响次要功能），2-严重（影响主要功能），3-致命（导致系统崩溃或数据丢失）',
  `priority` tinyint NOT NULL DEFAULT 2 COMMENT 'bug处理优先级：1-低（可延后处理），2-中（按计划处理），3-高（优先处理）',
  `module` tinyint NULL DEFAULT NULL COMMENT '所属模块：1-前端界面、2-后端逻辑、3-数据库、4-其他',
  `user_id` int NOT NULL COMMENT '报告人ID，记录提交该bug的用户ID',
  `create_time` datetime(3) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '创建时间，精确到毫秒，自动更新为记录插入时的时间',
  `update_time` datetime(3) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间，精确到毫秒，自动更新为记录最后修改时的时间',
  `remark` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '额外备注信息，复现步骤',
  `document` varchar(800) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '存储的文件',
  `status` tinyint NULL DEFAULT NULL COMMENT 'bug状态：1-待处理、2-处理中、3-已解决、4-已拒绝',
  `feedback` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '漏洞处理反馈',
  `handler_id` int NULL DEFAULT NULL COMMENT '处理人ID（关联用户表id）',
  PRIMARY KEY (`id`) USING BTREE COMMENT '主键索引，基于B树结构，确保id唯一且查询高效',
  INDEX `fk_reporter_id`(`user_id` ASC) USING BTREE COMMENT '报告人ID索引，提升按报告人查询bug列表的效率'
) ENGINE = InnoDB AUTO_INCREMENT = 64 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '软件缺陷管理表，存储bug的基本信息、严重程度、优先级等数据' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of bug
-- ----------------------------
INSERT INTO `bug` VALUES (17, '我我问问', 2, 1, 1, 15, '2025-12-15 14:45:07.865', '2025-12-15 14:45:07.864', NULL, NULL, 4, NULL, 15);
INSERT INTO `bug` VALUES (19, '人ca@', 1, 1, NULL, 15, '2025-12-16 16:29:10.936', '2025-12-16 16:29:10.935', NULL, NULL, 1, NULL, 12);
INSERT INTO `bug` VALUES (20, '7uu', 2, 1, 1, 15, '2025-11-15 15:48:59.630', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `bug` VALUES (21, '7uu', 2, 1, 1, 15, '2025-11-15 15:50:10.390', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `bug` VALUES (26, '11111111', 2, 1, 1, 15, '2025-12-01 23:32:07.362', '2025-12-01 23:32:07.361', NULL, 'http://localhost:8080/files/download/document_files/1763199648853_1.jpg', 3, NULL, 12);
INSERT INTO `bug` VALUES (43, '1', 2, 1, 1, 15, '2025-11-24 15:01:33.143', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `bug` VALUES (48, '1', 3, 3, 1, 12, '2025-12-15 14:44:58.120', '2025-12-15 14:44:58.119', '1', NULL, 4, NULL, 15);
INSERT INTO `bug` VALUES (49, '2', 2, 2, 1, 15, '2025-12-15 14:44:54.754', '2025-12-15 14:44:54.752', '2', 'http://localhost:8080/files/download/document_files/1764934312070_202413143500044 冯耀桂.docx', 3, NULL, 15);
INSERT INTO `bug` VALUES (50, '5', 2, 2, 2, 15, '2025-12-15 16:04:58.816', '2025-12-15 16:04:58.815', '7', 'http://localhost:8080/files/download/document_files/1764941449392_1763195074006_16_Ride_4k_24f57.jpg', 1, NULL, 15);
INSERT INTO `bug` VALUES (51, '5', 2, 2, 2, 15, '2025-12-15 14:44:47.588', '2025-12-15 14:44:47.579', '5', NULL, 1, NULL, 15);
INSERT INTO `bug` VALUES (52, '嗡嗡嗡嗡嗡嗡我', 3, 3, 1, 20, '2025-12-09 17:34:34.754', '2025-12-09 17:34:34.754', '12321', 'http://localhost:8080/files/download/document_files/1765250820613_A4PTD0M8LC7FUWH0J0@[SDS.jpg', NULL, NULL, NULL);
INSERT INTO `bug` VALUES (53, '12321', 3, 3, 1, 20, '2025-12-16 16:36:44.354', '2025-12-16 16:36:44.354', '嗡嗡嗡嗡嗡嗡我', 'http://localhost:8080/files/download/document_files/1765251051698_NSHM_PHOTO_2025_2_14_10_24_49.jpg', NULL, NULL, NULL);
INSERT INTO `bug` VALUES (54, '啊飒飒阿萨阿萨', 3, 3, 1, 20, '2025-12-16 16:24:38.549', '2025-12-16 16:24:38.548', '12萨', 'http://localhost:8080/files/download/document_files/1765273332149_NSHM_PHOTO_2025_2_19_14_29_08.jpg,http://localhost:8080/files/download/document_files/1765273332191_NSHM_PHOTO_2025_2_19_14_29_15.jpg', 4, NULL, 12);
INSERT INTO `bug` VALUES (55, '12321', 3, 3, 1, 20, '2025-12-16 16:24:36.615', '2025-12-16 16:24:36.614', '12321', 'http://localhost:8080/files/download/document_files/1765277604207_NSHM_PHOTO_2025_2_19_14_29_08.jpg', 3, NULL, 12);
INSERT INTO `bug` VALUES (60, '2787', 2, 2, 2, 20, '2025-12-16 16:24:34.793', '2025-12-16 16:24:34.791', '727', 'http://localhost:8080/files/download/document_files/1765436708491_A4PTD0M8LC7FUWH0J0@[SDS.jpg', 2, NULL, 12);
INSERT INTO `bug` VALUES (61, '42', 3, 2, 2, 20, '2025-12-16 16:24:53.793', '2025-12-16 16:24:53.791', '4524', 'http://localhost:8080/files/download/document_files/1765436832792_1.jpg', 1, NULL, 12);
INSERT INTO `bug` VALUES (62, '244354', 2, 2, 2, 20, '2025-12-16 16:24:30.899', '2025-12-16 16:24:30.894', '43534534534', 'http://localhost:8080/files/download/document_files/1765872194874_1763204982082_68f134525012a0f4f610c97a463b42e7.mp4', 1, NULL, 12);
INSERT INTO `bug` VALUES (63, '4634', 1, 3, 3, 15, '2025-12-16 16:09:48.357', NULL, '4534353435', 'http://localhost:8080/files/download/document_files/1765872588305_1763204982082_68f134525012a0f4f610c97a463b42e7.mp4,http://localhost:8080/files/download/document_files/1765872588307_1e1b685d95774ec29e1702107b6f49d8540e3af7.jpg,http://localhost:8080/files/download/document_files/1765872588311_1763195074006_16_Ride_4k_24f57.jpg,http://localhost:8080/files/download/document_files/1765872588353_1.jpg,http://localhost:8080/files/download/document_files/1765872588354_A4PTD0M8LC7FUWH0J0%40%5BSDS.jpg', NULL, NULL, NULL);

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `category_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `category_alias` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类别名',
  `create_user` int UNSIGNED NOT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_category_user`(`create_user` ASC) USING BTREE,
  CONSTRAINT `fk_category_user` FOREIGN KEY (`create_user`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (1, '美食', 'ms', 2, '2025-11-10 00:21:24', '2025-11-10 00:21:24');
INSERT INTO `category` VALUES (2, '美食', 'ms', 2, '2025-11-10 00:23:28', '2025-11-10 00:23:28');
INSERT INTO `category` VALUES (22, '美食', 'ms', 18, '2025-11-11 17:41:14', '2025-11-11 17:41:14');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `username` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '密码',
  `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '昵称',
  `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '邮箱',
  `user_pic` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '头像',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'USER' COMMENT '用户权限',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (2, 'ooo', 'e10adc3949ba59abbe56e057f20f883e', '', '', '', '2025-10-31 20:54:23', '2025-10-31 20:54:23', 'USER');
INSERT INTO `user` VALUES (7, 'chusheng1', 'd41d8cd98f00b204e9800998ecf8427e', '', '', '', '2025-11-02 21:35:24', '2025-11-02 21:35:24', 'USER');
INSERT INTO `user` VALUES (11, 'oooooo', 'e10adc3949ba59abbe56e057f20f883e', '6666', '273@163.com', NULL, '2025-11-03 00:00:56', '2025-12-15 15:11:12', 'USER');
INSERT INTO `user` VALUES (12, 'test123', 'cc03e747a6afbbcbf8be7668acfebee5', '44442', 'wacd@QQ.com', 'http://localhost:8080/files/download/1765437096664_A4PTD0M8LC7FUWH0J0%40%5BSDS.jpg', '2025-11-03 02:08:17', '2025-12-11 15:11:38', 'ADMIN');
INSERT INTO `user` VALUES (13, 'test1234', 'cc03e747a6afbbcbf8be7668acfebee5', '', '', '', '2025-11-03 02:12:31', '2025-11-03 02:12:31', 'USER');
INSERT INTO `user` VALUES (14, 'test1235555', 'cc03e747a6afbbcbf8be7668acfebee5', '', '', '', '2025-11-03 02:32:13', '2025-11-03 02:32:13', 'USER');
INSERT INTO `user` VALUES (15, 'pppppp', 'e10adc3949ba59abbe56e057f20f883e', 'wba', 'wacd@QQ.com', 'http://localhost:8080/files/download/1765438604719_1763195074006_16_Ride_4k_24f57.jpg', '2025-11-03 16:04:10', '2025-12-11 15:36:46', 'SUPER_ADMIN');
INSERT INTO `user` VALUES (16, '111111111', '7fa8282ad93047a4d6fe6111c93b308a', '', '', '', '2025-11-03 16:44:07', '2025-11-03 16:44:07', 'USER');
INSERT INTO `user` VALUES (17, 'test1233333', '3b4df34a31840b455ba7e8be9659030d', '', '', '', '2025-11-03 16:49:03', '2025-11-03 16:49:03', 'USER');
INSERT INTO `user` VALUES (18, 'ppppp', '51f6f8fe03a390d3de50ad49913d4b66', '2222', '1829182393@qq.com', NULL, '2025-11-03 23:13:11', '2025-12-15 16:58:54', 'USER');
INSERT INTO `user` VALUES (20, 'ling0', 'cc03e747a6afbbcbf8be7668acfebee5', 'test123 ', '123@163.com', 'http://localhost:8080/files/download/1765435499944_1763195074006_16_Ride_4k_24f57.jpg', '2025-12-09 11:05:25', '2025-12-11 14:45:09', 'USER');
INSERT INTO `user` VALUES (21, 'vsvss', 'e10adc3949ba59abbe56e057f20f883e', '222', '123@163.com', NULL, '2025-12-15 15:26:57', '2025-12-15 16:51:17', 'USER');

SET FOREIGN_KEY_CHECKS = 1;
