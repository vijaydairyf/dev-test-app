package com.devapp.devmain.entity;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

/**
 * Created by x on 24/1/18.
 */

public class ProducerProfile implements Serializable {
    @JsonProperty("name")
    public String name;
    @JsonProperty("id")
    public String id;
    @JsonProperty("barcode")
    public String barcode;
    @JsonProperty("milkType")
    public String milkType;
    @JsonProperty("mobileNumber")
    public String mobileNumber;
    @JsonProperty("email")
    public String email;
    @JsonProperty("isActive")
    public boolean isActive;
    @JsonProperty("regDate")
    public long regDate;
    @JsonProperty("lastModified")
    public long lastModified;
    @JsonProperty("isMultipleCans")
    public boolean isMultipleCans;
    @JsonProperty("contactPersonName")
    public String contactPersonName;
    @JsonProperty("producerType")
    public String producerType;
}
