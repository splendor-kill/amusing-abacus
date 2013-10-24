package com.tentacle.login.designer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tentacle.common.contract.IReloadable;
import com.tentacle.common.designer.CsvLoader;

public class VersionConfig extends CsvLoader<VersionConfig> implements IReloadable  {
    private static final String FILE_NAME = "res/VersionConfig.csv";

    private int majorVer;
	private int minorVer;
	private String pkgUrl;//resource package url
	private String platform;
	private int compulsive;
	private int appRes;
	private String appUrl;	
    private String channelId;
    private String helpUrl;
    private String activityUrl;
    private String rechargeNotifyUrl;
    private String tempCredentialUrl;
    private String tokenCredentialUrl;
	
    private List<VersionConfig> list;
	
    private static class LazyHolder {
        public static final VersionConfig INSTANCE = new VersionConfig();
    }
        
    public static VersionConfig getInst() {
        return LazyHolder.INSTANCE;
    }
	
    public VersionConfig getVersionInfo(String channelId, String platform) {
        System.out.println("getVersionInfo(" + channelId + ", " + platform + ")");
        for (VersionConfig term : list) {
            if (term.getChannelId().equals(channelId)
                    && term.getPlatform().equalsIgnoreCase(platform)) {
                return term;
            }
        }
        return null;
    }
	
	private static HashMap<String, String> columnMapping = new HashMap<String, String>() {
		private static final long serialVersionUID = 6230187050444521920L;
		{
			put("主版本号", "majorVer");
			put("次版本号", "minorVer");
			put("平台", "platform");
			put("是否强制更新", "compulsive");
			put("下载地址", "pkgUrl");
			put("资源(程序)", "appRes");
			put("程序下载地址", "appUrl");
			put("渠道号", "channelId");
			put("帮助地址", "helpUrl");
			put("活动地址", "activityUrl");
			put("充值通知地址", "rechargeNotifyUrl");
			put("临时证书地址", "tempCredentialUrl");
			put("令牌证书地址", "tokenCredentialUrl");
            // put("联运平台", "channelName");
		}
	};

    public static void main(String[] args) {
        VersionConfig verCfg = VersionConfig.getInst();
        verCfg.reload();
        for (VersionConfig cfg : verCfg.list) {
            System.out.println(cfg.getMajorVer() + " " + cfg.getMinorVer() + " " + cfg.getPlatform()
                    + " " + cfg.getCompulsive() + " " + cfg.getPkgUrl() + " " + cfg.getAppRes()
                    + " " + cfg.getAppUrl() + " " + cfg.getChannelId() + " " + cfg.getHelpUrl()
                    + " " + cfg.getActivityUrl() + " " + cfg.getRechargeNotifyUrl()
                    + " " + cfg.getTempCredentialUrl() + " " + cfg.getTokenCredentialUrl());
        }
        
        if (args.length == 2) {
            VersionConfig cfg = verCfg.getVersionInfo(args[0], args[1]);
            if (cfg == null) {
                System.out.println("the version not found with channel["+args[0]+"], platform["+args[1]+"]");
            } else {
                System.out.println("version["+cfg.getMajorVer()+"."+cfg.getMinorVer()+"]");
            }
        }
    }

	public int getMajorVer() {
		return majorVer;
	}

	public void setMajorVer(int majorVer) {
		this.majorVer = majorVer;
	}

	public int getMinorVer() {
		return minorVer;
	}

	public int getAppRes() {
		return appRes;
	}
	public void setAppRes(int appRes) {
		this.appRes = appRes;
	}
	public void setMinorVer(int minorVer) {
		this.minorVer = minorVer;
	}

	public String getPkgUrl() {
		return pkgUrl;
	}

	public void setPkgUrl(String pkgUrl) {
		this.pkgUrl = pkgUrl;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}
	public int getCompulsive() {
		return compulsive;
	}
	public void setCompulsive(int compulsive) {
		this.compulsive = compulsive;
	}

	@Override
	public boolean reload() {
	    if (list != null)
	        list.clear();
		load();
		return true;
	}

	@Override
	public String getName() {
		return "VersionConfig";
	}

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getHelpUrl() {
        return helpUrl;
    }

    public void setHelpUrl(String helpUrl) {
        this.helpUrl = helpUrl;
    }

    public String getActivityUrl() {
        return activityUrl;
    }

    public void setActivityUrl(String activityUrl) {
        this.activityUrl = activityUrl;
    }

    public String getRechargeNotifyUrl() {
        return rechargeNotifyUrl;
    }

    public void setRechargeNotifyUrl(String rechargeNotifyUrl) {
        this.rechargeNotifyUrl = rechargeNotifyUrl;
    }

    public String getTempCredentialUrl() {
        return tempCredentialUrl;
    }

    public void setTempCredentialUrl(String tempCredentialUrl) {
        this.tempCredentialUrl = tempCredentialUrl;
    }

    public String getTokenCredentialUrl() {
        return tokenCredentialUrl;
    }

    public void setTokenCredentialUrl(String tokenCredentialUrl) {
        this.tokenCredentialUrl = tokenCredentialUrl;
    }

    @Override
    protected String getCsvFileName() {
        return FILE_NAME;
    }

    @Override
    protected Map<String, String> getColumnMapping() {
        return columnMapping;
    }

    @Override
    protected void realDo(List<VersionConfig> list) {
        this.list = list;
    }


}
