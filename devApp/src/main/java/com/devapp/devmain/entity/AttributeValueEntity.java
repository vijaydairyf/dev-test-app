package com.devapp.devmain.entity;

/**
 * Created by x on 6/2/18.
 */

public class AttributeValueEntity implements Entity {

    private static final String BASIC_PROFILE = "BASIC_PROFILE";
    private static final String CONFIGURATION = "CONFIGURATION";


    private String key;
    private String value;
    private String oldValue;
    private int sentStatus;
    private long updatedTime;
    private String updatedBy;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public int getSentStatus() {
        return sentStatus;
    }

    public void setSentStatus(int sentStatus) {
        this.sentStatus = sentStatus;
    }

    public long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public Object getPrimaryKeyId() {
        return key;
    }

    @Override
    public void setPrimaryKeyId(Object id) {
        key = (String) id;
    }
}
