package com.tentacle.login.persist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.tentacle.common.domain.baseinfo.ServerConfigInfo;
import com.tentacle.common.persist.DbConnPoolManager;
import com.tentacle.common.persist.DbHelper;

public class ServerConfigService {
    private static final Logger logger = Logger.getLogger(ServerConfigService.class);
    private static final String SERVERLIST_QUERY = "SELECT * FROM ServerConfig ORDER BY id";

    public List<ServerConfigInfo> queryServerList() {
        List<ServerConfigInfo> serverList = new ArrayList<ServerConfigInfo>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Connection conn = DbConnPoolManager.getInst().getLoginDbConn();        
        try {
            pstmt = conn.prepareStatement(SERVERLIST_QUERY, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                ServerConfigInfo info = new ServerConfigInfo();
                info.setId(rs.getInt("serverId"));
                info.setPort(rs.getInt("port"));
                info.setIp(rs.getString("ip"));
                info.setName(rs.getString("name"));
                info.setNew(rs.getBoolean("isNew"));
                info.setDoaminName(rs.getString("domainName"));
                info.setHelpUrl(rs.getString("helpUrl"));
                info.setActivityUrl(rs.getString("activityUrl"));
                serverList.add(info);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            DbHelper.close(pstmt);
            DbHelper.close(rs);
            DbConnPoolManager.close(conn);
        }
        return serverList;
    }
    
}
