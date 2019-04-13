package com.devapp.devmain.entity.essae;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xx on 13/3/18.
 */

public class Snf {

    @JsonProperty("snfCorrection")
    public Double snf;
    @JsonProperty("lowerSnf")
    public Double lowerSnf;
    @JsonProperty("higherSnf")
    public Double higherSnf;
    @JsonIgnore
    public Map<String, Object> additionalProperties = new HashMap<String, Object>();

}
