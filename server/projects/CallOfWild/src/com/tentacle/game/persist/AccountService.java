package com.tentacle.game.persist;

import org.apache.log4j.Logger;

import com.tentacle.common.domain.baseinfo.AccountInfo;
import com.tentacle.common.persist.DatVector;
import com.tentacle.common.protocol.ProtoBasis.eErrorCode;
import com.tentacle.login.persist.LoginDbThread;

public class AccountService {
	private static final Logger logger = Logger.getLogger(AccountService.class);

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

}
