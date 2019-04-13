package com.devapp.devmain.milkline.entities;

import com.devapp.devmain.entity.Entity;
import com.devapp.devmain.entity.TransactionalEntity;
import com.devapp.devmain.postentities.CollectionConstants;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by u_pendra on 17/12/16.
 */

@TransactionalEntity
public class TankerCollectionEntity implements Serializable, Entity {


    public long colId;
    public String tankerNumber;
    public String supervisorCode;
    public String routeNumber;
    public String centerId;
    public double quantity;
    public double fat;
    public double snf;
    public double clr;
    public double temperature;
    public String quantityUnit;
    public ArrayList<String> compartmentNumbers;
    public double alcohol;
    public int sent;
    public long seqNum;
    public long collectionTime;
    public String status;
    public String comments;
    public long sequenceNum;
    public String postDate;
    public String postShift;

    public TankerCollectionEntity() {
        this.sent = CollectionConstants.UNSENT;
    }

    public long getColId() {
        return colId;
    }

    public void setColId(long colId) {
        this.colId = colId;
    }

    public String getTankerNumber() {
        return tankerNumber;
    }

    public void setTankerNumber(String tankerNumber) {
        this.tankerNumber = tankerNumber;
    }

    public String getSupervisorCode() {
        return supervisorCode;
    }

    public void setSupervisorCode(String supervisorCode) {
        this.supervisorCode = supervisorCode;
    }

    public String getRouteNumber() {
        return routeNumber;
    }

    public void setRouteNumber(String routeNumber) {
        this.routeNumber = routeNumber;
    }

    public String getCenterId() {
        return centerId;
    }

    public void setCenterId(String centerId) {
        this.centerId = centerId;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
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

    public double getClr() {
        return clr;
    }

    public void setClr(double clr) {
        this.clr = clr;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getQuantityUnit() {
        return quantityUnit;
    }

    public void setQuantityUnit(String quantityUnit) {
        this.quantityUnit = quantityUnit;
    }

    public ArrayList<String> getCompartmentNumbers() {
        return compartmentNumbers;
    }

    public void setCompartmentNumbers(ArrayList<String> compartmentNumbers) {
        this.compartmentNumbers = compartmentNumbers;
    }

    public double getAlcohol() {
        return alcohol;
    }

    public void setAlcohol(double alcohol) {
        this.alcohol = alcohol;
    }

    public int getSent() {
        return sent;
    }

    public void setSent(int sent) {
        this.sent = sent;
    }

    public long getSeqNum() {
        return seqNum;
    }

    public void setSeqNum(long seqNum) {
        this.seqNum = seqNum;
    }

    public long getCollectionTime() {
        return collectionTime;
    }

    public void setCollectionTime(long collectionTime) {
        this.collectionTime = collectionTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public long getSequenceNum() {
        return sequenceNum;
    }

    public void setSequenceNum(long sequenceNum) {
        this.sequenceNum = sequenceNum;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getPostShift() {
        return postShift;
    }

    public void setPostShift(String postShift) {
        this.postShift = postShift;
    }

    @Override
    public Object getPrimaryKeyId() {
        return colId;
    }

    @Override
    public void setPrimaryKeyId(Object id) {
        this.colId = (long) id;
    }

    public void resetSentMarkers() {
        this.sent = CollectionConstants.UNSENT;

    }
}
