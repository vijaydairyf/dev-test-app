package com.devapp.devmain.postentities;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by u_pendra on 17/1/18.
 */

public class TemperatureData {

    @JsonProperty("value")
    public double temperature;
    @JsonProperty("unit")
    public String unit;

}
