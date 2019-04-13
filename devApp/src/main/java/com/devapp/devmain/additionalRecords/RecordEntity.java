package com.devapp.devmain.additionalRecords;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by u_pendra on 29/9/17.
 */

public class RecordEntity implements Serializable {


    private String startDate;
    private String endDate;
    private String startShift;
    private String endShift;
    private String societyId;
    private ArrayList<String> producerList;
    private ArrayList<String> typeList;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStartShift() {
        return startShift;
    }

    public void setStartShift(String startShift) {
        this.startShift = startShift;
    }

    public String getEndShift() {
        return endShift;
    }

    public void setEndShift(String endShift) {
        this.endShift = endShift;
    }

    public ArrayList<String> getProducerList() {
        return producerList;
    }

    public void setProducerList(ArrayList<String> producerId) {
        this.producerList = producerId;
    }

    public ArrayList<String> getTypeList() {
        return typeList;
    }

    public void setTypeList(ArrayList<String> typeList) {
        this.typeList = typeList;
    }

    public String getSocietyId() {
        return societyId;
    }

    public void setSocietyId(String societyId) {
        this.societyId = societyId;
    }
}
