package com.devapp.devmain.postentities;

import com.devapp.devmain.ConsolidationPost.CustomSerializable;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Date;

/**
 * Created by u_pendra on 17/1/18.
 */

public class QuantityParamspost {

    public String mode;
    @JsonSerialize(using = CustomSerializable.class)
    @JsonProperty("measuredTime")
    public Date measurementTime;
    @JsonSerialize(using = CustomSerializable.class)
    public Date tippingEndTime;
    @JsonSerialize(using = CustomSerializable.class)
    public Date tippingStartTime;

    public double collectedQuantity;
    public double soldQuantity;
    @JsonIgnore
    public double availableQuantity;
    @JsonProperty("weighingScale")
    public WeighingScaleData weighingScaleData;
}
