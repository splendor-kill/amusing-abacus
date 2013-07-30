package com.tentacle.callofwild.domain.tradeinfo;

import java.util.Date;

public class PlayerMarketInfo {

	private long id;
	private long playerId;
	private long fkId;
	private int number;
	private Date consignTime = new Date(); //
	private int status;
	private int consignType;
	private long cityId;
	private int money;
	private int cfgNo;
	private double unitPrice;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	public long getFkId() {
		return fkId;
	}

	public void setFkId(long fkId) {
		this.fkId = fkId;
	}

	public Date getConsignTime() {
		return consignTime;
	}

	public void setConsignTime(Date consignTime) {
		this.consignTime = consignTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getConsignType() {
		return consignType;
	}

	public void setConsignType(int consignType) {
		this.consignType = consignType;
	}

	public long getCityId() {
		return cityId;
	}

	public void setCityId(long cityId) {
		this.cityId = cityId;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public int getCfgNo() {
		return cfgNo;
	}

	public void setCfgNo(int cfgNo) {
		this.cfgNo = cfgNo;
	}

	public double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}

}
