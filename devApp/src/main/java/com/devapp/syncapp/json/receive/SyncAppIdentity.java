package com.devapp.syncapp.json.receive;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by Pankaj on 5/1/2018.
 */

public class SyncAppIdentity {


    @JsonProperty("name")
    private String name;
    @JsonProperty("imei")
    private String imei;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    @Override
    public String toString() {
        return "SyncAppIdentity{" +
                "name='" + name + '\'' +
                ", imei='" + imei + '\'' +
                '}';
    }
}
