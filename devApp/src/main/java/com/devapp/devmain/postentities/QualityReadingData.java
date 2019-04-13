package com.devapp.devmain.postentities;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by u_pendra on 17/1/18.
 */

public class QualityReadingData {

    @JsonProperty("com")
    public String com;
    @JsonProperty("lactose")
    public double lactose;
    @JsonProperty("fat")
    public double fat;
    @JsonProperty("density")
    public double density;
    @JsonProperty("clr")
    public double clr;
    @JsonProperty("awm")
    public double awm;
    @JsonProperty("freezingPoint")
    public double freezingPoint;
    @JsonProperty("pH")
    public double pH;
    @JsonProperty("protein")
    public double protein;
    @JsonProperty("alcohol")
    public double alcohol;
    @JsonProperty("salt")
    public double salt;
    @JsonProperty("snf")
    public double snf;
    @JsonProperty("temperature")
    public TemperatureData temperature;

}
