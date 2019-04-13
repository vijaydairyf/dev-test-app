package com.devapp.devmain.postentities;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by u_pendra on 17/1/18.
 */

public class WeighingScaleData {

    public double measuredValue;
    @JsonProperty("measurementUnit")
    public String measurementUnit;
    public double inLtr;
    public double inKg;

}
