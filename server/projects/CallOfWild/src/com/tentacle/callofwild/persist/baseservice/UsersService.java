package com.tentacle.callofwild.persist.baseservice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.tentacle.callofwild.domain.baseinfo.UsersInfo;
import com.tentacle.callofwild.persist.DBPoolManager;
import com.tentacle.callofwild.persist.StaticDao;
import com.tentacle.callofwild.persist.base.DAOBject;
import com.tentacle.callofwild.persist.base.DaoLoginTread;

public class UsersService {
    private static final Logger logger = Logger.getLogger(UsersService.class);
    // 保存用户
    public static final String USER_SAVE = "INSERT INTO Users(id, UserName,PassWord,Email,phone_no,CardId,RegeditDate,platform,uid,auth_code,channel,phone_model,phone_resolution,phone_os,phone_manufacturer,phone_imei,phone_mac) "
            + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    // 查询所有的用户
    public static final String USER_QUERYALL = "SELECT * FROM Users";

    public static final String USER_QUERYMAXUSERID = "SELECT MAX(id) As maxId FROM Users";

    /**
     * 同步查询所有的用户
     * 
     * @param retList
     * @return
     */
    public static int synQueryAll(ArrayList<UsersInfo> retList) {
        UsersInfo userInfo = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Connection conn = DBPoolManager.getInstance().getGmAccountConnection();
        int ret = 0;
        try {
            pstmt = conn.prepareStatement(USER_QUERYALL, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                userInfo = new UsersInfo();
                userInfo.setId(rs.getInt("id"));
                userInfo.setUserName(rs.getString("UserName"));
                userInfo.setPassword(rs.getString("PassWord"));
                userInfo.setChannel(rs.getString("channel"));
                userInfo.setEmail(rs.getString("Email"));
                userInfo.setPhoneNumber(rs.getString("phone_no"));
                userInfo.setCardId(rs.getString("CardId"));
                userInfo.setRegeditDate(rs.getTimestamp("RegeditDate"));
                userInfo.setPhoneImei(rs.getString("phone_imei"));
                userInfo.setPhoneModel(rs.getString("phone_model"));
                userInfo.setPhoneResolution(rs.getString("phone_resolution"));
                userInfo.setPlatform(rs.getString("platform"));
                userInfo.setPhoneOs(rs.getString("phone_os"));
                userInfo.setPhoneManufacturer(rs.getString("phone_manufacturer"));
                userInfo.setUid(rs.getString("uid"));
                userInfo.setAuthCode(rs.getString("auth_code"));
                retList.add(userInfo);
            }
        } catch (SQLException e) {
            ret = -1;
            System.out.println("error!!!!!!!!!!!!!!!UsersService::synQueryAll");
            logger.error("error!!!!!!!!!!!!!!!UsersService::synQueryAll--" + e.getMessage());
        } finally {
            StaticDao.close(pstmt);
            StaticDao.close(rs);
            DBPoolManager.getInstance().close(conn);
        }

        return ret;
    }

    /**
     * 查询当前最大的用户ID
     * 
     * @return
     */
    public static int synQueryMaxId() {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Connection conn = DBPoolManager.getInstance().getGmAccountConnection();
        int retMaxId = 0;
        try {
            pstmt = conn.prepareStatement(USER_QUERYMAXUSERID, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                retMaxId = rs.getInt("maxId");
            }
        } catch (SQLException e) {
            retMaxId = -1;
            System.out.println("error!!!!!!!!!!!!!!!UsersService::synQueryMaxId");
            logger.error("error!!!!!!!!!!!!!!!UsersService::synQueryMaxId--" + e.getMessage());
        } finally {
            StaticDao.close(pstmt);
            StaticDao.close(rs);
            DBPoolManager.getInstance().close(conn);
        }

        return retMaxId;
    }

    /**
     * 异步保存用户信息
     * 
     * @param linfo
     * @return
     */
    public static int asynSave(UsersInfo userInfo) {
        DAOBject daoBject = new DAOBject("UsersService::asynSave");
        daoBject.setSql(USER_SAVE);
        Object[] objects = new Object[] { userInfo.getId(),
                userInfo.getUserName(), userInfo.getPassword(),
                userInfo.getEmail(), userInfo.getPhoneNumber(),
                userInfo.getCardId(), userInfo.getRegeditDate(),
                userInfo.getPlatform(), userInfo.getUid(),
                userInfo.getAuthCode(), userInfo.getChannel(),
                userInfo.getPhoneModel(), userInfo.getPhoneResolution(),
                userInfo.getPhoneOs(), userInfo.getPhoneManufacturer(),
                userInfo.getPhoneImei(), userInfo.getPhoneMac() };
        daoBject.setObjects(objects);
        DaoLoginTread.getInstance().addObject(daoBject);
        return 0;
    }
    
    public static void resetPwd(int userId, String newPwd) {
        DAOBject daoBject = new DAOBject("UsersService::resetPwd");
        daoBject.setSql("UPDATE Users SET PassWord=? WHERE id=?");
        daoBject.setObjects(new Object[] { newPwd, userId });
        DaoLoginTread.getInstance().addObject(daoBject);
    }

}
