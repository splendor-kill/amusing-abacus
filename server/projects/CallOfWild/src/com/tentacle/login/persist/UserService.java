package com.tentacle.login.persist;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.tentacle.common.domain.baseinfo.UserInfo;
import com.tentacle.common.persist.DatVector;
import com.tentacle.common.persist.DbConnPoolManager;
import com.tentacle.common.persist.DbHelper;

public class UserService {
    private static final Logger logger = Logger.getLogger(UserService.class);    
    private static final String USER_SAVE = "INSERT INTO User(id, name, pwd, email, phoneNo, cardId, registerDate, platform, uid, authCode, channelId, phoneModel, phoneResolution, phoneOs, phoneManufacturer, phoneImei, phoneImsi, phoneMac) "
            + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String USER_QUERYALL = "SELECT * FROM User";
    private static final String USER_QUERYMAXUSERID = "SELECT MAX(id) As maxId FROM User";

    public static int synQueryAll(ArrayList<UserInfo> retList) {
        UserInfo userInfo = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Connection conn = DbConnPoolManager.getInst().getLoginDbConn();
        int ret = 0;
        try {
            pstmt = conn.prepareStatement(USER_QUERYALL, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                userInfo = new UserInfo();
                userInfo.setId(rs.getInt("id"));
                userInfo.setName(rs.getString("name"));
                userInfo.setPwd(rs.getString("pwd"));
                userInfo.setEmail(rs.getString("email"));
                userInfo.setPhoneNo(rs.getString("phoneNo"));
                userInfo.setCardId(rs.getString("cardId"));
                userInfo.setRegisterDate(rs.getTimestamp("registerDate"));
                userInfo.setPlatform(rs.getString("platform"));
                userInfo.setUid(rs.getString("uid"));
                userInfo.setAuthCode(rs.getString("authCode"));
                userInfo.setChannelId(rs.getString("channelId"));
                userInfo.setPhoneModel(rs.getString("phoneModel"));
                userInfo.setPhoneResolution(rs.getString("phoneResolution"));
                userInfo.setPhoneOs(rs.getString("phoneOs"));
                userInfo.setPhoneManufacturer(rs.getString("phoneManufacturer"));
                userInfo.setPhoneImei(rs.getString("phoneImei"));
                userInfo.setPhoneImsi(rs.getString("phoneImsi"));
                userInfo.setPhoneMac(rs.getString("phoneMac"));
                retList.add(userInfo);
            }
        } catch (SQLException e) {
            ret = -1;
            logger.error(e.getMessage(), e);
        } finally {
            DbHelper.close(pstmt);
            DbHelper.close(rs);
            DbConnPoolManager.close(conn);
        }
        return ret;
    }

    public static int synQueryMaxId() {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Connection conn = DbConnPoolManager.getInst().getLoginDbConn();
        int retMaxId = 0;
        try {
            pstmt = conn.prepareStatement(USER_QUERYMAXUSERID, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                retMaxId = rs.getInt("maxId");
            }
        } catch (SQLException e) {
            retMaxId = -1;
            logger.error(e.getMessage(), e);
        } finally {
            DbHelper.close(pstmt);
            DbHelper.close(rs);
            DbConnPoolManager.close(conn);
        }
        return retMaxId;
    }

    public static void asynSave(UserInfo userInfo) {
        DatVector daoBject = new DatVector("UserService::asynSave");
        daoBject.setSql(USER_SAVE);
        Object[] objects = new Object[] { userInfo.getId(), userInfo.getName(),
                userInfo.getPwd(), userInfo.getEmail(), userInfo.getPhoneNo(),
                userInfo.getCardId(), userInfo.getRegisterDate(),
                userInfo.getPlatform(), userInfo.getUid(),
                userInfo.getAuthCode(), userInfo.getChannelId(),
                userInfo.getPhoneModel(), userInfo.getPhoneResolution(),
                userInfo.getPhoneOs(), userInfo.getPhoneManufacturer(),
                userInfo.getPhoneImei(), userInfo.getPhoneImsi(),
                userInfo.getPhoneMac() };
        daoBject.setObjects(objects);
        LoginDbThread.getInst().addObject(daoBject);
    }

    public static void resetPwd(int userId, String newPwd) {
        DatVector daoBject = new DatVector("UserService::resetPwd");
        daoBject.setSql("UPDATE User SET pwd=? WHERE id=?");
        daoBject.setObjects(new Object[] { newPwd, userId });
        LoginDbThread.getInst().addObject(daoBject);
    }

    public static List<String> queryAllUserName() {
        List<String> allUserName = new ArrayList<String>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Connection conn = DbConnPoolManager.getInst().getLoginDbConn();        
        try {
            final String SQL = "SELECT name FROM User";
            pstmt = conn.prepareStatement(SQL, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = pstmt.executeQuery();
            while (rs.next()) {           
                allUserName.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            DbHelper.close(pstmt);
            DbHelper.close(rs);
            DbConnPoolManager.close(conn);
        }
        return allUserName;
    }
    
    public static Map<String, Integer> queryImei() {
        Map<String, Integer> imei = new HashMap<String, Integer>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Connection conn = DbConnPoolManager.getInst().getLoginDbConn();        
        try {
            final String SQL = "SELECT u.phoneImei AS imei, COUNT(*) AS num FROM User u GROUP BY u.phoneImei";
            pstmt = conn.prepareStatement(SQL, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = pstmt.executeQuery();
            while (rs.next()) {           
                imei.put(rs.getString("imei"), rs.getInt("num"));
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            DbHelper.close(pstmt);
            DbHelper.close(rs);
            DbConnPoolManager.close(conn);
        }
        return imei;
    }
    
}
