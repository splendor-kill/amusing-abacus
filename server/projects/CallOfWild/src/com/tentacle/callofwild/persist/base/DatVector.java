package com.tentacle.callofwild.persist.base;

import java.util.List;

public class DatVector {
    public enum OPT_TYPE {
        OTHER, BATCH_OP, FOR_TERM
    };

    private String sql;
    private Object[] objects;
    private String sql1;
    private List<Object[]> listObjects;
    private OPT_TYPE optType;

    private String msg;

    public DatVector(String msg) {
        this.msg = msg;
    }

    public DatVector() {
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

    public OPT_TYPE getOptType() {
        return optType;
    }

    public void setOptType(OPT_TYPE optType) {
        this.optType = optType;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return (msg != null ? msg : "") + (sql != null ? sql : "") + (sql1 != null ? sql1 : "");
    }

}
