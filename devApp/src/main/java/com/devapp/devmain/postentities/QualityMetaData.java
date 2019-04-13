package com.devapp.devmain.postentities;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by u_pendra on 17/1/18.
 */

public class QualityMetaData {

    @JsonProperty("serialNumber")
    public String serialNumber;
    @JsonProperty("maNumber")
    public String maNumber;
    @JsonProperty("maMake")
    public String maMake;
    @JsonProperty("calibration")
    public String calibration;
    @JsonProperty("maData")
    public String maData;

}
