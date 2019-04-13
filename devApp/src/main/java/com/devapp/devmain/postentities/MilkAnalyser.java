package com.devapp.devmain.postentities;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by u_pendra on 17/1/18.
 */

public class MilkAnalyser {

    @JsonProperty("metaData")
    public QualityMetaData qualityMetaData;
    @JsonProperty("reading")
    public QualityReadingData qualityReadingData;
}
