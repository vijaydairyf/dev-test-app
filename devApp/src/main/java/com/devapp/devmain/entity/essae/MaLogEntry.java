package com.devapp.devmain.entity.essae;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xx on 13/3/18.
 */

public class MaLogEntry {

    @JsonProperty("type")
    public String type;
    @JsonProperty("data")
    public List<Datum> dataList;
    @JsonIgnore
    public Map<String, Object> additionalProperties = new HashMap<String, Object>();

}
