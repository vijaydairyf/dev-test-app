package com.devapp.devmain.ma;

import java.io.Serializable;

public class MAEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public String fat;
    public String snf;
    public String clr;
    public String temp;
    public String protein;
    public String addedWater;
    public String lactose;
    public String addedSalt;
    public String milkAnalyserId;
    public String timeStamp;
    public String errorCode;
    public String name;

    //Added for calibration

    public String date;
    public String time;
    public String calibration;

    public String rawData;
}
