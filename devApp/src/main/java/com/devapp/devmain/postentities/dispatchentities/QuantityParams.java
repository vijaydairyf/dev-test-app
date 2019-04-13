package com.devapp.devmain.postentities.dispatchentities;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Created by xx on 9/9/18.
 */

public class QuantityParams {
    public String mode;
    public double collectedQuantity;
    public double soldQuantity;
    @JsonIgnore
    public double availableQuantity;
    public WeighingScale weighingScale;
}
