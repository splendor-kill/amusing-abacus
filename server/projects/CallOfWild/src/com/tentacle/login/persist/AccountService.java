package com.tentacle.login.persist;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;


import com.tentacle.common.domain.baseinfo.AccountInfo;
import com.tentacle.common.persist.DbThread;
import com.tentacle.common.persist.DatVector;
import com.tentacle.common.persist.DbConnPoolManager;
import com.tentacle.common.protocol.ProtoBasis.eErrorCode;

public class AccountService {
	private static final Logger logger = Logger.getLogger(AccountService.class);
	private Connection conn = null;
	
	/***
	 * 保存账户关系
	 * @return
	 */
	public eErrorCode saveAccount(AccountInfo info) {
		DatVector o = new DatVector();
		o.setSql("");
		o.setObjects(parametersSaveAccount(info));
		LoginDbThread.getInst().addObject(o);
		return eErrorCode.OK;
	}

	private Object[] parametersSaveAccount(AccountInfo info) {
		Object[] objects = new Object[] { 
			info.getId(), 
			info.getPlayerId(),
			info.getGold()
		};
		return objects;
	}
	
	/***
	 * 更新账户信息
	 * @return
	 */
	public eErrorCode updateAccount(long playerId, int glod) {
		DatVector o = new DatVector();
		o.setSql("");
		o.setObjects(parametersUpdateAccount(playerId, glod));
		LoginDbThread.getInst().addObject(o);
		return eErrorCode.OK;
	}

	private Object[] parametersUpdateAccount(long playerId, int glod) {
		Object[] objects = new Object[] { glod, playerId };
		return objects;
	}
	
	/***
	 * 查询账户信息
	 * @return
	 */
	public List<AccountInfo> querAccounts() {

	    return null;
	}
}
