package com.devapp.devmain.agentfarmersplit;

import java.io.Serializable;

/**
 * Created by u_pendra on 16/10/17.
 */

public class QuantityParams implements Serializable {


    public double weighingQuantity;
    public double ltrQuantity;
    public double kgQuantity;
    public String measurementUnit;


    public String quantityMode;
    public long quantityTime;
    public long tippingStartTime;
    public long tippingEndTime;
}
