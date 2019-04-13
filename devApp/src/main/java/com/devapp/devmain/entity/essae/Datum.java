package com.devapp.devmain.entity.essae;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xx on 13/3/18.
 */

public class Datum {
    @JsonProperty("date")
    public String date;
    @JsonProperty("time")
    public String time;
    @JsonProperty("milkType")
    public String milkType;
    @JsonProperty("fat")
    public Fat fat;
    @JsonProperty("snf")
    public Snf snf;
    @JsonProperty("awmCorrection")
    public Double awm;
    @JsonProperty("temperature")
    public Temperature temperature;
    @JsonIgnore
    public Map<String, Object> additionalProperties = new HashMap<String, Object>();

}
