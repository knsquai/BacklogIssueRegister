package com.nittsu_infosys.tools.backlog.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.nittsu_infosys.tools.backlog.common.bean.DataBean;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;
import au.com.bytecode.opencsv.bean.CsvToBean;

public class CsvReader {

    private static final String[] HEADER = new String[] { "parentNumber", "subject", "detail", "person", "priority", "milestone", "category", "version", "startDay", "limitDay", "expectTime", "actualTime" };

    public List<String[]> opencsvToStringArray(File file) {
    	CSVReader reader = null;
        try {
            reader = new CSVReader(new InputStreamReader(new FileInputStream(file), "SJIS"));
        	return reader.readAll();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        return null;
    }

    public List<DataBean> opencsvToBean(File file) {
        try {
            CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(file), "SJIS"), ',', '"', 1);
            ColumnPositionMappingStrategy<DataBean> strat = new ColumnPositionMappingStrategy<DataBean>();
            strat.setType(DataBean.class);
            strat.setColumnMapping(HEADER);
            CsvToBean<DataBean> csv = new CsvToBean<DataBean>();
            return csv.parse(strat, reader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
