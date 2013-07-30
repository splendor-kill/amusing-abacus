package com.tentacle.callofwild.designer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tentacle.callofwild.util.Utils;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.CsvToBean;
import au.com.bytecode.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;


public class Gfw {
	private static String file_name = "res/GFW.csv";
	
	private String word;
	
	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public void load(Map<String, Integer> dict) {
		CSVReader reader = null;
		try {
			reader = new CSVReader(Utils.getUtf8Reader(file_name));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		HeaderColumnNameTranslateMappingStrategy<Gfw> strat = new HeaderColumnNameTranslateMappingStrategy<Gfw>();
		strat.setColumnMapping(columnMapping);

		strat.setType(Gfw.class);
		CsvToBean<Gfw> csv = new CsvToBean<Gfw>();

		try {
			List<Gfw> list = csv.parse(strat, reader);
			if (list != null && dict != null) {
				for (Gfw param : list) {
					dict.put(param.getWord(), null);
				}
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

	private static HashMap<String, String> columnMapping = new HashMap<String, String>() {
        private static final long serialVersionUID = 2276496759004199378L;

    {
		put("词汇", "word");
		put("保留0敏感1成人2", "type");
	}};


	public static void main(String[] args) {
		Gfw party = new Gfw();
		Map<String, Integer> dict = new HashMap<String, Integer>();
		party.load(dict);
		System.out.println(dict.keySet());
	}

}
