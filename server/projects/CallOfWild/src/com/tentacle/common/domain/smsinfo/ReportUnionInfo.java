package com.tentacle.common.domain.smsinfo;

public class ReportUnionInfo {

	private long smsId;
	private long playerId;
	private int stat;
	private long forwarderId;


	public long getForwarderId() {
		return forwarderId;
	}

	public void setForwarderId(long forwarderId) {
		this.forwarderId = forwarderId;
	}

	public long getSmsId() {
		return smsId;
	}

	public void setSmsId(long smsId) {
		this.smsId = smsId;
	}

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	public int getStat() {
		return stat;
	}

	public void setStat(int stat) {
		this.stat = stat;
	}

}
