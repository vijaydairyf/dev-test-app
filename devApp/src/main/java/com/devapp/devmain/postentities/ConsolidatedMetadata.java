package com.devapp.devmain.postentities;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by u_pendra on 17/1/18.
 */

public class ConsolidatedMetadata {

    @JsonProperty("deviceId")
    public String deviceId;

    @JsonProperty("id")
    public ConsolidatedId consolidatedId;

    @JsonProperty("lastConcludedShift")
    public ConcludedShift concludedShift;

    @JsonProperty("date")
    public ConsolidatedDate consolidatedDate;

    @JsonProperty("shift")
    public String shift;


}
