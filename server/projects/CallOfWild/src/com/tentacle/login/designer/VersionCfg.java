package com.tentacle.login.designer;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.tentacle.common.contract.IReloadable;
import com.tentacle.common.util.Utils;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.CsvToBean;
import au.com.bytecode.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;



public class VersionCfg implements IReloadable {
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

	private static final String file_name = "res/Versioncfg.csv";
	private static VersionCfg inst = null;
	private List<VersionCfg> list;

	public static VersionCfg getInstance() {
		if (inst == null) {
			inst = new VersionCfg();
			inst.load(file_name);
		}
		return inst;
	}

	private void load(String filename) {
		CSVReader reader = null;
		try {
			reader = new CSVReader(Utils.getUtf8Reader(filename));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		HeaderColumnNameTranslateMappingStrategy<VersionCfg> strat = new HeaderColumnNameTranslateMappingStrategy<VersionCfg>();
		strat.setColumnMapping(columnMapping);
		strat.setType(VersionCfg.class);
		CsvToBean<VersionCfg> csv = new CsvToBean<VersionCfg>();

		try {
			list = csv.parse(strat, reader);
		} catch (java.lang.RuntimeException e) {
			System.out.println("data error, check file[" + file_name+ "] please");
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
			}
		}
	}

    public VersionCfg getVersionInfo(String channelId, String platform) {
        System.out.println("getVersionInfo("+channelId+", "+platform+")");
        for (VersionCfg term : list) {
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
		}
	};

    public static void main(String[] args) {
        VersionCfg verCfg = VersionCfg.getInstance();
        for (VersionCfg cfg : verCfg.list) {
            System.out.println(cfg.getMajorVer() + " " + cfg.getMinorVer() + " " + cfg.getPlatform()
                    + " " + cfg.getCompulsive() + " " + cfg.getPkgUrl() + " " + cfg.getAppRes()
                    + " " + cfg.getAppUrl() + " " + cfg.getChannelId() + " " + cfg.getHelpUrl()
                    + " " + cfg.getActivityUrl() + " " + cfg.getRechargeNotifyUrl()
                    + " " + cfg.getTempCredentialUrl() + " " + cfg.getTokenCredentialUrl());
        }
        
        if (args.length == 2) {
            VersionCfg cfg = verCfg.getVersionInfo(args[0], args[1]);
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
		list.clear();
		load(file_name);
		return true;
	}

	@Override
	public String getName() {
		return "Versioncfg";
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


}
