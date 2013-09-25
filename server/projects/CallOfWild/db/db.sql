/*删除数据库*/
DROP DATABASE IF EXISTS callofwild;
/*创建数据库*/
CREATE DATABASE callofwild  DEFAULT CHARACTER SET UTF8;

/*使用当前DB*/
USE callofwild; 

/*
	玩家表
	drop table Player
*/
DROP TABLE IF EXISTS Player;
CREATE TABLE Player
(
	id NUMERIC(21) PRIMARY KEY,
	UserId INT,
	NAME VARCHAR(50),
	LEVEL INT ,
	Role INT,
	peerage INT,
	regtime DATETIME,
	is_newcomer_guide_finished BOOLEAN DEFAULT 0,
	newcomer_guide_stage INT DEFAULT 0,
	curChapterId INT,
	curSectionId INT	
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/************************************/

DROP TABLE IF EXISTS Map;
CREATE TABLE Map
(
	id NUMERIC(21) PRIMARY KEY,
	mapX INT,
	mapY INT,
	mapType INT,
	PlayerId NUMERIC(21)
) ENGINE=INNODB DEFAULT CHARSET=utf8;
/*
	城市(主城，附属城)
	DROP TABLE City
*/
DROP TABLE IF EXISTS City;
CREATE TABLE City
(
	id NUMERIC(21) PRIMARY KEY,
	LeaderId NUMERIC(21),
	PlayerId NUMERIC(21) ,
	LEVEL INT,
	CityType INT,
	CityNumber INT,
	Color INT,
	cityX INT ,
	cityY INT ,
	cityName VARCHAR(21),
	Woods INT ,
	Stones INT ,
	Irons INT ,
	Foods INT ,
	PersonInit INT ,
	PersonMax INT ,
	PersonIdle INT,
	PersonUsed INT,
	CopperMoney INT,
	Gold INT,
	Loyalty INT,
	Isoccupy INT,
	STATUS INT,
	last_time DATETIME,
	defensive_battle INT DEFAULT 0
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/*
	城市搬迁记录
	CityMoveLog
*/
DROP TABLE IF EXISTS CityMoveLog;
CREATE TABLE CityMoveLog
(
	id INT PRIMARY KEY AUTO_INCREMENT,
	CityId NUMERIC(21),
	OldX INT,
	OldY INT,
	NewX INT,
	NewY INT,
	Gold INT,
	MoveDate DATETIME
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/*
	野地(资源)
	drop table Wilderness
*/
DROP TABLE IF EXISTS Wilderness;
CREATE TABLE Wilderness
(
	id NUMERIC(21) PRIMARY KEY,
	CityId NUMERIC(21),
	LEVEL INT,
	TYPE INT,
	NAME VARCHAR(50),
	WildX INT,
	WildY INT,
	WildName VARCHAR(50),
	Isoccupy INT,
	TroopsId NUMERIC(21),
	BuildId NUMERIC(21),
	Woods INT,
	Stones INT,
	Irons INT,
	Foods INT
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/*
	空地
	drop table SpaceArea
*/
DROP TABLE IF EXISTS SpaceArea;
CREATE TABLE SpaceArea
(
	id NUMERIC(21) PRIMARY KEY,
	CityId NUMERIC(21),
	AreaX INT,
	AreaY INT,
	IsArea INT
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/*
	山寨
	drop table  Mount
*/
DROP TABLE IF EXISTS Mount;
CREATE TABLE Mount
(
	id NUMERIC(21) PRIMARY KEY,
	CityId NUMERIC(21),
	LEVEL INT,
	MountX INT,
	MountY INT,
	Loyalty INT,
	Isoccupy INT,
	TroopsId NUMERIC(21),
	BuildId NUMERIC(21),
	Woods INT,
	Stones INT,
	Irons INT,
	Foods INT, 
	restime NUMERIC(21)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/*
	基础建筑
	drop table  Build
*/
DROP TABLE IF EXISTS Build;
CREATE TABLE Build
(
	id NUMERIC(21) PRIMARY KEY,
	OwnerBuildId NUMERIC(21),
	CityId NUMERIC(21),
	LEVEL INT,
	BuildType INT,
	Sequence INT,
	state INT
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/*
	建筑动作
	drop table  Build
*/
DROP TABLE IF EXISTS BuildAction;
CREATE TABLE BuildAction
(
	id NUMERIC(21) PRIMARY KEY,
	BuildId NUMERIC(21),
	LEVEL INT,
	beginTime DATETIME,
	endTime DATETIME
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/*
	阵法类型 
	drop table  BattleArrayType
*/
DROP TABLE IF EXISTS BattleArrayType;
CREATE TABLE BattleArrayType
(
	id INT PRIMARY KEY AUTO_INCREMENT,
	TYPE INT,
	NAME VARCHAR(20)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/*
	阵法
	drop table  BattleArray
*/
DROP TABLE IF EXISTS BattleArray;
CREATE TABLE BattleArray
(
	id NUMERIC(21) PRIMARY KEY,
	TYPE INT,
	LEVEL INT,
	LevelName VARCHAR(20),
	NAME VARCHAR(20),
	BuildId	NUMERIC(21)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/*
	科研类型 
	drop table  TechnologyType
*/
DROP TABLE IF EXISTS TechnologyType;
CREATE TABLE TechnologyType
(
	id INT PRIMARY KEY AUTO_INCREMENT,
	TYPE INT,
	NAME VARCHAR(20)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/*
	科研类型 
	drop table  TechnologyType
*/
DROP TABLE IF EXISTS Technology;
CREATE TABLE Technology
(
	id NUMERIC(21) PRIMARY KEY,
	PlayerId NUMERIC(21) DEFAULT NULL,
	BuildId	NUMERIC(21),
	TYPE INT,
	NAME VARCHAR(20),
	LEVEL INT,
	Effective FLOAT,
	descrip VARCHAR(50)
) ENGINE=INNODB DEFAULT CHARSET=utf8;
/*****************************************************/

/*
	将领 
	drop table  Leader
*/
DROP TABLE IF EXISTS Leader;
CREATE TABLE Leader
(
	id NUMERIC(21) PRIMARY KEY,
	PlayerId	NUMERIC(21),
	LeaderName VARCHAR(20),
	LEVEL INT,
	Atk INT,
	Defense INT,
	Speed INT ,
	Lucky INT,
	Fame INT,
	unassignedAttrPoints INT,
	unassignedSkillPoints INT,
	last_time DATETIME
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/*
	将领技能表 Skill
	drop table  Skill
*/
DROP TABLE IF EXISTS Skill;
CREATE TABLE Skill
(
	id NUMERIC(21) PRIMARY KEY,
	LeaderId	NUMERIC(21),
	SkillType INT,
	LEVEL INT,
	UpdatePoint INT,
	OpenBoxLevel INT,
	DelayTime INT,
	CureEffect FLOAT,
	GetResource FLOAT,
	AdviseNumber FLOAT
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/*
	装备表 Equip
	drop table  Equip
*/
DROP TABLE IF EXISTS Equip;
CREATE TABLE Equip
(
	id NUMERIC(21) PRIMARY KEY,
	LeaderId NUMERIC(21) DEFAULT 0,
	EquipType INT DEFAULT 0,
	fkId NUMERIC(21) DEFAULT 0,
	LEVEL INT DEFAULT 0,
	NAME VARCHAR(50),
	UseLevel INT DEFAULT 0,
	Atk INT DEFAULT 0,
	Speed INT DEFAULT 0,
	Lucky INT DEFAULT 0,
	Defense FLOAT DEFAULT 0,
	Buyprice INT DEFAULT 0,
	Sellprice INT DEFAULT 0,
	STATUS INT,
	placeTime DATETIME,
	iconurl VARCHAR(200) NOT NULL,
	typeId INT DEFAULT 0,
	goldprice INT,
	strenTime DATETIME,
	autograph VARCHAR(50)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/*
	军队表 Troops
	drop table Troops
*/
DROP TABLE IF EXISTS Troops;
CREATE TABLE Troops
(
	id NUMERIC(21) PRIMARY KEY,
	isFrist INT ,
	CityId NUMERIC(21) NOT NULL,
	destX INT NOT NULL,
	destY INT NOT NULL,
	dispatchTime DATETIME,
	DestType INT,
	STATUS INT,
	intent INT
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/*
	军队逃跑记录日志 TroopsFleeLog
	drop table TroopsFleeLog
*/
DROP TABLE IF EXISTS TroopsFleeLog;
CREATE TABLE TroopsFleeLog
(
	id INT PRIMARY KEY AUTO_INCREMENT,
	CityId NUMERIC(21),
	FleeTime DATETIME,
	NpcBB INT,
	NpcQB INT,
	NpcGJB INT,
	NpcZC INT
) ENGINE=INNODB DEFAULT CHARSET=utf8;
	
	
/*
	军事力量表 Armforce
	drop table Armforce
*/
DROP TABLE IF EXISTS Armforce;
CREATE TABLE Armforce
(
	id NUMERIC(21) PRIMARY KEY,
	TroopsId NUMERIC(21),
	ArmType INT,
	Number INT 
) ENGINE=INNODB DEFAULT CHARSET=utf8;	


/*
	探险表Explore
	drop table Exploree
*/
DROP TABLE IF EXISTS Exploree;
CREATE TABLE Exploree
(
	id NUMERIC(21) PRIMARY KEY ,
	CityId NUMERIC(21),
	DestType INT,
	Mark INT ,
	Descrip VARCHAR(100),
	ResourceType INT,
	ResourceAmount INT,
	TroopsId NUMERIC(21),
	ExploreeBeginTime DATETIME,
	ExploreeEndTime DATETIME,
	WildX INT,
	WildY INT
) ENGINE=INNODB DEFAULT CHARSET=utf8;	

/*
	出使 OutMission
	drop table OutMission
*/
DROP TABLE IF EXISTS OutMission;
CREATE TABLE OutMission
(
	id NUMERIC(21) PRIMARY KEY,
	CityId NUMERIC(21),
	playerId NUMERIC(21),
	MissionCityId NUMERIC(21),
	Mark INT,
	Descrip VARCHAR(100),
	ResourceType INT,
	ResourceAmount INT,
	TroopsId NUMERIC(21),
	MissionTimers INT,
	UsedTimers INT,
	OutMissionTime DATETIME,
	OutMissionEndTime DATETIME
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/*
	占领野地 occupyWild
	drop table occupyWild
*/
DROP TABLE IF EXISTS occupyWild;
CREATE TABLE occupyWild
(
	id NUMERIC(21) PRIMARY KEY,
	TroopsId NUMERIC(21),
	CityId NUMERIC(21),
	WildLevel INT,
	OccupyBeginTime DATETIME,
	OccupyEndTime DATETIME,
	WildX INT,
	WildY INT,
	OwnerCityId NUMERIC(21),
	ownerPlayerId NUMERIC(21)

) ENGINE=INNODB DEFAULT CHARSET=utf8;

/*
	攻打山寨 OccupyMount
	drop table OccupyMount
*/
DROP TABLE IF EXISTS OccupyMount;
CREATE TABLE OccupyMount
(
	id NUMERIC(21) PRIMARY KEY,
	TroopsId NUMERIC(21),
	CityId NUMERIC(21),
	FortLevel INT,
	IsWin INT ,
	OccupyMountBeginTime DATETIME,
	OccupyMountEndTime DATETIME,
	MountX INT,
	MountY INT,
	OwnerCityId NUMERIC(21),
	ownerPlayerId NUMERIC(21)
) ENGINE=INNODB DEFAULT CHARSET=utf8;



/*
	攻占城池 OccupyCity
	drop table OccupyCity
*/
DROP TABLE IF EXISTS OccupyCity;
CREATE TABLE OccupyCity
(
	id NUMERIC(21) PRIMARY KEY,
	TroopsId NUMERIC(21),
	CityId NUMERIC(21),
	OwnerCityId NUMERIC(21),
	OccupyBeginTime DATETIME,
	OccupyEndTime DATETIME,
	Iswin INT,
	ownerPlayerId NUMERIC(21)
) ENGINE=INNODB DEFAULT CHARSET=utf8;


/*
	宝箱 TreasureChests
	drop table TreasureChests
*/
DROP TABLE IF EXISTS TreasureChests;
CREATE TABLE TreasureChests
(
	id NUMERIC(21) PRIMARY KEY,
	LEVEL INT,
	NAME VARCHAR(50),
	Rate INT,
	STATUS INT,
	placeTime DATETIME,
	iconurl VARCHAR(200),
	typeId INT
) ENGINE=INNODB DEFAULT CHARSET=utf8;


/*
	宝箱物品 Treasuregoods
	drop table Treasuregoods
*/
DROP TABLE IF EXISTS Treasuregoods;
CREATE TABLE Treasuregoods
(
	id NUMERIC(21) PRIMARY KEY,
	TreasureChestsId NUMERIC(21),
	ResourceId NUMERIC(21),
	EquipId NUMERIC(21)
) ENGINE=INNODB DEFAULT CHARSET=utf8;


/*
	装备类型表 EquipMaps
	drop table EquipMaps
*/
DROP TABLE IF EXISTS EquipMaps;
CREATE TABLE EquipMaps
(
	id INT PRIMARY KEY AUTO_INCREMENT,
	TYPE INT,
	NAME VARCHAR(50)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/*
	资源类型表 ResourceMaps
	drop table ResourceMaps
*/
DROP TABLE IF EXISTS ResourceMaps;
CREATE TABLE ResourceMaps
(
	id INT PRIMARY KEY AUTO_INCREMENT,
	TYPE INT,
	NAME VARCHAR(50)
) ENGINE=INNODB DEFAULT CHARSET=utf8;


/*
	背包 PlayerStorage
	drop table PlayerStorage
*/
DROP TABLE IF EXISTS PlayerStorage;
CREATE TABLE PlayerStorage
(
	id NUMERIC(21) PRIMARY KEY,
	PlayerId NUMERIC(21),
	TreasureChestsId NUMERIC(21),
	EquipId NUMERIC(21),
	upgradeStoneId NUMERIC(21),
	Number  INT,
	Descrip VARCHAR(50),
	STATUS INT
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/*
	资源表 Resource
	drop table Resource
*/
DROP TABLE IF EXISTS Resource;
CREATE TABLE Resource
(
	id NUMERIC(21) PRIMARY KEY,
	fkid NUMERIC(21),
	TYPE INT,
	Number  INT,
	NAME VARCHAR(50),
	price INT
) ENGINE=INNODB DEFAULT CHARSET=utf8;


/*
	升级石 UpgradeStone
	drop table UpgradeStone
*/
DROP TABLE IF EXISTS UpgradeStone;
CREATE TABLE UpgradeStone
(
	id NUMERIC(21) PRIMARY KEY,
	TYPE INT,
	UsedLevel INT,
	Number  INT DEFAULT 0,
	NAME VARCHAR(50),
	price INT,
	iconurl VARCHAR(200),
	placeTime DATETIME,
	typeId INT,
	STATUS INT
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/*
	系统商店表 SystemShop
	drop table SystemShop
*/
DROP TABLE IF EXISTS SystemShop;
CREATE TABLE SystemShop
(
	id INT PRIMARY KEY AUTO_INCREMENT,
	cfgid INT,
	goodsname VARCHAR(256),
	goodstype INT,
	ishot INT, 
	isnew INT, 
	coppers INT, 
	golds INT, 
	containnum INT, 
	maxsellnum INT DEFAULT 0, 
	discount1 INT DEFAULT 0, 
	discount2 INT DEFAULT 0, 
	discount3 INT DEFAULT 0, 
	description VARCHAR(512), 
	status INT,
	begintime DATETIME,
	endtime DATETIME
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/***
 * 商城商品购买数量记录表
 */
DROP TABLE IF EXISTS ShopBuyNum;
CREATE TABLE ShopBuyNum
(
	goodsid INT PRIMARY KEY, 
	number NUMERIC(21),
	updatetime DATETIME
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/***
 * 玩家战绩积分排行榜
 */
DROP TABLE IF EXISTS PlCbtGain;
CREATE TABLE PlCbtGain
(
	playerid NUMERIC(21) PRIMARY KEY, 
	cbtscore NUMERIC(21),
	status INT, 
	updatetime DATETIME
	
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/**
 * 联盟战绩积分排行榜
 */
DROP TABLE IF EXISTS AlCbtGain;
CREATE TABLE AlCbtGain
(
	allianceid NUMERIC(21) PRIMARY KEY, 
	cbtscore NUMERIC(21),
	status INT, 
	updatetime DATETIME
) ENGINE=INNODB DEFAULT CHARSET=utf8;
/*
	寄售系统表 PlayerMarket
	drop table PlayerMarket
*/
DROP TABLE IF EXISTS PlayerMarket;
CREATE TABLE PlayerMarket
(
	id NUMERIC(21) PRIMARY KEY,
	playerId NUMERIC(21),
	fk_id NUMERIC(21),
	consignType INT,
	consign_time DATETIME,
	STATUS INT,
	money INT,
	number INT,
	cityId NUMERIC(21),
	cfg_no INT,
	unit_price DOUBLE DEFAULT 0		
) ENGINE=INNODB DEFAULT CHARSET=utf8;


/*
	交易表 TradeLog
	drop table TradeLog
*/
DROP TABLE IF EXISTS TradeLog;
CREATE TABLE TradeLog
(
	id INT PRIMARY KEY AUTO_INCREMENT,
	SellPlayerId NUMERIC(21),
	BuyPlayerId NUMERIC(21),
	TradeTime DATETIME,
	ResourceId NUMERIC(21),
	EquipId NUMERIC(21),
	upgradeStoneId NUMERIC(21),
	Price_glod INT,
	Price_CopperMoney INT,
	Money INT,
	UsedPerson INT,
	commissions INT,
	woods INT,
	stone INT,
	foods INT,
	irons INT
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/**
 * 摇钱树表
 */
DROP TABLE IF EXISTS MoneyTree;
CREATE TABLE MoneyTree
(
   playerid NUMERIC(21) PRIMARY KEY ,
	usedfreenum INT,
	usedbuyednum INT,
	buyednum INT, 
	usetime DATETIME
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/**
 * 摇钱树获得的宝物记录表
 */
DROP TABLE IF EXISTS ObtainedGoods;
CREATE TABLE ObtainedGoods
(
	id INT PRIMARY KEY AUTO_INCREMENT,
	playerid NUMERIC(21),
	goodsid NUMERIC(21), 
	goodstype INT,
	cfgno INT,
	num INT, 
	obtaintime DATETIME
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/**
 * 摇钱树摇得的事件消息
 */
DROP TABLE IF EXISTS TreeMoneyEvent;
CREATE TABLE TreeMoneyEvent
(
    id NUMERIC(21) PRIMARY KEY ,
	playerid NUMERIC(21),
	eventdata BLOB, 
	eventtime DATETIME
) ENGINE=INNODB DEFAULT CHARSET=utf8;
/*
	系统消息表 SystemSms
	drop table SystemSms
*/
DROP TABLE IF EXISTS SystemSms;
CREATE TABLE SystemSms
(
	id NUMERIC(21) PRIMARY KEY,
	SenderId NUMERIC(21),
	ReceiverId NUMERIC(21),
	SendTime DATETIME,
	Title VARCHAR(256),
	content VARCHAR(500),
	IsReader INT DEFAULT -1,
	smsType INT DEFAULT 0,
	sms BLOB
) ENGINE=INNODB DEFAULT CHARSET=utf8;


/*
	玩家关系表 Unions
	drop table Unions
*/
DROP TABLE IF EXISTS Unions;
CREATE TABLE Unions
(
	id INT PRIMARY KEY AUTO_INCREMENT,
	PlayerId NUMERIC(21),
	OtherPlayerId NUMERIC(21),
	TYPE INT,
	STATUS INT
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/**
 * 玩家好友申请加入表
 */
DROP TABLE IF EXISTS AuditFriend;
CREATE TABLE AuditFriend
(
	id INT PRIMARY KEY AUTO_INCREMENT,
	playerid NUMERIC(21),
	auditfriendid NUMERIC(21),
	friendstatus INT,
	audittime DATETIME
) ENGINE=INNODB DEFAULT CHARSET=utf8;


/*
	战报关联ReportUnion
	drop table ReportUnion
*/
DROP TABLE IF EXISTS ReportUnion;
CREATE TABLE ReportUnion
(
	smsId NUMERIC(21) NOT NULL,
	playerId NUMERIC(21) NOT NULL,
	stat INT NOT NULL,
	forwarderId NUMERIC(21)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/*
	战报详情 Scout
	drop table Scout
*/
DROP TABLE IF EXISTS Jobs;
CREATE TABLE Jobs
(
	id NUMERIC(21) PRIMARY KEY,
	JobId NUMERIC(21),
	PlayerId NUMERIC(21),
	TYPE INT,
	finshDate DATETIME,
	STATUS INT,
	times INT DEFAULT 0,
	conditions INT
) ENGINE=INNODB DEFAULT CHARSET=utf8;


/*
    联盟任务
*/
DROP TABLE IF EXISTS AllyJob;
CREATE TABLE AllyJob
(
    Id NUMERIC(21) PRIMARY KEY,
    JobId NUMERIC(21),
    AllyId NUMERIC(21),
    Type INT,
    FinishDate DATETIME,
    Status INT,
    Times INT DEFAULT 0,
    Conditions INT
) ENGINE=INNODB DEFAULT CHARSET=utf8;


/*
	战报详情 Account
	drop table Account
*/
DROP TABLE IF EXISTS Account;
CREATE TABLE Account
(
	id NUMERIC(21) PRIMARY KEY,
	playerId NUMERIC(21),
	gold INT DEFAULT 0
) ENGINE=INNODB DEFAULT CHARSET=utf8;


/*
	战报详情 scheme
	drop table scheme
*/
DROP TABLE IF EXISTS scheme;
CREATE TABLE scheme
(
	id NUMERIC(21) PRIMARY KEY,
	playerId NUMERIC(21),
	cityId NUMERIC(21),
	isexpire INT DEFAULT 0,
	startTime DATETIME NOT NULL,
	endTime DATETIME NOT NULL,
	schemeType INT NOT NULL,
	schemeId NUMERIC(21) NOT NULL
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/*
	战报详情 OnLinePlayers
	drop table OnLinePlayers
*/
DROP TABLE IF EXISTS OnLinePlayers;
CREATE TABLE OnLinePlayers
(
	id NUMERIC(21) PRIMARY KEY,
	online_players INT,
	count_time DATETIME
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/*
	战报详情 system_time
	drop table system_time
*/
DROP TABLE IF EXISTS system_time;
CREATE TABLE system_time
(
	id NUMERIC(21) PRIMARY KEY,
	system_time NUMERIC(14)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/*
	详情 task_record
	drop table task_record
*/
DROP TABLE IF EXISTS task_record;
CREATE TABLE task_record
(
	id NUMERIC(21) PRIMARY KEY,
	fk_id NUMERIC(21),
	task_id NUMERIC(21),
	begin_time NUMERIC(14),
	span NUMERIC(14),
	stat INT,
	taskType INT,
	troop_id NUMERIC(21),
	param BLOB ,
	actor_id NUMERIC(21)
) ENGINE=INNODB DEFAULT CHARSET=utf8;


#######联盟

 /*
	联盟表 Alliance
	drop table Alliance
*/
DROP TABLE IF EXISTS Alliance;
CREATE TABLE Alliance
(
	id NUMERIC(21) PRIMARY KEY,
	allianceName VARCHAR(50),
	STATUS INT,
	notice VARCHAR(200),
	introduction VARCHAR(500),
	reputation INT,
	sequence INT,
	levels INT,
	money INT,
	peopleCapacity INT,
	peopleUsed INT,
	freePeople INT,
	last_time DATETIME,
	defensiveBattleArray INT
) ENGINE=INNODB DEFAULT CHARSET=utf8;

 /*
	联盟表 AllianceActor
	drop table AllianceActor
*/
DROP TABLE IF EXISTS AllianceActor;
CREATE TABLE AllianceActor
(
	id NUMERIC(21) PRIMARY KEY,
	allianceId NUMERIC(21),
	playerId NUMERIC(21),
	allyAuth INT,
	viprating INT,
	contribvalue INT,
	STATUS INT,
	statusTime DATETIME
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/*
	联盟表 AllianceTech
	drop table AllianceTech
*/
DROP TABLE IF EXISTS AllianceTech;
CREATE TABLE AllianceTech
(
	id NUMERIC(21) PRIMARY KEY,
	allianceId NUMERIC(21),
	levels INT,
	techType INT DEFAULT -1
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/*
	联盟表 AllianceBattle
	drop table AllianceBattle
*/
DROP TABLE IF EXISTS AllianceBattle;
CREATE TABLE AllianceBattle
(
	id NUMERIC(21) PRIMARY KEY,
	allianceId NUMERIC(21),
	levels INT,
	battleType INT DEFAULT -1
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/*
	联盟道具 AllianceProps
	drop table AllianceProps
*/
DROP TABLE IF EXISTS AllianceProps;
CREATE TABLE AllianceProps
(
	id NUMERIC(21) PRIMARY KEY,
	allianceId NUMERIC(21),
	propsId NUMERIC(21)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/*
	联盟道具 AllianceEvents
	drop table AllianceEvents
*/
DROP TABLE IF EXISTS AllianceEvents;
CREATE TABLE AllianceEvents
(
	id NUMERIC(21) PRIMARY KEY,
	allianceId NUMERIC(21),
	cfg_no INT,
	al_events BLOB,
	event_date DATETIME
) ENGINE=INNODB DEFAULT CHARSET=utf8;
####联盟结束

DROP TABLE IF EXISTS RedAlert;
CREATE TABLE RedAlert
(
	id NUMERIC(21) PRIMARY KEY,
	taskId NUMERIC(21),
	attackId NUMERIC(21),
	attackCityId NUMERIC(21),
	attackTroopsId NUMERIC(21),
	attackerX INT,
	attackerY INT,
	destType INT,
	attackedId NUMERIC(21),
	attackTime NUMERIC(13),
	attackEndTime NUMERIC(13),
	attackType INT
) ENGINE=INNODB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS consume_recode;
CREATE TABLE consume_recode
(
	id INT PRIMARY KEY AUTO_INCREMENT,
	player_id NUMERIC(21),
	consume_time DATETIME,
	consume_money INT,
	consume_type INT,
	consume_sub_type INT,
	currency INT,
	role INT,
	consume_where INT(3) DEFAULT 0
	
) ENGINE=INNODB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS feedback;
CREATE TABLE feedback
(
	id INT PRIMARY KEY AUTO_INCREMENT,
	player_id NUMERIC(21),
	feedback_time DATETIME,
	feedback_tilte VARCHAR(50),
	feedback_content VARCHAR(500)
	
) ENGINE=INNODB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS recharge;
CREATE TABLE recharge
(
	id NUMERIC(21) PRIMARY KEY,
	player_id NUMERIC(21),
	create_time DATETIME,
	pay_status INT,
	order_money INT,
	goods_count INT,
	uin VARCHAR(50),
	order_serial VARCHAR(60),
	deal_status BOOLEAN,
	chanel_code VARCHAR(30)
	
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/***
任务记录
**/
DROP TABLE IF EXISTS jobs_record;
CREATE TABLE jobs_record
(
	id NUMERIC(21) PRIMARY KEY,
	player_id NUMERIC(21),
	record_time NUMERIC(21),
	xnumber INT,
	xtype INT,
	stat BOOLEAN DEFAULT 0
	
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/***
玩家状态，禁言，无效
**/
DROP TABLE IF EXISTS player_stat;
CREATE TABLE player_stat
(
	id NUMERIC(21) PRIMARY KEY,
	player_id NUMERIC(21),
	begin_time NUMERIC(21),
	end_time NUMERIC(21),
	stat_type INT,
	cause_memo VARCHAR(500),
	supervisor VARCHAR(30)
		
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/**
 * 玩家体力表
 */
DROP TABLE IF EXISTS Vigor;
CREATE TABLE Vigor
(
    playerid NUMERIC(21) PRIMARY KEY,
    usedfreenum INT,
    usedbuyednum INT,
    buyednum INT,
    usetime DATETIME
) ENGINE=INNODB DEFAULT CHARSET=utf8;

/**
 * 系统级参数
 */
/*
DROP TABLE IF EXISTS SysParam;
CREATE TABLE SysParam
(
    id INT PRIMARY KEY,
    strId VARCHAR(32),
    iVal INT,
    strVal VARCHAR(128),
    recTime DATETIME
) ENGINE=INNODB DEFAULT CHARSET=utf8;
*/

/**
 * 物品掉落
 */
DROP TABLE IF EXISTS ItemDrop;
CREATE TABLE ItemDrop
(
    cfgNo INT PRIMARY KEY,
    lastResetTime DATETIME,
    pretend INT,
    genuine INT,
    cumulativeTime NUMERIC(21)
) ENGINE=INNODB DEFAULT CHARSET=utf8;


/**
索引创建
*/
CREATE INDEX IDX_MAP_XY ON Map (mapX,mapY); 
CREATE INDEX IDX_SYSTEMSMS_SENDID ON SystemSms (SenderId); 
CREATE INDEX IDX_SYSTEMSMS_ISREADER ON SystemSms (IsReader); 
CREATE INDEX IDX_SYSTEMSMS_ReceiverId ON SystemSms (ReceiverId); 

CREATE INDEX IDX_ALLIANCEACTOR_ALID ON AllianceActor (allianceId);
CREATE INDEX IDX_ALLIANCEACTOR_PLID ON AllianceActor (playerId);

CREATE INDEX IDX_AllianceBattle_ALID ON AllianceBattle (allianceId);

CREATE INDEX IDX_Player_NAME ON Player (`NAME`);

CREATE INDEX IDX_Technology_PlayerId ON Technology (PlayerId);

CREATE INDEX IDX_PlayerStorage_PlayerId ON PlayerStorage (`PlayerId`);
CREATE INDEX IDX_PlayerStorage_EquipId ON PlayerStorage (EquipId);
CREATE INDEX IDX_PlayerStorage_TreasureChestsId ON PlayerStorage (`TreasureChestsId`);
CREATE INDEX IDX_PlayerStorage_upgradeStoneId ON PlayerStorage (`upgradeStoneId`);

CREATE INDEX IDX_task_record_task_id ON task_record (task_id);
CREATE INDEX IDX_task_record_fk_id ON task_record (fk_id);
CREATE INDEX IDX_task_record_stat ON task_record (stat);

CREATE INDEX IDX_Jobs_JobId ON Jobs (JobId);
CREATE INDEX IDX_Jobs_STATUS ON Jobs (`STATUS`);
CREATE INDEX IDX_Jobs_PlayerId ON Jobs (`PlayerId`);
CREATE INDEX IDX_Jobs_JobId_Status ON Jobs (JobId, `STATUS`);

CREATE INDEX IDX_Armforce_troopsId ON Armforce (`TroopsId`);

CREATE INDEX IDX_ReportUnion_playerId ON ReportUnion (`playerId`);
CREATE INDEX IDX_ReportUnion_smsId ON ReportUnion (smsId);

CREATE INDEX IDX_PlayerMarket_consignType ON PlayerMarket (consignType);

#######索引创建结束######################

#######存储过程##########################
/**
玩家威望排名存储过程
*/
DELIMITER $$
DROP PROCEDURE IF EXISTS `rank_player`$$
CREATE PROCEDURE rank_player(IN playerId NUMERIC(21),OUT rank_index INT)
BEGIN  
	DECLARE tem_pid NUMERIC(21);
	DECLARE tem_name VARCHAR(30);
	DECLARE tem_level INT;  
	DECLARE tem_rep INT;
	DECLARE Done INT DEFAULT 0;

	DECLARE rs CURSOR FOR SELECT l.LeaderName,l.LEVEL,l.Fame,p.id FROM Leader l INNER JOIN Player p ON l.PlayerId = p.id ORDER BY l.Fame DESC;	
	DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET Done = 1;
	OPEN rs;
	FETCH FROM rs INTO tem_name,tem_level,tem_rep,tem_pid;
	SET rank_index = 1;
	/*记住 declare 定义变量之前，是不能有任何逻辑代码的*/
	
	REPEAT
		IF NOT Done THEN
			IF tem_pid != playerId THEN
				SET rank_index = rank_index + 1;
			ELSE
				SET Done = 1;
			END IF;
			FETCH NEXT FROM rs INTO tem_name,tem_level,tem_rep,tem_pid;
		END IF;
	UNTIL Done END REPEAT;
	CLOSE rs;
	
	SELECT l.LeaderName,l.LEVEL,l.Fame,p.id FROM Leader l INNER JOIN Player p ON l.PlayerId = p.id ORDER BY l.Fame DESC LIMIT 0,100;	
END$$
DELIMITER ;


/**
优化后超级富豪名存储过程
*/
DELIMITER $$
DROP PROCEDURE IF EXISTS `rank_super_rich`$$
CREATE PROCEDURE rank_super_rich(IN playerId NUMERIC(21),OUT rank_index INT)
BEGIN  
	DECLARE tem_pid NUMERIC(21);
	DECLARE tem_name VARCHAR(30);
	DECLARE tem_level INT;  
	DECLARE tem_totalMoney INT;
	DECLARE Done INT DEFAULT 0;

	DECLARE rs CURSOR FOR SELECT SUM(c.CopperMoney) AS totalMoney ,p.NAME,p.LEVEL,p.id FROM City c LEFT JOIN Player p ON c.PlayerId = p.id  GROUP BY p.id ORDER BY totalMoney DESC;	
	DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET Done = 1;
	OPEN rs;
	FETCH FROM rs INTO tem_totalMoney,tem_name,tem_level,tem_pid;

	SET rank_index = 1;
	/*记住 declare 定义变量之前，是不能有任何逻辑代码的*/
	REPEAT
		IF NOT Done THEN
			IF tem_pid != playerId THEN
				SET rank_index = rank_index + 1;
			ELSE
				SET Done = 1;
			END IF;
			FETCH NEXT FROM rs INTO tem_totalMoney,tem_name,tem_level,tem_pid;
		END IF;
	UNTIL Done END REPEAT;
	CLOSE rs;	
	SELECT SUM(c.CopperMoney) AS totalMoney ,p.NAME,p.LEVEL,p.id FROM City c LEFT JOIN Player p ON c.PlayerId = p.id  GROUP BY p.id ORDER BY totalMoney DESC LIMIT 0 ,100;
END$$
DELIMITER ;

/**
联盟声望存储过程
*/
DELIMITER $$
DROP PROCEDURE IF EXISTS `rank_alliance_rep`$$
CREATE PROCEDURE rank_alliance_rep(IN allianceId NUMERIC(21),OUT rank_index INT)
BEGIN 
	#声明变量
	DECLARE tem_id NUMERIC(21);	
	DECLARE tem_reputation INT;
	DECLARE Done INT DEFAULT 0;

	DECLARE rs CURSOR FOR SELECT id,reputation FROM Alliance ORDER BY reputation DESC,id ASC;	
	DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET Done = 1;
	
	#开始逻辑
	OPEN rs;
	FETCH FROM rs INTO tem_id,tem_reputation;
	SET rank_index = 1;
	IF allianceId != 0 THEN
		REPEAT
		IF NOT Done THEN
			IF tem_id != allianceId THEN
				SET rank_index = rank_index + 1;
			ELSE
				SET Done = 1;
			END IF;
			FETCH NEXT FROM rs INTO tem_id,tem_reputation;
		END IF;
		UNTIL Done END REPEAT;
		CLOSE rs;	
	ELSE
		CLOSE rs;
	END IF;
	
	SELECT * FROM Alliance ORDER BY reputation DESC,id ASC LIMIT 0 ,100;
END$$
DELIMITER ;		 

/**
玩家战绩排行存储过程
*/
DELIMITER $$
DROP PROCEDURE IF EXISTS `rank_player_battle`$$
CREATE PROCEDURE rank_player_battle(IN id NUMERIC(21),IN btype INT,OUT rank_index INT)
BEGIN 
	#声明变量
	DECLARE tem_id NUMERIC(21);	
	DECLARE tem_num INT;
	DECLARE tem_num1 INT;
	DECLARE Done INT DEFAULT 0;

	DECLARE rs CURSOR FOR SELECT COUNT(SenderId) AS num, (SELECT COUNT(*)  FROM SystemSms b1 WHERE b1.`ReceiverId` = b.SenderId AND  b1.SenderId <> 0) AS num1,b.SenderId 
	FROM SystemSms b INNER JOIN Player p ON b.SenderId = p.id
	WHERE b.IsReader = btype AND  b.SenderId <> 0 AND b.ReceiverId <> 0 GROUP BY SenderId  ORDER BY num DESC,num1 ASC;
	DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET Done = 1;
	
	#开始逻辑
	OPEN rs;
	FETCH FROM rs INTO tem_num,tem_num1,tem_id;
	SET rank_index = 1;
	IF id != 0 THEN
		REPEAT
		IF NOT Done THEN
			IF tem_id != id THEN
				SET rank_index = rank_index + 1;
			ELSE
				SET Done = 1;
			END IF;
			FETCH NEXT FROM rs INTO tem_num,tem_num1,tem_id;
		END IF;
		UNTIL Done END REPEAT;
		CLOSE rs;	
	ELSE
		CLOSE rs;
	END IF;
	SELECT COUNT(SenderId) AS num, (SELECT COUNT(*)  FROM SystemSms b1 WHERE b1.`ReceiverId` = b.SenderId AND  b1.SenderId <> 0) AS num1,b.* 
	FROM SystemSms b INNER JOIN Player p ON b.SenderId = p.id
	WHERE b.IsReader = btype AND  SenderId <> 0 AND b.ReceiverId <> 0 GROUP BY SenderId  ORDER BY num DESC ,num1 ASC LIMIT 0 ,100;
END$$
DELIMITER ;

/**
联盟战绩排行存储过程
*/
DELIMITER $$
DROP PROCEDURE IF EXISTS `rank_al_battle`$$
CREATE PROCEDURE rank_al_battle(IN id NUMERIC(21),IN btype INT,OUT rank_index INT)
BEGIN 
	#声明变量
	DECLARE tem_id NUMERIC(21);	
	DECLARE tem_num INT;
	DECLARE tem_num1 INT;
	DECLARE Done INT DEFAULT 0;

	DECLARE rs CURSOR FOR SELECT COUNT(SenderId) AS num, (SELECT COUNT(*)  FROM SystemSms b1 WHERE b1.`ReceiverId` = b.SenderId) AS num1,b.SenderId 
	FROM SystemSms b INNER JOIN Alliance p ON b.SenderId = p.id
	WHERE b.IsReader = btype AND  SenderId <> 0 GROUP BY SenderId  ORDER BY num DESC;
	DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET Done = 1;
	
	#开始逻辑
	OPEN rs;
	FETCH FROM rs INTO tem_num,tem_num1,tem_id;
	SET rank_index = 1;
	IF id != 0 THEN
		REPEAT
		IF NOT Done THEN
			IF tem_id != id THEN
				SET rank_index = rank_index + 1;
			ELSE
				SET Done = 1;
			END IF;
			FETCH NEXT FROM rs INTO tem_num,tem_num1,tem_id;
		END IF;
		UNTIL Done END REPEAT;
		CLOSE rs;	
	ELSE
		CLOSE rs;
	END IF;
	SELECT COUNT(SenderId) AS num, (SELECT COUNT(*)  FROM SystemSms b1 WHERE b1.`ReceiverId` = b.SenderId) AS num1,b.* 
	FROM SystemSms b INNER JOIN Alliance p ON b.SenderId = p.id
	WHERE b.IsReader = btype AND  SenderId <> 0 GROUP BY SenderId  ORDER BY num DESC LIMIT 0 ,100;
END$$
DELIMITER ;
#############################存储过程结束

#######初始化表数据######
/****
	初始化商城

INSERT INTO `SystemShop` VALUES ('1', '1', '匕首', '1', '0', '0', '10000', '0', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('2', '2', '鱼肠剑', '1', '0', '0', '80000', '0', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('3', '3', '斩马刀', '1', '0', '0', '0', '5', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('4', '4', '金背大刀', '1', '0', '0', '0', '10', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('5', '5', '宿铁刀', '1', '0', '0', '0', '20', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('6', '6', '偃月刀', '1', '0', '0', '0', '30', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('7', '7', '红缨枪', '1', '0', '0', '0', '40', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('8', '8', '乌金枪', '1', '0', '0', '0', '50', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('9', '9', '碧水剑', '1', '1', '0', '0', '60', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('10', '10', '倚天剑', '1', '1', '0', '0', '70', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('11', '11', '布甲', '3', '0', '0', '10000', '0', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('12', '12', '鱼鳞甲', '3', '0', '0', '80000', '0', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('13', '13', '乌金甲', '3', '0', '0', '0', '5', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('14', '14', '链铁甲', '3', '0', '0', '0', '10', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('15', '15', '乾坤战甲', '3', '0', '0', '0', '20', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('16', '16', '天地战甲', '3', '0', '0', '0', '30', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('17', '17', '麒麟铠甲', '3', '0', '0', '0', '40', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('18', '18', '黄金掩心甲', '3', '0', '0', '0', '50', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('19', '19', '金丝软甲', '3', '1', '1', '0', '60', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('20', '20', '青龙铠甲', '3', '1', '1', '0', '70', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('21', '21', '黄膘马', '4', '0', '0', '10000', '0', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('22', '22', '青鬃马', '4', '0', '0', '80000', '0', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('23', '23', '枣红马', '4', '0', '0', '0', '5', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('24', '24', '赤兔', '4', '0', '0', '0', '10', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('25', '25', '乌骓', '4', '0', '0', '0', '20', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('26', '26', '绝地', '4', '0', '0', '0', '30', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('27', '27', '翻羽', '4', '0', '0', '0', '40', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('28', '28', '奔霄', '4', '0', '0', '0', '50', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('29', '29', '追风', '4', '1', '1', '0', '60', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('30', '30', '浮云', '4', '1', '1', '0', '70', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('31', '31', '孙子兵法', '2', '0', '0', '10000', '0', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('32', '32', '六韬', '2', '0', '0', '80000', '0', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('33', '33', '尉缭子', '2', '0', '0', '0', '5', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('34', '34', '三略', '2', '0', '0', '0', '10', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('35', '35', '司马法', '2', '0', '0', '0', '20', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('36', '36', '吴子', '2', '0', '0', '0', '30', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('37', '37', '鬼谷子', '2', '0', '0', '0', '40', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('38', '38', '商君书', '2', '0', '0', '0', '50', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('39', '39', '八阵总述', '2', '1', '1', '0', '60', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('40', '40', '战国策', '2', '1', '1', '0', '70', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('41', '41', '白玉璧', '5', '0', '0', '10000', '0', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('42', '42', '青玉璧', '5', '0', '0', '80000', '0', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('43', '43', '云纹璧', '5', '0', '0', '0', '5', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('44', '44', '和氏璧', '5', '0', '0', '0', '10', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('45', '45', '谷纹璧', '5', '0', '0', '0', '20', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('46', '46', '蒲纹璧', '5', '0', '0', '0', '30', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('47', '47', '八卦璧', '5', '0', '0', '0', '40', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('48', '48', '龙凤璧', '5', '0', '0', '0', '50', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('49', '49', '浮云璧', '5', '0', '1', '0', '60', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('50', '50', '乾坤璧', '5', '0', '1', '0', '70', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('51', '51', '强化石', '8', '1', '0', '0', '10', '20', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('52', '52', '铁钥匙', '7', '0', '0', '0', '1', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('53', '53', '铜钥匙', '7', '0', '0', '0', '2', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('54', '54', '银钥匙', '7', '0', '0', '0', '3', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('55', '55', '金钥匙', '7', '1', '1', '0', '5', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('56', '56', '大地之眼', '9', '0', '1', '0', '50', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('57', '57', '海洋之心', '9', '0', '1', '0', '50', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('58', '58', '红玉扳指', '9', '1', '1', '0', '50', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('59', '59', '夜明珠', '9', '1', '1', '0', '50', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('60', '60', '玉玺', '9', '1', '1', '0', '500', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('61', '61', '木宝箱', '6', '0', '0', '0', '2', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('62', '62', '铁宝箱', '6', '0', '0', '0', '5', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('63', '63', '铜宝箱', '6', '0', '0', '0', '10', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('64', '64', '银宝箱', '6', '0', '0', '0', '30', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('65', '65', '金宝箱', '6', '1', '1', '0', '100', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('66', '66', '黑铁扳指', '9', '0', '0', '100000', '0', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('67', '67', '白银扳指', '9', '1', '0', '0', '50', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('68', '68', '伯爵符诏', '9', '1', '1', '0', '100', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('69', '69', '侯爵符诏', '9', '1', '1', '0', '150', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('70', '70', '公爵符诏', '9', '1', '1', '0', '200', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('71', '71', '打王鞭', '9', '1', '1', '0', '300', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('72', '72', '王冠', '9', '1', '1', '0', '500', '1', '0', '0', '0', '0', '', '2', NOW(), NOW());
INSERT INTO `SystemShop` VALUES ('73', '503', '招募令', '6', '1', '1', '0', '10', '1', '0', '0', '0', '0', '', '1', NOW(), NOW());
INSERT INTO `SystemShop` VALUES (74, 2002, '小喇叭', 12, 0, 1, 1, 0, 100, 0, 0, 0, 0, '', 2, NOW(), NOW());
*/

/*
INSERT INTO `SysParam` VALUES(1, 'server_shutdown_time', 0, '', CURDATE());
INSERT INTO `SysParam` VALUES(2, 'server_last_alive_time', 0, '', CURDATE());
INSERT INTO `SysParam` VALUES(3, 'server_startup_time', 0, '', CURDATE());
*/




