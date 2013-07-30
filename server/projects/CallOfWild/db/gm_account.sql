/*删除数据库*/
DROP DATABASE IF EXISTS gm_account;
/*创建数据库*/
CREATE DATABASE gm_account DEFAULT CHARACTER SET UTF8;

/*使用当前DB*/
USE gm_account; 

/*
	服务器配置表
*/
DROP TABLE IF EXISTS gm_account.ServerConfig;
CREATE TABLE ServerConfig
(
	id INT PRIMARY KEY AUTO_INCREMENT,
	PORT INT NOT NULL,
	ip VARCHAR(15) NOT NULL,
	NAME VARCHAR(50),
	isnew BOOLEAN DEFAULT 1,
	domain_name VARCHAR(100),
	dbip VARCHAR(20),
	dbuser VARCHAR(20),
	dbpass VARCHAR(50),
	servercode VARCHAR(20),
	serverip VARCHAR(20),
	helpUrl VARCHAR(250),
	activityUrl VARCHAR(250)
) ENGINE=INNODB DEFAULT CHARSET=utf8;
/*
	用户登录记录
*/
DROP TABLE IF EXISTS gm_account.LoginServerinfo;
CREATE TABLE LoginServerinfo
(
	id INT PRIMARY KEY AUTO_INCREMENT,
	ServerConfigId INT,
	UserId INT ,
	LoginTime DATETIME NULL,
	LogoutTime DATETIME,
	remoteIp VARCHAR(32),
	sessionKey VARCHAR(256)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE INDEX IDX_LSI_USER_ID ON LoginServerinfo(UserId);

/**
	用户资料
*/
DROP TABLE IF EXISTS gm_account.Users;
CREATE TABLE Users
(
	id INT PRIMARY KEY,
	UserName VARCHAR(50) NOT NULL,
	PASSWORD VARCHAR(32) NOT NULL,
	Email VARCHAR(100),
	phone_no VARCHAR(100),
	CardId VARCHAR(18),
	RegeditDate DATETIME,
	platform VARCHAR(250),
	uid VARCHAR(150),
	auth_code VARCHAR(300),
	channel VARCHAR(300),
	phone_model VARCHAR(128),
	phone_resolution VARCHAR(150),
	phone_os VARCHAR(200),
	phone_manufacturer VARCHAR(200),
	phone_imei VARCHAR(200),
	phone_mac VARCHAR(200)	
	
) ENGINE=INNODB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS gm_account.recharge;
CREATE TABLE recharge
(
	order_serial VARCHAR(60) PRIMARY KEY,
	player_id NUMERIC(21),
	create_time DATETIME,
	pay_status INT,
	order_money INT,
	goods_count INT,
	uin VARCHAR(50),
	deal_status BOOLEAN,
	chanel_code VARCHAR(30),
	db_host VARCHAR(60),
	db_port INT
) ENGINE=INNODB DEFAULT CHARSET=utf8;

#####  创建索引
CREATE INDEX IDX_RECHARGE_DEAL_STATUS ON recharge (deal_status);