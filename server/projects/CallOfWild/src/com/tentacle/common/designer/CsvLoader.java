package com.tentacle.common.designer;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.CsvToBean;
import au.com.bytecode.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;

import com.tentacle.common.util.Utils;

public abstract class CsvLoader<T> {
    private static final Logger logger = Logger.getLogger(CsvLoader.class);
    
    protected abstract String getCsvFileName();
    protected abstract Map<String, String> getColumnMapping();
    protected abstract void realDo(List<T> list);
    
    @SuppressWarnings("unchecked")
    private Class<T> getClassType() {
        ParameterizedType superclass = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<T>) superclass.getActualTypeArguments()[0];
    }
    
    public void load() {
        String file = getCsvFileName();
        CSVReader reader = null;
        try {
            reader = new CSVReader(Utils.getUtf8Reader(file));
        } catch (IOException e) {
            logger.error("read[" + file + "] failed", e);
        }

        HeaderColumnNameTranslateMappingStrategy<T> strat = new HeaderColumnNameTranslateMappingStrategy<T>();
        strat.setColumnMapping(getColumnMapping());

        strat.setType(getClassType());
        CsvToBean<T> csv = new CsvToBean<T>();

        try {
            List<T> list = csv.parse(strat, reader);
            realDo(list);
        } catch (java.lang.RuntimeException e) {
            logger.error("parse[" + file + "] failed", e);
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
    
}
