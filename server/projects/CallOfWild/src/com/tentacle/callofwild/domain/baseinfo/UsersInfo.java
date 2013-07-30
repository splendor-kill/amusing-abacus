package com.tentacle.callofwild.domain.baseinfo;

import java.util.Date;

/**
 * 
 * @author spfu
 * 
 */
public class UsersInfo {

	private int id;
	private String userName;
	private String password;
	private String email;

	private String cardId;
	private Date regeditDate;

	private String platform;
	private String uid;
	private String authCode;
	private String channel;

	private String phoneNumber;
	private String phoneModel;
	private String phoneResolution;
	private String phoneOs;
	private String phoneManufacturer;
	private String phoneImei;
	private String phoneMac;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public Date getRegeditDate() {
		return regeditDate;
	}

	public void setRegeditDate(Date regeditDate) {
		this.regeditDate = regeditDate;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getPhoneModel() {
		return phoneModel;
	}

	public void setPhoneModel(String phoneModel) {
		this.phoneModel = phoneModel;
	}

	public String getPhoneResolution() {
		return phoneResolution;
	}

	public void setPhoneResolution(String phoneResolution) {
		this.phoneResolution = phoneResolution;
	}

	public String getPhoneOs() {
		return phoneOs;
	}

	public void setPhoneOs(String phoneOs) {
		this.phoneOs = phoneOs;
	}

	public String getPhoneManufacturer() {
		return phoneManufacturer;
	}

	public void setPhoneManufacturer(String phoneManufacturer) {
		this.phoneManufacturer = phoneManufacturer;
	}

	public String getPhoneImei() {
		return phoneImei;
	}

	public void setPhoneImei(String phoneImei) {
		this.phoneImei = phoneImei;
	}

	public String getPhoneMac() {
		return phoneMac;
	}

	public void setPhoneMac(String phoneMac) {
		this.phoneMac = phoneMac;
	}

}
