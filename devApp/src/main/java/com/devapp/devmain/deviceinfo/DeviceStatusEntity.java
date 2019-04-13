package com.devapp.devmain.deviceinfo;

import java.io.Serializable;

/**
 * Created by u_pendra on 22/12/17.
 */


public class DeviceStatusEntity implements Serializable {

    //Need to send in licensing
    public float batteryPercent;
    public boolean charging;
    public String chargingType;
    public float batteryTemperature;
    public String batteryHealth;
    public int batteryVoltage;
    public float ambientTemperature;
    public int networkAsu;
    public String networkOperator;
    public String dataStatus;
    public String networkType;
    public String simSerialNumber;
    public long time;
    public int sent;
    public int lac;
    public int cellId;

}
