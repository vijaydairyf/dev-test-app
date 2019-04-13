package com.devapp.devmain.entity;

/**
 * Created by u_pendra on 11/7/18.
 */

public class ConfigSyncStatusEntity {


    private long columnId;
    private String configType;
    private int configStatus;
    private long modifiedTime;

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }

    public long getColumnId() {
        return columnId;
    }

    public void setColumnId(long columnId) {
        this.columnId = columnId;
    }

    public int getConfigStatus() {
        return configStatus;
    }

    public void setConfigStatus(int configStatus) {
        this.configStatus = configStatus;
    }

    public long getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(long modifiedTime) {
        this.modifiedTime = modifiedTime;
    }
}
