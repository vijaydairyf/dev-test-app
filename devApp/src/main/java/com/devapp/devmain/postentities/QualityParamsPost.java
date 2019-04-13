package com.devapp.devmain.postentities;

import com.devapp.devmain.ConsolidationPost.CustomSerializable;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Date;

/**
 * Created by u_pendra on 17/1/18.
 */

public class QualityParamsPost {

    public String mode;
    public String milkQuality;
    public int milkStatusCode;
    @JsonSerialize(using = CustomSerializable.class)
    public Date measuredTime;
    @JsonIgnore
    public Date qualityTime;

    @JsonProperty("milkAnalyzer")
    public MilkAnalyser milkAnalyser;


}
