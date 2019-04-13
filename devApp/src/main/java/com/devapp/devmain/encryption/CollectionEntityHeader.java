package com.devapp.devmain.encryption;

/**
 * Created by Upendra on 4/17/2015.
 */

import java.util.HashMap;
import java.util.Map;

public class CollectionEntityHeader implements SerializeableToCSV {

    public String societyId;
    public String shift;
    public String collectionDate;
    public long startTime;
    public long endTime;
    public String endShift;


    public CollectionEntityHeader() {
    }

    public String getSocietyId() {
        return societyId;
    }

    public void setSocietyId(String id) {
        this.societyId = id;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public String getCollectionDate() {
        return collectionDate;
    }

    public void setCollectionDate(String cDate) {
        this.collectionDate = cDate;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long mType) {
        this.startTime = mType;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long eTime) {
        this.endTime = eTime;
    }

    public String getEndShift() {
        return endShift;
    }

    public void setEndShift(String snf) {
        this.endShift = snf;
    }


    public String[] getColumnHeaders() {
        // TODO Auto-generated method stub
        return new String[]{"centerId", "Shift", "CollectionDate", "StartTime",
                "EndTime", "EndShift"};
    }

    public String[] getValueList() {
        // TODO Auto-generated method stub
        return new String[]{getSocietyId(), getShift(), getCollectionDate(),
                String.valueOf(getStartTime()), String.valueOf(getEndTime()), getEndShift()};
    }

    public Map<String, String> getColumnMap() {

        Map<String, String> columnMap = new HashMap<String, String>();
        columnMap.put("centerId", "societyId");
        columnMap.put("Shift", "shift");
        columnMap.put("CollectionDate", "collectionDate");
        columnMap.put("StartTime", "startTime");
        columnMap.put("EndTime", "endTime");
        columnMap.put("EndShift", "endShift");

        return columnMap;
    }

    public String toString() {
        return this.getSocietyId() + "," + this.getShift() + ","
                + this.getCollectionDate() + "," + this.getStartTime() + ","
                + this.getEndTime() + "," + this.getEndShift();
    }

}

