package com.devapp.devmain.encryption;

/**
 * Created by Upendra on 4/17/2015.
 */

import java.util.HashMap;
import java.util.Map;

public class RateChartEntityHeader implements SerializeableToCSV {

    public String name;
    public String milkType;
    public String validityEndDate;
    public String validityStartDate;
    public String isActive;
    public String ratechartType;
    public String ratechartShift;


    public RateChartEntityHeader() {
    }

    public String getName() {
        return name;
    }

    public void setName(String nam) {
        this.name = nam;
    }

    public String getMilkType() {
        return milkType;
    }

    public void setMilkType(String shift) {
        this.milkType = shift;
    }

    public String getValidityStartDate() {
        return validityStartDate;
    }

    public void setValidityStartDate(String cDate) {
        this.validityStartDate = cDate;
    }

    public String getValidityEndDate() {
        return validityEndDate;
    }

    public void setValidityEndDate(String mType) {
        this.validityEndDate = mType;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isAct) {

        this.isActive = isAct;
    }

    public String getRateChartType() {
        return ratechartType;
    }

    public void setRateChartType(String rateChartType) {
        this.ratechartType = rateChartType;
    }

    public String[] getColumnHeaders() {
        // TODO Auto-generated method stub
        return new String[]{"name", "MilkType", "IsActive", "ValidityStartDate", "ValidityEndDate", "RateChartType"};
    }

    public String[] getValueList() {
        // TODO Auto-generated method stub
        return new String[]{getName(), getMilkType(), getIsActive(), getValidityStartDate(), getValidityEndDate(), getRateChartType()};
    }

    public Map<String, String> getColumnMap() {

        Map<String, String> columnMap = new HashMap<String, String>();
        columnMap.put("name", "name");
        columnMap.put("MilkType", "milkType");
        columnMap.put("IsActive", "isActive");
        columnMap.put("ValidityStartDate", "validityStartDate");
        columnMap.put("ValidityEndDate", "validityEndDate");
        columnMap.put("RateChartType", "ratechartType");


        return columnMap;
    }

    public String toString() {
        return this.getName() + "," + this.getMilkType() + "," + this.getIsActive() + ","
                + this.getValidityStartDate() + "," + this.getValidityEndDate() + "," + this.getRateChartType();
    }

}


