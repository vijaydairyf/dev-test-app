package com.devapp.devmain.encryption;

/**
 * Created by Upendra on 4/17/2015.
 */

import java.util.HashMap;
import java.util.Map;

public class RateChartEntityBody implements SerializeableToCSV {

    public double fat;
    public double snf;
    public double rate;


    public RateChartEntityBody() {
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public double getSnf() {
        return snf;
    }

    public void setSnf(double snf) {
        this.snf = snf;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double cDate) {
        this.rate = cDate;
    }


    public String[] getColumnHeaders() {
        // TODO Auto-generated method stub
        return new String[]{"FAT", "SNF", "RATE"};
    }

    public String[] getValueList() {
        // TODO Auto-generated method stub
        return new String[]{String.valueOf(getFat()), String.valueOf(getSnf()), String.valueOf(getRate())};
    }

    public Map<String, String> getColumnMap() {

        Map<String, String> columnMap = new HashMap<String, String>();
        columnMap.put("FAT", "fat");
        columnMap.put("SNF", "snf");
        columnMap.put("RATE", "rate");


        return columnMap;
    }

    public String toString() {
        return this.getFat() + "," + this.getSnf() + ","
                + this.getRate();
    }

}



