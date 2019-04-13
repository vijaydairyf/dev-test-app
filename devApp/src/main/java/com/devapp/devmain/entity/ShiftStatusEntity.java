package com.devapp.devmain.entity;

import java.io.Serializable;

public class ShiftStatusEntity implements Serializable, Entity {


    public long columnId;
    public long startTime;
    public long endTime;
    public String date;
    public String shift;
    public int sentStatus;

    public long getColumnId() {
        return columnId;
    }

    public void setColumnId(long columnId) {
        this.columnId = columnId;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public int getSentStatus() {
        return sentStatus;
    }

    public void setSentStatus(int sentStatus) {
        this.sentStatus = sentStatus;
    }

    @Override
    public Object getPrimaryKeyId() {
        return null;
    }

    @Override
    public void setPrimaryKeyId(Object id) {

    }
}
