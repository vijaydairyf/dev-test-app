package com.devapp.kmfcommon;

import com.devapp.devmain.entity.ReportEntity;

public class UserMilkCollectionPojo {
    String clusterID = null;
    String weight = null;
    String timeAndDate = null;
    String weightManual = null;
    int noOfCans = 0;
    boolean selected = false;
    String selectedColumnIds = null;
    double kgQty = 0, ltrsQty = 0;
    long tippingStartTime, tippingEndTime, collectionTime;
    long lastColumnId;

    ReportEntity lastReportEntity;


    public UserMilkCollectionPojo(String clusterID, String weight,
                                  boolean selected, int noOfCans, String selectedcolumnId,
                                  String timeAndDate, String weightManual,
                                  double kgQty, double ltrQty, long tippingStartTime,
                                  long tippingEndTime, long collectionTime, long lastColumnId
            , ReportEntity lastReportEntity) {
        super();
        this.clusterID = clusterID;
        this.weight = weight;
        this.selected = selected;
        this.noOfCans = noOfCans;
        this.selectedColumnIds = selectedcolumnId;
        this.timeAndDate = timeAndDate;
        this.weightManual = weightManual;
        this.kgQty = kgQty;
        this.ltrsQty = ltrQty;
        this.tippingEndTime = tippingEndTime;
        this.tippingStartTime = tippingStartTime;
        this.collectionTime = collectionTime;
        this.lastColumnId = lastColumnId;
        this.lastReportEntity = lastReportEntity;
    }

    public String getWeightManual() {
        return weightManual;
    }

    public void setWeightManual(String weightManual) {
        this.weightManual = weightManual;
    }

    public String getTimeAndDate() {
        return timeAndDate;
    }

    public void setTimeAndDate(String timeAndDate) {
        this.timeAndDate = timeAndDate;
    }

    public String getClusterID() {
        return clusterID;
    }

    public void setClusterID(String clusterID) {
        this.clusterID = clusterID;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getNoOfCans() {
        return noOfCans;
    }

    public void setNoOfCans(int noOfCans) {
        this.noOfCans = noOfCans;
    }

    public void setSelectedColumnIds(String columnId) {
        this.selectedColumnIds = columnId;
    }

    public String getSelectedColumnId() {
        return selectedColumnIds;
    }

    public double getKgWeight() {
        return kgQty;
    }

    public double getLtrWeight() {
        return ltrsQty;
    }

    public long getTippingStartTime() {
        return tippingStartTime;
    }

    public long getTippingEndTime() {
        return tippingEndTime;
    }

    public long getCollectionTime() {
        return collectionTime;
    }

    public long getLastColumnId() {
        return lastColumnId;
    }

    public ReportEntity getLastCursor() {
        return lastReportEntity;
    }


}