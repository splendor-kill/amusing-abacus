package com.tentacle.callofwild.util;

import com.tentacle.callofwild.protocol.ProtoBasis.eResType;


public final class Consts {	
	public static String LOG_FILE_PATH = "log4j.properties";

	public static final int AL_LEADER_COUNT		= 3;
	
	public static final String LOGGER_DEBUG			= "yes";
	public static final String CONSOLE_DEBUG		= "yes";
	
	public static final String str_old_ver_do_not_support_session_key = "old_ver_do_not_support_session_key";
	public static final double client_version_start_support_session_key = 1.3;
   
	
	public static int ALLI_INVALID		= 0;
	public static int ALLI_VALID		= 1;
	
	public static final int TAX = 5;
	public static final int MAX_LEVEL = 20;
	
    public static final int WOOD_CFG_NO     = 10001;
    public static final int STONE_CFG_NO    = 10002;
    public static final int FOOD_CFG_NO     = 10003;
    public static final int IRON_CFG_NO     = 10004;
    public static final int COPPER_CFG_NO   = 10005;
    public static final int GOLD_CFG_NO     = 10006;
	
	//根据资源类型获取相应的资源配置
	public static int getCfgNoByResType(int resType) {
		int resCfgNo = 0;
		if (eResType.WOOD_VALUE == resType) {
			resCfgNo = WOOD_CFG_NO;
		}
		else if (eResType.STONE_VALUE == resType) {
			resCfgNo = STONE_CFG_NO;
		}
		else if (eResType.IRON_VALUE == resType) {
			resCfgNo = IRON_CFG_NO;
		}
		else if (eResType.FOOD_VALUE == resType) {
			resCfgNo = FOOD_CFG_NO;
		}
		else if (eResType.COPPER_VALUE == resType) {
			resCfgNo = COPPER_CFG_NO;
		}
		else if (eResType.GOLD_VALUE == resType) {
			resCfgNo = GOLD_CFG_NO;
		}
		
		return resCfgNo;
	}
	
	//根据资源配置ID获取资源类型
	public static int getResTypeByCfgNo(int resCfgNo) {
		int resType = eResType.RT_UNKNOWN_VALUE;
		
		if (WOOD_CFG_NO == resCfgNo) {
			resType = eResType.WOOD_VALUE;
		}
		else if (STONE_CFG_NO == resCfgNo) {
			resType = eResType.STONE_VALUE;
		}
		else if (IRON_CFG_NO == resCfgNo) {
			resType = eResType.IRON_VALUE;
		}
		else if (FOOD_CFG_NO == resCfgNo) {
			resType = eResType.FOOD_VALUE;
		}
		else if (COPPER_CFG_NO == resCfgNo) {
			resType = eResType.COPPER_VALUE;
		}
		else if (GOLD_CFG_NO == resCfgNo) {
			resType = eResType.GOLD_VALUE;
		}
		
		return resType;
	}
	
	public static class AllianceEvent{
		public static int EVENT_DOANT_GOLD		= 1;
		public static int EVENT_AL_UPGRADE		= 2;
		public static int EVENT_AL_SUCCESS		= 3;
		public static int EVENT_AL_FAIL			= 4;
		public static int EVENT_AL_CANCEL		= 5;
		public static int EVENT_DOANT_RES		= 6;
		public static int EVENT_DOANT_TR		= 7;
		public static int EVENT_DOANT_COPPER	= 8;
		public static int EVENT_BUY_GOLD		= 10;
		public static int EVENT_BUY_COPPER		= 11;
		public static int EVENT_SPEED_UP		= 12;
		public static int EVENT_RECALL_T		= 13;
		public static int EVENT_TECH			= 14;
		public static int EVENT_BATTLE			= 15;
		public static int EVENT_NEW_MEMBER  	= 16;
		public static int EVENT_KICK_MEMBER  	= 17;
		public static int EVENT_MEMBER_QUIT  	= 18;
		public static int EVENT_PROMO_MEMBER  	= 19;
		public static int EVENT_RE_MEMBER  		= 20;
		public static int EVENT_MEMBER_EX  		= 21;
	}

	public static class SchemeType {
		public static final int VIP_SCHEME 				  = 1;
		public static final int SPEEDUP_SCHEME            = 2;
		public static final int AVOID_WAR_SCHEME 		  = 3;
		public static final int ADD_YIELD_SCHEME 		  = 4;
		public static final int AUTO_BUY_FOOD_SCHEME 	  = 5;
		public static final int AL_AUTO_BUY_FOOD_SCHEME   = 6;
	}
	
    public static class PayFor91 {
        public static final String QUERY_RET_ACT    = "1";
        public static final String CHECK_LONG_ACT   = "4";
    }
	
