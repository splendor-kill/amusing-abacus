package com.tentacle.callofwild.server;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

import com.tentacle.callofwild.domain.baseinfo.UsersInfo;

/**
 * 用户信息管理
 * @author Marketing-01
 *
 */
public class UserInfoManager {
	public static final int IMEI_MAX_LENGTH = 10;
	//用户索引
	private HashMap<String, UsersInfo> mNameToUsers = new HashMap<String, UsersInfo>();
	//user id --> user info
	private TIntObjectMap<UsersInfo> idToUser = new TIntObjectHashMap<UsersInfo>();
	//IMEI号索引
	private TObjectIntHashMap<String> mImeiToNum = new TObjectIntHashMap<String>();
	//IMEI锁
	private ReentrantLock mImeiLock = new ReentrantLock();
	//锁
	private ReentrantLock mUserInfoLock = new ReentrantLock();
	//当前最大用户ID
	private int mCurMaxUserId = 0;
	
	private ReentrantLock mUserIdLock = new ReentrantLock();
	
	public static UserInfoManager gUserInfoManager = null;
	
    public static UserInfoManager inst() {
        if (null == gUserInfoManager) {
            gUserInfoManager = new UserInfoManager();
        }
        return gUserInfoManager;
    }
	/**
	 * 获取用户信息
	 * @param userName
	 * @return
	 */
    public UsersInfo getUsersInfo(String userName) {
        mUserInfoLock.lock();
        try {
            return mNameToUsers.get(userName);
        } finally {
            mUserInfoLock.unlock();
        }
    }
    
    public UsersInfo getUsersInfo(int userId) {
        mUserInfoLock.lock();
        try {
            return idToUser.get(userId);
        } finally {
            mUserInfoLock.unlock();
        }
    }
	
	/**
	 * 放入用户信息
	 * @param usersInfo
	 */
    public void putUsersInfo(UsersInfo usersInfo) {
        mUserInfoLock.lock();
        try {
            mNameToUsers.put(usersInfo.getUserName(), usersInfo);
            idToUser.put(usersInfo.getId(), usersInfo);
        } finally {
            mUserInfoLock.unlock();
        }

    }
	/**
	 * 放入IMEI号
	 * @param imei
	 */
    public void putImei(String imei) {
        mImeiLock.lock();
        try {
            int curNum = mImeiToNum.get(imei);
            curNum++;
            mImeiToNum.put(imei, curNum);
        } finally {
            mImeiLock.unlock();
        }
    }
	
    public int getImeiNum(String imei) {
        mImeiLock.lock();
        try {
            return mImeiToNum.get(imei);
        } finally {
            mImeiLock.unlock();
        }
    }
	/**
	 * 设置当前用户最大的ID
	 * @param maxId
	 */
    public void setCurMaxUserId(int maxId) {
        mUserIdLock.lock();
        try {
            mCurMaxUserId = maxId;
        } finally {
            mUserIdLock.unlock();
        }
    }
	
	/***
	 * 获取下一个用户ID
	 * @return
	 */
    public int getNextUserId() {
        mUserIdLock.lock();
        int retId = 0;
        try {
            retId = ++mCurMaxUserId;
        } finally {
            mUserIdLock.unlock();
        }
        return retId;
    }
}
