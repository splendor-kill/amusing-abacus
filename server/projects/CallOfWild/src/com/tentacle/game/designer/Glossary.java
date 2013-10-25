package com.tentacle.game.designer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tentacle.common.util.Utils;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.CsvToBean;
import au.com.bytecode.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;


public class Glossary {
	public static final int CULTURE_LANG_CHS = 0;
	public static final int CULTURE_LANG_CHT = 1;
	public static final int CULTURE_LANG_EN = 2;
	
	private String key;
	private String chs;		//Chinese simplified
	private String cht; 	//Chinese traditional
	private String en;		//English

	private List<String> keys = new ArrayList<String>();
	private Map<String, String> cacheChs = new HashMap<String, String>();
	private Map<String, String> cacheCht = new HashMap<String, String>();
	private Map<String, String> cacheEn = new HashMap<String, String>();
	
	private static String file_name = "res/Glossary.csv";	
	private static Glossary inst = null;
	
	public static Glossary getInstance() {
		if (inst == null) {
			inst = new Glossary();
			inst.load();
		}
		return inst;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
		
	public String getChs() {
		return chs;
	}

	public void setChs(String chs) {
		this.chs = chs;
	}

	public String getCht() {
		return cht;
	}

	public void setCht(String cht) {
		this.cht = cht;
	}

	public String getEn() {
		return en;
	}
	
	public void setEn(String en) {
		this.en = en;
	}

	private void load() {
		CSVReader reader = null;
		try {
			reader = new CSVReader(Utils.getUtf8Reader(file_name));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		HeaderColumnNameTranslateMappingStrategy<Glossary> strat = new HeaderColumnNameTranslateMappingStrategy<Glossary>();
		strat.setColumnMapping(columnMapping);

		strat.setType(Glossary.class);
		CsvToBean<Glossary> csv = new CsvToBean<Glossary>();

		try {
			List<Glossary> list = csv.parse(strat, reader);
			if (list != null) {
				for (Glossary param : list) {
					 String key = param.getKey();
					 keys.add(key);
					 cacheChs.put(key, param.getChs());
					 cacheCht.put(key, param.getCht());
					 cacheEn.put(key, param.getEn());
				}
				assert keys.size() == cacheChs.size();
				assert keys.size() == cacheCht.size();
				assert keys.size() == cacheEn.size();
			}
		} catch (java.lang.RuntimeException e) {
			System.out.println("data error, check file[" + file_name + "] please");
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
			}
		}
	}

	private static HashMap<String, String> columnMapping = new HashMap<String, String>() {{
		put("键", "key");
		put("简体中文", "chs");
		put("繁体中文", "cht");
		put("英文", "en");
	}};
	
	public List<String> getKeys() {
		return keys;
	}

	public String getStr(String key, int lang) {
		if (lang == CULTURE_LANG_CHS)
			return cacheChs.get(key);
		else if (lang == CULTURE_LANG_CHT)
			return cacheCht.get(key);
		else if (lang == CULTURE_LANG_EN)
			return cacheEn.get(key);
		return null;
	}
	
	public static void main(String[] args) {
		Glossary cfg = Glossary.getInstance();
		
		for (String k : cfg.getKeys()) {
			try {
				System.out.println(""+k+": "+cfg.getStr(k, CULTURE_LANG_CHS)+",\t"
						+cfg.getStr(k, CULTURE_LANG_CHT)+",\t"
						+cfg.getStr(k, CULTURE_LANG_EN)+"");				
			} catch (Exception e) {
				System.out.println();
			}
		}
	}
	
}
