package com.devapp.devmain.entity;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

/**
 * Created by u_pendra on 16/2/17.
 */
// TODO add javadoc
public class ConfigurationElement implements Serializable, Entity {

    // indicates the name of the configuration key.
    @JsonProperty("key")
    public String key;

    //indicates the current configuration value.
    @JsonProperty("value")
    public String value;

    //indicates the last known configuration value.
    @JsonProperty("lastValue")
    public String lastValue;

    //indicates the user who modified the configuration.
    @JsonProperty("userName")
    public String userName;

    //any non-zero positive value indicates that configuration is already synced with server.
    @JsonIgnore
    public int status;

    //indicates when the configuration was last modified.
    @JsonProperty("modifiedTime")
    public long modifiedDate;

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

    public String getLastValue() {
        return lastValue;
    }

    public void setLastValue(String lastValue) {
        this.lastValue = lastValue;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(long modifiedDate) {
        this.modifiedDate = modifiedDate;
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
