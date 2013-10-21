DROP DATABASE IF EXISTS cow_portal;

CREATE DATABASE cow_portal DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

USE cow_portal;

/*
    服务器配置表
*/
DROP TABLE IF EXISTS ServerConfig;
CREATE TABLE ServerConfig
(
  serverId INT PRIMARY KEY,
  port INT NOT NULL,
  ip VARCHAR(15) NOT NULL,
  name VARCHAR(30),
  isNew BOOL DEFAULT 1,
  domainName VARCHAR(100),
  helpUrl VARCHAR(200),
  activityUrl VARCHAR(200)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

/*
    用户登录记录
*/
DROP TABLE IF EXISTS LoginTrace;
CREATE TABLE LoginTrace
(
  id INT PRIMARY KEY AUTO_INCREMENT,
  serverId INT,
  userId INT,
  loginTime DATETIME,
  logoutTime DATETIME,
  peerIp VARCHAR(15),
  peerPort INT,
  sessionKey VARCHAR(100),
INDEX IDX_LT_USER_ID (userId)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

/*
    用户资料
*/
DROP TABLE IF EXISTS User;
CREATE TABLE User
(
  id INT PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  pwd VARCHAR(50) NOT NULL,
  email VARCHAR(100),
  phoneNo VARCHAR(30),
  cardId VARCHAR(30),
  registerDate DATETIME,
  platform VARCHAR(30),
  uid VARCHAR(100),
  authCode VARCHAR(200),
  channelId VARCHAR(30),
  phoneModel VARCHAR(100),
  phoneResolution VARCHAR(100),
  phoneOs VARCHAR(100),
  phoneManufacturer VARCHAR(100),
  phoneImei VARCHAR(100),
  phoneImsi VARCHAR(100),
  phoneMac VARCHAR(100)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

/*
    充值记录
*/
DROP TABLE IF EXISTS Recharge;
CREATE TABLE Recharge
(
  id INT NOT NULL AUTO_INCREMENT,
  orderSerial VARCHAR(100) PRIMARY KEY,
  playerId BIGINT,
  createTime DATETIME,
  payStatus INT,
  orderMoney INT,
  goodsCount INT,
  uin VARCHAR(50),
  dealStatus TINYINT,
  channelId VARCHAR(30),
  dbIp VARCHAR(15),
  dbPort INT,
  platform VARCHAR(30),
  serverId INT,
  imsi VARCHAR(100),
INDEX IDX_RECHARGE_DEAL_STATUS (dealStatus),
INDEX id (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

/*
    活动模板
*/
DROP TABLE IF EXISTS ActivityTemplate;
CREATE TABLE ActivityTemplate
(
    activityId INT PRIMARY KEY,
    name VARCHAR(30),                   ##活动名称
    publishTime DATETIME,               ##活动发布时间
    startTime DATETIME,                 ##开始时间
    endTime DATETIME,                   ##结束时间
    type INT,                           ##活动类型
    status INT,                         ##活动状态0代表未发布, 1代表已发布, 2代表已过期, 3代表已撤销
    description VARCHAR(500)            ##活动说明
) ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

/*
    活动内容
*/
DROP TABLE IF EXISTS ActivityContent;
CREATE TABLE ActivityContent(
    id INT PRIMARY KEY AUTO_INCREMENT,
    activityId INT,                     ##活动Id, 外键
    note VARCHAR(500),                  ##活动说明
    limitType INT,                      ##活动限制类型0代表单日, 1代表活动期间
    limitCount INT,                     ##活动限制次数0代表无次数限制
    demand VARCHAR(500),                ##活动达成条件
    award VARCHAR(500)                  ##活动奖励
) ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

/*
    活动在哪些服务器上
*/
DROP TABLE IF EXISTS ActivityInServer;
CREATE TABLE ActivityInServer(
    id INT PRIMARY KEY AUTO_INCREMENT,
    activityId INT,                     ##活动Id, 外键
    serverId INT                        ##0代表全区全服; 1,2,3,4代表各区服ID, 外键
) ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

