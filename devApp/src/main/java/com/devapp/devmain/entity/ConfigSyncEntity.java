package com.devapp.devmain.entity;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

/**
 * Created by x on 28/1/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigSyncEntity {

    @JsonProperty
    public String imeiNumber;
    @JsonProperty
    public String centerId;
    @JsonProperty
    public String configurationType;
    @JsonProperty
    public int version;
    @JsonProperty
    public long id;
    @JsonProperty
    public List<Object> configData;
    @JsonProperty
    public String routeId;
    @JsonProperty
    public String chillingCenterId;
    @JsonProperty
    public String orgId;

}
