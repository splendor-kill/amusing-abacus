package com.tentacle.callofwild.persist.base;

import java.util.List;



public class DAOBject implements Comparable<DAOBject>{	
	public enum OPT_TYPE{OTHER,BATCH_OP,FOR_TERM};
	private String sql; // sql 语句
	private Object[] objects; // update 时，传递参数
	private String sql1; // 列表保存 sql
	private List<Object[]> listObjects; // 列表保存 参数
	private OPT_TYPE optType;
	@Deprecated
	private Object o; // 当对象属性与DB TABLE中字段名称一致，顺序一致 可采用设置pojo类，此种方法更简单
	private String msg = " ";

	public DAOBject(String msg) {
		this.msg = msg;
	}
	
	public DAOBject() {
		
	}
	
	public Object[] getObjects() {
		return objects;
	}

	public void setObjects(Object[] objects) {
		this.objects = objects;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getSql1() {
		return sql1;
	}

	public void setSql1(String sql1) {
		this.sql1 = sql1;
	}

	public List<Object[]> getListObjects() {
		return listObjects;
	}

	public void setListObjects(List<Object[]> listObjects) {
		this.listObjects = listObjects;
	}

	@Deprecated
	public Object getO() {
		return o;
	}

	@Deprecated
	public void setO(Object o) {
		this.o = o;
	}

	public OPT_TYPE getOptType() {
		return optType;
	}

	public void setOptType(OPT_TYPE optType) {
		this.optType = optType;
	}

	@Override
	public int compareTo(DAOBject o) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public String getMsg() {
		return msg + (sql!=null?sql:"") + (sql1!=null?sql1:"");
	}
}
