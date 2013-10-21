package com.tentacle.login.server;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

import com.tentacle.common.domain.baseinfo.UserInfo;

public class UserInfoManager {
	public static final int IMEI_MAX_LENGTH = 10;
	//用户索引
	private HashMap<String, UserInfo> mNameToUsers = new HashMap<String, UserInfo>();
	//user id --> user info
	private TIntObjectMap<UserInfo> idToUser = new TIntObjectHashMap<UserInfo>();
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
	
//    public static UserInfoManager inst() {
//        if (null == gUserInfoManager) {
//            gUserInfoManager = new UserInfoManager();
//        }
//        return gUserInfoManager;
//    }

    public UserInfo getUsersInfo(String userName) {
        mUserInfoLock.lock();
        try {
            return mNameToUsers.get(userName);
        } finally {
            mUserInfoLock.unlock();
        }
    }
    
    public UserInfo getUsersInfo(int userId) {
        mUserInfoLock.lock();
        try {
            return idToUser.get(userId);
        } finally {
            mUserInfoLock.unlock();
        }
    }
	
    public void putUsersInfo(UserInfo usersInfo) {
        mUserInfoLock.lock();
        try {
            mNameToUsers.put(usersInfo.getName(), usersInfo);
            idToUser.put(usersInfo.getId(), usersInfo);
        } finally {
            mUserInfoLock.unlock();
        }
    }

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

    public void setCurMaxUserId(int maxId) {
        mUserIdLock.lock();
        try {
            mCurMaxUserId = maxId;
        } finally {
            mUserIdLock.unlock();
        }
    }
	
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
