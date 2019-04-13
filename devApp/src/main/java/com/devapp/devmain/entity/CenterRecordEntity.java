package com.devapp.devmain.entity;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by Upendra on 1/8/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CenterRecordEntity implements Serializable {


    public String chillingCenterId;
    public String centerId;
    public String centerName;
    public double snf;
    public double fat;
    public double rate;
    public String date;
    public String shift;
    public double amount;
    public double quantity;
    public String milkType;
    public String time;
    public String status;
    public String quantityMode;
    public String qualityMode;
    public long miliTime;


}
