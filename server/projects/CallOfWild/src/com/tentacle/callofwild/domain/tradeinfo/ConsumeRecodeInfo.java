package com.tentacle.callofwild.domain.tradeinfo;

import java.util.Date;

import com.tentacle.callofwild.persist.base.DAOBject;
import com.tentacle.callofwild.persist.base.DaoThread;
import com.tentacle.callofwild.util.Utils;

public class ConsumeRecodeInfo {

	private Date consumeTime;
	private int consumeGold;
	private int consumeType;
	private int consumeSubType;
	private int currency;
	private long playerId;
	private int consumeWhere;

	public void save() {
		String sql = "insert into consume_recode(consume_time,consume_money,consume_type,consume_sub_type,currency) values(?,?,?,?,?)";
		DAOBject o = new DAOBject();
		o.setSql(sql);
		o.setObjects(parametersSave(this));
		DaoThread.getInstance().addObject(o);
	}

	private Object[] parametersSave(ConsumeRecodeInfo info) {
		if (consumeTime == null)
			consumeTime = Utils.initDate();
		Object[] objects = new Object[] { consumeTime, consumeGold,
				consumeType, consumeSubType, currency };
		return objects;
	}

	/**
	 * 保存消费金币
	 * @param playerId
	 * @param consumeGold
	 * @param consumeType
	 * @param consumeSubType
	 * @param currency
	 */
	public static void save(long playerId, int consumeGold, int consumeType,
			int consumeSubType, int currency, int role, int where) {
		Object[] objects = new Object[] { 
			playerId, 
			new Date(), 
			consumeGold,
			consumeType, 
			consumeSubType,
			currency,
			role,
			where
		};

		String sql = "insert into consume_recode(player_id,consume_time,consume_money,consume_type,consume_sub_type,currency,role,consume_where) values(?,?,?,?,?,?,?,?)";
		DAOBject o = new DAOBject();
		o.setSql(sql);
		o.setObjects(objects);
		DaoThread.getInstance().addObject(o);
	}

	public Date getConsumeTime() {
		return consumeTime;
	}

	public void setConsumeTime(Date consumeTime) {
		this.consumeTime = consumeTime;
	}

	public int getConsumeGold() {
		return consumeGold;
	}

	public void setConsumeGold(int consumeGold) {
		this.consumeGold = consumeGold;
	}

	public int getConsumeType() {
		return consumeType;
	}

	public void setConsumeType(int consumeType) {
		this.consumeType = consumeType;
	}

	public int getConsumeSubType() {
		return consumeSubType;
	}

	public void setConsumeSubType(int consumeSubType) {
		this.consumeSubType = consumeSubType;
	}

	public int getCurrency() {
		return currency;
	}

	public void setCurrency(int currency) {
		this.currency = currency;
	}

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}
}
