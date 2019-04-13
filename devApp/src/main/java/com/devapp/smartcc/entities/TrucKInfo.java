package com.devapp.smartcc.entities;

import com.devapp.devmain.entity.Entity;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Upendra on 10/12/2016.
 */
public class TrucKInfo implements Serializable, Entity {
    @JsonProperty
    public String truckNumber;
    @JsonProperty
    public String truckId;
    @JsonProperty
    public String truckName;
    @JsonProperty
    public String model;
    @JsonProperty
    public String make;
    @JsonProperty
    public String type;
    @JsonProperty
    public double maxCapacity;
    @JsonProperty
    public int numOfCans;
    @JsonProperty
    public long lastServiceDate;
    @JsonProperty
    public long contractStartDate;
    @JsonProperty
    public String ownerName;
    @JsonProperty
    public String ownerContactNumber;
    @JsonProperty
    public String driverName;
    @JsonProperty
    public String driverContactNumber;
    @JsonProperty
    public List<String> routes;

    @Override
    public Object getPrimaryKeyId() {
        return truckId;
    }

    @Override
    public void setPrimaryKeyId(Object id) {
        this.truckId = (String) id;
    }
}
