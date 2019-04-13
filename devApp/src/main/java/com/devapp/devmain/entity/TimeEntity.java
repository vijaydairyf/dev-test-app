package com.devapp.devmain.entity;

import java.io.Serializable;

/**
 * Created by u_pendra on 4/5/17.
 */

public class TimeEntity implements Serializable {

    //Shift M or E
    public String shift;
    //HH:MM format
    public String time;
    //DD-MMM-YYYY format
    public String date;
    //Collection long date yyyymmdd
    public long lDate;
    //Collection millliTime
    public long milliTime;
    //Quality (MilkAnalyser time)
    public long qualityTime;
    //Quantity (Weighing time)
    public long quantityTime;
    //Quantity reading start time
    public long tippingStartTime;
    //Quantity reading end time
    public long tippingEndTime;

}
