package com.devapp.devmain.encryption;

import com.devapp.devmain.httptasks.PostCollectionRecordsService;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by u_pendra on 26/9/17.
 */

public class ConsolidatedReport implements SerializeableToCSV {

//    private String csvVersion = "2.0";
//    private String records;
//
//    public String getCsvVersion() {
//        return csvVersion;
//    }
//
//
//    public String getRecords() {
//        return records;
//    }
//
//    public void setRecords(String records) {
//        this.records = records;
//    }


    public ConsolidatedData consolidatedReport;

    public ConsolidatedData getConsolidatedReport() {
        return consolidatedReport;
    }

    public void setConsolidatedReport(ConsolidatedData consolidatedReport) {
        this.consolidatedReport = consolidatedReport;
    }

    @Override
    public String[] getColumnHeaders() {
        return new String[]{"consolidatedReport"};
    }

    @Override
    public String[] getValueList() {
        return new String[]{PostCollectionRecordsService.toJson(getConsolidatedReport())};
    }

    @Override
    public Map<String, String> getColumnMap() {
        return new HashMap<>();
    }
}
