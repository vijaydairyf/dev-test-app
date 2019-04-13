package com.devapp.syncapp.json.send;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by Pankaj on 5/2/2018.
 */

public class AmcuIdentity {

    @JsonProperty("imei")
    private String imei;
    @JsonProperty("societyCode")
    private String societyCode;
    @JsonProperty("societyName")
    private String societyName;

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getSocietyCode() {
        return societyCode;
    }

    public void setSocietyCode(String societyCode) {
        this.societyCode = societyCode;
    }

    public String getSocietyName() {
        return societyName;
    }

    public void setSocietyName(String societyName) {
        this.societyName = societyName;
    }

}
