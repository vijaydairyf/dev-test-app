package com.devapp.devmain.agentfarmersplit;

import java.io.Serializable;

/**
 * Created by u_pendra on 16/10/17.
 */

public class QualityParams implements Serializable {


    public double fat;
    public double snf;
    public double clr;
    public double density;
    public double lactose;
    public String maSerialNumber;
    public double temperature;
    public double pH;
    public double salt;
    public double protein;
    public double awm;
    public double freezingPoint;
    public String calibration;
    public String com;
    public double alcohol;
    public double conductivity;

    public String qualityMode;
    public long qualityTime;
    public long qualityStartTime;
    public long qualityEndTime;

    //
    public int milkStatusCode;
    public String milkQuality;
    public String maNumber;
    public String maName;

    public String maData;
}
