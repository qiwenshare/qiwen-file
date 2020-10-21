/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50730
Source Host           : localhost:3306
Source Database       : fileshare

Target Server Type    : MYSQL
Target Server Version : 50730
File Encoding         : 65001

Date: 2020-10-21 19:07:49
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for file
-- ----------------------------
DROP TABLE IF EXISTS `file`;
CREATE TABLE `file` (
  `fileId` bigint(20) NOT NULL,
  `extendName` varchar(255) DEFAULT NULL,
  `fileName` varchar(255) DEFAULT NULL,
  `filePath` varchar(255) DEFAULT NULL,
  `fileSize` bigint(20) DEFAULT NULL,
  `fileUrl` varchar(255) DEFAULT NULL,
  `isDir` int(11) DEFAULT NULL,
  `isOSS` int(11) DEFAULT NULL,
  `timeStampName` varchar(255) DEFAULT NULL,
  `uploadTime` varchar(255) DEFAULT NULL,
  `userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`fileId`),
  UNIQUE KEY `fileindex` (`fileName`,`filePath`,`extendName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for permission
-- ----------------------------
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission` (
  `permissionId` bigint(20) NOT NULL,
  `available` bit(1) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `parentId` bigint(20) DEFAULT NULL,
  `parentIds` varchar(255) DEFAULT NULL,
  `permission` varchar(255) DEFAULT NULL,
  `resourceType` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`permissionId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `roleId` bigint(20) NOT NULL,
  `available` bit(1) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`roleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for role_permission
-- ----------------------------
DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission` (
  `roleId` bigint(20) NOT NULL,
  `permissionId` bigint(20) NOT NULL,
  KEY `FKmsjtuo1smqbduu6wt9gekj7k6` (`permissionId`),
  KEY `FKsrw6jhwxy1l8i8urr987m0byj` (`roleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for storage
-- ----------------------------
DROP TABLE IF EXISTS `storage`;
CREATE TABLE `storage` (
  `storageId` bigint(20) NOT NULL,
  `storageSize` bigint(20) DEFAULT NULL,
  `userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`storageId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `userId` bigint(20) NOT NULL,
  `addrArea` varchar(255) DEFAULT NULL,
  `addrCity` varchar(255) DEFAULT NULL,
  `addrProvince` varchar(255) DEFAULT NULL,
  `birthday` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `imageUrl` varchar(255) DEFAULT NULL,
  `industry` varchar(255) DEFAULT NULL,
  `intro` varchar(255) DEFAULT NULL,
  `openId` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `position` varchar(255) DEFAULT NULL,
  `qqPassword` varchar(255) DEFAULT NULL,
  `realname` varchar(255) DEFAULT NULL,
  `registerTime` varchar(255) DEFAULT NULL,
  `salt` varchar(255) DEFAULT NULL,
  `sex` varchar(255) DEFAULT NULL,
  `telephone` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`userId`),
  UNIQUE KEY `openIdIndex` (`openId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role` (
  `userId` bigint(20) NOT NULL,
  `roleid` bigint(20) NOT NULL,
  KEY `FKbo5ik0bthje7hum554xb17ry6` (`roleid`),
  KEY `FKj5g46wgmq1wmqfhv78g7cxaqe` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- 初始化用户
insert ignore into user (userId, username, salt, password) value (1, 'admin', 'admin', 'df655ad8d3229f3269fad2a8bab59b6c');
insert ignore into role (roleId, role, description) values (1, 'admin', '管理员'),(2, 'user', '普通用户');
insert ignore into permission (permissionId, permission) values (1, 'admin'),(2, 'user');

delete from user_role where userId = 1 and roleId = 1;
insert into user_role (userId, roleId) value (1, 1);
delete from role_permission where roleId = 1 and permissionId = 1;
insert into role_permission (roleId, permissionId) value (1, 1);