package com.devapp.smartcc.entityandconstants;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.Serializable;

/**
 * Created by Upendra on 11/29/2016.
 */
public class QuantityEntity implements Serializable {

    public double ltrQuanity;
    public double kgQuantity;
    @JsonIgnore
    public double readQuantity;
    @JsonIgnore
    public double displayQuantity;

    public String unit;
}