	public static class ConsumeRecord {
		public static final int SPEED_BUILD			= 1;  //加速建筑
		public static final int SPEEDUP_TRAIN		= 2;  //玩家加速训练军队
		public static final int SPEEDUP_TECH		= 3;  //玩家加速科技
		public static final int SPEEDUP_TRAIN_AL	= 4;  //联盟加速训练军队
		public static final int SPEEDUP_TECH_AL		= 5;  //联盟加速科技
		public static final int BUY_VIP				= 6;  //购买会员
		public static final int BUY_AVOID			= 7;  //购买免战
		public static final int BUY_RES				= 8;  //购买资源
		public static final int BUY_PROPS			= 9;  //购买道具
		public static final int BUY_YIELD			= 10; //购买增强
		public static final int CURRENCY_GOLD		= 11; //元宝
		public static final int CURRENCY_COPPER		= 12; //铜钱
		public static final int SPEEDUP_BATT		= 13; //阵法加速
		public static final int ROLE_PLAYER 		= 14; //玩家
		public static final int ROLE_AL 	 		= 15; //联盟
		public static final int BUY_MALL 			= 16; //商城购买
		public static final int BUY_MARKET 			= 17; //集市购买
		public static final int TRAIN_ARM 			= 18; //训练军队
		public static final int BUY_EQUIP           = 19; //购买装备
		public static final int BUY_TREASURE        = 20; //购买宝物
		public static final int BUY_AUTO_FOOD       = 21; //购买宝物
		public static final int BUY_MONEYTREE       = 22; //购买摇钱树
		public static final int BUY_VIGOR           = 23; //购买体力值
		
	}
	
	//联盟成员，5级才可捐献
	public static final int AL_DONAT_LEVEL	 	= 5;
	public static final int AL_APPLY_LEVEL	 	= 5;
	
	
	public static class JobsRecordType{
		public static final int CONTINUOUS_LOGIN  = 0; //连续登录
		public static final int AVORD_WAR		  = 1; //免战
		public static final int BUY_VIP			  = 2; //购买VIP
		public static final int BUY_GOLD		  = 3; //购买黄金
		public static final int CUS_GOLD		  = 4; //黄金消费
		public static final int CONSIGN_ITEM	  = 5; //寄售
		public static final int BUY_ITEM_MARKET	  = 6; //集市购买物品
		public static final int KILL_ARMS		  = 7; //累计消灭军队
		public static final int KILL_ARMS_ONETIME = 8; //单次消灭军队
		public static final int ENVY_TIMES		  = 9; //累计出使次数
		public static final int EXPLORE_TIMES	  = 10; //累计探险次数
		
		public static final int EVERYDAY_EXPLORE 		= 11; //每天探险1次
		public static final int EVERYDAY_ENVY	  		= 12; //每天出使1次
		public static final int EVERYDAY_BATTLE	  		= 13; //每天发动1次战争
		public static final int EVERYDAY_OPEN_CHEST	 	= 14; //每天开1次宝箱
		public static final int EVERYDAY_STRENG	 		= 15; //每天强化1次装备
		public static final int EVERYDAY_MARKET_TR	 	= 16; //每天集市交易1次
		public static final int EVERYDAY_LOGIN		 	= 23; //每天登录1次
		public static final int MARKET_TR	 			= 22; //累计集市交易
		
		public static final int PLUNDER_RES				= 17; //单次掠夺资源
		public static final int ACCU_PLUNDER_RES_STONE	= 18; //累计掠夺石头
		public static final int ACCU_PLUNDER_RES_IRON	= 19; //累计掠夺铁
		public static final int ACCU_PLUNDER_RES_FOOD	= 20; //累计掠夺粮食
		public static final int ACCU_PLUNDER_RES_WOOD	= 21; //累计掠夺木材
		
		public static final int ENVY_TIMES_EVERYDAY		= 26; //每日累计出使次数
		public static final int EVERYDAY_ALLY_LOGIN     = 27; //联盟每天登录
		public static final int EVERYDAY_ONLINE         = 28; //每天在线
		
	}
	
	public static class GAMESRVSTATUS {
		public static final int RUN_WELL = 0;
		public static final int FULL = 1;
		public static final int BUSY = 2;
		public static final int HALTED = 3;
	}
	
	public static class PlayerStatType {
		public static final int PLAYERSTAT_OK         = 1;
		public static final int PLAYERSTAT_KICK       = 2;
		public static final int PLAYERSTAT_SHUTUP     = 3;
	}
	
	public static class ItemId {
		public static final int MONEYTREE_ID = 2001;
		public static final int LOUDSPEAKER_ID = 2002;
	}
	
	public static class ItemQuality {
	    public static final int WHITE   = 1;
	    public static final int GREEN   = 2;
	    public static final int BLUE    = 3;
	    public static final int PURPLE  = 4;
	    public static final int ORANGE  = 5;
	    public static final int RED     = 6;	    
	}
	
}
