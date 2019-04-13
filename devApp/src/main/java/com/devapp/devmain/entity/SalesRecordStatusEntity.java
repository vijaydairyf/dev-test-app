package com.devapp.devmain.entity;

import java.io.Serializable;

/**
 * Created by u_pendra on 5/2/18.
 */

public class SalesRecordStatusEntity implements Serializable, Entity {

    private long id;
    private long salesSeqNum;
    private long salesTableIdx;
    private int nwSendStatus;
    private int salesSMSStatus;
    private int shiftDataIdx;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSalesSeqNum() {
        return salesSeqNum;
    }

    public void setSalesSeqNum(long salesSeqNum) {
        this.salesSeqNum = salesSeqNum;
    }

    public long getSalesTableIdx() {
        return salesTableIdx;
    }

    public void setSalesTableIdx(long salesTableIdx) {
        this.salesTableIdx = salesTableIdx;
    }

    public int getNwSendStatus() {
        return nwSendStatus;
    }

    public void setNwSendStatus(int nwSendStatus) {
        this.nwSendStatus = nwSendStatus;
    }

    public int getSalesSMSStatus() {
        return salesSMSStatus;
    }

    public void setSalesSMSStatus(int salesSMSStatus) {
        this.salesSMSStatus = salesSMSStatus;
    }

    public int getShiftDataIdx() {
        return shiftDataIdx;
    }

    public void setShiftDataIdx(int shiftDataIdx) {
        this.shiftDataIdx = shiftDataIdx;
    }

    @Override
    public Object getPrimaryKeyId() {
        return id;
    }

    @Override
    public void setPrimaryKeyId(Object id) {
        this.id = (long) id;
    }
}
