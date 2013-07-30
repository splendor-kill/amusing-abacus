package com.tentacle.callofwild.util;

import java.util.HashMap;
import java.util.Map;

public class Grid<ROW, COL, VAL> {
	
	private Map<ROW, Map<COL, VAL>> content = new HashMap<ROW, Map<COL, VAL>>();

	public VAL put(ROW y, COL x, VAL val) {
		Map<COL, VAL> row = content.get(y);
		if (row == null) {
			row = new HashMap<COL, VAL>();
			content.put(y, row);
		}
		return row.put(x, val);
	}
	
	public VAL get(ROW y, COL x) {
		Map<COL, VAL> row = content.get(y);
		return (row != null) ? row.get(x) : null;
	}

}
