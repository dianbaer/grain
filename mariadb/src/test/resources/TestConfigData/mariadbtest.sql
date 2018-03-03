/*
Navicat MySQL Data Transfer

Source Server         : localhost_3307
Source Server Version : 50505
Source Host           : localhost:3307
Source Database       : mariadbtest

Target Server Type    : MYSQL
Target Server Version : 50505
File Encoding         : 65001

Date: 2017-09-29 14:33:00
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for testtable
-- ----------------------------
DROP TABLE IF EXISTS `testtable`;
CREATE TABLE `testtable` (
  `id` varchar(64) NOT NULL,
  `name` varchar(255) NOT NULL,
  `time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
