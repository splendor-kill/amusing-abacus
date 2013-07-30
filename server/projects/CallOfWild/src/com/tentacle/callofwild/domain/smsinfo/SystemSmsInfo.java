package com.tentacle.callofwild.domain.smsinfo;

import java.util.Date;

/**
 * 
 * @author spfu
 * 
 */
public class SystemSmsInfo {
	public static final int sms_type_universal = 0;
	public static final int sms_type_explore = 1;
	public static final int sms_type_send_envoy = 2;
	public static final int sms_type_al_inv = 3;

	private long id;
	private long senderId; // 赢家
	private long receiverId; // 输家
	private Date sendTime = new Date();
	private String title = " ";
	private String content = " ";
	private int btype = -1; // 战斗类型 (玩家，联盟) 默认-1
	private int smsType;
	private byte[] sms;
	
	private long forwarderId;	// 转发者，非转发邮件时为0

	public long getForwarderId() {
		return forwarderId;
	}

	public void setForwarderId(long forwarderId) {
		this.forwarderId = forwarderId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getSmsType() {
		return smsType;
	}

	public void setSmsType(int smsType) {
		this.smsType = smsType;
	}

	public byte[] getSms() {
		return sms;
	}

	public void setSms(byte[] sms) {
		this.sms = sms;
	}

	public long getSenderId() {
		return senderId;
	}

	public void setSenderId(long senderId) {
		this.senderId = senderId;
	}

	public long getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(long receiverId) {
		this.receiverId = receiverId;
	}

	public int getBtype() {
		return btype;
	}

	public void setBtype(int btype) {
		this.btype = btype;
	}

}
