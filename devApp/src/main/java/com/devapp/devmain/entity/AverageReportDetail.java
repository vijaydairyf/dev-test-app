package com.devapp.devmain.entity;

import java.io.Serializable;

public class AverageReportDetail implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public String farmerId;
    public String date;
    public String day;

    public double avgFat;
    public double avgSnf;
    public int totalMember;
    public double totalAmount;
    public double avgRate;
    public double totalQuantity;
    public double avgQuantity;
    public double avgAmount;


    public double aggFat;
    public double aggSnf;

    public int totalRejectedEntries;
    public int totalTestEntries;
    public int totalAcceptedEntries;
    public double amountWithIncentive;


    public int totalSent;
    public int totalUnsent;

    public int totalFarmer;
    public int totalCenter;
    public int totalCans;
    public int totalEntries;

    public double totalFatKg;
    public double totalSnfKg;
}
