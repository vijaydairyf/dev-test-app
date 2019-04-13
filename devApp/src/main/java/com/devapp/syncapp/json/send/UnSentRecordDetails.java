package com.devapp.syncapp.json.send;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by Pankaj on 5/2/2018.
 */

public class UnSentRecordDetails {

    @JsonProperty("totalRecords")
    private int totalRecords;

    @JsonProperty("fileName")
    private String fileName;

    @JsonProperty("encData")
    private String encData;

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getEncData() {
        return encData;
    }

    public void setEncData(String encData) {
        this.encData = encData;
    }
}
