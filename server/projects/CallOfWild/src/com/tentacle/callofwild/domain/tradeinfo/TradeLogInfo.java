package com.tentacle.callofwild.domain.tradeinfo;

import java.util.Date;

import com.tentacle.callofwild.protocol.ProtoBasis.eResType;

public class TradeLogInfo {
	private long id;
	private long sellPlayerId; //
	private long buyPlayerId;
	private Date tradeTime;
	private long resourceId;
	private long equipId;
	private long upgradeStoneId;
	private int price_glod;
	private int price_coppermoney;
	private int money;
	private int usedPerson;
	private int commissiones;
	private int woods;
	private int stone;
	private int foods;
	private int irons;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getSellPlayerId() {
		return sellPlayerId;
	}

	public void setSellPlayerId(long sellPlayerId) {
		this.sellPlayerId = sellPlayerId;
	}

	public long getBuyPlayerId() {
		return buyPlayerId;
	}

	public void setBuyPlayerId(long buyPlayerId) {
		this.buyPlayerId = buyPlayerId;
	}

	public Date getTradeTime() {
		return tradeTime;
	}

	public void setTradeTime(Date tradeTime) {
		this.tradeTime = tradeTime;
	}

	public long getResourceId() {
		return resourceId;
	}

	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}

	public long getEquipId() {
		return equipId;
	}

	public void setEquipId(long equipId) {
		this.equipId = equipId;
	}

	public long getUpgradeStoneId() {
		return upgradeStoneId;
	}

	public void setUpgradeStoneId(long upgradeStoneId) {
		this.upgradeStoneId = upgradeStoneId;
	}

	public int getPrice_glod() {
		return price_glod;
	}

	public void setPrice_glod(int price_glod) {
		this.price_glod = price_glod;
	}

	public int getPrice_coppermoney() {
		return price_coppermoney;
	}

	public void setPrice_coppermoney(int price_coppermoney) {
		this.price_coppermoney = price_coppermoney;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public int getUsedPerson() {
		return usedPerson;
	}

	public void setUsedPerson(int usedPerson) {
		this.usedPerson = usedPerson;
	}

	public int getCommissiones() {
		return commissiones;
	}

	public void setCommissiones(int commissiones) {
		this.commissiones = commissiones;
	}

	public int getWoods() {
		return woods;
	}

	public void setWoods(int woods) {
		this.woods = woods;
	}

	public int getStone() {
		return stone;
	}

	public void setStone(int stone) {
		this.stone = stone;
	}

	public int getFoods() {
		return foods;
	}

	public void setFoods(int foods) {
		this.foods = foods;
	}

	public int getIrons() {
		return irons;
	}

	public void setIrons(int irons) {
		this.irons = irons;
	}
	
	public void setRes(int resType, int num) {
		if (eResType.WOOD_VALUE == resType) {
			woods = num;
		}
		else if (eResType.STONE_VALUE == resType) {
			stone = num;
		}
		else if (eResType.IRON_VALUE == resType) {
			irons = num;
		}
		else if (eResType.FOOD_VALUE == resType) {
			foods = num;
		}
	}

}
