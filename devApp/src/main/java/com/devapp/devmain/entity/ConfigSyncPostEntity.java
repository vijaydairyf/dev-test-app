package com.devapp.devmain.entity;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by x on 28/1/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigSyncPostEntity implements Serializable {
    public String imeiNumber;
    public String configurationType;
    public String centerId;
    public String routeId;
    public String chillingCenterId;
    public String orgId;
}
