package com.devapp.devmain.entity.essae;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xx on 12/3/18.
 */

public class EssaeCleaningEntity implements Serializable {

    @JsonProperty("queriedTime")
    public String queriedTime;
    @JsonProperty("centerId")
    public String centerId;
    @JsonProperty("deviceId")
    public String deviceId;
    @JsonProperty("maLogEntryList")
    public List<MaLogEntry> maLogEntryList;
    @JsonIgnore
    public Map<String, Object> additionalProperties = new HashMap<String, Object>();

}







