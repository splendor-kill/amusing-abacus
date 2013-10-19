package com.tentacle.common.persist;

import java.util.List;

public class DatVector {
    public enum Type {
        OTHER, BATCH_OP, FOR_TERM
    };

    private String sql;
    private Object[] objects;
    private String sql1;
    private List<Object[]> listObjects;
    private Type optType;

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

    public Type getOptType() {
        return optType;
    }

    public void setOptType(Type optType) {
        this.optType = optType;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return (msg != null ? msg : "") + (sql != null ? sql : "") + (sql1 != null ? sql1 : "");
    }

}
